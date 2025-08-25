/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.clientservice;

import adtekfuji.clientservice.common.Paths;
import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import jakarta.ws.rs.core.GenericType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.ws.rs.core.MediaType;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.master.ReasonCategoryInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ReasonTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 理由情報REST
 *
 * @author
 */
public class ReasonCategoryInfoFacede {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    private final static String REASON_CATEGORY_PATH = "/reason/category";
    private final static String ID_TARGET_PATH = "/%s";
    private final static String TYPE_PATH = "/type";
    private final static String NAME_PATH = "/name";
    private final static String QUERY_PATH = "?";

    public ReasonCategoryInfoFacede() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    public ReasonCategoryInfoFacede(String uriBase) {
        restClient = new RestClient(new RestClientProperty(uriBase));
    }

    /**
     * 理由区分を追加する
     *
     * @param entity 休憩設定
     * @return サーバーからの応答
     */
    public ResponseEntity add(ReasonCategoryInfoEntity entity) {
        logger.debug("regist:{}", entity);
        try {
            return (ResponseEntity) restClient.post(REASON_CATEGORY_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
        }
    }

    /**
     * 理由区分を更新する
     *
     * @param entity 理由設定
     * @return サーバーからの応答
     */
    public ResponseEntity update(ReasonCategoryInfoEntity entity) {
        logger.debug("update:{}", entity);
        try {
            return (ResponseEntity) restClient.put(REASON_CATEGORY_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
        }
    }

    /**
     * 理由区分を削除する
     *
     * @param id 理由区分ID
     * @return サーバーからの応答
     */
    public ResponseEntity remove(long id) {
        logger.debug("remove:{}", id);
        try {
            return (ResponseEntity) restClient.delete(REASON_CATEGORY_PATH, id, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
        }
    }
    
    /**
     * 指定したIDの理由区分を取得する。
     *
     * @param id 理由区分ID
     * @return 理由区分
     */
    public ReasonCategoryInfoEntity find(long id) {
        logger.debug("find:{}", id);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(REASON_CATEGORY_PATH);
            sb.append(String.format(ID_TARGET_PATH, id));

            return (ReasonCategoryInfoEntity) restClient.find(sb.toString(), ReasonCategoryInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ReasonCategoryInfoEntity();
        }
    }

    /**
     * 指定した理由区分名の理由区分を取得する。
     *
     * @param name 理由区分名
     * @return 理由区分
     */
    public ReasonCategoryInfoEntity findByName(String name, ReasonTypeEnum type) {
        logger.debug("findByName:{}", name);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(REASON_CATEGORY_PATH);
            sb.append(NAME_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format("name=%s", name));
            sb.append(String.format("&type=%s", type.name()));

            return (ReasonCategoryInfoEntity) restClient.find(sb.toString(), ReasonCategoryInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ReasonCategoryInfoEntity();
        }
    }

    /**
     * 指定した理由種別の理由区分一覧を取得する
     *
     * @param type 理由種別
     * @return 理由区分一覧
     */
    public List<ReasonCategoryInfoEntity> findType(ReasonTypeEnum type) {
        logger.info("findType:{}", type);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(REASON_CATEGORY_PATH);
            sb.append(TYPE_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format("type=%s", type.name()));

            return restClient.find(sb.toString(), new GenericType<List<ReasonCategoryInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 理由区分の件数を取得する。
     *
     * @return 理由区分の件数
     */
    public Long count() {
        logger.debug("getCount: start");
        try {
            StringBuilder path = new StringBuilder();
            path.append(REASON_CATEGORY_PATH);
            path.append(Paths.COUNT_PATH);

            String count = (String) restClient.find(path.toString(), MediaType.TEXT_PLAIN_TYPE, String.class);
            return Long.parseLong(count);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return 0l;
        }
    }

    /**
     * 工程一覧を範囲指定で取得
     *
     * @param from 頭数
     * @param to 尾数
     * @return 工程一覧
     */
    public List<ReasonCategoryInfoEntity> findRange(Integer from, Integer to) {
        logger.debug("findRange: start");
        try {
            StringBuilder path = new StringBuilder();
            path.append(REASON_CATEGORY_PATH);
            path.append(Paths.RANGE_PATH);
            path.append(Paths.QUERY_PATH);

            // パラメータ
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                path.append(String.format(Paths.FROM_TO_PATH, from, to));
            }

            return restClient.findAll(path.toString(), new GenericType<List<ReasonCategoryInfoEntity>>() {});

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }
}
