package jp.adtekfuji.prodcountreporter;

import java.net.URL;
import java.util.Base64;
import java.util.Map;
import java.util.ResourceBundle;

import com.google.gson.Gson;
import io.vavr.control.Either;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.util.converter.DateTimeStringConverter;
import javafx.util.converter.NumberStringConverter;
import jp.adtekfuji.prodcountreporter.json.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainSceneFXMLController implements Initializable {

    private static final Logger logger = LogManager.getLogger();
    private final OutputActualInfo info = new OutputActualInfo();

    @FXML
    private TextField errorMailServer;
    @FXML
    private TextField errorMailPort;
    @FXML
    private TextField mailTo;

    @FXML
    private Pane mainPane;
    @FXML
    private Pane progressPane;
    @FXML
    private TextField adfactoryAddressField;

    @FXML
    private TextField fromSearchField;
    @FXML
    private TextField toSearchField;
    @FXML
    public TextField lastUpdateField;
    @FXML
    private TextField httpAddressField;
    @FXML
    private TextField authUserField;
    @FXML
    private TextField authPasswordField;

    private final ToggleGroup selectSearchGroup = new ToggleGroup();

    @FXML
    private RadioButton selectFromToSearch;
    @FXML
    private RadioButton selectLastUpdateSearch;

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
        fromSearchField.textProperty().bindBidirectional(info.fromSearchDatetimeProperty(), new DateTimeStringConverter("yyyy/MM/dd HH:mm:ss"));
        toSearchField.textProperty().bindBidirectional(info.toSearchDatetimeProperty(), new DateTimeStringConverter("yyyy/MM/dd HH:mm:ss"));
        lastUpdateField.textProperty().bindBidirectional(info.lastUpdateTimeProperty(), new DateTimeStringConverter("yyyy/MM/dd HH:mm:ss"));
        httpAddressField.textProperty().bindBidirectional(info.httpAddressProperty());
        authUserField.textProperty().bindBidirectional(info.authUserProperty());
        authPasswordField.textProperty().bindBidirectional(info.authPasswordProperty());

        errorMailServer.textProperty().bindBidirectional(info.errorMailServerProperty());
        errorMailPort.textProperty().bindBidirectional(info.errorMailPortProperty(), new NumberStringConverter());
        mailTo.textProperty().bindBidirectional(info.errorMailToProperty());

        fromSearchField.addEventFilter(KeyEvent.KEY_TYPED, datetimeValidate);
        toSearchField.addEventFilter(KeyEvent.KEY_TYPED, datetimeValidate);
        lastUpdateField.addEventFilter(KeyEvent.KEY_TYPED, datetimeValidate);

        selectFromToSearch.setToggleGroup(selectSearchGroup);
        selectFromToSearch.setUserData(OutputActualFacade.SEARCH_TYPE.FROM_TO_SEARCH);
        selectLastUpdateSearch.setToggleGroup(selectSearchGroup);
        selectLastUpdateSearch.setUserData(OutputActualFacade.SEARCH_TYPE.LAST_UPDATE);

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
                // 工数実績ファイルを作成して、HTTP転送する。
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
                        showAlert("実績情報出力", String.format("実績情報を出力しました。\r\n報告数: %d", this.getValue()), Alert.AlertType.INFORMATION);
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
     * @param title   タイトル
     * @param message メッセージ
     * @param type    ダイアログ種別
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        try {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait().get();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    @FXML
    private void onTestStart() {

        final String userName = info.getAuthUser();
        final String passWord = info.getAuthPassword();
        final String auth = userName + ":" + passWord;
        final String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        final int numInterval = Integer.parseInt(info.getNumInterval()) * 1000;


        Request request = new Request();
        request.inData.aufnr = "bbbbbbbbbbb"; // 指図番号
        request.inData.vornr = "cccc"; // 作業/活動番号
        request.inData.lmnga = "67890"; // 数値
        request.inData.budat = "20240415"; // 転記日付


        Gson gson = new Gson();
        String json = gson.toJson(request);
        OutputActualFacade outputActualFacade = new OutputActualFacade(info);

//        json = "{ \"IN_DATA\":{ \"AUFNR\":\"3130000071\", \"VORNR\":\"0010\", \"LMNGA\":\"1\", \"BUDAT\":\"20250415\"},\"OUT_RESULT\":{}}";



        logger.info("onTestStart {} ", json);
        Either<Map<String, String>, Map<String, String>> result = outputActualFacade.sendMessage(info.getHttpAddress(), encodedAuth, json, 3, numInterval);

        if (result.isLeft()) {
            logger.info(result.getLeft());
        } else if (result.isRight()) {
            logger.info(result.get());
        }
    }

}
