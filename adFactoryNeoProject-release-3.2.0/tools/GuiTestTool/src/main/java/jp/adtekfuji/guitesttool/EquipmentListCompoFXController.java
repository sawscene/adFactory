package jp.adtekfuji.guitesttool;

import adtekfuji.clientservice.EquipmentInfoFacade;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@FxComponent(id = "EquipmentListCompo", fxmlPath = "/fxml/equipment_list_compo.fxml")
public class EquipmentListCompoFXController implements Initializable {

    private final long MAX_LOAD_SIZE = 10;

    @FXML
    private Pane progressPane;
    @FXML
    private TableView<EquipmentInfoEntity> equipmentList;
    @FXML
    private TableColumn<EquipmentInfoEntity, Long> equipmentIdColumn;
    @FXML
    private TableColumn<EquipmentInfoEntity, Long> equipmentParentColumn;
    @FXML
    private TableColumn<EquipmentInfoEntity, String> equipmentNameColumn;
    @FXML
    private TableColumn<EquipmentInfoEntity, String> equipmentIdentColumn;

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final EquipmentInfoFacade equipmentFacade = new EquipmentInfoFacade();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        blockUI(true);

        equipmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("equipmentId"));
        equipmentParentColumn.setCellValueFactory(new PropertyValueFactory<>("parentId"));
        equipmentNameColumn.setCellValueFactory(new PropertyValueFactory<>("equipmentName"));
        equipmentIdentColumn.setCellValueFactory(new PropertyValueFactory<>("equipmentIdentify"));

        equipmentList.setOnMousePressed((MouseEvent event) -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                //工程カンバンリスト表示へ.
                EquipmentInfoEntity equipment = equipmentList.getSelectionModel().getSelectedItems().get(0);
                createEquipmentWorkDialog(equipment);
            }
        });

        //設備一覧表示.
        updateEquipment();
    }

    private void blockUI(Boolean flg) {
        Platform.runLater(() -> {
            sc.blockUI("EquipmentListCompo", flg);
            progressPane.setVisible(flg);
        });
    }

    @FXML
    private void onKeyPressed(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.F5)) {
            updateEquipment();
        }
    }

    private void updateEquipment() {
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    readEquipment();
                } finally {
                    blockUI(false);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void readEquipment() {
        ObservableList<EquipmentInfoEntity> table = FXCollections.observableArrayList();
        long max = equipmentFacade.count();
        for (long count = 0; count <= max; count += MAX_LOAD_SIZE) {
            List<EquipmentInfoEntity> organizatons = equipmentFacade.findRange(count, count + MAX_LOAD_SIZE - 1);
            table.addAll(organizatons);
            Platform.runLater(() -> {
                equipmentList.setItems(table);
                equipmentList.getSortOrder().add(equipmentIdColumn);
            });
        }
    }

    private void createEquipmentWorkDialog(EquipmentInfoEntity equipment) {
        sc.showModelessDialog("「" + equipment.getEquipmentName() + "」の工程カンバン一覧", "EquipmentWorkListCompo", equipment.getEquipmentId());
    }

}
