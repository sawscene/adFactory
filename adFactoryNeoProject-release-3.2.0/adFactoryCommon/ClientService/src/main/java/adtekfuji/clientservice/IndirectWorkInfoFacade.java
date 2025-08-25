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
import jp.adtekfuji.adFactory.entity.indirectwork.IndirectWorkInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 間接作業情報用RESTクラス
 *
 * @author nar-nakamura
 */
public class IndirectWorkInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    private final static String INDIRECT_WORK_PATH = "/indirect-work";
    private final static String COUNT_PATH = "/count";
    private final static String RANGE_PATH = "/range";
    private final static String ACTIVE_PATH = "/working";
    private final static String ID_TARGET_PATH = "/%s";
    private final static String WORK_NO_PATH = "/work-number";
    private final static String CATEGORY_PATH = "/category";

    private final static String QUERY_PATH = "?";
    private final static String AND_PATH = "&";
    private final static String FROM_TO_PATH = "from=%s&to=%s";
    private final static String CLASS_NUMBER_PATH = "class=%s";
    private final static String WORK_NUMBER_PATH = "work=%s";
    private final static String ID_PATH = "id=%s";

    /**
     * コンストラクタ
     */
    public IndirectWorkInfoFacade() {
        this.restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    /**
     * コンストラクタ
     *
     * @param uriBase
     */
    public IndirectWorkInfoFacade(String uriBase) {
        this.restClient = new RestClient(new RestClientProperty(uriBase));
    }

    /**
     * 間接作業情報一覧を取得する。
     *
     * @return 間接作業情報一覧
     */
    public List<IndirectWorkInfoEntity> findAll() {
        logger.debug("findAll:start");
        try {
            return restClient.findAll(INDIRECT_WORK_PATH, new GenericType<List<IndirectWorkInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 間接作業情報の件数を取得する。
     *
     * @return 件数
     */
    public Long count() {
        logger.debug("count:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(INDIRECT_WORK_PATH);
            sb.append(COUNT_PATH);

            return Long.parseLong((String) restClient.find(sb.toString(), MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return (long) 0;
        }
    }

    /**
     * 間接作業情報一覧を範囲指定して取得する。
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @return 指定された範囲の間接作業情報一覧
     */
    public List<IndirectWorkInfoEntity> findRange(Long from, Long to) {
        logger.debug("findRange:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(INDIRECT_WORK_PATH);
            sb.append(RANGE_PATH);

            // パラメータ
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(QUERY_PATH);
                sb.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
            }

            return restClient.findAll(sb.toString(), new GenericType<List<IndirectWorkInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 指定したIDの間接作業情報を取得する。
     *
     * @param id 間接作業ID
     * @return 間接作業情報
     */
    public IndirectWorkInfoEntity find(Long id) {
        logger.debug("find:{}", id);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(INDIRECT_WORK_PATH);
            sb.append(String.format(ID_TARGET_PATH, id.toString()));

            return (IndirectWorkInfoEntity) restClient.find(sb.toString(), IndirectWorkInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new IndirectWorkInfoEntity();
        }
    }

    /**
     * 指定した分類番号・作業番号の間接作業情報を取得する。
     *
     * @param classNumber 分類番号
     * @param workNumber 作業番号
     * @return 間接作業情報
     */
    public IndirectWorkInfoEntity find(String classNumber, String workNumber) {
        logger.debug("find:{},{}", classNumber, workNumber);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(INDIRECT_WORK_PATH);
            sb.append(WORK_NO_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(CLASS_NUMBER_PATH, RestClient.encode(classNumber)));
            sb.append(AND_PATH);
            sb.append(String.format(WORK_NUMBER_PATH, RestClient.encode(workNumber)));

            return (IndirectWorkInfoEntity) restClient.find(sb.toString(), IndirectWorkInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new IndirectWorkInfoEntity();
        }
    }

    /**
     * 間接作業情報を登録する。
     *
     * @param entity 間接作情報
     * @return 登録の成否
     */
    public ResponseEntity regist(IndirectWorkInfoEntity entity) {
        logger.debug("regist:{}", entity);
        try {
            return (ResponseEntity) restClient.post(INDIRECT_WORK_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 間接作業情報を更新する。
     *
     * @param entity 間接作業情報
     * @return 更新の成否
     */
    public ResponseEntity update(IndirectWorkInfoEntity entity) {
        try {
            return (ResponseEntity) restClient.put(INDIRECT_WORK_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 間接作業情報を削除する。
     *
     * @param entity 間接作業情報
     * @return 削除の成否
     */
    public ResponseEntity delete(IndirectWorkInfoEntity entity) {
        try {
            return (ResponseEntity) restClient.delete(INDIRECT_WORK_PATH, entity.getIndirectWorkId(), ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 作業区分で指定された間接作業の個数を取得
     *
     * @param workCategoryId 作業区分ID
     * @return 個数
     */
    public Long getCountCategorized(Long workCategoryId) {
        logger.debug("getCountCategorized:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(INDIRECT_WORK_PATH);
            sb.append(CATEGORY_PATH);
            sb.append(COUNT_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(ID_PATH, workCategoryId));

            return Long.parseLong((String) restClient.find(sb.toString(), MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return (long) 0;
        }
    }

    /**
     * 作業区分で指定された間接作業の一覧を取得
     *
     * @param workCategoryId 作業区分ID
     * @param from 頭数
     * @param to 尾数
     * @return 間接作業マスタ一覧
     */
    public List<IndirectWorkInfoEntity> getCategorizedWork(Long workCategoryId, Long from, Long to) {
        logger.debug("getCategorizedWork:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(INDIRECT_WORK_PATH);
            sb.append(CATEGORY_PATH);
            sb.append(RANGE_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(ID_PATH, workCategoryId));

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(AND_PATH);
                sb.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
            }

            return restClient.findAll(sb.toString(), new GenericType<List<IndirectWorkInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * IDにて指定した実施中の間接作業数を返す
     * @param indirectWorkId 指定間接作業ID
     * @return 作業中の間接作業数
     */
    public Long getActiveIndirectWorkCount(Long indirectWorkId) {
        logger.debug("getActiveIndirectWorkCount:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(INDIRECT_WORK_PATH);
            sb.append(ACTIVE_PATH);
            sb.append(COUNT_PATH);
            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(ID_PATH, indirectWorkId));

            return Long.parseLong((String) restClient.find(sb.toString(), MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return 0L;
        }
    }

}
