/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.component;

import adtekfuji.admanagerapp.workfloweditplugin.common.ScheduleRecordFactory;
import adtekfuji.admanagerapp.workfloweditplugin.entity.WorkSettingDialogEntity;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.DateUtils;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.schedule.ScheduleConditionInfoEntity;
import jp.adtekfuji.javafxcommon.controls.RestrictedTextField;
import jp.adtekfuji.javafxcommon.controls.TimeTextField;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程順関連付け設定ダイアログ
 *
 * @author ta.ito
 */
@FxComponent(id = "WorkflowAssociationSettingCompo", fxmlPath = "/fxml/compo/workflow_association_setting_compo.fxml")
public class WorkflowAssociationSettingCompoFxController implements Initializable, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final ObjectProperty<Date> startTimeProperty = new SimpleObjectProperty(DateUtils.min());
    private final ObjectProperty<Date> endTimeProperty = new SimpleObjectProperty(DateUtils.min());

    private WorkSettingDialogEntity inputData;

    //@FXML
    //private TimeHMTextField offsetTimeField;
    @FXML
    private CheckBox skipCheck;
    @FXML
    private ListView<EquipmentInfoEntity> equipmentsList;
    @FXML
    private ListView<OrganizationInfoEntity> organizationList;
    @FXML
    private Pane progressPane;
    @FXML
    private TimeTextField toPeriodsField;
    @FXML
    private TimeTextField fromPeriodsField;
    @FXML
    private RestrictedTextField standardDayField;
    @FXML
    private Label schedulePanLabel;
    @FXML
    private VBox schedulePain;

    /**
     * 工程設定ダイアログを初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    /**
     * 引数を設定する。
     * 
     * @param argument 
     */
    @Override
    public void setArgument(Object argument) {
        if (argument instanceof WorkSettingDialogEntity) {
            this.inputData = (WorkSettingDialogEntity) argument;

            // スキップ
            if (Objects.nonNull(this.inputData.getSkip())) {
                this.skipCheck.setSelected(this.inputData.getSkip());
            }

            this.skipCheck.setOnAction(event -> {
                if (this.skipCheck.isIndeterminate()) {
                    this.inputData.setSkip(null);
                } else {
                    this.inputData.setSkip(this.skipCheck.isSelected());
                }
            });

            // 設備
            Callback<ListView<EquipmentInfoEntity>, ListCell<EquipmentInfoEntity>> equipmentCellFactory = (ListView<EquipmentInfoEntity> param) -> new EquipmentListItemCell();
            this.equipmentsList.setCellFactory(equipmentCellFactory);
            if (Objects.nonNull(this.inputData.getEquipments())) {
                this.equipmentsList.getItems().addAll(this.inputData.getEquipments());
            }

            // 組織
            Callback<ListView<OrganizationInfoEntity>, ListCell<OrganizationInfoEntity>> organizationCellFactory = (ListView<OrganizationInfoEntity> param) -> new OrganizationListItemCell();
            this.organizationList.setCellFactory(organizationCellFactory);
            if (Objects.nonNull(this.inputData.getOrganizations())) {
                this.organizationList.getItems().addAll(this.inputData.getOrganizations());
            }

            if (this.inputData.isEditSingle()) {
                // 標準作業日
                if (Objects.nonNull(this.inputData.getStartTime())) {
                    this.standardDayField.setText(new SimpleDateFormat("D").format(this.inputData.getStartTime()));
                } else {
                    this.standardDayField.setText("1");
                }

                this.standardDayField.textProperty().addListener((observable, oldValue, newValue) -> {
                    int value = Integer.parseInt(newValue);
                    this.inputData.setStandardDay(value);
                });

                // 開始時間
                if (Objects.nonNull(this.inputData.getStartTime())) {
                    this.startTimeProperty.set(this.inputData.getStartTime());
                }
                this.fromPeriodsField.textProperty().bindBidirectional(this.startTimeProperty, new SimpleDateFormat("HH:mm:ss"));

                this.fromPeriodsField.textProperty().addListener((observable, oldValue, newValue) -> {
                    Date from = this.fromPeriodsField.getDate();
                    if (Objects.nonNull(from)) {
                        Date to = new Date(from.getTime() + this.inputData.getTaktTime());
                        this.inputData.setStartTime(from);
                        this.inputData.setEndTime(to);
                    }
                });

                // 終了時間
                if (Objects.nonNull(this.inputData.getEndTime())) {
                    this.endTimeProperty.set(this.inputData.getEndTime());
                }
                this.toPeriodsField.textProperty().bindBidirectional(this.endTimeProperty, new SimpleDateFormat("HH:mm:ss"));

                this.toPeriodsField.textProperty().addListener((observable, oldValue, newValue) -> {
                    Date to = this.toPeriodsField.getDate();
                    if (Objects.nonNull(to)) {
                        this.inputData.setEndTime(to);
                    }
                });
            }

            if (!this.inputData.isEditSingle() || inputData.isSeparatework()) {
                this.standardDayField.setDisable(true);
                this.fromPeriodsField.setDisable(true);
                this.toPeriodsField.setDisable(true);
                this.skipCheck.setAllowIndeterminate(true);
                this.skipCheck.setIndeterminate(true);
                this.schedulePain.setManaged(false);
                this.schedulePanLabel.setManaged(false);

            } else if (ClientServiceProperty.isLicensed("@ReporterOption")) {
                this.schedulePanLabel.setManaged(true);
                this.schedulePain.setManaged(true);
                this.schedulePain.getChildren().clear();
                Table<ScheduleConditionInfoEntity> table = new Table<>(this.schedulePain.getChildren());
                table.isColumnTitleRecord(true);
                table.isAddRecord(true);
                table.maxRecord(3);
                ScheduleRecordFactory scheduleRecordFactory = new ScheduleRecordFactory(table, this.inputData.getSchedule(), this::blockUI);
                table.setAbstractRecordFactory(scheduleRecordFactory);
            }
        }
    }

    /**
     * 設備選択ボタンを押下時の処理を実行する。
     * 
     * @param event 
     */
    @FXML
    public void onEditEquipment(ActionEvent event) {
        try {
            SelectDialogEntity param = new SelectDialogEntity();
            if (Objects.nonNull(this.inputData.getEquipments())) {
                param.equipments(this.inputData.getEquipments());
            }

            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Equipment"), "EquipmentSelectionCompo", param, (Stage) ((Node) event.getSource()).getScene().getWindow(), true);
            if (ret.equals(ButtonType.OK)) {
                this.inputData.setEquipments(param.getEquipments());
                this.equipmentsList.getItems().setAll(this.inputData.getEquipments());
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 組織選択ボタンを押下時の処理を実行する。
     * 
     * @param event 
     */
    @FXML
    public void onEditOrganization(ActionEvent event) {
        try {
            SelectDialogEntity param = new SelectDialogEntity();
            if (Objects.nonNull(this.inputData.getOrganizations())) {
                param.organizations(this.inputData.getOrganizations());
            }

            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Organization"), "OrganizationSelectionCompo", param, (Stage) ((Node) event.getSource()).getScene().getWindow(), true);
            if (ret.equals(ButtonType.OK)) {
                this.inputData.setOrganizations(param.getOrganizations());
                this.organizationList.getItems().setAll(this.inputData.getOrganizations());
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
     * 操作を禁止する。
     *
     * @param block
     */
    private void blockUI(boolean block) {
        this.progressPane.setVisible(block);
    }

}
