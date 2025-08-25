/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.reportoutplugin.component;

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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.stage.WindowEvent;
import jp.adtekfuji.adFactory.entity.view.ReportOutInfoEntity;
import jp.adtekfuji.javafxcommon.controls.TimeTextField;
import jp.adtekfuji.javafxcommon.validator.DateTimeValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 計画時間変更ダイアログ
 *
 * @author s-heya
 */
@FxComponent(id = "TimeChangeDialog", fxmlPath = "/fxml/admanagerreportoutplugin/time_change_dialog.fxml")
public class TimeChangeController implements Initializable, ArgumentDelivery, DialogHandler {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private final ObjectProperty<LocalDate> afterDateProperty;
    private final ObjectProperty<Date> afterTimeProperty;
    private ReportOutInfoEntity report;

    private Dialog dialog;

    @FXML
    private Label beforeTimeLabel;
    @FXML
    private DatePicker afterDatePicker;
    @FXML
    private TimeTextField afterTimeTextField;

    /**
     * コンストラクタ
     */
    public TimeChangeController() {
        this.afterDateProperty = new SimpleObjectProperty();
        this.afterTimeProperty = new SimpleObjectProperty();
    }

    /**
     * 実績時間の修正ダイアログを初期化する。
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    /**
     * パラメーターを設定する。
     * 
     * @param argument 
     */
    @Override
    public void setArgument(Object argument) {
        if (argument instanceof ReportOutInfoEntity) {
            this.report = (ReportOutInfoEntity) argument;
            
            SimpleDateFormat formatter = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));
            this.beforeTimeLabel.setText(formatter.format(report.getImplementDatetime()));
            this.afterDateProperty.set(DateUtils.toLocalDate(report.getImplementDatetime()));
            this.afterTimeProperty.set(report.getImplementDatetime());

            SimpleDateFormat timeFormat = new SimpleDateFormat(LocaleUtils.getString("key.TimeFormat"));
            this.afterDatePicker.valueProperty().bindBidirectional(this.afterDateProperty);
            DateTimeValidator.bindValidator(this.afterTimeTextField, this.afterTimeProperty, timeFormat);
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
            this.closeDialog();
        });
    }

    /**
     * OKボタンのアクション
     *
     * @param event 
     */
    @FXML
    private void onOk(ActionEvent event) {
        try {
            Date date = DateUtils.toDate(this.afterDateProperty.get(), DateUtils.toLocalTime(this.afterTimeProperty.get()));
            report.setImplementDatetime(date);

            this.dialog.setResult(ButtonType.OK);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ダイアログを閉じる。
     *
     * @param event 
     */
    @FXML
    private void onCancel(ActionEvent event) {
        this.closeDialog();
    }

    /**
     * キャンセル処理
     */
    private void closeDialog() {
        try {
            this.dialog.setResult(ButtonType.CANCEL);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
}
