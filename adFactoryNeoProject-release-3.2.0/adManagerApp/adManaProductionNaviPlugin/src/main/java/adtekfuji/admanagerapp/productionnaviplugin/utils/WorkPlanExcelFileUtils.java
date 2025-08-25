/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
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
     * Excelファイルを読み込む。
     *
     * @param filename ファイル名
     * @param sheetName シート名
     * @param readStartRow 読み込み開始行
     * @param readCols 項目の最大値
     * @param readRowCount 読み込む行数 (nullの場合は、開始行以降全て)
     * @return データ
     */
    public List<List<String>> readExcel(String filename, String sheetName, int readStartRow, int readCols, Integer readRowCount) {
        logger.info("readExcel: filename={}, sheetName={}, readStartRow={}, readCols={}", filename, sheetName, readStartRow, readCols);

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
            if (sheet == null) {
                return null;
            }

            XSSFRow row;
            XSSFCell cell;
            int cntColData;

            // 読み込み開始行から読み込み
            int rowNo = 1;
            for (int i = readStartRow - 1;; i++) {
                if (Objects.nonNull(readRowCount)) {
                    if (rowNo > readRowCount) {
                        break;
                    }
                    rowNo++;
                }

                cntColData = 0;

                row = sheet.getRow(i);                  // 行移動
                if (row == null) {
                    break;
                }

                List<String> col = new ArrayList<>();
                for (int j = 0; j < readCols; j++) {
                    // セルの読み込み
                    cell = row.getCell(j);

                    if (Objects.nonNull(cell)) {
                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_STRING:         // 文字列
                                col.add(cell.getStringCellValue());
                                cntColData++;
                                break;
                            case Cell.CELL_TYPE_FORMULA:        // 
                                col.add(cell.getRawValue());
                                cntColData++;
                                break;
                            case Cell.CELL_TYPE_NUMERIC:        // 数値 or 日付
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    // 日付
                                    Date date = this.roundDateMsec(cell.getDateCellValue());
                                    col.add(simpleDateFormat.format(date));
                                } else {
                                    // 数値
                                    col.add(cell.getRawValue());
                                }
                                cntColData++;
                                break;
                            case Cell.CELL_TYPE_BOOLEAN:        // 
                                col.add(Boolean.toString(cell.getBooleanCellValue()));
                                cntColData++;
                                break;
                            case Cell.CELL_TYPE_ERROR:          // 
                                cell.getErrorCellValue();
                                col.add("");
                                break;
                            case Cell.CELL_TYPE_BLANK:          // 
                                col.add("");
                                break;
                            default:
                                col.add("");
                        }
                    } else {
                        col.add("");
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
            if (Objects.isNull(sheet)) {
                return null;
            }

            HSSFRow row;
            HSSFCell cell;
            int cntColData;

            // 読み込み開始行から読み込み
            int rowNo = 1;
            for (int i = readStartRow - 1;; i++) {
                if (Objects.nonNull(readRowCount)) {
                    if (rowNo > readRowCount) {
                        break;
                    }
                    rowNo++;
                }

                cntColData = 0;

                row = sheet.getRow(i);                  // 行移動
                if (row == null) {
                    break;
                }

                List<String> col = new ArrayList<>();
                for (int j = 0; j < readCols; j++) {
                    // セルの読み込み
                    cell = row.getCell(j);

                    if (Objects.nonNull(cell)) {
                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_STRING:         // 文字列
                                col.add(cell.getStringCellValue());
                                cntColData++;
                                break;
                            case Cell.CELL_TYPE_FORMULA:        // 
                                String formulaString = formulaValueString(cell);
                                col.add(formulaString);
                                cntColData++;
                                break;
                            case Cell.CELL_TYPE_NUMERIC:        // 数値 or 日付
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    // 日付
                                    Date date = this.roundDateMsec(cell.getDateCellValue());
                                    col.add(simpleDateFormat.format(date));
                                } else {
                                    // 数値
                                    String numericString = formatNumericValue(cell.getNumericCellValue(), cell.getCellStyle().getDataFormat(), cell.getCellStyle().getDataFormatString());
                                    col.add(numericString);
                                }
                                cntColData++;
                                break;
                            case Cell.CELL_TYPE_BOOLEAN:        // 
                                col.add(Boolean.toString(cell.getBooleanCellValue()));
                                cntColData++;
                                break;
                            case Cell.CELL_TYPE_ERROR:          // 
                                cell.getErrorCellValue();
                                col.add("");
                                break;
                            case Cell.CELL_TYPE_BLANK:          // 
                                col.add("");
                                break;
                            default:
                                col.add("");
                        }
                    } else {
                        col.add("");
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
     * フォーミュラセルの表示文字列を取得する。
     *
     * @param cell セル
     * @return 表示文字列
     */
    private String formulaValueString(Cell cell) {
        String result = "";
        try {
            FormulaEvaluator formula = this.workbookXls.getCreationHelper().createFormulaEvaluator();
            CellValue formulaCell = formula.evaluate(cell);

            switch (formulaCell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC:
                    result = formatNumericValue(cell.getNumericCellValue(), cell.getCellStyle().getDataFormat(), cell.getCellStyle().getDataFormatString());
                    break;
                case Cell.CELL_TYPE_STRING:
                    result = formulaCell.getStringValue();
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    result = Boolean.toString(formulaCell.getBooleanValue());
                    break;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * 数値セルの表示文字列を取得する。
     *
     * @param value 数値
     * @param formatIndex フォーマット種別
     * @param formatString フォーマット文字列
     * @return 表示文字列
     */
    private String formatNumericValue(double value, int formatIndex, String formatString) {
        String result = "";
        try {
            DataFormatter formatter = new DataFormatter();
            result = formatter.formatRawCellContents(value, formatIndex, formatString);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
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
        logger.debug("  file={}", this.fileXls);
        try {
            // Excelファイル読み込み
            this.fileXls = new POIFSFileSystem(new FileInputStream(filename));
            // ワークブック読み込み
            this.workbookXls = new HSSFWorkbook(this.fileXls);
            logger.debug("  workbook={}", this.workbookXls);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
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
            if (Objects.nonNull(this.workbookXls)) {
                this.workbookXls.close();
            }
            // Excelファイルクローズ
            this.fileXls.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
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

        //logger.debug("  workbook={}", this.workbookXlms);
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
     * 日時のミリ秒を四捨五入する。
     *
     * @param date 日時
     * @return ミリ秒を四捨五入した日時
     */
    private Date roundDateMsec(Date date) {
        Date newDate = date;
        long msec = newDate.getTime() % 1000L;
        if (msec < 500) {
            newDate = new Date(newDate.getTime() - msec);
        } else {
            newDate = new Date(newDate.getTime() + (1000 - msec));
        }
        return newDate;
    }
}
