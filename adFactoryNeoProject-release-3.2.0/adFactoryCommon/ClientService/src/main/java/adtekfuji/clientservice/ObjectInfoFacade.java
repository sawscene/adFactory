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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import jakarta.ws.rs.core.MediaType;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.object.ObjectInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nar-nakamura
 */
public class ObjectInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    private final static String OBJECT_PATH = "/object";
    private final static String TYPE_PATH = "/type";
    private final static String COUNT_PATH = "/count";
    private final static String RANGE_PATH = "/range";

    private final static String QUERY_PATH = "?";
    private final static String AND_PATH = "&";
    private final static String ID_PATH = "id=%s";
    private final static String TYPEID_PATH = "typeid=%s";
    private final static String FROM_TO_PATH = "from=%s&to=%s";

    // キャッシュ
    private ArrayList<ObjectInfoEntity> cache;

    public ObjectInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    /**
     * モノ種別で指定されたモノの個数を取得
     *
     * @param objectTypeId モノ種別ID
     * @return 個数
     */
    public Long getAffilationHierarchyCount(Long objectTypeId) {
        logger.debug("getAffilationHierarchyCount:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(OBJECT_PATH);
            sb.append(TYPE_PATH);
            sb.append(COUNT_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(ID_PATH, objectTypeId));

            return Long.parseLong((String) restClient.find(sb.toString(), MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return (long) 0;
        }
    }

    /**
     * モノ種別で指定されたモノの一覧を取得
     *
     * @param objectTypeId モノ種別ID
     * @param from 頭数
     * @param to 尾数
     * @return モノマスタ一覧
     */
    public List<ObjectInfoEntity> getAffilationHierarchyRange(Long objectTypeId, Long from, Long to) {
        logger.debug("getAffilationHierarchyRange:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(OBJECT_PATH);
            sb.append(TYPE_PATH);
            sb.append(RANGE_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(ID_PATH, objectTypeId));

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(AND_PATH);
                sb.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
            }

            return restClient.findAll(sb.toString(), new GenericType<List<ObjectInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 新しいモノの登録
     *
     * @param entity 登録するモノ情報
     * @return 登録の成否
     */
    public ResponseEntity regist(ObjectInfoEntity entity) {
        logger.debug("regist:{}", entity);
        try {
            return (ResponseEntity) restClient.post(OBJECT_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * モノの更新
     *
     * @param objectId 更新対象 モノID
     * @param objectTypeId 更新対象 モノ種別ID
     * @param entity 登録するモノ情報
     * @return 更新の成否
     */
    public ResponseEntity update(String objectId, Long objectTypeId, ObjectInfoEntity entity) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(OBJECT_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(ID_PATH, RestClient.encode(objectId)));
            sb.append(AND_PATH);
            sb.append(String.format(TYPEID_PATH, objectTypeId));

            return (ResponseEntity) restClient.put(sb.toString(), entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返しますOrganization
            return new ResponseEntity();
        }
    }

    /**
     * モノの削除
     *
     * @param entity
     * @return
     */
    public ResponseEntity delete(ObjectInfoEntity entity) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(OBJECT_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(ID_PATH, RestClient.encode(entity.getObjectId())));
            sb.append(AND_PATH);
            sb.append(String.format(TYPEID_PATH, entity.getFkObjectTypeId()));

            return (ResponseEntity) restClient.delete(sb.toString(), ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);

        }
        return null;
    }

    /**
     * モノ情報を取得
     *
     * @param objectId モノID
     * @param objectTypeId モノ種別ID
     * @return モノ情報
     */
    public ObjectInfoEntity find(String objectId, Long objectTypeId) {
        logger.debug("find:{},{}", objectId, objectTypeId);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(OBJECT_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(ID_PATH, RestClient.encode(objectId)));
            sb.append(AND_PATH);
            sb.append(String.format(TYPEID_PATH, objectTypeId));

            return (ObjectInfoEntity) restClient.find(sb.toString(), ObjectInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ObjectInfoEntity();
        }
    }

    /**
     * 全てのモノ情報を取得
     *
     * @return モノ情報一覧
     */
    public List<ObjectInfoEntity> findAll() {
        logger.debug("findAll:start");
        try {
            return restClient.findAll(OBJECT_PATH, new GenericType<List<ObjectInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * モノ情報数を取得
     *
     * @return
     */
    public Long count() {
        logger.debug("count:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(OBJECT_PATH);
            sb.append(COUNT_PATH);

            return Long.parseLong((String) restClient.find(sb.toString(), MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return (long) 0;
        }
    }

    /**
     * 指定された範囲のモノ情報を取得
     *
     * @param from 頭数
     * @param to 尾数
     * @return 指定された範囲のモノ情報一覧
     */
    public List<ObjectInfoEntity> findRange(Long from, Long to) {
        logger.debug("findRange:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(OBJECT_PATH);
            sb.append(RANGE_PATH);

            // パラメータ
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(QUERY_PATH);
                sb.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
            }

            return restClient.findAll(sb.toString(), new GenericType<List<ObjectInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * モノ情報を取得する
     *
     * @param objectId
     * @param objectTypeId
     * @return
     */
    public ObjectInfoEntity get(String objectId, long objectTypeId) {
        ObjectInfoEntity objectInfo = null;

        if (Objects.isNull(this.cache)) {
            this.cache = new ArrayList<>();
        }

        Optional<ObjectInfoEntity> opt = this.cache.stream().filter(o -> objectId.equals(o.getObjectId()) && objectTypeId == o.getFkObjectTypeId()).findFirst();
        if (opt.isPresent()) {
            objectInfo = opt.get();
        } else {
            objectInfo = this.find(objectId, objectTypeId);
            if (Objects.nonNull(objectInfo.getObjectId())) {
                this.cache.add(objectInfo);
            }
        }

        return objectInfo;
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
