/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.clientservice;

import adtekfuji.property.AdProperty;
import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import jakarta.ws.rs.core.GenericType;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
import jakarta.ws.rs.core.MediaType;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程順階層情報取得用RESTクラス
 *
 * @author ta.ito
 */
public class WorkflowHierarchyInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();

    private final static String LITE_HIERARCHY_TOP_KEY = "LiteHierarchyTop";
    final private String liteTreeName;

    private final static String WORKTREE_PATH = "/workflow/tree";
    private final static String COUNT_PATH = "/count";
    private final static String RANGE_PATH = "/range";
    private final static String ID_TARGET_PATH = "/%s";
    private final static String HIERARCHY_PATH = "/hierarchy";
    private final static String NAME_PATH = "/name";

    private final static String QUERY_PATH = "?";
    private final static String AND_PATH = "&";
    private final static String ID_PATH = "id=%s";
    private final static String USER_PATH = "user=%s";
    private final static String AUTHID_PATH = "authId=%s";
    private final static String FROM_TO_PATH = "from=%s&to=%s";
    private final static String NAME_QUERY_PATH = "name=%s";
    private final static String HAS_CHILD_PATH = "hasChild=%s";

    public WorkflowHierarchyInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
        //Lite階層名を取得しておく
        Properties properties = AdProperty.getProperties();
        this.liteTreeName = properties.getProperty(LITE_HIERARCHY_TOP_KEY);
    }

    public WorkflowHierarchyInfoFacade(String uriBase) {
        restClient = new RestClient(new RestClientProperty(uriBase));
        //Lite階層名を取得しておく
        Properties properties = AdProperty.getProperties();
        this.liteTreeName = properties.getProperty(LITE_HIERARCHY_TOP_KEY);
    }

    public String getLiteTreeName() {
        return this.liteTreeName;
    }

    /**
     * 最上位階層に含まれる工程順階層の数を取得
     *
     * @return 最上位階層の工程順階層数
     */
    public Long getTopHierarchyCount() {
        logger.debug("getTopHierarchyCount:start");
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORKTREE_PATH);
            path.append(COUNT_PATH);

            // パラメータ
            if(Objects.nonNull(loginUserInfoEntity.getId()) && !LoginUserInfoEntity.ADMIN_LOGIN_ID.equals(loginUserInfoEntity.getLoginId())) {
                path.append(QUERY_PATH);
                path.append(String.format(USER_PATH, loginUserInfoEntity.getId()));
            }

            String count = (String) restClient.find(path.toString(), MediaType.TEXT_PLAIN_TYPE, String.class);
            return Long.parseLong(count);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return 0l;
    }

    /**
     * 最上位階層に含まれる工程順階層を範囲指定で取得
     *
     * @param from 頭数
     * @param to 尾数
     * @param hasChiled 工程順を含めるかどうか
     * @return 最上位階層の工程順階層一覧
     */
    public List<WorkflowHierarchyInfoEntity> getTopHierarchyRange(Long from, Long to, boolean hasChiled) {
        return getTopHierarchyRange(from, to, hasChiled, false);
    }

    /**
     * 最上位階層に含まれる工程順階層を範囲指定で取得
     *
     * @param from 頭数
     * @param to 尾数
     * @param hasChiled 工程順を含めるかどうか
     * @param withLite Lite 工程順階層を含める
     * @return 最上位階層の工程順階層一覧
     */
    public List<WorkflowHierarchyInfoEntity> getTopHierarchyRange(Long from, Long to, boolean hasChiled, boolean withLite) {
        logger.debug("getTopHierarchyRange:start");
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORKTREE_PATH);
            path.append(RANGE_PATH);

            // パラメータ
            path.append(QUERY_PATH);
            if(Objects.nonNull(loginUserInfoEntity.getId()) && !LoginUserInfoEntity.ADMIN_LOGIN_ID.equals(loginUserInfoEntity.getLoginId())) {
                path.append(String.format(USER_PATH, loginUserInfoEntity.getId()));
                path.append(AND_PATH);
            }

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                path.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
                path.append(AND_PATH);
            }

            path.append(String.format(HAS_CHILD_PATH, String.valueOf(hasChiled)));

            List<WorkflowHierarchyInfoEntity> entities = restClient.findAll(path.toString(), new GenericType<List<WorkflowHierarchyInfoEntity>>() {});
            if (withLite) {
                return entities;
            }
            
            return entities.stream().filter(s -> !s.getHierarchyName().equals(this.liteTreeName)).collect(Collectors.toList());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ArrayList<>();
    }

    /**
     * IDで指定された階層の工程順階層の数を取得
     *
     * @param hierarchyId 親階層ID
     * @return 指定階層の工程順階層数
     */
    public Long getAffilationHierarchyCount(Long hierarchyId) {
        logger.debug("getAffilationHierarchyCount:start");
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORKTREE_PATH);
            path.append(COUNT_PATH);

            // パラメータ
            path.append(QUERY_PATH);
            path.append(String.format(ID_PATH, hierarchyId.toString()));

            if(Objects.nonNull(loginUserInfoEntity.getId()) && !LoginUserInfoEntity.ADMIN_LOGIN_ID.equals(loginUserInfoEntity.getLoginId())) {
                path.append(AND_PATH);
                path.append(String.format(USER_PATH, loginUserInfoEntity.getId()));
            }

            String count = (String) restClient.find(path.toString(), MediaType.TEXT_PLAIN_TYPE, String.class);
            return Long.parseLong(count);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return 0l;
    }

    /**
     * IDで指定された階層の工程順階層一覧を取得
     *
     * @param hierarchyId 親階層ID
     * @param from 頭数
     * @param to 尾数
     * @return 指定階層の工程順階層一覧
     */
    public List<WorkflowHierarchyInfoEntity> getAffilationHierarchyRange(Long hierarchyId, Long from, Long to) {
        return this.getAffilationHierarchyRange(hierarchyId, from, to, true);
    }

    /**
     * 工程順階層を取得する。
     *
     * @param hierarchyId 親階層ID
     * @param from 頭数
     * @param to 尾数
     * @param hasChild 工程順を含めるかどうか
     * @return
     */
    public List<WorkflowHierarchyInfoEntity> getAffilationHierarchyRange(long hierarchyId, Long from, Long to, boolean hasChild) {
        logger.debug("getAffilationHierarchyRange:start");
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORKTREE_PATH);
            path.append(RANGE_PATH);

            // パラメータ
            path.append(QUERY_PATH);
            path.append(String.format(ID_PATH, String.valueOf(hierarchyId)));

            if(Objects.nonNull(loginUserInfoEntity.getId()) && !LoginUserInfoEntity.ADMIN_LOGIN_ID.equals(loginUserInfoEntity.getLoginId())) {
                path.append(AND_PATH);
                path.append(String.format(USER_PATH, loginUserInfoEntity.getId()));
            }

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                path.append(AND_PATH);
                path.append(String.format(FROM_TO_PATH,String.valueOf(from), String.valueOf(to)));
            }

            if (Objects.nonNull(hasChild)) {
                path.append(AND_PATH);
                path.append(String.format(HAS_CHILD_PATH, String.valueOf(hasChild)));
            }

            List<WorkflowHierarchyInfoEntity> entities = restClient.findAll(path.toString(), new GenericType<List<WorkflowHierarchyInfoEntity>>() {
            });
            return entities;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ArrayList<>();
    }

    /**
     * 新しい工程順階層の登録
     *
     * @param workflowHierarchyInfo
     * @return レスポンス
     */
    public ResponseEntity registHierarchy(WorkflowHierarchyInfoEntity workflowHierarchyInfo) {
        logger.debug("registHierarchy:{}", workflowHierarchyInfo);
        try {
            return (ResponseEntity) restClient.post(WORKTREE_PATH, workflowHierarchyInfo, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ResponseEntity();
    }

    /**
     * 工程順階層の更新
     *
     * @param workflowHierarchyInfo
     * @return レスポンス
     */
    public ResponseEntity updateHierarchy(WorkflowHierarchyInfoEntity workflowHierarchyInfo) {
        logger.debug("updateHierarchy:{}", workflowHierarchyInfo);
        try {
            return (ResponseEntity) restClient.put(WORKTREE_PATH, workflowHierarchyInfo, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ResponseEntity();
    }

    /**
     * 工程順階層の削除
     *
     * @param workflowHierarchyInfo
     * @return レスポンス
     */
    public ResponseEntity removeHierarchy(WorkflowHierarchyInfoEntity workflowHierarchyInfo) {
        logger.debug("removeHierarchy:{}", workflowHierarchyInfo);
        try {
            return (ResponseEntity) restClient.delete(WORKTREE_PATH, workflowHierarchyInfo.getWorkflowHierarchyId(), ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 工程順階層を取得する。
     *
     * @param id
     * @return レスポンス
     */
    public WorkflowHierarchyInfoEntity find(Long id) {
        logger.debug("find:{}", id);
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORKTREE_PATH);
            path.append(String.format(ID_TARGET_PATH, id.toString()));

            return (WorkflowHierarchyInfoEntity) restClient.find(path.toString(), WorkflowHierarchyInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 工程順階層を取得する。
     *
     * @param uri
     * @return
     */
    public WorkflowHierarchyInfoEntity findURI(String uri) {
        logger.debug("find:{}", uri);
        try {
            return (WorkflowHierarchyInfoEntity) restClient.find("/" + uri, WorkflowHierarchyInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 指定した工程順階層名の工程順階層を取得 (階層のみで工程順リストは取得しない)
     *
     * @param name 工程順階層名
     * @return 工程順階層
     */
    public WorkflowHierarchyInfoEntity findHierarchyName(String name) {
        logger.debug("findHierarchyName:{}", name);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WORKTREE_PATH);
            sb.append(HIERARCHY_PATH);
            sb.append(NAME_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(NAME_QUERY_PATH, name));

            return (WorkflowHierarchyInfoEntity) restClient.find(sb.toString(), WorkflowHierarchyInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new WorkflowHierarchyInfoEntity();
        }
    }

    /**
     * 工程順を検索する。
     * 
     * @param name 工程名
     * @param hierarchyId 工程階層ID
     * @param latestOnly 最新版のみかどうか
     * @return 工程情報一覧
     */
    public List<WorkflowInfoEntity> searchWorkflow(String name, Long hierarchyId, Boolean latestOnly) {
        logger.debug("searchWork:{}", name);
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORKTREE_PATH);
            path.append("/search");
            path.append(QUERY_PATH);

            path.append("name=");
            path.append(URLEncoder.encode(name, "UTF-8"));
            path.append(AND_PATH);
            path.append("hierarchyId=");
            path.append(hierarchyId);
            path.append(AND_PATH);
            path.append("latestOnly=");
            path.append(latestOnly);
            
            if(Objects.nonNull(loginUserInfoEntity.getId())) {
                path.append(AND_PATH);
                path.append(String.format(AUTHID_PATH, loginUserInfoEntity.getId()));
            }

            return restClient.findAll(path.toString(), new GenericType<List<WorkflowInfoEntity>>() {});
            
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;

    }
}
