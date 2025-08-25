/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
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
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.master.LabelEntity;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ラベルマスタファサード
 *
 * @author s-heya
 */
@Stateless
@Path("label")
public class LabelEntityFacadeREST extends AbstractFacade<LabelEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     */
    public LabelEntityFacadeREST() {
        super(LabelEntity.class);
    }

    /**
     * ラベルマスタを登録する。
     *
     * @param entity ラベルマスタ
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(LabelEntity entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("add: {}, authId={}", entity, authId);
        try {
            // 重複チェック
            TypedQuery<Long> query = this.em.createNamedQuery("LabelEntity.checkAdd", Long.class);
            query.setParameter("labelName", entity.getLabelName());
            if (query.getSingleResult() > 0) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }
            
            // 排他用バージョン
            entity.setVerInfo(1);

            super.create(entity);
            this.em.flush();

            // 作成した情報を元に、戻り値のURIを作成する。
            URI uri = new URI(new StringBuilder("label/").append(entity.getLabelId()).toString());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * ラベルマスタを更新する。
     *
     * @param entity 理由情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(LabelEntity entity, @QueryParam("authId") Long authId) {
        logger.info("update: {}, authId={}", entity, authId);
        try {
            // 排他用バージョンの確認
            LabelEntity target = super.find(entity.getLabelId());
            if (!target.getVerInfo().equals(entity.getVerInfo())) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.DIFFERENT_VER_INFO)).build();
            }

            // 重複チェック
            TypedQuery<Long> query = this.em.createNamedQuery("LabelEntity.checkUpdate", Long.class);
            query.setParameter("labelName", entity.getLabelName());
            query.setParameter("labelId", entity.getLabelId());
            if (query.getSingleResult() > 0) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }

            // 楽観的ロック
            this.em.lock(target, LockModeType.OPTIMISTIC);

            super.edit(entity);

            return Response.ok().entity(ResponseEntity.success()).build();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * ラベルマスタを削除する。
     *
     * @param id ラベルID
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
            super.remove(super.find(id));
            return Response.ok().entity(ResponseEntity.success()).build();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * ラベルマスタを取得する。
     *
     * @param id ラベルID
     * @param authId 認証ID
     * @return ラベルマスタ
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public LabelEntity find(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        return super.find(id);
    }

    /**
     * ラベルマスタを取得する。
     *
     * @param labelName ラベル名
     * @param authId 認証ID
     * @return ラベルマスタ
     */
    @Lock(LockType.READ)
    @GET
    @Path("name")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public LabelEntity findByName(@QueryParam("name") String labelName, @QueryParam("authId") Long authId) {
        TypedQuery<LabelEntity> query = this.em.createNamedQuery("LabelEntity.findByName", LabelEntity.class);
        query.setParameter("labelName", labelName);
        return query.getSingleResult();
    }

    /**
     * ラベルマスタ一覧を取得する。
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 指定された範囲の理由情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<LabelEntity> findRange(@QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findRange: from={}, to={}, authId={}", from, to, authId);
        TypedQuery<LabelEntity> query = this.em.createNamedQuery("LabelEntity.findAll", LabelEntity.class);
        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            query.setFirstResult(from);
            query.setMaxResults(to - from + 1);
        }
        return query.getResultList();
    }

    /**
     * ラベルマスタの件数を取得する。
     *
     * @param authId 認証ID
     * @return 件数
     */
    @Lock(LockType.READ)
    @GET
    @Path("count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String countAll(@QueryParam("authId") Long authId) {
        logger.info("countAll: authId={}", authId);
        return String.valueOf(super.count());
    }

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
     * EntityManager を設定する。
     * 
     * @param em EntityManager
     */
    protected void setEntityManager(EntityManager em) {
        this.em = em;
    }
}
