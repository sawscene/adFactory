/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.warehouseplugin;

import adtekfuji.clientservice.WarehouseInfoFaced;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.adinterfaceservice.entity.ReqStoreInItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * 支給品リストのインポート
 * @author 18-0326
 */
public class ImportSuppliesList {

    private static final Logger logger = LogManager.getLogger();
    private String initialDataListName;   //支給品リストファイル名 先頭文字列情報

    private final String root;   //フォルダパス
    private final String importTempPath;   //作業フォルダパス
    private File[] suppliesList;   //全ての支給品リストのファイル名
    private List<File> tempSuppliesListFiles;   //全ての支給品リストのコピーしたファイル名

    private HashMap<String, String> suppliesRowInfo; //エクセル読込情報(行)
    private List<HashMap<String, String>> suppliesInfoData; //エクセル読込情報(シート)

    private List<ReqStoreInItem> jsonFileData; //JSONに変換する情報(まとめ)
    private final FileManager fileManager;

     /**
     * コンストラクタ
     *
     * @param root 読み込みファイル ルートパス
     */
    public ImportSuppliesList(String root) {
        this.root = root;
        this.importTempPath = System.getenv("ADFACTORY_HOME") + File.separator + "temp" + File.separator;
        this.fileManager = new FileManager(root, importTempPath);
    }

    /**
     * 支給品リストを読み込む
     *
     */
    public void importData() {

        logger.info("ImportSuppliesList:Start import.");

        //プロパティファイルから、ファイル名の先頭文字列情報を取得
        WarehouseConfig config = WarehouseConfig.getInstance();
        this.initialDataListName = config.getSupplyListPrefix();

        FilenameFilter filter = (File file, String str) -> str.startsWith(initialDataListName) && str.endsWith(".xlsx");

        //フォルダパス内を検索し、全ての支給品リストのファイル名を取得。
        this.suppliesList = new File(root).listFiles(filter);

        //該当ファイルなし
        if (this.suppliesList == null || this.suppliesList.length == 0) {
            logger.info("File does not exist.");
            return;
        } else {
            //前回ファイル名を取得。            
            FilenameFilter delfilter = (File file, String str) -> (str.startsWith(initialDataListName) && (str.endsWith(".proc") || str.endsWith(".done") || str.endsWith(".error")));
            File[] oldFileList = new File(root).listFiles(delfilter);

            for (File oldFile : oldFileList) {
                // 前回ファイル削除  
                String delFilePath = root + oldFile.getName();
                fileManager.deleteFile(delFilePath);
            }
        }

        //コピー先エクセルファイル
        String importTmp;

        this.tempSuppliesListFiles = new ArrayList<>();    //tempファイルリスト
        this.jsonFileData = new ArrayList<>();    //JSONファイルリスト
        File tempFile;
        int size = this.suppliesList.length;

        for (int i = 0; i < size; i++) {

            fileManager.setOrgFileName(this.suppliesList[i].getName());

            // 作業フォルダにコピー
            if (!fileManager.copyToWorkingDir()) {
                continue;
            }

            // 一時ファイル取得
            importTmp = importTempPath + this.suppliesList[i].getName();
            tempFile = new File(importTmp);

            tempSuppliesListFiles.add(tempFile);

            //オリジナルファイルを.procにリネーム
            fileManager.renameOrgToProc();

            // 一時ファイルの読み込みを実施
            this.suppliesInfoData = new ArrayList<>();

            if (!readFileForExcel(tempFile)) {
                fileManager.renameProcToError();
                fileManager.deleteWorkFile();
                logger.error("ImportSuppliesList:{} Read Excel file failed.", this.suppliesList[i].getName());
                continue;
            }

            // 標準フォーマットに変換
            if (this.suppliesInfoData != null) {
                try {
                    conversionFormat();
                } catch (Exception ex) {
                    //変換失敗
                    fileManager.renameProcToError();
                    fileManager.deleteWorkFile();
                    logger.error("ImportSuppliesList:{} Format change failed.", this.suppliesList[i].getName());
                    logger.fatal(ex, ex);
                }
            }
        }

        //jsonファイルパス設定
        String jsonString = JsonUtils.objectToJson(this.jsonFileData);
        String jsonFileName = "add_supply" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".json";
        String jsonFile = importTempPath + jsonFileName;

        //jsonファイル出力        
        if (fileManager.outputJsonFileToWorkingDir(jsonString, jsonFileName)) {
            for (File suppliesFile : suppliesList) {
                fileManager.setOrgFileName(suppliesFile.getName());
                fileManager.renameProcToError();
                fileManager.deleteWorkFile();
            }
            logger.error("ImportSuppliesList:Output josn file failed.");
            return;
        }

        //EJBロジック「納入情報のインポート」を実施
        WarehouseInfoFaced faced = new WarehouseInfoFaced();
        ResponseEntity response = faced.importSupply(jsonFile);

        if (response
                != null && response.isSuccess()) {
            // インポート成功
            for (File suppliesFile : suppliesList) {
                fileManager.setOrgFileName(suppliesFile.getName());
                fileManager.renameProcToDone();
            }
            logger.info("ImportSuppliesList:Import Success.");

        } else {
            // インポート失敗
            for (File suppliesFile : suppliesList) {
                fileManager.setOrgFileName(suppliesFile.getName());
                fileManager.renameProcToError();
            }
            logger.error("ImportSuppliesList:Import Error.");
        }

        //一時ファイル削除
        for (File suppliesFile : suppliesList) {
            fileManager.setOrgFileName(suppliesFile.getName());
            fileManager.deleteWorkFile();
        }
        //JSONファイル削除

        fileManager.deleteJsonFile(jsonFile);
    }

    /**
     * エクエルファイルの読み込み
     *
     * @param tempFail 読み込みファイル
     * @return
     */
    private boolean readFileForExcel(File tempFail) {

        boolean ret = true;
        
        try (FileInputStream importFile = new FileInputStream(tempFail)) {
            Workbook workbook = WorkbookFactory.create(importFile);
            
            String prodNo = null;
            String prodName = null;
            
            // 日付
            Row dateRow = workbook.getSheetAt(0).getRow(2);
            if (Objects.isNull(dateRow)) {
                logger.error("ImportSuppliesList: Required item(arrPlan) is empty.");
                return false;
            }

            Cell dateCell = dateRow.getCell(12);
            if (Objects.isNull(dateCell)) {
                logger.error("ImportSuppliesList: Required item(arrPlan) is empty.");
                return false;
            }
                
            for (int index = 0; index < workbook.getNumberOfSheets(); index++) {
                Sheet sheet = workbook.getSheetAt(index);
                Cell cell;

                for (int r = 7; r < 27; r++) {
                    Row row = sheet.getRow(r);
                    if (Objects.isNull(row)) {
                        break;
                    }

                    String hinCode = null;
                    
                    // 品番
                    cell = row.getCell(1);
                    if (Objects.nonNull(cell) && !StringUtils.isEmpty(cell.getStringCellValue())) {
                        hinCode = cell.getStringCellValue();
                    }

                    // 品名
                    cell = row.getCell(3);
                    if (Objects.nonNull(cell) && !StringUtils.isEmpty(cell.getStringCellValue())) {
                        prodName = cell.getStringCellValue();
                    }
                    
                    if (!StringUtils.isEmpty(hinCode)) {
                        prodNo = hinCode;
                        continue;
                    }
                    
                    this.suppliesRowInfo = new HashMap<>();

                    // 倉庫オーダー
                    cell = row.getCell(4);
                    if (Objects.isNull(cell) || StringUtils.isEmpty(cell.getStringCellValue())) {
                        logger.warn("ImportSuppliesList: Required item(supplyNo) is empty.");
                        continue;
                    } else {
                        this.suppliesRowInfo.put(Constants.PROD_NO, prodNo);
                        this.suppliesRowInfo.put(Constants.PROD_NAME, prodName);
                        this.suppliesRowInfo.put(Constants.SUPPLY_NO, cell.getStringCellValue());
                    }

                    // 製造オーダー
                    cell = row.getCell(6);
                    if (Objects.isNull(cell)) {
                        this.suppliesRowInfo.put(Constants.ORDER_NO, "");
                    } else {
                        this.suppliesRowInfo.put(Constants.ORDER_NO, cell.getStringCellValue());
                    }

                    // 今回支給数
                    cell = row.getCell(11);
                    if (Objects.isNull(cell) || StringUtils.isEmpty(cell.getStringCellValue())) {
                        logger.warn("ImportSuppliesList: Required item(arrNum) is empty.");
                        continue;
                    } else {
                        this.suppliesRowInfo.put(Constants.ARR_NUM, cell.getStringCellValue());
                    }

                    // 納入予定日
                    if (Objects.isNull(cell) || StringUtils.isEmpty(dateCell.getStringCellValue())) {
                        this.suppliesRowInfo.put(Constants.ARR_PLAN, "");
                    } else {
                        this.suppliesRowInfo.put(Constants.ARR_PLAN, dateCell.getStringCellValue());
                    }

                    this.suppliesRowInfo.put(Constants.NO, "1");

                    this.suppliesInfoData.add(this.suppliesRowInfo);
                }
            }
            
            if (Objects.nonNull(workbook)) {
                workbook.close();
            }
            
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            ret = false;
        }

        return ret;
    }

    /**
     * 標準フォーマットに変換
     * 
     * @exception Exception
     */
    private void conversionFormat() throws Exception {
        // エクセルから読み込んだ情報を精査	エクセルシートリストを一行ずつ確認
        for (int r = 0; r < this.suppliesInfoData.size(); r++) {
            ReqStoreInItem item = new ReqStoreInItem();
            Map<String, String> rowInfo = this.suppliesInfoData.get(r);
            int arrNum = Integer.parseInt(rowInfo.get("arrNum").replaceAll("[^0-9]", ""));

            item.setSupplyNo(rowInfo.get("supplyNo"));
            item.setOrderNo(rowInfo.get("orderNo"));
            item.setNo(Integer.valueOf(rowInfo.get("no")));
            item.setProdNo(rowInfo.get("prodNo"));
            item.setProdName(rowInfo.get("prodName"));
            item.setArrNum(arrNum);
            item.setArrPlan(rowInfo.get("arrPlan"));
            //item.setMod(Integer.valueOf(rowInfo.get("mod")));
            //item.setDel(Integer.valueOf(rowInfo.get("del")));
            item.setCategory(1);

            this.jsonFileData.add(item);
        }
    }
}
