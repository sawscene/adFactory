/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.utils;

import adtekfuji.admanagerapp.productionnaviplugin.common.ProductionNaviPropertyConstants;
import adtekfuji.locale.LocaleUtils;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.JAXB;

import jp.adtekfuji.adFactory.entity.importformat.*;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * CSV形式（ヘッダ名指定）_インポートフォーマット設定ファイル ユーティリティ
 *
 * @author (AQTOR)Koga
 */
public class ImportHeaderFormatFileUtil {

    private static final Logger logger = LogManager.getLogger();

    private static final String CHARSET = "UTF-8";
    private static final String CONF_PATH = new StringBuilder(System.getenv("ADFACTORY_HOME")).append(File.separator).append("conf").toString();
    private static final String IMPORT_FORMAT_FILE = "adFactoryImportHeaderFormat.xml";

    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATETIME_FORMAT_2 = "yyyy/MM/dd HH:mm:ss";
    private static final String DATETIME_FORMAT_3 = "yyyy/MM/dd HH:mm";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATE_FORMAT_2 = "yyyy/MM/dd";

    /**
     * インポートフォーマット設定をファイルから読み込む。
     *
     * @return インポートフォーマット設定
     */
    public static ImportHeaderFormatInfo load() {
        logger.info("load");
        ImportHeaderFormatInfo info;
        try {
            File file = new File(CONF_PATH, IMPORT_FORMAT_FILE);
            if (file.exists()) {
                // 設定をファイルから読み込む。
                info = JAXB.unmarshal(file, ImportHeaderFormatInfo.class);
            } else {
                info = new ImportHeaderFormatInfo();
            }

            // 未設定の項目にデフォルト値をセットする。
            fillWorkHeaderFormatInfo(info);
            fillWorkPropHeaderFormatInfo(info);
            fillWorkflowHeaderFormatInfo(info);
            fillWorkflowPropHeaderFormatInfo(info);
            fillWorkKanbanHeaderFormatInfo(info);
            fillWorkKanbanPropHeaderFormatInfo(info);
            fillKanbanHeaderFormatInfo(info);
            fillKanbanPropHeaderFormatInfo(info);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            info = new ImportHeaderFormatInfo();
        }
        return info;
    }

    /**
     * インポートフォーマット設定をファイルに保存する。
     *
     * @param info インポートフォーマット設定
     * @return 結果 (true:成功, false：失敗)
     */
    public static boolean save(ImportHeaderFormatInfo info) {
        logger.info("save:{}", info);
        boolean result = false;
        try {
            // 設定をファイルに保存する。
            JAXB.marshal(info, new File(CONF_PATH, IMPORT_FORMAT_FILE));

            result = true;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return result;
    }

    /**
     * 工程情報のnull項目にデフォルト値をセットする。
     *
     * @param info インポートフォーマット設定
     */
    private static void fillWorkHeaderFormatInfo(ImportHeaderFormatInfo info) {
        try {
            // 工程情報
            if (Objects.isNull(info.getWorkHeaderFormatInfo())) {
                info.setWorkHeaderFormatInfo(new WorkHeaderFormatInfo());
            }
            // エンコード
            if (Objects.isNull(info.getWorkHeaderFormatInfo().getHeaderCsvFileEncode())) {
                info.getWorkHeaderFormatInfo().setHeaderCsvFileEncode(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_CSV_ENCODE);
            }
            // ファイル名
            if (Objects.isNull(info.getWorkHeaderFormatInfo().getHeaderCsvFileName())) {
                info.getWorkHeaderFormatInfo().setHeaderCsvFileName(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // ヘッダー行
            if (Objects.isNull(info.getWorkHeaderFormatInfo().getHeaderCsvHeaderRow())) {
                info.getWorkHeaderFormatInfo().setHeaderCsvHeaderRow(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 読み込み開始行
            if (Objects.isNull(info.getWorkHeaderFormatInfo().getHeaderCsvStartRow())) {
                info.getWorkHeaderFormatInfo().setHeaderCsvStartRow(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 工程階層
            if (Objects.isNull(info.getWorkHeaderFormatInfo().getHeaderCsvProcessHierarchyNames())) {
                info.getWorkHeaderFormatInfo().setHeaderCsvProcessHierarchyNames(new ArrayList<>());
            }
            // 工程階層（区切り文字）
            if (Objects.isNull(info.getWorkHeaderFormatInfo().getHeaderCsvHierarchyDelimiter())) {
                info.getWorkHeaderFormatInfo().setHeaderCsvHierarchyDelimiter(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 工程名（１フィールド目）
            if (Objects.isNull(info.getWorkHeaderFormatInfo().getHeaderCsvProcessNames())) {
                info.getWorkHeaderFormatInfo().setHeaderCsvProcessNames(new ArrayList<>());
            }
            // 工程名（区切り文字）
            if (Objects.isNull(info.getWorkHeaderFormatInfo().getHeaderCsvProcessDelimiter())) {
                info.getWorkHeaderFormatInfo().setHeaderCsvProcessDelimiter(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // タクトタイム
            if (Objects.isNull(info.getWorkHeaderFormatInfo().getHeaderCsvTactTime())) {
                info.getWorkHeaderFormatInfo().setHeaderCsvTactTime(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 単位
            if (Objects.isNull(info.getWorkHeaderFormatInfo().getHeaderCsvTactTimeUnit())) {
                info.getWorkHeaderFormatInfo().setHeaderCsvTactTimeUnit(LocaleUtils.getString("key.time.minute"));
            }
            // 作業内容（１フィールド目）
            if (Objects.isNull(info.getWorkHeaderFormatInfo().getHeaderCsvWorkContent1())) {
                info.getWorkHeaderFormatInfo().setHeaderCsvWorkContent1(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 作業内容（２フィールド目）
            if (Objects.isNull(info.getWorkHeaderFormatInfo().getHeaderCsvWorkContent2())) {
                info.getWorkHeaderFormatInfo().setHeaderCsvWorkContent2(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 作業内容（３フィールド目）
            if (Objects.isNull(info.getWorkHeaderFormatInfo().getHeaderCsvWorkContent3())) {
                info.getWorkHeaderFormatInfo().setHeaderCsvWorkContent3(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 工程プロパティ情報のnull項目にデフォルト値をセットする。
     *
     * @param info インポートフォーマット設定
     */
    private static void fillWorkPropHeaderFormatInfo(ImportHeaderFormatInfo info) {
        try {
            // 工程プロパティ情報
            if (Objects.isNull(info.getWorkPropHeaderFormatInfo())) {
                info.setWorkPropHeaderFormatInfo(new WorkPropHeaderFormatInfo());
            }
            // エンコード
            if (Objects.isNull(info.getWorkPropHeaderFormatInfo().getHeaderCsvFileEncode())) {
                info.getWorkPropHeaderFormatInfo().setHeaderCsvFileEncode(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_CSV_ENCODE);
            }
            // ファイル名
            if (Objects.isNull(info.getWorkPropHeaderFormatInfo().getHeaderCsvFileName())) {
                info.getWorkPropHeaderFormatInfo().setHeaderCsvFileName(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // ヘッダー行
            if (Objects.isNull(info.getWorkPropHeaderFormatInfo().getHeaderCsvHeaderRow())) {
                info.getWorkPropHeaderFormatInfo().setHeaderCsvHeaderRow(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 読み込み開始行
            if (Objects.isNull(info.getWorkPropHeaderFormatInfo().getHeaderCsvStartRow())) {
                info.getWorkPropHeaderFormatInfo().setHeaderCsvStartRow(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 工程名
            if (Objects.isNull(info.getWorkPropHeaderFormatInfo().getHeaderCsvProcessNames())) {
                info.getWorkPropHeaderFormatInfo().setHeaderCsvProcessNames(new ArrayList<>());
            }
            // 工程名（区切り文字）
            if (Objects.isNull(info.getWorkPropHeaderFormatInfo().getHeaderCsvProcessDelimiter())) {
                info.getWorkPropHeaderFormatInfo().setHeaderCsvProcessDelimiter(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // プロパティ
            List<PropHeaderFormatInfo> headerCsvPropValues = info.getWorkPropHeaderFormatInfo().getHeaderCsvPropValues();
            if (Objects.isNull(headerCsvPropValues)) {
                headerCsvPropValues = new ArrayList<>();
                info.getWorkPropHeaderFormatInfo().setHeaderCsvPropValues(headerCsvPropValues);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 工程順情報のnull項目にデフォルト値をセットする。
     *
     * @param info インポートフォーマット設定
     */
    private static void fillWorkflowHeaderFormatInfo(ImportHeaderFormatInfo info) {
        try {
            // 工程順情報
            if (Objects.isNull(info.getWorkflowHeaderFormatInfo())) {
                info.setWorkflowHeaderFormatInfo(new WorkflowHeaderFormatInfo());
            }
            // エンコード
            if (Objects.isNull(info.getWorkflowHeaderFormatInfo().getHeaderCsvFileEncode())) {
                info.getWorkflowHeaderFormatInfo().setHeaderCsvFileEncode(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_CSV_ENCODE);
            }
            // ファイル名
            if (Objects.isNull(info.getWorkflowHeaderFormatInfo().getHeaderCsvFileName())) {
                info.getWorkflowHeaderFormatInfo().setHeaderCsvFileName(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // ヘッダー行
            if (Objects.isNull(info.getWorkflowHeaderFormatInfo().getHeaderCsvHeaderRow())) {
                info.getWorkflowHeaderFormatInfo().setHeaderCsvHeaderRow(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 読み込み開始行
            if (Objects.isNull(info.getWorkflowHeaderFormatInfo().getHeaderCsvStartRow())) {
                info.getWorkflowHeaderFormatInfo().setHeaderCsvStartRow(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 工程順階層
            if (Objects.isNull(info.getWorkflowHeaderFormatInfo().getHeaderCsvWorkflowHierarchyNames())) {
                info.getWorkflowHeaderFormatInfo().setHeaderCsvWorkflowHierarchyNames(new ArrayList<>());
            }
            // 工程順階層（区切り文字）
            if (Objects.isNull(info.getWorkflowHeaderFormatInfo().getHeaderCsvHierarchyDelimiter())) {
                info.getWorkflowHeaderFormatInfo().setHeaderCsvHierarchyDelimiter(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 工程順名（１フィールド目）
            if (Objects.isNull(info.getWorkflowHeaderFormatInfo().getHeaderCsvWorkflowNames())) {
                info.getWorkflowHeaderFormatInfo().setHeaderCsvWorkflowNames(new ArrayList<>());
            }
            // 工程順名（区切り文字）
            if (Objects.isNull(info.getWorkflowHeaderFormatInfo().getHeaderCsvWorkflowDelimiter())) {
                info.getWorkflowHeaderFormatInfo().setHeaderCsvWorkflowDelimiter(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // モデル名（１フィールド目）
            if (Objects.isNull(info.getWorkflowHeaderFormatInfo().getHeaderCsvModelNames())) {
                info.getWorkflowHeaderFormatInfo().setHeaderCsvModelNames(new ArrayList<>());
            }
            // モデル名（区切り文字）
            if (Objects.isNull(info.getWorkflowHeaderFormatInfo().getHeaderCsvModelDelimiter())) {
                info.getWorkflowHeaderFormatInfo().setHeaderCsvModelDelimiter(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 工程名（１フィールド目）
            if (Objects.isNull(info.getWorkflowHeaderFormatInfo().getHeaderCsvProcessNames())) {
                info.getWorkflowHeaderFormatInfo().setHeaderCsvProcessNames(new ArrayList<>());
            }
            // 工程名（区切り文字）
            if (Objects.isNull(info.getWorkflowHeaderFormatInfo().getHeaderCsvProcessNameDelimiter())) {
                info.getWorkflowHeaderFormatInfo().setHeaderCsvProcessNameDelimiter(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 組織
            if (Objects.isNull(info.getWorkflowHeaderFormatInfo().getHeaderCsvOrganization())) {
                info.getWorkflowHeaderFormatInfo().setHeaderCsvOrganization(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 設備
            if (Objects.isNull(info.getWorkflowHeaderFormatInfo().getHeaderCsvEquipment())) {
                info.getWorkflowHeaderFormatInfo().setHeaderCsvEquipment(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 工程の並び順
            if (Objects.isNull(info.getWorkflowHeaderFormatInfo().getHeaderCsvProcOrder())) {
                info.getWorkflowHeaderFormatInfo().setHeaderCsvProcOrder(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 工程接続
            if (Objects.isNull(info.getWorkflowHeaderFormatInfo().getHeaderCsvProcCon())) {
                info.getWorkflowHeaderFormatInfo().setHeaderCsvProcCon(WorkflowHeaderFormatInfo.PROCESS_TYPE.PARALLEL);
            }
            // 完了工程を含めてリスケジュールするか？
            if (Objects.isNull(info.getWorkflowHeaderFormatInfo().getIsReschedule())) {
                info.getWorkflowHeaderFormatInfo().setIsReschedule(true);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 工程順プロパティ情報のnull項目にデフォルト値をセットする。
     *
     * @param info インポートフォーマット設定
     */
    private static void fillWorkflowPropHeaderFormatInfo(ImportHeaderFormatInfo info) {
        try {
            // 工程順プロパティ情報
            if (Objects.isNull(info.getWorkflowPropHeaderFormatInfo())) {
                info.setWorkflowPropHeaderFormatInfo(new WorkflowPropHeaderFormatInfo());
            }
            // エンコード
            if (Objects.isNull(info.getWorkflowPropHeaderFormatInfo().getHeaderCsvFileEncode())) {
                info.getWorkflowPropHeaderFormatInfo().setHeaderCsvFileEncode(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_CSV_ENCODE);
            }
            // ファイル名
            if (Objects.isNull(info.getWorkflowPropHeaderFormatInfo().getHeaderCsvFileName())) {
                info.getWorkflowPropHeaderFormatInfo().setHeaderCsvFileName(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // ヘッダー行
            if (Objects.isNull(info.getWorkflowPropHeaderFormatInfo().getHeaderCsvHeaderRow())) {
                info.getWorkflowPropHeaderFormatInfo().setHeaderCsvHeaderRow(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 読み込み開始行
            if (Objects.isNull(info.getWorkflowPropHeaderFormatInfo().getHeaderCsvStartRow())) {
                info.getWorkflowPropHeaderFormatInfo().setHeaderCsvStartRow(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 工程順名（１フィールド目）
            if (Objects.isNull(info.getWorkflowPropHeaderFormatInfo().getHeaderCsvWorkflowNames())) {
                info.getWorkflowPropHeaderFormatInfo().setHeaderCsvWorkflowNames(new ArrayList<>());
            }
            // 工程順名（区切り文字）
            if (Objects.isNull(info.getWorkflowPropHeaderFormatInfo().getHeaderCsvWorkflowDelimiter())) {
                info.getWorkflowPropHeaderFormatInfo().setHeaderCsvWorkflowDelimiter(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // プロパティ
            List<PropHeaderFormatInfo> headerCsvPropValues = info.getWorkflowPropHeaderFormatInfo().getHeaderCsvPropValues();
            if (Objects.isNull(headerCsvPropValues)) {
                headerCsvPropValues = new ArrayList();
                info.getWorkflowPropHeaderFormatInfo().setHeaderCsvPropValues(headerCsvPropValues);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 工程カンバン情報のnull項目にデフォルト値をセットする。
     *
     * @param info インポートフォーマット設定
     */
    private static void fillWorkKanbanHeaderFormatInfo(ImportHeaderFormatInfo info) {
        try {
            // 工程カンバン情報
            if (Objects.isNull(info.getWorkKanbanHeaderFormatInfo())) {
                info.setWorkKanbanHeaderFormatInfo(new WorkKanbanHeaderFormatInfo());
            }
            // エンコード
            if (Objects.isNull(info.getWorkKanbanHeaderFormatInfo().getHeaderCsvFileEncode())) {
                info.getWorkKanbanHeaderFormatInfo().setHeaderCsvFileEncode(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_CSV_ENCODE);
            }
            // ファイル名
            if (Objects.isNull(info.getWorkKanbanHeaderFormatInfo().getHeaderCsvFileName())) {
                info.getWorkKanbanHeaderFormatInfo().setHeaderCsvFileName(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // ヘッダー行
            if (Objects.isNull(info.getWorkKanbanHeaderFormatInfo().getHeaderCsvHeaderRow())) {
                info.getWorkKanbanHeaderFormatInfo().setHeaderCsvHeaderRow(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 読み込み開始行
            if (Objects.isNull(info.getWorkKanbanHeaderFormatInfo().getHeaderCsvStartRow())) {
                info.getWorkKanbanHeaderFormatInfo().setHeaderCsvStartRow(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // カンバン名
            if (Objects.isNull(info.getWorkKanbanHeaderFormatInfo().getHeaderCsvKanbanNames())) {
                info.getWorkKanbanHeaderFormatInfo().setHeaderCsvKanbanNames(new ArrayList<>());
            }
            // カンバン名（区切り文字）
            if (Objects.isNull(info.getWorkKanbanHeaderFormatInfo().getHeaderCsvKanbanDelimiter())) {
                info.getWorkKanbanHeaderFormatInfo().setHeaderCsvKanbanDelimiter(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 工程順名
            if (Objects.isNull(info.getWorkKanbanHeaderFormatInfo().getHeaderCsvWorkflowNames())) {
                info.getWorkKanbanHeaderFormatInfo().setHeaderCsvWorkflowNames(new ArrayList<>());
            }
            // 工程順名（区切り文字）
            if (Objects.isNull(info.getWorkKanbanHeaderFormatInfo().getHeaderCsvWorkflowDelimiter())) {
                info.getWorkKanbanHeaderFormatInfo().setHeaderCsvWorkflowDelimiter(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 工程順リビジョン
            if (Objects.isNull(info.getWorkKanbanHeaderFormatInfo().getHeaderCsvWorkflowRev())) {
                info.getWorkKanbanHeaderFormatInfo().setHeaderCsvWorkflowRev(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 工程名
            if (Objects.isNull(info.getWorkKanbanHeaderFormatInfo().getHeaderCsvWorkNames())) {
                info.getWorkKanbanHeaderFormatInfo().setHeaderCsvWorkNames(new ArrayList<>());
            }
            // 工程名（区切り文字）
            if (Objects.isNull(info.getWorkKanbanHeaderFormatInfo().getHeaderCsvWorkDelimiter())) {
                info.getWorkKanbanHeaderFormatInfo().setHeaderCsvWorkDelimiter(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // タクトタイム
            if (Objects.isNull(info.getWorkKanbanHeaderFormatInfo().getHeaderCsvTactTime())) {
                info.getWorkKanbanHeaderFormatInfo().setHeaderCsvTactTime(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 単位
            if (Objects.isNull(info.getWorkKanbanHeaderFormatInfo().getHeaderCsvTactTimeUnit())) {
                info.getWorkKanbanHeaderFormatInfo().setHeaderCsvTactTimeUnit(LocaleUtils.getString("key.time.minute"));
            }
            // 開始予定日時
            if (Objects.isNull(info.getWorkKanbanHeaderFormatInfo().getHeaderCsvStartDateTime())) {
                info.getWorkKanbanHeaderFormatInfo().setHeaderCsvStartDateTime(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 完了予定日時
            if (Objects.isNull(info.getWorkKanbanHeaderFormatInfo().getHeaderCsvEndDateTime())) {
                info.getWorkKanbanHeaderFormatInfo().setHeaderCsvEndDateTime(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 組織
            if (Objects.isNull(info.getWorkKanbanHeaderFormatInfo().getHeaderCsvOrganization())) {
                info.getWorkKanbanHeaderFormatInfo().setHeaderCsvOrganization(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 設備
            if (Objects.isNull(info.getWorkKanbanHeaderFormatInfo().getHeaderCsvEquipment())) {
                info.getWorkKanbanHeaderFormatInfo().setHeaderCsvEquipment(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 工程ンバンプロパティ情報のnull項目にデフォルト値をセットする。
     *
     * @param info インポートフォーマット設定
     */
    private static void fillWorkKanbanPropHeaderFormatInfo(ImportHeaderFormatInfo info) {
        try {
            // 工程カンバンプロパティ情報
            if (Objects.isNull(info.getWorkKanbanPropHeaderFormatInfo())) {
                info.setWorkKanbanPropHeaderFormatInfo(new WorkKanbanPropHeaderFormatInfo());
            }
            // エンコード
            if (Objects.isNull(info.getWorkKanbanPropHeaderFormatInfo().getHeaderCsvFileEncode())) {
                info.getWorkKanbanPropHeaderFormatInfo().setHeaderCsvFileEncode(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_CSV_ENCODE);
            }
            // ファイル名
            if (Objects.isNull(info.getWorkKanbanPropHeaderFormatInfo().getHeaderCsvFileName())) {
                info.getWorkKanbanPropHeaderFormatInfo().setHeaderCsvFileName(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // ヘッダー行
            if (Objects.isNull(info.getWorkKanbanPropHeaderFormatInfo().getHeaderCsvHeaderRow())) {
                info.getWorkKanbanPropHeaderFormatInfo().setHeaderCsvHeaderRow(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 読み込み開始行
            if (Objects.isNull(info.getWorkKanbanPropHeaderFormatInfo().getHeaderCsvStartRow())) {
                info.getWorkKanbanPropHeaderFormatInfo().setHeaderCsvStartRow(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // カンバン名
            if (Objects.isNull(info.getWorkKanbanPropHeaderFormatInfo().getHeaderCsvKanbanNames())) {
                info.getWorkKanbanPropHeaderFormatInfo().setHeaderCsvKanbanNames(new ArrayList<>());
            }
            // カンバン名（区切り文字）
            if (Objects.isNull(info.getWorkKanbanPropHeaderFormatInfo().getHeaderCsvKanbanDelimiter())) {
                info.getWorkKanbanPropHeaderFormatInfo().setHeaderCsvKanbanDelimiter(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 工程順名
            if (Objects.isNull(info.getWorkKanbanPropHeaderFormatInfo().getHeaderCsvWorkflowNames())) {
                info.getWorkKanbanPropHeaderFormatInfo().setHeaderCsvWorkflowNames(new ArrayList<>());
            }
            // 工程順名（区切り文字）
            if (Objects.isNull(info.getWorkKanbanPropHeaderFormatInfo().getHeaderCsvWorkflowDelimiter())) {
                info.getWorkKanbanPropHeaderFormatInfo().setHeaderCsvWorkflowDelimiter(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 工程順リビジョン
            if (Objects.isNull(info.getWorkKanbanPropHeaderFormatInfo().getHeaderCsvWorkflowRev())) {
                info.getWorkKanbanPropHeaderFormatInfo().setHeaderCsvWorkflowRev(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 工程名
            if (Objects.isNull(info.getWorkKanbanPropHeaderFormatInfo().getHeaderCsvWorkNames())) {
                info.getWorkKanbanPropHeaderFormatInfo().setHeaderCsvWorkNames(new ArrayList<>());
            }
            // 工程名（区切り文字）
            if (Objects.isNull(info.getWorkKanbanPropHeaderFormatInfo().getHeaderCsvWorkDelimiter())) {
                info.getWorkKanbanPropHeaderFormatInfo().setHeaderCsvWorkDelimiter(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // プロパティ
            List<PropHeaderFormatInfo> headerCsvPropValues = info.getWorkKanbanPropHeaderFormatInfo().getHeaderCsvPropValues();
            if (Objects.isNull(headerCsvPropValues)) {
                headerCsvPropValues = new ArrayList();
                info.getWorkKanbanPropHeaderFormatInfo().setHeaderCsvPropValues(headerCsvPropValues);
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * カンバン情報のnull項目にデフォルト値をセットする。
     *
     * @param info インポートフォーマット設定
     */
    private static void fillKanbanHeaderFormatInfo(ImportHeaderFormatInfo info) {
        try {
            // カンバン情報
            if (Objects.isNull(info.getKanbanHeaderFormatInfo())) {
                info.setKanbanHeaderFormatInfo(new KanbanHeaderFormatInfo());
            }
            // エンコード
            if (Objects.isNull(info.getKanbanHeaderFormatInfo().getHeaderCsvFileEncode())) {
                info.getKanbanHeaderFormatInfo().setHeaderCsvFileEncode(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_CSV_ENCODE);
            }
            // ファイル名
            if (Objects.isNull(info.getKanbanHeaderFormatInfo().getHeaderCsvFileName())) {
                info.getKanbanHeaderFormatInfo().setHeaderCsvFileName(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // ヘッダー行
            if (Objects.isNull(info.getKanbanHeaderFormatInfo().getHeaderCsvHeaderRow())) {
                info.getKanbanHeaderFormatInfo().setHeaderCsvHeaderRow(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 読み込み開始行
            if (Objects.isNull(info.getKanbanHeaderFormatInfo().getHeaderCsvStartRow())) {
                info.getKanbanHeaderFormatInfo().setHeaderCsvStartRow(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // カンバン階層
            if (Objects.isNull(info.getKanbanHeaderFormatInfo().getHeaderCsvKanbanHierarchyNames())) {
                info.getKanbanHeaderFormatInfo().setHeaderCsvKanbanHierarchyNames(new ArrayList<>());
            }
            // カンバン階層（区切り文字）
            if (Objects.isNull(info.getKanbanHeaderFormatInfo().getHeaderCsvHierarchyDelimiter())) {
                info.getKanbanHeaderFormatInfo().setHeaderCsvHierarchyDelimiter(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // カンバン名
            if (Objects.isNull(info.getKanbanHeaderFormatInfo().getHeaderCsvKanbanNames())) {
                info.getKanbanHeaderFormatInfo().setHeaderCsvKanbanNames(new ArrayList<>());
            }
            // カンバン名（区切り文字）
            if (Objects.isNull(info.getKanbanHeaderFormatInfo().getHeaderCsvKanbanDelimiter())) {
                info.getKanbanHeaderFormatInfo().setHeaderCsvKanbanDelimiter(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 工程順名
            if (Objects.isNull(info.getKanbanHeaderFormatInfo().getHeaderCsvWorkflowNames())) {
                info.getKanbanHeaderFormatInfo().setHeaderCsvWorkflowNames(new ArrayList<>());
            }
            // 工程順名（区切り文字）
            if (Objects.isNull(info.getKanbanHeaderFormatInfo().getHeaderCsvWorkflowDelimiter())) {
                info.getKanbanHeaderFormatInfo().setHeaderCsvWorkflowDelimiter(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 工程順リビジョン
            if (Objects.isNull(info.getKanbanHeaderFormatInfo().getHeaderCsvWorkflowRev())) {
                info.getKanbanHeaderFormatInfo().setHeaderCsvWorkflowRev(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // モデル名
            if (Objects.isNull(info.getKanbanHeaderFormatInfo().getHeaderCsvModelNames())) {
                info.getKanbanHeaderFormatInfo().setHeaderCsvModelNames(new ArrayList<>());
            }
            // モデル名（区切り文字）
            if (Objects.isNull(info.getKanbanHeaderFormatInfo().getHeaderCsvModelDelimiter())) {
                info.getKanbanHeaderFormatInfo().setHeaderCsvModelDelimiter(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 製造番号
            if (Objects.isNull(info.getKanbanHeaderFormatInfo().getHeaderCsvProductNumNames())) {
                info.getKanbanHeaderFormatInfo().setHeaderCsvProductNumNames(new ArrayList<>());
            }
            // 製造番号（区切り文字）
            if (Objects.isNull(info.getKanbanHeaderFormatInfo().getHeaderCsvProductDelimiter())) {
                info.getKanbanHeaderFormatInfo().setHeaderCsvProductDelimiter(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 開始予定日時
            if (Objects.isNull(info.getKanbanHeaderFormatInfo().getHeaderCsvStartDateTime())) {
                info.getKanbanHeaderFormatInfo().setHeaderCsvStartDateTime(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 生産タイプ
            if (Objects.isNull(info.getKanbanHeaderFormatInfo().getHeaderCsvProductionType())) {
                info.getKanbanHeaderFormatInfo().setHeaderCsvProductionType(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // ロット数量
            if (Objects.isNull(info.getKanbanHeaderFormatInfo().getHeaderCsvLotNum())) {
                info.getKanbanHeaderFormatInfo().setHeaderCsvLotNum(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // カンバンステータス（初期値）
            if (Objects.isNull(info.getKanbanHeaderFormatInfo().getKanbanInitStatus())) {
                info.getKanbanHeaderFormatInfo().setKanbanInitStatus(KanbanStatusEnum.PLANNING);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * カンバンプロパティ情報のnull項目にデフォルト値をセットする。
     *
     * @param info インポートフォーマット設定
     */
    private static void fillKanbanPropHeaderFormatInfo(ImportHeaderFormatInfo info) {
        try {
            // カンバンプロパティ情報
            if (Objects.isNull(info.getKanbanPropHeaderFormatInfo())) {
                info.setKanbanPropHeaderFormatInfo(new KanbanPropHeaderFormatInfo());
            }
            // エンコード
            if (Objects.isNull(info.getKanbanPropHeaderFormatInfo().getHeaderCsvFileEncode())) {
                info.getKanbanPropHeaderFormatInfo().setHeaderCsvFileEncode(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_CSV_ENCODE);
            }
            // ファイル名
            if (Objects.isNull(info.getKanbanPropHeaderFormatInfo().getHeaderCsvFileName())) {
                info.getKanbanPropHeaderFormatInfo().setHeaderCsvFileName(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // ヘッダー行
            if (Objects.isNull(info.getKanbanPropHeaderFormatInfo().getHeaderCsvHeaderRow())) {
                info.getKanbanPropHeaderFormatInfo().setHeaderCsvHeaderRow(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 読み込み開始行
            if (Objects.isNull(info.getKanbanPropHeaderFormatInfo().getHeaderCsvStartRow())) {
                info.getKanbanPropHeaderFormatInfo().setHeaderCsvStartRow(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // カンバン名
            if (Objects.isNull(info.getKanbanPropHeaderFormatInfo().getHeaderCsvKanbanNames())) {
                info.getKanbanPropHeaderFormatInfo().setHeaderCsvKanbanNames(new ArrayList<>());
            }
            // カンバン名（区切り文字）
            if (Objects.isNull(info.getKanbanPropHeaderFormatInfo().getHeaderCsvKanbanDelimiter())) {
                info.getKanbanPropHeaderFormatInfo().setHeaderCsvKanbanDelimiter(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 工程順名
            if (Objects.isNull(info.getKanbanPropHeaderFormatInfo().getHeaderCsvWorkflowNames())) {
                info.getKanbanPropHeaderFormatInfo().setHeaderCsvWorkflowNames(new ArrayList<>());
            }
            // 工程順名（区切り文字）
            if (Objects.isNull(info.getKanbanPropHeaderFormatInfo().getHeaderCsvWorkflowDelimiter())) {
                info.getKanbanPropHeaderFormatInfo().setHeaderCsvWorkflowDelimiter(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // 工程順リビジョン
            if (Objects.isNull(info.getKanbanPropHeaderFormatInfo().getHeaderCsvWorkflowRev())) {
                info.getKanbanPropHeaderFormatInfo().setHeaderCsvWorkflowRev(ProductionNaviPropertyConstants.INIT_PROD_HEADER_CSV_VALUE);
            }
            // プロパティ
            List<PropHeaderFormatInfo> headerCsvPropValues = info.getKanbanPropHeaderFormatInfo().getHeaderCsvPropValues();
            if (Objects.isNull(headerCsvPropValues)) {
                headerCsvPropValues = new ArrayList();
                info.getKanbanPropHeaderFormatInfo().setHeaderCsvPropValues(headerCsvPropValues);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 文字列を日時に変換する。
     *
     * @param value 文字列
     * @return 日時
     */
    public static Date stringToDateTime(String value) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT);
            return sdf.parse(value);
        } catch (ParseException e) {
            return stringToDateTime2(value);
        }
    }

    /**
     * 文字列(yyyy/MM/dd HH:mm:ss)を日時に変換する。
     *
     * @param value 文字列(yyyy/MM/dd HH:mm:ss)
     * @return 日時
     */
    private static Date stringToDateTime2(String value) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT_2);
            return sdf.parse(value);
        } catch (ParseException e) {
            return stringToDateTime3(value);
        }
    }

    /**
     * 文字列(yyyy/MM/dd HH:mm)を日時に変換する。
     *
     * @param value 文字列(yyyy/MM/dd HH:mm)
     * @return 日時
     */
    private static Date stringToDateTime3(String value) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT_3);
            return sdf.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 文字列を日付に変換する。
     *
     * @param value 文字列
     * @return 日付
     */
    public static Date stringToDate(String value) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            return sdf.parse(value);
        } catch (ParseException e) {
            return stringToDate2(value);
        }
    }

    /**
     * 文字列(yyyy/MM/dd)を日時に変換する。
     *
     * @param value 文字列(yyyy/MM/dd)
     * @return 日付
     */
    private static Date stringToDate2(String value) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_2);
            return sdf.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }
}
