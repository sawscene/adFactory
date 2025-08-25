/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.common;

import adtekfuji.admanagerapp.warehouseplugin.entity.AcceptanceInfo;
import adtekfuji.admanagerapp.warehouseplugin.utils.ExcelFileUtils;
import adtekfuji.barcode.Barcode;
import adtekfuji.utility.StringUtils;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jp.adtekfuji.excelreplacer.ExcelReplacer;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Excel帳票
 *
 * @author s-morita
 */
public class ExcelReportFactory {

    private final Logger logger = LogManager.getLogger();

    private final Map<String, List<String>> ledgerFileDatas = new HashMap();

    private final Map<String, XSSFWorkbook> workbooks = new HashMap();

    /**
     * コンストラクタ
     */
    public ExcelReportFactory() {
    }

    /**
     * テンプレートファイルを読み込む。
     *
     * @param templateFile テンプレートファイル
     * @return
     */
    public boolean loadTemplateWorkbook(File templateFile) {
        try {
            // ワークブック名
            String templateName = templateFile.getName().substring(0, templateFile.getName().lastIndexOf('.'));
            if (workbooks.containsKey(templateName)) {
                return true;
            }

            XSSFWorkbook templateWorkbook = ExcelFileUtils.loadExcelFile(templateFile);

            this.workbooks.put(templateName, templateWorkbook);

            if (!ledgerFileDatas.containsKey(templateFile.getPath())) {

                List<String> sheetNames = new ArrayList();
                for (int i = 0; i < templateWorkbook.getNumberOfSheets(); i++) {
                    XSSFSheet templateSheet = templateWorkbook.getSheetAt(i);

                    sheetNames.add(templateSheet.getSheetName());
                }
                ledgerFileDatas.put(templateFile.getPath(), sheetNames);
            }

            return true;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return false;
        }
    }

    /**
     * 現品票のタグを置換する。
     *
     * @param workbookPath 現品票テンプレートファイルのパス
     * @param reportInfo 現品票情報
     * @return
     */
    public boolean replaceAcceptanceSlipTags(String workbookPath, AcceptanceInfo reportInfo) {
        boolean result = false;
        try {
            XSSFWorkbook workbook = this.workbooks.get(workbookPath);
            String productNo = "";
            String productName = "";
            
            if(!StringUtils.isEmpty(reportInfo.getProductNo())){
                productNo = reportInfo.getProductNo().replace(" ", "_");
            }
            if(!StringUtils.isEmpty(reportInfo.getProductName())){
                productName = reportInfo.getProductName().replace(" ", "_");
            }
            
            
            String contents = new StringBuilder().append("0000 ")
                    .append(reportInfo.getFigureNo())
                    .append(" ")
                    .append(productNo)
                    .append(" ")
                    .append(productName)
                    .append(" ")
                    .append(reportInfo.getStockNum())
                    .append(" ")
                    .append(reportInfo.getPartsNo())
                    .toString();
            BufferedImage qrCodeImage;
            
            Map<String, Object> replaceMap = new HashMap<>();
          
            // 入荷日
            LocalDateTime localDateTime = Objects.isNull(reportInfo.getStockDate()) ? LocalDateTime.now() : reportInfo.getStockDate();
            DateTimeFormatter datetimeformatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            
            replaceMap.put("TAG_OUTPUT_DATE", datetimeformatter.format(localDateTime));
            // 担当者
            replaceMap.put("TAG_CHARGE_PARSON", reportInfo.getChargePerson());
            // FP部材識別コード
            replaceMap.put("TAG_FIGURE_NO", reportInfo.getFigureNo());
            // 品目
            replaceMap.put("TAG_PRODUCT_NO", reportInfo.getProductNo());
            // 品名
            replaceMap.put("TAG_PRODUCT_NAME", reportInfo.getProductName());
            // 指定棚
            replaceMap.put("TAG_LOCATION_NO", reportInfo.getLocationNo());
            // ロット番号
            replaceMap.put("TAG_LOT_NO", reportInfo.getSerialNo());
            // 資材番号
            replaceMap.put("TAG_MATERIAL_NO", reportInfo.getMaterialNo());
            // 数量
            replaceMap.put("TAG_STOCK_NUM", reportInfo.getStockNum());

            // QRコード
            qrCodeImage = Barcode.createQRCodeImage(contents, ErrorCorrectionLevel.Q, "MS932", 150);
            replaceMap.put("TAG_QRCODE_IMAGE", qrCodeImage);
            
            // ワークブックの全シートを対象に、タグを変換する。
            ExcelReplacer.replaceWorkbookTags(workbook, replaceMap, 0, ExcelReplacer::toUpperString);

            // 残ったタグを空欄に変換する。
            ExcelReplacer.replaceEmpty(workbook, createCheckWords());

            result = true;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * テンプレートファイルを読み込み、帳票ワークブックにシートを追加する。
     *
     * @param workbookName 読み込み先ワークブック名
     * @param templateFile テンプレートファイル
     * @return
     */
    public boolean mergeTemplateWorkbook(String workbookName, File templateFile) {
        try {
            XSSFWorkbook workbook = null;
            if (workbooks.containsKey(workbookName)) {
                workbook = this.workbooks.get(workbookName);
            }

            // まだ読み込んでいないテンプレートファイルのみ読み込む。
            if (!ledgerFileDatas.containsKey(templateFile.getPath())) {
                XSSFWorkbook templateWorkbook = ExcelFileUtils.loadExcelFile(templateFile);
                List<String> sheetNames = new ArrayList();
                if (Objects.isNull(workbook)) {
                    // 最初のテンプレートはそのまま読み込む。
                    workbook = templateWorkbook;
                    workbooks.put(workbookName, workbook);

                    for (int i = 0; i < templateWorkbook.getNumberOfSheets(); i++) {
                        XSSFSheet templateSheet = templateWorkbook.getSheetAt(i);

                        sheetNames.add(templateSheet.getSheetName());
                    }
                } else {
                    // テンプレートからフォント情報を取得して、ワークブックに追加する。
                    List<XSSFFont> srcFonts = ExcelFileUtils.getFontList(templateWorkbook);
                    Map<Short, Short> fontConvMap = ExcelFileUtils.appendFontList(workbook, srcFonts);

                    // テンプレートからスタイル情報を取得して、ワークブックに追加する。
                    List<XSSFCellStyle> srcStyles = ExcelFileUtils.getStyleList(templateWorkbook);
                    Map<Short, Short> styleConvMap = ExcelFileUtils.appendStyleList(workbook, srcStyles, fontConvMap);

                    // テンプレートからワークブックにシートをコピーする。
                    for (int i = 0; i < templateWorkbook.getNumberOfSheets(); i++) {
                        XSSFSheet templateSheet = templateWorkbook.getSheetAt(i);
                        XSSFSheet sheet = this.copySheet(workbookName, templateSheet, templateSheet.getSheetName(), styleConvMap);

                        sheetNames.add(sheet.getSheetName());
                    }
                }
                ledgerFileDatas.put(templateFile.getPath(), sheetNames);
            }

            return true;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return false;
        }
    }

    /**
     * テンプレートのシートを、指定した名前で帳票ワークブックにコピーする。
     *
     * @param templateSheet テンプレートのシート
     * @param sheetName 新しいシート名
     * @param styleConvMap シートインデックスの対応マップ
     * @return 帳票ワークブックのシート
     */
    private XSSFSheet copySheet(String workbookName, XSSFSheet templateSheet, String sheetName, Map<Short, Short> styleConvMap) {
        XSSFSheet sheet = null;
        long start = System.currentTimeMillis();
        try {
            XSSFWorkbook workbook = this.workbooks.get(workbookName);

            while (true) {
                sheet = workbook.getSheet(sheetName);
                if (Objects.isNull(sheet)) {
                    // 存在しないシート名の場合、シートを作成して内容をコピーする。
                    sheet = workbook.createSheet(sheetName);

                    ExcelFileUtils.copySheet(templateSheet, sheet, styleConvMap);

                    break;
                } else {
                    // 存在するシート名の場合、シート名の末尾に「_番号」を付けてシートを作成する。
                    int number = 1;
                    int pos = sheetName.lastIndexOf("_");

                    String sheetNameBase;
                    if (pos < 0) {
                        sheetNameBase = sheetName;
                    } else {
                        sheetNameBase = sheetName.substring(0, pos);
                        String tempNumber = sheetName.substring(pos + 1);
                        if (!tempNumber.isEmpty() && NumberUtils.isDigits(tempNumber)) {
                            number = Integer.parseInt(tempNumber);
                        } else {
                            sheetNameBase = sheetName;
                        }
                    }
                    number++;

                    // シート名の最大文字数(31文字)に入るよう調整する。
                    String numberString = String.valueOf(number);
                    int baseNameMaxLength = 30 - numberString.length();

                    if (sheetNameBase.length() > baseNameMaxLength) {
                        String trimBaseName = sheetNameBase.substring(0, baseNameMaxLength);
                        if (trimBaseName.equals(sheetNameBase)) {
                            sheetNameBase = "Sheet";
                            numberString = "1";
                        } else {
                            sheetNameBase = trimBaseName;
                        }
                    }

                    sheetName = new StringBuilder(sheetNameBase)
                            .append("_")
                            .append(numberString)
                            .toString();
                }
            }
            long tim = System.currentTimeMillis() - start;
            System.out.println(String.format("***** copySheet: %d ms", tim));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            sheet = null;
        }
        return sheet;
    }

    /**
     * 指定したファイルパスで、帳票ワークブックを保存する。
     *
     * @param workbookName
     * @param file ファイルパス
     * @return 結果
     */
    public boolean saveWorkbook(String workbookName, File file) {
        boolean ret = false;
        try {
            XSSFWorkbook workbook = this.workbooks.get(workbookName);

            if (Objects.nonNull(workbook)) {
                ExcelFileUtils.saveExcelFile(workbook, file);
                ret = true;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return ret;
    }

    /**
     *
     * @param workbookName
     * @return
     */
    public List<String> getFailedReplaceTags(String workbookName) {
        XSSFWorkbook workbook = this.workbooks.get(workbookName);

        List<String> faildReplaceTags = ExcelReplacer.getFailedReplaceData(workbook, createCheckWords());
        if (!faildReplaceTags.isEmpty()) {
            return faildReplaceTags;
        }
        return new ArrayList<>();
    }

    /**
     *
     * @return
     */
    private List<String> createCheckWords() {
        List<String> checkWords = new ArrayList<>();
        checkWords.add("TAG_");

        return checkWords;
    }
}
