/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.warehouseplugin;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * インポート処理クラス
 * 
 * @author s-heya
 */
public class ImportExecutor {

    private final Logger logger = LogManager.getLogger();
    private final String importRoot;
    private final String workingRoot;
    private final String settingFilePath;
    private final List<String> required;

    private String fileName;
    private FileManager fileManager;

    /**
     * コンストラクタ
     *
     * @param root 読み込みファイルのルートパス
     * @param settingRoot 設定ファイルのルートパス
     * @param settingFileName 設定ファイル名
     * @param defaultFileName インポートファイル名
     * @param required
     */
    public ImportExecutor(String root, String settingRoot, String settingFileName, String defaultFileName, List<String> required) {
        this.importRoot = root;
        this.settingFilePath = settingRoot + settingFileName;
        this.fileName = defaultFileName;
        this.required = Objects.nonNull(required) ? required : new ArrayList<>();

        this.workingRoot = System.getenv("ADFACTORY_HOME") + File.separator + "temp" + File.separator;
     }

    /**
     * 納入情報ファイルを読み込む
     *
     */
    public void importData() {
        try {
            logger.info("ImportExecutor: Start import.");

            // 設定ファイルを読み込む
            WarehouseCommonSettingInfo settingInfo = ImportUtility.readSetting(this.settingFilePath, this.required);

            if (Objects.isNull(settingInfo)) {
                logger.error("ImportExecutor: Read setting file failed.");
                return;
            }

             // ファイル名
            if (!StringUtils.isEmpty(settingInfo.getFileName())) {
                this.fileName = settingInfo.getFileName();
            }

            this.fileManager = new FileManager(this.importRoot, this.workingRoot, this.fileName);

            // 作業フォルダにピー
            String filePath = this.fileManager.findCopy();
            if (StringUtils.isEmpty(filePath)) {
                return;
            }

            // 前回ファイルを削除
            this.fileManager.cleanup();

            // ファイルを.procにリネーム
            this.fileManager.renameOrgToProc();

            // ファイルを読み込む
            List<List<String>> readData;
            if (WarehouseCommonSettingInfo.FORMAT_CSV.equalsIgnoreCase(settingInfo.getFormat())) {
                readData = ImportUtility.readCSV(filePath, settingInfo);
            } else {
                readData = ImportUtility.readExcel(filePath, settingInfo);
            }

            if (Objects.isNull(readData)) {
                throw new Exception("ImportExecutor: Read file failed.");
            }

             // ファイルから読み取ったデータをJsonに変換
            String jsonStr = this.doCconvert(readData, settingInfo);
            if (StringUtils.isEmpty(jsonStr)) {
                throw new Exception("ImportExecutor: Format change failed.");
            }

            // オブジェクトをJsonに変換  
            String jsonFileName = "import" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".json";
            String jsonFilePath = this.workingRoot + jsonFileName;

            if (this.fileManager.outputJsonFileToWorkingDir(jsonStr, jsonFileName)) {
                throw new Exception("ImportExecutor: Output josn file failed.");
            }

            // Rest APIを呼び出す
            ResponseEntity response = this.doImport(jsonFilePath);

            if (response != null && response.isSuccess()) {
                this.fileManager.renameProcToDone();
                logger.info("ImportExecutor: Import Success.");
            } else {
                this.fileManager.renameProcToError();
                logger.error("ImportExecutor: Import Error.");
            }

            this.fileManager.deleteWorkFile();
            this.fileManager.deleteJsonFile(jsonFilePath);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            this.fileManager.renameProcToError();
            this.fileManager.deleteWorkFile();
        }
    }

    /**
     * 読み取ったデータをJsonに変換する。
     *
     * @param readData 読み取ったデータ
     * @param settingInfo フォーマット情報
     * @return Json
     */
    protected String doCconvert(List<List<String>> readData, WarehouseCommonSettingInfo settingInfo) {
       return null;
    }
    
    /**
     * インポートAPIを呼び出す。
     * 
     * @param jsonFilePath Jsonファイルパス
     * @return 処理結果
     */
    protected ResponseEntity doImport(String jsonFilePath) {
        return null;
    }
}
