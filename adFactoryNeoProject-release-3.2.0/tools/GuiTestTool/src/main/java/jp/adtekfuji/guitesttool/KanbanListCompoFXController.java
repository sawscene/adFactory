package jp.adtekfuji.guitesttool;

import adtekfuji.clientservice.KanbanInfoFacade;
import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.utility.DateUtils;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@FxComponent(id = "KanbanListCompo", fxmlPath = "/fxml/kanban_list_compo.fxml")
public class KanbanListCompoFXController implements Initializable {

    private final long MAX_LOAD_SIZE = 10;

    @FXML
    private Pane progressPane;
    @FXML
    private ComboBox<WorkflowInfoEntity> workflowComboBox;
    @FXML
    private DatePicker planStartDatePicker;
    @FXML
    private DatePicker planEndDatePicker;
    @FXML
    private TableView<KanbanInfoEntity> kanbanListView;
    @FXML
    private TableColumn<KanbanInfoEntity, Long> kanbanIdColumn;
    @FXML
    private TableColumn<KanbanInfoEntity, String> kanbanNameColumn;
    @FXML
    private TableColumn<KanbanInfoEntity, String> kanbanSubNameColumn;
    @FXML
    private TableColumn<KanbanInfoEntity, Long> workflowIdColumn;
    @FXML
    private TableColumn<KanbanInfoEntity, String> workflowNameColumn;
    @FXML
    private TableColumn<KanbanInfoEntity, KanbanStatusEnum> statusColumn;
    @FXML
    private TableColumn<KanbanInfoEntity, String> planStartColumn;
    @FXML
    private TableColumn<KanbanInfoEntity, String> planEndColumn;
    @FXML
    private TableColumn<KanbanInfoEntity, String> interruptColumn;
    @FXML
    private TableColumn<KanbanInfoEntity, String> delayColumn;

    class WorkflowCell extends ListCell<WorkflowInfoEntity> {

        @Override
        protected void updateItem(WorkflowInfoEntity workflow, boolean empty) {
            super.updateItem(workflow, empty);
            if (Objects.nonNull(workflow)) {
                String rev = (Objects.isNull(workflow.getWorkflowRevision()) || workflow.getWorkflowRevision().equals("")) ? "" : "(" + workflow.getWorkflowRevision() + ")";
                setText(Objects.isNull(workflow.getWorkflowName()) ? "" : workflow.getWorkflowName() + rev);
            }
        }
    }

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final KanbanInfoFacade kanbanInfoFacade = new KanbanInfoFacade();
    private final WorkflowInfoFacade workflowInfoFacade = new WorkflowInfoFacade();
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private final Map<Long, WorkflowInfoEntity> workflowCollection = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        blockUI(true);
        workflowComboBox.setButtonCell(new WorkflowCell());
        workflowComboBox.setCellFactory((ListView<WorkflowInfoEntity> param) -> new WorkflowCell());
        planStartDatePicker.setValue(LocalDate.now());
        planEndDatePicker.setValue(LocalDate.now());

        kanbanIdColumn.setCellValueFactory(new PropertyValueFactory<>("kanbanId"));
        kanbanNameColumn.setCellValueFactory(new PropertyValueFactory<>("kanbanName"));
        kanbanSubNameColumn.setCellValueFactory(new PropertyValueFactory<>("kanbanSubname"));
        workflowIdColumn.setCellValueFactory(new PropertyValueFactory<>("fkWorkflowId"));
        workflowNameColumn.setCellValueFactory(new PropertyValueFactory<>("workflowName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("kanbanStatus"));
        statusColumn.setCellFactory(ComboBoxTableCell.<KanbanInfoEntity, KanbanStatusEnum>forTableColumn(FXCollections.observableArrayList(KanbanStatusEnum.values())));
        statusColumn.setOnEditCommit((TableColumn.CellEditEvent<KanbanInfoEntity, KanbanStatusEnum> event) -> {
            //カンバン情報更新へ.
            KanbanInfoEntity kanban = (event.getTableView().getItems().get(event.getTablePosition().getRow()));
            writeKanbanData(kanban.getKanbanId(), event.getNewValue());
        });
        planStartColumn.setCellValueFactory((TableColumn.CellDataFeatures<KanbanInfoEntity, String> param) -> Bindings.createStringBinding(() -> formatter.format(param.getValue().getStartDatetime())));
        planEndColumn.setCellValueFactory((TableColumn.CellDataFeatures<KanbanInfoEntity, String> param) -> Bindings.createStringBinding(() -> formatter.format(param.getValue().getCompDatetime())));
        //interruptColumn.setCellValueFactory(new PropertyValueFactory<>("fkInterruptReasonId"));
        //delayColumn.setCellValueFactory(new PropertyValueFactory<>("fkDelayReasonId"));

        kanbanListView.setOnMousePressed((MouseEvent event) -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                //工程カンバンリスト表示へ.
                KanbanInfoEntity kanban = kanbanListView.getSelectionModel().getSelectedItems().get(0);
                createWorkKanbanDialog(kanban);
            }
        });

        //工程順の読み込みと表示.
        updateWorkflowView();
    }

    private void blockUI(Boolean flg) {
        Platform.runLater(() -> {
            sc.blockUI("MainScenePane", flg);
            progressPane.setVisible(flg);
        });
    }

    @FXML
    private void onKeyPressed(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.F5)) {
            updateAll();
        }
    }

    @FXML
    private void onSearchAction(ActionEvent event) {
        updateKanbanView();
    }

    private void updateAll() {
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    readWorkflowThread();
                    readKanbanThread();
                } finally {
                    blockUI(false);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void updateWorkflowView() {
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    readWorkflowThread();
                } finally {
                    blockUI(false);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void readWorkflowThread() {
        ObservableList<WorkflowInfoEntity> table = FXCollections.observableArrayList();
        table.add(new WorkflowInfoEntity());
        long max = workflowInfoFacade.getWorkflowCount();
        for (long count = 0; count <= max; count += MAX_LOAD_SIZE) {
            List<WorkflowInfoEntity> workflows = workflowInfoFacade.getWorkflowRange(count, count + MAX_LOAD_SIZE - 1);
            for (WorkflowInfoEntity workflow : workflows) {
                workflowCollection.put(workflow.getWorkflowId(), workflow);
            }
            table.addAll(workflows);
        }
        Platform.runLater(() -> {
            workflowComboBox.setItems(table);
            workflowComboBox.getSelectionModel().select(0);
        });
    }

    private void updateKanbanView() {
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    readKanbanThread();
                } finally {
                    blockUI(false);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void readKanbanThread() {
        Long workflowId = workflowComboBox.getSelectionModel().getSelectedItem().getWorkflowId();
        Date startDay = Objects.isNull(planStartDatePicker.getValue()) ? null : DateUtils.getBeginningOfDate(planStartDatePicker.getValue());
        Date endDay = Objects.isNull(planEndDatePicker.getValue()) ? null : DateUtils.getEndOfDate(planEndDatePicker.getValue());
        KanbanSearchCondition condition = new KanbanSearchCondition().workflowId(workflowId).fromDate(startDay).toDate(endDay);

        ObservableList<KanbanInfoEntity> table = FXCollections.observableArrayList();
        long max = kanbanInfoFacade.countSearch(condition);
        for (long count = 0; count <= max; count += MAX_LOAD_SIZE) {
            List<KanbanInfoEntity> kanbans = kanbanInfoFacade.findSearchRange(condition, count, count + MAX_LOAD_SIZE - 1);
            for (KanbanInfoEntity kanban : kanbans) {
                kanban.setWorkflowName(workflowCollection.get(kanban.getFkWorkflowId()).getWorkflowName());
                //addPropertyColumn(kanban.getPropertyCollection());
            }
            table.addAll(kanbans);
            Platform.runLater(() -> {
                kanbanListView.setItems(table);
                kanbanListView.getSortOrder().add(kanbanIdColumn);
            });
        }
    }

    private Map<String, TableColumn<KanbanInfoEntity, String>> propColumns = new HashMap<>();

    private void addPropertyColumn(List<KanbanPropertyInfoEntity> props) {
        Platform.runLater(() -> {
            int loop = 0;
            for (KanbanPropertyInfoEntity prop : props) {
                final int index = loop;
                TableColumn<KanbanInfoEntity, String> propColumn = null;
                if (!propColumns.containsKey(prop.getKanbanPropertyName())) {
                    propColumn = new TableColumn<>(prop.getKanbanPropertyName());
                    kanbanListView.getColumns().add(propColumn);
                } else {
                    propColumn = propColumns.get(prop.getKanbanPropertyName());
                }
                propColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<KanbanInfoEntity, String>, ObservableValue<String>>() {

                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<KanbanInfoEntity, String> param) {
                        return param.getValue().getPropertyValue(param.getTableColumn().getCellData(index));
                    }
                });
                loop++;
            }
        });
    }

    private void writeKanbanData(Long kanbanId, KanbanStatusEnum status) {
        logger.info("update:{}->{}", kanbanId, status);
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    writeKanbanThread(kanbanId, status);
                    readKanbanThread();
                } finally {
                    blockUI(false);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void writeKanbanThread(Long kanbanId, KanbanStatusEnum status) {
        try {
            KanbanInfoEntity kanban = kanbanInfoFacade.find(kanbanId);
            kanban.setKanbanStatus(status);
            ResponseEntity response = kanbanInfoFacade.update(kanban);
            logger.info("response:{}", response);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    private void createWorkKanbanDialog(KanbanInfoEntity kanban) {
        sc.showModelessDialog("「" + kanban.getKanbanName() + "」のカンバン詳細", "WorkKanbanListCompo", kanban.getKanbanId());
    }

}
