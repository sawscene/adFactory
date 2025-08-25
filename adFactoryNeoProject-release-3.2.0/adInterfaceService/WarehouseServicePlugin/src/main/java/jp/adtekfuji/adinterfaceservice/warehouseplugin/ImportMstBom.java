/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.warehouseplugin;

import adtekfuji.clientservice.WarehouseInfoFaced;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.adinterfaceservice.entity.MstBomItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 部品構成マスタのインポート
 *
 * @author 14-0282
 */
public class ImportMstBom {

    private final String IMPORT_FILE_NAME = "mst_bom.xlsx";
    private final String SETTING_FILE_NAME = "mst_bom_setting.xml";

    private final Logger logger = LogManager.getLogger();
    private final String workingRoot;
    private final String settingFilePath;
    private final String workingPath;
    private final FileManager fileManager;

    /**
     * コンストラクタ
     * @param root 読み込みファイル ルートパス
     * @param settingRoot 設定ファイル ルートパス
     */
    public ImportMstBom(String root, String settingRoot) {
        this.workingRoot = System.getenv("ADFACTORY_HOME") + File.separator + "temp" + File.separator;
        this.workingPath = workingRoot + IMPORT_FILE_NAME;
        this.settingFilePath = settingRoot + SETTING_FILE_NAME;
        this.fileManager = new FileManager(root, workingRoot, IMPORT_FILE_NAME);
    }

    /**
     * 部品構成マスタを読み込む
     *
     */
    public void importData() {

        logger.info("ImportMstBom: Start import.");
        //作業フォルダにファイルコピー
        if (!fileManager.copyToWorkingDir()) {
            return;
        }

        //前回ファイル削除
        fileManager.deleteDoneFile();
        fileManager.deleteErrFile();
        fileManager.deleteProcFile();

        //オリジナルファイルを.procにリネーム
        fileManager.renameOrgToProc();

        //設定ファイル読み込み
        List<String> paramList = Arrays.asList("prodNo", "partsNo", "reqNum");
        WarehouseCommonSettingInfo settingInfo = ImportUtility.readSetting(settingFilePath, paramList);

        if (settingInfo == null) {
            fileManager.renameProcToError();
            fileManager.deleteWorkFile();
            logger.error("ImportMstBom: Read setting filefailed.");
            return;
        }

        //必須パラメタ確認
        if (checkRequiredParam(settingInfo)) {
            fileManager.renameProcToError();
            fileManager.deleteWorkFile();
            logger.error("ImportMstBom: Required params are missing.");
            return;
        }

        //納入情報ファイル(エクセル)読み込み
        List<List<String>> readData;
        try {
            readData = ImportUtility.readExcel(this.workingPath, settingInfo);
            if (readData == null) {
                fileManager.renameProcToError();
                fileManager.deleteWorkFile();
                logger.error("ImportMstBom: Read Excel file failed.");
                return;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            fileManager.renameProcToError();
            fileManager.deleteWorkFile();
            return;
        }

        //標準フォーマット変換
        List<MstBomItem> storeinList = changeFormat(readData, settingInfo);
        if (storeinList == null) {
            fileManager.renameProcToError();
            fileManager.deleteWorkFile();
            logger.error("ImportMstBom: Format change failed.");
            return;
        }

        //jsonファイル出力        
        String jsonStr = JsonUtils.objectToJson(storeinList);
        String jsonFileName = "add_bom" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".json";
        String jsonFilePath = this.workingRoot + jsonFileName;

        if (fileManager.outputJsonFileToWorkingDir(jsonStr, jsonFileName)) {
            fileManager.renameProcToError();
            fileManager.deleteWorkFile();
            logger.error("ImportMstBom: Output josn file failed.");

            return;
        }

        //インポート実施(EJB呼び出し)
        WarehouseInfoFaced faced = new WarehouseInfoFaced();
        ResponseEntity response = faced.importBom(jsonFilePath);

        //インポート結果確認
        checkImportResponse(response);

        fileManager.deleteWorkFile();
        fileManager.deleteJsonFile(jsonFilePath);

    }

     /**
     * 標準フォーマットに変換
     *
     * @param readData 読み込みデータ
     * @param settingInfo 設定ファイル
     * @return
     */
    private List<MstBomItem> changeFormat(List<List<String>> readData, WarehouseCommonSettingInfo settingInfo) {

        List<MstBomItem> storeinList = new ArrayList<>();

        try {
            for (int l = 0; l < readData.size(); l++) {
                MstBomItem item = new MstBomItem();
                for (int i = 0; i < settingInfo.getParameters().size(); i++) {
                    String key = settingInfo.getParameters().get(i).key;
                    Integer column = settingInfo.getParameters().get(i).column;

                    if (column > readData.get(l).size()) {
                        logger.error("ImportMstBom: The set column number is out of range.");
                        return null;
                    }

                    String val = readData.get(l).get(column - 1);
                    switch (key) {
                        case "prodNo":
                            if (StringUtils.isEmpty(val)) {
                                logger.error("ImportMstBom: Required item(prodNo) is empty.");
                                return null;
                            }
                            item.setProdNo(val);
                            break;
                        case "partsNo":
                            if (StringUtils.isEmpty(val)) {
                                logger.error("ImportMstBom: Required item(partsNo) is empty.");
                                return null;
                            }
                            item.setPartsNo(val);
                            break;
                        case "reqNum":
                            if (StringUtils.isEmpty(val)) {
                                logger.error("ImportMstBom: Required item(reqNum) is empty.");
                                return null;
                            }
                            item.setReqNum(Integer.parseInt(val));
                            break;
                        case "unitNo":
                            item.setUnitNo(val);
                            break;
                        default:
                            break;
                    }
                }
                storeinList.add(item);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
        return storeinList;
    }

     /**
     * インポート要求送信 結果確認
     *
     * @param response 要求送信 応答
     */
    private void checkImportResponse(ResponseEntity response) {
        if (response != null) {
            if (response.isSuccess()) {
                //インポート成功
                fileManager.renameProcToDone();
                logger.info("ImportMstBom:Import Success.");
                return;
            }
        }
        //インポート失敗
        fileManager.renameProcToError();
        logger.error("ImportMstBom:Import Error.");
    }

    /**
     * 必須項目確認
     *
     * @param settingInfo 設定情報
     * @return 
     */
    private boolean checkRequiredParam(WarehouseCommonSettingInfo settingInfo) {
        List<String> getInfParamList = new ArrayList<>();
        for (int i = 0; i < settingInfo.getParameters().size(); i++) {
            getInfParamList.add(settingInfo.getParameters().get(i).key);
        }

        List<String> requiredParamList = Arrays.asList("prodNo", "partsNo", "reqNum");
        for (int i = 0; i < requiredParamList.size(); i++) {
            if (!getInfParamList.contains(requiredParamList.get(i))) {
                return true;
            }
        }
        return false;
    }
}
