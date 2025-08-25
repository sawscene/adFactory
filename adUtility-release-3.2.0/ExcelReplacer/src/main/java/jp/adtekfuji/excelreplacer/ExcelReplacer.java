/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.excelreplacer;

import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author ke.yokoi
 */
public class ExcelReplacer {

    private static final String EXT_XLS = ",xls";
    private static final String TAG_ROW = "TAG_ROW";
    private static final String PIC_TAG_WORD = "_PIC_";
    private static final String QRCODE_TAG_WORD = "_QRCODE_";
    private static final String TAG_WORD = "TAG_";
    private static final String TAG_TM = "TAG_TM";

    public enum NumericUnitTypeEnum {

        TIME_UNIT_HOUR,
        TIME_UNIT_MINUTE,
        TIME_UNIT_SECOND,
        TIME_UNIT_MILLSECOND,
        TIME_UNIT_FORMAT,
        TIME_FORMAT_STREAM;

        public static NumericUnitTypeEnum getEnum(String key) {
            for (NumericUnitTypeEnum unit : NumericUnitTypeEnum.values()) {
                if (unit.equals(TIME_FORMAT_STREAM)) {
                    continue;
                }
                if (unit.toString().equals(key)) {
                    return unit;
                }
            }
            try {
                DateTimeFormatter.ofPattern(key);
                return TIME_FORMAT_STREAM;

            } catch (IllegalArgumentException ex) {
                return null;
            }
        }
    }

    /**
     * コンストラクタ
     */
    private ExcelReplacer() {
    }

    /**
     *
     * @param inFile
     * @param checkWords
     * @return failed replace data collections
     * @throws IOException
     */
    public static List<String> isRepraceCheck(File inFile, List<String> checkWords) throws IOException {
        //タグのリプレイス
        Workbook wb = null;
        FileInputStream file = null;
        List<String> failedReplaceData = new ArrayList<>();

        try {
            file = new FileInputStream(inFile);
            if (FilenameUtils.getExtension(inFile.getName()).equals(EXT_XLS)) {
                wb = new HSSFWorkbook(file);
            } else {
                wb = new XSSFWorkbook(file);
            }

            failedReplaceData = getFailedReplaceData(wb, checkWords);

        } finally {
            if (file != null) {
                file.close();
            }
            if (wb != null) {
                wb.close();
            }
        }
        return failedReplaceData;
    }

    /**
     * 未変換のタグ一覧を取得する。
     *
     * @param wb ワークブック
     * @param checkWords チェック対象のタグ一覧
     * @return 未変換のタグ一覧
     */
    public static List<String> getFailedReplaceData(Workbook wb, List<String> checkWords) {
        List<String> failedReplaceData = new ArrayList<>();

        Iterator iterator = wb.sheetIterator();
        while (iterator.hasNext()) {
            Sheet sheet = (Sheet) iterator.next();
            //タグが残っているか確認
            for (Row row : sheet) {
                for (Cell cell : row) {
                    // セルの型が文字列でないか、値がnullの場合は対象外。
                    if (!Objects.equals(cell.getCellType(), Cell.CELL_TYPE_STRING)
                            || Objects.isNull(cell.getStringCellValue())) {
                        continue;
                    }

                    String tagName = toUpperString(cell.getStringCellValue());

                    if (tagName.startsWith(TAG_WORD)) {
                        failedReplaceData.add(cell.getStringCellValue());
                    }
                }
            }
        }

        return failedReplaceData;
    }

    /**
     * 未変換のタグがあるセルを全て空欄にする。
     *
     * @param wb ワークブック
     * @param checkWords チェック対象のタグ一覧
     */
    public static void replaceEmpty(Workbook wb, List<String> checkWords) {
        Iterator iterator = wb.sheetIterator();
        while (iterator.hasNext()) {
            Sheet sheet = (Sheet) iterator.next();

            for (Row row : sheet) {
                for (Cell cell : row) {
                    // セルの型が文字列でないか、値がnullの場合は対象外。
                    if (!Objects.equals(cell.getCellType(), Cell.CELL_TYPE_STRING)
                            || Objects.isNull(cell.getStringCellValue())) {
                        continue;
                    }

                    String tagName = toUpperString(cell.getStringCellValue());

                    if (tagName.startsWith(TAG_WORD)) {
                        cell.setCellValue("");
                    }
                }
            }
        }
    }

    /**
     * 未変換のタグの取得及びエラー処理を行う。
     *
     * @param wb ワークブック
     * @param checkWords チェック対象のタグ一覧
     * @return 未変換タグ一覧
     */
//    public static List<String> checkFailedCell(Workbook wb, List<String> checkWords) {
//        return checkFailedCell(wb, checkWords, false, false);
//    }

    /**
     * 未変換のタグの取得及びエラー処理を行う。
     *
     * @param wb ワークブック
     * @param checkWords チェック対象のタグ一覧
     * @param isCheckKanbanNo カンバンタグをチェックする？(true:する, false:しない)
     * @param isRemoveTag 未変換のタグを削除する？(true:する, false:しない)
     * @return 未変換タグ一覧
     */
    public static List<String> checkFailedCell(Workbook wb, List<String> checkWords, boolean isCheckKanbanNo, boolean isRemoveTag, Function<String, String> tagConverter) {
        List<String> failedReplaceData = new ArrayList<>(); // 未変換タグ一覧

        // カンバンタグのパターン($番号.)
        Pattern kanbanNoPattern = Pattern.compile("^\\$[1-9][0-9]*\\.");

        // ワークブックに残っているタグを検索して処理する。
        Iterator iterator = wb.sheetIterator();
        while (iterator.hasNext()) {
            Sheet sheet = (Sheet) iterator.next();
            for (Row row : sheet) {
                for (Cell cell : row) {
                    // セルの型が文字列でないか、値がnullの場合は対象外。
                    if (!Objects.equals(cell.getCellType(), Cell.CELL_TYPE_STRING)
                            || Objects.isNull(cell.getStringCellValue())) {
                        continue;
                    }

                    // セルの値
                    String tagName = tagConverter.apply(cell.getStringCellValue());

                    // セルの値が通常タグかどうかチェックする。
                    boolean isExist = tagName.startsWith(TAG_WORD);

                    // セルの値が通常タグでない場合、カンバンタグかどうかチェックする。
                    if (!isExist
                            && isCheckKanbanNo
                            && kanbanNoPattern.matcher(tagName).find()) {
                        String kanbanTag = kanbanNoPattern.matcher(tagName).replaceFirst("");
                        isExist = kanbanTag.startsWith(TAG_WORD);
                    }

                    if (!isExist) {
                        continue;
                    }

                    // セルの値がタグだった場合、置換できなかったタグとして処理する。

                    // 未変換タグ一覧に追加する。
                    failedReplaceData.add(cell.getStringCellValue());

                    if (isRemoveTag) {
                        // 置換できなかったタグを削除する。
                        cell.setCellValue("");
                    } else {
                        // 置換できなかったタグの文字色・セルを赤く設定する

                        // セルスタイルのコピー
                        CellStyle style = wb.createCellStyle();
                        style.cloneStyleFrom(cell.getCellStyle());

                        // 背景色を薄い赤に設定する
                        style.setFillForegroundColor(IndexedColors.ROSE.getIndex()); // 背景色：薄い赤
                        style.setFillPattern(CellStyle.SOLID_FOREGROUND); // 塗り方：塗りつぶし

                        // 文字色を赤に設定する
                        Font font = wb.createFont();                                 
                        font.setFontName(wb.getFontAt(style.getFontIndex()).getFontName());
                        font.setColor(IndexedColors.DARK_RED.getIndex()); // 文字色：暗い赤
                        style.setFont(font);

                        // 設定値を更新する
                        cell.setCellStyle(style);
                    }
                }
            }
        }

        return failedReplaceData;
    }

    /**
     * Excel置換処理
     *
     * @param inFile 入力パス
     * @param outFile 出力パス
     * @param replaceWords 置換するキーと値の組み合わせ。日時指定の場合はエクセル側のタグにおいて括弧でフォーマットを指定する。
     * @throws IOException
     */
    public static void replace(File inFile, File outFile, Map<String, Object> replaceWords, Function<String, String> tagConverter) throws IOException {
        replace(inFile, outFile, replaceWords, null, null, null, tagConverter);
    }

    /**
     * Excel置換処理
     *
     * @param inFile 入力パス
     * @param outFile 出力パス
     * @param replaceWords 置換するキーと値の組み合わせ。日時指定の場合はエクセル側のタグにおいて括弧でフォーマットを指定する。
     * @param replaceRows リスト出力として置換するキーと値の組み合わせのリスト
     * @throws IOException
     */
    public static void replace(File inFile, File outFile, Map<String, Object> replaceWords, List<Map<String, Object>> replaceRows, Function<String, String> tagConverter) throws IOException {
        replace(inFile, outFile, replaceWords, replaceRows, null, null, tagConverter);
    }

    /**
     * Excel置換処理
     *
     * @param inFile 入力パス
     * @param outFile 出力パス
     * @param replaceWords 置換するキーと値の組み合わせ。日時指定の場合はエクセル側のタグにおいて括弧でフォーマットを指定する。
     * @param replaceRows リスト出力として置換するキーと値の組み合わせのリスト
     * @param isCheckKanbanNo カンバンタグをチェックする？(true:する, false:しない)
     * @param isRemoveTag 未変換のタグを削除する？(true:する, false:しない)
     * @return 未変換タグ一覧
     * @throws IOException
     */
    public static List<String> replace(File inFile, File outFile, Map<String, Object> replaceWords, List<Map<String, Object>> replaceRows, Boolean isCheckKanbanNo, Boolean isRemoveTag, Function<String, String> tagConverter) throws IOException {
        List<String> result = new ArrayList<>(); // 未変換タグ一覧

        Workbook wb = null;
        FileInputStream file = null;
        FileOutputStream fileOut = null;

        try {
            file = new FileInputStream(inFile);
            if (FilenameUtils.getExtension(inFile.getName()).equals(EXT_XLS)) {
                wb = new HSSFWorkbook(file);
            } else {
                wb = new XSSFWorkbook(file);
            }

            // ワークブックのタグを置換する。
            List<String> noReplaceTags = replaceWorkbookTags(wb, replaceWords, 0, replaceRows, isCheckKanbanNo, isRemoveTag, tagConverter);
            result.addAll(noReplaceTags);

            fileOut = new FileOutputStream(outFile);
            wb.write(fileOut);
            fileOut.flush();

        } finally {
            if (file != null) {
                file.close();
            }
            if (fileOut != null) {
                fileOut.close();
            }
            if (wb != null) {
                wb.close();
            }
        }

        return result;
    }

    /**
     * 対象ワークブックのタグを置換する。
     *
     * @param wb ワークブック
     * @param replaceWords 置換文字マップ
     * @param kanbanNo カンバン番号
     * @throws IOException
     */
    public static void replaceWorkbookTags(Workbook wb, Map<String, Object> replaceWords, int kanbanNo, Function<String, String> tagConverter) throws IOException {
        replaceWorkbookTags(wb, replaceWords, kanbanNo, null, null, null, tagConverter);
    }

    /**
     * 対象ワークブックのタグを置換する。
     *
     * @param wb ワークブック
     * @param replaceWords 置換文字マップ
     * @param kanbanNo カンバン番号
     * @throws IOException
     */
//    public static void replaceWorkbookTagsNoRecalc(Workbook wb, Map<String, Object> replaceWords, int kanbanNo) throws IOException {
//        replaceWorkbookTagsNoRecalc(wb, replaceWords, kanbanNo, null, null);
//    }

    /**
     * 対象ワークブックのタグを置換する。
     *
     * @param wb ワークブック
     * @param replaceWords 置換文字マップ
     * @param kanbanNo カンバン番号
     * @param isCheckKanbanNo カンバンタグをチェックする？(true:する, false:しない)
     * @param isRemoveTag 未変換のタグを削除する？(true:する, false:しない)
     * @return 未変換タグ一覧
     * @throws IOException
     */
//    public static List<String> replaceWorkbookTagsNoRecalc(Workbook wb, Map<String, Object> replaceWords, int kanbanNo, Boolean isCheckKanbanNo, Boolean isRemoveTag) throws IOException {
//        List<String> result = new ArrayList<>(); // 未変換タグ一覧
//
//        Iterator iterator = wb.sheetIterator();
//
//        while (iterator.hasNext()) {
//            Sheet sheet = (Sheet) iterator.next();
//
//            List<String> noReplaceTags = replaceSheetTags(sheet, replaceWords, kanbanNo, null, isCheckKanbanNo, isRemoveTag);
//            result.addAll(noReplaceTags);
//        }
//
//        return result;
//    }

    /**
     * 対象ワークブックのタグを置換する。
     *
     * @param wb ワークブック
     * @param replaceWords 置換文字マップ
     * @param kanbanNo カンバン番号
     * @param replaceRows リスト行
     * @throws IOException
     */
//    public static void replaceWorkbookTags(Workbook wb, Map<String, Object> replaceWords, int kanbanNo, List<Map<String, Object>> replaceRows) throws IOException {
//        replaceWorkbookTags(wb, replaceWords, kanbanNo, replaceRows, null, null);
//    }

    /**
     * 対象ワークブックのタグを置換する。
     *
     * @param wb ワークブック
     * @param replaceWords 置換文字マップ
     * @param kanbanNo カンバン番号
     * @param replaceRows リスト行
     * @param isCheckKanbanNo カンバンタグをチェックする？(true:する, false:しない)
     * @param isRemoveTag 未変換のタグを削除する？(true:する, false:しない)
     * @return 未変換タグ一覧
     * @throws IOException
     */
    public static List<String> replaceWorkbookTags(Workbook wb, Map<String, Object> replaceWords, int kanbanNo, List<Map<String, Object>> replaceRows, Boolean isCheckKanbanNo, Boolean isRemoveTag, Function<String, String> tagConverter) throws IOException {
        List<String> result = new ArrayList<>(); // 未変換タグ一覧

        Iterator iterator = wb.sheetIterator();

        while (iterator.hasNext()) {
            Sheet sheet = (Sheet) iterator.next();

            List<String> noReplaceTags = replaceSheetTags(sheet, replaceWords, kanbanNo, replaceRows, isCheckKanbanNo, isRemoveTag, tagConverter);
            result.addAll(noReplaceTags);
        }

        // セルの再計算
        wb.getCreationHelper().createFormulaEvaluator().evaluateAll();

        return result;
    }

    /**
     * 対象シートのタグを置換する。
     *
     * @param sheet ワークシート
     * @param replaceWords 置換文字マップ
     * @param kanbanNo カンバン番号
     * @throws IOException
     */
//    public static void replaceSheetTags(Sheet sheet, Map<String, Object> replaceWords, int kanbanNo) throws IOException {
//        replaceSheetTags(sheet, replaceWords, kanbanNo, null, null, null);
//    }

    /**
     * 対象シートのタグを置換する。
     *
     * @param sheet ワークシート
     * @param replaceWords 置換文字マップ
     * @param kanbanNo カンバン番号
     * @param replaceRows リスト行
     * @throws IOException
     */
//    public static void replaceSheetTags(Sheet sheet, Map<String, Object> replaceWords, int kanbanNo, List<Map<String, Object>> replaceRows) throws IOException {
//        replaceSheetTags(sheet, replaceWords, kanbanNo, replaceRows, null, null);
//    }


    /**
     * 最後の括弧を削除する。
     * 括弧がなければ同じ文字列を返す。
     * @param input 文字列
     * @return 最後の括弧の部分を削除した文字列
     */
    final static Pattern lastParenthesesPattern = Pattern.compile("(.*)\\([^)]+\\)$");
    public static String removeLastParenthesesContent(String input) {
        final Matcher matcher = lastParenthesesPattern.matcher(input);
        return matcher.find()
                ? matcher.group(1).trim()
                : input;
    }


    /**
     * 対象シートのタグを置換する。(isCheckKanbanNo か isRemoveTag が null の場合、未変換タグの処理なし)
     *
     * @param sheet ワークシート
     * @param replaceWords 置換文字マップ
     * @param kanbanNo カンバン番号
     * @param replaceRows リスト行
     * @param isCheckKanbanNo カンバンタグをチェックする？(true:する, false:しない)
     * @param isRemoveTag 未変換のタグを削除する？(true:する, false:しない)
     * @return 未変換タグ一覧
     * @throws IOException
     */
    public static List<String> replaceSheetTags(Sheet sheet, Map<String, Object> replaceWords, int kanbanNo, List<Map<String, Object>> replaceRows, Boolean isCheckKanbanNo, Boolean isRemoveTag, Function<String, String> tagConverter) throws IOException {
        List<String> failedReplaceData = new ArrayList<>(); // 未変換タグ一覧

        Workbook wb = sheet.getWorkbook();

        String kanbanNoTag = null;
        if (kanbanNo > 0) {
            kanbanNoTag = String.format("$%d.", kanbanNo);
        }

        if (Objects.nonNull(replaceRows) && !replaceRows.isEmpty()) {
            // TAG_ROW の行を検索
            int tagRowNo = getTagRowNo(sheet, TAG_ROW, kanbanNoTag, tagConverter);
            if (tagRowNo >= 0) {
                Row srcRow = sheet.getRow(tagRowNo);

                int dataNo = 1;
                int newRowNo = tagRowNo + 1;
                for (Map<String, Object> replaceRow : replaceRows) {
                    // 追加する行が最終行でない場合、後の行を下にずらす
                    if (sheet.getLastRowNum() > newRowNo + 1) {
                        sheet.shiftRows(newRowNo, sheet.getLastRowNum(), 1);
                    }

                    // 新しい行を追加
                    Row newRow = sheet.createRow(newRowNo);

                    // 追加した行のスタイル・セル値を設定
                    for (int col = 0; col < srcRow.getLastCellNum(); col++) {
                        Cell srcCell = srcRow.getCell(col);
                        Cell newCell = newRow.createCell(col);

                        // セルスタイルのコピー
                        CellStyle newCellStyle = wb.createCellStyle();
                        newCellStyle.cloneStyleFrom(srcCell.getCellStyle());
                        newCell.setCellStyle(newCellStyle);
                        // セルタイプのコピー
                        newCell.setCellType(srcCell.getCellType());
                        // セル値のコピー
                        switch (srcCell.getCellType()) {
                            case Cell.CELL_TYPE_BLANK:
                                newCell.setCellValue(srcCell.getStringCellValue());
                                break;
                            case Cell.CELL_TYPE_BOOLEAN:
                                newCell.setCellValue(srcCell.getBooleanCellValue());
                                break;
                            case Cell.CELL_TYPE_ERROR:
                                newCell.setCellValue(srcCell.getErrorCellValue());
                                break;
                            case Cell.CELL_TYPE_FORMULA:
                                newCell.setCellValue(srcCell.getCellFormula());
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
                                newCell.setCellValue(srcCell.getNumericCellValue());
                                break;
                            case Cell.CELL_TYPE_STRING:
                                newCell.setCellValue(srcCell.getStringCellValue());
                                break;
                            default:
                                break;
                        }
                    }

                    // リストのタグを置換
                    replaceRow.put(TAG_ROW, dataNo);
                    for (Map.Entry<String, Object> entry : replaceRow.entrySet()) {
                        List<Cell> tagCells = getTagCells(newRow, entry.getKey(), kanbanNoTag, tagConverter);
                        for (Cell tagCell : tagCells) {
                            setCellValue(wb, sheet, tagCell, entry.getValue(), tagConverter);
                        }
                    }

                    dataNo++;
                    newRowNo++;
                }

                // TAG_ROW の行を削除
                sheet.removeRow(srcRow);
                sheet.shiftRows(tagRowNo + 1, sheet.getLastRowNum(), -1);
            }
        }

        // タグを置換

        // カンバンタグのパターン($番号.)
        Pattern kanbanNoPattern = Pattern.compile("^\\$[1-9][0-9]*\\.");

        for (Row row : sheet) {
            for (Cell cell : row) {
                
                String cellValue = getCellValue(cell);
                if (StringUtils.isEmpty(cellValue)) {
                    // 値がnullの場合は対象外
                    continue;
                }

                String tagName = tagConverter.apply(cellValue);
                String some = removeLastParenthesesContent(tagName);

                boolean isReplace = false;
                for (Map.Entry<String, Object> entry : replaceWords.entrySet()) {
                    List<String> tags = getCheckTags(entry.getKey(), kanbanNoTag, tagConverter);
                    if (tags.contains(some)
                            || tags.contains(tagName)) {
                        setCellValue(wb, sheet, cell, entry.getValue(), tagConverter);
                        isReplace = true;
                        break;
                    }
                }

                // 置換された場合、もしくは未変換タグの処理なしの場合は次のセルに進む。
                if (isReplace
                        || Objects.isNull(isCheckKanbanNo)
                        || Objects.isNull(isRemoveTag)) {
                    continue;
                }

                // 未変換タグの処理

                // セルの値が通常タグかどうかチェックする。
                boolean isExist = some.startsWith(TAG_WORD);

                // セルの値が通常タグでない場合、カンバンタグかどうかチェックする。
                if (!isExist
                        && isCheckKanbanNo
                        && kanbanNoPattern.matcher(some).find()) {
                    String kanbanTag = kanbanNoPattern.matcher(some).replaceFirst("");
                    isExist = kanbanTag.startsWith(TAG_WORD);
                }

                if (!isExist) {
                    // カンバンタグではない
                    continue;
                }
                
                // セルの値がタグだった場合、置換できなかったタグとして処理する。

                // 未変換タグ一覧に追加する。
                failedReplaceData.add(cellValue);

                if (isRemoveTag) {
                    // 置換できなかったタグを削除する。
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    cell.setCellValue("");
                } else {
                    // 置換できなかったタグの文字色・セルを赤く設定する

                    // セルスタイルのコピー
                    CellStyle style = wb.createCellStyle();
                    style.cloneStyleFrom(cell.getCellStyle());

                    // 背景色を薄い赤に設定する
                    style.setFillForegroundColor(IndexedColors.ROSE.getIndex()); // 背景色：薄い赤
                    style.setFillPattern(CellStyle.SOLID_FOREGROUND); // 塗り方：塗りつぶし

                    // 文字色を赤に設定する
                    Font font = wb.createFont();                                 
                    font.setFontName(wb.getFontAt(style.getFontIndex()).getFontName());
                    font.setColor(IndexedColors.DARK_RED.getIndex()); // 文字色：暗い赤
                    style.setFont(font);

                    // 設定値を更新する
                    cell.setCellStyle(style);
                }
            }
        }

        return failedReplaceData;
    }

    /**
     * 対象シートから画像データタグのリストを作成
     *
     * @param sheet 対象シート
     * @param kanbanNo カンバン番号
     * @return 画像データタグのリスト
     */
//    public static List<String> getPictureTag(Sheet sheet, int kanbanNo) {
//        return getPictureTag(sheet, kanbanNo, false);
//    }

    /**
     * 対象シートから画像データタグのリストを作成
     *
     * @param sheet 対象シート
     * @param kanbanNo カンバン番号
     * @param isUpper タグを大文字にするか？
     * @return 画像データタグのリスト
     */
    public static List<String> getPictureTag(Sheet sheet, int kanbanNo, Function<String, String> tagConverter) {
        List<String> picTags = new ArrayList();
        for (Row row : sheet) {
            for (Cell cell : row) {
                // セルの型が文字列でないか、値がnullの場合は対象外。
                if (!Objects.equals(cell.getCellType(), Cell.CELL_TYPE_STRING)
                        || Objects.isNull(cell.getStringCellValue())) {
                    continue;
                }

                final String value = tagConverter.apply(cell.getStringCellValue());

                // リストに未追加の画像データタグの場合は追加する。
                if (value.contains(PIC_TAG_WORD) && !picTags.contains(value)) {
                    picTags.add(value);

                    if (kanbanNo > 0) {
                        // 帳票出力テンプレート複数用の処理
                        String kanbanNoTag = String.format("$%d.", kanbanNo);
                        if (value.startsWith(kanbanNoTag)) {
                            picTags.add(value.substring(3));
                        }
                    }
                }
            }
        }

        return picTags;
    }

    /**
     * 対象シートから指定タグの行インデックスを取得する
     *
     * @param sheet 対象シート
     * @param tag タグ
     * @return 行インデックス
     */
    private static int getTagRowNo(Sheet sheet, String tag, String kanbanNoTag, Function<String, String> tagConverter) {
        List<String> tags = getCheckTags(tag, kanbanNoTag, tagConverter);

        for (int i = 0; i < sheet.getLastRowNum() + 1; i++) {
            Row row = sheet.getRow(i);
            if (Objects.isNull(row)) {
                continue;
            }

            for (Cell cell : row) {
                // セルの型が文字列でないか、値がnullの場合は対象外。
                if (!Objects.equals(cell.getCellType(), Cell.CELL_TYPE_STRING)
                        || Objects.isNull(cell.getStringCellValue())) {
                    continue;
                }

                String tagName = tagConverter.apply(cell.getStringCellValue());

                if (tags.contains(tagName)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 対象行から指定タグのセルを全て取得する
     *
     * @param row 対象行
     * @param tag タグ
     * @return セルのリスト
     */
    private static List<Cell> getTagCells(Row row, String tag, String kanbanNoTag, Function<String, String> tagConverter) {
        List<Cell> cells = new ArrayList<>();
        List<String> tags = getCheckTags(tag, kanbanNoTag, tagConverter);

        for (Cell cell : row) {
            // セルの型が文字列でないか、値がnullの場合は対象外。
            if (!Objects.equals(cell.getCellType(), Cell.CELL_TYPE_STRING)
                    || Objects.isNull(cell.getStringCellValue())) {
                continue;
            }

            String tagName = tagConverter.apply(cell.getStringCellValue().split("\\(")[0]);

            if (tags.contains(tagName)) {
                cells.add(cell);
            }
        }
        return cells;
    }

    /**
     * 対象シートから指定タグのセルを全て取得する
     *
     * @param sheet 対象シート
     * @param tag タグ
     * @return セルのリスト
     */
    private static List<Cell> getTagCells(Sheet sheet, String tag, String kanbanNoTag, Function<String, String> tagConverter) {
        List<Cell> cells = new ArrayList();
        List<String> tags = getCheckTags(tag, kanbanNoTag, tagConverter);

        for (Row row : sheet) {
            for (Cell cell : row) {
                // セルの型が文字列でないか、値がnullの場合は対象外。
                if (!Objects.equals(cell.getCellType(), Cell.CELL_TYPE_STRING)
                        || Objects.isNull(cell.getStringCellValue())) {
                    continue;
                }

                String tagName = toUpperString(cell.getStringCellValue());
                String some = tagName.split("\\(")[0];

                if (tags.contains(some)
                        || tags.contains(tagName)) {
                    cells.add(cell);
                }
            }
        }

        return cells;
    }

    /**
     *
     * @param tag
     * @param kanbanNoTag
     * @return
     */
    private static List<String> getCheckTags(String tag, String kanbanNoTag, Function<String, String> tagConverter) {
        List<String> tags = new ArrayList();
        tags.add(tagConverter.apply(tag));

        if (Objects.nonNull(kanbanNoTag)) {
            // カンバン指定タグ
            String kanbanTag = new StringBuilder(kanbanNoTag).append(tag).toString();
            tags.add(tagConverter.apply(kanbanTag));
        }

        return tags;
    }

    /**
     * セルの値を取得する。
     * 
     * @param cell セル
     * @return セルの値
     */
    private static String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                // 文字列
                return cell.getRichStringCellValue().getString();
            case Cell.CELL_TYPE_FORMULA:
                // 数式
                switch(cell.getCachedFormulaResultType()) {
                    case Cell.CELL_TYPE_NUMERIC:
                        return null;
                    case Cell.CELL_TYPE_STRING:
                        return cell.getRichStringCellValue().getString();
                }
                break;
        }
        return null;
    }

    /**
     * 対象セルのセルタイプと値をセットする
     *
     * @param wb 対象ワークブック
     * @param sheet 対象ワークシート
     * @param cell 対象セル
     * @param value セルにセットする値
     * @throws IOException
     */
    private static void setCellValue(Workbook wb, Sheet sheet, Cell cell, Object value, Function<String, String> tagConverter) throws IOException {
        if (Objects.isNull(value)) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue("");
            return;
        }

        Matcher m = lastParenthesesPattern.matcher(cell.getStringCellValue());
        if (m.find()) {
            // フォーマット指定あり
            if (cell.getStringCellValue().startsWith(TAG_TM)) {
                // TAG_TM
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Object date = value;
        
                try {
                    if (value.getClass() == String.class) {
                        date = sdf.parse((String) value);
                    }

                    Pattern p3 = Pattern.compile("TAG_TM\\([^)]*\\)\\s*\\((?<format>[^)]*)\\)");
                    Matcher m3 = p3.matcher(cell.getStringCellValue());
                    if (m3.find()) {
                        String format = m3.group("format");
                        sdf = new SimpleDateFormat(format);
                    }
                } catch (Exception e) {
                }

                try {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    cell.setCellValue(sdf.format(date));
                } catch (Exception e) {
                }

            } else if (value.getClass() == Date.class) {
                // 日時
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                try {
                    Pattern p1 = Pattern.compile("\\(.*\\)");
                    Matcher m1 = p1.matcher(cell.getStringCellValue());
                    if (m1.find()) {
                        String format = m1.group();
                        sdf = new SimpleDateFormat(format.substring(1, format.length() - 1));
                    }
                } catch (Exception ex) {
                }
                cell.setCellType(Cell.CELL_TYPE_STRING);
                cell.setCellValue(sdf.format(value));

            } else if (value.getClass() == Integer.class || value.getClass() == Long.class) {
                // 整数
                Pattern p2 = Pattern.compile("\\(.*\\)");
                Matcher m2 = p2.matcher(cell.getStringCellValue());
                if (m2.find()) {
                    String numericType = m2.group();
                    numericType = numericType.substring(1, numericType.length() - 1);
                    NumericUnitTypeEnum typeEnum = NumericUnitTypeEnum.getEnum(tagConverter.apply(numericType));

                    if (!Objects.equals(typeEnum, NumericUnitTypeEnum.TIME_FORMAT_STREAM)) {
                        setNumeric(typeEnum, cell, value);
                    } else {
                        setNumeric(typeEnum, cell, value, numericType);
                    }
                }
            } else {
                cell.setCellType(Cell.CELL_TYPE_STRING);
                cell.setCellValue(value.toString());
            }
        } else if (value.getClass() == BufferedImage.class) {
            // 画像
            String tagName = tagConverter.apply(cell.getStringCellValue());
            if (tagName.contains(QRCODE_TAG_WORD)) {
                // QRコード画像
                setPictureResize(wb, sheet, cell, (BufferedImage) value, null, true, true);
            } else {
                // 他の画像
                setPicture(wb, sheet, cell, (BufferedImage) value);
            }
        } else {
            // 画像データのタグ名判定
            String tagName = tagConverter.apply(cell.getStringCellValue());
            if (tagName.contains(PIC_TAG_WORD)) {
                // リサイズ画像貼付処理
                setPictureResize(wb, sheet, cell, value.toString());
            } else {
                cell.setCellType(Cell.CELL_TYPE_STRING);
                cell.setCellValue(value.toString());
            }
        }
    }

    /**
     * ワークシートの指定位置に画像を貼り付ける。
     *
     * @param wb
     * @param sheet
     * @param cell
     * @param image
     * @throws IOException
     */
    private static void setPicture(Workbook wb, Sheet sheet, Cell cell, BufferedImage image) throws IOException {
        if (!wb.getClass().equals(XSSFWorkbook.class)) {
            //「*.xlsx」ファイル以外非対応
            return;
        }

        Integer col1 = cell.getColumnIndex();
        Integer row1 = cell.getRowIndex();
        Integer width = image.getWidth();
        Integer height = image.getHeight();
        Integer col2;
        Integer row2;

        Integer dx2 = XSSFShape.EMU_PER_PIXEL * width;
        Integer dy2 = XSSFShape.EMU_PER_PIXEL * height;

        for (col2 = col1; col2 < 65535; col2++) {
            dx2 -= XSSFShape.EMU_PER_PIXEL * (int) sheet.getColumnWidthInPixels(col2);
            if (dx2 < 0) {
                break;
            }
        }

        for (row2 = row1; row2 < 65535; row2++) {
            dy2 -= XSSFShape.EMU_PER_POINT * (int) sheet.getRow(row2).getHeightInPoints();
            if (dy2 < 0) {
                break;
            }
        }

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", stream);
            Integer pictureIdx = wb.addPicture(stream.toByteArray(), XSSFWorkbook.PICTURE_TYPE_PNG);

            Drawing dr = sheet.createDrawingPatriarch();

            XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, dx2, dy2, col1, row1, col2, row2);
            anchor.setAnchorType(XSSFClientAnchor.MOVE_DONT_RESIZE);// セルに合わせて移動するがサイズ変更はしない

            dr.createPicture(anchor, pictureIdx);
        }

        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue("");
    }

    /**
     * ワークシートの指定位置に1セル内にリサイズして画像を貼り付ける。
     * (セルが結合されている場合、結合セル全体の大きさにリサイズする)
     *
     * @param wb 対象ワークブック
     * @param sheet 対象ワークシート
     * @param cell 対象セル
     * @param picfile ファイル名(フルパス)
     * @throws IOException
     */
    private static void setPictureResize(Workbook wb, Sheet sheet, Cell cell, String picfile) throws IOException {
        if (!Objects.equals(wb.getClass(), XSSFWorkbook.class)) {
            // 「*.xlsx」ファイル以外非対応
            return;
        }
        String fileType = picfile.substring(picfile.lastIndexOf('.') + 1);
        BufferedImage picBufferedImage = ImageIO.read(new File(picfile));
        // リサイズ画像貼付処理
        setPictureResize(wb, sheet, cell, picBufferedImage, fileType, false, false);
    }

    /**
     * ワークシートの指定位置に1セル内にリサイズして画像を貼り付ける。
     * (セルが結合されている場合、結合セル全体の大きさにリサイズする)
     *
     * @param wb 対象ワークブック
     * @param sheet 対象ワークシート
     * @param cell 対象セル
     * @param picBufferedImage 画像ファイル
     * @param extension 拡張子
     * @param isCenter セルの中央に配置するか(true:中央, false:左上)
     * @param dispRuledLine 罫線が隠れないようにするか
     * @throws IOException
     */
    public static void setPictureResize(Workbook wb, Sheet sheet, Cell cell, BufferedImage picBufferedImage, String extension, boolean isCenter, boolean dispRuledLine) throws IOException {
        if (!Objects.equals(wb.getClass(), XSSFWorkbook.class)) {
            // 「*.xlsx」ファイル以外非対応
            return;
        }

        int col1 = cell.getColumnIndex();
        int row1 = cell.getRowIndex();
        int width = picBufferedImage.getWidth();
        int height = picBufferedImage.getHeight();

        int cellWidth = 0;
        int cellHeight = 0;
        int mergedColNum = 0;
        int mergedRowNum = 0;

        List<Integer> colWidthList = new LinkedList();
        List<Integer> rowHeightList = new LinkedList();

        // 対象セルが結合セルかチェックして、結合セルの場合は結合範囲全体のサイズを貼り付けサイズとする。
        boolean isMergedRegion = false;
        if (sheet.getNumMergedRegions() > 0) {
            final int row = row1;
            final int col = col1;
            Optional<CellRangeAddress> opt = sheet.getMergedRegions().stream()
                    .filter(p -> p.isInRange(row, col))
                    .findFirst();
            if (opt.isPresent()) {
                isMergedRegion = true;
                CellRangeAddress range = opt.get();

                for (int i = range.getFirstColumn(); i <= range.getLastColumn(); i++) {
                    int colWidth = (int)((double) XSSFShape.EMU_PER_POINT * getColumnWidthInPoints(wb, sheet, i));
                    colWidthList.add(colWidth);
                    cellWidth += colWidth;
                    mergedColNum++;
                }

                for (int i = range.getFirstRow(); i <= range.getLastRow(); i++) {
                    int rowHeight = (int) ((float) XSSFShape.EMU_PER_POINT * sheet.getRow(i).getHeightInPoints());
                    rowHeightList.add(rowHeight);
                    cellHeight += rowHeight;
                    mergedRowNum++;
                }
            }
        }

        // 対象セルが結合セルでない場合、対象セルのサイズを貼り付けサイズとする。
        if (!isMergedRegion) {
            cellWidth = (int)((double) XSSFShape.EMU_PER_POINT * getColumnWidthInPoints(wb, sheet, col1));
            cellHeight = (int) ((float) XSSFShape.EMU_PER_POINT * sheet.getRow(row1).getHeightInPoints());
        }

        int dx1 = 0;
        int dy1 = 0;
        int dx2 = 0;
        int dy2 = 0;

        if (dispRuledLine) {
            int mergin = XSSFShape.EMU_PER_PIXEL * 4;
            dx1 += mergin;
            dy1 += mergin;
            dx2 -= mergin;
            dy2 -= mergin;
            cellWidth -= mergin * 2;
            cellHeight -= mergin * 2;
        }

        // 画像データのEMU値
        int picWidth = XSSFShape.EMU_PER_PIXEL * width;
        int picHeight = XSSFShape.EMU_PER_PIXEL * height;

        // 幅と高さの各縮尺比率を算出
        double widthScale = (double) cellWidth / (double) picWidth;
        double heightScale = (double) cellHeight / (double) picHeight;

        // 余白EMU値初期化
        int spaceHeight = 0;
        int spaceWidth = 0;

        // 縮尺比率が低い方を基準にする
        if (widthScale < heightScale) {
            // 幅を基準にして、下端の余白を算出
            spaceHeight = cellHeight - ((int) ((double) picHeight * widthScale));
            int minRow = 0;
            if (isCenter) {
                spaceHeight /= 2;
                dy1 += spaceHeight;
                for (int rowHeight : rowHeightList) {
                    if (dy1 <= rowHeight) {
                        break;
                    }

                    dy1 -= rowHeight;
                    row1++;
                    mergedRowNum--;
                    minRow++;
                }
            }

            dy2 -= spaceHeight;
            for (int i = rowHeightList.size() - 1; i > minRow; i--) {
                int rowHeight = rowHeightList.get(i);
                if (-dy2 <= rowHeight) {
                    break;
                }

                dy2 += rowHeight;
                mergedRowNum--;
            }
        } else {
            // 高さを基準にして、右端の余白を算出
            spaceWidth = cellWidth - ((int) ((double) picWidth * heightScale));
            int minCol = 0;
            if (isCenter) {
                spaceWidth /= 2;
                dx1 += spaceWidth;
                for (int colWidth : colWidthList) {
                    if (dx1 <= colWidth) {
                        break;
                    }

                    dx1 -= colWidth;
                    col1++;
                    mergedColNum--;
                    minCol++;
                }
            }

            dx2 -= spaceWidth;
            for (int i = colWidthList.size() - 1; i > minCol; i--) {
                int colWidth = colWidthList.get(i);
                if (-dx2 <= colWidth) {
                    break;
                }

                dx2 += colWidth;
                mergedColNum--;
            }
        }

        Integer pictureIdx = null;
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {

            // 画像ファイル種別判定
            switch (Objects.nonNull(extension) ? extension : "") {
                case "png":
                    // pngで貼付
                    ImageIO.write(picBufferedImage, "png", stream);
                    pictureIdx = wb.addPicture(stream.toByteArray(), XSSFWorkbook.PICTURE_TYPE_PNG);
                    break;
                case "gif":
                    // gifで貼付
                    ImageIO.write(picBufferedImage, "gif", stream);
                    pictureIdx = wb.addPicture(stream.toByteArray(), XSSFWorkbook.PICTURE_TYPE_GIF);
                    break;
                case "bmp":
                    // bmpで貼付
                    ImageIO.write(picBufferedImage, "bmp", stream);
                    pictureIdx = wb.addPicture(stream.toByteArray(), XSSFWorkbook.PICTURE_TYPE_BMP);
                    break;
                default:
                    // jpgで貼付
                    ImageIO.write(picBufferedImage, "jpg", stream);
                    pictureIdx = wb.addPicture(stream.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
            }

            Drawing patriarch = sheet.createDrawingPatriarch();

            XSSFClientAnchor anchor;
            if (isMergedRegion) {
                // 結合セルの場合、結合範囲全体をアンカーにする。
                anchor = new XSSFClientAnchor(dx1, dy1, dx2, dy2, col1, row1, col1 + mergedColNum, row1 + mergedRowNum);
            } else {
                anchor = new XSSFClientAnchor(dx1, dy1, dx2, dy2, col1, row1, col1 + 1, row1 + 1);
            }

            // セルに合わせて移動、サイズ変更しない
            anchor.setAnchorType(XSSFClientAnchor.MOVE_DONT_RESIZE);

            patriarch.createPicture(anchor, pictureIdx);
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue("");
    }

    /**
     * セルに値をセットする。
     *
     * @param typeEnum
     * @param cell 対象セル
     * @param value 値
     */
    private static void setNumeric(ExcelReplacer.NumericUnitTypeEnum typeEnum, Cell cell, Object value) {
        setNumeric(typeEnum, cell, value, "");
    }

    /**
     * セルに値をセットする。
     *
     * @param typeEnum
     * @param cell 対象セル
     * @param value 値
     * @param format フォーマット
     */
    private static void setNumeric(ExcelReplacer.NumericUnitTypeEnum typeEnum, Cell cell, Object value, String format) {
        if (Objects.isNull(typeEnum)) {
            return;
        }

        Long time;
        if (Objects.equals(value.getClass(), Long.class)) {
            time = (Long) value;
        } else {
            time = ((Integer) value).longValue();
        }

        switch (typeEnum) {
            case TIME_UNIT_HOUR:
                time = time / (60 * 60 * 1000);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                cell.setCellValue(time.toString());
                break;
            case TIME_UNIT_MINUTE:
                time = time / (60 * 1000);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                cell.setCellValue(time.toString());
                break;
            case TIME_UNIT_SECOND:
                time = time / 1000;
                cell.setCellType(Cell.CELL_TYPE_STRING);
                cell.setCellValue(time.toString());
                break;
            case TIME_UNIT_MILLSECOND:
                cell.setCellType(Cell.CELL_TYPE_STRING);
                cell.setCellValue(time.toString());
                break;
            case TIME_UNIT_FORMAT:
                cell.setCellType(Cell.CELL_TYPE_STRING);
                cell.setCellValue(convertMillisToStringTime(time));
                break;
            case TIME_FORMAT_STREAM:
                cell.setCellType(Cell.CELL_TYPE_STRING);
                ZonedDateTime milliDate = ZonedDateTime.ofInstant(new Date(time).toInstant(), ZoneId.of("Etc/GMT"));
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
                cell.setCellValue(milliDate.format(dtf));
                break;
            default:
                cell.setCellType(Cell.CELL_TYPE_STRING);
                cell.setCellValue(time.toString());
                break;
        }
    }

    /**
     * ミリ秒を「HH:mm:ss」の文字列に変換する。
     *
     * @param msec ミリ秒
     * @return 「HH:mm:ss」の文字列
     */
    private static String convertMillisToStringTime(long msec) {
        boolean isMinus = false;
        if (msec < 0) {
            msec = Math.abs(msec);
            isMinus = true;
        }

        long hours = TimeUnit.MILLISECONDS.toHours(msec);
        msec -= TimeUnit.HOURS.toMillis(hours);

        long minutes = TimeUnit.MILLISECONDS.toMinutes(msec);
        msec -= TimeUnit.MINUTES.toMillis(minutes);

        long seconds = TimeUnit.MILLISECONDS.toSeconds(msec);

        StringBuilder sb = new StringBuilder();
        if (isMinus) {
            sb.append('-');
        }

        sb.append(String.format("%02d:%02d:%02d", hours, minutes, seconds));
        return sb.toString();
    }

    /**
     * 対象文字列の先頭が、先頭文字列リストに存在するか？
     *
     * @param checkWords 先頭文字列リスト
     * @param value 対象文字列
     * @return true: 存在する, false: 存在しない
     */
    private static boolean containsStartsWith(List<String> checkWords, String value) {
        String tagName = toUpperString(value);
        return checkWords.stream()
                .filter(checkWord -> tagName.startsWith(checkWord))
                .findFirst()
                .isPresent();
    }
    
    /**
     * 文字列を大文字に変換する。
     * 
     * @param str 文字列
     * @return 大文字に変換文字列
     */
    public static String toUpperString(String str) {
        if (!StringUtils.isEmpty(str)) {
        char[] array = str.toCharArray();
        for (int idx = 0; idx < str.length(); idx++) {
            char c = array[idx];
            if (c >= 'a' && c <= 'z') {
                array[idx] = (char) (c & -33);
            }
        }
        return new String(array);
    }
        
        return "";
    }

    /**
     * カラムの横幅ポイント数を取得する
     *
     * @param wb 対象ワークブック
     * @param sheet 対象シート
     * @param col1 対象カラム
     * 
     * @return 横幅ポイント数
     */
    private static double getColumnWidthInPoints(Workbook wb, Sheet sheet, int col1) {
        
        // 規定フォントを取得
        int fontSize = wb.getFontAt((short)0).getFontHeightInPoints();
        String fontName = wb.getFontAt((short)0).getFontName();
        java.awt.Font defaultFont = new java.awt.Font(fontName, java.awt.Font.PLAIN, fontSize);

        // 規定フォントの半角文字1文字の横幅を取得
        AffineTransform affinetransform = new AffineTransform();
        double fontWidth = defaultFont.getStringBounds("0", new FontRenderContext(affinetransform, true, true)).getWidth();

        // 「セルの横幅に何文字入るか」を取得し、半角文字の横幅を掛けて実際の横幅(ポイント)を算出
        double widthCharNum = sheet.getColumnWidth(col1) / 256.0;
        return widthCharNum * fontWidth;
    }
}
