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
import java.util.Optional;
import java.util.stream.Collectors;
import jakarta.ejb.EJB;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.enumerate.AccessHierarchyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jp.adtekfuji.adfactoryserver.entity.access.AccessHierarchyEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import org.apache.commons.collections.ListUtils;

/**
 * 階層アクセス権情報REST
 *
 * @author j.min
 */
@Singleton
@Path("access")
public class AccessHierarchyEntityFacadeREST extends AbstractFacade<AccessHierarchyEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    @EJB
    private OrganizationEntityFacadeREST organizationFacade;

    private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     */
    public AccessHierarchyEntityFacadeREST() {
        super(AccessHierarchyEntity.class);
    }

    /**
     * 指定した階層の階層アクセス権情報の件数を取得する。
     *
     * @param type 階層種別
     * @param id 階層ID
     * @param authId 認証ID
     * @return 件数
     */
    @Lock(LockType.READ)
    @GET
    @Path("tree/count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public Long countHierarchy(@QueryParam("type") AccessHierarchyTypeEnum type, @QueryParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("countHierarchy: type={}, id={}, authId={}", type, id, authId);

        // 階層種別ID・階層IDを指定して、階層アクセス権情報の件数を取得する。
        TypedQuery<Long> query = em.createNamedQuery("AccessHierarchyEntity.count", Long.class);
        query.setParameter("type", type);
        query.setParameter("id", id);

        return query.getSingleResult();
    }

    /**
     * 指定した階層へのアクセスが許可されている組織情報一覧を範囲指定して取得する。
     *
     * @param type 階層種別
     * @param id 階層ID
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 組織情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("tree/range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<OrganizationEntity> findHierarchyRange(@QueryParam("type") AccessHierarchyTypeEnum type, @QueryParam("id") Long id, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findHierarchyRange: type={}, id={}, from={}, to={}, authId={}", type, id, from, to, authId);
        return this.findOrganization(this.findRange(type, id, from, to));
    }

    /**
     * 階層アクセス権情報を追加する。
     *
     * @param entity 階層アクセス権情報
     * @param authId 認証ID
     * @return
     * @throws URISyntaxException 
     */
    @POST
    @Path("tree")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(AccessHierarchyEntity entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("add: {}, authId={}", entity, authId);
        try {
            // 階層アクセス権情報の重複を確認する。
            TypedQuery<Long> query = em.createNamedQuery("AccessHierarchyEntity.check", Long.class);
            query.setParameter("type", entity.getTypeId());
            query.setParameter("id", entity.getHierarchyId());
            query.setParameter("organizationId", entity.getOrganizationId());
            if (query.getSingleResult() > 0) {
                // 該当するものがあった場合、重複を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }

            // 階層アクセス権情報を登録する。
            em.persist(entity);
            // 作成した情報を元に、戻り値のURIを作成する。
            URI uri = new URI("access/tree/");
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 指定した階層・組織の階層アクセス権情報を削除する。
     *
     * @param type 階層種別
     * @param id 階層ID
     * @param organizationIds アクセス権を削除する組織ID一覧
     * @param authId 認証ID
     * @return 
     */
    @DELETE
    @Path("tree")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response remove(@QueryParam("type") AccessHierarchyTypeEnum type, @QueryParam("id") Long id, @QueryParam("data") List<Long> organizationIds, @QueryParam("authId") Long authId) {
        logger.info("remove: type={}, id={}, organizationIds={}, authId={}", type, id, organizationIds, authId);
        try {
            // 組織IDの指定がない場合、何もせずに成功扱いとする。
            if (Objects.isNull(organizationIds) || organizationIds.isEmpty()) {
                return Response.ok().entity(ResponseEntity.success()).build();
            }

            // 階層種別ID・階層ID・組織ID一覧を指定して、階層アクセス権情報を削除する。
            Query query = em.createNamedQuery("AccessHierarchyEntity.removeDatas");
            query.setParameter("type", type);
            query.setParameter("id", id);
            query.setParameter("organizationIds", organizationIds);

            query.executeUpdate();

            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 指定した階層の全組織の階層アクセス権情報を削除する。
     *
     * @param type 階層種別
     * @param id 階層ID
     */
    public void remove(AccessHierarchyTypeEnum type, Long id) {
        logger.info("remove: type={}, id={}", type, id);

        // 階層種別ID・階層IDを指定して、階層アクセス権情報を削除する。
        Query query1 = em.createNamedQuery("AccessHierarchyEntity.removeByTypeAndId");
        query1.setParameter("type", type);
        query1.setParameter("id", id);
        query1.executeUpdate();
    }

    /**
     * 指定した階層の階層アクセス権情報一覧を範囲指定して取得する。
     *
     * @param type 階層種別
     * @param id 階層ID
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @return 階層アクセス権情報一覧
     */
    @Lock(LockType.READ)
    private List<AccessHierarchyEntity> findRange(AccessHierarchyTypeEnum type, Long id, Integer from, Integer to) {
        // 階層種別ID・階層IDを指定して、階層アクセス権情報一覧を取得する。
        TypedQuery<AccessHierarchyEntity> query = em.createNamedQuery("AccessHierarchyEntity.find", AccessHierarchyEntity.class);
        query.setParameter("type", type);
        query.setParameter("id", id);

        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            query.setMaxResults(to - from + 1);
            query.setFirstResult(from);
        }

        return query.getResultList();
    }

    /**
     * 階層アクセス権情報一覧を指定して、組織情報一覧を取得する。
     *
     * @param accessHierarchies 階層アクセス権情報一覧
     * @return 組織情報一覧
     */
    @Lock(LockType.READ)
    private List<OrganizationEntity> findOrganization(List<AccessHierarchyEntity> accessHierarchies) {
        if (accessHierarchies.isEmpty()) {
            return new ArrayList();
        }

        // 階層アクセス権一覧から組織ID一覧を取得する。
        List<Long> organizationIds = accessHierarchies.stream()
                .map(a -> a.getOrganizationId())
                .distinct()
                .collect(Collectors.toList());

        // 組織ID一覧を指定して、組織情報一覧を取得する。
        TypedQuery<OrganizationEntity> query = em.createNamedQuery("OrganizationEntity.findByIdsNotRemove", OrganizationEntity.class);
        query.setParameter("organizationIds", organizationIds);

        return query.getResultList();
    }

    /**
     * 指定された階層に、ユーザーがアクセス可能か取得する。
     *
     * @param type 階層種別
     * @param id 階層ID
     * @param userId ユーザーID (組織ID)
     * @return アクセス (true: アクセス可能, false: アクセス不可)
     */
    @Lock(LockType.READ)
    public boolean isAccessible(AccessHierarchyTypeEnum type, Long id, Long userId) {
        // 階層種別ID・階層IDを指定して、階層アクセス権情報の組織ID一覧を取得する。
        TypedQuery<Long> query = em.createNamedQuery("AccessHierarchyEntity.getOrganizationIds", Long.class);
        query.setParameter("type", type);
        query.setParameter("id", id);

        List<Long> accessibleIds = query.getResultList();

        // 階層にアクセス権の設定が無い場合、アクセス可能。
        if (Objects.isNull(accessibleIds) || accessibleIds.isEmpty()) {
            return true;
        }

        // ユーザーのルートまでの親組織ID一覧を取得する。
        List<Long> organizationIds = organizationFacade.findAncestors(userId);

        // 親組織ID一覧のいずれかが、階層アクセス権情報の組織ID一覧に含まれていれば、アクセス可能。
        Optional<Long> opt = ListUtils.intersection(accessibleIds, organizationIds).stream().findFirst();
        return opt.isPresent();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
}
