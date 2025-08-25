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
import java.util.Optional;
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
import jp.adtekfuji.adfactoryserver.entity.indirectwork.IndirectWorkEntity;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 間接作業情報取得用REST：間接作業情報を操作するためのクラス
 *
 * @author nar-nakamura
 */
@Stateless
@Path("indirect-work")
public class IndirectWorkEntityFacadeREST extends AbstractFacade<IndirectWorkEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     */
    public IndirectWorkEntityFacadeREST() {
        super(IndirectWorkEntity.class);
    }

    /**
     * 指定したIDの間接作業情報を取得する。
     *
     * @param id 間接作業ID
     * @param authId 認証ID
     * @return 間接作業情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public IndirectWorkEntity find(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("find: id={}, authId={}", id, authId);
        try {
            return super.find(id);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 間接作業情報一覧を取得する。
     *
     * @param authId 認証ID
     * @return 間接作業情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<IndirectWorkEntity> findAll(@QueryParam("authId") Long authId) {
        logger.info("findAll: authId={}", authId);
        return this.findRange(null, null, authId);
    }

    /**
     * 間接作業情報一覧を範囲指定して取得する。
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 指定された範囲の間接作業情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<IndirectWorkEntity> findRange(@QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findRange: from={}, to={}, authId={}", from, to, authId);
        try {
            List<IndirectWorkEntity> entities;
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                entities = super.findRange(from, to);
            } else {
                entities = super.findAll();
            }

            if (!entities.isEmpty()) {
                // 間接工数実績で使用しているかどうかの情報を追加する。
                List<Long> ids = new ArrayList<>();
                for (IndirectWorkEntity entity : entities) {
                    ids.add(entity.getIndirectWorkId());
                }

                List<Long> usedIds = this.getUsedIndirectWorkIds(ids);
                for (IndirectWorkEntity entity : entities) {
                    Optional<Long> findId = usedIds.stream().filter(p -> p.equals(entity.getIndirectWorkId())).findFirst();
                    entity.setIsUsed(findId.isPresent());
                }
            }
            return entities;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 間接作業情報の件数を取得する。
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
        try {
            return String.valueOf(super.count());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定した分類番号・作業番号の間接作業情報を取得する。
     *
     * @param classNumber 分類番号
     * @param workNumber 作業番号
     * @param authId 認証ID
     * @return 間接作業情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("work-number")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public IndirectWorkEntity findByWorkNumber(@QueryParam("class") String classNumber, @QueryParam("work") String workNumber, @QueryParam("authId") Long authId) {
        logger.info("findByWorkNumber: classNumber={}, workNumber={}, authId={}", classNumber, workNumber, authId);
        TypedQuery<IndirectWorkEntity> query = this.em.createNamedQuery("IndirectWorkEntity.findByUk", IndirectWorkEntity.class);
        query.setParameter("classNumber", classNumber);
        query.setParameter("workNumber", workNumber);
        try {
            IndirectWorkEntity entity = query.getSingleResult();
            if (Objects.isNull(entity)) {
                // 検索した情報が存在しない場合は空のエンティティを返す
                return new IndirectWorkEntity();
            }
            return entity;
        } catch (Exception ex) {
            logger.fatal(ex);
            return new IndirectWorkEntity();
        }
    }

    /**
     * 間接作業情報を登録する。
     *
     * @param entity 間接作業情報
     * @param authId 認証ID
     * @return 成功：200 + 追加した情報のURI/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException 戻り値のURIが,文字列を URI 参照として解析できなかった場合例外を発生します
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(IndirectWorkEntity entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("add: {}, authId={}", entity, authId);
        try {
            // 重複確認
            TypedQuery<Long> query = this.em.createNamedQuery("IndirectWorkEntity.checkAddByUk", Long.class);
            query.setParameter("classNumber", entity.getClassNumber());
            query.setParameter("workNumber", entity.getWorkNumber());
            if (query.getSingleResult() > 0) {
                // 該当するものがあった場合、重複していることを通知する
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }

            // 作業区分の有無
            TypedQuery<Long> query1 = this.em.createNamedQuery("WorkCategoryEntity.countById", Long.class);
            query1.setParameter("workCategoryId", entity.getWorkCategoryId());
            if (query1.getSingleResult() == 0) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_WORK_CATEGORY)).build();
            }

            // 登録
            super.create(entity);
            this.em.flush();
            // 作成した情報を元に、戻り値のURIを作成する
            URI uri = new URI("indirectwork/" + entity.getIndirectWorkId());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 間接作業情報を更新する。
     *
     * @param entity 間接作業情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(IndirectWorkEntity entity, @QueryParam("authId") Long authId) {
        logger.info("update: {}, authId={}", entity, authId);
        try {
            // 排他用バージョンを確認する。
            IndirectWorkEntity target = super.find(entity.getIndirectWorkId());
            if (!target.getVerInfo().equals(entity.getVerInfo())) {
                // 現在の排他用バージョンと異なる場合、排他用バージョンが異なる事を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.DIFFERENT_VER_INFO)).build();
            }

            // 重複確認
            TypedQuery<Long> query = this.em.createNamedQuery("IndirectWorkEntity.checkUpdateByUk", Long.class);
            query.setParameter("classNumber", entity.getClassNumber());
            query.setParameter("workNumber", entity.getWorkNumber());
            query.setParameter("indirectWorkId", entity.getIndirectWorkId());
            if (query.getSingleResult() > 0) {
                // 該当するものがあった場合、重複していることを通知する
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }

            // 楽観的ロックをかける。
            this.em.lock(target, LockModeType.OPTIMISTIC);

            // 間接作業情報を更新する。
            super.edit(entity);

            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 間接作業情報を削除する。
     *
     * @param id 間接作業ID
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
            // 関連付けがある場合は削除不可
            if (this.getActualCount(id) > 0) {
                // 該当するものがあった場合、削除不可を通知する
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.EXIST_CHILD_DELETE)).build();
            }

            // 削除
            super.remove(super.find(id));
            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 間接工数実績で使用されている件数を取得する。
     *
     * @param id 間接作業ID
     * @return 使用件数
     */
    @Lock(LockType.READ)
    private long getActualCount(Long id) {
        TypedQuery<Long> query = this.em.createNamedQuery("IndirectWorkEntity.countIndirectActual", Long.class);
        query.setParameter("indirectWorkId", id);
        return query.getSingleResult();
    }

    /**
     * 間接工数実績で使用されている間接作業ID一覧を取得する。
     *
     * @param indirectWorkIds 対象とする間接作業ID一覧
     * @return 間接作業IDのリスト
     */
    @Lock(LockType.READ)
    private List<Long> getUsedIndirectWorkIds(List<Long> indirectWorkIds) {
        TypedQuery<Long> query = this.em.createNamedQuery("IndirectWorkEntity.getUsedIndirectWorkIds", Long.class);
        query.setParameter("indirectWorkIds", indirectWorkIds);
        return query.getResultList();
    }

    /**
     * 指定した作業区分の間接作業を取得する。
     *
     * @param workCategoryIds 工程分類IDのリスト
     * @param authId 認証ID
     * @return 間接作業情報のリスト
     */
    @Lock(LockType.READ)
    @GET
    @Path("category")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<IndirectWorkEntity> findCategory(@QueryParam("id") List<Long> workCategoryIds, @QueryParam("authId") Long authId) {
        logger.info("findCategory: workCategoryIds={}, authId={}", workCategoryIds, authId);
        try {
            return this.findCategory(workCategoryIds, null, null, authId);
        } catch (Exception ex) {
            logger.fatal(ex);
            throw ex;
        }
    }

    /**
     * 指定した作業区分の間接作業を取得する。
     *
     * @param workCategoryIds 作業区分ID(リスト)
     * @param from 取得範囲(from)
     * @param to 取得範囲(to)
     * @param authId 認証ID
     * @return 間接作業(エンティティ)のリスト
     */
    @Lock(LockType.READ)
    @GET
    @Path("category/range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<IndirectWorkEntity> findCategory(@QueryParam("id") List<Long> workCategoryIds, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findCategory: workCategoryIds={}, from={}, to={}, authId={}", workCategoryIds, from, to, authId);
        try {
            TypedQuery<IndirectWorkEntity> query = this.em.createNamedQuery("IndirectWorkEntity.findByWorkCategory", IndirectWorkEntity.class);
            query.setParameter("workCategoryIds", workCategoryIds);
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                query.setMaxResults(to - from + 1);
                query.setFirstResult(from);
            }
            return query.getResultList();
        } catch (Exception ex) {
            logger.fatal(ex);
            throw ex;
        }
    }

    /**
     * 指定した作業区分の間接作業のカウントを取得する。
     *
     * @param workCategoryIds 作業区分ID(リスト)
     * @param authId 認証ID
     * @return 検索に該当したモノ情報の数
     */
    @Lock(LockType.READ)
    @GET
    @Path("category/count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String countCategory(@QueryParam("id") List<Long> workCategoryIds, @QueryParam("authId") Long authId) {
        logger.info("countCategory: workCategoryIds={}, authId={}", workCategoryIds, authId);
        try {
            TypedQuery<Long> query = this.em.createNamedQuery("IndirectWorkEntity.countByWorkCategory", Long.class);
            query.setParameter("workCategoryIds", workCategoryIds);
            return String.valueOf(query.getSingleResult());
        } catch (Exception ex) {
            logger.fatal(ex);
            throw ex;
        }
    }

    /**
     * 作業中の作業数を返却
     * @param indirectWorkId 間接作業ID
     * @param authId 認証ID
     * @return 作業数
     */
    @Lock(LockType.READ)
    @GET
    @Path("/working/count")
    @Produces({"text/plain"})
    @ExecutionTimeLogging
    public Long countWorkingIndirectWork(@QueryParam("id") Long indirectWorkId, @QueryParam("authId") Long authId) {

        if (Objects.isNull(indirectWorkId)) {
            return 0L;
        }

        try {
            TypedQuery<Long> query = this.em.createNamedQuery("OperationEntity.countActiveIndirectWork", Long.class);
            query.setParameter(1, indirectWorkId);
            final Long count = query.getSingleResult();
            logger.info("active indirect work : {}", count);
            return count;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return 0L;
        }
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    protected void setEntityManager(EntityManager em) {
        this.em = em;
    }
}
