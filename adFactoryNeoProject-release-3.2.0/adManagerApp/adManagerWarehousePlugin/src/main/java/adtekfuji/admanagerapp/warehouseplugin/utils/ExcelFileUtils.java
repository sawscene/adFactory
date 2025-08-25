/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFPrintSetup;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFShapeGroup;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFSimpleShape;
import org.apache.poi.xssf.usermodel.XSSFTextBox;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Excelファイルユーティリティ
 *
 * @author nar-nakamura
 */
public class ExcelFileUtils {

    /**
     * ワークブックを読み込む。
     *
     * @param file Excelファイル (xlsx)
     * @return ワークブック
     * @throws Exception 
     */
    public static XSSFWorkbook loadExcelFile(File file) throws Exception {
        XSSFWorkbook workbook = null;
        try (FileInputStream fis = new FileInputStream(file)) {
            workbook = new XSSFWorkbook(fis);
        } catch (IOException ex) {
            throw ex;
        }
        return workbook;
    }

    /**
     * ワークブックを保存する。
     *
     * @param workbook ワークブック
     * @param file Excelファイル (xlsx)
     * @throws Exception 
     */
    public static void saveExcelFile(XSSFWorkbook workbook, File file) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
            fos.flush();
        } catch (IOException ex) {
            throw ex;
        }
    }

    /**
     * ワークブックのフォントリストを取得する。
     *
     * @param workbook ワークブック
     * @return フォントリスト
     */
    public static List<XSSFFont> getFontList(XSSFWorkbook workbook) {
        List<XSSFFont> fonts = new ArrayList();
        int fontNum = workbook.getNumberOfFonts();
        if (fontNum > 0) {
            for (short i = 0; i < fontNum; i++) {
                fonts.add(workbook.getFontAt(i));
            }
        }
        return fonts;
    }

    /**
     * ワークブックにフォントを追加する。
     *
     * @param workbook 追加先ワークブック
     * @param fonts 追加するフォント
     * @return フォントインデックスの変換マップ
     */
    public static Map<Short, Short> appendFontList(XSSFWorkbook workbook, List<XSSFFont> fonts) {
        Map<Short, Short> map = new HashMap();
        for (XSSFFont font : fonts) {
            XSSFFont newFont = workbook.createFont();

            XSSFFont oldFont = workbook.findFont(
                    font.getBoldweight(), font.getColor(), font.getFontHeight(), font.getFontName(),
                    font.getItalic(), font.getStrikeout(), font.getTypeOffset(), font.getUnderline());

            if (Objects.isNull(oldFont)) {
                newFont.setBold(font.getBold());
                newFont.setBoldweight(font.getBoldweight());
                newFont.setCharSet(font.getCharSet());
                if (Objects.nonNull(font.getXSSFColor())) {
                    newFont.setColor(font.getXSSFColor());
                }
                newFont.setFontHeight(font.getFontHeight());
                newFont.setFontName(font.getFontName());
                newFont.setItalic(font.getItalic());
                newFont.setStrikeout(font.getStrikeout());
                newFont.setTypeOffset(font.getTypeOffset());
                newFont.setUnderline(font.getUnderline());

                map.put(font.getIndex(), newFont.getIndex());
            } else {
                map.put(font.getIndex(), oldFont.getIndex());
            }
        }
        return map;
    }

    /**
     * ワークブックのスタイルリストを取得する。
     *
     * @param workbook ワークブック
     * @return スタイルリスト
     */
    public static List<XSSFCellStyle> getStyleList(XSSFWorkbook workbook) {
        List<XSSFCellStyle> styles = new ArrayList();
        int styleNum = workbook.getNumCellStyles();
        if (styleNum > 0) {
            for (short i = 0; i < styleNum; i++) {
                styles.add(workbook.getCellStyleAt(i));
            }
        }
        return styles;
    }

    /**
     * ワークブックにスタイルを追加する。
     *
     * @param workbook 追加先ワークブック
     * @param styles 追加するスタイル
     * @param fontConvMap フォントインデックスの変換マップ
     * @return スタイルインデックスの変換マップ
     */
    public static Map<Short, Short> appendStyleList(XSSFWorkbook workbook, List<XSSFCellStyle> styles, Map<Short, Short> fontConvMap) {
        Map<Short, Short> map = new HashMap();

        for (XSSFCellStyle style : styles) {
            XSSFCellStyle newStyle = workbook.createCellStyle();

            short fontId = fontConvMap.get(style.getFontIndex());
            XSSFFont font = workbook.getFontAt(fontId);

            newStyle.setAlignment(style.getAlignment());
            newStyle.setBorderBottom(style.getBorderBottom());
            newStyle.setBorderLeft(style.getBorderLeft());
            newStyle.setBorderRight(style.getBorderRight());
            newStyle.setBorderTop(style.getBorderTop());
            if (Objects.nonNull(style.getBottomBorderXSSFColor())) {
                newStyle.setBottomBorderColor(style.getBottomBorderXSSFColor());
            }
            newStyle.setDataFormat(style.getDataFormat());
            if (Objects.nonNull(style.getFillBackgroundXSSFColor())) {
                newStyle.setFillBackgroundColor(style.getFillBackgroundXSSFColor());
            }
            if (Objects.nonNull(style.getFillForegroundXSSFColor())) {
                newStyle.setFillForegroundColor(style.getFillForegroundXSSFColor());
            }
            newStyle.setFillPattern(style.getFillPattern());
            newStyle.setFont(font);
            newStyle.setHidden(style.getHidden());
            newStyle.setIndention(style.getIndention());
            if (Objects.nonNull(style.getLeftBorderXSSFColor())) {
                newStyle.setLeftBorderColor(style.getLeftBorderXSSFColor());
            }
            newStyle.setLocked(style.getLocked());
            if (Objects.nonNull(style.getRightBorderXSSFColor())) {
                newStyle.setRightBorderColor(style.getRightBorderXSSFColor());
            }
            newStyle.setRotation(style.getRotation());
            if (Objects.nonNull(style.getTopBorderXSSFColor())) {
                newStyle.setTopBorderColor(style.getTopBorderXSSFColor());
            }
            newStyle.setVerticalAlignment(style.getVerticalAlignment());
            newStyle.setWrapText(style.getWrapText());

            map.put(style.getIndex(), newStyle.getIndex());
        }
        return map;
    }

    /**
     * シートをコピーする。
     *
     * @param srcSheet コピー元シート
     * @param destSheet コピー先シート
     * @param styleConvMap スタイルインデックスの変換マップ
     */
    public static void copySheet(XSSFSheet srcSheet, XSSFSheet destSheet, Map<Short, Short> styleConvMap) {
        short maxColumnNum = 0;

        XSSFWorkbook srcWorkbook = srcSheet.getWorkbook();
        XSSFWorkbook destWorkbook = destSheet.getWorkbook();

        // 印刷範囲をコピーする。
        String srcPrintArea = srcWorkbook.getPrintArea(srcWorkbook.getSheetIndex(srcSheet));
        if (Objects.nonNull(srcPrintArea)) {
            // 取得した印刷範囲は「シート名!印刷範囲」となっているので、印刷範囲の部分だけ取得してコピー先シートに設定する。
            int pos = srcPrintArea.indexOf("!");
            if (pos >= 0) {
                String printArea = srcPrintArea.substring(pos + 1);
                destWorkbook.setPrintArea(destWorkbook.getSheetIndex(destSheet), printArea);
            }
        }

        destSheet.setDisplayGridlines(srcSheet.isDisplayGridlines());
        destSheet.setPrintGridlines(srcSheet.isPrintGridlines());

        destSheet.setFitToPage(srcSheet.getFitToPage());

        if (Objects.nonNull(srcSheet.getColumnBreaks())) {
            for (int i = 0; i < srcSheet.getColumnBreaks().length; i++) {
                destSheet.setColumnBreak(srcSheet.getColumnBreaks()[i]);
            }
        }

        if (Objects.nonNull(srcSheet.getRowBreaks())) {
            for (int i = 0; i < srcSheet.getRowBreaks().length; i++) {
                destSheet.setRowBreak(srcSheet.getRowBreaks()[i]);
            }
        }

        destSheet.setAutobreaks(srcSheet.getAutobreaks());

        // シートのヘッダーをコピーする。
        copySheetHeader(srcSheet, destSheet);
        // シートのフッターをコピーする。
        copySheetFooter(srcSheet, destSheet);

        // シートの行を１行づつコピーする。
        for (int i = srcSheet.getFirstRowNum(); i <= srcSheet.getLastRowNum(); i++) {
            XSSFRow srcRow = srcSheet.getRow(i);
            if (Objects.isNull(srcRow)) {
                continue;
            }

            XSSFRow destRow = destSheet.createRow(i);

            // 行をコピーする。
            copyRow(srcRow, destRow, styleConvMap);

            // 最終カラム番号
            if (srcRow.getLastCellNum() > maxColumnNum) {
                maxColumnNum = srcRow.getLastCellNum();
            }
        }

        // カラムの幅をコピーする。
        for (int i = 0; i <= maxColumnNum; i++) {
            destSheet.setColumnWidth(i, srcSheet.getColumnWidth(i));
        }

        // セルの結合情報をコピーする。
        for (int i = 0; i < srcSheet.getNumMergedRegions(); i++) {
            CellRangeAddress range = srcSheet.getMergedRegion(i);
            destSheet.addMergedRegion(range);
        }

        // シートのシェイプをコピーする。
        copySheetShapes(srcSheet, destSheet);
        // シートの印刷設定をコピーする。
        copySheetPrintSetup(srcSheet, destSheet);
    }

    /**
     * 行をコピーする。
     *
     * @param srcRow コピー元の行
     * @param destRow コピー先の行
     * @param styleConvMap スタイルインデックスの変換マップ
     */
    private static void copyRow(XSSFRow srcRow, XSSFRow destRow, Map<Short, Short> styleConvMap) {
        destRow.setHeight(srcRow.getHeight());

        if (srcRow.getFirstCellNum() < 0) {
            return;
        }

        for (int i = srcRow.getFirstCellNum(); i <= srcRow.getLastCellNum(); i++) {
            XSSFCell srcCell = srcRow.getCell(i);
            if (Objects.isNull(srcCell)) {
                continue;
            }

            XSSFCell destCell = destRow.getCell(i); 
            if (Objects.isNull(destCell)) {
                destCell = destRow.createCell(i);
            }

            copyCell(srcCell, destCell, styleConvMap);
        }
    }

    /**
     * セルをコピーする。
     *
     * @param srcCell コピー元のセル
     * @param destCell コピー先のセル
     * @param styleConvMap スタイルインデックスの変換マップ
     */
    private static void copyCell(XSSFCell srcCell, XSSFCell destCell, Map<Short, Short> styleConvMap) {
        XSSFWorkbook srcWorkbook = srcCell.getSheet().getWorkbook();
        XSSFWorkbook destWorkbook = destCell.getSheet().getWorkbook();

        if (srcWorkbook.equals(destWorkbook)) {
            // 同じワークブックの場合、そのままスタイルをコピーする。
            destCell.setCellStyle(srcCell.getCellStyle());
        } else {
            // 別のワークブックの場合
            short destStyleId = styleConvMap.get(srcCell.getCellStyle().getIndex());
            XSSFCellStyle destCellStyle = destWorkbook.getCellStyleAt(destStyleId);

            destCell.setCellStyle(destCellStyle);
        }

        switch (srcCell.getCellType()) {
            case XSSFCell.CELL_TYPE_STRING:
                destCell.setCellValue(srcCell.getStringCellValue());
                break;
            case XSSFCell.CELL_TYPE_NUMERIC:
                destCell.setCellValue(srcCell.getNumericCellValue());
                break;
            case XSSFCell.CELL_TYPE_BLANK:
                destCell.setCellValue(srcCell.getStringCellValue());
                break;
            case XSSFCell.CELL_TYPE_BOOLEAN:
                destCell.setCellValue(srcCell.getBooleanCellValue());
                break;
            case XSSFCell.CELL_TYPE_ERROR:
                destCell.setCellErrorValue(srcCell.getErrorCellValue());
                break;
            case XSSFCell.CELL_TYPE_FORMULA:
                destCell.setCellFormula(srcCell.getCellFormula());
                break;
            default:
                break;
        }
    }

    /**
     * シートのヘッダーをコピーする。
     *
     * @param srcSheet コピー元のシート
     * @param destSheet コピー先のシート
     */
    private static void copySheetHeader(XSSFSheet srcSheet, XSSFSheet destSheet) {
        Header srcHeader = srcSheet.getHeader();
        Header destHeader = destSheet.getHeader();

        destHeader.setCenter(srcHeader.getCenter());
        destHeader.setLeft(srcHeader.getLeft());
        destHeader.setRight(srcHeader.getRight());
    }

    /**
     * シートのフッターをコピーする。
     *
     * @param srcSheet コピー元のシート
     * @param destSheet コピー先のシート
     */
    private static void copySheetFooter(XSSFSheet srcSheet, XSSFSheet destSheet) {
        Footer srcFooter = srcSheet.getFooter();
        Footer destFooter = destSheet.getFooter();

        destFooter.setCenter(srcFooter.getCenter());
        destFooter.setLeft(srcFooter.getLeft());
        destFooter.setRight(srcFooter.getRight());
    }
    
    /**
     * シートの印刷設定をコピーする。
     *
     * @param srcSheet コピー元のシート
     * @param destSheet コピー先のシート
     */
    private static void copySheetPrintSetup(XSSFSheet srcSheet, XSSFSheet destSheet) {
        XSSFPrintSetup srcPrintSetup = srcSheet.getPrintSetup();
        XSSFPrintSetup destPrintSetup = destSheet.getPrintSetup();

        destPrintSetup.setPaperSize(srcPrintSetup.getPaperSize());
        destPrintSetup.setScale(srcPrintSetup.getScale());
        destPrintSetup.setPageStart(srcPrintSetup.getPageStart());
        destPrintSetup.setFitWidth(srcPrintSetup.getFitWidth());
        destPrintSetup.setFitHeight(srcPrintSetup.getFitHeight());
        destPrintSetup.setLeftToRight(srcPrintSetup.getLeftToRight());
        destPrintSetup.setLandscape(srcPrintSetup.getLandscape());
        destPrintSetup.setValidSettings(srcPrintSetup.getValidSettings());
        destPrintSetup.setNoColor(srcPrintSetup.getNoColor());
        destPrintSetup.setDraft(srcPrintSetup.getDraft());
        destPrintSetup.setNotes(srcPrintSetup.getNotes());
        destPrintSetup.setNoOrientation(srcPrintSetup.getNoOrientation());
        destPrintSetup.setUsePage(srcPrintSetup.getUsePage());
        destPrintSetup.setHResolution(srcPrintSetup.getHResolution());
        destPrintSetup.setVResolution(srcPrintSetup.getVResolution());
        destPrintSetup.setHeaderMargin(srcPrintSetup.getHeaderMargin());
        destPrintSetup.setFooterMargin(srcPrintSetup.getFooterMargin());
        destPrintSetup.setCopies(srcPrintSetup.getCopies());
        destPrintSetup.setOrientation(srcPrintSetup.getOrientation());
    }

    /**
     * シートのシェイプをコピーする。
     *
     * @param srcSheet コピー元のシート
     * @param destSheet コピー先のシート
     */
    private static void copySheetShapes(XSSFSheet srcSheet, XSSFSheet destSheet) {
        if (Objects.nonNull(srcSheet.getDrawingPatriarch()) && Objects.nonNull(srcSheet.getDrawingPatriarch().getShapes())) {
            for (XSSFShape shape : srcSheet.getDrawingPatriarch().getShapes()) {
                if (shape instanceof XSSFPicture) {
                    // TODO: 表示倍率が変わってしまう場合がある。
//                    System.out.println("***** shape instanceof XSSFPicture");
                    XSSFPicture srcPicture = (XSSFPicture) shape;
                    XSSFClientAnchor clientAnchor = srcPicture.getPreferredSize();

                    XSSFPictureData srcPictureData = srcPicture.getPictureData();
                    int destPictureId = destSheet.getWorkbook().addPicture(srcPictureData.getData(), srcPictureData.getPictureType());

                    XSSFDrawing draw = destSheet.createDrawingPatriarch();
                    draw.createPicture(clientAnchor, destPictureId);

                } else if (shape instanceof XSSFTextBox) {
//                    System.out.println("***** shape instanceof XSSFTextBox");
                    XSSFTextBox srcTextBox = (XSSFTextBox) shape;
                    XSSFClientAnchor clientAnchor = (XSSFClientAnchor) srcTextBox.getAnchor();

                    XSSFDrawing draw = destSheet.createDrawingPatriarch();
                    XSSFTextBox destTextBox = draw.createTextbox(clientAnchor);

                    destTextBox.getCTShape().set(srcTextBox.getCTShape().copy());

                } else  if (shape instanceof XSSFSimpleShape) {
//                    System.out.println("***** shape instanceof XSSFSimpleShape");
                    XSSFSimpleShape srcShape = (XSSFSimpleShape) shape;
                    XSSFClientAnchor clientAnchor = (XSSFClientAnchor) srcShape.getAnchor();

                    XSSFDrawing draw = destSheet.createDrawingPatriarch();
                    XSSFSimpleShape destShape = draw.createSimpleShape(clientAnchor);

                    destShape.getCTShape().set(srcShape.getCTShape().copy());

                } else if (shape instanceof XSSFShapeGroup) {
                    // TODO: グループ内のピクチャの参照がコピーされていない。
//                    System.out.println("***** shape instanceof XSSFShapeGroup");
                    XSSFShapeGroup srcGroup = (XSSFShapeGroup) shape;
                    XSSFClientAnchor clientAnchor = (XSSFClientAnchor) srcGroup.getAnchor();

                    XSSFDrawing draw = destSheet.createDrawingPatriarch();
                    XSSFShapeGroup destGroup = draw.createGroup(clientAnchor);

                    destGroup.getCTGroupShape().set(srcGroup.getCTGroupShape().copy());

                } else {
//                    System.out.println("***** shape instanceof etc");
                }
            }
        }
    }
}
