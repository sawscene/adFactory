/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import adtekfuji.utility.DateUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
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
import jp.adtekfuji.adFactory.entity.search.HolidaySearchCondition;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.holiday.HolidayEntity;
import jp.adtekfuji.adfactoryserver.entity.holiday.HolidayEntity_;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 休日情報取得用REST
 *
 * @author nar-nakamura
 */
@Singleton
@Path("holiday")
public class HolidayEntityFacadeREST extends AbstractFacade<HolidayEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     */
    public HolidayEntityFacadeREST() {
        super(HolidayEntity.class);
    }

    /**
     * 休日情報を追加する。
     *
     * @param entity 休日情報
     * @param authId 認証ID
     * @return 結果
     * @throws URISyntaxException 
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(HolidayEntity entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("add: {}, authId={}", entity, authId);
        try {
            super.create(entity);
            this.em.flush();

            URI uri = new URI("holiday/" + entity.getHolidayId().toString());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 休日情報を更新する。
     *
     * @param entity 休日情報
     * @param authId 認証ID
     * @return 結果
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(HolidayEntity entity, @QueryParam("authId") Long authId) {
        logger.info("update: {}, authId={}", entity, authId);
        try {
            // 排他用バージョンを確認する。
            HolidayEntity target = super.find(entity.getHolidayId());
            if (!target.getVerInfo().equals(entity.getVerInfo())) {
                // 現在の排他用バージョンと異なる場合、排他用バージョンが異なる事を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.DIFFERENT_VER_INFO)).build();
            }

            // 楽観的ロックをかける。
            this.em.lock(target, LockModeType.OPTIMISTIC);

            // 休日情報を更新する。
            super.edit(entity);

            return Response.ok().entity(ResponseEntity.success()).build();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 休日情報を削除する。
     *
     * @param id 休日ID
     * @param authId 認証ID
     * @return 結果
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
     * 休日情報を削除する。
     *
     * @param ids 休日ID一覧
     * @param authId 認証ID
     * @return 結果
     */
    @DELETE
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response remove(@QueryParam("id") final List<Long> ids, @QueryParam("authId") Long authId) {
        logger.info("remove: ids={}, authId={}", ids, authId);
        try {
            for (Long id : ids) {
                super.remove(super.find(id));
            }

            return Response.ok().entity(ResponseEntity.success()).build();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 休日情報を取得する。
     *
     * @param id 休日ID
     * @param authId 認証ID
     * @return 休日情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public HolidayEntity find(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("find: id={}, authId={}", id, authId);
        try {
            HolidayEntity entity = super.find(id);
            if (Objects.isNull(entity)) {
                return new HolidayEntity();
            }

            return entity;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new HolidayEntity();
        }
    }

    /**
     * 休日情報一覧を範囲取得する。
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 指定範囲の休日情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<HolidayEntity> findRange(@QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findRange: from={}, to={}, authId={}", from, to, authId);
        try {
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                return super.findRange(from, to);
            } else {
                return super.findAll();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList();
        }
    }

    /**
     * 休日情報の件数を取得する。
     *
     * @param authId 認証ID
     * @return 休日情報の件数
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
     * 条件を指定して休日情報一覧を取得する。
     *
     * @param condition 休日情報検索条件
     * @param authId 認証ID
     * @return 休日情報一覧
     */
    @Lock(LockType.READ)
    @PUT
    @Path("search")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<HolidayEntity> searchHoliday(HolidaySearchCondition condition, @QueryParam("authId") Long authId) {
        logger.info("searchHoliday: {}, authId={}", condition, authId);
        List<HolidayEntity> result = null;
        try {
            Query query = getSearchQuery(SearchType.SEARCH, condition);
            result = query.getResultList();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * 条件を指定して休日情報一覧を範囲取得する。
     *
     * @param condition 休日情報検索条件
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 休日情報一覧
     */
    @Lock(LockType.READ)
    @PUT
    @Path("search/range")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<HolidayEntity> searchHolidayRange(HolidaySearchCondition condition, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("searchHolidayRange: {}, from={}, to={}, authId={}", condition, from, to, authId);
        List<HolidayEntity> result = null;
        try {
            Query query = getSearchQuery(SearchType.SEARCH, condition);

            // from, to 両方指定がある場合は範囲取得する。
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                query.setMaxResults(to - from + 1);
                query.setFirstResult(from);
            }

            result = query.getResultList();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * 条件を指定して休日情報の件数を取得する。
     *
     * @param condition 休日情報検索条件
     * @param authId 認証ID
     * @return 休日情報の件数
     */
    @Lock(LockType.READ)
    @PUT
    @Path("search/count")
    @Consumes({"application/xml", "application/json"})
    @Produces({"text/plain"})
    @ExecutionTimeLogging
    public String countHoliday(HolidaySearchCondition condition, @QueryParam("authId") Long authId) {
        logger.info("countSearch:{}", condition);
        String result = null;
        try {
            Query query = getSearchQuery(SearchType.COUNT, condition);
            result = String.valueOf(query.getSingleResult());

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * 休日情報の検索クエリを取得する。
     *
     * @param type クエリ種別 (SEARCH：一覧取得, COUNT：件数取得)
     * @param condition 休日情報検索条件
     * @return 休日情報の検索クエリ休日情報の検索クエリ
     */
    @Lock(LockType.READ)
    private Query getSearchQuery(SearchType type, HolidaySearchCondition condition) {
        TypedQuery<HolidayEntity> query;

        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();

        Root<HolidayEntity> poolHoliday = cq.from(HolidayEntity.class);

        // 項目のパス
        jakarta.persistence.criteria.Path<Long> pathHolidayId = poolHoliday.get(HolidayEntity_.holidayId);
        jakarta.persistence.criteria.Path<Date> pathHolidayDate = poolHoliday.get(HolidayEntity_.holidayDate);

        // 検索条件
        Predicate where = getSearchWhere(condition, poolHoliday);

        if (type == SearchType.SEARCH) {
            // 一覧取得
            cq.select(poolHoliday)
                    .where(where)
                    .orderBy(cb.asc(pathHolidayDate), cb.asc(pathHolidayId));
        } else {
            // 件数取得
            cq.select(cb.count(poolHoliday))
                    .where(where);
        }
        query = this.em.createQuery(cq);

        return query;
    }

    /**
     * 休日情報の検索条件から、Predicate を取得する。
     *
     * @param condition 休日情報検索条件
     * @param poolHoliday
     * @return Predicate
     */
    @Lock(LockType.READ)
    private Predicate getSearchWhere(HolidaySearchCondition condition, Root<HolidayEntity> poolHoliday) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();

        // 項目のパス
        jakarta.persistence.criteria.Path<Date> pathHolidayDate = poolHoliday.get(HolidayEntity_.holidayDate);

        // 検索条件
        List<Predicate> where = new ArrayList<>();

        // 日付範囲の先頭 (指定日の00:00:00.000)
        if (Objects.nonNull(condition.getFromDate())) {
            Date fromDate = DateUtils.getBeginningOfDate(condition.getFromDate());
            where.add(cb.greaterThanOrEqualTo(pathHolidayDate, fromDate));
        }

        // 日付範囲の末尾 (指定日の23:59:59.999)
        if (Objects.nonNull(condition.getToDate())) {
            Date toDate = DateUtils.getEndOfDate(condition.getToDate());
            where.add(cb.lessThanOrEqualTo(pathHolidayDate, toDate));
        }

        return cb.and(where.toArray(new Predicate[where.size()]));
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    protected void setEntityManager(EntityManager em) {
        this.em = em;
    }
}
