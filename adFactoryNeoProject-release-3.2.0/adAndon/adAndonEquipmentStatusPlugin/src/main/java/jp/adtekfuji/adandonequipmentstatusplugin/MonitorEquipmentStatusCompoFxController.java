package jp.adtekfuji.adandonequipmentstatusplugin;

import adtekfuji.fxscene.FxComponent;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import jp.adtekfuji.adFactory.adinterface.command.ActualNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.CallingNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.plugin.AdAndonComponentInterface;
import jp.adtekfuji.andon.clientservice.AndonLineMonitorFacade;
import jp.adtekfuji.andon.common.AndonComponent;
import jp.adtekfuji.andon.common.AndonLoginFacade;
import jp.adtekfuji.andon.entity.MonitorEquipmentStatusInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@AndonComponent(title = "工程設備ステータスフレーム")
@FxComponent(id = "DailyEquipmentStatus", fxmlPath = "/fxml/equip_status_compo.fxml")
public class MonitorEquipmentStatusCompoFxController implements Initializable, AdAndonComponentInterface {

    private static final Logger logger = LogManager.getLogger();
    private final AndonLineMonitorFacade andonLineMonitorFacade = new AndonLineMonitorFacade();
    private final static int COLUMN_MAX_SIZE = 12;
    private List<MonitorEquipmentStatusInfoEntity> equipStatuses = null;
    private Long monitorId = 0L;
    private final Object lock = new Object();

    @FXML
    private AnchorPane anchorPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        anchorPane.getChildren().clear();
        anchorPane.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            draw();
        });
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
        if (msg instanceof ActualNoticeCommand || msg instanceof CallingNoticeCommand) {
            readTask(monitorId);
        } else if (msg instanceof ResetCommand) {
            readTask(this.monitorId);
        }
    }

    @Override
    public void exitComponent() {
    }

    private void readTask(Long monitorId) {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    synchronized (lock) {
                        equipStatuses = andonLineMonitorFacade.getDailyEquipmentStatusInfo(monitorId);
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
                if (Objects.isNull(equipStatuses)) {
                    return;
                }
                anchorPane.getChildren().clear();
                GridPane gridPane = new GridPane();
                gridPane.setAlignment(Pos.CENTER);
                gridPane.setHgap(3);
                gridPane.setVgap(3);
                gridPane.setPadding(new Insets(3));
                gridPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
                gridPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                double width = anchorPane.getWidth() / (equipStatuses.size() >= COLUMN_MAX_SIZE ? COLUMN_MAX_SIZE : (equipStatuses.size() % COLUMN_MAX_SIZE));
                double height = anchorPane.getHeight() / ((equipStatuses.size() / COLUMN_MAX_SIZE) + 1);
                //double fontHeight = anchorPane.getHeight() * ((equipStatuses.size() >= COLUMN_MAX_SIZE) ? 0.3 : 0.6);
                for (MonitorEquipmentStatusInfoEntity equipStatus : equipStatuses) {
                    String text = equipStatus.getName();
                    Label label = new Label(text);
                    label.setPrefSize(width, height);
                    label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                    label.setAlignment(Pos.CENTER);
                    label.setTextOverrun(OverrunStyle.CLIP);
                    Double size = Math.min(width / getLength(text), height * 0.8);
                    label.setStyle(String.format("-fx-font-size:%dpx; -fx-text-fill:%s; -fx-background-color:%s;",
                            size.longValue(), equipStatus.getFontColor(), equipStatus.getBackColor()));
                    int order = equipStatus.getOrder() - 1;
                    gridPane.add(label, order % COLUMN_MAX_SIZE, order / COLUMN_MAX_SIZE);
                }
                anchorPane.getChildren().add(gridPane);
                AnchorPane.setTopAnchor(gridPane, 0.0);
                AnchorPane.setBottomAnchor(gridPane, 0.0);
                AnchorPane.setLeftAnchor(gridPane, 0.0);
                AnchorPane.setRightAnchor(gridPane, 0.0);
            }
        });
    }

    private int getLength(String text) {
        try {
            return text.getBytes("Shift_JIS").length;
        } catch (UnsupportedEncodingException ex) {
        }
        return text.length();
    }

}
