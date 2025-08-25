/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.equipmenteditplugin.component;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.DateUtils;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import jp.adtekfuji.javafxcommon.validator.DateTimeValidator;

/**
 * 校正実施ダイアログのコントローラー
 *
 * @author fu-kato
 */
@FxComponent(id = "CalibrationCompo", fxmlPath = "/fxml/compo/calibration_compo.fxml")
public class CalibrationCompoController implements ArgumentDelivery {

    @FXML
    Label valueLabel;
    @FXML
    DatePicker datePicker;
    @FXML
    private TextField timeTextField;

    private final ObjectProperty<Date> timeProperty = new SimpleObjectProperty();

    /**
     * 呼出側で設定した前回校正日から当日までをカレンダーで選択させ選択した日付を返す
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {
        
        Date now = new Date();
        
        final Map<String, Object> dateMap = (Map<String, Object>) argument;

        datePicker.setValue((LocalDate) dateMap.get("today"));
        datePicker.valueProperty().addListener((ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) -> {
            dateMap.put("localDate", newValue);
        });

        // 前回校正日より前日もしくは当日より後日 は選択不可
        datePicker.setDayCellFactory((final DatePicker datePicker1) -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = (LocalDate) dateMap.get("today");
                LocalDate prevDate = (LocalDate) dateMap.get("prevDate");
                if ((Objects.nonNull(prevDate) && (date.isBefore(prevDate)))
                        || (Objects.nonNull(today) && date.isAfter(today))) {
                    setDisable(true);
                    setStyle("-fx-background-color: LightGrey;");
                }
            }
        });

        this.timeProperty.set(Date.from(LocalDateTime.of((LocalDate)dateMap.get("localDate"), (LocalTime)dateMap.get("localTime")).atZone(ZoneId.systemDefault()).toInstant()));
        SimpleDateFormat format = new SimpleDateFormat(LocaleUtils.getString("key.TimeFormat"));
        DateTimeValidator.bindValidator(this.timeTextField, this.timeProperty, format);
        
        this.timeProperty.addListener((observable, oldValue, newValue) -> {
            dateMap.put("localTime", DateUtils.toLocalTime(newValue));
        });
    }

}
