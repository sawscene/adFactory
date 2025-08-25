package jp.adtekfuji.adinterfaceservice.websocket.form.output;

import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.form.FormInfoEntity;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.websocket.Session;
import java.util.LinkedList;
import java.util.Objects;

/**
 * 帳票出力用Worker
 */
public class FormOutputWorker  extends Thread {

    static private final Logger logger = LogManager.getLogger();

    private boolean execution = false;

    /**
     * 実行用IF
     */
    interface Command {
        void apply();
    }

    /**
     * エクセル用帳票出力
     */
    static class ExcelFormOutput implements Command
    {
        private FormInfoEntity formInfoEntity; //出力情報
        private Session session; // セッション

        /**
         * コンストラクタ
         * @param formInfoEntity 出力情報
         * @param session セッション
         */
        ExcelFormOutput(FormInfoEntity formInfoEntity, Session session)
        {
            this.formInfoEntity = formInfoEntity;
            this.session = session;
        }

        /**
         * 実行
         */
        @Override
        public void apply()
        {
            ExcelTemplateOutput excelTemplateOutput = new ExcelTemplateOutput();
            ResponseEntity ret = excelTemplateOutput.execute(this.formInfoEntity);
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(JsonUtils.objectToJson(ret));
                } catch(Exception ex) {
                    logger.fatal(ex,ex);
                }
            }
        }
    }

    private final LinkedList<Command> recvQueue = new LinkedList<>();
    static FormOutputWorker instance;

    /**
     * インスタンス取得
     * @return インスタンス
     */
    static synchronized public FormOutputWorker getInstance()
    {
        if(Objects.isNull(FormOutputWorker.instance)) {
            FormOutputWorker.instance = new FormOutputWorker();
            FormOutputWorker.instance.startService();
        }
        return FormOutputWorker.instance;
    }

    /**
     * タスク実行
     */
    @Override
    public void run() {
        while (execution) {
            try {
                synchronized (recvQueue) {
                    if (recvQueue.isEmpty()) {
                        try {
                            recvQueue.wait();
                        } catch (InterruptedException ex) {
                            logger.fatal(ex, ex);
                        }
                        if (recvQueue.isEmpty()) {
                            continue;
                        }
                    }
                    Command cmd = recvQueue.removeFirst();
                    cmd.apply();
                }
            } catch (RuntimeException ex) {
                logger.fatal(ex, ex);
            }
        }
    }

    /**
     * サービス開始
     */
    public void startService()
    {
        this.execution = true;
        super.start();
    }

    /**
     * サービス完了
     */
    public void stopService() {
        this.execution = false;
    }

    /**
     * 通知
     * @param formInfoEntity 出力情報
     * @param session セッション
     */
    public void notice(FormInfoEntity formInfoEntity, Session session) {
        ExcelFormOutput excelFormOutput = new ExcelFormOutput(formInfoEntity, session);
        synchronized (this.recvQueue) {
            this.recvQueue.add(excelFormOutput);
            this.recvQueue.notify();
        }
    }
}
