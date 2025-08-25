package jp.adtekfuji.adfactoryserver.service;


import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.ledger.LedgerHierarchyEntity;
import jp.adtekfuji.adfactoryserver.entity.master.HierarchyEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkHierarchyEntity;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


@Singleton
@Path("ledger/tree")
public class LedgerHierarchyEntityFacadeREST extends AbstractFacade<LedgerHierarchyEntity> {
    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    @EJB
    private LedgerEntityFacadeREST ledgerEntityFacadeREST;

    public LedgerHierarchyEntityFacadeREST() {
        super(LedgerHierarchyEntity.class);
    }


    private final Logger logger = LogManager.getLogger();
    /**
     * EntityManager を取得する。
     *
     * @return EntityManager
     */
    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    /**
     * クラスを初期化する。
     */
    @PostConstruct
    public void initialize() {

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
    public LedgerHierarchyEntity find(@PathParam("id") Long id, @QueryParam("authId") Long authId) throws Exception {
        logger.info("find: id={}, authId={}", id, authId);
        return this.find(id);
    }


    /**
     * 親IDからの要素を取得
     * @param ids
     * @param authId
     * @return
     */
    @Lock(LockType.READ)
    @GET
    @Path("children")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<LedgerHierarchyEntity> findChild(@QueryParam("id") final List<Long> ids, @QueryParam("authId") Long authId) {
        logger.info("find: id={}, authId={}", ids, authId);
        if (Objects.isNull(ids) || ids.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            TypedQuery<LedgerHierarchyEntity> query = this.em.createNamedQuery("LedgerHierarchyEntity.findByParentIds", LedgerHierarchyEntity.class);
            query.setParameter("parentIds", ids);
            return query.getResultList();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 階層情報を登録する。
     *
     * @param entity 工程順階層情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(LedgerHierarchyEntity entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("add: {}, authId={}", entity, authId);
        try {

            // 階層情報を登録する。
            super.create(entity);
            em.flush();

            // 作成した情報を元に、戻り値のURIを作成する
            URI uri = new URI(new StringBuilder("ledger/tree/").append(entity.getHierarchyId()).toString());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } catch (Exception ex) {
            logger.fatal(ex);
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
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response remove(@QueryParam("id") final List<Long> ids, @QueryParam("authId") Long authId) {
        logger.info("remove: id={}, authId={}", ids, authId);
        try {
            // 指定された階層に、子階層がある場合は削除できない。
            if (!ledgerEntityFacadeREST.findChild(ids, authId).isEmpty() || !findChild(ids, authId).isEmpty()) {
                logger.info("not remove at exist child hierarchy:{}", ids);
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.EXIST_HIERARCHY_DELETE)).build();
            }

            // 階層情報を削除する。
            ids.stream()
                    .map(super::find)
                    .filter(Objects::nonNull)
                    .forEach(super::remove);
            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex);
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
    public Response update(LedgerHierarchyEntity entity, @QueryParam("authId") Long authId) {
        logger.info("update: {}, authId={}", entity, authId);
        try {
            // 自身は親階層に指定できない。
            if (Objects.isNull(entity.getParentHierarchyId()) || Objects.isNull(entity.getHierarchyId()) || Objects.equals(entity.getHierarchyId(), entity.getParentHierarchyId())) {
                // 移動不可能な階層
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.UNMOVABLE_HIERARCHY)).build();
            }

            // 排他用バージョンを確認する。
            LedgerHierarchyEntity target = super.find(entity.getHierarchyId());
            if (!Objects.equals(target.getVerInfo(), entity.getVerInfo())) {
                // 現在の排他用バージョンと異なる場合、排他用バージョンが異なる事を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.DIFFERENT_VER_INFO)).build();
            }

            TypedQuery<Boolean> query2 = this.em.createNamedQuery("LedgerHierarchyEntity.checkMovable", Boolean.class);
            query2.setParameter(1, entity.getHierarchyId());// 工程ID
            query2.setParameter(2, entity.getParentHierarchyId());

            Boolean isMovable = query2.getSingleResult();

            // 自身の子以降の階層への移動にならないか確認する。
            if (!isMovable) {
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
}
