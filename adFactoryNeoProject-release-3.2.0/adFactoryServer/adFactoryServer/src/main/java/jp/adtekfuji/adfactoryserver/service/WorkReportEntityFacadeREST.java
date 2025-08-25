/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import jakarta.ejb.EJB;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jp.adtekfuji.adfactoryserver.common.ServiceConfig;
import jp.adtekfuji.adfactoryserver.common.SystemConfig;
import jp.adtekfuji.adfactoryserver.entity.view.WorkReportEntity;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 作業日報情報(VIEW)取得用REST
 *
 * @author nar-nakamura
 */
@Stateless
@Path("workreport")
public class WorkReportEntityFacadeREST extends AbstractFacade<WorkReportEntity> {

    private static boolean isWorkReportDirectWorkEditable;

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    @EJB
    private OrganizationEntityFacadeREST organizationRest;

    private final Logger logger = LogManager.getLogger();

    private final List<Integer> workTypes;// 取得対象とする作業種別のリスト (0:直接作業, 1:間接作業, 2:中断時間)

    /**
     * コンストラクタ
     */
    public WorkReportEntityFacadeREST() {
        super(WorkReportEntity.class);

        ServiceConfig config = ServiceConfig.getInstance();

        if (config.getWorkReportSuspendEnabled()) {
            this.workTypes = Arrays.asList(0, 1, 2);// 直接作業, 間接作業, 中断時間
        } else {
            this.workTypes = Arrays.asList(0, 1);// 直接作業, 間接作業
        }

        isWorkReportDirectWorkEditable = SystemConfig.getInstance().isWorkReportDirectWorkEditable();

    }

    /**
     * 指定された期間の作業日報情報一覧を取得する。(作業日・作業者・工程順・注文番号(サブカンバン名)・工程で集計)
     *
     * @param fromDate 先頭年月日 (yyyyMMdd)
     * @param toDate 末尾年月日 (yyyyMMdd)
     * @param organizationIds 組織IDリスト (指定した組織の子以降の組織も対象になる)
     * @param authId 認証ID
     * @return 作業日報情報
     */
    @Lock(LockType.READ)
    @GET
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkReportEntity> sumByOrganizationId(@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate, @QueryParam("id") List<Long> organizationIds, @QueryParam("authId") Long authId) {
        logger.info("sumByOrganizationId: fromDate={}, toDate={}, organizationIds={}, authId={}", fromDate, toDate, organizationIds, authId);
        try {
            if (!isWorkReportDirectWorkEditable) {
                this.em.getEntityManagerFactory().getCache().evict(WorkReportEntity.class);

                TypedQuery<WorkReportEntity> query;
                if (organizationIds.isEmpty()) {
                    // 組織IDの指定がない場合、組織を検索条件に含めないクエリを使用する。
                    query = this.em.createNamedQuery("WorkReportEntity.findFromToDateOrderNumber", WorkReportEntity.class);
                } else {
                    // 指定された組織の子以降の組織も対象にする。
                    Set<Long> targetIds = this.getRelatedOrganizations(organizationIds);

                    // 組織IDの指定がある場合、組織を検索条件に含めるクエリを使用する。
                    query = this.em.createNamedQuery("WorkReportEntity.findFromToDateOrderNumber2", WorkReportEntity.class);
                    query.setParameter("organizationIds", targetIds);
                }

                query.setParameter("fromDate", fromDate);
                query.setParameter("toDate", toDate);
                query.setParameter("workTypes", this.workTypes);

                return query.getResultList();
            }

            // ******* 直接工数変更可 *****
            List<WorkReportEntity> ret = new ArrayList<>();
            if (organizationIds.isEmpty()) {
                if (this.workTypes.contains(0)) {
                    // 直接工数
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findDirectWorkDailyOrder", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    ret.addAll(query.getResultList());
                }

                if (this.workTypes.contains(1)) {
                    // 間接工数
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("WorkReportEntity.findIndirectWorkDailyOrder", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    ret.addAll(query.getResultList());
                }

                if (this.workTypes.contains(2)) {
                    // 中断時間
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findInterruptDailyOrder", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    ret.addAll(query.getResultList());
                }
                return ret;
            } else {
                try (Connection connection = this.em.unwrap(Connection.class)) {
                    Set<Long> targetIds = this.getRelatedOrganizations(organizationIds);
                    java.sql.Array idArray = connection.createArrayOf("integer", targetIds.toArray());

                    if (this.workTypes.contains(0)) {
                        // 直接工数
                        TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findDirectWorkDailyOrder2", WorkReportEntity.class);
                        query.setParameter(1, fromDate);
                        query.setParameter(2, toDate);
                        query.setParameter(3, idArray);
                        ret.addAll(query.getResultList());
                    }

                    if (this.workTypes.contains(1)) {
                        // 間接工数
                        TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("WorkReportEntity.findIndirectWorkDailyOrder2", WorkReportEntity.class);
                        query.setParameter(1, fromDate);
                        query.setParameter(2, toDate);
                        query.setParameter(3, idArray);
                        ret.addAll(query.getResultList());
                    }

                    if (this.workTypes.contains(2)) {
                        // 中断時間
                        TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findInterruptDailyOrder2", WorkReportEntity.class);
                        query.setParameter(1, fromDate);
                        query.setParameter(2, toDate);
                        query.setParameter(3, idArray);
                        ret.addAll(query.getResultList());
                    }
                } catch (SQLException ex) {
                    logger.fatal(ex, ex);
                }
                return ret;
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定された期間の作業日報情報一覧を範囲指定して取得する。(作業日・作業者・工程順・注文番号(サブカンバン名)・工程で集計)
     *
     * ※．一括取得でタイムアウトする場合のみ使用する。 取得範囲に関わらず実行毎にView全体の取得クエリが実行されるので、
     * 1回で取得する件数は多く、分割回数は少なくなるようにして使用すること。
     *
     * @param fromDate 先頭年月日 (yyyyMMdd)
     * @param toDate 末尾年月日 (yyyyMMdd)
     * @param organizationIds 組織IDリスト (指定した組織の子以降の組織も対象になる)
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 指定された範囲の作業日報情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkReportEntity> sumRangeByOrganizationId(@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate, @QueryParam("id") List<Long> organizationIds, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("sumRangeByOrganizationId: fromDate={}, toDate={}, organizationIds={}, from={}, to={}, authId={}", fromDate, toDate, organizationIds, from, to, authId);
        try {
            this.em.getEntityManagerFactory().getCache().evict(WorkReportEntity.class);

            if (!isWorkReportDirectWorkEditable) {
                // ******* 直接工数変更不可 *****
                TypedQuery<WorkReportEntity> query;
                if (organizationIds.isEmpty()) {
                    // 組織IDの指定がない場合、組織を検索条件に含めないクエリを使用する。
                    query = this.em.createNamedQuery("WorkReportEntity.findFromToDateOrderNumber", WorkReportEntity.class);

                } else {
                    // 指定された組織の子以降の組織も対象にする。
                    Set<Long> targetIds = this.getRelatedOrganizations(organizationIds);

                    // 組織IDの指定がある場合、組織を検索条件に含めるクエリを使用する。
                    query = this.em.createNamedQuery("WorkReportEntity.findFromToDateOrderNumber2", WorkReportEntity.class);
                    query.setParameter("organizationIds", targetIds);
                }

                query.setParameter("fromDate", fromDate);
                query.setParameter("toDate", toDate);
                query.setParameter("workTypes", this.workTypes);

                if (Objects.nonNull(from) && Objects.nonNull(to)) {
                    query.setMaxResults(to - from + 1);
                    query.setFirstResult(from);
                }

                return query.getResultList();
            }

            // ******* 直接工数変更可 *****
            List<WorkReportEntity> ret = new ArrayList<>();
            if (organizationIds.isEmpty()) {
                if (this.workTypes.contains(0)) {
                    // 直接工数
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findDirectWorkDailyOrder", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);

                    if (Objects.nonNull(from) && Objects.nonNull(to)) {
                        query.setMaxResults(to - from + 1);
                        query.setFirstResult(from);
                    }

                    ret.addAll(query.getResultList());
                }

                if (this.workTypes.contains(1)) {
                    // 間接工数
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("WorkReportEntity.findIndirectWorkDailyOrder", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);

                    if (Objects.nonNull(from) && Objects.nonNull(to)) {
                        query.setMaxResults(to - from + 1);
                        query.setFirstResult(from);
                    }

                    if (Objects.nonNull(from) && Objects.nonNull(to)) {
                        query.setMaxResults(to - from + 1);
                        query.setFirstResult(from);
                    }

                    ret.addAll(query.getResultList());
                }

                if (this.workTypes.contains(2)) {
                    // 中断時間
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findInterruptDailyOrder", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);

                    if (Objects.nonNull(from) && Objects.nonNull(to)) {
                        query.setMaxResults(to - from + 1);
                        query.setFirstResult(from);
                    }

                    if (Objects.nonNull(from) && Objects.nonNull(to)) {
                        query.setMaxResults(to - from + 1);
                        query.setFirstResult(from);
                    }

                    ret.addAll(query.getResultList());
                }
                return ret;
            } else {
                try (Connection connection = this.em.unwrap(Connection.class)) {
                    Set<Long> targetIds = this.getRelatedOrganizations(organizationIds);
                    java.sql.Array idArray = connection.createArrayOf("integer", targetIds.toArray());

                    if (this.workTypes.contains(0)) {
                        // 直接工数
                        TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findDirectWorkDailyOrder2", WorkReportEntity.class);
                        query.setParameter(1, fromDate);
                        query.setParameter(2, toDate);
                        query.setParameter(3, idArray);

                        if (Objects.nonNull(from) && Objects.nonNull(to)) {
                            query.setMaxResults(to - from + 1);
                            query.setFirstResult(from);
                        }

                        ret.addAll(query.getResultList());
                    }

                    if (this.workTypes.contains(1)) {
                        // 間接工数
                        TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("WorkReportEntity.findIndirectWorkDailyOrder2", WorkReportEntity.class);
                        query.setParameter(1, fromDate);
                        query.setParameter(2, toDate);
                        query.setParameter(3, idArray);

                        if (Objects.nonNull(from) && Objects.nonNull(to)) {
                            query.setMaxResults(to - from + 1);
                            query.setFirstResult(from);
                        }

                        ret.addAll(query.getResultList());
                    }

                    if (this.workTypes.contains(2)) {
                        // 中断時間
                        TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findInterruptDailyOrder2", WorkReportEntity.class);
                        query.setParameter(1, fromDate);
                        query.setParameter(2, toDate);
                        query.setParameter(3, idArray);

                        if (Objects.nonNull(from) && Objects.nonNull(to)) {
                            query.setMaxResults(to - from + 1);
                            query.setFirstResult(from);
                        }

                        ret.addAll(query.getResultList());
                    }
                } catch (SQLException ex) {
                    logger.fatal(ex, ex);
                }
                return ret;
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定された期間の作業日報情報の件数を取得する。(作業日・作業者・工程順・注文番号(サブカンバン名)・工程で集計)
     *
     * @param fromDate 先頭年月日 (yyyyMMdd)
     * @param toDate 末尾年月日 (yyyyMMdd)
     * @param organizationIds 組織IDリスト (指定した組織の子以降の組織も対象になる)
     * @param authId 認証ID
     * @return 件数
     */
    @Lock(LockType.READ)
    @GET
    @Path("count")
    @Consumes({"application/xml", "application/json"})
    @Produces({"text/plain"})
    @ExecutionTimeLogging
    public String countByOrganizationId(@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate, @QueryParam("id") List<Long> organizationIds, @QueryParam("authId") Long authId) {
        logger.info("countByOrganizationId: fromDate={}, toDate={}, organizationIds={}, authId={}", fromDate, toDate, organizationIds, authId);
        try {
            this.em.getEntityManagerFactory().getCache().evict(WorkReportEntity.class);

            if (!isWorkReportDirectWorkEditable) {

                TypedQuery<WorkReportEntity> query;
                if (organizationIds.isEmpty()) {
                    // 組織IDの指定がない場合、組織を検索条件に含めないクエリを使用する。
                    query = this.em.createNamedQuery("WorkReportEntity.findFromToDateOrderNumber", WorkReportEntity.class);
                } else {
                    // 指定された組織の子以降の組織も対象にする。
                    Set<Long> targetIds = this.getRelatedOrganizations(organizationIds);

                    // 組織IDの指定がある場合、組織を検索条件に含めるクエリを使用する。
                    query = this.em.createNamedQuery("WorkReportEntity.findFromToDateOrderNumber2", WorkReportEntity.class);
                    query.setParameter("organizationIds", targetIds);
                }

                query.setParameter("fromDate", fromDate);
                query.setParameter("toDate", toDate);
                query.setParameter("workTypes", this.workTypes);

                return String.valueOf(query.getResultList().size());
            }

            int size = 0;

            // ******* 直接工数変更可 *****
            List<WorkReportEntity> ret = new ArrayList<>();
            if (organizationIds.isEmpty()) {
                if (this.workTypes.contains(0)) {
                    // 直接工数
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findDirectWorkDailyOrder", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    size = query.getResultList().size();
                }

                if (this.workTypes.contains(1)) {
                    // 間接工数
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("WorkReportEntity.findIndirectWorkDailyOrder", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    size = Math.max(query.getResultList().size(), size);
                }

                if (this.workTypes.contains(2)) {
                    // 中断時間
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findInterruptDailyOrder", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    size = Math.max(query.getResultList().size(), size);
                }
                return String.valueOf(size);
            } else {
                try (Connection connection = this.em.unwrap(Connection.class)) {
                    Set<Long> targetIds = this.getRelatedOrganizations(organizationIds);
                    java.sql.Array idArray = connection.createArrayOf("integer", targetIds.toArray());

                    if (this.workTypes.contains(0)) {
                        // 直接工数
                        TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findDirectWorkDailyOrder2", WorkReportEntity.class);
                        query.setParameter(1, fromDate);
                        query.setParameter(2, toDate);
                        query.setParameter(3, idArray);
                        size = query.getResultList().size();
                    }

                    if (this.workTypes.contains(1)) {
                        // 間接工数
                        TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("WorkReportEntity.findIndirectWorkDailyOrder2", WorkReportEntity.class);
                        query.setParameter(1, fromDate);
                        query.setParameter(2, toDate);
                        query.setParameter(3, idArray);
                        size = Math.max(query.getResultList().size(), size);
                    }

                    if (this.workTypes.contains(2)) {
                        // 中断時間
                        TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findInterruptDailyOrder2", WorkReportEntity.class);
                        query.setParameter(1, fromDate);
                        query.setParameter(2, toDate);
                        query.setParameter(3, idArray);
                        size = Math.max(query.getResultList().size(), size);
                    }
                    return String.valueOf(size);
                } catch (SQLException ex) {
                    logger.fatal(ex, ex);
                }
                return String.valueOf(0);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定された作業者・日付の作業日報情報一覧を取得する。(作業日・作業者・工程順・カンバン名・工程で集計)
     *
     * @param organizationId 組織ID
     * @param workDate 作業日 (yyyyMMdd)
     * @param authId 認証ID
     * @return 作業日報情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("daily/kanban")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkReportEntity> findOrganizationIdDailyKanban(@QueryParam("organizationId") Long organizationId, @QueryParam("workDate") String workDate, @QueryParam("authId") Long authId) {
        logger.info("findOrganizationIdDailyKanban: organizationId={}, workDate={}, authId={}", organizationId, workDate, authId);
        try {
            this.em.getEntityManagerFactory().getCache().evict(WorkReportEntity.class);

            if (!isWorkReportDirectWorkEditable) {
                // ******* 直接工数変更不可 *****
                TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("WorkReportEntity.findOrganizationIdDailyKanban", WorkReportEntity.class);
                query.setParameter("organizationId", organizationId);
                query.setParameter("workDate", workDate);
                query.setParameter("workTypes", this.workTypes);

                return query.getResultList();
            }

            // ******* 直接工数変更可 *****
            List<WorkReportEntity> ret = new ArrayList<>();
            try (Connection connection = this.em.unwrap(Connection.class)) {
                java.sql.Array idArray = connection.createArrayOf("integer", Collections.singletonList(organizationId).toArray());

                if (this.workTypes.contains(0)) {
                    // 直接作業
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findDirectWorkDailyKanban2", WorkReportEntity.class);
                    query.setParameter(1, workDate);
                    query.setParameter(2, workDate);
                    query.setParameter(3, idArray);
                    ret.addAll(query.getResultList());
                }

                if (this.workTypes.contains(1)) {
                    // 間接作業
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("WorkReportEntity.findIndirectWorkDaily2", WorkReportEntity.class);
                    query.setParameter(1, workDate);
                    query.setParameter(2, workDate);
                    query.setParameter(3, idArray);
                    ret.addAll(query.getResultList());
                }

                if (this.workTypes.contains(2)) {
                    // 作業中断
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findInterruptDaily2", WorkReportEntity.class);
                    query.setParameter(1, workDate);
                    query.setParameter(2, workDate);
                    query.setParameter(3, idArray);
                    ret.addAll(query.getResultList());
                }

            } catch (SQLException ex) {
                logger.fatal(ex, ex);
            }

            return ret;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定された作業者・日付の作業日報情報一覧を取得する。(作業日・作業者・工程順・製造番号・工程で集計)
     *
     * @param organizationId 組織ID
     * @param workDate 作業日 (yyyyMMdd)
     * @param authId 認証ID
     * @return 作業日報情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("daily/production")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkReportEntity> findOrganizationIdDailyProduction(@QueryParam("organizationId") Long organizationId, @QueryParam("workDate") String workDate, @QueryParam("authId") Long authId) {
        logger.info("findOrganizationIdDailyProduction: organizationId={}, workDate={}, authId={}", organizationId, workDate, authId);
        try {
            this.em.getEntityManagerFactory().getCache().evict(WorkReportEntity.class);

            if (!isWorkReportDirectWorkEditable) {
                // ******* 直接工数変更不可 *****
                TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("WorkReportEntity.findOrganizationIdDailyProduction", WorkReportEntity.class);
                query.setParameter("organizationId", organizationId);
                query.setParameter("workDate", workDate);
                query.setParameter("workTypes", this.workTypes);
                return query.getResultList();
            }

            // ******* 直接工数変更可 *****
            List<WorkReportEntity> ret = new ArrayList<>();
            try (Connection connection = this.em.unwrap(Connection.class)) {
                java.sql.Array idArray = connection.createArrayOf("integer", Collections.singletonList(organizationId).toArray());
                if (this.workTypes.contains(0)) {
                    // 直接作業
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findDirectWorkDailyProduct2", WorkReportEntity.class);
                    query.setParameter(1, workDate);
                    query.setParameter(2, workDate);
                    query.setParameter(3, idArray);
                    ret.addAll(query.getResultList());
                }

                if (this.workTypes.contains(1)) {
                    // 間接作業
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("WorkReportEntity.findIndirectWorkDaily2", WorkReportEntity.class);
                    query.setParameter(1, workDate);
                    query.setParameter(2, workDate);
                    query.setParameter(3, idArray);
                    ret.addAll(query.getResultList());
                }

                if (this.workTypes.contains(2)) {
                    // 作業中断
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findInterruptDaily2", WorkReportEntity.class);
                    query.setParameter(1, workDate);
                    query.setParameter(2, workDate);
                    query.setParameter(3, idArray);
                    ret.addAll(query.getResultList());
                }
            } catch (SQLException ex) {
                logger.fatal(ex, ex);
            }

            return ret;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定された期間の作業日報情報一覧を取得する。(作業日・作業者・工程順・カンバン名・工程で集計)
     *
     * @param fromDate 先頭年月日 (yyyyMMdd)
     * @param toDate 末尾年月日 (yyyyMMdd)
     * @param organizationIds 組織IDリスト (指定した組織の子以降の組織も対象になる)
     * @param authId 認証ID
     * @return 作業日報情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("kanban")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkReportEntity> sumKanban(@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate, @QueryParam("id") List<Long> organizationIds, @QueryParam("authId") Long authId) {
        logger.info("sumKanban: fromDate={}, toDate={}, organizationIds={}, authId={}", fromDate, toDate, organizationIds, authId);
        try {
            this.em.getEntityManagerFactory().getCache().evict(WorkReportEntity.class);
            // ******** 直接工数変更不可 *******
            if (!isWorkReportDirectWorkEditable) {
                TypedQuery<WorkReportEntity> query;
                if (organizationIds.isEmpty()) {
                    // 組織IDの指定がない場合、組織を検索条件に含めないクエリを使用する。
                    query = this.em.createNamedQuery("WorkReportEntity.findFromToDateKanban", WorkReportEntity.class);
                } else {
                    // 指定された組織の子以降の組織も対象にする。
                    Set<Long> targetIds = this.getRelatedOrganizations(organizationIds);

                    // 組織IDの指定がある場合、組織を検索条件に含めるクエリを使用する。
                    query = this.em.createNamedQuery("WorkReportEntity.findFromToDateKanban2", WorkReportEntity.class);
                    query.setParameter("organizationIds", targetIds);
                }
                query.setParameter("fromDate", fromDate);
                query.setParameter("toDate", toDate);
                query.setParameter("workTypes", this.workTypes);

                return query.getResultList();
            }

            // ******* 直接工数変更可 *****
            // 工程ID指定
            if (organizationIds.isEmpty()) {
                List<WorkReportEntity> ret = new ArrayList<>();
                if (this.workTypes.contains(0)) {
                    // 直接作業
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findDirectWorkDailyKanban", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    ret.addAll(query.getResultList());
                }

                if (this.workTypes.contains(1)) {
                    // 間接作業
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("WorkReportEntity.findIndirectWorkDaily", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    ret.addAll(query.getResultList());
                }

                if (this.workTypes.contains(2)) {
                    // 作業中断
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findInterruptDaily", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    ret.addAll(query.getResultList());
                }
                return ret;
            }


            List<WorkReportEntity> ret = new ArrayList<>();
            try (Connection connection = this.em.unwrap(Connection.class)) {
                Set<Long> targetIds = this.getRelatedOrganizations(organizationIds);
                java.sql.Array idArray = connection.createArrayOf("integer", targetIds.toArray());

                if (this.workTypes.contains(0)) {
                    // 直接作業
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findDirectWorkDailyKanban2", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    query.setParameter(3, idArray);
                    ret.addAll(query.getResultList());
                }

                if (this.workTypes.contains(1)) {
                    // 間接作業
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("WorkReportEntity.findIndirectWorkDaily2", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    query.setParameter(3, idArray);
                    ret.addAll(query.getResultList());
                }

                if (this.workTypes.contains(2)) {
                    // 作業中断
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findInterruptDaily2", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    query.setParameter(3, idArray);
                    ret.addAll(query.getResultList());
                }

            } catch (SQLException ex) {
                logger.fatal(ex, ex);
            }

            return ret;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定された期間の作業日報情報の件数を取得する。(作業日・作業者・工程順・カンバン名・工程で集計)
     *
     * @param fromDate 先頭年月日 (yyyyMMdd)
     * @param toDate 末尾年月日 (yyyyMMdd)
     * @param organizationIds 組織IDリスト (指定した組織の子以降の組織も対象になる)
     * @param authId 認証ID
     * @return 件数
     */
    @Lock(LockType.READ)
    @GET
    @Path("kanban/count")
    @Consumes({"application/xml", "application/json"})
    @Produces({"text/plain"})
    @ExecutionTimeLogging
    public String countKanban(@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate, @QueryParam("id") List<Long> organizationIds, @QueryParam("authId") Long authId) {
        logger.info("countKanban: fromDate={}, toDate={}, organizationIds={}, authId={}", fromDate, toDate, organizationIds, authId);
        try {
            this.em.getEntityManagerFactory().getCache().evict(WorkReportEntity.class);

            // ****** 直接工数変更不可
            if (!isWorkReportDirectWorkEditable) {
                TypedQuery<WorkReportEntity> query;
                if (organizationIds.isEmpty()) {
                    // 組織IDの指定がない場合、組織を検索条件に含めないクエリを使用する。
                    query = this.em.createNamedQuery("WorkReportEntity.findFromToDateKanban", WorkReportEntity.class);
                } else {
                    // 指定された組織の子以降の組織も対象にする。
                    Set<Long> targetIds = this.getRelatedOrganizations(organizationIds);

                    // 組織IDの指定がある場合、組織を検索条件に含めるクエリを使用する。
                    query = this.em.createNamedQuery("WorkReportEntity.findFromToDateKanban2", WorkReportEntity.class);
                    query.setParameter("organizationIds", targetIds);
                }

                query.setParameter("fromDate", fromDate);
                query.setParameter("toDate", toDate);
                query.setParameter("workTypes", this.workTypes);

                return String.valueOf(query.getResultList().size());
            }

            // ******* 直接工数変更可 *****
            // 工程ID指定
            if (organizationIds.isEmpty()) {
                int max = 0;
                if (this.workTypes.contains(0)) {
                    // 直接作業
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findDirectWorkDailyKanban", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    max = query.getResultList().size();
                }

                if (this.workTypes.contains(1)) {
                    // 間接作業
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("WorkReportEntity.findIndirectWorkDaily", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    max = Math.max(max, query.getResultList().size());
                }

                if (this.workTypes.contains(2)) {
                    // 作業中断
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findInterruptDaily", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    max = Math.max(max, query.getResultList().size());
                }
                return String.valueOf(max);
            }



            try (Connection connection = this.em.unwrap(Connection.class)) {
                Set<Long> targetIds = this.getRelatedOrganizations(organizationIds);
                java.sql.Array idArray = connection.createArrayOf("integer", targetIds.toArray());

                int max = 0;
                if (this.workTypes.contains(0)) {
                    // 直接作業
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findDirectWorkDailyKanban2", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    query.setParameter(3, idArray);
                    max = query.getResultList().size();
                }

                if (this.workTypes.contains(1)) {
                    // 間接作業
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("WorkReportEntity.findIndirectWorkDaily2", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    query.setParameter(3, idArray);
                    max = Math.max(max, query.getResultList().size());
                }

                if (this.workTypes.contains(2)) {
                    // 作業中断
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findInterruptDaily2", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    query.setParameter(3, idArray);
                    max = Math.max(max, query.getResultList().size());
                }
                return String.valueOf(max);
            } catch (SQLException ex) {
                logger.fatal(ex, ex);
            }

            return "0";
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定された期間の作業日報情報一覧を取得する。(作業日・作業者・工程順・製造番号・工程で集計)
     *
     * @param fromDate 先頭年月日 (yyyyMMdd)
     * @param toDate 末尾年月日 (yyyyMMdd)
     * @param organizationIds 組織IDリスト (指定した組織の子以降の組織も対象になる)
     * @param authId 認証ID
     * @return 作業日報情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("production")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkReportEntity> sumProduction(@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate, @QueryParam("id") List<Long> organizationIds, @QueryParam("authId") Long authId) {
        logger.info("sumProduction: fromDate={}, toDate={}, organizationIds={}, authId={}", fromDate, toDate, organizationIds, authId);
        try {
            this.em.getEntityManagerFactory().getCache().evict(WorkReportEntity.class);

            // ****** 直接工数変更不可
            if (!isWorkReportDirectWorkEditable) {
                TypedQuery<WorkReportEntity> query;
                if (organizationIds.isEmpty()) {
                    // 組織IDの指定がない場合、組織を検索条件に含めないクエリを使用する。
                    query = this.em.createNamedQuery("WorkReportEntity.findFromToDateProduction", WorkReportEntity.class);
                } else {
                    // 指定された組織の子以降の組織も対象にする。
                    Set<Long> targetIds = this.getRelatedOrganizations(organizationIds);

                    // 組織IDの指定がある場合、組織を検索条件に含めるクエリを使用する。
                    query = this.em.createNamedQuery("WorkReportEntity.findFromToDateProduction2", WorkReportEntity.class);
                    query.setParameter("organizationIds", targetIds);
                }

                query.setParameter("fromDate", fromDate);
                query.setParameter("toDate", toDate);
                query.setParameter("workTypes", this.workTypes);

                return query.getResultList();
            }

            // ******* 直接工数変更可 *****
            if (organizationIds.isEmpty()) {
                List<WorkReportEntity> ret = new ArrayList<>();
                if (this.workTypes.contains(0)) {
                    // 直接作業
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findDirectWorkDailyProduct", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    ret.addAll(query.getResultList());
                }

                if (this.workTypes.contains(1)) {
                    // 間接作業
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("WorkReportEntity.findIndirectWorkDaily", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    ret.addAll(query.getResultList());
                }

                if (this.workTypes.contains(2)) {
                    // 作業中断
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findInterruptDaily", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    ret.addAll(query.getResultList());
                }
                return ret;
            }


            List<WorkReportEntity> ret = new ArrayList<>();
            try (Connection connection = this.em.unwrap(Connection.class)) {
                Set<Long> targetIds = this.getRelatedOrganizations(organizationIds);
                java.sql.Array idArray = connection.createArrayOf("integer", targetIds.toArray());
                if (this.workTypes.contains(0)) {
                    // 直接作業
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findDirectWorkDailyProduct2", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    query.setParameter(3, idArray);
                    ret.addAll(query.getResultList());
                }

                if (this.workTypes.contains(1)) {
                    // 間接作業
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("WorkReportEntity.findIndirectWorkDaily2", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    query.setParameter(3, idArray);
                    ret.addAll(query.getResultList());
                }

                if (this.workTypes.contains(2)) {
                    // 作業中断
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findInterruptDaily2", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    query.setParameter(3, idArray);
                    ret.addAll(query.getResultList());
                }
            } catch (SQLException ex) {
                logger.fatal(ex, ex);
            }

            return ret;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定された期間の作業日報情報の件数を取得する。(作業日・作業者・工程順・製造番号・工程で集計)
     *
     * @param fromDate 先頭年月日 (yyyyMMdd)
     * @param toDate 末尾年月日 (yyyyMMdd)
     * @param organizationIds 組織IDリスト (指定した組織の子以降の組織も対象になる)
     * @param authId 認証ID
     * @return 件数
     */
    @Lock(LockType.READ)
    @GET
    @Path("production/count")
    @Consumes({"application/xml", "application/json"})
    @Produces({"text/plain"})
    @ExecutionTimeLogging
    public String countProduction(@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate, @QueryParam("id") List<Long> organizationIds, @QueryParam("authId") Long authId) {
        logger.info("countProduction: fromDate={}, toDate={}, organizationIds={}, authId={}", fromDate, toDate, organizationIds, authId);
        try {
            this.em.getEntityManagerFactory().getCache().evict(WorkReportEntity.class);
            // ****** 直接工数変更不可
            if (!isWorkReportDirectWorkEditable) {
                TypedQuery<WorkReportEntity> query;
                if (organizationIds.isEmpty()) {
                    // 組織IDの指定がない場合、組織を検索条件に含めないクエリを使用する。
                    query = this.em.createNamedQuery("WorkReportEntity.findFromToDateProduction", WorkReportEntity.class);
                } else {
                    // 指定された組織の子以降の組織も対象にする。
                    Set<Long> targetIds = this.getRelatedOrganizations(organizationIds);

                    // 組織IDの指定がある場合、組織を検索条件に含めるクエリを使用する。
                    query = this.em.createNamedQuery("WorkReportEntity.findFromToDateProduction2", WorkReportEntity.class);
                    query.setParameter("organizationIds", targetIds);
                }

                query.setParameter("fromDate", fromDate);
                query.setParameter("toDate", toDate);
                query.setParameter("workTypes", this.workTypes);

                return String.valueOf(query.getResultList().size());
            }

            // ******* 直接工数変更可 *****
            if (organizationIds.isEmpty()) {
                int max = 0;
                if (this.workTypes.contains(0)) {
                    // 直接作業
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findDirectWorkDailyProduct", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    max = query.getResultList().size();
                }

                if (this.workTypes.contains(1)) {
                    // 間接作業
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("WorkReportEntity.findIndirectWorkDaily", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    max = Math.max(max, query.getResultList().size());
                }

                if (this.workTypes.contains(2)) {
                    // 作業中断
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findInterruptDaily", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    max = Math.max(max, query.getResultList().size());
                }
                return String.valueOf(max);
            }



            try (Connection connection = this.em.unwrap(Connection.class)) {
                int max = 0;
                Set<Long> targetIds = this.getRelatedOrganizations(organizationIds);
                java.sql.Array idArray = connection.createArrayOf("integer", targetIds.toArray());
                if (this.workTypes.contains(0)) {
                    // 直接作業
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findDirectWorkDailyProduct2", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    query.setParameter(3, idArray);
                    max = Math.max(max, query.getResultList().size());
                }

                if (this.workTypes.contains(1)) {
                    // 間接作業
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("WorkReportEntity.findIndirectWorkDaily2", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    query.setParameter(3, idArray);
                    max = Math.max(max, query.getResultList().size());
                }

                if (this.workTypes.contains(2)) {
                    // 作業中断
                    TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findInterruptDaily2", WorkReportEntity.class);
                    query.setParameter(1, fromDate);
                    query.setParameter(2, toDate);
                    query.setParameter(3, idArray);
                    max = Math.max(max, query.getResultList().size());
                }

                return String.valueOf(max);
            } catch (SQLException ex) {
                logger.fatal(ex, ex);
            }

            return "0";

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定した組織IDとその子以降の組織ID一覧を取得する。
     *
     * @param organizationIds 組織ID一覧
     * @return 指定した組織IDとその子以降の組織ID一覧
     */
    @Lock(LockType.READ)
    private Set<Long> getRelatedOrganizations(List<Long> organizationIds) {
        Set<Long> ids = new HashSet<>();
        ids.addAll(organizationIds);
        for (Long parentId : organizationIds) {
            ids.addAll(this.organizationRest.getOrganizationChildren(parentId));
        }
        return ids;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    protected void setEntityManager(EntityManager em) {
        this.em = em;
    }
}
