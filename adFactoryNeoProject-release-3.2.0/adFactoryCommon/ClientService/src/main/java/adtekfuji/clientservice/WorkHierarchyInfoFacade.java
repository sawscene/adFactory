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
import jp.adtekfuji.adFactory.entity.work.WorkHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程階層情報取得用RESTクラス
 *
 * @author ta.ito
 */
public class WorkHierarchyInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();

    private final static String LITE_HIERARCHY_TOP_KEY = "LiteHierarchyTop";
    final private String liteTreeName;

    private final static String WORKTREE_PATH = "/work/tree";
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
    private final static String APPROVE_PATH = "approve=%s";

    public WorkHierarchyInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
        //Lite階層名を取得しておく
        Properties properties = AdProperty.getProperties();
        this.liteTreeName = properties.getProperty(LITE_HIERARCHY_TOP_KEY);
    }

    public WorkHierarchyInfoFacade(String uriBase) {
        restClient = new RestClient(new RestClientProperty(uriBase));
        //Lite階層名を取得しておく
        Properties properties = AdProperty.getProperties();
        this.liteTreeName = properties.getProperty(LITE_HIERARCHY_TOP_KEY);
    }

    /**
     * 最上位階層に含まれる工程階層の数を取得
     *
     * @return 最上位階層の工程階層数
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
     * 最上位階層に含まれる工程階層を範囲指定で取得
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param hasChiled 子がいるか否かのフラグ(true:取得する, false:取得しない)
     * @param approve 承認済のみ取得(true:承認済のみ, false:全て)
     * @return 最上位階層の工程階層一覧
     */
    public List<WorkHierarchyInfoEntity> getTopHierarchyRange(Long from, Long to, boolean hasChiled, boolean approve) {
        logger.debug("getTopHierarchyRange: from={}, to={}, hasChiled={}, approve={}", from, to, hasChiled, approve);
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

            path.append(AND_PATH);
            path.append(String.format(APPROVE_PATH, String.valueOf(approve)));

            List<WorkHierarchyInfoEntity> entities = restClient.findAll(path.toString(), new GenericType<List<WorkHierarchyInfoEntity>>() {});
            return entities.stream().filter(s -> !s.getHierarchyName().equals(this.liteTreeName)).collect(Collectors.toList());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ArrayList<>();
    }

    /**
     * IDで指定された階層の工程階層の数を取得
     *
     * @param hierarchyId 親階層ID
     * @return 指定階層の工程階層数
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
     * 指定した階層の子階層情報一覧を取得する。
     *
     * @param hierarchyId 階層ID
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param hasChiled 子がいるか否かのフラグ(true:取得する, false:取得しない)
     * @param approve 承認済のみ取得(true:承認済のみ, false:全て)
     * @return 指定階層の工程階層一覧
     */
    public List<WorkHierarchyInfoEntity> getAffilationHierarchyRange(Long hierarchyId, Long from, Long to, boolean hasChiled, boolean approve) {
        logger.debug("getAffilationHierarchyRange: hierarchyId={}, from={}, to={}, hasChiled={}, approve={}", hierarchyId, from, to, hasChiled, approve);
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORKTREE_PATH);
            path.append(RANGE_PATH);

            // パラメータ
            path.append(QUERY_PATH);
            path.append(String.format(ID_PATH, hierarchyId.toString()));

            if(Objects.nonNull(loginUserInfoEntity.getId()) && !LoginUserInfoEntity.ADMIN_LOGIN_ID.equals(loginUserInfoEntity.getLoginId())) {
                path.append(AND_PATH);
                path.append(String.format(USER_PATH, loginUserInfoEntity.getId()));
            }

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                path.append(AND_PATH);
                path.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
            }

            path.append(AND_PATH);
            path.append(String.format(HAS_CHILD_PATH, String.valueOf(hasChiled)));

            path.append(AND_PATH);
            path.append(String.format(APPROVE_PATH, String.valueOf(approve)));

            List<WorkHierarchyInfoEntity> entities = restClient.findAll(path.toString(), new GenericType<List<WorkHierarchyInfoEntity>>() {
            });
            return entities;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ArrayList<>();
    }

    /**
     * 新しい工程階層の登録
     *
     * @param workHierarchyInfo
     * @return レスポンス
     */
    public ResponseEntity registHierarchy(WorkHierarchyInfoEntity workHierarchyInfo) {
        logger.debug("registHierarchy:{}", workHierarchyInfo);
        try {
            return (ResponseEntity) restClient.post(WORKTREE_PATH, workHierarchyInfo, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ResponseEntity();
    }

    /**
     * 工程階層の更新
     *
     * @param workHierarchyInfo
     * @return レスポンス
     */
    public ResponseEntity updateHierarchy(WorkHierarchyInfoEntity workHierarchyInfo) {
        logger.debug("updateHierarchy:{}", workHierarchyInfo);
        try {
            return (ResponseEntity) restClient.put(WORKTREE_PATH, workHierarchyInfo, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ResponseEntity();
    }

    /**
     * 工程階層の削除
     *
     * @param workHierarchyInfo
     * @return レスポンス
     */
    public ResponseEntity removeHierarchy(WorkHierarchyInfoEntity workHierarchyInfo) {
        logger.debug("removeHierarchy:{}", workHierarchyInfo);
        try {
            return (ResponseEntity) restClient.delete(WORKTREE_PATH, workHierarchyInfo.getWorkHierarchyId(), ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 工程階層を取得する。
     *
     * @param id
     * @return
     */
    public WorkHierarchyInfoEntity find(Long id) {
        logger.debug("find:{}", id);
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORKTREE_PATH);
            path.append(String.format(ID_TARGET_PATH, id.toString()));

            return (WorkHierarchyInfoEntity) restClient.find(path.toString(), WorkHierarchyInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 工程階層を取得する。
     *
     * @param uri
     * @return
     */
    public WorkHierarchyInfoEntity findURI(String uri) {
        logger.debug("find:{}", uri);
        try {
            return (WorkHierarchyInfoEntity) restClient.find("/" + uri, WorkHierarchyInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 指定した工程階層名の工程階層を取得 (階層のみで工程リストは取得しない)
     *
     * @param name 工程階層名
     * @return 工程階層
     */
    public WorkHierarchyInfoEntity findHierarchyName(String name) {
        logger.debug("findHierarchyName:{}", name);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WORKTREE_PATH);
            sb.append(HIERARCHY_PATH);
            sb.append(NAME_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(NAME_QUERY_PATH, name));

            return (WorkHierarchyInfoEntity) restClient.find(sb.toString(), WorkHierarchyInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new WorkHierarchyInfoEntity();
        }
    }

    /**
     * 工程を検索する。
     * 
     * @param name 工程名
     * @param hierarchyId 工程階層ID
     * @param latestOnly 最新版のみかどうか
     * @return 工程情報一覧
     */
    public List<WorkInfoEntity> searchWork(String name, Long hierarchyId, Boolean latestOnly) {
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

            return restClient.findAll(path.toString(), new GenericType<List<WorkInfoEntity>>() {});
            
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;

    }
}
