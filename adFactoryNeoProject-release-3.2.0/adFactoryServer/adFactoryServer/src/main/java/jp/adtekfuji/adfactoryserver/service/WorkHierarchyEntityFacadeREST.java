/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import adtekfuji.utility.StringUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import jakarta.ejb.EJB;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
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
import jp.adtekfuji.adFactory.enumerate.AuthorityEnum;
import jp.adtekfuji.adFactory.enumerate.HierarchyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.common.Constants;
import jp.adtekfuji.adfactoryserver.entity.access.Hierarchy;
import jp.adtekfuji.adfactoryserver.entity.approval.ApprovalEntity;
import jp.adtekfuji.adfactoryserver.entity.master.HierarchyEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkHierarchyEntity;
import jp.adtekfuji.adfactoryserver.model.FileManager;
import jp.adtekfuji.adfactoryserver.model.LicenseManager;
import jp.adtekfuji.adfactoryserver.model.approval.ApprovalModel;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import jp.adtekfuji.adfactoryserver.utility.QueryUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程階層情報REST
 *
 * @author ke.yokoi
 */
@Singleton
@Path("work/tree")
public class WorkHierarchyEntityFacadeREST {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    @Inject
    private ApprovalModel approvalModel;

    @EJB
    private HierarchyEntityFacadeREST hierarchyRest;

    @EJB
    private OrganizationEntityFacadeREST organizationFacade;

    private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     */
    public WorkHierarchyEntityFacadeREST() {
    }

    /**
     * 階層IDを指定して、階層情報を取得する。(工程情報一覧は取得しない)
     *
     * @param id 階層ID
     * @return 階層情報
     */
    @Lock(LockType.READ)
    public WorkHierarchyEntity findBasicInfo(Long id) {
        try {
            HierarchyEntity hierarchy = this.hierarchyRest.find(id);
            if (Objects.isNull(hierarchy)) {
                return null;
            } else {
                return hierarchy.downcast(WorkHierarchyEntity.class);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 指定した階層の子階層情報一覧を取得する。
     * ※．ユーザーIDが指定されている場合、アクセス可能な階層情報を対象とする。
     *
     * @param id 階層ID
     * @param userId ユーザーID (組織ID)
     * @param from 範囲の先頭
     * @param to 範囲の先頭
     * @param hasChild 子がいるか否かのフラグ
     * @param approve 承認済のみ取得 (true:承認済のみ, false:全て)
     * @param authId 認証ID
     * @return 階層情報一覧
     * @throws java.lang.Exception
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkHierarchyEntity> findTreeRange(@QueryParam("id") Long id, @QueryParam("user") Long userId, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("hasChild") Boolean hasChild, @QueryParam("approve") Boolean approve, @QueryParam("authId") Long authId) throws Exception {
        logger.info("findTree: id={}, userId={}, from={}, to={}, hasChild={}, approve={}, authId={}", id, userId, from, to, hasChild, approve, authId);

        List<HierarchyEntity> hierarchies = this.hierarchyRest.findTreeRange(HierarchyTypeEnum.WORK, id, userId, from, to, authId);
        List<WorkHierarchyEntity> entities = HierarchyEntityFacadeREST.downcastList(WorkHierarchyEntity.class, hierarchies);

        if (Objects.isNull(hasChild) || hasChild) {
            for (WorkHierarchyEntity entity : entities) {
                // 階層に属する工程情報一覧を取得してセットする。
                List<WorkEntity> works = this.findWork(entity.getWorkHierarchyId(), approve);
                entity.setWorkCollection(works);
            }
        }

        return entities;
    }

    /**
     * 指定した階層名の階層情報を取得する。(階層のみで工程リストは取得しない)
     *
     * @param name 階層名
     * @param authId 認証ID
     * @return 階層情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("hierarchy/name")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public WorkHierarchyEntity findHierarchyByName(@QueryParam("name") String name, @QueryParam("authId") Long authId) throws Exception {
        logger.info("findHierarchyByName: name={}, authId={}", name, authId);
        Long userId = null;

        HierarchyEntity hierarchy = this.hierarchyRest.findHierarchyByName(HierarchyTypeEnum.WORK, name, userId, authId);

        return hierarchy.downcast(WorkHierarchyEntity.class);
    }

    /**
     * 指定した階層の子階層情報の件数を取得する。
     * ※．ユーザーIDが指定されている場合、アクセス可能な階層情報を対象とする。
     *
     * @param id 階層ID
     * @param userId ユーザーID (組織ID)
     * @param authId 認証ID
     * @return 子階層の個数
     */
    @Lock(LockType.READ)
    @GET
    @Path("count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String countTree(@QueryParam("id") Long id, @QueryParam("user") Long userId, @QueryParam("authId") Long authId) {
        logger.info("countTree: id={}, userId={}, authId={}", id, userId, authId);
        return this.hierarchyRest.countTree(HierarchyTypeEnum.WORK, id, userId, authId);
    }

    /**
     * 指定した階層IDの階層情報を取得する。
     *
     * @param id 階層ID
     * @param authId 認証ID
     * @return 階層情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public WorkHierarchyEntity find(@PathParam("id") Long id, @QueryParam("authId") Long authId) throws Exception {
        logger.info("find: id={}, authId={}", id, authId);
        return this.find(id);
    }

    /**
     * 階層情報を登録する。
     *
     * @param entity 工程階層情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(WorkHierarchyEntity entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("add: {}, authId={}", entity, authId);
        try {
            HierarchyEntity hierarchy = entity.upcast();
            Response response = this.hierarchyRest.add(hierarchy, authId);

            // 成功の場合はdelay_reasonの戻り値のURIを作成、失敗時はResponseをそのまま返す。
            ResponseEntity result= (ResponseEntity) response.getEntity();
            if (!result.isSuccess()) {
                return response;
            } else {
                entity.setHierarchyId(hierarchy.getHierarchyId());

                // 作成した情報を元に、戻り値のURIを作成する
                URI uri = new URI(new StringBuilder("work/tree/").append(entity.getHierarchyId()).toString());
                return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 階層情報を更新する。
     *
     * @param entity 工程階層情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(WorkHierarchyEntity entity, @QueryParam("authId") Long authId) {
        logger.info("update: {}, authId={}", entity, authId);
        try {
            return this.hierarchyRest.update(entity.upcast(), authId);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 指定した階層IDの階層情報を削除する。
     *
     * @param id 工程階層ID
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @DELETE
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response remove(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("remove: id={}, authId={}", id, authId);
        try {
            return this.hierarchyRest.remove(HierarchyTypeEnum.WORK, id, authId);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 階層IDを指定して、階層情報を取得する。(工程情報一覧を含む)
     *
     * @param id 階層ID
     * @return 階層情報
     * @throws Exception 
     */
    private WorkHierarchyEntity find(@PathParam("id") Long id) throws Exception {
        HierarchyEntity hierarchy = this.hierarchyRest.find(id);
        if (Objects.isNull(hierarchy)) {
            return null;
        }

        WorkHierarchyEntity entity = hierarchy.downcast(WorkHierarchyEntity.class);
        if (Objects.nonNull(entity)) {
            List<WorkEntity> works = this.findWork(id, false);
            entity.setWorkCollection(works);
        }
        return entity;
    }

    /**
     * 階層IDを指定して、階層に属する工程情報一覧を取得する。
     *
     * @param hierarchyId 階層ID
     * @param approve 承認済のみ取得 (true:承認済のみ, false:全て)
     * @return 工程情報一覧
     */
    private List<WorkEntity> findWork(Long hierarchyId, Boolean approve) {
        TypedQuery<WorkEntity> query;
        if (Objects.nonNull(approve) && approve) {
            query = this.em.createNamedQuery("WorkEntity.findByHierarchyIdApprove", WorkEntity.class);
        } else {
            query = this.em.createNamedQuery("WorkEntity.findByHierarchyId", WorkEntity.class);
        }

        query.setParameter("hierarchyId", hierarchyId);
        List<WorkEntity> works = query.getResultList();

        // 承認機能ライセンス
        boolean approvalLicense = LicenseManager.getInstance().isLicenceOption(LicenseOptionType.ApprovalOption.getName());

        for (WorkEntity work : works) {
            work.setParentId(hierarchyId);

            // 承認機能ライセンスが有効な場合は申請情報も取得する。
            if (approvalLicense && Objects.nonNull(work.getApprovalId())) {
                ApprovalEntity approval = this.approvalModel.findApproval(work.getApprovalId());
                work.setApproval(approval);
            }
        }
        return works;
    }

    protected void setEntityManager(EntityManager em) {
        this.em = em;
    }

    protected void setHierarchyRest(HierarchyEntityFacadeREST hierarchyRest) {
        this.hierarchyRest = hierarchyRest;
    }

    /**
     * 工程を検索する。
     * 
     * @param name 工程名
     * @param hierarchyId 工程階層ID
     * @param latestOnly 最新版のみかどうか
     * @param authId 組織ID
     * @return 工程情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("search")
    @Consumes({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkEntity> searchWork(@QueryParam("name") String name, @QueryParam("hierarchyId") Long hierarchyId, @QueryParam("latestOnly") Boolean latestOnly, @QueryParam("authId") Long authId) {
        logger.info("searchWork: name={}, hierarchyId={}, latestOnly={}, authId={}", name, hierarchyId, latestOnly, authId);

        try {
            List<Long> parentIds = new ArrayList<>(Arrays.asList(hierarchyId));
            
            if (Objects.isNull(hierarchyId) || hierarchyId == 0) {
                String liteTreeName = FileManager.getInstance().getSystemProperties().getProperty(Constants.LITE_HIERARCHY_TOP_KEY);

                List<HierarchyEntity> hierarchies = this.hierarchyRest.findTreeRange(HierarchyTypeEnum.WORK, 0L, authId, null, null, authId);
                parentIds = hierarchies.stream().filter(s -> !StringUtils.equals(s.getHierarchyName(), liteTreeName))
                        .map(o -> o.getHierarchyId()).collect(Collectors.toList());
                if (parentIds.isEmpty()) {
                    return new ArrayList<>();
                }
            }

            List<Hierarchy> hierarchies = null;

            OrganizationEntity user = this.organizationFacade.find(authId);
            if (AuthorityEnum.SYSTEM_ADMIN.equals(user.getAuthorityType())) {
                // ADMIN の場合、すべての階層を取得
                Query query = this.em.createNamedQuery("Hierarchy.find", Hierarchy.class);
                query.setParameter(1, this.em.unwrap(Connection.class).createArrayOf("bigint", parentIds.toArray()));
                query.setParameter(2, AccessHierarchyTypeEnum.WorkHierarchy.getValue());
                hierarchies = query.getResultList();
            } else {
                // アクセス可能な階層のみを取得
                List<Long> organizationIds = this.organizationFacade.findAncestors(authId);
    
                Query query = this.em.createNamedQuery("Hierarchy.findAccessOnly", Hierarchy.class);
                query.setParameter(1, this.em.unwrap(Connection.class).createArrayOf("bigint", parentIds.toArray()));
                query.setParameter(2, AccessHierarchyTypeEnum.WorkHierarchy.getValue());
                query.setParameter(3, this.em.unwrap(Connection.class).createArrayOf("bigint", organizationIds.toArray()));
                hierarchies = query.getResultList();
            }
            
            List<Long> hierarchyIds = hierarchies.stream().map(o -> o.getHierarchyId()).collect(Collectors.toList());
            
            Query query = null;
            if (Objects.nonNull(latestOnly) && latestOnly) {
                query = this.em.createNamedQuery("WorkEntity.findLatestOnlyByHierarchyIdAndName", WorkEntity.class);
            } else {
                query = this.em.createNamedQuery("WorkEntity.findByHierarchyIdAndName", WorkEntity.class);
            }
            query.setParameter("hierarchyIds", hierarchyIds);
            query.setParameter("workName", QueryUtils.getLikeValue(name));
           
            return query.getResultList();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return null;
    }
    
    
}
