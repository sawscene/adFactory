package jp.adtekfuji.andonapp;

import adtekfuji.fxscene.FxScene;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.utility.ErrorUtils;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.andonapp.comm.LocalConfig;
import jp.adtekfuji.andonapp.common.AndonResizeHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@FxScene(id = "Main", fxmlPath = "/fxml/main.fxml")
public class MainFxController implements Initializable {

    private static final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Pane mainPane;
    @FXML
    private VBox defaultPane;
    @FXML
    private Label label;
    @FXML
    private Button retryButton;

    private AndonResizeHelper helper;

    /**
     * メイン画面を初期化する。
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // 画面サイズが変更されたら、mainPaneの表示倍率を更新する。
        anchorPane.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            changeSizeTask();
        });
        anchorPane.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            changeSizeTask();
        });

        Platform.runLater(() -> {
            sc.getStage().setFullScreen(LocalConfig.isFullScreen());
            login();
        });

        Platform.runLater(() -> {
            helper = new AndonResizeHelper(MainApp.getStage(), !LocalConfig.isShowFrame(), 300, 200);
        });
    }

    /**
     * 再試行ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onRetry(ActionEvent event) {
        Platform.runLater(() -> {
            retryButton.setVisible(false);
            label.setText("Connecting to server...");
            login();
        });
    }

    /**
     * サーバーにログインする。
     */
    private void login() {
        try {
            AndonPluginContainer container = AndonPluginContainer.getInstance();
            container.andonLogin();
            container.updateAndonSetting();
            defaultPane.setVisible(false);
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getBounds();
            container.visibleMonitor(mainPane, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            String[] message = ErrorUtils.getMessage(ex);
            label.setText(message[0] + "\n" + message[1]);
            retryButton.setVisible(true);
        }
    }

    /**
     * キーイベント処理
     *
     * @param ke
     */
    @FXML
    private void onKeyPressed(KeyEvent ke) {
        if (ke.getCode() == KeyCode.F5) {
            AndonPluginContainer.getInstance().notice(new ResetCommand());
        }
    }

    /**
     * anchorPaneのサイズにあわせて、mainPaneの表示倍率を更新する。
     */
    private void changeSizeTask() {
        Platform.runLater(() -> {
            // アスペクト比を維持したままmainPaneをスケーリングするための倍率
            double scale;
            double scaleX = anchorPane.getWidth() / mainPane.getWidth();
            double scaleY = anchorPane.getHeight() / mainPane.getHeight();
            if (scaleX < scaleY) {
                scale = scaleX;
            } else {
                scale = scaleY;
            }

            // スケーリング後のmainPaneのイメージサイズ
            double wid = (mainPane.getWidth() * scale);
            double hei = (mainPane.getHeight() * scale);

            // スケーリングされたmainPaneのイメージが、anchorpaneの中央に表示されるオフセット
            double offsetX = ((anchorPane.getWidth() - wid) / 2.0) - ((mainPane.getWidth() - wid) / 2.0);
            double offsetY = ((anchorPane.getHeight() - hei) / 2.0) - ((mainPane.getHeight() - hei) / 2.0);

            mainPane.setScaleX(scale);
            mainPane.setScaleY(scale);
            mainPane.setLayoutX(offsetX);
            mainPane.setLayoutY(offsetY);
        });
    }
}
