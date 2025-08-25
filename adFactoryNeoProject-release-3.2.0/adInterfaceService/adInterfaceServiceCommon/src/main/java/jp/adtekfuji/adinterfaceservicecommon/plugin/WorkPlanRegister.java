/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservicecommon.plugin;

import adtekfuji.clientservice.EquipmentInfoFacade;
import adtekfuji.clientservice.KanbanHierarchyInfoFacade;
import adtekfuji.clientservice.KanbanInfoFacade;
import adtekfuji.clientservice.OrganizationInfoFacade;
import adtekfuji.clientservice.WorkKanbanInfoFacade;
import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.csv.ImportKanbanCsv;
import jp.adtekfuji.adFactory.entity.csv.ImportKanbanPropertyCsv;
import jp.adtekfuji.adFactory.entity.csv.ImportKanbanStatusCsv;
import jp.adtekfuji.adFactory.entity.csv.ImportProductCsv;
import jp.adtekfuji.adFactory.entity.csv.ImportWorkKanbanCsv;
import jp.adtekfuji.adFactory.entity.csv.ImportWorkKanbanPropertyCsv;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.holiday.HolidayInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.ProductInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adinterfaceservicecommon.plugin.common.WorkPlanResultLogger;
import jp.adtekfuji.adinterfaceservicecommon.plugin.entity.WorkPlanInfo;
import jp.adtekfuji.adinterfaceservicecommon.plugin.util.DbConnector;
import jp.adtekfuji.adinterfaceservicecommon.plugin.util.ImportFormatFileUtil;
import jp.adtekfuji.adinterfaceservicecommon.plugin.util.WorkKanbanTimeReplaceUtils;
import jp.adtekfuji.adinterfaceservicecommon.plugin.util.WorkPlanWorkflowProcess;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 読み込んだ生産計画をサーバーに登録する
 *
 * @author fu-kato
 */
public class WorkPlanRegister implements WorkPlanResultLogger {

    private final Logger logger = LogManager.getLogger();

    private final KanbanInfoFacade kanbanInfoFacade = new KanbanInfoFacade();
    private final KanbanHierarchyInfoFacade kanbanHierarchyInfoFacade = new KanbanHierarchyInfoFacade();
    private final WorkflowInfoFacade workflowInfoFacade = new WorkflowInfoFacade();
    private final WorkKanbanInfoFacade workKanbanInfoFacade = new WorkKanbanInfoFacade();
    private final OrganizationInfoFacade organizationInfoFacade = new OrganizationInfoFacade();
    private final EquipmentInfoFacade equipmentInfoFacade = new EquipmentInfoFacade();
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();

    private final String ORDER = "内作外注";
    private final String SUBCON = "外注先";
    private final String PREFIX_TMP = "tmp_";
    private static final String CHARSET = "UTF-8";
    private static final String DELIMITER = "\\|";
    private static final Long RANGE = 20l;
    private static final int MAX_CHAR = 256;

    private final WorkPlanResultLogger importLogger;

    /**
     *
     * @param importLogger
     */
    public WorkPlanRegister(WorkPlanResultLogger importLogger) {
        this.importLogger = importLogger;
    }

    /**
     * インポートしたカンバンをサーバーに登録する
     *
     * @param ignoreSameKanban trueの場合すでに存在するカンバンに対して更新を行わない
     * @param workPlanInfo
     * @return 結果の文字列：個数　の組み合わせ
     * @throws Exception
     */
    public Map<String, Integer> update(boolean ignoreSameKanban, WorkPlanInfo workPlanInfo) throws Exception {
        return importKanban(ignoreSameKanban,
                workPlanInfo.getImportKanbans(),
                workPlanInfo.getImportKanbanProps(),
                workPlanInfo.getImportWorkKanbans(),
                workPlanInfo.getImportWkKanbanProps(),
                workPlanInfo.getImportKanbanStatuss(),
                workPlanInfo.getImportProduct());
    }

    /**
     * 工程カンバンプロパティを更新する。
     *
     * @param ignoreSameKanban
     * @param importWkKanbanProps
     * @return
     * @throws Exception
     */
    public Map<String, Integer> updateWorkKanbanProperty(boolean ignoreSameKanban, List<ImportWorkKanbanPropertyCsv> importWkKanbanProps) throws Exception {

        if (importWkKanbanProps.isEmpty()) {
            return null;
        }

        // 擬似的なカンバン情報のリストを作成
        final Set<String> importKanbanNames = importWkKanbanProps.stream()
                .map(workKanbanProp -> workKanbanProp.getKanbanName())
                .collect(Collectors.toSet());

        final boolean dbConnect = Boolean.parseBoolean(AdProperty.getProperties().getProperty("dbConnect", "false"));
        final DbConnector con = DbConnector.getInstance();

        final Map<String, OrganizationInfoEntity> organizationMap = new HashMap();
        final Map<String, EquipmentInfoEntity> equipmentMap = new HashMap();

        int procNum = 0;
        int skipKanbanNum = 0;
        int successNum = 0;
        int failedNum = 0;

        for (String kanbanName : importKanbanNames) {
            logger.debug("Import the kanban: " + kanbanName);

            procNum++;

            if (StringUtils.isEmpty(kanbanName)) {
                skipKanbanNum++;
                continue;
            }

            boolean exsitKanban = false;
            List<KanbanInfoEntity> kanbans = null;

            if (dbConnect) {
                exsitKanban = con.exsitKanban(kanbanName);
            } else {
                KanbanSearchCondition condition = new KanbanSearchCondition().kanbanName(kanbanName);
                kanbans = kanbanInfoFacade.findSearch(condition);
                exsitKanban = !kanbans.isEmpty();
            }

            if (!exsitKanban && ignoreSameKanban) {
                skipKanbanNum++;
                continue;
            }

            KanbanInfoEntity kanban = kanbanInfoFacade.findName(kanbanName);

            List<WorkKanbanInfoEntity> workKanbans = new ArrayList<>();
            Long workkanbanCnt = workKanbanInfoFacade.countFlow(kanban.getKanbanId());
            for (long nowCnt = 0; nowCnt < workkanbanCnt; nowCnt += RANGE) {
                workKanbans.addAll(workKanbanInfoFacade.getRangeFlow(nowCnt, nowCnt + RANGE - 1, kanban.getKanbanId()));
            }
            kanban.setWorkKanbanCollection(workKanbans);

            List<WorkKanbanInfoEntity> sepWorkKanbans = new ArrayList<>();
            Long separateCnt = workKanbanInfoFacade.countSeparate(kanban.getKanbanId());
            for (long nowCnt = 0; nowCnt < separateCnt; nowCnt += RANGE) {
                sepWorkKanbans.addAll(workKanbanInfoFacade.getRangeSeparate(nowCnt, nowCnt + RANGE - 1, kanban.getKanbanId()));
            }
            kanban.setSeparateworkKanbanCollection(sepWorkKanbans);

            // 登録済のカンバンの場合は、カンバンステータスを一旦「計画中(Planning)」に戻す。
            KanbanStatusEnum kanbanStatus;
            if (Objects.nonNull(kanban.getKanbanId())) {
                kanbanStatus = kanban.getKanbanStatus();

                // カンバンステータスが「中止(Suspend)」，「その他(Other)」，「完了(Completion)」の場合は更新しない
                //      ※．KanbanStatusEnumは、「中止(Suspend)」が INTERRUPT で、「一時中断(Interrupt)」が SUSPEND なので注意。
                if (kanbanStatus.equals(KanbanStatusEnum.INTERRUPT)
                        || kanbanStatus.equals(KanbanStatusEnum.OTHER)
                        || kanbanStatus.equals(KanbanStatusEnum.COMPLETION)) {
                    // 更新できないカンバンのためスキップ
                    skipKanbanNum++;
                    //addResult(String.format("  > %s: %s", LocaleUtils.getString("key.ImportKanban_NotUpdateKanban"), LocaleUtils.getString(kanbanStatus.getResourceKey())));
                    continue;
                }

                if (!kanbanStatus.equals(KanbanStatusEnum.PLANNING)) {
                    kanban.setKanbanStatus(KanbanStatusEnum.PLANNING);
                    ResponseEntity updateStatusRes = kanbanInfoFacade.update(kanban);
                    if (Objects.isNull(updateStatusRes) || !updateStatusRes.isSuccess()) {
                        // 更新できないカンバンのためスキップ (ステータス変更できない状態だった)
                        skipKanbanNum++;
                        //addResult(String.format("  > %s: %s", LocaleUtils.getString("key.ImportKanban_NotUpdateKanban"), LocaleUtils.getString(kanbanStatus.getResourceKey())));
                        continue;
                    }
                }
            } else {
                kanbanStatus = KanbanStatusEnum.PLANNED;
            }

            kanban.setKanbanName(kanbanName);

            this.udpateWorkKanbanProp(kanban, kanbanName, importWkKanbanProps);

            // 内作外注の切り替えを行う
            final List<ImportWorkKanbanCsv> importWorkKanbans = this.createImportWorkKanbanCsv(kanbanName, workKanbans);
            this.updateWorkKanban(kanban, kanbanName, importWorkKanbans, organizationMap, equipmentMap);

            // 更新日時
            Date updateDateTime = DateUtils.toDate(LocalDateTime.now());
            kanban.setUpdateDatetime(updateDateTime);

            // 更新者
            kanban.setFkUpdatePersonId(loginUserInfoEntity.getId());

            // カンバン更新
            logger.debug("import kanban:{}", kanban);
            ResponseEntity updateRes = kanbanInfoFacade.update(kanban);
            if (Objects.nonNull(updateRes) && updateRes.isSuccess()) {
                // カンバンステータスを元の状態に戻す
                kanban.setKanbanStatus(kanbanStatus);
                kanbanInfoFacade.update(kanban);
                successNum++;
            } else {
                failedNum++;
                addResult(String.format("  > %s", LocaleUtils.getString("key.ImportKanban_UpdateFailed")));
            }
        }

        if (dbConnect) {
            con.closeDB();
        }

        addResult(String.format("%s: %s (%s: %s, %s: %s, %s: %s)",
                LocaleUtils.getString("key.ImportKanban_ProccessNum"), procNum,
                LocaleUtils.getString("key.ImportKanban_SuccessNum"), successNum,
                LocaleUtils.getString("key.ImportKanban_SkipNum"), skipKanbanNum,
                LocaleUtils.getString("key.ImportKanban_FailedNum"), failedNum));

        Map<String, Integer> ret = new HashMap<>();
        ret.put("procNum", procNum);
        ret.put("successNum", successNum);
        ret.put("skipKanbanNum", skipKanbanNum);
        ret.put("failedNum", failedNum);

        return ret;
    }

    /**
     *
     * @param ignoreSameKanban
     * @param importKanbans
     * @param importKanbanProps
     * @param importWorkKanbans
     * @param importWkKanbanProps
     * @param importKanbanStatuss
     * @param importProduct
     * @return
     * @throws UnsupportedEncodingException
     * @throws Exception
     */
    private Map<String, Integer> importKanban(boolean ignoreSameKanban,
            List<ImportKanbanCsv> importKanbans, List<ImportKanbanPropertyCsv> importKanbanProps,
            List<ImportWorkKanbanCsv> importWorkKanbans,
            List<ImportWorkKanbanPropertyCsv> importWkKanbanProps,
            List<ImportKanbanStatusCsv> importKanbanStatuss,
            List<ImportProductCsv> importProduct) throws UnsupportedEncodingException, Exception {

        int procNum = 0;
        int skipKanbanNum = 0;
        int successNum = 0;
        int failedNum = 0;

        Map<String, KanbanHierarchyInfoEntity> kanbanHierarchyMap = new HashMap();
        Map<String, WorkflowInfoEntity> workflowMap = new HashMap();
        Map<String, OrganizationInfoEntity> organizationMap = new HashMap();
        Map<String, EquipmentInfoEntity> equipmentMap = new HashMap();

        Map<String, KanbanStatusEnum> statusMap = new HashMap<>();
        if (importKanbanStatuss.size() > 0) {
            statusMap = new HashMap();
            for (ImportKanbanStatusCsv data : importKanbanStatuss) {
                statusMap.put(data.getKanbanName(), KanbanStatusEnum.getEnum(data.getKanbanStatus()));
            }
        }

        boolean dbConnect = Boolean.parseBoolean(AdProperty.getProperties().getProperty("dbConnect", "false"));
        DbConnector con = DbConnector.getInstance();

        String targetPropName = LocaleUtils.getString("key.InstructionNumber");// 指示数

        //addResult("--------------------------------------------");
        boolean errFlg = false;
        for (ImportKanbanCsv importKanban : importKanbans) {
            logger.debug("Import the kanban: " + importKanban);

            procNum++;
            errFlg = false;
            //addResult(LocaleUtils.getString("key.import.production.kanba") + ":" + (procNum + line - 1) + "行目");

            String kanbanName = importKanban.getKanbanName();
            if (StringUtils.isEmpty(kanbanName)) {
                skipKanbanNum++;
                //addResult(String.format("  > %s: %s", LocaleUtils.getString("key.KanbanName"), LocaleUtils.getString("key.impprt.data.not")));
                continue;
            }

            // 読み込みカンバン
            //addResult(String.format(" %s: %s", LocaleUtils.getString("key.ImportKanban_TargetKanbanName"), kanbanName));
            // サイズチェック
            //if (Objects.nonNull(importKanban.getKanbanHierarchyName())) {
            //    // 文字サイズ
            //    if (importKanban.getKanbanHierarchyName().length() > MAX_CHAR) {
            //        addResult(String.format("  > %s:%s", LocaleUtils.getString("key.KanbanHierarch"), LocaleUtils.getString("key.warn.enterCharacters256")));
            //        errFlg = true;
            //    }
            //}
            //if (Objects.nonNull(importKanban.getKanbanName())) {
            //    // 文字サイズ
            //    if (importKanban.getKanbanName().length() > MAX_CHAR) {
            //        addResult(String.format("  > %s:%s", LocaleUtils.getString("key.Legend"), LocaleUtils.getString("key.warn.enterCharacters256")));
            //        errFlg = true;
            //    }
            //}
            //if (Objects.nonNull(importKanban.getModelName())) {
            //    // 文字サイズ
            //    if (importKanban.getModelName().length() > MAX_CHAR) {
            //        addResult(String.format("  > %s:%s", LocaleUtils.getString("key.ModelName"), LocaleUtils.getString("key.warn.enterCharacters256")));
            //        errFlg = true;
            //    }
            //}
            //if (Objects.nonNull(importKanban.getWorkflowName())) {
            //    // 文字サイズ
            //    if (importKanban.getWorkflowName().length() > MAX_CHAR) {
            //        addResult(String.format("  > %s:%s", LocaleUtils.getString("key.OrderProcessesName"), LocaleUtils.getString("key.warn.enterCharacters256")));
            //        errFlg = true;
            //    }
            //}
            // カンバン階層
            KanbanHierarchyInfoEntity kanbanHierarchy;
            if (kanbanHierarchyMap.containsKey(importKanban.getKanbanHierarchyName())) {
                kanbanHierarchy = kanbanHierarchyMap.get(importKanban.getKanbanHierarchyName());
            } else {
                logger.debug(" カンバン階層名の検索:" + importKanban.getKanbanHierarchyName());
                kanbanHierarchy = kanbanHierarchyInfoFacade.findHierarchyName(URLEncoder.encode(importKanban.getKanbanHierarchyName(), CHARSET));
                kanbanHierarchyMap.put(importKanban.getKanbanHierarchyName(), kanbanHierarchy);
            }
            Long kanbanHierarchyId = kanbanHierarchy.getKanbanHierarchyId();
            if (Objects.isNull(kanbanHierarchyId)) {
                errFlg = true;
                // 存在しないカンバン階層のためスキップ
                addResult(String.format("  > %s: %s", LocaleUtils.getString("key.ImportKanban_HierarchyNothing"), importKanban.getKanbanHierarchyName()));
            }

            // 工程順の確認
            WorkflowInfoEntity workflow = null;
            Long workflowId = null;
            if (!StringUtils.isEmpty(importKanban.getWorkflowName())) {
                final String workflowNameAndRev = importKanban.createWorkflowName();

                if (workflowMap.containsKey(workflowNameAndRev)) {
                    workflow = workflowMap.get(workflowNameAndRev);
                } else {
                    final String workflowName = importKanban.getEnableConcat()
                            ? importKanban.getModelName() + importKanban.getWorkflowName()
                            : importKanban.getWorkflowName();

                    workflow = findWorkflow(workflowName, importKanban.getWorkflowRev());

                    // モデル名と工程順名を結合させる場合は見つからないときに工程順名だけで再検索を行う
                    if (importKanban.getEnableConcat() && (Objects.isNull(workflow) || Objects.isNull(workflow.getWorkflowId()))) {
                        workflow = findWorkflow(importKanban.getWorkflowName(), importKanban.getWorkflowRev());
                    }
                    workflowMap.put(workflowNameAndRev, workflow);
                }
                workflowId = workflow.getWorkflowId();
                if (Objects.isNull(workflowId)) {
                    errFlg = true;
                    // 存在しない工程順のためスキップ
                    addResult(String.format("  > %s: %s", LocaleUtils.getString("key.ImportKanban_WorkflowNothing"), workflowNameAndRev));
                }
            } else {
                errFlg = true;
                // 工程順の指定なし
                addResult(String.format("  > %s", LocaleUtils.getString("key.ImportKanban_WorkflowNothing")));
            }

            // 開始予定日時
            Date startDatetime = null;
            if (!StringUtils.isEmpty(importKanban.getStartDatetime())) {
                startDatetime = ImportFormatFileUtil.stringToDateTime(importKanban.getStartDatetime());
                if (Objects.isNull(startDatetime)) {
                    errFlg = true;
                    addResult(String.format("  > %s: %s", LocaleUtils.getString("key.error.format.datetime"), LocaleUtils.getString("key.import.read.plans.start.datetime")));
                }
            }

            if (errFlg) {
                skipKanbanNum++;
                continue;
            }

            boolean exsitKanban = false;
            List<KanbanInfoEntity> kanbans = null;
            KanbanInfoEntity kanban;

            if (dbConnect) {
                exsitKanban = con.exsitKanban(kanbanName);
            } else {
                KanbanSearchCondition condition = new KanbanSearchCondition()
                        .kanbanName(kanbanName)
                        .workflowId(workflowId);
                kanbans = kanbanInfoFacade.findSearch(condition);
                exsitKanban = !kanbans.isEmpty();
            }

            if (!exsitKanban) {
                kanban = new KanbanInfoEntity();
                if (Objects.isNull(startDatetime)) {
                    startDatetime = new Date();
                }
            } else if (dbConnect) {
                if (ignoreSameKanban) {
                    skipKanbanNum++;
                    continue;
                }

                KanbanSearchCondition condition = new KanbanSearchCondition()
                        .kanbanName(kanbanName)
                        .workflowId(workflowId);
                kanbans = kanbanInfoFacade.findSearch(condition);
            }

            Optional<KanbanInfoEntity> findKanban = kanbans.stream().filter(k -> kanbanName.equals(k.getKanbanName())).findFirst();
            if (findKanban.isPresent()) {

                if (ignoreSameKanban) {
                    skipKanbanNum++;
                    //addResult(String.format("  > %s", LocaleUtils.getString("key.ImportKanban_ExistingKanban")));
                    continue;
                }

                kanban = findKanban.get();

                List<WorkKanbanInfoEntity> workKanbans = new ArrayList<>();
                Long workkanbanCnt = workKanbanInfoFacade.countFlow(kanban.getKanbanId());
                for (long nowCnt = 0; nowCnt < workkanbanCnt; nowCnt += RANGE) {
                    workKanbans.addAll(workKanbanInfoFacade.getRangeFlow(nowCnt, nowCnt + RANGE - 1, kanban.getKanbanId()));
                }
                kanban.setWorkKanbanCollection(workKanbans);

                List<WorkKanbanInfoEntity> sepWorkKanbans = new ArrayList<>();
                Long separateCnt = workKanbanInfoFacade.countSeparate(kanban.getKanbanId());
                for (long nowCnt = 0; nowCnt < separateCnt; nowCnt += RANGE) {
                    sepWorkKanbans.addAll(workKanbanInfoFacade.getRangeSeparate(nowCnt, nowCnt + RANGE - 1, kanban.getKanbanId()));
                }
                kanban.setSeparateworkKanbanCollection(sepWorkKanbans);
            } else {
                kanban = new KanbanInfoEntity();
            }

            // 登録済のカンバンの場合は、カンバンステータスを一旦「計画中(Planning)」に戻す。
            KanbanStatusEnum kanbanStatus;
            if (Objects.nonNull(kanban.getKanbanId())) {
                kanbanStatus = kanban.getKanbanStatus();

                // カンバンステータスが「中止(Suspend)」，「その他(Other)」，「完了(Completion)」の場合は更新しない
                //      ※．KanbanStatusEnumは、「中止(Suspend)」が INTERRUPT で、「一時中断(Interrupt)」が SUSPEND なので注意。
                if (kanbanStatus.equals(KanbanStatusEnum.INTERRUPT)
                        || kanbanStatus.equals(KanbanStatusEnum.OTHER)
                        || kanbanStatus.equals(KanbanStatusEnum.COMPLETION)) {
                    // 更新できないカンバンのためスキップ
                    skipKanbanNum++;
                    //addResult(String.format("  > %s: %s", LocaleUtils.getString("key.ImportKanban_NotUpdateKanban"), LocaleUtils.getString(kanbanStatus.getResourceKey())));
                    continue;
                }

                if (!kanbanStatus.equals(KanbanStatusEnum.PLANNING)) {
                    kanban.setKanbanStatus(KanbanStatusEnum.PLANNING);
                    ResponseEntity updateStatusRes = kanbanInfoFacade.update(kanban);
                    if (Objects.isNull(updateStatusRes) || !updateStatusRes.isSuccess()) {
                        // 更新できないカンバンのためスキップ (ステータス変更できない状態だった)
                        skipKanbanNum++;
                        //addResult(String.format("  > %s: %s", LocaleUtils.getString("key.ImportKanban_NotUpdateKanban"), LocaleUtils.getString(kanbanStatus.getResourceKey())));
                        continue;
                    }
                }
            } else {
                kanbanStatus = KanbanStatusEnum.PLANNED;
            }

            // 開始予定日時
            if (Objects.nonNull(startDatetime)) {
                kanban.setStartDatetime(startDatetime);
            }

            kanban.setKanbanName(kanbanName);
            kanban.setParentId(kanbanHierarchyId);
            kanban.setFkWorkflowId(workflowId);

            // 新規作成分は一旦登録
            if (Objects.isNull(kanban.getKanbanId())) {
                // モデル名
                kanban.setModelName(workflow.getModelName());

                // カンバン追加
                //addResult(String.format("  > %s", LocaleUtils.getString("key.ImportKanban_CreateKanban")));
                ResponseEntity createRes = kanbanInfoFacade.regist(kanban);
                if (Objects.nonNull(createRes) && createRes.isSuccess()) {
                    // 追加成功
                    //addResult(String.format("  > %s", LocaleUtils.getString("key.ImportKanban_RegistSuccess")));
                } else {
                    // 追加失敗
                    failedNum++;
                    addResult(String.format("  > %s", LocaleUtils.getString("key.ImportKanban_RegistFailed")));
                    continue;
                }
                kanban = kanbanInfoFacade.findURI(createRes.getUri());
            } else if (Objects.nonNull(kanban.getStartDatetime())) {
                List<BreakTimeInfoEntity> breakTimes = WorkKanbanTimeReplaceUtils.getWorkKanbanBreakTimes(kanban.getWorkKanbanCollection());
                List<HolidayInfoEntity> holidays = WorkKanbanTimeReplaceUtils.getHolidays(kanban.getStartDatetime());
                WorkPlanWorkflowProcess workflowProcess = new WorkPlanWorkflowProcess(workflow);
                workflowProcess.setBaseTime(kanban, breakTimes, kanban.getStartDatetime(), holidays);
            }

            // モデル名
            if (!StringUtils.isEmpty(importKanban.getModelName())) {
                kanban.setModelName(importKanban.getModelName());
            }

            // 工程カンバンをオーダー順にソートする。
            kanban.getWorkKanbanCollection().sort(Comparator.comparing(p -> p.getWorkKanbanOrder()));

            // カンバンプロパティ
            List<ImportKanbanPropertyCsv> props = importKanbanProps.stream().filter(p -> kanbanName.equals(p.getKanbanName())).collect(Collectors.toList());
            for (ImportKanbanPropertyCsv prop : props) {
                String propName = prop.getKanbanPropertyName();
                if (StringUtils.isEmpty(propName)) {
                    continue;
                }
                //if (Objects.nonNull(prop.getKanbanName())) {
                //    // 文字サイズ
                //    if (prop.getKanbanName().length() > MAX_CHAR) {
                //        addResult(String.format(" > %s:%s:%s", LocaleUtils.getString("key.import.production.kanba.property"), LocaleUtils.getString("key.Legend"), LocaleUtils.getString("key.warn.enterCharacters256")));
                //    }
                //}
                //if (Objects.nonNull(prop.getKanbanPropertyName())) {
                //    // 文字サイズ
                //    if (prop.getKanbanPropertyName().length() > MAX_CHAR) {
                //        addResult(String.format(" > %s:%s:%s", LocaleUtils.getString("key.import.production.kanba.property"), LocaleUtils.getString("key.property.name"), LocaleUtils.getString("key.warn.enterCharacters256")));
                //    }
                //}

                // プロパティ型
                CustomPropertyTypeEnum propType = null;
                if (!StringUtils.isEmpty(prop.getKanbanPropertyType())) {
                    String propTypeString = prop.getKanbanPropertyType();
                    if (CustomPropertyTypeEnum.TYPE_STRING.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_STRING;
                    } else if (CustomPropertyTypeEnum.TYPE_BOOLEAN.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_BOOLEAN;
                    } else if (CustomPropertyTypeEnum.TYPE_INTEGER.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_INTEGER;
                    } else if (CustomPropertyTypeEnum.TYPE_NUMERIC.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_NUMERIC;
                    } else if (CustomPropertyTypeEnum.TYPE_IP4_ADDRESS.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_IP4_ADDRESS;
                    } else if (CustomPropertyTypeEnum.TYPE_MAC_ADDRESS.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_MAC_ADDRESS;
                    } else if (CustomPropertyTypeEnum.TYPE_PLUGIN.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_PLUGIN;
                    } else if (CustomPropertyTypeEnum.TYPE_TRACE.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_TRACE;
                    } else {
                        addResult(String.format("  > %s:%s:%s", LocaleUtils.getString("key.import.production.kanba.property"), LocaleUtils.getString("key.type"), propTypeString));
                    }
                }

                // 更新対象のカンバンプロパティ
                Optional<KanbanPropertyInfoEntity> findKanbanProp = kanban.getPropertyCollection().stream().filter(p -> propName.equals(p.getKanbanPropertyName())).findFirst();
                if (findKanbanProp.isPresent()) {
                    KanbanPropertyInfoEntity kanbanProp = findKanbanProp.get();

                    // プロパティ型
                    if (Objects.nonNull(propType)) {
                        kanbanProp.setKanbanPropertyType(propType);
                    }

                    // プロパティ値
                    if (!StringUtils.isEmpty(prop.getKanbanPropertyValue())) {
                        kanbanProp.setKanbanPropertyValue(prop.getKanbanPropertyValue());
                    }
                } else if (Objects.nonNull(propType)) {
                    // 存在しないプロパティの場合は、プロパティ名とプロパティ値が入っていたら追加する。
                    KanbanPropertyInfoEntity kanbanProp = new KanbanPropertyInfoEntity();
                    kanbanProp.setKanbanPropertyName(propName);
                    kanbanProp.setKanbanPropertyType(propType);

                    // プロパティ値
                    if (!StringUtils.isEmpty(prop.getKanbanPropertyValue())) {
                        kanbanProp.setKanbanPropertyValue(prop.getKanbanPropertyValue());
                    }

                    // プロパティ表示順の最大値から、次に追加するプロパティの表示順を設定
                    int propOrder = 1;
                    Optional<KanbanPropertyInfoEntity> lastProp = kanban.getPropertyCollection().stream().max(Comparator.comparingInt(p -> p.getKanbanPropertyOrder()));
                    if (lastProp.isPresent()) {
                        propOrder = lastProp.get().getKanbanPropertyOrder() + 1;
                    }
                    kanbanProp.setKanbanPropertyOrder(propOrder);

                    kanban.getPropertyCollection().add(kanbanProp);
                }
            }

            // 製品情報をインポートする。
            List<ImportProductCsv> products = importProduct.stream().filter(p -> kanbanName.equals(p.getKanbanName())).collect(Collectors.toList());
            Integer order = 1;
            // 更新対象の製品情報
            if (Objects.isNull(kanban.getProducts())) {
                kanban.setProducts(new ArrayList());
            }
            for (ImportProductCsv product : products) {
                // ユニークID
                String uniqueId = product.getUniqueId();
                if (StringUtils.isEmpty(uniqueId)) {
                    continue;
                }

                //if (Objects.nonNull(product.getKanbanName())) {
                //    if (product.getKanbanName().length() > MAX_CHAR) {
                //        addResult(String.format(" > %s:%s:%s",
                //                LocaleUtils.getString("key.ProductInformation"),
                //                LocaleUtils.getString("key.Legend"),
                //                String.format(LocaleUtils.getString("key.warn.enterCharacters"), 256)));
                //    }
                //}
                //if (Objects.nonNull(uniqueId)) {
                //    if (uniqueId.length() > 32) {
                //        addResult(String.format(" > %s:%s:%s",
                //                LocaleUtils.getString("key.ProductInformation"),
                //                LocaleUtils.getString("key.Product.UniqueID"),
                //                String.format(LocaleUtils.getString("key.warn.enterCharacters"), 32)));
                //    }
                //}
                Optional<ProductInfoEntity> findProduct
                        = kanban.getProducts().stream().filter(p -> uniqueId.equals(p.getUniqueId())).findFirst();
                if (findProduct.isPresent()) {
                    // 存在する場合、オーダーを更新する。
                    findProduct.get().setOrderNum(order++);
                } else if (Objects.nonNull(uniqueId)) {
                    // 存在しない場合は追加する。
                    ProductInfoEntity productEntity = new ProductInfoEntity();
                    productEntity.setUniqueId(uniqueId);// ユニークID
                    productEntity.setOrderNum(order++);

                    kanban.getProducts().add(productEntity);
                }
            }

            // ロット数量
            kanban.setLotQuantity(kanban.getProducts().size());

            // 製品情報がある場合、カンバンプロパティの最後に指示数を追加する。
            if (!kanban.getProducts().isEmpty()) {

                Optional<KanbanPropertyInfoEntity> opt = kanban.getPropertyCollection().stream()
                        .filter(p -> p.getKanbanPropertyName().equals(targetPropName)).findFirst();

                KanbanPropertyInfoEntity kanbanProp;
                if (opt.isPresent()) {
                    kanbanProp = opt.get();
                } else {
                    kanbanProp = new KanbanPropertyInfoEntity();

                    kanbanProp.setKanbanPropertyName(targetPropName);
                    kanbanProp.setKanbanPropertyType(CustomPropertyTypeEnum.TYPE_INTEGER);

                    kanban.getPropertyCollection().add(kanbanProp);
                }

                kanbanProp.setKanbanPropertyValue(String.valueOf(kanban.getProducts().size()));

                // 指示数のプロパティ表示順が最後になるようにする。
                int propOrder = 1;
                Optional<KanbanPropertyInfoEntity> lastProp = kanban.getPropertyCollection().stream()
                        .filter(p -> !p.getKanbanPropertyName().equals(targetPropName))
                        .max(Comparator.comparingInt(p -> p.getKanbanPropertyOrder()));
                if (lastProp.isPresent()) {
                    propOrder = lastProp.get().getKanbanPropertyOrder() + 1;
                }
                kanbanProp.setKanbanPropertyOrder(propOrder);
            }

            // 工程カンバンの更新
            this.updateWorkKanban(kanban, kanbanName, importWorkKanbans, organizationMap, equipmentMap);

            // 工程カンバンプロパティの更新
            this.udpateWorkKanbanProp(kanban, kanbanName, importWkKanbanProps);

            // 内作外注の切り替えを行う
            importWorkKanbans = this.createImportWorkKanbanCsv(kanbanName, kanban.getWorkKanbanCollection());
            this.updateWorkKanban(kanban, kanbanName, importWorkKanbans, organizationMap, equipmentMap);

            // 更新日時
            Date updateDateTime = DateUtils.toDate(LocalDateTime.now());
            kanban.setUpdateDatetime(updateDateTime);

            // 更新者
            kanban.setFkUpdatePersonId(loginUserInfoEntity.getId());

            // カンバン更新
            //addResult(String.format("  > %s", LocaleUtils.getString("key.ImportKanban_UpdateKanban")));
            logger.debug("import kanban:{}", kanban);
            ResponseEntity updateRes = kanbanInfoFacade.update(kanban);
            if (Objects.nonNull(updateRes) && updateRes.isSuccess()) {
                // 更新成功

                // kanban_status.csvの値を反映する.
                if (statusMap.containsKey(kanbanName)) {
                    kanban.setKanbanStatus(statusMap.get(kanbanName));
                } else {
                    // カンバンステータスを元の状態に戻す
                    kanban.setKanbanStatus(kanbanStatus);
                }
                kanbanInfoFacade.update(kanban);

                successNum++;
                //addResult(String.format("  > %s", LocaleUtils.getString("key.ImportKanban_UpdateSuccess")));
            } else {
                // 更新失敗
                failedNum++;
                addResult(String.format("  > %s", LocaleUtils.getString("key.ImportKanban_UpdateFailed")));
            }
        }

        if (dbConnect) {
            con.closeDB();
        }

        addResult(String.format("%s: %s (%s: %s, %s: %s, %s: %s)",
                LocaleUtils.getString("key.ImportKanban_ProccessNum"), procNum,
                LocaleUtils.getString("key.ImportKanban_SuccessNum"), successNum,
                LocaleUtils.getString("key.ImportKanban_SkipNum"), skipKanbanNum,
                LocaleUtils.getString("key.ImportKanban_FailedNum"), failedNum));

        Map<String, Integer> ret = new HashMap<>();
        ret.put("procNum", procNum);
        ret.put("successNum", successNum);
        ret.put("skipKanbanNum", skipKanbanNum);
        ret.put("failedNum", failedNum);

        return ret;
    }

    /**
     * 工程カンバンプロパティを更新する
     *
     * @param kanban 更新対象のカンバン。このカンバンの中身を書き換えるため注意。
     * @param kanbanName
     * @param importWkKanbanProps
     */
    private void udpateWorkKanbanProp(KanbanInfoEntity kanban, String kanbanName, List<ImportWorkKanbanPropertyCsv> importWkKanbanProps) {

        // 工程カンバンプロパティ
        List<WorkKanbanInfoEntity> wKanList = kanban.getWorkKanbanCollection();
        List<ImportWorkKanbanPropertyCsv> csvList = importWkKanbanProps.stream().filter(p -> kanbanName.equals(p.getKanbanName())).collect(Collectors.toList());

        csvList.stream().forEach((csv) -> {

            Optional<WorkKanbanInfoEntity> optional;
            if (!csv.getWorkName().isEmpty()) {
                // 工程名があるとき、工程名で工程カンバン検索
                optional = wKanList.stream().filter(p -> csv.getWorkName().equals(p.getWorkName())).findFirst();
            } else {
                // 工程名がないとき、工程の番号で工程カンバン検索
                optional = wKanList.stream().filter(p -> csv.getWorkNum().equals(String.valueOf(1 + wKanList.indexOf(p)))).findFirst();
            }

            // 該当する工程カンバンがなければスキップ
            if (!optional.isPresent()) {
                return;
            }

            WorkKanbanInfoEntity workKanban = optional.get();

            String propName = csv.getWkKanbanPropName();
            if (!StringUtils.isEmpty(propName)) {

                // プロパティ型
                CustomPropertyTypeEnum propType = null;
                if (!StringUtils.isEmpty(csv.getWkKanbanPropType())) {
                    String propTypeString = csv.getWkKanbanPropType();
                    if (CustomPropertyTypeEnum.TYPE_STRING.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_STRING;
                    } else if (CustomPropertyTypeEnum.TYPE_BOOLEAN.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_BOOLEAN;
                    } else if (CustomPropertyTypeEnum.TYPE_INTEGER.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_INTEGER;
                    } else if (CustomPropertyTypeEnum.TYPE_NUMERIC.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_NUMERIC;
                    } else if (CustomPropertyTypeEnum.TYPE_IP4_ADDRESS.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_IP4_ADDRESS;
                    } else if (CustomPropertyTypeEnum.TYPE_MAC_ADDRESS.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_MAC_ADDRESS;
                    } else if (CustomPropertyTypeEnum.TYPE_PLUGIN.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_PLUGIN;
                    } else if (CustomPropertyTypeEnum.TYPE_TRACE.name().equals(propTypeString)) {
                        propType = CustomPropertyTypeEnum.TYPE_TRACE;
                    } else {
                        addResult(String.format(" > %s:%s:%s", LocaleUtils.getString("key.import.production.work.kanba.property"), LocaleUtils.getString("key.type"), propTypeString));
                    }
                }

                Optional<WorkKanbanPropertyInfoEntity> propOpt = workKanban.getPropertyCollection().stream().filter(p -> propName.equals(p.getWorkKanbanPropName())).findFirst();
                if (propOpt.isPresent()) {
                    // 工程カンバンプロパティを上書き
                    WorkKanbanPropertyInfoEntity wkKanbanPropEntity = propOpt.get();
                    if (Objects.nonNull(propType)) {
                        wkKanbanPropEntity.setWorkKanbanPropType(propType);
                    }
                    wkKanbanPropEntity.setWorkKanbanPropValue(csv.getWkKanbanPropValue());

                } else if (Objects.nonNull(propType)) {
                    // 工程カンバンプロパティを追加
                    WorkKanbanPropertyInfoEntity wkKanbanPropEntity = new WorkKanbanPropertyInfoEntity();
                    wkKanbanPropEntity.setWorkKanbanPropName(propName);
                    wkKanbanPropEntity.setWorkKanbanPropType(propType);
                    wkKanbanPropEntity.setWorkKanbanPropValue(csv.getWkKanbanPropValue());

                    int propOrder = 1;
                    Optional<WorkKanbanPropertyInfoEntity> lastProp = workKanban.getPropertyCollection().stream().max(Comparator.comparingInt(p -> p.getWorkKanbanPropOrder()));
                    if (lastProp.isPresent()) {
                        propOrder = lastProp.get().getWorkKanbanPropOrder() + 1;
                    }
                    wkKanbanPropEntity.setWorkKanbanPropOrder(propOrder);

                    workKanban.getPropertyCollection().add(wkKanbanPropEntity);
                }
            }
        });
    }

    /**
     *
     * @param kanban
     * @param kanbanName
     * @param importWorkKanbans
     * @param organizationMap
     * @param equipmentMap
     * @throws UnsupportedEncodingException
     */
    private void updateWorkKanban(KanbanInfoEntity kanban, String kanbanName, List<ImportWorkKanbanCsv> importWorkKanbans, Map<String, OrganizationInfoEntity> organizationMap, Map<String, EquipmentInfoEntity> equipmentMap) throws UnsupportedEncodingException {
        List<ImportWorkKanbanCsv> works = importWorkKanbans.stream().filter(p -> kanbanName.equals(p.getKanbanName())).collect(Collectors.toList());
        for (ImportWorkKanbanCsv work : works) {

            // 2019/12/18 工程名項目の追加対応 工程カンバンの検索処理を工程名を考慮したものに変更
            Optional<WorkKanbanInfoEntity> opt;
            String workName = work.getWorkName();
            if (!StringUtils.isEmpty(workName)) {
                // 工程名があるとき、工程名で工程カンバン検索
                opt = kanban.getWorkKanbanCollection().stream().filter(p -> workName.equals(p.getWorkName())).findFirst();
            } else {
                // 工程名がないとき、工程の番号で工程カンバン検索
                opt = kanban.getWorkKanbanCollection().stream().filter(p -> work.getWorkNum().equals(String.valueOf(1 + kanban.getWorkKanbanCollection().indexOf(p)))).findFirst();
            }

            // 該当する工程カンバンがなければスキップ
            if (!opt.isPresent()) {
                if (!StringUtils.isEmpty(workName)) {
                    addResult(String.format("  > %s:%s", LocaleUtils.getString("key.alert.notfound.workkanbanError"), workName));
                } else {
                    addResult(String.format("  > %s:%s", LocaleUtils.getString("key.alert.notfound.workkanbanError"), work.getWorkNum()));
                }
                continue;
            }

            WorkKanbanInfoEntity workKanban = opt.get();

            // 工程カンバンステータスが「計画中(Planning)」，「計画済み(Planned)」以外の場合は更新しない
            KanbanStatusEnum workStatus = workKanban.getWorkStatus();
            if (!workStatus.equals(KanbanStatusEnum.PLANNING)
                    && !workStatus.equals(KanbanStatusEnum.PLANNED)) {
                // 更新できない工程カンバンのためスキップ
                addResult(String.format("  > %s:%s:%s",
                        LocaleUtils.getString("key.import.work.plan.work.kanban"), LocaleUtils.getString("key.ImportKanban_NotUpdateWorkKanban"), LocaleUtils.getString(workStatus.getResourceKey())));
                continue;
            }

            // スキップフラグ
            if (!StringUtils.isEmpty(work.getSkipFlag())) {
                if (work.getSkipFlag().equals("1")) {
                    workKanban.setSkipFlag(true);
                } else {
                    workKanban.setSkipFlag(false);
                }
            }

            // 開始予定日時
            if (!StringUtils.isEmpty(work.getStartDatetime())) {
                Date workStartDateTime = ImportFormatFileUtil.stringToDateTime(work.getStartDatetime());
                if (Objects.isNull(workStartDateTime)) {
                    addResult(String.format("  > %s:%s:%s",
                            LocaleUtils.getString("key.import.work.plan.work.kanban"), LocaleUtils.getString("key.error.format.datetime"), LocaleUtils.getString(work.getStartDatetime())));
                    continue;
                }
                workKanban.setStartDatetime(workStartDateTime);
            }

            // 完了予定日時
            if (!StringUtils.isEmpty(work.getCompDatetime())) {
                Date workCompDateTime = ImportFormatFileUtil.stringToDateTime(work.getCompDatetime());
                if (Objects.isNull(workCompDateTime)) {
                    addResult(String.format("  > %s:%s:%s",
                            LocaleUtils.getString("key.import.work.plan.work.kanban"), LocaleUtils.getString("key.error.format.datetime"), LocaleUtils.getString(work.getCompDatetime())));
                    continue;
                }
                workKanban.setCompDatetime(workCompDateTime);
            }

            // 組織
            String organizations = work.getOrganizations();
            if (!StringUtils.isEmpty(organizations)) {
                String[] oranizationIdents = organizations.split(DELIMITER, 0);

                List<Long> orgIdList = new ArrayList<>();
                for (String oranizationIdent : oranizationIdents) {
                    if (oranizationIdent.equals(DELIMITER)) {
                        continue;
                    }
                    OrganizationInfoEntity entity;
                    if (organizationMap.containsKey(oranizationIdent)) {
                        entity = organizationMap.get(oranizationIdent);
                    } else {
                        entity = organizationInfoFacade.findName(URLEncoder.encode(oranizationIdent, CHARSET));
                        organizationMap.put(oranizationIdent, entity);
                    }

                    if (Objects.nonNull(entity.getOrganizationId())) {
                        orgIdList.add(entity.getOrganizationId());
                    } else {
                        // 存在しない組織識別名
                        addResult(String.format("  > %s:%s:%s ",
                                LocaleUtils.getString("key.import.work.plan.work.kanban"), LocaleUtils.getString("key.ImportKanban_OrganizationNothing"), oranizationIdent));
                    }
                }

                workKanban.setOrganizationCollection(orgIdList);
            }

            // 設備
            String equipments = work.getEquipments();
            if (!StringUtils.isEmpty(equipments)) {
                String[] equipmentIdents = equipments.split(DELIMITER, 0);

                List<Long> equipIdList = new ArrayList<>();
                for (String equipmentIdent : equipmentIdents) {
                    if (equipmentIdent.equals(DELIMITER)) {
                        continue;
                    }
                    EquipmentInfoEntity entity;
                    if (equipmentMap.containsKey(equipmentIdent)) {
                        entity = equipmentMap.get(equipmentIdent);
                    } else {
                        entity = equipmentInfoFacade.findName(equipmentIdent);
                        equipmentMap.put(equipmentIdent, entity);
                    }

                    if (Objects.nonNull(entity.getEquipmentId())) {
                        equipIdList.add(entity.getEquipmentId());
                    } else {
                        // 存在しない設備識別名
                        addResult(String.format("  > %s:%s:%s ",
                                LocaleUtils.getString("key.import.work.plan.work.kanban"), LocaleUtils.getString("key.ImportKanban_EquipmentNothing"), equipmentIdent));
                    }
                }

                workKanban.setEquipmentCollection(equipIdList);
            }

            if (!works.isEmpty()) {
                // 開始予定日時
                kanban.getWorkKanbanCollection().sort(Comparator.comparing(p -> p.getStartDatetime()));
                Date startDateTime = kanban.getWorkKanbanCollection().get(0).getStartDatetime();
                kanban.setStartDatetime(startDateTime);

                // 完了予定日時
                kanban.getWorkKanbanCollection().sort(Comparator.comparing(p -> p.getCompDatetime()));
                Date compDateTime = kanban.getWorkKanbanCollection().get(kanban.getWorkKanbanCollection().size() - 1).getCompDatetime();
                kanban.setCompDatetime(compDateTime);
            }
        }
    }

    /**
     *
     * @param workflowName
     * @param rev
     * @return
     * @throws UnsupportedEncodingException
     */
    private WorkflowInfoEntity findWorkflow(final String workflowName, final String rev) throws UnsupportedEncodingException {
        WorkflowInfoEntity workflow;
        if (Objects.nonNull(rev) && !rev.isEmpty()) {
            workflow = workflowInfoFacade.findName(URLEncoder.encode(workflowName, CHARSET), Integer.valueOf(rev));
        } else {
            workflow = workflowInfoFacade.findName(URLEncoder.encode(workflowName, CHARSET));
        }
        return workflow;
    }

    /**
     *
     * @param message
     */
    @Override
    public void addResult(String message) {
        importLogger.addResult(message);
    }

    /**
     * 工程カンバンの順番を求める
     *
     * @param workKanbans
     * @param pred 順番がほしい工程カンバンの情報
     * @return 指定した条件に一致する工程カンバンが存在した場合その工程カンバンの順番。見つからなかった場合-1を返す
     */
    private String getWorkKanbanOrder(List<WorkKanbanInfoEntity> workKanbans, Predicate<WorkKanbanInfoEntity> pred) {

        int count = 1;
        for (WorkKanbanInfoEntity workKanban : workKanbans) {
            if (pred.test(workKanban)) {
                break;
            }
            ++count;
        }

        return count - 1 >= workKanbans.size() ? String.valueOf(-1) : String.valueOf(count);
    }

    /**
     * 追加の工程カンバン更新情報を作成する。現状「内作外注切り替え」のみ
     *
     * <pre>
     * 工程カンバンプロパティに「内注外作」が存在する場合、その値に応じて各工程カンバンのスキップを決定する
     * 内注外注が0の場合、なにもしない
     * 内注外注が1の場合、工程カンバンプロパティに「外注先」が存在するときその工程をスキップする
     * 内注外注が2の場合、工程カンバンプロパティに「外注先」が存在しないときその工程をスキップする
     * </pre>
     *
     * @param kanbanName
     * @param workKanbans
     * @param importWkKanbanProps
     * @return
     */
    private List<ImportWorkKanbanCsv> createImportWorkKanbanCsv(String kanbanName, List<WorkKanbanInfoEntity> workKanbans) {

        final Function<Boolean, String> toSkip = x -> x ? "1" : "0";

        final Predicate<WorkKanbanPropertyInfoEntity> predicate = wp
                -> SUBCON.equals(wp.getWorkKanbanPropName()) && !StringUtils.isEmpty(wp.getWorkKanbanPropValue());

        boolean flag = false;
        for (WorkKanbanInfoEntity workKanban : workKanbans) {
            flag = workKanban.getPropertyCollection().stream().filter(predicate).count() > 0;
            if (flag) {
                break;
            }
        }

        final boolean outsourcing = flag;

        // 工程カンバンからその工程カンバンプロパティをもとに工程カンバンのスキップ情報を書き換えるインポート情報を生成する
        final Function<WorkKanbanInfoEntity, Stream<ImportWorkKanbanCsv>> mapper = workKanban
                -> workKanban.getPropertyCollection().stream()
                        .filter(prop -> ORDER.equals(prop.getWorkKanbanPropName()))
                        .map(prop -> {
                            String workNum = getWorkKanbanOrder(workKanbans, wk -> Objects.equals(wk.getWorkName(), workKanban.getWorkName()));
                            String value = prop.getWorkKanbanPropValue();

                            if (StringUtils.isEmpty(value)) {
                                return new ImportWorkKanbanCsv();
                            }

                            switch (value) {
                                case "1":
                                    return new ImportWorkKanbanCsv(kanbanName, workNum, toSkip.apply(outsourcing), "", "", "", "", workKanban.getWorkName(), "");
                                case "2":
                                    return new ImportWorkKanbanCsv(kanbanName, workNum, toSkip.apply(!outsourcing), "", "", "", "", workKanban.getWorkName(), "");
                                case "0":
                                default:
                                    return new ImportWorkKanbanCsv();
                            }
                        });

        return workKanbans.stream()
                .flatMap(mapper)
                .collect(toList());
    }
}
