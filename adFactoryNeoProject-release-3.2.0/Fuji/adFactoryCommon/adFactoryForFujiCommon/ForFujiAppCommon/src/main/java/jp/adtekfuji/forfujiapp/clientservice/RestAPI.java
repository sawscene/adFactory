/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | s
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.clientservice;

import adtekfuji.clientservice.ActualResultInfoFacade;
import adtekfuji.clientservice.AgendaFacade;
import adtekfuji.clientservice.BreaktimeInfoFacade;
import adtekfuji.clientservice.DelayReasonInfoFacade;
import adtekfuji.clientservice.DisplayedStatusInfoFacade;
import adtekfuji.clientservice.EquipmentInfoFacade;
import adtekfuji.clientservice.KanbanInfoFacade;
import adtekfuji.clientservice.OrganizationInfoFacade;
import adtekfuji.clientservice.WorkHierarchyInfoFacade;
import adtekfuji.clientservice.WorkKanbanInfoFacade;
import adtekfuji.clientservice.WorkflowHierarchyInfoFacade;
import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.property.AdProperty;
import adtekfuji.rest.RestClient;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javax.ws.rs.core.MediaType;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;
import jp.adtekfuji.adFactory.entity.agenda.KanbanTopicInfoEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.master.DelayReasonInfoEntity;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.search.ActualSearchCondition;
import jp.adtekfuji.adFactory.entity.search.KanbanTopicSearchCondition;
import jp.adtekfuji.adFactory.entity.work.WorkHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.forfujiapp.common.AdFactoryForFujiClientAppConfig;
import jp.adtekfuji.forfujiapp.common.ClientPropertyConstants;
import jp.adtekfuji.forfujiapp.common.RestConstants;
import jp.adtekfuji.forfujiapp.common.agenda.CustomAgendaConcurrentEntity;
import jp.adtekfuji.forfujiapp.common.agenda.CustomAgendaEntity;
import jp.adtekfuji.forfujiapp.common.agenda.CustomAgendaItemEntity;
import jp.adtekfuji.forfujiapp.entity.monitor.MonitorGraphInfoEntity;
import jp.adtekfuji.forfujiapp.entity.monitor.MonitorListInfoEntity;
import jp.adtekfuji.forfujiapp.entity.monitor.MonitorPanelInfoEntity;
import jp.adtekfuji.forfujiapp.entity.search.UnitSearchCondition;
import jp.adtekfuji.forfujiapp.entity.unit.ConUnitAssociateInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unit.UnitHierarchyInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unit.UnitInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unit.UnitKanbanInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unit.UnitPropertyInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unittemplate.ConUnitTemplateAssociateInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateHierarchyInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateInfoEntity;
import jp.adtekfuji.forfujiapp.utils.DisplayStatusSelector;
import jp.adtekfuji.javafxcommon.dialog.DialogBox;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * RestAPI
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.26.Wed
 */
public class RestAPI {

    // adFactoryRESTForFuji クライアント
    private static final AdFactoryForFujiClientAppConfig CONFIG = new AdFactoryForFujiClientAppConfig();
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ProgressMonitorEntityFacade PROGRESS_MONITOR_REST = new ProgressMonitorEntityFacade();
    private static final UnitTemplateHierarchyInfoFacade UNITTEMPLATE_HIERARCHY_REST = new UnitTemplateHierarchyInfoFacade();
    private static final UnitTemplateInfoFacade UNITTEMPLATE_REST = new UnitTemplateInfoFacade();
    private static final UnitHierarchyInfoFacade UNIT_HIERARCHY_REST = new UnitHierarchyInfoFacade();
    private static final UnitInfoFacade UNIT_REST = new UnitInfoFacade();
    private static final WorkHierarchyInfoFacade WORK_HIERARCHY_REST = new WorkHierarchyInfoFacade();
    private static final WorkflowHierarchyInfoFacade WORKFLOW_HIERARCHY_REST = new WorkflowHierarchyInfoFacade();
    private static final WorkflowInfoFacade WORKFLOW_REST = new WorkflowInfoFacade();
    private static final KanbanInfoFacade KANBAN_REST = new KanbanInfoFacade();
    private static final WorkKanbanInfoFacade WORK_KANBAN_REST = new WorkKanbanInfoFacade();
    private static final ActualResultInfoFacade ACTUAL_RESULT_REST = new ActualResultInfoFacade();
    private static final EquipmentInfoFacade EQUIPMENT_REST = new EquipmentInfoFacade();
    private static final BreaktimeInfoFacade BREAKTIME_REST = new BreaktimeInfoFacade();
    private static final DelayReasonInfoFacade DELAY_REASON_REST = new DelayReasonInfoFacade();

    // adFactoryREST クライアント
    private static final DisplayedStatusInfoFacade displayedStatusRest = new DisplayedStatusInfoFacade();
    private static final OrganizationInfoFacade organizationRest = new OrganizationInfoFacade();
    private static final AgendaFacade agendRest = new AgendaFacade();

    private final static long RANGE = 300;
    private final static long ACTUAL_RANGE = 30;
    private final static String TAG_NEW_LINE = "\r\n";

    /**
     * 指定された生産ユニットの進捗モニタパネル表示の情報を取得
     *
     * @param conditin
     * @param titleOption
     * @return
     */
    public static List<MonitorPanelInfoEntity> getMonitorPanel(UnitSearchCondition conditin, String titleOption) {
        List<MonitorPanelInfoEntity> result = null;
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getMonitorPanel start.");
            while (isContinue) {
                try {
                    result = PROGRESS_MONITOR_REST.getMonitorPanelEntity(conditin, titleOption);
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getMonitorPanel end.");
        }
        return result;
    }

    /**
     * 指定された生産ユニットの進捗モニタグラフ表示の情報を取得
     *
     * @param conditin
     * @param titleOption
     * @return
     */
    public static List<MonitorGraphInfoEntity> getMonitorGraph(UnitSearchCondition conditin, String titleOption) {
        List<MonitorGraphInfoEntity> result = null;
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getMonitorGraph start.");
            while (isContinue) {
                try {
                    result = PROGRESS_MONITOR_REST.getMonitorGraph(conditin, titleOption);
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getMonitorGraph end.");
        }
        return result;
    }

    /**
     * 指定された生産ユニットの進捗モニタリスト表示の情報を取得
     *
     * @param conditin
     * @param mainTitle
     * @param subTitle
     * @return
     */
    public static List<MonitorListInfoEntity> getMonitorList(UnitSearchCondition conditin, String mainTitle, String subTitle) {
        List<MonitorListInfoEntity> result = null;
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getMonitorList start.");
            while (isContinue) {
                try {
                    result = PROGRESS_MONITOR_REST.getMonitorListEntity(conditin, mainTitle, subTitle);
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getMonitorList end.");
        }
        return result;
    }

    /**
     * 生産ユニットの計画実績を取得する
     *
     * @param units
     * @param startDate
     * @param endDate
     * @param statuses
     * @return
     */
    public static List<CustomAgendaEntity> getUnitAgenda(List<UnitInfoEntity> units, Date startDate, Date endDate, List<DisplayedStatusInfoEntity> statuses) {
        LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitAgenda start.");

        try {
            List<KanbanTopicInfoEntity> topics = searchUnitAgenda(units, startDate, endDate);

            List<CustomAgendaEntity> agendas = new LinkedList<>();
            for (UnitInfoEntity unit : units) {
                List<KanbanTopicInfoEntity> temp = topics.stream().filter(o -> unit.getKanbanIds().contains(o.getKanbanId())).collect(Collectors.toList());
                CustomAgendaEntity agenda = convertAgendaToUnit(temp, unit, startDate, endDate, statuses);
                agendas.add(agenda);
            }

            return agendas;
        } catch (Exception ex) {
            DialogBox.alert(ex);
            LOGGER.fatal(ex, ex);
            return new ArrayList<>();
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitAgenda end.");
        }
    }

    /**
     * 月内に該当する作業者の予定を取得
     *
     * @param organizations
     * @param startDate
     * @param endDate
     * @param statuses
     * @return
     */
    public static List<CustomAgendaEntity> getOrganizationAgenda(List<OrganizationInfoEntity> organizations, Date startDate, Date endDate, List<DisplayedStatusInfoEntity> statuses) {
        LOGGER.info(RestAPI.class.getSimpleName() + "::getOrganizationAgenda start.");
        List<CustomAgendaEntity> agendas = new LinkedList<>();

        try {
            Set<Long> organizationIds = new HashSet<>();
            for (OrganizationInfoEntity organization : organizations) {
                organizationIds.add(organization.getOrganizationId());
            }

            //Date monthFarst = DateUtils.getBeginningOfDate(startDate);
            //Date monthLast = DateUtils.getEndOfDate(endDate);
            //
            //Calendar agendaStartDate = Calendar.getInstance();
            //agendaStartDate.setTime(new Date(monthFarst.getTime()));
            //Calendar agendaEndDate = Calendar.getInstance();
            //agendaEndDate.setTime(new Date(monthLast.getTime()));
            KanbanTopicSearchCondition condition = new KanbanTopicSearchCondition(KanbanTopicSearchCondition.ContentType.DAYS_ORGANIZATION);
            condition.setPrimaryKeys(new ArrayList<>(organizationIds));
            condition.setFromDate(startDate);
            condition.setToDate(endDate);

            List<KanbanTopicInfoEntity> topics = searchKanbanTopic(condition);

            for (OrganizationInfoEntity organization : organizations) {
                List<KanbanTopicInfoEntity> temp = topics.stream().filter(o -> o.getOrganizationId().longValue() == organization.getOrganizationId()).collect(Collectors.toList());
                CustomAgendaEntity agenda = convertAgendaToOrganization(temp, organization, startDate, endDate, statuses);
                agendas.add(agenda);
            }
        } catch (Exception ex) {
            DialogBox.alert(ex);
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getOrganizationAgenda end.");
        }

        return agendas;
    }

    /**
     * 指定した月のユニットの生産予定を取得
     *
     * @param units
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    private static List<KanbanTopicInfoEntity> searchUnitAgenda(List<UnitInfoEntity> units, Date startDate, Date endDate) throws Exception {
        Set<Long> kanbanIds = new HashSet<>();
        for (UnitInfoEntity unit : units) {
            kanbanIds.addAll(unit.getKanbanIds());
        }

        KanbanTopicSearchCondition condition = new KanbanTopicSearchCondition(KanbanTopicSearchCondition.ContentType.MONTHS_KANBAN);
        condition.setPrimaryKeys(new ArrayList<>(kanbanIds));
        condition.setFromDate(startDate);
        condition.setToDate(endDate);

        List<KanbanTopicInfoEntity> topics = searchKanbanTopic(condition);
        return topics;
    }

    /**
     * 予実データをユニット情報に合わせてに変更する
     *
     * @param topics 予実データ
     * @param month 表示する月
     * @param unit 表示するユニットデータ
     */
    private static CustomAgendaEntity convertAgendaToUnit(List<KanbanTopicInfoEntity> topics, UnitInfoEntity unit, Date startDate, Date endDate, List<DisplayedStatusInfoEntity> statuses) {
        Properties properties = AdProperty.getProperties();
        DisplayStatusSelector displayStatusSelector = new DisplayStatusSelector(statuses);

        CustomAgendaEntity unitAgenda = new CustomAgendaEntity();
        unitAgenda.setUnitId(unit.getUnitId());
        unitAgenda.setTitle1(unit.getUnitName());

        StringBuilder title = new StringBuilder();

        String[] valus = properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_TITLE, "").split(",");
        for (int i = 0; i < valus.length; i++) {
            String value = valus[i];
            Optional<UnitPropertyInfoEntity> optional = unit.getUnitPropertyCollection().stream().filter(o -> StringUtils.equals(o.getUnitPropertyName(), value)).findFirst();
            if (optional.isPresent()) {
                title.append(optional.get().getUnitPropertyValue());
                title.append(TAG_NEW_LINE);
                if (i < valus.length - 1) {
                    title.append(TAG_NEW_LINE);
                }
            }
        }

        if (!title.toString().isEmpty()) {
            unitAgenda.setTitle1(title.toString());
        }

        List<CustomAgendaItemEntity> plans = new ArrayList<>();
        List<CustomAgendaItemEntity> actuals = new ArrayList<>();

        for (KanbanTopicInfoEntity topic : topics) {
            DisplayedStatusInfoEntity status = displayStatusSelector.getKanbanDisplayStatus(topic.getKanbanStatus(), topic.getPlanStartTime(), topic.getPlanEndTime(), topic.getActualStartTime(), topic.getActualEndTime());

            CustomAgendaItemEntity plan = new CustomAgendaItemEntity();
            plan.createKanbanPlanData(topic, status);
            plans.add(plan);

            CustomAgendaItemEntity actual = new CustomAgendaItemEntity();
            actual.createKanbanActualData(topic, status);
            actuals.add(actual);
        }

        createPlan(unitAgenda, plans, startDate, endDate);
        createActual(unitAgenda, actuals, startDate, endDate);

        return unitAgenda;
    }

    /**
     * 予実データをユニット情報に合わせてに変更する
     *
     * @param agendaEntitys 予実データ
     * @param unit 表示するユニットデータ
     */
    private static CustomAgendaEntity convertAgendaToOrganization(List<KanbanTopicInfoEntity> topics, OrganizationInfoEntity organization, Date startDate, Date endDate, List<DisplayedStatusInfoEntity> statuses) {
        DisplayStatusSelector displayStatusSelector = new DisplayStatusSelector(statuses);
        CustomAgendaEntity organizationAgenda = new CustomAgendaEntity();
        if (!topics.isEmpty()) {
            organizationAgenda.setTitle1(organization.getOrganizationName());
            organizationAgenda.setOrganizationId(organization.getOrganizationId());

            // カンバン予実情報を予実モニターに表示する末端の情報に変換
            List<CustomAgendaItemEntity> plans = new ArrayList<>();
            List<CustomAgendaItemEntity> actuals = new ArrayList<>();
            for (KanbanTopicInfoEntity topic : topics) {
                DisplayedStatusInfoEntity status = displayStatusSelector.getKanbanDisplayStatus(topic.getKanbanStatus(),
                        topic.getPlanStartTime(), topic.getPlanEndTime(),
                        topic.getActualStartTime(), topic.getActualEndTime());
                CustomAgendaItemEntity plan = new CustomAgendaItemEntity();
                plan.createOrganizationPlanData(topic, status);
                plans.add(plan);
                CustomAgendaItemEntity actual = new CustomAgendaItemEntity();
                actual.createOrganizationActualData(topic, status);
                actuals.add(actual);
            }
            createPlan(organizationAgenda, plans, startDate, endDate);
            createActual(organizationAgenda, actuals, startDate, endDate);
        }
        return organizationAgenda;
    }

    /**
     * 予実の計画データを作成
     *
     * @param agenda 計画データ
     * @param agendaItems カンバンデータ
     * @param month 作成する月の情報
     */
    private static void createPlan(CustomAgendaEntity agenda, List<CustomAgendaItemEntity> agendaItems, Date startDate, Date endDate) {
        // カンバン予実情報を予実モニターに表示する末端の情報に変換
//        Collections.sort(agendaItems, (CustomAgendaItemEntity o1, CustomAgendaItemEntity o2) -> o1.getStartTime().compareTo(o2.getStartTime()));
        List<DatetimeConcurrent> plans = new ArrayList<>();
        for (CustomAgendaItemEntity agendaItem : agendaItems) {
            if (Objects.isNull(agendaItem.getStartTime()) || Objects.isNull(agendaItem.getEndTIme())) {
                continue;
            }
            if ((agendaItem.getStartTime().before(startDate) && agendaItem.getEndTIme().before(startDate))
                    || agendaItem.getStartTime().after(endDate) && agendaItem.getEndTIme().after(endDate)) {
                continue;
            }
            boolean find = false;
            for (DatetimeConcurrent c : plans) {
                if ((c.getStart().before(agendaItem.getStartTime()) && c.getEnd().after(agendaItem.getStartTime()))
                        || (c.getStart().before(agendaItem.getEndTIme()) && c.getEnd().after(agendaItem.getEndTIme()))
                        || c.getStart().equals(agendaItem.getStartTime()) || c.getEnd().equals(agendaItem.getEndTIme())) {
                    c.getConcurrent().addItem(agendaItem);
                    if (c.getStart().before(agendaItem.getStartTime())) {
                        c.setStart(agendaItem.getStartTime());
                    }
                    if (c.getEnd().before(agendaItem.getEndTIme())) {
                        c.setEnd(agendaItem.getEndTIme());
                    }
                    find = true;
                    break;
                }
            }
            if (find == false) {
                DatetimeConcurrent concurrent = new DatetimeConcurrent(
                        agendaItem.getStartTime(), agendaItem.getEndTIme(), new CustomAgendaConcurrentEntity().addItem(agendaItem));
                plans.add(concurrent);
            }
        }
        for (DatetimeConcurrent c : plans) {
            agenda.addPlan(c.getConcurrent());
        }
    }

    /**
     * 予実の実績データを作成
     *
     * @param agenda 計画データ
     * @param agendaItems 実績データ
     * @param month 作成する月の情報
     */
    private static void createActual(CustomAgendaEntity agenda, List<CustomAgendaItemEntity> agendaItems, Date startDate, Date endDate) {
//        Collections.sort(agendaItems, (CustomAgendaItemEntity o1, CustomAgendaItemEntity o2) -> o1.getStartTime().compareTo(o2.getStartTime()));
        List<DatetimeConcurrent> actual = new ArrayList<>();
        for (CustomAgendaItemEntity agendaItem : agendaItems) {
            if (Objects.isNull(agendaItem.getStartTime()) || Objects.isNull(agendaItem.getEndTIme())) {
                continue;
            }
            if ((agendaItem.getStartTime().before(startDate) && agendaItem.getEndTIme().before(startDate))
                    || agendaItem.getStartTime().after(endDate) && agendaItem.getEndTIme().after(endDate)) {
                continue;
            }
            boolean find = false;
            for (DatetimeConcurrent c : actual) {
                if ((c.getStart().before(agendaItem.getStartTime()) && c.getEnd().after(agendaItem.getStartTime()))
                        || (c.getStart().before(agendaItem.getEndTIme()) && c.getEnd().after(agendaItem.getEndTIme()))
                        || c.getStart().equals(agendaItem.getStartTime()) || c.getEnd().equals(agendaItem.getEndTIme())) {
                    c.getConcurrent().addItem(agendaItem);
                    if (c.getStart().before(agendaItem.getStartTime())) {
                        c.setStart(agendaItem.getStartTime());
                    }
                    if (c.getEnd().before(agendaItem.getEndTIme())) {
                        c.setEnd(agendaItem.getEndTIme());
                    }
                    find = true;
                    break;
                }
            }
            if (find == false) {
                DatetimeConcurrent concurrent = new DatetimeConcurrent(
                        agendaItem.getStartTime(), agendaItem.getEndTIme(), new CustomAgendaConcurrentEntity().addItem(agendaItem));
                actual.add(concurrent);
            }
        }
        for (DatetimeConcurrent c : actual) {
            agenda.addActual(c.getConcurrent());
        }
    }

    /**
     * 指定されたユニットの下位に存在する全てのカンバンを取得する
     *
     * @param parent 親ユニット
     * @return カンバンの関連情報
     */
    private static List<ConUnitAssociateInfoEntity> getUnitAssociateKanbanOnly(UnitInfoEntity parent) {
        List<ConUnitAssociateInfoEntity> kanbanAssociate = new ArrayList<>();
        for (ConUnitAssociateInfoEntity con : parent.getConUnitAssociateCollection()) {
            if (Objects.nonNull(con.getFkKanbanId())) {
                kanbanAssociate.add(con);
            } else if (Objects.nonNull(con.getFkUnitId())) {
                kanbanAssociate.addAll(getUnitAssociateKanbanOnly(UNIT_REST.find(con.getFkUnitId())));
            }
        }

        return kanbanAssociate;
    }

    /**
     * ユニットを検索する
     *
     * @param condition　検索条件
     * @return
     */
    public static List<UnitInfoEntity> searchUnit(UnitSearchCondition condition) {
        List<UnitInfoEntity> result = new ArrayList<>();
        Long count = 0l;
        Long nowCount = 0l;
        Boolean isCount = false;
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::searchUnit start.");
            while (isContinue) {
                try {
                    //ID指定無:最上位階層取得/ID指定有:指定されたID以下階層取得
                    if (!isCount) {
                        count = UNIT_REST.countSearch(condition);
                    }
                    isCount = true;
                    for (; nowCount < count; nowCount += RANGE) {
                        result.addAll(UNIT_REST.findSearch(nowCount, nowCount + RANGE - 1, condition));
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
            LOGGER.info(RestAPI.class.getSimpleName() + "::searchUnit end.");
        }
        return result;
    }

    /**
     * ユニットを検索する
     *
     * @param condition　検索条件
     * @return
     */
    public static List<UnitInfoEntity> basicSearchUnit(UnitSearchCondition condition) {
        List<UnitInfoEntity> result = new ArrayList<>();
        Long count = 0l;
        Long nowCount = 0l;
        Boolean isCount = false;
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::searchUnit start.");
            while (isContinue) {
                try {
                    //ID指定無:最上位階層取得/ID指定有:指定されたID以下階層取得
                    if (!isCount) {
                        count = UNIT_REST.countSearch(condition);
                    }
                    isCount = true;
                    for (; nowCount < count; nowCount += RANGE) {
                        result.addAll(UNIT_REST.findBasicSearch(nowCount, nowCount + RANGE - 1, condition));
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
            LOGGER.info(RestAPI.class.getSimpleName() + "::searchUnit end.");
        }
        return result;
    }

    /**
     * 指定されたIDのユニットテンプレート階層を取得
     *
     * @param id
     * @return
     */
    public static UnitTemplateHierarchyInfoEntity getUnitTemplateHierarchy(Long id) {
        UnitTemplateHierarchyInfoEntity result = new UnitTemplateHierarchyInfoEntity();
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitTemplateHierarchy start.");
            while (isContinue) {
                try {
                    result = UNITTEMPLATE_HIERARCHY_REST.findTree(id);
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitTemplateHierarchy end.");
        }
        return result;
    }

    /**
     * ユニットテンプレート階層取得
     *
     * @param id 親階層ID
     * @param hasChild
     * @return
     */
    public static List<UnitTemplateHierarchyInfoEntity> getUnitTemplateHierarchyChilds(long id, boolean hasChild) {
        List<UnitTemplateHierarchyInfoEntity> result = new ArrayList<>();
        Long count = 0l;
        Long nowCount = 0l;
        Boolean isCount = false;
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitTemplateHierarchyChilds start.");
            while (isContinue) {
                try {
                    //ID指定無:最上位階層取得/ID指定有:指定されたID以下階層取得
                    if (!isCount) {
                        count = UNITTEMPLATE_HIERARCHY_REST.findTreeCount(id);
                    }
                    isCount = true;
                    for (; nowCount < count; nowCount += RANGE) {
                        result.addAll(UNITTEMPLATE_HIERARCHY_REST.findTreeRange(id, nowCount, nowCount + RANGE - 1, hasChild));
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
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitTemplateHierarchyChilds end.");
        }
        return result;
    }

    /**
     * ユニットテンプレート階層 子階層数取得
     *
     * @param id 親階層ID
     * @return
     */
    public static Long getUnitTemplateHierarchyChildsCount(Long id) {
        Long result = 0l;
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitTemplateHierarchyChildsCount start.");
            while (isContinue) {
                try {
                    //ID指定無:最上位階層取得/ID指定有:指定されたID以下階層取得
                    result = UNITTEMPLATE_HIERARCHY_REST.findTreeCount(id);
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitTemplateHierarchyChildsCount end.");
        }
        return result;
    }

    /**
     * ユニットテンプレート階層の登録
     *
     * @param entity 登録するユニットテンプレート階層
     * @return 登録結果
     */
    public static ResponseEntity registUnitTemplateHierarchy(UnitTemplateHierarchyInfoEntity entity) {
        ResponseEntity result = null;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::registHierarchy start.");

            Boolean isContinue = true;

            while (isContinue) {
                try {
                    result = UNITTEMPLATE_HIERARCHY_REST.regist(entity);
                    final ResponseEntity rs = result;
                    Platform.runLater(() -> {
                        ResponseAnalyzer.getAnalyzeResult(rs);
                    });
                    break;
                } catch (Exception ex) {
                    isContinue = DialogBox.alert(ex);
                }
            }
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::registHierarchy end.");
        }

        return result;
    }

    /**
     * ユニットテンプレート階層の更新
     *
     * @param entity 更新するユニットテンプレート階層
     * @return 複製結果
     */
    public static ResponseEntity updateUnitTemplateHierarchy(UnitTemplateHierarchyInfoEntity entity) {
        ResponseEntity result = null;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::registHierarchy start.");

            Boolean isContinue = true;

            while (isContinue) {
                try {
                    result = UNITTEMPLATE_HIERARCHY_REST.update(entity);
                    final ResponseEntity rs = result;
                    Platform.runLater(() -> {
                        ResponseAnalyzer.getAnalyzeResult(rs);
                    });
                    break;
                } catch (Exception ex) {
                    isContinue = DialogBox.alert(ex);
                }
            }
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::registHierarchy end.");
        }

        return result;
    }

    /**
     * ユニットテンプレート階層の削除
     *
     * @param entity 削除するユニットテンプレート階層
     * @return 削除結果
     */
    public static ResponseEntity deleteUnitTemplateHierarchy(UnitTemplateHierarchyInfoEntity entity) {
        ResponseEntity result = null;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::registHierarchy start.");

            Boolean isContinue = true;

            while (isContinue) {
                try {
                    result = UNITTEMPLATE_HIERARCHY_REST.remove(entity);
                    if (result.isSuccess()) {
                        ResponseAnalyzer.getAnalyzeResult(result);
                    }
                    break;
                } catch (Exception ex) {
                    isContinue = DialogBox.alert(ex);
                }
            }
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::registHierarchy end.");
        }

        return result;
    }

    /**
     * ユニットテンプレート階層の登録
     *
     * @param entity 登録するユニットテンプレート階層
     * @return 登録結果
     */
    public static ResponseEntity registUnitTemplate(UnitTemplateInfoEntity entity) {
        ResponseEntity result = null;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::registUnitTemplate start.");

            Boolean isContinue = true;

            while (isContinue) {
                try {
                    result = UNITTEMPLATE_REST.regist(entity);
                    break;
                } catch (Exception ex) {
                    isContinue = DialogBox.alert(ex);
                }
            }
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::registUnitTemplate end.");
        }

        return result;
    }

    /**
     * ユニットテンプレート階層の更新
     *
     * @param entity 更新するユニットテンプレート階層
     * @return 複製結果
     */
    public static ResponseEntity updateUnitTemplate(UnitTemplateInfoEntity entity) {
        ResponseEntity result = null;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::updateUnitTemplate start.");

            Boolean isContinue = true;

            while (isContinue) {
                try {
                    result = UNITTEMPLATE_REST.update(entity);
                    break;
                } catch (Exception ex) {
                    isContinue = DialogBox.alert(ex);
                }
            }
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::updateUnitTemplate end.");
        }

        return result;
    }

    /**
     * ユニットテンプレートの複製
     *
     * @param entity 複製するユニットテンプレート
     * @return 複製結果
     */
    public static ResponseEntity copyUnitTemplate(UnitTemplateInfoEntity entity) {
        ResponseEntity result = null;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::copyUnitTemplate start.");

            Boolean isContinue = true;

            while (isContinue) {
                try {
                    result = UNITTEMPLATE_REST.copy(entity.getUnitTemplateId());
                    break;
                } catch (Exception ex) {
                    isContinue = DialogBox.alert(ex);
                }
            }
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::copyUnitTemplate end.");
        }

        return result;
    }

    /**
     * ユニットテンプレートの削除
     *
     * @param entity 削除するユニットテンプレート
     * @return 削除結果
     */
    public static ResponseEntity deleteUnitTemplate(UnitTemplateInfoEntity entity) {
        ResponseEntity result = null;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::deleteUnitTemplate start.");

            Boolean isContinue = true;

            while (isContinue) {
                try {
                    result = UNITTEMPLATE_REST.remove(entity);
                    break;
                } catch (Exception ex) {
                    isContinue = DialogBox.alert(ex);
                }
            }
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::deleteUnitTemplate end.");
        }

        return result;
    }

    /**
     * 指定されたIDの工程順階層を取得
     *
     * @param id
     * @return
     */
    public static WorkflowHierarchyInfoEntity getWorkflowHierarchy(Long id) {
        WorkflowHierarchyInfoEntity result = new WorkflowHierarchyInfoEntity();
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getWorkflowHierarchy start.");
            while (isContinue) {
                try {
                    result = WORKFLOW_HIERARCHY_REST.find(id);
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getWorkflowHierarchy end.");
        }
        return result;
    }

    /**
     * 工程順階層取得
     *
     * @param id 親階層ID
     * @return
     */
    public static List<WorkflowHierarchyInfoEntity> getWorkflowHierarchyChilds(Long id) {
        List<WorkflowHierarchyInfoEntity> result = new ArrayList<>();
        Long count = 0l;
        Long nowCount = 0l;
        Boolean isCount = false;
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getWorkflowHierarchyChilds start.");
            while (isContinue) {
                try {
                    //ID指定無:最上位階層取得/ID指定有:指定されたID以下階層取得
                    if (Objects.isNull(id)) {
                        if (!isCount) {
                            count = WORKFLOW_HIERARCHY_REST.getTopHierarchyCount();
                        }
                        for (; nowCount < count; nowCount += RANGE) {
                            result.addAll(WORKFLOW_HIERARCHY_REST.getTopHierarchyRange(nowCount, nowCount + RANGE - 1, true));
                        }
                    } else {
                        if (!isCount) {
                            count = WORKFLOW_HIERARCHY_REST.getAffilationHierarchyCount(id);
                        }
                        for (; nowCount < count; nowCount += RANGE) {
                            result.addAll(WORKFLOW_HIERARCHY_REST.getAffilationHierarchyRange(id, nowCount, nowCount + RANGE - 1));
                        }
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
            LOGGER.info(RestAPI.class.getSimpleName() + "::getWorkflowHierarchyChilds end.");
        }
        return result;
    }

    /**
     * 工程順階層 子階層数取得
     *
     * @param id 親階層ID
     * @return
     */
    public static Long getWorkflowHierarchyChildsCount(Long id) {
//        List<WorkflowHierarchyInfoEntity> result = new ArrayList<>();
        Long result = 0l;
//        Long nowCount = 0l;
//        Boolean isCount = false;
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getWorkflowHierarchyChildsCount start.");
            while (isContinue) {
                try {
                    //ID指定無:最上位階層取得/ID指定有:指定されたID以下階層取得
                    if (Objects.isNull(id)) {
                        result = WORKFLOW_HIERARCHY_REST.getTopHierarchyCount();
                    } else {
                        result = WORKFLOW_HIERARCHY_REST.getAffilationHierarchyCount(id);
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
            LOGGER.info(RestAPI.class.getSimpleName() + "::getWorkflowHierarchyChildsCount end.");
        }
        return result;
    }

    /**
     * 指定されたIDのユニットテンプレートを取得
     *
     * @param id 取得するユニットテンプレートのID
     * @return ユニットテンプレート
     */
    public static WorkflowInfoEntity getWorkflow(Long id) {
        WorkflowInfoEntity result = new WorkflowInfoEntity();
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getWorkflow start.");
            while (isContinue) {
                try {
                    result = WORKFLOW_REST.find(id);
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getWorkflow end.");
        }
        return result;
    }

    /**
     * 指定されたIDの工程順のタクトタイムを取得 *スタンダード側をラップした特殊レストなのでほかに公開しない
     *
     * @param id タクトタイムを取得する工程順
     * @return タクトタイム
     */
    public static Long getWorkflowTactTime(Long id) {
        Long result = 0l;
        Boolean isContinue = true;
        RestClient restClient = new RestClient(new AdFactoryForFujiClientAppConfig().getAdFactoryForFujiServerAddress());
        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getWorkflowTactTime start.");
            while (isContinue) {
                try {
                    StringBuilder path = new StringBuilder();
                    path.append(RestConstants.PATH_WORKFLOW);
                    path.append(RestConstants.PATH_TACTTIME);
                    path.append(String.format(RestConstants.PATH_ID_TARGET, id.toString()));
                    String count = (String) restClient.find(path.toString(), MediaType.TEXT_PLAIN_TYPE, String.class);
                    result = Long.parseLong(count);
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getWorkflowTactTime end.");
        }
        return result;
    }

    /**
     * 工程階層取得
     *
     * @param id
     * @return
     */
    public static List<WorkHierarchyInfoEntity> getWorkHierarchy(Long id) {
        List<WorkHierarchyInfoEntity> result = new ArrayList<>();
        Long count = 0l;
        Long nowCount = 0l;
        Boolean isCount = false;
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getWorkHierarchy start.");
            while (isContinue) {
                try {
                    //ID指定無:最上位階層取得/ID指定有:指定されたID以下階層取得
                    if (Objects.isNull(id)) {
                        if (!isCount) {
                            count = WORK_HIERARCHY_REST.getTopHierarchyCount();
                        }
                        for (; nowCount < count; nowCount += RANGE) {
                            result.addAll(WORK_HIERARCHY_REST.getTopHierarchyRange(nowCount, nowCount + RANGE - 1, true, false));
                        }
                    } else {
                        if (!isCount) {
                            count = WORK_HIERARCHY_REST.getAffilationHierarchyCount(id);
                        }
                        for (; nowCount < count; nowCount += RANGE) {
                            result.addAll(WORK_HIERARCHY_REST.getAffilationHierarchyRange(id, nowCount, nowCount + RANGE - 1, true, false));
                        }
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
            LOGGER.info(RestAPI.class.getSimpleName() + "::getWorkHierarchy end.");
        }
        return result;
    }

    /**
     * 工程階層の子階層数取得
     *
     * @param id
     * @return
     */
    public static Long getWorkHierarchyCount(Long id) {
        Long result = 0l;
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getWorkHierarchyCount start.");
            while (isContinue) {
                try {
                    //ID指定無:最上位階層取得/ID指定有:指定されたID以下階層数取得
                    if (Objects.isNull(id)) {
                        result = WORK_HIERARCHY_REST.getTopHierarchyCount();
                    } else {
                        result = WORK_HIERARCHY_REST.getAffilationHierarchyCount(id);
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
            LOGGER.info(RestAPI.class.getSimpleName() + "::getWorkHierarchyCount end.");
        }
        return result;
    }

    /**
     * 指定されたIDのユニットテンプレートを取得
     *
     * @param id 取得するユニットテンプレートのID
     * @return ユニットテンプレート
     */
    public static UnitTemplateInfoEntity getUnitTemplate(Long id) {
        UnitTemplateInfoEntity result = new UnitTemplateInfoEntity();
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitTemplate start.");
            while (isContinue) {
                try {
                    result = UNITTEMPLATE_REST.find(id);
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitTemplate end.");
        }
        return result;
    }

    /**
     * 指定された名称のユニットテンプレートを取得
     *
     * @param name 取得するユニットテンプレート名
     * @return ユニットテンプレート
     */
    public static UnitTemplateInfoEntity getUnitTemplateByName(String name) {
        UnitTemplateInfoEntity result = new UnitTemplateInfoEntity();
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitTemplateByName start.");
            while (isContinue) {
                try {
                    result = UNITTEMPLATE_REST.findName(name);
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitTemplateByName end.");
        }
        return result;
    }

    /**
     * 生産ユニットテンプレートを取得する
     *
     * @param unitTemplateIds
     * @return
     */
    public static List<UnitTemplateInfoEntity> getUnitTemplate(List<Long> unitTemplateIds) {
        List<UnitTemplateInfoEntity> result = null;
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitTemplate start.");
            while (isContinue) {
                try {
                    result = UNITTEMPLATE_REST.find(unitTemplateIds);
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
            result = new ArrayList<>();
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitTemplate end.");
        }
        return result;
    }

    /**
     * 指定されたIDの工程順のタクトタイムを取得 *スタンダード側をラップした特殊レストなのでほかに公開しない
     *
     * @param id タクトタイムを取得する工程順
     * @return タクトタイム
     */
    public static Long getUnitTemplateTactTime(Long id) {
        Long result = 0l;
        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitTemplateTactTime start.");
            UnitTemplateInfoEntity entity = getUnitTemplate(id);
            if (Objects.nonNull(entity.getConUnitTemplateAssociateCollection()) && !entity.getConUnitTemplateAssociateCollection().isEmpty()) {
                if (entity.getConUnitTemplateAssociateCollection().size() == 1) {
                    result = entity.getConUnitTemplateAssociateCollection().get(0).getTaktTime();
                } else if (entity.getConUnitTemplateAssociateCollection().size() > 1) {
                    Long min = entity.getConUnitTemplateAssociateCollection().get(0).getStandardStartTime().getTime();
                    Long max = entity.getConUnitTemplateAssociateCollection().get(0).getStandardEndTime().getTime();
                    for (ConUnitTemplateAssociateInfoEntity con : entity.getConUnitTemplateAssociateCollection()) {
                        if (min > con.getStandardStartTime().getTime()) {
                            min = con.getStandardStartTime().getTime();
                        }
                        if (max < con.getStandardEndTime().getTime()) {
                            max = con.getStandardEndTime().getTime();
                        }
                    }
                    result = max - min;
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitTemplateTactTime end.");
        }
        return result;
    }

    /**
     * 指定されたIDのユニット階層を取得
     *
     * @param id
     * @return
     */
    public static UnitHierarchyInfoEntity getUnitHierarchy(Long id) {
        UnitHierarchyInfoEntity result = new UnitHierarchyInfoEntity();
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitHierarchy start.");
            while (isContinue) {
                try {
                    result = UNIT_HIERARCHY_REST.findTree(id);
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitHierarchy end.");
        }
        return result;
    }

    /**
     * 指定された名称のユニット階層を取得
     *
     * @param name
     * @return
     */
    public static UnitHierarchyInfoEntity getUnitHierarchyByName(String name) {
        UnitHierarchyInfoEntity result = new UnitHierarchyInfoEntity();
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitHierarchyByName start.");
            while (isContinue) {
                try {
                    result = UNIT_HIERARCHY_REST.findHierarchyName(name);
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitHierarchyByName end.");
        }
        return result;
    }

    /**
     * ユニット階層取得
     *
     * @param id 親階層ID
     * @param hasChild 階下のユニットを取得するかどうか
     * @return
     */
    public static List<UnitHierarchyInfoEntity> getUnitHierarchyChilds(Long id, Boolean hasChild) {
        List<UnitHierarchyInfoEntity> result = new ArrayList<>();
        Long count = 0l;
        Long nowCount = 0l;
        Boolean isCount = false;
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitHierarchyChilds start.");
            while (isContinue) {
                try {
                    //ID指定無:最上位階層取得/ID指定有:指定されたID以下階層取得
                    if (!isCount) {
                        count = UNIT_HIERARCHY_REST.findTreeCount(id);
                    }
                    isCount = true;
                    for (; nowCount < count; nowCount += RANGE) {
                        result.addAll(UNIT_HIERARCHY_REST.findTreeRange(id, nowCount, nowCount + RANGE - 1, hasChild));
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
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitHierarchyChilds end.");
        }
        return result;
    }

    /**
     * ユニット階層の登録
     *
     * @param entity 登録するユニット階層
     * @return 登録結果
     */
    public static ResponseEntity registUnitHierarchy(UnitHierarchyInfoEntity entity) {
        ResponseEntity result = null;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::registHierarchy start.");

            Boolean isContinue = true;

            while (isContinue) {
                try {
                    result = UNIT_HIERARCHY_REST.regist(entity);
                    final ResponseEntity rs = result;
                    Platform.runLater(() -> {
                        ResponseAnalyzer.getAnalyzeResult(rs);
                    });
                    break;
                } catch (Exception ex) {
                    isContinue = DialogBox.alert(ex);
                }
            }
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::registHierarchy end.");
        }

        return result;
    }

    /**
     * ユニット階層の更新
     *
     * @param entity 更新するユニット階層
     * @return 複製結果
     */
    public static ResponseEntity updateUnitHierarchy(UnitHierarchyInfoEntity entity) {
        ResponseEntity result = null;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::registHierarchy start.");

            Boolean isContinue = true;

            while (isContinue) {
                try {
                    result = UNIT_HIERARCHY_REST.update(entity);
                    final ResponseEntity rs = result;
                    Platform.runLater(() -> {
                        ResponseAnalyzer.getAnalyzeResult(rs);
                    });
                    break;
                } catch (Exception ex) {
                    isContinue = DialogBox.alert(ex);
                }
            }
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::registHierarchy end.");
        }

        return result;
    }

    /**
     * ユニット階層の削除
     *
     * @param entity 削除するユニット階層
     * @return 削除結果
     */
    public static ResponseEntity deleteUnitHierarchy(UnitHierarchyInfoEntity entity) {
        ResponseEntity result = null;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::registHierarchy start.");

            Boolean isContinue = true;

            while (isContinue) {
                try {
                    result = UNIT_HIERARCHY_REST.remove(entity);
                    final ResponseEntity rs = result;
                    Platform.runLater(() -> {
                        ResponseAnalyzer.getAnalyzeResult(rs);
                    });
                    break;
                } catch (Exception ex) {
                    isContinue = DialogBox.alert(ex);
                }
            }
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::registHierarchy end.");
        }

        return result;
    }

    /**
     * 指定されたIDのユニットを取得
     *
     * @param id 取得するユニットのID
     * @return ユニット
     */
    public static UnitInfoEntity getUnit(Long id) {
        UnitInfoEntity result = new UnitInfoEntity();
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnit start.");
            while (isContinue) {
                try {
                    result = UNIT_REST.find(id);
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnit end.");
        }
        return result;
    }

    /**
     * 指定された名称のユニットを取得
     *
     * @param name 取得するユニットの名称
     * @return ユニット
     */
    public static UnitInfoEntity getUnitByName(String name) {
        UnitInfoEntity result = new UnitInfoEntity();
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitByName start.");
            while (isContinue) {
                try {
                    result = UNIT_REST.find(name);
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitByName end.");
        }
        return result;
    }

    /**
     * URIからユニットを取得
     *
     * @param uri 取得するユニットのURI
     * @return ユニット
     */
    public static UnitInfoEntity getUnitUri(String uri) {
        UnitInfoEntity result = new UnitInfoEntity();
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitUri start.");
            while (isContinue) {
                try {
                    result = UNIT_REST.findURI(uri);
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitUri end.");
        }
        return result;
    }

    /**
     * 指定されたIDのステータスを取得
     *
     * @param id 取得するユニットのID
     * @return ユニット
     */
    public static KanbanStatusEnum getUnitStatus(Long id) {
        KanbanStatusEnum result = KanbanStatusEnum.PLANNING;
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnit start.");
            while (isContinue) {
                try {
                    result = UNIT_REST.getStatus(id);
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnit end.");
        }
        return result;
    }

    /**
     * ユニットの登録
     *
     * @param entity 登録するユニット
     * @return 登録結果
     */
    public static ResponseEntity registUnit(UnitInfoEntity entity) {
        ResponseEntity result = null;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::registUnit start.");

            Boolean isContinue = true;

            while (isContinue) {
                try {
                    result = UNIT_REST.regist(entity);
                    if (!result.isSuccess()) {
                        final ResponseEntity rs = result;
                        Platform.runLater(() -> {
                            ResponseAnalyzer.getAnalyzeResult(rs);
                        });
                    }
                    break;
                } catch (Exception ex) {
                    isContinue = DialogBox.alert(ex);
                }
            }
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::registUnit end.");
        }

        return result;
    }

    /**
     * ユニットの更新
     *
     * @param entity 更新するユニット
     * @return 複製結果
     */
    public static ResponseEntity updateUnit(UnitInfoEntity entity) {
        ResponseEntity result = null;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::updateUnit start.");

            Boolean isContinue = true;

            while (isContinue) {
                try {
                    result = UNIT_REST.update(entity);
                    if (!result.isSuccess()) {
                        final ResponseEntity rs = result;
                        Platform.runLater(() -> {
                            ResponseAnalyzer.getAnalyzeResult(rs);
                        });
                    }
                    break;
                } catch (Exception ex) {
                    isContinue = DialogBox.alert(ex);
                }
            }
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::updateUnit end.");
        }

        return result;
    }

    /**
     * ユニットの削除
     *
     * @param entity 削除するユニット
     * @return 削除結果
     */
    public static ResponseEntity deleteUnit(UnitInfoEntity entity) {
        ResponseEntity result = null;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::deleteUnit start.");

            Boolean isContinue = true;

            while (isContinue) {
                try {
                    result = UNIT_REST.remove(entity);
                    if (!result.isSuccess()) {
                        final ResponseEntity rs = result;
                        Platform.runLater(() -> {
                            ResponseAnalyzer.getAnalyzeResult(rs);
                        });
                    }
                    break;
                } catch (Exception ex) {
                    isContinue = DialogBox.alert(ex);
                }
            }
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::deleteUnit end.");
        }

        return result;
    }

    /**
     * 指定されたIDのカンバンを取得
     *
     * @param id カンバンID
     * @return
     */
    public static KanbanInfoEntity getKanban(Long id) {
        KanbanInfoEntity result = new KanbanInfoEntity();
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getKanban start.");
            while (isContinue) {
                try {
                    result = KANBAN_REST.find(id);
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getKanban end.");
        }
        return result;
    }

    /**
     * 指定された名前のカンバンを取得
     *
     * @param name カンバン名
     * @return
     */
    public static KanbanInfoEntity getKanban(String name) {
        KanbanInfoEntity result = new KanbanInfoEntity();
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getKanban start.");
            while (isContinue) {
                try {
                    result = KANBAN_REST.findName(name);
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getKanban end.");
        }
        return result;
    }

    /**
     * カンバン情報を取得する。
     *
     * @param kanbanIds
     * @return
     */
    public static List<UnitKanbanInfoEntity> getUnitKanbans(List<Long> kanbanIds) {
        List<UnitKanbanInfoEntity> result = new ArrayList<>();
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitKanbans start.");
            while (isContinue) {
                try {
                    result = UNIT_REST.findUnitKanbans(kanbanIds);
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitKanbans end.");
        }
        return result;
    }

    /**
     * 工程カンバン取得
     *
     * @param id 親カンバンID
     * @return
     */
    public static List<WorkKanbanInfoEntity> getWorkKanbans(Long id) {
        List<WorkKanbanInfoEntity> result = new ArrayList<>();
        Long count = 0l;
        Long nowCount = 0l;
        Boolean isCount = false;
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getWorkKanbans start.");
            while (isContinue) {
                try {
                    //ID指定無:最上位階層取得/ID指定有:指定されたID以下階層取得
                    if (!isCount) {
                        count = WORK_KANBAN_REST.countFlow(id);
                    }
                    isCount = true;
                    for (; nowCount < count; nowCount += RANGE) {
                        result.addAll(WORK_KANBAN_REST.getRangeFlow(nowCount, nowCount + RANGE - 1, id));
                    }
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                } finally {
                    LOGGER.info(RestAPI.class.getSimpleName() + "::getWorkKanbans end.");
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        }
        return result;
    }

    /**
     * 追加工程カンバン取得
     *
     * @param id 親カンバンID
     * @return
     */
    public static List<WorkKanbanInfoEntity> getSeparateWorkKanbans(Long id) {
        List<WorkKanbanInfoEntity> result = new ArrayList<>();
        Long count = 0l;
        Long nowCount = 0l;
        Boolean isCount = false;
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getSeparateWorkKanbans start.");
            while (isContinue) {
                try {
                    //ID指定無:最上位階層取得/ID指定有:指定されたID以下階層取得
                    if (!isCount) {
                        count = WORK_KANBAN_REST.countSeparate(id);
                    }
                    isCount = true;
                    for (; nowCount < count; nowCount += RANGE) {
                        result.addAll(WORK_KANBAN_REST.getRangeSeparate(nowCount, nowCount + RANGE - 1, id));
                    }
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                } finally {
                    LOGGER.info(RestAPI.class.getSimpleName() + "::getSeparateWorkKanbans end.");
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        }
        return result;
    }

    /**
     * カンバンの更新
     *
     * @param entity 更新するカンバン
     * @return 複製結果
     */
    public static ResponseEntity updateKanban(KanbanInfoEntity entity) {
        ResponseEntity result = null;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::updateKanban start.");

            Boolean isContinue = true;

            while (isContinue) {
                try {
                    result = KANBAN_REST.update(entity);
                    if (!result.isSuccess()) {
                        final ResponseEntity rs = result;
                        Platform.runLater(() -> {
                            ResponseAnalyzer.getAnalyzeResult(rs);
                        });
                    }
                    break;
                } catch (Exception ex) {
                    isContinue = DialogBox.alert(ex);
                }
            }
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::updateKanban end.");
            result = new ResponseEntity().errorType(ServerErrorTypeEnum.SERVER_FETAL);
        }

        return result;
    }

    /**
     * 実績情報検索
     *
     * @param condition
     * @return
     */
    public static List<ActualResultEntity> searchActualResult(ActualSearchCondition condition) {
        List<ActualResultEntity> result = new ArrayList<>();
        Long count = 0l;
        Long nowCount = 0l;
        Boolean isCount = false;
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::searchActualResult start.");
            while (isContinue) {
                try {
                    //ID指定無:最上位階層取得/ID指定有:指定されたID以下階層取得
                    if (!isCount) {
                        count = ACTUAL_RESULT_REST.searchCount(condition);
                    }
                    for (; nowCount < count; nowCount += ACTUAL_RANGE) {
                        result.addAll(ACTUAL_RESULT_REST.searchRange(condition, nowCount, nowCount + ACTUAL_RANGE - 1));
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
            LOGGER.info(RestAPI.class.getSimpleName() + "::searchActualResult end.");
        }
        return result;
    }

    /**
     * 予実情報の取得
     *
     * @param condition
     * @return
     */
    public static List<KanbanTopicInfoEntity> searchKanbanTopic(KanbanTopicSearchCondition condition) {
        List<KanbanTopicInfoEntity> result = null;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::searchKanbanTopic start.");

            Boolean isContinue = true;
            while (isContinue) {
                try {
                    result = agendRest.findTopic(condition);
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }

        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::searchKanbanTopic end.");
        }

        return Objects.nonNull(result) ? result : new ArrayList<>();
    }

    /**
     * 指定されたIDの作業者を取得
     *
     * @param id
     * @return
     */
    public static OrganizationInfoEntity getOrganization(Long id) {
        OrganizationInfoEntity result = new OrganizationInfoEntity();
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getOrganization start.");
            while (isContinue) {
                try {
                    result = organizationRest.find(id);
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getOrganization end.");
        }
        return result;
    }

    /**
     * 全ての組織情報を取得
     *
     * @return
     */
    public static List<OrganizationInfoEntity> getOrganizations() {
        List<OrganizationInfoEntity> result = new ArrayList<>();
        Long count = 0l;
        Long nowCount = 0l;
        Boolean isCount = false;
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getOrganizations start.");
            while (isContinue) {
                try {
                    //ID指定無:最上位階層取得/ID指定有:指定されたID以下階層取得
                    if (!isCount) {
                        count = organizationRest.count();
                    }
                    isCount = true;
                    for (; nowCount < count; nowCount += RANGE) {
                        result.addAll(organizationRest.findRange(nowCount, nowCount + RANGE - 1));
                    }
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                } finally {
                    LOGGER.info(RestAPI.class.getSimpleName() + "::getOrganizations end.");
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        }
        return result;
    }

    /**
     * 指定されたIDの作業者端末を取得
     *
     * @param id
     * @return
     */
    public static EquipmentInfoEntity getEquipment(Long id) {
        EquipmentInfoEntity result = new EquipmentInfoEntity();
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getEquipment start.");
            while (isContinue) {
                try {
                    result = EQUIPMENT_REST.find(id);
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getEquipment end.");
        }
        return result;
    }

    /**
     * 全ての設備情報を取得
     *
     * @return
     */
    public static List<EquipmentInfoEntity> getEquipments() {
        List<EquipmentInfoEntity> result = new ArrayList<>();
        Long count = 0l;
        Long nowCount = 0l;
        Boolean isCount = false;
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getEquipments start.");
            while (isContinue) {
                try {
                    //ID指定無:最上位階層取得/ID指定有:指定されたID以下階層取得
                    if (!isCount) {
                        count = EQUIPMENT_REST.count();
                    }
                    isCount = true;
                    for (; nowCount < count; nowCount += RANGE) {
                        result.addAll(EQUIPMENT_REST.findRange(nowCount, nowCount + RANGE - 1));
                    }
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                } finally {
                    LOGGER.info(RestAPI.class.getSimpleName() + "::getEquipments end.");
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        }
        return result;
    }

    /**
     * 指定されたIDの休憩を取得
     *
     * @param id
     * @return
     */
    public static BreakTimeInfoEntity getBreakTime(Long id) {
        BreakTimeInfoEntity result = new BreakTimeInfoEntity();
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getBreakTime start.");
            while (isContinue) {
                try {
                    result = BREAKTIME_REST.find(id);
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getBreakTime end.");
        }
        return result;
    }

    /**
     * 遅延理由取得
     *
     * @return
     */
    public static List<DelayReasonInfoEntity> getDelayReasons() {
        List<DelayReasonInfoEntity> result = new ArrayList<>();
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getDelayReasons start.");
            while (isContinue) {
                try {
                    result = DELAY_REASON_REST.findAll();
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getDelayReasons end.");
        }
        return result;
    }

    /**
     * 表示ステータスの取得
     *
     * @return
     */
    public static List<DisplayedStatusInfoEntity> getDisplayedStatuses() {
        List<DisplayedStatusInfoEntity> result = new ArrayList<>();
        Boolean isContinue = true;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getDisplayedStatus start.");
            while (isContinue) {
                try {
                    result = displayedStatusRest.findAll();
                    break;
                } catch (Exception ex) {
                    LOGGER.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getDisplayedStatus end.");
        }
        return result;
    }

   /**
     * ユニットを検索する
     *
     * @param hierarchyId 階層ID
     * @return ユニット情報リスト
     */
    public static List<UnitInfoEntity> getUnitByHierarchyId(long hierarchyId) {
        return getUnitByHierarchyId(hierarchyId, true);
    }

    /**
     * ユニットを検索する
     *
     * @param hierarchyId 階層ID
     * @param isAll すべて取得？ (true:すべて, false:未完了のみ)
     * @return ユニット情報リスト
     */
    public static List<UnitInfoEntity> getUnitByHierarchyId(long hierarchyId, boolean isAll) {
        List<UnitInfoEntity> result = new ArrayList<>();
        boolean isContinue = true;
        long from = 0;
        long restNum = 1000;// 1回のRESTで取得する数
        try {
            while (isContinue) {
                // ユニット階層に属するユニット情報を取得する。
                List<UnitInfoEntity> units = UNIT_HIERARCHY_REST.findUnitRange(hierarchyId, from, from + restNum - 1, isAll);
                if (units.isEmpty()) {
                    break;
                }

                result.addAll(units);

                if (units.size() < restNum) {
                    break;
                }

                from += restNum;
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitByHierarchyId end.");
        }
        return result;
    }

    public static List<UnitTemplateInfoEntity> getUnitTemplateByHierarchyId(long hierarchyId) {
        List<UnitTemplateInfoEntity> result = new ArrayList<>();
        boolean isContinue = true;
        long from = 0;
        long count = -1;

        try {
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitByHierarchyId start.");
            while (isContinue) {
                try {
                    if (count < 0) {
                        count = UNITTEMPLATE_HIERARCHY_REST.countUnitTemplate(hierarchyId);
                    }
                    for (; from < count; from += 1000) {
                        result.addAll(UNITTEMPLATE_HIERARCHY_REST.findUnitTemplateRange(hierarchyId, from, from + 1000 - 1));
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
            LOGGER.info(RestAPI.class.getSimpleName() + "::getUnitByHierarchyId end.");
        }
        return result;
    }
}
