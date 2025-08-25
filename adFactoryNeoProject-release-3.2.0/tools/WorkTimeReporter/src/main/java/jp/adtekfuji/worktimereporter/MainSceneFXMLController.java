package jp.adtekfuji.worktimereporter;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.util.converter.DateTimeStringConverter;
import javafx.util.converter.NumberStringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainSceneFXMLController implements Initializable {

    private static final Logger logger = LogManager.getLogger();
    private final OutputActualInfo info = new OutputActualInfo();

    @FXML
    private Pane mainPane;
    @FXML
    private Pane progressPane;
    @FXML
    private TextField adfactoryAddressField;
    @FXML
    private TextField uptakeIntervalField;
    @FXML
    private TextField fromSearchField;
    @FXML
    private TextField toSearchField;
    @FXML
    private TextField folderAddressField;
    @FXML
    private TextField userField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField errorMailServerField;
    @FXML
    private TextField errorMailPortField;
    @FXML
    private TextField errorMailRecipientField;

    private final ToggleGroup selectSearchGroup = new ToggleGroup();
    @FXML
    private RadioButton selectIntervalTime;
    @FXML
    private RadioButton selectFromToSearch;

    private final EventHandler<KeyEvent> numericValidate = (KeyEvent event) -> {
        if (!event.getCharacter().matches("\\d")) {
            event.consume();
        }
    };

    private final EventHandler<KeyEvent> datetimeValidate = (KeyEvent event) -> {
        if (!event.getCharacter().matches("\\d|:|-|/|\\s")) {
            event.consume();
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            info.load();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        adfactoryAddressField.textProperty().bindBidirectional(info.adFactoryAddressProperty());
        uptakeIntervalField.textProperty().bindBidirectional(info.uptakeIntervalProperty(), new NumberStringConverter());
        fromSearchField.textProperty().bindBidirectional(info.fromSearchDatetimeProperty(), new DateTimeStringConverter("yyyy/MM/dd HH:mm:ss"));
        toSearchField.textProperty().bindBidirectional(info.toSearchDatetimeProperty(), new DateTimeStringConverter("yyyy/MM/dd HH:mm:ss"));
        folderAddressField.textProperty().bindBidirectional(info.getFolderAddressProperty());
        userField.textProperty().bindBidirectional(info.getUserNameProperty());
        passwordField.textProperty().bindBidirectional(info.getPasswordProperty());
        errorMailServerField.textProperty().bindBidirectional(info.getErrorMailServerProperty());
        errorMailPortField.textProperty().bindBidirectional(info.getErrorMailPortProperty(), new NumberStringConverter());
        errorMailRecipientField.textProperty().bindBidirectional(info.getErrorMailToProperty());

        uptakeIntervalField.addEventFilter(KeyEvent.KEY_TYPED, numericValidate);
        fromSearchField.addEventFilter(KeyEvent.KEY_TYPED, datetimeValidate);
        toSearchField.addEventFilter(KeyEvent.KEY_TYPED, datetimeValidate);

        selectIntervalTime.setToggleGroup(selectSearchGroup);
        selectIntervalTime.setUserData(OutputActualFacade.SEARCH_TYPE.INTERVAL_TIME);
        selectFromToSearch.setToggleGroup(selectSearchGroup);
        selectFromToSearch.setUserData(OutputActualFacade.SEARCH_TYPE.FROM_TO_SEARCH);

        blockUI(false);
    }

    private void blockUI(boolean block) {
        Platform.runLater(() -> {
            mainPane.setDisable(block);
            progressPane.setVisible(block);
        });
    }

    @FXML
    private void onCloseAction(ActionEvent event) {
        logger.info("onCloseAction");
        try {
            info.save();
        } catch (Exception ex) {
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
                // 工数実績ファイルを作成して、フォルダーにコピーする。
                OutputActualFacade outputActualFacade = new OutputActualFacade(info);
                return outputActualFacade.output((OutputActualFacade.SEARCH_TYPE) selectSearchGroup.getSelectedToggle().getUserData());
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                try {
                    // 処理結果
                    if (this.getValue() == 0) {
                        showAlert("実績情報出力", "出力対象がありません。", Alert.AlertType.INFORMATION);
                    } else if (this.getValue() > 0) {
                        showAlert("実績情報出力", String.format("実績情報を出力しました。\r\nファイル数: %d", this.getValue()), Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("実績情報出力", "実績情報出力でエラーが発生しました。", Alert.AlertType.ERROR);
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
                    showAlert("実績情報出力", "実績情報出力でエラーが発生しました。", Alert.AlertType.ERROR);
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
