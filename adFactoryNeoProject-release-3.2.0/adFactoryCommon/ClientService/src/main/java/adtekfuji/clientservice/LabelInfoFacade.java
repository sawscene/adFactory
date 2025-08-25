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
import jp.adtekfuji.adFactory.entity.master.LabelInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ラベルマスタ取得用RESTクラス
 *
 * @author kentarou.suzuki
 */
public class LabelInfoFacade {

    /**
     * ログ出力クラス
     */
    private final Logger logger = LogManager.getLogger();
    /**
     * RESTクライアント
     */
    private final RestClient restClient;
    /**
     * リクエストURIのベースパス
     */
    private final static String LABEL_PATH = "/label";
    /**
     * リクエストURIのサブパス(名称指定時)
     */
    private final static String NAME_PATH = "/name";
    /**
     * リクエストURIのサブパス(範囲指定時)
     */
    private final static String RANGE_PATH = "/range";
    /**
     * リクエストURIのサブパス(件数取得時)
     */
    private final static String COUNT_PATH = "/count";
    /**
     * リクエストURIのサブパス(ID指定時)
     */
    private final static String ID_TARGET_PATH = "/%s";
    /**
     * リクエストURIのサブパス(クエリ指定時)
     */
    private final static String QUERY_PATH = "?";
    /**
     * リクエストURIのサブパス(範囲クエリ指定時)
     */
    private final static String FROM_TO_PATH = "from=%d&to=%d";

    /**
     * コンストラクタ
     */
    public LabelInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    /**
     * 指定のIDに該当するラベルを取得する。
     *
     * @param id ラベルID
     * @return IDに該当するラベル
     */
    public LabelInfoEntity find(long id) {
        logger.info("find:{}", id);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(LABEL_PATH);
            sb.append(String.format(ID_TARGET_PATH, id));

            return (LabelInfoEntity) restClient.find(sb.toString(), LabelInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new LabelInfoEntity();
        }
    }

    /**
     * 指定の名称に該当するラベル取得する。
     *
     * @param labelName ラベル名
     * @return 名称に該当するラベル
     */
    public LabelInfoEntity findName(String labelName) {
        logger.info("findByName:{}", labelName);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(LABEL_PATH);
            sb.append(NAME_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format("labelName=%s", labelName));

            return (LabelInfoEntity) restClient.find(sb.toString(), LabelInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new LabelInfoEntity();
        }
    }
    
    /**
     * 指定の範囲に該当するラベル一覧を取得する。
     * 
     * <pre>
     * fromとtoを省略した場合、全件取得する。
     * </pre>
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @return 指定範囲のラベル一覧
     */
    public List<LabelInfoEntity> findRange(Long from, Long to) {
        logger.info("findRange: from={}, to={}", from, to);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(LABEL_PATH);
            sb.append(RANGE_PATH);

            // パラメータ
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(QUERY_PATH);
                sb.append(String.format(FROM_TO_PATH, from, to));
            }

            return restClient.findAll(sb.toString(), new GenericType<List<LabelInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList();
        }
    }

    /**
     * ラベルマスタの件数を取得する。
     *
     * @return ラベルマスタの件数
     */
    public Long count() {
        logger.info("count");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(LABEL_PATH);
            sb.append(COUNT_PATH);

            return Long.parseLong((String) restClient.find(sb.toString(), MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return 0L;
        }
    }

    /**
     * 指定のラベルを追加する。
     * 
     * @param entity ラベルマスタエンティティ
     * @return サーバーからの応答
     */
    public ResponseEntity add(LabelInfoEntity entity) {
        logger.info("add:{}", entity);
        try {
            return (ResponseEntity) restClient.post(LABEL_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 指定のラベルを更新する。
     * 
     * @param entity ラベルマスタエンティティ
     * @return サーバーからの応答
     */
    public ResponseEntity update(LabelInfoEntity entity) {
        logger.info("update:{}", entity);
        try {
            return (ResponseEntity) restClient.put(LABEL_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 指定のIDに該当するラベルを削除する。
     *
     * @param id ラベルID
     * @return サーバーからの応答
     */
    public ResponseEntity remove(long id) {
        logger.info("remove:{}", id);
        try {
            return (ResponseEntity) restClient.delete(LABEL_PATH, id, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
        }
    }
}
