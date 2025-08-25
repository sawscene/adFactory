/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.dialog;

import jp.adtekfuji.forfujiapp.dialog.entity.WorkSettingDialogEntity;
import jp.adtekfuji.forfujiapp.javafx.record.factory.WorkKanbanPropertyRecordFactory;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanPropertyInfoEntity;
import adtekfuji.cash.CashManager;
import adtekfuji.locale.LocaleUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程カンバン設定ダイアログ
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.11.17.Tsu
 */
@FxComponent(id = "WorkKanbanSettingDialog", fxmlPath = "/fxml/dialog/workkanbanSettingDialog.fxml")
public class WorkKanbanSettingDialog implements Initializable, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private WorkSettingDialogEntity<WorkKanbanPropertyInfoEntity> settingDialogEntity;
    private final CashManager cashManager = CashManager.getInstance();

    private static SimpleDateFormat sdf = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));

    private final static String DEFAULT_TAKTTIME = "00:00:00";
    private final static String DEFAULT_OFFSETTIME = "00:00:00";
    private final static String DATETIME_REGEX = "\\d|:|/|-|\\s";
    private final static String TAKTTIME_REGEX = "\\d|:";
    private final String DEFAULT_STARTTIME_OFFSETTIME = sdf.format(new Date());

    @FXML
    private TextField TactTimeTextField;
    @FXML
    private TextField OffsetTimeTextField;
    @FXML
    private ToggleGroup offsetSetting;
    @FXML
    private RadioButton radioOffset;
    @FXML
    private RadioButton radioStartTimeOffset;
    @FXML
    private CheckBox skipCheck;
    @FXML
    private ListView<EquipmentInfoEntity> equipmentsList;
    @FXML
    private ListView<OrganizationInfoEntity> organizationList;
    @FXML
    private VBox propertyPane;
    @FXML
    private Pane progressPane;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sdf = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));
        offsetSetting.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) -> {
            if (radioStartTimeOffset.isSelected()) {
                settingDialogEntity.setIsStartTimeOffset(Boolean.TRUE);
                OffsetTimeTextField.setText(DEFAULT_STARTTIME_OFFSETTIME);
            } else if (radioOffset.isSelected()) {
                settingDialogEntity.setIsStartTimeOffset(Boolean.FALSE);
                OffsetTimeTextField.setText(DEFAULT_OFFSETTIME);
            }
        });

        skipCheck.setOnAction((ActionEvent event) -> {
            if (skipCheck.isIndeterminate()) {
                settingDialogEntity.setSkip(null);
            } else {
                settingDialogEntity.setSkip(skipCheck.isSelected());
            }
        });

        blockUI(false);
    }

    @Override
    public void setArgument(Object argument) {
        if (argument instanceof WorkSettingDialogEntity) {
            settingDialogEntity = (WorkSettingDialogEntity) argument;

            TactTimeTextField.setText(DEFAULT_TAKTTIME);
            OffsetTimeTextField.setText(DEFAULT_OFFSETTIME);

            //TODO:編集情報が単一だった場合あらかじめデータ設定画面に表示する
            if (settingDialogEntity.getSeparateEditFlag()) {
                //スキップ状態を設定
                skipCheck.setSelected(settingDialogEntity.getSkip());

                List<EquipmentInfoEntity> equipments = cashManager.getItemList(EquipmentInfoEntity.class, new ArrayList());
                settingDialogEntity.getEquipmentIds().stream().forEach((eId) -> {
                    equipments.stream().filter((e) -> (Objects.equals(eId, e.getEquipmentId()))).forEach((e) -> {
                        equipmentsList.getItems().add(e);
                    });
                });
                settingDialogEntity.setEquipments(equipmentsList.getItems());

                List<OrganizationInfoEntity> organizations = cashManager.getItemList(OrganizationInfoEntity.class, new ArrayList());
                settingDialogEntity.getOrganizationIds().stream().forEach((oId) -> {
                    organizations.stream().filter((e) -> (Objects.equals(oId, e.getOrganizationId()))).forEach((e) -> {
                        organizationList.getItems().add(e);
                    });
                });
                settingDialogEntity.setOrganizations(organizationList.getItems());

                // カスタムフィールド表示
                propertyPane.getChildren().clear();
                Table table = new Table(propertyPane.getChildren()).isAddRecord(Boolean.TRUE)
                        .isColumnTitleRecord(Boolean.TRUE).title(LocaleUtils.getString("key.CustomField")).styleClass("ContentTitleLabel");
                settingDialogEntity.getPropertys().sort((entity1, entity2) -> {
                    if (Objects.isNull(entity1.getWorkKanbanPropOrder()) || Objects.isNull(entity2.getWorkKanbanPropOrder())) {
                        return 0;
                    }
                    return entity1.getWorkKanbanPropOrder().compareTo(entity2.getWorkKanbanPropOrder());
                });
                table.setAbstractRecordFactory(new WorkKanbanPropertyRecordFactory(table, settingDialogEntity.getPropertys()));
            } else {
                skipCheck.setAllowIndeterminate(true);
                skipCheck.setIndeterminate(true);
            }

            //画面のノードと設定エンティティのメンバをバインド
            TactTimeTextField.textProperty().bindBidirectional(settingDialogEntity.tactTimeProperty());
            TactTimeTextField.addEventFilter(KeyEvent.KEY_TYPED, (KeyEvent event) -> {
                if (!event.getCharacter().matches(TAKTTIME_REGEX)) {
                    event.consume();
                }
            });
            settingDialogEntity.offsetTimeProperty().bind(OffsetTimeTextField.textProperty());
            OffsetTimeTextField.addEventFilter(KeyEvent.KEY_TYPED, (KeyEvent event) -> {
                if (!event.getCharacter().matches(DATETIME_REGEX)) {
                    event.consume();
                }
            });

            Callback<ListView<EquipmentInfoEntity>, ListCell<EquipmentInfoEntity>> equipmentCellFactory = (ListView<EquipmentInfoEntity> param) -> new EquipmentListItemCell();
            equipmentsList.setCellFactory(equipmentCellFactory);
            Callback<ListView<OrganizationInfoEntity>, ListCell<OrganizationInfoEntity>> organizationCellFactory = (ListView<OrganizationInfoEntity> param) -> new OrganizationListItemCell();
            organizationList.setCellFactory(organizationCellFactory);
        }
    }

    /**
     * 設備設定用ダイアログ表示
     *
     */
    @FXML
    public void OnEditEquipment(ActionEvent event) {
        try {
            List<EquipmentInfoEntity> org = settingDialogEntity.getEquipments();
            SelectDialogEntity selectDialogEntity = new SelectDialogEntity().equipments(settingDialogEntity.getEquipments());
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Equipment"), "EquipmentSelectionCompo", selectDialogEntity);
            if (ret.equals(ButtonType.OK)) {
                settingDialogEntity.setEquipments(selectDialogEntity.getEquipments());
                settingDialogEntity.getEquipmentIds().clear();
                settingDialogEntity.getEquipments().stream().forEach((e) -> {
                    settingDialogEntity.getEquipmentIds().add(e.getEquipmentId());
                });
                settingDialogEntity.Update();
                Platform.runLater(() -> {
                    equipmentsList.getItems().clear();
                    equipmentsList.getItems().addAll(settingDialogEntity.getEquipments());
                });
            } else {
                settingDialogEntity.equipmentsProperty().get().clear();
                settingDialogEntity.equipmentsProperty().get().addAll(org);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 作業者設定用ダイアログ表示
     *
     */
    @FXML
    public void OnEditOrganization(ActionEvent event) {
        try {
            List<OrganizationInfoEntity> org = settingDialogEntity.getOrganizations();
            SelectDialogEntity selectDialogEntity = new SelectDialogEntity().organizations(settingDialogEntity.getOrganizations());
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Organization"), "OrganizationSelectionCompo", selectDialogEntity);
            if (ret.equals(ButtonType.OK)) {
                settingDialogEntity.setOrganizations(selectDialogEntity.getOrganizations());
                settingDialogEntity.getOrganizationIds().clear();
                settingDialogEntity.getOrganizations().stream().forEach((e) -> {
                    settingDialogEntity.getOrganizationIds().add(e.getOrganizationId());
                });

                settingDialogEntity.Update();
                Platform.runLater(() -> {
                    organizationList.getItems().clear();
                    organizationList.getItems().addAll(settingDialogEntity.getOrganizations());
                });
            } else {
                settingDialogEntity.organizationsProperty().get().clear();
                settingDialogEntity.organizationsProperty().get().addAll(org);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ListView表示用セル
     *
     */
    class EquipmentListItemCell extends ListCell<EquipmentInfoEntity> {

        @Override
        protected void updateItem(EquipmentInfoEntity item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setText(item.getEquipmentName());
            } else {
                setText("");
            }
        }
    }

    /**
     * ListView表示用セル
     *
     */
    class OrganizationListItemCell extends ListCell<OrganizationInfoEntity> {

        @Override
        protected void updateItem(OrganizationInfoEntity item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setText(item.getOrganizationName());
            } else {
                setText("");
            }
        }
    }

    /**
     * UIロック
     *
     * @param flg
     */
    private void blockUI(Boolean flg) {
        progressPane.setVisible(flg);
    }

}
