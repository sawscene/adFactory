package jp.adtekfuji.admonitorpluginformat;

import adtekfuji.fxscene.FxComponent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import jp.adtekfuji.adFactory.adinterface.command.ActualNoticeCommand;
import jp.adtekfuji.adFactory.plugin.AdAndonComponentInterface;
import jp.adtekfuji.andon.common.AndonComponent;
import jp.adtekfuji.andon.common.AndonLoginFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * モニタープラグインテンプレート画面
 * 
 * @author e.mori
 * @version 1.6.2
 * @since 2017.02.08.Wen
 */
@AndonComponent(title = "フレームテンプレート")
@FxComponent(id = "MonitorTemplate", fxmlPath = "/fxml/template_compo.fxml")
public class MonitorTemplateCompoFxController implements Initializable, AdAndonComponentInterface {

    private static final Logger logger = LogManager.getLogger();
    private final Object lock = new Object();

    @FXML
    private AnchorPane anchorPane;

    /**
     * 初期処理
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 画面サイズが変更された場合再描画        
        anchorPane.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            draw();
        });
        readTask(AndonLoginFacade.getMonitorId());
    }

    /**
     * 実績受信時の画面更新処理
     * 
     * @param msg 受信コマンド 
     */
    @Override
    public void updateDisplay(Object msg) {
        if (msg instanceof ActualNoticeCommand) {
            ActualNoticeCommand command = (ActualNoticeCommand) msg;
            readTask(command.getMonitorId());
        }
    }

    @Override
    public void exitComponent() {
        logger.info("exitComponent");
    }

    /**
     * 進捗情報読み込み(サーバーから)
     * 
     * @param monitorId 自身の設備ID
     */
    private void readTask(Long monitorId) {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    synchronized (lock) {
                        
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
                draw();
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * 描画処理
     * 
     */
    private void draw() {
        Platform.runLater(() -> {
            synchronized (lock) {
                // 描画処理
             }
        });
    }

}
