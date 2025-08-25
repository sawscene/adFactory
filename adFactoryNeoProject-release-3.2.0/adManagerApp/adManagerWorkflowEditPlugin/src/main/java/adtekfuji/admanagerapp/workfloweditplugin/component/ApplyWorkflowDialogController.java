/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.component;

import adtekfuji.admanagerapp.workfloweditplugin.common.ApplyWorkflowRow;
import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程の使用状況ダイアログ
 *
 * @author s-heya
 */
@FxComponent(id = "ApplyWorkflowDialog", fxmlPath = "/fxml/admanagerworkfloweditplugin/apply_workflow_dialog.fxml")
public class ApplyWorkflowDialogController implements Initializable, ArgumentDelivery, DialogHandler {
    
    private final Logger logger = LogManager.getLogger();
    private final static WorkflowInfoFacade workflowFacade = new WorkflowInfoFacade();
    private final ObservableList<ApplyWorkflowRow> rows = FXCollections.observableArrayList();
    private WorkInfoEntity work;

    private Dialog dialog;
    private CheckBox checkBox;

    @FXML
    private StackPane stackPane;
    @FXML
    private Label descLabel;
    @FXML
    private Pane progress;
    @FXML
    private PropertySaveTableView<ApplyWorkflowRow> workflowList;
    @FXML
    private TableColumn selectedColumn;
    @FXML
    private TableColumn<ApplyWorkflowRow, String> nameColumn;       // 工程順
    @FXML
    private TableColumn<ApplyWorkflowRow, String> revColumn;        // 対象工程の版数
    @FXML
    private TableColumn<ApplyWorkflowRow, String> kanbanColumn;     // カンバン
    @FXML
    private TableColumn<ApplyWorkflowRow, String> afterColumn;      // 適用後の工程順の版数
    
    /**
     * 工程の使用状況ダイアログを初期化する。
     * 
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.progress.setVisible(false);
 
        this.workflowList.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));
        this.workflowList.init("ApplyWorkflowDialog");

        // カラムの初期化
        this.checkBox = new CheckBox();
        this.checkBox.setOnAction((ActionEvent event) -> {
            if (checkBox.isSelected()) {
                this.workflowList.getItems().forEach(o -> { 
                    if (!o.isDisabled()) {
                        o.setSelected(Boolean.TRUE);
                    }
                });
            } else {
                this.workflowList.getItems().forEach(o -> {
                    if (!o.isDisabled()) {
                        o.setSelected(Boolean.FALSE);
                    }
                });
            }
        });

        workflowList.setRowFactory(tv -> {
            TableRow<ApplyWorkflowRow> row = new TableRow<>();
            // 工程順が最新版ではない場合、行を無効にする
            row.disableProperty().bind(Bindings.selectBoolean(row.itemProperty(), "disabled"));
            return row ;
        });

        this.selectedColumn.setGraphic(checkBox);
        this.selectedColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        this.selectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectedColumn));

        this.nameColumn.setCellValueFactory(new PropertyValueFactory<>("workflowName"));
        this.revColumn.setCellValueFactory(new PropertyValueFactory<>("workRev"));
        this.kanbanColumn.setCellValueFactory(new PropertyValueFactory<>("existKanban"));
        this.afterColumn.setCellValueFactory(new PropertyValueFactory<>("afterRev"));
    }    
    
    /**
     * パラメータを設定する。
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {
        this.work = (WorkInfoEntity) argument;
        
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(LocaleUtils.getString("applyWorkflowDesc1"), this.work.getWorkName() + ":" + this.work.getWorkRev()));
        sb.append(System.getProperty("line.separator"));
        sb.append(LocaleUtils.getString("applyWorkflowDesc2"));

        this.descLabel.setText(sb.toString());

        this.updateTable();
    }
    
    /**
     * Dialog を設定する。
     * 
     * @param dialog Dialog
     */
    @Override
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;

        Stage stage = (Stage) this.dialog.getDialogPane().getScene().getWindow();
        stage.setMinWidth(800.0);
        stage.setMinHeight(400.0);
        stage.heightProperty().addListener((observable, oldValue, newValue) -> this.stackPane.requestLayout());
        stage.widthProperty().addListener((observable, oldValue, newValue) -> this.stackPane.requestLayout());

        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(w -> onClose(null));
    }

    /**
     * 工程順に適用する。
     *
     * @param event
     */
    @FXML
    private void onApply(ActionEvent event) {
        try {
            logger.info("onApply start.");

            List<Long> ids = this.workflowList.getItems().stream()
                    .filter(o -> o.isSelected())
                    .map(o -> o.getWorkflow().getWorkflowId())
                    .collect(Collectors.toList());
            
            if (ids.isEmpty()) {
                // 工程順が選択されていません
                SceneContiner.getInstance().showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("noWorkflow"));
                return;
            }
            
            ResponseEntity responce = workflowFacade.updateWork(ids, this.work.getWorkId());
            if (responce.isSuccess()) {
                SceneContiner.getInstance().showAlert(Alert.AlertType.INFORMATION, LocaleUtils.getString("key.info"), LocaleUtils.getString("appliedWorkflow"));
                // リストを再更新
                this.updateTable();
            } else {
                SceneContiner.getInstance().showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.Error"), LocaleUtils.getString("key.FaildToProcess"));
            }
            
        } finally {
            logger.info("onApply end.");
        }
    }
    
    /**
     * ダイアログを閉じる。
     * 
     * @param event 
     */
    @FXML
    private void onClose(ActionEvent event) {
        try {
            logger.info("onClose start.");
                this.dialog.setResult(ButtonType.CANCEL);
                this.dialog.close();

        } finally {
            logger.info("onClose end.");
        }
    }

    /**
     * テーブルを更新する。
     */
    private void updateTable() {
        Platform.runLater(() -> this.blockUI(true));
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    List<WorkflowInfoEntity> workflows = workflowFacade.findByWorkName(work.getWorkName());
                    
                    rows.clear();

                    workflows.forEach(workflow -> {
                            Boolean existKanban = workflowFacade.existAssignedKanban(workflow.getWorkflowId());
                            WorkInfoEntity w = workflowFacade.findWork(workflow.getWorkflowId(), work.getWorkName());
                            rows.add(new ApplyWorkflowRow(workflow, w.getWorkRev(), work.getLatestRev(), existKanban));
                        });

                    Platform.runLater(() -> {
                        List<TableColumn<ApplyWorkflowRow, ?>> sortOrder = new ArrayList<>(workflowList.getSortOrder());
                        workflowList.getSortOrder().clear();
                        workflowList.getItems().clear();
                        workflowList.getItems().addAll(rows);
                        workflowList.getSortOrder().addAll(sortOrder);
                    });

                } finally {
                    Platform.runLater(() -> blockUI(false));
                }
                return null;
            }
        };
        new Thread(task).start();
    }
    
    /**
     * 操作を無効にする。
     *
     * @param block true: 無効化、false: 有効化
     */
    private void blockUI(boolean block) {
        SceneContiner.getInstance().blockUI("ContentNaviPane", block);
        this.progress.setVisible(block);
    }
}
