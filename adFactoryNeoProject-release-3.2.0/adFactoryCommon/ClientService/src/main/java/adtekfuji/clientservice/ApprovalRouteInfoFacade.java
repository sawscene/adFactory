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
import jp.adtekfuji.adFactory.entity.approval.ApprovalRouteInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 承認ルートマスタ取得用RESTクラス
 *
 * @author akihiro.yoshida
 */
public class ApprovalRouteInfoFacade {

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
    private final static String APPROVAL_PATH = "/approval-route";

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
     * リクエストURIのサブパス(コピー時)
     */
    private final static String COPY_PATH = "/copy";

    /**
     * リクエストURIのサブパス(パスの区切り文字)
     */
    private final static String SEPARATOR_PATH = "/";

    /**
     * コンストラクタ
     */
    public ApprovalRouteInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    /**
     * 指定のIDに該当する承認ルートを取得する。
     *
     * @param id 承認ルートID
     * @return IDに該当する承認ルート
     */
    public ApprovalRouteInfoEntity find(long id) {
        logger.info("find:{}", id);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(APPROVAL_PATH);
            sb.append(String.format(ID_TARGET_PATH, id));

            return (ApprovalRouteInfoEntity) restClient.find(sb.toString(), ApprovalRouteInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ApprovalRouteInfoEntity();
        }
    }

    /**
     * 指定の範囲に該当する承認ルート一覧を取得する。
     *
     * <pre>
     * fromとtoを省略した場合、全件取得する。
     * </pre>
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @return 指定範囲の承認ルート一覧
     */
    public List<ApprovalRouteInfoEntity> findRange(Long from, Long to) {
        logger.info("findRange: from={}, to={}", from, to);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(APPROVAL_PATH);
            sb.append(RANGE_PATH);

            // パラメータ
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(QUERY_PATH);
                sb.append(String.format(FROM_TO_PATH, from, to));
            }

            return restClient.findAll(sb.toString(), new GenericType<List<ApprovalRouteInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList();
        }
    }

    /**
     * 承認ルートマスタの件数を取得する。
     *
     * @return 承認ルートマスタの件数
     */
    public Long count() {
        logger.info("count");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(APPROVAL_PATH);
            sb.append(COUNT_PATH);

            return Long.parseLong((String) restClient.find(sb.toString(), MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return 0L;
        }
    }

    /**
     * 指定の承認ルートを追加する。
     *
     * @param entity 承認ルートマスタエンティティ
     * @return サーバーからの応答
     */
    public ResponseEntity add(ApprovalRouteInfoEntity entity) {
        logger.info("add:{}", entity);
        try {
            return (ResponseEntity) restClient.post(APPROVAL_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 指定の承認ルートを更新する。
     *
     * @param entity 承認ルートマスタエンティティ
     * @return サーバーからの応答
     */
    public ResponseEntity update(ApprovalRouteInfoEntity entity) {
        logger.info("update:{}", entity);
        try {
            return (ResponseEntity) restClient.put(APPROVAL_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 指定のIDに該当する承認ルートを削除する。
     *
     * @param id 承認ルートID
     * @return サーバーからの応答
     */
    public ResponseEntity remove(long id) {
        logger.info("remove:{}", id);
        try {
            return (ResponseEntity) restClient.delete(APPROVAL_PATH, id, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
        }
    }

    /**
     * 承認ルートのコピー
     *
     * @param entity コピー元のエンティティ
     * @return レスポンス
     */
    public ResponseEntity copy(ApprovalRouteInfoEntity entity) {
        logger.debug("copyroute:{}", entity);
        try {
            StringBuilder path = new StringBuilder();
            path.append(APPROVAL_PATH);
            path.append(COPY_PATH);
            path.append(SEPARATOR_PATH);
            path.append(entity.getRouteId());

            return (ResponseEntity) restClient.post(path.toString(), entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity(ex);
        }
    }
}
