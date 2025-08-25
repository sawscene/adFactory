package jp.adtekfuji.worktimeresultcopier;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.util.converter.NumberStringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainSceneFXMLController implements Initializable {

    private static final Logger logger = LogManager.getLogger();
    private final OutputActualInfo info = new OutputActualInfo();


    @FXML
    private Pane progressPane;
    @FXML
    private TextField inFolder;
    @FXML
    private TextField outFolder;
    @FXML
    private TextField userField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField shareFolder;
    @FXML
    private TextField errorMailServer;
    @FXML
    private TextField errorMailPort;
    @FXML
    private TextField errorMailTo;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            info.load();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        
        inFolder.textProperty().bindBidirectional(info.inFolderProperty());
        outFolder.textProperty().bindBidirectional(info.outFolderProperty());
        userField.textProperty().bindBidirectional(info.userProperty());
        passwordField.textProperty().bindBidirectional(info.passwordProperty());

        shareFolder.textProperty().bindBidirectional(info.shareFolderProperty());
        
        errorMailServer.textProperty().bindBidirectional(info.getErrorMailServerProperty());
        errorMailPort.textProperty().bindBidirectional(info.getErrorMailPortProperty(), new NumberStringConverter());
        errorMailTo.textProperty().bindBidirectional(info.getErrorMailToProperty());

        blockUI(false);
    }

    private void blockUI(boolean block) {
        Platform.runLater(() -> {
            progressPane.setVisible(block);
        });
    }

    @FXML
    private void onCloseAction(ActionEvent event) {
        logger.info("onCloseAction");
        try {
            info.save();
        } catch (Exception ex) {
            showAlert("設定保存", "設定の保存でエラーが発生しました。", Alert.AlertType.ERROR);
            logger.fatal(ex, ex);
        }
        Platform.exit();
    }

    @FXML
    private void onActualOutAction(ActionEvent event) {
        logger.info("onActualOutAction");
        blockUI(true);
        Task task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                // 設定を保存する。
                info.save();
                ResultFileProcessorFacade resultFileProcessorFacade = new ResultFileProcessorFacade(info);

                return resultFileProcessorFacade.invoke();
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                try {
                    // 処理結果
                    if (this.getValue() == 0) {
                        showAlert("連携結果取込", "連携結果ファイルがありません。", Alert.AlertType.INFORMATION);
                    } else if (this.getValue() > 0) {
                        showAlert("連携結果取込", "連携結果をコピーしました。", Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("連携結果取込", "連携結果取込でエラーが発生しました。", Alert.AlertType.ERROR);
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    blockUI(false);
                }
            }

            @Override
            protected void failed() {
                super.failed();
                try {
                    logger.fatal(this.getException(), this.getException());
                    showAlert("連携結果取込", "連携結果取込でエラーが発生しました。", Alert.AlertType.ERROR);
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    blockUI(false);
                }
            }
        };
        new Thread(task).start();
    }
    

    /**
     * メッセージダイアログを表示する。
     *
     * @param title タイトル
     * @param message メッセージ
     * @param type ダイアログ種別
     * @return 押下したボタンの種類
     */
    private ButtonType showAlert(String title, String message, Alert.AlertType type) {
        ButtonType result = null;
        try {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            result = alert.showAndWait().get();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }
}
