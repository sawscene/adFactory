/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * AND open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import jakarta.ejb.EJB;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TemporalType;
import jakarta.ws.rs.*;
import jp.adtekfuji.adFactory.entity.agenda.AgendaConcurrentEntity;
import jp.adtekfuji.adFactory.entity.agenda.AgendaEntity;
import jp.adtekfuji.adFactory.entity.agenda.AgendaItemEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.search.ActualSearchCondition;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.entity.search.KanbanTopicSearchCondition;
import jp.adtekfuji.adFactory.enumerate.ActualResultDailyEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.LightPatternEnum;
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;
import jp.adtekfuji.adfactoryserver.entity.actual.ActualResultEntity;
import jp.adtekfuji.adfactoryserver.entity.agenda.AbstractTopicEntity;
import jp.adtekfuji.adfactoryserver.entity.agenda.ActualProductEntity;
import jp.adtekfuji.adfactoryserver.entity.agenda.DatetimeConcurrent;
import jp.adtekfuji.adfactoryserver.entity.agenda.KanbanTopicEntity;
import jp.adtekfuji.adfactoryserver.entity.agenda.WorkKanbanTopicEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.WorkKanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.master.BreaktimeEntity;
import jp.adtekfuji.adfactoryserver.entity.master.DisplayedStatusEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;
import jp.adtekfuji.adfactoryserver.utility.CalcWorkKanbanDelayTime;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import jp.adtekfuji.adfactoryserver.utility.WorkKanbanTimeData;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * アジェンダ情報REST
 *
 * @author ke.yokoi
 */
@Stateless
@Path("agenda")
public class AgendaFacadeREST {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    @EJB
    private KanbanEntityFacadeREST kanbanRest;

    @EJB
    private ActualResultEntityFacadeREST actualResultRest;

    @EJB
    private EquipmentEntityFacadeREST equipmentRest;

    @EJB
    private OrganizationEntityFacadeREST organizationRest;

    @EJB
    private WorkKanbanEntityFacadeREST workKanbanRest;
    @EJB
    private WorkEntityFacadeREST workRest;
    @EJB
    private DisplayedStatusEntityFacadeREST statusRest;
    @EJB
    private BreaktimeEntityFacadeREST breaktimeRest;

    private final Logger logger = LogManager.getLogger();

    private Map<StatusPatternEnum, DisplayedStatusEntity> statuses = new HashMap<>();

    /**
     * 検索範囲タイプ
     */
    private static enum TermType {
        PLAN,      // 計画日時を対象とする
        ACTUAL;    // 実績日時を対象とする
    }

    /**
     * コンストラクタ
     */
    public AgendaFacadeREST() {
    }
    
    /**
     * カンバン別計画実績情報一覧の件数を取得する。
     *
     * @param condition 検索条件
     * @param authId 認証ID
     * @return 件数
     */
    @Lock(LockType.READ)
    @PUT
    @Path("topic/count")
    @Consumes({"application/xml", "application/json"})
    @Produces({"text/plain"})
    @ExecutionTimeLogging
    public String countTopic(KanbanTopicSearchCondition condition, @QueryParam("authId") Long authId) {
        logger.info("countTopic: {}, authId={}", condition, authId);
        try {
            Long loginUserId = null;
            long count = 0;
            Query query;

            switch (condition.getContentType()) {
                case DAYS_KANBAN:// 日別カンバン計画実績
                    this.em.getEntityManagerFactory().getCache().evict(WorkKanbanTopicEntity.class);

                    // カンバンID一覧・日時範囲を指定して、計画実績情報の件数を取得する。
                    query = this.em.createNamedQuery("WorkKanbanTopicEntity.countByKanbanIdTerm", WorkKanbanTopicEntity.class);
                    query.setParameter("kanbanIds", condition.getPrimaryKeys());
                    query.setParameter("fromDate", condition.getFromDate(), TemporalType.TIMESTAMP);
                    query.setParameter("toDate", condition.getToDate(), TemporalType.TIMESTAMP);

                    count = (long) query.getSingleResult();
                    break;

                case MONTHS_KANBAN:// 月別カンバン計画実績
                    this.em.getEntityManagerFactory().getCache().evict(KanbanTopicEntity.class);

                    // カンバンID一覧を指定して、計画実績情報の件数を取得する。
                    query = this.em.createNamedQuery("KanbanTopicEntity.countByKanbanId");
                    query.setParameter("kanbanIds", condition.getPrimaryKeys());

                    count = (long) query.getSingleResult();
                    break;

                case DAYS_ORGANIZATION:// 日別作業者計画実績
                    this.em.getEntityManagerFactory().getCache().evict(WorkKanbanTopicEntity.class);

                    if (Objects.nonNull(condition.isWithParents()) && condition.isWithParents()) {
                        // 親キーも含める
                        Set<Long> organizationIds = new HashSet();

                        for (Long organizationId : condition.getPrimaryKeys()) {
                            List<Long> parentIds = this.organizationRest.getOrganizationParentPerpetuity(organizationId);

                            organizationIds.clear();
                            organizationIds.add(organizationId);
                            organizationIds.addAll(parentIds);

                            // 組織ID一覧・日時範囲を指定して、計画実績情報の件数を取得する。
                            query = this.em.createNamedQuery("WorkKanbanTopicEntity.countByOrganizationIdTerm");
                            query.setParameter("organizationIds", organizationIds);
                            query.setParameter("fromDate", condition.getFromDate(), TemporalType.TIMESTAMP);
                            query.setParameter("toDate", condition.getToDate(), TemporalType.TIMESTAMP);

                            count += (long) query.getSingleResult();

                            // 永続化コンテキストをdetached状態にしないと、処理を継続できない
                            this.em.clear();
                        }

                    } else {
                        // 親キーを含めない

                        // 組織ID一覧・計画日時範囲を指定して、計画実績情報の件数を取得する。
                        query = this.em.createNamedQuery("WorkKanbanTopicEntity.countByOrganizationIdTerm");
                        query.setParameter("organizationIds", condition.getPrimaryKeys());
                        query.setParameter("fromDate", condition.getFromDate(), TemporalType.TIMESTAMP);
                        query.setParameter("toDate", condition.getToDate(), TemporalType.TIMESTAMP);

                        count = (long) query.getSingleResult();
                    }
                    break;

                case DAYS_LINE:// ライン別カンバン計画実績
                    Set<Long> set = new HashSet();
                    for (Long equimplentId : condition.getPrimaryKeys()) {
                        set.addAll(this.equipmentRest.getChild(equimplentId, loginUserId));
                    }

                    List<Long> equipmentIds = new ArrayList(set);
                    List<KanbanStatusEnum> kanbanStatuses = Arrays.asList(KanbanStatusEnum.PLANNED, KanbanStatusEnum.WORKING, KanbanStatusEnum.SUSPEND, KanbanStatusEnum.COMPLETION);
                    Set<Long> kanbanIds = new HashSet(this.kanbanRest.find(null, equipmentIds, condition.getModelName(), kanbanStatuses, condition.getFromDate(), condition.getToDate()));
                    kanbanIds.addAll(this.actualResultRest.findKanban(equipmentIds, condition.getModelName(), condition.getFromDate(), condition.getToDate()));

                    if (!kanbanIds.isEmpty()) {
                        this.em.getEntityManagerFactory().getCache().evict(WorkKanbanTopicEntity.class);

                        // カンバンID一覧を指定して、計画実績情報一覧を取得する。
                        query = this.em.createNamedQuery("WorkKanbanTopicEntity.countByKanbanId", WorkKanbanTopicEntity.class);
                        query.setParameter("kanbanIds", new ArrayList<>(kanbanIds));

                        count = (long) query.getSingleResult();
                    }
                    break;

                default:
                    throw new IllegalArgumentException();
            }

            return String.valueOf(count);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return "0";
        } finally {
            logger.info("countTopic end.");
        }
    }
        
    /**
     * カンバン別計画実績情報一覧を取得する。
     *
     * @param condition 検索条件
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 計画実績情報一覧
     */
    @Lock(LockType.READ)
    @PUT
    @Path("/topic/search/range")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<AbstractTopicEntity> searchTopic(KanbanTopicSearchCondition condition, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("searchTopic: {}, from={}, to={}, authId={}", condition, from, to, authId);
        List<AbstractTopicEntity> result = this.searchTopicMain(condition, from, to, authId, TermType.PLAN);
        logger.info("searchTopic end.");
        return result;
    }
    
    /**
     * 実績日時範囲を指定してカンバン別計画実績情報一覧を取得する。
     *
     * @param condition 検索条件
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 計画実績情報一覧
     */
    @Lock(LockType.READ)
    @PUT
    @Path("/topic/actual/search/range")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<AbstractTopicEntity> searchActualTopic(KanbanTopicSearchCondition condition, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("searchActualTopic: {}, from={}, to={}, authId={}", condition, from, to, authId);
        List<AbstractTopicEntity> result = this.searchTopicMain(condition, from, to, authId, TermType.ACTUAL);
        logger.info("searchActualTopic end.");
        return result;
    }

    /**
     *
     * @param ids カンバン階層ID一覧
     * @param from 開始期間
     * @param to 完了期間
     * @param authId 認証ID
     * @return 表示データ
     */
    @Lock(LockType.READ)
    @GET
    @Path("/topic/actualProduction/range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<ActualProductEntity> findActualProduction(@QueryParam("id") List<Long> ids, @QueryParam("nowDate") String now, @QueryParam("displayPeriod") Long displayPeriod, @QueryParam("fromDate") String from, @QueryParam("toDate") String to, @QueryParam("authId") Long authId) {
        logger.info("findActualProduction: ids={}, now={}, displayPeriod={}, authId={}", ids, now, displayPeriod, authId);

        if (ids.isEmpty() || Objects.isNull(now) || Objects.isNull(displayPeriod)) {
            return new ArrayList<>();
        }

        try {
            // タグ名一覧をSQLパラメータ用の配列に変換する。
            java.sql.Array idArray = this.em.unwrap(Connection.class).createArrayOf("integer", ids.toArray());

            // 現在の日時
            final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            Date nowDate = df.parse(now);

            // 描画最終日
            Date toDate = df.parse(to);
            Date fromDate = df.parse(from);

            // 完了時に非表示にする期限
            Calendar cal = Calendar.getInstance();
            cal.setTime(DateUtils.truncate(nowDate, Calendar.DAY_OF_MONTH));
            cal.add(Calendar.DAY_OF_MONTH, -displayPeriod.intValue()+1);
            Date displayPeriodDate = cal.getTime();

            // 注文番号を指定して、残り台数を取得する。
            final Query query = em
                    .createNamedQuery("ActualProductEntity.find", ActualProductEntity.class)
                    .setParameter(1, displayPeriodDate)
                    .setParameter(2, nowDate)
                    .setParameter(3, idArray)
                    .setParameter(4, toDate)
                    .setParameter(5, fromDate);

            final List<ActualProductEntity> actualProductEntities = query.getResultList();
            return actualProductEntities;
        } catch (ParseException | SQLException ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }


    /**
     * カンバン別計画実績情報一覧を取得する。
     *
     * @param condition 検索条件
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @param termType 検索範囲のタイプ
     * @return 計画実績情報一覧
     */
    private List<AbstractTopicEntity> searchTopicMain(KanbanTopicSearchCondition condition, Integer from, Integer to, Long authId, TermType termType) {
        logger.info("searchTopicMain: {}, from={}, to={}, authId={}, termType={}", condition, from, to, authId, termType);
        try {
            Long loginUserId = null;

            if (Objects.isNull(condition.getPrimaryKeys()) || condition.getPrimaryKeys().isEmpty()) {
                return new ArrayList();
            }

            List<AbstractTopicEntity> result = null;
            Query query;

            switch (condition.getContentType()) {
                case DAYS_KANBAN:// 日別カンバン計画実績
                    this.em.getEntityManagerFactory().getCache().evict(WorkKanbanTopicEntity.class);

                    switch (termType) {
                        case PLAN:
                        default:
                            // カンバンID一覧・計画日時範囲を指定して、計画実績情報一覧を取得する。
                            query = this.em.createNamedQuery("WorkKanbanTopicEntity.findByKanbanIdTerm", WorkKanbanTopicEntity.class);
                            break;
                        case ACTUAL:
                            // カンバンID一覧・実績日時範囲を指定して、計画実績情報一覧を取得する。
                            query = this.em.createNamedQuery("WorkKanbanTopicEntity.findByKanbanIdActualTerm", WorkKanbanTopicEntity.class);
                            break;
                    }
                    query.setParameter("kanbanIds", condition.getPrimaryKeys());
                    query.setParameter("fromDate", condition.getFromDate(), TemporalType.TIMESTAMP);
                    query.setParameter("toDate", condition.getToDate(), TemporalType.TIMESTAMP);

                    if (Objects.nonNull(from) && Objects.nonNull(to)) {
                        query.setMaxResults(to - from + 1);
                        query.setFirstResult(from);
                    }

                    result = query.getResultList();

                    // 組織ごとに工程カンバンが存在するので、重複を取り除く
                    //result = result.stream().distinct().collect(Collectors.toList());
                    break;

                case MONTHS_KANBAN:// 月別カンバン計画実績
                    this.em.getEntityManagerFactory().getCache().evict(KanbanTopicEntity.class);

                    // カンバンID一覧を指定して、計画実績情報一覧を取得する。
                    query = this.em.createNamedQuery("KanbanTopicEntity.findByKanbanId", KanbanTopicEntity.class);
                    query.setParameter("kanbanIds", condition.getPrimaryKeys());

                    if (Objects.nonNull(from) && Objects.nonNull(to)) {
                        query.setMaxResults(to - from + 1);
                        query.setFirstResult(from);
                    }

                    result = query.getResultList();
                    break;

                case DAYS_ORGANIZATION:// 日別作業者計画実績
                    this.em.getEntityManagerFactory().getCache().evict(WorkKanbanTopicEntity.class);

                    if (Objects.nonNull(condition.isWithParents()) && condition.isWithParents()) {
                        // 親キーも含める
                        result = new ArrayList();
                        Set<Long> organizationIds = new HashSet<>();
                        Map<Long, List<Long>> parentMap = new HashMap<>();

                        for (Long organizationId : condition.getPrimaryKeys()) {
                            List<Long> parentIds = this.organizationRest.getOrganizationParentPerpetuity(organizationId);
                            organizationIds.add(organizationId);
                            organizationIds.addAll(parentIds);
                            parentMap.put(organizationId, parentIds);
                        }
                        
                        switch (termType) {
                            case PLAN:
                            default:
                                // 組織ID一覧・計画日時範囲を指定して、計画実績情報一覧を取得する。
                                query = this.em.createNamedQuery("WorkKanbanTopicEntity.findByOrganizationIdTerm", WorkKanbanTopicEntity.class);
                                break;
                            case ACTUAL:
                                // 組織ID一覧・実績日時範囲を指定して、計画実績情報一覧を取得する。
                                query = this.em.createNamedQuery("WorkKanbanTopicEntity.findByOrganizationIdActualTerm", WorkKanbanTopicEntity.class);
                                break;
                        }

                        query.setParameter("organizationIds", organizationIds);
                        query.setParameter("fromDate", condition.getFromDate(), TemporalType.TIMESTAMP);
                        query.setParameter("toDate", condition.getToDate(), TemporalType.TIMESTAMP);

                        result = query.getResultList();
                        
                        final List<AbstractTopicEntity> planList = new ArrayList();
                        for (Long organizationId : condition.getPrimaryKeys()) {
                            List<Long> parentIds = parentMap.get(organizationId);
                            result.stream()
                                .filter(o -> parentIds.contains(o.getOrganizationId()))
                                .forEach(o -> planList.add(new AbstractTopicEntity(organizationId, o.getOrganizationId(), o)));
                        }

                        result.addAll(planList);

                        // 永続化コンテキストをdetached状態にしないと、処理を継続できない
                        this.em.clear();

                    } else {
                        // 親キーを含めない

                        switch (termType) {
                            case PLAN:
                            default:
                                // 組織ID一覧・計画日時範囲を指定して、計画実績情報一覧を取得する。
                                query = this.em.createNamedQuery("WorkKanbanTopicEntity.findByOrganizationIdTerm", WorkKanbanTopicEntity.class);
                                break;
                            case ACTUAL:
                                // 組織ID一覧・実績日時範囲を指定して、計画実績情報一覧を取得する。
                                query = this.em.createNamedQuery("WorkKanbanTopicEntity.findByOrganizationIdActualTerm", WorkKanbanTopicEntity.class);
                                break;
                        }
                        query.setParameter("organizationIds", condition.getPrimaryKeys());
                        query.setParameter("fromDate", condition.getFromDate(), TemporalType.TIMESTAMP);
                        query.setParameter("toDate", condition.getToDate(), TemporalType.TIMESTAMP);

                        if (Objects.nonNull(from) && Objects.nonNull(to)) {
                            query.setMaxResults(to - from + 1);
                            query.setFirstResult(from);
                        }

                        result = query.getResultList();
                    }
                    break;

                case DAYS_LINE:// ライン別カンバン計画実績
                    Set<Long> set = new HashSet();
                    for (Long equimplentId : condition.getPrimaryKeys()) {
                        set.addAll(this.equipmentRest.getChild(equimplentId, loginUserId));
                    }

                    List<Long> equipmentIds = new ArrayList(set);
                    List<KanbanStatusEnum> kanbanStatuses = Arrays.asList(KanbanStatusEnum.PLANNED, KanbanStatusEnum.WORKING, KanbanStatusEnum.SUSPEND, KanbanStatusEnum.COMPLETION);
                    Set<Long> kanbanIds = new HashSet(this.kanbanRest.find(null, equipmentIds, condition.getModelName(), kanbanStatuses, condition.getFromDate(), condition.getToDate()));
                    kanbanIds.addAll(this.actualResultRest.findKanban(equipmentIds, condition.getModelName(), condition.getFromDate(), condition.getToDate()));

                    if (!kanbanIds.isEmpty()) {
                        this.em.getEntityManagerFactory().getCache().evict(WorkKanbanTopicEntity.class);

                        // カンバンID一覧を指定して、計画実績情報一覧を取得する。
                        query = this.em.createNamedQuery("WorkKanbanTopicEntity.findByKanbanId", WorkKanbanTopicEntity.class);
                        query.setParameter("kanbanIds", new ArrayList<>(kanbanIds));

                        if (Objects.nonNull(from) && Objects.nonNull(to)) {
                            query.setMaxResults(to - from + 1);
                            query.setFirstResult(from);
                        }

                        result = query.getResultList();
                    } else {
                        result = new ArrayList();
                    }
                    break;

                default:
                    throw new IllegalArgumentException();
            }

            return result;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList();
        } finally {
            logger.info("searchTopicMain end.");
        }
    }

    /**
     * カンバンのスケジュール情報を取得する。
     *
     * @param id カンバンID
     * @param dateString 作業日
     * @param authId 認証ID
     * @return 予定データ
     * @throws ParseException 
     */
    @GET
    @Path("kanban")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public AgendaEntity findByKanban(@QueryParam("id") Long id, @QueryParam("date") String dateString, @QueryParam("authId") Long authId) throws ParseException {
        logger.info("findByKanban: id={}, dateString={}, authId={}", id, dateString, authId);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        //表示用ステータス情報の収集.
        getStatuses();

        Date date = new Date();
        if (Objects.nonNull(dateString)) {
            date = dateFormat.parse(dateString);
        }

        //カンバン取得.
        KanbanEntity kanban = kanbanRest.find(id);
        if (Objects.isNull(kanban.getKanbanId())) {
            return new AgendaEntity();
        }

        //計画取得.
        DisplayedStatusEntity allStatus = statuses.get(StatusPatternEnum.COMP_NORMAL);
        List<DatetimeConcurrent> plans = new ArrayList<>();
        List<WorkKanbanEntity> workKanbans = new ArrayList<>();
        workKanbans.addAll(kanban.getWorkKanbanCollection());
        workKanbans.addAll(kanban.getSeparateworkKanbanCollection());
        workKanbans.removeIf(workKanban -> {
            return workKanban.getSkipFlag() == true;
        });
        allStatus = createPlan(true, date, plans, allStatus, workKanbans);

        //実績取得.
        List<DatetimeConcurrent> actuals = new ArrayList<>();
        if (Objects.nonNull(kanban.getWorkKanbanCollection())) {
            for (WorkKanbanEntity workKanban : kanban.getWorkKanbanCollection()) {
                ActualSearchCondition actualCondition = new ActualSearchCondition()
                        .workKanbanList(Arrays.asList(workKanban.getWorkKanbanId()))
                        .fromDate(adtekfuji.utility.DateUtils.getBeginningOfDate(date)).toDate(adtekfuji.utility.DateUtils.getEndOfDate(date));
//                List<ActualResultEntity> actualResultList = actualResultRest.findSearchWithoutProperty(actualCondition);
                List<ActualResultEntity> actualResultList = actualResultRest.searchBasicInfo(actualCondition);
                createActual(true, actuals, actualResultList, workKanban);
            }
        }
        if (Objects.nonNull(kanban.getWorkKanbanCollection())) {
            for (WorkKanbanEntity workKanban : kanban.getSeparateworkKanbanCollection()) {
                ActualSearchCondition actualCondition = new ActualSearchCondition()
                        .workKanbanList(Arrays.asList(workKanban.getWorkKanbanId()))
                        .fromDate(adtekfuji.utility.DateUtils.getBeginningOfDate(date)).toDate(adtekfuji.utility.DateUtils.getEndOfDate(date));
//                List<ActualResultEntity> actualResultList = actualResultRest.findSearchWithoutProperty(actualCondition);
                List<ActualResultEntity> actualResultList = actualResultRest.searchBasicInfo(actualCondition);
                createActual(true, actuals, actualResultList, workKanban);
            }
        }

        logger.info("AgendaConcurrentEntity:{},{}", kanban.getKanbanName(), allStatus.getStatusName());

        //エンティティ化.
        List<AgendaConcurrentEntity> concurrentPlans = new ArrayList<>();
        List<AgendaConcurrentEntity> concurrentActuals = new ArrayList<>();
        for (DatetimeConcurrent c : plans) {
            concurrentPlans.add(c.getConcurrent());
        }
        for (DatetimeConcurrent c : actuals) {
            concurrentActuals.add(c.getConcurrent());
        }
        AgendaEntity agenda = new AgendaEntity(kanban.getKanbanName(), kanban.getWorkflowName(), allStatus.getFontColor(), allStatus.getBackColor());
        agenda.addAllPlans(concurrentPlans);
        agenda.addAllActuals(concurrentActuals);
        agenda.setDelayTimeMillisec(0L);
        //カンバンが
        if (checkDelayTime(kanban)) {
            agenda.setDelayTimeMillisec(getDelayTime(workKanbans));
        }
        return agenda;
    }

    /**
     * 作業者のスケジュール情報を取得する。
     *
     * @param id 組織ID
     * @param dateString 作業日
     * @param authId 認証ID
     * @return 予定データ
     * @throws ParseException 
     */
    @GET
    @Path("organization")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public AgendaEntity findByOrganization(@QueryParam("id") Long id, @QueryParam("date") String dateString, @QueryParam("authId") Long authId) throws ParseException {
        logger.info("findByOrganization: id={}, dateString={}, authId={}", id, dateString, authId);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // 表示ステータスを取得
        getStatuses();

        Date date = new Date();
        if (Objects.nonNull(dateString)) {
            date = dateFormat.parse(dateString);
        }

        // 組織を取得
        OrganizationEntity organization = organizationRest.find(id);
        OrganizationEntity parent = null;
        if (Objects.nonNull(organization.getParentOrganizationId())) {
            parent = organizationRest.find(organization.getParentOrganizationId());
        }

        // 工程カンバンを取得
        KanbanSearchCondition kanbanCondition = new KanbanSearchCondition()
                .fromDate(adtekfuji.utility.DateUtils.getBeginningOfDate(date)).toDate(adtekfuji.utility.DateUtils.getEndOfDate(date))
                .organizationList(Arrays.asList(id)).organizationIdWithParent(true).skipFlag(false);
//        List<WorkKanbanEntity> workKanbans = workKanbanRest.findSearch(kanbanCondition);
        List<WorkKanbanEntity> workKanbans = workKanbanRest.searchWorkKanban(kanbanCondition, null, null, authId);

        DisplayedStatusEntity allStatus = statuses.get(StatusPatternEnum.COMP_NORMAL);
        List<DatetimeConcurrent> plans = new ArrayList<>();
        allStatus = createPlan(false, date, plans, allStatus, workKanbans);

        List<DatetimeConcurrent> actuals = new ArrayList<>();
        if (!workKanbans.isEmpty()) {
            for (WorkKanbanEntity workKanban : workKanbans) {
                ActualSearchCondition actualCondition = new ActualSearchCondition()
                        .workKanbanList(Arrays.asList(workKanban.getWorkKanbanId())).organizationList(Arrays.asList(id))
                        .fromDate(adtekfuji.utility.DateUtils.getBeginningOfDate(date)).toDate(adtekfuji.utility.DateUtils.getEndOfDate(date));
//                List<ActualResultEntity> actualResultList = actualResultRest.findSearchWithoutProperty(actualCondition);
                List<ActualResultEntity> actualResultList = actualResultRest.searchBasicInfo(actualCondition);
                createActual(false, actuals, actualResultList, workKanban);
            }
        }

        logger.info("AgendaConcurrentEntity:{},{}", organization.getOrganizationName(), allStatus.getStatusName());

        //エンティティ化.
        List<AgendaConcurrentEntity> concurrentPlans = new ArrayList<>();
        List<AgendaConcurrentEntity> concurrentActuals = new ArrayList<>();
        for (DatetimeConcurrent c : plans) {
            concurrentPlans.add(c.getConcurrent());
        }
        for (DatetimeConcurrent c : actuals) {
            concurrentActuals.add(c.getConcurrent());
        }
        String parentName = (Objects.isNull(parent) || Objects.isNull(parent.getOrganizationName())) ? "" : parent.getOrganizationName();
        AgendaEntity agenda = new AgendaEntity(organization.getOrganizationName(), parentName, allStatus.getFontColor(), allStatus.getBackColor());
        agenda.addAllPlans(concurrentPlans);
        agenda.addAllActuals(concurrentActuals);
        agenda.setDelayTimeMillisec(getDelayTime(workKanbans));
        return agenda;
    }


    /**
     * 計画を作成
     *
     * @param isKanban
     * @param date
     * @param plans
     * @param allStatus
     * @param workKanbans
     * @return
     */
    private DisplayedStatusEntity createPlan(boolean isKanban, Date date, List<DatetimeConcurrent> plans, DisplayedStatusEntity allStatus, List<WorkKanbanEntity> workKanbans) {
        Collections.sort(workKanbans, (WorkKanbanEntity o1, WorkKanbanEntity o2) -> o1.getStartDatetime().compareTo(o2.getStartDatetime()));
        Map<Long, String> organizationSet = new HashMap<>();
        Date startDate = adtekfuji.utility.DateUtils.getBeginningOfDate(date);
        Date endDate = adtekfuji.utility.DateUtils.getEndOfDate(date);
        Date nowDate = new Date();

        for (WorkKanbanEntity workKanban : workKanbans) {
            if ((workKanban.getStartDatetime().before(startDate) && workKanban.getCompDatetime().before(startDate))
                    || workKanban.getStartDatetime().after(endDate) && workKanban.getCompDatetime().after(endDate)) {
                continue;
            }
            // 開始実績を取得
            ActualSearchCondition farstConditon = new ActualSearchCondition()
                    .kanbanId(workKanban.getKanbanId())
                    .workKanbanList(Arrays.asList(workKanban.getWorkKanbanId()))
                    .statusList(Arrays.asList(KanbanStatusEnum.WORKING))
                    .resultDailyEnum(ActualResultDailyEnum.ALL);
            ActualResultEntity actual1 = actualResultRest.searchFirst(farstConditon);
            // 完了実績を取得
            ActualSearchCondition lastConditon = new ActualSearchCondition()
                    .kanbanId(workKanban.getKanbanId())
                    .workKanbanList(Arrays.asList(workKanban.getWorkKanbanId()))
                    .statusList(Arrays.asList(KanbanStatusEnum.COMPLETION))
                    .resultDailyEnum(ActualResultDailyEnum.ALL);
            ActualResultEntity actual2 = actualResultRest.findLastActualResult(lastConditon, null);

            // 表示ステータス
            Date actualStart = Objects.nonNull(actual1) ? actual1.getImplementDatetime() : null;
            Date actualEnd = Objects.nonNull(actual2) ? actual2.getImplementDatetime() : null;
            DisplayedStatusEntity status = getDisplayedStatus(workKanban, actualStart, actualEnd, nowDate);
            allStatus = statuses.get(StatusPatternEnum.compareStatus(allStatus.getStatusName(), status.getStatusName()));

            // 組織名
            String organizationName = "";
            int count = 0;
            if (Objects.nonNull(workKanban.getOrganizationCollection())) {
                for (Long id : workKanban.getOrganizationCollection()) {
                    String name = "";
                    if (organizationSet.containsKey(id)) {
                        name = organizationSet.get(id);
                    } else {
//                        name = organizationRest.findWithoutProperty(id).getOrganizationName();
                        name = organizationRest.findBasicInfo(id).getOrganizationName();
                        organizationSet.put(id, name);
                    }
                    organizationName += (count == 0) ? name : ", " + name;
                    count++;
                }
            }

            AgendaItemEntity item;
            if (isKanban) {
                item = new AgendaItemEntity(workKanban.getWorkName(), organizationName, "", workKanban.getTaktTime(),
                        workKanban.getStartDatetime(), workKanban.getCompDatetime(), status.getFontColor(), status.getBackColor(), "#ffffff", (status.getLightPattern() == LightPatternEnum.BLINK));
            } else {
                item = new AgendaItemEntity(workKanban.getKanbanName(), workKanban.getWorkflowName(), workKanban.getWorkName(), workKanban.getTaktTime(),
                        workKanban.getStartDatetime(), workKanban.getCompDatetime(), status.getFontColor(), status.getBackColor(), "#ffffff", (status.getLightPattern() == LightPatternEnum.BLINK));
            }
            boolean find = false;
            for (DatetimeConcurrent c : plans) {
                if ((c.getStart().before(workKanban.getStartDatetime()) && c.getEnd().after(workKanban.getStartDatetime()))
                        || (c.getStart().before(workKanban.getCompDatetime()) && c.getEnd().after(workKanban.getCompDatetime()))
                        || c.getStart().equals(workKanban.getStartDatetime()) || c.getEnd().equals(workKanban.getCompDatetime())) {
                    c.getConcurrent().addItem(item);
                    if (c.getStart().before(workKanban.getStartDatetime())) {
                        c.setStart(workKanban.getStartDatetime());
                    }
                    if (c.getEnd().before(workKanban.getCompDatetime())) {
                        c.setEnd(workKanban.getCompDatetime());
                    }
                    find = true;
                    break;
                }
            }
            if (find == false) {
                DatetimeConcurrent concurrent = new DatetimeConcurrent(
                        workKanban.getStartDatetime(), workKanban.getCompDatetime(), new AgendaConcurrentEntity().addItem(item));
                plans.add(concurrent);
            }
        }
        return allStatus;
    }

    //実績の予実情報を作成.
    private void createActual(boolean isKanban, List<DatetimeConcurrent> actuals, List<ActualResultEntity> actualResultList, WorkKanbanEntity workKanban) {
        Collections.sort(actualResultList, (ActualResultEntity o1, ActualResultEntity o2) -> o1.getImplementDatetime().compareTo(o2.getImplementDatetime()));
        Date startDatetime = null;
        Date endDatetime = null;
        Date nowDate = new Date();
        boolean isFind = false;
        String organizationName = "";
        for (ActualResultEntity actual : actualResultList) {
            if (actual.getActualStatus() == KanbanStatusEnum.WORKING) {
                if (Objects.nonNull(endDatetime)) {
                    createActualItem(isKanban, actuals, startDatetime, endDatetime, nowDate, organizationName, workKanban);
                    organizationName = "";
                    startDatetime = null;
                    endDatetime = null;
                    isFind = false;
                }
                if (Objects.isNull(startDatetime) || startDatetime.after(actual.getImplementDatetime())) {
                    startDatetime = actual.getImplementDatetime();
                    isFind = true;
                }
                if (Objects.nonNull(actual.getOrganizationId())) {
//                    String name = organizationRest.findWithoutProperty(actual.getOrganizationId()).getOrganizationName();
                    String name = organizationRest.findBasicInfo(actual.getOrganizationId()).getOrganizationName();
                    organizationName += (organizationName.equals("")) ? "" : ", ";
                    organizationName += name;
                }
            }
            if (Objects.nonNull(startDatetime) && (actual.getActualStatus() != KanbanStatusEnum.WORKING)) {
                if (Objects.isNull(endDatetime) || endDatetime.before(actual.getImplementDatetime())) {
                    endDatetime = actual.getImplementDatetime();
                }
            }
        }
        if (isFind) {
            //完了や一時中止などがない場合は、現在時刻＋タクトタイムとする.
            if (Objects.isNull(endDatetime)) {
                Calendar endtime = Calendar.getInstance();
                endtime.setTime(startDatetime);
                endtime.add(Calendar.MILLISECOND, workKanban.getTaktTime());
                endDatetime = new Date();
                if (endDatetime.before(endtime.getTime())) {
                    endDatetime = endtime.getTime();
                }
            }
            createActualItem(isKanban, actuals, startDatetime, endDatetime, nowDate, organizationName, workKanban);
        }
    }

    private void createActualItem(boolean isKanban, List<DatetimeConcurrent> actuals, Date startDatetime, Date endDatetime, Date nowDate, String organizationName, WorkKanbanEntity workKanban) {
        if (Objects.isNull(startDatetime) || Objects.isNull(endDatetime)) {
            return;
        }

        //ステータスを決めるための開始終了時間は、最初の実績の開始時間と最後の実績の完了時間にする.
        ActualSearchCondition farstConditon = new ActualSearchCondition()
                .kanbanId(workKanban.getKanbanId())
                .workKanbanList(Arrays.asList(workKanban.getWorkKanbanId()))
                .statusList(Arrays.asList(KanbanStatusEnum.COMPLETION))
                .resultDailyEnum(ActualResultDailyEnum.ALL);
        ActualResultEntity actual1 = actualResultRest.searchFirst(farstConditon);
        ActualSearchCondition lastConditon = new ActualSearchCondition()
                .kanbanId(workKanban.getKanbanId())
                .workKanbanList(Arrays.asList(workKanban.getWorkKanbanId()))
                .statusList(Arrays.asList(KanbanStatusEnum.COMPLETION))
                .resultDailyEnum(ActualResultDailyEnum.ALL);
        ActualResultEntity actual2 = actualResultRest.findLastActualResult(lastConditon, null);
        Date firstActualStart = Objects.nonNull(actual1) ? actual1.getImplementDatetime() : null;
        Date lastActualEnd = Objects.nonNull(actual2) ? actual2.getImplementDatetime() : null;
        DisplayedStatusEntity status = getDisplayedStatus(workKanban, firstActualStart, lastActualEnd, nowDate);

        AgendaItemEntity item;
        if (isKanban) {
            item = new AgendaItemEntity(workKanban.getWorkName(), organizationName, "", workKanban.getTaktTime(),
                    startDatetime, endDatetime, status.getFontColor(), status.getBackColor(), "#ffffff", (status.getLightPattern() == LightPatternEnum.BLINK));
        } else {
            item = new AgendaItemEntity(workKanban.getKanbanName(), workKanban.getWorkflowName(), workKanban.getWorkName(), workKanban.getTaktTime(),
                    startDatetime, endDatetime, status.getFontColor(), status.getBackColor(), "#ffffff", (status.getLightPattern() == LightPatternEnum.BLINK));
        }
        boolean find = false;
        for (DatetimeConcurrent c : actuals) {
            if ((c.getStart().before(startDatetime) && c.getEnd().after(startDatetime))
                    || (c.getStart().before(endDatetime) && c.getEnd().after(endDatetime))
                    || c.getStart().equals(startDatetime) || c.getEnd().equals(endDatetime)) {
                c.getConcurrent().addItem(item);
                if (c.getStart().before(startDatetime)) {
                    c.setStart(startDatetime);
                }
                if (c.getEnd().before(endDatetime)) {
                    c.setEnd(endDatetime);
                }
                find = true;
                break;
            }
        }
        if (!find) {
            DatetimeConcurrent concurrent = new DatetimeConcurrent(
                    startDatetime, endDatetime, new AgendaConcurrentEntity().addItem(item));
            actuals.add(concurrent);
        }
    }

    //表示用ステータス情報の収集.
    private void getStatuses() {
        for (DisplayedStatusEntity s : statusRest.findAll()) {
            statuses.put(s.getStatusName(), s);
        }
    }

    //ステータスに対応した表示ステータスを決める.
    private DisplayedStatusEntity getDisplayedStatus(WorkKanbanEntity workKanban, Date actualStartDatetime, Date actualEndDatetime, Date nowDatetime) {
        StatusPatternEnum pattern = StatusPatternEnum.getStatusPattern(
                workKanban.getKanbanStatus(), workKanban.getWorkStatus(), workKanban.getStartDatetime(), workKanban.getCompDatetime(), actualStartDatetime, actualEndDatetime, nowDatetime);
        DisplayedStatusEntity displayedStatus = statuses.get(pattern);
        //工程に色が指定してある場合は、その色を使用する.
        if (displayedStatus.getStatusName() == StatusPatternEnum.WORK_NORMAL) {
//            WorkEntity work = workRest.findWithoutProperty(workKanban.getWorkId());
            WorkEntity work = workRest.findBasicInfo(workKanban.getWorkId());
            if (Objects.nonNull(work.getFontColor())) {
                displayedStatus.setFontColor(work.getFontColor());
            }
            if (Objects.nonNull(work.getBackColor())) {
                displayedStatus.setBackColor(work.getBackColor());
            }
        }
        return displayedStatus;
    }

    /**
     * カンバンの全体遅れ、前倒し時間の計算必要性を確認する
     *
     */
    private boolean checkDelayTime(KanbanEntity kanban) {
        List<WorkKanbanEntity> workKanbans = new ArrayList<>();
        workKanbans.addAll(kanban.getWorkKanbanCollection());
        workKanbans.addAll(kanban.getSeparateworkKanbanCollection());
        return !(kanban.getStartDatetime().after(getNow(workKanbans)) && (kanban.getKanbanStatus().equals(KanbanStatusEnum.PLANNED)
                || kanban.getKanbanStatus().equals(KanbanStatusEnum.PLANNING)));
    }

    /**
     * 全体遅れ・前倒し時間の計算.
     *
     */
    private long getDelayTime(List<WorkKanbanEntity> workKanbans) {
        List<WorkKanbanTimeData> datas = new ArrayList<>();
        for (WorkKanbanEntity workKanban : workKanbans) {
            //作業者の休憩時間間隔を取得.
            List<BreakTimeInfoEntity> breaktimeCollection = new ArrayList<>();
            if (Objects.nonNull(workKanban.getOrganizationCollection()) && !workKanban.getOrganizationCollection().isEmpty()) {
                OrganizationEntity organization = organizationRest.find(workKanban.getOrganizationCollection().get(0));
                for (Long breaktimeId : organization.getBreaktimeCollection()) {
                    BreaktimeEntity breaktimeEntity = breaktimeRest.find(breaktimeId);
                    breaktimeCollection.add(new BreakTimeInfoEntity(breaktimeEntity.getBreaktimeName(), breaktimeEntity.getStarttime(), breaktimeEntity.getEndtime()));
                }
            }

            if (workKanban.getWorkStatus() == KanbanStatusEnum.WORKING) {
                ActualSearchCondition lastConditon = new ActualSearchCondition()
                        .kanbanId(workKanban.getKanbanId())
                        .workKanbanList(Arrays.asList(workKanban.getWorkKanbanId()))
                        .statusList(Arrays.asList(KanbanStatusEnum.COMPLETION))
                        .resultDailyEnum(ActualResultDailyEnum.ALL);
                ActualResultEntity actual = actualResultRest.findLastActualResult(lastConditon, null);
                if (Objects.nonNull(actual)) {
                    datas.add(new WorkKanbanTimeData(workKanban.getWorkStatus(), workKanban.getStartDatetime(), workKanban.getCompDatetime(), actual.getImplementDatetime(), null, breaktimeCollection, workKanban.getSumTimes()));
                }
            } else {
                datas.add(new WorkKanbanTimeData(workKanban.getWorkStatus(), workKanban.getStartDatetime(), workKanban.getCompDatetime(), null, null, breaktimeCollection, workKanban.getSumTimes()));
            }
        }

        return CalcWorkKanbanDelayTime.calcDelayTimes(datas, getNow(workKanbans), getShiftTime(workKanbans));
    }

    //工程順に関係なく一番早く始めた実績の開始時間と予定の開始時間の差を取得.
    private long getShiftTime(List<WorkKanbanEntity> workKanbans) {
        ActualResultEntity firstActual;
        Date firstActualStart;
        Date firstPlanStart;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 10);
        firstActualStart = cal.getTime();
        firstPlanStart = cal.getTime();

        boolean firstAcualexist = false;
        for (WorkKanbanEntity workKanban : workKanbans) {
            //最初の作業開始時間を取得.
            ActualSearchCondition farstConditon = new ActualSearchCondition()
                    .kanbanId(workKanban.getKanbanId())
                    .workKanbanList(Arrays.asList(workKanban.getWorkKanbanId()))
                    .statusList(Arrays.asList(KanbanStatusEnum.WORKING))
                    .resultDailyEnum(ActualResultDailyEnum.ALL);
            firstActual = actualResultRest.findFirstActualResult(farstConditon, null);
            if (Objects.nonNull(firstActual) && firstActualStart.after(firstActual.getImplementDatetime())) {
                firstActualStart = firstActual.getImplementDatetime();
                firstAcualexist = true;
            }
            //最初の予定開始時間を取得.
            if (firstPlanStart.after(workKanban.getStartDatetime())) {
                firstPlanStart = workKanban.getStartDatetime();
            }
        }

        //作業実績あり.
        if (firstAcualexist) {
            return firstPlanStart.getTime() - firstActualStart.getTime();
        }
        //作業実績なし.
        return 0;
    }

    //カンバンの全体遅れを計算するための現在時間を返す.
    //完了しているカンバンは最後の完了時間を返す.
    private Date getNow(List<WorkKanbanEntity> workKanbans) {
        Date now = new Date(0L);
        for (WorkKanbanEntity workKanban : workKanbans) {
            if (workKanban.getWorkStatus() == KanbanStatusEnum.COMPLETION) {
                //すべての工程が終わっているなら最後の完了時間を返す.
                ActualSearchCondition lastConditon = new ActualSearchCondition()
                        .kanbanId(workKanban.getKanbanId())
                        .workKanbanList(Arrays.asList(workKanban.getWorkKanbanId()))
                        .statusList(Arrays.asList(KanbanStatusEnum.COMPLETION))
                        .resultDailyEnum(ActualResultDailyEnum.ALL);
                ActualResultEntity actual = actualResultRest.findLastActualResult(lastConditon, null);
                if (Objects.nonNull(actual) && actual.getImplementDatetime().after(now)) {
                    now = actual.getImplementDatetime();
                }
            } else {
                //1つでも完了してない工程があれば現在時間を返す.
                now = new Date();
                break;
            }
        }
        return now;
    }

    /**
     * EntityManagerを設定する。
     *
     * @param em
     */
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public void setKanbanRest(KanbanEntityFacadeREST kanbanRest) {
        this.kanbanRest = kanbanRest;
    }

    public void setActualResultRest(ActualResultEntityFacadeREST actualResultRest) {
        this.actualResultRest = actualResultRest;
    }

    public void setOrganizationRest(OrganizationEntityFacadeREST organizationRest) {
        this.organizationRest = organizationRest;
    }
}
