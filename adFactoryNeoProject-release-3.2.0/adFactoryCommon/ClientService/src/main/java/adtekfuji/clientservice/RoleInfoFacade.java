/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.clientservice;

import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import jakarta.ws.rs.core.GenericType;
import java.util.List;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.master.RoleAuthorityInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 役割設定取得用RESTクラス
 *
 * @author ke.yokoi
 */
public class RoleInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    private final static String ROLE_PATH = "/role";

    public RoleInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }
    
    /**
     * 役割設定全取得
     *
     * @return 役割リスト
     */
    public List<RoleAuthorityInfoEntity> findAll() {
        logger.debug("findAll:");
        try {
            return restClient.findAll(ROLE_PATH, new GenericType<List<RoleAuthorityInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }
    
        /**
     * 指定のIDに該当する役割設定を取得
     *
     * @param id 設定ID
     * @return IDに該当する休憩設定
     */
    public RoleAuthorityInfoEntity find(long id) {
        logger.debug("find:{}", id);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ROLE_PATH).append("/").append(id);

            return (RoleAuthorityInfoEntity) restClient.find(sb.toString(), RoleAuthorityInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new RoleAuthorityInfoEntity();
        }
    }

    /**
     * 指定の役割設定を登録する
     *
     * @param entity 設定
     * @return サーバーからの応答
     */
    public ResponseEntity add(RoleAuthorityInfoEntity entity) {
        logger.debug("regist:{}", entity);
        try {
            return (ResponseEntity) restClient.post(ROLE_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }
    
        /**
     * 指定の役割設定を更新する
     *
     * @param entity 休憩設定
     * @return サーバーからの応答
     */
    public ResponseEntity update(RoleAuthorityInfoEntity entity) {
        logger.debug("update:{}", entity);
        try {
            return (ResponseEntity) restClient.put(ROLE_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 指定のIDに該当する役割設定を削除する
     *
     * @param id 設定ID
     * @return サーバーからの応答
     */
    public ResponseEntity remove(long id) {
        logger.debug("remove:{}", id);
        try {
            return (ResponseEntity) restClient.delete(ROLE_PATH, id, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
        }
    }

}
