/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import jakarta.ejb.EJB;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.enumerate.AccessHierarchyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanHierarchyEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.TreeKanbanHierarchyEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * カンバン階層情報REST
 *
 * @author ke.yokoi
 */
@Singleton
@Path("kanban/tree")
public class KanbanHierarchyEntityFacadeREST extends AbstractFacade<KanbanHierarchyEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    @EJB
    private KanbanEntityFacadeREST kanbanFacade;

    @EJB
    private WorkflowEntityFacadeREST workflowFacade;

    @EJB
    private OrganizationEntityFacadeREST organizationFacade;

    @EJB
    private AccessHierarchyEntityFacadeREST authRest;

    private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     */
    public KanbanHierarchyEntityFacadeREST() {
        super(KanbanHierarchyEntity.class);
    }

    /**
     * カンバン階層IDを指定して、カンバン階層情報を取得する。(基本情報のみ)
     *
     * @param id カンバン階層ID
     * @return カンバン階層情報
     */
    @Lock(LockType.READ)
    public KanbanHierarchyEntity findBasicInfo(Long id) {
        return super.find(id);
    }
	
    /**
     * 指定した階層の子階層情報一覧を取得する。
     * ※．ユーザーIDが指定されている場合、アクセス可能な階層情報を対象とする。
     *
     * @param id カンバン階層ID
     * @param userId ユーザーID (組織ID)
     * @param from 範囲の先頭
     * @param to 範囲の先頭
     * @param hasChild 子階層保持フラグ
     * @param authId 認証ID
     * @return カンバン階層マスタ情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<KanbanHierarchyEntity> findTreeRange(@QueryParam("id") Long id, @QueryParam("user") Long userId, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("hasChild") Boolean hasChild, @QueryParam("authId") Long authId) {
        logger.info("findTreeRange: id={}, userId={}, from={}, to={}, hasChild={}, authId={}", id, userId, from, to, hasChild, authId);

        // 階層IDの指定がない場合はルートとする。
        if (Objects.isNull(id)) {
            id = 0L;
        }

        // admin の場合、すべての階層にアクセスできるようにする
        Long _userId = userId;
        OrganizationEntity admin = this.organizationFacade.findByName(ADMIN_USER, null, null);
        if (Objects.equals(userId, admin.getOrganizationId())) {
            _userId = null;
        }

        // 指定した階層の子階層情報一覧を取得する。
        List<KanbanHierarchyEntity> entities = this.findChild(id, _userId, from, to);

        if (Objects.isNull(hasChild) || hasChild) {
            for (KanbanHierarchyEntity entity : entities) {
                entity.setParentId(id);
                entity.setChildCount(this.countChild(entity.getKanbanHierarchyId(), _userId));

                // 階層に属するカンバン情報一覧を取得してセットする。
                List<KanbanEntity> kanbans = this.findKanban(entity.getKanbanHierarchyId());
                entity.setKanbanCollection(kanbans);
            }
        } else {
            for (KanbanHierarchyEntity entity : entities) {
                entity.setParentId(id);
                entity.setChildCount(this.countChild(entity.getKanbanHierarchyId(), _userId));
            }
        }

        return entities;
    }

    /**
     * 指定した階層名の階層情報を取得する。(階層のみでカンバン一覧は取得しない)
     *
     * @param name 階層名
     * @param userId ユーザーID (組織ID)
     * @param authId 認証ID
     * @return カンバン階層
     */
    @Lock(LockType.READ)
    @GET
    @Path("hierarchy/name")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public KanbanHierarchyEntity findHierarchyByName(@QueryParam("name") String name, @QueryParam("user") Long userId, @QueryParam("authId") Long authId) {
        logger.info("findHierarchyByName: name={}, userId={}, authId={}", name, userId, authId);
        KanbanHierarchyEntity hierarchy;

        // 階層名を指定して、階層情報を取得する。
        TypedQuery<KanbanHierarchyEntity> queryExist = this.em.createNamedQuery("KanbanHierarchyEntity.findByHierarchyName", KanbanHierarchyEntity.class);
        queryExist.setParameter("hierarchyName", name);
        try {
            hierarchy = queryExist.getSingleResult();
        } catch (NoResultException ex) {
            logger.fatal(ex);
            return new KanbanHierarchyEntity();
        }

        // 対象階層からルートまでの階層アクセス権をチェックする。
        boolean isAccessible = this.isHierarchyAccessible(hierarchy.getKanbanHierarchyId(), userId);
        if (!isAccessible) {
            return new KanbanHierarchyEntity();
        }

        hierarchy.setParentId(this.findParentId(hierarchy.getKanbanHierarchyId()));
        hierarchy.setChildCount(this.countChild(hierarchy.getKanbanHierarchyId(), userId));
        return hierarchy;
    }

    /**
     * カンバン階層IDで階層情報を取得する。
     * @param ids カンバンID
     * @param userId ユーザーID (組織ID)
     * @param authId 認証ID
     * @return カンバン階層
     */
    @Lock(LockType.READ)
    @GET
    @Path("hierarchy/id")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<KanbanHierarchyEntity> findHierarchyById(@QueryParam("id") List<Long> ids, @QueryParam("user") Long userId, @QueryParam("authId") Long authId) {
        logger.info("findHierarchyById: id={}, userId={}, authId={}", ids, userId, authId);
        if(ids.isEmpty()) {
            return new ArrayList<>();
        }

        TypedQuery<KanbanHierarchyEntity> queryExist = this.em.createNamedQuery("KanbanHierarchyEntity.findByHierarchyId", KanbanHierarchyEntity.class);
        queryExist.setParameter("hierarchId", ids);
        List<KanbanHierarchyEntity> ret = queryExist
                .getResultList()
                .stream()
                .filter(entity -> this.isHierarchyAccessible(entity.getKanbanHierarchyId(), userId))
                .collect(Collectors.toList());

        ret.forEach(entity-> {
                    entity.setParentId(this.findParentId(entity.getKanbanHierarchyId()));
                    entity.setChildCount(this.countChild(entity.getKanbanHierarchyId(), userId));
                });

        return ret;
    }


    /**
     * カンバン階層情報の件数を取得する
     *
     * @param id 階層ID
     * @param userId ユーザーID (組織ID)
     * @param authId 認証ID
     * @return カンバン階層情報の件数
     */
    @Lock(LockType.READ)
    @GET
    @Path("count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String countTree(@QueryParam("id") Long id, @QueryParam("user") Long userId, @QueryParam("authId") Long authId) {
        logger.info("countTree: id={}, userId={}, authId={}", id, userId, authId);

        // 階層IDの指定がない場合はルートとする。
        if (Objects.isNull(id)) {
            id = 0L;
        }

        // admin の場合、すべての階層にアクセスできるようにする
        Long _userId = userId;
        OrganizationEntity admin = this.organizationFacade.findByName(ADMIN_USER, null, null);
        if (Objects.equals(userId, admin.getOrganizationId())) {
            _userId = null;
        }

        // 指定した階層の子階層の件数を取得する。
        Long count = this.countChild(id, _userId);
        return String.valueOf(count);
    }

    /**
     * カンバンIDを指定し、カンバン階層マスタ情報を取得する
     *
     * @param id カンバンID
     * @param authId 認証ID
     * @return カンバン階層情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public KanbanHierarchyEntity find(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("find: id={}, authId={}", id, authId);

        KanbanHierarchyEntity entity = super.find(id);
        if (Objects.isNull(entity)) {
            return null;
        }

        // 親階層IDを取得する。
        Long parentId = this.findParentId(id);
        if (Objects.nonNull(parentId)) {
            entity.setParentId(parentId);
        }

        // 階層に属するカンバン情報一覧を取得する。
        List<KanbanEntity> kanbans = this.findKanban(id);
        entity.setKanbanCollection(kanbans);

        return entity;
    }

    /**
     * カンバン階層マスタ情報を登録する。
     *
     * @param entity カンバン階層マスタ情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(KanbanHierarchyEntity entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("add: {}, authId={}", entity, authId);
        try {
            // 階層名の重複を確認する。
            TypedQuery<Long> query = this.em.createNamedQuery("KanbanHierarchyEntity.checkAddByHierarchyName", Long.class);
            query.setParameter("hierarchyName", entity.getHierarchyName());
            if (query.getSingleResult() > 0) {
                // 該当するものがあった場合、重複を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }

            // 階層情報を登録する。
            super.create(entity);
            this.em.flush();

            // 階層情報(親子関係)を登録する。
            this.addHierarchy(entity);

            // 作成した情報を元に、戻り値のURIを作成する。
            URI uri = new URI(new StringBuilder("kanban/tree/").append(entity.getKanbanHierarchyId()).toString());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * カンバン階層マスタ情報を更新する。
     *
     * @param entity カンバン階層マスタ情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(KanbanHierarchyEntity entity, @QueryParam("authId") Long authId) {
        logger.info("update: {}, authId={}", entity, authId);
        try {
            if (Objects.nonNull(entity.getKanbanHierarchyId())
                    && Objects.nonNull(entity.getParentId())
                    && !isRegistableHierarchy(entity.getKanbanHierarchyId(), entity.getParentId())) {
                logger.fatal("not Register Kanban Hierarchy");
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.UNMOVABLE_HIERARCHY)).build();
            }

            // 階層名の重複を確認する。
            TypedQuery<Long> query = this.em.createNamedQuery("KanbanHierarchyEntity.checkUpdateByHierarchyName", Long.class);
            query.setParameter("hierarchyName", entity.getHierarchyName());
            query.setParameter("kanbanHierarchyId", entity.getKanbanHierarchyId());
            if (query.getSingleResult() > 0) {
                // 該当するものがあった場合、重複を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }

            // 階層情報を更新する。
            super.edit(entity);
            this.em.flush();

            // 階層情報(親子関係)を更新する。
            this.registHierarchy(entity);

            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * カンバン階層情報IDを指定し、カンバン階層マスタ情報を一件削除する
     *
     * @param id カンバン階層ID
     * @param authId 認証ID
     * @return DBアクセス結果
     */
    @DELETE
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response remove(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("remove: id={}, authId={}", id, authId);
        try {
            // 指定された階層に、子階層がある場合は削除できない。
            TypedQuery<Long> query1 = this.em.createNamedQuery("TreeKanbanHierarchyEntity.countChild", Long.class);
            query1.setParameter("parentId", id);
            Long num1 = query1.getSingleResult();
            if (num1 > 0) {
                logger.info("not remove at exist child hierarchy:{}", id);
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.EXIST_HIERARCHY_DELETE)).build();
            }

            // 指定された階層に、カンバンがある場合は削除できない。
            TypedQuery<Long> query2 = this.em.createNamedQuery("ConKanbanHierarchyEntity.countChild", Long.class);
            query2.setParameter("kanbanHierarchyId", id);
            Long num2 = query2.getSingleResult();
            if (num2 > 0) {
                logger.info("not remove at exist child hierarchy:{}", id);
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.EXIST_CHILD_DELETE)).build();
            }

            // 階層アクセス権情報を削除する。
            this.authRest.remove(AccessHierarchyTypeEnum.KanbanHierarchy, id);

            // 階層情報(親子関係)を削除する。
            this.removeHierarchy(id);

            // 階層情報を削除する。
            super.remove(super.find(id));
            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 階層IDを指定して、階層に属するカンバン情報一覧を取得する。
     *
     * @param hierarchyId 階層ID
     * @return カンバン情報一覧
     */
    @Lock(LockType.READ)
    private List<KanbanEntity> findKanban(Long hierarchyId) {
        TypedQuery<KanbanEntity> query = this.em.createNamedQuery("KanbanEntity.findByKanbanHierarchyId", KanbanEntity.class);
        query.setParameter("hierarchyId", hierarchyId);
        List<KanbanEntity> kanbans = query.getResultList();
        for (KanbanEntity kanban : kanbans) {
            // 工程順情報を取得する。
            WorkflowEntity workflow = this.workflowFacade.findBasicInfo(kanban.getWorkflowId());
            kanban.setWorkflowName(workflow.getWorkflowName());
            kanban.setWorkflowRev(workflow.getWorkflowRev());
            kanban.setParentId(hierarchyId);
        }
        return kanbans;
    }

    /**
     * 指定した階層の子階層情報一覧を取得する。
     * ※．ユーザーIDが指定されている場合、アクセス可能な階層情報を対象とする。
     *
     * @param id 階層ID
     * @param userId ユーザーID (組織ID)
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @return 子階層情報一覧
     */
    @Lock(LockType.READ)
    private List<KanbanHierarchyEntity> findChild(Long id, Long userId, Integer from, Integer to) {
        TypedQuery<KanbanHierarchyEntity> query;

        if(Objects.isNull(userId)) {
            // 指定した階層IDの子階層情報一覧を取得する。
            query = this.em.createNamedQuery("KanbanHierarchyEntity.findChild", KanbanHierarchyEntity.class);
        } else {
            // ユーザーIDが指定されている場合、アクセス可能な階層情報を対象とする。

            // ユーザーのルートまでの親階層ID一覧を取得する。
            List<Long> organizationIds = this.organizationFacade.findAncestors(userId);

            // 指定した階層IDの子階層情報一覧を取得する。(指定ユーザーがアクセス可能な階層のみ)
            query = this.em.createNamedQuery("KanbanHierarchyEntity.findChildByUserId", KanbanHierarchyEntity.class);
            //query.setParameter("type", AccessHierarchyTypeEnum.KanbanHierarchy);
            query.setParameter("ancestors", organizationIds);
        }
        query.setParameter("hierarchyId", id);

        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            query.setMaxResults(to - from + 1);
            query.setFirstResult(from);
        }
        return query.getResultList();
    }

    /**
     * 階層IDを指定して、親階層IDを取得する。
     *
     * @param id 階層ID
     * @return 親階層ID
     */
    @Lock(LockType.READ)
    private Long findParentId(Long id) {
        TypedQuery<Long> query = this.em.createNamedQuery("TreeKanbanHierarchyEntity.findParentId", Long.class);
        query.setParameter("childId", id);
        query.setMaxResults(1);

        try {
            return query.getSingleResult();
        } catch (NoResultException ex) {
            // 親階層が設定されていない。
            return null;
        }
    }

    /**
     * 指定した階層の子階層の件数を取得する。
     * ※．ユーザーIDが指定されている場合、アクセス可能な階層情報を対象とする。
     *
     * @param hierarchyId 階層ID
     * @param userId ユーザーID (組織ID)
     * @return 子階層の件数
     */
    @Lock(LockType.READ)
    private long countChild(Long hierarchyId, Long userId) {
        TypedQuery<Long> query;

        if(Objects.isNull(userId)) {
            // 指定した階層IDの子階層情報の件数を取得する。
            query = this.em.createNamedQuery("KanbanHierarchyEntity.countChild", Long.class);
        } else {
            // ユーザーIDが指定されている場合、アクセス可能な階層情報を対象とする。

            // ユーザーのルートまでの親階層ID一覧を取得する。
            List<Long> organizationIds = this.organizationFacade.findAncestors(userId);

            // 指定した階層IDの子階層情報の件数を取得する。(指定ユーザーがアクセス可能な階層のみ)
            query = this.em.createNamedQuery("KanbanHierarchyEntity.countChildByUserId", Long.class);
            //query.setParameter("type", AccessHierarchyTypeEnum.KanbanHierarchy);
            query.setParameter("ancestors", organizationIds);
        }
        query.setParameter("hierarchyId", hierarchyId);

        return query.getSingleResult();
    }

    /**
     * カンバン階層情報(親子関係)を削除する。
     *
     * @param id 階層ID
     */
    private void removeHierarchy(Long id) {
        // 子階層IDを指定して、カンバン階層情報を削除する。
        Query query = this.em.createNamedQuery("TreeKanbanHierarchyEntity.removeByChildId");
        query.setParameter("childId", id);
        query.executeUpdate();
    }

    /**
     * カンバン階層情報(親子関係)を登録する。
     *
     * @param entity カンバン階層マスタ情報
     */
    private void addHierarchy(KanbanHierarchyEntity entity) {
        TreeKanbanHierarchyEntity hierarchy = new TreeKanbanHierarchyEntity(entity.getParentId(), entity.getKanbanHierarchyId());
        this.em.persist(hierarchy);
    }


    /**
     * カンバン階層情報(親子関係)を登録または更新する。
     *
     * @param entity カンバン階層マスタ情報
     * @return 
     */
    private boolean registHierarchy(KanbanHierarchyEntity entity) {
        boolean result = false;

        // 階層IDを指定して、カンバン階層情報の件数を取得する。
        TypedQuery<Long> countQuery = this.em.createNamedQuery("TreeKanbanHierarchyEntity.countByChildId", Long.class);
        countQuery.setParameter("childId", entity.getKanbanHierarchyId());

        Long count = countQuery.getSingleResult();
        if (count == 0) {
            // カンバン階層情報(親子関係)を新規登録する。
            this.addHierarchy(entity);
            result = true;
        } else {
            // カンバン階層情報(親子関係)を更新する。
            Query updateQuery = this.em.createNamedQuery("TreeKanbanHierarchyEntity.updateParentId");
            updateQuery.setParameter("parentId", entity.getParentId());
            updateQuery.setParameter("childId", entity.getKanbanHierarchyId());

            int updateCount = updateQuery.executeUpdate();
            if (updateCount == 1) {
                result = true;
            }
        }

        return result;
    }

    /**
     * 指定された階層に、ユーザーがアクセス可能か取得する。
     *
     * @param id 階層ID
     * @param userId ユーザーID (組織ID)
     * @return アクセス (true: アクセス可能, false: アクセス不可)
     */
    @Lock(LockType.READ)
    public boolean isHierarchyAccessible(Long id, Long userId) {
        // ユーザーIDが未指定の場合、アクセス可能。
        if (Objects.isNull(userId)) {
            return true;
        }

        // 階層のアクセス権をチェックする。
        boolean isAccessible = this.authRest.isAccessible(AccessHierarchyTypeEnum.KanbanHierarchy, id, userId);
        if (!isAccessible) {
            return false;
        }

        // 階層IDを指定して、親階層IDを取得する。
        TypedQuery<Long> query = this.em.createNamedQuery("TreeKanbanHierarchyEntity.findParentId", Long.class);
        query.setParameter("childId", id);

        Long parentId;
        try {
            parentId = query.getSingleResult();
        } catch (Exception ex) {
            parentId = null;
        }

        // 親階層が存在する場合、親階層の階層アクセス権をチェックする。
        if (Objects.nonNull(parentId) && parentId > 0){
            isAccessible = this.isHierarchyAccessible(parentId, userId);
        }

        return isAccessible;
    }

    private Long findParentHierarchy(Long childId) {
        TypedQuery<Long> query = this.em.createNamedQuery("TreeKanbanHierarchyEntity.findParentId", Long.class);
        query.setParameter("childId", childId);
        query.setMaxResults(1);
        try {
            return query.getSingleResult();
        } catch (Exception ex) {
            return null;
        }
    }

    private boolean isRegistableHierarchy(Long id, Long parentId) {
        if (Objects.isNull(parentId)) {
            // 無効な親の場合はNG
            return false;
        }

        if (Objects.isNull(id)) {
            // 自分自身が未登録の為、登録可能
            return true;
        }

        if (Objects.equals(id, parentId)) {
            // 自分自身とつながっているのでNG
            return false;
        }

        if (Objects.equals(0L, parentId)) {
            // ルートまで繋がっているので登録可能。
            return true;
        }

        return isRegistableHierarchy(id, findParentHierarchy(parentId));
    }

    /**
     * 指定カンバン階層以下の階層ＩＤを取得する
     * @param hierarchyName カンバン階層名
     * @return カンバン階層ID
     */
    public List<Long> getKanbanHierarchyIdsByName(String hierarchyName) {
        try {
            TypedQuery<Long> query = this.em.createNamedQuery("TreeKanbanHierarchyEntity.findDescendantsIdByName", Long.class);
            query.setParameter(1, hierarchyName);

            return query.getResultList();
        } catch (Exception ex) {
            logger.info(ex, ex);
            return new ArrayList<>();
        }
    };


    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    protected void setEntityManager(EntityManager em) {
        this.em = em;
    }

    protected void setKanbanFacadeRest(KanbanEntityFacadeREST rest) {
        this.kanbanFacade = rest;
    }

    protected void setWorkflowFacadeRest(WorkflowEntityFacadeREST rest) {
        this.workflowFacade = rest;
    }

    protected void setAuthRest(AccessHierarchyEntityFacadeREST authRest) {
        this.authRest = authRest;
    }

    protected void setOrganizationFacade(OrganizationEntityFacadeREST organizationFacade) { this.organizationFacade = organizationFacade; }
}
