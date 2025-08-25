/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.common;

import adtekfuji.admanagerapp.kanbaneditplugin.utils.ExcelFileUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jp.adtekfuji.excelreplacer.ExcelReplacer;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author e-mori
 */
public class LedgerSheetFactory {

    private final Logger logger = LogManager.getLogger();

    private final Map<String, List<String>> ledgerFileDatas = new HashMap();

    private final Map<String, XSSFWorkbook> workbooks = new HashMap();

    private final ReportTagFactory reportTagFactory = new ReportTagFactory();

    /**
     * コンストラクタ
     */
    public LedgerSheetFactory() {
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
     * @throws java.lang.Exception
     */
    public boolean saveWorkbook(String workbookName, File file) throws Exception {
        try {
            XSSFWorkbook workbook = this.workbooks.get(workbookName);
            return this.reportTagFactory.saveWorkbook(workbook, file);
        } catch (Exception ex) {
            throw ex;
        }
    }
   
    /**
     * 帳票ワークブックのタグを変換する。
     *
     * @param workbookName
     * @param ledgerData
     * @param workNo
     * @param isCheckKanbanNo カンバンタグをチェックする？(true:する, false:しない)
     * @param isRemoveTag 未変換のタグを削除する？(true:する, false:しない)
     * @param ledgerTagCase タグ識別設定
     * @return 未変換タグ一覧
     */
    public List<String> replaceTags(String workbookName, KanbanLedgerPermanenceData ledgerData, Integer workNo, boolean isCheckKanbanNo, boolean isRemoveTag, LedgerTagCase ledgerTagCase) {
        return replaceTags(workbookName, ledgerData, 0, null, workNo, isCheckKanbanNo, isRemoveTag, ledgerTagCase);
    }

    /**
     * 帳票ワークブックのタグを変換する。
     *
     * @param workbookName
     * @param ledgerData
     * @param kanbanNo
     * @param templatePaths
     * @param workNo
     * @param isCheckKanbanNo カンバンタグをチェックする？(true:する, false:しない)
     * @param isRemoveTag 未変換のタグを削除する？(true:する, false:しない)
     * @param ledgerTagCase タグ識別設定
     * @return 未変換タグ一覧
     */
    public List<String> replaceTags(String workbookName, KanbanLedgerPermanenceData ledgerData, int kanbanNo, List<String> templatePaths, Integer workNo, boolean isCheckKanbanNo, boolean isRemoveTag, LedgerTagCase ledgerTagCase) {
        List<String> result = new ArrayList<>(); // 未変換タグ一覧

        XSSFWorkbook workbook = this.workbooks.get(workbookName);

        Map<String, Object> replaceMap = ledgerTagCase.getMap(); // キーは大文字・小文字の違いをを無視

        // カンバン
        replaceMap.putAll(this.reportTagFactory.createReplaceDataKanban(ledgerData.getKanbanInfoEntity(), ledgerData.getActualResultInfoEntities(), ledgerData.getTraceabilityEntities(), ledgerData.getUseExtensionTag(), ledgerTagCase));
        // 工程カンバン
        replaceMap.putAll(this.reportTagFactory.createReplaceDataWorkKanban(ledgerData.getWorkKanbanInfoEntities(), ledgerData.getActualResultInfoEntities(), 
                ledgerData.getKanbanInfoEntity().getProductionType(), ledgerData.getKanbanInfoEntity().getLotQuantity(), workNo, ledgerData.getUseExtensionTag(), ledgerTagCase));
        // 追加工程
        replaceMap.putAll(this.reportTagFactory.createReplaceDataSeparateWorkKanban(ledgerData.getSeparateworkWorkKanbanInfoEntities(), ledgerData.getActualResultInfoEntities(), ledgerData.getUseExtensionTag(), ledgerTagCase));

        // 部品トレース
        if (ledgerData.isEnablePartsTrace()) {
            replaceMap.putAll(this.reportTagFactory.createReplaceDataAssemblyParts(ledgerData.getAssemblyPartsInfos(), ledgerTagCase));
        }

        // QRコード
        if (ledgerData.getUseQRCodeTag()) {
            replaceMap.putAll(this.reportTagFactory.createReplaceDataQRCode(ledgerData.getKanbanInfoEntity(), ledgerTagCase));
        }

        try {
            if (kanbanNo > 0) {
                // 指定されたテンプレートに対応したシートを対象に、タグを変換する。
                for (String templatePath : templatePaths) {
                    List<String> sheetNames = ledgerFileDatas.get(templatePath);
                    for (String sheetName : sheetNames) {
                        XSSFSheet sheet = workbook.getSheet(sheetName);

                        // 画像データ設定
                        replaceMap.putAll(this.reportTagFactory.createReplacePictureData(ledgerData.getKanbanInfoEntity().getKanbanId(), sheet, kanbanNo, ledgerTagCase));
                        // タグを置換する。
                        List<String> noReplaceTags = ExcelReplacer.replaceSheetTags(sheet, replaceMap, kanbanNo, null, isCheckKanbanNo, isRemoveTag, ledgerTagCase.getTagConverter());
                        result.addAll(noReplaceTags);

                        // ダウンロードファイル(画像データ)削除処理
                        this.reportTagFactory.downloadTempFileDelete("picturedata");
                    }
                }
            } else {
                // ワークブックの全シートを対象に、タグを変換する。
                Iterator iterator = workbook.sheetIterator();
                while (iterator.hasNext()) {
                    Sheet sheet = (Sheet) iterator.next();

                    // 画像データ設定
                    replaceMap.putAll(this.reportTagFactory.createReplacePictureData(ledgerData.getKanbanInfoEntity().getKanbanId(), sheet, kanbanNo, ledgerTagCase));
                    // タグを置換する。
                    List<String> noReplaceTags = ExcelReplacer.replaceSheetTags(sheet, replaceMap, kanbanNo, null, isCheckKanbanNo, isRemoveTag, ledgerTagCase.getTagConverter());
                    result.addAll(noReplaceTags);

                    // ダウンロードファイル(画像データ)削除処理
                    this.reportTagFactory.downloadTempFileDelete("picturedata");
                }
            }
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }

        return result;
    }

    /**
     *
     * @param workbookName
     * @return
     */
    public List<String> getFailedReplaceTags(String workbookName) {
        XSSFWorkbook workbook = this.workbooks.get(workbookName);

        List<String> checkWords = this.reportTagFactory.createCheckWords();
        List<String> faildReplaceTags = ExcelReplacer.getFailedReplaceData(workbook, checkWords);
        if (!faildReplaceTags.isEmpty()) {
            return faildReplaceTags;
        }
        return new ArrayList<>();
    }

    /**
     * ワークブックを閉じる
     * 
     * @throws java.lang.Exception
     */
    public void closeWorkbookAll() throws Exception {
        for (XSSFWorkbook workbook : this.workbooks.values()) {
            workbook.close();
        }
    }
}
