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
import jp.adtekfuji.adFactory.entity.holiday.HolidayInfoEntity;
import jp.adtekfuji.adFactory.entity.search.HolidaySearchCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 休日情報取得用RESTクラス
 *
 * @author nar-nakamura
 */
public class HolidayInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    private final static String HOLIDAY_PATH = "/holiday";
    private final static String COUNT_PATH = "/count";
    private final static String RANGE_PATH = "/range";
    private final static String SEARCH_PATH = "/search";

    private final static String QUERY_PATH = "?";
    private final static String AND_PATH = "&";
    private final static String ID_PATH = "id=%d";
    private final static String FROM_TO_PATH = "from=%d&to=%d";

    /**
     * コンストラクタ
     */
    public HolidayInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    /**
     * 休日情報を追加する。
     *
     * @param entity 休日情報
     * @return 結果
     */
    public ResponseEntity regist(HolidayInfoEntity entity) {
        logger.debug("regist:{}", entity);
        try {
            return (ResponseEntity) restClient.post(HOLIDAY_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
        }
    }

    /**
     * 休日情報を更新する。
     *
     * @param entity 休日情報
     * @return 結果
     */
    public ResponseEntity update(HolidayInfoEntity entity) {
        logger.debug("update:{}", entity);
        try {
            return (ResponseEntity) restClient.put(HOLIDAY_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
        }
    }

    /**
     * 休日情報を削除する。
     *
     * @param id 休日ID
     * @return 結果
     */
    public ResponseEntity delete(Long id) {
        logger.debug("delete:{}", id);
        try {
            return (ResponseEntity) restClient.delete(HOLIDAY_PATH, id, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
        }
    }

    /**
     * 休日情報を削除する。
     *
     * @param ids 休日ID一覧
     * @return 結果
     */
    public ResponseEntity delete(List<Long> ids) {
        logger.info("delete:{}", ids);
        try {
            if (ids.isEmpty()) {
                return new ResponseEntity();
            }

            StringBuilder sb = new StringBuilder();
            sb.append(HOLIDAY_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(ID_PATH, ids.get(0)));

            for (int i = 1; i < ids.size(); i++) {
                sb.append(AND_PATH);
                sb.append(String.format(ID_PATH, ids.get(i)));
            }

            return (ResponseEntity) restClient.delete(sb.toString(), ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
        }
    }

    /**
     * 休日情報を取得する。
     *
     * @param id 休日ID
     * @return 休日情報
     */
    public HolidayInfoEntity find(Long id) {
        logger.debug("find:{}", id);
        try {
            return (HolidayInfoEntity) restClient.find(HOLIDAY_PATH, id, HolidayInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new HolidayInfoEntity();
        }
    }

    /**
     * 休日情報一覧を取得する。
     *
     * @return 休日情報一覧
     */
    public List<HolidayInfoEntity> findAll() {
        logger.debug("findAll");
        try {
            return this.findRange(null, null);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList();
        }
    }

    /**
     * 休日情報一覧を範囲取得する。
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @return 指定範囲の休日情報一覧
     */
    public List<HolidayInfoEntity> findRange(Long from, Long to) {
        logger.debug("findRange: from={}, to={}", from, to);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(HOLIDAY_PATH);
            sb.append(RANGE_PATH);

            // パラメータ
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(QUERY_PATH);
                sb.append(String.format(FROM_TO_PATH, from, to));
            }

            return restClient.findAll(sb.toString(), new GenericType<List<HolidayInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList();
        }
    }

    /**
     * 休日情報の件数を取得する。
     *
     * @return 休日情報の件数
     */
    public Long count() {
        logger.debug("count");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(HOLIDAY_PATH);
            sb.append(COUNT_PATH);

            return Long.parseLong((String) restClient.find(sb.toString(), MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return 0L;
        }
    }

    /**
     * 条件を指定して休日情報一覧を取得する。
     *
     * @param condition 休日情報検索条件
     * @return 休日情報一覧
     */
    public List<HolidayInfoEntity> search(HolidaySearchCondition condition) {
        logger.info("search:{}", condition);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(HOLIDAY_PATH);
            sb.append(SEARCH_PATH);

            return restClient.put(sb.toString(), condition, new GenericType<List<HolidayInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 条件を指定して休日情報一覧を範囲取得する。
     *
     * @param condition 休日情報検索条件
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @return 休日情報一覧
     */
    public List<HolidayInfoEntity> searchRange(HolidaySearchCondition condition, Long from, Long to) {
        logger.info("searchRange:{}, from={}, to={}", condition, from, to);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(HOLIDAY_PATH);
            sb.append(SEARCH_PATH);
            sb.append(RANGE_PATH);

            // パラメータ
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(QUERY_PATH);
                sb.append(String.format(FROM_TO_PATH, from, to));
            }

            return restClient.put(sb.toString(), condition, new GenericType<List<HolidayInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 条件を指定して休日情報の件数を取得する。
     *
     * @param condition 休日情報検索条件
     * @return 休日情報の件数
     */
    public Long searchCount(HolidaySearchCondition condition) {
        logger.info("searchCount:{}", condition);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(HOLIDAY_PATH);
            sb.append(SEARCH_PATH);
            sb.append(COUNT_PATH);

            return Long.parseLong((String) restClient.put(sb.toString(), condition, MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return 0L;
        }
    }
}
