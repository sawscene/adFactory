/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.services;

import adtekfuji.utility.StringUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.JAXB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * インポートユーティリティー
 * 
 * @author 14-0282
 */
public class ImportUtility {

    private static final String CSV_QUOTE = "\"";

    private static final Logger logger = LogManager.getLogger();

     /**
     * 設定ファイルの読み込み
     * @param path
     * @param paramList
     * @return 
     */
    public static WarehouseCommonSettingInfo readSetting(String path, List<String> paramList) {
        WarehouseCommonSettingInfo info = null;

        try {
            File file = new File(path);
            if (file.exists()) {
                // 設定をファイルから読み込む。
                info = JAXB.unmarshal(file, WarehouseCommonSettingInfo.class);

                //エンコード値確認
                if (!"UTF-8".equals(info.getEncode())
                        && !"S-JIS".equals(info.getEncode())
                        && !"UTF-16".equals(info.getEncode())) {
                    logger.error("The set encode value is out of regulation: {}", path);
                    return null;
                }

                // カラム番号の重複確認
                ArrayList<Object> columnList = new ArrayList<>();
                for (int i = 0; i < info.getParameters().size(); i++) {
                    columnList.add(info.getParameters().get(i).column);
                }

                if ((columnList.size() != new HashSet<>(columnList).size())) {
                    logger.error("Duplicate the set column number: {} ", path);
                    return null;
                }

                // 必須パラメータの確認
                for (String param : paramList) {
                    boolean exist = false;
                    for (int i = 0; i < info.getParameters().size(); i++) {
                        String key = info.getParameters().get(i).key;
                        if (StringUtils.equals(key, param)) {
                            exist = true;
                            break;
                        }
                    }
                    
                    if (!exist) {
                        logger.error("Missing parameter({}): {}", param, path);
                        return null;
                    }
                }
            } else {
                logger.error("SettingFile does not exist: {}", path);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            info = null;
        }
        return info;
    }

    /**
     * CSV読み込み
     *
     * @param filePath　ファイルパス
     * @param settingInfo 共通インポート設定ファイルデータ
     * @return 読み込みデータ(行単位)
     */
    public static List<List<String>> readCSV(String filePath, WarehouseCommonSettingInfo settingInfo) {

        List<List<String>> rows = new ArrayList<>();

        File file = new File(filePath);
        if (!file.exists()) {
            // ファイルが存在しない
            logger.error("CSVFile does not exist:{}", filePath);
            return null;
        }

        try {
            InputStream input = new FileInputStream(filePath);

            String encode = settingInfo.getEncode();
            if ("S-JIS".equals(encode)) {
                encode = "SJIS";
            }
            InputStreamReader ireader = new InputStreamReader(input, encode);

            try (BufferedReader br = new BufferedReader(ireader)) {

                String line;
                Integer lineCnt = 0;

                while ((line = br.readLine()) != null) {

                    //開始行確認
                    lineCnt++;
                    if (lineCnt < settingInfo.getStartLine()) {
                        continue;
                    }

                    List<String> row = new ArrayList<>();

                    String[] items = line.split(settingInfo.getSeparator(), -1);
                    for (String tmp : items) {

                        //ダブルクオーテーション：全てあり設定時
                        if (settingInfo.isDoubleQuotation()) {

                            if (tmp.length() >= 2
                                    && tmp.startsWith(CSV_QUOTE)
                                    && tmp.endsWith(CSV_QUOTE)) {
                                //ダブルクオーテーション削除
                                tmp = tmp.substring(1, tmp.length() - 1);
                            }
                        }
                        row.add(tmp);
                    }
                    //1行分のデータをセット
                    rows.add(row);
                }
            } catch (IOException ex) {
                logger.fatal(ex, ex);
                return null;
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            logger.fatal(ex, ex);
            return null;
        }
        return rows;
    }

    /**
     * Excelファイル読み込み
     *
     * @param filePath　ファイルパス
     * @param settingInfo 共通インポート設定ファイルデータ
     * @return 読み込みデータ(行単位)
     */
    public static List<List<String>> readExcel(String filePath, WarehouseCommonSettingInfo settingInfo) {

        List<List<String>> rowsData = new ArrayList<>();

        File file = new File(filePath);
        if (!file.exists()) {
            // ファイルが存在しない
            logger.error("ExcelFile does not exist:{}", filePath);
            return null;
        }
        
        try (InputStream in = new FileInputStream(file); 
                Workbook excel = WorkbookFactory.create(in)) {
            int sheetNum = excel.getNumberOfSheets();
            
            for (int index = 0; index < sheetNum; index++) {
                Sheet sheet = excel.getSheetAt(index);

                // 開始・終了行数取得
                int rowStart = sheet.getFirstRowNum();
                int rowEnd = sheet.getLastRowNum();
                Integer rowCnt = rowStart;

                // 最大セル番号取得
                Integer maxColumnNum = getMaxColumnNum(settingInfo);
                if (maxColumnNum == null) {
                    logger.error("Cell number acquisition failure:{}", filePath);
                    return null;
                }

                for (int i = rowStart; i <= rowEnd; i++) {
                    rowCnt++;
                    if (rowCnt < settingInfo.getStartLine()) {
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

     /**
     *設定ファイル内 最大カラム数取得
     */
    private static Integer getMaxColumnNum(WarehouseCommonSettingInfo settingInfo) {
        Integer[] columnList;
        try {
            columnList = new Integer[settingInfo.getParameters().size()];

            for (int i = 0; i < settingInfo.getParameters().size(); i++) {
                columnList[i] = settingInfo.getParameters().get(i).column;
            }

            int maxColumnNum = columnList[0];
            for (int i = 1; i < columnList.length; i++) {
                int v = columnList[i];
                if (v > maxColumnNum) {
                    maxColumnNum = v;
                }
            }
            return maxColumnNum;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }
}
