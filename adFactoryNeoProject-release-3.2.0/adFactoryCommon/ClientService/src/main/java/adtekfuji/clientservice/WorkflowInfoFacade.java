/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.clientservice;

import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import adtekfuji.utility.StringUtils;
import jakarta.ws.rs.core.GenericType;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.ws.rs.core.MediaType;
import jp.adtekfuji.adFactory.entity.ListWrapper;
import jp.adtekfuji.adFactory.entity.ObjectParam;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.ResultResponse;
import jp.adtekfuji.adFactory.entity.lite.LiteWorkflowInfo;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.response.ResponseWorkflowInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.ConWorkflowWorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowDataCheckInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.enumerate.WorkflowDateCheckErrorTypeEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程順情報取得用RESTクラス
 *
 * @author ta.ito
 */
public class WorkflowInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();

    private final static String WORKFLOW_PATH = "/workflow";
    private final static String WORK_PATH = "/work";
    private final static String COUNT_PATH = "/count";
    private final static String RANGE_PATH = "/range";
    private final static String COPY_PATH = "/copy";
    private final static String ID_TARGET_PATH = "/%s";
    private final static String NAME_PATH = "/name";
    private final static String REVISION_PATH = "/revision";
    private final static String EXIST_ASSIGNED_KANBAN = "/exist/assigned-kanban";
    private final static String CHECK_WORKFLOW = "/check-workflow";
    private final static String LITE_PATH = "/lite";
    private final static String ALL_PATH = "/all";
    private final static String WORKING_WK_COUNT_PATH = "/workingWKCount";
    private final static String AUTHID_PATH = "authId=%s";

    private final static String QUERY_PATH = "?";
    private final static String AND_PATH = "&";
    private final static String SEPARATOR_PATH = "/";
    private final static String ID_PATH = "id=";
    private final static String FROM_TO_PATH = "from=%s&to=%s";
    private final static String NAME_QUERY = "name=%s";
    private final static String USER_PATH = "user=%s";
    private final static String REV_QUERY = "rev=%d";
    private final static String GET_LATEST_REV = "getlatestrev=%s";
    private final static String IMPORT_SUSPEND_KANBAN = "importSuspend=%s";

    public WorkflowInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    public WorkflowInfoFacade(String uriBase) {
        restClient = new RestClient(new RestClientProperty(uriBase));
    }

    /**
     * 工程順を取得
     *
     * @param id 工程順ID
     * @return 工程順
     */
    public WorkflowInfoEntity find(Long id) {
        return find(id, null);
    }

    /**
     * 工程順を取得
     *
     * @param id 工程順ID
     * @param getLatestRev 最新版数を取得する？
     * @return 工程順
     */
    public WorkflowInfoEntity find(Long id, Boolean getLatestRev) {
        logger.debug("find:{}", id);
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORKFLOW_PATH);
            path.append(String.format(ID_TARGET_PATH, id.toString()));

            // パラメータ
            if (Objects.nonNull(getLatestRev)) {
                path.append(QUERY_PATH);
                path.append(String.format(GET_LATEST_REV, String.valueOf(getLatestRev)));
            }

            return (WorkflowInfoEntity) restClient.find(path.toString(), WorkflowInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 工程順を取得
     *
     * @param name 工程順名
     * @return 工程順
     */
    public WorkflowInfoEntity findName(String name) {
        return this.findName(name, null);
    }

    /**
     * 工程順を取得
     *
     * @param name 工程順名
     * @param rev 工程順の版数
     * @return 工程順
     */
    public WorkflowInfoEntity findName(String name, Integer rev) {
        logger.debug("findName:{}", name);
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORKFLOW_PATH);
            path.append(NAME_PATH);

            // パラメータ
            path.append(QUERY_PATH);
            path.append(String.format(NAME_QUERY, name));

            if (Objects.nonNull(rev)) {
                path.append(AND_PATH);
                path.append(String.format(REV_QUERY, rev));
            }

            if (Objects.nonNull(loginUserInfoEntity.getId()) && !LoginUserInfoEntity.ADMIN_LOGIN_ID.equals(loginUserInfoEntity.getLoginId())) {
                path.append(AND_PATH);
                path.append(String.format(USER_PATH, loginUserInfoEntity.getId()));
            }

            return (WorkflowInfoEntity) restClient.find(path.toString(), WorkflowInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 工程順一覧の数を取得
     *
     * @return 工程順一覧の数
     */
    public Long getWorkflowCount() {
        logger.debug("getWorkflowCount:start");
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORKFLOW_PATH);
            path.append(COUNT_PATH);

            String count = (String) restClient.find(path.toString(), MediaType.TEXT_PLAIN_TYPE, String.class);
            return Long.parseLong(count);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return 0l;
    }

    /**
     * 工程順一覧を範囲指定で取得
     *
     * @param from 頭数
     * @param to 尾数
     * @return 工程順一覧
     */
    public List<WorkflowInfoEntity> getWorkflowRange(Long from, Long to) {
        logger.debug("getWorkflowRange:start");
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORKFLOW_PATH);
            path.append(RANGE_PATH);

            // パラメータ
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                path.append(QUERY_PATH);
                path.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
            }

            return restClient.findAll(path.toString(), new GenericType<List<WorkflowInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ArrayList<>();
    }

    /**
     * 新しい工程順の登録
     *
     * @param workflowInfo
     * @return レスポンス
     */
    public ResponseEntity registWork(WorkflowInfoEntity workflowInfo) {
        logger.debug("registWorkflow:{}", workflowInfo);
        try {

            // 追加情報をJSONにする
            String jsonStr = JsonUtils.objectsToJson(workflowInfo.getKanbanPropertyTemplateInfoCollection());
            workflowInfo.setWorkflowAddInfo(jsonStr);

            return (ResponseEntity) restClient.post(WORKFLOW_PATH, workflowInfo, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ResponseEntity();
    }

    /**
     * 工程順の更新
     *
     * @param workflowInfo
     * @return レスポンス
     */
    public ResponseEntity updateWork(WorkflowInfoEntity workflowInfo) {
        logger.debug("updateWorkflow:{}", workflowInfo);
        try {

            // 追加情報をJSONにする
            String jsonStr = JsonUtils.objectsToJson(workflowInfo.getKanbanPropertyTemplateInfoCollection());
            workflowInfo.setWorkflowAddInfo(jsonStr);

            return (ResponseEntity) restClient.put(WORKFLOW_PATH, workflowInfo, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ResponseEntity();
    }

    /**
     * 工程順のコピー
     *
     * @param workflowInfo
     * @return レスポンス
     */
    public ResponseEntity copyWork(WorkflowInfoEntity workflowInfo) {
        logger.debug("copyWorkflow:{}", workflowInfo);
        try {

            // 追加情報をJSONにする
            String jsonStr = JsonUtils.objectsToJson(workflowInfo.getKanbanPropertyTemplateInfoCollection());
            workflowInfo.setWorkflowAddInfo(jsonStr);

            StringBuilder path = new StringBuilder();
            path.append(WORKFLOW_PATH);
            path.append(COPY_PATH);
            path.append(SEPARATOR_PATH);
            path.append(workflowInfo.getWorkflowId());

            return (ResponseEntity) restClient.post(path.toString(), workflowInfo, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ResponseEntity();
    }

    /**
     * 工程順の削除
     *
     * @param workflowInfo
     * @return レスポンス
     */
    public ResponseEntity removeWork(WorkflowInfoEntity workflowInfo) {
        logger.debug("removeWorkflow:{}", workflowInfo);
        try {
            return (ResponseEntity) restClient.delete(WORKFLOW_PATH, workflowInfo.getWorkflowId(), ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 工程オフセット取得
     *
     * @param workflowInfoEntity 工程順
     * @param workId 工程ID
     * @return レスポンス
     */
    public Long getWorkOffset(WorkflowInfoEntity workflowInfoEntity, Long workId) {

        logger.debug("getWorkOffset:{}", workflowInfoEntity, workId);

        // ワークフローワーク取得
        List<ConWorkflowWorkInfoEntity> conWorkflowWorkInfoEntitys = workflowInfoEntity.getConWorkflowWorkInfoCollection();
        if (conWorkflowWorkInfoEntitys.isEmpty()) {
            return 0L;
        }

        // ワークをオーダー順にソート.
        conWorkflowWorkInfoEntitys.sort((a, b) -> a.getWorkflowOrder() - b.getWorkflowOrder());

        // 引数のワークIDで検索.
        ConWorkflowWorkInfoEntity conWorkflowWorkInfoEntity = conWorkflowWorkInfoEntitys.stream().filter(e -> e.getFkWorkId().equals(workId)).findFirst().orElse(null);
        if (Objects.isNull(conWorkflowWorkInfoEntity)) {
            return 0L;
        }

        // 検索した次のインデックスを取得.
        int nextIndex = conWorkflowWorkInfoEntitys.indexOf(conWorkflowWorkInfoEntity) + 1;
        if (conWorkflowWorkInfoEntitys.size() <= nextIndex) {
            return 0L;
        }

        // 次工程がスキップなら0.
        ConWorkflowWorkInfoEntity conWorkflowWorkInfoEntityNext = conWorkflowWorkInfoEntitys.get(nextIndex);
        if (conWorkflowWorkInfoEntityNext.getSkipFlag()) {
            return 0L;
        }

        // 前工程がスキップなら.
        // 次工程の基準開始時間から、前工程の基準開始時間を引く(=オフセット).
        if (conWorkflowWorkInfoEntity.getSkipFlag()) {
            return conWorkflowWorkInfoEntityNext.getStandardStartTime().getTime() - conWorkflowWorkInfoEntity.getStandardStartTime().getTime();
        }

        // 次工程の基準開始時間から、前工程の基準完了時間を引く(=オフセット).
        return conWorkflowWorkInfoEntityNext.getStandardStartTime().getTime() - conWorkflowWorkInfoEntity.getStandardEndTime().getTime();
    }

    /**
     * 工程順を改訂する。(コピーして新しい版数を付与)
     *
     * @param id 工程順ID
     * @return 結果 (成功時は改訂後の工程順エンティティを含む)
     */
    public ResponseWorkflowInfoEntity revise(Long id) {
        logger.debug("revision:{}", id);
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORKFLOW_PATH);
            path.append(REVISION_PATH);
            path.append(SEPARATOR_PATH);
            path.append(id);

            return (ResponseWorkflowInfoEntity) restClient.post(path.toString(), null, ResponseWorkflowInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ResponseWorkflowInfoEntity();
    }

    /**
     * 工程順IDを指定して、該当する工程順を使用しているカンバンの件数があるかどうかを取得する。
     *
     * @param id 工程順ID
     * @return 使用しているカンバンがあるか (true:ある, false:ない)
     */
    public Boolean existAssignedKanban(Long id) {
        return existAssignedKanban(id, false);
    }

    /**
     * 工程順IDを指定して、該当する工程順を使用しているカンバンの件数があるかどうかを取得する。
     * 
     * @param id 工程順ID
     * @param incompleteOnly 未完了のカンバンのみを対象とする
     * @return 使用しているカンバンがあるか (true:ある, false:ない)
     */
    public Boolean existAssignedKanban(Long id, Boolean incompleteOnly) {
        logger.debug("existAssignedKanban:{}", id, incompleteOnly);
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORKFLOW_PATH);
            path.append(String.format(ID_TARGET_PATH, id.toString()));
            path.append(EXIST_ASSIGNED_KANBAN);

            // パラメータ
            path.append(QUERY_PATH);
            path.append("incompleteOnly=");
            path.append(incompleteOnly);

            String result = (String) restClient.find(path.toString(), MediaType.TEXT_PLAIN_TYPE, String.class);
            if (StringUtils.equals(result, "1")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }
    
    /**
     * データチェックを実施
     * @param id データチェックを行うworkflowID
     * @return データチェック結果
     */
    public List<WorkflowDataCheckInfoEntity> checkWorkflow(Long id)
    {
        logger.debug("checkWorkflow:{}", id);

        try {
            StringBuilder path = new StringBuilder();
            path.append(WORKFLOW_PATH);
            path.append(CHECK_WORKFLOW);
            path.append(QUERY_PATH);
            path.append(ID_PATH);
            path.append(id.toString());

            return restClient.find(path.toString(), new GenericType<List<WorkflowDataCheckInfoEntity>>() {
            });

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            List<WorkflowDataCheckInfoEntity> ret = new ArrayList<>();
            ret.add(new WorkflowDataCheckInfoEntity(WorkflowDateCheckErrorTypeEnum.TagServerError, ""));
            return ret;
        }finally{
            logger.debug("end checkWorkflow:{}", id);
        }
    }

    /**
     * 新しいLite工程順の登録
     *
     * @param liteWorkflowInfo
     * @return レスポンス
     */
    public ResponseEntity registLiteWork(LiteWorkflowInfo liteWorkflowInfo) {
        logger.debug("registLiteWorkflow:{}", liteWorkflowInfo);
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORKFLOW_PATH);
            path.append(LITE_PATH);

            return (ResponseEntity) restClient.post(path.toString(), liteWorkflowInfo, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ResponseEntity();
    }

    /**
     * Lite工程順の更新
     *
     * @param liteWorkflowInfo
     * @return レスポンス
     */
    public ResponseEntity updateLiteWork(LiteWorkflowInfo liteWorkflowInfo) {
        logger.debug("updateLiteWorkflow:{}", liteWorkflowInfo);
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORKFLOW_PATH);
            path.append(LITE_PATH);

            return (ResponseEntity) restClient.put(path.toString(), liteWorkflowInfo, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ResponseEntity();
    }

    /**
     * Lite工程順のコピー
     *
     * @param liteWorkflowInfo
     * @return レスポンス
     */
    public ResponseEntity copyLiteWork(LiteWorkflowInfo liteWorkflowInfo) {
        logger.debug("copyLiteWorkflow:{}", liteWorkflowInfo);
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORKFLOW_PATH);
            path.append(LITE_PATH);
            path.append(COPY_PATH);
            path.append(String.format(ID_TARGET_PATH, liteWorkflowInfo.getWorkflow().getWorkflowId().toString()));

            return (ResponseEntity) restClient.post(path.toString(), liteWorkflowInfo, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ResponseEntity();
    }

    /**
     * Lite工程順の削除
     *
     * @param liteWorkflowInfo
     * @return レスポンス
     */
    public ResponseEntity removeLiteWork(LiteWorkflowInfo liteWorkflowInfo) {
        logger.debug("removeLiteWorkflow:{}", liteWorkflowInfo);
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORKFLOW_PATH);
            path.append(LITE_PATH);

            return (ResponseEntity) restClient.delete(path.toString(), liteWorkflowInfo.getWorkflow().getWorkflowId(), ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /*
     * 工程順情報の一括登録
     *
     * @param workInfos 登録する工程順一覧
     * @param isImportToSuspendKanban 中断中カンバンに取り込むか
     * @return ResponseEntity
     */
    public List<ResultResponse> addAll(List<WorkflowInfoEntity> workInfos, boolean isImportToSuspendKanban) {
        logger.debug("addAll:{}", workInfos);
        try {
            // 追加情報をJSONにする
            workInfos.forEach(workflow -> workflow.setWorkflowAddInfo(JsonUtils.objectsToJson(workflow.getKanbanPropertyTemplateInfoCollection())));

            StringBuilder path = new StringBuilder();
            path.append(WORKFLOW_PATH);
            path.append(ALL_PATH);

            // パラメータ
            path.append(QUERY_PATH);
            path.append(String.format(IMPORT_SUSPEND_KANBAN, isImportToSuspendKanban));


            return (List<ResultResponse>) restClient.post(path.toString(), new ObjectParam(workInfos), new GenericType<List<ResultResponse>>(){});
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        } finally {
            logger.debug("addAll : end");
        }
    }

    /**
     * 工程順にて作業中の工程カンバンのカウント数を工程カンバン名で取得する
     * @param workflowName
     * @return カウント数
     */
    public Long countWorkingWorkKanbanByWorkflowNames(List<String> workflowName) {
        logger.debug("countWorkflow:{}", workflowName);
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORKFLOW_PATH);
            path.append(WORKING_WK_COUNT_PATH);

            return Long.valueOf((String) restClient.post(path.toString(), new ListWrapper(workflowName), String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return -1L;
        } finally {
            logger.debug("addAll : end");
        }

    }

    /**
     * 工程を使用した最新版の工程順を取得する
     * 
     * @param workName 工程名
     * @return 工程順情報
     */
    public List<WorkflowInfoEntity> findByWorkName(String workName) {
        logger.debug("findByWorkName: {}", workName);
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORKFLOW_PATH);
            path.append("/work-name?workName=");
            path.append(URLEncoder.encode(workName, "UTF-8"));
            return (List<WorkflowInfoEntity>) restClient.find(path.toString(), new GenericType<List<WorkflowInfoEntity>>(){});
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        } finally {
            logger.debug("findByWorkName: end");
        }
    }  
    
    /**
     * 工程順の工程を更新する。
     * 
     * @param workflowIds 工程順ID
     * @param workId 工程ID
     * @return 
     */
    public ResponseEntity updateWork(List<Long> workflowIds, Long workId) {
        logger.debug("updateWork: start.");
        try {
            if (workflowIds.isEmpty()) {
                return new ResponseEntity();
            }
            
            StringBuilder path = new StringBuilder();
            path.append(WORKFLOW_PATH);
            path.append(WORK_PATH);
            path.append(QUERY_PATH);
            path.append(ID_PATH);
            path.append(workflowIds.get(0));
            for (int i = 1; i < workflowIds.size(); i++) {
                path.append(AND_PATH);
                path.append(ID_PATH);
                path.append(workflowIds.get(i));
            }
            path.append("&workId=");
            path.append(workId);
            if(Objects.nonNull(loginUserInfoEntity.getId())) {
                path.append(AND_PATH);
                path.append(String.format(AUTHID_PATH, loginUserInfoEntity.getId()));
            }
            return (ResponseEntity) restClient.put(path.toString(), null, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
        }  finally {
            logger.debug("updateWork: end");
        }
    }

    /**
     * 工程情報を取得する。
     * 
     * @param workflowId 工程順ID
     * @param workName 工程名
     * @return 工程情報
     */
    public WorkInfoEntity findWork(Long workflowId, String workName) {
        logger.debug("findWork: {}", workflowId);
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORKFLOW_PATH);
            path.append(WORK_PATH);
            path.append(QUERY_PATH);
            path.append(ID_PATH);
            path.append(workflowId);
            path.append(AND_PATH);
            path.append("workName=");
            path.append(URLEncoder.encode(workName, "UTF-8"));
            if(Objects.nonNull(loginUserInfoEntity.getId())) {
                path.append(AND_PATH);
                path.append(String.format(AUTHID_PATH, loginUserInfoEntity.getId()));
            }
            return (WorkInfoEntity) restClient.find(path.toString(), WorkInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }
    
    public void setReadTimeout(Integer time) {
        restClient.setReadTimeout(time);
    }

    public Integer getReadTimeout() {
        return restClient.getReadTimeout();
    }

}
