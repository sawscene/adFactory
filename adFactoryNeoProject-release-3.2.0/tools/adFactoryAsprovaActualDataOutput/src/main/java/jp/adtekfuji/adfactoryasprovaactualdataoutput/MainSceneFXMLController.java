package jp.adtekfuji.adfactoryasprovaactualdataoutput;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.util.converter.DateTimeStringConverter;
import javafx.util.converter.NumberStringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 作業実績出力ツール画面
 *
 * @author koga
 */
public class MainSceneFXMLController implements Initializable {

    private static final Logger logger = LogManager.getLogger();
    private final OutputActualInfo info = new OutputActualInfo();

    @FXML
    private Pane mainPane;
    @FXML
    private Pane progressPane;

    @FXML
    private TextField adfactoryAddressField;
    private final ToggleGroup selectSearchGroup = new ToggleGroup();
    @FXML
    private RadioButton selectFromToSearch;
    @FXML
    private RadioButton selectFromLastSearched;
    @FXML
    private TextField fromSearchField;
    @FXML
    private TextField toSearchField;
    @FXML
    public TextField lastSearchedField;
    @FXML
    public TextField startWorkNameField;
    @FXML
    public TextField endWorkNameField;
    @FXML
    private TextField folderPathField;
    @FXML
    private TextField errorMailServerField;
    @FXML
    private TextField errorMailPortField;
    @FXML
    private TextField mailToField;

    /**
     * 数値バリデーション
     */
    private final EventHandler<KeyEvent> numericValidate = (KeyEvent event) -> {
        if (!event.getCharacter().matches("\\d")) {
            event.consume();
        }
    };

    /**
     * 日付バリデーション
     */
    private final EventHandler<KeyEvent> datetimeValidate = (KeyEvent event) -> {
        if (!event.getCharacter().matches("\\d|:|-|/|\\s")) {
            event.consume();
        }
    };

    /**
     * 初期化
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // 設定の読み込み
            info.load();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        // ADFACTORYサーバーアドレス
        adfactoryAddressField.textProperty().bindBidirectional(info.adFactoryAddressProperty());
        // データ取り込み間隔
        selectFromToSearch.setToggleGroup(selectSearchGroup);
        selectFromToSearch.setUserData(OutputActualFacade.SEARCH_TYPE.FROM_TO_SEARCH);
        selectFromLastSearched.setToggleGroup(selectSearchGroup);
        selectFromLastSearched.setUserData(OutputActualFacade.SEARCH_TYPE.FROM_LAST_SEARCHED);
        // 検索範囲日時（FROM）
        fromSearchField.textProperty()
                .bindBidirectional(info.fromSearchDatetimeProperty(), new DateTimeStringConverter("yyyy/MM/dd HH:mm:ss"));
        fromSearchField.addEventFilter(KeyEvent.KEY_TYPED, datetimeValidate);
        // 検索範囲日時（TO）
        toSearchField.textProperty()
                .bindBidirectional(info.toSearchDatetimeProperty(), new DateTimeStringConverter("yyyy/MM/dd HH:mm:ss"));
        toSearchField.addEventFilter(KeyEvent.KEY_TYPED, datetimeValidate);
        // 前回実行日時
        lastSearchedField.textProperty()
                .bindBidirectional(info.lastSearchedDatetimeProperty(), new DateTimeStringConverter("yyyy/MM/dd HH:mm:ss"));
        lastSearchedField.addEventFilter(KeyEvent.KEY_TYPED, datetimeValidate);
        // 開発工程名
        startWorkNameField.textProperty().bindBidirectional(info.startWorkNameProperty());
        // 終了工程名
        endWorkNameField.textProperty().bindBidirectional(info.endWorkNameProperty());
        // フォルダーパス
        folderPathField.textProperty().bindBidirectional(info.folderPathProperty());
        // メールサーバー
        errorMailServerField.textProperty().bindBidirectional(info.errorMailServerProperty());
        // メールポート番号
        errorMailPortField.textProperty().bindBidirectional(info.errorMailPortProperty(), new NumberStringConverter());
        errorMailPortField.addEventFilter(KeyEvent.KEY_TYPED, numericValidate);
        // メール送信先
        mailToField.textProperty().bindBidirectional(info.errorMailToProperty());

        blockUI(false);
    }

    /**
     * 画面編集可否
     *
     * @param true:編集可、false:編集不可
     */
    private void blockUI(boolean block) {
        Platform.runLater(() -> {
            mainPane.setDisable(block);
            progressPane.setVisible(block);
        });
    }

    /**
     * 設定を保存して閉じるボタン処理
     *
     * @param event
     */
    @FXML
    private void onCloseAction(ActionEvent event) {
        logger.info("onCloseAction");
        try {
            // 設定を保存
            info.save();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        Platform.exit();
    }

    /**
     * 実績報告ボタン処理
     *
     * @param event
     */
    @FXML
    private void onActualOutAction(ActionEvent event) {
        logger.info("onActualOutAction");

        // 設定を保存
        info.save();
        // 作業実績出力（カンバンに作業コードが含まれる作業実績）
        this.outputActualResult();
        // 作業実績出力（工程に作業コードが含まれる作業実績）
        this.outputWorkActualResult();
    }

    /**
     * 作業実績出力（カンバンに作業コードが含まれる作業実績）
     */
    private void outputActualResult() {
        blockUI(true);
        Task task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                // 実績情報ファイル（CSV）を作成し、フォルダーパスに格納
                OutputActualFacade outputActualFacade = new OutputActualFacade(info);
                return outputActualFacade.output(
                        (OutputActualFacade.SEARCH_TYPE) selectSearchGroup.getSelectedToggle().getUserData()
                        , OutputActualFacade.OUTPUT_TYPE.KANBAN_WORK_CODE);
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                try {
                    // 処理結果
                    if (this.getValue() == 0) {
                        showAlert("実績情報出力（カンバンに作業コードが含まれる実績情報）", "出力対象がありません。", Alert.AlertType.INFORMATION);
                    } else if (this.getValue() > 0) {
                        showAlert("実績情報出力（カンバンに作業コードが含まれる実績情報）", String.format("実績情報を出力しました。\r\n報告数: %d", this.getValue()), Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("実績情報出力（カンバンに作業コードが含まれる実績情報）", "実績情報出力でエラーが発生しました。", Alert.AlertType.ERROR);
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
                    showAlert("実績情報出力（カンバンに作業コードが含まれる実績情報）", "実績情報出力でエラーが発生しました。エラー内容は通知メールを参照ください。", Alert.AlertType.ERROR);
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
     * 作業実績出力（工程に作業コードが含まれる作業実績）
     */
    private void outputWorkActualResult() {
        blockUI(true);
        Task task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                // 実績情報ファイル（CSV）を作成し、フォルダーパスに格納
                OutputActualFacade outputActualFacade = new OutputActualFacade(info);
                return outputActualFacade.output(
                        (OutputActualFacade.SEARCH_TYPE) selectSearchGroup.getSelectedToggle().getUserData()
                        , OutputActualFacade.OUTPUT_TYPE.PROCESS_WORK_CODE);
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                try {
                    // 処理結果
                    if (this.getValue() == 0) {
                        showAlert("実績情報出力（工程に作業コードが含まれる実績情報）", "出力対象がありません。", Alert.AlertType.INFORMATION);
                    } else if (this.getValue() > 0) {
                        showAlert("実績情報出力（工程に作業コードが含まれる実績情報）", String.format("実績情報を出力しました。\r\n報告数: %d", this.getValue()), Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("実績情報出力（工程に作業コードが含まれる実績情報）", "実績情報出力でエラーが発生しました。", Alert.AlertType.ERROR);
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
                    showAlert("実績情報出力（工程に作業コードが含まれる実績情報）", "実績情報出力でエラーが発生しました。エラー内容は通知メールを参照ください。", Alert.AlertType.ERROR);
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
