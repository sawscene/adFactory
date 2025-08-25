/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.component;

import adtekfuji.admanagerapp.kanbaneditplugin.common.StatusChange;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * カンバンステータス変更ダイアログ
 *
 * @author HN)y-harada
 */
@FxComponent(id = "StatusChangeDialog", fxmlPath = "/fxml/admanagerapp/kanbaneditplugin/status_change_dialog.fxml")
public class StatusChangeFxController implements Initializable, ArgumentDelivery, DialogHandler {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private StatusChange statusChange;
    private Dialog dialog;

    @FXML
    private ComboBox<KanbanStatusEnum> kanbanStatusCombo;

    @FXML
    private CheckBox forcedCheck;
    
    /**
     * コンストラクタ
     */
    public StatusChangeFxController() {
    }

    /**
     * プロパティ情報型表示用セルクラス
     *
     */
    class KanbanStatusEnumComboBoxCellFactory extends ListCell<KanbanStatusEnum> {

        @Override
        protected void updateItem(KanbanStatusEnum item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else {
                setText(LocaleUtils.getString(item.getResourceKey()));
            }
        }
    }

    /**
     * カンバンステータス変更ダイアログを初期化する。
     * 
     * @param url URL
     * @param rb リソースバンドル
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Callback<ListView<KanbanStatusEnum>, ListCell<KanbanStatusEnum>> comboCellFactory = (ListView<KanbanStatusEnum> param) -> new StatusChangeFxController.KanbanStatusEnumComboBoxCellFactory();
            kanbanStatusCombo.setButtonCell(new StatusChangeFxController.KanbanStatusEnumComboBoxCellFactory());
            kanbanStatusCombo.setCellFactory(comboCellFactory);
            Platform.runLater(() -> {
                kanbanStatusCombo.setItems(FXCollections.observableArrayList(Arrays.asList(KanbanStatusEnum.PLANNED, KanbanStatusEnum.COMPLETION, KanbanStatusEnum.INTERRUPT)));
                kanbanStatusCombo.setVisible(true);
            });
    
            this.forcedCheck.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                // 強制フラグが変更されたら、変更ステータス一覧を変更する
                if (newValue) {
                    kanbanStatusCombo.setItems(FXCollections.observableArrayList(Arrays.asList(KanbanStatusEnum.CanChangeKanbanStatusEnum)));
                } else {
                    kanbanStatusCombo.setItems(FXCollections.observableArrayList(Arrays.asList(KanbanStatusEnum.PLANNED, KanbanStatusEnum.COMPLETION, KanbanStatusEnum.INTERRUPT)));
                }
            });
            
            
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * パラメーターを設定する。
     * 
     * @param argument パラメーター
     */
    @Override
    public void setArgument(Object argument) {
        if (argument instanceof StatusChange) {
            this.statusChange = (StatusChange) argument;
        }
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
            this.cancelDialog();
        });
    }

    /**
     * 変更ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onChange(ActionEvent event) {
        try {
            // 選択したカンバンのステータスを設定する。
            this.statusChange.setNewStatus(this.kanbanStatusCombo.getValue());
            this.statusChange.setForced(this.forcedCheck.isSelected());

            this.dialog.setResult(ButtonType.OK);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * キャンセルボタンのアクション
     * 
     * @param event アクションイベント
     */
    @FXML
    private void onCancel(ActionEvent event) {
        this.cancelDialog();
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
