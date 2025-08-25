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
import java.util.Set;
import jakarta.ejb.EJB;
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
import jp.adtekfuji.adFactory.entity.search.ScheduleSearchCondition;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.schedule.ScheduleEntity;
import jp.adtekfuji.adfactoryserver.entity.schedule.ScheduleEntity_;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 予定情報取得用REST
 *
 * @author nar-nakamura
 */
@Singleton
@Path("schedule")
public class ScheduleEntityFacadeREST extends AbstractFacade<ScheduleEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    @EJB
    private OrganizationEntityFacadeREST organizationRest;

    private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     */
    public ScheduleEntityFacadeREST() {
        super(ScheduleEntity.class);
    }

    /**
     * 予定情報を追加する。
     *
     * @param entity 予定情報
     * @return 結果
     * @param authId 認証ID
     * @throws URISyntaxException 
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(ScheduleEntity entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("add: {}, authId={}", entity, authId);
        try {
            super.create(entity);
            em.flush();

            URI uri = new URI("schedule/" + entity.getScheduleId().toString());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 予定情報を更新する。
     *
     * @param entity 予定情報
     * @param authId 認証ID
     * @return 結果
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(ScheduleEntity entity, @QueryParam("authId") Long authId) {
        logger.info("update: {}, authId={}", entity, authId);
        try {
            // 排他用バージョンを確認する。
            ScheduleEntity target = super.find(entity.getScheduleId());
            if (!target.getVerInfo().equals(entity.getVerInfo())) {
                // 現在の排他用バージョンと異なる場合、排他用バージョンが異なる事を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.DIFFERENT_VER_INFO)).build();
            }

            // 楽観的ロックをかける。
            this.em.lock(target, LockModeType.OPTIMISTIC);

            // 予定情報を更新する。
            super.edit(entity);

            return Response.ok().entity(ResponseEntity.success()).build();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 予定情報を削除する。
     *
     * @param id 予定ID
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
     * 予定情報を削除する。
     *
     * @param ids 予定ID一覧
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
     * 予定情報を取得する。
     *
     * @param id 予定ID
     * @param authId 認証ID
     * @return 予定情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ScheduleEntity find(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("find: id={}, authId={}", id, authId);
        try {
            ScheduleEntity entity = super.find(id);
            if (Objects.isNull(entity)) {
                return new ScheduleEntity();
            }

            return entity;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ScheduleEntity();
        }
    }

    /**
     * 予定情報一覧を範囲取得する。
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 指定範囲の予定情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<ScheduleEntity> findRange(@QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
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
     * 予定情報の件数を取得する。
     *
     * @param authId 認証ID
     * @return 予定情報の件数
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
     * 条件を指定して予定情報一覧を取得する。
     *
     * @param condition 予定情報検索条件
     * @param authId 認証ID
     * @return 予定情報一覧
     */
    @Lock(LockType.READ)
    @PUT
    @Path("search")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<ScheduleEntity> searchSchedule(ScheduleSearchCondition condition, @QueryParam("authId") Long authId) {
        logger.info("searchSchedule: {}, authId={}", condition, authId);
        List<ScheduleEntity> result = null;
        try {
            Query query = getSearchQuery(SearchType.SEARCH, condition);
            result = query.getResultList();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * 条件を指定して予定情報一覧を範囲取得する。
     *
     * @param condition 予定情報検索条件
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 予定情報一覧
     */
    @Lock(LockType.READ)
    @PUT
    @Path("search/range")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<ScheduleEntity> searchScheduleRange(ScheduleSearchCondition condition, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("searchScheduleRange: {}, from={}, to={}, authId={}", condition, from, to, authId);
        List<ScheduleEntity> result = null;
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
     * 条件を指定して予定情報の件数を取得する。
     *
     * @param condition 予定情報検索条件
     * @param authId 認証ID
     * @return 予定情報の件数
     */
    @Lock(LockType.READ)
    @PUT
    @Path("search/count")
    @Consumes({"application/xml", "application/json"})
    @Produces({"text/plain"})
    @ExecutionTimeLogging
    public String countSchedule(ScheduleSearchCondition condition, @QueryParam("authId") Long authId) {
        logger.info("countSchedule:{}", condition);
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
     * 予定情報の検索クエリを取得する。
     *
     * @param type クエリ種別 (SEARCH：一覧取得, COUNT：件数取得)
     * @param condition 予定情報検索条件
     * @return 予定情報の検索クエリ
     */
    @Lock(LockType.READ)
    private Query getSearchQuery(SearchType type, ScheduleSearchCondition condition) {
        TypedQuery<ScheduleEntity> query;

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();

        Root<ScheduleEntity> poolSchedule = cq.from(ScheduleEntity.class);

        // 項目のパス
        jakarta.persistence.criteria.Path<Long> pathScheduleId = poolSchedule.get(ScheduleEntity_.scheduleId);
        jakarta.persistence.criteria.Path<Date> pathScheduleFromDate = poolSchedule.get(ScheduleEntity_.scheduleFromDate);
        jakarta.persistence.criteria.Path<Long> pathFkOrganizationId = poolSchedule.get(ScheduleEntity_.organizationId);

        // 検索条件
        Predicate where = getSearchWhere(condition, poolSchedule);

        if (type == SearchType.SEARCH) {
            // 一覧取得
            cq.select(poolSchedule)
                    .where(where)
                    .orderBy(cb.asc(pathScheduleFromDate), cb.asc(pathFkOrganizationId), cb.asc(pathScheduleId));
        } else {
            // 件数取得
            cq.select(cb.count(poolSchedule))
                    .where(where);
        }
        query = em.createQuery(cq);

        return query;
    }

    /**
     * 予定情報の検索条件から、Predicate を取得する。
     *
     * @param condition 予定情報検索条件
     * @param poolSchedule 
     * @return Predicate
     */
    @Lock(LockType.READ)
    private Predicate getSearchWhere(ScheduleSearchCondition condition, Root<ScheduleEntity> poolSchedule) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // 項目のパス
        jakarta.persistence.criteria.Path<Date> pathScheduleFromDate = poolSchedule.get(ScheduleEntity_.scheduleFromDate);
        jakarta.persistence.criteria.Path<Date> pathScheduleToDate = poolSchedule.get(ScheduleEntity_.scheduleToDate);
        jakarta.persistence.criteria.Path<Long> pathFkOrganizationId = poolSchedule.get(ScheduleEntity_.organizationId);

        // 検索条件
        List<Predicate> where = new ArrayList<>();

        // 日時範囲の先頭
        if (Objects.nonNull(condition.getFromDate())) {
            where.add(cb.or(cb.greaterThanOrEqualTo(pathScheduleFromDate, condition.getFromDate()), cb.greaterThanOrEqualTo(pathScheduleToDate, condition.getFromDate())));
        }

        // 日時範囲の末尾
        if (Objects.nonNull(condition.getToDate())) {
            where.add(cb.or(cb.lessThanOrEqualTo(pathScheduleFromDate, condition.getToDate()), cb.lessThanOrEqualTo(pathScheduleToDate, condition.getToDate())));
        }

        // 組織ID一覧
        if (Objects.nonNull(condition.getOrganizationIdCollection()) && !condition.getOrganizationIdCollection().isEmpty()) {
            // 指定した組織IDとその子以降の組織IDを、全て検索対象にする。
            Set<Long> ids = organizationRest.getRelatedOrganizationIds(condition.getOrganizationIdCollection());
            where.add(pathFkOrganizationId.in(ids));
        }

        return cb.and(where.toArray(new Predicate[where.size()]));
    }

    public void setOrganizationRest(OrganizationEntityFacadeREST organizationRest) {
        this.organizationRest = organizationRest;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    protected void setEntityManager(EntityManager em) {
        this.em = em;
    }
}
