/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.clientservice;

import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.approval.ApprovalInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 申請用RESTクラス
 *
 * @author akihiro.yoshida
 */
public class ApprovalFlowInfoFacade {

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
    private final static String APPROVAL_PATH = "/approval";

    /**
     * リクエストURIのサブパス(申請)
     */
    private final static String APPLY_PATH = "/apply";

    /**
     * リクエストURIのサブパス(申請取消)
     */
    private final static String CANCEL_PATH = "/cancel";

    /**
     * リクエストURIのパラメータ指定記号
     */
    private final static String QUERY_PATH = "?";

    /**
     * コンストラクタ
     */
    public ApprovalFlowInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    /**
     * 工程・工程順の変更を申請する。
     *
     * @param entity 申請情報
     * @param authId 認証ID
     * @return サーバーからの応答
     */
    public ResponseEntity apply(ApprovalInfoEntity entity, Long authId) {
        logger.info("apply: entity={}, authId={}", entity, authId);
        try {
            StringBuilder path = new StringBuilder();
            path.append(APPROVAL_PATH);
            path.append(APPLY_PATH);

            // パラメータ
            if (Objects.nonNull(authId)) {
                path.append(QUERY_PATH);
                path.append(String.format("authId=%d", authId));
            }

            return (ResponseEntity) restClient.post(path.toString(), entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 工程・工程順の変更申請を取り消す。
     *
     * @param entity 申請情報
     * @param authId 認証ID
     * @return サーバーからの応答
     */
    public ResponseEntity cancelApply(ApprovalInfoEntity entity, Long authId) {
        logger.info("cancelApply: entity={}, authId={}", entity, authId);
        try {
            StringBuilder path = new StringBuilder();
            path.append(APPROVAL_PATH);
            path.append(CANCEL_PATH);

            // パラメータ
            if (Objects.nonNull(authId)) {
                path.append(QUERY_PATH);
                path.append(String.format("authId=%d", authId));
            }

            return (ResponseEntity) restClient.put(path.toString(), entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }
}
