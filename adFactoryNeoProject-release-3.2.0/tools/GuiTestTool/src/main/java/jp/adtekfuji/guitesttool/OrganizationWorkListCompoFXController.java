package jp.adtekfuji.guitesttool;

import adtekfuji.clientservice.ActualResultInfoFacade;
import adtekfuji.clientservice.OrganizationInfoFacade;
import adtekfuji.clientservice.WorkKanbanInfoFacade;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.search.ActualSearchCondition;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@FxComponent(id = "OrganizationWorkListCompo", fxmlPath = "/fxml/organization_work_list_compo.fxml")
public class OrganizationWorkListCompoFXController implements Initializable, ArgumentDelivery {

    @FXML
    private TextField organizationIdField;
    @FXML
    private TextField organizationNameField;
    @FXML
    private TextField organizationIdentField;

    @FXML
    private TableView<WorkKanbanInfoEntity> workKanbanList;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, Long> kanbanIdColumn1;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, String> kanbanNameColumn1;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, Long> workflowIdColumn1;
    @FXML
    private TableColumn<WorkKanbanInfoEntity, String> workflowNameColumn1;
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
    private final ContextMenu contextMenu = new ContextMenu();

    private final long MAX_LOAD_SIZE = 10;
    private final Logger logger = LogManager.getLogger();
    private final static ResourceBundle rb = ResourceBundle.getBundle("locale.locale");
    private final SceneContiner sc = SceneContiner.getInstance();
    private final OrganizationInfoFacade organizationFacade = new OrganizationInfoFacade();
    private final WorkKanbanInfoFacade workKanbanFacade = new WorkKanbanInfoFacade();
    private final ActualResultInfoFacade actualResultFacade = new ActualResultInfoFacade();
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private OrganizationInfoEntity selectOrganization = null;

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

        //工程カンバンリスト
        kanbanIdColumn1.setCellValueFactory(new PropertyValueFactory<>("fkKanbanId"));
        kanbanNameColumn1.setCellValueFactory(new PropertyValueFactory<>("kanbanName"));
        workflowIdColumn1.setCellValueFactory(new PropertyValueFactory<>("fkWorkflowId"));
        workflowNameColumn1.setCellValueFactory(new PropertyValueFactory<>("workflowName"));
        workKanbanIdColumn1.setCellValueFactory(new PropertyValueFactory<>("workKanbanId"));
        workIdColumn1.setCellValueFactory(new PropertyValueFactory<>("fkWorkId"));
        workNameColumn1.setCellValueFactory(new PropertyValueFactory<>("workName"));
        workStatusColumn1.setCellValueFactory(new PropertyValueFactory<>("workStatus"));
        workStatusColumn1.setCellValueFactory(new PropertyValueFactory<>("workStatus"));
        workStatusColumn1.setCellFactory(ComboBoxTableCell.<WorkKanbanInfoEntity, KanbanStatusEnum>forTableColumn(FXCollections.observableArrayList(KanbanStatusEnum.values())));
        workStatusColumn1.setOnEditCommit((TableColumn.CellEditEvent<WorkKanbanInfoEntity, KanbanStatusEnum> event) -> {
            //実績通知.
            WorkKanbanInfoEntity workKanban = (event.getTableView().getItems().get(event.getTablePosition().getRow()));
            actualThread(workKanban.getFkKanbanId(), workKanban, event.getNewValue());
        });
        workImpFlgColumn1.setCellValueFactory(new PropertyValueFactory<>("implementFlag"));
        workSkipFlgColumn1.setCellValueFactory(new PropertyValueFactory<>("skipFlag"));
        workTaktTimeColumn1.setCellValueFactory(new PropertyValueFactory<>("taktTime"));
        workSumTimeColumn1.setCellValueFactory(new PropertyValueFactory<>("sumTimes"));
        workStartDatetimeColumn1.setCellValueFactory((TableColumn.CellDataFeatures<WorkKanbanInfoEntity, String> param) -> Bindings.createStringBinding(() -> formatter.format(param.getValue().getStartDatetime())));
        workEndDatetimeColumn1.setCellValueFactory((TableColumn.CellDataFeatures<WorkKanbanInfoEntity, String> param) -> Bindings.createStringBinding(() -> formatter.format(param.getValue().getCompDatetime())));
        workEquipmentColumn1.setCellValueFactory((TableColumn.CellDataFeatures<WorkKanbanInfoEntity, String> param) -> {
            String text = "";
            if (Objects.nonNull(param.getValue().getEquipmentCollection())) {
                for (Long id : param.getValue().getEquipmentCollection()) {
                    text += id + ",";
                }
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

        MenuItem menu1 = new MenuItem(KanbanStatusEnum.PLANNING.name());
        menu1.setOnAction((ActionEvent event) -> {
            changeStatus(KanbanStatusEnum.PLANNING);
        });
        MenuItem menu2 = new MenuItem(KanbanStatusEnum.PLANNED.name());
        menu2.setOnAction((ActionEvent event) -> {
            changeStatus(KanbanStatusEnum.PLANNED);
        });
        MenuItem menu3 = new MenuItem(KanbanStatusEnum.WORKING.name());
        menu3.setOnAction((ActionEvent event) -> {
            changeStatus(KanbanStatusEnum.WORKING);
        });
        MenuItem menu4 = new MenuItem(KanbanStatusEnum.COMPLETION.name());
        menu4.setOnAction((ActionEvent event) -> {
            changeStatus(KanbanStatusEnum.COMPLETION);
        });
        MenuItem menu5 = new MenuItem(KanbanStatusEnum.SUSPEND.name());
        menu5.setOnAction((ActionEvent event) -> {
            changeStatus(KanbanStatusEnum.SUSPEND);
        });
        MenuItem menu6 = new MenuItem(KanbanStatusEnum.INTERRUPT.name());
        menu6.setOnAction((ActionEvent event) -> {
            changeStatus(KanbanStatusEnum.INTERRUPT);
        });
        contextMenu.getItems().addAll(menu1, menu2, menu3, menu4, menu5, menu6);
        workKanbanList.setContextMenu(contextMenu);
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
            sc.blockUI("OrganizationWorkListCompo", flg);
            progressPane.setVisible(flg);
        });
    }

    @FXML
    private void onKeyPressed(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.F5)) {
            updateView(selectOrganization.getOrganizationId());
        }
    }

    private void updateView(Long organizaitonId) {
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    readOrganizationThread(organizaitonId);
                } finally {
                    blockUI(false);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void readOrganizationThread(Long organizaitonId) {
        selectOrganization = organizationFacade.find(organizaitonId);

        Platform.runLater(() -> {
            organizationIdField.setText(selectOrganization.getOrganizationId().toString());
            organizationNameField.setText(selectOrganization.getOrganizationName());
            organizationIdentField.setText(selectOrganization.getOrganizationIdentify());

            //工程カンバン一覧
            ObservableList<WorkKanbanInfoEntity> table1 = FXCollections.observableArrayList();
            KanbanSearchCondition kanbanCondition = new KanbanSearchCondition().organizationList(Arrays.asList(organizaitonId)).organizationIdWithParent(true)
                    .implementFlag(true).skipFlag(false).statusList(Arrays.asList(KanbanStatusEnum.PLANNED, KanbanStatusEnum.WORKING, KanbanStatusEnum.SUSPEND))
                    .parentStatusList(Arrays.asList(KanbanStatusEnum.PLANNED, KanbanStatusEnum.WORKING, KanbanStatusEnum.SUSPEND));
            long max = workKanbanFacade.countSearch(kanbanCondition);
            for (long count = 0; count <= max; count += MAX_LOAD_SIZE) {
                table1.addAll(workKanbanFacade.findSearchRange(kanbanCondition, count, count + MAX_LOAD_SIZE - 1));
                workKanbanList.setItems(table1);
                workKanbanList.getSortOrder().add(workStartDatetimeColumn1);
            }
            //実績一覧
            ObservableList<ActualResultEntity> table2 = FXCollections.observableArrayList();
            ActualSearchCondition actualCondition = new ActualSearchCondition().organizationList(Arrays.asList(organizaitonId));
            max = actualResultFacade.searchCount(actualCondition);
            for (long count = 0; count <= max; count += MAX_LOAD_SIZE) {
                table2.addAll(actualResultFacade.searchRange(actualCondition, count, count + MAX_LOAD_SIZE - 1));
                actualList.setItems(table2);
                actualList.getSortOrder().add(actualIdColumn);
            }
        });
    }

    private void changeStatus(KanbanStatusEnum status) {
        WorkKanbanInfoEntity workKanban = workKanbanList.getSelectionModel().getSelectedItem();
        actualThread(workKanban.getFkKanbanId(), workKanban, status);
    }

    private void actualThread(Long kanbanId, WorkKanbanInfoEntity workKanban, KanbanStatusEnum status) {
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    ReportingActualResult.getInstance().report(kanbanId, workKanban.getWorkKanbanId(), status, null, selectOrganization.getOrganizationId());
                    readOrganizationThread(selectOrganization.getOrganizationId());
                } finally {
                    blockUI(false);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

}
