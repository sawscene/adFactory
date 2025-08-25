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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryforfujiserver.entity.unit.UnitKanbanHierarchyEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unit.UnitWorkflowEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate.ConUnitTemplateAssociateEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate.ConUnitTemplateHierarchyEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate.TreeUnitTemplateHierarchyEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate.UnitTemplateEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate.UnitTemplatePropertyEntity;
import jp.adtekfuji.adfactoryforfujiserver.service.standard.KanbanHierarchyEntityFacade;
import jp.adtekfuji.adfactoryforfujiserver.service.standard.OrganizationEntityFacade;
import jp.adtekfuji.adfactoryforfujiserver.service.standard.WorkflowEntityFacade;
import jp.adtekfuji.adfactoryforfujiserver.utility.ExecutionTimeLogging;
import jp.adtekfuji.forfujiapp.entity.accessfuji.AccessHierarchyFujiTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ユニット情報REST
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.13.Thr
 */
@Singleton
@Path("unittemplate")
public class UnitTemplateEntityFacadeREST extends AbstractFacade<UnitTemplateEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryForFujiServer_war_1.0PU")
    private EntityManager em;
    private final Logger logger = LogManager.getLogger();
    @EJB
    private WorkflowEntityFacade workflowEntityFacade;
    @EJB
    private KanbanHierarchyEntityFacade kanbanHierarchyEntityFacade;
    @EJB
    private OrganizationEntityFacade organizationFacade;
    
    private final static String UNIT_TEMPLATE_URI = "unittemplate/";

    public UnitTemplateEntityFacadeREST() {
        super(UnitTemplateEntity.class);
    }

    /**
     * 指定されたIDのプロパティ情報を追加
     *
     * @param id ユニットテンプレートID
     * @return ユニットテンプレート情報
     */
    @Lock(LockType.READ)
    public UnitTemplateEntity findWithoutDatails(Long id) {
        UnitTemplateEntity entity = super.find(id);
        if (Objects.isNull(entity)) {
            return new UnitTemplateEntity();
        }
        ConUnitTemplateHierarchyEntity hierarchy = findParent(id);
        if (Objects.nonNull(hierarchy)) {
            entity.setParentId(hierarchy.getConUnitTemplateHierarchyEntityPK().getFkUnitTemplateHierarchyId());
        }
        entity.setUnitTemplatePropertyCollection(getPropertyEntity(id));
        return entity;
    }

    /**
     * 指定されたIDのユニットテンプレートを取得
     *
     * @param id ユニットテンプレートID
     * @return ユニットテンプレート情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public UnitTemplateEntity findWithDatails(@PathParam("id") Long id) {
        logger.info("findWithDatails:{}", id);
        UnitTemplateEntity entity = super.find(id);
        if (Objects.isNull(entity)) {
            return new UnitTemplateEntity();
        }

        if (Objects.nonNull(entity.getFkOutputKanbanHierarchyId()) && entity.getFkOutputKanbanHierarchyId() != 0L) {
            List<UnitKanbanHierarchyEntity> kanbanHierarchy = PostgreAPI.getKanbanHierarchies(em, Arrays.asList(entity.getFkOutputKanbanHierarchyId()));
            if (!kanbanHierarchy.isEmpty()) {
                entity.setOutputKanbanHierarchyName(kanbanHierarchy.get(0).getHierarchyName());
            }
        }

        ConUnitTemplateHierarchyEntity hierarchy = findParent(id);
        if (Objects.nonNull(hierarchy)) {
            entity.setParentId(hierarchy.getConUnitTemplateHierarchyEntityPK().getFkUnitTemplateHierarchyId());
        }

        entity.setUnitTemplatePropertyCollection(getPropertyEntity(id));
        entity.setConUnitTemplateAssociateCollection(this.getUnitTemplateAssociateEntityCollection(id, true));
        return entity;
    }

    /**
     * 指定されたIDのプロパティ情報を追加
     *
     * @param unitTemplateId
     * @return
     */
    @Lock(LockType.READ)
    public UnitTemplateEntity find(Long unitTemplateId) {
        UnitTemplateEntity entity = super.find(unitTemplateId);
        if (Objects.isNull(entity)) {
            return new UnitTemplateEntity();
        }
        return entity;
    }

    /**
     * 生産ユニットテンプレートを取得する
     *
     * @param unitTemplateIds
     * @return
     */
    @Lock(LockType.READ)
    @GET
    @Path("find")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<UnitTemplateEntity> find(@QueryParam("id") List<Long> unitTemplateIds) {
        logger.info("find:{}", unitTemplateIds);

        if (Objects.isNull(unitTemplateIds) || unitTemplateIds.isEmpty()) {
            throw new IllegalArgumentException("Invalid arguments.");
        }

        TypedQuery<UnitTemplateEntity> query = em.createNamedQuery("UnitTemplateEntity.findByUnitTemplateIds", UnitTemplateEntity.class);
        query.setParameter("unitTemplateIds", unitTemplateIds);
        return query.getResultList();
    }

    /**
     * 生産ユニットテンプレートを取得する
     *
     * @param name
     * @param userId
     * @return
     */
    @Lock(LockType.READ)
    @GET
    @Path("name")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public UnitTemplateEntity findName(@QueryParam("name") String name, @QueryParam("user") Long userId) {
        logger.info("findName:{},{}", name, userId);
        UnitTemplateEntity result;
        TypedQuery<UnitTemplateEntity> queryExist = em.createNamedQuery("UnitTemplateEntity.findByUnitTemplateName", UnitTemplateEntity.class);
        queryExist.setParameter("unitTemplateName", name);
        try {
            result = queryExist.getSingleResult();
        } catch (NoResultException ex) {
            logger.fatal(ex);
            return new UnitTemplateEntity();
        }
        ConUnitTemplateHierarchyEntity hierarchy = this.findParent(result.getUnitTemplateId());
        if (Objects.nonNull(hierarchy)) {
            result.setParentId(hierarchy.getConUnitTemplateHierarchyEntityPK().getFkUnitTemplateHierarchyId());
        }
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT DISTINCT(u) FROM UnitTemplateHierarchyEntity u");
        if(Objects.nonNull(userId)) {
            sb.append(" LEFT JOIN AccessHierarchyFujiEntity a ON a.typeId = :type AND u.unitTemplateHierarchyId = a.fkHierarchyId");
            sb.append(" WHERE u.unitTemplateHierarchyId = :unitTemplateHierarchyId");
            sb.append(" AND (a.fkOrganizationId IS NULL OR a.fkOrganizationId IN :ancestors)");
        } else {
            sb.append(" WHERE u.unitTemplateHierarchyId = :unitTemplateHierarchyId");
        }
        Query query = em.createQuery(sb.toString());
        if(Objects.nonNull(userId)) {
            query.setParameter("type", AccessHierarchyFujiTypeEnum.UnitTemplateHierarchy);
            query.setParameter("ancestors", organizationFacade.findAncesors(userId));
        }
        //親階層からアクセス権判定
        for(Long id : this.findAncestors(result.getParentId())) {
            query.setParameter("unitTemplateHierarchyId", id);
            try {
                query.getSingleResult();
            } catch (NoResultException ex) {
                logger.fatal(ex);
                return new UnitTemplateEntity();
            }
        }
        try {
            if (Objects.isNull(result.getFkOutputKanbanHierarchyId())) {
                KanbanHierarchyInfoEntity kanbanhierarchy = this.kanbanHierarchyEntityFacade.find(result.getFkOutputKanbanHierarchyId());
                if (Objects.nonNull(kanbanhierarchy) && Objects.nonNull(kanbanhierarchy.getHierarchyName())) {
                    result.setOutputKanbanHierarchyName(kanbanhierarchy.getHierarchyName());
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        result.setUnitTemplatePropertyCollection(this.getPropertyEntity(result.getUnitTemplateId()));
        result.setConUnitTemplateAssociateCollection(this.getUnitTemplateAssociateEntityCollection(result.getUnitTemplateId(), true));

        return result;
    }

    /**
     * ユニットテンプレート追加
     *
     * @param entity 追加すユニットテンプレート情報
     * @return 作成されたユニットテンプレートのパスを返す
     * @throws URISyntaxException
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(UnitTemplateEntity entity) throws URISyntaxException {
        logger.info("add:{}", entity);
        // 重複確認
        TypedQuery<ConUnitTemplateHierarchyEntity> query = em.createNamedQuery("ConUnitTemplateHierarchyEntity.findByFkUnitTemplateHierarchyId", ConUnitTemplateHierarchyEntity.class);
        query.setParameter("fkUnitTemplateHierarchyId", entity.getParentId());
        List<ConUnitTemplateHierarchyEntity> conHierarchys = query.getResultList();
        for (ConUnitTemplateHierarchyEntity conHierarchy : conHierarchys) {
            UnitTemplateEntity checkTarget = super.find(conHierarchy.getConUnitTemplateHierarchyEntityPK().getFkUnitTemplateId());
            if (checkTarget.getUnitTemplateName().equals(entity.getUnitTemplateName())) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }
        }
        // 作成.
        super.create(entity);
        em.flush();
        // 階層関連付け
        addHierarchy(entity);
        // プロパティ.
        addProperty(entity);
        // 関連付け.
        addUnitTemplateAssociateCollection(entity);
        URI uri = new URI(UNIT_TEMPLATE_URI + entity.getUnitTemplateId().toString());
        return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
    }

    /**
     * ユニットテンプレート更新
     *
     * @param entity 更新するユニットテンプレート
     * @return OK:成功/IDENTNAME_OVERLAP:識別子重複で追加
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(UnitTemplateEntity entity) {
        logger.info("update:{}", entity);
        // 重複確認
        TypedQuery<ConUnitTemplateHierarchyEntity> query = em.createNamedQuery("ConUnitTemplateHierarchyEntity.findByFkUnitTemplateHierarchyId", ConUnitTemplateHierarchyEntity.class);
        query.setParameter("fkUnitTemplateHierarchyId", entity.getParentId());
        List<ConUnitTemplateHierarchyEntity> conHierarchys = query.getResultList();
        for (ConUnitTemplateHierarchyEntity conHierarchy : conHierarchys) {
            UnitTemplateEntity checkTarget = super.find(conHierarchy.getConUnitTemplateHierarchyEntityPK().getFkUnitTemplateId());
            if (checkTarget.getUnitTemplateName().equals(entity.getUnitTemplateName())
                    && !checkTarget.getUnitTemplateId().equals(entity.getUnitTemplateId())) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }
        }
        // 編集
        super.edit(entity);
        // 階層関連付け
        removeHierarchy(entity.getUnitTemplateId());
        addHierarchy(entity);
        // プロパティ
        removeProperty(entity.getUnitTemplateId());
        addProperty(entity);
        // 関連付け
        removeUnitTemplateAssociateCollection(entity.getUnitTemplateId());
        addUnitTemplateAssociateCollection(entity);
        return Response.ok().entity(ResponseEntity.success()).build();
    }

    /**
     * ユニットテンプレート削除
     *
     * @param id 削除するユニットテンプレートのID
     * @return OK:成功/IDENTNAME_OVERLAP:識別子重複で追加
     */
    @DELETE
    @Path("{id}")
    @ExecutionTimeLogging
    public Response remove(@PathParam("id") Long id) {
        logger.info("remove:{}", id);
        // 関連付けが無い場合は完全削除
        TypedQuery<Long> query1 = em.createNamedQuery("UnitTemplateEntity.countUnitAssociation", Long.class);
        query1.setParameter("fkUnitTemplateId", id);
        Long num1 = query1.getSingleResult();
        if (num1 == 0) {
            logger.info("remove-real:{}", id);
            // 階層関連付け
            removeHierarchy(id);
            // プロパティ
            removeProperty(id);
            // 関連付け
            removeUnitTemplateAssociateCollection(id);
            // 削除
            super.remove(super.find(id));
            return Response.ok().entity(ResponseEntity.success()).build();
        }
        // 論理削除
        UnitTemplateEntity entity = findWithDatails(id);
        if (Objects.isNull(entity.getUnitTemplateId())) {
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
        // 名前を変える(削除済みにする)
        boolean isFind = true;
        int num = 1;
        String original = entity.getUnitTemplateName() + SUFFIX_REMOVE;
        String name = original + num;
        while (isFind) {
            if (Objects.nonNull(findName(name, null).getUnitTemplateName())) {
                num = num + 1;
                name = original + num;
                continue;
            }
            isFind = false;
        }
        logger.info("remove-logic:{},{}", id, name);
        entity.setUnitTemplateName(name);
        entity.setRemoveFlag(true);
        super.edit(entity);
        // 階層
        removeHierarchy(id);
        return Response.ok().entity(ResponseEntity.success()).build();
    }

    /**
     * ユニットテンプレート複製
     *
     * @param id 複製するユニットテンプレートのID
     * @return 複製したユニットテンプレートのパス
     * @throws URISyntaxException 不正なURIが生成された場合のエラー
     */
    @POST
    @Path("copy/{id}")
    @Consumes({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response copy(@PathParam("id") Long id) throws URISyntaxException {
        logger.info("copy:{}", id);
        UnitTemplateEntity entity = findWithDatails(id);
        if (Objects.isNull(entity)) {
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_UPDATE)).build();
        }
        boolean isFind = true;
        String name = entity.getUnitTemplateName() + SUFFIX_COPY;
        while (isFind) {
            if (Objects.nonNull(findName(name, null).getUnitTemplateId())) {
                name += SUFFIX_COPY;
                continue;
            }
            isFind = false;
        }
        UnitTemplateEntity newEntity = new UnitTemplateEntity(entity);
        newEntity.setUnitTemplateName(name);
        add(newEntity);
        URI uri = new URI(UNIT_TEMPLATE_URI + newEntity.getUnitTemplateId().toString());
        return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
    }

    /**
     * 指定されたIDに該当するプロパティをDBから取得する
     *
     * @param id ユニットテンプレートID
     * @return プロパティ情報のリスト
     */
    private List<UnitTemplatePropertyEntity> getPropertyEntity(Long id) {
        TypedQuery<UnitTemplatePropertyEntity> query = em.createNamedQuery("UnitTemplatePropertyEntity.findByFkUnitTemplateId", UnitTemplatePropertyEntity.class);
        query.setParameter("fkUnitTemplateId", id);
        return query.getResultList();
    }

    /**
     * ユニットテンプレートのプロパティ情報を追加
     *
     * @param entity プロパティを追加するユニットテンプレート
     */
    private void addProperty(UnitTemplateEntity entity) {
        if (Objects.nonNull(entity.getUnitTemplatePropertyCollection())) {
            for (UnitTemplatePropertyEntity property : entity.getUnitTemplatePropertyCollection()) {
                property.setFkUnitTemplateId(entity.getUnitTemplateId());
                em.persist(property);
            }
        }
    }

    /**
     * ユニットテンプレートのプロパティ情報を削除
     *
     * @param entity プロパティを削除するユニットテンプレート
     */
    private void removeProperty(Long id) {
        Query query = em.createNamedQuery("UnitTemplatePropertyEntity.removeByFkUnitTemplateId");
        query.setParameter("fkUnitTemplateId", id);
        query.executeUpdate();
    }

    /**
     * 指定されたユニットテンプレートIDの所属する階層情報を取得
     *
     * @param id ユニットテンプレートID
     * @return 階層関連付け情報
     */
    private ConUnitTemplateHierarchyEntity findParent(Long id) {
        TypedQuery<ConUnitTemplateHierarchyEntity> query = em.createNamedQuery("ConUnitTemplateHierarchyEntity.findByFkUnitTemplateId", ConUnitTemplateHierarchyEntity.class);
        query.setParameter("fkUnitTemplateId", id);
        query.setMaxResults(1);
        ConUnitTemplateHierarchyEntity hierarchy = null;
        try {
            hierarchy = query.getSingleResult();
        } catch (NoResultException ex) {
            //親なしってこと.
        }
        return hierarchy;
    }

    /**
     * ユニットテンプレートの階層関連付け追加
     *
     * @param entity 階層関連付けを行うユニットテンプレート
     */
    private void addHierarchy(UnitTemplateEntity entity) {
        ConUnitTemplateHierarchyEntity hierarchy = new ConUnitTemplateHierarchyEntity(entity.getParentId(), entity.getUnitTemplateId());
        em.persist(hierarchy);
    }

    /**
     * ユニットテンプレートの階層関連付け削除
     *
     * @param entity 階層関連付けの削除を行うユニットテンプレート
     */
    private void removeHierarchy(Long id) {
        Query query = em.createNamedQuery("ConUnitTemplateHierarchyEntity.removeByFkUnitTemplateId");
        query.setParameter("fkUnitTemplateId", id);
        query.executeUpdate();
    }

    /**
     * 指定されたユニットテンプレートIDの関連付け情報を取得する
     *
     * @param id ユニットテンプレートID
     * @param needDetail true:関連付けのある情報のIDと名前を付与する/false:IDだけを渡す
     * @return
     */
    private List<ConUnitTemplateAssociateEntity> getUnitTemplateAssociateEntityCollection(Long id, boolean needDetail) {
        TypedQuery<ConUnitTemplateAssociateEntity> query = em.createNamedQuery("ConUnitTemplateAssociateEntity.findByFkParentUnitTemplateId", ConUnitTemplateAssociateEntity.class);
        query.setParameter("fkParentUnitTemplateId", id);
        List<ConUnitTemplateAssociateEntity> entities = query.getResultList();

        if (needDetail) {
            Set<Long> workflowIds = new HashSet<Long>();
            for (ConUnitTemplateAssociateEntity entity : entities) {
                if (Objects.nonNull(entity.getFkWorkflowId())) {
                    workflowIds.add(entity.getFkWorkflowId());
                }
            }

            // 工程順を取得
            List<UnitWorkflowEntity> workflows = null;
            if (!workflowIds.isEmpty()) {
                workflows = PostgreAPI.getWorkflows(this.em, new ArrayList<>(workflowIds));
            }

            for (ConUnitTemplateAssociateEntity entity : entities) {
                if (Objects.nonNull(entity.getFkUnitTemplateId())) {
                    entity.setUnitTemplateName(super.find(entity.getFkUnitTemplateId()).getUnitTemplateName());
                } else if (Objects.nonNull(entity.getFkWorkflowId())) {
                    Optional<UnitWorkflowEntity> optional = workflows.stream().filter(o -> Objects.equals(o.getWorkflowId(), entity.getFkWorkflowId())).findFirst();
                    if (optional.isPresent()) {
                        entity.setWorkflowName(optional.get().getWorkflowName());
                    }
                }
            }
        }
        return entities;
    }

    /**
     * ユニットテンプレート関連付けテーブル追加
     *
     * @param entity 関連付けをするユニットテンプレート
     */
    private void addUnitTemplateAssociateCollection(UnitTemplateEntity entity) {
        //ユニットテンプレート関連付けを登録.
        if (Objects.nonNull(entity.getConUnitTemplateAssociateCollection())) {
            for (ConUnitTemplateAssociateEntity work : entity.getConUnitTemplateAssociateCollection()) {
                work.setFkParentUnitTemplateId(entity.getUnitTemplateId());
                em.persist(work);
            }
        }
    }

    /**
     * ユニットテンプレート関連付けテーブル削除
     *
     * @param entity 関連付けの削除を行うユニットテンプレート
     */
    private void removeUnitTemplateAssociateCollection(Long id) {
        //ユニットテンプレート関連付けを削除.
        Query query = em.createNamedQuery("ConUnitTemplateAssociateEntity.removeByfkParentUnitTemplateId");
        query.setParameter("fkParentUnitTemplateId", id);
        query.executeUpdate();
    }

    private List<Long> findAncestors(Long unitTemplateHierarchyId) {
        TypedQuery<TreeUnitTemplateHierarchyEntity> query = em.createNamedQuery("TreeUnitTemplateHierarchyEntity.findByChildId", TreeUnitTemplateHierarchyEntity.class);
        List<Long> parentIdsOfLoginUser = new ArrayList<>();
        if(Objects.nonNull(unitTemplateHierarchyId)) {
            Long childId = unitTemplateHierarchyId;
            while(childId != 0L) {
                query.setParameter("childId", childId);
                TreeUnitTemplateHierarchyEntity parent = query.getSingleResult();
                parentIdsOfLoginUser.add(childId);
                childId = parent.getTreeUnitTemplateHierarchyEntityPK().getParentId();
            }
        }
        return parentIdsOfLoginUser;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public void setWorkflowEntityFacade(WorkflowEntityFacade workflowEntityFacade) {
        this.workflowEntityFacade = workflowEntityFacade;
    }
}
