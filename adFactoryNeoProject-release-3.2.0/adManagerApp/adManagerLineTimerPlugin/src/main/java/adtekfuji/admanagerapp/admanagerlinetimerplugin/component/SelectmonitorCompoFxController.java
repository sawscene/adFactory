/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.admanagerlinetimerplugin.component;

import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.clientservice.EquipmentInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.search.EquipmentSearchCondition;
import jp.adtekfuji.adFactory.enumerate.EquipmentTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author ke.yokoi
 */
@FxComponent(id = "SelectMonitorCompo", fxmlPath = "/fxml/compo/select_monitor_compo.fxml")
public class SelectmonitorCompoFxController implements Initializable, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final EquipmentInfoFacade equipmentInfoFacade = new EquipmentInfoFacade();
    private final long MAX_LOAD_SIZE = ClientServiceProperty.getRestRangeNum();
    private ObjectProperty<EquipmentInfoEntity> equipmentProperty = null;

    @FXML
    private ListView<EquipmentInfoEntity> monitorList;
    @FXML
    private Pane progressPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        blockUI(true);
        monitorList.setCellFactory((ListView<EquipmentInfoEntity> param) -> {
            ListCell<EquipmentInfoEntity> cell = new ListCell<EquipmentInfoEntity>() {
                @Override
                protected void updateItem(EquipmentInfoEntity e, boolean bln) {
                    super.updateItem(e, bln);
                    if (e != null) {
                        setText(e.getEquipmentName() + "(" + e.getEquipmentIdentify() + ")");
                    }
                }
            };
            return cell;
        });
        monitorList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends EquipmentInfoEntity> observable, EquipmentInfoEntity oldValue, EquipmentInfoEntity newValue) -> {
            equipmentProperty.set(newValue);
        });
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                readMonitorList();
                return null;
            }
        };
        new Thread(task).start();
    }

    @Override
    public void setArgument(Object argument) {
        equipmentProperty = (ObjectProperty<EquipmentInfoEntity>) argument;
    }

    private void blockUI(Boolean flg) {
        Platform.runLater(() -> {
            progressPane.setVisible(flg);
        });
    }

    private void readMonitorList() {
        logger.info("readMonitorList");
        try {
            //アンドンモニタ設備一覧取得.
            List<EquipmentInfoEntity> monitors = new ArrayList<>();
            EquipmentSearchCondition condition = new EquipmentSearchCondition().equipmentType(EquipmentTypeEnum.MONITOR);
            long max = equipmentInfoFacade.countSearch(condition);
            for (long count = 0; count <= max; count += MAX_LOAD_SIZE) {
                monitors.addAll(equipmentInfoFacade.findSearchRange(condition, count, count + MAX_LOAD_SIZE - 1));
            }
            Platform.runLater(() -> {
                SortedList<EquipmentInfoEntity> sortedList = new SortedList<>(FXCollections.observableArrayList(monitors));
                sortedList.setComparator((EquipmentInfoEntity o1, EquipmentInfoEntity o2) -> o1.getEquipmentName().compareTo(o2.getEquipmentName()));
                monitorList.setItems(sortedList);
            });
        } finally {
            blockUI(false);
        }
    }

}
