/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adtekfuji.admanagerapp.dsitemeditplugin.controller;

import adtekfuji.utility.StringUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jp.adtekfuji.adFactory.entity.dsKanban.DsParts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * 構成部品インポート
 * 
 * @author s-heya
 */
public class ImportBomService {
    private final Logger logger = LogManager.getLogger();
    private final String PREPROCESS_REGEX = "([\\(（])?[HＨ][0-9０-９][0-9０-９][0-9０-９]([\\)）])?";

    private final File file;

    /**
     * コンストラクタ
     * @param file 部品表(エクセルファイル)
     */
    public ImportBomService(File file) {
        this.file = file;
    }

    /**
     * 構成部品情報を取り込む。
     * 
     * @return 構成部品情報一覧
     */
    public List<DsParts> importBom() {
        List<DsParts> dsPartsList = new ArrayList<>();
        Pattern pattern = Pattern.compile(PREPROCESS_REGEX);
        
        List<List<String>> rowsData = this.readExcel(this.file);
        
        int count = 1;
        for (int row = 0; row < rowsData.size(); row++) {
            List<String> rowData = rowsData.get(row);

            String productName = rowData.get(0);    // 品名
            String productNo = rowData.get(4);      // 品番

            if (!StringUtils.isEmpty(productName) && !StringUtils.isEmpty(productName)) {
                Integer quantity;                       // 数量
                try {
                    quantity = Integer.parseInt(rowData.get(9));
                } catch (Exception ex) {
                    logger.warn(ex);
                    quantity = 0;
                }

                String _preProcess = rowData.get(11);    // 前区
                String preProcess = null;

                if (!StringUtils.isEmpty(_preProcess)) {
                    Matcher m = pattern.matcher(_preProcess);
                    if (m.find()) {
                        // 前区は、最初の"Hxxx"を取り込む
                        preProcess = m.group(0);
                    }
                }

                dsPartsList.add(new DsParts(String.valueOf(count++), productNo, productName, quantity, preProcess));
            }
        }
      
        for (int row = 0; row < rowsData.size(); row++) {
            List<String> rowData = rowsData.get(row);

            String productName = rowData.get(17);   // 品名
            String productNo = rowData.get(21);     // 品番

            if (!StringUtils.isEmpty(productName) && !StringUtils.isEmpty(productName)) {
                Integer quantity;                       // 数量

                try {
                    quantity = Integer.parseInt(rowData.get(26));
                } catch (Exception ex) {
                    logger.warn(ex);
                    quantity = 0;
                }

                String _preProcess = rowData.get(28);    // 前区
                String preProcess = null;

                if (!StringUtils.isEmpty(_preProcess)) {
                    Matcher m = pattern.matcher(_preProcess);
                    if (m.find()) {
                        // 前区は、最初の"Hxxx"を取り込む
                        preProcess = m.group(0);
                    }
                }

                dsPartsList.add(new DsParts(String.valueOf(count++), productNo, productName, quantity, preProcess));
            }
        }
      
        return dsPartsList;
    }

    /**
     * エクセルファイルから値を取得する。
     * 
     * @param file
     * @return 
     */
    public List<List<String>> readExcel(File file) {

        List<List<String>> rowsData = new ArrayList<>();

        if (!file.exists()) {
            // ファイルが存在しない
            logger.error("File does not exist:{}", file.getPath());
            return null;
        }
        
        try (InputStream in = new FileInputStream(file); 
                Workbook excel = WorkbookFactory.create(in)) {
            int sheetNum = excel.getNumberOfSheets();
            
            for (int index = 0; index < sheetNum; index++) {
                Sheet sheet = excel.getSheetAt(index);

                // 開始・終了行数取得
                int rowStart = sheet.getFirstRowNum();
                int rowEnd = 24;
                int rowCnt = rowStart;

                // 最大セル番号取得
                int maxColumnNum = 34;

                for (int i = rowStart; i <= rowEnd; i++) {
                    rowCnt++;
                    if (rowCnt < 3) {
                        // 読み込み行に到達していない
                        continue;
                    }

                    Row row = sheet.getRow(i);
                    if (Objects.isNull(row)) {
                        // データが存在しない
                        break;
                    }

                    String[] rowData = new String[maxColumnNum];

                    for (int column = 0; column < maxColumnNum; column++) {

                        Cell cell = row.getCell(column);
                        if (cell == null) {
                            continue;
                        }

                        int cellType = cell.getCellType();
                        //if (column == 0 && Cell.CELL_TYPE_BLANK == cellType) {
                        //    // 取込終了
                        //    return rowsData;
                        //}

                        switch (cellType) {
                            case Cell.CELL_TYPE_NUMERIC:
                                rowData[column] = String.valueOf((int) cell.getNumericCellValue());
                                break;

                            case Cell.CELL_TYPE_STRING:
                                rowData[column] = cell.getStringCellValue();
                                break;

                            case Cell.CELL_TYPE_FORMULA:
                                switch (cell.getCachedFormulaResultType()) {
                                    case Cell.CELL_TYPE_NUMERIC:
                                        rowData[column] = String.valueOf(cell.getNumericCellValue());
                                        break;
                                    case Cell.CELL_TYPE_STRING:
                                        rowData[column] = cell.getStringCellValue();
                                        break;
                                }
                                break;

                            case Cell.CELL_TYPE_BLANK:
                                break;

                            case Cell.CELL_TYPE_BOOLEAN:
                                rowData[column] = String.valueOf(cell.getBooleanCellValue());
                                break;

                            default:
                                break;
                        }
                    }
                    rowsData.add(Arrays.asList(rowData));
                }
            }
            
            //excel.close();
            //in.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
        return rowsData;
    }  
}
