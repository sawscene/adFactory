/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Comparator;
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
import jp.adtekfuji.adFactory.enumerate.ReasonTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.master.ReasonCategoryEntity;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 理由区分マスタ REST API
 *
 * @author HN)y-harada
 */
@Stateless
@Path("reason/category")
public class ReasonCategoryEntityFacadeREST extends AbstractFacade<ReasonCategoryEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    @EJB
    private ReasonMasterEntityFacadeREST reasonFacade;

    private final Logger logger = LogManager.getLogger();
    
    protected final String DEFAULT_CATEGORY_NAME = "default";

    /**
     * コンストラクタ
     */
    public ReasonCategoryEntityFacadeREST() {
        super(ReasonCategoryEntity.class);
    }

    /**
     * コンストラクタ
     *
     * @param entityManager
     * @param reasonMasterEntityFacadeREST
     */
    public ReasonCategoryEntityFacadeREST(EntityManager entityManager, ReasonMasterEntityFacadeREST reasonMasterEntityFacadeREST) {
        super(ReasonCategoryEntity.class);
        this.em = entityManager;
        this.reasonFacade = reasonMasterEntityFacadeREST;
    }

    /**
     * 理由区分を追加する。
     *
     * @param entity 理由区分エンティティ
     * @param authId 認証ID
     * @return 成功：200 /失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException 戻り値のURIが,文字列を URI 参照として解析できなかった場合例外を発生します
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(ReasonCategoryEntity entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("add: {}, authId={}", entity, authId);
        try {
            if (Objects.nonNull(entity.getReasonCategoryId())) {
                TypedQuery<Long> countByIdQuery = this.em.createNamedQuery("ReasonCategoryEntity.countById", Long.class);
                countByIdQuery.setParameter("reasonCategoryId", entity.getReasonCategoryId());
                if (countByIdQuery.getSingleResult() > 0) {
                    return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
                }
            }

            TypedQuery<Long> countByNameQuery = this.em.createNamedQuery("ReasonCategoryEntity.countByName", Long.class);
            countByNameQuery.setParameter("reasonCategoryName", entity.getReasonCategoryName());
            countByNameQuery.setParameter("reasonType", entity.getReasonType());
            if (countByNameQuery.getSingleResult() > 0) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NAME_OVERLAP)).build();
            }

            super.create(entity);
            this.em.flush();

            URI uri = new URI("reason/category/" + entity.getReasonCategoryId());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 理由区分を更新する。
     *
     * @param entity 理由区分情報エンティティ
     * @param authId 認証ID
     * @return 成功：200 失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(ReasonCategoryEntity entity, @QueryParam("authId") Long authId) {
        logger.info("update: {}, authId={}", entity, authId);
        try {
            TypedQuery<ReasonCategoryEntity> findByIdQuery = this.em.createNamedQuery("ReasonCategoryEntity.findById", ReasonCategoryEntity.class);
            findByIdQuery.setParameter("reasonCategoryId", entity.getReasonCategoryId());
            ReasonCategoryEntity target = findByIdQuery.getSingleResult();
            if (Objects.isNull(target.getReasonCategoryId())) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_UPDATE)).build();
            }

            if (!target.getVerInfo().equals(entity.getVerInfo())) {
                // 現在の排他用バージョンと異なる場合、排他用バージョンが異なる事を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.DIFFERENT_VER_INFO)).build();
            }

            // 区分名重複チェック
            TypedQuery<Long> countByKeyQuery = this.em.createNamedQuery("ReasonCategoryEntity.countByKey", Long.class);
            countByKeyQuery.setParameter("reasonCategoryId", entity.getReasonCategoryId());
            countByKeyQuery.setParameter("reasonCategoryName", entity.getReasonCategoryName());
            countByKeyQuery.setParameter("reasonType", entity.getReasonType());
            if (countByKeyQuery.getSingleResult() > 0) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NAME_OVERLAP)).build();
            }

            // 楽観的ロックをかける。
            this.em.lock(target, LockModeType.OPTIMISTIC);

            // 理由区分を更新する。
            super.edit(entity);
            this.em.flush();

            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 理由区分を削除する。
     *
     * @param id 理由区分ID
     * @param authId 認証ID
     * @return 成功：200 失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @DELETE
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public Response remove(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("remove: id={}, authId={}", id, authId);
        try {
            // 組織に関連付けされているか
            if (this.isRelated(id)) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.EXIST_RELATION_DELETE)).build();    
            }
            
            // 理由区分に理由が存在する
            long count = Long.parseLong(this.reasonFacade.countByCategory(Arrays.asList(id), authId));
            if (!(count > 0)) {
                ReasonCategoryEntity target = super.find(id);
                if (Objects.nonNull(target)) {
                    super.remove(target);
                }
                // createDefaultReasonCategory(authId);
                return Response.ok().entity(ResponseEntity.success()).build();
            }

            // 削除不可
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.EXIST_CHILD_DELETE)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定したIDの理由区分を取得する。
     *
     * @param id 理由区分ID
     * @param authId 認証ID
     * @return 理由区分
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public ReasonCategoryEntity find(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("find: id={}, authId={}", id, authId);
        try {
            return super.find(id);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定した区分名の情報を検索する。
     *
     * @param name 検索する理由区分名
     * @param reasonType 検索する理由種別
     * @param authId 認証ID
     * @return 理由区分
     */
    @Lock(LockType.READ)
    @GET
    @Path("name")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ReasonCategoryEntity findName(@QueryParam("name") String name, @QueryParam("type") ReasonTypeEnum reasonType, @QueryParam("authId") Long authId) {
        logger.info("findName: name={}, reasonType={}, authId={}", name, reasonType, authId);
        try {
            // createDefaultReasonCategory(authId);

            if (Objects.isNull(name) || Objects.isNull(reasonType)) {
                return new ReasonCategoryEntity();
            }

            TypedQuery<ReasonCategoryEntity> query = this.em.createNamedQuery("ReasonCategoryEntity.findByName", ReasonCategoryEntity.class);
            query.setParameter("reasonCategoryName", name);
            query.setParameter("reasonType", reasonType);

            ReasonCategoryEntity entity = query.getSingleResult();
            if (Objects.isNull(entity)) {
                return new ReasonCategoryEntity();
            }
            return entity;
        } catch (Exception ex) {
            logger.fatal(ex);
            return new ReasonCategoryEntity();
        }
    }

    /**
     * 指定した理由種別の理由区分一覧を取得する。
     *
     * @param reasonType 理由種別
     * @param authId 認証ID
     * @return
     */
    @Lock(LockType.READ)
    @GET
    @Path("type")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<ReasonCategoryEntity> findByType(@QueryParam("type") ReasonTypeEnum reasonType, @QueryParam("authId") Long authId) {
        logger.info("findByType: reasonType={}, authId={}", reasonType, authId);
        try {
            // createDefaultReasonCategory(authId);
            
            List<ReasonCategoryEntity> entities;
            
            TypedQuery<ReasonCategoryEntity> query = this.em.createNamedQuery("ReasonCategoryEntity.findByType", ReasonCategoryEntity.class);
            query.setParameter("reasonType", reasonType);
            
            entities = query.getResultList();
            
            entities.sort(Comparator.comparing(entity -> entity.getReasonCategoryName()));
            return entities;
        } catch (Exception ex) {
            logger.fatal(ex);
            throw ex;
        }
    }

    /**
     * 指定した理由種別の理由区分数を取得する。
     *
     * @param reasonType 理由種別
     * @param authId 認証ID
     * @return
     */
    @Lock(LockType.READ)
    private String countByType(@QueryParam("type") ReasonTypeEnum reasonType, @QueryParam("authId") Long authId) {
        logger.info("countAllByType: reasonType={}, authId={}", reasonType, authId);
        try {
            TypedQuery<Long> query = this.em.createNamedQuery("ReasonCategoryEntity.countByType", Long.class);
            query.setParameter("reasonType", reasonType);

            return String.valueOf(query.getSingleResult());

        } catch (Exception ex) {
            logger.fatal(ex);
            throw ex;
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
    public List<ReasonCategoryEntity> findRange(@QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findRange: from={}, to={}, authId={}", from, to, authId);
        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            return super.findRange(from, to);
        } else {
            return super.findAll();
        }
    }

    /**
     * 理由区分の件数を取得する。
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
     * 理由区分が組織に関連付けされているかを返す。
     * 
     * @param reasonCategoryId 理由区分ID
     * @return 
     */
    @Lock(LockType.READ)
    private boolean isRelated(Long reasonCategoryId) {
        try {
            // 論理削除された組織を含む
            TypedQuery<Long> query = this.em.createNamedQuery("ConOrganizationReasonEntity.countByReasonCategoryId", Long.class);
            query.setParameter("reasonCategoryId", reasonCategoryId);
            long count = query.getSingleResult();
            return count > 0;
        } catch (Exception ex) {
            logger.fatal(ex);
            throw ex;
        }
    }
    
    /**
     * 理由区分の初期データを作成する。
     *
     * @param authId 認証ID
     */
    @Lock(LockType.READ)
    private void createDefaultReasonCategory(Long authId) {

        // 不良理由
        long count = Long.parseLong(this.countByType(ReasonTypeEnum.TYPE_DEFECT, authId));
        if (count != 0) {
            return;
        }
        logger.info("create default ReasonCategory.");
        try {
            this.add(new ReasonCategoryEntity(ReasonTypeEnum.TYPE_DEFECT, DEFAULT_CATEGORY_NAME), authId);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * エンティティマネージャーを取得する
     *
     * @return エンティティマネージャー
     */
    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    protected void setEntityManager(EntityManager em) {
        this.em = em;
    }
    
    protected void setReasonMasterEntityFacadeREST(ReasonMasterEntityFacadeREST reasonMasterEntityFacadeREST) {
        this.reasonFacade = reasonMasterEntityFacadeREST;
    }
}
