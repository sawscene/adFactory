/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.clientservice;

import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import jakarta.ws.rs.core.GenericType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jakarta.ws.rs.core.MediaType;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.entity.search.ProducibleWorkKanbanCondition;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author e-mori
 */
public class WorkKanbanInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    private final static String WORKKANBAN_PATH = "/kanban/work";
    private final static String KANBAN_ID_PATH = "/kanban-id";
    private final static String SEPARATE_PATH = "/separate";
    private final static String COUNT_PATH = "/count";
    private final static String RANGE_PATH = "/range";
    private final static String SEARCH_PATH = "/search";
    public static final String NAME_PATH = "/name";
    private static final String UPDATE_OUTPUT_PATH = "/update/output";

    private final static String QUERY_PATH = "?";
    private final static String AND_PATH = "&";
    private final static String KANBANID_PATH = "kanbanid=%s";
    private final static String FROM_TO_PATH = "from=%s&to=%s";

    /**
     * コンストラクタ
     */
    public WorkKanbanInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    /**
     * コンストラクタ
     *
     * @param uriBase 
     */
    public WorkKanbanInfoFacade(String uriBase) {
        restClient = new RestClient(new RestClientProperty(uriBase));
    }

    /**
     * カンバンIDを指定して、通常工程の工程カンバン一覧情報の件数を取得する。
     *
     * @param kanbanid カンバンID
     * @return 件数
     * @throws Exception 
     */
    public Long countFlow(Long kanbanid) throws Exception {
        logger.debug("countFlow:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WORKKANBAN_PATH);
            sb.append(KANBAN_ID_PATH);
            sb.append(COUNT_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(KANBANID_PATH, kanbanid.toString()));

            String count = (String) restClient.find(sb.toString(), MediaType.TEXT_PLAIN_TYPE, String.class);
            return Long.parseLong(count);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * カンバンIDを指定して、通常工程の工程カンバン一覧情報を取得する。
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param kanbanid カンバンID
     * @return 工程カンバン情報一覧
     * @throws Exception 
     */
    public List<WorkKanbanInfoEntity> getRangeFlow(Long from, Long to, Long kanbanid) throws Exception {
        logger.debug("getRangeFlow:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WORKKANBAN_PATH);
            sb.append(KANBAN_ID_PATH);
            sb.append(RANGE_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(KANBANID_PATH, kanbanid.toString()));

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(AND_PATH);
                sb.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
            }
            
            return restClient.find(sb.toString(), new GenericType<List<WorkKanbanInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * カンバンIDを指定して、追加工程の工程カンバン一覧情報の件数を取得する。
     *
     * @param kanbanid カンバンID
     * @return 件数
     * @throws Exception 
     */
    public Long countSeparate(Long kanbanid) throws Exception {
        logger.debug("countSeparate:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WORKKANBAN_PATH);
            sb.append(SEPARATE_PATH);
            sb.append(COUNT_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(KANBANID_PATH, kanbanid.toString()));

            String count = (String) restClient.find(sb.toString(), MediaType.TEXT_PLAIN_TYPE, String.class);
            return Long.parseLong(count);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * カンバンIDを指定して、追加工程の工程カンバン情報一覧を取得する。
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param kanbanid カンバンID
     * @return 工程カンバン情報一覧
     * @throws Exception 
     */
    public List<WorkKanbanInfoEntity> getRangeSeparate(Long from, Long to, Long kanbanid) throws Exception {
        logger.debug("getRangeSeparate:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WORKKANBAN_PATH);
            sb.append(SEPARATE_PATH);
            sb.append(RANGE_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(KANBANID_PATH, kanbanid.toString()));

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(AND_PATH);
                sb.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
            }

            return restClient.find(sb.toString(), new GenericType<List<WorkKanbanInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 検索数取得
     *
     * @param condition 条件
     * @return 検索数
     */
    public Long countSearch(KanbanSearchCondition condition) {
        logger.info("countSearch:{}", condition);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WORKKANBAN_PATH);
            sb.append(SEARCH_PATH);
            sb.append(COUNT_PATH);

            return Long.parseLong((String) restClient.put(sb.toString(), condition, MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return (long) 0;
        }
    }

    /**
     * 指定された範囲のカンバン情報を検索
     *
     * @param condition 条件
     * @return 指定された範囲のカンバン一覧
     */
    public List<WorkKanbanInfoEntity> findSearch(KanbanSearchCondition condition) {
        logger.info("findSearchRange:{}", condition);
        try {
            // 範囲を決めずに取得
            
            return findSearchRange(condition, null, null);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 指定された範囲のカンバン情報を検索
     *
     * @param condition 条件
     * @param from 頭数
     * @param to 尾数
     * @return 指定された範囲のカンバン一覧
     */
    public List<WorkKanbanInfoEntity> findSearchRange(KanbanSearchCondition condition, Long from, Long to) {
        logger.info("findSearchRange:{}", condition);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WORKKANBAN_PATH);
            sb.append(SEARCH_PATH);
            sb.append(RANGE_PATH);

            // パラメータ
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(QUERY_PATH);
                sb.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
            }
            
            return restClient.put(sb.toString(), condition, new GenericType<List<KanbanInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 指定された範囲のカンバン情報を検索
     *
     * @param condition 条件
     * @param from 頭数
     * @param to 尾数
     * @return 指定された範囲のカンバン一覧
     */
    public List<WorkKanbanInfoEntity> findProductWorkKanban(ProducibleWorkKanbanCondition condition, Long from, Long to) {
        logger.info("findProductWorkKanban:{}", condition);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WORKKANBAN_PATH);
            sb.append(SEARCH_PATH);

            // パラメータ
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(QUERY_PATH);
                sb.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
            }

            return restClient.put(sb.toString(), condition, new GenericType<List<WorkKanbanInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 工程カンバンを取得する。
     *
     * @param kanbanId カンバンID
     * @param workName 工程名
     * @return
     */
    public WorkKanbanInfoEntity getWorkKanban(Long kanbanId, String workName) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WORKKANBAN_PATH);
            sb.append(NAME_PATH);

            // パラメータ
            sb.append("?kanbanId=");
            sb.append(kanbanId);
            sb.append("&workName=");
            sb.append(RestClient.encode(workName.trim()));
            
            return (WorkKanbanInfoEntity) restClient.find(sb.toString(), WorkKanbanInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 工程カンバンを取得する。
     *
     * @param kanbanId
     * @return
     */
    public List<WorkKanbanInfoEntity> getWorkKanbans(long kanbanId) {
        try {
            return this.getRangeFlow(null, null, kanbanId);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 指定された範囲のカンバン情報を検索
     *
     * @param ids 条件
     * @return 指定された範囲のカンバン一覧
     */
    public List<WorkKanbanInfoEntity> find(List<Long> ids) {
        logger.info("findSearchRange:{}", ids);

        try {
            if (ids.isEmpty()) {
                return new ArrayList<>();
            }
            StringBuilder sb = new StringBuilder();
            sb.append(WORKKANBAN_PATH);

            // パラメータ
            sb.append("?id=");
            sb.append(ids.get(0));

            for (int i = 1; i < ids.size(); i++) {
                sb.append("&id=");
                sb.append(ids.get(i));
            }
            
            return restClient.find(sb.toString(), new GenericType<List<WorkKanbanInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 工程カンバン情報の実績出力フラグを更新する。
     *
     * @param workKanbanIds 更新対象の工程カンバン一覧
     * @param outputFlag 更新する実績出力フラグ
     * @param outputDate 出力日時
     * @return ResponseEntity
     */
    public ResponseEntity updateOutputFlg(List<Long> workKanbanIds, boolean outputFlag, Date outputDate) {
        logger.debug("updateOutputFlg:{}{}{}", workKanbanIds, outputFlag, outputDate);
        try {
            if (Objects.isNull(workKanbanIds) || workKanbanIds.isEmpty() || Objects.isNull(outputDate)) {
                return new ResponseEntity();
            }

            StringBuilder sb = new StringBuilder();
            sb.append(WORKKANBAN_PATH);
            sb.append(UPDATE_OUTPUT_PATH);

            // パラメータ
            sb.append("?workKanbanIds=");
            sb.append(workKanbanIds.get(0));
            for (int i = 1; i < workKanbanIds.size(); i++) {
                sb.append("&workKanbanIds=");
                sb.append(workKanbanIds.get(i));
            }
            sb.append("&outputFlag=");
            sb.append(outputFlag);
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
            sb.append("&outputDate=");
            sb.append(dateFormat.format(outputDate));
            
            return (ResponseEntity) restClient.put(sb.toString(), null, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
        }
    }
    
}
