/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.chartplugin.controller;

import adtekfuji.admanagerapp.chartplugin.common.Constants;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 日付ペイン
 *
 * @author fu-kato
 */
public class DatePane implements Initializable {

    private final Logger logger = LogManager.getLogger();

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private DatePicker toDatePicker;

    /**
     * 開始年月日取得(時間は0:00固定)
     *
     * @return
     */
    public Date getFrom() {
        return getFrom(0, 0);
    }

    /**
     * 終了年月日取得(時間は23:59固定)
     *
     * @return
     */
    public Date getTo() {
        return getTo(23, 59);
    }

    /**
     * 開始年月日取得
     *
     * @param hour
     * @param minute
     * @return
     */
    public Date getFrom(int hour, int minute) {
        LocalDateTime fromLocalDate = getLocalFrom(
                hour,
                minute);
        Date fromDate = Date.from(fromLocalDate.atZone(ZoneId.systemDefault()).toInstant());
        return fromDate;
    }

    /**
     * 終了年月日取得
     *
     * @param hour
     * @param minute
     * @return
     */
    public Date getTo(int hour, int minute) {
        LocalDateTime toLocalDate = getLocalTo(
                hour,
                minute);
        Date toDate = Date.from(toLocalDate.atZone(ZoneId.systemDefault()).toInstant());
        return toDate;
    }

    /**
     * ローカル終了年月日取得
     *
     * @param hour
     * @param minute
     * @return
     */
    public LocalDateTime getLocalTo(int hour, int minute) {
        return LocalDateTime.of(
                this.toDatePicker.getValue().getYear(),
                this.toDatePicker.getValue().getMonth(),
                this.toDatePicker.getValue().getDayOfMonth(),
                hour,
                minute);
    }

    /**
     * ローカル開始年月日取得
     *
     * @param hour
     * @param minute
     * @return
     */
    public LocalDateTime getLocalFrom(int hour, int minute) {
        return LocalDateTime.of(
                this.fromDatePicker.getValue().getYear(),
                this.fromDatePicker.getValue().getMonth(),
                this.fromDatePicker.getValue().getDayOfMonth(),
                hour,
                minute);
    }

    /**
     * 日付ペインの初期化
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.fromDatePicker.setValue(LocalDate.now());
        this.toDatePicker.setValue(LocalDate.now());

        // 終了日より後日は選択不可
        this.fromDatePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        if (Objects.nonNull(toDatePicker.getValue()) && date.isAfter(toDatePicker.getValue())) {
                            setDisable(true);
                            setStyle("-fx-background-color: LightGrey;");
                        }
                    }
                };
            }
        });

        // 開始日より前日は選択不可
        this.toDatePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        if (Objects.nonNull(fromDatePicker.getValue()) && date.isBefore(fromDatePicker.getValue())) {
                            setDisable(true);
                            setStyle("-fx-background-color: LightGrey;");
                        }
                    }
                };
            }
        });
    }

    /**
     * プロパティを読み込む。
     *
     * @param properties
     */
    public void loadProperties(Properties properties) {
        String value;

        // 対象日
        value = properties.getProperty(Constants.TIMELINE_FROM_DATE);
        if (!StringUtils.isEmpty(value)) {
            LocalDate fromDate = LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            this.fromDatePicker.setValue(fromDate);
        }

        value = properties.getProperty(Constants.TIMELINE_TO_DATE);
        if (!StringUtils.isEmpty(value)) {
            LocalDate toDate = LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            this.toDatePicker.setValue(toDate);
        }
    }

    /**
     * プロパティを保存する。
     *
     * @param properties
     */
    public void saveProperties(Properties properties) {
        String from = Objects.isNull(this.fromDatePicker.getValue()) ? "" : DateTimeFormatter.ofPattern("yyyy/MM/dd").format(this.fromDatePicker.getValue());
        String to = Objects.isNull(this.toDatePicker.getValue()) ? "" : DateTimeFormatter.ofPattern("yyyy/MM/dd").format(this.toDatePicker.getValue());
        properties.setProperty(Constants.TIMELINE_FROM_DATE, from);
        properties.setProperty(Constants.TIMELINE_TO_DATE, to);
    }
}
