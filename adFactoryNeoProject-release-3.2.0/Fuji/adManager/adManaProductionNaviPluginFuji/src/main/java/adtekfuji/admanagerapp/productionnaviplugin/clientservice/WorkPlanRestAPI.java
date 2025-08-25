/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | s
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.clientservice;

import adtekfuji.clientservice.ActualResultInfoFacade;
import adtekfuji.clientservice.AgendaFacade;
import adtekfuji.clientservice.BreaktimeInfoFacade;
import adtekfuji.clientservice.DisplayedStatusInfoFacade;
import adtekfuji.clientservice.OrganizationInfoFacade;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;
import jp.adtekfuji.adFactory.entity.agenda.KanbanTopicInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.search.ActualSearchCondition;
import jp.adtekfuji.adFactory.entity.search.KanbanTopicSearchCondition;
import adtekfuji.admanagerapp.productionnaviplugin.common.agenda.WorkPlanCustomAgendaEntity;
import adtekfuji.admanagerapp.productionnaviplugin.common.agenda.WorkPlanCustomAgendaItemEntity;
import adtekfuji.admanagerapp.productionnaviplugin.common.agenda.WorkPlanCustomAgendaConcurrentEntity;
import adtekfuji.clientservice.ScheduleInfoFacade;
import java.util.HashMap;
import java.util.Map;
import jp.adtekfuji.adFactory.entity.schedule.ScheduleInfoEntity;
import jp.adtekfuji.adFactory.entity.search.ScheduleSearchCondition;
import jp.adtekfuji.javafxcommon.dialog.DialogBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import adtekfuji.cash.CashManager;
import adtekfuji.clientservice.KanbanHierarchyInfoFacade;
import adtekfuji.clientservice.KanbanInfoFacade;
import adtekfuji.clientservice.WorkKanbanInfoFacade;
import adtekfuji.clientservice.WorkflowInfoFacade;
import java.util.stream.Collectors;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.holiday.HolidayInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.PlanChangeCondition;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;

/**
 * RestAPI
 *
 * @author (TST)min
 * @version 2.0.0
 * @since 2018/09/28
 */
public class WorkPlanRestAPI {

    private final Logger LOGGER = LogManager.getLogger();

    // adFactoryREST クライアント
    private final KanbanInfoFacade KANBAN_REST = new KanbanInfoFacade();
    private final KanbanHierarchyInfoFacade KANBAN_HIERARCHY_REST = new KanbanHierarchyInfoFacade();
    private final OrganizationInfoFacade ORGANIZATION_REST = new OrganizationInfoFacade();
    private final AgendaFacade AGENDA_REST = new AgendaFacade();
    private final ActualResultInfoFacade ACTUAL_RESULT_REST = new ActualResultInfoFacade();
    private final DisplayedStatusInfoFacade DISPLAYED_STATUS_REST = new DisplayedStatusInfoFacade();
    private final ScheduleInfoFacade SCHEDULE_REST = new ScheduleInfoFacade();
    private final BreaktimeInfoFacade BREAKTIME_REST = new BreaktimeInfoFacade();

    private final LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();

    private final long RANGE = 300;

    /**
     * 予実の計画データを作成
     *
     * @param agenda
     * @param agendaItems
     * @param startDate
     * @param endDate
     */
    public void createPlan(WorkPlanCustomAgendaEntity agenda, List<WorkPlanCustomAgendaItemEntity> agendaItems, Date startDate, Date endDate) {
        LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + " ::createPlan start.");

        // カンバン予実情報を予実モニターに表示する末端の情報に変換
//        Collections.sort(agendaItems, (CustomAgendaItemEntity o1, CustomAgendaItemEntity o2) -> o1.getStartTime().compareTo(o2.getStartTime()));
        List<DatetimeConcurrent> plans = new ArrayList<>();
        for (WorkPlanCustomAgendaItemEntity agendaItem : agendaItems) {

            if (Objects.isNull(agendaItem.getStartTime()) || Objects.isNull(agendaItem.getEndTime())) {
                continue;
            }
            if ((agendaItem.getStartTime().before(startDate) && agendaItem.getEndTime().before(startDate))
                    || agendaItem.getStartTime().after(endDate) && agendaItem.getEndTime().after(endDate)) {
                continue;
            }
            boolean find = false;
            for (DatetimeConcurrent c : plans) {
                if ((c.getStart().before(agendaItem.getStartTime()) && c.getEnd().after(agendaItem.getStartTime()))
                        || (c.getStart().before(agendaItem.getEndTime()) && c.getEnd().after(agendaItem.getEndTime()))
                        || c.getStart().equals(agendaItem.getStartTime()) || c.getEnd().equals(agendaItem.getEndTime())) {
                    c.getConcurrent().addItem(agendaItem);
                    if (c.getStart().before(agendaItem.getStartTime())) {
                        c.setStart(agendaItem.getStartTime());
                    }
                    if (c.getEnd().before(agendaItem.getEndTime())) {
                        c.setEnd(agendaItem.getEndTime());
                    }
                    find = true;
                    break;
                }
            }
            if (find == false) {
                DatetimeConcurrent concurrent = new DatetimeConcurrent(
                        agendaItem.getStartTime(), agendaItem.getEndTime(), new WorkPlanCustomAgendaConcurrentEntity().addItem(agendaItem));
                plans.add(concurrent);
            }
        }
        for (DatetimeConcurrent c : plans) {
            agenda.addPlan(c.getConcurrent());
        }
        LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + " ::createPlan end.");
    }

    /**
     * 予実の実績データを作成
     *
     * @param agenda
     * @param agendaItems
     * @param startDate
     * @param endDate
     */
    public void createActual(WorkPlanCustomAgendaEntity agenda, List<WorkPlanCustomAgendaItemEntity> agendaItems, Date startDate, Date endDate) {
        LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + " ::createActual start.");

        List<DatetimeConcurrent> actual = new ArrayList<>();
        for (WorkPlanCustomAgendaItemEntity agendaItem : agendaItems) {
            if (Objects.isNull(agendaItem.getStartTime()) || Objects.isNull(agendaItem.getEndTime())) {
                continue;
            }

            if ((agendaItem.getStartTime().before(startDate) && agendaItem.getEndTime().before(startDate))
                    || agendaItem.getStartTime().after(endDate) && agendaItem.getEndTime().after(endDate)) {
                continue;
            }

            boolean find = false;
            for (DatetimeConcurrent c : actual) {
                if ((c.getStart().before(agendaItem.getStartTime()) && c.getEnd().after(agendaItem.getStartTime()))
                        || (c.getStart().before(agendaItem.getEndTime()) && c.getEnd().after(agendaItem.getEndTime()))
                        || c.getStart().equals(agendaItem.getStartTime()) || c.getEnd().equals(agendaItem.getEndTime())) {
                    c.getConcurrent().addItem(agendaItem);
                    if (c.getStart().before(agendaItem.getStartTime())) {
                        c.setStart(agendaItem.getStartTime());
                    }
                    if (c.getEnd().before(agendaItem.getEndTime())) {
                        c.setEnd(agendaItem.getEndTime());
                    }
                    find = true;
                    break;
                }
            }

            if (find == false) {
                DatetimeConcurrent concurrent = new DatetimeConcurrent(
                        agendaItem.getStartTime(), agendaItem.getEndTime(), new WorkPlanCustomAgendaConcurrentEntity().addItem(agendaItem));
                actual.add(concurrent);
            }
        }

        for (DatetimeConcurrent c : actual) {
            agenda.addActual(c.getConcurrent());
        }

        LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + " ::createActual end.");
    }

    /**
     * workKanbanの作業者リスト取得
     *
     * @param topics
     * @return workKanbanOrgaIdsMap
     */
    public Map<Long, List<Long>> getWorkKanbanOrgaIdsMap(List<KanbanTopicInfoEntity> topics) {
        Map<Long, List<Long>> result = new HashMap<>();
        List<Long> orgaIds = new ArrayList<>();
        Long beforeId = -1L;
        for (KanbanTopicInfoEntity t : topics) {
            if (beforeId.equals(t.getWorkKanbanId())) {
                orgaIds.add(t.getOrganizationId());
            } else {
                if (!beforeId.equals(-1L)) {
                    result.put(beforeId, orgaIds);
                    orgaIds = new ArrayList<>();
                }
                beforeId = t.getWorkKanbanId();
                orgaIds.add(t.getOrganizationId());
            }
        }
        result.put(beforeId, orgaIds);
        return result;
    }

    /**
     * 実績情報検索
     *
     * @param condition
     * @return
     */
    public List<ActualResultEntity> searchActualResult(ActualSearchCondition condition) {
        List<ActualResultEntity> result = new ArrayList<>();
        Long count = 0l;
        Long nowCount = 0l;
        Boolean isCount = false;
        Boolean isContinue = true;

//        ACTUAL_RESULT_REST.getLast(condition);
        try {
            LOGGER.info(WorkPlanRestAPI.class.getSimpleName() + " ::searchActualResult start.");
            while (isContinue) {
                try {
                    //ID指定無:最上位階層取得/ID指定有:指定されたID以下階層取得
                    if (!isCount) {
                        count = ACTUAL_RESULT_REST.searchCount(condition);
                    }
                    for (; nowCount < count; nowCount += RANGE) {
                        result.addAll(ACTUAL_RESULT_REST.searchRange(condition, nowCount, nowCount + RANGE - 1));
                    }
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(WorkPlanRestAPI.class.getSimpleName() + " ::searchActualResult end.");
        }
        return result;
    }

    /**
     * 予実情報の取得
     *
     * @param condition
     * @return
     */
    public List<KanbanTopicInfoEntity> searchKanbanTopic(KanbanTopicSearchCondition condition) {
        List<KanbanTopicInfoEntity> result = null;

        try {
            LOGGER.info(WorkPlanRestAPI.class.getSimpleName() + "::searchKanbanTopic start.");

            Boolean isContinue = true;
            while (isContinue) {
                try {
                    result = AGENDA_REST.findTopic(condition);
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }

        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(WorkPlanRestAPI.class.getSimpleName() + "::searchKanbanTopic end.");
        }

        return Objects.nonNull(result) ? result : new ArrayList<>();
    }

    /**
     * 指定されたIDの作業者を取得
     *
     * @param id
     * @return
     */
    public OrganizationInfoEntity searchOrganization(Long id) {
        OrganizationInfoEntity result = new OrganizationInfoEntity();
        Boolean isContinue = true;

        try {
            LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + "::searchOrganization start.");
            while (isContinue) {
                try {
                    result = CacheUtils.getCacheOrganization(id);
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + "::searchOrganization end.");
        }
        return result;
    }

    /**
     * 組織階層IDでTop組織階層を取得する
     *
     * @return
     */
    public List<OrganizationInfoEntity> searchTopOrganizationHierarchys() {
        LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + "::searchTopOrganizationHierarchys start.");
        long count = ORGANIZATION_REST.getTopHierarchyCount();
        List<OrganizationInfoEntity> hierarchies = new ArrayList();
        for (long from = 0; from <= count; from += RANGE) {
            List<OrganizationInfoEntity> entities = ORGANIZATION_REST.getTopHierarchyRange(from, from + RANGE - 1);
            hierarchies.addAll(entities);
        }
        LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + "::searchTopOrganizationHierarchys end.");
        return hierarchies;
    }

    /**
     * 組織階層IDで組織階層を取得する
     *
     * @return
     */
    public List<OrganizationInfoEntity> searchOrganizationHierarchys(Long organizationHierarchyId) {
        LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + "::searchOrganizationHierarchys start.");
        long count = ORGANIZATION_REST.getAffilationHierarchyCount(organizationHierarchyId);
        List<OrganizationInfoEntity> hierarchies = new ArrayList();
        for (long from = 0; from <= count; from += RANGE) {
            List<OrganizationInfoEntity> entities = ORGANIZATION_REST.getAffilationHierarchyRange(organizationHierarchyId, from, from + RANGE - 1);
            hierarchies.addAll(entities);
        }
        LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + "::searchOrganizationHierarchys end.");
        return hierarchies;
    }

    /**
     * 
     * @param organizations
     * @param organizationId
     * @return 
     */
    public List<OrganizationInfoEntity> searchOrganizationsOfOrganizationHierarchys(List<OrganizationInfoEntity> organizations, Long organizationId) {
        List<OrganizationInfoEntity> result = organizations;
        organizations.add(searchOrganization(organizationId));
        // 子組織の件数を取得する。
        long count = ORGANIZATION_REST.getAffilationHierarchyCount(organizationId);
        // 対象組織情報を取得（子の組織も）
        for (long from = 0; from <= count; from += RANGE) {
            List<OrganizationInfoEntity> datas = ORGANIZATION_REST.getAffilationHierarchyRange(organizationId, from, from + RANGE - 1);
            for (OrganizationInfoEntity data : datas) {
                searchOrganizationsOfOrganizationHierarchys(result, data.getOrganizationId());
            }
        }
        return result;
    }

    /**
     * 指定した日付範囲の休日一覧を取得する。
     *
     * @param startDate 日付範囲の先頭
     * @param endDate 日付範囲の末尾
     * @return 休日一覧
     */
    public List<HolidayInfoEntity> searchHolidays(Date startDate, Date endDate) {
        List<HolidayInfoEntity> result = new ArrayList<>();
        Boolean isContinue = true;

        try {
            LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + "::searchHolidays start.");
            CashManager cache = CashManager.getInstance();
            while (isContinue) {
                try {
                    CacheUtils.createCacheHoliday(true);

                    // キャッシュから休日一覧を取得する。
                    List<HolidayInfoEntity> holidays = cache.getItemList(HolidayInfoEntity.class, new ArrayList());
                    if (Objects.isNull(holidays) || holidays.isEmpty()) {
                        return result;
                    }

                    // 指定した日付範囲の休日一覧に絞り込む。
                    result = holidays.stream()
                            .filter(p -> (p.getHolidayDate().equals(startDate) || p.getHolidayDate().after(startDate))
                                    && (p.getHolidayDate().equals(endDate) || p.getHolidayDate().before(endDate)))
                            .collect(Collectors.toList());
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + "::searchHolidays end.");
        }
        return result;
    }

    /**
     * 指定した期間のカンバン者業者予定を取得
     *
     * @param organizationIds
     * @param startDate
     * @param endDate
     * @return
     */
    public List<ScheduleInfoEntity> searchSchedules(List<Long> organizationIds, Date startDate, Date endDate) {
        List<ScheduleInfoEntity> result = new ArrayList<>();
        Long count = 0l;
        Long nowCount = 0l;
        Boolean isCount = false;
        Boolean isContinue = true;

        ScheduleSearchCondition condition = new ScheduleSearchCondition();
        condition.setOrganizationIdCollection(organizationIds);
        condition.fromDate(startDate);
        condition.toDate(endDate);

        try {
            LOGGER.info(WorkPlanRestAPI.class.getSimpleName() + " ::searchSchedules start.");
            while (isContinue) {
                try {
                    //ID指定無:最上位階層取得/ID指定有:指定されたID以下階層取得
                    if (!isCount) {
                        count = SCHEDULE_REST.searchCount(condition);
                    }
                    for (; nowCount < count; nowCount += RANGE) {
                        result.addAll(SCHEDULE_REST.searchRange(condition, nowCount, nowCount + RANGE - 1));
                    }
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(WorkPlanRestAPI.class.getSimpleName() + " ::searchSchedules end.");
        }
        return result;
    }

    /**
     * 指定されたIDの休憩を取得
     *
     * @return
     */
    public List<BreakTimeInfoEntity> searchBreaktimes() {
        List<BreakTimeInfoEntity> result = new ArrayList<>();
        Boolean isContinue = true;

        try {
            LOGGER.info(WorkPlanRestAPI.class.getSimpleName() + " ::searchBreaktimes start.");
            while (isContinue) {
                try {
                    result = BREAKTIME_REST.findAll();
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(WorkPlanRestAPI.class.getSimpleName() + " ::searchBreaktimes end.");
        }
        return result;
    }

    /**
     * 表示ステータスの取得
     *
     * @return
     */
    public List<DisplayedStatusInfoEntity> searchDisplayedStatuses() {
        List<DisplayedStatusInfoEntity> result = new ArrayList<>();
        Boolean isContinue = true;

        try {
            LOGGER.info(WorkPlanRestAPI.class.getSimpleName() + " ::searchDisplayedStatuses start.");
            while (isContinue) {
                try {
                    result = DISPLAYED_STATUS_REST.findAll();
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(WorkPlanRestAPI.class.getSimpleName() + " ::searchDisplayedStatuses end.");
        }
        return result;
    }

    /**
     * 工程順IDで工程順を取得する
     *
     * @param workflowid
     * @return
     */
    public WorkflowInfoEntity searchWorkflow(Long workflowid) {
        LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + "::searchWorkflow start:");
        WorkflowInfoFacade facade = new WorkflowInfoFacade();
        return facade.find(workflowid);
    }

    /**
     * カンバン階層でTopカンバン階層を取得する
     *
     * @return
     * @throws java.lang.Exception
     */
    public List<KanbanHierarchyInfoEntity> searchTopKanbanHierarchys() throws Exception {
        LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + "::searchTopKanbanHierarchys start.");
        long count = KANBAN_HIERARCHY_REST.getTopHierarchyCount();
        List<KanbanHierarchyInfoEntity> hierarchies = new ArrayList();
        for (long from = 0; from <= count; from += RANGE) {
            List<KanbanHierarchyInfoEntity> entities = KANBAN_HIERARCHY_REST.getTopHierarchyRange(from, from + RANGE - 1);
            hierarchies.addAll(entities);
        }
        LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + "::searchTopKanbanHierarchys end.");
        return hierarchies;
    }

    /**
     * カンバン階層IDでカンバン階層を取得する
     *
     * @param kanbanHierarchyId
     * @return
     */
    public List<KanbanHierarchyInfoEntity> searchKanbanHierarchys(Long kanbanHierarchyId) {
        LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + "::searchKanbanHierarchys start.");
        long count = KANBAN_HIERARCHY_REST.getAffilationHierarchyCount(kanbanHierarchyId);
        List<KanbanHierarchyInfoEntity> hierarchies = new ArrayList();
        for (long from = 0; from <= count; from += RANGE) {
            List<KanbanHierarchyInfoEntity> entities = KANBAN_HIERARCHY_REST.getAffilationHierarchyRange(kanbanHierarchyId, from, from + RANGE - 1);
            hierarchies.addAll(entities);
        }
        LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + "::searchKanbanHierarchys end.");
        return hierarchies;
    }

    /**
     * 
     * @param kanbans
     * @param kanbanHierarchyId
     * @param search
     * @return 
     */
    public List<KanbanInfoEntity> searchKanbansOfKanbanHierarchys(List<KanbanInfoEntity> kanbans, Long kanbanHierarchyId, KanbanSearchCondition search) {
        List<KanbanInfoEntity> result = kanbans;
        search.setHierarchyId(kanbanHierarchyId);
        result.addAll(findSearchKanbans(search));

        // 子組織の件数を取得する。
        long count = KANBAN_HIERARCHY_REST.getAffilationHierarchyCount(kanbanHierarchyId);
        // 対象組織情報を取得（子の組織も）
        for (long from = 0; from <= count; from += RANGE) {
            List<KanbanHierarchyInfoEntity> datas = KANBAN_HIERARCHY_REST.getAffilationHierarchyRange(kanbanHierarchyId, from, from + RANGE - 1);
            for (KanbanHierarchyInfoEntity data : datas) {
                searchKanbansOfKanbanHierarchys(result, data.getKanbanHierarchyId(), search);
            }
        }
        return result;
    }

    /**
     * カンバン階層でカンバン階層を登録する
     *
     * @param kanbanHierarchy
     * @return
     * @throws java.lang.Exception
     */
    public ResponseEntity registKanbanHierarchy(KanbanHierarchyInfoEntity kanbanHierarchy) throws Exception {
        LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + "::registKanbanHierarchy:");
        return KANBAN_HIERARCHY_REST.regist(kanbanHierarchy);
    }

    /**
     * カンバン階層でカンバン階層を更新する
     *
     * @param kanbanHierarchy
     * @return
     * @throws java.lang.Exception
     */
    public ResponseEntity updateKanbanHierarchy(KanbanHierarchyInfoEntity kanbanHierarchy) throws Exception {
        LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + "::updateKanbanHierarchy:");
        return KANBAN_HIERARCHY_REST.update(kanbanHierarchy);
    }

    /**
     * カンバン階層でカンバン階層を削除する
     *
     * @param kanbanHierarchy
     * @return
     * @throws java.lang.Exception
     */
    public ResponseEntity deleteKanbanHierarchy(KanbanHierarchyInfoEntity kanbanHierarchy) throws Exception {
        LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + "::deleteKanbanHierarchy:");
        return KANBAN_HIERARCHY_REST.delete(kanbanHierarchy);
    }

    /**
     * カンバンIDでカンバンを取得する
     *
     * @param kanbanId
     * @return
     */
    public KanbanInfoEntity searchKanban(Long kanbanId) {
        LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + "::searchKanban:");
        return KANBAN_REST.find(kanbanId);
    }

    /**
     * searchConditionでカンバンを取得する
     *
     * @param search
     * @return
     */
    public List<KanbanInfoEntity> findSearchKanbans(KanbanSearchCondition search) {
        LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + "::findSearchKanbans:");
        return KANBAN_REST.findSearch(search);
    }

    /**
     * カンバンでカンバンを更新する
     *
     * @param kanban
     * @return
     * @throws java.lang.Exception
     */
    public ResponseEntity updateKanban(KanbanInfoEntity kanban) throws Exception {
        LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + "::updateKanban:");
        return KANBAN_REST.update(kanban);
    }

    /**
     * カンバンでカンバンを削除する
     *
     * @param kanbanId
     * @return
     * @throws java.lang.Exception
     */
    public ResponseEntity deleteKanban(Long kanbanId) throws Exception {
        LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + "::deleteKanban:");
        return KANBAN_REST.delete(kanbanId);
    }

    /**
     * カンバンでカンバンを削除(Forced)する
     *
     * @param kanbanId
     * @return
     * @throws java.lang.Exception
     */
    public ResponseEntity deleteKanbanForced(Long kanbanId) throws Exception {
        LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + "::deleteKanbanForced:");
        return KANBAN_REST.deleteForced(kanbanId);
    }

    /**
     * カンバンの計画時間を変更する。(規定件数づつ処理)
     *
     * @param condition
     * @param ids
     * @return
     */
    public ResponseEntity kanbanPlanChange(PlanChangeCondition condition, List<Long> ids) {
        LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + "::kanbanPlanChange:");
        return KANBAN_REST.planChange(condition, ids, loginUser.getId());
    }

    /**
     * カンバンIDで工程カンバンを取得する
     *
     * @param kanbanId
     * @return
     */
    public List<WorkKanbanInfoEntity> searchWorkKanbans(Long kanbanId) {
        try {
            LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + "::searchWorkKanbans start.");
            List<WorkKanbanInfoEntity> entitys = new ArrayList<>();
            WorkKanbanInfoFacade facade = new WorkKanbanInfoFacade();
            Long workkanbanCnt = facade.countFlow(kanbanId);
            for (long nowCnt = 0; nowCnt < workkanbanCnt; nowCnt += RANGE) {
                entitys.addAll(facade.getRangeFlow(nowCnt, nowCnt + RANGE - 1, kanbanId));
            }
            return entitys;
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
            return null;
        } finally {
            LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + "::searchWorkKanbans end.");
        }
    }

    /**
     * カンバンIDで工程カンバンを取得する
     *
     * @param kanbanId
     * @return
     */
    public List<WorkKanbanInfoEntity> searchSeparateWorkKanbans(Long kanbanId) {
        try {
            LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + "::searchSeparateWorkKanbans start.");
            List<WorkKanbanInfoEntity> entitys = new ArrayList<>();
            WorkKanbanInfoFacade facade = new WorkKanbanInfoFacade();
            Long workkanbanCnt = facade.countSeparate(kanbanId);
            for (long nowCnt = 0; nowCnt < workkanbanCnt; nowCnt += RANGE) {
                entitys.addAll(facade.getRangeSeparate(nowCnt, nowCnt + RANGE - 1, kanbanId));
            }
            return entitys;
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
            return null;
        } finally {
            LOGGER.debug(WorkPlanRestAPI.class.getSimpleName() + "::searchSeparateWorkKanbans end.");
        }
    }
}
