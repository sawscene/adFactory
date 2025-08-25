/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import adtekfuji.utility.DateUtils;

import java.util.*;
import java.util.stream.Collectors;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TemporalType;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

import jp.adtekfuji.adFactory.entity.chart.SummaryItem;
import jp.adtekfuji.adFactory.entity.chart.SummaryTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.actual.OrganizaionWorkRecordEntity;
import jp.adtekfuji.adfactoryserver.entity.chart.*;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * チャートREST
 *
 * @author s-heya
 */
@Stateless
@Path("chart")
public class ChartFacadeREST {

    private static final String pattern[] = {DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern(), "yyyy-MM-dd"};

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    private final Logger logger = LogManager.getLogger();

    /**
     * タイムラインデータを取得する。
     *
     * @param workflowId 対象工程順ID
     * @param workIds    対象工程ID
     * @param fromDate   対象期間
     * @param toDate     対象期間
     * @param authId     認証ID
     * @return
     */
    @Lock(LockType.READ)
    @GET
    @Path("timeline")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<TimeLineEntity> getTimeLine(@QueryParam("workflow") final Long workflowId, @QueryParam("work") final List<Long> workIds, @QueryParam("fromDate") final String fromDate, @QueryParam("toDate") final String toDate, @QueryParam("authId") Long authId) {
        try {
            logger.info("getTimeLine start: {}, {}, {}, {}", workflowId, workIds, fromDate, toDate);

            if (Objects.isNull(workflowId) || Objects.isNull(workIds) || workIds.isEmpty()) {
                return new ArrayList<>();
            }

            Date fromDateTime = Objects.nonNull(fromDate) ? org.apache.commons.lang3.time.DateUtils.parseDate(fromDate, pattern) : new Date();
            //fromDateTime = DateUtils.getBeginningOfDate(fromDateTime);

            Date toDateTime = Objects.nonNull(toDate) ? org.apache.commons.lang3.time.DateUtils.parseDate(toDate, pattern) : new Date();
            //toDateTime = DateUtils.getEndOfDate(toDateTime);

            Query query = em.createNamedQuery("TimeLineEntity.findForTimeline");
            query.setParameter("fromDate", fromDateTime, TemporalType.TIMESTAMP);
            query.setParameter("toDate", toDateTime, TemporalType.TIMESTAMP);
            query.setParameter("workflowId", workflowId);
            query.setParameter("workIds", workIds);

            List<TimeLineEntity> entities = query.getResultList();
            return entities;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        } finally {
            logger.info("getTimeLine end.");
        }
    }

    /**
     * 生産情報(タイムライン)を取得する。
     *
     * @param workflowId 工程順ID
     * @param workIds    工程ID
     * @param fromDate   日時範囲の先頭
     * @param toDate     日時範囲の末尾
     * @param authId     認証ID
     * @return
     */
    @Lock(LockType.READ)
    @GET
    @Path("timeline/summary")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ProductionSummaryEntity getProductionTimeLine(@QueryParam("workflow") final Long workflowId, @QueryParam("work") final List<Long> workIds, @QueryParam("fromDate") final String fromDate, @QueryParam("toDate") final String toDate, @QueryParam("authId") Long authId) {
        try {
            logger.info("getProductionTimeLine start: {}, {}, {}, {}", workflowId, workIds, fromDate, toDate);

            if (Objects.isNull(workflowId) || Objects.isNull(workIds) || workIds.isEmpty()) {
                return new ProductionSummaryEntity();
            }

            ProductionSummaryEntity entity = new ProductionSummaryEntity();

            List<TimeLineEntity> timeLine = this.getTimeLine(workflowId, workIds, fromDate, toDate, authId);
            entity.setTimeLine(timeLine);

            Date fromDateTime = Objects.nonNull(fromDate) ? org.apache.commons.lang3.time.DateUtils.parseDate(fromDate, pattern) : new Date();
            Date toDateTime = Objects.nonNull(toDate) ? org.apache.commons.lang3.time.DateUtils.parseDate(toDate, pattern) : new Date();

            List<SummaryItem> items = new LinkedList<>();

            // 対象期間中の生産数
            Query query1 = em.createNamedQuery("TimeLineEntity.countProduction");
            query1.setParameter("fromDate", fromDateTime, TemporalType.TIMESTAMP);
            query1.setParameter("toDate", toDateTime, TemporalType.TIMESTAMP);
            query1.setParameter("workflowId", workflowId);
            Long countProduction = (Long) query1.getSingleResult();
            items.add(new SummaryItem(SummaryTypeEnum.PRODUCT, countProduction));

            // 対象期間中の仕掛数
            Query query2 = em.createNamedQuery("TimeLineEntity.countInProcess");
            query2.setParameter("fromDate", fromDateTime, TemporalType.TIMESTAMP);
            query2.setParameter("toDate", toDateTime, TemporalType.TIMESTAMP);
            query2.setParameter("workflowId", workflowId);
            Long countInProcess = (Long) query2.getSingleResult();
            items.add(new SummaryItem(SummaryTypeEnum.INPROCESS, countInProcess));

            // 対象期間中の中断数
            Query query3 = em.createNamedQuery("TimeLineEntity.summarySuspend");
            query3.setParameter("fromDate", fromDateTime, TemporalType.TIMESTAMP);
            query3.setParameter("toDate", toDateTime, TemporalType.TIMESTAMP);
            query3.setParameter("workflowId", workflowId);
            query3.setParameter("workIds", workIds);
            List<SummaryItem> summarySuspend = query3.getResultList();
            items.addAll(summarySuspend);

            // 対象期間中の遅延数
            Query query4 = em.createNamedQuery("TimeLineEntity.summaryDelay");
            query4.setParameter("fromDate", fromDateTime, TemporalType.TIMESTAMP);
            query4.setParameter("toDate", toDateTime, TemporalType.TIMESTAMP);
            query4.setParameter("workflowId", workflowId);
            query4.setParameter("workIds", workIds);
            List<SummaryItem> summaryDelay = query4.getResultList();
            items.addAll(summaryDelay);

            entity.setSummaryItems(items);

            return entity;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ProductionSummaryEntity();
        } finally {
            logger.info("getProductionTimeLine end.");
        }
    }

    /**
     * カンバン集計データを取得する
     *
     * @param workflowId 対象工程順ID
     * @param fromDate   対象期間
     * @param toDate     対象期間
     * @param authId     認証ID
     * @return
     */
    @Lock(LockType.READ)
    @GET
    @Path("kanban")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<KanbanSummaryEntity> calcKanban(@QueryParam("workflow") final Long workflowId, @QueryParam("fromDate") final String fromDate, @QueryParam("toDate") final String toDate, @QueryParam("authId") Long authId) {
        try {
            logger.info("calcKanban start: {}, {}, {}", workflowId, fromDate, toDate);

            if (Objects.isNull(workflowId)) {
                return new ArrayList<>();
            }

            Date fromDateTime = Objects.nonNull(fromDate) ? org.apache.commons.lang3.time.DateUtils.parseDate(fromDate, pattern) : new Date();
            fromDateTime = DateUtils.getBeginningOfDate(fromDateTime);

            Date toDateTime = Objects.nonNull(toDate) ? org.apache.commons.lang3.time.DateUtils.parseDate(toDate, pattern) : new Date();
            toDateTime = DateUtils.getEndOfDate(toDateTime);

            Query query = em.createNamedQuery("KanbanSummaryEntity.calcKanban");
            query.setParameter(1, fromDateTime, TemporalType.TIMESTAMP);
            query.setParameter(2, toDateTime, TemporalType.TIMESTAMP);
            query.setParameter(3, workflowId);

            List<KanbanSummaryEntity> entities = query.getResultList();
            return entities;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        } finally {
            logger.info("calcKanban end.");
        }
    }

    /**
     * 生産情報(カンバン)を取得する。
     *
     * @param workflowId 工程順ID
     * @param fromDate   日時範囲の先頭
     * @param toDate     日時範囲の末尾
     * @param authId     認証ID
     * @return
     */
    @Lock(LockType.READ)
    @GET
    @Path("kanban/summary")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ProductionSummaryEntity getProductionKanban(@QueryParam("workflow") final Long workflowId, @QueryParam("fromDate") final String fromDate, @QueryParam("toDate") final String toDate, @QueryParam("authId") Long authId) {
        try {
            logger.info("getProductionKanban start: {}, {}, {}", workflowId, fromDate, toDate);

            if (Objects.isNull(workflowId)) {
                return new ProductionSummaryEntity();
            }

            Date fromDateTime = Objects.nonNull(fromDate) ? org.apache.commons.lang3.time.DateUtils.parseDate(fromDate, pattern) : new Date();
            fromDateTime = DateUtils.getBeginningOfDate(fromDateTime);

            Date toDateTime = Objects.nonNull(toDate) ? org.apache.commons.lang3.time.DateUtils.parseDate(toDate, pattern) : new Date();
            toDateTime = DateUtils.getEndOfDate(toDateTime);

            Query query = em.createNamedQuery("KanbanSummaryEntity.calcKanban");
            query.setParameter(1, fromDateTime, TemporalType.TIMESTAMP);
            query.setParameter(2, toDateTime, TemporalType.TIMESTAMP);
            query.setParameter(3, workflowId);
            List<KanbanSummaryEntity> entities = query.getResultList();

            List<SummaryItem> items = new LinkedList<>();

            // 対象期間中のカンバン数
            Query query1 = em.createNamedQuery("TimeLineEntity.countProduction");
            query1.setParameter("fromDate", fromDateTime, TemporalType.TIMESTAMP);
            query1.setParameter("toDate", toDateTime, TemporalType.TIMESTAMP);
            query1.setParameter("workflowId", workflowId);

            Long countProduction = (Long) query1.getSingleResult();
            if (Objects.isNull(countProduction)) {
                countProduction = 0L;
            }

            items.add(new SummaryItem(SummaryTypeEnum.PRODUCT, countProduction));

            // カンバン毎の平均作業時間
            Query query2 = em.createNamedQuery("KanbanSummaryEntity.countWorkTime");
            query2.setParameter("fromDate", fromDateTime, TemporalType.TIMESTAMP);
            query2.setParameter("toDate", toDateTime, TemporalType.TIMESTAMP);
            query2.setParameter("workflowId", workflowId);

            Long countWorkTime = (Long) query2.getSingleResult();
            if (Objects.isNull(countWorkTime)) {
                countWorkTime = 0L;
            }

            long average = 0;
            if (!entities.isEmpty()) {
                average = countWorkTime / entities.size();
            }
            items.add(new SummaryItem(SummaryTypeEnum.AVG_WORK_TIME, average));

            // 1台あたりの作業時間 (総時間 / ロット数の合計)
            Query query3 = em.createNamedQuery("KanbanSummaryEntity.sumLotQuantity");
            query3.setParameter("fromDate", fromDateTime, TemporalType.TIMESTAMP);
            query3.setParameter("toDate", toDateTime, TemporalType.TIMESTAMP);
            query3.setParameter("workflowId", workflowId);

            Long sumLotQuantity = (Long) query3.getSingleResult();

            long workTimePerUnit = 0;
            if (Objects.isNull(sumLotQuantity)) {
                sumLotQuantity = 0L;
            } else {
                workTimePerUnit = countWorkTime / sumLotQuantity;
            }

            items.add(new SummaryItem(SummaryTypeEnum.PRODUCTION_VOLUME, sumLotQuantity));
            items.add(new SummaryItem(SummaryTypeEnum.WORK_TIME_PER_UNIT, workTimePerUnit));

            ProductionSummaryEntity entity = new ProductionSummaryEntity();
            entity.setKanban(entities);
            entity.setSummaryItems(items);

            return entity;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ProductionSummaryEntity();
        } finally {
            logger.info("getProductionKanban end.");
        }
    }

    /**
     * 生産情報(カンバン)を取得する。
     *
     * @param workflowId 工程順ID
     * @param fromDate   日時範囲の先頭
     * @param toDate     日時範囲の末尾
     * @param authId     認証ID
     * @return
     */
    @Lock(LockType.READ)
    @GET
    @Path("workkanban/summary")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ProductionSummaryEntity getProductionWorkKanban(@QueryParam("workflow") final Long workflowId, @QueryParam("fromDate") final String fromDate, @QueryParam("toDate") final String toDate, @QueryParam("authId") Long authId) {
        try {
            logger.info("getProductionWorkKanban start: {}, {}, {}", workflowId, fromDate, toDate);

            if (Objects.isNull(workflowId)) {
                return new ProductionSummaryEntity();
            }

            Date fromDateTime = Objects.nonNull(fromDate) ? org.apache.commons.lang3.time.DateUtils.parseDate(fromDate, pattern) : new Date();
            fromDateTime = DateUtils.getBeginningOfDate(fromDateTime);

            Date toDateTime = Objects.nonNull(toDate) ? org.apache.commons.lang3.time.DateUtils.parseDate(toDate, pattern) : new Date();
            toDateTime = DateUtils.getEndOfDate(toDateTime);

            Query query = em.createNamedQuery("WorkKanbanSummaryEntity.calcWorkKanban");
            query.setParameter(1, fromDateTime, TemporalType.TIMESTAMP);
            query.setParameter(2, toDateTime, TemporalType.TIMESTAMP);
            query.setParameter(3, workflowId);
            List<WorkKanbanSummaryEntity> entities = query.getResultList();

            Map<Long, List<WorkKanbanSummaryEntity>> workKanbanSummaryEntitiesMap = entities.stream()
                    .collect(Collectors.groupingBy(WorkKanbanSummaryEntity::getKanbanId));

            List<SummaryItem> items = new LinkedList<>();

            // カンバンの数
            final int countProduction = workKanbanSummaryEntitiesMap.size();
            items.add(new SummaryItem(SummaryTypeEnum.PRODUCT, (long) countProduction));

            // 総作業時間
            final long sumWorkTime = entities.stream().mapToLong(WorkKanbanSummaryEntity::getWorkTimes).sum();

            // 総生産数
            Long sumLotQuantity = workKanbanSummaryEntitiesMap
                    .values()
                    .stream()
                    .map(l -> l.get(0))
                    .map(WorkKanbanSummaryEntity::getLotQuantity)
                    .map(l-> Objects.isNull(l) || l==0 ? 1 : l)
                    .reduce((long)0, Long::sum);
            items.add(new SummaryItem(SummaryTypeEnum.PRODUCTION_VOLUME, sumLotQuantity));

            // 平均作業時間
            final Long average = 0!=countProduction ? sumWorkTime/countProduction : 0;
            items.add(new SummaryItem(SummaryTypeEnum.AVG_WORK_TIME, average));

            // 1台当たりの作業時間
            final Long workTimePerUnit = 0!=sumLotQuantity ? sumWorkTime/sumLotQuantity : 0;
            items.add(new SummaryItem(SummaryTypeEnum.WORK_TIME_PER_UNIT, workTimePerUnit));

            ProductionSummaryEntity entity = new ProductionSummaryEntity();
            entity.setWorkKanban(entities);
            entity.setSummaryItems(items);

            return entity;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ProductionSummaryEntity();
        } finally {
            logger.info("getProductionWorkKanban end.");
        }
    }


    /**
     * 工程集計データを取得する。
     *
     * @param workflowId 工程順ID
     * @param fromDate   日時範囲の先頭
     * @param toDate     日時範囲の末尾
     * @param authId     認証ID
     * @return
     */
    @Lock(LockType.READ)
    @GET
    @Path("work")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkSummaryEntity> calcWork(@QueryParam("workflow") final Long workflowId, @QueryParam("fromDate") final String fromDate, @QueryParam("toDate") final String toDate, @QueryParam("authId") Long authId) {
        try {
            logger.info("calcWork start: {}, {}, {}", workflowId, fromDate, toDate);

            if (Objects.isNull(workflowId)) {
                return new ArrayList<>();
            }

            Date fromDateTime = Objects.nonNull(fromDate) ? org.apache.commons.lang3.time.DateUtils.parseDate(fromDate, pattern) : new Date();
            fromDateTime = DateUtils.getBeginningOfDate(fromDateTime);

            Date toDateTime = Objects.nonNull(toDate) ? org.apache.commons.lang3.time.DateUtils.parseDate(toDate, pattern) : new Date();
            toDateTime = DateUtils.getEndOfDate(toDateTime);

            Query query = em.createNamedQuery("WorkSummaryEntity.calcWork");
            query.setParameter(1, fromDateTime, TemporalType.TIMESTAMP);
            query.setParameter(2, toDateTime, TemporalType.TIMESTAMP);
            query.setParameter(3, workflowId);

            List<WorkSummaryEntity> entities = query.getResultList();
            return entities;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        } finally {
            logger.info("calcWork end.");
        }
    }

    /**
     * 生産情報(工程)を取得する。
     *
     * @param workflowId 工程順ID
     * @param fromDate   日時範囲の先頭
     * @param toDate     日時範囲の末尾
     * @param authId     認証ID
     * @return
     */
    @Lock(LockType.READ)
    @GET
    @Path("work/summary")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ProductionSummaryEntity getProductionWork(@QueryParam("workflow") final Long workflowId, @QueryParam("fromDate") final String fromDate, @QueryParam("toDate") final String toDate, @QueryParam("authId") Long authId) {
        try {
            logger.info("getProductionWork start: {}, {}, {}", workflowId, fromDate, toDate);

            if (Objects.isNull(workflowId)) {
                return new ProductionSummaryEntity();
            }

            Date fromDateTime = Objects.nonNull(fromDate) ? org.apache.commons.lang3.time.DateUtils.parseDate(fromDate, pattern) : new Date();
            fromDateTime = DateUtils.getBeginningOfDate(fromDateTime);

            Date toDateTime = Objects.nonNull(toDate) ? org.apache.commons.lang3.time.DateUtils.parseDate(toDate, pattern) : new Date();
            toDateTime = DateUtils.getEndOfDate(toDateTime);

            Query query = em.createNamedQuery("WorkSummaryEntity.calcWork");
            query.setParameter(1, fromDateTime, TemporalType.TIMESTAMP);
            query.setParameter(2, toDateTime, TemporalType.TIMESTAMP);
            query.setParameter(3, workflowId);
            List<WorkSummaryEntity> entities = query.getResultList();

            List<SummaryItem> items = new LinkedList<>();

            // 対象期間中の生産数
            if (!entities.isEmpty()) {
                final long kanbanCount = entities.get(0).getKanbanCount();
                items.add(new SummaryItem(SummaryTypeEnum.PRODUCT, kanbanCount));
            } else {
                items.add(new SummaryItem(SummaryTypeEnum.PRODUCT, (long) 0));
            }

            // 平均作業時間
            final double average = entities.stream().mapToDouble(WorkSummaryEntity::getAvgWorkTime).average().orElse(0.0);
            items.add(new SummaryItem(SummaryTypeEnum.AVG_WORK_TIME, (long) average));

            // 標準偏差
            if (!entities.isEmpty()) {
                final double sigma =
                        Math.sqrt(
                                entities.stream()
                                        .mapToDouble(WorkSummaryEntity::getAvgWorkTime)
                                        .reduce(0, (result, element) -> result + (element - average) * (element - average))
                                        / entities.size());
                items.add(new SummaryItem(SummaryTypeEnum.STDDEV_WORK_TIME, (long) sigma));
            } else {
                items.add(new SummaryItem(SummaryTypeEnum.STDDEV_WORK_TIME, (long) 0));
            }

            ProductionSummaryEntity entity = new ProductionSummaryEntity();
            entity.setWork(entities);
            entity.setSummaryItems(items);

            return entity;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ProductionSummaryEntity();
        } finally {
            logger.info("getProductionWork end.");
        }
    }

    /**
     * 組織集計データを取得する。
     *
     * @param workflowId 工程順ID
     * @param fromDate   日時範囲の先頭
     * @param toDate     日時範囲の末尾
     * @param authId     認証ID
     * @return
     */
    @Lock(LockType.READ)
    @GET
    @Path("organization")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<OrganizationSummaryEntity> calcOrganization(@QueryParam("workflow") final Long workflowId, @QueryParam("fromDate") final String fromDate, @QueryParam("toDate") final String toDate, @QueryParam("authId") Long authId) {
        try {
            logger.info("calcOrganization start: {}, {}, {}", workflowId, fromDate, toDate);

            if (Objects.isNull(workflowId)) {
                return new ArrayList<>();
            }

            em.getEntityManagerFactory().getCache().evict(OrganizaionWorkRecordEntity.class);

            Date fromDateTime = Objects.nonNull(fromDate) ? org.apache.commons.lang3.time.DateUtils.parseDate(fromDate, pattern) : new Date();
            fromDateTime = DateUtils.getBeginningOfDate(fromDateTime);

            Date toDateTime = Objects.nonNull(toDate) ? org.apache.commons.lang3.time.DateUtils.parseDate(toDate, pattern) : new Date();
            toDateTime = DateUtils.getEndOfDate(toDateTime);

            Query query = em.createNamedQuery("OrganizationSummaryEntity.calcOrganization");
            query.setParameter(1, fromDateTime, TemporalType.TIMESTAMP);
            query.setParameter(2, toDateTime, TemporalType.TIMESTAMP);
            query.setParameter(3, workflowId);

            List<OrganizationSummaryEntity> entities = query.getResultList();
            return entities;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        } finally {
            logger.info("calcOrganization end.");
        }
    }

    /**
     * 生産情報(組織)を取得する。
     *
     * @param workflowId 工程順ID
     * @param fromDate   日時範囲の先頭
     * @param toDate     日時範囲の末尾
     * @param authId     認証ID
     * @return
     */
    @Lock(LockType.READ)
    @GET
    @Path("organization/summary")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ProductionSummaryEntity getProductionOrganization(@QueryParam("workflow") final Long workflowId, @QueryParam("fromDate") final String fromDate, @QueryParam("toDate") final String toDate, @QueryParam("authId") Long authId) {
        try {
            logger.info("getProductionOrganization start: {}, {}, {}", workflowId, fromDate, toDate);

            if (Objects.isNull(workflowId)) {
                return new ProductionSummaryEntity();
            }

            em.getEntityManagerFactory().getCache().evict(OrganizaionWorkRecordEntity.class);

            Date fromDateTime = Objects.nonNull(fromDate) ? org.apache.commons.lang3.time.DateUtils.parseDate(fromDate, pattern) : new Date();
            fromDateTime = DateUtils.getBeginningOfDate(fromDateTime);

            Date toDateTime = Objects.nonNull(toDate) ? org.apache.commons.lang3.time.DateUtils.parseDate(toDate, pattern) : new Date();
            toDateTime = DateUtils.getEndOfDate(toDateTime);

            Query query = em.createNamedQuery("OrganizationSummaryEntity.calcOrganization");
            query.setParameter(1, fromDateTime, TemporalType.TIMESTAMP);
            query.setParameter(2, toDateTime, TemporalType.TIMESTAMP);
            query.setParameter(3, workflowId);

            List<OrganizationSummaryEntity> entities = query.getResultList();


            // 対象期間中の生産数
            final int kanbanCount = entities.stream()
                    .collect(Collectors.groupingBy(OrganizationSummaryEntity::getWorkId))
                    .entrySet()
                    .stream()
                    .mapToInt(l->l.getValue().stream().mapToInt(OrganizationSummaryEntity::getKanbanCount).sum())
                    .findFirst().orElse(0);

            List<SummaryItem> items = new LinkedList<>();
            items.add(new SummaryItem(SummaryTypeEnum.PRODUCT, (long) kanbanCount));

            // 平均作業時間
            final double average = entities.stream().mapToDouble(OrganizationSummaryEntity::getAvgWorkTime).average().orElse(0.0);
            items.add(new SummaryItem(SummaryTypeEnum.AVG_WORK_TIME, (long) average));

            // 標準偏差
            if (!entities.isEmpty()) {
                final double sigma =
                        Math.sqrt(
                                entities.stream()
                                        .mapToDouble(OrganizationSummaryEntity::getAvgWorkTime)
                                        .reduce(0, (result, element) -> result + (element - average) * (element - average))
                                        / entities.size());
                items.add(new SummaryItem(SummaryTypeEnum.STDDEV_WORK_TIME, (long) sigma));
            } else {
                items.add(new SummaryItem(SummaryTypeEnum.STDDEV_WORK_TIME, (long) 0));
            }

            ProductionSummaryEntity entity = new ProductionSummaryEntity();
            entity.setOrganization(entities);
            entity.setSummaryItems(items);

            return entity;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ProductionSummaryEntity();
        } finally {
            logger.info("getProductionOrganization end.");
        }
    }
}
