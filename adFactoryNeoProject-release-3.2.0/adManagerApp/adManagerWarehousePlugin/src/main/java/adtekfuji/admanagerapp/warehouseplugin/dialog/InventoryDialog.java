/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.dialog;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 棚卸ダイアログ
 *
 * @author nar-nakamura
 */
@FxComponent(id = "InventoryDialog", fxmlPath = "/fxml/warehouseplugin/inventory_dialog.fxml")
public class InventoryDialog implements ArgumentDelivery, DialogHandler {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private Dialog dialog;

    private InventoryDialogArgument dialogArgument;

    @FXML
    private Label infoLabel;
    @FXML
    private ComboBox areaNameCombo;
    @FXML
    private Button okButton;

    /**
     * コンストラクタ
     */
    public InventoryDialog() {
    }

    /**
     * 引数を設定する。
     *
     * @param argument 引数
     */
    @Override
    public void setArgument(Object argument) {
        if (argument instanceof InventoryDialogArgument) {
            this.dialogArgument = (InventoryDialogArgument) argument;

            switch (this.dialogArgument.getDialogType()) {
                case COMPLETE:
                    this.initCompleteDialog();
                    break;
                case CANCEL:
                    this.initCancelDialog();
                    break;
                case START:
                default:
                    this.initStartDialog();
                    break;
            }

            for (String areaName : this.dialogArgument.getAreaNames()) {
                if (!this.areaNameCombo.getItems().contains(areaName)) {
                    this.areaNameCombo.getItems().add(areaName);
                }
            }

            // 区画選択イベント
            this.areaNameCombo.valueProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
                if (Objects.isNull(newValue)) {
                    return;
                }

                this.okButton.setDisable(false);
            });
        }
    }

    /**
     * 棚卸開始ダイアログとして初期化する。
     */
    private void initStartDialog() {
        this.infoLabel.setText(LocaleUtils.getString("key.InventoryDialog.StartMessage"));
        this.okButton.setText(LocaleUtils.getString("key.start"));

        this.okButton.setDisable(true);
    }

    /**
     * 棚卸中止ダイアログとして初期化する。
     */
    private void initCancelDialog() {
        this.infoLabel.setText(LocaleUtils.getString("key.InventoryDialog.CancelMessage"));
        this.okButton.setText(LocaleUtils.getString("key.KanbanStatusInterrupt"));

        this.okButton.setDisable(true);
    }

    /**
     * 棚卸完了ダイアログとして初期化する。
     */
    private void initCompleteDialog() {
        this.infoLabel.setText(LocaleUtils.getString("key.InventoryDialog.CompleteMessage"));
        this.okButton.setText(LocaleUtils.getString("key.complete"));

        this.okButton.setDisable(true);
    }

    /**
     * ダイアログを設定する。
     *
     * @param dialog ダイアログ
     */
    @Override
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest((WindowEvent we) -> {
            this.closeDialog(ButtonType.CLOSE);
        });
    }

    /**
     * OKボタンのアクション
     *
     * @param event 
     */
    @FXML
    private void onOkButton(ActionEvent event) {
        this.closeDialog(ButtonType.OK);
    }

    /**
     * キャンセルボタンのアクション
     *
     * @param event 
     */
    @FXML
    private void onCancelButton(ActionEvent event) {
        this.closeDialog(ButtonType.CANCEL);
    }

    /**
     * 閉じるボタンのアクション
     *
     * @param event 
     */
    @FXML
    private void onCloseButton(ActionEvent event) {
        this.closeDialog(ButtonType.CLOSE);
    }

    /**
     * ダイアログを閉じる。
     *
     * @param buttonType ボタン種別
     */
    private void closeDialog(ButtonType buttonType) {
        try {
            // 選択区画名
            String areaName = null;
            if (Objects.nonNull(this.areaNameCombo.getValue())) {
                areaName = (String) this.areaNameCombo.getValue();
            }

            this.dialogArgument.setSelectedAreaName(areaName);

            this.dialog.setResult(buttonType);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 選択区画名を取得する。
     *
     * @return 選択区画名
     */
    public String getSelectedAreaName() {
        return this.dialogArgument.getSelectedAreaName();
    }
}
