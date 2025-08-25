package jp.adtekfuji.adandonequipemtplannumplugin;

import adtekfuji.fxscene.FxComponent;
import java.net.URL;
import java.util.Date;
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
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import jp.adtekfuji.adFactory.adinterface.command.ActualNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.plugin.AdAndonComponentInterface;
import jp.adtekfuji.adFactory.utility.BreaktimeUtil;
import jp.adtekfuji.andon.clientservice.AndonLineMonitorFacade;
import jp.adtekfuji.andon.common.AndonComponent;
import jp.adtekfuji.andon.common.AndonLoginFacade;
import jp.adtekfuji.andon.entity.MonitorEquipmentPlanNumInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@AndonComponent(title = "工程計画実績数フレーム")
@FxComponent(id = "DailyEquipmentPlanNum", fxmlPath = "/fxml/equip_plunnum_compo.fxml")
public class MonitorEquipmentPlanNumCompoFxController implements Initializable, AdAndonComponentInterface {

    private static final Logger logger = LogManager.getLogger();
    private final AndonLineMonitorFacade andonLineMonitorFacade = new AndonLineMonitorFacade();
    private final static int COLUMN_MAX_SIZE = 12;
    private List<MonitorEquipmentPlanNumInfoEntity> equipPlanNums = null;
    private final Object lock = new Object();
    private Long monitorId;

    @FXML
    private AnchorPane anchorPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // モニタID
        this.monitorId = AndonLoginFacade.getMonitorId();

        anchorPane.getChildren().clear();
        anchorPane.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            draw();
        });
        anchorPane.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            draw();
        });
        readTask(this.monitorId);
    }

    @Override
    public void updateDisplay(Object msg) {
        if (msg instanceof ActualNoticeCommand) {
            ActualNoticeCommand command = (ActualNoticeCommand) msg;
            readTask(command.getMonitorId());
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
                        equipPlanNums = andonLineMonitorFacade.getDailyEquipmentPlanInfo(monitorId);
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
                if (Objects.isNull(equipPlanNums)) {
                    return;
                }
                anchorPane.getChildren().clear();
                GridPane gridPane = createGrid();
                double width = anchorPane.getWidth() / (equipPlanNums.size() >= COLUMN_MAX_SIZE ? COLUMN_MAX_SIZE : (equipPlanNums.size() % COLUMN_MAX_SIZE));
                double height = anchorPane.getHeight();
                for (MonitorEquipmentPlanNumInfoEntity equipPlanNum : equipPlanNums) {
                    int order = equipPlanNum.getOrder() - 1;
                    if (order >= COLUMN_MAX_SIZE) {
                        continue;
                    }
                    if (Objects.nonNull(equipPlanNum.getSuspendTime())) {
                        Long diff = BreaktimeUtil.getDiffTime(null, equipPlanNum.getSuspendTime(), new Date());
                        Label label = createLabel(String.valueOf(diff / 60 / 1000), width, height, "red");
                        gridPane.add(label, order % COLUMN_MAX_SIZE, order / COLUMN_MAX_SIZE);
                    } else {
                        GridPane innarGrid = createGrid();
                        Label label1 = createLabel(String.valueOf(equipPlanNum.getActualNum()), width, height / 2, "black");
                        Separator separator = createSeparator(width);
                        Label label2 = createLabel(String.valueOf(equipPlanNum.getPlanNum()), width, height / 2, "black");
                        innarGrid.add(label1, 0, 0);
                        innarGrid.add(separator, 0, 1);
                        innarGrid.add(label2, 0, 2);
                        gridPane.add(innarGrid, order % COLUMN_MAX_SIZE, order / COLUMN_MAX_SIZE);
                    }
                }
                anchorPane.getChildren().add(gridPane);
                AnchorPane.setTopAnchor(gridPane, 0.0);
                AnchorPane.setBottomAnchor(gridPane, 0.0);
                AnchorPane.setLeftAnchor(gridPane, 0.0);
                AnchorPane.setRightAnchor(gridPane, 0.0);
            }
        });
    }

    private GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(3);
        grid.setVgap(3);
        grid.setPadding(new Insets(3));
        grid.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        grid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return grid;
    }

    private Label createLabel(String text, double width, double height, String color) {
        Label label = new Label(text);
        label.setPrefSize(width, height);
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);
        label.setTextOverrun(OverrunStyle.CLIP);
        Double size = Math.min(width / text.length(), height * 0.8);
        label.setStyle(String.format("-fx-font-size:%dpx; -fx-text-fill:white; -fx-background-color:%s;", size.longValue(), color));
        return label;
    }

    private Separator createSeparator(double width) {
        Separator separator = new Separator();
        separator.setPrefSize(width, 2);
        separator.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return separator;
    }

}
