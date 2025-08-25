package jp.adtekfuji.adinterfaceservice.websocket.form.output;

import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.form.FormInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.adinterfaceservice.socketcomm.SocketLinkageCtrl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.Objects;

/**
 * 帳票出力用エンドポイント
 */
@ServerEndpoint("/form/output")
public class FormOutputEndpoint {

    static private final Logger logger = LogManager.getLogger();

    private static SocketLinkageCtrl ctrl = null;

    /**
     * 接続開始
     * @param session セッション
     */
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("onOpen " + session);
        ctrl = SocketLinkageCtrl.getInstance();
        ctrl.addClientWebSocket(session);
    }

    /**
     * メッセージ受信
     * @param message メッセージ
     * @param session セッション
     */
    @OnMessage
    public void onMessage(String message, Session session) {

        try {

            FormInfoEntity formInfoEntity = JsonUtils.jsonToObject(message, FormInfoEntity.class);
            if (Objects.isNull(formInfoEntity)) {
                // 受信情報がおかしい
                session.getBasicRemote().sendText(JsonUtils.objectToJson(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)));
                return;
            }

//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(new Date());
//            calendar.set(Calendar.DATE, -1);
//
//            FormInfoEntity formInfoEntity = new FormInfoEntity(FormInfoEntity.FormCategoryEnum.WORKFLOW, 662L, "aaa", "C:/Users/yu.nara/Desktop/帳票/テスト.xlsx");
//            formInfoEntity.setFromDate(calendar.getTime());
//            formInfoEntity.setToDate(new Date());

            // 帳票出力開始
            FormOutputWorker.getInstance().notice(formInfoEntity, session);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

    }

    /**
     * バイナリー情報受信
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessageBin(byte[] message, Session session) {
        // バイナリデータを受信した場合
        //  ※一度に受信できるサイズが getDefaultMaxBinaryMessageBufferSize() で65536 バイトに定義されている
        //String str = new String(message);
    }

    /**
     * エラー発生
     * @param t
     */
    @OnError
    public void onError(Throwable t) {
        logger.fatal(t, t);
    }

    /**
     * 通信切断
     * @param session セッション
     */
    @OnClose
    public void onClose(Session session) {
//        System.out.println("onClose " + session);
        ctrl.deleteSocketClient(session);
    }
}
