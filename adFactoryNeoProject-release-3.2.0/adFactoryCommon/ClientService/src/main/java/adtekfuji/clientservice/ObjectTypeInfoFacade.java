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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jakarta.ws.rs.core.MediaType;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.object.ObjectTypeInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nar-nakamura
 */
public class ObjectTypeInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    private final static String OBJECTTYPE_PATH = "/object-type";
    private final static String COUNT_PATH = "/count";
    private final static String RANGE_PATH = "/range";
    private final static String NAME_PATH = "/name";

    private final static String QUERY_PATH = "?";
    private final static String ID_PATH = "/%s";
    private final static String FROM_TO_PATH = "from=%s&to=%s";
    private final static String NAME_QUERY_PATH = "name=%s";

    // キャッシュ
    private Map<Long, ObjectTypeInfoEntity> cache;

    public ObjectTypeInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    /**
     * 新しいモノ種別の登録
     *
     * @param entity 登録するモノ種別
     * @return 登録の成否
     */
    public ResponseEntity regist(ObjectTypeInfoEntity entity) {
        logger.debug("regist:{}", entity);
        try {
            return (ResponseEntity) restClient.post(OBJECTTYPE_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * モノ種別の更新
     *
     * @param entity
     * @return
     */
    public ResponseEntity update(ObjectTypeInfoEntity entity) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(OBJECTTYPE_PATH);

            return (ResponseEntity) restClient.put(sb.toString(), entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * モノ種別の削除
     *
     * @param entity
     * @return
     */
    public ResponseEntity delete(ObjectTypeInfoEntity entity) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(OBJECTTYPE_PATH);
            sb.append(String.format(ID_PATH, entity.getObjectTypeId()));

            return (ResponseEntity) restClient.delete(sb.toString(), ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * モノ種別を取得
     *
     * @param objectTypeId モノ種別ID
     * @return モノ種別
     */
    public ObjectTypeInfoEntity find(Long objectTypeId) {
        logger.debug("find:{}", objectTypeId);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(OBJECTTYPE_PATH);
            sb.append(String.format(ID_PATH, objectTypeId));

            return (ObjectTypeInfoEntity) restClient.find(sb.toString(), ObjectTypeInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ObjectTypeInfoEntity();
        }
    }

    /**
     * 全てのモノ種別を取得
     *
     * @return モノ種別一覧
     */
    public List<ObjectTypeInfoEntity> findAll() {
        logger.debug("findAll:start");
        try {
            return restClient.findAll(OBJECTTYPE_PATH, new GenericType<List<ObjectTypeInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * モノ種別数を取得
     *
     * @return
     */
    public Long count() {
        logger.debug("count:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(OBJECTTYPE_PATH);
            sb.append(COUNT_PATH);

            return Long.parseLong((String) restClient.find(sb.toString(), MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return (long) 0;
        }
    }

    /**
     * 指定された範囲のモノ種別を取得
     *
     * @param from 頭数
     * @param to 尾数
     * @return 指定された範囲のモノ種別一覧
     */
    public List<ObjectTypeInfoEntity> findRange(Long from, Long to) {
        logger.debug("findRange:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(OBJECTTYPE_PATH);
            sb.append(RANGE_PATH);

            // パラメータ
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(QUERY_PATH);
                sb.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
            }

            return restClient.findAll(sb.toString(), new GenericType<List<ObjectTypeInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 指定された名前のモノ種別を取得
     *
     * @param name 名前
     * @return モノ種別
     */
    public ObjectTypeInfoEntity findName(String name) {
        logger.debug("find name:{}", name);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(OBJECTTYPE_PATH);
            sb.append(NAME_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(NAME_QUERY_PATH, name));

            return (ObjectTypeInfoEntity) restClient.find(sb.toString(), ObjectTypeInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ObjectTypeInfoEntity();
        }
    }

    /**
     * モノ情報を取得する
     *
     * @param objectTypeId モノ種別ID
     * @return
     */
    public ObjectTypeInfoEntity get(Long objectTypeId) {
        ObjectTypeInfoEntity objectType = null;

        if (Objects.isNull(this.cache)) {
            this.cache = new HashMap<>();
        }

        if (this.cache.containsKey(objectTypeId)) {
            objectType = this.cache.get(objectTypeId);
        } else {
            objectType = this.find(objectTypeId);
            if (Objects.nonNull(objectType.getObjectTypeId())) {
                this.cache.put(objectTypeId, objectType);
            }
        }

        return objectType;
    }

    /**
     * キャッシュをクリアする
     */
    public void clearCache() {
        if (Objects.nonNull(this.cache)) {
            this.cache.clear();
        }
    }
}
