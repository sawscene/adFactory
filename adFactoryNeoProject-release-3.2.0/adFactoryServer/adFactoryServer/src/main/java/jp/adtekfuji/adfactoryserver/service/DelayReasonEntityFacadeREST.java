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
import jakarta.ejb.EJB;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Stateless;
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
import jp.adtekfuji.adFactory.enumerate.LightPatternEnum;
import jp.adtekfuji.adFactory.enumerate.ReasonTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.master.DelayReasonEntity;
import jp.adtekfuji.adfactoryserver.entity.master.ReasonMasterEntity;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 遅延理由情報REST：遅延理由情報を操作するためのクラス
 *
 * @author ke.yokoi
 */
@Stateless
@Path("delay-reason")
public class DelayReasonEntityFacadeREST {

    @EJB
    private ReasonMasterEntityFacadeREST reasonRest;

    private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     */
    public DelayReasonEntityFacadeREST() {
    }

    /**
     * 遅延理由情報を登録する。
     *
     * @param entity 遅延理由情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException 
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(DelayReasonEntity entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("add: {}, authId={}", entity, authId);
        try {
            ReasonMasterEntity reason = entity.upcast();
            Response response = this.reasonRest.add(reason, authId);

            // 成功の場合はdelay-reasonの戻り値のURIを作成、失敗時はResponseをそのまま返す。
            ResponseEntity result= (ResponseEntity) response.getEntity();
            if (result.isSuccess()) {
                entity.setReasonId(reason.getReasonId());

                // 作成した情報を元に、戻り値のURIを作成する
                URI uri = new URI(new StringBuilder("delay-reason/").append(entity.getReasonId()).toString());
                return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
            } else {
                return response;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 遅延理由情報を更新する。
     *
     * @param entity 遅延理由情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(DelayReasonEntity entity, @QueryParam("authId") Long authId) {
        logger.info("update: {}, authId={}", entity, authId);
        try {
            return this.reasonRest.update(entity.upcast(), authId);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 指定したIDの遅延理由情報を削除する。
     *
     * @param id 遅延理由ID
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
            return this.reasonRest.removeByType(ReasonTypeEnum.TYPE_DELAY, id, authId);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 指定したIDの遅延理由情報を取得する。
     *
     * @param id 遅延理由ID
     * @param authId 認証ID
     * @return 遅延理由情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public DelayReasonEntity find(@PathParam("id") Long id, @QueryParam("authId") Long authId) throws Exception {
        this.createDelayReasonTable(authId);

        ReasonMasterEntity reason = this.reasonRest.findByType(ReasonTypeEnum.TYPE_DELAY, id, authId);
        if (Objects.isNull(reason)) {
            return null;
        } else {
            return reason.downcast(DelayReasonEntity.class);
        }
    }

    /**
     * 遅延理由情報一覧を取得する。
     *
     * @param authId 認証ID
     * @return 遅延理由情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<DelayReasonEntity> findAll(@QueryParam("authId") Long authId) throws Exception {
        logger.info("findAll: authId={}", authId);
        this.createDelayReasonTable(authId);

        List<ReasonMasterEntity> reasons = this.reasonRest.findByType(ReasonTypeEnum.TYPE_DELAY, null, null, authId);
        List<DelayReasonEntity> delayReasons = ReasonMasterEntityFacadeREST.downcastList(DelayReasonEntity.class, reasons);
        return delayReasons;
    }

    /**
     * 遅延理由情報一覧を範囲指定して取得する。
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 指定された範囲の遅延理由情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<DelayReasonEntity> findRange(@QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) throws Exception {
        logger.info("findRange: from={}, to={}, authId={}", from, to, authId);
        this.createDelayReasonTable(authId);

        List<ReasonMasterEntity> reasons = this.reasonRest.findByType(ReasonTypeEnum.TYPE_DELAY, from, to, authId);
        List<DelayReasonEntity> delayReasons = ReasonMasterEntityFacadeREST.downcastList(DelayReasonEntity.class, reasons);
        return delayReasons;
    }

    /**
     * 遅延理由情報の件数を取得する。
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
        this.createDelayReasonTable(authId);

        return this.reasonRest.countByType(ReasonTypeEnum.TYPE_DELAY, authId);
    }

    /**
     * 遅延理由の初期データを作成する。
     *
     * @param authId 認証ID
     */
    private void createDelayReasonTable(Long authId) {
        if (Integer.parseInt(this.reasonRest.countByType(ReasonTypeEnum.TYPE_DELAY, authId)) != 0) {
            return;
        }
        logger.info("create default DelayReason.");
        try {
            this.reasonRest.add(new ReasonMasterEntity(ReasonTypeEnum.TYPE_DELAY, "other", "#000000", "#FF8000", LightPatternEnum.LIGHTING), authId);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    public void setReasonRest(ReasonMasterEntityFacadeREST reasonRest) {
        this.reasonRest = reasonRest;
    }
}
