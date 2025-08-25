package jp.adtekfuji.adinterfaceservice.warehouseplugin;

import adtekfuji.clientservice.WarehouseInfoFaced;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.warehouse.MstLocationInfo;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 棚マスタのインポート
 *
 * @author s-heya
 */
public class ImportMstLocation {

    private final Logger logger = LogManager.getLogger();

    private final String FILE_MST_LOCATION = "mst_location.xlsx";
    private final String FILE_SETTING = "mst_location_setting.xml";
    private final String FIELD_AREA = "area";
    private final String FIELD_LOC = "loc";
    private final String FIELD_NEW_AREA = "newArea";
    private final String FIELD_NEW_LOC = "newLoc";
    private final String FIELD_ORDER = "order";
    private final String FIELD_LOC_SPEC = "locSpec";
    private final String FIELD_TYPE = "type";
    
    private final String importRoot;        // インポートフォルダ
    private final String tempRoot;          // 作業フォルダ
    private final FileManager fileManager;

    /**
     * コンストラクタ
     * 
     * @param root インポートフォルダ
     */
    public ImportMstLocation(String root) {
        this.importRoot = root;
        this.tempRoot = System.getenv("ADFACTORY_HOME") + File.separator + "temp" + File.separator;
        this.fileManager = new FileManager(root, this.tempRoot, FILE_MST_LOCATION);
    }

    /**
     * 棚マスタを読み込む
     *
     */
    public void importData() {
        try {
            logger.info("ImportMstLocation: Start import.");

            // 作業フォルダにコピー
            if (!this.fileManager.copyToWorkingDir()) {
                return;
            }

            // 前回ファイルを削除
            this.fileManager.deleteDoneFile();
            this.fileManager.deleteErrFile();
            this.fileManager.deleteProcFile();

            this.fileManager.renameOrgToProc();

            WarehouseCommonSettingInfo settingInfo = ImportUtility.readSetting(this.importRoot + FILE_SETTING, 
                Arrays.asList(FIELD_AREA, FIELD_LOC, FIELD_NEW_AREA, FIELD_NEW_LOC, FIELD_ORDER, FIELD_LOC_SPEC, FIELD_TYPE));

            if (Objects.isNull(settingInfo)) {
                logger.error("ImportMstLocation: Read setting file failed.");
                return;
            }

            // フォーマットファイルを検証
            //if (!this.verifyFormat(settingInfo)) {
            //    logger.error("ImportMstLocation: Format file is incorrect.");
            //    return;
            //}

            // Excelを読み込む
            List<List<String>> readData = ImportUtility.readExcel(this.tempRoot + FILE_MST_LOCATION, settingInfo);
            if (Objects.isNull(readData)) {
                throw new Exception("ImportMstLocation: Read excel file failed.");
            }

            // Excelから読み取ったデータをオブジェクトに変換
            List<MstLocationInfo> locationList = this.convertToObjects(readData, settingInfo);
            if (Objects.isNull(locationList)) {
                throw new Exception("ImportMstLocation: Format change failed.");
            }

            // オブジェクトをJsonに変換
            String json = JsonUtils.objectToJson(locationList);

            // アップロードファイルを出力
            String jsonFile = "location_" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".json";
            String jsonPath = this.tempRoot + jsonFile;
            if (this.fileManager.outputJsonFileToWorkingDir(json, jsonFile)) {
                throw new Exception("ImportMstLocation: Output josn file failed.");
            }

            // Rest APIを呼び出す
            WarehouseInfoFaced faced = new WarehouseInfoFaced();
            ResponseEntity response = faced.importLocation(jsonPath);

            if (response != null && response.isSuccess()) {
                this.fileManager.renameProcToDone();
                logger.info("ImportMstLocation: Import Success.");
            } else {
                this.fileManager.renameProcToError();
                logger.error("ImportMstLocation: Import Error.");
            }

            this.fileManager.deleteWorkFile();
            this.fileManager.deleteJsonFile(jsonPath);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            this.fileManager.renameProcToError();
            this.fileManager.deleteWorkFile();
        }
    }

    /**
     * Excelから読み取ったデータをオブジェクトに変換する。
     *
     * @param readData Excelから読み取ったデータ
     * @param settingInfo フォーマット情報
     * @return オブジェクト
     */
    private List<MstLocationInfo> convertToObjects(List<List<String>> readData, WarehouseCommonSettingInfo settingInfo) {
      
        try {
            List<MstLocationInfo> list = new ArrayList<>();

            for (int row = 0; row < readData.size(); row++) {
                MstLocationInfo location = new MstLocationInfo();
                List<String> rowData = readData.get(row);

                for (int i = 0; i < settingInfo.getParameters().size(); i++) {
                    String key = settingInfo.getParameters().get(i).key;
                    int column = settingInfo.getParameters().get(i).column - 1;
                    
                    if (rowData.size() <= column) {
                        continue;
                    }
                            
                    String value = rowData.get(column);
                    if (StringUtils.isEmpty(value)) {
                        continue;
                    }
                    
                    switch (key) {
                        case FIELD_AREA:
                            if (StringUtils.isEmpty(value)) {
                                logger.warn("ImportMstLocation: Required item(area) is empty.");
                                // 取り敢えず固定名を設定
                                value = "倉庫";
                            }
                            location.setAreaName(value);
                            break;
                        case FIELD_LOC:
                            location.setLocationNo(value);
                            break;
                        case FIELD_NEW_AREA:
                            location.setJsonNewAreaName(value);
                            break;
                        case FIELD_NEW_LOC:
                            location.setJsonNewlocationNo(value);
                            break;
                        case FIELD_ORDER:
                            location.setGuideOrder(Integer.parseInt(value));
                            break;
                        case FIELD_LOC_SPEC:
                            location.setLocationSpec(value);
                            break;
                        case FIELD_TYPE:
                            location.setLocationType(Integer.parseInt(value));
                            break;
                        default:
                            break;
                    }
                }

                if (StringUtils.isEmpty(location.getAreaName())
                    && StringUtils.isEmpty(location.getLocationNo())) {
                    continue;
                }
                    
                // データが不足している
                if (StringUtils.isEmpty(location.getAreaName())
                        || StringUtils.isEmpty(location.getLocationNo())) {
                    throw new Exception("Required data is missing: " + (row + 1));
                }

                list.add(location);
            }

            return list;
            
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * フォーマットファイルを検証する。
     * 
     * @param settingInfo フォーマット情報
     * @return 
     */
    private boolean verifyFormat(WarehouseCommonSettingInfo settingInfo) {
        List<String> paramList = new ArrayList<>();
        for (int i = 0; i < settingInfo.getParameters().size(); i++) {
            paramList.add(settingInfo.getParameters().get(i).key);
        }
        return paramList.containsAll(Arrays.asList( "area", "loc" ));
    }
}
