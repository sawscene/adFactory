/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.clientservice;

import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import jakarta.ws.rs.core.GenericType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.chart.KanbanSummaryInfoEntity;
import jp.adtekfuji.adFactory.entity.chart.OrganizationSummaryInfoEntity;
import jp.adtekfuji.adFactory.entity.chart.ProductionSummaryInfoEntity;
import jp.adtekfuji.adFactory.entity.chart.TimeLineInfoEntity;
import jp.adtekfuji.adFactory.entity.chart.WorkSummaryInfoEntity;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 作業分析グラフREST API
 *
 * @author s-heya
 */
public class ChartFacade {
    private final Logger logger = LogManager.getLogger();
    private final static FastDateFormat formatter = FastDateFormat.getInstance(DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern());

    private final RestClient restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));

    private final static String TIMELINE_PATH = "/chart/timeline";
    private final static String WORK_PATH = "/chart/work";
    private final static String ORGANIZATION_PATH = "/chart/organization";
    private final static String KANBAN_PATH = "/chart/kanban";
    private final static String WORKKANBAN_PATH = "/chart/workkanban";
    private final static String SUMMARY_SPATH = "/summary";
    private final static String WORKFLOW_ID = "?workflow=";
    private final static String WORK_ID = "&work=";
    private final static String FROM_DATE = "&fromDate=";
    private final static String TO_DATE = "&toDate=";

    /**
     * タイムラインデータを取得する。
     *
     * @param workflowId
     * @param workIds
     * @param fromDate
     * @param toDate
     * @return
     */
    public List<TimeLineInfoEntity> getTimeLine(Long workflowId, List<Long> workIds, Date fromDate, Date toDate) {
        logger.info("getTimeLine: {},{},{},{}", workflowId, workIds, fromDate, toDate);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(TIMELINE_PATH);

            // パラメータ
            sb.append(WORKFLOW_ID);
            sb.append(workflowId);
            sb.append(WORK_ID);
            sb.append(workIds.get(0));

            for (int i = 1; i < workIds.size(); i++) {
                sb.append(WORK_ID);
                sb.append(workIds.get(i));
            }

            sb.append(FROM_DATE);
            sb.append(RestClient.encode(formatter.format(fromDate)));
            sb.append(TO_DATE);
            sb.append(RestClient.encode(formatter.format(toDate)));

            return restClient.find(sb.toString(), new GenericType<List<TimeLineInfoEntity>>() {});
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
     * @param workflowId
     * @param workIds
     * @param fromDate
     * @param toDate
     * @return
     */
    public ProductionSummaryInfoEntity getProductionSummary(Long workflowId, List<Long> workIds, Date fromDate, Date toDate) {
        logger.info("getProductionSummary: {},{},{},{}", workflowId, workIds, fromDate, toDate);
        try {
            if (workIds.isEmpty()) {
                return new ProductionSummaryInfoEntity();
            }

            StringBuilder sb = new StringBuilder();
            sb.append(TIMELINE_PATH);
            sb.append(SUMMARY_SPATH);

            // パラメータ
            sb.append(WORKFLOW_ID);
            sb.append(workflowId);
            sb.append(WORK_ID);
            sb.append(workIds.get(0));

            for (int i = 1; i < workIds.size(); i++) {
                sb.append(WORK_ID);
                sb.append(workIds.get(i));
            }

            sb.append(FROM_DATE);
            sb.append(RestClient.encode(formatter.format(fromDate)));
            sb.append(TO_DATE);
            sb.append(RestClient.encode(formatter.format(toDate)));

            return (ProductionSummaryInfoEntity) restClient.find(sb.toString(), ProductionSummaryInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ProductionSummaryInfoEntity();
        } finally {
           logger.info("getProductionSummary end.");
        }
    }

    /**
     * 工程集計データを取得する。
     *
     * @param workflowId
     * @param fromDate
     * @param toDate
     * @return
     */
    public List<WorkSummaryInfoEntity> calcWork(Long workflowId, Date fromDate, Date toDate) {
        logger.info("getTimeLine: {},{},{}", workflowId, fromDate, toDate);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WORK_PATH);

            // パラメータ
            sb.append(WORKFLOW_ID);
            sb.append(workflowId);
            sb.append(FROM_DATE);
            sb.append(RestClient.encode(formatter.format(fromDate)));
            sb.append(TO_DATE);
            sb.append(RestClient.encode(formatter.format(toDate)));

            return restClient.find(sb.toString(), new GenericType<List<WorkSummaryInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        } finally {
            logger.info("getTimeLine end.");
        }
    }

    /**
     * 生産情報(工程)を取得する。
     *
     * @param workflowId
     * @param fromDate
     * @param toDate
     * @return
     */
    public ProductionSummaryInfoEntity getProductionWork(Long workflowId, Date fromDate, Date toDate) {
        logger.info("getProductionWork: {},{},{}", workflowId, fromDate, toDate);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WORK_PATH);
            sb.append(SUMMARY_SPATH);

            // パラメータ
            sb.append(WORKFLOW_ID);
            sb.append(workflowId);
            sb.append(FROM_DATE);
            sb.append(RestClient.encode(formatter.format(fromDate)));
            sb.append(TO_DATE);
            sb.append(RestClient.encode(formatter.format(toDate)));

            return (ProductionSummaryInfoEntity) restClient.find(sb.toString(), ProductionSummaryInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ProductionSummaryInfoEntity();
        } finally {
           logger.info("getProductionWork end.");
        }
    }

    /**
     * 組織集計データを取得する。
     *
     * @param workflowId
     * @param fromDate
     * @param toDate
     * @return
     */
    public List<OrganizationSummaryInfoEntity> calcOrganization(Long workflowId, Date fromDate, Date toDate) {
        logger.info("getTimeLine: {},{},{}", workflowId, fromDate, toDate);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ORGANIZATION_PATH);

            // パラメータ
            sb.append(WORKFLOW_ID);
            sb.append(workflowId);
            sb.append(FROM_DATE);
            sb.append(RestClient.encode(formatter.format(fromDate)));
            sb.append(TO_DATE);
            sb.append(RestClient.encode(formatter.format(toDate)));

            return restClient.find(sb.toString(), new GenericType<List<OrganizationSummaryInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        } finally {
            logger.info("getTimeLine end.");
        }
    }

    /**
     * 生産情報(組織)を取得する。
     *
     * @param workflowId
     * @param fromDate
     * @param toDate
     * @return
     */
    public ProductionSummaryInfoEntity getProductionOrganization(Long workflowId, Date fromDate, Date toDate) {
        logger.info("getProductionOrganization: {},{},{}", workflowId, fromDate, toDate);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ORGANIZATION_PATH);
            sb.append(SUMMARY_SPATH);

            // パラメータ
            sb.append(WORKFLOW_ID);
            sb.append(workflowId);
            sb.append(FROM_DATE);
            sb.append(RestClient.encode(formatter.format(fromDate)));
            sb.append(TO_DATE);
            sb.append(RestClient.encode(formatter.format(toDate)));

            return (ProductionSummaryInfoEntity) restClient.find(sb.toString(), ProductionSummaryInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ProductionSummaryInfoEntity();
        } finally {
            logger.info("getProductionOrganization end.");
        }
    }

    /**
     * カンバン集計データを取得する
     *
     * @param workflowId 対象工程順ID
     * @param fromDate      対象期間
     * @param toDate        対象期間
     * @return
     */
    public List<KanbanSummaryInfoEntity> calcKanban(Long workflowId, Date fromDate, Date toDate) {
        logger.info("getTimeLine: {},{},{}", workflowId, fromDate, toDate);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);

            // パラメータ
            sb.append(WORKFLOW_ID);
            sb.append(workflowId);
            sb.append(FROM_DATE);
            sb.append(RestClient.encode(formatter.format(fromDate)));
            sb.append(TO_DATE);
            sb.append(RestClient.encode(formatter.format(toDate)));

            return restClient.find(sb.toString(), new GenericType<List<KanbanSummaryInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        } finally {
            logger.info("getTimeLine end.");
        }
    }

    /**
     * 生産情報(カンバン)を取得する。
     *
     * @param workflowId
     * @param fromDate
     * @param toDate
     * @return
     */
    public ProductionSummaryInfoEntity getProductionKanban(Long workflowId, Date fromDate, Date toDate) {
        logger.info("getProductionKanban: {},{},{}", workflowId, fromDate, toDate);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WORKKANBAN_PATH);
            sb.append(SUMMARY_SPATH);

            // パラメータ
            sb.append(WORKFLOW_ID);
            sb.append(workflowId);
            sb.append(FROM_DATE);
            sb.append(RestClient.encode(formatter.format(fromDate)));
            sb.append(TO_DATE);
            sb.append(RestClient.encode(formatter.format(toDate)));

            return (ProductionSummaryInfoEntity) restClient.find(sb.toString(), ProductionSummaryInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ProductionSummaryInfoEntity();
        } finally {
           logger.info("getProductionKanban end.");
        }
    }
}
