/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import jakarta.ejb.AccessTimeout;
import jakarta.ejb.EJB;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.job.KanbanProduct;
import jp.adtekfuji.adFactory.entity.kanban.WorkKanbanGroupEntity;
import jp.adtekfuji.adFactory.entity.kanban.WorkKanbanGroupKey;
import jp.adtekfuji.adFactory.entity.master.ServiceInfoEntity;
import jp.adtekfuji.adFactory.entity.search.AddInfoSearchCondition;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.entity.search.ProducibleWorkKanbanCondition;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentEntity;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentEntity_;
import jp.adtekfuji.adfactoryserver.entity.kanban.ConKanbanHierarchyEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.ConKanbanHierarchyEntity_;
import jp.adtekfuji.adfactoryserver.entity.kanban.ConWorkkanbanEquipmentEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.ConWorkkanbanEquipmentEntity_;
import jp.adtekfuji.adfactoryserver.entity.kanban.ConWorkkanbanOrganizationEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.ConWorkkanbanOrganizationEntity_;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity_;
import jp.adtekfuji.adfactoryserver.entity.kanban.WorkKanbanDetail;
import jp.adtekfuji.adfactoryserver.entity.kanban.WorkKanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.WorkKanbanEntity_;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity_;
import jp.adtekfuji.adfactoryserver.entity.response.ResponseWorkKanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity;
import jp.adtekfuji.adfactoryserver.model.FileManager;
import jp.adtekfuji.adfactoryserver.service.workflow.WorkflowInteface;
import jp.adtekfuji.adfactoryserver.service.workflow.WorkflowModelFacade;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程カンバン情報REST
 *
 * @author ke.yokoi
 */
@Singleton
@Path("kanban/work")
public class WorkKanbanEntityFacadeREST extends AbstractFacade<WorkKanbanEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    @EJB
    private OrganizationEntityFacadeREST organizationRest;

    @EJB
    private EquipmentEntityFacadeREST equipmentRest;

    private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     */
    public WorkKanbanEntityFacadeREST() {
        super(WorkKanbanEntity.class);
    }

    /**
     * 工程カンバンIDを指定して、工程カンバン情報を取得する。(基本情報のみ)
     *
     * @param id 工程カンバンID
     * @return 工程カンバン情報
     */
    @Lock(LockType.READ)
    public WorkKanbanEntity findBasicInfo(Long id) {
        WorkKanbanEntity workKanban = super.find(id);
        if (Objects.isNull(workKanban)) {
            return new WorkKanbanEntity();
        }
        return workKanban;
    }

    /**
     * 工程カンバンIDを指定して、工程カンバン情報を取得する。
     *
     * @param id 工程カンバンID
     * @param authId 認証ID
     * @return 工程カンバン情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public WorkKanbanEntity find(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("find: id={}, authId={}", id, authId);

        WorkKanbanEntity entity = super.find(id);
        if (Objects.isNull(entity)) {
            return new WorkKanbanEntity();
        }

        // 詳細情報を取得してセットする。
        this.getDetails(entity);

        // 設備ID一覧を取得してセットする。
        entity.setEquipmentCollection(this.getEquipmentCollection(id));
        // 組織ID一覧を取得してセットする。
        entity.setOrganizationCollection(this.getOrganizationCollection(id));

        return entity;
    }

    /**
     * 工程カンバン情報一覧を取得する。
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 工程カンバン情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkKanbanEntity> findRange(@QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findRange: from={}, to={}, authId={}", from, to, authId);

        List<WorkKanbanEntity> entities;
        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            entities = super.findRange(from, to);
        } else {
            entities = super.findAll();
        }

        for (WorkKanbanEntity entity : entities) {
            // 詳細情報を取得してセットする。
            this.getDetails(entity);

            // 設備ID一覧を取得してセットする。
            entity.setEquipmentCollection(this.getEquipmentCollection(entity.getWorkKanbanId()));
            // 組織ID一覧を取得してセットする。
            entity.setOrganizationCollection(this.getOrganizationCollection(entity.getWorkKanbanId()));
        }
        return entities;
    }

    /**
     * 工程カンバン情報の件数を取得する。
     *
     * @param authId 認証ID
     * @return 工程カンバン情報の件数
     */
    @Lock(LockType.READ)
    @GET
    @Path("count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String countAll(@QueryParam("authId") Long authId) {
        logger.info("countAll: authId={}", authId);
        return String.valueOf(super.count());
    }

    /**
     * 工程カンバン情報を取得する。
     *
     * @param kanbanId カンバンID
     * @param workflowId 工程順ID
     * @param workId 工程ID
     * @param separateFlg 追加工程フラグ
     * @return 工程カンバン情報
     */
    @Lock(LockType.READ)
    @ExecutionTimeLogging
    public WorkKanbanEntity findWorkId(Long kanbanId, Long workflowId, Long workId, Boolean separateFlg) {
        // カンバンID・工程順ID・工程ID・追加工程フラグを指定して、工程カンバン情報を取得する。
        TypedQuery<WorkKanbanEntity> query = this.em.createNamedQuery("WorkKanbanEntity.findByKanbanWorkflowWorkSeparate", WorkKanbanEntity.class);
        query.setParameter("kanbanId", kanbanId);
        query.setParameter("workflowId", workflowId);
        query.setParameter("workId", workId);
        query.setParameter("separateWorkFlag", separateFlg);
        WorkKanbanEntity entity = query.getSingleResult();

        return entity;
    }


    @Lock(LockType.READ)
    @ExecutionTimeLogging
    public List<WorkKanbanEntity> findAllByName(String workName)
    {
        logger.info("findAllByName: workName={}", workName);
        try {
            if (StringUtils.isEmpty(workName)) {
                return new ArrayList<>();
            }

            // カンバンID・工程名を指定して、工程カンバン情報を取得する。
            TypedQuery<WorkKanbanEntity> query = this.em.createNamedQuery("WorkKanbanEntity.findByWorkNameOnly", WorkKanbanEntity.class);
            query.setParameter("workName", workName);

            List<WorkKanbanEntity> ret = query.getResultList();
            return ret;
        } catch (Exception ex) {
            logger.error(ex, ex);
            return new ArrayList<>();
        }finally {
            logger.debug("findByWorkName end.");
        }
    }

    /**
     * 指定したカンバンID・工程名の工程カンバン情報を取得する。
     *
     * @param kanbanId カンバンID
     * @param workName 工程名
     * @param authId 認証ID
     * @return 工程カンバン情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("name")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public WorkKanbanEntity findByWorkName(@QueryParam("kanbanId") Long kanbanId, @QueryParam("workName") String workName, @QueryParam("authId") Long authId) {
        logger.info("findByWorkName: kanbanId={}, workName={}, authId={}", kanbanId, workName, authId);
        try {
            // カンバンID・工程名を指定して、工程カンバン情報を取得する。
            TypedQuery<WorkKanbanEntity> query = this.em.createNamedQuery("WorkKanbanEntity.findByWorkName", WorkKanbanEntity.class);
            query.setParameter("kanbanId", kanbanId);
            query.setParameter("workName", workName);

            List<WorkKanbanEntity> resultList = query.getResultList();
            if (resultList.isEmpty()) {
                logger.info("Not found workKanban :{}", workName);
                return null;
            }
            return resultList.get(0);
        } finally {
            logger.debug("findByWorkName end.");
        }
    }

    /**
     * カンバンIDを指定して、通常工程の工程カンバン情報一覧を取得する。
     *
     * @param kanbanId カンバンID
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 工程カンバン情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("kanban-id/range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkKanbanEntity> findByKanbanId(@QueryParam("kanbanid") Long kanbanId, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findByKanbanId: kanbanId={}, from={}, to={}, authId={}", kanbanId, from, to, authId);
        return this.getWorkKanban(kanbanId, false, from, to);
    }

    /**
     * カンバンIDを指定して、通常工程の工程カンバン情報の件数を取得する。
     *
     * @param kanbanId カンバンID
     * @param authId 認証ID
     * @return 件数
     */
    @Lock(LockType.READ)
    @GET
    @Path("kanban-id/count")
    @Produces({"text/plain"})
    @ExecutionTimeLogging
    public String countByKanbanId(@QueryParam("kanbanid") Long kanbanId, @QueryParam("authId") Long authId) {
        logger.info("countByKanbanId: kanbanId={}, authId={}", kanbanId, authId);
        return String.valueOf(this.countWorkKanban(kanbanId, false));
    }

    /**
     * カンバンIDを指定して、追加工程の工程カンバン情報一覧を取得する。
     *
     * @param kanbanId カンバンID
     * @param from 開始範囲
     * @param to 終了範囲
     * @param authId 認証ID
     * @return 工程カンバン情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("separate/range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkKanbanEntity> findSeparateWork(@QueryParam("kanbanid") Long kanbanId, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findSeparateWork: kanbanId={}", kanbanId);
        return this.getWorkKanban(kanbanId, true, from, to);
    }

    /**
     * カンバンIDを指定して、追加工程の工程カンバン情報の件数を取得する。
     *
     * @param kanbanId カンバンID
     * @param authId 認証ID
     * @return 件数
     */
    @Lock(LockType.READ)
    @GET
    @Path("separate/count")
    @Produces({"text/plain"})
    @ExecutionTimeLogging
    public String countSeparateWork(@QueryParam("kanbanid") Long kanbanId, @QueryParam("authId") Long authId) {
        logger.info("countSeparateWork: kanbanId={}, authId={}", kanbanId, authId);
        return String.valueOf(this.countWorkKanban(kanbanId, true));
    }

    /**
     * カンバンID・追加工程フラグを指定して、工程カンバン情報一覧を取得する。
     *
     * @param kanbanId カンバンID
     * @param isSeparate 追加工程フラグ (true:追加工程, false:通常工程)
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @return 工程カンバン情報一覧
     */
    @Lock(LockType.READ)
    public List<WorkKanbanEntity> getWorkKanban(Long kanbanId, Boolean isSeparate, Integer from, Integer to) {
        // カンバンID・追加工程フラグを指定して、工程カンバン情報一覧を取得する。
        TypedQuery<WorkKanbanEntity> query = this.em.createNamedQuery("WorkKanbanEntity.findByKanbanIdAndSeparateFlg", WorkKanbanEntity.class);
        query.setParameter("kanbanId", kanbanId);
        query.setParameter("separateWorkFlag", isSeparate);

        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            query.setMaxResults(to - from + 1);
            query.setFirstResult(from);
        }

        List<WorkKanbanEntity> entities = query.getResultList();
        for (WorkKanbanEntity entity : entities) {
            // 詳細情報を取得してセットする。
            this.getDetails(entity);

            // 設備ID一覧を取得してセットする。
            entity.setEquipmentCollection(this.getEquipmentCollection(entity.getWorkKanbanId()));
            // 組織ID一覧を取得してセットする。
            entity.setOrganizationCollection(this.getOrganizationCollection(entity.getWorkKanbanId()));
        }
        return entities;
    }

    @Lock(LockType.READ)
    public List<WorkKanbanEntity> getWorkKanbanNoDitail(Long kanbanId, Boolean isSeparate, Integer from, Integer to) {
        // カンバンID・追加工程フラグを指定して、工程カンバン情報一覧を取得する。
        TypedQuery<WorkKanbanEntity> query = this.em.createNamedQuery("WorkKanbanEntity.findByKanbanIdAndSeparateFlg", WorkKanbanEntity.class);
        query.setParameter("kanbanId", kanbanId);
        query.setParameter("separateWorkFlag", isSeparate);

        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            query.setMaxResults(to - from + 1);
            query.setFirstResult(from);
        }

        List<WorkKanbanEntity> entities = query.getResultList();

        for (WorkKanbanEntity entity : entities) {
            // 詳細情報を取得してセットする。
            this.getDetails(entity);
        }

        return entities;
    }


    /**
     * カンバンID・追加工程フラグを指定して、工程カンバン情報の件数を取得する。
     *
     * @param kanbanId カンバンID
     * @param isSeparate 追加工程フラグ (true:追加工程, false:通常工程)
     * @return 件数
     */
    @Lock(LockType.READ)
    public Long countWorkKanban(Long kanbanId, Boolean isSeparate) {
        // カンバンID・追加工程フラグを指定して、工程カンバン情報の件数を取得する。
        TypedQuery<Long> query = this.em.createNamedQuery("WorkKanbanEntity.countByKanbanIdAndSeparateFlg", Long.class);
        query.setParameter("kanbanId", kanbanId);
        query.setParameter("separateWorkFlag", isSeparate);

        return query.getSingleResult();
    }

    /**
     * カンバンID・追加工程フラグを指定して、工程カンバン情報一覧を取得する。(基本情報のみ)
     *
     * @param kanbanId カンバンID
     * @param isSeparate 追加工程フラグ (true:追加工程, false:通常工程)
     * @return 工程カンバン情報一覧
     */
    @Lock(LockType.READ)
    public List<WorkKanbanEntity> getWorkKanbans(long kanbanId, boolean isSeparate) {
        TypedQuery<WorkKanbanEntity> query = this.em.createNamedQuery("WorkKanbanEntity.findByKanbanIdAndSeparateFlg", WorkKanbanEntity.class);
        query.setParameter("kanbanId", kanbanId);
        query.setParameter("separateWorkFlag", isSeparate);
        return query.getResultList();
    }

    /**
     * 工程カンバンID一覧を指定して、工程カンバン情報一覧を取得する。
     * [GET] /rest/kanban/work
     *
     * @param ids 工程カンバンID一覧
     * @param authId 認証ID
     * @return 工程カンバン情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkKanbanEntity> findByWorkKanbanId(@QueryParam("id") final List<Long> ids, @QueryParam("authId") Long authId) {
        logger.info("findByWorkKanbanId: ids={}, authId={}", ids, authId);
        try {
            if(Objects.isNull(ids) || ids.isEmpty()) {
                return new ArrayList<>();
            }

            TypedQuery<WorkKanbanEntity> query = this.em.createNamedQuery("WorkKanbanEntity.findByWorkkanbanIds", WorkKanbanEntity.class);
            query.setParameter("workKanbanId", ids);

            List<WorkKanbanEntity> workKanbanEntities = query.getResultList();
            workKanbanEntities.forEach(this::getDetails);
            return workKanbanEntities;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    @Lock(LockType.READ)
    @ExecutionTimeLogging
    public List<WorkKanbanEntity> findByKanbanId(final List<Long> kanbanIds, @QueryParam("authId") Long authId) {
        logger.info("findByWorkKanbanId: ids={}, authId={}", kanbanIds, authId);
        try {
            if(kanbanIds.isEmpty()) {
                return new ArrayList<>();
            }

            TypedQuery<WorkKanbanEntity> query = this.em.createNamedQuery("WorkKanbanEntity.findByKanbanIds", WorkKanbanEntity.class);
            query.setParameter("kanbanIds", kanbanIds);

            return query.getResultList();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * カンバン検索条件を指定して、工程カンバン情報一覧を取得する。
     *
     * @param condition 検索条件
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 工程カンバン情報一覧
     */
    @Lock(LockType.READ)
    @PUT
    @Path("search/range")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkKanbanEntity> searchWorkKanban(KanbanSearchCondition condition, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("searchWorkKanban: {}, from={}, to={}, authId={}", condition, from, to, authId);
        try {
            if (Objects.isNull(condition.getKanbanId())
                    && (Objects.isNull(condition.getKanbanName()) || condition.getKanbanName().isEmpty())
                    && Objects.isNull(condition.getFromDate())
                    && Objects.isNull(condition.getToDate())
                    && Objects.isNull(condition.getWorkKanbanCollection())
                    && Objects.isNull(condition.getOutputFlag())
                    && Objects.isNull(condition.getHierarchyId())) {
                logger.error("KanbanSearchCondition is insufficient.");
                return new ArrayList();
            }

            Query query = this.getSearchQuery(SearchType.SEARCH, condition);

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                query.setMaxResults(to - from + 1);
                query.setFirstResult(from);
            }

            List<WorkKanbanEntity> entities = query.getResultList();

            if (condition.isAdditionalInfo()) {
                for (WorkKanbanEntity entity : entities) {
                    // 詳細情報を取得してセットする。
                    this.getDetails(entity);
                }
            } else {
                // 工程名を取得してセットする。
                Map<Long, String> workMap = new HashMap();

                for (WorkKanbanEntity entity : entities) {
                    if (workMap.containsKey(entity.getWorkId())) {
                        entity.setWorkName(workMap.get(entity.getWorkId()));
                    } else {
                        // 工程名を取得する。
                        TypedQuery<String> workQuery = em.createNamedQuery("WorkEntity.findWorkName", String.class);
                        workQuery.setParameter("workId", entity.getWorkId());
                        String workName = workQuery.getSingleResult();
                        entity.setWorkName(workName);
                        workMap.put(entity.getWorkId(), workName);
                    }
                }
            }

            return entities;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * カンバン検索条件を指定して、工程カンバン情報の件数を取得する。
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
    public String countWorkKanban(KanbanSearchCondition condition, @QueryParam("authId") Long authId) {
        logger.info("countWorkKanban: {}", condition, authId);
        try {
            if (Objects.isNull(condition.getKanbanId())
                    && (Objects.isNull(condition.getKanbanName()) || condition.getKanbanName().isEmpty())
                    && Objects.isNull(condition.getFromDate())
                    && Objects.isNull(condition.getToDate())
                    && Objects.isNull(condition.getWorkKanbanCollection())
                    && Objects.isNull(condition.getOutputFlag())
                    && Objects.isNull(condition.getHierarchyId())) {
                logger.error("KanbanSearchCondition is insufficient.");
                return String.valueOf(0);
            }

            Query query = this.getSearchQuery(SearchType.COUNT, condition);

            return String.valueOf(query.getSingleResult());

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 生産可能な工程カンバンを検索
     * @param condition 検索条件
     * @param from 開始
     * @param to 終了
     * @param authId 認証
     * @return 生産可能な工程カンバン
     */
    @Lock(LockType.READ)
    @PUT
    @Path("search")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkKanbanEntity> searchProducibleWorkKanban(ProducibleWorkKanbanCondition condition, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId)
    {
        logger.info("searchProducibleWorkKanban: {} {} {} {}", condition.getEquipmentCollection(), condition.getOrganizationCollection(), condition, authId);

        try {
            List<KanbanStatusEnum> workKanbanStatus = Collections.singletonList(KanbanStatusEnum.PLANNED);
            List<KanbanStatusEnum> kanbanStatus = Arrays.asList(KanbanStatusEnum.PLANNED, KanbanStatusEnum.WORKING, KanbanStatusEnum.SUSPEND);

            java.sql.Array workKanbanStatusArray = this.em.unwrap(Connection.class).createArrayOf("varchar", workKanbanStatus.toArray());
            java.sql.Array kanbanStatusArray = this.em.unwrap(Connection.class).createArrayOf("varchar", kanbanStatus.toArray());
            java.sql.Array equipmentArray = this.em.unwrap(Connection.class).createArrayOf("bigint", condition.getEquipmentCollection().toArray());
            java.sql.Array organizationArray = this.em.unwrap(Connection.class).createArrayOf("bigint", condition.getOrganizationCollection().toArray());

            List<Function<Query, Query>> parameters = new ArrayList<>();

            // 工程カンバン
            final String search_workKanban = "SELECT wk.* FROM (SELECT * FROM trn_work_kanban WHERE implement_flag = ?1 AND skip_flag = ?2 AND work_status = ANY(?3)) wk ";
            parameters.add((query -> query.setParameter(1, 't'))); // 実施フラグ
            parameters.add((query -> query.setParameter(2, 'f'))); // スキップフラグ
            parameters.add((query -> query.setParameter(3, workKanbanStatusArray))); // 工程カンバン状態

            // 設備
            final String eids = "eids AS (SELECT equipment_id eid  FROM mst_equipment me  WHERE me.equipment_id = ANY(?4) UNION DISTINCT  SELECT parent_equipment_id eid  FROM mst_equipment me, eids  WHERE eids.eid = me.equipment_id AND me.parent_equipment_id <> 0)";
            final String searchEquipment = "JOIN con_workkanban_equipment cwe ON wk.work_kanban_id = cwe.workkanban_id AND cwe.equipment_id = ANY (SELECT eids.eid FROM eids) ";
            parameters.add((query -> query.setParameter(4, equipmentArray)));

            // 組織
            final String oids = "oids As (SELECT mo.organization_id oid FROM mst_organization mo WHERE mo.organization_id = ANY(?5) UNION DISTINCT SELECT mo.parent_organization_id oid FROM mst_organization mo, oids WHERE oids.oid = mo.organization_id AND mo.parent_organization_id <> 0)";
            final String searchOrganization = "JOIN con_workkanban_organization cwo ON wk.work_kanban_id = cwo.workkanban_id AND cwo.organization_id = ANY (SELECT oids.oid FROM oids) ";
            parameters.add((query -> query.setParameter(5, organizationArray)));

            // カンバン
            final String searchKanban = "JOIN trn_kanban tk ON wk.kanban_id = tk.kanban_id AND tk.kanban_status = ANY(?6) ";
            parameters.add((query -> query.setParameter(6, kanbanStatusArray)));

            // 追加情報
            List<AddInfoSearchCondition> addInfoSearchConditions = condition.getAddInfoSearchConditions();

            List<String> addInfoSql = new ArrayList<>();
            if (Objects.nonNull(addInfoSearchConditions)) {
                int i = 6; // SQLのパラメータ開始番号
                for (int n=0; n<addInfoSearchConditions.size(); ++n) {
                    final int keyNo = i + 1;
                    final int valNo = keyNo + 1;
                    final AddInfoSearchCondition addInfoSearchCondition = addInfoSearchConditions.get(n);
                    addInfoSql.add(String.format("JOIN jsonb_to_recordset(wk.work_kanban_add_info) as a%d(key TEXT, val TEXT) ON a%d.key = ?%d AND ",n, n, keyNo));
                    parameters.add((query -> query.setParameter(keyNo, addInfoSearchCondition.getKey())));

                    final String type = addInfoSearchCondition.getSearchType().type;
                    addInfoSql.add(String.format("a%d.val" + type + "?%s ", n, valNo));
                    parameters.add((query -> query.setParameter(valNo, addInfoSearchCondition.getVal())));
                    i = valNo;
                }
            }

            String sql = "WITH recursive " + eids + ", " + oids + search_workKanban + searchKanban + searchEquipment + searchOrganization + String.join("", addInfoSql) + " ORDER BY wk.start_datetime";
            logger.info(sql);

            final Query query = em.createNativeQuery(sql, WorkKanbanEntity.class);
            parameters.forEach(func -> func.apply(query));
            if (to >= from) {
                query.setMaxResults(to - from + 1);
                query.setFirstResult(from);
            }

            return (List<WorkKanbanEntity>) query.getResultList();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 検索条件で工程カンバン情報を検索するクエリを取得する。
     *
     * @param type 検索種別
     * @param condition 検索条件
     * @return 検索クエリ
     */
    @Lock(LockType.READ)
    private Query getSearchQuery(SearchType type, KanbanSearchCondition condition) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();

        Root<WorkKanbanEntity> poolWorkKanban = cq.from(WorkKanbanEntity.class);

        jakarta.persistence.criteria.Path<Long> pathWorkKanbanId = poolWorkKanban.get(WorkKanbanEntity_.workKanbanId);
        jakarta.persistence.criteria.Path<Long> pathKanbanId = poolWorkKanban.get(WorkKanbanEntity_.kanbanId);
        jakarta.persistence.criteria.Path<Long> pathWorkflowId = poolWorkKanban.get(WorkKanbanEntity_.workflowId);
        jakarta.persistence.criteria.Path<Long> pathWorkId = poolWorkKanban.get(WorkKanbanEntity_.workId);

        jakarta.persistence.criteria.Path<Boolean> pathImplementFlag = poolWorkKanban.get(WorkKanbanEntity_.implementFlag);
        jakarta.persistence.criteria.Path<Boolean> pathSkipFlag = poolWorkKanban.get(WorkKanbanEntity_.skipFlag);

        jakarta.persistence.criteria.Path<Date> pathStartDatetime = poolWorkKanban.get(WorkKanbanEntity_.startDatetime);
        jakarta.persistence.criteria.Path<Date> pathCompDatetime = poolWorkKanban.get(WorkKanbanEntity_.compDatetime);

        jakarta.persistence.criteria.Path<KanbanStatusEnum> pathWorkStatus = poolWorkKanban.get(WorkKanbanEntity_.workStatus);
        jakarta.persistence.criteria.Path<Boolean> pathNeedActualOutputFlag = poolWorkKanban.get(WorkKanbanEntity_.needActualOutputFlag);
        jakarta.persistence.criteria.Path<Boolean> pathSeparateWorkFlag = poolWorkKanban.get(WorkKanbanEntity_.separateWorkFlag);

        // 検索条件
        List<Predicate> where = new LinkedList();

        // 検索条件で、設備ID一覧の指定があり、親設備フラグが有効な場合、設備ID一覧に親設備のIDを追加する。
        HashSet<Long> equipmentIdCollection = new HashSet<>();
        if (Objects.nonNull(condition.getEquipmentCollection())) {
            equipmentIdCollection.addAll(condition.getEquipmentCollection());
            if (Objects.nonNull(condition.getEquipmentIdWithParent()) && condition.getEquipmentIdWithParent()) {
                for (Long id : condition.getEquipmentCollection()) {
                    equipmentIdCollection.addAll(this.equipmentRest.getEquipmentPerpetuity(id));
                }
            }
        }

        // 検索条件で、組織ID一覧の指定があり、親組織フラグが有効な場合、組織ID一覧に親組織のIDを追加する。
        HashSet<Long> organizationIdCollection = new HashSet<>();
        if (Objects.nonNull(condition.getOrganizationCollection())) {
            organizationIdCollection.addAll(condition.getOrganizationCollection());
            if (Objects.nonNull(condition.getOrganizationIdWithParent()) && condition.getOrganizationIdWithParent()) {
                for (Long id : condition.getOrganizationCollection()) {
                    organizationIdCollection.addAll(this.organizationRest.getOrganizationPerpetuity(id));
                }
            }
        }

        // カンバン
        if (Objects.nonNull(condition.getKanbanId())
                || Objects.nonNull(condition.getKanbanName())
                || Objects.nonNull(condition.getKanbanSubname())
                || Objects.nonNull(condition.getModelName())
                || Objects.nonNull(condition.getParentStatusCollection())
                || Objects.nonNull(condition.getHierarchyId())) {
            Root<KanbanEntity> subKanban = cq.from(KanbanEntity.class);

            jakarta.persistence.criteria.Path<Long> pathId = subKanban.get(KanbanEntity_.kanbanId);
            jakarta.persistence.criteria.Path<String> pathName = subKanban.get(KanbanEntity_.kanbanName);
            jakarta.persistence.criteria.Path<String> pathSubName = subKanban.get(KanbanEntity_.kanbanSubname);
            jakarta.persistence.criteria.Path<String> pathModelName = subKanban.get(KanbanEntity_.modelName);
            jakarta.persistence.criteria.Path<KanbanStatusEnum> pathStatus = subKanban.get(KanbanEntity_.kanbanStatus);

            Subquery<Long> kanbanSubquery = cq.subquery(Long.class);

            List<Predicate> subWhere = new ArrayList<>();

            // カンバンID
            if (Objects.nonNull(condition.getKanbanId())) {
                subWhere.add(cb.equal(pathId, condition.getKanbanId()));
            }

            // カンバン名
            if (Objects.nonNull(condition.getKanbanName())) {
                subWhere.add(cb.or(
                        cb.like(cb.lower(pathName), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(condition.getKanbanName())) + "%"),
                        cb.like(pathName, "%" + StringUtils.escapeLikeChar(condition.getKanbanName()) + "%")
                ));
            }

            // サブカンバン名
            if (Objects.nonNull(condition.getKanbanSubname())) {
                subWhere.add(cb.or(
                        cb.like(cb.lower(pathSubName), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(condition.getKanbanSubname())) + "%"),
                        cb.like(pathSubName, "%" + StringUtils.escapeLikeChar(condition.getKanbanSubname()) + "%")
                ));
            }

            // モデル名
            if (Objects.nonNull(condition.getModelName())) {
                subWhere.add(cb.equal(pathModelName, condition.getModelName()));
            }

            // カンバンステータス
            if (Objects.nonNull(condition.getParentStatusCollection())) {
                subWhere.add(pathStatus.in(condition.getParentStatusCollection()));
            }

            if (Objects.nonNull(condition.getHierarchyId())) {
                // カンバン階層IDで検索
                Root<ConKanbanHierarchyEntity> subKanbanHierarchy = cq.from(ConKanbanHierarchyEntity.class);
                jakarta.persistence.criteria.Path<Long> pathHierarchyId = subKanbanHierarchy.get(ConKanbanHierarchyEntity_.kanbanHierarchyId);
                jakarta.persistence.criteria.Path<Long> pathHierarchyKanbanId = subKanbanHierarchy.get(ConKanbanHierarchyEntity_.kanbanId);
                List<Predicate> subHierarchyWhere = new ArrayList<>();

                // カンバン階層ID
                if (Objects.nonNull(condition.getHierarchyId())) {
                    subHierarchyWhere.add(pathHierarchyId.in(condition.getHierarchyId()));
                }

                Subquery<Long> kanbanHierarchySubquery = cq.subquery(Long.class);
                kanbanHierarchySubquery.select(pathHierarchyKanbanId)
                        .where(cb.and(subHierarchyWhere.toArray(new Predicate[where.size()])));

                subWhere.add(pathKanbanId.in(kanbanHierarchySubquery));
            }

            kanbanSubquery.select(pathId)
                    .where(cb.and(subWhere.toArray(new Predicate[where.size()])));

            where.add(pathKanbanId.in(kanbanSubquery));
        }

        // 工程順
        if (Objects.nonNull(condition.getWorkflowIdCollection()) && !condition.getWorkflowIdCollection().isEmpty()) {
            where.add(pathWorkflowId.in(condition.getWorkflowIdCollection()));
        }

        // 工程ID
        if (Objects.nonNull(condition.getWorkId())) {
            where.add(cb.equal(pathWorkId, condition.getWorkId()));
        }

        // 実施フラグ
        if (Objects.nonNull(condition.getImplementFlag())) {
            where.add(cb.equal(pathImplementFlag, condition.getImplementFlag()));
        }

        // スキップフラグ
        if (Objects.nonNull(condition.getSkipFlag())) {
            where.add(cb.equal(pathSkipFlag, condition.getSkipFlag()));
        }

        // 工程ステータス
        if (Objects.nonNull(condition.getKanbanStatusCollection())) {
            where.add(pathWorkStatus.in(condition.getKanbanStatusCollection()));
        }

        // 設備
        if (!equipmentIdCollection.isEmpty() || Objects.nonNull(condition.getEquipmentNameCollection())) {
            Root<ConWorkkanbanEquipmentEntity> subCon = cq.from(ConWorkkanbanEquipmentEntity.class);

            jakarta.persistence.criteria.Path<Long> pathConWorkKanbanId = subCon.get(ConWorkkanbanEquipmentEntity_.workKanbanId);
            jakarta.persistence.criteria.Path<Long> pathConEquId = subCon.get(ConWorkkanbanEquipmentEntity_.equipmentId);

            Subquery<Long> conSubquery = cq.subquery(Long.class);

            List<Predicate> subWhere = new ArrayList();

            // 設備ID
            if (!equipmentIdCollection.isEmpty()) {
                subWhere.add(pathConEquId.in(equipmentIdCollection));
            }

            // 設備名または設備識別名
            if (Objects.nonNull(condition.getEquipmentNameCollection())) {
                Root<EquipmentEntity> subEqu = cq.from(EquipmentEntity.class);

                jakarta.persistence.criteria.Path<Long> pathId = subEqu.get(EquipmentEntity_.equipmentId);
                jakarta.persistence.criteria.Path<String> pathName = subEqu.get(EquipmentEntity_.equipmentName);
                jakarta.persistence.criteria.Path<String> pathIdent = subEqu.get(EquipmentEntity_.equipmentIdentify);

                Subquery<Long> equSubquery = cq.subquery(Long.class);

                List<Predicate> equSubWhere = new ArrayList<>();

                for (String name : condition.getEquipmentNameCollection()) {
                    equSubWhere.add(cb.like(cb.lower(pathName), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(name)) + "%"));
                    equSubWhere.add(cb.like(pathName, "%" + StringUtils.escapeLikeChar(name) + "%"));
                    equSubWhere.add(cb.like(cb.lower(pathIdent), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(name)) + "%"));
                    equSubWhere.add(cb.like(pathIdent, "%" + StringUtils.escapeLikeChar(name) + "%"));
                }

                equSubquery.select(pathId)
                        .where(cb.or(equSubWhere.toArray(new Predicate[equSubWhere.size()])));

                subWhere.add(pathConEquId.in(equSubquery));
            }

            conSubquery.select(pathConWorkKanbanId)
                    .where(cb.and(subWhere.toArray(new Predicate[subWhere.size()])));

            where.add(pathWorkKanbanId.in(conSubquery));
        }

        // 組織
        if (!organizationIdCollection.isEmpty() || Objects.nonNull(condition.getOrganizationNameCollection())) {
            Root<ConWorkkanbanOrganizationEntity> subCon = cq.from(ConWorkkanbanOrganizationEntity.class);

            jakarta.persistence.criteria.Path<Long> pathConWorkKanbanId = subCon.get(ConWorkkanbanOrganizationEntity_.workKanbanId);
            jakarta.persistence.criteria.Path<Long> pathConOrgId = subCon.get(ConWorkkanbanOrganizationEntity_.organizationId);

            Subquery<Long> conSubquery = cq.subquery(Long.class);

            List<Predicate> subWhere = new ArrayList<>();

            // 組織ID
            if (!organizationIdCollection.isEmpty()) {
                subWhere.add(pathConOrgId.in(organizationIdCollection));
            }

            // 組織名または組織識別名
            if (Objects.nonNull(condition.getOrganizationNameCollection())) {
                Root<OrganizationEntity> subOrg = cq.from(OrganizationEntity.class);

                jakarta.persistence.criteria.Path<Long> pathId = subOrg.get(OrganizationEntity_.organizationId);
                jakarta.persistence.criteria.Path<String> pathName = subOrg.get(OrganizationEntity_.organizationName);
                jakarta.persistence.criteria.Path<String> pathIdent = subOrg.get(OrganizationEntity_.organizationIdentify);

                Subquery<Long> orgSubquery = cq.subquery(Long.class);

                List<Predicate> orgSubWhere = new ArrayList<>();

                for (String name : condition.getOrganizationNameCollection()) {
                    orgSubWhere.add(cb.like(cb.lower(pathName), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(name)) + "%"));
                    orgSubWhere.add(cb.like(pathName, "%" + StringUtils.escapeLikeChar(name) + "%"));
                    orgSubWhere.add(cb.like(cb.lower(pathIdent), "%" + StringUtils.escapeLikeChar(StringUtils.toLowerCase(name)) + "%"));
                    orgSubWhere.add(cb.like(pathIdent, "%" + StringUtils.escapeLikeChar(name) + "%"));
                }

                orgSubquery.select(pathId)
                        .where(cb.or(orgSubWhere.toArray(new Predicate[orgSubWhere.size()])));

                subWhere.add(pathConOrgId.in(orgSubquery));
            }

            conSubquery.select(pathConWorkKanbanId)
                    .where(cb.and(subWhere.toArray(new Predicate[subWhere.size()])));

            where.add(pathWorkKanbanId.in(conSubquery));
        }

        // 計画日時
        if (Objects.nonNull(condition.getFromDate())) {
            where.add(cb.or(
                    cb.greaterThanOrEqualTo(pathStartDatetime, condition.getFromDate()),
                    cb.greaterThanOrEqualTo(pathCompDatetime, condition.getFromDate())
            ));
        }
        if (Objects.nonNull(condition.getToDate())) {
            where.add(cb.or(
                    cb.lessThanOrEqualTo(pathStartDatetime, condition.getToDate()),
                    cb.lessThanOrEqualTo(pathCompDatetime, condition.getToDate())
            ));
        }

        // 工程カンバンID一覧
        if (Objects.nonNull(condition.getWorkKanbanCollection())) {
            where.add(pathWorkKanbanId.in(condition.getWorkKanbanCollection()));
        }

        // 要実績出力フラグ
        if (Objects.nonNull(condition.getOutputFlag())) {
            where.add(pathNeedActualOutputFlag.in(condition.getOutputFlag()));
        }

        // 追加工程フラグ
        if (Objects.nonNull(condition.getSeparateWorkFlag())) {
            where.add(cb.equal(pathSeparateWorkFlag, condition.getSeparateWorkFlag()));
        }

        if (SearchType.COUNT.equals(type)) {
            cq.select(cb.count(pathWorkKanbanId))
                    .where(cb.and(where.toArray(new Predicate[where.size()])));
        } else {
            cq.select(poolWorkKanban)
                    .where(cb.and(where.toArray(new Predicate[where.size()])))
                    .orderBy(cb.asc(pathStartDatetime), cb.asc(pathWorkKanbanId));
        }

        return this.em.createQuery(cq);
    }

    /**
     * 工程カンバン情報を登録する。
     *
     * @param entity 工程カンバン情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(WorkKanbanEntity entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("add: {}, authId={}", entity, authId);
        try {
            // 工程カンバン情報を登録する。
            super.create(entity);
            this.em.flush();

            // 関連付け情報を登録する。
            this.addConnection(entity);

            // 作成した情報を元に、戻り値のURIを作成する。
            URI uri = new URI(new StringBuilder("kanban/work/").append(entity.getWorkKanbanId()).toString());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 工程カンバン情報一覧を登録する。
     *
     * @param entities 工程カンバン情報一覧
     */
    @ExecutionTimeLogging
    public void addAll(List<WorkKanbanEntity> entities) {
        logger.info("addAll num={}", entities.size());
        for (WorkKanbanEntity entity : entities) {
            // 工程カンバン情報を登録する。
            super.create(entity);
            this.em.flush();

            // 関連付け情報を登録する。
            this.addConnection(entity);
        }
    }

    /**
     * 工程カンバン情報を更新する。
     *
     * @param entity 工程カンバン情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(WorkKanbanEntity entity, @QueryParam("authId") Long authId) {
        logger.info("update: {}, authId={}", entity, authId);

        // 工程カンバン情報を更新する。(基本情報のみ)
        this.editWorkKanban(entity);

        // 関連付け情報を更新する。
        this.removeConnection(entity.getWorkKanbanId());
        this.addConnection(entity);

        return Response.ok().entity(ResponseEntity.success()).build();
    }

    /**
     * 工程カンバン情報を更新する。(基本情報のみ)
     *
     * @param entity
     */
    // findSearchRange の処理が長時間かかった場合、実績通知APIでタイムアウトが発生するため、@AccessTimeoutを設定
    @AccessTimeout(60000)
    public void editWorkKanban(WorkKanbanEntity entity) {
        super.edit(entity);
    }

    /**
     * カンバン検索条件を指定して、工程グループ情報一覧を取得する。
     *
     * @param condition 検索条件
     * @param authId 認証ID
     * @return　工程グループ情報一覧
     */
    @Lock(LockType.READ)
    @PUT
    @Path("search/group")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkKanbanGroupEntity> searchGroup(KanbanSearchCondition condition, @QueryParam("authId") Long authId) {
        logger.info("searchGroup: {}, authId={}", condition, authId);

        Map<Long, String> workflowMap = new HashMap<>();

        Query query = this.getSearchQuery(SearchType.SEARCH, condition);
        List<WorkKanbanEntity> workKanbans = query.getResultList();
        for (WorkKanbanEntity workKanban : workKanbans) {
            // カンバン情報を取得する。
            Query kanbanQuery = this.em.createNamedQuery("KanbanEntity.findByKanbanId", KanbanEntity.class);
            kanbanQuery.setParameter("kanbanId", workKanban.getKanbanId());
            KanbanEntity kanban = (KanbanEntity) kanbanQuery.getSingleResult();

            // 生産タイプが「ロット生産」以外の場合、工程順名のセットは行なわない。
            if (kanban.getProductionType() != 1) {
                continue;
            }

            // 工程順名を取得してセットする。(workflowMapに未読込の場合のみクエリ実行)
            if (workflowMap.containsKey(workKanban.getWorkflowId())) {
                workKanban.setWorkflowName(workflowMap.get(workKanban.getWorkflowId()));
            } else {
                // 工程順名を取得する。
                TypedQuery<String> workflowQuery = em.createNamedQuery("WorkflowEntity.findWorkflowName", String.class);
                workflowQuery.setParameter("workflowId", workKanban.getWorkflowId());
                String workflowName = workflowQuery.getSingleResult();
                workKanban.setWorkflowName(workflowName);
                workflowMap.put(workKanban.getWorkflowId(), workflowName);
            }
        }

        // 開始時間と工程順名でグループ化する。
        List<WorkKanbanGroupEntity> entities = new LinkedList<>();
        Map<WorkKanbanGroupKey, List<Long>> group = workKanbans.stream()
                .filter(o -> !StringUtils.isEmpty(o.getWorkflowName()))
                .collect(Collectors.groupingBy(o -> o.getGroupKey(), Collectors.mapping(o -> o.getWorkKanbanId(), Collectors.toList())));
        group.forEach((k, v) -> entities.add(WorkKanbanGroupEntity.createGroup(k, v)));

        return entities;
    }

    /**
     * 工程カンバン情報一覧を更新する。
     *
     * @param kanbanId カンバンID
     * @param entities 工程カンバン情報一覧
     */
    @ExecutionTimeLogging
    public void updateAll(Long kanbanId, List<WorkKanbanEntity> entities) {
        logger.info("updateAll: kanbanId={}, num={}", kanbanId, entities.size());

        //工程カンバンの追加・更新
        List<Long> workKanbanIds = new ArrayList<>();

        for (WorkKanbanEntity entity : entities) {
            if (Objects.isNull(entity.getWorkKanbanId())) {
                entity.setWorkStatus(KanbanStatusEnum.PLANNED);

                // 工程カンバン情報を登録する。
                super.create(entity);
                this.em.flush();

                // 関連付け情報を登録する。
                this.addConnection(entity);
            } else {
                super.edit(entity);

                // 関連付け情報を更新する。
                this.removeConnection(entity.getWorkKanbanId());
                this.addConnection(entity);
            }

            workKanbanIds.add(entity.getWorkKanbanId());
        }

        this.em.flush();

        //工程カンバンの関連付け削除
        // カンバンIDを指定して、工程カンバン情報一覧を取得する。
        TypedQuery<WorkKanbanEntity> query = this.em.createNamedQuery("WorkKanbanEntity.findByKanbanId", WorkKanbanEntity.class);
        query.setParameter("kanbanId", kanbanId);
        for (WorkKanbanEntity entity : query.getResultList()) {
            if (!workKanbanIds.contains(entity.getWorkKanbanId())) {
                entity.setKanbanId(0L);
                super.edit(entity);
            }
        }

        this.em.flush();
    }

    /**
     * 工程カンバン情報を削除する。
     *
     * @param entities 工程カンバン情報一覧
     */
    @ExecutionTimeLogging
    public void removeAll(List<WorkKanbanEntity> entities) {
        logger.info("removeAll num={}", entities.size());
        for (WorkKanbanEntity entity : entities) {
            // 関連付け情報を削除する。
            this.removeConnection(entity.getWorkKanbanId());
            super.remove(entity);
        }
    }

    /**
     * 詳細情報を取得してセットする。
     *
     * @param entity 工程カンバン情報
     */
    @Lock(LockType.READ)
    private void getDetails(WorkKanbanEntity entity) {
        try {
            // 工程カンバンの詳細情報を取得する。
            TypedQuery<WorkKanbanDetail> query = this.em.createNamedQuery("WorkKanbanEntity.findDetails", WorkKanbanDetail.class);
            query.setParameter("workKanbanId", entity.getWorkKanbanId());

            WorkKanbanDetail detail = query.getSingleResult();

            // カンバン名
            entity.setKanbanName((Objects.isNull(detail.getKanbanName())) ? "" : detail.getKanbanName());
            // カンバンステータス
            entity.setKanbanStatus(detail.getKanbanStatus());
            // 工程順名
            entity.setWorkflowName((Objects.isNull(detail.getWorkflowName())) ? "" : detail.getWorkflowName());
            // 工程名
            entity.setWorkName((Objects.isNull(detail.getWorkName())) ? "" : detail.getWorkName());
            // コンテンツ
            entity.setContent((Objects.isNull(detail.getContent())) ? "" : detail.getContent());
            // コンテンツ種別
            entity.setContentType(detail.getContentType());

        } catch (NoResultException ex) {
            logger.fatal(ex);
        }
    }

    /**
     * 工程カンバンIDを指定して、関連付けられた設備ID一覧を取得する。
     *
     * @param id 工程カンバンID
     * @return 設備ID一覧
     */
    @Lock(LockType.READ)
    public List<Long> getEquipmentCollection(Long id) {
        if (Objects.isNull(id)) {
            return new ArrayList<>();
        }

        TypedQuery<Long> query = this.em.createNamedQuery("ConWorkkanbanEquipmentEntity.findEquipmentIdByWorkKanbanId", Long.class);
        query.setParameter("workKanbanId", id);
        return query.getResultList();
    }

    /**
     * 工程カンバンIDを指定して、関連付けられた組織ID一覧を取得する。
     *
     * @param id 工程カンバンID
     * @return 組織ID一覧
     */
    @Lock(LockType.READ)
    public List<Long> getOrganizationCollection(Long id) {
        if (Objects.isNull(id)) {
            return new ArrayList<>();
        }

        TypedQuery<Long> query = this.em.createNamedQuery("ConWorkkanbanOrganizationEntity.findOrganizationIdByWorkKanbanId", Long.class);
        query.setParameter("workKanbanId", id);
        return query.getResultList();
    }

    /**
     * 工程カンバンID一覧を指定して、関連付けられた組織ID一覧を取得する。
     *
     * @param ids 工程カンバンID一覧
     * @return 組織ID一覧
     */
    @Lock(LockType.READ)
    public List<Long> getOrganizationCollection(List<Long> ids) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        TypedQuery<Long> query = this.em.createNamedQuery("ConWorkkanbanOrganizationEntity.findOrganizationId", Long.class);
        query.setParameter("workKanbanIds", ids);
        return query.getResultList();
    }

    /**
     * 関連付け情報を削除する。
     *
     * @param id 工程カンバンID
     */
    private void removeConnection(Long id) {
        // 工程カンバンIDを指定して、工程カンバン・設備関連付け情報を削除する。
        removeEquipmentConnection(id);

        // 工程カンバンIDを指定して、工程カンバン・組織関連付け情報を削除する。
        removeOrganizationConnection(id);
    }

    /**
     * 工程カンバンIDを指定して、工程カンバン・設備関連付け情報を削除する。
     * @param id 工程カンバンID
     */
    public void removeEquipmentConnection(Long id) {
        Query query1 = this.em.createNamedQuery("ConWorkkanbanEquipmentEntity.removeByWorkKanbanId");
        query1.setParameter("workKanbanId", id);
        query1.executeUpdate();
    }

    /**
     * 工程カンバンIDを指定して、工程カンバン・組織関連付け情報を削除する。
     * @param id 工程カンバンID
     */
    public void removeOrganizationConnection(Long id) {
        Query query2 = this.em.createNamedQuery("ConWorkkanbanOrganizationEntity.removeByWorkKanbanId");
        query2.setParameter("workKanbanId", id);
        query2.executeUpdate();
    }


    /**
     * 関連付け情報を登録する。
     *
     * @param entity 工程カンバン情報
     */
    public void addConnection(WorkKanbanEntity entity) {
        // 工程カンバン・設備関連付け情報を登録する。
        if (Objects.nonNull(entity.getEquipmentCollection())) {
            for (Long id : entity.getEquipmentCollection()) {
                ConWorkkanbanEquipmentEntity con = new ConWorkkanbanEquipmentEntity(entity.getWorkKanbanId(), id);
                this.em.persist(con);
            }
        }

        // 工程カンバン・組織関連付け情報を登録する。
        if (Objects.nonNull(entity.getOrganizationCollection())) {
            for (Long id : entity.getOrganizationCollection()) {
                ConWorkkanbanOrganizationEntity con = new ConWorkkanbanOrganizationEntity(entity.getWorkKanbanId(), id);
                this.em.persist(con);
            }
        }
    }

    /**
     * 実施フラグを更新する。
     *
     * @param kanbanId カンバンID
     * @param workId 工程ID
     * @param serialNumber シリアル番号
     * @throws Exception
     */
    public void updateImplementFlag(Long kanbanId, Long workId, Integer serialNumber) throws Exception {
        logger.info("updateImplementFlag: kanbanId={}, workId={}, serialNumber={}", kanbanId, workId, serialNumber);

        TypedQuery<Long> query;
        if (Objects.isNull(serialNumber)) {
            query = this.em.createNamedQuery("WorkKanbanEntity.updateImplementFlagByWorkId", Long.class);
        } else {
            query = this.em.createNamedQuery("WorkKanbanEntity.updateImplementFlagBySerialNumber", Long.class);
            query.setParameter("serialNumber", serialNumber);
        }
        query.setParameter("kanbanId", kanbanId);
        query.setParameter("workId", workId);

        int count = query.executeUpdate();

        logger.info("updateImplementFlag end:{}", count);
    }

    /**
     * 工程カンバン情報を取得する。
     *
     * @param kanbanId カンバンID
     * @param workId 工程ID
     * @param serialNumber シリアル番号
     * @return 工程カンバン情報
     * @throws Exception
     */
    @Lock(LockType.READ)
    public WorkKanbanEntity findBySerial(Long kanbanId, Long workId, Integer serialNumber) throws Exception {
        try {
            // カンバンID・工程ID・シリアル番号を指定して、工程カンバン情報一覧を取得する。
            TypedQuery<WorkKanbanEntity> query = this.em.createNamedQuery("WorkKanbanEntity.findByWorkIdAndSerialNumber", WorkKanbanEntity.class);
            query.setParameter("kanbanId", kanbanId);
            query.setParameter("workId", workId);
            query.setParameter("serialNumber", serialNumber);
            return query.getSingleResult();
        } catch (NoResultException ex) {
            logger.warn("findBySerial: NoResultException kanbanId={}, workId={}, serialNumber={}", kanbanId, workId, serialNumber);
            return null;
        }
    }

    /**
     * 工程カンバン情報一覧を取得する。
     *
     * @param kanbanId カンバンID
     * @param serialNumber シリアル番号
     * @return 工程カンバン情報一覧
     * @throws Exception
     */
    @Lock(LockType.READ)
    public List<WorkKanbanEntity> findBySerial(Long kanbanId, Integer serialNumber) throws Exception {
        try {
            // カンバンID・シリアル番号を指定して、工程カンバン情報一覧を取得する。
            TypedQuery<WorkKanbanEntity> query = this.em.createNamedQuery("WorkKanbanEntity.findBySerialNumber", WorkKanbanEntity.class);
            query.setParameter("kanbanId", kanbanId);
            query.setParameter("serialNumber", serialNumber);
            return query.getResultList();
        } catch (NoResultException ex) {
            logger.warn("findBySerial: NoResultException kanbanId={}, serialNumber={}", kanbanId, serialNumber);
            return null;
        }
    }

    /**
     * カンバンID・工程番号・設備ID・組織IDを指定して、次の工程カンバン情報を取得する。
     *
     * @param kanbanId カンバンID
     * @param workIdent 工程番号(工程追加情報の「工程番号」の値)
     * @param equipmentId 設備ID
     * @param organizationId 組織ID
     * @param authId 認証ID
     * @return 工程カンバン情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("next")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public WorkKanbanEntity nextWorkKanban(@QueryParam("kanbanId") Long kanbanId, @QueryParam("workIdent") String workIdent, @QueryParam("equipmentId") Long equipmentId, @QueryParam("organizationId") Long organizationId, @QueryParam("authId") Long authId) {
        logger.info("nextWorkKanban: kanbanId={}, workIdent={}, equipmentId={}, organizationId={}, authId={}", kanbanId, workIdent, equipmentId, organizationId, authId);
        try {
            // 工程番号の追加情報の名称
            String workPropName = FileManager.getInstance().getSystemProperties().getProperty("workIdentProperty", "工程番号");

            // 指定したプロパティ名・値の追加情報を持つ工程ID一覧を取得する。
            TypedQuery<Long> workQuery = this.em.createNamedQuery("WorkEntity.findByWorkPropValue", Long.class);
            workQuery.setParameter(1, workPropName);
            workQuery.setParameter(2, workIdent);

            List<Long> workIds = workQuery.getResultList();
            if (workIds.isEmpty()) {
                return null;
            }

            // 次の工程カンバンを取得する。
            TypedQuery<WorkKanbanEntity> workKanbanQuery = this.em.createNamedQuery("WorkKanbanEntity.findNext", WorkKanbanEntity.class);
            workKanbanQuery.setParameter("kanbanId", kanbanId);
            workKanbanQuery.setParameter("workIds", workIds);
            workKanbanQuery.setParameter("equipmentIds", this.equipmentRest.getEquipmentPerpetuity(equipmentId));
            workKanbanQuery.setParameter("organizationIds", this.organizationRest.getOrganizationPerpetuity(organizationId));
            List<WorkKanbanEntity> entities = workKanbanQuery.getResultList();

            if (entities.isEmpty()) {
                return null;
            }

            WorkKanbanEntity workKanban = entities.get(0);

            // 詳細情報を取得してセットする。
            this.getDetails(workKanban);

            return workKanban;
        } finally {
            logger.info("nextWorkKanban end.");
        }
    }
    
    /**
     * 指定したシリアル番号の工程カンバンを取得する。
     * 
     * @param kanbanId カンバンID
     * @param uid シリアル番号
     * @param workName 工程名
     * @param equipmentId 設備ID
     * @param support true: 応援者、false: 主作業者  
     * @param authId 組織ID
     * @return Response
     */
    @Lock(LockType.READ)
    @GET
    @Path("product")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response findWorkKanbanByUID(@QueryParam("kanbanId") Long kanbanId, @QueryParam("uid") String uid, @QueryParam("workName") String workName, @QueryParam("equipmentId") Long equipmentId, @QueryParam("support") Boolean support, @QueryParam("authId") Long authId) {
        logger.info("findWorkKanbanByUID: kanbanId={}, uid={}, workName={}, equipmentId={}, support={}, authId={}", kanbanId, uid, workName, equipmentId, support, authId);

        KanbanEntity kanban;

        try {
            TypedQuery<KanbanEntity> kanbanQuery = this.em.createNamedQuery("KanbanEntity.findByKanbanId", KanbanEntity.class);
            kanbanQuery.setParameter("kanbanId", kanbanId);
            kanban = kanbanQuery.getSingleResult();
        } catch (NoResultException ex) {
            // カンバンが見つからない
            return Response.ok().entity(ResponseWorkKanbanEntity.failed(ServerErrorTypeEnum.NOTFOUND_KANBAN)).build();
        }
        
        boolean exist = false;
        List<KanbanProduct> products = KanbanProduct.lookupProductList(kanban.getServiceInfo(), ServiceInfoEntity.SERVICE_INFO_PRODUCT);
        for (KanbanProduct product : products) {
            if (StringUtils.equals(product.getUid(), uid)) {
                exist = true;
                break;
            }
        }
        
        if (!exist) {
            // シリア番号が見つからない
            return Response.ok().entity(ResponseWorkKanbanEntity.failed(ServerErrorTypeEnum.NOTFOUND_SERIAL_NO)).build();
        }

        TypedQuery<Long> query = this.em.createNamedQuery("WorkKanbanEntity.findProductByUID", Long.class);
        query.setParameter(1, uid);
        query.setParameter(2, kanbanId);
        List<Long> workKanbanIds = query.getResultList();

        List<WorkKanbanEntity> list = this.getWorkKanbans(kanbanId, workName, equipmentId, support, authId, workKanbanIds);
        if (list.isEmpty()) {
            // 割り当てられた作業はありません
            return Response.ok().entity(ResponseWorkKanbanEntity.failed(ServerErrorTypeEnum.NOT_ASSIGNED_WORK)).build();
        }

        return Response.ok().entity(ResponseWorkKanbanEntity.success(list)).build();
    }
    
    /**
     * 工程カンバンを取得する。
     * 
     * @param kanbanId カンバンID
     * @param workName 工程名
     * @param equipmentId 設備ID
     * @param support true: 応援者、false: 主作業者  
     * @param organizationId 組織ID
     * @param workKanbanIds 工程カンバンID
     * @return 工程カンバン一覧
     */
    private List<WorkKanbanEntity> getWorkKanbans(Long kanbanId, String workName, Long equipmentId, Boolean support, Long organizationId, List<Long> workKanbanIds) {
        KanbanSearchCondition condition = new KanbanSearchCondition();
        condition.setKanbanId(kanbanId);
        condition.setImplementFlag(true);
        condition.setSkipFlag(false);
        if (support) {
            condition.setKanbanStatusCollection(Arrays.asList(KanbanStatusEnum.WORKING));
        } else {
            condition.setKanbanStatusCollection(Arrays.asList(KanbanStatusEnum.PLANNED, KanbanStatusEnum.WORKING, KanbanStatusEnum.SUSPEND, KanbanStatusEnum.DEFECT));
        }
        condition.setParentStatusCollection(Arrays.asList(KanbanStatusEnum.PLANNED, KanbanStatusEnum.WORKING, KanbanStatusEnum.SUSPEND, KanbanStatusEnum.DEFECT));
        condition.setEquipmentIdWithParent(true);
        condition.setEquipmentCollection(Arrays.asList(equipmentId));
        condition.setOrganizationIdWithParent(true);
        condition.setOrganizationCollection(Arrays.asList(organizationId));
        condition.setIsAdditionalInfo(true);
        
        if (Objects.nonNull(workKanbanIds) && !workKanbanIds.isEmpty()) {
            condition.setKanbanStatusCollection(Arrays.asList(KanbanStatusEnum.PLANNED, KanbanStatusEnum.WORKING, KanbanStatusEnum.SUSPEND, KanbanStatusEnum.COMPLETION, KanbanStatusEnum.DEFECT));
            condition.setWorkKanbanCollection(workKanbanIds);
        }
        
        if (!StringUtils.isEmpty(workName)) {
            try {
                TypedQuery<WorkEntity> workQuery = this.em.createNamedQuery("WorkEntity.findLatestRevByName", WorkEntity.class);
                workQuery.setParameter("workName", workName);
                WorkEntity work = workQuery.getSingleResult();
                condition.setWorkId(work.getWorkId());
            } catch (NoResultException ex) {
                // 工程が見つからない
                // return Response.ok().entity(ResponseWorkKanbanEntity.failed(ServerErrorTypeEnum.NOTFOUND_WORK)).build();
            }
        }

        return this.searchWorkKanban(condition, null, null, null);
    }

    /**
     * 1日の完了数を取得する。
     *
     * @param workflowId 対象工程順ID
     * @param workId 対象工程ID
     * @param date 対象日
     * @return 1日の完了数
     */
    @Lock(LockType.READ)
    @ExecutionTimeLogging
    public Long countCompletionForDay(Long workflowId, Long workId, Date date) {
        logger.info("countCompletionForDay: workflowId={}, workId={}, date={}", workflowId, workId, date);
        try {
            Date fromDate = DateUtils.getBeginningOfDate(date);
            Date toDate = DateUtils.getEndOfDate(date);

            // 1日の完了数を取得する。
            TypedQuery<Long> query = this.em.createNamedQuery("WorkKanbanEntity.countCompletionForDay", Long.class);
            query.setParameter("workflowId", workflowId);
            query.setParameter("workId", workId);
            query.setParameter("fromDate", fromDate);
            query.setParameter("toDate", toDate);

            return query.getSingleResult();
        } finally {
            logger.info("countCompletionForDay end.");
        }
    }

    /**
     * 作業のやり直しをおこなうため、工程カンバン情報を更新する。
     * 
     * @param workKanbanIds 工程カンバンID一覧
     * @return 処理結果
     */
    @PUT
    @Path("/rework")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public Response updateRework(@QueryParam("id") List<Long> workKanbanIds, @QueryParam("authId") Long authId) {
        logger.info("updateRework: workKanbanIds={}", workKanbanIds);

        if (Objects.isNull(workKanbanIds) || workKanbanIds.isEmpty()) {
            return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
        }

        for (Long workKanbanId : workKanbanIds) {
            WorkKanbanEntity workKanban = this.find(workKanbanId);
            if (Objects.isNull(workKanban) || !workKanban.getImplementFlag()) {
                continue;
            }

            workKanban.setWorkStatus(KanbanStatusEnum.PLANNED);
            workKanban.setActualStartTime(null);
            workKanban.setActualCompTime(null);
            if (Objects.isNull(workKanban.getReworkNum())) {
                workKanban.setReworkNum(1);
            } else {
                workKanban.setReworkNum(workKanban.getReworkNum() + 1);
            }
        }
        
        return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.SUCCESS)).build();
    }
    /**
     * スキップ設定・解除をする
     * @param workKanbanIds 設定する工程カンバンID群
     * @param isSkip true:スキップ、false:スキップ解除
     * @return 結果
     */
    @PUT
    @Path("/skip")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public Response updateSkip(@QueryParam("id") List<Long> workKanbanIds, @QueryParam("isSkip") Boolean isSkip) {
        logger.info("updateRework: workKanbanIds={}, skip={}", workKanbanIds, isSkip);

        if (Objects.isNull(workKanbanIds) || workKanbanIds.isEmpty() || Objects.isNull(isSkip)) {
            return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
        }

        for (Long workKanbanId : workKanbanIds) {
            WorkKanbanEntity workKanban = this.find(workKanbanId);
            if (Objects.isNull(workKanban)) {
                continue;
            }
            workKanban.setSkipFlag(isSkip);

            if (isSkip) {
                this.advanceWorkflow(workKanban);
            }
        }

        return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.SUCCESS)).build();
    }

    /**
     * 工程順を進める。
     * 
     * @param workKanban 工程カンバン情報
     */
    private void advanceWorkflow(WorkKanbanEntity workKanban) {
        try {
            logger.info("progressWork start: workKanbanId={}", workKanban.getWorkKanbanId());

            TypedQuery<KanbanEntity> kanbanQuery = this.em.createNamedQuery("KanbanEntity.findByKanbanId", KanbanEntity.class);
            kanbanQuery.setParameter("kanbanId", workKanban.getKanbanId());
            KanbanEntity kanban = kanbanQuery.getSingleResult();
            
            // スキップした工程の実施フラグが立っていたら、工程順を進める
            if (kanban.getKanbanStatus() == KanbanStatusEnum.PLANNING
                || kanban.getKanbanStatus() == KanbanStatusEnum.INTERRUPT
                || kanban.getKanbanStatus() == KanbanStatusEnum.DEFECT
                || !workKanban.getImplementFlag()) {
                logger.info("Not processed: kanbanStatus={} implementFlag={}", kanban.getKanbanStatus(), workKanban.getImplementFlag());
                return;
            }
            
            List<WorkKanbanEntity> workKanbans = this.getWorkKanban(workKanban.getKanbanId(), false, null, null);
            
            TypedQuery<WorkflowEntity> workflowQuery = this.em.createNamedQuery("WorkflowEntity.findByIds", WorkflowEntity.class);
            workflowQuery.setParameter("workflowIds", Arrays.asList(workKanban.getWorkflowId()));
            WorkflowEntity workflow = workflowQuery.getSingleResult();
            
            // 工程順を進める
            WorkflowInteface workflowModel = WorkflowModelFacade.createInstance(this, workKanbans, workflow.getWorkflowDiaglam());
            workflowModel.executeWorkflow(workKanban.getWorkId(), null);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("progressWork end.");
        }
    }
   
    /**
     * 工程カンバン情報の実績出力フラグを更新する。
     *
     * @param workKanbanIds 更新対象の工程カンバン一覧
     * @param outputFlag 更新する実績出力フラグ
     * @param outputDateStr 出力日時(文字列)
     * @return ReposnseEntity.uri 更新カラム数
     */
    @Lock(LockType.READ)
    @PUT
    @Path("/update/output")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response updateOutputFlag(@QueryParam("workKanbanIds") List<Long> workKanbanIds, @QueryParam("outputFlag") Boolean outputFlag, @QueryParam("outputDate") String outputDateStr){
        logger.info("updateOutputFlag: kanbanId={}, workId={}, serialNumber={}", workKanbanIds, outputFlag, outputDateStr);
        int count = 0;
        try {
            String pattern[] = {DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern(), "yyyy-MM-dd-hh-mm-ss"};
            Date outputDate = this.stringToDate(outputDateStr, pattern, null);

            // QueryParamチェック
            if (Objects.isNull(workKanbanIds) || workKanbanIds.isEmpty() || Objects.isNull(outputFlag) || Objects.isNull(outputDate)) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
            }

            TypedQuery<Long> query;
            query = this.em.createNamedQuery("WorkKanbanEntity.updateOutputFlagByWorkKanbanIds", Long.class);
            query.setParameter("workKanbanIds", workKanbanIds);
            query.setParameter("needActualOutputFlag", outputFlag);
            query.setParameter("actualOutputDatetime", outputDate);
            count = query.executeUpdate();

            URI uri = new URI(new StringBuilder("kanban/work/update/output/").append(count).toString());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        } finally {
            logger.info("updateOutputFlag end:{}", count);
        }
    }

    /**
     * String型をDate型に変換する
     * @param dateString 対象文字列
     * @param dateFormat フォーマット
     * @param defaultValue デフォルト日時
     * @return
     */
    @Lock(LockType.READ)
    private Date stringToDate(String dateString, String dateFormat[], Date defaultValue) {
        try {
            return Objects.nonNull(dateString) ? org.apache.commons.lang3.time.DateUtils.parseDate(dateString, dateFormat) : defaultValue;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return defaultValue;
        }
    }

    /**
     * 最終実績IDを更新する。
     *
     * @param id 工程カンバンID
     * @param lastActualId 最終実績ID
     */
    public void updateLastActualId(long id, Long lastActualId) {
        logger.info("updateLastActualId: id={}, lastActualId={}", id, lastActualId);
        Query query = this.em.createNamedQuery("WorkKanbanEntity.updateLastActualId");
        query.setParameter("lastActualId", lastActualId);
        query.setParameter("workKanbanId", id);
        query.executeUpdate();
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public void setOrganizationRest(OrganizationEntityFacadeREST organizationRest) {
        this.organizationRest = organizationRest;
    }

    public void setEquipmentRest(EquipmentEntityFacadeREST equipmentRest) {
        this.equipmentRest = equipmentRest;
    }


    public List<WorkKanbanEntity> findWorkIdByWorkflowWork(Long workflowId, Long workId, Boolean separateFlg) {
        try {
            // カンバンID・工程順ID・工程ID・追加工程フラグを指定して、工程カンバン情報を取得する。
            TypedQuery<WorkKanbanEntity> query = this.em.createNamedQuery("WorkKanbanEntity.findByWorkflowWorkSeparate", WorkKanbanEntity.class);
            query.setParameter("workflowId", workflowId);
            query.setParameter("workId", workId);
            query.setParameter("separateWorkFlag", separateFlg);
            List<WorkKanbanEntity> entity = query.getResultList();

            return entity;
        } catch (Exception ex) {
            logger.fatal(ex,ex);
        }
            return new ArrayList<>();
    }

    
}
