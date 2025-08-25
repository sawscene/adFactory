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
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
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
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.master.BreaktimeEntity;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 休憩時間情報REST：休憩時間情報を操作するためのクラス
 *
 * @author ke.yokoi
 */
@Stateless
@Path("break-time")
public class BreaktimeEntityFacadeREST extends AbstractFacade<BreaktimeEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     */
    public BreaktimeEntityFacadeREST() {
        super(BreaktimeEntity.class);
    }

    // 認証IDはv2では未使用。(将来、ログ機能で使用予定)
    /**
     * 休憩時間情報を登録する。
     *
     * @param entity 休憩時間情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException 
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(BreaktimeEntity entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("add: {}, authId={}", entity, authId);
        try {
            // 休憩名称の重複を確認する。
            TypedQuery<Long> query = this.em.createNamedQuery("BreaktimeEntity.checkAddByName", Long.class);
            query.setParameter("breaktimeName", entity.getBreaktimeName());
            if (query.getSingleResult() > 0) {
                // 該当するものがあった場合、重複を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }

            // 休憩時間情報を登録する。
            super.create(entity);
            this.em.flush();
            // 作成した情報を元に、戻り値のURIを作成する。
            URI uri = new URI(new StringBuilder("breaktime/").append(entity.getBreaktimeId()).toString());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    // 認証IDはv2では未使用。(将来、ログ機能で使用予定)
    /**
     * 休憩時間情報を更新する。
     *
     * @param entity 休憩時間情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(BreaktimeEntity entity, @QueryParam("authId") Long authId) {
        logger.info("update: {}, authId={}", entity, authId);
        try {
            // 排他用バージョンを確認する。
            BreaktimeEntity target = super.find(entity.getBreaktimeId());
            if (!target.getVerInfo().equals(entity.getVerInfo())) {
                // 現在の排他用バージョンと異なる場合、排他用バージョンが異なる事を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.DIFFERENT_VER_INFO)).build();
            }

            // 休憩名称の重複を確認する。
            TypedQuery<Long> query = this.em.createNamedQuery("BreaktimeEntity.checkUpdateByName", Long.class);
            query.setParameter("breaktimeName", entity.getBreaktimeName());
            query.setParameter("breaktimeId", entity.getBreaktimeId());
            if (query.getSingleResult() > 0) {
                // 該当するものがあった場合、重複を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }

            // 楽観的ロックをかける。
            this.em.lock(target, LockModeType.OPTIMISTIC);

            // 休憩時間情報を更新する。
            super.edit(entity);

            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    // 認証IDはv2では未使用。(将来、ログ機能で使用予定)
    /**
     * 指定した休憩時間IDの休憩時間情報を削除する。
     *
     * @param id 休憩時間ID
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
            // 組織・休憩関連付けテーブルの削除する。
            Query query = this.em.createNamedQuery("ConOrganizationBreaktimeEntity.removeByBreaktimeId");
            query.setParameter("breaktimeId", id);
            query.executeUpdate();

            // 休憩時間情報を削除する。
            super.remove(super.find(id));
            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    // 認証IDはv2では未使用。(将来、ログ機能で使用予定)
    /**
     * 指定した休憩時間IDの休憩時間情報を取得する。
     *
     * @param id 休憩時間ID
     * @param authId 認証ID
     * @return 休憩時間情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public BreaktimeEntity find(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        return super.find(id);
    }

    // 認証IDはv2では未使用。(将来、ログ機能で使用予定)
    /**
     * 休憩時間情報一覧を取得する。
     *
     * @param authId 認証ID
     * @return 休憩時間情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<BreaktimeEntity> findAll(@QueryParam("authId") Long authId) {
        logger.info("findAll: authId={}", authId);
        return super.findAll();
    }

    // 認証IDはv2では未使用。(将来、ログ機能で使用予定)
    /**
     * 休憩時間情報一覧を範囲指定して取得する。
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 指定された範囲の休憩時間情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<BreaktimeEntity> findRange(@QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findAll: from={}, to={}, authId={}", from, to, authId);
        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            return super.findRange(from, to);
        } else {
            return super.findAll();
        }
    }

    /**
     * 休憩時間情報の件数を取得する。
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
     * 指定した休憩IDの休憩時間情報を取得する。
     *
     * @param breaktimeIds 休憩ID一覧
     * @return 休憩時間情報一覧
     */
    @Lock(LockType.READ)
    public List<BreaktimeEntity> find(List<Long> breaktimeIds) {
        if (breaktimeIds.isEmpty()) {
            return new ArrayList<>();
        }
        Query query = this.em.createNamedQuery("BreaktimeEntity.findByBreaktimeId", BreaktimeEntity.class);
        query.setParameter("breaktimeIds", breaktimeIds);
        return query.getResultList();
    }

    /**
     * 指定した組織の休憩時間情報を取得する。
     * 
     * @param organizationId 組織ID
     * @return 休憩時間情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("organizaion")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<BreaktimeEntity> findByOrganizationId(Long organizationId) {
        if(Objects.isNull(organizationId)) {
            return new ArrayList<>();
        }
        Query query = this.em.createNamedQuery("BreaktimeEntity.findByOrganizationId", BreaktimeEntity.class);
        query.setParameter("organizationId", organizationId);
        return query.getResultList();
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    protected void setEntityManager(EntityManager em) {
        this.em = em;
    }
}
