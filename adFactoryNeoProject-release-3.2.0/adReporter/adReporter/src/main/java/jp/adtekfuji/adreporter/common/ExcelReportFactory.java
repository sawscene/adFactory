/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adreporter.common;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import jp.adtekfuji.adFactory.adreporter.info.DisposalSlipInfo;
import jp.adtekfuji.adappentity.ActualPropertyEntity;
import jp.adtekfuji.adappentity.ActualResultEntity;
import jp.adtekfuji.adappentity.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adappentity.KanbanEntity;
import jp.adtekfuji.adappentity.ProductEntity;
import jp.adtekfuji.adappentity.WorkKanbanEntity;
import jp.adtekfuji.adappentity.WorkKanbanPropertyEntity;
import jp.adtekfuji.adreporter.utils.ExcelFileUtils;
import jp.adtekfuji.adreporter.utils.LocaleUtils;
import jp.adtekfuji.excelreplacer.ExcelReplacer;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Excel帳票
 *
 * @author nar-nakamura
 */
public class ExcelReportFactory {

    private final Logger logger = LogManager.getLogger();

    private static final ResourceBundle rb = LocaleUtils.getResourceBundle();

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
     * 廃棄伝票のタグを置換する。
     *
     * @param workbookPath 廃棄伝票テンプレートファイルのパス
     * @param reportInfo 廃棄伝票
     * @param product 廃棄品の情報
     * @return
     */
    public boolean replaceDisposalSlipTags(String workbookPath, DisposalSlipInfo reportInfo, ProductEntity product) {
        boolean result = false;
        try {
            XSSFWorkbook workbook = this.workbooks.get(workbookPath);

            Map<String, Object> replaceMap = new HashMap<>();

            KanbanEntity kanban;
            List<WorkKanbanEntity> workKanbans;
            List<WorkKanbanEntity> separateworkWorkKanbans;
            List<ActualResultEntity> actuals;

            kanban = reportInfo.getKanban();
            if (Objects.isNull(kanban)) {
                return result;
            }

            workKanbans = kanban.getWorkKanbans();
            if (Objects.isNull(workKanbans)) {
                workKanbans = new ArrayList();
            }

            separateworkWorkKanbans = kanban.getSeparateWorkKanbans();
            if (Objects.isNull(separateworkWorkKanbans)) {
                separateworkWorkKanbans = new ArrayList();
            }

            actuals = reportInfo.getActualResults();
            if (Objects.isNull(actuals)) {
                actuals = new ArrayList();
            }

            // 出力日時
            Date outputDate = new Date();
            replaceMap.put("TAG_OUTPUT_DATE", outputDate);
            // 発行者
            replaceMap.put("TAG_OUTPUT_ORGANIZATION", reportInfo.getIssuerName());

            // 不良理由
            replaceMap.put("TAG_DEFECT_REASON", product.getDefectType());
            // 工程名
            replaceMap.put("TAG_DEFECT_WORK_NAME", product.getDefectWorkName());
            // 不良数
            replaceMap.put("TAG_DEFECT_NUM", product.getDefectNum());

            // カンバン
            replaceMap.putAll(createReplaceDataKanban(kanban, actuals));
            // 工程カンバン
            replaceMap.putAll(createReplaceDataWorkKanban(workKanbans, actuals));
            // 追加工程
            replaceMap.putAll(createReplaceDataSeparateWorkKanban(separateworkWorkKanbans, actuals));

            // ワークブックの全シートを対象に、タグを変換する。
            ExcelReplacer.replaceWorkbookTags(workbook, replaceMap, 0, null, false, true);

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
            //System.out.println(String.format("***** copySheet: %d ms", tim));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            sheet = null;
        }
        return sheet;
    }

    /**
     * 指定したファイルパスで、帳票ワークブックを保存する。
     *
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
     * カンバン用帳票リプレースデータ作成
     *
     * @param kanbanEntity
     * @param actualResultInfoEntities
     * @return
     */
    private Map<String, Object> createReplaceDataKanban(KanbanEntity kanbanEntity, List<ActualResultEntity> actualResultInfoEntities) throws Exception {
        Map<String, Object> map = new HashMap<>();

        // カンバン名
        map.put("TAG_KANBAN_NAME", kanbanEntity.getKanbanName());
        // カンバンステータス
        String kanbanStatus = null;
        if (Objects.nonNull(kanbanEntity.getKanbanStatus())) {
            kanbanStatus = LocaleUtils.getString(kanbanEntity.getKanbanStatus().getResourceKey());
        }
        map.put("TAG_KANBAN_STATUS", kanbanStatus);
        // 開始予定日時
        map.put("TAG_KANBAN_PLAN_START", kanbanEntity.getStartDatetime());
        // 完了予定日時
        map.put("TAG_KANBAN_PLAN_END", kanbanEntity.getCompDatetime());
        // 開始実績日時
        Optional<ActualResultEntity> start = actualResultInfoEntities.stream().min(Comparator.comparing(entity -> entity.getImplementDatetime()));
        if (start.isPresent()) {
            map.put("TAG_KANBAN_ACTUAL_START", start.get().getImplementDatetime());
        }
        // 完了実績日時
        Optional<ActualResultEntity> compDate = actualResultInfoEntities.stream().max(Comparator.comparing(entity -> entity.getImplementDatetime()));
        if (compDate.isPresent()) {
            map.put("TAG_KANBAN_ACTUAL_END", compDate.get().getImplementDatetime());
        }
        // カンバンプロパティ(プロパティ名)
        if (Objects.nonNull(kanbanEntity.getProperties())) {
            kanbanEntity.getProperties().stream().forEach((_item) -> {
                map.put(String.format("TAG_KANBAN_PROPERTY(%s)", _item.getKanbanPropName()), _item.getKanbanPropValue());
            });
        }
        return map;
    }

    /**
     * 工程カンバン用帳票リプレースデータ作成
     *
     * @param workKanbanEntities
     * @param actualResultInfoEntities
     * @return
     */
    private Map<String, Object> createReplaceDataWorkKanban(List<WorkKanbanEntity> workKanbanEntities, List<ActualResultEntity> actualResultInfoEntities) {
        Map<String, Object> map = new HashMap<>();
        String workflowName = null;
        String workRevison = null;
        workKanbanEntities.sort(Comparator.comparing(work -> work.getWorkKanbanOrder()));
        for (int index = 1; workKanbanEntities.size() >= index; index++) {
            WorkKanbanEntity work = workKanbanEntities.get(index - 1);
            List<ActualResultEntity> workingList = new ArrayList<>();
            List<ActualResultEntity> compList = new ArrayList<>();
            List<String> interruptList = new ArrayList<>();
            List<String> delayList = new ArrayList<>();
            List<String> parentOrganizations = new ArrayList<>();
            List<String> parentEquipments = new ArrayList<>();
            List<String> organizations = new ArrayList<>();
            List<String> equipments = new ArrayList<>();

            for (ActualResultEntity actual : actualResultInfoEntities) {
                if (actual.getFkWorkKanbanId().equals(work.getWorkKanbanId())) {
                    if (Objects.isNull(workflowName) && Objects.isNull(workRevison)) {
                        workflowName = actual.getWorkflowName();
                        workRevison = actual.getWorkflowRevision();
                    }
                    setParentOrganizations(parentOrganizations, actual);
                    setOrganizations(organizations, actual);
                    setParentEquipments(parentEquipments, actual);
                    setEquipments(equipments, actual);
                    switch (actual.getActualStatus()) {
                        case WORKING:
                            workingList.add(actual);
                            break;
                        case COMPLETION:
                            compList.add(actual);
                            setDelay(delayList, actual);
                            break;
                        case SUSPEND:
                            setIntterupt(interruptList, actual);
                            break;
                        default:
                            break;
                    }
                }
            }

            // 工程名
            map.put(String.format("TAG_WORK_NAME%s", index), work.getName());
            // 工程カンバンステータス
            String workStatus = null;
            if (Objects.nonNull(work.getWorkStatus())) {
                workStatus = LocaleUtils.getString(work.getWorkStatus().getResourceKey());
            }
            map.put(String.format("TAG_WORK_STATUS%s", index), workStatus);
            // タクトタイム
            map.put(String.format("TAG_WORK_TAKTTIME%s", index), work.getTaktTime());
            // 作業時間
            map.put(String.format("TAG_WORK_WORKTIME%s", index), work.getSumTimes());
            // 開始予定日時
            map.put(String.format("TAG_WORK_PLAN_START%s", index), work.getStartDatetime());
            // 完了予定日時
            map.put(String.format("TAG_WORK_PLAN_END%s", index), work.getCompDatetime());
            // A品実績数
            map.put(String.format("TAG_WORK_A_ACTUAL_NUM%s", index), work.getActualNum1());
            // B品実績数
            map.put(String.format("TAG_WORK_B_ACTUAL_NUM%s", index), work.getActualNum2());
            // C品実績数
            map.put(String.format("TAG_WORK_C_ACTUAL_NUM%s", index), work.getActualNum3());
            // 中断理由
            StringBuilder interrupts = new StringBuilder();
            for (String interrupt : interruptList) {
                interrupts.append(interrupt);
                interrupts.append(",");
            }
            deleteLastTrailingCharacter(interrupts);
            map.put(String.format("TAG_WORK_INTERRUPT%s", index), interrupts.toString());
            // 遅延理由
            StringBuilder delays = new StringBuilder();
            for (String delay : delayList) {
                delays.append(delay);
                delays.append(",");
            }
            deleteLastTrailingCharacter(delays);
            map.put(String.format("TAG_WORK_DELAY%s", index), delays.toString());
            // 工程カンバンプロパティ(プロパティ名)
            for (WorkKanbanPropertyEntity pro : work.getProperties()) {
                map.put(String.format("TAG_WORK_PROPERTY%s(%s)", index, pro.getWorkKanbanPropName()), pro.getWorkKanbanPropValue());
            }
            createReplaceDataWorkKanbanActual(map, index, workingList, compList, parentOrganizations, organizations, parentEquipments, equipments);

            this.createReplaceDataTraceability(map, index, compList);
        }

        // 工程順名
        map.put("TAG_WORKFLOW_NAME", workflowName);
        // 工程順版数
        map.put("TAG_WORKFLOW_REVISION", workRevison);
        return map;
    }

    /**
     * 実績用帳票リプレースデータ作成
     *
     * @param map
     * @param index
     * @param workingList
     * @param compList
     * @param pO
     * @param o
     * @param pE
     * @param e
     */
    private void createReplaceDataWorkKanbanActual(Map<String, Object> map, int index, List<ActualResultEntity> workingList, List<ActualResultEntity> compList, List<String> pO, List<String> o, List<String> pE, List<String> e) {
        // 開始実績日時
        Optional<ActualResultEntity> start = workingList.stream().min(Comparator.comparing(entity -> entity.getImplementDatetime()));
        if (start.isPresent()) {
            map.put(String.format("TAG_WORK_ACTUAL_START%s", index), start.get().getImplementDatetime());
        }
        // 完了実績日時
        Optional<ActualResultEntity> compDate = compList.stream().max(Comparator.comparing(entity -> entity.getImplementDatetime()));
        if (compDate.isPresent()) {
            map.put(String.format("TAG_WORK_ACTUAL_END%s", index), compDate.get().getImplementDatetime());
        }
        // 親組織名
        StringBuilder parentOrganizations = new StringBuilder();
        for (String parentOrganization : pO) {
            parentOrganizations.append(parentOrganization);
            parentOrganizations.append(",");
        }
        deleteLastTrailingCharacter(parentOrganizations);
        map.put(String.format("TAG_WORK_PARENT_ORGANIZATION%s", index), parentOrganizations.toString());
        // 組織名
        StringBuilder organizations = new StringBuilder();
        for (String organization : o) {
            organizations.append(organization);
            organizations.append(",");
        }
        deleteLastTrailingCharacter(organizations);
        map.put(String.format("TAG_WORK_ORGANIZATION%s", index), organizations.toString());
        // 親設備名
        StringBuilder parentEquipments = new StringBuilder();
        for (String parentEquipmet : pE) {
            parentEquipments.append(parentEquipmet);
            parentEquipments.append(",");
        }
        deleteLastTrailingCharacter(parentEquipments);
        map.put(String.format("TAG_WORK_PARENT_EQUIPMENT%s", index), parentEquipments.toString());
        // 設備名
        StringBuilder equipmets = new StringBuilder();
        for (String equipmet : e) {
            equipmets.append(equipmet);
            equipmets.append(",");
        }
        deleteLastTrailingCharacter(equipmets);
        map.put(String.format("TAG_WORK_EQUIPMENT%s", index), equipmets.toString());
    }

    /**
     * 工程カンバン用帳票リプレースデータ作成
     *
     * @param workKanbanEntities
     * @return
     */
    private Map<String, Object> createReplaceDataSeparateWorkKanban(List<WorkKanbanEntity> workKanbanEntities, List<ActualResultEntity> actualResultList) {
        Map<String, Object> replaceWords = new HashMap<>();
        workKanbanEntities.sort(Comparator.comparing(work -> work.getWorkKanbanOrder()));

        for (Integer index = 1; workKanbanEntities.size() >= index; index++) {
            WorkKanbanEntity work = workKanbanEntities.get(index - 1);
            List<ActualResultEntity> workingList = new ArrayList<>();
            List<ActualResultEntity> compList = new ArrayList<>();
            List<String> interruptList = new ArrayList<>();
            List<String> delayList = new ArrayList<>();
            List<String> parentOrganizations = new ArrayList<>();
            List<String> parentEquipments = new ArrayList<>();
            List<String> organizations = new ArrayList<>();
            List<String> equipments = new ArrayList<>();

            for (ActualResultEntity actual : actualResultList) {
                if (actual.getFkWorkKanbanId().equals(work.getWorkKanbanId())) {
                    setParentOrganizations(parentOrganizations, actual);
                    setOrganizations(organizations, actual);
                    setParentEquipments(parentEquipments, actual);
                    setEquipments(equipments, actual);
                    switch (actual.getActualStatus()) {
                        case WORKING:
                            workingList.add(actual);
                            break;
                        case COMPLETION:
                            compList.add(actual);
                            setDelay(delayList, actual);
                            break;
                        case SUSPEND:
                            setIntterupt(interruptList, actual);
                            break;
                        default:
                            break;
                    }
                }
            }

            // 工程名
            replaceWords.put(String.format("TAG_WORK_NAME_S%s", index), work.getName());
            // 工程カンバンステータス
            replaceWords.put(String.format("TAG_WORK_STATUS_S%s", index), LocaleUtils.getString(work.getWorkStatus().getResourceKey()));
            // タクトタイム
            replaceWords.put(String.format("TAG_WORK_TAKTTIME_S%s", index), work.getTaktTime());
            // 作業時間
            replaceWords.put(String.format("TAG_WORK_WORKTIME_S%s", index), work.getSumTimes());
            // 開始予定日時
            replaceWords.put(String.format("TAG_WORK_PLAN_START_S%s", index), work.getStartDatetime());
            // 完了予定日時
            replaceWords.put(String.format("TAG_WORK_PLAN_END_S%s", index), work.getCompDatetime());
            // A品実績数
            replaceWords.put(String.format("TAG_WORK_A_ACTUAL_NUM%s", index), work.getActualNum1());
            // B品実績数
            replaceWords.put(String.format("TAG_WORK_B_ACTUAL_NUM%s", index), work.getActualNum2());
            // C品実績数
            replaceWords.put(String.format("TAG_WORK_C_ACTUAL_NUM%s", index), work.getActualNum3());
            // 中断理由
            StringBuilder interrupts = new StringBuilder();
            for (String interrupt : interruptList) {
                interrupts.append(interrupt);
                interrupts.append(",");
            }
            deleteLastTrailingCharacter(interrupts);
            replaceWords.put(String.format("TAG_WORK_INTERRUPT_S%s", index), interrupts.toString());
            // 遅延理由
            StringBuilder delays = new StringBuilder();
            for (String delay : delayList) {
                delays.append(delay);
                delays.append(",");
            }
            deleteLastTrailingCharacter(delays);
            replaceWords.put(String.format("TAG_WORK_DELAY_S%s", index), delays.toString());
            // 工程カンバンプロパティ(プロパティ名)
            for (WorkKanbanPropertyEntity pro : work.getProperties()) {
                replaceWords.put(String.format("TAG_WORK_PROPERTY_S%s(%s)", index, pro.getWorkKanbanPropName()), pro.getWorkKanbanPropValue());
            }
            createReplaceDataSeparateWorkKanbanActual(replaceWords, index, workingList, compList, parentOrganizations, organizations, parentEquipments, equipments);
        }

        return replaceWords;
    }

    /**
     * 実績用帳票リプレースデータ作成
     *
     * @param map
     * @param index
     * @param workingList
     * @param compList
     * @param pO
     * @param o
     * @param pE
     * @param e
     */
    private void createReplaceDataSeparateWorkKanbanActual(Map<String, Object> map, int index, List<ActualResultEntity> workingList, List<ActualResultEntity> compList, List<String> pO, List<String> o, List<String> pE, List<String> e) {
        // 開始実績日時
        Optional<ActualResultEntity> start = workingList.stream().min(Comparator.comparing(entity -> entity.getImplementDatetime()));
        if (start.isPresent()) {
            map.put(String.format("TAG_WORK_ACTUAL_START_S%s", index), start.get().getImplementDatetime());
        }
        // 完了実績日時
        Optional<ActualResultEntity> compDate = compList.stream().max(Comparator.comparing(entity -> entity.getImplementDatetime()));
        if (compDate.isPresent()) {
            map.put(String.format("TAG_WORK_ACTUAL_END_S%s", index), compDate.get().getImplementDatetime());
        }
        // 親組織名
        StringBuilder parentOrganizations = new StringBuilder();
        for (String parentOrganization : pO) {
            parentOrganizations.append(parentOrganization);
            parentOrganizations.append(",");
        }
        deleteLastTrailingCharacter(parentOrganizations);
        map.put(String.format("TAG_WORK_PARENT_ORGANIZATION_S%s", index), parentOrganizations.toString());
        // 組織名
        StringBuilder organizations = new StringBuilder();
        for (String organization : o) {
            organizations.append(organization);
            organizations.append(",");
        }
        deleteLastTrailingCharacter(organizations);
        map.put(String.format("TAG_WORK_ORGANIZATION_S%s", index), organizations.toString());
        // 親設備名
        StringBuilder parentEquipments = new StringBuilder();
        for (String parentEquipmet : pE) {
            parentEquipments.append(parentEquipmet);
            parentEquipments.append(",");
        }
        deleteLastTrailingCharacter(parentEquipments);
        map.put(String.format("TAG_WORK_PARENT_EQUIPMENT_S%s", index), parentEquipments.toString());
        // 設備名
        StringBuilder equipmets = new StringBuilder();
        for (String equipmet : e) {
            equipmets.append(equipmet);
            equipmets.append(",");
        }
        deleteLastTrailingCharacter(equipmets);
        map.put(String.format("TAG_WORK_EQUIPMENT_S%s", index), equipmets.toString());
    }

    /**
     *
     * @param parentOrganizations
     * @param actual
     */
    private void setParentOrganizations(List<String> parentOrganizations, ActualResultEntity actual) {
        if (Objects.nonNull(actual.getOrganizationParentName())) {
            if (!parentOrganizations.contains(actual.getOrganizationParentName())) {
                parentOrganizations.add(actual.getOrganizationParentName());
            }
        }
    }

    /**
     *
     * @param organizations
     * @param actual
     */
    private void setOrganizations(List<String> organizations, ActualResultEntity actual) {
        if (Objects.nonNull(actual.getOrganizationName())) {
            if (!organizations.contains(actual.getOrganizationName())) {
                organizations.add(actual.getOrganizationName());
            }
        }
    }

    /**
     *
     * @param parentEquipments
     * @param actual
     */
    private void setParentEquipments(List<String> parentEquipments, ActualResultEntity actual) {
        if (Objects.nonNull(actual.getEquipmentParentName())) {
            if (!parentEquipments.contains(actual.getEquipmentParentName())) {
                parentEquipments.add(actual.getEquipmentParentName());
            }
        }
    }

    /**
     *
     * @param equipments
     * @param actual
     */
    private void setEquipments(List<String> equipments, ActualResultEntity actual) {
        if (Objects.nonNull(actual.getEquipmentName())) {
            if (!equipments.contains(actual.getEquipmentName())) {
                equipments.add(actual.getEquipmentName());
            }
        }
    }

    /**
     *
     * @param interruptList
     * @param actual
     */
    private void setIntterupt(List<String> interruptList, ActualResultEntity actual) {
        if (Objects.nonNull(actual.getInterruptReason())) {
            if (!interruptList.contains(actual.getInterruptReason())) {
                interruptList.add(actual.getInterruptReason());
            }
        }
    }

    /**
     *
     * @param delayList
     * @param actual
     */
    private void setDelay(List<String> delayList, ActualResultEntity actual) {
        if (Objects.nonNull(actual.getDelayReason())) {
            if (!delayList.contains(actual.getDelayReason())) {
                delayList.add(actual.getDelayReason());
            }
        }
    }

    /**
     *
     * @param sb
     */
    private void deleteLastTrailingCharacter(StringBuilder sb) {
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    /**
     * 置換データを生成する トレーサビリティ情報
     *
     * @param map
     * @param index
     * @param actualResultList
     */
    private void createReplaceDataTraceability(Map<String, Object> map, int index, List<ActualResultEntity> actualResultList) {
        for (ActualResultEntity actualResult : actualResultList) {
            for (ActualPropertyEntity actualProperty : actualResult.getPropertyCollection()) {
                if (!CustomPropertyTypeEnum.TYPE_TRACE.equals(actualProperty.getActualPropType())) {
                    continue;
                }
                map.put(actualProperty.getActualPropName(), actualProperty.getActualPropValue());
            }
        }
    }
}
