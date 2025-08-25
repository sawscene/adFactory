/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.property;

import java.util.*;

import adtekfuji.locale.LocaleUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.object.ObjectInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkPropertyInfoEntity;
import jp.adtekfuji.adFactory.enumerate.WorkPropertyCategoryEnum;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.CellInterface;

/**
 * トレーサビリティ設定コンボボックスセル
 *
 * @author s-heya
 */
public class CellTraceabilityComboBox extends AbstractCell {

    private final ComboBox<String> comboBox = new ComboBox<>();
    private final WorkPropertyInfoEntity workProperty;
    private final ObservableList<ObjectInfoEntity> useParts;
    private final List<EquipmentInfoEntity> manufactureEquipments;
    private final List<EquipmentInfoEntity> measureEquipments;
    private final ResourceBundle rb;
    private final Map<String, String> timeStampCategory = new TreeMap<String, String>(){{
        put(LocaleUtils.getString("key.NowDatetime"), "NOW");
        put(LocaleUtils.getString("notExist"), "NONE");
    }};
    /**
     * 項目の有効/無効状態(true：無効、false：有効)
     */
    private final boolean isDisabled;

    /**
     * コンストラクタ
     *
     * @param abstractCellInterface
     * @param workProperty
     * @param useParts
     * @param manufactureEquipments
     * @param measureEquipments
     * @param isDisabled 項目の有効/無効状態(true：無効、false：有効)
     * @param rb
     */
    public CellTraceabilityComboBox(CellInterface abstractCellInterface, WorkPropertyInfoEntity workProperty, ObservableList<ObjectInfoEntity> useParts, List<EquipmentInfoEntity> manufactureEquipments, List<EquipmentInfoEntity> measureEquipments, ResourceBundle rb, boolean isDisabled) {
        super(abstractCellInterface);
        this.workProperty = workProperty;
        this.useParts = useParts;
        this.manufactureEquipments = manufactureEquipments;
        this.measureEquipments = measureEquipments;
        this.rb = rb;
        this.isDisabled = isDisabled;
    }

    /**
     * ノードを生成する
     */
    @Override
    public void createNode() {

        this.updateItems();

        this.comboBox.setPrefWidth(200.0);

        if (WorkPropertyCategoryEnum.TIMESTAMP.equals(this.workProperty.getWorkPropCategory())) {
            String target = this.workProperty.getWorkPropValue();
            String item
                    = this.timeStampCategory
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().equals(target))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(timeStampCategory.keySet().iterator().next());
            this.comboBox.getSelectionModel().select(item);
        } else {
            this.comboBox.getSelectionModel().select(this.workProperty.getWorkPropValue());
        }

        this.comboBox.setCellFactory(v -> new ListCell<String>() {
            @Override
            public void updateItem(String newValue, boolean empty) {
                super.updateItem(newValue, empty);

                if (empty) {
                    setText("");
                } else if (Objects.equals("", newValue)) {
                    setText(getDescriptionString());
                } else {
                    setText(newValue);
                }
            }
        });

        this.comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (WorkPropertyCategoryEnum.TIMESTAMP.equals(this.workProperty.getWorkPropCategory())) {
                this.workProperty.workPropValueProperty().setValue(timeStampCategory.get(newValue));
            } else {
                this.workProperty.workPropValueProperty().setValue(newValue);
            }
        });

        this.workProperty.workPropCategoryProperty().addListener(new ChangeListener<WorkPropertyCategoryEnum>() {
            @Override
            public void changed(ObservableValue<? extends WorkPropertyCategoryEnum> observable, WorkPropertyCategoryEnum oldValue, WorkPropertyCategoryEnum newValue) {
                updateItems();
                if (WorkPropertyCategoryEnum.TIMESTAMP.equals(newValue)) {
                    comboBox.valueProperty().set(timeStampCategory.keySet().iterator().next());
                } else {
                    comboBox.valueProperty().set("");
                }
            }
        });

        this.useParts.addListener(new ListChangeListener<ObjectInfoEntity>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends ObjectInfoEntity> changed) {
                if (WorkPropertyCategoryEnum.PARTS == workProperty.getWorkPropCategory()) {
                    updateItems();
                    comboBox.valueProperty().set("");
                }
            }
        });

        if (this.isDisabled) {
            this.setDisable(true);
        } else {
            // commonのsetNodeのdisable設定が自分自身であるため、初期設定内容を自分自身に反映
            this.setDisable(this.comboBox.isDisable());
        }
        super.setNode(this.comboBox);
    }

    /**
     *
     * @return
     */
    private String getDescriptionString() {
        if (Objects.isNull(this.workProperty.getWorkPropCategory())) {
            return "";
        }

        switch (this.workProperty.getWorkPropCategory()) {
            case PARTS:
                return "<" + LocaleUtils.getString("key.UseParts") + ">";
            case WORK:
            case INSPECTION:
                return "<" + LocaleUtils.getString("key.UseEquipment") + ">";
            case MEASURE:
                return "<" + LocaleUtils.getString("key.UseEquipment") + ">";
            default:
                return "";
        }
    }

    /**
     * 選択項目を更新する
     */
    private void updateItems() {
        this.comboBox.getItems().clear();
        this.comboBox.setEditable(false);
        this.comboBox.setDisable(false);

        if (Objects.nonNull(this.workProperty.getWorkPropCategory())) {
            switch (this.workProperty.getWorkPropCategory()) {
                case PARTS:
                    this.comboBox.getItems().add("");
                    for (ObjectInfoEntity object : this.useParts) {
                        this.comboBox.getItems().add(object.getObjectId());
                    }
                    break;
                case WORK:
                case INSPECTION:
                    this.comboBox.getItems().add("");
                    for (EquipmentInfoEntity equipment : this.manufactureEquipments) {
                        this.comboBox.getItems().add(equipment.getEquipmentIdentify());
                    }
                    break;
                case MEASURE:
                    this.comboBox.getItems().add("");
                    for (EquipmentInfoEntity equipment : this.measureEquipments) {
                        this.comboBox.getItems().add(equipment.getEquipmentIdentify());
                    }
                    break;
                case CUSTOM:
                case TIMER:
                case LIST:
                    this.comboBox.setEditable(true);
                    break;
                case TIMESTAMP:
                    this.comboBox.getItems().addAll(timeStampCategory.keySet());
                    break;
                case PRODUCT:// 完成品種別は選択項目は無効
                default:
                    this.comboBox.setDisable(true);
                    break;
            }
        }
    }
}
