package jp.adtekfuji.adandonlinestatusplugin;

import adtekfuji.fxscene.FxComponent;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.AnchorPane;
import jp.adtekfuji.adFactory.adinterface.command.ActualNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.CallingNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.LineTimerNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.plugin.AdAndonComponentInterface;
import jp.adtekfuji.andon.clientservice.AndonLineMonitorFacade;
import jp.adtekfuji.andon.common.AndonComponent;
import jp.adtekfuji.andon.common.AndonLoginFacade;
import jp.adtekfuji.andon.entity.MonitorLineStatusInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@AndonComponent(title = "ライン全体ステータスフレーム")
@FxComponent(id = "DailyLineStatus", fxmlPath = "/fxml/line_status_compo.fxml")
public class MonitorLinetStatusCompoFxController implements Initializable, AdAndonComponentInterface {

    private static final Logger logger = LogManager.getLogger();
    private final AndonLineMonitorFacade andonLineMonitorFacade = new AndonLineMonitorFacade();
    private MonitorLineStatusInfoEntity lineStatus = null;
    private final MelodyPlayer melodyPlayer = new MelodyPlayer();
    private Long monitorId = 0L;
    private final Object lock = new Object();

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label statusLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        statusLabel.setText("");
        anchorPane.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            draw();
        });
        try {
            monitorId = AndonLoginFacade.getMonitorId();
            readTask(monitorId);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    @Override
    public void updateDisplay(Object msg) {
        if (msg instanceof ActualNoticeCommand || msg instanceof CallingNoticeCommand || msg instanceof LineTimerNoticeCommand || msg instanceof ResetCommand) {
            try {
                readTask(monitorId);
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        }
    }

    @Override
    public void exitComponent() {
        logger.info("exitComponent");
        try {
            melodyPlayer.stop();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    private void readTask(Long monitorId) {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    synchronized (lock) {
                        lineStatus = andonLineMonitorFacade.getDailyLineStatusInfo(monitorId);
                    }
                    melodyPlayer.play(lineStatus.getMelodyFilePath(), lineStatus.getMelodyReplay());
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
                draw();
                return null;
            }
        };
        new Thread(task).start();
    }

    private void draw() {
        Platform.runLater(() -> {
            synchronized (lock) {
                if (Objects.isNull(lineStatus)) {
                    return;
                }
                String text = lineStatus.getStatus();
                Double size = Math.min(statusLabel.getWidth() / text.length(), statusLabel.getHeight() * 0.8);
                String style = String.format("-fx-font-size:%dpx; -fx-text-fill:%s; -fx-background-color:%s;",
                        size.longValue(), lineStatus.getFontColor(), lineStatus.getBackColor());
                statusLabel.setText(text);
                statusLabel.setStyle(style);
            }
        });
    }

    @FXML
    private void onSpeakerMouseCliked(MouseEvent event) {
        melodyPlayer.switchMute();
    }

    @FXML
    private void onSpeakerTouchPressed(TouchEvent event) {
        melodyPlayer.switchMute();
    }

}
