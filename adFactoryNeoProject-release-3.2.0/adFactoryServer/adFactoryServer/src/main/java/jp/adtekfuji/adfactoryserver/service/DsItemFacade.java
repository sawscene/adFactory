/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import adtekfuji.utility.StringUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
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
import jp.adtekfuji.adFactory.entity.SampleResponse;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.dsKanban.MstDsItem;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 品番マスタ情報REST
 * 
 * @author s-heya
 */
@Stateless
@Path("dsItem")
public class DsItemFacade extends AbstractFacade<MstDsItem> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    private final Logger logger = LogManager.getLogger();
   
    /**
     * コンストラクタ
     */
    public DsItemFacade() {
        super(MstDsItem.class);
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
     * テスト用：EntityManager を設定する。
     *
     * @param em EntityManager
     */
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
    
    /**
     * 品番マスタ情報を取得する。
     * 
     * @param id 品番マスタ情報ID
     * @param authId 認証ID
     * @return 
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public MstDsItem find(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("find: id={}, authId={}", id, authId);
        MstDsItem dsItem = super.find(id);
        if (Objects.isNull(dsItem)) {
            return new MstDsItem();
        }
        return dsItem;
    }

    /**
     * 品番マスタ情報を登録する
     * 
     * @param entity
     * @param authId
     * @return
     * @throws URISyntaxException 
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(MstDsItem entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("add: {}, authId={}", entity, authId);
        try {
            
            // 重複確認
            TypedQuery<Long> query = this.em.createNamedQuery("MstDsItem.count", Long.class);
            query.setParameter("category", entity.getCategory());
            query.setParameter("productNo", entity.getProductNo());
            if (query.getSingleResult() > 0) {
                // 該当するものがあった場合、重複していることを通知する
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }

            entity.setUpdatePersonId(authId);
            entity.setUpdateDatetime(new Date());
            
            super.create(entity);
            this.em.flush();

            URI uri = new URI(new StringBuilder("dsItem/").append(entity.getProductId()).toString());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 品番マスタ情報の登録件数を返す。
     * 
     * @param category
     * @param productNo
     * @param authId
     * @return 
     */
    @Lock(LockType.READ)
    @GET
    @Path("count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String countByCategory(@QueryParam("category") Integer category, @QueryParam("productNo") String productNo, @QueryParam("authId") Long authId) {
        logger.info("findAllByCategory: category={}, productNo={}", category, productNo);
        try {
            if (Objects.isNull(category)) {
                return "0";
            }

            TypedQuery<Long> query = null;
            if (StringUtils.isEmpty(productNo)) {
                query = this.em.createNamedQuery("MstDsItem.countByCategory", Long.class);
                query.setParameter("category", category);
            
            } else {
                query = this.em.createNamedQuery("MstDsItem.countByCategoryAndProductNo", Long.class);
                query.setParameter("category", category);
                query.setParameter("productNo", "%" + addHyphen(productNo) + "%");
            }
            
            return String.valueOf(query.getSingleResult());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return "0";
        }
    }

    /**
     * 品番マスタ情報一覧を取得する。
     * 
     * @param category 区分
     * @param productNo 品番
     * @param from 範囲開始
     * @param to 範囲終了
     * @param authId 認証ID
     * @return 品番マスタ一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<MstDsItem> findRangeByCategory(@QueryParam("category") Integer category, @QueryParam("productNo") String productNo, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findAllByCategory: category={}", category);
        try {
            if (Objects.isNull(category)) {
                return new ArrayList<>();
            }
            
            TypedQuery<MstDsItem> query = null;
            if (StringUtils.isEmpty(productNo)) {
                query = this.em.createNamedQuery("MstDsItem.findByCategory", MstDsItem.class);
                query.setParameter("category", category);
            } else {
                query = this.em.createNamedQuery("MstDsItem.findByCategoryAndProductNo", MstDsItem.class);
                query.setParameter("category", category);
                query.setParameter("productNo", "%" + addHyphen(productNo) + "%");
            }
            
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                query.setMaxResults(to - from + 1);
                query.setFirstResult(from);
            }

            return query.getResultList();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 品番マスタ情報を取得する。
     * 
     * @param category 区分
     * @param productNo 品番
     * @return 品番マスタ情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("productNo")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public MstDsItem findByProductNo(@QueryParam("category") Integer category, @QueryParam("productNo") String productNo) {
        logger.info("findAllByCategory: category={}, productNo={}", category, productNo);
        try {
            if (Objects.isNull(category) 
                    || StringUtils.isEmpty(productNo)) {
                return new MstDsItem();
            }
            
            TypedQuery<MstDsItem> query = this.em.createNamedQuery("MstDsItem.findByProductNo", MstDsItem.class);
            query.setParameter("category", category);
            query.setParameter("productNo", addHyphen(productNo));
            
            return query.getSingleResult();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new MstDsItem();
        }
    }

    /**
     * 品番マスタ情報を削除する。
     * 
     * @param id 品番マスタ情報ID
     * @param authId 認証ID
     * @return 処理結果
     */
    @DELETE
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public SampleResponse remove(@QueryParam("id") List<Long> ids, @QueryParam("authId") Long authId) {
        logger.info("remove: ids={}, authId={}", ids, authId);
        try {
            if (Objects.isNull(ids) || ids.isEmpty()) {
                return new SampleResponse(ServerErrorTypeEnum.INVALID_ARGUMENT.name(), null);
            }
            
            TypedQuery<MstDsItem> query = this.em.createNamedQuery("MstDsItem.deleteByProductId", MstDsItem.class);
            query.setParameter("productIds", ids);
            int count = query.executeUpdate();

            logger.info("removed: count={}", count);

            return new SampleResponse(ServerErrorTypeEnum.SUCCESS.name(), null);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new SampleResponse(ServerErrorTypeEnum.SERVER_FETAL.name(), null);
        }
    }

    /**
     * 品番マスタ情報を更新する。
     * 
     * @param entity 品番マスタ情報
     * @param authId 認証ID
     * @return 処理結果
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(MstDsItem entity, @QueryParam("authId") Long authId) {
        logger.info("update: {}", entity);

        try {
            MstDsItem target = this.find(entity.getProductId(), authId);
            if (Objects.isNull(target.getProductId())) {
                // 品番マスタ情報が見つからない
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_UPDATE)).build();
            }

            if (!target.getVerInfo().equals(entity.getVerInfo())) {
                // バージョンが異なる
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.DIFFERENT_VER_INFO)).build();
            }

            // 重複確認
            if (!Objects.equals(entity.getCategory(), target.getCategory())
                    || !StringUtils.equals(entity.getProductNo(), target.getProductNo())) {
                TypedQuery<Long> query = this.em.createNamedQuery("MstDsItem.count", Long.class);
                query.setParameter("category", entity.getCategory());
                query.setParameter("productNo", entity.getProductNo());
                if (query.getSingleResult() > 0) {
                    // 既に登録されている
                    return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
                }
            }

            // 楽観的ロックをかける。
            this.em.lock(target, LockModeType.OPTIMISTIC);

            entity.setUpdatePersonId(authId);
            entity.setUpdateDatetime(new Date());

            super.edit(entity);

            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 品番にハイフンを追加する。
     * 
     * @param productNo
     * @return 
     */
    public String addHyphen(String productNo) {
        if (productNo.length() == 10) {
            if (productNo.contains("-")) {
                return productNo;
            }
            return productNo.substring(0, 6) + '-' + productNo.substring(6, 10);
        }
        return productNo;
    }
}
