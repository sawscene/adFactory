/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.clientservice;

import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import jakarta.ws.rs.core.GenericType;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import jakarta.ws.rs.core.MediaType;
import jp.adtekfuji.adFactory.entity.ListWrapper;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.ResultResponse;
import jp.adtekfuji.adFactory.entity.assemblyparts.AssemblyPartsInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.*;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.entity.search.PropertySearchCondition;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author e-mori
 */
public class KanbanInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();
    private final RestClient restClient;

    private static final String KANBAN_PATH = "/kanban";
    private static final String CREATE_CONDITION_PATH = "/create/condition";
    private static final String COUNT_PATH = "/count";
    private static final String NAME_PATH = "/name";
    private static final String RANGE_PATH = "/range";
    private static final String SEARCH_PATH = "/search";
    private static final String COPY_PATH = "/copy";
    private static final String ID_TARGET_PATH = "/%s";
    private static final String REPORT_PATH = "/report";
    private static final String MULTI_REPORT_PATH = "/multi-report";
    private static final String EXIST_PATH = "/exist";
    private static final String FORCED_PATH = "/forced";
    private static final String PARENT_ID_PATH = "/parent/%d";
    private static final String PLAN_PATH = "/plan";
    private static final String PROPERTY_PATH = "/property";
    private static final String STATUS_PATH = "/status";
    private static final String REMAIN_PATH = "/service/els/rem";
    private static final String PARTS_PATH = "/parts";
    private static final String PRODUCT_PLAN_PATH = "/product-plan/import";
    private static final String WITH_WORKING_WORK = "/withWorkingWK";

    private static final String QUERY_PATH = "?";
    private static final String AND_PATH = "&";
    private static final String ID_PATH = "id=%s";
    private static final String FROM_TO_PATH = "from=%s&to=%s";
    private static final String NAME_QUERY_PATH = "name=%s";
    private static final String PORDER_QUERY_PATH = "porder=%s";
    private static final String MODEL_NAME_QUERY_PATH = "modelName=%s";
    private static final String WORKFLOW_NAME_QUERY_PATH = "workflowName=%s";
    private static final String WORK_NAME_QUERY_PATH = "workName=%s";
    private static final String FROM_DATE_QUERY_PATH = "fromDate=%s";
    private static final String TO_DATE_QUERY_PATH = "toDate=%s";
    private static final String KANBAN_STATUS_QUERY_PATH = "status=%s";
    private static final String AUTH_ID_PATH = "authId=%d";
    private static final String PARENT_PID_PATH = "parentPid=%s";
    private static final String PRODUCT_NUM_QUERY_PATH = "productNo=%s";
    
    /**
     * ラベルクエリパス
     */
    private static final String LABEL_QUERY_PATH = "label=%s";
    
    /**
     * ラベルパス
     */
    private static final String LABEL_PATH = "/label";
    
    private static final String APPROVAL_PATH = "/approval";
    
    /**
     * バージョン情報のクエリパス
     */
    private static final String VERINFO_QUERY_PATH = "verInfo=%s";

    /**
     * コンストラクタ
     */
    public KanbanInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    /**
     * コンストラクタ
     *
     * @param uriBase
     */
    public KanbanInfoFacade(String uriBase) {
        restClient = new RestClient(new RestClientProperty(uriBase));
    }

    /**
     * カンバン情報取得
     *
     * @param id カンバンID
     * @return IDに該当するカンバン
     */
    public KanbanInfoEntity find(Long id) {
        logger.debug("find:{}", id);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append(String.format(ID_TARGET_PATH, id.toString()));

            return (KanbanInfoEntity) restClient.find(sb.toString(), KanbanInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new KanbanInfoEntity();
        }
    }

    /**
     *
     * @param uri
     * @return
     * @throws Exception
     */
    public KanbanInfoEntity findURI(String uri) throws Exception {
        logger.debug("findURI:{}", uri);
        try {
            return (KanbanInfoEntity) restClient.find("/" + uri, KanbanInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     *
     * @param name
     * @return
     */
    public KanbanInfoEntity findName(String name) {
        logger.debug("findName:{}", name);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append(NAME_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(NAME_QUERY_PATH, name));

            return (KanbanInfoEntity) restClient.find(sb.toString(), KanbanInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new KanbanInfoEntity();
        }
    }

    /**
     * 指定した注番の残り台数を取得する
     *
     * @param porder 注番
     * @return 残り台数
     */
    public String findRemain(String porder) {
        logger.debug("findRemain:{}", porder);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append(REMAIN_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(PORDER_QUERY_PATH, porder));

            return (String) restClient.find(sb.toString(), MediaType.TEXT_PLAIN_TYPE, String.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return "0";
        }
    }

    /**
     * 新しいカンバンの登録
     *
     * @param entity 登録するカンバン情報
     * @return 登録の成否
     * @throws java.lang.Exception
     */
    public ResponseEntity regist(KanbanInfoEntity entity) throws Exception {
        logger.debug("regist:{}", entity);
        try {
            // JSONの追加情報を更新する。
            this.updateAddInfo(entity);

            return (ResponseEntity) restClient.post(KANBAN_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 新しいカンバンの登録
     *
     * @param condition 登録するカンバン情報
     * @return 登録の成否
     * @throws java.lang.Exception
     */
    public ResponseEntity createConditon(KanbanCreateCondition condition) throws Exception {
        logger.debug("createConditon:{}", condition);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append(CREATE_CONDITION_PATH);

            return (ResponseEntity) restClient.post(sb.toString(), condition, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * カンバンの更新
     *
     * @param entity
     * @return
     */
    public ResponseEntity update(KanbanInfoEntity entity) throws Exception {
        logger.debug("update:{}", entity);
        try {
            // JSONの追加情報を更新する。
            this.updateAddInfo(entity);

            return (ResponseEntity) restClient.put(KANBAN_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * カンバンのコピー
     *
     * @param entity
     * @return
     */
    public ResponseEntity copy(KanbanInfoEntity entity) {
        logger.debug("copy:{}", entity);
        try {
            // JSONの追加情報を更新する。
            this.updateAddInfo(entity);

            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append(COPY_PATH);
            sb.append(String.format(ID_TARGET_PATH, entity.getKanbanId()));

            return (ResponseEntity) restClient.post(sb.toString(), null, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * カンバンの削除
     *
     * @param id
     * @return
     */
    public ResponseEntity delete(Long id) {
        logger.debug("delete:{}", id);
        try {
            return (ResponseEntity) restClient.delete(KANBAN_PATH, id, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
        }
    }

    /**
     * カンバンの強制削除
     *
     * @param id
     * @return
     */
    public ResponseEntity deleteForced(Long id) {
        logger.debug("deleteForced:{}", id);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append(FORCED_PATH);

            return (ResponseEntity) restClient.delete(sb.toString(), id, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
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
            sb.append(KANBAN_PATH);
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
    public List<KanbanInfoEntity> findSearch(KanbanSearchCondition condition) {
        logger.info("findSearchRange:{}", condition);
        try {
            return this.findSearchRange(condition, null, null);
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
    public List<KanbanInfoEntity> findSearchRange(KanbanSearchCondition condition, Long from, Long to) {
        logger.info("findSearchRange:{}", condition);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
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
     *
     * @param condition
     * @param from
     * @param to
     * @return
     */
    public List<KanbanInfoEntity> searchResults(KanbanSearchCondition condition, Long from, Long to) {
        logger.info("searchResults:{}", condition);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append("/results");
            sb.append(SEARCH_PATH);
            sb.append(RANGE_PATH);

            // パラメータ
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(QUERY_PATH);
                sb.append(String.format(FROM_TO_PATH, String.valueOf(from), String.valueOf(to)));
            }

            return restClient.put(sb.toString(), condition, new GenericType<List<KanbanInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 実績の登録
     *
     * @param report 登録する実績情報
     * @return 登録の成否
     */
    public ActualProductReportResult report(ActualProductReportEntity report) {
        logger.info("report:{}", report);
        try {
            // 完成品情報が存在する場合、シリアル番号情報(JSON)を更新する。
            if (Objects.nonNull(report.getParts())) {
                List<PartsInfoEntity> partsInfos = JsonUtils.jsonToObjects(report.getParts(), PartsInfoEntity[].class);
                for (PartsInfoEntity partsInfo : partsInfos) {
                    // シリアル番号情報一覧をJSON文字列に変換する。
                    String jsonStr = JsonUtils.objectsToJson(partsInfo.getSerialNoInfos());
                    partsInfo.setSerialNoInfo(jsonStr);
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append(REPORT_PATH);

            return (ActualProductReportResult) restClient.post(sb.toString(), report, ActualProductReportResult.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ActualProductReportResult();
        }
    }

    /**
     * 実績の登録
     *
     * @param report 登録する実績情報
     * @return 登録の成否
     */
    public ActualProductReportResult multiReport(ActualProductReportEntity report) {
        logger.info("report:{}", report);
        try {
            // 完成品情報が存在する場合、シリアル番号情報(JSON)を更新する。
            if (Objects.nonNull(report.getParts())) {
                List<PartsInfoEntity> partsInfos = JsonUtils.jsonToObjects(report.getParts(), PartsInfoEntity[].class);
                for (PartsInfoEntity partsInfo : partsInfos) {
                    // シリアル番号情報一覧をJSON文字列に変換する。
                    String jsonStr = JsonUtils.objectsToJson(partsInfo.getSerialNoInfos());
                    partsInfo.setSerialNoInfo(jsonStr);
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append(MULTI_REPORT_PATH);

            return (ActualProductReportResult) restClient.post(sb.toString(), report, ActualProductReportResult.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ActualProductReportResult();
        }
    }


    /**
     *
     * @param entity
     * @return
     * @throws java.lang.Exception
     */
    public KanbanExistCollection exist(KanbanExistCollection entity) throws Exception {
        logger.info("exist");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append(EXIST_PATH);

            return (KanbanExistCollection) restClient.find(sb.toString(), entity, KanbanExistCollection.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * カンバンID一覧を指定して、カンバン情報一覧を取得する。
     *
     * @param ids 条件
     * @return 指定された範囲のカンバン一覧
     */
    public List<KanbanInfoEntity> find(List<Long> ids) {
        return this.find(ids, false);
    }

    /**
     * カンバンID一覧を指定して、カンバン情報一覧を取得する。
     *
     * @param ids 条件
     * @param isDetail 詳細情報を取得する？ (true: する, false: しない)
     * @return 指定された範囲のカンバン一覧
     */
    public List<KanbanInfoEntity> find(List<Long> ids, boolean isDetail) {
        logger.info("find: isDetail={}, ids={}", isDetail, ids);
        try {
            if (ids.isEmpty()) {
                return new ArrayList<>();
            }
            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);

            // パラメータ
            sb.append("?detail=").append(isDetail);

            for (Long id : ids) {
                sb.append("&id=").append(id);
            }

            return restClient.find(sb.toString(), new GenericType<List<KanbanInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 指定されたカンバン階層IDのカンバン情報一覧を取得する。
     *
     * @param parentId カンバン階層ID
     * @param kanbanName カンバン名
     * @param modelName モデル名
     * @param fromDate 開始予定日
     * @param toDate 終了予定日
     * @param kanbanStatus カンバンステータス
     * @return カンバン情報一覧
     */
    public List<KanbanInfoEntity> findByParentId(Long parentId, String kanbanName, String modelName, Date fromDate, Date toDate, List<KanbanStatusEnum> kanbanStatus) {
        logger.info("findByParentId:parentId={}, kanbanName={}, modelName={}, fromDate={}, toDate={}, kanbanStatus={}", parentId, kanbanName, modelName, fromDate, toDate, kanbanStatus);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append(String.format(PARENT_ID_PATH, parentId));

            // パラメータ
            int cnt = 0;

            // カンバン名
            if (Objects.nonNull(kanbanName) && !kanbanName.isEmpty()) {
                sb.append(cnt == 0 ? QUERY_PATH : AND_PATH);
                sb.append(String.format(NAME_QUERY_PATH, URLEncoder.encode(kanbanName, "UTF-8")));
                cnt++;
            }

            // モデル名
            if (Objects.nonNull(modelName) && !modelName.isEmpty()) {
                sb.append(cnt == 0 ? QUERY_PATH : AND_PATH);
                sb.append(String.format(MODEL_NAME_QUERY_PATH, URLEncoder.encode(modelName, "UTF-8")));
                cnt++;
            }

            // 作業予定日
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if (Objects.nonNull(fromDate)) {
                sb.append(cnt == 0 ? QUERY_PATH : AND_PATH);
                sb.append(String.format(FROM_DATE_QUERY_PATH, dateFormat.format(fromDate)));
                cnt++;
            }
            if (Objects.nonNull(toDate)) {
                sb.append(cnt == 0 ? QUERY_PATH : AND_PATH);
                sb.append(String.format(TO_DATE_QUERY_PATH, dateFormat.format(toDate)));
                cnt++;
            }

            // カンバンステータス
            if (Objects.nonNull(kanbanStatus)) {
                for (KanbanStatusEnum status : kanbanStatus) {
                    sb.append(cnt == 0 ? QUERY_PATH : AND_PATH);
                    sb.append(String.format(KANBAN_STATUS_QUERY_PATH, status));
                    cnt++;
                }
            }
            
            if (Objects.nonNull(loginUserInfoEntity.getId())) {
                sb.append(cnt == 0 ? QUERY_PATH : AND_PATH);
                sb.append(String.format(AUTH_ID_PATH, loginUserInfoEntity.getId()));
            }
                            
            return restClient.find(sb.toString(), new GenericType<List<KanbanInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 指定されたカンバン階層IDのカンバン情報一覧を取得する。
     *
     * @param parentId カンバン階層ID
     * @param kanbanName カンバン名
     * @param fromDate 開始予定日
     * @param toDate 終了予定日
     * @param kanbanStatus カンバンステータス
     * @return カンバン情報一覧
     */
    public List<KanbanInfoEntity> findByParentId(Long parentId, String kanbanName, Date fromDate, Date toDate, List<KanbanStatusEnum> kanbanStatus) {
        logger.info("findByParentId:parentId={}, kanbanName={}, fromDate={}, toDate={}, kanbanStatus={}", parentId, kanbanName, fromDate, toDate, kanbanStatus);
        try {
            return findByParentId(parentId, kanbanName, null, fromDate, toDate, kanbanStatus);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 指定されたカンバン階層IDのカンバン情報一覧を取得する。
     *
     * @param parentId カンバン階層ID
     * @param kanbanName カンバン名
     * @param modelName モデル名
     * @param productNo 製造番号
     * @param fromDate 開始予定日
     * @param toDate 終了予定日
     * @param kanbanStatus カンバンステータス
     * @return カンバン情報一覧
     */
    public List<KanbanInfoEntity> findByParentId(Long parentId, String kanbanName, String modelName, String productNo, String workflowName, String workName, Date fromDate, Date toDate, List<KanbanStatusEnum> kanbanStatus) {
        logger.info("findByParentId:parentId={}, kanbanName={}, modelName={}, productNo={}, fromDate={}, toDate={}, kanbanStatus={}", parentId, kanbanName, modelName, productNo, fromDate, toDate, kanbanStatus);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append(String.format(PARENT_ID_PATH, parentId));

            // パラメータ
            int cnt = 0;

            // カンバン名
            if (Objects.nonNull(kanbanName) && !kanbanName.isEmpty()) {
                sb.append(cnt == 0 ? QUERY_PATH : AND_PATH);
                sb.append(String.format(NAME_QUERY_PATH, URLEncoder.encode(kanbanName, "UTF-8")));
                cnt++;
            }

            // モデル名
            if (Objects.nonNull(modelName) && !modelName.isEmpty()) {
                sb.append(cnt == 0 ? QUERY_PATH : AND_PATH);
                sb.append(String.format(MODEL_NAME_QUERY_PATH, URLEncoder.encode(modelName, "UTF-8")));
                cnt++;
            }

            // 製造番号
            if (Objects.nonNull(productNo) && !productNo.isEmpty()) {
                sb.append(cnt == 0 ? QUERY_PATH : AND_PATH);
                sb.append(String.format(PRODUCT_NUM_QUERY_PATH, URLEncoder.encode(productNo, "UTF-8")));
                cnt++;
            }

            // 工程順名
            if (Objects.nonNull(workflowName) && !workflowName.isEmpty()) {
                sb.append(cnt == 0 ? QUERY_PATH : AND_PATH);
                sb.append(String.format(WORKFLOW_NAME_QUERY_PATH, URLEncoder.encode(workflowName, "UTF-8")));
                cnt++;
            }

            // 工程名
            if (Objects.nonNull(workName) && !workName.isEmpty()) {
                sb.append(cnt == 0 ? QUERY_PATH : AND_PATH);
                sb.append(String.format(WORK_NAME_QUERY_PATH, URLEncoder.encode(workName, "UTF-8")));
                cnt++;
            }

            // 作業予定日
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if (Objects.nonNull(fromDate)) {
                sb.append(cnt == 0 ? QUERY_PATH : AND_PATH);
                sb.append(String.format(FROM_DATE_QUERY_PATH, dateFormat.format(fromDate)));
                cnt++;
            }
            if (Objects.nonNull(toDate)) {
                sb.append(cnt == 0 ? QUERY_PATH : AND_PATH);
                sb.append(String.format(TO_DATE_QUERY_PATH, dateFormat.format(toDate)));
                cnt++;
            }

            // カンバンステータス
            if (Objects.nonNull(kanbanStatus)) {
                for (KanbanStatusEnum status : kanbanStatus) {
                    sb.append(cnt == 0 ? QUERY_PATH : AND_PATH);
                    sb.append(String.format(KANBAN_STATUS_QUERY_PATH, status));
                    cnt++;
                }
            }
            sb.append(cnt == 0 ? QUERY_PATH : AND_PATH);

            if(Objects.nonNull(loginUserInfoEntity.getId())) {
                sb.append(String.format(AUTH_ID_PATH, loginUserInfoEntity.getId()));
            }
                            
            return restClient.find(sb.toString(), new GenericType<List<KanbanInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * カンバンの計画時間を変更する。
     *
     * @param condition 計画時間変更条件
     * @param kanbanIds カンバンID一覧
     * @param authId 認証ID(更新者)
     * @return 結果
     */
    public ResponseEntity planChange(PlanChangeCondition condition, List<Long> kanbanIds, Long authId) {
        logger.debug("planChange: condition={}, kanbanIds={}", condition, kanbanIds);
        try {
            if (kanbanIds.isEmpty()) {
                return new ResponseEntity();
            }
            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append(PLAN_PATH);

            // パラメータ
            sb.append("?id=");
            sb.append(kanbanIds.get(0));
            for (int i = 1; i < kanbanIds.size(); i++) {
                sb.append("&id=");
                sb.append(kanbanIds.get(i));
            }

            if (Objects.nonNull(authId)) {
                sb.append(AND_PATH);
                sb.append(String.format(AUTH_ID_PATH, authId));
            }

            return (ResponseEntity) restClient.put(sb.toString(), condition, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
        }
    }

    /**
     * カンバンプロパティ一覧を取得する。
     *
     * @param condition プロパティ検索条件
     * @return カンバンプロパティ一覧
     */
    public List<KanbanPropertyInfoEntity> findKanbanPropSearch(PropertySearchCondition condition) {
        logger.info("findKanbanPropSearch: {}", condition);
        try {
            if (condition.getParentIdList().isEmpty()) {
                return new ArrayList();
            }

            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append(PROPERTY_PATH);
            sb.append(SEARCH_PATH);

            return (List<KanbanPropertyInfoEntity>) restClient.put(sb.toString(), condition, new GenericType<List<KanbanPropertyInfoEntity>>() {
            });

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList();
        }
    }

    /**
     * カンバンのステータスを更新する。
     *
     * @param kanbanIds カンバンID一覧
     * @param status カンバンステータス
     * @param authId 認証ID(更新者)
     * @return 処理応答
     */
    public ResponseEntity updateStatus(List<Long> kanbanIds, KanbanStatusEnum status, Long authId) {
        return updateStatus(kanbanIds, status, false, authId);
    }
    
    /**
     * カンバンのステータスを更新する。
     *
     * @param kanbanIds カンバンID一覧
     * @param status カンバンステータス
     * @param cancel 強制中断
     * @param authId 認証ID(更新者)
     * @return 処理結果
     */
    public ResponseEntity updateStatus(List<Long> kanbanIds, KanbanStatusEnum status, boolean cancel, Long authId) {
        logger.debug("updateStatus: kanbanIds={}, status={}, cancel={}, authId={}", kanbanIds, status, cancel, authId);
        try {
            if (kanbanIds.isEmpty()) {
                return new ResponseEntity();
            }

            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append(STATUS_PATH);
            sb.append("?id=");
            sb.append(kanbanIds.get(0));
            for (int i = 1; i < kanbanIds.size(); i++) {
                sb.append("&id=");
                sb.append(kanbanIds.get(i));
            }
            sb.append(AND_PATH);
            sb.append(String.format(KANBAN_STATUS_QUERY_PATH, status));
            sb.append("&cancel=");
            sb.append(cancel);

            if (Objects.nonNull(authId)) {
                sb.append(AND_PATH);
                sb.append(String.format(AUTH_ID_PATH, authId));
            }

            return (ResponseEntity) restClient.put(sb.toString(), null, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
        }
    }
    
    /**
     * カンバンラベルの更新
     * 
     * @param id カンバンID
     * @param labelIds ラベルID
     * @param verInfo バージョン
     * @param authId 認証ID(更新者)
     * @return 処理結果
     * @throws Exception API実行時に発生した例外
     */
    public ResponseEntity updateLabel(Long id, List<Long> labelIds, Integer verInfo, Long authId) throws Exception {
        logger.info("updateLabel: id={}, labelIds={}, verInfo={}, authId={}", id, labelIds, verInfo, authId);
        try {
            StringBuilder path = new StringBuilder()
                    .append(KANBAN_PATH)
                    .append(LABEL_PATH)
                    .append("/")
                    .append(id)
                    .append(QUERY_PATH)
                    .append(labelIds.stream()
                            .map(labelId -> new StringBuilder().append(String.format(LABEL_QUERY_PATH, labelId)).append(AND_PATH))
                            .collect(Collectors.joining()))
                    .append(String.format(VERINFO_QUERY_PATH, verInfo));
            
            if (Objects.nonNull(authId)) {
                path.append(AND_PATH).append(String.format(AUTH_ID_PATH, authId));
            }

            return (ResponseEntity) restClient.put(path.toString(), null, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * Entity内のプロパティ一覧の内容で、JSONの追加情報を更新する。
     *
     * @param entity カンバン情報
     */
    private void updateAddInfo(KanbanInfoEntity entity) {
        // 追加情報をJSONにする
        String jsonStr = JsonUtils.objectsToJson(entity.getPropertyCollection());
        entity.setKanbanAddInfo(jsonStr);

        // 工程カンバン(通常工程)が存在する場合、工程カンバン(通常工程)の追加情報一覧をJSON文字列に変換してセットする。
        if (Objects.nonNull(entity.getWorkKanbanCollection())) {
            for (WorkKanbanInfoEntity workKanban : entity.getWorkKanbanCollection()) {
                String workKanbanAddInfo = JsonUtils.objectsToJson(workKanban.getPropertyCollection());
                workKanban.setWorkKanbanAddInfo(workKanbanAddInfo);
            }
        }

        // 工程カンバン(追加工程)が存在する場合、工程カンバン(追加工程)の追加情報一覧をJSON文字列に変換してセットする。
        if (Objects.nonNull(entity.getSeparateworkKanbanCollection())) {
            for (WorkKanbanInfoEntity workKanban : entity.getSeparateworkKanbanCollection()) {
                String workKanbanAddInfo = JsonUtils.objectsToJson(workKanban.getPropertyCollection());
                workKanban.setWorkKanbanAddInfo(workKanbanAddInfo);
            }
        }
    }

    /**
     * カンバンIDを指定して、承認情報を追加・更新する。
     *
     * @param id カンバンID
     * @param verInfo バージョン
     * @param approval 承認情報
     * @param authId 認証ID(更新者)
     * @return サーバーからの応答
     */
    public ResponseEntity updateApproval(Long id, Integer verInfo, ApprovalEntity approval, Long authId) {
        logger.info("updateApproval: id={}, approval={}", id, approval);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append(APPROVAL_PATH);
            sb.append("/");
            sb.append(id);
            
            int cnt = 0;

            if (Objects.nonNull(verInfo)) {
                sb.append(cnt == 0 ? QUERY_PATH : AND_PATH);
                sb.append(String.format(VERINFO_QUERY_PATH, verInfo));
                cnt++;
            }

            if (Objects.nonNull(authId)) {
                sb.append(cnt == 0 ? QUERY_PATH : AND_PATH);
                sb.append(String.format(AUTH_ID_PATH, authId));
                cnt++;
            }

            return (ResponseEntity) restClient.put(sb.toString(), approval, ResponseEntity.class);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * JSONファイルから設備をインポートする。
     *
     * @param filePath JSONファイルのパス
     * @return 結果
     */
    public List<ResultResponse> importFile(String filePath) {
        try {
            logger.info("importFile: " + filePath);

            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append("/import");

            return (List<ResultResponse>) restClient.upload(sb.toString(), filePath, new GenericType<List<ResultResponse>>(){});
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * カンバン親PIDを指定して、使用部品情報の件数を取得する。
     *
     * @param kanbanPartsId カンバン親PID
     * @param authId 認証ID
     * @return 件数
     */
    public Long countPartsByKanbanPartsId(String kanbanPartsId, Long authId) {
        return this.countAssemblyParts(kanbanPartsId, null, authId);
    }

    /**
     * カンバン名を指定して、使用部品情報の件数を取得する。
     *
     * @param kanbanName カンバン名
     * @param authId 認証ID
     * @return 件数
     */
    public Long countPartsByKanbanName(String kanbanName, Long authId) {
        return this.countAssemblyParts(null, kanbanName, authId);
    }

    /**
     * カンバン親PIDまたはカンバン名を指定して、使用部品情報の件数を取得する。
     *
     * @param kanbanPartsId カンバン親PID
     * @param kanbanName カンバン名
     * @param authId 認証ID
     * @return 件数
     */
    private Long countAssemblyParts(String kanbanPartsId, String kanbanName, Long authId) {
        logger.info("countAssemblyParts: kanbanPartsId={}, kanbanName={}, authId={}", kanbanPartsId, kanbanName, authId);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append(PARTS_PATH);
            sb.append(COUNT_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            if (Objects.nonNull(kanbanPartsId)) {
                sb.append(String.format(PARENT_PID_PATH, kanbanPartsId));
            } else {
                sb.append(String.format(NAME_QUERY_PATH, kanbanName));
            }

            if (Objects.nonNull(authId)) {
                sb.append(AND_PATH);
                sb.append(String.format(AUTH_ID_PATH, authId));
            }

            return Long.parseLong((String) restClient.find(sb.toString(), MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return (long) 0;
        }
    }

    /**
     * カンバン親PIDを指定して、使用部品情報一覧を取得する。
     *
     * @param kanbanPartsId カンバン親PID
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 使用部品情報一覧
     */
    public List<AssemblyPartsInfoEntity> findPartsByKanbanPartsId(String kanbanPartsId, Integer from, Integer to, Long authId)  {
        return this.findAssemblyParts(kanbanPartsId, null, from, to, authId);
    }

    /**
     * カンバン名を指定して、使用部品情報一覧を取得する。
     *
     * @param kanbanName カンバン名
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 使用部品情報一覧
     */
    public List<AssemblyPartsInfoEntity> findPartsByKanbanName(String kanbanName, Integer from, Integer to, Long authId)  {
        return this.findAssemblyParts(null, kanbanName, from, to, authId);
    }

    /**
     * カンバン親PIDまたはカンバン名を指定して、使用部品情報一覧を取得する。
     *
     * @param kanbanPartsId カンバン親PID
     * @param kanbanName カンバン名
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 使用部品情報一覧
     */
    private List<AssemblyPartsInfoEntity> findAssemblyParts(String kanbanPartsId, String kanbanName, Integer from, Integer to, Long authId)  {
        logger.info("findAssemblyParts: kanbanPartsId={}, kanbanName={}, authId={}", kanbanPartsId, kanbanName, authId);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append(PARTS_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            if (Objects.nonNull(kanbanPartsId)) {
                sb.append(String.format(PARENT_PID_PATH, kanbanPartsId));
            } else {
                sb.append(String.format(NAME_QUERY_PATH, kanbanName));
            }

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(AND_PATH);
                sb.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
            }

            if (Objects.nonNull(authId)) {
                sb.append(AND_PATH);
                sb.append(String.format(AUTH_ID_PATH, authId));
            }

            return restClient.find(sb.toString(), new GenericType<List<AssemblyPartsInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }


    /**
     * 計画取込
     * @param filePath ファイルパス
     * @return 処理結果
     */
    public ResponseEntity importPlanInfo(String filePath) {
        try {
            logger.info("importPlanInfo");

            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append(PRODUCT_PLAN_PATH);

            return (ResponseEntity) restClient.upload(sb.toString(), filePath, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }


    /**
     * 工程順にて作業中の工程カンバンのカウント数を工程カンバン名で取得する
     * @param workflowName
     * @return カウント数
     */
    public List<KanbanInfoEntity> getKanbanWithWorkingWorkByWorkflowNames(List<String> workflowName) {
        logger.debug("countWorkflow:{}", workflowName);
        try {
            StringBuilder path = new StringBuilder();
            path.append(KANBAN_PATH);
            path.append(WITH_WORKING_WORK);

            return restClient.post(path.toString(), new ListWrapper(workflowName), new GenericType<List<KanbanInfoEntity>>(){});
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        } finally {
            logger.debug("addAll : end");
        }
    }

    public void setReadTimeout(Integer time) {
        restClient.setReadTimeout(time);
    }

    public Integer getReadTimeout() {
        return restClient.getReadTimeout();
    }

}
