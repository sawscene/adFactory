package jp.adtekfuji.guitesttool;

import adtekfuji.clientservice.ActualResultInfoFacade;
import adtekfuji.clientservice.KanbanInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.search.ActualSearchCondition;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@FxComponent(id = "WorkKanbanListCompo", fxmlPath = "/fxml/work_kanban_list_compo.fxml")
public class WorkKanbanListCompoFXController implements Initializable, ArgumentDelivery {

    @FXML
    private TextField kanbanIdField;
    @FXML
    private TextField kanbanNameField;
    @FXML
    private TextField kanbanSubnameField;
    @FXML
    private ComboBox<KanbanStatusEnum> kanbanStatusCombo;
    @FXML
    private TextField workflowIdField;
    @FXML
    private TextField workflowNameField;
    @FXML
    private TextField kanbanStartDatetimeField;
    @FXML
    private TextField kanbanEndDatetimeField;
    @FXML
    private TableView<WorkKanbanInfoEntity> workKanbanList;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, Long> workKanbanIdColumn1;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, Long> workIdColumn1;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, String> workNameColumn1;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, KanbanStatusEnum> workStatusColumn1;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, Boolean> workImpFlgColumn1;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, Boolean> workSkipFlgColumn1;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, String> workOrganizationColumn1;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, String> workEquipmentColumn1;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, Long> workTaktTimeColumn1;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, Long> workSumTimeColumn1;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, String> workStartDatetimeColumn1;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, String> workEndDatetimeColumn1;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, Integer> workOrderColumn1;
    @FXML
    private TableView<WorkKanbanInfoEntity> separateWorkList;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, Long> workKanbanIdColumn2;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, Long> workIdColumn2;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, String> workNameColumn2;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, KanbanStatusEnum> workStatusColumn2;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, Boolean> workImpFlgColumn2;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, Boolean> workSkipFlgColumn2;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, String> workOrganizationColumn2;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, String> workEquipmentColumn2;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, Long> workTaktTimeColumn2;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, Long> workSumTimeColumn2;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, String> workStartDatetimeColumn2;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, String> workEndDatetimeColumn2;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, Integer> workOrderColumn2;
    @FXML
    private TableView<ActualResultEntity> actualList;
    @FXML
    private TableColumn<ActualResultEntity, Long> actualIdColumn;
    @FXML
    private TableColumn<ActualResultEntity, String> actualTimeColumn;
    @FXML
    private TableColumn<ActualResultEntity, KanbanStatusEnum> actualStatusColumn;
    @FXML
    private TableColumn<ActualResultEntity, Long> actualKanbanIdColumn;
    @FXML
    private TableColumn<ActualResultEntity, String> actualKanbanNameColumn;
    @FXML
    private TableColumn<ActualResultEntity, Long> actualWorkflowIdColumn;
    @FXML
    private TableColumn<ActualResultEntity, String> actualWorkflowNameColumn;
    @FXML
    private TableColumn<ActualResultEntity, Long> actualWorkKanbanIdColumn;
    @FXML
    private TableColumn<ActualResultEntity, Long> actualWorkIdColumn;
    @FXML
    private TableColumn<ActualResultEntity, String> actualWorkNameColumn;
    @FXML
    private TableColumn<ActualResultEntity, String> actualEquipmentColumn;
    @FXML
    private TableColumn<ActualResultEntity, String> actualOrganizationColumn;
    @FXML
    private TableColumn<ActualResultEntity, Integer> actualWorkTimeColumn;
    @FXML
    private TableColumn<ActualResultEntity, String> actualInterruptColumn;
    @FXML
    private TableColumn<ActualResultEntity, String> actualDelayColumn;
    @FXML
    private Pane progressPane;

    private final long MAX_LOAD_SIZE = 10;
    private final Logger logger = LogManager.getLogger();
    private final static ResourceBundle rb = ResourceBundle.getBundle("locale.locale");
    private final SceneContiner sc = SceneContiner.getInstance();
    private final KanbanInfoFacade kanbanInfoFacade = new KanbanInfoFacade();
    private final ActualResultInfoFacade actualResultFacade = new ActualResultInfoFacade();
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private KanbanInfoEntity selectedKanban = null;

    class KanbanStatusEnumComboBoxCellFactory extends ListCell<KanbanStatusEnum> {

        @Override
        protected void updateItem(KanbanStatusEnum item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else {
                setText(rb.getString(item.getResourceKey()));
            }
        }
    }
    Callback<ListView<KanbanStatusEnum>, ListCell<KanbanStatusEnum>> comboCellFactory = (ListView<KanbanStatusEnum> param) -> new KanbanStatusEnumComboBoxCellFactory();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        blockUI(false);

        kanbanStatusCombo.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends KanbanStatusEnum> observable, KanbanStatusEnum oldValue, KanbanStatusEnum newValue) -> {
            if (Objects.nonNull(oldValue) && Objects.nonNull(newValue) && oldValue != newValue) {
                writeKanbanData(selectedKanban.getKanbanId(), newValue);
            }
        });
        //工程リスト
        workKanbanIdColumn1.setCellValueFactory(new PropertyValueFactory<>("workKanbanId"));
        workIdColumn1.setCellValueFactory(new PropertyValueFactory<>("fkWorkId"));
        workNameColumn1.setCellValueFactory(new PropertyValueFactory<>("workName"));
        workStatusColumn1.setCellValueFactory(new PropertyValueFactory<>("workStatus"));
        workStatusColumn1.setCellValueFactory(new PropertyValueFactory<>("workStatus"));
        workStatusColumn1.setCellFactory(ComboBoxTableCell.<WorkKanbanInfoEntity, KanbanStatusEnum>forTableColumn(FXCollections.observableArrayList(KanbanStatusEnum.values())));
        workStatusColumn1.setOnEditCommit((TableColumn.CellEditEvent<WorkKanbanInfoEntity, KanbanStatusEnum> event) -> {
            //実績通知.
            WorkKanbanInfoEntity workKanban = (event.getTableView().getItems().get(event.getTablePosition().getRow()));
            actualThread(selectedKanban.getKanbanId(), workKanban, event.getNewValue());
        });
        workImpFlgColumn1.setCellValueFactory(new PropertyValueFactory<>("implementFlag"));
        workSkipFlgColumn1.setCellValueFactory(new PropertyValueFactory<>("skipFlag"));
        workTaktTimeColumn1.setCellValueFactory(new PropertyValueFactory<>("taktTime"));
        workSumTimeColumn1.setCellValueFactory(new PropertyValueFactory<>("sumTimes"));
        workStartDatetimeColumn1.setCellValueFactory((TableColumn.CellDataFeatures<WorkKanbanInfoEntity, String> param) -> Bindings.createStringBinding(() -> formatter.format(param.getValue().getStartDatetime())));
        workEndDatetimeColumn1.setCellValueFactory((TableColumn.CellDataFeatures<WorkKanbanInfoEntity, String> param) -> Bindings.createStringBinding(() -> formatter.format(param.getValue().getCompDatetime())));
        workOrderColumn1.setCellValueFactory(new PropertyValueFactory<>("workKanbanOrder"));
        workOrganizationColumn1.setCellValueFactory((TableColumn.CellDataFeatures<WorkKanbanInfoEntity, String> param) -> {
            String text = "";
            for (Long id : param.getValue().getOrganizationCollection()) {
                text += id + ",";
            }
            return new SimpleStringProperty(text);
        });
        workEquipmentColumn1.setCellValueFactory((TableColumn.CellDataFeatures<WorkKanbanInfoEntity, String> param) -> {
            String text = "";
            for (Long id : param.getValue().getEquipmentCollection()) {
                text += id + ",";
            }
            return new SimpleStringProperty(text);
        });

        //バラ工程リスト
        workKanbanIdColumn2.setCellValueFactory(new PropertyValueFactory<>("workKanbanId"));
        workIdColumn2.setCellValueFactory(new PropertyValueFactory<>("fkWorkId"));
        workNameColumn2.setCellValueFactory(new PropertyValueFactory<>("workName"));
        workStatusColumn2.setCellValueFactory(new PropertyValueFactory<>("workStatus"));
        workStatusColumn2.setCellValueFactory(new PropertyValueFactory<>("workStatus"));
        workStatusColumn2.setCellFactory(ComboBoxTableCell.<WorkKanbanInfoEntity, KanbanStatusEnum>forTableColumn(FXCollections.observableArrayList(KanbanStatusEnum.values())));
        workStatusColumn2.setOnEditCommit((TableColumn.CellEditEvent<WorkKanbanInfoEntity, KanbanStatusEnum> event) -> {
            //実績通知.
            WorkKanbanInfoEntity workKanban = (event.getTableView().getItems().get(event.getTablePosition().getRow()));
            actualThread(selectedKanban.getKanbanId(), workKanban, event.getNewValue());
        });
        workImpFlgColumn2.setCellValueFactory(new PropertyValueFactory<>("implementFlag"));
        workSkipFlgColumn2.setCellValueFactory(new PropertyValueFactory<>("skipFlag"));
        workTaktTimeColumn2.setCellValueFactory(new PropertyValueFactory<>("taktTime"));
        workSumTimeColumn2.setCellValueFactory(new PropertyValueFactory<>("sumTimes"));
        workStartDatetimeColumn2.setCellValueFactory((TableColumn.CellDataFeatures<WorkKanbanInfoEntity, String> param) -> Bindings.createStringBinding(() -> formatter.format(param.getValue().getStartDatetime())));
        workEndDatetimeColumn2.setCellValueFactory((TableColumn.CellDataFeatures<WorkKanbanInfoEntity, String> param) -> Bindings.createStringBinding(() -> formatter.format(param.getValue().getCompDatetime())));
        workOrderColumn2.setCellValueFactory(new PropertyValueFactory<>("workKanbanOrder"));
        workOrganizationColumn2.setCellValueFactory((TableColumn.CellDataFeatures<WorkKanbanInfoEntity, String> param) -> {
            String text = "";
            for (Long id : param.getValue().getOrganizationCollection()) {
                text += id + ",";
            }
            return new SimpleStringProperty(text);
        });
        workEquipmentColumn2.setCellValueFactory((TableColumn.CellDataFeatures<WorkKanbanInfoEntity, String> param) -> {
            String text = "";
            for (Long id : param.getValue().getEquipmentCollection()) {
                text += id + ",";
            }
            return new SimpleStringProperty(text);
        });
        
        //実績リスト
        actualIdColumn.setCellValueFactory(new PropertyValueFactory<>("actualId"));
        actualTimeColumn.setCellValueFactory((TableColumn.CellDataFeatures<ActualResultEntity, String> param) -> Bindings.createStringBinding(() -> formatter.format(param.getValue().getImplementDatetime())));
        actualStatusColumn.setCellValueFactory(new PropertyValueFactory<>("actualStatus"));
        actualKanbanIdColumn.setCellValueFactory(new PropertyValueFactory<>("fkKanbanId"));
        actualKanbanNameColumn.setCellValueFactory(new PropertyValueFactory<>("kanbanName"));
        actualWorkflowIdColumn.setCellValueFactory(new PropertyValueFactory<>("fkWorkflowId"));
        actualWorkflowNameColumn.setCellValueFactory(new PropertyValueFactory<>("workflowName"));
        actualWorkKanbanIdColumn.setCellValueFactory(new PropertyValueFactory<>("fkWorkKanbanId"));
        actualWorkIdColumn.setCellValueFactory(new PropertyValueFactory<>("fkWorkId"));
        actualWorkNameColumn.setCellValueFactory(new PropertyValueFactory<>("workName"));
        actualEquipmentColumn.setCellValueFactory(new PropertyValueFactory<>("equipmentName"));
        actualOrganizationColumn.setCellValueFactory(new PropertyValueFactory<>("organizationName"));
        actualWorkTimeColumn.setCellValueFactory(new PropertyValueFactory<>("workingTime"));
        actualInterruptColumn.setCellValueFactory(new PropertyValueFactory<>("interruptReason"));
        actualDelayColumn.setCellValueFactory(new PropertyValueFactory<>("delayReason"));
    }

    @Override
    public void setArgument(Object argument) {
        if (argument instanceof Long) {
            updateView((Long) argument);
        } else {
            blockUI(false);
        }
    }

    private void blockUI(Boolean flg) {
        Platform.runLater(() -> {
            sc.blockUI("WorkKanbanListCompo", flg);
            progressPane.setVisible(flg);
        });
    }

    @FXML
    private void onKeyPressed(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.F5)) {
            updateView(selectedKanban.getKanbanId());
        }
    }

    private void updateView(Long kanbanId) {
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    readWorkKanbanThread(kanbanId);
                } finally {
                    blockUI(false);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void readWorkKanbanThread(Long kanbanId) {
        selectedKanban = kanbanInfoFacade.find(kanbanId);

        Platform.runLater(() -> {
            kanbanIdField.setText(selectedKanban.getKanbanId().toString());
            kanbanNameField.setText(selectedKanban.getKanbanName());
            kanbanSubnameField.setText(selectedKanban.getKanbanSubname());
            kanbanStatusCombo.setItems(FXCollections.observableArrayList(KanbanStatusEnum.values()));
            kanbanStatusCombo.getSelectionModel().select(selectedKanban.getKanbanStatus());
            kanbanStatusCombo.setButtonCell(new KanbanStatusEnumComboBoxCellFactory());
            kanbanStatusCombo.setCellFactory(comboCellFactory);
            kanbanStatusCombo.setEditable(false);
            workflowIdField.setText(selectedKanban.getFkWorkflowId().toString());
            workflowNameField.setText(selectedKanban.getWorkflowName());
            kanbanStartDatetimeField.setText(formatter.format(selectedKanban.getStartDatetime()));
            kanbanEndDatetimeField.setText(formatter.format(selectedKanban.getCompDatetime()));
            //工程カンバン一覧
            ObservableList<WorkKanbanInfoEntity> table1 = FXCollections.observableArrayList();
            table1.addAll(selectedKanban.getWorkKanbanCollection());
            workKanbanList.setItems(table1);
            workKanbanList.getSortOrder().add(workOrderColumn1);
            //バラ工程カンバン一覧
            ObservableList<WorkKanbanInfoEntity> table2 = FXCollections.observableArrayList();
            table2.addAll(selectedKanban.getSeparateworkKanbanCollection());
            separateWorkList.setItems(table2);
            separateWorkList.getSortOrder().add(workOrderColumn2);
            //実績一覧
            ObservableList<ActualResultEntity> table3 = FXCollections.observableArrayList();
            ActualSearchCondition actualCondition = new ActualSearchCondition().kanbanId(kanbanId);
            long max = actualResultFacade.searchCount(actualCondition);
            for (long count = 0; count <= max; count += MAX_LOAD_SIZE) {
                table3.addAll(actualResultFacade.searchRange(actualCondition, count, count + MAX_LOAD_SIZE - 1));
                actualList.setItems(table3);
                actualList.getSortOrder().add(actualIdColumn);
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
                    KanbanInfoEntity kanban = kanbanInfoFacade.find(kanbanId);
                    kanban.setKanbanStatus(status);
                    ResponseEntity response = kanbanInfoFacade.update(kanban);
                    logger.info("response:{}", response);
                    readWorkKanbanThread(kanbanId);
                } finally {
                    blockUI(false);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void actualThread(Long kanbanId, WorkKanbanInfoEntity workKanban, KanbanStatusEnum status) {
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    ReportingActualResult.getInstance().report(kanbanId, workKanban.getWorkKanbanId(), status, null, null);
                    readWorkKanbanThread(kanbanId);
                } finally {
                    blockUI(false);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

}
