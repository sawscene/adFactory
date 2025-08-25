/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.search.ActualSearchCondition;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryforfujiserver.entity.search.SearchType;
import jp.adtekfuji.adfactoryforfujiserver.entity.unit.ConUnitAssociateEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unit.ConUnitHierarchyEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unit.UnitEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unit.UnitKanbanEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unit.UnitPropertyEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unit.UnitWorkKanbanEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate.ConUnitTemplateAssociateEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate.UnitTemplateEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate.UnitTemplatePropertyEntity;
import jp.adtekfuji.adfactoryforfujiserver.service.standard.ActualResultEntityFacade;
import jp.adtekfuji.adfactoryforfujiserver.service.standard.KanbanEntityFacade;
import jp.adtekfuji.adfactoryforfujiserver.service.standard.WorkflowEntityFacade;
import jp.adtekfuji.adfactoryforfujiserver.utility.DateUtils;
import jp.adtekfuji.adfactoryforfujiserver.utility.ExecutionTimeLogging;
import jp.adtekfuji.forfujiapp.entity.search.UnitSearchCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 生産ユニット情報REST
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.13.Thr
 */
@Singleton
@Path("unit")
public class UnitEntityFacadeREST extends AbstractFacade<UnitEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryForFujiServer_war_1.0PU")
    private EntityManager em;

    private final Logger logger = LogManager.getLogger();

    @EJB
    private UnitTemplateEntityFacadeREST unitTemplateEntityFacadeREST;
    @EJB
    private KanbanEntityFacade kanbanEntityFacade;
    @EJB
    private WorkflowEntityFacade workflowEntityFacade;
    @EJB
    private ActualResultEntityFacade actualResultEntityFacade;

    private final static int UNITTEMPLATE_ENTITYSET_CAPACITY = 100;
    private final static int WORKFLOW_ENTITYSET_CAPACITY = 1000;
    private final static float LOAD_FACTOR = 0.75f;

    private final Map<Long, UnitTemplateEntity> unitTemplateEntitySet = Collections.synchronizedMap(new LinkedHashMap(UNITTEMPLATE_ENTITYSET_CAPACITY, LOAD_FACTOR, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > UNITTEMPLATE_ENTITYSET_CAPACITY;
        }
    });

    private final Map<Long, WorkflowInfoEntity> workflowEntitySet = Collections.synchronizedMap(new LinkedHashMap(WORKFLOW_ENTITYSET_CAPACITY, LOAD_FACTOR, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > WORKFLOW_ENTITYSET_CAPACITY;
        }
    });

    public UnitEntityFacadeREST() {
        super(UnitEntity.class);
    }

    /**
     * 指定されたIDのプロパティ情報を追加
     *
     * @param unitId
     * @return
     */
    @Lock(LockType.READ)
    public UnitEntity find(Long unitId) {
        UnitEntity entity = super.find(unitId);
        if (Objects.isNull(entity)) {
            return new UnitEntity();
        }
        return entity;
    }

    /**
     * 指定されたIDのプロパティ情報を追加
     *
     * @param unitId
     * @return
     */
    @Lock(LockType.READ)
    public UnitEntity findWithoutDatails(Long unitId) {
        UnitEntity entity = super.find(unitId);
        if (Objects.isNull(entity)) {
            return new UnitEntity();
        }
        ConUnitHierarchyEntity hierarchy = findHierarchy(entity.getUnitId());
        if (Objects.nonNull(hierarchy)) {
            entity.setParentId(hierarchy.getConUnitHierarchyEntityPK().getFkUnitHierarchyId());
        }
        return entity;
    }

    /**
     * 指定されたIDの生産ユニットを取得
     *
     * @param unitId 生産ユニットID
     * @return 生産ユニット情報
     * @throws Exception
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public UnitEntity findWithDatails(@PathParam("id") Long unitId) throws Exception {
        logger.info("find:{}", unitId);
        UnitEntity entity = super.find(unitId);
        if (Objects.isNull(entity)) {
            return new UnitEntity();
        }
        //階層情報の追加
        ConUnitHierarchyEntity hierarchy = findHierarchy(entity.getUnitId());
        if (Objects.nonNull(hierarchy)) {
            entity.setParentId(hierarchy.getConUnitHierarchyEntityPK().getFkUnitHierarchyId());
        }
        // ユニットテンプレート情報の追加
        getTemplateData(entity);
        // ユニットプロパティデータの追加
        entity.setUnitPropertyCollection(getProperty(unitId));
        // ユニット関連付けデータの追加
        entity.setConUnitAssociateCollection(getAssociate(unitId, true));
        return entity;
    }

    /**
     * 指定された名前の生産ユニットを取得
     *
     * @param unitName 生産ユニット名
     * @return 生産ユニット情報
     * @throws java.lang.Exception
     */
    @Lock(LockType.READ)
    @GET
    @Path("name")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public UnitEntity findName(@QueryParam("name") String unitName) throws Exception {
        logger.info("findName:{}", unitName);
        TypedQuery<UnitEntity> query = em.createNamedQuery("UnitEntity.findByUnitName", UnitEntity.class);
        query.setParameter("unitName", unitName);
        try {
            UnitEntity entity = query.getSingleResult();
            //階層情報の追加
            ConUnitHierarchyEntity hierarchy = findHierarchy(entity.getUnitId());
            if (Objects.nonNull(hierarchy)) {
                entity.setParentId(hierarchy.getConUnitHierarchyEntityPK().getFkUnitHierarchyId());
            }
            // ユニットテンプレート情報の追加
            getTemplateData(entity);
            // ユニットプロパティデータの追加
            entity.setUnitPropertyCollection(getProperty(entity.getUnitId()));
            // ユニット関連付けデータの追加
            entity.setConUnitAssociateCollection(getAssociate(entity.getUnitId(), true));
            return entity;
        } catch (NoResultException ex) {
            logger.fatal(ex);
            return new UnitEntity();
        }
    }

    /**
     * 生産ユニット情報の作成
     *
     * @param entity 作成する生産ユニット情報
     * @return
     * @throws URISyntaxException
     * @throws Exception
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(UnitEntity entity) throws URISyntaxException, Exception {
        return add(entity, true);
    }

    /**
     * 生産ユニット情報の作成
     *
     * @param entity
     * @param isTop
     * @return
     * @throws URISyntaxException
     * @throws Exception
     */
    public Response add(UnitEntity entity, boolean isTop) throws URISyntaxException, Exception {
        logger.info("add:{}", entity);
        // 重複確認
        TypedQuery<ConUnitHierarchyEntity> query = em.createNamedQuery("ConUnitHierarchyEntity.findByFkUnitHierarchyId", ConUnitHierarchyEntity.class);
        query.setParameter("fkUnitHierarchyId", entity.getParentId());
        List<ConUnitHierarchyEntity> conHierarchys = query.getResultList();
        for (ConUnitHierarchyEntity conHierarchy : conHierarchys) {
            UnitEntity checkTarget = super.find(conHierarchy.getConUnitHierarchyEntityPK().getFkUnitId());
            if (checkTarget.getUnitName().equals(entity.getUnitName())) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }
        }

        // 最初のaddの場合は、カンバン名の長さチェックを行なう。
        if (isTop) {
            Response checkResponse = checkAdd(entity);
            if (Objects.nonNull(checkResponse)) {
                logger.info("***** name length error.");
                return checkResponse;
            }
        }

        UnitTemplateEntity template = getUnitTemplate(entity.getFkUnitTemplateId());
        for (ConUnitTemplateAssociateEntity conTemplate : template.getConUnitTemplateAssociateCollection()) {
            if (Objects.nonNull(conTemplate.getFkUnitTemplateId())) {
                // 関連がユニットテンプレートの場合、ユニット作成を行う
                UnitTemplateEntity tmp = getUnitTemplate(conTemplate.getFkUnitTemplateId());
                Long cnt = Long.parseLong(countBasicSearch(
                        new UnitSearchCondition(entity.getUnitName() + "_" + tmp.getUnitTemplateName(), tmp.getUnitTemplateId(), null, null)));
                if (cnt > 0L) {
                    return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
                }
            } else if (Objects.nonNull(conTemplate.getFkWorkflowId())) {
                // 関連がワークフローだった場合、カンバン作成を行う
                WorkflowInfoEntity workflow = getWorkflow(conTemplate.getFkWorkflowId());
                Long cnt = Long.parseLong(kanbanEntityFacade.countSearch(
                        new KanbanSearchCondition(null, entity.getUnitName() + "_" + workflow.getWorkflowName(), null, workflow.getWorkflowId(), null, null)));
                if (cnt > 0L) {
                    return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
                }
            }
        }
        // 作成
        super.create(entity);
        em.flush();
        // 階層
        if (Objects.nonNull(entity.getParentId())) {
            addHierarchy(entity);
        }
        // ユニットテンプレートを取得.
        if (Objects.nonNull(entity.getFkUnitTemplateId())) {
            // プロパティ
            if (Objects.nonNull(entity.getUnitPropertyCollection())
                    && !entity.getUnitPropertyCollection().isEmpty()) {
                addProperty(entity);
            } else {
                copyProperty(entity, template);
            }

            // ユニットデータ
            Response response = addUnitAssociateDetail(entity, template);
            if (Objects.nonNull(response)) {
                ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
                if (!responseEntity.isSuccess()) {
                    return response;
                }
            }
        }
        URI uri = new URI("unit/" + entity.getUnitId());
        return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
    }

    /**
     * 生産ユニット情報の更新
     *
     * @param entity 作成する生産ユニット情報
     * @return
     * @throws URISyntaxException
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(UnitEntity entity) throws URISyntaxException {
        logger.info("update:{}", entity);
        // 重複確認
        TypedQuery<ConUnitHierarchyEntity> query = em.createNamedQuery("ConUnitHierarchyEntity.findByFkUnitHierarchyId", ConUnitHierarchyEntity.class);
        query.setParameter("fkUnitHierarchyId", entity.getParentId());
        List<ConUnitHierarchyEntity> conHierarchys = query.getResultList();
        for (ConUnitHierarchyEntity conHierarchy : conHierarchys) {
            UnitEntity checkTarget = super.find(conHierarchy.getConUnitHierarchyEntityPK().getFkUnitId());
            if (checkTarget.getUnitName().equals(entity.getUnitName())
                    && !checkTarget.getUnitId().equals(entity.getUnitId())) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }
        }
        // 編集
        super.edit(entity);
        // 階層
        removeHierarchy(entity.getUnitId());
        addHierarchy(entity);
        // プロパティ
        removeProperty(entity.getUnitId());
        addProperty(entity);
        return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.SUCCESS)).build();
    }

    /**
     * 生産ユニット情報の削除
     *
     * @param unitId 削除する生産ユニットID
     * @return 削除結果
     * @throws URISyntaxException
     * @throws Exception
     */
    @DELETE
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response remove(@PathParam("id") Long unitId) throws URISyntaxException, Exception {
        logger.info("remove:{}", unitId);
        //無い情報は削除できない.
        UnitEntity unit = findWithDatails(unitId);
        if (Objects.isNull(unit.getUnitId())) {
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_DELETE)).build();
        }
        if (removeCheckKanban(unitId)) {
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.THERE_START_NON_DELETABLE)).build();
        }
        // カンバンとユニット削除
        removeUnitAssociateDetail(unit.getUnitId());
        // 階層
        removeHierarchy(unit.getUnitId());
        // プロパティ
        removeProperty(unit.getUnitId());
        // 削除
        super.remove(unit);
        return Response.ok().entity(ResponseEntity.success()).build();
    }

    /**
     * 生産ユニット情報の個数を取得する
     *
     * @param condition 検索条件
     * @return ヒット数
     */
    @Lock(LockType.READ)
    public String countBasicSearch(UnitSearchCondition condition) {
        logger.info("countBasicSearch:{}", condition);
        Query query = getSearchQuery(SearchType.COUNT, condition);
        return String.valueOf(query.getSingleResult());
    }

    /**
     * 生産ユニット情報を検索する
     *
     * @param condition 検索条件
     * @param from 検索開始範囲
     * @param to 検索終了範囲
     * @return 該当したユニット情報
     */
    @Lock(LockType.READ)
    @PUT
    @Path("basicsearch/range")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<UnitEntity> findBasicSearch(UnitSearchCondition condition, @QueryParam("from") Integer from, @QueryParam("to") Integer to) {
        logger.info("findBasicSearch:{}", condition);

        Query query = getSearchQuery(SearchType.SEARCH, condition);
        query.setMaxResults(to - from + 1);
        query.setFirstResult(from);
        List<UnitEntity> entities = query.getResultList();
        for (UnitEntity entity : entities) {
            ConUnitHierarchyEntity hierarchy = findHierarchy(entity.getUnitId());
            if (Objects.nonNull(hierarchy)) {
                entity.setParentId(hierarchy.getConUnitHierarchyEntityPK().getFkUnitHierarchyId());
            }
            getTemplateData(entity);

            entity.setUnitPropertyCollection(getProperty(entity.getUnitId()));
            entity.setConUnitAssociateCollection(getAssociate(entity.getUnitId(), false));
        }

        return entities;
    }

    /**
     * 生産ユニット情報を検索する
     *
     * @param condition
     * @return
     */
    @Lock(LockType.READ)
    @PUT
    @Path("search/count")
    @Consumes({"application/xml", "application/json"})
    @Produces({"text/plain"})
    @ExecutionTimeLogging
    public String countSearch(UnitSearchCondition condition) {
        logger.info("countSearch:{}", condition);

        if (Objects.isNull(condition.getUnittemplateIdCollection()) || condition.getUnittemplateIdCollection().isEmpty()
                || Objects.isNull(condition.getFromDate()) || Objects.isNull(condition.getToDate())) {
            throw new IllegalArgumentException("Invalid arguments.");
        }

        TypedQuery<UnitEntity> query = em.createNamedQuery("UnitEntity.countByUnitTemplateIds", UnitEntity.class);
        query.setParameter("unitTemplateIds", condition.getUnittemplateIdCollection());
        query.setParameter("fromDate", condition.getFromDate(), TemporalType.TIMESTAMP);
        query.setParameter("toDate", condition.getToDate(), TemporalType.TIMESTAMP);

        return String.valueOf(query.getSingleResult());
    }

    /**
     * 生産ユニット情報を検索する
     *
     * @param condition
     * @param from
     * @param to
     * @return
     */
    @Lock(LockType.READ)
    @PUT
    @Path("search/range")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<UnitEntity> findSearchRange(UnitSearchCondition condition, @QueryParam("from") Integer from, @QueryParam("to") Integer to) {
        logger.info("findSearchRange:{}", condition);

        if (Objects.isNull(condition.getUnittemplateIdCollection()) || condition.getUnittemplateIdCollection().isEmpty()
                || Objects.isNull(condition.getFromDate()) || Objects.isNull(condition.getToDate())) {
            throw new IllegalArgumentException("Invalid arguments.");
        }

        TypedQuery<UnitEntity> query = em.createNamedQuery("UnitEntity.findByUnitTemplateIds", UnitEntity.class);
        query.setParameter("unitTemplateIds", condition.getUnittemplateIdCollection());
        query.setParameter("fromDate", condition.getFromDate(), TemporalType.TIMESTAMP);
        query.setParameter("toDate", condition.getToDate(), TemporalType.TIMESTAMP);

        if (Objects.nonNull(to) && Objects.nonNull(from)) {
            query.setMaxResults(to - from + 1);
            query.setFirstResult(from);
        }

        List<UnitEntity> entities = query.getResultList();
        if (condition.IsWithAssociate()) {
            for (UnitEntity entity : entities) {
                //ConUnitHierarchyEntity hierarchy = findHierarchy(entity.getUnitId());
                //if (Objects.nonNull(hierarchy)) {
                //    entity.setParentId(hierarchy.getConUnitHierarchyEntityPK().getFkUnitHierarchyId());
                //}

                // 生産ユニットテンプレート名
                UnitTemplateEntity unitTemplate = this.unitTemplateEntityFacadeREST.find(entity.getFkUnitTemplateId());
                entity.setUnitTemplateName(unitTemplate.getUnitTemplateName());

                // プロパティ
                List<UnitPropertyEntity> unitProperties = getProperty(entity.getUnitId());
                entity.setUnitPropertyCollection(unitProperties);

                //entity.setConUnitAssociateCollection(getAssociate(entity.getUnitId(), false));
                // カンバンID
                List<Long> kanbanIds = this.getKanbanIds(entity.getUnitId());
                entity.setKanbanIds(kanbanIds);
            }
        } else {
            for (UnitEntity entity : entities) {
                // 生産ユニットテンプレート名
                UnitTemplateEntity unitTemplate = this.unitTemplateEntityFacadeREST.find(entity.getFkUnitTemplateId());
                entity.setUnitTemplateName(unitTemplate.getUnitTemplateName());

                // カンバンID
                List<Long> kanbanIds = this.getKanbanIds(entity.getUnitId());
                entity.setKanbanIds(kanbanIds);
            }
        }
        return entities;
    }

    /**
     * ユニットエンティティが使用したテンプレートの名前を取得する
     *
     * @param entity 関連データを入れるユニット
     */
    private void getTemplateData(UnitEntity entity) {
        if (Objects.nonNull(entity.getFkUnitTemplateId())) {
            UnitTemplateEntity template = unitTemplateEntityFacadeREST.findWithoutDatails(entity.getFkUnitTemplateId());
            entity.setUnitTemplateName(template.getUnitTemplateName());
            entity.setWorkflowDiaglam(template.getWorkflowDiaglam());
        }
    }

    /**
     * 指定されてたIDの親階層の情報を返す
     *
     * @param unitId ユニットID
     * @return 階層関連付け
     */
    private ConUnitHierarchyEntity findHierarchy(Long unitId) {
        TypedQuery<ConUnitHierarchyEntity> query = em.createNamedQuery("ConUnitHierarchyEntity.findByFkUnitId", ConUnitHierarchyEntity.class);
        query.setParameter("fkUnitId", unitId);
        query.setMaxResults(1);
        ConUnitHierarchyEntity hierarchy = null;
        try {
            hierarchy = query.getSingleResult();
        } catch (NoResultException ex) {
            //親なしってこと.
        }
        return hierarchy;
    }

    /**
     * ユニットの階層関連付け追加
     *
     * @param entity 階層関連付けを行うユニットテンプレート
     */
    private void addHierarchy(UnitEntity entity) {
        if (Objects.nonNull(entity.getParentId())) {
            ConUnitHierarchyEntity hierarchy = new ConUnitHierarchyEntity(entity.getParentId(), entity.getUnitId());
            em.persist(hierarchy);
        }
    }

    /**
     * ユニットの階層関連付け削除
     *
     * @param entity 階層関連付けの削除を行うユニットテンプレート
     */
    private void removeHierarchy(Long unitId) {
        Query query = em.createNamedQuery("ConUnitHierarchyEntity.removeByFkUnitId");
        query.setParameter("fkUnitId", unitId);
        query.executeUpdate();
    }

    /**
     * ユニットテンプレートエンティティの詳細を取得する(キャッシュ機能付き)
     *
     * @param templateId ユニットテンプレートID
     * @return ユニットテンプレート情報
     */
    private UnitTemplateEntity getUnitTemplate(Long templateId) {
        UnitTemplateEntity template = null;
        if (unitTemplateEntitySet.containsKey(templateId)) {
            template = unitTemplateEntitySet.get(templateId);
            UnitTemplateEntity nowTemplate = unitTemplateEntityFacadeREST.findWithoutDatails(templateId);
            if (nowTemplate.getUpdateDatetime().after(template.getUpdateDatetime())) {
                template = unitTemplateEntityFacadeREST.findWithDatails(templateId);
                unitTemplateEntitySet.put(templateId, template);
            }
        } else {
            template = unitTemplateEntityFacadeREST.findWithDatails(templateId);
            unitTemplateEntitySet.put(templateId, template);
        }
        return template;
    }

    /**
     * 工程順エンティティの詳細を取得する(キャッシュ機能付き)
     *
     * @param workflowId 工程順ID
     * @return 工程順情報
     */
    private WorkflowInfoEntity getWorkflow(Long workflowId) {
        WorkflowInfoEntity workflow = null;
        if (workflowEntitySet.containsKey(workflowId)) {
            workflow = workflowEntitySet.get(workflowId);
            WorkflowInfoEntity nowWorkflow = workflowEntityFacade.find(workflowId);
            if (nowWorkflow.getUpdateDatetime().after(workflow.getUpdateDatetime())) {
                workflow = workflowEntityFacade.find(workflowId);
                workflowEntitySet.put(workflowId, workflow);
            }
        } else {
            workflow = workflowEntityFacade.find(workflowId);
            workflowEntitySet.put(workflowId, workflow);
        }
        return workflow;
    }

    /**
     * 指定されたユニットIDのプロパティデータ取得
     *
     * @param unitId ユニットID
     * @return プロパティ
     */
    public List<UnitPropertyEntity> getProperty(Long unitId) {
        TypedQuery<UnitPropertyEntity> query = em.createNamedQuery("UnitPropertyEntity.findByFkUnitId", UnitPropertyEntity.class);
        query.setParameter("fkUnitId", unitId);
        return query.getResultList();
    }

    /**
     * 指定されたユニットのプロパティデータを追加する
     *
     * @param entity ユニット情報
     */
    private void addProperty(UnitEntity entity) {
        if (Objects.nonNull(entity.getUnitPropertyCollection())) {
            for (UnitPropertyEntity property : entity.getUnitPropertyCollection()) {
                //property.setKanbannPropertyId(null);
                property.setFkUnitId(entity.getUnitId());
                em.persist(property);
            }
        }
    }

    /**
     * ユニットのプロパティを複製
     *
     * @param entity プロパティを紐づける生産ユニット
     * @param template 生産ユニットのテンプレート情報
     */
    private void copyProperty(UnitEntity entity, UnitTemplateEntity template) {
        // ユニットテンプレートのプロパティをコピーする
        if (Objects.nonNull(template.getUnitTemplatePropertyCollection())) {
            for (UnitTemplatePropertyEntity propTemp : template.getUnitTemplatePropertyCollection()) {
                UnitPropertyEntity prop = new UnitPropertyEntity(entity.getUnitId(),
                        propTemp.getUnitTemplatePropertyName(), propTemp.getUnitTemplatePropertyType(), propTemp.getUnitTemplatePropertyValue(), propTemp.getUnitTemplatePropertyOrder());
                em.persist(prop);
            }
        }
    }

    /**
     * 指定されたユニットIDのプロパティデータ削除
     *
     * @param unitId ユニットID
     */
    private void removeProperty(Long unitId) {
        Query query = em.createNamedQuery("UnitPropertyEntity.removeByFkUnitId");
        query.setParameter("fkUnitId", unitId);
        query.executeUpdate();
    }

    /**
     * 指定されたユニットテンプレートIDの関連付け情報を取得する
     *
     * @param parentUnitId ユニットテンプレートID
     * @param withDetails true:関連付けのある情報のIDと名前を付与する/false:IDだけを渡す
     * @return
     */
    private List<ConUnitAssociateEntity> getAssociate(Long parentUnitId, boolean withDetails) {
        TypedQuery<ConUnitAssociateEntity> query = em.createNamedQuery("ConUnitAssociateEntity.findByFkParentUnitId", ConUnitAssociateEntity.class);
        query.setParameter("fkParentUnitId", parentUnitId);
        List<ConUnitAssociateEntity> units = query.getResultList();
        if (withDetails) {
            for (ConUnitAssociateEntity unit : units) {
                if (Objects.nonNull(unit.getFkUnitId())) {
                    unit.setUnitName(super.find(unit.getFkUnitId()).getUnitName());
                } else if (Objects.nonNull(unit.getFkKanbanId())) {
                    List<UnitKanbanEntity> kanbans = this.getKanbans(Arrays.asList(unit.getFkKanbanId()));
                    if (!kanbans.isEmpty()) {
                        unit.setKanbanName(kanbans.get(0).getKanbanName());
                    }
                    //KanbanEntity kanban = EjbClient.getKanban(unit.getFkKanbanId());
                    // unit.setKanbanName(kanban.getKanbanName());
                    //unit.setKanbanName(kanbanEntityFacade.findWithDatails(unit.getFkKanbanId()).getKanbanName());
                }
            }
        }
        return units;
    }

    /**
     * ユニットの関連付けのみを取得する
     *
     * @param unitId ユニットID
     * @return ユニット関連付け(ユニットのみ)
     */
    public List<ConUnitAssociateEntity> getAssociatedUnit(Long unitId) {
        TypedQuery<ConUnitAssociateEntity> query = em.createNamedQuery("ConUnitAssociateEntity.findByFkParentUnitIdAndFkUnit", ConUnitAssociateEntity.class);
        query.setParameter("fkParentUnitId", unitId);
        query.setParameter("Limit", 0);
        return query.getResultList();
    }

    /**
     * カンバンの関連付けのみを取得する
     *
     * @param unitId ユニットID
     * @return ユニット関連付け(カンバンのみ)
     */
    public List<ConUnitAssociateEntity> getAssociatedKanban(Long unitId) {
        TypedQuery<ConUnitAssociateEntity> query = em.createNamedQuery("ConUnitAssociateEntity.findByFkParentUnitIdAndFkKanban", ConUnitAssociateEntity.class);
        query.setParameter("fkParentUnitId", unitId);
        query.setParameter("Limit", 0);
        return query.getResultList();
    }

    /**
     * ユニット関連情報の中身の生成処理
     *
     * @param entity 関連情報を詳細化する生産ユニット
     * @param template 生産ユニットのテンプレート元データ
     * @throws URISyntaxException
     */
    private Response addUnitAssociateDetail(UnitEntity entity, UnitTemplateEntity template) throws URISyntaxException, Exception {
        Response response = null;
        Date startDate = entity.getStartDatetime();
        Date endDate = entity.getCompDatetime();
        Date farstWorkTime = null;
        template.getConUnitTemplateAssociateCollection().sort(Comparator.comparing(kanban -> kanban.getUnitTemplateAssociateOrder()));
        for (ConUnitTemplateAssociateEntity conTemplate : template.getConUnitTemplateAssociateCollection()) {
            if (Objects.nonNull(conTemplate.getFkUnitTemplateId())) {
                // 関連がユニットテンプレートの場合、ユニット作成を行う
                UnitTemplateEntity tmp = getUnitTemplate(conTemplate.getFkUnitTemplateId());
                UnitEntity unit = new UnitEntity(null, entity.getUnitName() + "_" + tmp.getUnitTemplateName(),
                        tmp.getUnitTemplateId(), tmp.getWorkflowDiaglam());
                unit.setFkUpdatePersonId(entity.getFkUpdatePersonId());
                unit.setUpdateDatetime(entity.getUpdateDatetime());
                unit.setStartDatetime(startDate);
                // 第一工程の時間は親ユニットの開始時間に合わせる
                if (Objects.isNull(farstWorkTime)) {
                    unit.setStartDatetime(startDate);
                    unit.setCompDatetime(new Date(
                            startDate.getTime() + (conTemplate.getStandardEndTime().getTime() - conTemplate.getStandardStartTime().getTime())));
                    farstWorkTime = conTemplate.getStandardStartTime();
                } else {
                    // 第一工程目以降は最初の工程の時間と実施する工程の時間の差分をだし現在時刻にタス
                    unit.setStartDatetime(new Date(
                            startDate.getTime() + (conTemplate.getStandardStartTime().getTime() - farstWorkTime.getTime())));
                    unit.setCompDatetime(new Date(
                            unit.getStartDatetime().getTime() + (conTemplate.getStandardEndTime().getTime() - conTemplate.getStandardStartTime().getTime())));
                }
                response = add(unit, false);
                if (Objects.nonNull(response)) {
                    ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
                    logger.info("*****2 responseEntity={}", responseEntity);
                    if (!responseEntity.isSuccess()) {
                        return response;
                    }
                }
                ConUnitAssociateEntity con = new ConUnitAssociateEntity(entity.getUnitId(), unit.getUnitId(), null);
                con.setUnitAssociateOrder(conTemplate.getUnitTemplateAssociateOrder());
                if (endDate.before(unit.getCompDatetime())) {
                    endDate = unit.getCompDatetime();
                }
                em.persist(con);
            } else if (Objects.nonNull(conTemplate.getFkWorkflowId())) {
                // 関連がワークフローだった場合、カンバン作成を行う
                KanbanInfoEntity kanban = new KanbanInfoEntity();
                WorkflowInfoEntity workflow = getWorkflow(conTemplate.getFkWorkflowId());
                kanban.setParentId(template.getFkOutputKanbanHierarchyId());
                kanban.setKanbanName(entity.getUnitName() + "_" + workflow.getWorkflowName());
                kanban.setFkWorkflowId(workflow.getWorkflowId());
                kanban.setFkUpdatePersonId(entity.getFkUpdatePersonId());
                kanban.setUpdateDatetime(entity.getUpdateDatetime());

                // モデル名
                kanban.setModelName(workflow.getModelName());

                // 第一工程の時間は親ユニットの開始時間に合わせる
                if (Objects.isNull(farstWorkTime)) {
                    kanban.setStartDatetime(startDate);
                    kanban.setCompDatetime(new Date(
                            startDate.getTime() + (conTemplate.getStandardEndTime().getTime() - conTemplate.getStandardStartTime().getTime())));
                    farstWorkTime = conTemplate.getStandardStartTime();
                } else {
                    // 第一工程目以降は最初の工程の時間と実施する工程の時間の差分をだし現在時刻にタス
                    kanban.setStartDatetime(new Date(
                            startDate.getTime() + (conTemplate.getStandardStartTime().getTime() - farstWorkTime.getTime())));
                    kanban.setCompDatetime(new Date(
                            kanban.getStartDatetime().getTime() + (conTemplate.getStandardEndTime().getTime() - conTemplate.getStandardStartTime().getTime())));
                }
                ResponseEntity rs = kanbanEntityFacade.add(kanban);

                ConUnitAssociateEntity con = new ConUnitAssociateEntity(entity.getUnitId(), null, Long.parseLong(rs.getUri().substring(7)));
                con.setUnitAssociateOrder(conTemplate.getUnitTemplateAssociateOrder());
                if (endDate.before(kanban.getCompDatetime())) {
                    endDate = kanban.getCompDatetime();
                }
                em.persist(con);
            }
            entity.setCompDatetime(endDate);
        }
        return response;
    }

    /**
     * ユニット関連情報の中身の削除処理
     *
     * @param unitId 中身を削除するユニットID
     * @throws URISyntaxException
     * @throws Exception
     */
    private void removeUnitAssociateDetail(Long unitId) throws URISyntaxException, Exception {
        for (ConUnitAssociateEntity associatesByUnit : getAssociatedUnit(unitId)) {
            removeUnitAssociateDetail(associatesByUnit.getFkUnitId());
            // 階層
            removeHierarchy(associatesByUnit.getFkUnitId());
            // プロパティ
            removeProperty(associatesByUnit.getFkUnitId());
            // 削除
            super.remove(super.find(associatesByUnit.getFkUnitId()));
        }
        for (ConUnitAssociateEntity associatesByKanban : getAssociatedKanban(unitId)) {
            // カンバン削除
            kanbanEntityFacade.remove(associatesByKanban.getFkKanbanId());
        }
        removeUnitAssociateCollection(unitId);
    }

    /**
     * 生産ユニット関連付けテーブル削除
     *
     * @param entity 関連付けの削除を行うユニットテンプレート
     */
    private void removeUnitAssociateCollection(Long id) {
        //ユニットテンプレート関連付けを削除.
        Query query = em.createNamedQuery("ConUnitAssociateEntity.removeByFkParentUnitId");
        query.setParameter("fkParentUnitId", id);
        query.executeUpdate();
    }

    /**
     * カンバンが削除できるか確認
     *
     * @param unitId ユニットID
     * @return false:削除可/true:削除不可
     */
    private boolean removeCheckKanban(Long unitId) throws Exception {
        boolean isRemove = false;
        for (ConUnitAssociateEntity associatesByKanban : getAssociatedKanban(unitId)) {
            // 実績があるカンバンは削除できない
            ActualSearchCondition condition = new ActualSearchCondition().kanbanId(associatesByKanban.getFkKanbanId());
            Long count = actualResultEntityFacade.countSearch(condition);
            if (count > 0) {
                isRemove = true;
            }
        }
        for (ConUnitAssociateEntity associatesByUnit : getAssociatedUnit(unitId)) {
            if (!isRemove) {
                isRemove = removeCheckKanban(associatesByUnit.getFkUnitId());
            }
        }

        return isRemove;
    }

    /**
     * 生産ユニットの状態を取得する
     *
     * @param unitId
     * @return
     */
    @Lock(LockType.READ)
    @GET
    @Path("status/{id}")
    @Consumes({"application/xml", "application/json"})
    @Produces({"text/plain"})
    @ExecutionTimeLogging
    public String getUnitStatusName(@PathParam("id") Long unitId) {
        List<Long> kanbanIds = this.getKanbanIds(unitId);
        return this.getUnitStatus(kanbanIds).name();
    }

    /**
     * 生産ユニットの状態を取得する
     *
     * @param kanbanIds
     * @return
     */
    public KanbanStatusEnum getUnitStatus(List<Long> kanbanIds) {
        List<UnitKanbanEntity> kanbans = this.getKanbans(kanbanIds);

        if (kanbans.stream().filter(o -> o.getKanbanStatus() == KanbanStatusEnum.WORKING).count() > 0) {
            return KanbanStatusEnum.WORKING;
        }

        if (kanbans.stream().filter(o -> o.getKanbanStatus() == KanbanStatusEnum.SUSPEND).count() > 0) {
            return KanbanStatusEnum.SUSPEND;
        }

        long completed = kanbans.stream().filter(o -> o.getKanbanStatus() == KanbanStatusEnum.COMPLETION).count();
        if (kanbans.size() == completed) {
            return KanbanStatusEnum.COMPLETION;
        }
        if (kanbans.size() > 0) {
            return KanbanStatusEnum.WORKING;
        }

        return KanbanStatusEnum.PLANNED;
    }

    /**
     * カンバンを取得する
     *
     * @param kanbanIds
     * @return
     */
    @Lock(LockType.READ)
    @GET
    @Path("kanban")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<UnitKanbanEntity> getKanbans(@QueryParam("id") final List<Long> kanbanIds) {
        return PostgreAPI.getKanbans(this.em, kanbanIds);
    }

    /**
     * 生産ユニットの進捗時間を取得する
     *
     * @param kanbanIds
     * @return
     */
    public long getProgressTime(List<Long> kanbanIds) {
        try {
            logger.info("getProgressTime: {}", kanbanIds);

            long progressTime = 0L;
            Date date = new Date(0L);
            UnitWorkKanbanEntity lastWorkKanban = null;

            List<UnitWorkKanbanEntity> workKanbans = PostgreAPI.getWorkKanbans(em, kanbanIds);

            for (UnitWorkKanbanEntity workKanban : workKanbans) {
                switch (workKanban.getWorkStatus()) {
                    case WORKING:
                        if (date.before(workKanban.getActualStartTime())) {
                            lastWorkKanban = workKanban;
                        }
                        break;
                    case COMPLETION:
                        if (date.before(workKanban.getActualCompTime())) {
                            lastWorkKanban = workKanban;
                        }
                        break;
                    default:
                        break;
                }
            }

            if (Objects.nonNull(lastWorkKanban)) {
                if (lastWorkKanban.getWorkStatus() == KanbanStatusEnum.WORKING) {
                    progressTime = DateUtils.differenceOfDateTimeMillsec(lastWorkKanban.getStartDatetime(), lastWorkKanban.getActualStartTime());
                } else {
                    progressTime = DateUtils.differenceOfDateTimeMillsec(lastWorkKanban.getCompDatetime(), lastWorkKanban.getActualCompTime());
                }
            }

            return progressTime;
        } finally {
            logger.info("getProgressTime end.");
        }
    }

    /**
     * 生産ユニット内のすべてのカンバンIDを列挙する
     *
     * @param unitId
     * @return
     */
    public List<Long> getKanbanIds(Long unitId) {
        List<Long> kanbanIds = new ArrayList<>();
        for (ConUnitAssociateEntity associate : this.getAssociate(unitId, false)) {
            if (Objects.nonNull(associate.getFkUnitId())) {
                kanbanIds.addAll(this.getKanbanIds(associate.getFkUnitId()));
            } else {
                kanbanIds.add(associate.getFkKanbanId());
            }
        }
        return kanbanIds;
    }

    /**
     * 検索用クエリー生成処理
     *
     * @param type 検索タイプ COUNT:件数検索/SEARCH:情報検索
     * @param condition 検索条件
     * @return 検索用クエリー
     */
    private Query getSearchQuery(SearchType type, UnitSearchCondition condition) {
        StringBuilder sb = new StringBuilder();
        Class clz;
        if (type == SearchType.SEARCH) {
            sb.append("SELECT u FROM UnitEntity u");
            clz = UnitEntity.class;
        } else {
            sb.append("SELECT COUNT(u.unitId) FROM UnitEntity u");
            clz = String.class;
        }
        final String where = " WHERE";
        final String and = " AND";
        final String or = " OR";
        int cnt = 0;
        int loop;

        // 生産ユニット階層の検索条件追加
        //if (Objects.nonNull(condition.getHierarchyId())) {
        //    sb.append(cnt == 0 ? where : and);
        //    sb.append(" u.unitId IN (SELECT c.conUnitHierarchyEntityPK.fkUnitId FROM ConUnitHierarchyEntity c WHERE c.conUnitHierarchyEntityPK.fkUnitHierarchyId = :hierarchyId)");
        //    cnt++;
        //}
        // 生産ユニットIDの検索条件追加
        //if (Objects.nonNull(condition.getUnitId())) {
        //    sb.append(cnt == 0 ? where : and);
        //    sb.append(" u.unitId = :unitId");
        //    cnt++;
        //}
        // 生産ユニット名の検索条件追加
        if (Objects.nonNull(condition.getUnitName())) {
            sb.append(cnt == 0 ? where : and);
            sb.append(" (LOWER(u.unitName) LIKE :unitName1 OR u.unitName LIKE :unitName2)");
            cnt++;
        }
        // ユニットテンプレートの検索条件追加
        if (Objects.nonNull(condition.getUnittemplateIdCollection())) {
            sb.append(cnt == 0 ? where : and);
            sb.append(" (");
            loop = 0;
            for (Long unittemplateId : condition.getUnittemplateIdCollection()) {
                sb.append(loop == 0 ? "" : or);
                sb.append(" u.fkUnitTemplateId = :fkUnitTemplateId");
                sb.append(loop);
                loop++;
            }
            sb.append(")");
            cnt++;
        }
        // 生産ユニットのプロパティの検索条件 Todo:Lower & Like に変える？
        //if (Objects.nonNull(condition.getPropertyCollection())) {
        //    sb.append(cnt == 0 ? where : and);
        //    sb.append(" (");
        //    loop = 0;
        //    for (PropertyCondition property : condition.getPropertyCollection()) {
        //        sb.append(loop == 0 ? "" : or);
        //        sb.append(" u.unitId IN (SELECT p.fkUnitId FROM UnitPropertyEntity p WHERE LOWER(p.unitPropertyName) LIKE :unitPropertyName");
        //        sb.append(loop);
        //        sb.append(" AND LOWER(p.unitPropertyValue) LIKE :unitPropertyValue");
        //        sb.append(loop);
        //        sb.append(")");
        //        loop++;
        //    }
        //    sb.append(")");
        //    cnt++;
        //}
        if (Objects.nonNull(condition.getFromDate())) {
            sb.append(cnt == 0 ? where : and);
            sb.append(" (u.startDatetime >= :searchStartDate OR u.compDatetime >= :searchStartDate)");
            cnt++;
        }
        if (Objects.nonNull(condition.getToDate())) {
            sb.append(cnt == 0 ? where : and);
            sb.append(" (u.startDatetime <= :searchEndDate OR u.compDatetime <= :searchEndDate)");
            cnt++;
        }
        if (type == SearchType.SEARCH) {
            sb.append(" ORDER BY u.startDatetime");
        }

        //　クエリーの変数に検索条件のデータを埋め込み
        logger.info("unit search query:{}", sb.toString());
        Query query = em.createQuery(sb.toString(), clz);
        //if (Objects.nonNull(condition.getHierarchyId())) {
        //    query.setParameter("hierarchyId", condition.getHierarchyId());
        //}
        //if (Objects.nonNull(condition.getUnitId())) {
        //    query.setParameter("unitId", condition.getUnitId());
        //}
        if (Objects.nonNull(condition.getUnitName())) {
            query.setParameter("unitName1", "%" + condition.getUnitName().toLowerCase() + "%");
            query.setParameter("unitName2", "%" + condition.getUnitName() + "%");
        }
        if (Objects.nonNull(condition.getUnittemplateIdCollection())) {
            loop = 0;
            for (Long fkUnitTemplateId : condition.getUnittemplateIdCollection()) {
                query.setParameter("fkUnitTemplateId" + loop, fkUnitTemplateId);
                loop++;
            }
        }
        //if (Objects.nonNull(condition.getPropertyCollection())) {
        //    loop = 0;
        //    for (PropertyCondition property : condition.getPropertyCollection()) {
        //        query.setParameter("unitPropertyName" + loop, "%" + property.getKey().toLowerCase() + "%");
        //        query.setParameter("unitPropertyValue" + loop, "%" + property.getValue().toLowerCase() + "%");
        //        loop++;
        //    }
        //}
        if (Objects.nonNull(condition.getFromDate())) {
            query.setParameter("searchStartDate", condition.getFromDate(), TemporalType.TIMESTAMP);
        }
        if (Objects.nonNull(condition.getToDate())) {
            query.setParameter("searchEndDate", condition.getToDate(), TemporalType.TIMESTAMP);
        }
        return query;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public void setUnitTemplateEntityFacadeREST(UnitTemplateEntityFacadeREST unitTemplateEntityFacadeREST) {
        this.unitTemplateEntityFacadeREST = unitTemplateEntityFacadeREST;
    }

    public void setKanbanEntityFacade(KanbanEntityFacade kanbanEntityFacade) {
        this.kanbanEntityFacade = kanbanEntityFacade;
    }

    public void setWorkflowEntityFacade(WorkflowEntityFacade workflowEntityFacade) {
        this.workflowEntityFacade = workflowEntityFacade;
    }

    public void setActualResultEntityFacade(ActualResultEntityFacade actualResultEntityFacade) {
        this.actualResultEntityFacade = actualResultEntityFacade;
    }

    /**
     * ユニット追加時のチェック (名前の長さ)
     *
     * @param entity
     * @return
     * @throws Exception
     */
    public Response checkAdd(UnitEntity entity) throws Exception {
        logger.info("checkAdd:{}", entity);
        UnitTemplateEntity template = getUnitTemplate(entity.getFkUnitTemplateId());
        for (ConUnitTemplateAssociateEntity conTemplate : template.getConUnitTemplateAssociateCollection()) {
            if (Objects.nonNull(conTemplate.getFkUnitTemplateId())) {
                // 関連がユニットテンプレート
                UnitTemplateEntity tmp = getUnitTemplate(conTemplate.getFkUnitTemplateId());
                String unitName = entity.getUnitName() + "_" + tmp.getUnitTemplateName();
                if (unitName.length() > 256) {
                    // ユニット名が256文字を超える場合は「INVALID_ARGUMENT」(無効な引数です)を返す。
                    return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
                }
            } else if (Objects.nonNull(conTemplate.getFkWorkflowId())) {
                // 関連がワークフロー
                WorkflowInfoEntity workflow = getWorkflow(conTemplate.getFkWorkflowId());
                String kanbanName = entity.getUnitName() + "_" + workflow.getWorkflowName();
                if (kanbanName.length() > 256) {
                    // カンバン名が256文字を超える場合は「INVALID_ARGUMENT」(無効な引数です)を返す。
                    return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
                }
            }
        }

        if (Objects.nonNull(entity.getFkUnitTemplateId())) {
            // ユニットデータ
            Response response = checkAddUnitAssociateDetail(entity, template);
            if (Objects.nonNull(response)) {
                ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
                if (!responseEntity.isSuccess()) {
                    return response;
                }
            }
        }
        return null;
    }

    /**
     * ユニット関連情報の中身のチェック (名前の長さ)
     *
     * @param entity 関連情報を詳細化する生産ユニット
     * @param template 生産ユニットのテンプレート元データ
     * @throws Exception
     */
    private Response checkAddUnitAssociateDetail(UnitEntity entity, UnitTemplateEntity template) throws Exception {
        Response response = null;
        template.getConUnitTemplateAssociateCollection().sort(Comparator.comparing(kanban -> kanban.getUnitTemplateAssociateOrder()));
        for (ConUnitTemplateAssociateEntity conTemplate : template.getConUnitTemplateAssociateCollection()) {
            if (Objects.nonNull(conTemplate.getFkUnitTemplateId())) {
                // 関連がユニットテンプレートの場合
                UnitTemplateEntity tmp = getUnitTemplate(conTemplate.getFkUnitTemplateId());
                UnitEntity unit = new UnitEntity(null, entity.getUnitName() + "_" + tmp.getUnitTemplateName(),
                        tmp.getUnitTemplateId(), tmp.getWorkflowDiaglam());
                response = checkAdd(unit);
                if (Objects.nonNull(response)) {
                    ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
                    if (!responseEntity.isSuccess()) {
                        return response;
                    }
                }
            }
        }
        return response;
    }
}
