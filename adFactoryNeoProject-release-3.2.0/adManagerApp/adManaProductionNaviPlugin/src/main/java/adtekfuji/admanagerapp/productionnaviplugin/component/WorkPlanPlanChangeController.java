/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.component;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.DateUtils;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.WindowEvent;
import jp.adtekfuji.adFactory.entity.kanban.PlanChangeCondition;
import jp.adtekfuji.javafxcommon.controls.TimeTextField;
import jp.adtekfuji.javafxcommon.validator.DateTimeValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author (TST)min
 * @version 2.0.0
 * @since 2018/09/28
 */
@FxComponent(id = "WorkPlanPlanChangeDialog", fxmlPath = "/fxml/admanagerapp/workplanplugin/work_plan_plan_change_dialog.fxml")
public class WorkPlanPlanChangeController implements Initializable, ArgumentDelivery, DialogHandler{
    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();

    private final ObjectProperty<LocalDate> startDateProperty;
    private final ObjectProperty<Date> startTimeProperty;
    private final ObjectProperty<Date> interruptFromTimeProperty;
    private final ObjectProperty<Date> interruptToTimeProperty;

    private PlanChangeCondition condition;

    private Dialog dialog;

    @FXML
    private HBox startDatePane;
    @FXML
    private CheckBox startDateCheckBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private TextField startTimeTextField;
    @FXML
    private CheckBox interruptTimeCheckBox;
    @FXML
    private HBox interruptTimePane;
    @FXML
    private TimeTextField interruptFromTextField;
    @FXML
    private TimeTextField interruptToTextField;

    /**
     * コンストラクタ
     */
    public WorkPlanPlanChangeController() {
        this.startDateProperty = new SimpleObjectProperty();
        this.startTimeProperty = new SimpleObjectProperty();
        this.interruptFromTimeProperty = new SimpleObjectProperty();
        this.interruptToTimeProperty = new SimpleObjectProperty();
    }

    /**
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 開始時間のチェックボックスで入力欄の無効状態を制御する。
        this.startDatePane.setDisable(!this.startDateCheckBox.isSelected());
        this.startDateCheckBox.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            this.startDatePane.setDisable(!newValue);
        });

        // 中断時間のチェックボックスで入力欄の無効状態を制御する。
        this.interruptTimePane.setDisable(!this.interruptTimeCheckBox.isSelected());
        this.interruptTimeCheckBox.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            this.interruptTimePane.setDisable(!newValue);
        });
    }

    /**
     * 
     * @param argument 
     */
    @Override
    public void setArgument(Object argument) {
        if (argument instanceof PlanChangeCondition) {
            this.condition = (PlanChangeCondition) argument;

            this.startDateCheckBox.setSelected(true);
            this.interruptTimeCheckBox.setSelected(false);

            this.startDateProperty.set(DateUtils.toLocalDate(this.condition.getStartDatetime()));
            this.startTimeProperty.set(this.condition.getStartDatetime());
            this.interruptFromTimeProperty.set(this.condition.getInterruptFromTime());
            this.interruptToTimeProperty.set(this.condition.getInterruptToTime());

            SimpleDateFormat timeFormat = new SimpleDateFormat(LocaleUtils.getString("key.TimeFormat"));

            // 開始時間
            this.startDatePicker.valueProperty().bindBidirectional(this.startDateProperty);
            DateTimeValidator.bindValidator(this.startTimeTextField, this.startTimeProperty, timeFormat);

            // 中断時間
            DateTimeValidator.bindValidator(this.interruptFromTextField, this.interruptFromTimeProperty, timeFormat);
            DateTimeValidator.bindValidator(this.interruptToTextField, this.interruptToTimeProperty, timeFormat);
        }
    }

    /**
     * 
     * @param dialog 
     */
    @Override
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest((WindowEvent we) -> {
            this.cancelDialog();
        });
    }

    /**
     * OKボタンのアクション
     *
     * @param event 
     */
    @FXML
    private void onOkButton(ActionEvent event) {
        try {
            // 計画時間の変更条件を設定する。
            if (!this.setCondition()) {
                return;
            }

            this.dialog.setResult(ButtonType.OK);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * キャンセルボタンのアクション
     *
     * @param event 
     */
    @FXML
    private void onCancelButton(ActionEvent event) {
        this.cancelDialog();
    }

    /**
     * キャンセル処理
     */
    private void cancelDialog() {
        try {
            this.dialog.setResult(ButtonType.CANCEL);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 計画時間の変更条件を設定する。
     */
    private boolean setCondition() {
        // 開始時間 (チェックOFFの場合は null)
        Date startDatetime = null;
        if (this.startDateCheckBox.isSelected()) {
            startDatetime = DateUtils.toDate(this.startDateProperty.get(), DateUtils.toLocalTime(this.startTimeProperty.get()));
        }

        // 中断時間 (チェックOFFの場合は null)
        Date interruptFromTime = null;
        Date interruptToTime = null;
        if (this.interruptTimeCheckBox.isSelected()) {
            interruptFromTime = DateUtils.toDate(this.startDateProperty.get(), DateUtils.toLocalTime(this.interruptFromTimeProperty.get()));
            interruptToTime = DateUtils.toDate(this.startDateProperty.get(), DateUtils.toLocalTime(this.interruptToTimeProperty.get()));

            if (interruptFromTime.after(interruptToTime)) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.ChangePlans"), LocaleUtils.getString("key.DateCompErrMessage3"));
                this.interruptFromTextField.requestFocus();
                return false;
            }
        }

        this.condition.setStartDatetime(startDatetime);
        this.condition.setInterruptFromTime(interruptFromTime);
        this.condition.setInterruptToTime(interruptToTime);

        return true;
    }
}
