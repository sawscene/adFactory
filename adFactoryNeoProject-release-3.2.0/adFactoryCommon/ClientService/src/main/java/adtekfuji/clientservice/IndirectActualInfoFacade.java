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
import jakarta.ws.rs.core.MediaType;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.indirectwork.IndirectActualInfoEntity;
import jp.adtekfuji.adFactory.entity.search.IndirectActualSearchCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 間接工数実績情報用RESTクラス
 *
 * @author nar-nakamura
 */
public class IndirectActualInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    private final static String INDIRECT_ACTUAL_PATH = "/indirect-actual";
    private final static String COUNT_PATH = "/count";
    private final static String RANGE_PATH = "/range";
    private final static String SEARCH_PATH = "/search";
    private final static String ID_TARGET_PATH = "/%s";

    private final static String QUERY_PATH = "?";
    private final static String AND_PATH = "&";
    private final static String FROM_TO_PATH = "from=%s&to=%s";

    /**
     * コンストラクタ
     */
    public IndirectActualInfoFacade() {
        this.restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    /**
     * コンストラクタ
     *
     * @param uriBase 
     */
    public IndirectActualInfoFacade(String uriBase) {
        this.restClient = new RestClient(new RestClientProperty(uriBase));
    }

    /**
     * 間接工数実績情報一覧を取得する。
     *
     * @return 間接工数実績情報一覧
     */
    public List<IndirectActualInfoEntity> findAll() {
        logger.debug("findAll:start");
        try {
            return restClient.findAll(INDIRECT_ACTUAL_PATH, new GenericType<List<IndirectActualInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 間接工数実績情報の件数を取得する。
     *
     * @return 件数
     */
    public Long count() {
        logger.debug("count:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(INDIRECT_ACTUAL_PATH);
            sb.append(COUNT_PATH);

            return Long.parseLong((String) restClient.find(sb.toString(), MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return (long) 0;
        }
    }

    /**
     * 間接工数実績情報一覧を範囲指定して取得する。
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @return 指定された範囲の間接工数実績情報一覧
     */
    public List<IndirectActualInfoEntity> findRange(Long from, Long to) {
        logger.debug("findRange:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(INDIRECT_ACTUAL_PATH);
            sb.append(RANGE_PATH);

            // パラメータ
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(QUERY_PATH);
                sb.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
            }

            return restClient.findAll(sb.toString(), new GenericType<List<IndirectActualInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 指定したIDの間接工数実績情報を取得する。
     *
     * @param id 間接作業ID
     * @return 間接工数実績情報
     */
    public IndirectActualInfoEntity find(Long id) {
        logger.debug("find:{}", id);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(INDIRECT_ACTUAL_PATH);
            sb.append(String.format(ID_TARGET_PATH, id.toString()));

            return (IndirectActualInfoEntity) restClient.find(sb.toString(), IndirectActualInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new IndirectActualInfoEntity();
        }
    }

    /**
     * 条件検索して間接工数実績一覧の件数を取得する。
     *
     * @param condition 検索条件
     * @return 件数
     */
    public Long searchCount(IndirectActualSearchCondition condition) {
        logger.info("searchCount:{}", condition);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(INDIRECT_ACTUAL_PATH);
            sb.append(SEARCH_PATH);
            sb.append(COUNT_PATH);

            return Long.parseLong((String) restClient.put(sb.toString(), condition, MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return 0L;
    }

    /**
     * 条件検索して間接工数実績一覧を範囲指定して取得する。
     *
     * @param condition 検索条件
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @return 指定された範囲の間接工数実績情報一覧
     */
    public List<IndirectActualInfoEntity> searchRange(IndirectActualSearchCondition condition, Long from, Long to) {
        logger.info("searchRange:{},{},{}", condition, from, to);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(INDIRECT_ACTUAL_PATH);
            sb.append(SEARCH_PATH);
            sb.append(RANGE_PATH);

            // パラメータ
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
            }

            return restClient.put(sb.toString(), condition, new GenericType<List<IndirectActualInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ArrayList<>();
    }

    /**
     * 間接工数実績情報を登録する。
     *
     * @param entity 間接工数実績情報
     * @return 登録の成否
     */
    public ResponseEntity regist(IndirectActualInfoEntity entity) {
        logger.debug("regist:{}", entity);
        try {
            return (ResponseEntity) restClient.post(INDIRECT_ACTUAL_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 間接工数実績情報を更新する。
     *
     * @param entity 間接工数実績情報
     * @return 更新の成否
     */
    public ResponseEntity update(IndirectActualInfoEntity entity) {
        try {
            return (ResponseEntity) restClient.put(INDIRECT_ACTUAL_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 間接工数実績情報を削除する。
     *
     * @param entity 間接工数実績情報
     * @return 削除の成否
     */
    public ResponseEntity delete(IndirectActualInfoEntity entity) {
        try {
            return (ResponseEntity) restClient.delete(INDIRECT_ACTUAL_PATH, entity.getIndirectActualId(), ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }
}
