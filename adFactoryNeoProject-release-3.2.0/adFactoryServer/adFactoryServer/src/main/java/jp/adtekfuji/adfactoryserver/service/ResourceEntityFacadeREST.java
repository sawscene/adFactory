/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.enumerate.ResourceTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.resource.ResourceEntity;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * リソーステーブル REST
 */
@Singleton
@Path("resource")
public class ResourceEntityFacadeREST extends AbstractFacade<ResourceEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     */
    public ResourceEntityFacadeREST() {
        super(ResourceEntity.class);
    }

    /**
     * リソースを検索する。
     *
     * @param id id
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ResourceEntity find(@PathParam("id") Long id) {
        logger.info("find: {}", id);
        try {
            return super.find(id);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    @Lock(LockType.READ)
    @GET
    @Path("typekey")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ResourceEntity findByTypeKey(@QueryParam("type") ResourceTypeEnum type, @QueryParam("key") String key) {
        logger.info("findByTypeKey: {} {}", type, key);
        try {
            Query query = this.em.createNamedQuery("ResourceEntity.findByTypeKey");
            query.setParameter("resourceType", type);
            query.setParameter("resourceKey", key);
            List<ResourceEntity> entities = (List<ResourceEntity>) query.getResultList();

            return entities.isEmpty() ? null : entities.get(entities.size() - 1);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * リソースを登録する。
     *
     * @param entity リソース
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @ExecutionTimeLogging
    @PUT
    @Path("add")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public Response add(ResourceEntity entity) {
        logger.info("add: {}", entity);
        try {
            super.create(entity);
            this.em.flush();

            // 作成した情報を元に、戻り値のURIを作成する。
            URI uri = new URI(new StringBuilder("resource/").append(entity.getResourceId()).toString());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * リソースを更新する。
     *
     * @param entity 更新する情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(ResourceEntity entity, @QueryParam("authId") Long authId) {
        logger.info("update: {}, authId={}", entity, authId);
        try {
            // 更新対象を確認する。
            ResourceEntity target = super.find(entity.getResourceId());
            if (Objects.isNull(target)) {
                // 該当なし
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_UPDATE)).build();
            }

            // 更新
            super.edit(entity);

            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * リソースを削除する。
     *
     * @param id リソースID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @ExecutionTimeLogging
    public Response remove(Long id) {
        logger.info("remove:{}", id);
        try {
            if (Objects.nonNull(id)) {
                // 削除対象を確認する。
                ResourceEntity target = super.find(id);
                if (Objects.nonNull(target)) {
                    // 組織情報を削除する。
                    super.remove(super.find(id));
                }
            }

            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * リソースを更新する。
     *
     * @param id リソースID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @ExecutionTimeLogging
    public Response copy(Long id) {
        ResourceEntity entity = super.find(id);
        if (Objects.isNull(entity)) {
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_UPDATE)).build();
        }

        ResourceEntity newEntity = new ResourceEntity(entity);
        return this.add(newEntity);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }
}
