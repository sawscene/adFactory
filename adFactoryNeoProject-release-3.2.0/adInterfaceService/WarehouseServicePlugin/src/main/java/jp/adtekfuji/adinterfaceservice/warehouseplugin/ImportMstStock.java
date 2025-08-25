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
import jp.adtekfuji.adinterfaceservice.entity.MstStockItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * 保管方法マスタのインポート
 *
 * @author 18-0326
 */
public class ImportMstStock {

    private final String SETTING_FILE_NAME = "mst_stock_setting.xml";

    private final Logger logger = LogManager.getLogger();
    private final String importRoot;
    private final String workingRoot;
    private final String settingFilePath;

    private String fileName = "mst_stock.xlsx";
    private String workingPath;
    private FileManager fileManager;

    /**
     * コンストラクタ
     * @param root 読み込みファイル ルートパス
     * @param settingRoot 設定ファイル ルートパス
     */
    public ImportMstStock(String root, String settingRoot) {
        this.importRoot = root;
        this.workingRoot = System.getenv("ADFACTORY_HOME") + File.separator + "temp" + File.separator;
        this.workingPath = this.workingRoot + this.fileName;
        this.settingFilePath = settingRoot + SETTING_FILE_NAME;
    }

    /**
     * 保管方法マスタを読み込む
     */
    public void importData() {
       try {
            logger.info("ImportMstStock: Start import.");

            // 設定ファイルを読み込み
            List<String> paramList = Arrays.asList("prodNo", "area", "loc");
            WarehouseCommonSettingInfo settingInfo = ImportUtility.readSetting(this.settingFilePath, paramList);

            if (Objects.isNull(settingInfo)) {
                logger.error("ImportMstStock: Read setting file failed.");
                return;
            }

            // フォーマットを検証
            //if (verifyFormat(settingInfo)) {
            //    logger.error("ImportMstStock: Format file is incorrect.");
            //    return;
            //}

            // ファイル名
            if (!StringUtils.isEmpty(settingInfo.getFileName())) {
                this.fileName = settingInfo.getFileName();
                this.workingPath = this.workingRoot + this.fileName;
            }

            this.fileManager = new FileManager(this.importRoot, this.workingRoot, this.fileName);

            // 作業フォルダにピー
            if (!fileManager.copyToWorkingDir()) {
                return;
            }

            // 前回ファイルを削除
            this.fileManager.deleteDoneFile();
            this.fileManager.deleteErrFile();
            this.fileManager.deleteProcFile();

            // ファイルを.procにリネーム
            this.fileManager.renameOrgToProc();
            
            List<List<String>> readData;
            if (WarehouseCommonSettingInfo.FORMAT_CSV.equalsIgnoreCase(settingInfo.getFormat())) {
                readData = ImportUtility.readCSV(this.workingPath, settingInfo);
            } else {
                readData = ImportUtility.readExcel(this.workingPath, settingInfo);
            }

            if (Objects.isNull(readData)) {
                throw new Exception("ImportMstStock: Read Excel file failed.");
            }

            // ファイルから読み取ったデータをオブジェクトに変換
            List<MstStockItem> list = convertToObjects(readData, settingInfo);
            if (Objects.isNull(list)) {
                throw new Exception("ImportMstStock: Format change failed.");
            }
            
            // オブジェクトをJsonに変換
            String json = JsonUtils.objectToJson(list);
            String jsonFile = "add_stock" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".json";
            String jsonPath = this.workingRoot + jsonFile;

            if (this.fileManager.outputJsonFileToWorkingDir(json, jsonFile)) {
                throw new Exception("ImportMstStock: Output josn file failed.");
            }
            
            // Rest APIを呼び出す
            WarehouseInfoFaced faced = new WarehouseInfoFaced();
            ResponseEntity response = faced.importStock(jsonPath);

            if (response != null && response.isSuccess()) {
                this.fileManager.renameProcToDone();
                logger.info("ImportMstStock: Import Success.");
            } else {
                this.fileManager.renameProcToError();
                logger.error("ImportMstStock: Import Error.");
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
     * 読み取ったデータをオブジェクトに変換する。
     *
     * @param readData 読み取ったデータ
     * @param settingInfo フォーマット情報
     * @return オブジェクト
     */
    private List<MstStockItem> convertToObjects(List<List<String>> readData, WarehouseCommonSettingInfo settingInfo) {
        List<MstStockItem> stockItem = new ArrayList<>();

        try {
            for (int row = 0; row < readData.size(); row++) {
                MstStockItem item = new MstStockItem();
                List<String> rowData = readData.get(row);
                
                for (int i = 0; i < settingInfo.getParameters().size(); i++) {
                    String key = settingInfo.getParameters().get(i).key;
                    Integer column = settingInfo.getParameters().get(i).column;

                    if (rowData.size() < column) {
                        continue;
                    }

                    String value = rowData.get(column - 1);
                    switch (key) {
                        case ("prodNo"):
                            item.setProdNo(value);
                            break;

                        case ("area"):
                            if (StringUtils.isEmpty(value)) {
                                //logger.warn("ImportMstStock: Required item(area) is empty.");
                                // 取り敢えず固定名を設定
                                value = "倉庫";
                            }
                            item.setArea(value);
                            break;

                        case ("loc"):
                            item.setLoc(value);
                            break;

                        case ("rank"):
                            if (!StringUtils.isEmpty(value)) {
                                item.setRank(Integer.parseInt(value));
                            }
                            break;
                        default:
                            break;
                    }
                }
                
                if (StringUtils.isEmpty(item.getProdNo())
                    && StringUtils.isEmpty(item.getLoc())) {
                    logger.error("ImportMstStock: Required item (prodNo or loc) is empty.");
                    continue;
                }               
                
                stockItem.add(item);
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
        return stockItem;
    }

    /**
     * フォーマットファイルを検証する。
     * 
     * @param settingInfo フォーマット情報
     * @return 検証結果
     */
    private boolean verifyFormat(WarehouseCommonSettingInfo settingInfo) {
        List<String> getInfParamList = new ArrayList<>();
        for (int i = 0; i < settingInfo.getParameters().size(); i++) {
            getInfParamList.add(settingInfo.getParameters().get(i).key);
        }

        List<String> requiredParamList = Arrays.asList("prodNo", "area", "loc");
        for (int i = 0; i < requiredParamList.size(); i++) {
            if (!getInfParamList.contains(requiredParamList.get(i))) {
                logger.info("File does not load:{}", SETTING_FILE_NAME);
                return true;
            }
        }
        return false;
    }
}
