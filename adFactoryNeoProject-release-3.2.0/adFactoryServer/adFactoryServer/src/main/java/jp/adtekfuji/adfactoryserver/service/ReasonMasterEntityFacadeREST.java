/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import jakarta.ejb.EJB;
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
import jp.adtekfuji.adFactory.enumerate.LightPatternEnum;
import jp.adtekfuji.adFactory.enumerate.ReasonTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.master.ReasonCategoryEntity;
import jp.adtekfuji.adfactoryserver.entity.master.ReasonMasterEntity;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 理由情報REST：理由情報を操作するためのクラス
 *
 * @author nar-nakamura
 */
@Stateless
@Path("reason")
public class ReasonMasterEntityFacadeREST extends AbstractFacade<ReasonMasterEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;
    
    @EJB
    private ReasonCategoryEntityFacadeREST reasonCategoryEntityFacadeREST;

    private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     */
    public ReasonMasterEntityFacadeREST() {
        super(ReasonMasterEntity.class);
    }

    /**
     * 理由マスタを継承しているクラスに変換する。
     *
     * @param <T> 
     * @param destClass 理由マスタを継承しているクラス
     * @param list 理由情報一覧
     * @return 理由マスタを継承しているクラスの理由情報一覧
     * @throws Exception 
     */
    @Lock(LockType.READ)
    public static <T extends ReasonMasterEntity> List<T> downcastList(Class<T> destClass, List<ReasonMasterEntity> list) throws Exception {
        List<T> destList = new LinkedList();
        for (ReasonMasterEntity reason : list) {
            T destReason = reason.downcast(destClass);
            destList.add(destReason);
        }
        return destList;
    }

    /**
     * 理由情報を登録する。
     *
     * @param entity 理由情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(ReasonMasterEntity entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("add: {}, authId={}", entity, authId);
        try {
            TypedQuery<Long> query;
            if (Objects.isNull(entity.getReasonCategoryId())) {
                // 理由種別・理由の重複を確認する。
                query = this.em.createNamedQuery("ReasonMasterEntity.checkAddByReason", Long.class);
                query.setParameter("reasonType", entity.getReasonType());
                query.setParameter("reason", entity.getReason());
            } else {
                // 理由種別・理由・理由区分IDの重複を確認する。
                query = this.em.createNamedQuery("ReasonMasterEntity.checkAddByReasonCategoryId", Long.class);
                query.setParameter("reasonType", entity.getReasonType());
                query.setParameter("reason", entity.getReason());
                query.setParameter("reasonCategoryId", entity.getReasonCategoryId()); 
            }

            if (query.getSingleResult() > 0) {
                // 該当するものがあった場合、重複を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }

            // 理由を登録する。
            super.create(entity);
            this.em.flush();
            // 作成した情報を元に、戻り値のURIを作成する。
            URI uri = new URI(new StringBuilder("reason/").append(entity.getReasonId()).toString());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 理由情報を更新する。
     *
     * @param entity 理由情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(ReasonMasterEntity entity, @QueryParam("authId") Long authId) {
        logger.info("update: {}, authId={}", entity, authId);
        try {
            // 排他用バージョンを確認する。
            ReasonMasterEntity target = super.find(entity.getReasonId());
            if (!target.getVerInfo().equals(entity.getVerInfo())) {
                // 現在の排他用バージョンと異なる場合、排他用バージョンが異なる事を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.DIFFERENT_VER_INFO)).build();
            }

            TypedQuery<Long> query;
            if (Objects.isNull(entity.getReasonCategoryId())) {
                // 理由種別・理由の重複を確認する。
                query = this.em.createNamedQuery("ReasonMasterEntity.checkUpdateByReason", Long.class);
                query.setParameter("reasonType", entity.getReasonType());
                query.setParameter("reason", entity.getReason());
                query.setParameter("reasonId", entity.getReasonId());
            } else {
                // 理由種別・理由・理由区分IDの重複を確認する。
                query = this.em.createNamedQuery("ReasonMasterEntity.checkUpdateByReasonCategoryId", Long.class);
                query.setParameter("reasonType", entity.getReasonType());
                query.setParameter("reason", entity.getReason());
                query.setParameter("reasonId", entity.getReasonId());
                query.setParameter("reasonCategoryId", entity.getReasonCategoryId());
            }

            if (query.getSingleResult() > 0) {
                // 該当するものがあった場合、重複を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }

            // 楽観的ロックをかける。
            this.em.lock(target, LockModeType.OPTIMISTIC);

            // 理由情報を更新する。
            super.edit(entity);

            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 指定した理由IDの理由情報を削除する。
     *
     * @param id 理由ID
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
            // 理由情報を削除する。
            super.remove(super.find(id));
            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 指定した理由種別・理由IDの理由情報を削除する。
     *
     * @param reasonType 理由種別
     * @param id 理由ID
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    public Response removeByType(ReasonTypeEnum reasonType, Long id, Long authId) {
        logger.info("removeByType: reasonType={}, id={}, authId={}", reasonType, id, authId);
        try {
            TypedQuery<ReasonMasterEntity> query = this.em.createNamedQuery("ReasonMasterEntity.removeByType", ReasonMasterEntity.class);
            query.setParameter("reasonType", reasonType);
            query.setParameter("reasonId", id);
            query.executeUpdate();
            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 指定した理由IDの理由情報を取得する。
     *
     * @param id 理由ID
     * @param authId 認証ID
     * @return 理由情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ReasonMasterEntity find(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        return super.find(id);
    }

    /**
     * 指定した理由種別・理由IDの理由情報を取得する。
     *
     * @param reasonType 理由種別
     * @param id 理由ID
     * @param authId 認証ID
     * @return 理由情報
     */
    @Lock(LockType.READ)
    public ReasonMasterEntity findByType(ReasonTypeEnum reasonType, Long id, Long authId) {
        try {
            TypedQuery<ReasonMasterEntity> query = this.em.createNamedQuery("ReasonMasterEntity.findByTypeAndReasonId", ReasonMasterEntity.class);
            query.setParameter("reasonType", reasonType);
            query.setParameter("reasonId", id);
            ReasonMasterEntity entity = query.getSingleResult();
            return entity;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 指定した理由種別の理由情報一覧を取得する。
     *
     * @param reasonType 理由種別
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 理由情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("type")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<ReasonMasterEntity> findByType(@QueryParam("type") ReasonTypeEnum reasonType, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findByType: reasonType={}, from={}, to={}, authId={}", reasonType, from, to, authId);
        TypedQuery<ReasonMasterEntity> query = this.em.createNamedQuery("ReasonMasterEntity.findByType", ReasonMasterEntity.class);
        query.setParameter("reasonType", reasonType);
        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            query.setMaxResults(to - from + 1);
            query.setFirstResult(from);
        }
        return query.getResultList();
    }
    
    /**
     * 指定した理由区分IDの理由情報一覧を取得する。
     *
     * @param reasonCategoryIds 理由区分ID
     * @param authId 認証ID
     * @return 理由情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<ReasonMasterEntity> findByCategoryId(@QueryParam("reasonCategoryId") List<Long> reasonCategoryIds, @QueryParam("authId") Long authId) {
        logger.info("findByCategoryId: reasonCategoryId={}, authId={}", reasonCategoryIds, authId);
        try {
            this.createDefaultReason(authId);
            TypedQuery<ReasonMasterEntity> query;
            if (Objects.nonNull(reasonCategoryIds)) {
                query = this.em.createNamedQuery("ReasonMasterEntity.findByCategoryId", ReasonMasterEntity.class);
                query.setParameter("reasonCategoryIds", reasonCategoryIds);  
            } else {
                logger.info("findByCategoryId: Argument Error");
                return new ArrayList();
            }

            return query.getResultList();
        } catch(Exception ex) {
            logger.fatal(ex);
            return new ArrayList();
        } finally {
            logger.info("findByCategoryId: end");
        }
    }

    /**
     * 指定した理由区分名の理由情報一覧を取得する。
     *
     * @param reasonCategoryName 理由区分名
     * @param reasonType 理由種別
     * @param authId 認証ID
     * @return 理由情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("category-name")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<ReasonMasterEntity> findByCategoryName(@QueryParam("reasonCategoryName") String reasonCategoryName, @QueryParam("type") ReasonTypeEnum reasonType, @QueryParam("authId") Long authId) {
        logger.info("findByCategoryName: reasonCategoryName={}, authId={}", reasonCategoryName, authId);
        try {
            this.createDefaultReason(authId);
            TypedQuery<ReasonMasterEntity> query;
            if (Objects.nonNull(reasonCategoryName) && Objects.nonNull(reasonType)) {
                query = this.em.createNamedQuery("ReasonMasterEntity.findByCategoryName", ReasonMasterEntity.class);
                query.setParameter("reasonCategoryName", reasonCategoryName);
                query.setParameter("reasonType", reasonType);
            } else {
                logger.info("findByCategoryName: Argument Error");
                return new ArrayList();
            }

            return query.getResultList();
        } catch(Exception ex) {
            logger.fatal(ex);
            return new ArrayList();
        } finally {
            logger.info("findByCategoryName: end");
        }
    }

    /**
     * 理由情報一覧を範囲指定して取得する。
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
    public List<ReasonMasterEntity> findRange(@QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findRange: from={}, to={}, authId={}", from, to, authId);
        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            return super.findRange(from, to);
        } else {
            return super.findAll();
        }
    }

    /**
     * 理由情報の件数を取得する。
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
     * 指定した理由種別の理由情報の件数を取得する。
     *
     * @param reasonType 理由種別
     * @param authId 認証ID
     * @return 件数
     */
    @Lock(LockType.READ)
    public String countByType(ReasonTypeEnum reasonType, Long authId) {
        logger.info("countByType: reasonType={}, authId={}", reasonType, authId);
        TypedQuery<Long> query = this.em.createNamedQuery("ReasonMasterEntity.countByType", Long.class);
        query.setParameter("reasonType", reasonType);
        return String.valueOf(query.getSingleResult());
    }

    /**
     * 指定した理由区分に含まれる理由件数を取得する。
     *
     * @param reasonCategoryIds 理由区分ID(リスト)
     * @param authId 認証ID
     * @return 件数
     */
    @Lock(LockType.READ)
    public String countByCategory(List<Long> reasonCategoryIds, Long authId) {
        logger.info("countCategory: reasonCategoryIds={}, authId={}", reasonCategoryIds, authId);
        try {
            TypedQuery<Long> query = this.em.createNamedQuery("ReasonMasterEntity.countByCategory", Long.class);
            query.setParameter("reasonCategoryIds", reasonCategoryIds);
            return String.valueOf(query.getSingleResult());
        } catch (Exception ex) {
            logger.fatal(ex);
            throw ex;
        }
    }

    /**
     * 理由の初期データを作成する。
     *
     * @param authId 認証ID
     */
    @Lock(LockType.READ)
    private void createDefaultReason(Long authId) {
        // 不良理由
        ReasonCategoryEntity defoultCategory = reasonCategoryEntityFacadeREST.findName(reasonCategoryEntityFacadeREST.DEFAULT_CATEGORY_NAME, ReasonTypeEnum.TYPE_DEFECT, authId);
        long count = Long.parseLong(this.countByCategory(Arrays.asList(defoultCategory.getReasonCategoryId()), authId));
        
        if (count != 0) {
            return;
        }
        logger.info("create default defectReason.");
        try {
            this.add(new ReasonMasterEntity(ReasonTypeEnum.TYPE_DEFECT, "other", "#000000", "#FF8000", LightPatternEnum.LIGHTING, defoultCategory.getReasonCategoryId()), authId);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    protected void setEntityManager(EntityManager em) {
        this.em = em;
    }
    
    protected void setReasonCategoryEntityFacadeREST(ReasonCategoryEntityFacadeREST reasonCategoryEntityFacadeREST) {
        this.reasonCategoryEntityFacadeREST = reasonCategoryEntityFacadeREST;
    }
}
