/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import java.math.BigInteger;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.ejb.EJB;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.kanban.TraceabilityEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.operation.OperateAppEnum;
import jp.adtekfuji.adFactory.entity.operation.OperationTypeEnum;
import jp.adtekfuji.adFactory.entity.search.ActualSearchCondition;
import jp.adtekfuji.adFactory.entity.search.IndirectWorkSearchCondition;
import jp.adtekfuji.adFactory.entity.search.ReportOutSearchCondition;
import jp.adtekfuji.adFactory.enumerate.ActualResultDailyEnum;
import jp.adtekfuji.adFactory.enumerate.CompCountTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.utility.BreaktimeUtil;
import jp.adtekfuji.adfactoryserver.common.Constants;
import jp.adtekfuji.adfactoryserver.entity.actual.*;
import jp.adtekfuji.adfactoryserver.entity.agenda.WorkKanbanTopicEntity;
import jp.adtekfuji.adfactoryserver.entity.chart.TimeLineEntity;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity_;
import jp.adtekfuji.adfactoryserver.entity.kanban.WorkKanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.master.BreaktimeEntity;
import jp.adtekfuji.adfactoryserver.entity.operation.OperateChangeResultEntity;
import jp.adtekfuji.adfactoryserver.entity.operation.OperationAddInfoEntity;
import jp.adtekfuji.adfactoryserver.entity.operation.OperationEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import jp.adtekfuji.adfactoryserver.entity.view.ReportOutEntity;
import jp.adtekfuji.adfactoryserver.entity.view.ReportOutEntity_;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity;
import jp.adtekfuji.adfactoryserver.model.TraceabilityJdbc;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import jp.adtekfuji.adfactoryserver.utility.JsonUtils;
import jp.adtekfuji.andon.entity.DefectEntity;
import jp.adtekfuji.andon.entity.ProductivityEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.QueryHints;

/**
 * 工程実績情報REST
 *
 * @author ke.yokoi
 */
@Stateless
@Path("actual")
public class ActualResultEntityFacadeREST extends AbstractFacade<ActualResultEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    @EJB
    private KanbanHierarchyEntityFacadeREST kanbanHierarchyRest;

    @EJB
    private WorkflowHierarchyEntityFacadeREST workflowHierarchyRest;

    @EJB
    private WorkHierarchyEntityFacadeREST workHierarchyRest;

    @EJB
    private KanbanEntityFacadeREST kanbanRest;

    @EJB
    private WorkKanbanEntityFacadeREST workKandanRest;

    @EJB
    private WorkflowEntityFacadeREST workflowRest;

    @EJB
    private WorkEntityFacadeREST workRest;

    @EJB
    private OrganizationEntityFacadeREST organizationRest;

    @EJB
    private EquipmentEntityFacadeREST equipmentRest;

    @EJB
    private BreaktimeEntityFacadeREST breaktimeRest;

    @EJB
    private OperationEntityFacadeREST operationRest;

    private final Logger logger = LogManager.getLogger();

    private static final String pattern[] = {"yyyy-MM-dd"};

    /**
     * コンストラクタ
     */
    public ActualResultEntityFacadeREST() {
        super(ActualResultEntity.class);
    }

    /**
     * 工程実績情報を登録する。
     *
     * @param entity 工程実績情報
     */
    @ExecutionTimeLogging
    public void add(ActualResultEntity entity) {
        logger.info("add: {}", entity);
        super.create(entity);
        this.em.flush();
    }

    /**
     * 工程実績IDを指定して、工程実績情報を取得する。
     *
     * @param id 工程実績ID
     * @param authId 認証ID
     * @return 工程実績情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ActualResultEntity find(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("find: id={}, authId={}", id, authId);
        try {
            ActualResultEntity actual = super.find(id);

            // 詳細情報を取得してセットする。
            this.getDetails(actual);

            return actual;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 工程実績情報一覧を取得する。
     *
     * @param authId 認証ID
     * @return 工程実績情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("all")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<ActualResultEntity> findAll(@QueryParam("authId") Long authId) {
        logger.info("findAll: authId={}", authId);
        return this.findRange(null, null, authId);
    }

    /**
     * 工程実績情報一覧を取得する。
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 工程実績情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<ActualResultEntity> findRange(@QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findRange: from={}, to={}, authId={}", from, to, authId);
        try {
            List<ActualResultEntity> actuals;
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                actuals = super.findRange(from, to);
            } else {
                actuals = super.findAll();
            }

            for (ActualResultEntity actual : actuals) {
                // 詳細情報を取得してセットする。
                this.getDetails(actual);
            }

            return actuals;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * カンバンID一覧を指定して、工程実績情報一覧を取得する。
     *
     * @param kanbanIds カンバンID一覧
     * @param isDetail 詳細情報を取得する？ (true: する, false: しない)
     * @param authId 認証ID
     * @return 工程実績情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<ActualResultEntity> find(@QueryParam("id") final List<Long> kanbanIds, @QueryParam("detail") Boolean isDetail, @QueryParam("authId") Long authId) {
        logger.info("find: isDetail={}, kanbanIds={}, authId={}", isDetail, kanbanIds, authId);
        try {
            // カンバンID一覧を指定して、工程実績情報一覧を取得する。
            TypedQuery<ActualResultEntity> query = this.em.createNamedQuery("ActualResultEntity.findByKanbanIds", ActualResultEntity.class);
            query.setParameter("kanbanIds", kanbanIds);

            List<ActualResultEntity> actuals = query.getResultList();

            if (Objects.nonNull(isDetail) && isDetail) {
                for (ActualResultEntity actual : actuals) {
                    // 詳細情報を取得してセットする。
                    this.getDetails(actual);
                }
            }

            return actuals;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 工程実績情報の件数を取得する。
     *
     * @param authId 認証ID
     * @return 工程実績情報の件数
     */
    @Lock(LockType.READ)
    @GET
    @Path("count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String countAll(@QueryParam("authId") Long authId) {
        logger.info("countAll: authId={}", authId);
        try {
            return String.valueOf(super.count());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 工程実績検索条件を指定して、工程実績情報一覧を取得する。
     *
     * @param condition 検索条件
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 工程実績情報一覧
     */
    @Lock(LockType.READ)
    @PUT
    @Path("search/range")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<ActualResultEntity> searchActualResult(ActualSearchCondition condition, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("searchActualResult: {}, from={}, to={}, authId={}", condition, from, to, authId);
        try {
            Query query = this.getSearchQuery(SearchType.SEARCH, condition);

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                query.setMaxResults(to - from + 1);
                query.setFirstResult(from);
            }

            List<ActualResultEntity> actuals = query.getResultList();
            for (ActualResultEntity actual : actuals) {
                // 詳細情報を取得してセットする。
                this.getDetails(actual);
            }

            return actuals;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 工程実績検索条件を指定して、工程実績情報の件数を取得する。
     *
     * @param condition 検索条件
     * @param authId 認証ID
     * @return 件数
     */
    @Lock(LockType.READ)
    @PUT
    @Path("search/count")
    @Consumes({"application/xml", "application/json"})
    @Produces({"text/plain"})
    @ExecutionTimeLogging
    public String countActualResult(ActualSearchCondition condition, @QueryParam("authId") Long authId) {
        logger.info("countActualResult:{}", condition);
        try {
            Query query = this.getSearchQuery(SearchType.COUNT, condition);
            return String.valueOf(query.getSingleResult());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 工程実績検索条件を指定して、工程実績情報一覧を取得する。
     *
     * @param condition 検索条件
     * @return 工程実績情報一覧
     */
    public List<ActualResultEntity> searchBasicInfo(ActualSearchCondition condition) {
        List<ActualResultEntity> actuals = getSearchQuery(SearchType.SEARCH, condition).getResultList();
        return actuals;
    }

    /**
     * 検索条件で工程実績情報を検索するクエリを取得する。
     *
     * @param type 検索種別
     * @param condition 検索条件
     * @return 検索クエリ
     */
    @Lock(LockType.READ)
    private Query getSearchQuery(SearchType type, ActualSearchCondition condition) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();

        Root<ActualResultEntity> poolActual = cq.from(ActualResultEntity.class);

        jakarta.persistence.criteria.Path<Long> pathActualId = poolActual.get(ActualResultEntity_.actualId);
        jakarta.persistence.criteria.Path<Date> pathImplementDatetime = poolActual.get(ActualResultEntity_.implementDatetime);
        jakarta.persistence.criteria.Path<KanbanStatusEnum> pathActualStatus = poolActual.get(ActualResultEntity_.actualStatus);

        jakarta.persistence.criteria.Path<Long> pathKanbanId = poolActual.get(ActualResultEntity_.kanbanId);
        jakarta.persistence.criteria.Path<Long> pathWorkKanbanId = poolActual.get(ActualResultEntity_.workKanbanId);
        jakarta.persistence.criteria.Path<Long> pathWorkflowId = poolActual.get(ActualResultEntity_.workflowId);
        jakarta.persistence.criteria.Path<Long> pathWorkId = poolActual.get(ActualResultEntity_.workId);
        jakarta.persistence.criteria.Path<Long> pathEquipmentId = poolActual.get(ActualResultEntity_.equipmentId);
        jakarta.persistence.criteria.Path<Long> pathOrganizationId = poolActual.get(ActualResultEntity_.organizationId);

        jakarta.persistence.criteria.Path<String> pathInterruptReason = poolActual.get(ActualResultEntity_.interruptReason);
        jakarta.persistence.criteria.Path<String> pathDelayReason = poolActual.get(ActualResultEntity_.delayReason);

        jakarta.persistence.criteria.Path<String> pathKanbanName = poolActual.get(ActualResultEntity_.kanbanName);
        jakarta.persistence.criteria.Path<String> pathEquipmentName = poolActual.get(ActualResultEntity_.equipmentName);
        jakarta.persistence.criteria.Path<String> pathOrganizationName = poolActual.get(ActualResultEntity_.organizationName);
        jakarta.persistence.criteria.Path<String> pathWorkName = poolActual.get(ActualResultEntity_.workName);
        jakarta.persistence.criteria.Path<Boolean> removeFlag = poolActual.get(ActualResultEntity_.removeFlag);

        // 検索条件
        List<Predicate> where = new ArrayList();

        // カンバンID
        if (Objects.nonNull(condition.getKanbanId())) {
            where.add(cb.equal(pathKanbanId, condition.getKanbanId()));
        }

        // カンバン名
        if (Objects.nonNull(condition.getKanbanName())) {
            where.add(cb.or(
                    cb.like(cb.lower(pathKanbanName), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(condition.getKanbanName())) + "%"),
                    cb.like(pathKanbanName, "%" + StringUtils.escapeLikeChar(condition.getKanbanName()) + "%")
            ));
        }

        // サブカンバン名
        if (Objects.nonNull(condition.getKanbanSubname())) {
            Root<KanbanEntity> subKan = cq.from(KanbanEntity.class);

            jakarta.persistence.criteria.Path<Long> pathKanId = subKan.get(KanbanEntity_.kanbanId);
            jakarta.persistence.criteria.Path<String> pathSubname = subKan.get(KanbanEntity_.kanbanSubname);

            Subquery<Long> conSubquery = cq.subquery(Long.class);

            conSubquery.select(pathKanId).where(cb.or(
                    cb.like(cb.lower(pathSubname), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(condition.getKanbanSubname())) + "%"),
                    cb.like(pathSubname, "%" + StringUtils.escapeLikeChar(condition.getKanbanSubname()) + "%")
            ));

            where.add(pathKanbanId.in(conSubquery));
        }

        // 工程順ID
        if (Objects.nonNull(condition.getWorkflowId())) {
            where.add(cb.equal(pathWorkflowId, condition.getWorkflowId()));
        }

        // 実施日時
        if (Objects.nonNull(condition.getFromDate())) {
            where.add(cb.greaterThanOrEqualTo(pathImplementDatetime, condition.getFromDate()));
        }
        if (Objects.nonNull(condition.getToDate())) {
            where.add(cb.lessThanOrEqualTo(pathImplementDatetime, condition.getToDate()));
        }

        // 工程カンバンID
        if (Objects.nonNull(condition.getWorkKanbanCollection())) {
            where.add(pathWorkKanbanId.in(condition.getWorkKanbanCollection()));
        }

        // 工程実績ステータス
        if (Objects.nonNull(condition.getKanbanStatusCollection())) {
            where.add(pathActualStatus.in(condition.getKanbanStatusCollection()));
        }

        // 設備ID
        if (Objects.nonNull(condition.getEquipmentCollection())) {
            where.add(pathEquipmentId.in(condition.getEquipmentCollection()));
        }

        // 設備名
        if (Objects.nonNull(condition.getEquipmentNameCollection())) {
            List<Predicate> nameSubWhere = new ArrayList();
            for (String name : condition.getEquipmentNameCollection()) {
                nameSubWhere.add(cb.like(cb.lower(pathEquipmentName), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(name)) + "%"));
                nameSubWhere.add(cb.like(pathEquipmentName, "%" + StringUtils.escapeLikeChar(name) + "%"));
            }

            where.add(cb.or(nameSubWhere.toArray(new Predicate[nameSubWhere.size()])));
        }

        // 組織ID
        if (Objects.nonNull(condition.getOrganizationCollection())) {
            where.add(pathOrganizationId.in(condition.getOrganizationCollection()));
        }

        // 組織名
        if (Objects.nonNull(condition.getOrganizationNameCollection())) {
            List<Predicate> nameSubWhere = new ArrayList();
            for (String name : condition.getOrganizationNameCollection()) {
                nameSubWhere.add(cb.like(cb.lower(pathOrganizationName), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(name)) + "%"));
                nameSubWhere.add(cb.like(pathOrganizationName, "%" + StringUtils.escapeLikeChar(name) + "%"));
            }

            where.add(cb.or(nameSubWhere.toArray(new Predicate[nameSubWhere.size()])));
        }

        // 工程
        if (Objects.nonNull(condition.getWorkNameCollection())) {
            // 工程名
            List<Predicate> nameSubWhere = new ArrayList();
            for (String name : condition.getWorkNameCollection()) {
                nameSubWhere.add(cb.like(cb.lower(pathWorkName), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(name)) + "%"));
                nameSubWhere.add(cb.like(pathWorkName, "%" + StringUtils.escapeLikeChar(name) + "%"));
            }

            where.add(cb.or(nameSubWhere.toArray(new Predicate[nameSubWhere.size()])));
        } else if (Objects.nonNull(condition.getWorkCollection())) {
            // 工程ID
            where.add(pathWorkId.in(condition.getWorkNameCollection()));
        }

        // 中断理由
        if (Objects.nonNull(condition.getInterruptReason())) {
            where.add(cb.equal(pathInterruptReason, condition.getInterruptReason()));
        }

        // 遅延理由
        if (Objects.nonNull(condition.getDelayReason())) {
            where.add(cb.equal(pathDelayReason, condition.getDelayReason()));
        }

        // 設備ID(NULL, NULL以外)
        if (Objects.nonNull(condition.getEquipmentIsNull())) {
            if (condition.getEquipmentIsNull()) {
                where.add(cb.isNull(pathEquipmentId));
            } else {
                where.add(cb.isNotNull(pathEquipmentId));
            }
        }

        if (Objects.nonNull(condition.getCheckRemoveFlag())) {
            if (condition.getCheckRemoveFlag()) {
                where.add(cb.isFalse(removeFlag));
            }
        }

        if (SearchType.COUNT.equals(type)) {
            cq.select(cb.count(pathActualId))
                    .where(cb.and(where.toArray(new Predicate[where.size()])));
        } else {
            List<Order> orders = new LinkedList();
            if (Objects.nonNull(condition.isOrderDesc()) && condition.isOrderDesc()) {
                orders.add(cb.desc(pathImplementDatetime));
                orders.add(cb.desc(pathActualId));
            } else {
                orders.add(cb.asc(pathImplementDatetime));
                orders.add(cb.asc(pathActualId));
            }

            cq.select(poolActual)
                    .where(cb.and(where.toArray(new Predicate[where.size()])))
                    .orderBy(orders);
        }

        return this.em.createQuery(cq);
    }

    /**
     * 詳細情報を取得してセットする。
     *
     * @param actual 工程実績情報
     */
    @Lock(LockType.READ)
    private void getDetails(ActualResultEntity actual) {
        // カンバン情報
        if (Objects.nonNull(actual.getKanbanId())) {
            // カンバン情報を取得する。(基本情報のみ)
            KanbanEntity kanban = this.kanbanRest.findBasicInfo(actual.getKanbanId());

            // カンバン階層情報
            if (Objects.nonNull(kanban.getParentId()) && kanban.getParentId() != 0) {
                // カンバン階層情報を取得して、階層名をセットする。
                actual.setKanbanParentName(this.kanbanHierarchyRest.findBasicInfo(kanban.getParentId()).getHierarchyName());
            }

            if (StringUtils.isEmpty(actual.getKanbanName())) {
                actual.setKanbanName(kanban.getKanbanName());// カンバン名
            }
            actual.setKanbanSubname(kanban.getKanbanSubname());// サブカンバン名
        }

        // 工程カンバン情報
        if (Objects.nonNull(actual.getWorkKanbanId())) {
            // 工程カンバン情報を取得する。(基本情報のみ)
            WorkKanbanEntity workKanban = this.workKandanRest.findBasicInfo(actual.getWorkKanbanId());
            actual.setIsSeparateWork(workKanban.getSkipFlag());// スキップフラグ
            actual.setTaktTime(workKanban.getTaktTime());// タクトタイム
            actual.setSerialNumber(workKanban.getSerialNumber()); // シリアル番号
        }

        // 設備情報
        if (Objects.nonNull(actual.getEquipmentId())) {
            // 設備情報を取得する。
            EquipmentEntity equipment = this.equipmentRest.find(actual.getEquipmentId(), null);
            if (StringUtils.isEmpty(actual.getEquipmentName())) {
                actual.setEquipmentName(equipment.getEquipmentName());// 設備名
            }
            actual.setEquipmentIdentName(equipment.getEquipmentIdentify());// 設備識別名

            // 親設備の設備情報
            if (Objects.nonNull(equipment.getParentEquipmentId())) {
                // 設備情報を取得する。
                EquipmentEntity parent = this.equipmentRest.find(equipment.getParentEquipmentId(), null);
                actual.setEquipmentParentName(parent.getEquipmentName());// 親設備の設備名
                actual.setEquipmentParentIdentName(parent.getEquipmentIdentify());// 親設備の設備識別名
            }
        }

        // 組織情報
        if (Objects.nonNull(actual.getOrganizationId())) {
            OrganizationEntity organization = this.organizationRest.find(actual.getOrganizationId(), null);

            if (StringUtils.isEmpty(actual.getOrganizationName())) {
                actual.setOrganizationName(organization.getOrganizationName());
            }
            actual.setOrganizationIdentName(organization.getOrganizationIdentify());

            // 親組織の組織情報
            if (Objects.nonNull(organization.getParentOrganizationId())) {
                OrganizationEntity parent = this.organizationRest.find(organization.getParentOrganizationId(), null);
                actual.setOrganizationParentName(parent.getOrganizationName());
                actual.setOrganizationParentIdentName(parent.getOrganizationIdentify());
            }
        }

        // 工程順情報
        if (Objects.nonNull(actual.getWorkflowId())) {
            WorkflowEntity workflow = this.workflowRest.findBasicInfo(actual.getWorkflowId());
            if (Objects.nonNull(workflow.getParentId())) {
                actual.setWorkflowParentName(this.workflowHierarchyRest.findBasicInfo(workflow.getParentId()).getHierarchyName());
            }

            if (StringUtils.isEmpty(actual.getWorkflowName())) {
                actual.setWorkflowName(workflow.getWorkflowName());
            }
            actual.setWorkflowRevision(workflow.getWorkflowRevision());
        }

        // 工程情報
        if (Objects.nonNull(actual.getWorkId())) {
            WorkEntity work = this.workRest.findBasicInfo(actual.getWorkId());
            if (Objects.nonNull(work.getParentId())) {
                actual.setWorkParentName(this.workHierarchyRest.findBasicInfo(work.getParentId()).getHierarchyName());
            }

            if (StringUtils.isEmpty(actual.getWorkName())) {
                actual.setWorkName(work.getWorkName());
            }
        }
    }

    /**
     * 指定した日付単位の最初の工程実績情報を取得する。
     *
     * @param condition 検索条件(kanbanId, workKanbanCollection, equipmentCollection, organizationCollection, kanbanStatusCollection, resultDailyEnum, modelName, fromDate, toDate)
     * @param authId 認証ID
     * @return 工程実績情報
     */
    @Lock(LockType.READ)
    @PUT
    @Path("first")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ActualResultEntity findFirstActualResult(ActualSearchCondition condition, @QueryParam("authId") Long authId) {
        logger.info("findFirstActualResult: {}, authId={}", condition, authId);
        return this.getActualResult(true, condition.getKanbanId(), condition.getWorkKanbanCollection(), condition.getEquipmentCollection(), 
                (Objects.nonNull(condition.getOrganizationCollection()) && !condition.getOrganizationCollection().isEmpty()) ? condition.getOrganizationCollection().get(0) : null, 
                condition.getKanbanStatusCollection(), condition.getResultDailyEnum(), condition.getModelName(), null, condition.getFromDate(), condition.getToDate());
    }

    /**
     * 指定した日付単位の最後の工程実績情報を取得する。
     *
     * @param condition 検索条件(kanbanId, workKanbanCollection, equipmentCollection, organizationCollection, kanbanStatusCollection, resultDailyEnum, modelName, fromDate, toDate)
     * @param authId 認証ID
     * @return 工程実績情報
     */
    public ActualResultEntity findLastActualResult(ActualSearchCondition condition, Long authId) {
        return this.getActualResult(false, condition.getKanbanId(), condition.getWorkKanbanCollection(), condition.getEquipmentCollection(), 
                (Objects.nonNull(condition.getOrganizationCollection()) && !condition.getOrganizationCollection().isEmpty()) ? condition.getOrganizationCollection().get(0) : null, 
                condition.getKanbanStatusCollection(), condition.getResultDailyEnum(), condition.getModelName(), condition.getSerialNo(), condition.getFromDate(), condition.getToDate());
    }

    /**
     * 指定した日付単位の最後の工程実績情報を取得する。
     *
     * @param condition 検索条件(kanbanId, workKanbanCollection, equipmentCollection, organizationCollection, kanbanStatusCollection, resultDailyEnum, modelName, fromDate, toDate)
     * @param withTrace トレーサビリティデータを付加する (FUJI岡崎様向け)
     * @param authId 認証ID
     * @return 工程実績情報
     */
    @Lock(LockType.READ)
    @PUT
    @Path("last")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ActualResultEntity findLastActualResult(ActualSearchCondition condition, @QueryParam("withTrace") Boolean withTrace, @QueryParam("authId") Long authId) {
        logger.info("findLastActualResult: {}, withTrace={}, authId={}", condition, withTrace, authId);
        ActualResultEntity actualResult = this.getActualResult(false, condition.getKanbanId(), condition.getWorkKanbanCollection(), condition.getEquipmentCollection(), 
                (Objects.nonNull(condition.getOrganizationCollection()) && !condition.getOrganizationCollection().isEmpty()) ? condition.getOrganizationCollection().get(0) : null,
                condition.getKanbanStatusCollection(), condition.getResultDailyEnum(), condition.getModelName(), condition.getSerialNo(), condition.getFromDate(), condition.getToDate());

        // トレーサビリティデータを付加する (FUJI岡崎様向け)
        if (Objects.nonNull(actualResult) 
                && Objects.nonNull(withTrace) && withTrace
                && Objects.nonNull(condition.getWorkKanbanCollection()) && !condition.getWorkKanbanCollection().isEmpty()) {
            TraceabilityJdbc jbdc = TraceabilityJdbc.getInstance();
            List<TraceabilityEntity> traceabilities = jbdc.getWorkKanbanTraceability(condition.getWorkKanbanCollection().get(0), false);
            String str = JsonUtils.objectToJson(traceabilities);
            actualResult.setTraceabilities(str);
        }

        return actualResult;
    }
    
    /**
     * 指定した日付単位の最後の工程実績情報を取得する。
     *
     * @param condition 検索条件(kanbanId, workKanbanCollection, equipmentCollection, organizationCollection, kanbanStatusCollection, resultDailyEnum, modelName, fromDate, toDate)
     * @param authId 認証ID
     * @return 工程実績情報
     */
    @Lock(LockType.READ)
    @PUT
    @Path("last/list")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<ActualResultEntity> findLastActualResulList(ActualSearchCondition condition, @QueryParam("authId") Long authId) {
        logger.info("findLastActualResultList: {}, authId={}", condition, authId);
        List<ActualResultEntity> resultList = new ArrayList<>();
        
        if (Objects.isNull(condition.getOrganizationCollection()) 
                || condition.getOrganizationCollection().isEmpty()) {

            if (Objects.nonNull(condition.getEquipmentCollection())) {
                // 設備別
                for (Long equipmentId : condition.getEquipmentCollection()) {
                    ActualResultEntity actual = this.getActualResult(false, condition.getKanbanId(), condition.getWorkKanbanCollection(), 
                            Arrays.asList(equipmentId), null, condition.getKanbanStatusCollection(), condition.getResultDailyEnum(), 
                            condition.getModelName(), condition.getSerialNo(), condition.getFromDate(), condition.getToDate());
                    if (Objects.nonNull(actual)) {
                        resultList.add(actual);
                    }
                }
            }
            return resultList;
        }
        
        // 作業者別
        for (Long organizationId : condition.getOrganizationCollection()) {
            ActualResultEntity actual = this.getActualResult(false, condition.getKanbanId(), condition.getWorkKanbanCollection(), 
                    condition.getEquipmentCollection(), organizationId, condition.getKanbanStatusCollection(), condition.getResultDailyEnum(), 
                    condition.getModelName(), condition.getSerialNo(), condition.getFromDate(), condition.getToDate());
            
            if (Objects.nonNull(actual)) {
                resultList.add(actual);
            }
        }
        
        return resultList;
    }

    /**
     * ライン(設備ID一覧)・モデル名・日時範囲を指定して、タイムライン情報(VIEW)を取得する。
     *
     * @param equipmentIds 設備ID一覧
     * @param modelName モデル名
     * @param fromDate 日時範囲の先頭
     * @param toDate 日時範囲の末尾
     * @return タイムライン情報
     */
    @Lock(LockType.READ)
    @ExecutionTimeLogging
    public TimeLineEntity getFirstTimeLine(List<Long> equipmentIds, String modelName, Date fromDate, Date toDate) {
        logger.info("getFirstTimeLine: equipmentIds={}, modelName={}, fromDate={}, toDate={}", equipmentIds, modelName, fromDate, toDate);
        try {
            // 設備IDが未指定の場合、nullを返す。
            if (Objects.isNull(equipmentIds) || equipmentIds.isEmpty()) {
                return null;
            }

            this.em.getEntityManagerFactory().getCache().evict(TimeLineEntity.class);

            // 設備ID一覧・モデル名・日時範囲を指定して、タイムライン情報(VIEW)を取得する。
            TypedQuery<TimeLineEntity> query = this.em.createNamedQuery("TimeLineEntity.findByEquipmentIds", TimeLineEntity.class);
            query.setParameter("fromDate", fromDate, TemporalType.TIMESTAMP);
            query.setParameter("toDate", toDate, TemporalType.TIMESTAMP);
            query.setParameter("equipmentIds", equipmentIds);
            query.setParameter("modelName", StringUtils.escapeLike(modelName));
            query.setMaxResults(1);

            return query.getSingleResult();
        } catch (Exception ex) {
            logger.warn("Actual data does not exist.");
            return null;
        } finally {
            logger.info("getFirstTimeLine end.");
        }
    }

    /**
     * 指定した日付単位の最初または最後の実績通知を取得する。
     *
     * @param isFirst true:最初, false:最後
     * @param kanbanId カンバンID
     * @param workKanbanIds 工程カンバンID
     * @param equipmentIds 設備ID
     * @param organizationIds 組織ID
     * @param actualStatuses 実績ステータス
     * @param unit 日付単位
     * @param modelName モデル名
     * @param serialNo シリアル番号
     * @param fromDate 日時範囲の先頭 (unit が ActualResultDailyEnum.ALL の場合のみ有効)
     * @param toDate 日時範囲の末尾 (unit が ActualResultDailyEnum.ALL の場合のみ有効)
     * @return
     */
    @Lock(LockType.READ)
    private ActualResultEntity getActualResult(boolean isFirst, Long kanbanId, List<Long> workKanbanIds, List<Long> equipmentIds,
            Long organizationId, List<KanbanStatusEnum> actualStatuses, ActualResultDailyEnum unit, String modelName, String serialNo, 
            Date fromDate, Date toDate) {
        logger.info("getActualResult: isFirst={}, kanbanId={}, workKanbanIds={}, equipmentIds={}, organizationId={}, actualStatuses={}, unit={}, modelName={}, fromDate={}, toDate={}", isFirst, kanbanId, workKanbanIds, equipmentIds, organizationId, actualStatuses, unit, modelName, fromDate, toDate);
        try {
            CriteriaBuilder cb = this.em.getCriteriaBuilder();
            CriteriaQuery cq = cb.createQuery();

            Root<ActualResultEntity> poolActual = cq.from(ActualResultEntity.class);

            jakarta.persistence.criteria.Path<Long> pathActualId = poolActual.get(ActualResultEntity_.actualId);
            jakarta.persistence.criteria.Path<Date> pathImplementDatetime = poolActual.get(ActualResultEntity_.implementDatetime);
            jakarta.persistence.criteria.Path<KanbanStatusEnum> pathActualStatus = poolActual.get(ActualResultEntity_.actualStatus);

            jakarta.persistence.criteria.Path<Long> pathKanbanId = poolActual.get(ActualResultEntity_.kanbanId);
            jakarta.persistence.criteria.Path<Long> pathWorkKanbanId = poolActual.get(ActualResultEntity_.workKanbanId);
            jakarta.persistence.criteria.Path<Long> pathEquipmentId = poolActual.get(ActualResultEntity_.equipmentId);
            jakarta.persistence.criteria.Path<Long> pathOrganizationId = poolActual.get(ActualResultEntity_.organizationId);
            jakarta.persistence.criteria.Path<String> pathSerialNo = poolActual.get(ActualResultEntity_.serialNo);

            // 検索条件
            List<Predicate> where = new ArrayList();

            where.add(cb.greaterThan(pathKanbanId, 0L));

            // モデル名
            if (Objects.nonNull(modelName) && !modelName.isEmpty()) {
                Root<KanbanEntity> subKan = cq.from(KanbanEntity.class);

                jakarta.persistence.criteria.Path<Long> pathKanId = subKan.get(KanbanEntity_.kanbanId);
                jakarta.persistence.criteria.Path<String> pathModelName = subKan.get(KanbanEntity_.modelName);

                Subquery<Long> conSubquery = cq.subquery(Long.class);

                String prefix = "";
                if (modelName.startsWith(".*")) {
                    prefix = "%";
                }

                String suffix = "";
                if (modelName.endsWith(".*")) {
                    suffix = "%";
                }

                String searchModelName = modelName.replaceFirst("^(\\.\\*)", "").replaceFirst("(\\.\\*)$", "");

                conSubquery.select(pathKanId).where(cb.or(
                        cb.like(cb.lower(pathModelName), prefix + StringUtils.escapeLikeChar(StringUtils.toLowerCase(searchModelName)) + suffix),
                        cb.like(pathModelName, prefix + StringUtils.escapeLikeChar(searchModelName) + suffix)
                ));

                where.add(pathKanbanId.in(conSubquery));

                // TODO: 工程実績にモデル名を追加したら、下の処理に変える。
//                where.add(cb.or(
//                        cb.like(cb.lower(pathModelName), prefix + StringUtils.escapeLikeChar(StringUtils.toLowerCase(searchModelName)) + suffix),
//                        cb.like(pathModelName, prefix + StringUtils.escapeLikeChar(searchModelName) + suffix)
//                ));
            }

            // カンバンID
            if (Objects.nonNull(kanbanId)) {
                where.add(cb.equal(pathKanbanId, kanbanId));
            }

            // 工程カンバンID
            if (Objects.nonNull(workKanbanIds) && !workKanbanIds.isEmpty()) {
                where.add(cb.equal(pathWorkKanbanId, workKanbanIds.get(0)));
            }

            // 設備ID
            if (Objects.nonNull(equipmentIds) && !equipmentIds.isEmpty()) {
                where.add(cb.equal(pathEquipmentId, equipmentIds.get(0)));
            }
            
            // 組織ID
            if (Objects.nonNull(organizationId)) {
                where.add(cb.equal(pathOrganizationId, organizationId));
            }

            // 工程実績ステータス
            if (Objects.nonNull(actualStatuses) && !actualStatuses.isEmpty()) {
                where.add(cb.equal(pathActualStatus, actualStatuses.get(0)));
            }
            
            // シリアル番号
            if (!StringUtils.isEmpty(serialNo)) {
                where.add(cb.equal(pathSerialNo, serialNo));
            }

            // 実施日時
            if (unit.equals(ActualResultDailyEnum.ALL)) {
                // ALL
                if (Objects.nonNull(fromDate)) {
                    where.add(cb.greaterThanOrEqualTo(pathImplementDatetime, fromDate));
                }
                if (Objects.nonNull(toDate)) {
                    where.add(cb.lessThanOrEqualTo(pathImplementDatetime, toDate));
                }
            } else {
                // DAILY
                Date now = new Date();
                Date fromDt = DateUtils.getBeginningOfDate(now);
                Date toDt = DateUtils.getEndOfDate(now);

                where.add(cb.greaterThanOrEqualTo(pathImplementDatetime, fromDt));
                where.add(cb.lessThanOrEqualTo(pathImplementDatetime, toDt));
            }

            // 最初か最後か
            List<Order> orders;
            if (isFirst) {
                orders = Arrays.asList(cb.asc(pathImplementDatetime), cb.asc(pathActualId));
            } else {
                orders = Arrays.asList(cb.desc(pathImplementDatetime), cb.desc(pathActualId));
            }

            cq.select(poolActual)
                    .where(cb.and(where.toArray(new Predicate[where.size()])))
                    .orderBy(orders);

            Query query = this.em.createQuery(cq);

            query.setMaxResults(1);

            return (ActualResultEntity) query.getSingleResult();
        } catch (NoResultException e) {
            logger.warn("Actual data does not exist.");
            return null;
        }
    }

    /**
     * 工程毎の生産情報を取得する。(当日分)
     *
     * @param workIds 工程ID一覧
     * @param authId 認証ID
     * @return 生産情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("daily/work/productivity")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<ProductivityEntity> findDailyWorkProductivity(@QueryParam("id") final List<Long> workIds, @QueryParam("authId") Long authId) {
        logger.info("findDailyWorkProductivity: workIds={}, authId={}", workIds, authId);
        try {
            // 工程IDが未指定の場合、空のリストを返す。
            if (Objects.isNull(workIds) || workIds.isEmpty()) {
                return new ArrayList();
            }

            Date now = new Date();
            Date fromDate = DateUtils.getBeginningOfDate(now);
            Date toDate = new Date(fromDate.getTime() + (3600 * 24 * 1000));

            // 工程ID一覧・実績日時の範囲を指定して、工程別生産情報を取得する。
            TypedQuery<ProductivityEntity> query = this.em.createNamedQuery("ProductivityEntity.completionByWorkId", ProductivityEntity.class);
            query.setParameter("fromDate", fromDate, TemporalType.TIMESTAMP);
            query.setParameter("toDate", toDate, TemporalType.TIMESTAMP);
            query.setParameter("workIds", workIds);

            return query.getResultList();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        } finally {
            logger.info("findDailyWorkProductivity end.");
        }
    }

    /**
     * 設備毎の生産情報を取得する。(当日分)
     *
     * @param equipmentIds 設備ID一覧
     * @param authId 認証ID
     * @return 生産情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("daily/equipment/productivity")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<ProductivityEntity> findDailyEquipmentProductivity(@QueryParam("id") final List<Long> equipmentIds, @QueryParam("authId") Long authId) {
        logger.info("findDailyEquipmentProductivity: equipmentIds={}, authId={}", equipmentIds, authId);
        try {
            // 設備IDが未指定の場合、空のリストを返す。
            if (Objects.isNull(equipmentIds) || equipmentIds.isEmpty()) {
                return new ArrayList();
            }

            Date now = new Date();
            Date fromDate = DateUtils.getBeginningOfDate(now);
            Date toDate = new Date(fromDate.getTime() + (3600 * 24 * 1000));

            // 設備ID一覧・実績日時の範囲を指定して、設備毎の生産情報を取得する。
            TypedQuery<ProductivityEntity> query = this.em.createNamedQuery("ProductivityEntity.completionByEquipmentId", ProductivityEntity.class);
            query.setParameter("fromDate", fromDate, TemporalType.TIMESTAMP);
            query.setParameter("toDate", toDate, TemporalType.TIMESTAMP);
            query.setParameter("equipmentIds", equipmentIds);

            return query.getResultList();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        } finally {
            logger.info("findDailyEquipmentProductivity end.");
        }
    }

    /**
     * 工程毎の不具合情報を取得する。(当日分)
     *
     * @param workIds 工程ID一覧
     * @param authId 認証ID
     * @return 不具合情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("daily/work/defect")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<DefectEntity> findDailyWorkDefect(@QueryParam("id") final List<Long> workIds, @QueryParam("authId") Long authId) {
        logger.info("findDailyWorkDefect: workIds={}, authId={}", workIds, authId);
        try {
            // 工程IDが未指定の場合、空のリストを返す。
            if (Objects.isNull(workIds) || workIds.isEmpty()) {
                return new ArrayList();
            }

            Date now = new Date();
            Date fromDate = DateUtils.getBeginningOfDate(now);
            Date toDate = new Date(fromDate.getTime() + (3600 * 24 * 1000));

            // 設備ID一覧・実績日時の範囲を指定して、工程毎の不具合情報を取得する。
            TypedQuery<DefectEntity> suspendQuery = this.em.createNamedQuery("DefectEntity.suspendByWorkId", DefectEntity.class);
            suspendQuery.setParameter("fromDate", fromDate, TemporalType.TIMESTAMP);
            suspendQuery.setParameter("toDate", toDate, TemporalType.TIMESTAMP);
            suspendQuery.setParameter("workIds", workIds);

            List<DefectEntity> result = suspendQuery.getResultList();
            List<Long> list = result.stream().map(o -> o.getId()).collect(Collectors.toList());
            if (list.isEmpty()) {
                return new ArrayList();
            }

            // 工程ID一覧・実績日時の範囲を指定して、工程別生産情報を取得する。
            TypedQuery<ProductivityEntity> completionQuery = this.em.createNamedQuery("ProductivityEntity.completionByWorkId", ProductivityEntity.class);
            completionQuery.setParameter("fromDate", fromDate, TemporalType.TIMESTAMP);
            completionQuery.setParameter("toDate", toDate, TemporalType.TIMESTAMP);
            completionQuery.setParameter("workIds", list);

            // 結果を結合する。
            List<ProductivityEntity> productivities = completionQuery.getResultList();
            Map<Long, ProductivityEntity> map = productivities.stream()
                    .collect(Collectors.toMap(ProductivityEntity::getId, d -> d));
            for (DefectEntity defect : result) {
                if (map.containsKey(defect.getId())) {
                    ProductivityEntity productivity = map.get(defect.getId());
                    defect.setProdCount(productivity.getProdCount());
                }
            }

            return result;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        } finally {
            logger.info("findDailyWorkDefect end.");
        }
    }

    /**
     * 作業履歴情報(VIEW)の件数を取得する。
     *
     * @param type 種別(組織, カンバン)
     * @param primaryIds ID一覧(組織ID, カンバンID)
     * @param fromDate 日時範囲の先頭
     * @param toDate 日時範囲の末尾
     * @param authId 認証ID
     * @return 作業履歴情報の件数
     */
    @Lock(LockType.READ)
    @GET
    @Path("history/{type}/count")
    @Produces({"text/plain"})
    @ExecutionTimeLogging
    public String countHistory(@PathParam("type") final String type, @QueryParam("id") final List<Long> primaryIds, @QueryParam("fromDate") final String fromDate, @QueryParam("toDate") final String toDate, @QueryParam("authId") Long authId) {
        logger.info("countHistory: type={}, primaryIds={}, fromDate={}, toDate={}, authId={}", type, primaryIds, fromDate, toDate, authId);
        try {
            // IDが未指定の場合、「0」を返す。
            if (Objects.isNull(primaryIds) || primaryIds.isEmpty()) {
                return "0";
            }

            this.em.getEntityManagerFactory().getCache().evict(WorkRecordEntity.class);

            Date fromDateTime = Objects.nonNull(fromDate) ? org.apache.commons.lang3.time.DateUtils.parseDate(fromDate, pattern) : new Date();
            fromDateTime = DateUtils.getBeginningOfDate(fromDateTime);

            Date toDateTime = Objects.nonNull(toDate) ? org.apache.commons.lang3.time.DateUtils.parseDate(toDate, pattern) : new Date();
            toDateTime = DateUtils.getEndOfDate(toDateTime);

            TypedQuery<Long> query;
            if (type.equals(Constants.ORGANIZATION)) {
                // 組織ID一覧・実績日時の範囲を指定して、作業履歴の件数を取得する。
                query = this.em.createNamedQuery("WorkRecordEntity.countActualByOrganizationId", Long.class);
                query.setParameter("organizationIds", primaryIds);
            } else {
                // カンバンID一覧・実績日時の範囲を指定して、作業履歴の件数を取得する。
                query = this.em.createNamedQuery("WorkRecordEntity.countActualByKanbanId", Long.class);
                query.setParameter("kanbanIds", primaryIds);
            }

            query.setParameter("fromDate", fromDateTime, TemporalType.TIMESTAMP);
            query.setParameter("toDate", toDateTime, TemporalType.TIMESTAMP);

            return String.valueOf(query.getSingleResult());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return "0";
        } finally {
            logger.info("countHistory end.");
        }
    }

    /**
     * 作業履歴情報(VIEW)一覧を取得する。
     *
     * @param type 種別(組織, カンバン)
     * @param primaryIds ID一覧(組織ID, カンバンID)
     * @param fromDate 日時範囲の先頭
     * @param toDate 日時範囲の末尾
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 作業履歴情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("history/{type}/range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkRecordEntity> findHistory(@PathParam("type") final String type, @QueryParam("id") final List<Long> primaryIds, @QueryParam("fromDate") final String fromDate, @QueryParam("toDate") final String toDate, @QueryParam("from") final Integer from, @QueryParam("to") final Integer to, @QueryParam("authId") Long authId) {
        logger.info("findHistory: type={}, primaryIds={}, fromDate={}, toDate={}, from={}, to={}, authId={}", type, primaryIds, fromDate, toDate, from, to, authId);
        try {
            // IDが未指定の場合、空のリストを返す。
            if (Objects.isNull(primaryIds) || primaryIds.isEmpty()) {
                return new ArrayList();
            }

            this.em.getEntityManagerFactory().getCache().evict(WorkRecordEntity.class);

            Date fromDateTime = Objects.nonNull(fromDate) ? org.apache.commons.lang3.time.DateUtils.parseDate(fromDate, pattern) : new Date();
            fromDateTime = DateUtils.getBeginningOfDate(fromDateTime);

            Date toDateTime = Objects.nonNull(toDate) ? org.apache.commons.lang3.time.DateUtils.parseDate(toDate, pattern) : new Date();
            toDateTime = DateUtils.getEndOfDate(toDateTime);

            TypedQuery<WorkRecordEntity> query;
            if (type.equalsIgnoreCase(Constants.ORGANIZATION)) {
                // 組織ID一覧・実績日時の範囲を指定して、作業履歴情報を取得する。
                query = this.em.createNamedQuery("WorkRecordEntity.findActualByOrganizationId", WorkRecordEntity.class);
                query.setParameter("organizationIds", primaryIds);
                query.setParameter("fromDate", fromDateTime, TemporalType.TIMESTAMP);
                query.setParameter("toDate", toDateTime, TemporalType.TIMESTAMP);
            } else if (type.equalsIgnoreCase(Constants.KANBAN)) {
                // カンバンID一覧・実績日時の範囲を指定して、作業履歴情報を取得する。
                query = this.em.createNamedQuery("WorkRecordEntity.findActualByKanbanId", WorkRecordEntity.class);
                query.setParameter("kanbanIds", primaryIds);
                query.setParameter("fromDate", fromDateTime, TemporalType.TIMESTAMP);
                query.setParameter("toDate", toDateTime, TemporalType.TIMESTAMP);
            } else {
                query = this.em.createNamedQuery("WorkRecordEntity.findActualByWorkKanbanId", WorkRecordEntity.class);
                query.setParameter("workKanbanIds", primaryIds);
            }

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                query.setMaxResults(to - from + 1);
                query.setFirstResult(from);
            }

            return query.getResultList();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList();
        } finally {
            logger.info("findHistory end.");
        }
    }

    /**
     * 品質トレーサビリティ情報一覧を取得する。
     *
     * @param tagNames タグ名
     * @param fromDate 開始日時
     * @param toDate 終了日時
     * @param from データ取得開始インデックス
     * @param days 何日分のデータを取得するか (開始日時を設定した場合は無効)
     * @param max 最大取得データ数
     * @param authId 認証ID
     * @return 品質トレーサビリティ情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("trace")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<TraceEntity> findTrace(@QueryParam("tag") final List<String> tagNames, @QueryParam("fromDate") final String fromDate, @QueryParam("toDate") final String toDate, @QueryParam("from") final Integer from, @QueryParam("days") Integer days, @QueryParam("max") final Integer max, @QueryParam("authId") Long authId) {
        logger.info("findTrace: tagNames={}, fromDate={}, toDate={}, from={}, days={}, max={}, authId={}", tagNames, fromDate, toDate, from, days, max, authId);
        try {
            // タグ名が未指定の場合、空のリストを返す。
            if (Objects.isNull(tagNames) || tagNames.isEmpty()) {
                return new ArrayList();
            }

            this.em.getEntityManagerFactory().getCache().evict(TraceEntity.class);

            Date toDateTime = Objects.nonNull(toDate) ? org.apache.commons.lang3.time.DateUtils.parseDate(toDate, pattern) : new Date();
            toDateTime = DateUtils.getEndOfDate(toDateTime);

            Date fromDateTime;
            if (Objects.nonNull(fromDate)) {
                fromDateTime = org.apache.commons.lang3.time.DateUtils.parseDate(fromDate, pattern);
            } else {
                Calendar cal = Calendar.getInstance();
                cal.setTime(toDateTime);
                if (Objects.nonNull(days)) {
                    cal.add(Calendar.DATE, -days);
                } else {
                    cal.add(Calendar.MONTH, -1);
                }
                fromDateTime = cal.getTime();
            }
            fromDateTime = DateUtils.getBeginningOfDate(fromDateTime);

            // タグ名一覧をSQLパラメータ用の配列に変換する。
            java.sql.Array tagNamesArray = this.em.unwrap(Connection.class).createArrayOf("varchar", tagNames.toArray());

            // タグ名(プロパティ名)・日時範囲を指定して、品質トレーサビリティ情報(VIEW)一覧を取得する。
            TypedQuery<TraceEntity> query = this.em.createNamedQuery("ActualResultEntity.findByTagName", TraceEntity.class);

            query.setParameter(1, fromDateTime, TemporalType.TIMESTAMP);
            query.setParameter(2, toDateTime, TemporalType.TIMESTAMP);
            query.setParameter(3, tagNamesArray);

            if (Objects.nonNull(max) && max > 0) {
                query.setMaxResults(max);// 最大取得件数
            }

            if (Objects.nonNull(from) && from >= 0) {
                query.setFirstResult(from);
            } else {
                // タグ名(プロパティ名)・日時範囲を指定して、品質トレーサビリティ情報(VIEW)の件数を取得する。
                TypedQuery<Long> countQuery = this.em.createNamedQuery("ActualResultEntity.countByTagName", Long.class);

                countQuery.setParameter(1, fromDateTime, TemporalType.TIMESTAMP);
                countQuery.setParameter(2, toDateTime, TemporalType.TIMESTAMP);
                countQuery.setParameter(3, tagNamesArray);

                Long count = countQuery.getSingleResult();
                if (count.intValue() > max) {
                    query.setFirstResult(count.intValue() - max);
                }
            }

            return query.getResultList();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList();
        } finally {
            logger.info("getTrace end.");
        }
    }

    /**
     * カンバンIDで工程実績を削除する。
     *
     * @param kanbanId カンバンID
     */
    public void removeKanbanActuals(Long kanbanId) {
        logger.info("removeKanbanActuals: kanbanId={}", kanbanId);

        // カンバンIDで工程実績を削除する
        Query query = this.em.createNamedQuery("ActualResultEntity.removeByKanbanId");
        query.setParameter("kanbanId", kanbanId);
        query.executeUpdate();
    }

    /**
     * 工程実績IDを指定して、実績出力情報(VIEW)を取得する。
     *
     * @param id 工程実績ID
     * @param authId 認証ID
     * @return 実績出力情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("reportout/{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ReportOutEntity findReportOut(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("findReportOut: id={}, authId={}", id, authId);
        try {
            // 実績IDを指定して、実績出力情報を取得する。
            TypedQuery<ReportOutEntity> query = this.em.createNamedQuery("ReportOutEntity.findByActualId", ReportOutEntity.class);
            query.setParameter("actualId", id);
            return query.getSingleResult();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 条件を指定して、実績出力情報一覧を取得する。 (パラメータの「from」と「to」の両方指定がある場合は範囲取得する)
     *
     * @param condition 検索条件
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 実績出力情報一覧
     */
    @Lock(LockType.READ)
    @PUT
    @Path("reportout/search/range")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<ReportOutEntity> searchReportOut(ReportOutSearchCondition condition, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("searchReportOut: {}, from={}, to={}, authId={}", condition, from, to, authId);
        List<ReportOutEntity> result = null;
        try {
            // キャッシュをクリア
            this.em.clear();
            
            Query query = this.getReportOutSearchQuery(SearchType.SEARCH, condition);

            // キャッシュを無効にする
            query.setHint(QueryHints.CACHE_USAGE, CacheUsage.DoNotCheckCache);

            // from, to 両方指定がある場合は範囲取得する。
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                query.setMaxResults(to - from + 1);
                query.setFirstResult(from);
            }

            result = query.getResultList();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * 条件を指定して、間接作業実績出力情報一覧を取得する。 (パラメータの「from」と「to」の両方指定がある場合は範囲取得する)
     *
     * @param condition 検索条件
     * @param authId 認証ID
     * @return 間接実績出力情報一覧
     */
    @Lock(LockType.READ)
    @PUT
    @Path("indirect-reportout/search/range")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<IndirectWorkReportOutEntity> searchIndirectReportOut(IndirectWorkSearchCondition condition, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        TypedQuery<IndirectWorkReportOutEntity> query = this.em.createNamedQuery("IndirectWorkReportOutEntity.reportOut", IndirectWorkReportOutEntity.class);

        query.setParameter(1, condition.getFromDate(), TemporalType.TIMESTAMP);
        query.setParameter(2, condition.getToDate(), TemporalType.TIMESTAMP);


        return query.getResultList();
    }


    /**
     * 条件を指定して、実績出力情報の件数を取得する。
     *
     * @param condition 検索条件
     * @param authId 認証ID
     * @return 件数
     */
    @Lock(LockType.READ)
    @PUT
    @Path("reportout/search/count")
    @Consumes({"application/xml", "application/json"})
    @Produces({"text/plain"})
    @ExecutionTimeLogging
    public String countReportOut(ReportOutSearchCondition condition, @QueryParam("authId") Long authId) {
        logger.info("countReportOut: {}, authId={}", condition, authId);
        String result = null;
        try {
            // キャッシュをクリア
            this.em.clear();

            Query query = this.getReportOutSearchQuery(SearchType.COUNT, condition);
            // キャッシュを無効にする
            query.setHint(QueryHints.CACHE_USAGE, CacheUsage.DoNotCheckCache);
            Object count = query.getSingleResult();
            if (Objects.isNull(count)) {
                count = 0;
            }

            result = String.valueOf(count);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * ライン(設備ID一覧)・モデル名・日時範囲を指定して、生産数を取得する。
     *
     * @param equipmentIds 設備ID一覧
     * @param fromDate 日時範囲の先頭
     * @param toDate 日時範囲の末尾
     * @param modelName モデル名
     * @return
     */
    @Lock(LockType.READ)
    public Long getLineProduct(List<Long> equipmentIds, Date fromDate, Date toDate, String modelName) {
        logger.info("getLineProduct: equipmentIds={}, fromDate={}, toDate={}, modelName={}", equipmentIds, fromDate, toDate, modelName);
        try {
            CriteriaBuilder cb = this.em.getCriteriaBuilder();
            CriteriaQuery cq = cb.createQuery();

            Root<LineProductEntity> poolLineProduct = cq.from(LineProductEntity.class);

            jakarta.persistence.criteria.Path<BigInteger> pathEquipmentId = poolLineProduct.get(LineProductEntity_.equipmentId);
            jakarta.persistence.criteria.Path<Date> pathActualEndTime = poolLineProduct.get(LineProductEntity_.actualEndTime);
            jakarta.persistence.criteria.Path<String> pathModelName = poolLineProduct.get(LineProductEntity_.modelName);
            jakarta.persistence.criteria.Path<Integer> pathLotQuantity = poolLineProduct.get(LineProductEntity_.lotQuantity);
            jakarta.persistence.criteria.Path<Integer> pathDefectNum = poolLineProduct.get(LineProductEntity_.defectNum);

            // 検索条件
            List<Predicate> where = new ArrayList<>();
            where.add(pathEquipmentId.in(equipmentIds));
            where.add(cb.greaterThanOrEqualTo(pathActualEndTime, fromDate));
            where.add(cb.lessThanOrEqualTo(pathActualEndTime, toDate));

            if (Objects.nonNull(modelName) && !modelName.isEmpty()) {
                where.add(cb.like(pathModelName, StringUtils.escapeLike(modelName)));
            }

            // ロット数量が null の場合は「1」とする。
            CriteriaBuilder.Coalesce<Integer> coLotQuantity = cb.coalesce();
            coLotQuantity.value(pathLotQuantity);
            coLotQuantity.value(1);

            // 完了数(ロット数量の合計)を取得する。
            cq.select(cb.sumAsLong(coLotQuantity))
                    .where(cb.and(where.toArray(new Predicate[where.size()])));

            Query query = this.em.createQuery(cq);

            Object result = query.getSingleResult();
            if (Objects.isNull(result)) {
                // 対象レコードが0件の場合はnullになるので、完了数0件で返す。
                return 0L;
            }

            return (Long) result;
        } finally {
            logger.info("getLineProduct end.");
        }
    }

    /**
     * 実績出力情報の検索クエリを取得する。
     *
     * @param type クエリ種別 (SEARCH：一覧取得, COUNT：完成数取得)
     * @param condition 検索条件
     * @return 検索クエリ
     */
    @Lock(LockType.READ)
    private Query getReportOutSearchQuery(SearchType type, ReportOutSearchCondition condition) {
        TypedQuery<ReportOutEntity> query;

        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();

        Root<ReportOutEntity> poolReportOut = cq.from(ReportOutEntity.class);

        // 項目のパス
        jakarta.persistence.criteria.Path<Long> pathActualId = poolReportOut.get(ReportOutEntity_.actualId);
        jakarta.persistence.criteria.Path<Date> pathImplementDatetime = poolReportOut.get(ReportOutEntity_.implementDatetime);
        jakarta.persistence.criteria.Path<Integer> pathCompNum = poolReportOut.get(ReportOutEntity_.compNum);

        // 検索条件
        Predicate where = this.getReportOutWhere(condition, poolReportOut);

        if (type == SearchType.SEARCH) {
            // 一覧取得
            switch (condition.getSortType()) {
                case ACTUAL_ID:// 工程実績ID順
                    cq.select(poolReportOut)
                            .where(where)
                            .orderBy(cb.asc(pathActualId));
                    break;
                case IMPLEMENT_DATETIME:// 実施日時順
                default:
                    cq.select(poolReportOut)
                            .where(where)
                            .orderBy(cb.asc(pathImplementDatetime), cb.asc(pathActualId));
            }
        } else {
            // 完成数の合計を取得
            if (condition.isCountLatestEquipment() || condition.isDistinctWorkKanban()) {
                // 工程計画実績数フレーム (adMonitorEquipmentPlanNumPlugin)
                // 当日工程計画実績数フレーム (adAndonDailyWorkPlanNumPlugin)

                // 一番最後に完了を押した設備がその工程カンバンを作業したとしてカウント(複数人作業向け)
                Subquery<Long> sub = cq.subquery(Long.class);
                Root<ReportOutEntity> rootSub = sub.from(ReportOutEntity.class);

                jakarta.persistence.criteria.Path<Long> pathSubWorkKanbanId = rootSub.get(ReportOutEntity_.workKanbanId);

                // 完成数が null の場合は「1」とする。
                CriteriaBuilder.Coalesce<Integer> coCompNum = cb.coalesce();
                coCompNum.value(pathCompNum);
                coCompNum.value(1);

                // １つの工程カンバンに複数の完了実績がある場合、最後の実績のみカウント対象とする。(複数人作業向け)
                sub.select(cb.max(rootSub.<Long>get("implementDatetime")))
                        .groupBy(pathSubWorkKanbanId);

                // 完了数(完成数の合計)を取得する。
                cq.select(cb.sumAsLong(coCompNum))
                        .where(cb.and(where, pathImplementDatetime.in(sub)));
            } else {
                // 実績出力情報の件数
                cq.select(cb.count(poolReportOut))
                        .where(where);
            }
        }
        query = this.em.createQuery(cq);

        return query;
    }

    /**
     * 設備ID一覧・日時範囲を指定して、カンバンID一覧を取得する。
     *
     * @param equipmentIds 設備ID一覧
     * @param fromDate 日時範囲の先頭
     * @param toDate 日時範囲の末尾
     * @return カンバンID一覧
     */
    @Lock(LockType.READ)
    @ExecutionTimeLogging
    public List<Long> findKanban(List<Long> equipmentIds, Date fromDate, Date toDate) {
        logger.info("findKanban: equipmentIds={}, fromDate={}, toDate={}", equipmentIds, fromDate, toDate);
        try {
            // 設備ID一覧・日時範囲を指定して、カンバンID一覧を取得する。
            Query query = this.em.createNamedQuery("ActualResultEntity.findKanbanByEquipmentId", WorkKanbanTopicEntity.class);
            query.setParameter("equipmentIds", equipmentIds);
            query.setParameter("fromDate", fromDate, TemporalType.TIMESTAMP);
            query.setParameter("toDate", toDate, TemporalType.TIMESTAMP);

            return query.getResultList();
        } finally {
            logger.info("findKanban end.");
        }
    }

    /**
     * 設備ID一覧・モデル名・日時範囲を指定して、カンバンID一覧を取得する。
     *
     * @param equipmentIds 設備ID一覧
     * @param modelName 擬似的な正規表現で表されるモデル名 (「.*」は任意の文字列、「%」はそのまま%の文字として解釈される)
     * @param fromDate 日時範囲の先頭
     * @param toDate 日時範囲の末尾
     * @return カンバンID一覧
     */
    @Lock(LockType.READ)
    @ExecutionTimeLogging
    public List<Long> findKanban(List<Long> equipmentIds, String modelName, Date fromDate, Date toDate) {
        logger.info("findKanban: equipmentIds={}, modelName={}, fromDate={}, toDate={}", equipmentIds, modelName, fromDate, toDate);
        try {
            // 設備ID一覧・モデル名・日時範囲を指定して、カンバンID一覧を取得する。
            Query query = this.em.createNamedQuery("ActualResultEntity.findKanbanByEquipmentIdAndModelName", WorkKanbanTopicEntity.class);
            query.setParameter("equipmentIds", equipmentIds);
            query.setParameter("fromDate", fromDate, TemporalType.TIMESTAMP);
            query.setParameter("toDate", toDate, TemporalType.TIMESTAMP);

            if (Objects.isNull(modelName) || StringUtils.isEmpty(modelName)) {
                query.setParameter("modelName", "%");
            } else {
                String a = StringUtils.escapeLike(modelName);
                query.setParameter("modelName", a);
            }

            return query.getResultList();
        } finally {
            logger.info("findKanban end.");
        }
    }

    /**
     * 設備別に実績を集計する。
     * (NativeQueryで取得)
     *
     * @param equimpmentIds 設備ID一覧
     * @param statuses 工程実績ステータス一覧
     * @param modelName モデル名
     * @param date 日付
     * @return 実績数
     */
    @Lock(LockType.READ)
    public Integer sumByEquipmentId(List<Long> equimpmentIds, List<KanbanStatusEnum> statuses, String modelName, Date date) {
        // 設備IDが未指定の場合、「0」を返す。
        if (Objects.isNull(equimpmentIds) || equimpmentIds.isEmpty()) {
            return 0;
        }

        // 工程実績ステータスが未指定の場合、「0」を返す。
        if (Objects.isNull(statuses) || statuses.isEmpty()) {
            return 0;
        }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT SUM(t.comp_num) FROM");
        sql.append(" (SELECT r.work_kanban_id,");
        sql.append(" (CASE WHEN SUM(r.comp_num) IS NOT NULL THEN SUM(r.comp_num) ELSE 1 END) AS comp_num");
        sql.append(" FROM trn_actual_result r");
        sql.append(" LEFT JOIN trn_kanban k ON k.kanban_id = r.kanban_id");
        sql.append(" WHERE DATE_TRUNC('DAY', r.implement_datetime) = ?date");
        sql.append(" AND r.actual_status IN (");

        for (KanbanStatusEnum staus : statuses) {
            sql.append("'");
            sql.append(staus);
            sql.append("'");
            sql.append(",");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(") AND r.equipment_id IN (");

        for (Long id : equimpmentIds) {
            sql.append(id);
            sql.append(",");
        }
        sql.deleteCharAt(sql.length() - 1);

        if (!StringUtils.isEmpty(modelName)) {
            sql.append(") AND k.model_name LIKE ?modelName");
            sql.append(" GROUP BY r.work_kanban_id) AS t");
        } else {
            sql.append(") GROUP BY r.work_kanban_id) AS t");
        }

        Query query = this.em.createNativeQuery(sql.toString());
        query.setParameter("date", date, TemporalType.DATE);
        query.setParameter("modelName", "%" + modelName + "%");

        Object result = query.getSingleResult();
        return Objects.nonNull(result) ? ((Number) result).intValue() : 0;
    }

    /**
     * 工程別に実績を集計する。
     *
     * @param workIds 工程ID一覧
     * @param statuses 工程実績ステータス一覧
     * @param modelName モデル名
     * @param date 日付
     * @return 実績数
     */
    @Lock(LockType.READ)
    public Integer sumByWorkId(List<Long> workIds, List<KanbanStatusEnum> statuses, String modelName, Date date) {
        // 工程IDが未指定の場合、「0」を返す。
        if (Objects.isNull(workIds) || workIds.isEmpty()) {
            return 0;
        }

        // 工程実績ステータスが未指定の場合、「0」を返す。
        if (Objects.isNull(statuses) || statuses.isEmpty()) {
            return 0;
        }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT SUM(t.comp_num) FROM");
        sql.append(" (SELECT r.work_kanban_id,");
        sql.append(" (CASE WHEN SUM(r.comp_num) IS NOT NULL THEN SUM(r.comp_num) ELSE 1 END) AS comp_num");
        sql.append(" FROM trn_actual_result r");
        sql.append(" LEFT JOIN trn_kanban k ON k.kanban_id = r.kanban_id");
        sql.append(" WHERE DATE_TRUNC('DAY', r.implement_datetime) = ?date");
        sql.append(" AND r.actual_status IN (");

        for (KanbanStatusEnum staus : statuses) {
            sql.append("'");
            sql.append(staus);
            sql.append("'");
            sql.append(",");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(") AND r.work_id IN (");

        for (Long id : workIds) {
            sql.append(id);
            sql.append(",");
        }
        sql.deleteCharAt(sql.length() - 1);

        if (!StringUtils.isEmpty(modelName)) {
            sql.append(") AND k.model_name LIKE ?modelName");
            sql.append(" GROUP BY r.work_kanban_id) AS t");
        } else {
            sql.append(") GROUP BY r.work_kanban_id) AS t");
        }

        Query query = this.em.createNativeQuery(sql.toString());
        query.setParameter("date", date, TemporalType.DATE);
        query.setParameter("modelName", "%" + modelName + "%");

        Object result = query.getSingleResult();
        return Objects.nonNull(result) ? ((Number) result).intValue() : 0;
    }

    /**
     * 実績出力情報の検索条件から、Predicate を取得する。
     *
     * @param condition 実績出力情報の検索条件
     * @param poolReportOut 実績出力情報のルート
     * @return Predicate(WHERE条件)
     */
    @Lock(LockType.READ)
    private Predicate getReportOutWhere(ReportOutSearchCondition condition, Root<ReportOutEntity> poolReportOut) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();

        // 項目のパス
        jakarta.persistence.criteria.Path<Date> pathImplementDatetime = poolReportOut.get(ReportOutEntity_.implementDatetime);
        jakarta.persistence.criteria.Path<KanbanStatusEnum> pathActualStatus = poolReportOut.get(ReportOutEntity_.actualStatus);
        jakarta.persistence.criteria.Path<Long> pathKanbanId = poolReportOut.get(ReportOutEntity_.kanbanId);
        jakarta.persistence.criteria.Path<Long> pathWorkflowId = poolReportOut.get(ReportOutEntity_.workflowId);
        jakarta.persistence.criteria.Path<Long> pathWorkId = poolReportOut.get(ReportOutEntity_.workId);
        jakarta.persistence.criteria.Path<Long> pathWorkKanbanId = poolReportOut.get(ReportOutEntity_.workKanbanId);

        jakarta.persistence.criteria.Path<String> pathOrganizationName = poolReportOut.get(ReportOutEntity_.organizationName);

        jakarta.persistence.criteria.Path<Long> pathEquipmentId = poolReportOut.get(ReportOutEntity_.equipmentId);
        jakarta.persistence.criteria.Path<Long> pathOrganizationId = poolReportOut.get(ReportOutEntity_.organizationId);
        jakarta.persistence.criteria.Path<String> pathKanbanName = poolReportOut.get(ReportOutEntity_.kanbanName);
        jakarta.persistence.criteria.Path<String> pathModelName = poolReportOut.get(ReportOutEntity_.modelName);
        jakarta.persistence.criteria.Path<String> pathInterruptReason = poolReportOut.get(ReportOutEntity_.interruptReason);
        jakarta.persistence.criteria.Path<String> pathDelayReason = poolReportOut.get(ReportOutEntity_.delayReason);
        jakarta.persistence.criteria.Path<String> pathProductionNumber = poolReportOut.get(ReportOutEntity_.productionNumber);

        // 検索条件
        List<Predicate> where = new ArrayList();

        // 実施日時
        if (Objects.nonNull(condition.getFromDate())) {
            where.add(cb.greaterThanOrEqualTo(pathImplementDatetime, condition.getFromDate()));
        }
        if (Objects.nonNull(condition.getToDate())) {
            where.add(cb.lessThanOrEqualTo(pathImplementDatetime, condition.getToDate()));
        }

        // 工程実績ステータス一覧
        if (Objects.nonNull(condition.getActualStatusCollection()) && !condition.getActualStatusCollection().isEmpty()) {
            where.add(pathActualStatus.in(condition.getActualStatusCollection()));
        }

        // 工程順ID (ver.1.8.1 までの検索条件)
        if (Objects.nonNull(condition.getWorkflowId())) {
            where.add(cb.equal(pathWorkflowId, condition.getWorkflowId()));
        }

        // 作業者名一覧 (ver.1.8.1 までの検索条件)
        if (Objects.nonNull(condition.getOrganizationNameCollection()) && !condition.getOrganizationNameCollection().isEmpty()) {
            List<Predicate> likeOr = new ArrayList();
            for (String name : condition.getOrganizationNameCollection()) {
                likeOr.add(cb.or(cb.like(cb.lower(pathOrganizationName), "%" + StringUtils.toLowerCase(name) + "%")));
                likeOr.add(cb.or(cb.like(pathOrganizationName, "%" + name + "%")));
            }
            where.add(cb.or(likeOr.toArray(new Predicate[likeOr.size()])));
        }

        // カンバンID一覧 (ver.1.8.2からの検索条件)
        if (Objects.nonNull(condition.getKanbanIdCollection()) && !condition.getKanbanIdCollection().isEmpty()) {
            where.add(pathKanbanId.in(condition.getKanbanIdCollection()));
        }

        // 工程順ID一覧 (ver.1.8.2からの検索条件)
        if (Objects.nonNull(condition.getWorkflowIdCollection()) && !condition.getWorkflowIdCollection().isEmpty()) {
            where.add(pathWorkflowId.in(condition.getWorkflowIdCollection()));
        }

        // 工程ID一覧 (ver.1.8.2からの検索条件)
        if (Objects.nonNull(condition.getWorkIdCollection()) && !condition.getWorkIdCollection().isEmpty()) {
            where.add(pathWorkId.in(condition.getWorkIdCollection()));
        }

        // 工程カンバンID一覧 (ver.1.8.2からの検索条件)
        if (Objects.nonNull(condition.getWorkKanbanIdCollection()) && !condition.getWorkKanbanIdCollection().isEmpty()) {
            where.add(pathWorkKanbanId.in(condition.getWorkKanbanIdCollection()));
        }

        // 設備ID一覧 (ver.1.8.2からの検索条件)
        if (Objects.nonNull(condition.getEquipmentIdCollection()) && !condition.getEquipmentIdCollection().isEmpty()) {
            // 指定した設備IDとその子以降の設備IDを、全て検索対象にする。
            Set<Long> ids = this.equipmentRest.getRelatedEquipmentIds(condition.getEquipmentIdCollection());
            where.add(pathEquipmentId.in(ids));
        }

        // 組織ID一覧 (ver.1.8.2からの検索条件)
        if (Objects.nonNull(condition.getOrganizationIdCollection()) && !condition.getOrganizationIdCollection().isEmpty()) {
            // 指定した組織IDとその子以降の組織IDを、全て検索対象にする。
            Set<Long> ids = this.organizationRest.getRelatedOrganizationIds(condition.getOrganizationIdCollection());
            where.add(pathOrganizationId.in(ids));
        }

        // カンバン名 (ver.1.8.2からの検索条件)
        if (!StringUtils.isEmpty(condition.getKanbanName())) {
            where.add(cb.like(cb.lower(pathKanbanName), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(condition.getKanbanName())) + "%"));
        }

        // モデル名 (ver.1.8.2からの検索条件)
        if (!StringUtils.isEmpty(condition.getModelName())) {
            where.add(cb.like(cb.lower(pathModelName), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(condition.getModelName())) + "%"));
        }

        // 中断理由
        if (!StringUtils.isEmpty(condition.getInterruptReason())) {
            where.add(cb.equal(pathInterruptReason, condition.getInterruptReason()));
        }

        // 遅延理由
        if (!StringUtils.isEmpty(condition.getDelayReason())) {
            where.add(cb.equal(pathDelayReason, condition.getDelayReason()));
        }
        
        // 製造番号
        if (!StringUtils.isEmpty(condition.getProductionNumber())) {
            where.add(cb.like(cb.lower(pathProductionNumber), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(condition.getProductionNumber())) + "%"));
        }
        
        return cb.and(where.toArray(new Predicate[where.size()]));
    }

    /**
     * 対象設備または工程の、指定期間中の完成数を取得する。
     *
     * @param type 実績数のカウント方法
     * @param ids 対象設備ID一覧、または対象工程ID一覧
     * @param fromDate 日時範囲の先頭
     * @param toDate 日時範囲の末尾
     * @param modelName モデル名
     * @return　完成数
     */
    @Lock(LockType.READ)
    @GET
    @Path("/completedNum")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public String getCompletedNum(@QueryParam("type") String type, @QueryParam("id") List<Long> ids, @QueryParam("fromDate")  Date fromDate, @QueryParam("toDate") Date toDate, @QueryParam("modelName") String modelName) {
        CompCountTypeEnum compCountType;
        if (StringUtils.isEmpty(type)) {
            compCountType = CompCountTypeEnum.EQUIPMENT;
        } else {
            compCountType = CompCountTypeEnum.valueOf(type);
        }
        
        if (Objects.isNull(ids) || ids.isEmpty()) {
            return "0";
        }
        
        if (Objects.isNull(fromDate)) {
            fromDate = DateUtils.getBeginningOfMonth(new Date());
        }
        
        if (Objects.isNull(toDate)) {
            toDate = DateUtils.getEndOfMonth(new Date());
        }
        
        return String.valueOf(this.getTargetCompletedNum(compCountType, ids, fromDate, toDate, modelName));
    }
    
    /**
     * 対象設備または工程の、指定期間中の完成数を取得する。
     *
     * @param compCountType 実績数のカウント方法
     * @param ids 対象設備ID一覧、または対象工程ID一覧
     * @param fromDate 日時範囲の先頭
     * @param toDate 日時範囲の末尾
     * @param modelName モデル名
     * @return　完成数
     */
    @Lock(LockType.READ)
    public int getTargetCompletedNum(CompCountTypeEnum compCountType, List<Long> ids, Date fromDate, Date toDate, String modelName) {
        try {
            CriteriaBuilder cb = this.em.getCriteriaBuilder();
            CriteriaQuery cq = cb.createQuery();

            Root<ActualResultEntity> poolActualResult = cq.from(ActualResultEntity.class);

            Subquery<Long> kanbanSubquery = cq.subquery(Long.class);
            Root<KanbanEntity> poolKanban = kanbanSubquery.from(KanbanEntity.class);

            jakarta.persistence.criteria.Path<Long> pathKanbanId = poolKanban.get(KanbanEntity_.kanbanId);
            jakarta.persistence.criteria.Path<String> pathModelName = poolKanban.get(KanbanEntity_.modelName);

            kanbanSubquery.select(pathKanbanId)
                    .where(cb.like(pathModelName, StringUtils.escapeLike(modelName)));

            // 検索条件
            List<Predicate> where = new ArrayList();

            jakarta.persistence.criteria.Path<Long> pathFkKanbanId = poolActualResult.get(ActualResultEntity_.kanbanId);
            jakarta.persistence.criteria.Path<KanbanStatusEnum> pathActualStatus = poolActualResult.get(ActualResultEntity_.actualStatus);
            jakarta.persistence.criteria.Path<Long> pathEquipmentId = poolActualResult.get(ActualResultEntity_.equipmentId);
            jakarta.persistence.criteria.Path<Long> pathWorkId = poolActualResult.get(ActualResultEntity_.workId);
            jakarta.persistence.criteria.Path<Date> pathImplementDatetime = poolActualResult.get(ActualResultEntity_.implementDatetime);
            jakarta.persistence.criteria.Path<Integer> pathCompNum = poolActualResult.get(ActualResultEntity_.compNum);

            switch (compCountType) {
                case EQUIPMENT:// 対象設備を巡回した数をカウント
                    where.add(pathEquipmentId.in(ids));
                    break;
                case WORK:// 対象工程を巡回した数をカウント
                    where.add(pathWorkId.in(ids));
                    break;
            }

            where.add(cb.equal(pathActualStatus, KanbanStatusEnum.COMPLETION));
            where.add(cb.greaterThanOrEqualTo(pathImplementDatetime, fromDate));
            where.add(cb.lessThanOrEqualTo(pathImplementDatetime, toDate));
            where.add(cb.or(pathCompNum.isNull(), cb.greaterThan(pathCompNum, 0)));// 完成数が null か 0より大きい (やり直しの実績を除外)

            // モデル名が指定されている場合、該当するモデル名のカンバンのみ対象とする。
            if (Objects.nonNull(modelName) && !modelName.isEmpty()) {
                where.add(pathFkKanbanId.in(kanbanSubquery));
            }

            // 完成数が null の場合は「1」とする。
            CriteriaBuilder.Coalesce<Integer> coCompNum = cb.coalesce();
            coCompNum.value(pathCompNum);
            coCompNum.value(1);

            TypedQuery<ActualResultEntity> query = this.em.createQuery(cq.select(cb.sumAsLong(coCompNum))
                    .where(where.toArray(new Predicate[where.size()])));

            Object count = query.getSingleResult();
            if (Objects.isNull(count)) {
                count = 0L;
            }

            return Integer.valueOf(String.valueOf(count));

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 対象設備または工程の、指定期間中の実績数を取得する。
     *
     * @param compCountType 実績数のカウント方法
     * @param ids 対象設備ID一覧、または対象工程ID一覧
     * @param fromDate 日時範囲の先頭
     * @param toDate 日時範囲の末尾
     * @param modelName モデル名
     * @return　完成数
     */   
    @Lock(LockType.READ)
    @ExecutionTimeLogging
    public int getActualNum(CompCountTypeEnum compCountType, List<Long> ids, Date fromDate, Date toDate, String modelName) {
        try {
            CriteriaBuilder cb = this.em.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);

            Root<ActualResultEntity> poolActualResult = cq.from(ActualResultEntity.class);

            Subquery<Long> kanbanSubquery = cq.subquery(Long.class);
            Root<KanbanEntity> poolKanban = kanbanSubquery.from(KanbanEntity.class);

            jakarta.persistence.criteria.Path<Long> pathKanbanId = poolKanban.get(KanbanEntity_.kanbanId);
            jakarta.persistence.criteria.Path<String> pathModelName = poolKanban.get(KanbanEntity_.modelName);

            kanbanSubquery.select(pathKanbanId)
                    .where(cb.like(pathModelName, StringUtils.escapeLike(modelName)));

            // 検索条件
            List<Predicate> where = new ArrayList();

            jakarta.persistence.criteria.Path<Long> pathFkKanbanId = poolActualResult.get(ActualResultEntity_.kanbanId);
            jakarta.persistence.criteria.Path<KanbanStatusEnum> pathActualStatus = poolActualResult.get(ActualResultEntity_.actualStatus);
            jakarta.persistence.criteria.Path<Long> pathEquipmentId = poolActualResult.get(ActualResultEntity_.equipmentId);
            jakarta.persistence.criteria.Path<Long> pathWorkId = poolActualResult.get(ActualResultEntity_.workId);
            jakarta.persistence.criteria.Path<Date> pathImplementDatetime = poolActualResult.get(ActualResultEntity_.implementDatetime);
            jakarta.persistence.criteria.Path<Integer> pathCompNum = poolActualResult.get(ActualResultEntity_.compNum);

            switch (compCountType) {
                case EQUIPMENT:     // 対象設備を巡回した数をカウント
                    where.add(pathEquipmentId.in(ids));
                    break;
                case WORK:          // 対象工程を巡回した数をカウント
                    where.add(pathWorkId.in(ids));
                    break;
            }

            where.add(cb.equal(pathActualStatus, KanbanStatusEnum.COMPLETION));
            where.add(cb.greaterThanOrEqualTo(pathImplementDatetime, fromDate));
            where.add(cb.lessThanOrEqualTo(pathImplementDatetime, toDate));
            where.add(cb.or(pathCompNum.isNull(), cb.greaterThan(pathCompNum, 0)));// 完成数が null か 0より大きい (やり直しの実績を除外)

            // モデル名が指定されている場合、該当するモデル名のカンバンのみ対象とする。
            if (Objects.nonNull(modelName) && !modelName.isEmpty()) {
                where.add(pathFkKanbanId.in(kanbanSubquery));
            }

            // 完成数が null の場合は「1」とする。
            CriteriaBuilder.Coalesce<Integer> coCompNum = cb.coalesce();
            coCompNum.value(pathCompNum);
            coCompNum.value(1);
            Expression<Integer> sum = cb.sum(coCompNum);

            TypedQuery<Tuple> query = this.em.createQuery(cq.select(cb.tuple(pathWorkId, sum))
                    .where(where.toArray(new Predicate[where.size()])).groupBy(pathWorkId));

            List<Tuple> tuples = query.getResultList();
            if (tuples.size() != ids.size()) {
                return 0;
            }
            
            Optional<Tuple> optional = tuples.stream().min(Comparator.comparingInt(o -> Integer.valueOf(String.valueOf(o.get(sum)))));
            if (!optional.isPresent()) {
                return 0;
            }

            return Integer.valueOf(String.valueOf(optional.get().get(sum)));

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定条件の工程実績を検索して、最初の１件を取得する。
     *
     * @param condition 工程実績の検索条件
     * @return 工程実績
     */
    @PUT
    @Path("search/first")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ActualResultEntity searchFirst(ActualSearchCondition condition) {
        logger.info("searchFirst:{}", condition);
        // 作業実績を降順で検索して、最初の１件を取得する。
        Query query = getSearchQuery(SearchType.SEARCH, condition);
        query.setMaxResults(1);
        ActualResultEntity actual = (ActualResultEntity) query.getSingleResult();
        if (Objects.nonNull(actual)) {
            this.getDetails(actual);
        }
        return actual;
    }

    /**
     * 工程実績の実績時間を更新する。
     * 
     * @param id 工程実績ID
     * @param time 実績時間
     * @param authId 認証ID
     * @return 処理結果
     */
    @PUT
    @Path("time/{id}")
    @Consumes({"text/plain"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response updateTime(@PathParam("id") Long id, String time, @QueryParam("authId") Long authId) {
        logger.info("updateTime: id={}, time={}, authId={}", id, time, authId);
        
        try {
            Date date = DateUtils.parse(time);
            if (Objects.isNull(date)) {
                // 入力時間が不正
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
            }
            
            ActualResultEntity actualResult = this.find(id);
            if (Objects.isNull(actualResult)) {
                // 工程実績IDが不正
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_UPDATE)).build();
            }

            // 休憩時間を取得
            List<BreakTimeInfoEntity> breakTimes = new ArrayList();
            List<Long> breaktimeIds = this.organizationRest.getBreaktimes(actualResult.getOrganizationId());
            if (!breaktimeIds.isEmpty()) {
                List<BreaktimeEntity> breaktimes = this.breaktimeRest.find(breaktimeIds);
                breakTimes = breaktimes.stream()
                        .map(o -> new BreakTimeInfoEntity(o.getBreaktimeName(), o.getStarttime(), o.getEndtime()))
                        .collect(Collectors.toList());
            }

            if (Objects.nonNull(actualResult.getPairId())) {
                ActualResultEntity prev = this.find(actualResult.getPairId());
                if (Objects.nonNull(prev)) {
                    if (date.before(prev.getImplementDatetime())) {
                        // 前の工程実績より時間が古いため
                        return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
                    }
                }
            }
            
            ActualResultEntity next = this.findByPairId(actualResult.getActualId());
            if (Objects.nonNull(next)) {
                if (date.after(next.getImplementDatetime())) {
                    // 後の工程実績より時間が新しいため
                    return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
                }
            }
                        
            ActualResultEntity oldResult = actualResult.clone();
            
            // パーティションテーブルから工程実績を削除
            this.remove(actualResult);
            this.em.flush();

            // 工程実績、工程カンバンを更新
            switch (actualResult.getActualStatus()) {
                case WORKING:
                    {
                        // 工程実績を再登録
                        actualResult.setImplementDatetime(date);
                        this.add(actualResult);

                        if (Objects.nonNull(next) 
                                && (KanbanStatusEnum.SUSPEND.equals(next.getActualStatus()) 
                                    || KanbanStatusEnum.COMPLETION.equals(next.getActualStatus()))) {
                            // 作業時間を更新
                            int workTime = (int) BreaktimeUtil.getDiffTime(breakTimes, date, next.getImplementDatetime());
                            int diffTime = workTime - next.getWorkingTime();

                            next.setWorkingTime(workTime);
                            next.setPairId(actualResult.getActualId());
                            this.em.merge(next);
                            
                            // 累計作業時間を更新
                            WorkKanbanEntity workKanban = this.workKandanRest.find(actualResult.getWorkKanbanId(), null);
                            workKanban.setSumTimes(workKanban.getSumTimes() + diffTime);
                            this.em.merge(workKanban);
                            this.em.flush();
                        }
                    }
                    break;

                case COMPLETION:
                case SUSPEND: 
                    {
                        int diffTime;

                        // 作業時間の差異を算出
                        if (date.before(actualResult.getImplementDatetime())) {
                            diffTime = - (int) BreaktimeUtil.getDiffTime(breakTimes, date, actualResult.getImplementDatetime());
                        } else {
                            diffTime = (int) BreaktimeUtil.getDiffTime(breakTimes, actualResult.getImplementDatetime(), date);
                        }

                        // 工程実績を再登録
                        actualResult.setImplementDatetime(date);
                        actualResult.setWorkingTime(actualResult.getWorkingTime() + diffTime);
                        this.add(actualResult);

                        // 累計作業時間を更新
                        WorkKanbanEntity workKanban = this.workKandanRest.find(actualResult.getWorkKanbanId(), null);
                        workKanban.setSumTimes(workKanban.getSumTimes() + diffTime);
                        this.em.merge(workKanban);

                        if (Objects.nonNull(next) 
                                && KanbanStatusEnum.WORKING.equals(next.getActualStatus())) {
                            // ペアIDを更新
                            next.setPairId(actualResult.getActualId());
                            this.em.merge(next);                                
                        }

                        this.em.flush();
                    }
                    break;

                default:
                    // 工程実績を再登録
                    actualResult.setImplementDatetime(date);
                    this.add(actualResult);
                    break;
            }

            // 操作実績を登録
            OperationAddInfoEntity addInfo = new OperationAddInfoEntity();
            addInfo.setChangeResult(new OperateChangeResultEntity(oldResult, actualResult));
            OperationEntity operation = new OperationEntity(new Date(), null, authId, OperateAppEnum.ADMANAGER, OperationTypeEnum.CHANGE_RESULT, addInfo);
            this.operationRest.add(operation);
            
            // 実績出力情報(VIEW)がDBとEntityManagerで異なるためキャッシュをクリア。
            this.em.getEntityManagerFactory().getCache().evict(ReportOutEntity.class);

            return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.SUCCESS)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * ペアIDを指定して、工程実績を取得する
     * 
     * @param pairId ペアID
     * @return 
     */ 
    private ActualResultEntity findByPairId(Long pairId) {
        TypedQuery<ActualResultEntity> query = this.em.createNamedQuery("ActualResultEntity.findByPairId", ActualResultEntity.class);
        query.setParameter("pairId", pairId);
        List<ActualResultEntity> list = query.getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }
    
    /**
     * 工程実績IDを指定して、品質データを更新する。
     *
     * @param id 工程実績ID
     * @param addInfo 品質データ
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @PUT
    @Path("add-info/{id}")
    @Consumes({"text/plain"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response updateAddInfo(@PathParam("id") Long id, String addInfo, @QueryParam("authId") Long authId) {
        logger.info("updateAddInfo: id={}, authId={}", id, authId);
        try {
            // 工程実績IDを指定して、追加情報を更新する。
            Query updateQuery = this.em.createNamedQuery("ActualResultEntity.updateAddInfo");
            updateQuery.setParameter("addInfo", addInfo);
            updateQuery.setParameter("actualId", id);

            updateQuery.executeUpdate();
            // 実績出力情報(VIEW)がDBとEntityManagerで異なるためキャッシュをクリア。
            this.em.getEntityManagerFactory().getCache().evict(ReportOutEntity.class);

            return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.SUCCESS)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    @Lock(LockType.READ)
    @ExecutionTimeLogging
    public List<ActualResultEntity> findSuspendingActualResult(Long equipmentId, Long organizationId)
    {
        logger.info("findSuspendingActualResult: equipmentId={}, organizationId={}", equipmentId, organizationId);
        if (Objects.isNull(equipmentId) || Objects.isNull(organizationId)) {
            return null;
        }

        Query query = this.em.createNamedQuery("ActualResultEntity.findSuspendingActualResult");
        query.setParameter("equipmentId", equipmentId);
        query.setParameter("organizationId", organizationId);

        return (List<ActualResultEntity>) query.getResultList();

    }

    @Lock(LockType.READ)
    public List<ActualResultEntity> find(List<Long> ids, Long authId) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }

        TypedQuery<ActualResultEntity> query = this.em.createNamedQuery("ActualResultEntity.findByActualIds", ActualResultEntity.class);
        query.setParameter("actualIds", ids);

        return query.getResultList();
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    protected void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public void setKanbanHierarchyRest(KanbanHierarchyEntityFacadeREST kanbanHierarchyRest) {
        this.kanbanHierarchyRest = kanbanHierarchyRest;
    }

    public void setWorkflowHierarchyRest(WorkflowHierarchyEntityFacadeREST workflowHierarchyRest) {
        this.workflowHierarchyRest = workflowHierarchyRest;
    }

    public void setWorkHierarchyRest(WorkHierarchyEntityFacadeREST workHierarchyRest) {
        this.workHierarchyRest = workHierarchyRest;
    }

    public void setKanbanRest(KanbanEntityFacadeREST kanbanRest) {
        this.kanbanRest = kanbanRest;
    }

    public void setWorkKandanRest(WorkKanbanEntityFacadeREST workKandanRest) {
        this.workKandanRest = workKandanRest;
    }

    public void setWorkflowRest(WorkflowEntityFacadeREST workflowRest) {
        this.workflowRest = workflowRest;
    }

    public void setWorkRest(WorkEntityFacadeREST workRest) {
        this.workRest = workRest;
    }

    public void setOrganizationRest(OrganizationEntityFacadeREST organizationRest) {
        this.organizationRest = organizationRest;
    }

    public void setEquipmentRest(EquipmentEntityFacadeREST equipmentRest) {
        this.equipmentRest = equipmentRest;
    }






}
