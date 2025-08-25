package jp.adtekfuji.adandondailydelaynumplugin;

import adtekfuji.fxscene.FxComponent;
import adtekfuji.property.AdProperty;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Function;
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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import jp.adtekfuji.adFactory.adinterface.command.ActualNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.plugin.AdAndonComponentInterface;
import jp.adtekfuji.andon.clientservice.AndonLineMonitorFacade;
import jp.adtekfuji.andon.common.AndonComponent;
import jp.adtekfuji.andon.common.AndonLoginFacade;
import jp.adtekfuji.andon.common.Constants;
import jp.adtekfuji.andon.entity.MonitorReasonNumInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@AndonComponent(title = "遅延理由カウントフレーム")
@FxComponent(id = "DailyDelayNum", fxmlPath = "/fxml/daily_delaynum_compo.fxml")
public class MonitorDailyDelayNumCompoFxController implements Initializable, AdAndonComponentInterface {

    private static final Logger logger = LogManager.getLogger();
    private final AndonLineMonitorFacade andonLineMonitorFacade = new AndonLineMonitorFacade();
    private List<MonitorReasonNumInfoEntity> reasonNumInfos = null;
    private final Object lock = new Object();
    private Long monitorId;
    private double fontSize;

    @FXML
    private AnchorPane anchorPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // モニタID
        this.monitorId = AndonLoginFacade.getMonitorId();

        //フォントサイズ
        if (!AdProperty.getProperties().containsKey(Constants.FONT_SIZE_LARGE)) {
           AdProperty.getProperties().setProperty(Constants.FONT_SIZE_LARGE, Constants.DEF_FONT_SIZE_LARGE);
        }
        this.fontSize = Double.parseDouble(AdProperty.getProperties().getProperty(Constants.FONT_SIZE_LARGE));

        anchorPane.getChildren().clear();
        anchorPane.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            draw();
        });
        anchorPane.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            draw();
        });
        readTask(AndonLoginFacade.getMonitorId());
    }

    /**
     * 画面更新
     * 
     * @param msg 通知コマンド
     */
    @Override
    public void updateDisplay(Object msg) {
        if (msg instanceof ActualNoticeCommand) {
            ActualNoticeCommand command = (ActualNoticeCommand) msg;
            
            // モデル名
            //if (!StringUtils.like(command.getModelName(), this.setting.getModelName())) {
            //    return;
            //}
    
            // 工程カンバンステータス
            if (!KanbanStatusEnum.COMPLETION.equals(command.getWorkKanbanStatus())) {
                return;
            }
            
            readTask(command.getMonitorId());
            
        } else if (msg instanceof ResetCommand) {
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
                        reasonNumInfos = andonLineMonitorFacade.getDailyDelayReasonInfo(monitorId);
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
                anchorPane.getChildren().clear();
                if (Objects.isNull(reasonNumInfos)) {
                    return;
                }
                GridPane gridPane = new GridPane();
                gridPane.setAlignment(Pos.CENTER);
                gridPane.setHgap(3);
                gridPane.setVgap(3);
                gridPane.setPadding(new Insets(3));
                ColumnConstraints column1 = new ColumnConstraints();
                ColumnConstraints column2 = new ColumnConstraints();
                column1.setHgrow(Priority.SOMETIMES);
                column2.setHgrow(Priority.SOMETIMES);
                gridPane.getColumnConstraints().addAll(column1, column2);
                int maxNum = reasonNumInfos.size();
                double width = anchorPane.getWidth() / 2;
                double height = (anchorPane.getHeight() / maxNum) * 0.8;
                
                //フォントサイズ調整用
                final Font font = Font.font("Meiryo UI", this.fontSize);
                final double fontHeight = this.anchorPane.getHeight() / maxNum;
                Function<Label,  String> adjust = (label) -> {
                    //デフォルトのフォントサイズで幅が収まるか調べる
                    Text helper = new Text(label.getText());
                    helper.setFont(font);
                    double w = label.getPrefWidth();
                    double fs = (w >= helper.getBoundsInLocal().getWidth()) ? this.fontSize : this.fontSize * (w / helper.getBoundsInLocal().getWidth() * 0.9);
                    //補正後のフォントサイズで高さが収まるか調べる
                    helper.setFont(Font.font("Meiryo UI", fs));
                    double h = label.getPrefHeight();
                    fs = fontHeight >= helper.getBoundsInLocal().getHeight() ? fs : fs * (fontHeight / helper.getBoundsInLocal().getHeight()  * 0.99);
                    return String.format("-fx-font-size:%fpx; -fx-text-fill:%s;", fs, "white");
                };
                
                //進捗モニタに設定された遅延理由から構築
                int count = 0;
                for (MonitorReasonNumInfoEntity reason : reasonNumInfos) {
                    RowConstraints rowConst = new RowConstraints();
                    rowConst.setPercentHeight(fontHeight);
                    gridPane.getRowConstraints().add(rowConst);
                    
                    Label label1 = new Label(reason.getReason());
                    label1.setPrefWidth(this.anchorPane.getWidth() * 0.6);
                    label1.setAlignment(Pos.CENTER_LEFT);
                    label1.setStyle(adjust.apply(label1));

                    String text = String.valueOf(reason.getReasonNum());
                    Label label2 = new Label(text);
                    label2.setPrefWidth(this.anchorPane.getWidth() * 0.4);
                    label2.setStyle(adjust.apply(label2));
                    label2.setAlignment(Pos.CENTER_RIGHT);

                    gridPane.add(label1, 0, count);
                    gridPane.add(label2, 1, count);
                    count++;
                }
                anchorPane.getChildren().add(gridPane);
                AnchorPane.setTopAnchor(gridPane, 0.0);
                AnchorPane.setBottomAnchor(gridPane, 0.0);
                AnchorPane.setLeftAnchor(gridPane, 0.0);
                AnchorPane.setRightAnchor(gridPane, 0.0);
            }
        });
    }

    private Label createLabel(String text, Pos pos, long fontsize) {
        Label label = new Label(text);
        label.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        label.setAlignment(pos);
        label.setTextOverrun(OverrunStyle.CLIP);
        label.setStyle(String.format("-fx-font-size:%dpx; -fx-text-fill:white; -fx-background-color:black;", fontsize));
        return label;
    }

    private int getLength(String text) {
        try {
            return text.getBytes("Shift_JIS").length;
        } catch (UnsupportedEncodingException ex) {
        }
        return text.length();
    }

}
