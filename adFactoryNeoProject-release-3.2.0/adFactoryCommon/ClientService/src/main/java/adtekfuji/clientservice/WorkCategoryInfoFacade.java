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
import jp.adtekfuji.adFactory.entity.indirectwork.WorkCategoryInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 間接作業 作業区分情報取得用RESTクラス
 *
 * @author s-maeda
 */
public class WorkCategoryInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    private final static String WORK_CATEGORY_PATH = "/workcategory";
    private final static String COUNT_PATH = "/count";
    private final static String RANGE_PATH = "/range";
    private final static String ID_TARGET_PATH = "/%s";
    private final static String NAME_PATH = "/name";

    private final static String QUERY_PATH = "?";
    private final static String FROM_TO_PATH = "from=%s&to=%s";
    private final static String NAME_QUERY = "name=%s";

    public WorkCategoryInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    public WorkCategoryInfoFacade(String uriBase) {
        restClient = new RestClient(new RestClientProperty(uriBase));
    }

    /**
     * 作業区分を取得
     *
     * @param id
     * @return 作業区分
     */
    public WorkCategoryInfoEntity find(Long id) {
        logger.debug("find:{}", id);
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORK_CATEGORY_PATH);
            path.append(String.format(ID_TARGET_PATH, id.toString()));

            return (WorkCategoryInfoEntity) restClient.find(path.toString(), WorkCategoryInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 作業区分を取得
     *
     * @param name
     * @return 作業区分
     */
    public WorkCategoryInfoEntity find(String name) {
        logger.debug("find:{}", name);
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORK_CATEGORY_PATH);
            path.append(NAME_PATH);

            // パラメータ
            path.append(QUERY_PATH);
            path.append(String.format(NAME_QUERY, name));

            return (WorkCategoryInfoEntity) restClient.find(path.toString(), WorkCategoryInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 作業区分情報一覧を取得する。
     *
     * @return 作業区分情報一覧
     */
    public List<WorkCategoryInfoEntity> findAll() {
        logger.debug("findAll");
        try {
            return this.findRange(null, null);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList();
        }
    }

    /**
     * 作業区分の数を取得
     *
     * @return 作業区分の数
     */
    public Long count() {
        logger.debug("count:start");
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORK_CATEGORY_PATH);
            path.append(COUNT_PATH);

            String count = (String) restClient.find(path.toString(), MediaType.TEXT_PLAIN_TYPE, String.class);
            return Long.parseLong(count);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return 0l;
    }

    /**
     * 作業区分を範囲指定で取得
     *
     * @param from 頭数
     * @param to 尾数
     * @return 作業区分一覧
     */
    public List<WorkCategoryInfoEntity> findRange(Long from, Long to) {
        logger.debug("findRange:start");
        try {
            StringBuilder path = new StringBuilder();
            path.append(WORK_CATEGORY_PATH);
            path.append(RANGE_PATH);

            // パラメータ
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                path.append(QUERY_PATH);
                path.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
            }

            List<WorkCategoryInfoEntity> entities = restClient.findAll(path.toString(), new GenericType<List<WorkCategoryInfoEntity>>() {
            });
            return entities;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ArrayList<>();
    }

    /**
     * 新しい作業区分の登録
     *
     * @param workCategoryInfo
     * @return レスポンス
     */
    public ResponseEntity regist(WorkCategoryInfoEntity workCategoryInfo) {
        logger.debug("regist:{}", workCategoryInfo);
        try {
            return (ResponseEntity) restClient.post(WORK_CATEGORY_PATH, workCategoryInfo, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ResponseEntity();
    }

    /**
     * 作業区分の更新
     *
     * @param workCategoryInfo
     * @return レスポンス
     */
    public ResponseEntity update(WorkCategoryInfoEntity workCategoryInfo) {
        logger.debug("update:{}", workCategoryInfo);
        try {
            return (ResponseEntity) restClient.put(WORK_CATEGORY_PATH, workCategoryInfo, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ResponseEntity();
    }

    /**
     * 作業区分の削除
     *
     * @param workCategoryInfo
     * @return レスポンス
     */
    public ResponseEntity delete(WorkCategoryInfoEntity workCategoryInfo) {
        logger.debug("delete:{}", workCategoryInfo);
        try {
            return (ResponseEntity) restClient.delete(WORK_CATEGORY_PATH, workCategoryInfo.getWorkCategoryId(), ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }
}
