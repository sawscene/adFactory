package jp.adtekfuji.adandonlinetakttimeplugin;

import adtekfuji.fxscene.FxComponent;
import adtekfuji.property.AdProperty;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.plugin.AdAndonComponentInterface;
import jp.adtekfuji.andon.clientservice.AndonLineMonitorFacade;
import jp.adtekfuji.andon.common.AndonComponent;
import jp.adtekfuji.andon.common.AndonLoginFacade;
import jp.adtekfuji.andon.common.Constants;
import jp.adtekfuji.andon.entity.MonitorLineTaktInfoEntity;
import jp.adtekfuji.andon.utility.MonitorTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@AndonComponent(title = "ラインタクトタイムフレーム")
@FxComponent(id = "LineTaktTime", fxmlPath = "/fxml/line_takttime_compo.fxml")
public class MonitorLinetTaktTimeCompoFxController implements Initializable, AdAndonComponentInterface {

    private static final Logger logger = LogManager.getLogger();
    private final AndonLineMonitorFacade andonLineMonitorFacade = new AndonLineMonitorFacade();
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private long monitorId = 0L;
    private MonitorLineTaktInfoEntity lineTakt = null;
    private double fontSize = Double.NaN;

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label takttimeLabel;
    @FXML
    private Label taktLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // フォントサイズ
        if (!AdProperty.getProperties().containsKey(Constants.FONT_SIZE_3LARGE)) {
            AdProperty.getProperties().setProperty(Constants.FONT_SIZE_3LARGE, Constants.DEF_FONT_SIZE_3LARGE);
        }
        this.fontSize = Double.parseDouble(AdProperty.getProperties().getProperty(Constants.FONT_SIZE_3LARGE));

        this.taktLabel.setText("");

        this.anchorPane.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            draw();
        });

        this.anchorPane.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            draw();
        });

        try {
            this.monitorId = AndonLoginFacade.getMonitorId();
            this.readTask();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    @Override
    public void updateDisplay(Object msg) {
        if (msg instanceof ResetCommand) {
            this.readTask();
        }
    }

    @Override
    public void exitComponent() {
        logger.info("exitComponent");
    }

    private void readTask() {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    lineTakt = andonLineMonitorFacade.getDailyTakttimeInfo(monitorId);
                    Platform.runLater(() -> draw());
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void draw() {
        long taktTime = 0L;

        if (Objects.nonNull(this.lineTakt)) {
            taktTime = this.lineTakt.getTaktTime();
        }

        double width = this.anchorPane.getWidth() / 2;
        this.takttimeLabel.setPrefWidth(width);
        this.taktLabel.setPrefWidth(width);

        Double size = MonitorTools.getFontSize(this.takttimeLabel.getText(), width, this.anchorPane.getHeight(), this.fontSize);
        this.takttimeLabel.setStyle(String.format("-fx-font-size:%dpx; -fx-text-fill: white;", size.longValue()));

        LocalTime localTime = LocalTime.of(0, 0, 0).plus(taktTime, ChronoUnit.SECONDS);
        this.taktLabel.setText(localTime.format(this.timeFormatter));
        size = MonitorTools.getFontSize(this.taktLabel.getText(), width, this.anchorPane.getHeight(), this.fontSize);
        this.taktLabel.setStyle(String.format("-fx-font-size:%dpx; -fx-text-fill: white;", size.longValue()));
    }
}
