/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.component;

import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanWorkGroupPropertyDataRecordFactory;
import adtekfuji.admanagerapp.productionnaviplugin.utils.WorkPlanKanbanDefaultOffsetData;
import adtekfuji.admanagerapp.productionnaviplugin.utils.WorkPlanWorkGroupPropertyData;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.validator.DateTimeValidator;
import jp.adtekfuji.javafxcommon.validator.NumericValidator;

/**
 * カンバン生成方法設定ダイアログ
 *
 * @author (TST)min
 * @version 2.0.0
 * @since 2018/09/28
 */
@FxComponent(id = "WorkPlanWorkKanbanStarttimeOffsetCompo", fxmlPath = "/fxml/compo/work_plan_work_kanban_starttime_offset_compo.fxml")
public class WorkPlanWorkKanbanStarttimeOffsetCompoController implements Initializable, ArgumentDelivery, DialogHandler {

    public class NumberStringConverter extends StringConverter<Number> {
        // NumberFormat formatter = numberFormatInteger;

        @Override
        public String toString(Number value) {
            return  "Total: " + String.valueOf(value);
        }

        @Override
        public Number fromString(String text) {
            //try {
            //    return formatter.parse(text);
            //} catch (ParseException e) {
            //    throw new RuntimeException( e);
            //}
            return null;
        }
    }

    private SimpleDateFormat TO_CONVERT_TIME_PATTERN = new SimpleDateFormat(LocaleUtils.getString("key.TimeFormat"));
    private SimpleDateFormat TO_CONVERT_DATE_PATTERN = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));
    private WorkPlanKanbanDefaultOffsetData defaultOffsetData;
    private Dialog dialog;

    @FXML
    private TextField offsetTimeTextFeild;
    @FXML
    private CheckBox checkOffsetWorkingHours;
    @FXML
    private GridPane WorkingHoursPane;
    @FXML
    private TextField openingTimeTextFeild;
    @FXML
    private TextField closingTimeTextFeild;
    @FXML
    private CheckBox checkLotProduction;
    @FXML
    private GridPane LotProductionPane;
    @FXML
    private CheckBox checkOnePieceFlow;
    @FXML
    private TextField lotQuantityField;
    @FXML
    private VBox workGroupPane;
    @FXML
    private Label totalLabel;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
      TO_CONVERT_TIME_PATTERN = new SimpleDateFormat(LocaleUtils.getString("key.TimeFormat"));
      TO_CONVERT_DATE_PATTERN = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));
      
    }

    /**
     * 初期処理
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {
        if (argument instanceof WorkPlanKanbanDefaultOffsetData) {
            this.defaultOffsetData = (WorkPlanKanbanDefaultOffsetData) argument;
            // 基本作業開始時間
            createOffsetTimeTextFeild();
            // 就業時間
            createWorkingHoursForm();
            // ロット生産
            createLotProductionForm();
        }
    }

    /**
     * Dialogを設定する。
     *
     * @param dialog
     */
    @Override
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> dialog.close());
    }

    /**
     * 基本作業開始時間の入力フォーム生成
     *
     */
    private void createOffsetTimeTextFeild() {
        // 基本作業開始時間の入力フォームバリデーション設定
        DateTimeValidator.bindValidator(offsetTimeTextFeild, defaultOffsetData.startOffsetTimeProperty(), TO_CONVERT_DATE_PATTERN);
    }

    /**
     * 稼働時間の設定フォームの生成
     *
     */
    private void createWorkingHoursForm() {
        // 稼働時間の設定有効無効のイベント設定
        checkOffsetWorkingHours.setSelected(defaultOffsetData.getCheckOffsetWorkingHours());
        WorkingHoursPane.setDisable(!defaultOffsetData.getCheckOffsetWorkingHours());

        checkOffsetWorkingHours.selectedProperty().bindBidirectional(defaultOffsetData.checkOffsetWorkingHoursProperty());
        checkOffsetWorkingHours.setOnAction((ActionEvent event) -> {
            if (checkOffsetWorkingHours.isSelected()) {
                WorkingHoursPane.setDisable(false);
            } else {
                WorkingHoursPane.setDisable(true);
            }
        });
        // 始業時間の入力フォームバリデーション設定
        DateTimeValidator.bindValidator(openingTimeTextFeild, defaultOffsetData.openingTimeProperty(), TO_CONVERT_TIME_PATTERN);
        // 終業時間の入力フォームバリデーション設定
        DateTimeValidator.bindValidator(closingTimeTextFeild, defaultOffsetData.closingTimeProperty(), TO_CONVERT_TIME_PATTERN);
    }

    /**
     * ロット生産の設定フォームの生成
     *
     */
    private void createLotProductionForm() {
        // 稼働時間の設定有効無効のイベント設定
        checkLotProduction.setSelected(defaultOffsetData.getCheckLotProduction());
        LotProductionPane.setDisable(!defaultOffsetData.getCheckLotProduction());
        checkLotProduction.selectedProperty().bindBidirectional(defaultOffsetData.checkLotProductionProperty());
        checkLotProduction.setOnAction((ActionEvent event) -> {
            if (checkLotProduction.isSelected()) {
                LotProductionPane.setDisable(false);
                if (checkOnePieceFlow.isSelected()) {
                    workGroupPane.setDisable(true);
                } else {
                    workGroupPane.setDisable(false);
                }
            } else {
                LotProductionPane.setDisable(true);
            }
        });

        // 稼働時間の設定有効無効のイベント設定
        checkOnePieceFlow.selectedProperty().bindBidirectional(defaultOffsetData.checkOnePieceFlowProperty());
        checkOnePieceFlow.setOnAction((ActionEvent event) -> {
            if (checkOnePieceFlow.isSelected()) {
                workGroupPane.setDisable(true);
            } else {
                workGroupPane.setDisable(false);
            }
        });

        checkOnePieceFlow.setSelected(defaultOffsetData.getCheckOnePieceFlow());
        workGroupPane.setDisable(defaultOffsetData.getCheckOnePieceFlow());
        if (!Objects.nonNull(defaultOffsetData.getWorkGroupProps())) {
            defaultOffsetData.setWorkGroupProps(new LinkedList<>());
        }
        NumericValidator.bindValidator(lotQuantityField, defaultOffsetData.lotQuantityProperty()).addRange(1, 300);

        workGroupPane.getChildren().clear();
        Table workGroupTable = new Table(workGroupPane.getChildren()).isAddRecord(true).isColumnTitleRecord(true);
        workGroupTable.setAbstractRecordFactory(new WorkPlanWorkGroupPropertyDataRecordFactory(workGroupTable, defaultOffsetData.getWorkGroupProps(), defaultOffsetData.startOffsetTimeProperty(), defaultOffsetData.sumProperty()));
        
        Bindings.bindBidirectional(totalLabel.textProperty(), defaultOffsetData.sumProperty(), new WorkPlanWorkKanbanStarttimeOffsetCompoController.NumberStringConverter());
    }

    /**
     * 入力データのチェックをおこなう。
     *
     * @return
     */
    private Boolean check() {
        // ロット生産を行う場合の確認処理
        if (defaultOffsetData.getCheckLotProduction()) {
            if (!defaultOffsetData.getCheckOnePieceFlow()) {
                int total = 0;
                for (WorkPlanWorkGroupPropertyData data : defaultOffsetData.getWorkGroupProps()) {
                    boolean error = false;

                    int value = data.getQauntity().intValue();
                    if (0 >= value) {
                        SceneContiner sc = SceneContiner.getInstance();
                        sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.warn.inputRequired"));
                        error = true;
                    }

                    if (!error && Objects.isNull(data.getStartTime())) {
                        error = true;
                    }

                    if (error) {
                        SceneContiner sc = SceneContiner.getInstance();
                        sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.warn.inputRequired"));
                        return false;
                    }

                    total += value;
                }

                if (total != defaultOffsetData.getLotQuantity()) {
                    SceneContiner sc = SceneContiner.getInstance();
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.warn.productionNum"));
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * OKボタンのアクションを実行する。
     *
     * @param event
     */
    @FXML
    private void onOk(ActionEvent event) {
        if (this.check()) {
            this.dialog.setResult(ButtonType.OK);
            this.dialog.close();
        }
    }
}
