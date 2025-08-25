/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import jakarta.ejb.EJB;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.enumerate.AccessHierarchyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.HierarchyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.master.HierarchyEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import static jp.adtekfuji.adfactoryserver.service.AbstractFacade.ADMIN_USER;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 階層情報REST
 *
 * @author nar-nakamura
 */
@Stateless
public class HierarchyEntityFacadeREST extends AbstractFacade<HierarchyEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    @EJB
    private OrganizationEntityFacadeREST organizationFacade;

    @EJB
    private AccessHierarchyEntityFacadeREST authRest;

    private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     */
    public HierarchyEntityFacadeREST() {
        super(HierarchyEntity.class);
    }

    /**
     * 階層マスタを継承しているクラスに変換する。
     *
     * @param <T> 
     * @param destClass 階層マスタを継承しているクラス
     * @param list 階層情報一覧
     * @return 階層マスタを継承しているクラスの階層情報一覧
     * @throws Exception 
     */
    @Lock(LockType.READ)
    public static <T extends HierarchyEntity> List<T> downcastList(Class<T> destClass, List<HierarchyEntity> list) throws Exception {
        List<T> destList = new LinkedList();
        for (HierarchyEntity hierarchy : list) {
            T destReason = hierarchy.downcast(destClass);
            destList.add(destReason);
        }
        return destList;
    }

    /**
     * 指定した階層の子階層情報一覧を取得する。
     * ※．ユーザーIDが指定されている場合、アクセス可能な階層情報を対象とする。
     *
     * @param type 階層種別
     * @param id 工程階層ID
     * @param userId ユーザーID (組織ID)
     * @param from 範囲の先頭
     * @param to 範囲の先頭
     * @param authId 認証ID
     * @return 階層情報一覧
     */
    @Lock(LockType.READ)
    public List<HierarchyEntity> findTreeRange(HierarchyTypeEnum type, Long id, Long userId, Integer from, Integer to, Long authId) {
        logger.info("findTreeRange: type={}, id={}, userId={}, from={}, to={}, authId={}", type, id, userId, from, to, authId);

        // 設備IDの指定がない場合はルートとする。
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
        List<HierarchyEntity> entities = this.findChild(type, id, _userId, from, to);
        for (HierarchyEntity entity : entities) {
            entity.setChildCount(this.countChild(type, entity.getHierarchyId(), _userId));
        }

        return entities;
    }

    /**
     * 指定した階層名の階層情報を取得する。
     *
     * @param type 階層種別
     * @param name 階層名
     * @param userId ユーザーID (組織ID)
     * @param authId 認証ID
     * @return 階層情報
     */
    @Lock(LockType.READ)
    public HierarchyEntity findHierarchyByName(HierarchyTypeEnum type, String name, Long userId, Long authId) {
        logger.info("findHierarchyByName: type={}, name={}, userId={}, authId={}", type, name, userId, authId);

        TypedQuery<HierarchyEntity> query = em.createNamedQuery("HierarchyEntity.findByHierarchyName", HierarchyEntity.class);
        query.setParameter("hierarchyType", type);
        query.setParameter("hierarchyName", name);

        HierarchyEntity entity;
        try {
            entity = query.getSingleResult();
        } catch (NoResultException ex) {
            logger.fatal(ex);
            return new HierarchyEntity();
        }

        // 対象階層からルートまでの階層アクセス権をチェックする。
        boolean isAccessible = this.isHierarchyAccessible(type, entity.getHierarchyId(), userId);
        if (!isAccessible) {
            return new HierarchyEntity();
        }

        return entity;
    }
 
    /**
     * 指定した階層名の階層情報を取得する。
     * 階層へのアクセス権可否は、isHierarchyAccessible()にて確認する必要があります。
     * 
     * @param type 階層種別
     * @param name 階層名
     * @return 階層情報
     */
    @Lock(LockType.READ)
    public HierarchyEntity findHierarchyByName(HierarchyTypeEnum type, String name) {
        logger.info("findHierarchyByName: type={}, name={}", type, name);

        HierarchyEntity entity;
        try {
            TypedQuery<HierarchyEntity> query = em.createNamedQuery("HierarchyEntity.findByHierarchyName", HierarchyEntity.class);
            query.setParameter("hierarchyType", type);
            query.setParameter("hierarchyName", name);
            entity = query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }

        return entity;
    }
    
    /**
     * 指定した階層の子階層情報の件数を取得する。
     * ※．ユーザーIDが指定されている場合、アクセス可能な階層情報を対象とする。
     *
     * @param type 階層種別
     * @param id 階層ID
     * @param userId ユーザーID (組織ID)
     * @param authId 認証ID
     * @return 子階層の個数
     */
    @Lock(LockType.READ)
    public String countTree(HierarchyTypeEnum type, Long id, Long userId, Long authId) {
        logger.info("countTree: type={}, id={}, userId={}, authId={}", type, id, userId, authId);

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
        Long count = this.countChild(type, id, _userId);
        return String.valueOf(count);
    }

    /**
     * 階層情報を登録する。
     *
     * @param entity 階層情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException
     */
    public Response add(HierarchyEntity entity, Long authId) throws URISyntaxException {
        logger.info("add: {}, authId={}", entity, authId);
        try {
            // 階層種別・階層名の重複を確認する。
            TypedQuery<Long> query = em.createNamedQuery("HierarchyEntity.checkAddByHierarchyName", Long.class);
            query.setParameter("hierarchyType", entity.getHierarchyType());
            query.setParameter("hierarchyName", entity.getHierarchyName());
            if (query.getSingleResult() > 0) {
                // 該当するものがあった場合、重複を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }

            // 階層情報を登録する。
            super.create(entity);
            em.flush();
            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 階層情報を更新する。
     *
     * @param entity 階層情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    public Response update(HierarchyEntity entity, Long authId) {
        logger.info("update: {}, authId={}", entity, authId);
        try {
            // 排他用バージョンを確認する。
            HierarchyEntity target = super.find(entity.getHierarchyId());
            if (!target.getVerInfo().equals(entity.getVerInfo())) {
                // 現在の排他用バージョンと異なる場合、排他用バージョンが異なる事を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.DIFFERENT_VER_INFO)).build();
            }

            // 自身は親階層に指定できない。
            if (Objects.nonNull(entity.getParentHierarchyId()) && Objects.equals(entity.getHierarchyId(), entity.getParentHierarchyId())) {
                // 移動不可能な階層
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.UNMOVABLE_HIERARCHY)).build();
            }

            // 階層種別・階層名の重複を確認する。
            TypedQuery<Long> query = this.em.createNamedQuery("HierarchyEntity.checkUpdateByHierarchyName", Long.class);
            query.setParameter("hierarchyType", entity.getHierarchyType());
            query.setParameter("hierarchyName", entity.getHierarchyName());
            query.setParameter("hierarchyId", entity.getHierarchyId());
            if (query.getSingleResult() > 0) {
                // 該当するものがあった場合、重複を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }

            // 自身の子以降の階層への移動にならないか確認する。
            if (!this.isMoveableHierarchy(entity.getHierarchyId(), entity.getParentHierarchyId())) {
                // 移動不可能な階層
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.UNMOVABLE_HIERARCHY)).build();
            }

            // 楽観的ロックをかける。
            this.em.lock(target, LockModeType.OPTIMISTIC);

            // 階層情報を更新する。
            super.edit(entity);
            em.flush();

            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 指定した階層IDの階層情報を削除する。
     *
     * @param type 階層種別
     * @param id 階層ID
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    public Response remove(HierarchyTypeEnum type, Long id, Long authId) {
        logger.info("remove: type={}, id={}, authId={}", type, id, authId);
        try {
            if (Objects.isNull(type)) {
            // 階層IDを指定して、階層種別を取得する。
                type = this.getHierarchyType(id);
            }

            AccessHierarchyTypeEnum accessHierarchyType = this.getAccessHierarchyType(type);

            // 指定された階層に、子階層がある場合は削除できない。
            if (this.countChild(type, id, null) > 0) {
                logger.info("not remove at exist child hierarchy:{}", id);
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.EXIST_HIERARCHY_DELETE)).build();
            }

            // 階層IDを指定して、階層関連付け情報の件数を取得する。
            TypedQuery<Long> query = this.em.createNamedQuery("ConHierarchyEntity.countChild", Long.class);
            query.setParameter("hierarchyId", id);
            Long num = query.getSingleResult();
            if (num > 0) {
                logger.info("not remove at exist child data:{}", id);
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.EXIST_CHILD_DELETE)).build();
            }

            // 階層アクセス権情報を削除する。
            this.authRest.remove(accessHierarchyType, id);

            // 階層情報を削除する。
            super.remove(super.find(id));
            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 階層IDを指定して、階層情報を取得する。
     *
     * @param id 階層ID
     * @return 階層情報
     */
    @Lock(LockType.READ)
    public HierarchyEntity find(Long id) {
        return super.find(id);
    }

    /**
     * 指定した階層の子階層情報一覧を取得する。
     * ※．ユーザーIDが指定されている場合、アクセス可能な階層情報を対象とする。
     *
     * @param type 階層種別
     * @param id 階層ID
     * @param userId ユーザーID (組織ID)
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @return 子階層情報一覧
     */
    @Lock(LockType.READ)
    private List<HierarchyEntity> findChild(HierarchyTypeEnum type, Long id, Long userId, Integer from, Integer to) {
        TypedQuery<HierarchyEntity> query;

        if(Objects.isNull(userId)) {
            // 指定した階層IDの子階層情報一覧を取得する。
            query = this.em.createNamedQuery("HierarchyEntity.findChild", HierarchyEntity.class);
        } else {
            // ユーザーIDが指定されている場合、アクセス可能な階層情報を対象とする。

            // ユーザーのルートまでの親階層ID一覧を取得する。
            List<Long> organizationIds = this.organizationFacade.findAncestors(userId);

            AccessHierarchyTypeEnum accessHierarchyType = this.getAccessHierarchyType(type);

            // 指定した階層IDの子階層情報一覧を取得する。(指定ユーザーがアクセス可能な階層のみ)
            query = this.em.createNamedQuery("HierarchyEntity.findChildByUserId", HierarchyEntity.class);
            query.setParameter("type", accessHierarchyType);
            query.setParameter("ancestors", organizationIds);
        }
        query.setParameter("hierarchyType", type);
        query.setParameter("hierarchyId", id);

        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            query.setMaxResults(to - from + 1);
            query.setFirstResult(from);
        }
        return query.getResultList();
    }

    /**
     * 指定した階層の子階層の件数を取得する。
     * ※．ユーザーIDが指定されている場合、アクセス可能な階層情報を対象とする。
     *
     * @param type 階層種別
     * @param hierarchyId 階層ID
     * @param userId ユーザーID (組織ID)
     * @return 子階層の件数
     */
    @Lock(LockType.READ)
    private long countChild(HierarchyTypeEnum type, Long hierarchyId, Long userId) {
        TypedQuery<Long> query;

        if(Objects.isNull(userId)) {
            // 指定した階層IDの子階層情報の件数を取得する。
            query = this.em.createNamedQuery("HierarchyEntity.countChild", Long.class);
        } else {
            // ユーザーIDが指定されている場合、アクセス可能な階層情報を対象とする。

            // ユーザーのルートまでの親階層ID一覧を取得する。
            List<Long> organizationIds = this.organizationFacade.findAncestors(userId);

            AccessHierarchyTypeEnum accessHierarchyType = this.getAccessHierarchyType(type);

            // 指定した階層IDの子階層情報の件数を取得する。(指定ユーザーがアクセス可能な階層のみ)
            query = this.em.createNamedQuery("HierarchyEntity.countChildByUserId", Long.class);
            query.setParameter("type", accessHierarchyType);
            query.setParameter("ancestors", organizationIds);
        }
        query.setParameter("hierarchyType", type);
        query.setParameter("hierarchyId", hierarchyId);

        return query.getSingleResult();
    }

    /**
     * 階層IDを指定して、階層種別を取得する。
     *
     * @param hierarchyId 階層ID
     * @return 階層種別
     */
    @Lock(LockType.READ)
    private HierarchyTypeEnum getHierarchyType(Long hierarchyId) {
        // 階層IDを指定して、階層種別を取得する。
        TypedQuery<HierarchyTypeEnum> queryType = em.createNamedQuery("HierarchyEntity.getTypeById", HierarchyTypeEnum.class);
        queryType.setParameter("hierarchyId", hierarchyId);

        return queryType.getSingleResult();
    }

    /**
     * 指定された階層に、ユーザーがアクセス可能か取得する。
     *
     * @param type 階層種別
     * @param id 階層ID
     * @param userId ユーザーID
     * @return アクセス (true: アクセス可能, false: アクセス不可)
     */
    @Lock(LockType.READ)
    public boolean isHierarchyAccessible(HierarchyTypeEnum type, Long id, Long userId) {
        // ユーザーIDが未指定の場合、アクセス可能。
        if (Objects.isNull(userId)) {
            return true;
        }
        // admin の場合、すべての階層にアクセスできるようにする
        OrganizationEntity admin = this.organizationFacade.findByName(ADMIN_USER, null, null);
        if (Objects.equals(userId, admin.getOrganizationId())) {
            return true;
        }
        
        AccessHierarchyTypeEnum accessHierarchyType = this.getAccessHierarchyType(type);

        // 階層のアクセス権をチェックする。
        boolean isAccessible = this.authRest.isAccessible(accessHierarchyType, id, userId);
        if (!isAccessible) {
            return false;
        }

        // 階層IDを指定して、親階層IDを取得する。
        TypedQuery<Long> query = this.em.createNamedQuery("HierarchyEntity.findParentId", Long.class);
        query.setParameter("hierarchyId", id);

        Long parentId;
        try {
            parentId = query.getSingleResult();
        } catch (Exception ex) {
            parentId = null;
        }

        // 親階層が存在する場合、親階層の階層アクセス権をチェックする。
        if (Objects.nonNull(parentId) && parentId > 0){
            isAccessible = this.isHierarchyAccessible(type, parentId, userId);
        }

        return isAccessible;
    }

    /**
     * 階層種別からアクセス権の階層種別を取得する。
     *
     * @param type 階層種別
     * @return アクセス権の階層種別
     */
    @Lock(LockType.READ)
    private AccessHierarchyTypeEnum getAccessHierarchyType(HierarchyTypeEnum type) {
        switch (type) {
            case WORK:
                return AccessHierarchyTypeEnum.WorkHierarchy;
            case WORKFLOW:
                return AccessHierarchyTypeEnum.WorkflowHierarchy;
            default:
                return null;
        }
    }

    /**
     * 移動可能な階層かチェックする。
     *
     * @param id 階層ID
     * @param parentId 移動先の階層ID
     * @return 移動可能な階層か (true:可能, false:不可)
     */
    private boolean isMoveableHierarchy(Long id, Long parentId) {
        // ルートへの移動は可能。
        if (parentId.equals(0L)) {
            return true;
        }

        // 階層IDを指定して、親階層IDを取得する。
        Long nextParentId = this.findParentId(parentId);

        if (Objects.isNull(nextParentId)) {
            // ルートまで繋がっていないので移動不可。
            return false;
        } else if (nextParentId.equals(id)) {
            // ループするため移動不可。
            return false;
        } else if (nextParentId.equals(0L)) {
            // ルートまで繋がっているので移動可能。
            return true;
        } else {
            // さらに親階層をチェックする。
            return this.isMoveableHierarchy(id, nextParentId);
        }
    }

    /**
     * 階層IDを指定して、親階層IDを取得する。
     *
     * @param id 階層ID
     * @return 親階層ID
     */
    private Long findParentId(Long id) {
        try {
            // 階層IDを指定して、親階層IDを取得する。
            TypedQuery<Long> query = this.em.createNamedQuery("HierarchyEntity.findParentId", Long.class);
            query.setParameter("hierarchyId", id);

            return query.getSingleResult();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public void setAuthRest(AccessHierarchyEntityFacadeREST authRest) {
        this.authRest = authRest;
    }

    public void setOrganizationFacade(OrganizationEntityFacadeREST organizationFacade) { this.organizationFacade = organizationFacade; }
}
