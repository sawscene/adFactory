/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.common;

import adtekfuji.admanagerapp.kanbaneditplugin.entity.ReplaceTagResult;
import adtekfuji.admanagerapp.kanbaneditplugin.utils.ExcelFileUtils;
import adtekfuji.barcode.Barcode;
import adtekfuji.clientservice.ActualResultInfoFacade;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import jp.adtekfuji.adFactory.entity.actual.ActualPropertyEntity;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;
import jp.adtekfuji.adFactory.entity.approval.ApprovalFlowInfoEntity;
import jp.adtekfuji.adFactory.entity.assemblyparts.AssemblyPartsInfoEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.job.KanbanProduct;
import jp.adtekfuji.adFactory.entity.kanban.ApprovalEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.TraceabilityEntity;
import jp.adtekfuji.adFactory.entity.master.ServiceInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkSectionInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.ConWorkflowWorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.WorkPropertyCategoryEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.excelreplacer.ExcelReplacer;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import static org.apache.commons.codec.binary.Hex.encodeHexString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author nar-nakamura
 */
public class ReportTagFactory {

    private final Logger logger = LogManager.getLogger();

    private static final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final ActualResultInfoFacade actualResultFacade = new ActualResultInfoFacade();

    /**
     * コンストラクタ
     */
    public ReportTagFactory() {
    }

    /**
     * タグマップを作成する。
     *
     * @param kanban カンバン情報
     * @param workflow 工程カンバン情報
     * @param traces トレーサビリティ情報
     * @param useExtensionTag 「拡張フラグを使用するか」（true：使用する/false：使用しない）
     * @param useQRCodeTag 「拡張フラグを使用するか」（true：使用する/false：使用しない）
     * @param ledgerTagCase タグ識別設定
     * @return タグマップ
     */
    public Map<String, Object> createTagMap(KanbanInfoEntity kanban, WorkflowInfoEntity workflow, List<TraceabilityEntity> traces, boolean useExtensionTag, boolean useQRCodeTag, LedgerTagCase ledgerTagCase) {
        Map<String, Object> tagMap = ledgerTagCase.getMap(); // キーは大文字・小文字の違いをを無視

        // カンバンのタグマップを追加する。
        tagMap.putAll(this.createReplaceDataKanban(kanban, kanban.getActualResultCollection(), traces, useExtensionTag, ledgerTagCase));

        // ロット一個流し生産はシリアル毎に出力する。
        int outputNum;
        if (kanban.getProductionType() == 1) {
            outputNum = kanban.getLotQuantity();
        } else {
            outputNum = 1;
        }

        for (int workNo = 1; workNo <= outputNum; workNo++) {
            // 工程カンバンと工程実績のタグマップを追加する。
            tagMap.putAll(this.createReplaceDataWorkKanban(kanban.getWorkKanbanCollection(), kanban.getActualResultCollection(), 
                    kanban.getProductionType(), kanban.getLotQuantity(), workNo, useExtensionTag, ledgerTagCase));
            // 追加工程と工程実績のタグマップを追加する。
            tagMap.putAll(this.createReplaceDataSeparateWorkKanban(kanban.getSeparateworkKanbanCollection(), kanban.getActualResultCollection(), useExtensionTag, ledgerTagCase));
        }

        // QRコード
        if (useQRCodeTag) {
            tagMap.putAll(this.createReplaceDataQRCode(kanban, ledgerTagCase));
        }

        return tagMap;
    }

    /**
     * カンバンのタグマップを追加する。
     *
     * @param kanban カンバン
     * @param actuals 工程実績一覧
     * @param traces トレーサビリティ情報
     * @param useExtensionTag 「拡張フラグを使用するか」（true：使用する/false：使用しない）
     * @param ledgerTagCase タグ識別設定
     * @return タグマップ
     */
    public Map<String, Object> createReplaceDataKanban(KanbanInfoEntity kanban, List<ActualResultEntity> actuals, List<TraceabilityEntity> traces, boolean useExtensionTag, LedgerTagCase ledgerTagCase) {
        Map<String, Object> tagMap = ledgerTagCase.getMap(); // キーは大文字・小文字の違いをを無視

        // カンバン名
        tagMap.put("TAG_KANBAN_NAME", kanban.getKanbanName());
        // カンバン ステータス
        tagMap.put("TAG_KANBAN_STATUS", LocaleUtils.getString(kanban.getKanbanStatus().getResourceKey()));
        // カンバン 計画開始日時
        tagMap.put("TAG_KANBAN_PLAN_START", kanban.getStartDatetime());
        // カンバン 計画完了日時
        tagMap.put("TAG_KANBAN_PLAN_END", kanban.getCompDatetime());
        // カンバン モデル名
        tagMap.put("TAG_MODEL_NAME", kanban.getModelName());
        // カンバン ロット数量
        tagMap.put("TAG_LOT_NUM", kanban.getLotQuantity());
        // カンバン 製造番号
        tagMap.put("TAG_KANBAN_PNUM", kanban.getProductionNumber());
        // カンバン 更新者
        tagMap.put("TAG_KANBAN_UPDATER", getOrganizationName(kanban.getFkUpdatePersonId()));
        // カンバン 更新日
        tagMap.put("TAG_KANBAN_UPDATE_DATE", kanban.getUpdateDatetime());

        // 工程順名
        if (!StringUtils.isEmpty(kanban.getWorkflowName())) {
            tagMap.put("TAG_WORKFLOW_NAME", kanban.getWorkflowName());
        }

        // 工程順版数
        if (Objects.nonNull(kanban.getWorkflowName())) {
            tagMap.put("TAG_WORKFLOW_REVISION", kanban.getWorkflowRev());
        }

        //カンバン情報.工程順IDと一致する工程順情報を取得
        WorkflowInfoEntity workflowInfo = CacheUtils.getCacheWorkflow(kanban.getFkWorkflowId());
        if (Objects.nonNull(workflowInfo)) {
            // 工程順 更新者
            tagMap.put("TAG_WORKFLOW_UPDATER", getOrganizationName(workflowInfo.getFkUpdatePersonId()));

            // 工程順 更新日
            tagMap.put("TAG_WORKFLOW_UPDATE_DATE", workflowInfo.getUpdateDatetime());

            // 工程順 作業番号
            tagMap.put("TAG_WORKFLOW_WNUM", workflowInfo.getWorkflowNumber());

            // 工程順 作業時間枠　開始
            tagMap.put("TAG_WORKFLOW_TIME_START", workflowInfo.getOpenTime());

            // 工程順 作業時間枠　終了
            tagMap.put("TAG_WORKFLOW_TIME_END", workflowInfo.getCloseTime());

            // 工程順 作業順序
            tagMap.put("TAG_WORKFLOW_POLICY", workflowInfo.getSchedulePolicy());

            //拡張タグ使用フラグ
            if (useExtensionTag) {
                // 工程順 帳票テンプレート
                StringBuilder workLedgerPath = new StringBuilder();
                List<String> ledgerPathes = Arrays.asList(workflowInfo.getLedgerPath().split("\\|"));
                int index = 1;
                for (String ledgerPath : ledgerPathes) {
                    tagMap.put(String.format("TAG_WORKFLOW_REPORT_%s", index), ledgerPath);
                    index++;
                }

                // 工程順　承認者 承認日
                if (Objects.nonNull(workflowInfo.getApproval())) {
                    if (Objects.nonNull(workflowInfo.getApproval().getApprovalFlows())) {
                        List<ApprovalFlowInfoEntity> ApprovalFlows = workflowInfo.getApproval().getApprovalFlows()
                                .stream()
                                .filter(p -> ApprovalStatusEnum.APPROVE.equals(p.getApprovalState()) || ApprovalStatusEnum.FINAL_APPROVE.equals(p.getApprovalState()))
                                .collect(Collectors.toList());
                        for (ApprovalFlowInfoEntity ApprovalFlow : ApprovalFlows) {
                            tagMap.put(String.format("TAG_WORKFLOW_APPROVER_%s", ApprovalFlow.getApprovalOrder()), getOrganizationName(ApprovalFlow.getApproverId()));
                            tagMap.put(String.format("TAG_WORKFLOW_APPROVE_DATE_%s", ApprovalFlow.getApprovalOrder()), ApprovalFlow.getApprovalDatetime());
                        }
                    }
                }
            }
        }

        // カンバン 実績開始日時
        Optional<ActualResultEntity> start = actuals.stream()
                .min(Comparator.comparing(actual -> actual.getImplementDatetime()));
        if (start.isPresent()) {
            tagMap.put("TAG_KANBAN_ACTUAL_START", start.get().getImplementDatetime());
        }

        // カンバン 実績完了日時
        Optional<ActualResultEntity> compDate = actuals.stream()
                .max(Comparator.comparing(actual -> actual.getImplementDatetime()));
        if (compDate.isPresent()) {
            tagMap.put("TAG_KANBAN_ACTUAL_END", compDate.get().getImplementDatetime());
        }

        // カンバン プロパティ(プロパティ名)
        kanban.getPropertyCollection().stream().forEach((prop) -> {
            tagMap.put(String.format("TAG_KANBAN_PROPERTY(%s)", prop.getKanbanPropertyName()), prop.getKanbanPropertyValue());
        });

        // カンバン 承認情報
        if (Objects.nonNull(kanban.getApproval())) {
            List<ApprovalEntity> approves = JsonUtils.jsonToObjects(kanban.getApproval(), ApprovalEntity[].class);
            approves.stream().forEach((approve) -> {
                if (Objects.isNull(approve.getApprove()) || Objects.isNull(approve.getOrder())) {
                    return;
                }
                // 承認可否
                tagMap.put(String.format("TAG_KANBAN_APPROVE_%s", approve.getOrder()), approve.getApprove() ? LocaleUtils.getString("key.ApprovalStatus") : LocaleUtils.getString("key.DisapprovalStatus"));
                // 承認者
                if (!StringUtils.isEmpty(approve.getApprover())) {
                    tagMap.put(String.format("TAG_KANBAN_APPROVER_%s", approve.getOrder()), approve.getApprover());
                }
                // 承認理由
                if (!StringUtils.isEmpty(approve.getReason())) {
                    tagMap.put(String.format("TAG_KANBAN_APPROVE_REASON_%s", approve.getOrder()), approve.getReason());
                }
                // 承認日付
                if (Objects.nonNull(approve.getDate())) {
                    tagMap.put(String.format("TAG_KANBAN_APPROVE_DATE_%s", approve.getOrder()), approve.getDate());
                }
            });
        }

        // カンバン サービス情報
        if (!StringUtils.isEmpty(kanban.getServiceInfo())) {
            List<ServiceInfoEntity> serviceInfos = JsonUtils.jsonToObjects(kanban.getServiceInfo(), ServiceInfoEntity[].class);
            for (ServiceInfoEntity serviceInfo : serviceInfos) {
                if (StringUtils.isEmpty(serviceInfo.getService())
                        || Objects.isNull(serviceInfo.getJob())) {
                    continue;
                }

                switch (serviceInfo.getService()) {
                    case "product": // プロダクト情報
                        List<Map<String, Object>> productMapList = (List<Map<String, Object>>) serviceInfo.getJob();
                        for (Map<String, Object> productMap : productMapList) {
                            KanbanProduct kanbanProduct = new KanbanProduct(productMap);

                            // カンバン シリアル番号
                            tagMap.put(String.format("TAG_KANBAN_SERIAL_%s", kanbanProduct.getOrderNumber()), kanbanProduct.getUid());
                            // カンバン シリアルの不良理由
                            tagMap.put(String.format("TAG_KANBAN_DEFECT_%s", kanbanProduct.getOrderNumber()), kanbanProduct.getDefect());
                        }
                        break;
                    case "els":
                    default:
                        // 出力タグなし
                }
            }
        }

        // カンバンステータスが不良の場合のみ、ロットアウト時の不良理由を出力する。
        if (Objects.equals(kanban.getKanbanStatus(), KanbanStatusEnum.DEFECT)) {
            // 一番新しい不良実績を取得する。
            Optional<ActualResultEntity> optDefect = actuals.stream()
                    .filter(p -> Objects.equals(p.getActualStatus(), KanbanStatusEnum.DEFECT))
                    .sorted(Comparator.comparing(ActualResultEntity::getImplementDatetime).reversed())
                    .findFirst();
            if (optDefect.isPresent()) {
                // カンバン 不良理由
                tagMap.put("TAG_KANBAN_DEFECT", optDefect.get().getDefectReason());
            }
        }

        // トレーサビリティ情報
        this.createReplaceDataTraceabilityData(tagMap, traces);

        return tagMap;
    }

    /**
     * 工程カンバンと工程実績のタグマップを作成する。
     *
     * @param workKanbans 工程カンバン一覧
     * @param actuals 工程実績一覧
     * @param productionType 生産タイプ
     * @param lotQuantity ロット数
     * @param workNo シリアル毎に出力する場合の番号(1～)
     * @param useExtensionTag 「拡張フラグを使用するか」（true：使用する/false：使用しない）
     * @param ledgerTagCase タグ識別設定
     * @return タグマップ
     */
    public Map<String, Object> createReplaceDataWorkKanban(List<WorkKanbanInfoEntity> workKanbans, List<ActualResultEntity> actuals, Integer productionType, Integer lotQuantity, Integer workNo, boolean useExtensionTag, LedgerTagCase ledgerTagCase) {
        Map<String, Object> tagMap = ledgerTagCase.getMap(); // キーは大文字・小文字の違いをを無視
//        String workflowName = null;
//        String workRevison = null;

        // 工程カンバンの表示順でソートする。
        workKanbans.sort(Comparator.comparing(work -> work.getWorkKanbanOrder()));

        for (int index = 1; workKanbans.size() >= index; index++) {
            WorkKanbanInfoEntity work = workKanbans.get(index - 1);
 
            int workIndex = index;
            if (productionType != 0 && Objects.nonNull(lotQuantity) && lotQuantity > 1 && Objects.nonNull(workNo)) {
                // ロット生産でworkNoが指定されている場合
                workIndex = Double.valueOf(Math.ceil(Integer.valueOf(index).doubleValue() / lotQuantity.doubleValue())).intValue();
                if (!workNo.equals(work.getSerialNumber())) {
                    // 他のシリアル番号は無視
                    continue;
                }
            }
            
            List<ActualResultEntity> workingList = new ArrayList<>();
            List<ActualResultEntity> traceabilityList = new ArrayList<>();
            List<String> interruptList = new ArrayList<>();
            List<String> delayList = new ArrayList<>();
            List<String> parentOrganizations = new ArrayList<>();
            List<String> parentEquipments = new ArrayList<>();
            List<String> organizations = new ArrayList<>();
            List<String> equipments = new ArrayList<>();

            for (ActualResultEntity actual : actuals) {
                if (actual.getFkWorkKanbanId().equals(work.getWorkKanbanId())) {
//                    if (Objects.isNull(workflowName) && Objects.isNull(workRevison)) {
//                        workflowName = actual.getWorkflowName();
//                        workRevison = actual.getWorkflowRevision();
//                    }

                    // 工程実績の親組織を、親組織一覧に追加する。
                    this.setParentOrganizations(parentOrganizations, actual);
                    // 工程実績の組織を、組織一覧に追加する。
                    this.setOrganizations(organizations, actual);
                    // 工程実績の親設備を、親設備一覧に追加する。
                    this.setParentEquipments(parentEquipments, actual);
                    // 工程実績の設備を、設備一覧に追加する。
                    this.setEquipments(equipments, actual);

                    switch (actual.getActualStatus()) {
                        case WORKING:
                            workingList.add(actual);
                            break;
                        case COMPLETION:
                            traceabilityList.add(actual);

                            // 工程実績の遅延理由を、遅延理由一覧に追加する。
                            this.setDelay(delayList, actual);
                            break;
                        case SUSPEND:
                            traceabilityList.add(actual);

                            // 工程実績の中断理由を、中断理由一覧に追加する。
                            this.setIntterupt(interruptList, actual);
                            break;
                        default:
                            break;
                    }
                }
            }

            // 工程名
            tagMap.put(String.format("TAG_WORK_NAME%s", workIndex), work.getWorkName());
            // 工程カンバンステータス
            tagMap.put(String.format("TAG_WORK_STATUS%s", workIndex), LocaleUtils.getString(work.getWorkStatus().getResourceKey()));
            // 工程カンバン タクトタイム
            tagMap.put(String.format("TAG_WORK_TAKTTIME%s", workIndex), work.getTaktTime());
            // 工程カンバン 作業時間
            tagMap.put(String.format("TAG_WORK_WORKTIME%s", workIndex), work.getSumTimes());
            // 工程カンバン 計画開始日時
            tagMap.put(String.format("TAG_WORK_PLAN_START%s", workIndex), work.getStartDatetime());
            // 工程カンバン 計画完了日時
            tagMap.put(String.format("TAG_WORK_PLAN_END%s", workIndex), work.getCompDatetime());
            // 工程カンバン スキップ
            tagMap.put(String.format("TAG_WORK_SKIP%s", workIndex), work.getSkipFlag());

            //工程カンバン情報.工程IDと一致する工程情報を取得
            WorkInfoEntity workInfo = CacheUtils.getCacheWork(work.getFkWorkId());
            if (Objects.nonNull(workInfo)) {
                // 工程 版数
                tagMap.put(String.format("TAG_WORK_REVISION%s", workIndex), workInfo.getWorkRev());
                // 工程 更新者
                tagMap.put(String.format("TAG_WORK_UPDATER%s", workIndex), getOrganizationName(workInfo.getUpdatePersonId()));
                // 工程 更新日
                tagMap.put(String.format("TAG_WORK_UPDATE_DATE%s", workIndex), workInfo.getUpdateDatetime());
                // 工程 作業番号
                tagMap.put(String.format("TAG_WORK_WNUM%s", workIndex), workInfo.getWorkNumber());
                // 工程 作業内容
                tagMap.put(String.format("TAG_WORK_CONTENTS%s", workIndex), workInfo.getContent());

                //拡張タグ使用フラグ
                if (useExtensionTag) {
                    //工程　承認者
                    if (Objects.nonNull(workInfo.getApproval())) {
                        if (Objects.nonNull(workInfo.getApproval().getApprovalFlows())) {
                            List<ApprovalFlowInfoEntity> ApprovalFlows = workInfo.getApproval().getApprovalFlows()
                                    .stream()
                                    .filter(p -> ApprovalStatusEnum.APPROVE.equals(p.getApprovalState()) || ApprovalStatusEnum.FINAL_APPROVE.equals(p.getApprovalState()))
                                    .collect(Collectors.toList());;
                            for (ApprovalFlowInfoEntity ApprovalFlow : ApprovalFlows) {
                                tagMap.put(String.format("TAG_WORK_APPROVER%s_%s", workIndex, ApprovalFlow.getApprovalOrder()), getOrganizationName(ApprovalFlow.getApproverId()));
                                tagMap.put(String.format("TAG_WORK_APPROVE_DATE%s_%s", workIndex, ApprovalFlow.getApprovalOrder()), ApprovalFlow.getApprovalDatetime());
                            }
                        }
                    }
                    // 工程 シート名
                    for (WorkSectionInfoEntity workSection : workInfo.getWorkSectionCollection()) {
                        tagMap.put(String.format("TAG_WORK_SHEET%s_%s", workIndex, workSection.getWorkSectionOrder()), workSection.getDocumentTitle());

                        List<WorkPropertyInfoEntity> workProperties = workInfo.getPropertyInfoCollection().stream()
                                .filter(p -> workSection.getWorkSectionOrder().equals(p.getWorkSectionOrder()))
                                .sorted(Comparator.comparing(WorkPropertyInfoEntity::getWorkPropOrder))
                                .collect(Collectors.toList());
                        //品質トレーサビリティ
                        int dispIndex = 1;
                        for (WorkPropertyInfoEntity PropertyInfo : workProperties) {
                            //品質トレーサビリティ 種別
                            tagMap.put(String.format("TAG_TRACE_CATGORY%s_%s_%s", index, workSection.getWorkSectionOrder(), dispIndex), WorkPropertyCategoryEnum.getName(rb, PropertyInfo.getWorkPropCategory()));
                            //品質トレーサビリティ 項目名
                            tagMap.put(String.format("TAG_TRACE_NAME%s_%s_%s", index, workSection.getWorkSectionOrder(), dispIndex), PropertyInfo.getWorkPropName());
                            //品質トレーサビリティ 基準値 下限
                            tagMap.put(String.format("TAG_TRACE_MIN%s_%s_%s", index, workSection.getWorkSectionOrder(), dispIndex), PropertyInfo.getWorkPropLowerTolerance());
                            //品質トレーサビリティ 基準値 上限
                            tagMap.put(String.format("TAG_TRACE_MAX%s_%s_%s", index, workSection.getWorkSectionOrder(), dispIndex), PropertyInfo.getWorkPropUpperTolerance());

                            dispIndex++;
                        }
                    }
                }
            }

            // 工程カンバン 中断理由
            StringBuilder interrupts = new StringBuilder();
            for (String interrupt : interruptList) {
                interrupts.append(interrupt);
                interrupts.append(",");
            }

            this.deleteLastTrailingCharacter(interrupts);

            tagMap.put(String.format("TAG_WORK_INTERRUPT%s", workIndex), interrupts.toString());

            // 工程カンバン 遅延理由
            StringBuilder delays = new StringBuilder();
            for (String delay : delayList) {
                delays.append(delay);
                delays.append(",");
            }

            this.deleteLastTrailingCharacter(delays);

            tagMap.put(String.format("TAG_WORK_DELAY%s", workIndex), delays.toString());

            // シリアル番号 実績完了日時
            if (!StringUtils.isEmpty(work.getServiceInfo())) {
                List<ServiceInfoEntity> serviceInfos = JsonUtils.jsonToObjects(work.getServiceInfo(), ServiceInfoEntity[].class);
                for (ServiceInfoEntity serviceInfo : serviceInfos) {
                    if (StringUtils.isEmpty(serviceInfo.getService())
                            || Objects.isNull(serviceInfo.getJob())) {
                        continue;
                    }

                    switch (serviceInfo.getService()) {
                        case "product": // プロダクト情報
                            List<KanbanProduct> products = KanbanProduct.toKanbanProducts(serviceInfo);
                            for (KanbanProduct o : products) {
                                tagMap.put(String.format("TAG_WORK_ACTUAL_START%s_%s", workIndex, o.getOrderNumber()), Objects.nonNull(o.getStartTime()) ? DateUtils.parse(o.getStartTime()) : "");
                                tagMap.put(String.format("TAG_WORK_ACTUAL_END%s_%s", workIndex, o.getOrderNumber()), Objects.nonNull(o.getCompTime()) ? DateUtils.parse(o.getCompTime()) : "");
                            }
                            break;

                        case "els":
                        default:
                            // 出力タグなし
                    }
                }
            }
            
            //拡張タグ使用フラグ
            if (useExtensionTag) {
                // 割当組織一覧の取得
                List<String> assignOrganizations = getAssignOrganizations(work.getOrganizationCollection());
                StringBuilder Organizations = new StringBuilder();
                for (String Organization : assignOrganizations) {
                    Organizations.append(Organization);
                    Organizations.append(",");
                }

                this.deleteLastTrailingCharacter(Organizations);

                //工程カンバン 割当作業者
                tagMap.put(String.format("TAG_WORK_ASSIGN_ORGANIZATION%s", workIndex), Organizations.toString());

                // 割当設備一覧の取得
                List<String> assignEquipments = getAssignEquipments(work.getEquipmentCollection());
                StringBuilder Equipments = new StringBuilder();
                for (String Equipment : assignEquipments) {
                    Equipments.append(Equipment);
                    Equipments.append(",");
                }

                this.deleteLastTrailingCharacter(Equipments);

                //工程カンバン 割当設備
                tagMap.put(String.format("TAG_WORK_ASSIGN_EQUIPMENT%s", workIndex), Equipments.toString());
            }

            // 工程カンバンプロパティ
            for (WorkKanbanPropertyInfoEntity pro : work.getPropertyCollection()) {
                tagMap.put(String.format("TAG_WORK_PROPERTY%s(%s)", workIndex, pro.getWorkKanbanPropName()), pro.getWorkKanbanPropValue());
            }

            // タグマップに、工程カンバンの工程実績タグマップを追加する。
            this.createReplaceDataWorkKanbanActual(tagMap, workIndex, workingList, traceabilityList, parentOrganizations, organizations, parentEquipments, equipments);

            // 工程実績情報からトレーサビリティ情報を取得して、タグマップに追加する。
            this.createReplaceDataTraceability(tagMap, traceabilityList);

            //拡張タグ使用フラグ
            if (useExtensionTag) {
                Comparator<ActualResultEntity> comparator
                        = Comparator.comparing(ActualResultEntity::getImplementDatetime)
                                .thenComparing(ActualResultEntity::getActualId);
                List<ActualResultEntity> actualResults = actuals.stream()
                        .filter(p -> p.getFkWorkKanbanId().equals(work.getWorkKanbanId()))
                        .sorted(comparator)
                        .collect(Collectors.toList());
                int actualIndex = 1;
                for (ActualResultEntity actualResult : actualResults) {
                    // 作業履歴 作業者
                    tagMap.put(String.format("TAG_HIS_ORGANIZATION%s_%s", workIndex, actualIndex), actualResult.getOrganizationName());
                    // 作業履歴 設備
                    tagMap.put(String.format("TAG_HIS_EQUIPMENT%s_%s", workIndex, actualIndex), actualResult.getEquipmentName());
                    // 作業履歴 中断理由
                    tagMap.put(String.format("TAG_HIS_SUSPEND_REASON%s_%s", workIndex, actualIndex), actualResult.getInterruptReason());
                    // 作業履歴 遅延理由
                    tagMap.put(String.format("TAG_HIS_DELAY_REASON%s_%s", workIndex, actualIndex), actualResult.getDelayReason());
                    // 作業履歴 ステータス
                    tagMap.put(String.format("TAG_HIS_STATUS%s_%s", workIndex, actualIndex), KanbanStatusEnum.getMessage(rb, actualResult.getActualStatus()));
                    // 作業履歴 中断時間
                    tagMap.put(String.format("TAG_HIS_SUSPEND%s_%s", workIndex, actualIndex), actualResult.getNonWorkTime());

                    // 作業履歴の開始時間、完了時間
                    switch (actualResult.getActualStatus()) {
                        case WORKING:
                            tagMap.put(String.format("TAG_HIS_START%s_%s", workIndex, actualIndex), actualResult.getImplementDatetime());
                            break;
                        case SUSPEND:
                        case COMPLETION:
                            tagMap.put(String.format("TAG_HIS_COMP%s_%s", workIndex, actualIndex), actualResult.getImplementDatetime());
                            break;
                    }

                    // 品質トレーサビリティ実績
                    for (ActualPropertyEntity actualProperty : actualResult.getPropertyCollection()) {
                        if (!CustomPropertyTypeEnum.TYPE_TRACE.equals(actualProperty.getActualPropType())) {
                            continue;
                        }

                        tagMap.put(String.format("TAG_HIS_TRACE%s_%s(%s)", workIndex, actualIndex, actualProperty.getActualPropName()), actualProperty.getActualPropValue());
                    }

                    actualIndex++;
                }
            }
        }

        return tagMap;
    }

    /**
     * タグマップに、工程カンバンの工程実績タグマップを追加する。
     *
     * @param map タグマップ
     * @param index 工程の連番(1～)
     * @param workingList 開始実績一覧
     * @param compList 完了実績一覧
     * @param parentOrganizations 親組織一覧
     * @param organizations 組織一覧
     * @param parentEquipments 親設備一覧
     * @param equipments 設備一覧
     */
    public void createReplaceDataWorkKanbanActual(Map<String, Object> map, int index, List<ActualResultEntity> workingList, List<ActualResultEntity> compList, List<String> parentOrganizations, List<String> organizations, List<String> parentEquipments, List<String> equipments) {
        // 工程カンバン 実績開始日時
        Optional<ActualResultEntity> start = workingList.stream()
                .min(Comparator.comparing(entity -> entity.getImplementDatetime()));
        if (start.isPresent()) {
            map.put(String.format("TAG_WORK_ACTUAL_START%s", index), start.get().getImplementDatetime());
        }
        
        // 工程カンバン 実績完了日時
        Optional<ActualResultEntity> compDate = compList.stream()
                .max(Comparator.comparing(entity -> entity.getImplementDatetime()));
        if (compDate.isPresent()) {
            map.put(String.format("TAG_WORK_ACTUAL_END%s", index), compDate.get().getImplementDatetime());
        }

        // 工程カンバン 実施親組織
        StringBuilder workParentOrganizations = new StringBuilder();
        for (String parentOrganization : parentOrganizations) {
            workParentOrganizations.append(parentOrganization);
            workParentOrganizations.append(",");
        }

        this.deleteLastTrailingCharacter(workParentOrganizations);

        map.put(String.format("TAG_WORK_PARENT_ORGANIZATION%s", index), workParentOrganizations.toString());

        // 工程カンバン 実施組織
        StringBuilder workOrganizations = new StringBuilder();
        for (String organization : organizations) {
            workOrganizations.append(organization);
            workOrganizations.append(",");
        }

        this.deleteLastTrailingCharacter(workOrganizations);

        map.put(String.format("TAG_WORK_ORGANIZATION%s", index), workOrganizations.toString());

        // 工程カンバン 実施親設備
        StringBuilder workParentEquipments = new StringBuilder();
        for (String parentEquipmet : parentEquipments) {
            workParentEquipments.append(parentEquipmet);
            workParentEquipments.append(",");
        }

        this.deleteLastTrailingCharacter(workParentEquipments);

        map.put(String.format("TAG_WORK_PARENT_EQUIPMENT%s", index), workParentEquipments.toString());

        // 工程カンバン 実施設備
        StringBuilder workEquipmets = new StringBuilder();
        for (String equipmet : equipments) {
            workEquipmets.append(equipmet);
            workEquipmets.append(",");
        }

        this.deleteLastTrailingCharacter(workEquipmets);

        map.put(String.format("TAG_WORK_EQUIPMENT%s", index), workEquipmets.toString());
    }

    /**
     * 追加工程カンバンと工程実績のタグマップを作成する。
     *
     * @param workKanbans 追加工程カンバン一覧
     * @param actuals 工程実績一覧
     * @param useExtensionTag 「拡張フラグを使用するか」（true：使用する/false：使用しない）
     * @param ledgerTagCase タグ識別設定
     * @return タグマップ
     */
    public Map<String, Object> createReplaceDataSeparateWorkKanban(List<WorkKanbanInfoEntity> workKanbans, List<ActualResultEntity> actuals, boolean useExtensionTag, LedgerTagCase ledgerTagCase) {
        Map<String, Object> tagMap = ledgerTagCase.getMap(); // キーは大文字・小文字の違いをを無視

        // 追加工程カンバンの表示順でソートする。
        workKanbans.sort(Comparator.comparing(work -> work.getWorkKanbanOrder()));

        for (Integer index = 1; workKanbans.size() >= index; index++) {
            WorkKanbanInfoEntity work = workKanbans.get(index - 1);

            List<ActualResultEntity> workingList = new ArrayList<>();
            List<ActualResultEntity> traceabilityList = new ArrayList<>();
            List<String> interruptList = new ArrayList<>();
            List<String> delayList = new ArrayList<>();
            List<String> parentOrganizations = new ArrayList<>();
            List<String> parentEquipments = new ArrayList<>();
            List<String> organizations = new ArrayList<>();
            List<String> equipments = new ArrayList<>();

            for (ActualResultEntity actual : actuals) {
                if (actual.getFkWorkKanbanId().equals(work.getWorkKanbanId())) {
                    // 工程実績の親組織を、親組織一覧に追加する。
                    this.setParentOrganizations(parentOrganizations, actual);
                    // 工程実績の組織を、組織一覧に追加する。
                    this.setOrganizations(organizations, actual);
                    // 工程実績の親設備を、親設備一覧に追加する。
                    this.setParentEquipments(parentEquipments, actual);
                    // 工程実績の設備を、設備一覧に追加する。
                    this.setEquipments(equipments, actual);

                    switch (actual.getActualStatus()) {
                        case WORKING:
                            workingList.add(actual);
                            break;
                        case COMPLETION:
                            traceabilityList.add(actual);

                            // 工程実績の遅延理由を、遅延理由一覧に追加する。
                            this.setDelay(delayList, actual);
                            break;
                        case SUSPEND:
                            traceabilityList.add(actual);

                            // 工程実績の中断理由を、中断理由一覧に追加する。
                            this.setIntterupt(interruptList, actual);
                            break;
                        default:
                            break;
                    }
                }
            }

            // 追加工程名
            tagMap.put(String.format("TAG_WORK_NAME_S%s", index), work.getWorkName());
            // 追加工程カンバンステータス
            tagMap.put(String.format("TAG_WORK_STATUS_S%s", index), LocaleUtils.getString(work.getWorkStatus().getResourceKey()));
            // 追加工程カンバン タクトタイム
            tagMap.put(String.format("TAG_WORK_TAKTTIME_S%s", index), work.getTaktTime());
            // 追加工程カンバン 作業時間
            tagMap.put(String.format("TAG_WORK_WORKTIME_S%s", index), work.getSumTimes());
            // 追加工程カンバン 計画開始日時
            tagMap.put(String.format("TAG_WORK_PLAN_START_S%s", index), work.getStartDatetime());
            // 追加工程カンバン 計画完了日時
            tagMap.put(String.format("TAG_WORK_PLAN_END_S%s", index), work.getCompDatetime());
            // 追加工程カンバン スキップ
            tagMap.put(String.format("TAG_WORK_SKIP_S%s", index), work.getSkipFlag());

            //追加工程カンバン情報.工程IDと一致する工程情報を取得
            WorkInfoEntity workInfo = CacheUtils.getCacheWork(work.getFkWorkId());
            if (Objects.nonNull(workInfo)) {
                // 工程 版数
                tagMap.put(String.format("TAG_WORK_REVISION_S%s", index), workInfo.getWorkRev());
                // 工程 更新者
                tagMap.put(String.format("TAG_WORK_UPDATER_S%s", index), getOrganizationName(workInfo.getUpdatePersonId()));
                // 工程 更新日
                tagMap.put(String.format("TAG_WORK_UPDATE_DATE_S%s", index), workInfo.getUpdateDatetime());
                // 工程 作業番号
                tagMap.put(String.format("TAG_WORK_WNUM_S%s", index), workInfo.getWorkNumber());
                // 工程 作業内容
                tagMap.put(String.format("TAG_WORK_CONTENTS_S%s", index), workInfo.getContent());

                //拡張タグ使用フラグ
                if (useExtensionTag) {
                    //工程　承認者
                    if (Objects.nonNull(workInfo.getApproval())) {
                        if (Objects.nonNull(workInfo.getApproval().getApprovalFlows())) {
                            List<ApprovalFlowInfoEntity> ApprovalFlows = workInfo.getApproval().getApprovalFlows()
                                    .stream()
                                    .filter(p -> ApprovalStatusEnum.APPROVE.equals(p.getApprovalState()) || ApprovalStatusEnum.FINAL_APPROVE.equals(p.getApprovalState()))
                                    .collect(Collectors.toList());;
                            for (ApprovalFlowInfoEntity ApprovalFlow : ApprovalFlows) {
                                tagMap.put(String.format("TAG_WORK_APPROVER_S%s_%s", index, ApprovalFlow.getApprovalOrder()), getOrganizationName(ApprovalFlow.getApproverId()));
                                tagMap.put(String.format("TAG_WORK_APPROVE_DATE_S%s_%s", index, ApprovalFlow.getApprovalOrder()), ApprovalFlow.getApprovalDatetime());
                            }
                        }
                    }
                    // 工程 シート名   
                    for (WorkSectionInfoEntity workSection : workInfo.getWorkSectionCollection()) {
                        tagMap.put(String.format("TAG_WORK_SHEET_S%s_%s", index, workSection.getWorkSectionOrder()), workSection.getDocumentTitle());

                        List<WorkPropertyInfoEntity> workProperties = workInfo.getPropertyInfoCollection().stream()
                                .filter(p -> workSection.getWorkSectionOrder().equals(p.getWorkSectionOrder()))
                                .sorted(Comparator.comparing(WorkPropertyInfoEntity::getWorkPropOrder))
                                .collect(Collectors.toList());
                        //品質トレーサビリティ
                        int dispIndex = 1;
                        for (WorkPropertyInfoEntity PropertyInfo : workProperties) {
                            //品質トレーサビリティ 種別
                            tagMap.put(String.format("TAG_TRACE_CATGORY_S%s_%s_%s", index, workSection.getWorkSectionOrder(), dispIndex), WorkPropertyCategoryEnum.getName(rb, PropertyInfo.getWorkPropCategory()));
                            //品質トレーサビリティ 項目名
                            tagMap.put(String.format("TAG_TRACE_NAME_S%s_%s_%s", index, workSection.getWorkSectionOrder(), dispIndex), PropertyInfo.getWorkPropName());
                            //品質トレーサビリティ 基準値 下限
                            tagMap.put(String.format("TAG_TRACE_MIN_S%s_%s_%s", index, workSection.getWorkSectionOrder(), dispIndex), PropertyInfo.getWorkPropLowerTolerance());
                            //品質トレーサビリティ 基準値 上限
                            tagMap.put(String.format("TAG_TRACE_MAX_S%s_%s_%s", index, workSection.getWorkSectionOrder(), dispIndex), PropertyInfo.getWorkPropUpperTolerance());

                            dispIndex++;
                        }
                    }
                }
            }

            // 追加工程カンバン 中断理由
            StringBuilder interrupts = new StringBuilder();
            for (String interrupt : interruptList) {
                interrupts.append(interrupt);
                interrupts.append(",");
            }

            this.deleteLastTrailingCharacter(interrupts);

            tagMap.put(String.format("TAG_WORK_INTERRUPT_S%s", index), interrupts.toString());

            // 追加工程カンバン 遅延理由
            StringBuilder delays = new StringBuilder();
            for (String delay : delayList) {
                delays.append(delay);
                delays.append(",");
            }

            this.deleteLastTrailingCharacter(delays);

            tagMap.put(String.format("TAG_WORK_DELAY_S%s", index), delays.toString());

            // 追加工程カンバンプロパティ
            for (WorkKanbanPropertyInfoEntity pro : work.getPropertyCollection()) {
                tagMap.put(String.format("TAG_WORK_PROPERTY_S%s(%s)", index, pro.getWorkKanbanPropName()), pro.getWorkKanbanPropValue());
            }

            //拡張タグ使用フラグ
            if (useExtensionTag) {
                // 割当組織一覧の取得    
                List<String> assignOrganizations = new ArrayList<>();
                assignOrganizations = getAssignOrganizations(work.getOrganizationCollection());
                StringBuilder Organizations = new StringBuilder();
                for (String Organization : assignOrganizations) {
                    Organizations.append(Organization);
                    Organizations.append(",");
                }

                this.deleteLastTrailingCharacter(Organizations);

                // 追加工程カンバン 割当作業者
                tagMap.put(String.format("TAG_WORK_ASSIGN_ORGANIZATION_S%s", index), Organizations.toString());

                // 割当設備一覧の取得
                List<String> assignEquipments = new ArrayList<>();
                assignEquipments = getAssignEquipments(work.getEquipmentCollection());
                StringBuilder Equipments = new StringBuilder();
                for (String Equipment : assignEquipments) {
                    Equipments.append(Equipment);
                    Equipments.append(",");
                }

                this.deleteLastTrailingCharacter(Equipments);

                // 追加工程カンバン 割当設備
                tagMap.put(String.format("TAG_WORK_ASSIGN_EQUIPMENT_S%s", index), Equipments.toString());
            }

            // タグマップに、追加工程カンバンの工程実績タグマップを追加する。
            this.createReplaceDataSeparateWorkKanbanActual(tagMap, index, workingList, traceabilityList, parentOrganizations, organizations, parentEquipments, equipments);

            // 工程実績情報からトレーサビリティ情報を取得して、タグマップに追加する。
            this.createReplaceDataTraceability(tagMap, traceabilityList);

            //拡張タグ使用フラグ
            if (useExtensionTag) {
                Comparator<ActualResultEntity> comparator
                        = Comparator.comparing(ActualResultEntity::getImplementDatetime)
                                .thenComparing(ActualResultEntity::getActualId);
                List<ActualResultEntity> actualResults = actuals.stream()
                        .filter(p -> p.getFkWorkKanbanId().equals(work.getWorkKanbanId()))
                        .sorted(comparator)
                        .collect(Collectors.toList());
                int actualIndex = 1;
                for (ActualResultEntity actualResult : actualResults) {
                    // 作業履歴 作業者
                    tagMap.put(String.format("TAG_HIS_ORGANIZATION_S%s_%s", index, actualIndex), actualResult.getOrganizationName());
                    // 作業履歴 設備
                    tagMap.put(String.format("TAG_HIS_EQUIPMENT_S%s_%s", index, actualIndex), actualResult.getEquipmentName());
                    // 作業履歴 中断理由
                    tagMap.put(String.format("TAG_HIS_SUSPEND_REASON_S%s_%s", index, actualIndex), actualResult.getInterruptReason());
                    // 作業履歴 遅延理由
                    tagMap.put(String.format("TAG_HIS_DELAY_REASON_S%s_%s", index, actualIndex), actualResult.getDelayReason());
                    // 作業履歴 ステータス
                    tagMap.put(String.format("TAG_HIS_STATUS_S%s_%s", index, actualIndex), KanbanStatusEnum.getMessage(rb, actualResult.getActualStatus()));
                    // 作業履歴 中断時間
                    tagMap.put(String.format("TAG_HIS_SUSPEND_S%s_%s", index, actualIndex), actualResult.getNonWorkTime());

                    // 作業履歴の開始時間、完了時間
                    switch (actualResult.getActualStatus()) {
                        case WORKING:
                            tagMap.put(String.format("TAG_HIS_START_S%s_%s", index, actualIndex), actualResult.getImplementDatetime());
                            break;
                        case SUSPEND:
                        case COMPLETION:
                            tagMap.put(String.format("TAG_HIS_COMP_S%s_%s", index, actualIndex), actualResult.getImplementDatetime());
                            break;
                    }

                    // 作業履歴 品質トレーサビリティ実績
                    for (ActualPropertyEntity actualProperty : actualResult.getPropertyCollection()) {
                        if (!CustomPropertyTypeEnum.TYPE_TRACE.equals(actualProperty.getActualPropType())) {
                            continue;
                        }

                        tagMap.put(String.format("TAG_HIS_TRACE_S%s_%s(%s)", index, actualIndex, actualProperty.getActualPropName()), actualProperty.getActualPropValue());
                    }

                    actualIndex++;
                }
            }
        }

        return tagMap;
    }

    /**
     * タグマップに、追加工程カンバンの工程実績タグマップを追加する。
     *
     * @param tagMap タグマップ
     * @param index 工程の連番(1～)
     * @param workingList 開始実績一覧
     * @param compList 完了実績一覧
     * @param parentOrganizations 親組織一覧
     * @param organizations 組織一覧
     * @param parentEquipments 親設備一覧
     * @param equipments 設備一覧
     */
    private void createReplaceDataSeparateWorkKanbanActual(Map<String, Object> tagMap, int index, List<ActualResultEntity> workingList, List<ActualResultEntity> compList, List<String> parentOrganizations, List<String> organizations, List<String> parentEquipments, List<String> equipments) {
        // 追加工程カンバン 実績開始日時
        Optional<ActualResultEntity> start = workingList.stream()
                .min(Comparator.comparing(entity -> entity.getImplementDatetime()));
        if (start.isPresent()) {
            tagMap.put(String.format("TAG_WORK_ACTUAL_START_S%s", index), start.get().getImplementDatetime());
        }

        // 追加工程カンバン 実績完了日時
        Optional<ActualResultEntity> compDate = compList.stream()
                .max(Comparator.comparing(entity -> entity.getImplementDatetime()));
        if (compDate.isPresent()) {
            tagMap.put(String.format("TAG_WORK_ACTUAL_END_S%s", index), compDate.get().getImplementDatetime());
        }

        // 追加工程カンバン 実施親組織
        StringBuilder workParentOrganizations = new StringBuilder();
        for (String parentOrganization : parentOrganizations) {
            workParentOrganizations.append(parentOrganization);
            workParentOrganizations.append(",");
        }

        this.deleteLastTrailingCharacter(workParentOrganizations);

        tagMap.put(String.format("TAG_WORK_PARENT_ORGANIZATION_S%s", index), workParentOrganizations.toString());

        // 追加工程カンバン 実施組織
        StringBuilder workOrganizations = new StringBuilder();
        for (String organization : organizations) {
            workOrganizations.append(organization);
            workOrganizations.append(",");
        }

        this.deleteLastTrailingCharacter(workOrganizations);

        tagMap.put(String.format("TAG_WORK_ORGANIZATION_S%s", index), workOrganizations.toString());

        // 追加工程カンバン 実施親設備
        StringBuilder workParentEquipments = new StringBuilder();
        for (String parentEquipmet : parentEquipments) {
            workParentEquipments.append(parentEquipmet);
            workParentEquipments.append(",");
        }

        this.deleteLastTrailingCharacter(workParentEquipments);

        tagMap.put(String.format("TAG_WORK_PARENT_EQUIPMENT_S%s", index), workParentEquipments.toString());

        // 追加工程カンバン 実施設備
        StringBuilder workEquipmets = new StringBuilder();
        for (String equipmet : equipments) {
            workEquipmets.append(equipmet);
            workEquipmets.append(",");
        }

        this.deleteLastTrailingCharacter(workEquipmets);

        tagMap.put(String.format("TAG_WORK_EQUIPMENT_S%s", index), workEquipmets.toString());
    }

    /**
     * 画像データ用帳票リプレースデータ作成
     *
     * @param kanbanId カンバンID
     * @param sheet ワークシート
     * @param kanbanNo カンバン番号
     * @param ledgerTagCase タグ識別設定
     * @return イメージタグのマップ
     */
    public Map<String, Object> createReplacePictureData(long kanbanId, Sheet sheet, int kanbanNo, LedgerTagCase ledgerTagCase) throws IOException {
        Map<String, Object> map = ledgerTagCase.getMap(); // キーは大文字・小文字の違いをを無視

        // 画像データタグのリストを作成
        List<String> imageTagNames = ExcelReplacer.getPictureTag(sheet, kanbanNo, ledgerTagCase.getTagConverter());

        if (!imageTagNames.isEmpty()) {
            for (String imageTagName : imageTagNames) {
                String picImagePath = this.pictureTagDataGet(kanbanId, imageTagName);
                if (!(picImagePath == null)) {
                    // 画像データをマッピング
                    map.put(imageTagName, picImagePath);
                }
            }
        }
        return map;
    }

    /**
     * 画像データ一時ファイル生成
     *
     * @param picKanbanId カンバンID
     * @param picTag 画像データタグ
     * @return ファイル名(フルパス)
     */
    private String pictureTagDataGet(Long picKanbanId, String picTag) {
        byte[] data;
        try {
            // 画像データ取得
            data = actualResultFacade.downloadFileData(picKanbanId, picTag);
        } catch (RuntimeException ex) {
            logger.fatal(ex.getMessage());
            return null;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }

        // 画像データファイル格納ディレクトリ
        StringBuilder filename = new StringBuilder();
        filename.append(System.getenv("ADFACTORY_HOME")).append(File.separator);
        filename.append("temp").append(File.separator);
        filename.append("picturedata");

        // 画像データ格納ディレクトリ生成
        File directory = new File(filename.toString());
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                logger.error("PictureDataDirectory Create Error");
                return null;
            }
        }
        // 画像ファイル種別判定
        byte[] picHeader = new byte[8];
        int roopCnt = 0;
        String fileType = "";

        for (roopCnt = 0; roopCnt < 8; roopCnt++) {
            picHeader[roopCnt] = data[roopCnt];
        }
        String fileHeader = encodeHexString(picHeader);
        fileHeader = fileHeader.toUpperCase();

        if (fileHeader.equals("89504E470D0A1A0A")) {
            fileType = "png";
        } else if (fileHeader.matches("^FFD8.*")) {
            fileType = "jpg";
        } else if (fileHeader.matches("^474946383961.*") || fileHeader.matches("^474946383761.*")) {
            fileType = "gif";
        } else if (fileHeader.matches("^424D.*")) {
            fileType = "bmp";
        } else {
            logger.error("PictureDataFile Type Error");
            return null;
        }

        // ファイル名（フルパス）
        filename.append(File.separator).append(picTag).append(".").append(fileType);
        File picFile = new File(filename.toString());
        if (picFile.exists()) {
            // 対象ファイルが存在していたら削除
            if (!picFile.delete()) {
                logger.error("PictureDataFile Delete Error");
                return null;
            }
        }
        try {
            // ファイル生成
            InputStream picImage = new ByteArrayInputStream(data);
            BufferedImage buffImage = ImageIO.read(picImage);
            ImageIO.write(buffImage, fileType, new File(filename.toString()));
            return filename.toString();
        } catch (Exception ex) {
            logger.error(filename.toString());
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * ダウンロードファイル削除
     *
     * @param deleteDirectory 削除フォルダ名
     */
    public void downloadTempFileDelete(String deleteDirectory) {
        // ダウンロードデータファイル格納ディレクトリ
        StringBuilder dirname = new StringBuilder();
        dirname.append(System.getenv("ADFACTORY_HOME")).append(File.separator);
        dirname.append("temp").append(File.separator).append(deleteDirectory);

        // 指定ディレクトリのファイルリスト作成
        File delDirectory = new File(dirname.toString());
        File[] delFileList = delDirectory.listFiles();
        if (Objects.isNull(delFileList)) {
            return;
        }

        // ファイルリストのファイル削除
        for (File delfile : delFileList) {
            if (!delfile.delete()) {
                logger.error("PictureDataFile Delete Error");
            }
        }
    }

    /**
     * 工程実績の親組織を、親組織一覧に追加する。
     *
     * @param parentOrganizations 親組織一覧
     * @param actual 工程実績
     */
    private void setParentOrganizations(List<String> parentOrganizations, ActualResultEntity actual) {
        if (Objects.nonNull(actual.getOrganizationParentName())) {
            if (!parentOrganizations.contains(actual.getOrganizationParentName())) {
                parentOrganizations.add(actual.getOrganizationParentName());
            }
        }
    }

    /**
     * 工程実績の組織を、組織一覧に追加する。
     *
     * @param organizations 組織一覧
     * @param actual 工程実績
     */
    private void setOrganizations(List<String> organizations, ActualResultEntity actual) {
        if (Objects.nonNull(actual.getOrganizationName())) {
            if (!organizations.contains(actual.getOrganizationName())) {
                organizations.add(actual.getOrganizationName());
            }
        }
    }

    /**
     * 工程実績の親設備を、親設備一覧に追加する。
     *
     * @param parentEquipments 親設備一覧
     * @param actual 工程実績
     */
    private void setParentEquipments(List<String> parentEquipments, ActualResultEntity actual) {
        if (Objects.nonNull(actual.getEquipmentParentName())) {
            if (!parentEquipments.contains(actual.getEquipmentParentName())) {
                parentEquipments.add(actual.getEquipmentParentName());
            }
        }
    }

    /**
     * 工程実績の設備を、設備一覧に追加する。
     *
     * @param equipments 設備一覧
     * @param actual 工程実績
     */
    private void setEquipments(List<String> equipments, ActualResultEntity actual) {
        if (Objects.nonNull(actual.getEquipmentName())) {
            if (!equipments.contains(actual.getEquipmentName())) {
                equipments.add(actual.getEquipmentName());
            }
        }
    }

    /**
     * 割当組織名一覧を取得する。
     *
     * @param organizationIds 割当組織ID一覧
     * @return 割当組織名一覧
     */
    private List<String> getAssignOrganizations(List<Long> organizationIds) {
        List<String> organizationNames = new ArrayList<>();
        for (Long organizationId : organizationIds) {
            organizationNames.add(getOrganizationName(organizationId));
        }

        return organizationNames;
    }

    /**
     * 割当設備名一覧を取得する。
     *
     * @param equipmentIds 割当設備ID一覧
     * @return 割当設備名一覧
     */
    private List<String> getAssignEquipments(List<Long> equipmentIds) {
        List<String> equipmentNames = new ArrayList<>();
        for (Long equipmentId : equipmentIds) {
            equipmentNames.add(getEquipmentName(equipmentId));
        }

        return equipmentNames;
    }

    /**
     * 組織名を取得する。
     *
     * @param organizationId 組織ID
     * @return 組織名
     */
    private String getOrganizationName(Long organizationId) {
        if (Objects.isNull(organizationId)) {
            return "";
        }
        OrganizationInfoEntity entity = CacheUtils.getCacheOrganization(organizationId);
        if (Objects.nonNull(entity)) {
            return entity.getOrganizationName();
        } else {
            return "";
        }
    }

    /**
     * 設備名を取得する。
     *
     * @param equipmentId 設備ID
     * @return 設備名
     */
    private String getEquipmentName(Long equipmentId) {
        if (Objects.isNull(equipmentId)) {
            return "";
        }
        EquipmentInfoEntity entity = CacheUtils.getCacheEquipment(equipmentId);
        if (Objects.nonNull(entity)) {
            return entity.getEquipmentName();
        } else {
            return "";
        }
    }

    /**
     * 工程実績の中断理由を、中断理由一覧に追加する。
     *
     * @param interruptList 中断理由一覧
     * @param actual 工程実績
     */
    private void setIntterupt(List<String> interruptList, ActualResultEntity actual) {
        if (Objects.nonNull(actual.getInterruptReason())) {
            if (!interruptList.contains(actual.getInterruptReason())) {
                interruptList.add(actual.getInterruptReason());
            }
        }
    }

    /**
     * 工程実績の遅延理由を、遅延理由一覧に追加する。
     *
     * @param delayList 遅延理由一覧
     * @param actual 工程実績
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
     * @return
     */
    public List<String> createCheckWords() {
        List<String> checkWords = new ArrayList<>();
        checkWords.add("TAG_");

        return checkWords;
    }

    /**
     * 最後の１文字を削除する。
     *
     * @param sb StringBuilder
     */
    private void deleteLastTrailingCharacter(StringBuilder sb) {
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    /**
     * 工程実績情報からトレーサビリティ情報を取得して、タグマップに追加する。
     *
     * @param tagMap タグマップ
     * @param actuals 工程実績一覧
     */
    private void createReplaceDataTraceability(Map<String, Object> tagMap, List<ActualResultEntity> actuals) {
        for (ActualResultEntity actual : actuals) {
            for (ActualPropertyEntity actualProperty : actual.getPropertyCollection()) {
                if (!CustomPropertyTypeEnum.TYPE_TRACE.equals(actualProperty.getActualPropType())) {
                    continue;
                }

                Date date = DateUtils.parse(actualProperty.getActualPropValue());
                if (Objects.isNull(date)) {
                    tagMap.put(actualProperty.getActualPropName(), actualProperty.getActualPropValue());
                } else {
                    tagMap.put(actualProperty.getActualPropName(), date);
                }
            }
        }
    }

    /**
     * トレーサビリティDBのトレーサビリティ情報から置換データを生成する。
     *
     * @param tagMap タグマップ
     * @param traces トレーサビリティ一覧
     */
    private void createReplaceDataTraceabilityData(Map<String, Object> tagMap, List<TraceabilityEntity> traces) {
        if (Objects.isNull(traces) || traces.isEmpty()) {
            return;
        }

        for (TraceabilityEntity trace : traces) {
            String tag = trace.getTraceTag();// タグ
            String okTag = new StringBuilder(tag).append("_OK").toString();// チェック状態
            String equipmentTag = new StringBuilder(tag).append("_EQUIPMENT").toString();// 設備管理名

            // チェック状態
            if (Objects.nonNull(trace.getTraceConfirm()) && trace.getTraceConfirm()) {
                tagMap.put(okTag, "1");
            } else {
                tagMap.put(okTag, "0");
            }

            // 値
            if (Objects.nonNull(trace.getTraceValue())) {
                tagMap.put(tag, trace.getTraceValue());
            } else {
                tagMap.put(tag, "");
            }

            // 設備管理名
            if (Objects.nonNull(trace.getEquipmentName())) {
                tagMap.put(equipmentTag, trace.getEquipmentName());
            } else {
                tagMap.put(equipmentTag, "");
            }

            // 追加トレーサビリティ
            if (Objects.nonNull(trace.getTraceProps()) && !trace.getTraceProps().isEmpty()) {
                Map<String, String> traceMap = JsonUtils.jsonToMap(trace.getTraceProps());
                for (Map.Entry<String, String> entry : traceMap.entrySet()) {
                    tagMap.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    /**
     * ワークブックのタグを置換して、ファイルに保存する。
     *
     * @param templateFile テンプレートファイル
     * @param outputFile 出力ファイル
     * @param kanbanNos カンバンNo一覧
     * @param kanbanTagMaps カンバンNoとタグ一覧のマップ
     * @param kanbanIdMap カンバンNoとカンバンIDのマップ
     * @param isRemoveTag 未変換のタグを削除する？(true:する, false:しない)
     * @param ledgerTagCase タグ識別設定
     * @return 結果
     */
    public ReplaceTagResult replaceTags(File templateFile, File outputFile, List<Integer> kanbanNos, Map<Integer, Map<String, Object>> kanbanTagMaps, Map<Integer, Long> kanbanIdMap, boolean isRemoveTag, LedgerTagCase ledgerTagCase) {
        ReplaceTagResult replaceTagResult = new ReplaceTagResult();
        try {
            XSSFWorkbook workbook = ExcelFileUtils.loadExcelFile(templateFile);

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                XSSFSheet sheet = workbook.getSheetAt(i);

                for (Integer kanbanNo : kanbanNos) {
                    long kanbanId = kanbanIdMap.get(kanbanNo);
                    Map<String, Object> tagMap = ledgerTagCase.getMap();
                    tagMap.putAll(kanbanTagMaps.get(kanbanNo));

                    // 画像データ設定
                    tagMap.putAll(this.createReplacePictureData(kanbanId, sheet, kanbanNo, ledgerTagCase));
                    // タグを置換する。
                    ExcelReplacer.replaceSheetTags(sheet, tagMap, kanbanNo, null, null, null, ledgerTagCase.getTagConverter());
                    // ダウンロードファイル(画像データ)削除処理
                    this.downloadTempFileDelete("picturedata");
                }
            }

            // 置換できなかったタグの取得及びエラー表示を設定する。
            List<String> faildReplaceTags = ExcelReplacer.checkFailedCell(workbook, this.createCheckWords(), true, isRemoveTag, ledgerTagCase.getTagConverter());
            replaceTagResult.getFaildReplaceTags().addAll(faildReplaceTags);

            // ワークブックを保存する。
            boolean result = this.saveWorkbook(workbook, outputFile);
            replaceTagResult.setSuccess(result);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return replaceTagResult;
    }

    /**
     * ワークブックをファイルに保存する。
     *
     * @param workbook ワークブック
     * @param file ファイル
     * @return 結果(true:成功, false:失敗)
     * @throws Exception
     */
    public boolean saveWorkbook(XSSFWorkbook workbook, File file) throws Exception {
        boolean succeeded = false;
        try {
            if (Objects.nonNull(workbook)) {
                boolean evaluate = false;

                // ワークブックの再計算を実行する。
                try {
                    logger.info("Evaluate of excel formula.");
                    workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
                    evaluate = true;
                } catch (Exception ex) {
                    logger.warn("Failed to evaluate of excel formula.");
                }

                ExcelFileUtils.saveExcelFile(workbook, file);

                // ワークブックの再計算ができなかった場合、Excelでファイルを開いて再計算させて保存しなおす。
                if (!evaluate) {
                    this.recalcWorkbook(file);
                }

                succeeded = true;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return succeeded;
    }

    /**
     * 帳票ファイルの再計算を試みる</br>
     * 要 Microsoft Excel
     *
     * @param workbook
     */
    private void recalcWorkbook(File workbook) throws Exception {
        Writer fw = null;

        Process process = null;
        try {
            logger.info("recalcWorkbook start.");

            // tempフォルダパス
            String tempPath = Paths.get(System.getenv("ADFACTORY_HOME"), "temp").toString();
            File temp = new File(tempPath);
            if (!temp.exists()) {
                logger.info("Create the temporary directory.");
                temp.mkdir();
            }

            // スクリプトファイルパス
            String filePath = Paths.get(tempPath, "recalc_" + workbook.getName() + ".vbs").toString();
            File vbs = new File(filePath);
            vbs.createNewFile();

            // 非表示で開き → 強制再計算を実行し → 保存し、閉じ → 自身を削除する
            fw = new OutputStreamWriter(new FileOutputStream(vbs), "MS932");

            fw.write("On Error Resume Next" + System.lineSeparator());// エラー発生時、処理を続行する。

            fw.write("Set oXlsApp = CreateObject(\"Excel.Application\")" + System.lineSeparator());
            fw.write("If Not oXlsApp Is Nothing Then" + System.lineSeparator());
            fw.write("oXlsApp.Application.Visible = false" + System.lineSeparator());

            fw.write("Set excel = oXlsApp.Application.Workbooks.Open(\""
                    + workbook.getPath() + "\")" + System.lineSeparator());// EXCELファイルを開く。
            fw.write("If Err.Number = 0 Then" + System.lineSeparator());// EXCELファイルが開けた場合のみ再計算を実行する。

            fw.write("oXlsApp.CalculateFull" + System.lineSeparator());// 再計算する。
            fw.write("If Err.Number <> 0 Then" + System.lineSeparator());// エラーが発生した場合はエラーメッセージを表示する。
            fw.write("WScript.Echo Err.Description" + System.lineSeparator());
            fw.write("Err.Clear" + System.lineSeparator());
            fw.write("End If" + System.lineSeparator());

            fw.write("excel.save" + System.lineSeparator());// 保存する。
            fw.write("If Err.Number <> 0 Then" + System.lineSeparator());// エラーが発生した場合はエラーメッセージを表示する。
            fw.write("WScript.Echo Err.Description" + System.lineSeparator());
            fw.write("Err.Clear" + System.lineSeparator());
            fw.write("End If" + System.lineSeparator());

            fw.write("oXlsApp.Quit" + System.lineSeparator());// EXCELを閉じる。

            fw.write("Else" + System.lineSeparator());// EXCELファイルが開けなかった場合はエラーメッセージを表示する。

            fw.write("WScript.Echo Err.Description" + System.lineSeparator());
            fw.write("Err.Clear" + System.lineSeparator());
            fw.write("End If" + System.lineSeparator());

            fw.write("Set oXlsApp = Nothing" + System.lineSeparator());
            fw.write("End If" + System.lineSeparator());

            fw.write("Set oFSO = CreateObject(\"Scripting.FileSystemObject\")" + System.lineSeparator());
            fw.write("oFSO.DeleteFile(Wscript.ScriptFullName)" + System.lineSeparator());// 自身(vbsファイル)を削除する。

            fw.write("On Error Goto 0" + System.lineSeparator());// エラー発生時、処理を終了する。

            fw.flush();
            fw.close();
            fw = null;

            ProcessBuilder pb = new ProcessBuilder("cscript", filePath);

            process = pb.start();
            process.waitFor();

        } catch (Exception ex) {
            throw ex;
        } finally {
            try {
                if (Objects.nonNull(process)) {
                    process.destroy();
                }
            } catch (Exception ex) {
            }
            try {
                if (Objects.nonNull(fw)) {
                    fw.close();
                }
            } catch (IOException ex) {
            }

            logger.info("recalcWorkbook end.");
        }
    }

    /**
     * 使用部品のタグマップを作成する。
     *
     * @param partsInfos 使用部品情報一覧
     * @param ledgerTagCase タグ識別設定
     * @return タグマップ
     */
    public Map<String, Object> createReplaceDataAssemblyParts(List<AssemblyPartsInfoEntity> partsInfos, LedgerTagCase ledgerTagCase) {
        Map<String, Object> tagMap = ledgerTagCase.getMap(); // キーは大文字・小文字の違いをを無視
        if (Objects.isNull(partsInfos) || partsInfos.isEmpty()) {
            return tagMap;
        }

        Comparator<AssemblyPartsInfoEntity> comparator = Comparator.comparing(AssemblyPartsInfoEntity::getAno)
                .thenComparing(AssemblyPartsInfoEntity::getPnoNum);
        Collections.sort(partsInfos, comparator);

        for (Integer index = 1; partsInfos.size() >= index; index++) {
            AssemblyPartsInfoEntity partsInfo = partsInfos.get(index - 1);

            // PID
            tagMap.put(String.format("TAG_PARTS_PID_%d", index), partsInfo.getPartsId());
            // 品名
            tagMap.put(String.format("TAG_PARTS_NAME_%d", index), partsInfo.getProductName());
            // 品目
            tagMap.put(String.format("TAG_PARTS_NO_%d", index), partsInfo.getProductNumber());
            // シリアル番号
            tagMap.put(String.format("TAG_PARTS_SERIAL_%d", index), partsInfo.getSerialNumber());
            // 数量
            tagMap.put(String.format("TAG_PARTS_NUM_%d", index), partsInfo.getQauntity());
            // 使用フラグ
            tagMap.put(String.format("TAG_PARTS_ASSEMBLED_%d", index), partsInfo.getFixedFlag());
            // 使用確定日時
            tagMap.put(String.format("TAG_PARTS_ASSEMBLED_DATE_%d", index), partsInfo.getFixedDate());
            // 親品目フラグ
            tagMap.put(String.format("TAG_PARTS_PARENT_%d", index), partsInfo.getParentFlag());
        }

        return tagMap;
    }

    /**
     * QRコードのタグマップを作成する。
     *
     * @param kanban カンバン
     * @param ledgerTagCase タグ識別設定
     * @return タグマップ
     */
    public Map<String, Object> createReplaceDataQRCode(KanbanInfoEntity kanban, LedgerTagCase ledgerTagCase) {
        Map<String, Object> tagMap = ledgerTagCase.getMap(); // キーは大文字・小文字の違いをを無視
        if (Objects.isNull(kanban)) {
            return tagMap;
        }

        //カンバン情報.工程順IDと一致する工程順情報を取得
        WorkflowInfoEntity workflowInfo = CacheUtils.getCacheWorkflow(kanban.getFkWorkflowId());
        if (Objects.isNull(workflowInfo)) {
            return tagMap;
        }

        // カンバン用QRコード
        BufferedImage qrCodeImage = this.createQRCode(
                new StringBuilder("KS")
                .append(",")
                .append(kanban.getKanbanName())
                .append(",")
                .append(workflowInfo.getWorkflowName())
                .append(":")
                .append(workflowInfo.getWorkflowRev())
                .toString()
        );
        if (Objects.nonNull(qrCodeImage)) {
            tagMap.put("TAG_QRCODE_KANBAN", qrCodeImage);
        }

        // シリアル番号用 QRコード
        if (Objects.nonNull(kanban.getServiceInfo())) {
            // サービス情報
            List<KanbanProduct> products = KanbanProduct.lookupProductList(kanban.getServiceInfo());
            for (KanbanProduct product : products) {
                BufferedImage serialQrCodeImage = this.createQRCode(
                        new StringBuilder("SS")
                                .append(",")
                                .append(kanban.getKanbanName())
                                .append(",")
                                .append(workflowInfo.getWorkflowName())
                                .append(":")
                                .append(workflowInfo.getWorkflowRev())
                                .append(",")
                                .append(product.getUid())
                                .toString()
                );
                if (Objects.isNull(serialQrCodeImage)) {
                    break;
                }
                tagMap.put(String.format("TAG_QRCODE_SERIAL_%s", String.valueOf(product.getOrderNumber())), serialQrCodeImage);
            }
        }

        // 工程用 QRコード
        if (Objects.nonNull(workflowInfo.getConWorkflowWorkInfoCollection())) {
            List<ConWorkflowWorkInfoEntity> conWorkflowWorkInfoCollection = workflowInfo.getConWorkflowWorkInfoCollection();
            // 工程をソート
            conWorkflowWorkInfoCollection.sort(Comparator.comparing(work -> work.getWorkflowOrder()));
            for (int i=0; i < conWorkflowWorkInfoCollection.size(); i++){
                ConWorkflowWorkInfoEntity conWorkInfo = conWorkflowWorkInfoCollection.get(i);
                BufferedImage workQrCodeImage = this.createQRCode(
                        new StringBuilder("WS")
                                .append(",")
                                .append(kanban.getKanbanName())
                                .append(",")
                                .append(workflowInfo.getWorkflowName())
                                .append(":")
                                .append(workflowInfo.getWorkflowRev())
                                .append(",")
                                .append(conWorkInfo.getWorkName())
                                .toString()
                );
                if (Objects.isNull(workQrCodeImage)) {
                   break;
                }
                tagMap.put(String.format("TAG_QRCODE_WORK_%s", String.valueOf(i+1)), workQrCodeImage);
            }
        }

        return tagMap;
    }

    /**
     * QRコードのBufferdImageを作成する。
     *
     * @param contents QRコードの内容
     * @return BufferdImage
     */
    private BufferedImage createQRCode(String contents) {
        try{
            BufferedImage qrCodeImage = Barcode.createQRCodeImage(contents, ErrorCorrectionLevel.Q, "MS932", 150);
            return qrCodeImage;
        } catch (Exception ex){
            logger.fatal(ex);
            return null;
        }
    }
}
