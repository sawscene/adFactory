/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.clientservice;

import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import jakarta.ws.rs.core.GenericType;
import java.util.List;
import jp.adtekfuji.adFactory.entity.kanban.TraceabilityEntity;
import jp.adtekfuji.adFactory.entity.search.TraceabilitySearchCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * トレーサビリティREST
 *
 * @author nar-nakamura
 */
public class TraceabilityFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    private final static String TRACEABILITY_PATH = "/traceability";
    private final static String KANBAN_PATH = "/kanban";
    private final static String WORKKANBAN_PATH = "/kanban/work";

    private final static String SEARCH_PATH = "/search";
    private final static String QUERY_PATH = "?";
    private final static String ID_PATH = "id=%s";

    public TraceabilityFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    public TraceabilityFacade(String uriBase) {
        restClient = new RestClient(new RestClientProperty(uriBase));
    }

    /**
     * 指定されたカンバンIDのトレーサビリティ一覧を取得する。
     *
     * @param kanbanId カンバンID
     * @return トレーサビリティ一覧 (エラー時はnull)
     */
    public List<TraceabilityEntity> findKanbanTraceability(Long kanbanId) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(TRACEABILITY_PATH);
            sb.append(KANBAN_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(ID_PATH, kanbanId));

            return (List<TraceabilityEntity>) restClient.findAll(sb.toString(), new GenericType<List<TraceabilityEntity>>() {});
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 指定された工程カンバンIDのトレーサビリティ一覧を取得する。
     *
     * @param workKanbanId 工程カンバンID
     * @return トレーサビリティ一覧 (エラー時はnull)
     */
    public List<TraceabilityEntity> findWorkKanbanTraceability(Long workKanbanId) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(TRACEABILITY_PATH);
            sb.append(WORKKANBAN_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(ID_PATH, workKanbanId));

            return (List<TraceabilityEntity>) restClient.findAll(sb.toString(), new GenericType<List<TraceabilityEntity>>() {});
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 検索条件を指定して、トレーサビリティ一覧を取得する。
     *
     * @param condition 検索条件
     * @return トレーサビリティ一覧
     */
    public List<TraceabilityEntity> findSearch(TraceabilitySearchCondition condition) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(TRACEABILITY_PATH);
            sb.append(SEARCH_PATH);

            return restClient.put(sb.toString(), condition, new GenericType<List<TraceabilityEntity>>() {});
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 検索条件を指定して、カンバンでグルーピングしたトレーサビリティ一覧を取得する。
     *
     * @param condition 検索条件
     * @return カンバンでグルーピングしたトレーサビリティ一覧
     */
    public List<TraceabilityEntity> findSearchKanban(TraceabilitySearchCondition condition) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(TRACEABILITY_PATH);
            sb.append(SEARCH_PATH);
            sb.append(KANBAN_PATH);

            return restClient.put(sb.toString(), condition, new GenericType<List<TraceabilityEntity>>() {});
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }}
