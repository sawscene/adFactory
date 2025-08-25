/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.clientservice;

import jp.adtekfuji.adFactory.utility.JsonUtils;
import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import jakarta.ws.rs.core.GenericType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.ws.rs.core.MediaType;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.access.AccessHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.enumerate.AccessHierarchyTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author j.min
 */
public class AccessHierarchyInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();
    
    private final static String ACCESS_PATH = "/access";
    private final static String TREE_PATH = "/tree";
    private final static String COUNT_PATH = "/count";
    private final static String RANGE_PATH = "/range";
    private final static String ID_TARGET_PATH = "/%s";

    private final static String QUERY_PATH = "?";
    private final static String AND_PATH = "&";
    private final static String TYPE_PATH = "type=%s";
    private final static String ID_PATH = "id=%s";
    private final static String DATA_PATH = "data=%s";
    private final static String FROM_TO_PATH = "from=%s&to=%s";

    public AccessHierarchyInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    public AccessHierarchyInfoFacade(String uriBase){
        restClient = new RestClient(new RestClientProperty(uriBase));
    }

    /**
     * 指定した階層のアクセス権に許可した組織情報を取得する。
     *
     * @param type アクセス階層タイプ
     * @param parentId 階層ID
     * @return 階層のアクセス組織
     */
    public List<OrganizationInfoEntity> find(AccessHierarchyTypeEnum type, Long parentId) {
        logger.debug("find:{},{}", type, parentId);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ACCESS_PATH);
            sb.append(TREE_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(TYPE_PATH, type));
            sb.append(AND_PATH);
            sb.append(String.format(ID_PATH, parentId.toString()));
            
            return restClient.findAll(sb.toString(), new GenericType<List<OrganizationInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }
    
    /**
     * 指定した階層のアクセス権に許可した組織情報の件数を取得する。
     *
     * @param type アクセス階層タイプ
     * @param parentId 階層ID
     * @return 階層のアクセス組織個数
     */
    public Long getCount(AccessHierarchyTypeEnum type, Long parentId) {
        logger.debug("getCount:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ACCESS_PATH);
            sb.append(TREE_PATH);
            sb.append(COUNT_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(TYPE_PATH, type));
            sb.append(AND_PATH);
            sb.append(String.format(ID_PATH, parentId.toString()));

            return Long.parseLong((String) restClient.find(sb.toString(), MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return (long) 0;
        }
    }

    /**
     * 指定した階層のアクセス権に許可したfrom～toの組織情報を取得する。
     *
     * @param type アクセス階層タイプ
     * @param parentId 階層ID
     * @param from 頭数
     * @param to 尾数
     * @return 階層のアクセス組織のfrom～to組織情報
     */
    public List<OrganizationInfoEntity> getRange(AccessHierarchyTypeEnum type, Long parentId, Long from, Long to) {
        logger.debug("getRange:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ACCESS_PATH);
            sb.append(TREE_PATH);
            sb.append(RANGE_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(TYPE_PATH, type));
            sb.append(AND_PATH);
            sb.append(String.format(ID_PATH, parentId.toString()));

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(AND_PATH);
                sb.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
            }

            return restClient.findAll(sb.toString(), new GenericType<List<OrganizationInfoEntity>>() {
            });          
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }
    
    /**
     * 指定した階層にアクセス権を拒否⇒許可する組織IDを登録する。
     *
     * @param type アクセス階層タイプ
     * @param parentId 階層ID
     * @param datas 許可する組織ID
     * @return 登録の成否
     */
    public ResponseEntity regist(AccessHierarchyTypeEnum type, Long parentId, List<OrganizationInfoEntity> datas) {
        logger.debug("regist:{},{},{}", type, parentId, datas);
     
        try {
            ResponseEntity result = new ResponseEntity();
            for(OrganizationInfoEntity data : datas) {
                // 追加情報をJSONにする
                String jsonStr = JsonUtils.objectsToJson(data.getPropertyInfoCollection());
                data.setOrganizationAddInfo(jsonStr);
                
                StringBuilder sb = new StringBuilder();
                sb.append(ACCESS_PATH);
                sb.append(TREE_PATH);

                result = (ResponseEntity) restClient.post(sb.toString()
                        , new AccessHierarchyInfoEntity(type, parentId, data.getOrganizationId()), ResponseEntity.class);
            }
            return result;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }
    
    /**
     * 指定した階層にアクセス権を許可⇒拒否する組織IDを削除する。
     *
     * @param type アクセス階層タイプ
     * @param parentId 階層ID
     * @param datas 拒否する組織ID
     * @return 登録の成否
     */
    public ResponseEntity delete(AccessHierarchyTypeEnum type, Long parentId, List<OrganizationInfoEntity> datas) {
        logger.debug("delete:{},{},{}", type, parentId, datas);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ACCESS_PATH);
            sb.append(TREE_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(TYPE_PATH, type));
            sb.append(AND_PATH);
            sb.append(String.format(ID_PATH, parentId.toString()));
            sb.append(AND_PATH);
            sb.append(String.format(DATA_PATH, datas.get(0).getOrganizationId()));

            for (int i = 1; i < datas.size(); i++) {
                sb.append(AND_PATH);
                sb.append(String.format(DATA_PATH, datas.get(i).getOrganizationId()));
            }

            return (ResponseEntity) restClient.delete(sb.toString(), ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ResponseEntity();
    }
}
