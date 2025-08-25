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
import jp.adtekfuji.adFactory.entity.master.InterruptReasonInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 中断理由取得用RESTクラス
 *
 * @author e-mori
 */
public class InterruptReasonInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    private final static String INTERRUPT_PATH = "/interrupt-reason";
    private final static String ID_TARGET_PATH = "/%s";

    public InterruptReasonInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    /**
     * 中断理由全取得
     *
     * @return 中断理由リスト
     */
    public List<InterruptReasonInfoEntity> findAll() {
        logger.debug("findAll:");
        try {
            return restClient.findAll(INTERRUPT_PATH, new GenericType<List<InterruptReasonInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 指定のIDに該当する中断理由を取得
     *
     * @param id 中断理由ID
     * @return IDに該当する中断理由
     */
    public InterruptReasonInfoEntity find(long id) {
        logger.debug("find:{}", id);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(INTERRUPT_PATH);
            sb.append(String.format(ID_TARGET_PATH, id));

            return (InterruptReasonInfoEntity) restClient.find(sb.toString(), InterruptReasonInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new InterruptReasonInfoEntity();
        }
    }

    /**
     * 指定の中断理由を登録する
     *
     * @param entity 中断理由
     * @return サーバーからの応答
     */
    public ResponseEntity add(InterruptReasonInfoEntity entity) {
        logger.debug("regist:{}", entity);
        try {
            return (ResponseEntity) restClient.post(INTERRUPT_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 指定の中断理由を更新する
     *
     * @param entity 中断理由
     * @return サーバーからの応答
     */
    public ResponseEntity update(InterruptReasonInfoEntity entity) {
        logger.debug("update:{}", entity);
        try {
            return (ResponseEntity) restClient.put(INTERRUPT_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 指定のIDに該当する中断理由を削除する
     *
     * @param id 中断理由ID
     * @return サーバーからの応答
     */
    public ResponseEntity remove(long id) {
        logger.debug("remove:{}", id);
        try {
            return (ResponseEntity) restClient.delete(INTERRUPT_PATH, id, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
        }
    }
}
