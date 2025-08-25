/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.addatabaseapp.component;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import jp.adtekfuji.addatabase.utils.LocaleUtils;

/**
 * 定期実行タイプ「毎月」選択時の実行日選択ペインのコントローラ
 *
 * @author s-maeda
 */
public class DatabaseMainteDayOfMonthFxContoroller implements DatabaseMainteDayInfoPaneFxController, Initializable {

    @FXML
    private ComboBox<String> dayOfMonthCombo;

    private int selectedDay;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        for (int day = 1; day <= 31; day++) {
            this.dayOfMonthCombo.getItems().add(day + LocaleUtils.getString("key.Schedule.DaySuffix"));
        }
        this.dayOfMonthCombo.getItems().add(LocaleUtils.getString("key.Schedule.LastDay"));

        setDayOfMonth("1");

        this.dayOfMonthCombo.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (!newValue.equals(oldValue)) {
                this.selectedDay = newValue.intValue() + 1;
            }
        });
    }

    /**
     * 引数で指定した日に、実行日指定コンボボックスの選択を切り替える
     *
     * @param selectedDay
     */
    public void setDayOfMonth(String selectedDay) {
        try {
            this.selectedDay = Integer.valueOf(selectedDay);
            this.selectedDay = ((1 <= this.selectedDay) && (this.selectedDay <= 32)) ? this.selectedDay : 1;
        } catch (NumberFormatException ex) {
            this.selectedDay = 1;
        }

        if (this.selectedDay == 32) {
            this.dayOfMonthCombo.getSelectionModel().select(LocaleUtils.getString("key.Schedule.LastDay"));
        } else {
            this.dayOfMonthCombo.getSelectionModel().select(this.selectedDay + LocaleUtils.getString("key.Schedule.DaySuffix"));
        }
    }

    /**
     * 実行日データ取得
     */
    @Override
    public String get() {
        return String.valueOf(this.selectedDay);
    }
}
