/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.andonsetting.component;

import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.clientservice.EquipmentInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.search.EquipmentSearchCondition;
import jp.adtekfuji.adFactory.enumerate.EquipmentTypeEnum;

/**
 * 進捗モニタ設定インポートダイアログのコントローラ
 *
 * @author s-maeda
 */
@FxComponent(id = "ImportMonitorSelectionCompo", fxmlPath = "/fxml/compo/import_monitor_selection_compo.fxml")
public class ImportMonitorSelectionCompoController implements Initializable, ArgumentDelivery {

    @FXML
    private StackPane stackPane;
    @FXML
    private Pane progressPane;
    @FXML
    private ListView<EquipmentInfoEntity> monitorList;

    private final List<EquipmentInfoEntity> monitors = new ArrayList<>();
    private EquipmentInfoEntity selectedEntity;

    private final EquipmentInfoFacade equipmentInfoFacade = new EquipmentInfoFacade();
    private final long MAX_LOAD_SIZE = ClientServiceProperty.getRestRangeNum();

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        this.blockUI(true);

        // リストの表示名設定
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

        // リスト選択時処理
        monitorList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends EquipmentInfoEntity> observable, EquipmentInfoEntity oldValue, EquipmentInfoEntity newValue) -> {
            selectedEntity.setEquipmentId(newValue.getEquipmentId());
        });

        // 初期表示処理
        Platform.runLater(() -> {
            try {
                EquipmentSearchCondition condition = new EquipmentSearchCondition();
                condition.setEquipmentType(EquipmentTypeEnum.MONITOR);

                Long max = equipmentInfoFacade.countSearch(condition);
                for (long count = 0; count <= max; count += MAX_LOAD_SIZE) {
                    monitors.addAll(equipmentInfoFacade.findSearchRange(condition, count, count + MAX_LOAD_SIZE - 1));
                }

                SortedList<EquipmentInfoEntity> sortedList = new SortedList<>(FXCollections.observableArrayList(monitors));
                sortedList.setComparator((EquipmentInfoEntity o1, EquipmentInfoEntity o2) -> o1.getEquipmentName().compareTo(o2.getEquipmentName()));
                monitorList.setItems(sortedList);

            } finally {
                blockUI(false);
            }
        });
    }

    /**
     * 引数取り込み
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {
        if (Objects.nonNull(argument) && (argument instanceof EquipmentInfoEntity)) {
            this.selectedEntity = (EquipmentInfoEntity) argument;
        }
    }

    /**
     * UIロック
     *
     * @param flg
     */
    private void blockUI(Boolean flg) {
        this.stackPane.setDisable(flg);
        this.progressPane.setVisible(flg);
    }

}
