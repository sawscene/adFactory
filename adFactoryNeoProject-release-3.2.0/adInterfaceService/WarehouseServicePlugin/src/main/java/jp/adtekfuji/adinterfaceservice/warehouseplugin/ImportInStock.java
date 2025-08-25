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
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.adinterfaceservice.entity.InStockItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 在庫情報のインポート
 *
 * @author s-heya
 */
public class ImportInStock {

    private final Logger logger = LogManager.getLogger();

    private final String SETTING_FILE_NAME = "instock_setting.xml";
    private final String FIELD_SUPPLY_NO = "supplyNo";
    private final String FIELD_AREA_NO = "areaNo";
    private final String FIELD_AREA_NAME = "areaName";
    private final String FIELD_LOC_NO = "locNo";
    private final String FIELD_PROD_NO = "prodNo";
    private final String FIELD_PRODUCT_NAME = "productName";
    private final String FIELD_SPEC = "spec";
    private final String FIELD_IN_STOK_NUM = "inStockNum";
    private final String DUMMY_CODE = "9999";
   
    private final String importRoot;
    private final String workingRoot;
    private final String settingFilePath;

    private String fileName = "instock.csv";
    private String workingPath;
    private FileManager fileManager;

    /**
     * コンストラクタ
     * 
     * @param root インポートフォルダ
     * @param settingRoot 設定ファイル ルートパス
     */
    public ImportInStock(String root, String settingRoot) {
        this.importRoot = root;
        this.workingRoot = System.getenv("ADFACTORY_HOME") + File.separator + "temp" + File.separator;
        this.workingPath = this.workingRoot + this.fileName;
        this.settingFilePath = settingRoot + SETTING_FILE_NAME;
    }

    /**
     * 棚マスタを読み込む
     *
     */
    public void importData() {
        try {
            logger.info("ImportInStock: Start import.");

            // 設定ファイルを読み込み
            List<String> paramList = Arrays.asList(FIELD_PROD_NO, FIELD_IN_STOK_NUM);
            WarehouseCommonSettingInfo settingInfo = ImportUtility.readSetting(this.settingFilePath, paramList);

            if (Objects.isNull(settingInfo)) {
                logger.error("ImportInStock: Read setting file failed.");
                return;
            }

            // フォーマットファイルを検証
            if (this.verifyFormat(settingInfo)) {
                logger.error("ImportInStock: Format file is incorrect.");
                return;
            }

            // ファイル名
            if (!StringUtils.isEmpty(settingInfo.getFileName())) {
                this.fileName = settingInfo.getFileName();
                this.workingPath = this.workingRoot + this.fileName;
            }

            this.fileManager = new FileManager(this.importRoot, this.workingRoot, this.fileName);

            // 作業フォルダにコピー
            if (!fileManager.copyToWorkingDir()) {
                return;
            }

            // 前回ファイルを削除
            this.fileManager.deleteDoneFile();
            this.fileManager.deleteErrFile();
            this.fileManager.deleteProcFile();

            // ファイルを.procにリネーム
            this.fileManager.renameOrgToProc();

            // 在庫情報を読み込み
            List<List<String>> readData;
            if (WarehouseCommonSettingInfo.FORMAT_CSV.equalsIgnoreCase(settingInfo.getFormat())) {
                readData = ImportUtility.readCSV(this.workingPath, settingInfo);
            } else {
                readData = ImportUtility.readExcel(this.workingPath, settingInfo);
            }

            if (Objects.isNull(readData)) {
                throw new Exception("ImportInStock: Read Excel file failed.");
            }

            // ファイルから読み取ったデータをオブジェクトに変換
            List<InStockItem> inStockList = this.convertToObjects(readData, settingInfo);
            if (Objects.isNull(inStockList)) {
                throw new Exception("ImportInStock: Format change failed.");
            }

            // オブジェクトをJsonに変換
            String json = JsonUtils.objectToJson(inStockList);

            // アップロードファイルを出力
            String jsonFile = "instock_" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".json";
            String jsonPath = this.workingRoot + jsonFile;

            if (fileManager.outputJsonFileToWorkingDir(json, jsonFile)) {
                throw new Exception("ImportInStock: Output josn file failed.");
            }

            // Rest APIを呼び出す
            WarehouseInfoFaced faced = new WarehouseInfoFaced();
            ResponseEntity response = faced.importInStock(jsonPath);

            if (response != null && response.isSuccess()) {
                this.fileManager.renameProcToDone();
                logger.info("ImportInStock: Import Success.");
            } else {
                this.fileManager.renameProcToError();
                logger.error("ImportInStock: Import Error.");
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
    private List<InStockItem> convertToObjects(List<List<String>> readData, WarehouseCommonSettingInfo settingInfo) {
      
        try {
            List<InStockItem> list = new ArrayList<>();

            for (int row = 0; row < readData.size(); row++) {
                InStockItem obj = new InStockItem();
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
                        case FIELD_SUPPLY_NO:
                            obj.setSupplyNo(value);
                            break;
                        case FIELD_PROD_NO:
                            obj.setProdNo(value);
                            break;
                        case FIELD_AREA_NO:
                            obj.setAreaNo(value);
                            break;
                        case FIELD_AREA_NAME:
                            obj.setAreaName(value);
                            break;
                        case FIELD_LOC_NO:
                            obj.setLocNo(value);
                            break;
                        case FIELD_IN_STOK_NUM:
                            try {
                                int num = Integer.parseInt(value);
                                obj.setInStockNum(num);
                            } catch (NumberFormatException ex) {
                                logger.fatal(ex, ex);
                            }
                            break;
                        case FIELD_PRODUCT_NAME:
                            obj.setProdName(value);
                            break;
                        case FIELD_SPEC:
                            obj.setSpec(value);
                            break;
                        default:
                            break;
                    }
                }

                if (StringUtils.isEmpty(obj.getProdNo()) 
                        || StringUtils.isEmpty(obj.getAreaName())) {
                   continue;
                }
                
                if (StringUtils.isEmpty(obj.getSupplyNo())) {
                    //obj.setSupplyNo(DUMMY_CODE + obj.getAreaNo() + Constants.SEPARATOR + obj.getProdNo());
                    obj.setSupplyNo(obj.getAreaName() + Constants.SEPARATOR + obj.getProdNo());
                }
                    
                list.add(obj);
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
     * @return 検証結果
     */
    private boolean verifyFormat(WarehouseCommonSettingInfo settingInfo) {
        List<String> paramList = new ArrayList<>();
        for (int i = 0; i < settingInfo.getParameters().size(); i++) {
            paramList.add(settingInfo.getParameters().get(i).key);
        }
        return paramList.containsAll(Arrays.asList( "area", "loc" ));
    }
}
