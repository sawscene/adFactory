/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.dialog;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;
import jp.adtekfuji.javafxcommon.dialog.MessageDialogEnum.MessageDialogResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * メッセージダイアログのコントローラー
 *
 * @author nar-nakamura
 */
@FxComponent(id = "MessageDialog", fxmlPath = "/fxml/javafxcommon/message_dialog.fxml")
public class MessageDialogFxController implements Initializable, ArgumentDelivery, DialogHandler {

    private final static Logger logger = LogManager.getLogger();

    private Dialog dialog;

    @FXML
    private Pane messagePane;
    @FXML
    private ImageView iconImage;
    @FXML
    private Label messageLabel;
    @FXML
    private Button yesButton;
    @FXML
    private Button yesToAllButton;
    @FXML
    private Button noButton;
    @FXML
    private Button noToAllButton;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    /**
     * メッセージダイアログのコントローラー
     */
    public MessageDialogFxController() {
    }

    /**
     * 初期化処理
     *
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    /**
     * 引数を設定する。
     *
     * @param argument MessageDialogArgument
     */
    @Override
    public void setArgument(Object argument) {
        if (argument instanceof MessageDialogArgument) {
            MessageDialogArgument arg = (MessageDialogArgument) argument;

            // メッセージ枠
            this.messagePane.setStyle(String.format("-fx-border-width: %f; -fx-border-color: %s; -fx-background-color: %s",
                    arg.getBorderWidth(), arg.getBorderColor(), arg.getBackgroundColor()));

            // メッセージ
            this.messageLabel.setText(arg.getMessage());

            // ダイアログ種別
            Image img;
            switch (arg.getDialogType()) {
                case Infomation:
                    img = new Image(this.getClass().getResourceAsStream("/image/information.png"));
                    break;
                case Question:
                    img = new Image(this.getClass().getResourceAsStream("/image/question.png"));
                    break;
                case Warning:
                    img = new Image(this.getClass().getResourceAsStream("/image/alert.png"));
                    break;
                case Error:
                    img = new Image(this.getClass().getResourceAsStream("/image/error.png"));
                    break;
                default:
                    img = null;
                    break;
            }

            if (Objects.nonNull(img)) {
                this.iconImage.setVisible(true);
                this.iconImage.setManaged(true);
                this.iconImage.setImage(img);
            } else {
                this.iconImage.setVisible(false);
                this.iconImage.setManaged(false);
            }

            // ボタン
            switch (arg.getDialogButtons()) {
                case YesNo:
                    this.yesButton.setVisible(true);
                    this.yesToAllButton.setVisible(false);
                    this.noButton.setVisible(true);
                    this.noToAllButton.setVisible(false);
                    this.okButton.setVisible(false);
                    this.cancelButton.setVisible(false);

                    this.yesButton.setManaged(true);
                    this.yesToAllButton.setManaged(false);
                    this.noButton.setManaged(true);
                    this.noToAllButton.setManaged(false);
                    this.okButton.setManaged(false);
                    this.cancelButton.setManaged(false);

                    Platform.runLater(() -> {
                        this.yesButton.requestFocus();
                    });
                    break;
                case YesNoCancel:
                    this.yesButton.setVisible(true);
                    this.yesToAllButton.setVisible(false);
                    this.noButton.setVisible(true);
                    this.noToAllButton.setVisible(false);
                    this.okButton.setVisible(false);
                    this.cancelButton.setVisible(true);

                    this.yesButton.setManaged(true);
                    this.yesToAllButton.setManaged(false);
                    this.noButton.setManaged(true);
                    this.noToAllButton.setManaged(false);
                    this.okButton.setManaged(false);
                    this.cancelButton.setManaged(true);

                    Platform.runLater(() -> {
                        this.yesButton.requestFocus();
                    });
                    break;
                case YesToAllNoCancel:
                    this.yesButton.setVisible(true);
                    this.yesToAllButton.setVisible(true);
                    this.noButton.setVisible(true);
                    this.noToAllButton.setVisible(false);
                    this.okButton.setVisible(false);
                    this.cancelButton.setVisible(true);

                    this.yesButton.setManaged(true);
                    this.yesToAllButton.setManaged(true);
                    this.noButton.setManaged(true);
                    this.noToAllButton.setManaged(false);
                    this.okButton.setManaged(false);
                    this.cancelButton.setManaged(true);

                    Platform.runLater(() -> {
                        this.yesButton.requestFocus();
                    });
                    break;
                case YesToAllNoToAllCancel:
                    this.yesButton.setVisible(true);
                    this.yesToAllButton.setVisible(true);
                    this.noButton.setVisible(true);
                    this.noToAllButton.setVisible(true);
                    this.okButton.setVisible(false);
                    this.cancelButton.setVisible(true);

                    this.yesButton.setManaged(true);
                    this.yesToAllButton.setManaged(true);
                    this.noButton.setManaged(true);
                    this.noToAllButton.setManaged(true);
                    this.okButton.setManaged(false);
                    this.cancelButton.setManaged(true);

                    Platform.runLater(() -> {
                        this.yesButton.requestFocus();
                    });
                    break;
                case OK:
                    this.yesButton.setVisible(false);
                    this.yesToAllButton.setVisible(false);
                    this.noButton.setVisible(false);
                    this.noToAllButton.setVisible(false);
                    this.okButton.setVisible(true);
                    this.cancelButton.setVisible(false);

                    this.yesButton.setManaged(false);
                    this.yesToAllButton.setManaged(false);
                    this.noButton.setManaged(false);
                    this.noToAllButton.setManaged(false);
                    this.okButton.setManaged(true);
                    this.cancelButton.setManaged(false);

                    Platform.runLater(() -> {
                        this.okButton.requestFocus();
                    });
                    break;
                case OKCancel:
                default:
                    this.yesButton.setVisible(false);
                    this.yesToAllButton.setVisible(false);
                    this.noButton.setVisible(false);
                    this.noToAllButton.setVisible(false);
                    this.okButton.setVisible(true);
                    this.cancelButton.setVisible(true);

                    this.yesButton.setManaged(false);
                    this.yesToAllButton.setManaged(false);
                    this.noButton.setManaged(false);
                    this.noToAllButton.setManaged(false);
                    this.okButton.setManaged(true);
                    this.cancelButton.setManaged(true);

                    Platform.runLater(() -> {
                        this.okButton.requestFocus();
                    });
                    break;
            }
        }
    }

    /**
     * 
     * @param dialog 
     */
    @Override
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest((WindowEvent we) -> {
            this.cancelDialog();
        });
    }

    /**
     * 「はい」ボタン
     *
     * @param event 
     */
    @FXML
    private void onYesButton(ActionEvent event) {
        try {
            this.dialog.setResult(MessageDialogResult.Yes);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 「すべて はい」ボタン
     *
     * @param event 
     */
    @FXML
    private void onYesToAllButton(ActionEvent event) {
        try {
            this.dialog.setResult(MessageDialogResult.YesToAll);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 「いいえ」ボタン
     *
     * @param event 
     */
    @FXML
    private void onNoButton(ActionEvent event) {
        try {
            this.dialog.setResult(MessageDialogResult.No);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 「すべて いいえ」ボタン
     *
     * @param event 
     */
    @FXML
    private void onNoToAllButton(ActionEvent event) {
        try {
            this.dialog.setResult(MessageDialogResult.NoToAll);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 「OK」ボタン
     *
     * @param event 
     */
    @FXML
    private void onOkButton(ActionEvent event) {
        try {
            this.dialog.setResult(MessageDialogResult.Ok);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 「キャンセル」ボタン
     *
     * @param event 
     */
    @FXML
    private void onCancelButton(ActionEvent event) {
        this.cancelDialog();
    }

    /**
     * キャンセル処理
     */
    private void cancelDialog() {
        try {
            this.dialog.setResult(MessageDialogResult.Cancel);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
}
