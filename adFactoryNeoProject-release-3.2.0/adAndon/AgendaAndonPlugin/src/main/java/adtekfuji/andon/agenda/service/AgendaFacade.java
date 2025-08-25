/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.andon.agenda.service;

import adtekfuji.andon.agenda.model.data.ConfigData;
import adtekfuji.rest.RestClient;
import jakarta.ws.rs.core.GenericType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import jakarta.ws.rs.core.MediaType;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;
import jp.adtekfuji.adFactory.entity.actual.WorkRecordInfoEntity;
import jp.adtekfuji.adFactory.entity.agenda.ActualProductInfoEntity;
import jp.adtekfuji.adFactory.entity.agenda.KanbanTopicInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.operation.OperationEntity;
import jp.adtekfuji.adFactory.entity.search.ActualSearchCondition;
import jp.adtekfuji.adFactory.entity.search.KanbanTopicSearchCondition;
import jp.adtekfuji.adFactory.entity.search.OperationSerachCondition;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * アジェンダREST
 *
 * @author s-heya
 */
public class AgendaFacade {

    public enum Type {
        KANBAN,
        ORGANIZATION,
        WORKKANBAN,
    }

    private final Logger logger = LogManager.getLogger();
    private final ConfigData commonData = ConfigData.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final RestClient restClient;

    private final String PATH_AGENDA = "/agenda";
    private final String PATH_TOPIC = "/topic";
    private final String PATH_ACTUAL = "/actual";
    private final String PATH_COUNT = "/count";
    private final String PATH_SEARCH = "/search";
    private final String PATH_RANGE = "/range?";
    private final String PATH_ACTUAL_PRODUCT = "/actualProduction";
    private final String PATH_OPERATION = "/operation";
    private final String PATH_LAST = "/last";


    private final String PATH_HISTORY_COUNT = "/actual/history/%s/count?id=";
    private final String PATH_HISTORY_RANGE = "/actual/history/%s/range?id=";
    private final String PATH_KANBAN = "/kanban/%s";
    private final String PATH_WORKKANBAN = "/kanban/work/%s";
    private final String QUERY_ID = "&id=";
    private final String QUERY_FROMDATE = "&fromDate=";
    private final String QUERY_TODATE = "&toDate=";
    private final String QUERY_NOWDATE = "&nowDate=";
    private final String QUERY_DISPLAYPERIOD = "&displayPeriod=";
    private final String QUERY_FROM = "&from=";
    private final String QUERY_TO = "&to=";

    public AgendaFacade() {
        this.restClient = new RestClient(this.commonData.getAdFactoryServerURI());
    }

    // TODO: 未使用
    /**
     * カンバン別計画実績の個数を取得する。
     *
     * @param condition
     * @return
     */
    public String countTopic(KanbanTopicSearchCondition condition) {
        logger.info("countTopic:{}", condition);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(PATH_AGENDA);
            sb.append(PATH_TOPIC);
            sb.append(PATH_COUNT);
            return (String) restClient.put(sb.toString(), condition, MediaType.TEXT_PLAIN_TYPE, String.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * カンバン別計画実績を取得する。
     *
     * @param condition
     * @return
     */
    public List<KanbanTopicInfoEntity> findTopic(KanbanTopicSearchCondition condition) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(PATH_AGENDA);
            sb.append(PATH_TOPIC);
            sb.append(PATH_SEARCH);
            sb.append(PATH_RANGE);
            return restClient.put(sb.toString(), condition, new GenericType<List<KanbanTopicInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    // TODO: 未使用
    /**
     * カンバン別計画実績を取得する。
     *
     * @param condition
     * @param from
     * @param to
     * @return
     */
    public List<KanbanTopicInfoEntity> findTopic(KanbanTopicSearchCondition condition, int from, int to) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(PATH_AGENDA);
            sb.append(PATH_TOPIC);
            sb.append(PATH_SEARCH);
            sb.append(PATH_RANGE);
            sb.append(QUERY_FROM);
            sb.append(from);
            sb.append(QUERY_TO);
            sb.append(to);
            return restClient.put(sb.toString(), condition, new GenericType<List<KanbanTopicInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }
    
    /**
     * 実績日時を考慮してカンバン別計画実績を取得する。
     *
     * @param condition
     * @return
     */
    public List<KanbanTopicInfoEntity> findActualTopic(KanbanTopicSearchCondition condition) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(PATH_AGENDA);
            sb.append(PATH_TOPIC);
            sb.append(PATH_ACTUAL);
            sb.append(PATH_SEARCH);
            sb.append(PATH_RANGE);
            return restClient.put(sb.toString(), condition, new GenericType<List<KanbanTopicInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * カンバン別計画実績を取得する
     * 
     * @param ids
     * @param nowDate
     * @param displayPeriod
     * @param fromDate
     * @param toDate
     * @return 
     */
    public List<ActualProductInfoEntity> findActualProductTopic(List<Long> ids, Date nowDate, Long displayPeriod, Date fromDate, Date toDate) {
        try {
            final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

            StringBuilder sb = new StringBuilder();
            sb.append(PATH_AGENDA);
            sb.append(PATH_TOPIC);
            sb.append(PATH_ACTUAL_PRODUCT);
            sb.append(PATH_RANGE);
            sb.append(ids.stream().map(id->"id="+id).collect(Collectors.joining("&")));
            sb.append(QUERY_NOWDATE);
            sb.append(df.format(nowDate));
            sb.append(QUERY_DISPLAYPERIOD);
            sb.append(displayPeriod);
            sb.append(QUERY_FROMDATE);
            sb.append(df.format(fromDate));
            sb.append(displayPeriod);
            sb.append(QUERY_TODATE);
            sb.append(df.format(toDate));

            return restClient.find(sb.toString(), new GenericType<List<ActualProductInfoEntity>>() {});

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    // TODO: 未使用
    /**
     * 作業履歴のデータ数を取得する。
     *
     * @param type
     * @param primaryIds
     * @param fromDate
     * @param toDate
     * @return
     */
    public String countHistory(Type type, List<Long> primaryIds, Date fromDate, Date toDate) {
        try {
            if (primaryIds.isEmpty()) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(PATH_HISTORY_COUNT, type.name().toLowerCase()));
            sb.append(primaryIds.get(0));
            for (int ii = 1; ii < primaryIds.size(); ii++) {
                sb.append(QUERY_ID);
                sb.append(primaryIds.get(ii));
            }
            sb.append(QUERY_FROMDATE);
            sb.append(dateFormat.format(fromDate));
            sb.append(QUERY_TODATE);
            sb.append(dateFormat.format(toDate));
            return (String) restClient.find(sb.toString(), String.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 作業履歴を取得する。
     *
     * @param type
     * @param primaryIds
     * @param fromDate
     * @param toDate
     * @return
     */
    public List<WorkRecordInfoEntity> getHistory(Type type, List<Long> primaryIds, Date fromDate, Date toDate) {
        try {
            if (primaryIds.isEmpty()) {
                return new ArrayList<>();
            }
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(PATH_HISTORY_RANGE, type.name().toLowerCase()));
            sb.append(primaryIds.get(0));
            for (int ii = 1; ii < primaryIds.size(); ii++) {
                sb.append(QUERY_ID);
                sb.append(primaryIds.get(ii));
            }
            if (Objects.nonNull(fromDate)) {
                sb.append(QUERY_FROMDATE);
                sb.append(dateFormat.format(fromDate));
            }
            if (Objects.nonNull(toDate)) {
                sb.append(QUERY_TODATE);
                sb.append(dateFormat.format(toDate));
            }
            return restClient.find(sb.toString(), new GenericType<List<WorkRecordInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }
    
    /**
     * Liteモニター 前回の作業者操作実績を取得する。
     * 
     * @param condition 作業者操作実績 検索条件
     * @return 前回の作業者操作実績
     */
    public OperationEntity getLastOperation(OperationSerachCondition condition) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(PATH_OPERATION);
            sb.append(PATH_LAST);
            return (OperationEntity) restClient.put(sb.toString(), condition, OperationEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * Liteモニター 前回の作業実績を取得する。
     *
     * @param condition 工程実績情報 検索条件
     * @return 前回の作業実績
     */
    public ActualResultEntity getLastActualResult(ActualSearchCondition condition) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(PATH_ACTUAL);
            sb.append(PATH_LAST);
            return (ActualResultEntity) restClient.put(sb.toString(), condition, ActualResultEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * Liteモニター カンバン情報を取得する。
     *
     * @param kanbanId カンバンID
     * @return カンバン情報
     */
    public KanbanInfoEntity getKanban(Long kanbanId) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(PATH_KANBAN, String.valueOf(kanbanId)));
            return (KanbanInfoEntity) restClient.find(sb.toString(), KanbanInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * Liteモニター 工程カンバン情報を取得する。
     *
     * @param workKanbanId 工程カンバンID
     * @return 工程カンバン情報
     */
    public WorkKanbanInfoEntity getWorkKanban(Long workKanbanId) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(PATH_WORKKANBAN, String.valueOf(workKanbanId)));
            return (WorkKanbanInfoEntity) restClient.find(sb.toString(), WorkKanbanInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }
}
