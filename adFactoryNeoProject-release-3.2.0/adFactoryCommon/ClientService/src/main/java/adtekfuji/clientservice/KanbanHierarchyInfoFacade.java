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
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
import jakarta.ws.rs.core.MediaType;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author e-mori
 */
public class KanbanHierarchyInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();

    private final static String LITE_HIERARCHY_TOP_KEY = "LiteHierarchyTop";
    final private String liteTreeName;

    private final static String KANBAN_PATH = "/kanban";
    private final static String TREE_PATH = "/tree";
    private final static String COUNT_PATH = "/count";
    private final static String RANGE_PATH = "/range";
    private final static String HIERARCHY_PATH = "/hierarchy";
    private final static String NAME_PATH = "/name";
    private final static String IDS_PATH = "/id";

    private final static String QUERY_PATH = "?";
    private final static String AND_PATH = "&";
    private final static String ID_PATH = "id=%s";
    private final static String USER_PATH = "user=%s";
    private final static String FROM_TO_PATH = "from=%s&to=%s";
    private final static String NAME_QUERY_PATH = "name=%s";
    private final static String HAS_CHILD_PATH = "hasChild=%s";

    public KanbanHierarchyInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
        //Lite階層名を取得しておく
        Properties properties = AdProperty.getProperties();
        this.liteTreeName = properties.getProperty(LITE_HIERARCHY_TOP_KEY);
    }

    public KanbanHierarchyInfoFacade(String uriBase){
        restClient = new RestClient(new RestClientProperty(uriBase));
        //Lite階層名を取得しておく
        Properties properties = AdProperty.getProperties();
        this.liteTreeName = properties.getProperty(LITE_HIERARCHY_TOP_KEY);
    }

    /**
     * 最上位階層に含まれるカンバン階層の数を取得
     * @return
     * @throws Exception 
     */
    public Long getTopHierarchyCount() throws Exception {
        logger.debug("getTopHierarchyCount:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append(TREE_PATH);
            sb.append(COUNT_PATH);

            // パラメータ
            if(Objects.nonNull(loginUserInfoEntity.getId()) && !LoginUserInfoEntity.ADMIN_LOGIN_ID.equals(loginUserInfoEntity.getLoginId())) {
                sb.append(QUERY_PATH);
                sb.append(String.format(USER_PATH, loginUserInfoEntity.getId()));
            }

            return Long.parseLong((String) restClient.find(sb.toString(), MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 最上位のカンバン階層を取得する。
     *
     * @param from 頭数
     * @param to 尾数
     * @return カンバン階層一覧
     * @throws java.lang.Exception
     * @throws java.net.SocketTimeoutException
     */
    public List<KanbanHierarchyInfoEntity> getTopHierarchyRange(Long from, Long to) throws Exception, SocketTimeoutException {
        return this.getTopHierarchyRange(from, to, false);
    }
    

    /**
     * 最上位のカンバン階層を取得する。
     *
     * @param from 頭数
     * @param to 尾数
     * @param isAll true: すべての階層 false: adFactory Lite 階層を除く
     * @return カンバン階層一覧
     * @throws java.lang.Exception
     * @throws java.net.SocketTimeoutException
     */
    public List<KanbanHierarchyInfoEntity> getTopHierarchyRange(Long from, Long to, boolean isAll) throws Exception, SocketTimeoutException {
        logger.debug("getTopHierarchyRange:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append(TREE_PATH);
            sb.append(RANGE_PATH);

            // パラメータ
            sb.append(QUERY_PATH);

            if(Objects.nonNull(loginUserInfoEntity.getId()) && !LoginUserInfoEntity.ADMIN_LOGIN_ID.equals(loginUserInfoEntity.getLoginId())) {
                sb.append(String.format(USER_PATH, loginUserInfoEntity.getId()));
                sb.append(AND_PATH);
            }

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
                sb.append(AND_PATH);
            }

            sb.append(String.format(HAS_CHILD_PATH, String.valueOf(false)));

            List<KanbanHierarchyInfoEntity> entities = restClient.findAll(sb.toString(), new GenericType<List<KanbanHierarchyInfoEntity>>() {});
            return isAll ? entities : entities.stream().filter(s -> !s.getHierarchyName().equals(this.liteTreeName)).collect(Collectors.toList());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * IDで指定された階層のカンバン階層個数取得
     *
     * @param hierarchyId 親階層ID
     * @return カンバン階層個数
     */
    public Long getAffilationHierarchyCount(Long hierarchyId) {
        logger.debug("getAffilationHierarchyCount:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append(TREE_PATH);
            sb.append(COUNT_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(ID_PATH, hierarchyId.toString()));

            if(Objects.nonNull(loginUserInfoEntity.getId()) && !LoginUserInfoEntity.ADMIN_LOGIN_ID.equals(loginUserInfoEntity.getLoginId())) {
                sb.append(AND_PATH);
                sb.append(String.format(USER_PATH, loginUserInfoEntity.getId()));
            }

            return Long.parseLong((String) restClient.find(sb.toString(), MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return (long) 0;
        }
    }

    /**
     * 指定された階層にぶら下がったカンバン階層を取得する。
     *
     * @param hierarchyId 階層ID
     * @param from 頭数
     * @param to 尾数
     * @return カンバン階層一覧
     */
    public List<KanbanHierarchyInfoEntity> getAffilationHierarchyRange(Long hierarchyId, Long from, Long to) {
        return this.getAffilationHierarchyRange(hierarchyId, from, to, false);
    }

    /**
     * 指定された階層にぶら下がったカンバン階層を取得する。
     *
     * @param hierarchyId 階層ID
     * @param from 頭数
     * @param to 尾数
     * @param isAll true: すべての階層 false: adFactory Lite 階層を除く
     * @return カンバン階層一覧
     */
    public List<KanbanHierarchyInfoEntity> getAffilationHierarchyRange(Long hierarchyId, Long from, Long to, boolean isAll) {
        logger.debug("getAffilationHierarchyRange:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append(TREE_PATH);
            sb.append(RANGE_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(ID_PATH, hierarchyId.toString()));

            if(Objects.nonNull(loginUserInfoEntity.getId()) && !LoginUserInfoEntity.ADMIN_LOGIN_ID.equals(loginUserInfoEntity.getLoginId())) {
                sb.append(AND_PATH);
                sb.append(String.format(USER_PATH, loginUserInfoEntity.getId()));
            }

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(AND_PATH);
                sb.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
            }

            sb.append(AND_PATH);
            sb.append(String.format(HAS_CHILD_PATH, String.valueOf(false)));

            List<KanbanHierarchyInfoEntity> entities = restClient.findAll(sb.toString(), new GenericType<List<KanbanHierarchyInfoEntity>>() {});
            if (hierarchyId != 0) {
                return entities;
            }
            
            return isAll ? entities : entities.stream().filter(o -> !o.getHierarchyName().equals(this.liteTreeName)).collect(Collectors.toList());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * カンバン階層情報取得
     *
     * @param id カンバンID
     * @return IDに該当するカンバン階層
     */
    public KanbanHierarchyInfoEntity find(Long id) {
        logger.debug("find:{}", id);
        try {
            return (KanbanHierarchyInfoEntity) this.getAffilationHierarchyRange(id, null, null);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new KanbanHierarchyInfoEntity();
        }
    }



    /**
     * 指定した組織IDのカンバン階層情報を取得する。
     *
     * @param ids カンバン階層ID一覧
     * @return 官官階層情報一覧
     */
    public List<KanbanHierarchyInfoEntity> find(List<Long> ids) {
        logger.info("find:{}", ids);
        try {
            if (ids.isEmpty()) {
                return new ArrayList<>();
            }

            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append(TREE_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(ID_PATH, ids.get(0)));

            for (int i = 1; i < ids.size(); i++) {
                sb.append(AND_PATH);
                sb.append(String.format(ID_PATH, ids.get(i)));
            }

            return restClient.find(sb.toString(), new GenericType<List<OrganizationInfoEntity>>(){});
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * カンバン階層を取得する。
     *
     * @param uri
     * @return
     */
    public KanbanHierarchyInfoEntity findURI(String uri) {
        logger.debug("find:{}", uri);
        try {
            return (KanbanHierarchyInfoEntity) restClient.find("/" + uri, KanbanHierarchyInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 新しいカンバン階層の登録
     *
     * @param entity 登録するカンバン階層情報
     * @return 登録の成否
     */
    public ResponseEntity regist(KanbanHierarchyInfoEntity entity) {
        logger.debug("regist:{}", entity);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append(TREE_PATH);

            return (ResponseEntity) restClient.post(sb.toString(), entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * カンバン階層の更新
     *
     * @param entity
     * @return
     */
    public ResponseEntity update(KanbanHierarchyInfoEntity entity) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append(TREE_PATH);

            return (ResponseEntity) restClient.put(sb.toString(), entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * カンバン階層の削除
     *
     * @param entity
     * @return
     */
    public ResponseEntity delete(KanbanHierarchyInfoEntity entity) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append(TREE_PATH);

            return (ResponseEntity) restClient.delete(sb.toString(), entity.getKanbanHierarchyId(), ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ResponseEntity();
    }

    /**
     * 指定したカンバン階層名のカンバン階層を取得 (階層のみでカンバンリストは取得しない)
     *
     * @param name カンバン階層名
     * @return カンバン階層
     */
    public KanbanHierarchyInfoEntity findHierarchyName(String name) {
        logger.debug("findHierarchyName:{}", name);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append(TREE_PATH);
            sb.append(HIERARCHY_PATH);
            sb.append(NAME_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(NAME_QUERY_PATH, name));

            if(Objects.nonNull(loginUserInfoEntity.getId()) && !LoginUserInfoEntity.ADMIN_LOGIN_ID.equals(loginUserInfoEntity.getLoginId())) {
                sb.append(AND_PATH);
                sb.append(String.format(USER_PATH, loginUserInfoEntity.getId()));
            }

            return (KanbanHierarchyInfoEntity) restClient.find(sb.toString(), KanbanHierarchyInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new KanbanHierarchyInfoEntity();
        }
    }

    /**
     * idにてカンバン階層情報を取得
     * @param ids カンバン階層id
     * @return カンバン階層情報
     */
    public List<KanbanHierarchyInfoEntity> findHierarchyIds(List<Long> ids) {
        logger.debug("findHierarchyIds={}",ids);
        try {

            if(ids.isEmpty()) {
                return new ArrayList<>();
            }

            StringBuilder sb = new StringBuilder();
            sb.append(KANBAN_PATH);
            sb.append(TREE_PATH);
            sb.append(HIERARCHY_PATH);
            sb.append(IDS_PATH);
            sb.append(QUERY_PATH);
            sb.append(ids.stream()
                    .map(id->String.format(ID_PATH, id))
                    .collect(Collectors.joining(AND_PATH)));

            return restClient.find(sb.toString(), new GenericType<List<KanbanHierarchyInfoEntity>>(){});
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }


}
