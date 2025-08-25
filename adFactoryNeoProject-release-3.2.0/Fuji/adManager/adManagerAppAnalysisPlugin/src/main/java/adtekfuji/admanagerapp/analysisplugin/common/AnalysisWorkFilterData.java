/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.analysisplugin.common;

import adtekfuji.admanagerapp.analysisplugin.javafx.CheckTableData;
import java.util.Objects;
import java.util.ResourceBundle;

import adtekfuji.locale.LocaleUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 設定項目情報クラス
 *
 * @author e-mori
 * @version 1.4.2
 * @since 2016.08.01.Mon
 */
public class AnalysisWorkFilterData {

    /**
     * 実績時間の単位
     */
    public enum TimeUnitEnum {

        SECOND(1000),
        MINUTE(60000);

        private final Integer timeunit;

        private TimeUnitEnum(Integer unit) {
            this.timeunit = unit;
        }

        public Integer getTimeUnit() {
            return timeunit;
        }

        public static TimeUnitEnum getEnum(String str) {
            TimeUnitEnum[] enumArray = TimeUnitEnum.values();
            for (TimeUnitEnum enumStr : enumArray) {
                if (str.equals(enumStr.toString())) {
                    return enumStr;
                }
            }
            return null;
        }

        public static String getLocale(ResourceBundle rb, TimeUnitEnum item) {
            switch (item) {
                case MINUTE:
                    return LocaleUtils.getString("key.time.minute");
                case SECOND:
                    return LocaleUtils.getString("key.time.second");
                default:
                    return "";
            }
        }
    }

    private StringProperty filterTactTimeEarliestProperty;
    private StringProperty filterTactTimeSlowestProperty;
    private ObjectProperty<ObservableList<CheckTableData>> filterDelayReasonProperty;
    private ObjectProperty<TimeUnitEnum> timeunitProperty;

    private final Integer filterTactTimeEarliest;
    private final Integer filterTactTimeSlowest;
    private ObservableList<CheckTableData> filterDelayReason = FXCollections.observableArrayList();
    private TimeUnitEnum timeunit;

    public AnalysisWorkFilterData() {
        this.filterTactTimeEarliest = 0;
        this.filterTactTimeSlowest = 0;
    }

    public AnalysisWorkFilterData(Integer filterTactTimeEarliest, Integer filterTactTimeSlowest, ObservableList<CheckTableData> filterDelayReason, TimeUnitEnum timeunit) {
        this.filterTactTimeEarliest = filterTactTimeEarliest;
        this.filterTactTimeSlowest = filterTactTimeSlowest;
        this.filterDelayReason = filterDelayReason;
        this.timeunit = timeunit;
    }

    public StringProperty filterTactTimeEarliestProperty() {
        if (Objects.isNull(filterTactTimeEarliestProperty)) {
            filterTactTimeEarliestProperty = new SimpleStringProperty(filterTactTimeEarliest.toString());
        }
        return filterTactTimeEarliestProperty;
    }

    public StringProperty filterTactTimeSlowestProperty() {
        if (Objects.isNull(filterTactTimeSlowestProperty)) {
            filterTactTimeSlowestProperty = new SimpleStringProperty(filterTactTimeSlowest.toString());
        }
        return filterTactTimeSlowestProperty;
    }

    public ObjectProperty<ObservableList<CheckTableData>> filterDelayReasonProperty() {
        if (Objects.isNull(filterDelayReasonProperty)) {
            filterDelayReasonProperty = new SimpleObjectProperty(filterDelayReason);
        }
        return filterDelayReasonProperty;
    }

    public ObjectProperty<TimeUnitEnum> timeunitProperty() {
        if (Objects.isNull(timeunitProperty)) {
            timeunitProperty = new SimpleObjectProperty(timeunit);
        }
        return timeunitProperty;
    }

    public Integer getFilterTactTimeEarliest() {
        if (Objects.nonNull(filterTactTimeEarliestProperty)) {
            return Integer.parseInt(filterTactTimeEarliestProperty.get());
        }
        return filterTactTimeEarliest;
    }

    public Integer getFilterTactTimeSlowest() {
        if (Objects.nonNull(filterTactTimeSlowestProperty)) {
            return Integer.parseInt(filterTactTimeSlowestProperty.get());
        }
        return filterTactTimeSlowest;
    }

    public ObservableList<CheckTableData> getFilterDelayReason() {
        if (Objects.nonNull(filterDelayReasonProperty)) {
            return filterDelayReasonProperty.get();
        }
        return filterDelayReason;
    }

    public TimeUnitEnum getTimeUnit() {
        if (Objects.nonNull(timeunitProperty)) {
            return timeunitProperty.get();
        }
        return timeunit;
    }
}
