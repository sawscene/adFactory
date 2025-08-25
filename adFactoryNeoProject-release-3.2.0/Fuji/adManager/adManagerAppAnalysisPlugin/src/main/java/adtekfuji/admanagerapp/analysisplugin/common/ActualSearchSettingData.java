/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.analysisplugin.common;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 実績検索条件データクラス
 *
 * @author e-mori
 * @version 1.4.2
 * @since 2016.08.03.Wed
 */
public class ActualSearchSettingData {

    private ObjectProperty<LocalDate> startDatetimeProperty;
    private ObjectProperty<LocalDate> endDatetimeProperty;
    private ObjectProperty<ObservableList<WorkTableData>> workTabelDatasProperty;

    private LocalDate startDate;
    private LocalDate endDate;
    private ObservableList<WorkTableData> workTabelDatas = FXCollections.observableArrayList();

    public static final String KEY_SEARCH_START_DATE = "Search.StartDate";
    public static final String KEY_SEARCH_END_DATE = "Search.EndDate";

    public ActualSearchSettingData() {
    }

    public ActualSearchSettingData(LocalDate startDate, LocalDate endDate, ObservableList<WorkTableData> workTabelDatas) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.workTabelDatas = workTabelDatas;
    }

    public ObjectProperty<LocalDate> startDatetimeProperty() {
        if (Objects.isNull(startDatetimeProperty)) {
            startDatetimeProperty = new SimpleObjectProperty<>(startDate);
        }
        return startDatetimeProperty;
    }

    public ObjectProperty<LocalDate> endDatetimeProperty() {
        if (Objects.isNull(endDatetimeProperty)) {
            endDatetimeProperty = new SimpleObjectProperty<>(endDate);
        }
        return endDatetimeProperty;
    }

    public ObjectProperty<ObservableList<WorkTableData>> workTabelDatasProperty() {
        if (Objects.isNull(workTabelDatasProperty)) {
            workTabelDatasProperty = new SimpleObjectProperty<>(workTabelDatas);
        }
        return workTabelDatasProperty;
    }

    public LocalDate getStartDate() {
        if (Objects.nonNull(startDatetimeProperty)) {
            return startDatetimeProperty.get();
        }
        return startDate;
    }

    public LocalDate getEndDate() {
        if (Objects.nonNull(endDatetimeProperty)) {
            return endDatetimeProperty.get();
        }
        return endDate;
    }

    public List<WorkTableData> getworkTabelDatas() {
        if (Objects.nonNull(workTabelDatasProperty)) {
            return workTabelDatasProperty.get();
        }
        return workTabelDatas;
    }

    public void setStartDate(LocalDate startDate) {
        if (Objects.nonNull(startDatetimeProperty)) {
            this.startDatetimeProperty.set(startDate);
        } else {
            this.startDate = startDate;
        }
    }

    public void setEndDate(LocalDate endDate) {
        if (Objects.nonNull(endDatetimeProperty)) {
            this.endDatetimeProperty.set(endDate);
        } else {
            this.endDate = endDate;
        }
    }

    public void setworkTabelDatas(ObservableList<WorkTableData> workTabelDatas) {
        if (Objects.nonNull(workTabelDatasProperty)) {
            this.workTabelDatasProperty.set(workTabelDatas);
        } else {
            this.workTabelDatas = workTabelDatas;
        }
    }
}
