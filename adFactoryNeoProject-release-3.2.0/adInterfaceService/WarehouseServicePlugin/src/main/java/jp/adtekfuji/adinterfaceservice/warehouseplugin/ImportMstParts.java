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
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.adinterfaceservice.entity.MstPartsItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 部品マスタのインポート
 *
 * @author 14-0282
 */
public class ImportMstParts {

    private final String SETTING_FILE_NAME = "mst_parts_setting.xml";

    private final Logger logger = LogManager.getLogger();
    private final String importRoot;
    private final String workingRoot;
    private final String settingFilePath;

    private String fileName = "mst_parts.xlsx";
    private String workingPath;
    private FileManager fileManager;

    /**
     * コンストラクタ
     *
     * @param root 読み込みファイル ルートパス
     * @param settingRoot 設定ファイル ルートパス
     */
    public ImportMstParts(String root, String settingRoot) {
        this.importRoot = root;
        this.workingRoot = System.getenv("ADFACTORY_HOME") + File.separator + "temp" + File.separator;
        this.workingPath = this.workingRoot + this.fileName;
        this.settingFilePath = settingRoot + SETTING_FILE_NAME;
    }

    /**
     * 部品マスタを読み込む
     */
    public void importData() {
        try {
            logger.info("ImportMstParts: Start import.");

            // 設定ファイルを読み込み
            List<String> paramList = Arrays.asList("prodNo", "prodName");
            WarehouseCommonSettingInfo settingInfo = ImportUtility.readSetting(this.settingFilePath, paramList);

            if (Objects.isNull(settingInfo)) {
                logger.error("ImportMstParts: Read setting file failed.");
                return;
            }

            // フォーマットを検証
            //if (verifyFormat(settingInfo)) {
            //    logger.error("ImportMstParts: Format file is incorrect.");
            //    return;
            //}

            // ファイル名
            if (!StringUtils.isEmpty(settingInfo.getFileName())) {
                this.fileName = settingInfo.getFileName();
                this.workingPath = this.workingRoot + this.fileName;
            }

            this.fileManager = new FileManager(this.importRoot, this.workingRoot, this.fileName);

            // 作業フォルダにピー
            if (!this.fileManager.copyToWorkingDir()) {
                return;
            }

            // 前回ファイルを削除
            this.fileManager.deleteDoneFile();
            this.fileManager.deleteErrFile();
            this.fileManager.deleteProcFile();

            // ファイルを.procにリネーム
            this.fileManager.renameOrgToProc();

            // 部品マスタを読み込み
            List<List<String>> readData;
            if (WarehouseCommonSettingInfo.FORMAT_CSV.equalsIgnoreCase(settingInfo.getFormat())) {
                readData = ImportUtility.readCSV(this.workingPath, settingInfo);
            } else {
                readData = ImportUtility.readExcel(this.workingPath, settingInfo);
            }

            if (Objects.isNull(readData)) {
                throw new Exception("ImportMstParts: Read file failed.");
            }

            // ファイルから読み取ったデータをオブジェクトに変換
            List<MstPartsItem> list = convertToObjects(readData, settingInfo);
            if (Objects.isNull(list)) {
                throw new Exception("ImportMstParts: Format change failed.");
            }

            // オブジェクトをJsonに変換
            String json = JsonUtils.objectToJson(list);
            String jsonFile = "add_parts" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".json";
            String jsonPath = this.workingRoot + jsonFile;

            if (fileManager.outputJsonFileToWorkingDir(json, jsonFile)) {
                throw new Exception("ImportMstParts: Output josn file failed.");
            }

            // Rest APIを呼び出す
            WarehouseInfoFaced faced = new WarehouseInfoFaced();
            ResponseEntity response = faced.importPartMst(jsonPath);

            if (response != null && response.isSuccess()) {
                this.fileManager.renameProcToDone();
                logger.info("ImportMstParts: Import Success.");
            } else {
                this.fileManager.renameProcToError();
                logger.error("ImportMstParts: Import Error.");
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
    private List<MstPartsItem> convertToObjects(List<List<String>> readData, WarehouseCommonSettingInfo settingInfo) {

        List<MstPartsItem> storeinList = new ArrayList<>();

        try {
            for (int l = 0; l < readData.size(); l++) {
                MstPartsItem item = new MstPartsItem();
                for (int i = 0; i < settingInfo.getParameters().size(); i++) {
                    String key = settingInfo.getParameters().get(i).key;
                    Integer column = settingInfo.getParameters().get(i).column;

                    if (column > readData.get(l).size()) {
                        logger.error("ImportMstParts: The set column number is out of range.");
                        return null;
                    }

                    String val = readData.get(l).get(column - 1);
                    switch (key) {
                        case ("prodNo"):
                            if (StringUtils.isEmpty(val)) {
                                logger.warn("ImportMstParts: Required item(ProdNo) is empty.");
                                continue;
                            }
                            item.setProdNo(val);
                            break;
                        case ("prodName"):
                            item.setProdName(val);
                            break;
                        case ("vendor"):
                            item.setVendor(val);
                            break;
                        case ("spec"):
                            item.setSpec(val);
                            break;
                        case ("figNo"):
                            item.setFigNo(val);
                            break;
                        case ("unit"):
                            item.setUnit(val);
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

        List<String> requiredParamList = Arrays.asList("prodNo");
        for (int i = 0; i < requiredParamList.size(); i++) {
            if (!paramList.contains(requiredParamList.get(i))) {
                return true;
            }
        }
        return false;
    }
}
