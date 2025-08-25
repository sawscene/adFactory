package jp.adtekfuji.adandontitleplugin;

import adtekfuji.fxscene.FxComponent;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.plugin.AdAndonComponentInterface;
import jp.adtekfuji.andon.clientservice.AndonLineMonitorFacade;
import jp.adtekfuji.andon.common.AndonComponent;
import jp.adtekfuji.andon.common.AndonLoginFacade;
import jp.adtekfuji.andon.entity.MonitorTitleInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@AndonComponent(title = "タイトルフレーム")
@FxComponent(id = "MonitorTitle", fxmlPath = "/fxml/title_compo.fxml")
public class MonitorTitleCompoFxController implements Initializable, AdAndonComponentInterface {

    private static final Logger logger = LogManager.getLogger();
    private final AndonLineMonitorFacade andonLineMonitorFacade = new AndonLineMonitorFacade();
    private MonitorTitleInfoEntity titleInfo = null;
    private final Object lock = new Object();
    private Long monitorId;

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label titleLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // モニタID
        this.monitorId = AndonLoginFacade.getMonitorId();

        titleLabel.setText("");
        anchorPane.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            draw();
        });
        readTask(monitorId);
    }

    @Override
    public void updateDisplay(Object msg) {
        if (msg instanceof ResetCommand) {
            readTask(this.monitorId);
        }
    }

    @Override
    public void exitComponent() {
        logger.info("exitComponent");
    }

    private void readTask(Long monitorId) {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    synchronized (lock) {
                        titleInfo = andonLineMonitorFacade.getTitleInfo(monitorId);
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

    private void draw() {
        Platform.runLater(() -> {
            synchronized (lock) {
                if (Objects.isNull(titleLabel)) {
                    return;
                }
                String text = titleInfo.getTitle();
                titleLabel.setText(text);
                switch (titleInfo.getHorizonAlignment()) {
                    case ALIGN_LEFT:
                        titleLabel.setAlignment(Pos.CENTER_LEFT);
                        break;
                    case ALIGN_CENTER:
                        titleLabel.setAlignment(Pos.CENTER);
                        break;
                    case ALIGN_RIGHT:
                        titleLabel.setAlignment(Pos.CENTER_RIGHT);
                        break;
                }

                Double size = Math.min(titleLabel.getWidth() / text.length(), titleLabel.getHeight() * 0.8);
                //titleLabel.setStyle(String.format("-fx-font-family:Meiryo UI; -fx-fx-font-weight:bold; -fx-font-size:%dpx; -fx-text-fill:white; -fx-font-smoothing-type:gray;", size.longValue()));
                titleLabel.setStyle(String.format("-fx-font-size:%dpx; -fx-text-fill:white;", size.longValue()));
             }
        });
    }

}
