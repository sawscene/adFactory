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
import java.util.List;
import java.util.Objects;
import jakarta.ws.rs.core.MediaType;
import jp.adtekfuji.adFactory.entity.agenda.AgendaEntity;
import jp.adtekfuji.adFactory.entity.agenda.KanbanTopicInfoEntity;
import jp.adtekfuji.adFactory.entity.search.KanbanTopicSearchCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 予実取得用REST
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.12.12.Mon
 */
public class AgendaFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    private final static String AGENDA_PATH = "/agenda";
    private final static String TOPIC_PATH = "/topic";
    private final static String COUNT_PATH = "/count";
    private final static String RANGE_PATH = "/range";
    private final static String FROM_TO_PATH = "?from=%s&to=%s";
    private final static String SEARCH_PATH = "/search";

    private final static String KANBAN_PATH = "/kanban";
    private final static String ORGANIZATION_PATH = "/organization";

    private final static String QUERY_PATH = "?";
    private final static String QUERY_AND = "&";
    private final static String QUERY_ID = "id=%s";
    private final static String QUERY_DATE = "date=%s";

    public AgendaFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    public AgendaFacade(String uriBase) {
        restClient = new RestClient(new RestClientProperty(uriBase));
    }

    /**
     * 予実情報を検索する
     *
     * @param condition 条件
     * @return 予実情報
     */
    public List<KanbanTopicInfoEntity> findTopic(KanbanTopicSearchCondition condition) {
        logger.info("findTopic:{}", condition);
        try {
            return this.findTopic(condition, null, null);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ArrayList<>();
    }

    /**
     * 予実情報検索数取得
     *
     * @param condition 条件
     * @return 検索数
     */
    public Long countTopic(KanbanTopicSearchCondition condition) {
        logger.info("countTopic:{}", condition);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(AGENDA_PATH);
            sb.append(TOPIC_PATH);
            sb.append(COUNT_PATH);

            return Long.parseLong((String) restClient.put(sb.toString(), condition, MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return 0L;
    }

    /**
     * 指定された範囲の予実情報を検索
     *
     * @param condition 条件
     * @param from 頭数
     * @param to 尾数
     * @return 指定された範囲の予実情報一覧
     */
    public List<KanbanTopicInfoEntity> findTopic(KanbanTopicSearchCondition condition, Long from, Long to) {
        logger.info("findTopic:{},{},{}", condition, from, to);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(AGENDA_PATH);
            sb.append(TOPIC_PATH);
            sb.append(SEARCH_PATH);
            sb.append(RANGE_PATH);

            // パラメータ
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
            }

            return restClient.put(sb.toString(), condition, new GenericType<List<KanbanTopicInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ArrayList<>();
    }

    /**
     * カンバンのスケジュール情報を取得する。
     *
     * @param kanbanId カンバンID
     * @param dateString 作業日
     * @return 予定データ
     * @throws Exception 
     */
    public AgendaEntity findByKanban(Long kanbanId, String dateString) throws Exception {
        logger.info("findByKanban: kanbanId={}, dateString={}", kanbanId, dateString);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(AGENDA_PATH);
            sb.append(KANBAN_PATH);
            sb.append(QUERY_PATH);
            sb.append(String.format(QUERY_ID, kanbanId.toString()));
            sb.append(QUERY_AND);
            sb.append(String.format(QUERY_DATE, dateString));

            return (AgendaEntity) restClient.find(sb.toString(), AgendaEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 作業者のスケジュール情報を取得する。
     *
     * @param organizationId 組織ID
     * @param dateString 作業日
     * @return 予定データ
     * @throws Exception 
     */
    public AgendaEntity findByOrganization(Long organizationId, String dateString) throws Exception {
        logger.info("findByOrganization: organizationId={}, dateString={}", organizationId, dateString);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(AGENDA_PATH);
            sb.append(ORGANIZATION_PATH);
            sb.append(QUERY_PATH);
            sb.append(String.format(QUERY_ID, organizationId.toString()));
            sb.append(QUERY_AND);
            sb.append(String.format(QUERY_DATE, dateString));

            return (AgendaEntity) restClient.find(sb.toString(), AgendaEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }
}
