/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.utils;

import adtekfuji.admanagerapp.productionnaviplugin.common.ProductionNaviPropertyConstants;
import adtekfuji.clientservice.WorkflowInfoFacade;
import java.io.File;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.JAXB;
import jp.adtekfuji.adFactory.entity.importformat.HolidayFormatInfo;
import jp.adtekfuji.adFactory.entity.importformat.ImportFormatInfo;
import jp.adtekfuji.adFactory.entity.importformat.KanbanFormatInfo;
import jp.adtekfuji.adFactory.entity.importformat.KanbanPropFormatInfo;
import jp.adtekfuji.adFactory.entity.importformat.KanbanStatusFormatInfo;
import jp.adtekfuji.adFactory.entity.importformat.ProductFormatInfo;
import jp.adtekfuji.adFactory.entity.importformat.WorkKanbanFormatInfo;
import jp.adtekfuji.adFactory.entity.importformat.WorkKanbanPropFormatInfo;
import jp.adtekfuji.adFactory.entity.importformat.WorkflowRegexInfo;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * インポートフォーマット設定ファイル ユーティリティ
 *
 * @author nar-nakamura
 */
public class ImportFormatFileUtil {

    private static final Logger logger = LogManager.getLogger();

    private static final String CHARSET = "UTF-8";
    private static final String CONF_PATH = new StringBuilder(System.getenv("ADFACTORY_HOME")).append(File.separator).append("conf").toString();
    private static final String IMPORT_FORMAT_FILE = "adFactoryImportFormat.xml";

    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATETIME_FORMAT_2 = "yyyy/MM/dd HH:mm:ss";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATE_FORMAT_2 = "yyyy/MM/dd";

    /**
     * インポートフォーマット設定をファイルから読み込む。
     *
     * @return インポートフォーマット設定
     */
    public static ImportFormatInfo load() {
        logger.info("load");
        ImportFormatInfo info;
        try {
            File file = new File(CONF_PATH, IMPORT_FORMAT_FILE);
            if (file.exists()) {
                // 設定をファイルから読み込む。
                info = JAXB.unmarshal(file, ImportFormatInfo.class);
            } else {
                info = new ImportFormatInfo();
            }

            // 未設定の項目にデフォルト値をセットする。
            fillKanbanFormatInfoValue(info);
            fillKanbanPropFormatInfoValue(info);
            fillWorkKanbanFormatInfoValue(info);
            fillWorkKanbanPropFormatInfoValue(info);
            fillKanbanStatusFormatInfoValue(info);
            fillProductFormatInfoValue(info);
            fillHolidayFormatInfoValue(info);

            // 工程カンバンプロパティ更新情報については名前のみ
            WorkKanbanPropFormatInfo updateInfo = new WorkKanbanPropFormatInfo();
            updateInfo.setCsvFileName(ProductionNaviPropertyConstants.INIT_PROD_UPDATEWORKKNBANPROP_CSV_FILE);
            info.setUpdateWorkKanbanPropFormatInfo(updateInfo);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            info = new ImportFormatInfo();
        }
        return info;
    }

    /**
     * インポートフォーマット設定をファイルに保存する。
     *
     * @param info インポートフォーマット設定
     * @return 結果 (true:成功, false：失敗)
     */
    public static boolean save(ImportFormatInfo info) {
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
     * カンバン情報のnull項目にデフォルト値をセットする。
     *
     * @param info インポートフォーマット設定
     */
    private static void fillKanbanFormatInfoValue(ImportFormatInfo info) {
        try {
            // カンバン
            if (Objects.isNull(info.getKanbanFormatInfo())) {
                info.setKanbanFormatInfo(new KanbanFormatInfo());
            }

            // CSV：ファイル名
            if (Objects.isNull(info.getKanbanFormatInfo().getCsvFileName())) {
                info.getKanbanFormatInfo().setCsvFileName(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_CSV_FILE);
            }
            // CSV：エンコード
            if (Objects.isNull(info.getKanbanFormatInfo().getCsvFileEncode())) {
                info.getKanbanFormatInfo().setCsvFileEncode(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_CSV_ENCODE);
            }
            // Excel：シート名
            if (Objects.isNull(info.getKanbanFormatInfo().getXlsSheetName())) {
                info.getKanbanFormatInfo().setXlsSheetName(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_XLS_SHEET);
            }

            // CSV：読み込み開始行
            if (Objects.isNull(info.getKanbanFormatInfo().getCsvStartRow())) {
                info.getKanbanFormatInfo().setCsvStartRow(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_CSV_LINE);
            }
            // Excel：読み込み開始行
            if (Objects.isNull(info.getKanbanFormatInfo().getXlsStartRow())) {
                info.getKanbanFormatInfo().setXlsStartRow(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_XLS_LINE);
            }

            // CSV：カンバン階層名
            if (Objects.isNull(info.getKanbanFormatInfo().getCsvHierarchyName())) {
                info.getKanbanFormatInfo().setCsvHierarchyName(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_CSV_HIERA_NAME);
            }
            // Excel：カンバン階層名
            if (Objects.isNull(info.getKanbanFormatInfo().getXlsHierarchyName())) {
                info.getKanbanFormatInfo().setXlsHierarchyName(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_XLS_HIERA_NAME);
            }

            // CSV：カンバン名
            if (Objects.isNull(info.getKanbanFormatInfo().getCsvKanbanName())) {
                info.getKanbanFormatInfo().setCsvKanbanName(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_CSV_NAME);
            }
            // Excel：カンバン名
            if (Objects.isNull(info.getKanbanFormatInfo().getXlsKanbanName())) {
                info.getKanbanFormatInfo().setXlsKanbanName(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_XLS_NAME);
            }

            // CSV：工程順名
            if (Objects.isNull(info.getKanbanFormatInfo().getCsvWorkflowName())) {
                info.getKanbanFormatInfo().setCsvWorkflowName(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_CSV_WF_NAME);
            }
            // Excel：工程順名
            if (Objects.isNull(info.getKanbanFormatInfo().getXlsWorkflowName())) {
                info.getKanbanFormatInfo().setXlsWorkflowName(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_XLS_WF_NAME);
            }

            // CSV：工程順版数
            if (Objects.isNull(info.getKanbanFormatInfo().getCsvWorkflowRev())) {
                info.getKanbanFormatInfo().setCsvWorkflowRev(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_CSV_WF_REV);
            }
            // Excel：工程順版数
            if (Objects.isNull(info.getKanbanFormatInfo().getXlsWorkflowRev())) {
                info.getKanbanFormatInfo().setXlsWorkflowRev(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_XLS_WF_REV);
            }

            // CSV：モデル名
            if (Objects.isNull(info.getKanbanFormatInfo().getCsvModelName())) {
                info.getKanbanFormatInfo().setCsvModelName(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_CSV_MODEL_NAME);
            }
            // Excel：モデル名
            if (Objects.isNull(info.getKanbanFormatInfo().getXlsModelName())) {
                info.getKanbanFormatInfo().setXlsModelName(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_XLS_MODEL_NAME);
            }

            // CSV：開始予定日時
            if (Objects.isNull(info.getKanbanFormatInfo().getCsvStartDateTime())) {
                info.getKanbanFormatInfo().setCsvStartDateTime(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_CSV_START_DT);
            }
            // Excel：開始予定日時
            if (Objects.isNull(info.getKanbanFormatInfo().getXlsStartDateTime())) {
                info.getKanbanFormatInfo().setXlsStartDateTime(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_XLS_START_DT);
            }

            // CSV：生産タイプ
            //if (Objects.isNull(info.getKanbanFormatInfo().getCsvProductionType())) {
            //    info.getKanbanFormatInfo().setCsvProductionType(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_CSV_PRODUCTION_TYPE);
            //}
            // Excel：生産タイプ
            //if (Objects.isNull(info.getKanbanFormatInfo().getXlsProductionType())) {
            //    info.getKanbanFormatInfo().setXlsProductionType(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_XLS_PRODUCTION_TYPE);
            //}

            // CSV：ロット数量
            //if (Objects.isNull(info.getKanbanFormatInfo().getCsvLotNum())) {
            //    info.getKanbanFormatInfo().setCsvLotNum(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_CSV_LOT_NUM);
            //}
            // Excel：ロット数量
            //if (Objects.isNull(info.getKanbanFormatInfo().getXlsLotNum())) {
            //    info.getKanbanFormatInfo().setXlsLotNum(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_XLS_LOT_NUM);
            //}

            // モデル名と工程順名の組み合わせで工程順を指定する
            if (Objects.isNull(info.getKanbanFormatInfo().getIsCheckWorkflowWithModel())) {
                info.getKanbanFormatInfo().setIsCheckWorkflowWithModel(false);
            }

            // モデル名で工程順を指定する
            if (Objects.isNull(info.getKanbanFormatInfo().getIsCheckWorkflowRegex())) {
                info.getKanbanFormatInfo().setIsCheckWorkflowRegex(false);
            }

            // モデル名の条件と工程順
            WorkflowInfoFacade workflowInfoFacade = new WorkflowInfoFacade();

            for (WorkflowRegexInfo regexInfo : info.getKanbanFormatInfo().getWorkflowRegexInfos()) {
                try {
                    WorkflowInfoEntity workflow;
                    if (Objects.nonNull(regexInfo.isUseLatest()) && regexInfo.isUseLatest()) {
                        workflow = workflowInfoFacade.findName(URLEncoder.encode(regexInfo.getWorkflowName(), CHARSET));
                    } else {
                        workflow = workflowInfoFacade.findName(URLEncoder.encode(regexInfo.getWorkflowName(), CHARSET), regexInfo.getWorkflowRev());
                    }
                    if (Objects.nonNull(workflow.getWorkflowId())) {
                        regexInfo.setWorkflow(workflow);
                    } else {
                        regexInfo.setWorkflow(null);
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            }

            // カンバン階層を指定する
            if (Objects.isNull(info.getKanbanFormatInfo().getIsCheckKanbanHierarchy())) {
                info.getKanbanFormatInfo().setIsCheckKanbanHierarchy(false);
            }
            // カンバン階層名
            if (Objects.isNull(info.getKanbanFormatInfo().getKanbanHierarchyName())) {
                info.getKanbanFormatInfo().setKanbanHierarchyName("");
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
    private static void fillKanbanPropFormatInfoValue(ImportFormatInfo info) {
        try {
            // カンバンプロパティ
            if (Objects.isNull(info.getKanbanPropFormatInfo())) {
                info.setKanbanPropFormatInfo(new KanbanPropFormatInfo());
            }

            // CSV：ファイル名
            if (Objects.isNull(info.getKanbanPropFormatInfo().getCsvFileName())) {
                info.getKanbanPropFormatInfo().setCsvFileName(ProductionNaviPropertyConstants.INIT_PROD_KANBANPROP_CSV_FILE);
            }

            // CSV：エンコード
            if (Objects.isNull(info.getKanbanPropFormatInfo().getCsvFileEncode())) {
                info.getKanbanPropFormatInfo().setCsvFileEncode(ProductionNaviPropertyConstants.INIT_PROD_KANBANPROP_CSV_ENCODE);
            }

            // Excel：シート名
            if (Objects.isNull(info.getKanbanPropFormatInfo().getXlsSheetName())) {
                info.getKanbanPropFormatInfo().setXlsSheetName(ProductionNaviPropertyConstants.INIT_PROD_KANBANPROP_XLS_SHEET);
            }

            // フォーマット選択
            if (Objects.isNull(info.getKanbanPropFormatInfo().getSelectedFormat())) {
                info.getKanbanPropFormatInfo().setSelectedFormat(ProductionNaviPropertyConstants.INIT_PROD_KANBANPROP_FORMAT_NO);
            }

            // CSV：読み込み開始行
            if (Objects.isNull(info.getKanbanPropFormatInfo().getCsvStartRow())) {
                info.getKanbanPropFormatInfo().setCsvStartRow(ProductionNaviPropertyConstants.INIT_PROD_KANBANPROP_CSV_LINE);
            }

            // Excel：読み込み開始行
            if (Objects.isNull(info.getKanbanPropFormatInfo().getXlsStartRow())) {
                info.getKanbanPropFormatInfo().setXlsStartRow(ProductionNaviPropertyConstants.INIT_PROD_KANBANPROP_XLS_LINE);
            }

            // CSV：カンバン名
            if (Objects.isNull(info.getKanbanPropFormatInfo().getCsvKanbanName())) {
                info.getKanbanPropFormatInfo().setCsvKanbanName(ProductionNaviPropertyConstants.INIT_PROD_KANBANPROP_CSV_NAME);
            }

            // Excel：カンバン名
            if (Objects.isNull(info.getKanbanPropFormatInfo().getXlsKanbanName())) {
                info.getKanbanPropFormatInfo().setXlsKanbanName(ProductionNaviPropertyConstants.INIT_PROD_KANBANPROP_XLS_NAME);
            }

            // CSV：プロパティ名
            if (Objects.isNull(info.getKanbanPropFormatInfo().getCsvPropName())) {
                info.getKanbanPropFormatInfo().setCsvPropName(ProductionNaviPropertyConstants.INIT_PROD_KANBANPROP_CSV_PROP_NAME);
            }

            // Excel：プロパティ名
            if (Objects.isNull(info.getKanbanPropFormatInfo().getXlsPropName())) {
                info.getKanbanPropFormatInfo().setXlsPropName(ProductionNaviPropertyConstants.INIT_PROD_KANBANPROP_XLS_PROP_NAME);
            }

            // CSV：プロパティ型
            if (Objects.isNull(info.getKanbanPropFormatInfo().getCsvPropType())) {
                info.getKanbanPropFormatInfo().setCsvPropType(ProductionNaviPropertyConstants.INIT_PROD_KANBANPROP_CSV_PROP_TYPE);
            }

            // Excel：プロパティ型
            if (Objects.isNull(info.getKanbanPropFormatInfo().getXlsPropType())) {
                info.getKanbanPropFormatInfo().setXlsPropType(ProductionNaviPropertyConstants.INIT_PROD_KANBANPROP_XLS_PROP_TYPE);
            }

            // CSV：プロパティ値
            if (Objects.isNull(info.getKanbanPropFormatInfo().getCsvPropValue())) {
                info.getKanbanPropFormatInfo().setCsvPropValue(ProductionNaviPropertyConstants.INIT_PROD_KANBANPROP_CSV_PROP_VALUE);
            }

            // Excel：プロパティ値
            if (Objects.isNull(info.getKanbanPropFormatInfo().getXlsPropValue())) {
                info.getKanbanPropFormatInfo().setXlsPropValue(ProductionNaviPropertyConstants.INIT_PROD_KANBANPROP_XLS_PROP_VALUE);
            }

            // CSV：ヘッダー行
            if (Objects.isNull(info.getKanbanPropFormatInfo().getF2CsvHeaderRow())) {
                info.getKanbanPropFormatInfo().setF2CsvHeaderRow(ProductionNaviPropertyConstants.INIT_PROD_KANBANPROP_F2_CSV_HEADER_ROW);
            }
            // Excel：ヘッダー行
            if (Objects.isNull(info.getKanbanPropFormatInfo().getF2XlsHeaderRow())) {
                info.getKanbanPropFormatInfo().setF2XlsHeaderRow(ProductionNaviPropertyConstants.INIT_PROD_KANBANPROP_F2_XLS_HEADER_ROW);
            }

            // CSV：読み込み開始行
            if (Objects.isNull(info.getKanbanPropFormatInfo().getF2CsvStartRow())) {
                info.getKanbanPropFormatInfo().setF2CsvStartRow(ProductionNaviPropertyConstants.INIT_PROD_KANBANPROP_F2_CSV_START_ROW);
            }
            // Excel：読み込み開始行
            if (Objects.isNull(info.getKanbanPropFormatInfo().getF2XlsStartRow())) {
                info.getKanbanPropFormatInfo().setF2XlsStartRow(ProductionNaviPropertyConstants.INIT_PROD_KANBANPROP_F2_XLS_START_ROW);
            }

            // CSV：カンバン名
            if (Objects.isNull(info.getKanbanPropFormatInfo().getF2CsvKanbanName())) {
                info.getKanbanPropFormatInfo().setF2CsvKanbanName(ProductionNaviPropertyConstants.INIT_PROD_KANBANPROP_F2_CSV_KANBAN_NAME);
            }
            // Excel：カンバン名
            if (Objects.isNull(info.getKanbanPropFormatInfo().getF2XlsKanbanName())) {
                info.getKanbanPropFormatInfo().setF2XlsKanbanName(ProductionNaviPropertyConstants.INIT_PROD_KANBANPROP_F2_XLS_KANBAN_NAME);
            }

            // CSV：プロパティ値
            List<String> csvPropValues = info.getKanbanPropFormatInfo().getF2CsvPropValues();
            if (Objects.isNull(csvPropValues)) {
                csvPropValues = new ArrayList();
            }
            if (csvPropValues.isEmpty()) {
                csvPropValues.add(ProductionNaviPropertyConstants.INIT_PROD_KANBANPROP_F2_CSV_PROP_VALUE);
                info.getKanbanPropFormatInfo().setF2CsvPropValues(csvPropValues);
            }
            // Excel：プロパティ値
            List<String> xlsPropValues = info.getKanbanPropFormatInfo().getF2XlsPropValues();
            if (Objects.isNull(xlsPropValues)) {
                xlsPropValues = new ArrayList();
            }
            if (xlsPropValues.isEmpty()) {
                xlsPropValues.add(ProductionNaviPropertyConstants.INIT_PROD_KANBANPROP_F2_XLS_PROP_VALUE);
                info.getKanbanPropFormatInfo().setF2XlsPropValues(xlsPropValues);
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
    private static void fillWorkKanbanFormatInfoValue(ImportFormatInfo info) {
        try {
            // 工程カンバン
            if (Objects.isNull(info.getWorkKanbanFormatInfo())) {
                info.setWorkKanbanFormatInfo(new WorkKanbanFormatInfo());
            }

            // CSV：ファイル名
            if (Objects.isNull(info.getWorkKanbanFormatInfo().getCsvFileName())) {
                info.getWorkKanbanFormatInfo().setCsvFileName(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBAN_CSV_FILE);
            }

            // CSV：エンコード
            if (Objects.isNull(info.getWorkKanbanFormatInfo().getCsvFileEncode())) {
                info.getWorkKanbanFormatInfo().setCsvFileEncode(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBAN_CSV_ENCODE);
            }

            // Excel：シート名
            if (Objects.isNull(info.getWorkKanbanFormatInfo().getXlsSheetName())) {
                info.getWorkKanbanFormatInfo().setXlsSheetName(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBAN_XLS_SHEET);
            }

            // CSV：読み込み開始行
            if (Objects.isNull(info.getWorkKanbanFormatInfo().getCsvStartRow())) {
                info.getWorkKanbanFormatInfo().setCsvStartRow(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBAN_XLS_LINE);
            }

            // Excel：読み込み開始行
            if (Objects.isNull(info.getWorkKanbanFormatInfo().getXlsStartRow())) {
                info.getWorkKanbanFormatInfo().setXlsStartRow(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBAN_XLS_LINE);
            }

            // CSV：カンバン名
            if (Objects.isNull(info.getWorkKanbanFormatInfo().getCsvKanbanName())) {
                info.getWorkKanbanFormatInfo().setCsvKanbanName(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBAN_CSV_NAME);
            }

            // Excel：カンバン名
            if (Objects.isNull(info.getWorkKanbanFormatInfo().getXlsKanbanName())) {
                info.getWorkKanbanFormatInfo().setXlsKanbanName(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBAN_XLS_NAME);
            }

            // CSV：工程の番号
            if (Objects.isNull(info.getWorkKanbanFormatInfo().getCsvWorkNum())) {
                info.getWorkKanbanFormatInfo().setCsvWorkNum(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBAN_CSV_NUM);
            }

            // Excel：工程の番号
            if (Objects.isNull(info.getWorkKanbanFormatInfo().getXlsWorkNum())) {
                info.getWorkKanbanFormatInfo().setXlsWorkNum(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBAN_XLS_NUM);
            }

            // CSV：スキップフラグ
            if (Objects.isNull(info.getWorkKanbanFormatInfo().getCsvSkipFlag())) {
                info.getWorkKanbanFormatInfo().setCsvSkipFlag(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBAN_CSV_SKIP_FLAG);
            }

            // Excel：スキップフラグ
            if (Objects.isNull(info.getWorkKanbanFormatInfo().getXlsSkipFlag())) {
                info.getWorkKanbanFormatInfo().setXlsSkipFlag(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBAN_XLS_SKIP_FLAG);
            }

            // CSV：開始予定日時
            if (Objects.isNull(info.getWorkKanbanFormatInfo().getCsvStartDateTime())) {
                info.getWorkKanbanFormatInfo().setCsvStartDateTime(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBAN_CSV_START_DT);
            }

            // Excel：開始予定日時
            if (Objects.isNull(info.getWorkKanbanFormatInfo().getXlsStartDateTime())) {
                info.getWorkKanbanFormatInfo().setXlsStartDateTime(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBAN_XLS_START_DT);
            }

            // CSV：完了予定日時
            if (Objects.isNull(info.getWorkKanbanFormatInfo().getCsvCompDateTime())) {
                info.getWorkKanbanFormatInfo().setCsvCompDateTime(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBAN_CSV_COMP_DT);
            }

            // Excel：完了予定日時
            if (Objects.isNull(info.getWorkKanbanFormatInfo().getXlsCompDateTime())) {
                info.getWorkKanbanFormatInfo().setXlsCompDateTime(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBAN_XLS_COMP_DT);
            }

            // CSV：組織識別名
            if (Objects.isNull(info.getWorkKanbanFormatInfo().getCsvOrganizationIdentName())) {
                info.getWorkKanbanFormatInfo().setCsvOrganizationIdentName(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBAN_CSV_USER);
            }

            // Excel：組織識別名
            if (Objects.isNull(info.getWorkKanbanFormatInfo().getXlsOrganizationIdentName())) {
                info.getWorkKanbanFormatInfo().setXlsOrganizationIdentName(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBAN_XLS_USER);
            }

            // CSV：設備識別名
            if (Objects.isNull(info.getWorkKanbanFormatInfo().getCsvEquipmentIdentName())) {
                info.getWorkKanbanFormatInfo().setCsvEquipmentIdentName(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBAN_CSV_EQUIPMENTS);
            }

            // Excel：設備識別名
            if (Objects.isNull(info.getWorkKanbanFormatInfo().getXlsEquipmentIdentName())) {
                info.getWorkKanbanFormatInfo().setXlsEquipmentIdentName(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBAN_XLS_EQUIPMENTS);
            }
            
            // CSV：工程名 2019/12/18 工程名項目の追加対応
            if (Objects.isNull(info.getWorkKanbanFormatInfo().getCsvWorkName())) {
                info.getWorkKanbanFormatInfo().setCsvWorkName(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBAN_CSV_WKNAME);
            }

            // Excel：工程名 2019/12/18 工程名項目の追加対応
            if (Objects.isNull(info.getWorkKanbanFormatInfo().getXlsWorkName())) {
                info.getWorkKanbanFormatInfo().setXlsWorkName(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBAN_XLS_WKNAME);
            }
            
            // CSV：タクトタイム 2020/02/20 MES連携 タクトタイム追加
            if (Objects.isNull(info.getWorkKanbanFormatInfo().getCsvTactTime())) {
                info.getWorkKanbanFormatInfo().setCsvTactTime(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBAN_CSV_TACTTIME);
            }

            // Excel：タクトタイム 2020/02/20 MES連携 タクトタイム追加
            if (Objects.isNull(info.getWorkKanbanFormatInfo().getXlsTactTime())) {
                info.getWorkKanbanFormatInfo().setXlsTactTime(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBAN_XLS_TACTTIME);
            }
            
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 工程カンバンプロパティ情報のnull項目にデフォルト値をセットする。
     *
     * @param info インポートフォーマット設定
     */
    private static void fillWorkKanbanPropFormatInfoValue(ImportFormatInfo info) {
        try {
            // 工程カンバンプロパティ
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo())) {
                info.setWorkKanbanPropFormatInfo(new WorkKanbanPropFormatInfo());
            }

            // CSV：ファイル名
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getCsvFileName())) {
                info.getWorkKanbanPropFormatInfo().setCsvFileName(ProductionNaviPropertyConstants.INIT_PROD_WORKKNBANPROP_CSV_FILE);
            }

            // CSV：エンコード
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getCsvFileEncode())) {
                info.getWorkKanbanPropFormatInfo().setCsvFileEncode(ProductionNaviPropertyConstants.INIT_PROD_WORKKNBANPROP_CSV_ENCODE);
            }

            // Excel：シート名
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getXlsSheetName())) {
                info.getWorkKanbanPropFormatInfo().setXlsSheetName(ProductionNaviPropertyConstants.INIT_PROD_WORKKNBANPROP_XLS_SHEET);
            }
            
            // フォーマット選択  2020/02/20 MES連携 追加
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getSelectedFormat())) {
                info.getWorkKanbanPropFormatInfo().setSelectedFormat(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBANPROP_FORMAT_NO);
            }

            // CSV：読み込み開始行
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getCsvStartRow())) {
                info.getWorkKanbanPropFormatInfo().setCsvStartRow(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBANPROP_CSV_LINE);
            }

            // Excel：読み込み開始行
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getXlsStartRow())) {
                info.getWorkKanbanPropFormatInfo().setXlsStartRow(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBANPROP_XLS_LINE);
            }

            // CSV：カンバン名
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getCsvKanbanName())) {
                info.getWorkKanbanPropFormatInfo().setCsvKanbanName(ProductionNaviPropertyConstants.INIT_PROD_WORKKNBANPROP_CSV_NAME);
            }

            // Excel：カンバン名
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getXlsKanbanName())) {
                info.getWorkKanbanPropFormatInfo().setXlsKanbanName(ProductionNaviPropertyConstants.INIT_PROD_WORKKNBANPROP_XLS_NAME);
            }

            // CSV：工程の番号
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getCsvWorkNum())) {
                info.getWorkKanbanPropFormatInfo().setCsvWorkNum(ProductionNaviPropertyConstants.INIT_PROD_WORKKNBANPROP_CSV_NUM);
            }

            // Excel：工程の番号
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getXlsWorkNum())) {
                info.getWorkKanbanPropFormatInfo().setXlsWorkNum(ProductionNaviPropertyConstants.INIT_PROD_WORKKNBANPROP_XLS_NUM);
            }

            // CSV：プロパティ名
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getCsvPropName())) {
                info.getWorkKanbanPropFormatInfo().setCsvPropName(ProductionNaviPropertyConstants.INIT_PROD_WORKKNBANPROP_CSV_PROP_NAME);
            }

            // Excel：プロパティ名
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getXlsPropName())) {
                info.getWorkKanbanPropFormatInfo().setXlsPropName(ProductionNaviPropertyConstants.INIT_PROD_WORKKNBANPROP_XLS_PROP_NAME);
            }

            // CSV：プロパティ型
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getCsvPropType())) {
                info.getWorkKanbanPropFormatInfo().setCsvPropType(ProductionNaviPropertyConstants.INIT_PROD_WORKKNBANPROP_CSV_PROP_TYPE);
            }

            // Excel：プロパティ型
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getXlsPropType())) {
                info.getWorkKanbanPropFormatInfo().setXlsPropType(ProductionNaviPropertyConstants.INIT_PROD_WORKKNBANPROP_XLS_PROP_TYPE);
            }

            // CSV：プロパティ値
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getCsvPropValue())) {
                info.getWorkKanbanPropFormatInfo().setCsvPropValue(ProductionNaviPropertyConstants.INIT_PROD_WORKKNBANPROP_CSV_ROP_VALUE);
            }

            // Excel：プロパティ値
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getXlsPropValue())) {
                info.getWorkKanbanPropFormatInfo().setXlsPropValue(ProductionNaviPropertyConstants.INIT_PROD_WORKKNBANPROP_XLS_ROP_VALUE);
            }

            // CSV：工程名
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getCsvWorkName())) {
                info.getWorkKanbanPropFormatInfo().setCsvWorkName(ProductionNaviPropertyConstants.INIT_PROD_WORKKNBANPROP_CSV_WKNAME);
            }

            // Excel：工程名
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getXlsWorkName())) {
                info.getWorkKanbanPropFormatInfo().setXlsWorkName(ProductionNaviPropertyConstants.INIT_PROD_WORKKNBANPROP_XLS_WKNAME);
            }

            // 2020/02/20 MES連携 フォーマット２追加
            // CSV：ヘッダー行 (フォーマット２)
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getF2CsvHeaderRow())) {
                info.getWorkKanbanPropFormatInfo().setF2CsvHeaderRow(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBANPROP_F2_CSV_HEADER_ROW);
            }
            // Excel：ヘッダー行 (フォーマット２)
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getF2XlsHeaderRow())) {
                info.getWorkKanbanPropFormatInfo().setF2XlsHeaderRow(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBANPROP_F2_XLS_HEADER_ROW);
            }

            // CSV：読み込み開始行 (フォーマット２)
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getF2CsvStartRow())) {
                info.getWorkKanbanPropFormatInfo().setF2CsvStartRow(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBANPROP_F2_CSV_START_ROW);
            }
            // Excel：読み込み開始行 (フォーマット２)
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getF2XlsStartRow())) {
                info.getWorkKanbanPropFormatInfo().setF2XlsStartRow(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBANPROP_F2_XLS_START_ROW);
            }

            // CSV：カンバン名 (フォーマット２)
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getF2CsvKanbanName())) {
                info.getWorkKanbanPropFormatInfo().setF2CsvKanbanName(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBANPROP_F2_CSV_KANBAN_NAME);
            }
            // Excel：カンバン名 (フォーマット２)
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getF2XlsKanbanName())) {
                info.getWorkKanbanPropFormatInfo().setF2XlsKanbanName(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBANPROP_F2_XLS_KANBAN_NAME);
            }
            
            // CSV：工程名 (フォーマット２)
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getF2CsvWorkName())) {
                info.getWorkKanbanPropFormatInfo().setF2CsvWorkName(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBANPROP_F2_CSV_WKNAME);
            }
            // Excel：工程名 (フォーマット２)
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getF2XlsWorkName())) {
                info.getWorkKanbanPropFormatInfo().setF2XlsWorkName(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBANPROP_F2_XLS_WKNAME);
            }

            // CSV：工程の番号 (フォーマット２)
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getF2CsvWorkNo())) {
                info.getWorkKanbanPropFormatInfo().setF2CsvWorkNo(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBANPROP_F2_CSV_NUM);
            }
            // Excel：工程の番号 (フォーマット２)
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getF2XlsWorkNo())) {
                info.getWorkKanbanPropFormatInfo().setF2XlsWorkNo(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBANPROP_F2_XLS_NUM);
            }

            // CSV：プロパティ値 (フォーマット２)
            List<String> csvPropValues = info.getWorkKanbanPropFormatInfo().getF2CsvPropValues();
            if (Objects.isNull(csvPropValues)) {
                csvPropValues = new ArrayList();
            }
            if (csvPropValues.isEmpty()) {
                csvPropValues.add(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBANPROP_F2_CSV_PROP_VALUE);
                info.getWorkKanbanPropFormatInfo().setF2CsvPropValues(csvPropValues);
            }
            // Excel：プロパティ値 (フォーマット２)
            List<String> xlsPropValues = info.getWorkKanbanPropFormatInfo().getF2XlsPropValues();
            if (Objects.isNull(xlsPropValues)) {
                xlsPropValues = new ArrayList();
            }
            if (xlsPropValues.isEmpty()) {
                xlsPropValues.add(ProductionNaviPropertyConstants.INIT_PROD_WORKKANBANPROP_F2_XLS_PROP_VALUE);
                info.getWorkKanbanPropFormatInfo().setF2XlsPropValues(xlsPropValues);
            }
            
            // カンバン階層名
            if (Objects.isNull(info.getKanbanFormatInfo().getKanbanHierarchyName())) {
                info.getKanbanFormatInfo().setKanbanHierarchyName("");
            }
            
            //　プロパティを組み合わせて読み込む (フォーマット２)
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getF2IsCheckUnionProp())) {
                info.getWorkKanbanPropFormatInfo().setF2IsCheckUnionProp(false);
            }
            // 新しいプロパティ名 (フォーマット２)
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getF2UnionPropNewName())) {
                info.getWorkKanbanPropFormatInfo().setF2UnionPropNewName("");
            }
            // 組み合わせるプロパティ名(左) (フォーマット２)
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getF2UnionPropLeftName())) {
                info.getWorkKanbanPropFormatInfo().setF2UnionPropLeftName("");
            }
            // 組み合わせるプロパティ名(右) (フォーマット２)
            if (Objects.isNull(info.getWorkKanbanPropFormatInfo().getF2UnionPropRightName())) {
                info.getWorkKanbanPropFormatInfo().setF2UnionPropRightName("");
            }
            
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * カンバンステータス情報のnull項目にデフォルト値をセットする。
     *
     * @param info インポートフォーマット設定
     */
    private static void fillKanbanStatusFormatInfoValue(ImportFormatInfo info) {
        try {
            // カンバンステータス
            if (Objects.isNull(info.getKanbanStatusFormatInfo())) {
                info.setKanbanStatusFormatInfo(new KanbanStatusFormatInfo());
            }

            // CSV：ファイル名
            if (Objects.isNull(info.getKanbanStatusFormatInfo().getCsvFileName())) {
                info.getKanbanStatusFormatInfo().setCsvFileName(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_STATUS_CSV_FILE);
            }

            // CSV：エンコード
            if (Objects.isNull(info.getKanbanStatusFormatInfo().getCsvFileEncode())) {
                info.getKanbanStatusFormatInfo().setCsvFileEncode(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_STATUS_CSV_ENCODE);
            }

            // Excel：シート名
            if (Objects.isNull(info.getKanbanStatusFormatInfo().getXlsSheetName())) {
                info.getKanbanStatusFormatInfo().setXlsSheetName(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_STATUS_XLS_SHEET);
            }

            // CSV：読み込み開始行
            if (Objects.isNull(info.getKanbanStatusFormatInfo().getCsvStartRow())) {
                info.getKanbanStatusFormatInfo().setCsvStartRow(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_STATUS_CSV_LINE);
            }

            // Excel：読み込み開始行
            if (Objects.isNull(info.getKanbanStatusFormatInfo().getXlsStartRow())) {
                info.getKanbanStatusFormatInfo().setXlsStartRow(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_STATUS_XLS_LINE);
            }

            // CSV：カンバン名
            if (Objects.isNull(info.getKanbanStatusFormatInfo().getCsvKanbanName())) {
                info.getKanbanStatusFormatInfo().setCsvKanbanName(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_STATUS_CSV_NAME);
            }

            // Excel：カンバン名
            if (Objects.isNull(info.getKanbanStatusFormatInfo().getXlsKanbanName())) {
                info.getKanbanStatusFormatInfo().setXlsKanbanName(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_STATUS_XLS_NAME);
            }

            // CSV：ステータス
            if (Objects.isNull(info.getKanbanStatusFormatInfo().getCsvKanbanStatus())) {
                info.getKanbanStatusFormatInfo().setCsvKanbanStatus(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_STATUS_CSV_STATUS_NAME);
            }

            // Excel：ステータス
            if (Objects.isNull(info.getKanbanStatusFormatInfo().getXlsKanbanStatus())) {
                info.getKanbanStatusFormatInfo().setXlsKanbanStatus(ProductionNaviPropertyConstants.INIT_PROD_KANBAN_STATUS_XLS_STATUS_NAME);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 製品情報のnull項目にデフォルト値をセットする。
     *
     * @param info インポートフォーマット設定
     */
    private static void fillProductFormatInfoValue(ImportFormatInfo info) {
        try {
            // カンバンステータス
            if (Objects.isNull(info.getProductFormatInfo())) {
                info.setProductFormatInfo(new ProductFormatInfo());
            }

            // CSV：ファイル名
            if (Objects.isNull(info.getProductFormatInfo().getCsvFileName())) {
                info.getProductFormatInfo().setCsvFileName(ProductionNaviPropertyConstants.INIT_PROD_PRODUCT_CSV_FILE);
            }

            // CSV：エンコード
            if (Objects.isNull(info.getProductFormatInfo().getCsvFileEncode())) {
                info.getProductFormatInfo().setCsvFileEncode(ProductionNaviPropertyConstants.INIT_PROD_PRODUCT_CSV_ENCODE);
            }

            // Excel：シート名
            if (Objects.isNull(info.getProductFormatInfo().getXlsSheetName())) {
                info.getProductFormatInfo().setXlsSheetName(ProductionNaviPropertyConstants.INIT_PROD_PRODUCT_XLS_SHEET);
            }

            // CSV：読み込み開始行
            if (Objects.isNull(info.getProductFormatInfo().getCsvStartRow())) {
                info.getProductFormatInfo().setCsvStartRow(ProductionNaviPropertyConstants.INIT_PROD_PRODUCT_CSV_LINE);
            }

            // Excel：読み込み開始行
            if (Objects.isNull(info.getProductFormatInfo().getXlsStartRow())) {
                info.getProductFormatInfo().setXlsStartRow(ProductionNaviPropertyConstants.INIT_PROD_PRODUCT_XLS_LINE);
            }

            // CSV：ユニークID
            if (Objects.isNull(info.getProductFormatInfo().getCsvUniqueID())) {
                info.getProductFormatInfo().setCsvUniqueID(ProductionNaviPropertyConstants.INIT_PROD_PRODUCT_CSV_UNIQUE_ID);
            }

            // Excel：ユニークID
            if (Objects.isNull(info.getProductFormatInfo().getXlsUniqueID())) {
                info.getProductFormatInfo().setXlsUniqueID(ProductionNaviPropertyConstants.INIT_PROD_PRODUCT_XLS_UNIQUE_ID);
            }

            // CSV：カンバン名
            if (Objects.isNull(info.getProductFormatInfo().getCsvKanbanName())) {
                info.getProductFormatInfo().setCsvKanbanName(ProductionNaviPropertyConstants.INIT_PROD_PRODUCT_CSV_KANBAN_NAME);
            }

            // Excel：カンバン名
            if (Objects.isNull(info.getProductFormatInfo().getXlsKanbanName())) {
                info.getProductFormatInfo().setXlsKanbanName(ProductionNaviPropertyConstants.INIT_PROD_PRODUCT_XLS_KANBAN_NAME);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 休日情報のnull項目にデフォルト値をセットする。
     *
     * @param info インポートフォーマット設定
     */
    private static void fillHolidayFormatInfoValue(ImportFormatInfo info) {
        try {
            // 休日
            HolidayFormatInfo holidayFormatInfo = info.getHolidayFormatInfo();
            if (Objects.isNull(holidayFormatInfo)) {
                holidayFormatInfo = new HolidayFormatInfo();
                info.setHolidayFormatInfo(holidayFormatInfo);
            }

            // CSV：エンコード
            if (Objects.isNull(holidayFormatInfo.getCsvFileEncode())) {
                holidayFormatInfo.setCsvFileEncode(ProductionNaviPropertyConstants.INIT_HOLIDAY_CSV_ENCODE);
            }

            // Excel：シート名
            if (Objects.isNull(holidayFormatInfo.getXlsSheetName())) {
                holidayFormatInfo.setXlsSheetName(ProductionNaviPropertyConstants.INIT_HOLIDAY_XLS_SHEET_NAME);
            }

            // CSV：読み込み開始行
            if (Objects.isNull(holidayFormatInfo.getCsvStartRow())) {
                holidayFormatInfo.setCsvStartRow(ProductionNaviPropertyConstants.INIT_HOLIDAY_CSV_LINE);
            }

            // Excel：読み込み開始行
            if (Objects.isNull(holidayFormatInfo.getXlsStartRow())) {
                holidayFormatInfo.setXlsStartRow(ProductionNaviPropertyConstants.INIT_HOLIDAY_XLS_LINE);
            }

            // CSV：休日
            if (Objects.isNull(holidayFormatInfo.getCsvHolidayDate())) {
                holidayFormatInfo.setCsvHolidayDate(ProductionNaviPropertyConstants.INIT_HOLIDAY_CSV_HOLIDAY);
            }

            // Excel：休日
            if (Objects.isNull(holidayFormatInfo.getXlsHolidayDate())) {
                holidayFormatInfo.setXlsHolidayDate(ProductionNaviPropertyConstants.INIT_HOLIDAY_XLS_HOLIDAY);
            }

            // CSV：休日名
            if (Objects.isNull(holidayFormatInfo.getCsvHolidayName())) {
                holidayFormatInfo.setCsvHolidayName(ProductionNaviPropertyConstants.INIT_HOLIDAY_CSV_NAME);
            }

            // Excel：休日名
            if (Objects.isNull(holidayFormatInfo.getXlsHolidayName())) {
                holidayFormatInfo.setXlsHolidayName(ProductionNaviPropertyConstants.INIT_HOLIDAY_XLS_NAME);
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
