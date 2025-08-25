/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.util.ArrayList;
import java.util.List;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.enumerate.LightPatternEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;
import jp.adtekfuji.adfactoryserver.entity.master.DisplayedStatusEntity;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ステータス表示情報REST
 *
 * @author ke.yokoi
 */
@Stateless
@Path("visual-style")
public class DisplayedStatusEntityFacadeREST extends AbstractFacade<DisplayedStatusEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;
    private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     */
    public DisplayedStatusEntityFacadeREST() {
        super(DisplayedStatusEntity.class);
    }

    /**
     * ステータス表示情報を更新する。
     *
     * @param entity ステータス表示情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(DisplayedStatusEntity entity, @QueryParam("authId") Long authId) {
        logger.info("update: {}, authId={}", entity, authId);
        try {
            // 排他用バージョンを確認する。
            DisplayedStatusEntity target = super.find(entity.getStatusId());
            if (!target.getVerInfo().equals(entity.getVerInfo())) {
                // 現在の排他用バージョンと異なる場合、排他用バージョンが異なる事を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.DIFFERENT_VER_INFO)).build();
            }

            // 楽観的ロックをかける。
            this.em.lock(target, LockModeType.OPTIMISTIC);

            // ステータス表示情報を更新する。
            super.edit(entity);

            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * ステータス表示情報を取得する。
     *
     * @param id ステータス表示ID
     * @param authId 認証ID
     * @return ステータス表示情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public DisplayedStatusEntity find(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("find: id={}, authId={}", id, authId);
        this.createDisplayedStatusTable();
        return super.find(id);
    }

    /**
     * ステータス表示情報一覧を取得する。
     *
     * @param authId 認証ID
     * @return ステータス表示情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<DisplayedStatusEntity> findAll(@QueryParam("authId") Long authId) {
        logger.info("findAll: authId={}", authId);
        this.createDisplayedStatusTable();
        return super.findAll();
    }

    /**
     * ステータス表示情報一覧を範囲指定して取得する。
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return　指定された範囲のステータス表示情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<DisplayedStatusEntity> findRange(@QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findRange: from={}, to={}, authId={}", from, to, authId);
        this.createDisplayedStatusTable();
        return super.findRange(from, to);
    }

    /**
     * ステータス表示情報の件数を取得する。
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
        this.createDisplayedStatusTable();
        return String.valueOf(super.count());
    }

    /**
     * ステータス表示情報の初期データを作成する。
     *
     * @param authId 認証ID
     */
    private void createDisplayedStatusTable() {
        List<DisplayedStatusEntity> statuses = new ArrayList<>();
        statuses.add(new DisplayedStatusEntity(StatusPatternEnum.PLAN_NORMAL, "#000000", "#FFFFFF", LightPatternEnum.LIGHTING));
        statuses.add(new DisplayedStatusEntity(StatusPatternEnum.PLAN_DELAYSTART, "#000000", "#FFFFFF", LightPatternEnum.LIGHTING));
        statuses.add(new DisplayedStatusEntity(StatusPatternEnum.WORK_NORMAL, "#000000", "#7FFFFF", LightPatternEnum.LIGHTING));
        statuses.add(new DisplayedStatusEntity(StatusPatternEnum.WORK_DELAYSTART, "#000000", "#FFFF66", LightPatternEnum.LIGHTING));
        statuses.add(new DisplayedStatusEntity(StatusPatternEnum.WORK_DELAYCOMP, "#FF0000", "#FFFF66", LightPatternEnum.LIGHTING));
        statuses.add(new DisplayedStatusEntity(StatusPatternEnum.SUSPEND_NORMAL, "#FFFFFF", "#FF9832", LightPatternEnum.LIGHTING));
        statuses.add(new DisplayedStatusEntity(StatusPatternEnum.INTERRUPT_NORMAL, "#FFFFFF", "#FF0B0B", LightPatternEnum.LIGHTING));
        statuses.add(new DisplayedStatusEntity(StatusPatternEnum.COMP_NORMAL, "#FFFFFF", "#3F7F7F", LightPatternEnum.LIGHTING));
        statuses.add(new DisplayedStatusEntity(StatusPatternEnum.COMP_DELAYCOMP, "#000000", "#99993C", LightPatternEnum.LIGHTING));
        statuses.add(new DisplayedStatusEntity(StatusPatternEnum.BREAK_TIME, "#000000", "#7FFF81", LightPatternEnum.LIGHTING));
        statuses.add(new DisplayedStatusEntity(StatusPatternEnum.CALLING, "#FFFFFF", "#FF00D4", LightPatternEnum.LIGHTING));
        for (DisplayedStatusEntity status : statuses) {
            TypedQuery<DisplayedStatusEntity> query = this.em.createNamedQuery("DisplayedStatusEntity.findByStatusName", DisplayedStatusEntity.class);
            query.setParameter("statusName", status.getStatusName());
            try {
                query.getSingleResult();
            } catch (NoResultException ex) {
                super.create(status);
            }
        }
        this.em.flush();
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    protected void setEntityManager(EntityManager em) {
        this.em = em;
    }
}
