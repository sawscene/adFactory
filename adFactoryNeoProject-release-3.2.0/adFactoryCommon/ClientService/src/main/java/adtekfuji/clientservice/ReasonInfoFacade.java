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
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.master.ReasonInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ReasonTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 理由情報REST
 *
 * @author
 */
public class ReasonInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    private final static String REASON_PATH = "/reason";
    private final static String ID_TARGET_PATH = "/%s";
    private final static String TYPE_PATH = "/type";
    private final static String CATEGORY_NAME_PATH = "/category-name";
    private final static String QUERY_PATH = "?";

    public ReasonInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    public ReasonInfoFacade(String uriBase) {
        restClient = new RestClient(new RestClientProperty(uriBase));
    }

    /**
     * 指定のIDに該当する理由設定を取得
     *
     * @param id 休憩設定ID
     * @return IDに該当する休憩設定
     */
    public ReasonInfoEntity find(long id) {
        logger.debug("find:{}", id);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(REASON_PATH);
            sb.append(String.format(ID_TARGET_PATH, id));

            return (ReasonInfoEntity) restClient.find(sb.toString(), ReasonInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ReasonInfoEntity();
        }
    }

    /**
     * 指定の理由設定を登録する
     *
     * @param entity 休憩設定
     * @return サーバーからの応答
     */
    public ResponseEntity add(ReasonInfoEntity entity) {
        logger.debug("regist:{}", entity);
        try {
            return (ResponseEntity) restClient.post(REASON_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 指定の理由設定を更新する
     *
     * @param entity 理由設定
     * @return サーバーからの応答
     */
    public ResponseEntity update(ReasonInfoEntity entity) {
        logger.debug("update:{}", entity);
        try {
            return (ResponseEntity) restClient.put(REASON_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 指定のIDに該当する理由設定を削除する
     *
     * @param id 休憩設定ID
     * @return サーバーからの応答
     */
    public ResponseEntity remove(long id) {
        logger.debug("remove:{}", id);
        try {
            return (ResponseEntity) restClient.delete(REASON_PATH, id, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
        }
    }

    /**
     * 指定されたタイプの理由一覧を取得
     *
     * @param type
     * @return 指定された範囲のカンバン一覧
     */
    public List<ReasonInfoEntity> findType(ReasonTypeEnum type) {
        logger.info("findType:{}", type);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(REASON_PATH);
            sb.append(TYPE_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format("type=%s", type.name()));

            return restClient.find(sb.toString(), new GenericType<List<ReasonInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 指定した理由区分IDの理由情報一覧を取得する。
     *
     * @param reasonCategoryId 理由区分ID
     * @return 理由区分一覧
     */
    public List<ReasonInfoEntity> findAllByCategoryId(Long reasonCategoryId) {
        logger.info("findAllByCategoryId:{}", reasonCategoryId);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(REASON_PATH);

            // パラメータ
            sb.append(QUERY_PATH);

            if (Objects.nonNull(reasonCategoryId)) {
                sb.append(String.format("reasonCategoryId=%d", reasonCategoryId));
            } else {
                return new ArrayList<>();
            }

            return restClient.find(sb.toString(), new GenericType<List<ReasonInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 指定した理由区分名の理由情報一覧を取得する。
     *
     * @param name 理由区分名
     * @param type 理由種別
     * @return 理由区分一覧
     */
    public List<ReasonInfoEntity> findAllByCategoryName(String name, ReasonTypeEnum type) {
        logger.info("findAllByCategory:{}, {}", name, type);
        try {
            if (StringUtils.isEmpty(name) || Objects.isNull(type)) {
                return new ArrayList<>();
            }                
                
            StringBuilder sb = new StringBuilder();
            sb.append(REASON_PATH);
            sb.append(CATEGORY_NAME_PATH);
            sb.append(QUERY_PATH);
            sb.append("reasonCategoryName=");
            sb.append(URLEncoder.encode(name, "UTF-8"));
            sb.append("&type=");
            sb.append(type.name());

            return restClient.find(sb.toString(), new GenericType<List<ReasonInfoEntity>>() {});
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }
}
