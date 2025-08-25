/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.service;

import adtekfuji.utility.StringUtils;
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
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryforfujiserver.entity.unit.ConUnitHierarchyEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unit.TreeUnitHierarchyEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unit.UnitEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unit.UnitHierarchyEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unit.UnitKanbanEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate.UnitTemplateEntity;
import jp.adtekfuji.adfactoryforfujiserver.service.standard.OrganizationEntityFacade;
import jp.adtekfuji.adfactoryforfujiserver.utility.ExecutionTimeLogging;
import jp.adtekfuji.forfujiapp.entity.accessfuji.AccessHierarchyFujiTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ユニット階層用REST
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.14.Fri
 */
@Singleton
@Path("unit/tree")
public class UnitHierarchyEntityFacadeREST extends AbstractFacade<UnitHierarchyEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryForFujiServer_war_1.0PU")
    private EntityManager em;
    @EJB
    private UnitEntityFacadeREST unitEntityFacadeREST;
    @EJB
    private UnitTemplateEntityFacadeREST unittemplateEntityFacadeREST;
    @EJB
    private OrganizationEntityFacade organizationFacade;
    @EJB
    private AccessHierarchyFujiEntityFacadeREST authRest;
    private final Logger logger = LogManager.getLogger();

    public UnitHierarchyEntityFacadeREST() {
        super(UnitHierarchyEntity.class);
    }

    @Lock(LockType.READ)
    public UnitHierarchyEntity findWithoutProperty(Long id) {
        return super.find(id);
    }

    /**
     * 指定した親IDに含まれるユニット階層一覧を取得する
     *
     * @param parentId 親階層ID
     * @return 階層リスト
     */
    @Lock(LockType.READ)
    @GET
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<UnitHierarchyEntity> findTree(@QueryParam("id") Long parentId) {
        if (Objects.isNull(parentId)) {
            parentId = 0L;
        }
        logger.info("findTree:{}", parentId);
        List<UnitHierarchyEntity> entities = new ArrayList<>();
        List<TreeUnitHierarchyEntity> hirearchys = findChild(parentId, null, null, null);
        for (TreeUnitHierarchyEntity hirearchy : hirearchys) {
            UnitHierarchyEntity entity = find(hirearchy.getTreeUnitHierarchyEntityPK().getChildId());
            entity.setParentId(parentId);
            entities.add(entity);
        }
        // 名前でソート
        entities.sort(Comparator.comparing(entity -> entity.getHierarchyName()));

        return entities;
    }

    /**
     * 指定した親IDに含まれるユニット階層一覧の個数を取得する
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
        List<TreeUnitHierarchyEntity> hirearchys = findChild(parentId, userId, null, null);
        return String.valueOf(hirearchys.size());
    }

    /**
     * 指定した親IDに含まれるユニット階層一覧の範囲を取得する
     *
     * @param parentId 親階層のID(指定がない場合は最上階を検索)
     * @param from 検索開始範囲
     * @param to 検索終了範囲
     * @param userId
     * @param hasChild
     * @return 階層情報のリスト
     * @throws java.lang.Exception
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<UnitHierarchyEntity> findTreeRange(@QueryParam("id") Long parentId, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("user") Long userId, @QueryParam("hasChild") Boolean hasChild) throws Exception {
        if (Objects.isNull(parentId)) {
            parentId = 0L;
        }
        logger.info("findTreeRange:{},{},{},{},{}", parentId, from, to, userId, hasChild);
        List<UnitHierarchyEntity> entities = new ArrayList<>();

        if (Objects.nonNull(hasChild) && !hasChild) {
            List<TreeUnitHierarchyEntity> hirearchys = this.findChild(parentId, userId, from, to);
            for (TreeUnitHierarchyEntity hirearchy : hirearchys) {
                UnitHierarchyEntity entity = super.find(hirearchy.getTreeUnitHierarchyEntityPK().getChildId());
                if (Objects.isNull(entity)) {
                    continue;
                }

                long count = Long.parseLong(this.findTreeCount(entity.getUnitHierarchyId(), userId));
                entity.setChildCount(count);
                entity.setParentId(parentId);
                entities.add(entity);
            }
        } else {
            List<TreeUnitHierarchyEntity> hirearchys = this.findChild(parentId, userId, from, to);
            for (TreeUnitHierarchyEntity hirearchy : hirearchys) {
                // 取得した階層に所属するユニットテンプレートを埋め込む
                UnitHierarchyEntity entity = super.find(hirearchy.getTreeUnitHierarchyEntityPK().getChildId());
                entity.setParentId(parentId);
                entities.add(entity);
            }
        }
        // 名前でソート
        entities.sort(Comparator.comparing(entity -> entity.getHierarchyName()));

        return entities;
    }

    /**
     * 指定されたIDのユニットテンプレート階層を取得する
     *
     * @param id
     * @return
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public UnitHierarchyEntity findId(@PathParam("id") Long id) {
        logger.info("find:{}", id);
        UnitHierarchyEntity entity = find(id);
        TreeUnitHierarchyEntity parent = findParent(id);
        if (Objects.nonNull(parent)) {
            entity.setParentId(parent.getTreeUnitHierarchyEntityPK().getParentId());
        }
        return entity;
    }

    /**
     * 生産ユニット階層を取得する
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
    public UnitHierarchyEntity findHierarchyName(@QueryParam("name") String name, @QueryParam("user") Long userId) {
        logger.info("findHierarchyName:{},{}", name, userId);
        if (StringUtils.isEmpty(name)) {
            return new UnitHierarchyEntity();
        }
        UnitHierarchyEntity result;
        TypedQuery<UnitHierarchyEntity> queryExist = em.createNamedQuery("UnitHierarchyEntity.findByHierarchyName", UnitHierarchyEntity.class);
        queryExist.setParameter("hierarchyName", name);
        try {
            result = queryExist.getSingleResult();
        } catch (NoResultException ex) {
            logger.fatal(ex);
            return new UnitHierarchyEntity();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT DISTINCT(u) FROM UnitHierarchyEntity u");
        if(Objects.nonNull(userId)) {
            sb.append(" LEFT JOIN AccessHierarchyFujiEntity a ON a.typeId = :type AND u.unitHierarchyId = a.fkHierarchyId");
            sb.append(" WHERE u.unitHierarchyId = :unitHierarchyId");
            sb.append(" AND (a.fkOrganizationId IS NULL OR a.fkOrganizationId IN :ancestors)");
        } else {
            sb.append(" WHERE u.unitHierarchyId = :unitHierarchyId");
        }
        Query query = em.createQuery(sb.toString());
        if(Objects.nonNull(userId)) {
            query.setParameter("type", AccessHierarchyFujiTypeEnum.UnitHierarchy);
            query.setParameter("ancestors", organizationFacade.findAncesors(userId));
        }
        //親階層からアクセス権判定
        for(Long id : this.findAncestors(result.getUnitHierarchyId())) {
            query.setParameter("unitHierarchyId", id);
            try {
                query.getSingleResult();
            } catch (NoResultException ex) {
                logger.fatal(ex);
                return new UnitHierarchyEntity();
            }
        }
        result.setChildCount(Long.valueOf(findChild(result.getUnitHierarchyId(), null, null, null).size()));
        return result;
    }

    /**
     * ユニット階層の追加
     *
     * @param entity 追加する階層情報
     * @return OK:追加成功/IDENTANAME_OVERLAP:名前重複
     * @throws URISyntaxException DB制約違反
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(UnitHierarchyEntity entity) throws URISyntaxException {
        logger.info("add:{}", entity);
        // 重複確認.同じ階層に同じ名前があったときのみエラー
        List<TreeUnitHierarchyEntity> trees = findChild(entity.getParentId(), null, null, null);
        for (TreeUnitHierarchyEntity tree : trees) {
            TypedQuery<UnitHierarchyEntity> query = em.createNamedQuery("UnitHierarchyEntity.findByUnitHierarchyId", UnitHierarchyEntity.class);
            query.setParameter("unitHierarchyId", tree.getTreeUnitHierarchyEntityPK().getChildId());
            if (query.getSingleResult().getHierarchyName().equals(entity.getHierarchyName())) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }
        }
        // 作成
        super.create(entity);
        em.flush();
        // 階層
        addHierarchy(entity);
        URI uri = new URI("unit/tree/" + entity.getUnitHierarchyId().toString());
        return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
    }

    /**
     * ユニット階層の更新
     *
     * @param entity 更新する階層情報
     * @return OK:追加成功/IDENTANAME_OVERLAP:名前重複
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(UnitHierarchyEntity entity) {
        logger.info("update:{}", entity);
        List<TreeUnitHierarchyEntity> trees = findChild(entity.getParentId(), null, null, null);
        for (TreeUnitHierarchyEntity tree : trees) {
            TypedQuery<UnitHierarchyEntity> query = em.createNamedQuery("UnitHierarchyEntity.findByUnitHierarchyId", UnitHierarchyEntity.class);
            query.setParameter("unitHierarchyId", tree.getTreeUnitHierarchyEntityPK().getChildId());
            UnitHierarchyEntity target = query.getSingleResult();
            // 同じ名前で違うIDだった場合重複としてはじく
            if (target.getHierarchyName().equals(entity.getHierarchyName())
                    && !target.getUnitHierarchyId().equals(entity.getUnitHierarchyId())) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }
        }
        // 更新
        super.edit(entity);
        // 階層
        removeHierarchy(entity.getUnitHierarchyId());
        addHierarchy(entity);
        return Response.ok().entity(ResponseEntity.success()).build();
    }

    /**
     * ユニット階層の削除
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
        authRest.find(AccessHierarchyFujiTypeEnum.UnitHierarchy, id).stream().forEach((auth) -> {
            datas.add(auth.getFkOrganizationId());
        });
        authRest.remove(AccessHierarchyFujiTypeEnum.UnitHierarchy, id, datas);

        // 子があるうちは削除できない.
        TypedQuery<Long> query1 = em.createNamedQuery("TreeUnitHierarchyEntity.countChild", Long.class);
        query1.setParameter("parentId", id);
        Long num1 = query1.getSingleResult();
        if (num1 > 0) {
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.EXIST_HIERARCHY_DELETE)).build();
        }
        TypedQuery<Long> query2 = em.createNamedQuery("ConUnitHierarchyEntity.countChild", Long.class);
        query2.setParameter("fkUnitHierarchyId", id);
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
     * 指定されたIDの階層情報を取得する(所属するユニット込)
     *
     * @param id
     * @return
     */
    private UnitHierarchyEntity find(@PathParam("id") Long id) {
        TreeUnitHierarchyEntity hierarchy = findParent(id);
        UnitHierarchyEntity entity = super.find(id);
        if (Objects.nonNull(entity)) {
            if (Objects.nonNull(hierarchy)) {
                entity.setParentId(hierarchy.getTreeUnitHierarchyEntityPK().getParentId());
                entity.setChildCount(Long.valueOf(findChild(id, null, null, null).size()));
            }
            // ユニットの情報を付与する
            TypedQuery<ConUnitHierarchyEntity> query = em.createNamedQuery("ConUnitHierarchyEntity.findByFkUnitHierarchyId", ConUnitHierarchyEntity.class);
            query.setParameter("fkUnitHierarchyId", id);
            List<UnitEntity> collection = new ArrayList<>();
            for (ConUnitHierarchyEntity con : query.getResultList()) {
                UnitEntity unit = unitEntityFacadeREST.findWithoutDatails(con.getConUnitHierarchyEntityPK().getFkUnitId());
                unit.setParentId(id);
                UnitTemplateEntity unittemplate = unittemplateEntityFacadeREST.findWithoutDatails(unit.getFkUnitTemplateId());
                unit.setUnitTemplateName(unittemplate.getUnitTemplateName());
                collection.add(unit);
            }
            entity.setUnitCollection(collection);
        }
        return entity;
    }

    /**
     * ユニット階層に属するユニット情報の個数を取得する
     *
     * @param id ユニット階層ID
     * @param isAll すべて取得？ (true:すべて, false:未完了のみ)
     * @return 件数
     */
    @Lock(LockType.READ)
    @GET
    @Path("unit/count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String countUnit(@QueryParam("id") Long id, @QueryParam("all") Boolean isAll) {
        logger.info("countUnit: id={}, all={}", id, isAll);
        Long unitCount = 0L;
        if (Objects.isNull(id)) {
            id = 0L;
        }

        // all の指定がない場合、すべて対象(true)とする。
        if (Objects.isNull(isAll)) {
            isAll = true;
        }

        TypedQuery<UnitEntity> query = em.createNamedQuery("UnitEntity.findByUnitHierarchyId", UnitEntity.class);
        query.setParameter("fkUnitHierarchyId", id);
        List<UnitEntity> entities = query.getResultList();

        List<Long> kanbanIds = new ArrayList();
        for (UnitEntity entity : entities) {
            UnitTemplateEntity unitTemplate = unittemplateEntityFacadeREST.find(entity.getFkUnitTemplateId());
            entity.setParentId(id);
            entity.setUnitTemplateName(unitTemplate.getUnitTemplateName());
            
            if (isAll) {
                unitCount++;
            } else {
                List<Long> unitKanbanIds = getUnitKanbanIdList(entity.getUnitId());
                if (!unitKanbanIds.isEmpty()) {
                    entity.setKanbanIds(unitKanbanIds);
                    kanbanIds.addAll(unitKanbanIds);
                }
            }
        }

        if (!isAll) {
            List<UnitKanbanEntity> unitKanbans;
            if (!kanbanIds.isEmpty()) {
                unitKanbans = PostgreAPI.getKanbans(this.em, kanbanIds);
            } else {
                unitKanbans = new ArrayList();
            }

            boolean isAdd;
            for (UnitEntity entity : entities) {
                isAdd = false;
                if (Objects.isNull(entity.getKanbanIds())) {
                    // カンバンなしのユニットは未完了扱いにする。
                    isAdd = true;
                } else {
                    entity.setIsCompleted(true);
                    for (Long kanbanId : entity.getKanbanIds()) {
                        long notCompNum = unitKanbans.stream().filter(p -> kanbanId.equals(p.getKanbanId()) && !KanbanStatusEnum.COMPLETION.equals(p.getKanbanStatus())).count();
                        if (notCompNum > 0) {
                            isAdd = true;
                            break;
                        }
                    }
                }

                if (isAdd) {
                    unitCount++;
                }
            }
        }

        return String.valueOf(unitCount);
    }

    /**
     * ユニット階層に属するユニット情報を取得する
     *
     * @param id ユニット階層ID
     * @param from rangeの先頭
     * @param to rangeの末尾
     * @param isAll すべて取得？ (true:すべて, false:未完了のみ)
     * @return ユニット情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("unit/range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<UnitEntity> findUnitRange(@QueryParam("id") Long id, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("all") Boolean isAll) {
        logger.info("findUnitRange: id={}, from={}, to={}, all={}", id, from, to, isAll);
        List<UnitEntity> result = this.findUnit(id, isAll);

        if (from >= result.size()) {
            return new ArrayList();
        }

        if (to >= result.size()) {
            to = result.size() - 1;
        }

        return result.subList(from, to + 1);
    }

    /**
     * ユニット階層に属するユニット情報を取得する
     *
     * @param id ユニット階層ID
     * @param isAll すべて取得？ (true:すべて, false:未完了のみ)
     * @return ユニット情報一覧
     */
    private List<UnitEntity> findUnit(Long id, Boolean isAll) {
        List<UnitEntity> result = new ArrayList();

        if (Objects.isNull(id)) {
            id = 0L;
        }

        // all の指定がない場合、すべて対象(true)とする。
        if (Objects.isNull(isAll)) {
            isAll = true;
        }

        TypedQuery<UnitEntity> query = em.createNamedQuery("UnitEntity.findByUnitHierarchyId", UnitEntity.class);
        query.setParameter("fkUnitHierarchyId", id);
        List<UnitEntity> entities = query.getResultList();

        List<Long> kanbanIds = new ArrayList();
        for (UnitEntity entity : entities) {
            UnitTemplateEntity unitTemplate = unittemplateEntityFacadeREST.find(entity.getFkUnitTemplateId());
            entity.setParentId(id);
            entity.setUnitTemplateName(unitTemplate.getUnitTemplateName());

            List<Long> unitKanbanIds = getUnitKanbanIdList(entity.getUnitId());
            if (!unitKanbanIds.isEmpty()) {
                entity.setKanbanIds(unitKanbanIds);
                kanbanIds.addAll(unitKanbanIds);
            }
        }

        List<UnitKanbanEntity> unitKanbans;
        if (!kanbanIds.isEmpty()) {
            unitKanbans = PostgreAPI.getKanbans(this.em, kanbanIds);
        } else {
            unitKanbans = new ArrayList();
        }

        boolean isAdd;
        for (UnitEntity entity : entities) {
            isAdd = false;
            if (Objects.isNull(entity.getKanbanIds())) {
                // カンバンなしのユニットは未完了扱いにする。
                entity.setIsCompleted(false);
            } else {
                entity.setIsCompleted(true);
                for (Long kanbanId : entity.getKanbanIds()) {
                    long notCompNum = unitKanbans.stream().filter(p -> kanbanId.equals(p.getKanbanId()) && !KanbanStatusEnum.COMPLETION.equals(p.getKanbanStatus())).count();
                    if (notCompNum > 0) {
                        entity.setIsCompleted(false);// 未完了
                        isAdd = true;
                        break;
                    }
                }
            }

            if (isAll || isAdd) {
                result.add(entity);
            }
        }

        return result;
    }

    /**
     * 指定されたユニットのカンバンID一覧を取得する。
     *
     * @param unitId ユニットID
     * @return カンバンID一覧
     */
    private List<Long> getUnitKanbanIdList(Long unitId) {
        logger.info("getUnitKanbanIdList: unitId={}", unitId);
        List<Long> result = new ArrayList();

        // 指定されたユニット直属の子ユニットID一覧を取得する。
        TypedQuery<Long> query1 = em.createNamedQuery("ConUnitAssociateEntity.findChildUnitId", Long.class);
        query1.setParameter("fkParentUnitId", unitId);
        List<Long> childUnitIds = query1.getResultList();
        for (Long childUnitId : childUnitIds) {
            // 子ユニットのカンバンID一覧を取得する。
            List<Long> childKanbanIds = getUnitKanbanIdList(childUnitId);
            if (!childKanbanIds.isEmpty()) {
                // 結果に追加する。
                result.addAll(childKanbanIds);
            }
        }

        // 指定されたユニット直属のカンバンID一覧を取得する。
        TypedQuery<Long> query2 = em.createNamedQuery("ConUnitAssociateEntity.findUnitKanbanId", Long.class);
        query2.setParameter("fkParentUnitId", unitId);
        List<Long> kanbanIds = query2.getResultList();
        if (Objects.nonNull(kanbanIds) && !kanbanIds.isEmpty()) {
            // 結果に追加する。
            result.addAll(kanbanIds);
        }

        return result;
    }

    /**
     * 指定したユニット階層名のユニット階層を検索する
     *
     * @param id 親階層のID
     * @param from 検索開始範囲
     * @param to 検索終了範囲
     * @return 取得した子階層のリスト
     */
    private List<TreeUnitHierarchyEntity> findChild(Long id, Long userId, Integer from, Integer to) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT DISTINCT(t) FROM TreeUnitHierarchyEntity t");
        if(Objects.nonNull(userId)) {
            sb.append(" LEFT JOIN AccessHierarchyFujiEntity a ON a.typeId = :type AND t.treeUnitHierarchyEntityPK.childId = a.fkHierarchyId");
            sb.append(" WHERE t.treeUnitHierarchyEntityPK.parentId = :parentId");
            sb.append(" AND (a.fkOrganizationId IS NULL OR a.fkOrganizationId IN :ancestors)");
        } else {
            sb.append(" WHERE t.treeUnitHierarchyEntityPK.parentId = :parentId");
        }
        Query query = em.createQuery(sb.toString());
        query.setParameter("parentId", id);
        if(Objects.nonNull(userId)) {
            query.setParameter("type", AccessHierarchyFujiTypeEnum.UnitHierarchy);
            query.setParameter("ancestors", organizationFacade.findAncesors(userId));
        }
        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            query.setMaxResults(to - from + 1);
            query.setFirstResult(from);
        }
        return query.getResultList();
    }

    /**
     * 指定したユニット階層名の親階層を検索する
     *
     * @param id 親階層のID
     * @return 取得した取得した親階層
     */
    private TreeUnitHierarchyEntity findParent(Long id) {
        TypedQuery<TreeUnitHierarchyEntity> query = em.createNamedQuery("TreeUnitHierarchyEntity.findByChildId", TreeUnitHierarchyEntity.class);
        query.setParameter("childId", id);
        query.setMaxResults(1);
        TreeUnitHierarchyEntity hierarchy = null;
        try {
            hierarchy = query.getSingleResult();
        } catch (NoResultException ex) {
            //親なしってこと.
        }
        return hierarchy;
    }

    /**
     * 指定した階層情報の削除
     *
     * @param id 階層のID
     */
    private void removeHierarchy(Long id) {
        Query query = em.createNamedQuery("TreeUnitHierarchyEntity.removeByChildId");
        query.setParameter("childId", id);
        query.executeUpdate();
    }

    /**
     * 指定した階層情報の追加
     *
     * @param entity 追加する階層情報
     */
    private void addHierarchy(UnitHierarchyEntity entity) {
        TreeUnitHierarchyEntity hierarchy = new TreeUnitHierarchyEntity(entity.getParentId(), entity.getUnitHierarchyId());
        em.persist(hierarchy);
    }

    private List<Long> findAncestors(Long unitHierarchyId) {
        TypedQuery<TreeUnitHierarchyEntity> query = em.createNamedQuery("TreeUnitHierarchyEntity.findByChildId", TreeUnitHierarchyEntity.class);
        List<Long> parentIdsOfLoginUser = new ArrayList<>();
        if(Objects.nonNull(unitHierarchyId)) {
            Long childId = unitHierarchyId;
            while(childId != 0L) {
                query.setParameter("childId", childId);
                TreeUnitHierarchyEntity parent = query.getSingleResult();
                parentIdsOfLoginUser.add(childId);
                childId = parent.getTreeUnitHierarchyEntityPK().getParentId();
            }
        }
        return parentIdsOfLoginUser;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    protected void setEntityManager(EntityManager em) {
        this.em = em;
    }

    protected void setUnitEntityFacadeREST(UnitEntityFacadeREST rest) {
        this.unitEntityFacadeREST = rest;
    }

    protected void setAuthRest(AccessHierarchyFujiEntityFacadeREST authRest) {
        this.authRest = authRest;
    }
}
