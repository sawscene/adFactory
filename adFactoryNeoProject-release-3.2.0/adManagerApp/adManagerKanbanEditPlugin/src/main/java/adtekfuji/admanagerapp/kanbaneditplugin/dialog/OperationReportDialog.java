/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.dialog;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 処理結果レポートダイアログ
 *
 * @author s-heya
 */
@FxComponent(id = "OperationReportDialog", fxmlPath = "/fxml/admanagerapp/kanbaneditplugin/operation_report_dialog.fxml")
public class OperationReportDialog implements Initializable, ArgumentDelivery, DialogHandler {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private Dialog dialog;

    @FXML
    private Label messageLabel;
    @FXML
    private TextArea resultText;

    /**
     * コンストラクタ
     */
    public OperationReportDialog() {
    }

    /**
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    /**
     * 
     * @param argument 
     */
    @Override
    public void setArgument(Object argument) {
        if (argument instanceof String[]) {
            String[] array = (String[]) argument;
            this.messageLabel.setText(array[0]);
            this.resultText.setText(array[1]);
        }
    }

    /**
     * Dialog を設定する。
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
     * OKボタンが押下された。
     *
     * @param event 
     */
    @FXML
    private void onOk(ActionEvent event) {
        try {
            this.dialog.setResult(ButtonType.OK);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * キャンセル処理
     */
    private void cancelDialog() {
        try {
            this.dialog.setResult(ButtonType.CANCEL);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
}
