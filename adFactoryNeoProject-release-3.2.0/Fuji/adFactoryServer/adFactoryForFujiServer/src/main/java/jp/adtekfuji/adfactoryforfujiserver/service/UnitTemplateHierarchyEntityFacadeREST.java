/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate.ConUnitTemplateHierarchyEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate.TreeUnitTemplateHierarchyEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate.UnitTemplateEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate.UnitTemplateHierarchyEntity;
import jp.adtekfuji.adfactoryforfujiserver.service.standard.OrganizationEntityFacade;
import jp.adtekfuji.adfactoryforfujiserver.utility.ExecutionTimeLogging;
import jp.adtekfuji.forfujiapp.entity.accessfuji.AccessHierarchyFujiTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ユニットテンプレート階層用REST
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.13.Thr
 */
@Singleton
@Path("unittemplate/tree")
public class UnitTemplateHierarchyEntityFacadeREST extends AbstractFacade<UnitTemplateHierarchyEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryForFujiServer_war_1.0PU")
    private EntityManager em;
    @EJB
    private UnitTemplateEntityFacadeREST unitTemplateFacadeEntityREST;
    @EJB
    private OrganizationEntityFacade organizationFacade;
    @EJB
    private AccessHierarchyFujiEntityFacadeREST authRest;
    private final Logger logger = LogManager.getLogger();

    public UnitTemplateHierarchyEntityFacadeREST() {
        super(UnitTemplateHierarchyEntity.class);
    }

    /**
     * 指定した親IDに含まれるユニットテンプレート階層一覧を取得する
     *
     * @param parentId 親階層ID
     * @return 階層リスト
     */
    @Lock(LockType.READ)
    @GET
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<UnitTemplateHierarchyEntity> findTree(@QueryParam("id") Long parentId) {
        if (Objects.isNull(parentId)) {
            parentId = 0L;
        }
        logger.info("findTree:{}", parentId);
        List<UnitTemplateHierarchyEntity> entities = new ArrayList<>();
        List<TreeUnitTemplateHierarchyEntity> hirearchys = findChild(parentId, null, null, null);
        for (TreeUnitTemplateHierarchyEntity hirearchy : hirearchys) {
            UnitTemplateHierarchyEntity entity = find(hirearchy.getTreeUnitTemplateHierarchyEntityPK().getChildId(), true);
            entity.setParentId(parentId);
            entities.add(entity);
        }
        // 名前順でソート
        entities.sort(Comparator.comparing(entity -> entity.getHierarchyName()));
        return entities;
    }

    /**
     * 指定した親IDに含まれるユニットテンプレート階層一覧の個数を取得する
     *
     * @param parentId 親階層のID(ID指定なしは最上階を検索)
     * @param userId
     * @return 子階層の数
     */
    @Lock(LockType.READ)
    @GET
    @Path("count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String findTreeCount(@QueryParam("id") Long parentId, @QueryParam("user") Long userId) {
        if (Objects.isNull(parentId)) {
            parentId = 0L;
        }
        logger.info("findTreeCount:{},{}", parentId, userId);
        List<TreeUnitTemplateHierarchyEntity> hirearchys = findChild(parentId, userId, null, null);
        return String.valueOf(hirearchys.size());
    }

    /**
     * 指定した親IDに含まれるユニットテンプレート階層一覧の範囲を取得する
     *
     * @param parentId 親階層のID(指定がない場合は最上階を検索)
     * @param from 検索開始範囲
     * @param to 検索終了範囲
     * @param userId
     * @param hasChild
     * @return 階層情報のリスト
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<UnitTemplateHierarchyEntity> findTreeRange(@QueryParam("id") Long parentId, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("user") Long userId, @QueryParam("hasChild") Boolean hasChild) {
        if (Objects.isNull(parentId)) {
            parentId = 0L;
        }
        logger.info("findTreeRange:{},{},{},{},{}", parentId, from, to, userId, hasChild);
        List<UnitTemplateHierarchyEntity> entities = new ArrayList<>();
        List<TreeUnitTemplateHierarchyEntity> hirearchys = findChild(parentId, userId, from, to);

        for (TreeUnitTemplateHierarchyEntity hirearchy : hirearchys) {
            // 取得した階層に所属するユニットテンプレートを埋め込む
            UnitTemplateHierarchyEntity entity = find(hirearchy.getTreeUnitTemplateHierarchyEntityPK().getChildId(), hasChild);
            entity.setParentId(parentId);
            entities.add(entity);
        }
        // 名前順でソート
        entities.sort(Comparator.comparing(entity -> entity.getHierarchyName()));

        return entities;
    }

    /**
     * ユニットテンプレート階層の追加
     *
     * @param entity 追加する階層情報
     * @return OK:追加成功/IDENTANAME_OVERLAP:名前重複
     * @throws URISyntaxException DB制約違反
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(UnitTemplateHierarchyEntity entity) throws URISyntaxException {
        logger.info("add:{}", entity);
        // 重複確認.同じ階層に同じ名前があったときのみエラー
        List<TreeUnitTemplateHierarchyEntity> trees = findChild(entity.getParentId(), null, null, null);
        for (TreeUnitTemplateHierarchyEntity tree : trees) {
            TypedQuery<UnitTemplateHierarchyEntity> query = em.createNamedQuery("UnitTemplateHierarchyEntity.findByUnitTemplateHierarchyId", UnitTemplateHierarchyEntity.class);
            query.setParameter("unitTemplateHierarchyId", tree.getTreeUnitTemplateHierarchyEntityPK().getChildId());
            if (query.getSingleResult().getHierarchyName().equals(entity.getHierarchyName())) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }
        }
        // 作成.
        super.create(entity);
        em.flush();
        // 階層関連付追加
        addHierarchy(entity);
        URI uri = new URI("template/tree/" + entity.getUnitTemplateHierarchyId().toString());
        return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
    }

    /**
     * ユニットテンプレート階層の更新
     *
     * @param entity 更新する階層情報
     * @return OK:追加成功/IDENTANAME_OVERLAP:名前重複
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(UnitTemplateHierarchyEntity entity) {
        logger.info("update:{}", entity);
        //重複確認.
        List<TreeUnitTemplateHierarchyEntity> trees = findChild(entity.getParentId(), null, null, null);
        for (TreeUnitTemplateHierarchyEntity tree : trees) {
            TypedQuery<UnitTemplateHierarchyEntity> query = em.createNamedQuery("UnitTemplateHierarchyEntity.findByUnitTemplateHierarchyId", UnitTemplateHierarchyEntity.class);
            query.setParameter("unitTemplateHierarchyId", tree.getTreeUnitTemplateHierarchyEntityPK().getChildId());
            UnitTemplateHierarchyEntity target = query.getSingleResult();
            // 同じ名前で違うIDだった場合重複としてはじく
            if (target.getHierarchyName().equals(entity.getHierarchyName())
                    && !target.getUnitTemplateHierarchyId().equals(entity.getUnitTemplateHierarchyId())) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }
        }
        // 更新
        super.edit(entity);
        // 階層
        removeHierarchy(entity.getUnitTemplateHierarchyId());
        addHierarchy(entity);
        return Response.ok().entity(ResponseEntity.success()).build();
    }

    /**
     * ユニットテンプレート階層の削除
     *
     * @param id 削除する階層のID
     * @return OK:追加成功/EXIST_HIERARCHY_DELETE:子階層有/EXIST_CHILD_DELETE:子要素有
     */
    @DELETE
    @Path("{id}")
    @ExecutionTimeLogging
    public Response remove(@PathParam("id") Long id) {
        logger.info("remove:{}", id);
        //アクセス権削除
        List<Long> datas = new ArrayList<>();
        authRest.find(AccessHierarchyFujiTypeEnum.UnitTemplateHierarchy, id).stream().forEach((auth) -> {
            datas.add(auth.getFkOrganizationId());
        });
        authRest.remove(AccessHierarchyFujiTypeEnum.UnitTemplateHierarchy, id, datas);

        // 子階層がある場合削除できない.
        TypedQuery<Long> query1 = em.createNamedQuery("TreeUnitTemplateHierarchyEntity.countChild", Long.class);
        query1.setParameter("parentId", id);
        Long num1 = query1.getSingleResult();
        if (num1 > 0) {
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.EXIST_HIERARCHY_DELETE)).build();
        }

        // 子要素がある場合削除できない.
        TypedQuery<Long> query2 = em.createNamedQuery("ConUnitTemplateHierarchyEntity.countChild", Long.class);
        query2.setParameter("fkUnitTemplateHierarchyId", id);
        Long num2 = query2.getSingleResult();
        if (num2 > 0) {
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.EXIST_CHILD_DELETE)).build();
        }
        // 階層
        removeHierarchy(id);
        // 削除
        super.remove(super.find(id));
        return Response.ok().entity(ResponseEntity.success()).build();
    }

    /**
     * 指定されたIDの階層情報を取得する(所属するユニットテンプレート込)
     *
     * @param id
     * @param hasChild
     * @return
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public UnitTemplateHierarchyEntity find(@PathParam("id") Long id, @QueryParam("hasChild") Boolean hasChild) {
        UnitTemplateHierarchyEntity entity = super.find(id);
        if (Objects.nonNull(entity)) {
            TypedQuery<ConUnitTemplateHierarchyEntity> query = em.createNamedQuery("ConUnitTemplateHierarchyEntity.findByFkUnitTemplateHierarchyId", ConUnitTemplateHierarchyEntity.class);
            query.setParameter("fkUnitTemplateHierarchyId", id);

            List<UnitTemplateEntity> templateCollection = new ArrayList<>();

            if (Objects.isNull(hasChild) || hasChild) {
                // 関連付けを元にユニットテンプレート情報を取得し階層情報に埋め込む
                for (ConUnitTemplateHierarchyEntity con : query.getResultList()) {
                    UnitTemplateEntity template = unitTemplateFacadeEntityREST.findWithoutDatails(con.getConUnitTemplateHierarchyEntityPK().getFkUnitTemplateId());
                    template.setParentId(id);
                    templateCollection.add(template);
                }
            }

            entity.setUnitTemplateCollection(templateCollection);
        }

        TreeUnitTemplateHierarchyEntity hierarchy = findParent(id);
        if (Objects.nonNull(hierarchy)) {
            entity.setParentId(hierarchy.getTreeUnitTemplateHierarchyEntityPK().getParentId());
            entity.setChildCount(Long.valueOf(findChild(id, null, null, null).size()));
        }

        return entity;
    }

    /**
     * 階層に属するユニット情報の個数を取得する
     *
     * @param id 階層ID
     * @return
     */
    @Lock(LockType.READ)
    @GET
    @Path("unittemplate/count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String countUnitTemplate(@QueryParam("id") Long id) {
        logger.info("countUnitTemplate:{}", id);
        if (Objects.isNull(id)) {
            id = 0L;
        }
        Query query = em.createNamedQuery("ConUnitTemplateHierarchyEntity.countChild", Long.class);
        query.setParameter("fkUnitTemplateHierarchyId", id);
        return String.valueOf(query.getSingleResult());
    }

    /**
     * 階層に属するユニット情報を取得する
     *
     * @param id 階層ID
     * @param from
     * @param to
     * @return
     */
    @Lock(LockType.READ)
    @GET
    @Path("unittemplate/range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<UnitTemplateEntity> findUnitTemplateRange(@QueryParam("id") Long id, @QueryParam("from") Integer from, @QueryParam("to") Integer to) {
        logger.info("findUnitTemplateRange:{}", id, from, to);

        if (Objects.isNull(id)) {
            id = 0L;
        }

        TypedQuery<UnitTemplateEntity> query = em.createNamedQuery("UnitTemplateEntity.findByHierarchyId", UnitTemplateEntity.class);
        query.setParameter("unitTemplateHierarchyId", id);
        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            query.setMaxResults(to - from + 1);
            query.setFirstResult(from);
        }

        List<UnitTemplateEntity> entities = query.getResultList();

        return entities;
    }

    /**
     * 指定したユニットテンプレート階層名のユニットテンプレート階層を検索する
     *
     * @param id 親階層のID
     * @param from 検索開始範囲
     * @param to 検索終了範囲
     * @return 取得した子階層のリスト
     */
    private List<TreeUnitTemplateHierarchyEntity> findChild(Long id, Long userId, Integer from, Integer to) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT DISTINCT(t) FROM TreeUnitTemplateHierarchyEntity t");
        if(Objects.nonNull(userId)) {
            sb.append(" LEFT JOIN AccessHierarchyFujiEntity a ON a.typeId = :type AND t.treeUnitTemplateHierarchyEntityPK.childId = a.fkHierarchyId");
            sb.append(" WHERE t.treeUnitTemplateHierarchyEntityPK.parentId = :parentId");
            sb.append(" AND (a.fkOrganizationId IS NULL OR a.fkOrganizationId IN :ancestors)");
        } else {
            sb.append(" WHERE t.treeUnitTemplateHierarchyEntityPK.parentId = :parentId");
        }
        Query query = em.createQuery(sb.toString());
        query.setParameter("parentId", id);
        if(Objects.nonNull(userId)) {
            query.setParameter("type", AccessHierarchyFujiTypeEnum.UnitTemplateHierarchy);
            query.setParameter("ancestors", organizationFacade.findAncesors(userId));
        }
        // 範囲指定がある場合はクエリーに埋め込む
        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            query.setMaxResults(to - from + 1);
            query.setFirstResult(from);
        }
        return query.getResultList();
    }

    /**
     * 指定したユニットテンプレート階層名の親階層を検索する
     *
     * @param id 親階層のID
     * @return 取得した親階層
     */
    private TreeUnitTemplateHierarchyEntity findParent(Long id) {
        TypedQuery<TreeUnitTemplateHierarchyEntity> query = em.createNamedQuery("TreeUnitTemplateHierarchyEntity.findByChildId", TreeUnitTemplateHierarchyEntity.class);
        query.setParameter("childId", id);
        query.setMaxResults(1);
        TreeUnitTemplateHierarchyEntity hierarchy = null;
        try {
            hierarchy = query.getSingleResult();
        } catch (NoResultException ex) {
            //親なし
        }
        return hierarchy;
    }

    /**
     * 指定した階層情報の削除
     *
     * @param id 階層のID
     */
    private void removeHierarchy(Long id) {
        Query query = em.createNamedQuery("TreeUnitTemplateHierarchyEntity.removeByChildId");
        query.setParameter("childId", id);
        query.executeUpdate();
    }

    /**
     * 指定した階層情報の追加
     *
     * @param entity 追加する階層情報
     */
    private void addHierarchy(UnitTemplateHierarchyEntity entity) {
        TreeUnitTemplateHierarchyEntity hierarchy = new TreeUnitTemplateHierarchyEntity(entity.getParentId(), entity.getUnitTemplateHierarchyId());
        em.persist(hierarchy);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    protected void setEntityManager(EntityManager em) {
        this.em = em;
    }

    protected void setUnitTemplateEntityFacadeREST(UnitTemplateEntityFacadeREST rest) {
        this.unitTemplateFacadeEntityREST = rest;
    }

    protected void setAuthRest(AccessHierarchyFujiEntityFacadeREST authRest) {
        this.authRest = authRest;
    }

}
