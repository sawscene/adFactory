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
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 休憩設定取得用RESTクラス
 *
 * @author e-mori
 */
public class BreaktimeInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    private final static String BREAKTIME_PATH = "/break-time";
    private final static String ID_TARGET_PATH = "/%s";

    public BreaktimeInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    public BreaktimeInfoFacade(String uriBase) {
        restClient = new RestClient(new RestClientProperty(uriBase));
    }

    /**
     * 休憩設定全取得
     *
     * @return 休憩設定リスト
     */
    public List<BreakTimeInfoEntity> findAll() {
        logger.debug("findAll:");
        try {
            return restClient.findAll(BREAKTIME_PATH, new GenericType<List<BreakTimeInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 指定のIDに該当する休憩設定を取得
     *
     * @param id 休憩設定ID
     * @return IDに該当する休憩設定
     */
    public BreakTimeInfoEntity find(long id) {
        logger.debug("find:{}", id);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(BREAKTIME_PATH);
            sb.append(String.format(ID_TARGET_PATH, id));

            return (BreakTimeInfoEntity) restClient.find(sb.toString(), BreakTimeInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new BreakTimeInfoEntity();
        }
    }

    /**
     * 指定の休憩設定を登録する
     *
     * @param entity 休憩設定
     * @return サーバーからの応答
     */
    public ResponseEntity add(BreakTimeInfoEntity entity) {
        logger.debug("regist:{}", entity);
        try {
            return (ResponseEntity) restClient.post(BREAKTIME_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 指定の休憩設定を更新する
     *
     * @param entity 休憩設定
     * @return サーバーからの応答
     */
    public ResponseEntity update(BreakTimeInfoEntity entity) {
        logger.debug("update:{}", entity);
        try {
            return (ResponseEntity) restClient.put(BREAKTIME_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 指定のIDに該当する休憩設定を削除する
     *
     * @param id 休憩設定ID
     * @return サーバーからの応答
     */
    public ResponseEntity remove(long id) {
        logger.debug("remove:{}", id);
        try {
            return (ResponseEntity) restClient.delete(BREAKTIME_PATH, id, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
        }
    }

}
