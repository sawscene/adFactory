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
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanReportInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * カンバン帳票情報REST
 *
 * @author nar-nakamura
 */
public class KanbanReportInfoFacede {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    private static final String KANBAN_REPORT_PATH = "/report-file";
    private static final String KANBAN_ID_PATH = "/kanban-id";
    private static final String COUNT_PATH = "/count";
    private static final String RANGE_PATH = "/range";
    private final static String QUERY_PATH = "?";
    private final static String AND_PATH = "&";
    private final static String ID_PATH = "id=%s";
    private final static String FROM_TO_PATH = "from=%s&to=%s";

    /**
     * コンストラクタ
     */
    public KanbanReportInfoFacede() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    /**
     * コンストラクタ
     *
     * @param uriBase
     */
    public KanbanReportInfoFacede(String uriBase) {
        restClient = new RestClient(new RestClientProperty(uriBase));
    }

    /**
     * カンバン帳票情報を登録する。
     *
     * @param entity カンバン帳票情報
     * @return サーバーからの応答
     */
    public ResponseEntity regist(KanbanReportInfoEntity entity) {
        logger.debug("regist:{}", entity);
        try {
            return (ResponseEntity) restClient.post(KANBAN_REPORT_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
        }
    }

    /**
     * カンバン帳票情報を更新する。
     *
     * @param entity カンバン帳票情報
     * @return サーバーからの応答
     */
    public ResponseEntity update(KanbanReportInfoEntity entity) {
        logger.debug("update:{}", entity);
        try {
            return (ResponseEntity) restClient.put(KANBAN_REPORT_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
        }
    }

    /**
     * 指定したカンバン帳票IDのカンバン帳票情報を削除する。
     *
     * @param id カンバン帳票ID
     * @return サーバーからの応答
     */
    public ResponseEntity delete(Long id) {
        logger.debug("delete:{}", id);
        try {
            return (ResponseEntity) restClient.delete(KANBAN_REPORT_PATH, id, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
        }
    }

    /**
     * カンバンID一覧を指定して、カンバン帳票情報の件数を取得する。
     *
     * @param kanbanIds カンバンID一覧
     * @return 件数
     */
    public Long countByKanbanId(List<Long> kanbanIds) {
        logger.debug("countByKanbanId:{}", kanbanIds);
        try {
            if (kanbanIds.isEmpty()) {
                return 0L;
            }

            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_REPORT_PATH);
            sb.append(KANBAN_ID_PATH);
            sb.append(COUNT_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(ID_PATH, kanbanIds.get(0)));
            for (int i = 1; i < kanbanIds.size(); i++) {
                sb.append(AND_PATH);
                sb.append(String.format(ID_PATH, kanbanIds.get(i)));
            }

            return Long.parseLong((String) restClient.find(sb.toString(), MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return 0L;
        }
    }

    /**
     * カンバンID一覧を指定して、カンバン帳票情報一覧を取得する。
     * (from, to のどちらかが null の場合は全件取得)
     *
     * @param kanbanIds カンバンID一覧
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @return カンバン帳票情報一覧
     */
    public List<KanbanReportInfoEntity> findByKanbanId(List<Long> kanbanIds, Integer from, Integer to) {
        logger.debug("findByKanbanId:{}, from={}, to={}", kanbanIds, from, to);
        try {
            if (kanbanIds.isEmpty()) {
                return new ArrayList<>();
            }

            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_REPORT_PATH);
            sb.append(KANBAN_ID_PATH);
            sb.append(RANGE_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(ID_PATH, kanbanIds.get(0)));
            for (int i = 1; i < kanbanIds.size(); i++) {
                sb.append(AND_PATH);
                sb.append(String.format(ID_PATH, kanbanIds.get(i)));
            }

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(AND_PATH);
                sb.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
            }

            return restClient.find(sb.toString(), new GenericType<List<KanbanReportInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }
}
