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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import jakarta.ws.rs.core.MediaType;
import jp.adtekfuji.adFactory.entity.ListWrapper;
import jp.adtekfuji.adFactory.entity.ObjectParam;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.master.AddInfoEntity;
import jp.adtekfuji.adFactory.entity.response.ResponseWorkInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.enumerate.WorkPropertyCategoryEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程情報取得用RESTクラス
 *
 * @author ta.ito
 */
public class WorkInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    private final static String WORK_PATH = "/work";
    private final static String WORKFLOW_PATH = "/workflow";
    private final static String COUNT_PATH = "/count";
    private final static String RANGE_PATH = "/range";
    private final static String COPY_PATH = "/copy";
    private final static String ID_TARGET_PATH = "/%s";
    private final static String NAME_PATH = "/name";

    private final static String EXIST_ASSIGNED_WORKFLOW = "/exist/assigned-workflow";
    /**
     * 版数のパス
     */
    private final static String REVISION_PATH = "/revision";
    private final static String TIME_PATH = "/time";
    private final static String ALL_PATH = "/all";

    private final static String QUERY_PATH = "?";
    private final static String AND_PATH = "&";
    private final static String SEPARATOR_PATH = "/";
    private final static String FROM_TO_PATH = "from=%s&to=%s";
    private final static String NAME_QUERY = "name=%s";
    private final static String IS_SHIFT_QUERY = "isShift=%s";
    private final static String WORKFLOWID_PATH = "workflowId=%s";
    private final static String WITH_DEVICE_PATH = "withDevice=%s";

    /**
     * リクエストURIのサブパス(最新版数)
     */
    private final static String LATEST_REV_PATH = "getlatestrev=%s";

    public WorkInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    public WorkInfoFacade(String uriBase) {
        restClient = new RestClient(new RestClientProperty(uriBase));
    }
    /**
     * 
     * 工程を取得
     *
     * @param workId
     * @return
     */
    public WorkInfoEntity find(Long workId) {
        return find(workId, false);
    }

    /**
     * 工程を取得
     *
     * @param id 工程ID
     * @param withDevice trueの場合設備も一緒に取得する
     * @return 工程
     */
    public WorkInfoEntity find(Long id, boolean withDevice) {
        return find(id, withDevice, false);
    }

    /**
     * 工程を取得
     *
     * @param id 工程ID
     * @param withDevice trueの場合設備も一緒に取得する
     * @param getlatestrev trueの場合最新版数も一緒に取得する
     * @return 工程
     */
    public WorkInfoEntity find(Long id, boolean withDevice, boolean getlatestrev) {
        logger.debug("find:{},{}", id, withDevice);
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORK_PATH);
            path.append(String.format(ID_TARGET_PATH, id.toString()));
            path.append(QUERY_PATH);
            path.append(String.format(WITH_DEVICE_PATH, withDevice));
            path.append(AND_PATH);
            path.append(String.format(LATEST_REV_PATH, getlatestrev));

            return (WorkInfoEntity) restClient.find(path.toString(), WorkInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 工程を取得
     *
     * @param name 工程名
     * @return 工程順
     */
    public WorkInfoEntity findName(String name) {
        logger.debug("findName:{}", name);
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORK_PATH);
            path.append(NAME_PATH);

            // パラメータ
            path.append(QUERY_PATH);
            path.append(String.format(NAME_QUERY, name));

            return (WorkInfoEntity) restClient.find(path.toString(), WorkInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 工程を取得
     * @param name
     * @return
     */
    public List<WorkInfoEntity> findAllByName(List<String> name) {
        logger.debug("findAllName:{}", name);
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORK_PATH);
            path.append(NAME_PATH);
            path.append(ALL_PATH);
            return (List<WorkInfoEntity>) restClient.post(path.toString(), new ListWrapper(name), new GenericType<List<WorkInfoEntity>>(){});
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }


    /**
     * 工程一覧の数を取得
     *
     * @return 工程一覧の数
     */
    public Long getWorkCount() {
        logger.debug("getWorkCount:start");
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORK_PATH);
            path.append(COUNT_PATH);

            String count = (String) restClient.find(path.toString(), MediaType.TEXT_PLAIN_TYPE, String.class);
            return Long.parseLong(count);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return 0l;
    }

    /**
     * 工程一覧を範囲指定で取得
     *
     * @param from 頭数
     * @param to 尾数
     * @return 工程一覧
     */
    public List<WorkInfoEntity> getWorkRange(Long from, Long to) {
        logger.debug("getWorkRange:start");
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORK_PATH);
            path.append(RANGE_PATH);

            // パラメータ
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                path.append(QUERY_PATH);
                path.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
            }

            return restClient.findAll(path.toString(), new GenericType<List<WorkInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ArrayList<>();
    }

    /**
     * 新しい工程の登録
     *
     * @param workInfo 登録する工程のエンティティ
     * @return レスポンス
     */
    public ResponseEntity registWork(WorkInfoEntity workInfo) {
        logger.debug("registWork:{}", workInfo);
        try {
            // JSONの追加情報・検査情報を更新する。
            this.updateAddInfo(workInfo);

            return (ResponseEntity) restClient.post(WORK_PATH, workInfo, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity(ex);
        }
    }

    /**
     * 工程の更新
     *
     * @param workInfo 更新内容
     * @return レスポンス
     */
    public ResponseEntity updateWork(WorkInfoEntity workInfo) {
        logger.debug("updateWork:{}", workInfo);
        try {
            // JSONの追加情報・検査情報を更新する。
            this.updateAddInfo(workInfo);

            return (ResponseEntity) restClient.put(WORK_PATH, workInfo, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity(ex);
        }
    }

    /**
     * 工程のコピー
     *
     * @param workInfo コピー元のエンティティ
     * @return レスポンス
     */
    public ResponseEntity copyWork(WorkInfoEntity workInfo) {
        logger.debug("copyWork:{}", workInfo);
        try {
            // JSONの追加情報・検査情報を更新する。
            this.updateAddInfo(workInfo);

            StringBuilder path = new StringBuilder();
            path.append(WORK_PATH);
            path.append(COPY_PATH);
            path.append(SEPARATOR_PATH);
            path.append(workInfo.getWorkId());

            return (ResponseEntity) restClient.post(path.toString(), workInfo, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity(ex);
        }
    }

    /**
     * 工程の削除
     *
     * @param workInfo 削除対象のエンティティ
     * @return レスポンス
     */
    public ResponseEntity removeWork(WorkInfoEntity workInfo) {
        logger.debug("removeWork:{}", workInfo);
        try {
            return (ResponseEntity) restClient.delete(WORK_PATH, workInfo.getWorkId(), ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity(ex);
        }
    }

    /**
     * 工程順IDを指定して、該当する工程順を使用しているカンバンの件数があるかどうかを取得する。
     *
     * @param id 工程ID
     * @return 使用している工程順があるか (true:ある, false:ない)
     */
    public Boolean existAssignedWorkflow(Long id) {
        logger.debug("existAssignedこうてWorkflow:{}", id);
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORK_PATH);
            path.append(String.format(ID_TARGET_PATH, id.toString()));
            path.append(EXIST_ASSIGNED_WORKFLOW);

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
     * 工程を改訂する。(コピーして新しい版数を付与)
     *
     * @param id 工程ID
     * @return 結果 (成功時は改訂後の工程エンティティを含む)
     */
    public ResponseWorkInfoEntity revise(Long id) {
        logger.debug("revision:{}", id);
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORK_PATH);
            path.append(REVISION_PATH);
            path.append(SEPARATOR_PATH);
            path.append(id);

            return (ResponseWorkInfoEntity) restClient.post(path.toString(), null, ResponseWorkInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ResponseWorkInfoEntity();
    }

    /**
     *
     * 作業時間を更新します
     *
     * @param entity 更新対象を決める工程のエンティティ
     * @param isShift 作業時間シフト要否フラグ
     * @return 更新された工程順のリスト
     */
    public List<WorkflowInfoEntity> reschedule(WorkInfoEntity entity, boolean isShift) {
        logger.debug(WorkInfoFacade.class.getName() + "reschedule:entity={},isShift={}", entity, isShift);
        try {
            // JSONの追加情報・検査情報を更新する。
            this.updateAddInfo(entity);

            StringBuilder path = new StringBuilder();
            path.append(WORKFLOW_PATH);
            path.append(TIME_PATH);

            // パラメータ
            path.append(QUERY_PATH);
            path.append(String.format(IS_SHIFT_QUERY, isShift));

            return restClient.put(path.toString(), entity, new GenericType<List<WorkflowInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 工程順に属する工程の個数を取得する。
     *
     * @param workflowId カウント対象の工程のID
     * @return 工程の個数
     */
    public long getWorkCountByWorkflow(long workflowId) {
        logger.debug("getWorkCountByWorkflow:start");
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORK_PATH);
            path.append(WORKFLOW_PATH);
            path.append(COUNT_PATH);

            // パラメータ
            path.append(QUERY_PATH);
            path.append(String.format(WORKFLOWID_PATH, workflowId));

            String count = (String) restClient.find(path.toString(), MediaType.TEXT_PLAIN_TYPE, String.class);
            return Long.parseLong(count);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return 0L;
    }

    /**
     * 工程順に属する工程を取得する。
     *
     * @param workflowId 取得対象の工程順の工程順ID
     * @param from 頭数
     * @param to 尾数
     * @return 工程エンティティのリスト
     */
    public List<WorkInfoEntity> getWorkRangeByWorkflow(long workflowId, Long from, Long to) {
        logger.debug("getWorkRangeByWorkflow:start");
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORK_PATH);
            path.append(WORKFLOW_PATH);

            // パラメータ
            path.append(QUERY_PATH);
            path.append(String.format(WORKFLOWID_PATH, workflowId));

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                path.append(AND_PATH);
                path.append(String.format(FROM_TO_PATH, String.valueOf(from), String.valueOf(to)));
            }

            return restClient.findAll(path.toString(), new GenericType<List<WorkInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ArrayList<>();
    }

    /**
     * Entity内のプロパティ一覧の内容で、JSONの追加情報・検査情報を更新する。
     *
     * @param workInfo 工程情報
     */
    private void updateAddInfo(WorkInfoEntity workInfo) {
        // 工程プロパティ一覧を、追加情報と検査情報に分ける。
        List<AddInfoEntity> addInfos = this.getAddInfo(workInfo.getPropertyInfoCollection());
        List<WorkPropertyInfoEntity> checkInfos = this.getCheckInfo(workInfo.getPropertyInfoCollection());

        // 追加情報一覧をJSON文字列に変換する。
        String workAddInfo = JsonUtils.objectsToJson(addInfos);
        // 検査情報一覧をJSON文字列に変換する。
        String workCheckInfo = JsonUtils.objectsToJson(checkInfos);

        workInfo.setWorkAddInfo(workAddInfo);
        workInfo.setWorkCheckInfo(workCheckInfo);
    }

    /**
     * 工程プロパティ一覧から追加情報を取得する。
     *
     * @param props 工程プロパティ一覧
     * @return 追加情報の工程プロパティ一覧
     */
    private List<AddInfoEntity> getAddInfo(List<WorkPropertyInfoEntity> props) {
        List<AddInfoEntity> addInfos = new LinkedList();
        List<WorkPropertyInfoEntity> targetProps = props.stream()
                .filter(p -> Objects.isNull(p.getWorkPropCategory()) || WorkPropertyCategoryEnum.INFO.equals(p.getWorkPropCategory()))
                .collect(Collectors.toList());

        for (WorkPropertyInfoEntity prop : targetProps) {
            addInfos.add(new AddInfoEntity(prop.getWorkPropName(), prop.getWorkPropType(), prop.getWorkPropValue(), prop.getWorkPropOrder(), null));
        }

        return addInfos;
    }

    /**
     * 工程プロパティ一覧から検査情報を取得する。
     *
     * @param props 工程プロパティ一覧
     * @return 検査情報の工程プロパティ一覧
     */
    private List<WorkPropertyInfoEntity> getCheckInfo(List<WorkPropertyInfoEntity> props) {
        return props.stream()
                .filter(p -> Objects.nonNull(p.getWorkPropCategory()) && !WorkPropertyCategoryEnum.INFO.equals(p.getWorkPropCategory()))
                .collect(Collectors.toList());
    }

    /**
     * 工程情報の一括登録
     *
     * @param works 登録する工程一覧
     * @param isUpdateTaktTime タクトタイムを更新するか
     * @return レスポンス
     */
    public ResponseEntity addAll(List<WorkInfoEntity> works, boolean isUpdateTaktTime) {
        logger.debug("addAll:{}", works);
        try {
            // JSONの追加情報・検査情報を更新する。
            works.forEach(workInfo -> this.updateAddInfo(workInfo));

            StringBuilder path = new StringBuilder();
            path.append(WORK_PATH);
            path.append(ALL_PATH);

            path.append(QUERY_PATH);
            path.append("isUpdateTaktTime=");
            path.append(isUpdateTaktTime);

            return (ResponseEntity) restClient.post(path.toString(), new ObjectParam(works), ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity(ex);
        } finally {
            logger.debug("addAll : end");
        }
    }
}

