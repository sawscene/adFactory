/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservicecommon.plugin.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author (TST)H.Nishimura
 * @version 2.0.0
 * @since 2018/09/28
 */
public class WorkPlanExcelFileUtils {

    private final Logger logger = LogManager.getLogger();

    private final static String EXT_XLS = ",xls";
    private final static String EXT_XLMS = ",xlms";

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private XSSFWorkbook workbookXlms = null;
    private POIFSFileSystem fileXls = null;
    private HSSFWorkbook workbookXls = null;

    /**
     * コンストラクタ
     */
    public WorkPlanExcelFileUtils() {
    }

    /**
     * コンストラクタ
     *
     * @param _simpleDateFormat 日付フォーマット
     */
    public WorkPlanExcelFileUtils(SimpleDateFormat _simpleDateFormat) {
        this.simpleDateFormat = _simpleDateFormat;
    }

    /**
     * 日付パターンの取得
     *
     * @return 日付パターン
     */
    public SimpleDateFormat getSimpleDateFormat() {
        return this.simpleDateFormat;
    }

    /**
     * 日付パターンの取得
     *
     * @param _simpleDateFormat 日付パターン
     */
    public void setSimpleDateFormat(SimpleDateFormat _simpleDateFormat) {
        this.simpleDateFormat = _simpleDateFormat;
    }

    /**
     *
     * @param filename ファイル名
     * @param sheetName シート名
     * @param readStartRow 読み込み開始行
     * @param readCols 項目の最大値
     * @param readRowCount 読み込む行数 (nullの場合は、開始行以降全て)
     * @return データ
     */
    public List<List<String>> readExcel(String filename, String sheetName, int readStartRow, int readCols, Integer readRowCount) {
        logger.info(":readExcel start");
        logger.info("  filename=" + filename);
        logger.info("  sheetName=" + sheetName);
        logger.info("  readStartRow=" + readStartRow);
        logger.info("  readCols=" + readCols);

        if (filename.toLowerCase().endsWith(".xls")) {
            return this.excelXls(filename, sheetName, readStartRow, readCols, readRowCount);
        } else if (filename.toLowerCase().endsWith(".xlsx") || filename.toLowerCase().endsWith(".xlsm")) {
            return this.excelXlsx(filename, sheetName, readStartRow, readCols, readRowCount);
        }

        return null;
    }

    /**
     * Office Excel (xlsx)
     *
     * @param filename ファイル
     * @param sheetName シート名
     * @param readStartRow 読み込み開始行
     * @param readCols 項目の最大値
     * @param readRowCount 読み込む行数 (nullの場合は、開始行以降全て)
     * @return 読み込み結果
     */
    private List<List<String>> excelXlsx(String filename, String sheetName, int readStartRow, int readCols, Integer readRowCount) {
        List<List<String>> values = new ArrayList<>();

        try {
            // Excelファイル読み込み
            // ワークブック読み込み
            this.openXmls(filename);
            if (Objects.isNull(this.workbookXlms)) {
                return null;
            }

            // シート読み込み
            XSSFSheet sheet = this.workbookXlms.getSheet(sheetName);
            logger.debug("  sheet=" + sheet);
            if (sheet == null) {
                return null;
            }

            XSSFRow row;
            XSSFCell cell;
            int cntColData;

            // 読み込み開始行から読み込み
            logger.debug(" line start=" + readStartRow);
            int rowNo = 1;
            for (int i = readStartRow - 1;; i++) {
                if (Objects.nonNull(readRowCount)) {
                    if (rowNo > readRowCount) {
                        break;
                    }
                    rowNo++;
                }

                logger.debug((i + 1) + " 行目");
                cntColData = 0;

                row = sheet.getRow(i);                  // 行移動
                if (row == null) {
                    break;
                }

                List<String> col = new ArrayList<>();
                for (int j = 0; j < readCols; j++) {
                    logger.debug((j + 1) + " 項目");
                    // セルの読み込み
                    cell = row.getCell(j);

                    if (Objects.nonNull(cell)) {
                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_STRING:         // 文字列
                                logger.debug("CELL_TYPE_STRING:" + cell.getStringCellValue());
                                col.add(cell.getStringCellValue());
                                cntColData++;
                                break;
                            case Cell.CELL_TYPE_FORMULA:        // 
                                logger.debug("CELL_TYPE_FORMULA:" + cell.getCellFormula());
                                col.add(cell.getCellFormula());
                                cntColData++;
                                break;
                            case Cell.CELL_TYPE_NUMERIC:        // 数値 or 日付
                                // 日付タイプ
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    logger.debug("CELL_TYPE_DATE:" + cell.getDateCellValue() + ", " + simpleDateFormat.format(cell.getDateCellValue()));
                                    col.add(simpleDateFormat.format(cell.getDateCellValue()));
                                } // 数値タイプ
                                else {
                                    logger.debug("CELL_TYPE_NUMERIC:" + cell.getNumericCellValue() + ", " + Double.toString(cell.getNumericCellValue()));
                                    col.add(Double.toString(cell.getNumericCellValue()));
                                }
                                cntColData++;
                                break;
                            case Cell.CELL_TYPE_BOOLEAN:        // 
                                logger.debug("CELL_TYPE_BOOLEAN:" + Boolean.toString(cell.getBooleanCellValue()));
                                col.add(Boolean.toString(cell.getBooleanCellValue()));
                                cntColData++;
                                break;
                            case Cell.CELL_TYPE_ERROR:          // 
                                logger.debug("CELL_TYPE_ERROR:" + "");
                                cell.getErrorCellValue();
                            case Cell.CELL_TYPE_BLANK:          // 
                                logger.debug("CELL_TYPE_BLANK:" + "");
                                col.add("");
                                break;
                        }
                    }
                }

                if (cntColData == 0) {
                    break;
                }

                values.add(col);
            }

        } finally {
            this.closeXmls();
        }

        return values;
    }

    /**
     * Office Excel (xls)
     *
     * @param filename ファイル
     * @param sheetName シート名
     * @param readStartRow 読み込み開始行
     * @param readCols 項目の最大値
     * @return 読み込み結果
     */
    private List<List<String>> excelXls(String filename, String sheetName, int readStartRow, int readCols, Integer readRowCount) {
        List<List<String>> values = new ArrayList<>();

        try {
            // Excelファイル読み込み
            this.openXml(filename);
            if (Objects.isNull(this.fileXls)) {
                return null;
            }
            if (Objects.isNull(this.workbookXls)) {
                return null;
            }

            // シート読み込み
            HSSFSheet sheet = this.workbookXls.getSheet(sheetName);
            logger.debug("  sheet=" + sheet);
            if (Objects.isNull(sheet)) {
                return null;
            }

            HSSFRow row;
            HSSFCell cell;
            int cntColData;

            // 読み込み開始行から読み込み
            logger.debug(" line start=" + readStartRow);
            int rowNo = 1;
            for (int i = readStartRow - 1;; i++) {
                if (Objects.nonNull(readRowCount)) {
                    if (rowNo > readRowCount) {
                        break;
                    }
                    rowNo++;
                }

                logger.debug((i + 1) + " 行目");
                cntColData = 0;

                row = sheet.getRow(i);                  // 行移動
                if (row == null) {
                    break;
                }

                List<String> col = new ArrayList<>();
                for (int j = 0; j < readCols; j++) {
                    logger.debug((j + 1) + " 項目");
                    // セルの読み込み
                    cell = row.getCell(j);

                    if (Objects.nonNull(cell)) {
                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_STRING:         // 文字列
                                logger.debug("CELL_TYPE_STRING:" + cell.getStringCellValue());
                                col.add(cell.getStringCellValue());
                                cntColData++;
                                break;
                            case Cell.CELL_TYPE_FORMULA:        // 
                                logger.debug("CELL_TYPE_FORMULA:" + cell.getCellFormula());
                                col.add(cell.getCellFormula());
                                cntColData++;
                                break;
                            case Cell.CELL_TYPE_NUMERIC:        // 数値 or 日付
                                // 日付タイプ
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    logger.debug("CELL_TYPE_DATE:" + cell.getDateCellValue() + ", " + simpleDateFormat.format(cell.getDateCellValue()));
                                    col.add(simpleDateFormat.format(cell.getDateCellValue()));
                                } // 数値タイプ
                                else {
                                    logger.debug("CELL_TYPE_NUMERIC:" + cell.getNumericCellValue() + ", " + Double.toString(cell.getNumericCellValue()));
                                    col.add(Double.toString(cell.getNumericCellValue()));
                                }
                                cntColData++;
                                break;
                            case Cell.CELL_TYPE_BOOLEAN:        // 
                                logger.debug("CELL_TYPE_BOOLEAN:" + Boolean.toString(cell.getBooleanCellValue()));
                                col.add(Boolean.toString(cell.getBooleanCellValue()));
                                cntColData++;
                                break;
                            case Cell.CELL_TYPE_ERROR:          // 
                                logger.debug("CELL_TYPE_ERROR:" + "");
                                cell.getErrorCellValue();
                            case Cell.CELL_TYPE_BLANK:          // 
                                logger.debug("CELL_TYPE_BLANK:" + "");
                                col.add("");
                                break;
                        }
                    }
                }

                if (cntColData == 0) {
                    break;
                }

                values.add(col);
            }

        } finally {
            this.closeXml();
        }

        return values;
    }

    /**
     * Excelオープン
     *
     * @param filename ファイル名
     * @return 結果
     */
    public boolean open(String filename) {
        if (filename.toLowerCase().endsWith(".xls")) {
            return this.openXml(filename);
        } else if (filename.toLowerCase().endsWith(".xlsx")) {
            return this.openXmls(filename);
        }
        return false;
    }

    /**
     * Excelクローズ
     *
     * @return 結果
     */
    public boolean close() {
        boolean value = this.closeXml();
        if (!value) {
            value = this.closeXmls();
        }
        return value;
    }

    /**
     * Excelオープン(xml)
     *
     * @param filename ファイル名
     * @return 結果
     */
    private boolean openXml(String filename) {
        if (Objects.nonNull(this.fileXls)) {
            this.logger.warn(" 既にオープンしています。");
            return false;
        }

        boolean value = false;
        logger.debug("  file=" + this.fileXls);
        try {
            // Excelファイル読み込み
            this.fileXls = new POIFSFileSystem(new FileInputStream(filename));
            // ワークブック読み込み
            this.workbookXls = new HSSFWorkbook(this.fileXls);
            logger.debug("  workbook=" + this.workbookXls);
        } catch (IOException ex) {
            logger.warn(ex, ex);
        } finally {
            if (Objects.nonNull(this.fileXls) && Objects.nonNull(this.workbookXls)) {
                value = true;
            }
        }

        return value;
    }

    /**
     * Excelクローズ(xml)
     *
     * @return 結果
     */
    private boolean closeXml() {
        if (Objects.isNull(this.fileXls)) {
            this.logger.warn(" 既にクローズしています。");
            return false;
        }

        boolean value = false;
        try {
            // ワークブッククローズ
            this.workbookXls.close();
            // Excelファイルクローズ
            this.fileXls.close();
        } catch (IOException ex) {
            logger.warn(ex, ex);
        } finally {
            if (Objects.isNull(this.fileXls)) {
                value = true;
            }
        }

        return value;
    }

    /**
     * Excelオープン(xmls)
     *
     * @param filename ファイル名
     * @return 結果
     */
    private boolean openXmls(String filename) {
        if (Objects.nonNull(this.workbookXlms)) {
            this.logger.warn(" 既にオープンしています。");
            return false;
        }

        logger.debug("  workbook=" + this.workbookXlms);
        boolean value = false;
        try {
            // Excelファイル読み込み
            // ワークブック読み込み
            this.workbookXlms = new XSSFWorkbook(new FileInputStream(filename));
        } catch (IOException ex) {
            logger.warn(ex, ex);
        } finally {
            if (Objects.nonNull(this.workbookXlms)) {
                value = true;
            }
        }

        return value;
    }

    /**
     * Excelクローズ(xmls)
     *
     * @return 結果
     */
    private boolean closeXmls() {
        if (Objects.isNull(this.workbookXlms)) {
            this.logger.warn(" 既にクローズしています。");
            return false;
        }

        boolean value = false;
        try {
            // Excelファイル読み込み
            this.workbookXlms.close();
        } catch (IOException ex) {
            logger.warn(ex, ex);
        } finally {
            if (Objects.isNull(this.workbookXlms)) {
                value = true;
            }
        }

        return value;
    }

    /**
     * Office Excel (xls)
     *
     * @param sheetName シート名
     * @param readStartRow 読み込み開始行
     * @param readCols 項目の最大値
     * @return 読み込み結果
     */
    private List<List<String>> excelXls(String sheetName, int readStartRow, int readCols) {
        List<List<String>> values = new ArrayList<>();

        try {
            // シート読み込み
            HSSFSheet sheet = this.workbookXls.getSheet(sheetName);
            logger.debug("  sheet=" + sheet);
            if (Objects.isNull(sheet)) {
                return null;
            }

            HSSFRow row;
            HSSFCell cell;
            int cntColData;

            // 読み込み開始行から読み込み
            logger.debug(" line start=" + readStartRow);
            for (int i = readStartRow - 1;; i++) {
                logger.debug((i + 1) + " 行目");
                cntColData = 0;

                row = sheet.getRow(i);                  // 行移動
                if (row == null) {
                    break;
                }

                List<String> col = new ArrayList<>();
                for (int j = 0; j < readCols; j++) {
                    logger.debug((j + 1) + " 項目");
                    // セルの読み込み
                    cell = row.getCell(j);

                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_STRING:         // 文字列
                            logger.debug("CELL_TYPE_STRING:" + cell.getStringCellValue());
                            col.add(cell.getStringCellValue());
                            cntColData++;
                            break;
                        case Cell.CELL_TYPE_FORMULA:        // 
                            logger.debug("CELL_TYPE_FORMULA:" + cell.getCellFormula());
                            col.add(cell.getCellFormula());
                            cntColData++;
                            break;
                        case Cell.CELL_TYPE_NUMERIC:        // 数値 or 日付
                            // 日付タイプ
                            if (DateUtil.isCellDateFormatted(cell)) {
                                logger.debug("CELL_TYPE_DATE:" + cell.getDateCellValue() + ", " + simpleDateFormat.format(cell.getDateCellValue()));
                                col.add(simpleDateFormat.format(cell.getDateCellValue()));
                            } // 数値タイプ
                            else {
                                logger.debug("CELL_TYPE_NUMERIC:" + cell.getNumericCellValue() + ", " + Double.toString(cell.getNumericCellValue()));
                                col.add(Double.toString(cell.getNumericCellValue()));
                            }
                            cntColData++;
                            break;
                        case Cell.CELL_TYPE_BOOLEAN:        // 
                            logger.debug("CELL_TYPE_BOOLEAN:" + Boolean.toString(cell.getBooleanCellValue()));
                            col.add(Boolean.toString(cell.getBooleanCellValue()));
                            cntColData++;
                            break;
                        case Cell.CELL_TYPE_ERROR:          // 
                            logger.debug("CELL_TYPE_ERROR:" + "");
                            cell.getErrorCellValue();
                        case Cell.CELL_TYPE_BLANK:          // 
                            logger.debug("CELL_TYPE_BLANK:" + "");
                            col.add("");
                            break;
                    }
                }

                if (cntColData == 0) {
                    break;
                }

                values.add(col);
            }

        } finally {
            this.closeXml();
        }

        return values;
    }

    /**
     * Office Excel (xlsx)
     *
     * @param sheetName シート名
     * @param readStartRow 読み込み開始行
     * @param readCols 項目の最大値
     * @return 読み込み結果
     */
    private List<List<String>> excelXlsx(String sheetName, int readStartRow, int readCols) {
        List<List<String>> values = new ArrayList<>();

        try {
            // シート読み込み
            XSSFSheet sheet = this.workbookXlms.getSheet(sheetName);
            logger.debug("  sheet=" + sheet);
            if (sheet == null) {
                return null;
            }

            XSSFRow row;
            XSSFCell cell;
            int cntColData;

            // 読み込み開始行から読み込み
            logger.debug(" line start=" + readStartRow);
            for (int i = readStartRow - 1;; i++) {
                logger.debug((i + 1) + " 行目");
                cntColData = 0;

                row = sheet.getRow(i);                  // 行移動
                if (row == null) {
                    break;
                }

                List<String> col = new ArrayList<>();
                for (int j = 0; j < readCols; j++) {
                    logger.debug((j + 1) + " 項目");
                    // セルの読み込み
                    cell = row.getCell(j);

                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_STRING:         // 文字列
                            logger.debug("CELL_TYPE_STRING:" + cell.getStringCellValue());
                            col.add(cell.getStringCellValue());
                            cntColData++;
                            break;
                        case Cell.CELL_TYPE_FORMULA:        // 
                            logger.debug("CELL_TYPE_FORMULA:" + cell.getCellFormula());
                            col.add(cell.getCellFormula());
                            cntColData++;
                            break;
                        case Cell.CELL_TYPE_NUMERIC:        // 数値 or 日付
                            // 日付タイプ
                            if (DateUtil.isCellDateFormatted(cell)) {
                                logger.debug("CELL_TYPE_DATE:" + cell.getDateCellValue() + ", " + simpleDateFormat.format(cell.getDateCellValue()));
                                col.add(simpleDateFormat.format(cell.getDateCellValue()));
                            } // 数値タイプ
                            else {
                                logger.debug("CELL_TYPE_NUMERIC:" + cell.getNumericCellValue() + ", " + Double.toString(cell.getNumericCellValue()));
                                col.add(Double.toString(cell.getNumericCellValue()));
                            }
                            cntColData++;
                            break;
                        case Cell.CELL_TYPE_BOOLEAN:        // 
                            logger.debug("CELL_TYPE_BOOLEAN:" + Boolean.toString(cell.getBooleanCellValue()));
                            col.add(Boolean.toString(cell.getBooleanCellValue()));
                            cntColData++;
                            break;
                        case Cell.CELL_TYPE_ERROR:          // 
                            logger.debug("CELL_TYPE_ERROR:" + "");
                            cell.getErrorCellValue();
                        case Cell.CELL_TYPE_BLANK:          // 
                            logger.debug("CELL_TYPE_BLANK:" + "");
                            col.add("");
                            break;
                    }
                }

                if (cntColData == 0) {
                    break;
                }

                values.add(col);
            }

        } finally {
            this.closeXmls();
        }

        return values;
    }

    /**
     *
     * @param sheetName シート名
     * @param readStartRow 読み込み開始行
     * @param readCols
     * @return データ
     */
    public List<List<String>> readExcel(String sheetName, int readStartRow, int readCols) {
        logger.debug(":readExcel start");
        logger.debug("  sheetName=" + sheetName);
        logger.debug("  readStartRow=" + readStartRow);
        logger.debug("  readCols=" + readCols);

        if (Objects.nonNull(this.workbookXls)) {
            return this.excelXls(sheetName, readStartRow, readCols);
        } else if (Objects.nonNull(this.workbookXlms)) {
            return this.excelXlsx(sheetName, readStartRow, readCols);
        }

        return null;
    }

    /**
     *
     * @param value
     * @return
     */
    public static String printColumn(int value) {
        return CellReference.convertNumToColString(value);
    }

    /**
     *
     * @param value
     * @return
     */
    public static int printColumn(String value) {
        return CellReference.convertColStringToIndex(value);
    }
}
