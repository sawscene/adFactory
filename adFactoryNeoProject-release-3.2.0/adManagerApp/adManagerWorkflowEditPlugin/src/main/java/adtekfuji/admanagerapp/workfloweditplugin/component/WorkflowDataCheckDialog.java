/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.component;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import adtekfuji.locale.LocaleUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.WindowEvent;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowDataCheckInfoEntity;
import jp.adtekfuji.adFactory.enumerate.WorkflowDateCheckErrorTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * データチェックダイアログ
 *
 * @author
 */
@FxComponent(id = "WorkflowDataCheckDialog", fxmlPath = "/fxml/compo/workflow_data_check_dialog.fxml")
public class WorkflowDataCheckDialog implements Initializable, ArgumentDelivery, DialogHandler {

    private final Logger logger = LogManager.getLogger();
    private ResourceBundle rb = null;

    private Dialog dialog;
    private final ObservableList<DisplayDataCheckEntity> list = FXCollections.observableArrayList();

    @FXML
    private StackPane anchorPane;
    @FXML
    private TableView<DisplayDataCheckEntity> dataCheckListTableView;
    @FXML
    private TableColumn<DisplayDataCheckEntity, String> levelColumn; // レベル
    @FXML
    private TableColumn<DisplayDataCheckEntity, String> errorTypeColumn; // エラータイプ
    @FXML
    private TableColumn<DisplayDataCheckEntity, String> messageColumn; // メッセージ

    /**
     * 表示用エンティティ
     *
     * @author
     */
    public static class DisplayDataCheckEntity {
        public static final String LINE_SEPARATOR = System.getProperty("line.separator");
        private final StringProperty level = new SimpleStringProperty();
        private final StringProperty type = new SimpleStringProperty();
        private final StringProperty message = new SimpleStringProperty();

        /**
         * コンストラクタ
         *
         * @param rb
         * @param entity エンティティ
         *
         */
        public DisplayDataCheckEntity(ResourceBundle rb, WorkflowDataCheckInfoEntity entity) {

            this.level.setValue(LocaleUtils.getString("key.Warning"));
            this.type.setValue(LocaleUtils.getString(entity.getErrorType().getType()));
            this.message.setValue(LocaleUtils.getString(entity.getErrorType().getMessage()) + LINE_SEPARATOR + entity.getMessage());
        }

        public StringProperty errorTypeProperty() {
            return type;
        }

        public StringProperty messageProperty() {
            return message;
        }

        public StringProperty levelProperty() {
            return level;
        }
    }

    /**
     * コンストラクタ
     */
    public WorkflowDataCheckDialog() {

    }

    @Override
    public void initialize(URL location, ResourceBundle rb) {

        dataCheckListTableView.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        this.rb = rb;

        this.dataCheckListTableView.setEditable(true);
        // レベル
        this.levelColumn.setCellValueFactory((TableColumn.CellDataFeatures<DisplayDataCheckEntity, String> param) -> param.getValue().levelProperty());
        // エラータイプ
        this.errorTypeColumn.setCellValueFactory((TableColumn.CellDataFeatures<DisplayDataCheckEntity, String> param) -> param.getValue().errorTypeProperty());
        // メッセージ
        this.messageColumn.setCellValueFactory((TableColumn.CellDataFeatures<DisplayDataCheckEntity, String> param) -> param.getValue().messageProperty());
    }

    @Override
    public void setArgument(Object argument) {
        if (argument instanceof List) {
            updateView((List) argument);
        }
    }

    /**
     * 画面更新
     *
     * @param argument
     */
    private void updateView(List argument) {
        blockUI(true);
        Platform.runLater(() -> {
            try {
                List<Object> workflows = argument;
                this.dataCheckListTableView.getItems().clear();
                this.dataCheckListTableView.getSortOrder().clear();
                this.list.clear();

                this.list.addAll(workflows.stream()
                        .filter(WorkflowDataCheckInfoEntity.class::isInstance)
                        .map(WorkflowDataCheckInfoEntity.class::cast)
                        .map(entity -> new DisplayDataCheckEntity(rb, entity))
                        .collect(Collectors.toList()));

                this.dataCheckListTableView.setItems(this.list);

            } catch (Exception ex) {
                logger.fatal(ex, ex);
            } finally {
                blockUI(false);
            }
        });
    }

    @Override
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest((WindowEvent we) -> {
            this.okDialog();
        });
    }

    /**
     * OK処理
     */
    private void okDialog() {
        try {
            this.dialog.setResult(ButtonType.CANCEL);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * OKボタンのアクション
     *
     * @param event イベント
     */
    @FXML
    private void onOKButton(ActionEvent event) {
        this.okDialog();
    }

    /**
     * UIロック
     *
     * @param flg True＝ロック
     */
    private void blockUI(Boolean flg) {
        this.anchorPane.setDisable(flg);
    }
}
