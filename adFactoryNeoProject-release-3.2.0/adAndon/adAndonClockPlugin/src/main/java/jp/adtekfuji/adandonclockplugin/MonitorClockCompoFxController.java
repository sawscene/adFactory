package jp.adtekfuji.adandonclockplugin;

import adtekfuji.fxscene.FxComponent;
import adtekfuji.property.AdProperty;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.plugin.AdAndonComponentInterface;
import jp.adtekfuji.andon.clientservice.AndonLineMonitorFacade;
import jp.adtekfuji.andon.common.AndonComponent;
import jp.adtekfuji.andon.common.AndonLoginFacade;
import jp.adtekfuji.andon.entity.MonitorClockInfoEntity;
import jp.adtekfuji.andon.utility.MonitorTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@AndonComponent(title = "日付時刻フレーム")
@FxComponent(id = "MonitorClock", fxmlPath = "/fxml/clock_compo.fxml")
public class MonitorClockCompoFxController implements Initializable, AdAndonComponentInterface {

    public static final String FONT_SIZE_CLOCK = "fontSizeClock";
    public static final String DEF_FONT_SIZE_CLOCK = "999.0";

    private static final Logger logger = LogManager.getLogger();
    private final AndonLineMonitorFacade andonLineMonitorFacade = new AndonLineMonitorFacade();
    private MonitorClockInfoEntity clockInfo = null;
    private SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
    private Timeline timer = null;
    private final Object lock = new Object();
    private Long monitorId;
    private double fontSizeClock = Double.NaN;

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label clockLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // フォントサイズ
        if (!AdProperty.getProperties().containsKey(FONT_SIZE_CLOCK)) {
            AdProperty.getProperties().setProperty(FONT_SIZE_CLOCK, DEF_FONT_SIZE_CLOCK);
        }
        this.fontSizeClock = Double.parseDouble(AdProperty.getProperties().getProperty(FONT_SIZE_CLOCK));
            
        // モニタID
        this.monitorId = AndonLoginFacade.getMonitorId();

        clockLabel.setText("");
        anchorPane.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            draw();
        });
        timer = new Timeline(new KeyFrame(Duration.millis(1000), (ActionEvent event) -> {
            draw();
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        readTask(this.monitorId);
    }

    /**
     * 画面更新
     * 
     * @param msg 通知コマンド
     */
    @Override
    public void updateDisplay(Object msg) {
        //if (msg instanceof ActualNoticeCommand) {
        //    ActualNoticeCommand command = (ActualNoticeCommand) msg;
        //    readTask(command.getMonitorId());
        //} else
        if (msg instanceof ResetCommand) {
            readTask(this.monitorId);
        }
    }

    @Override
    public void exitComponent() {
        logger.info("exitComponent");
        try {
            if (Objects.nonNull(timer)) {
                timer.stop();
            }
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
                        clockInfo = andonLineMonitorFacade.getClockInfo(monitorId);
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
            timer.pause();
            synchronized (lock) {
                if (Objects.nonNull(clockInfo)) {
                    sd = new SimpleDateFormat(clockInfo.getClockFormat());
                    switch (clockInfo.getHorizonAlignment()) {
                        case ALIGN_LEFT:
                            clockLabel.setAlignment(Pos.CENTER_LEFT);
                            break;
                        case ALIGN_CENTER:
                            clockLabel.setAlignment(Pos.CENTER);
                            break;
                        case ALIGN_RIGHT:
                            clockLabel.setAlignment(Pos.CENTER_RIGHT);
                            break;
                    }
                }
            }

            String text = sd.format(new Date());
            clockLabel.setText(text);
            //Double size = Math.min(anchorPane.getWidth() / text.length(), anchorPane.getHeight() * 0.8);
            Double size = MonitorTools.getFontSize(text, this.anchorPane.getWidth(), this.anchorPane.getHeight(), this.fontSizeClock);
            clockLabel.setStyle(String.format("-fx-font-size:%dpx; -fx-text-fill:white;", size.longValue()));
            timer.play();
        });
    }

}
