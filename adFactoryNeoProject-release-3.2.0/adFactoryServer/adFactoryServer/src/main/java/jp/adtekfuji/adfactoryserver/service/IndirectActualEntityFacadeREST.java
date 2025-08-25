/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

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
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
import jp.adtekfuji.adFactory.entity.search.IndirectActualSearchCondition;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.indirectwork.IndirectActualEntity;
import jp.adtekfuji.adfactoryserver.entity.indirectwork.IndirectActualEntity_;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 間接工数実績取得用REST：間接工数実績を操作するためのクラス
 *
 * @author nar-nakamura
 */
@Stateless
@Path("indirect-actual")
public class IndirectActualEntityFacadeREST extends AbstractFacade<IndirectActualEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     */
    public IndirectActualEntityFacadeREST() {
        super(IndirectActualEntity.class);
    }

    /**
     * 間接工数実績IDを指定して、間接工数実績情報を取得する。
     *
     * @param id 間接工数実績ID
     * @param authId 認証ID
     * @return 間接工数実績情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public IndirectActualEntity find(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("find: id={}, authId={}", id, authId);
        try {
            return super.find(id);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 間接工数実績情報一覧を取得する。
     *
     * @param authId 認証ID
     * @return 間接工数実績情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<IndirectActualEntity> findAll(@QueryParam("authId") Long authId) {
        logger.info("findAll: authId={}", authId);
        return this.findRange(null, null, authId);
    }

    /**
     * 間接工数実績情報一覧を取得する。
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 間接工数実績情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<IndirectActualEntity> findRange(@QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findRange: from={}, to={}, authId={}", from, to, authId);
        try {
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                return super.findRange(from, to);
            } else {
                return super.findAll();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 間接工数実績情報の件数を取得する。
     * 
     * @param authId 認証ID
     * @return 間接工数実績情報の件数
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
     * 間接工数実績検索条件を指定して、間接工数実績情報一覧を取得する。
     *
     * @param condition 検索条件
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 間接工数実績情報一覧
     */
    @Lock(LockType.READ)
    @PUT
    @Path("search/range")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<IndirectActualEntity> searchIndirectActual(IndirectActualSearchCondition condition, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("searchIndirectActual: {}, from={}, to={}, authId={}", condition, from, to, authId);
        try {
            Query query = this.getSearchQuery(SearchType.SEARCH, condition);

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                query.setMaxResults(to - from + 1);
                query.setFirstResult(from);
            }

            return query.getResultList();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 間接工数実績検索条件を指定して、間接工数実績情報の件数を取得する。
     *
     * @param condition 検索条件
     * @param authId 認証ID
     * @return 件数
     */
    @Lock(LockType.READ)
    @PUT
    @Path("search/count")
    @Consumes({"application/xml", "application/json"})
    @Produces({"text/plain"})
    @ExecutionTimeLogging
    public String countIndirectActual(IndirectActualSearchCondition condition, @QueryParam("authId") Long authId) {
        logger.info("countIndirectActual: {}, authId={}", condition, authId);
        try {
            Query query = this.getSearchQuery(SearchType.COUNT, condition);
            return String.valueOf(query.getSingleResult());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 検索条件で間接工数実績情報を検索するクエリを取得する。
     *
     * @param type 検索種別
     * @param condition 検索条件
     * @return 検索クエリ
     */
    @Lock(LockType.READ)
    private Query getSearchQuery(SearchType type, IndirectActualSearchCondition condition) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();

        Root<IndirectActualEntity> poolIndirectActual = cq.from(IndirectActualEntity.class);

        jakarta.persistence.criteria.Path<Long> pathIndirectActualId = poolIndirectActual.get(IndirectActualEntity_.indirectActualId);
        jakarta.persistence.criteria.Path<Date> pathImplementDatetime = poolIndirectActual.get(IndirectActualEntity_.implementDatetime);
        jakarta.persistence.criteria.Path<Long> pathOrganizationId = poolIndirectActual.get(IndirectActualEntity_.organizationId);
        jakarta.persistence.criteria.Path<Long> pathIndirectWorkId = poolIndirectActual.get(IndirectActualEntity_.indirectWorkId);

        // 検索条件
        List<Predicate> where = new ArrayList();

        // 実施日時
        if (Objects.nonNull(condition.getFromDate())) {
            where.add(cb.greaterThanOrEqualTo(pathImplementDatetime, condition.getFromDate()));
        }
        if (Objects.nonNull(condition.getToDate())) {
            where.add(cb.lessThanOrEqualTo(pathImplementDatetime, condition.getToDate()));
        }

        // 組織ID
        if (Objects.nonNull(condition.getOrganizationCollection())) {
            where.add(pathOrganizationId.in(condition.getOrganizationCollection()));
        }

        // 間接作業ID
        if (Objects.nonNull(condition.getIndirectWorkId())) {
            where.add(cb.equal(pathIndirectWorkId, condition.getIndirectWorkId()));
        }

        if (SearchType.COUNT.equals(type)) {
            cq.select(cb.count(pathIndirectActualId))
                    .where(cb.and(where.toArray(new Predicate[where.size()])));
        } else {
            cq.select(poolIndirectActual)
                    .where(cb.and(where.toArray(new Predicate[where.size()])))
                    .orderBy(cb.asc(pathImplementDatetime), cb.asc(pathIndirectActualId));
        }

        return this.em.createQuery(cq);
    }

    /**
     * 間接工数実績情報を登録する。
     *
     * @param entity 間接工数実績情報
     * @param authId 認証ID
     * @return 成功：200 + 追加した情報のURI/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException 戻り値のURIが,文字列を URI 参照として解析できなかった場合例外を発生します
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(IndirectActualEntity entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("add: {}, authId={}", entity, authId);
        try {
            if (Objects.isNull(entity.getImplementDatetime())) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
            }
            
            // 間接工数実績情報を登録する。
            super.create(entity);
            this.em.flush();

            // 作成した情報を元に、戻り値のURIを作成する。
            URI uri = new URI("indirectactual/" + entity.getIndirectActualId());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 間接工数実績情報を更新する。
     *
     * @param entity 間接工数実績情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(IndirectActualEntity entity, @QueryParam("authId") Long authId) {
        logger.info("update: {}, authId={}", entity, authId);
        try {
            if (Objects.isNull(entity.getImplementDatetime())) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
            }

            // 間接工数実績情報を更新する。
            super.edit(entity);

            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 指定した間接工数実績IDの間接工数実績情報を削除する。
     *
     * @param id 間接工数実績ID
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
            // 間接工数実績情報を削除する。
            super.remove(super.find(id));

            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
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
