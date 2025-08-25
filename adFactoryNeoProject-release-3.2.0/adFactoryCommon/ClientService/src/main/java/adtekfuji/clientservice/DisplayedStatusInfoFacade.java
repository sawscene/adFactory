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
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 表示用ステータス取得用RESTクラス
 *
 * @author e-mori
 */
public class DisplayedStatusInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    private final static String DISPLAYEDSTATUS_PATH = "/visual-style";
    private final static String ID_TARGET_PATH = "/%s";

    public DisplayedStatusInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    public DisplayedStatusInfoFacade(String uriBase) {
        restClient = new RestClient(new RestClientProperty(uriBase));
    }

    /**
     * 表示用ステータス全取得
     *
     * @return 表示用ステータスリスト
     */
    public List<DisplayedStatusInfoEntity> findAll() {
        logger.debug("findAll:");
        try {
            return restClient.findAll(DISPLAYEDSTATUS_PATH, new GenericType<List<DisplayedStatusInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 指定のIDに該当する表示用ステータスを取得
     *
     * @param id 表示用ステータスID
     * @return IDに該当する表示用ステータス
     */
    public DisplayedStatusInfoEntity find(long id) {
        logger.debug("find:{}", id);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(DISPLAYEDSTATUS_PATH);
            sb.append(String.format(ID_TARGET_PATH, id));

            return (DisplayedStatusInfoEntity) restClient.find(sb.toString(), DisplayedStatusInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new DisplayedStatusInfoEntity();
        }
    }

    /**
     * 指定の表示用ステータスを登録する
     *
     * @param entity 表示用ステータス
     * @return サーバーからの応答
     */
    public ResponseEntity add(DisplayedStatusInfoEntity entity) {
        logger.debug("regist:{}", entity);
        try {
            return (ResponseEntity) restClient.post(DISPLAYEDSTATUS_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 指定の表示用ステータスを更新する
     *
     * @param entity 表示用ステータス
     * @return サーバーからの応答
     */
    public ResponseEntity update(DisplayedStatusInfoEntity entity) {
        logger.debug("update:{}", entity);
        try {
            return (ResponseEntity) restClient.put(DISPLAYEDSTATUS_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 指定のIDに該当する表示用ステータスを削除する
     *
     * @param id 表示用ステータスID
     * @return サーバーからの応答
     */
    public ResponseEntity remove(long id) {
        logger.debug("remove:{}", id);
        try {
            return (ResponseEntity) restClient.delete(DISPLAYEDSTATUS_PATH, id, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
        }
    }

}
