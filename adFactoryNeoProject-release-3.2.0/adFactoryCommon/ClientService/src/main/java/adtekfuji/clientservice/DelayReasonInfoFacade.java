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
import jp.adtekfuji.adFactory.entity.master.DelayReasonInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 遅延理由取得用RESTクラス
 *
 * @author e-mori
 */
public class DelayReasonInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    private final static String DELAY_PATH = "/delay-reason";
    private final static String ID_TARGET_PATH = "/%s";

    public DelayReasonInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    public DelayReasonInfoFacade(String uriBase) {
        restClient = new RestClient(new RestClientProperty(uriBase));
    }

    /**
     * 遅延理由全取得
     *
     * @return 遅延理由リスト
     */
    public List<DelayReasonInfoEntity> findAll() {
        logger.debug("findAll:");
        try {
            return restClient.findAll(DELAY_PATH, new GenericType<List<DelayReasonInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 指定のIDに該当する遅延理由を取得
     *
     * @param id 遅延理由ID
     * @return IDに該当する遅延理由
     */
    public DelayReasonInfoEntity find(long id) {
        logger.debug("find:{}", id);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(DELAY_PATH);
            sb.append(String.format(ID_TARGET_PATH, id));

            return (DelayReasonInfoEntity) restClient.find(sb.toString(), DelayReasonInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new DelayReasonInfoEntity();
        }
    }

    /**
     * 指定の遅延理由を登録する
     *
     * @param entity 遅延理由
     * @return サーバーからの応答
     */
    public ResponseEntity add(DelayReasonInfoEntity entity) {
        logger.debug("regist:{}", entity);
        try {
            return (ResponseEntity) restClient.post(DELAY_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 指定の遅延理由を更新する
     *
     * @param entity 遅延理由
     * @return サーバーからの応答
     */
    public ResponseEntity update(DelayReasonInfoEntity entity) {
        logger.debug("update:{}", entity);
        try {
            return (ResponseEntity) restClient.put(DELAY_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 指定のIDに該当する遅延理由を削除する
     *
     * @param id 遅延理由ID
     * @return サーバーからの応答
     */
    public ResponseEntity remove(long id) {
        logger.debug("remove:{}", id);
        try {
            return (ResponseEntity) restClient.delete(DELAY_PATH, id, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
        }
    }

}
