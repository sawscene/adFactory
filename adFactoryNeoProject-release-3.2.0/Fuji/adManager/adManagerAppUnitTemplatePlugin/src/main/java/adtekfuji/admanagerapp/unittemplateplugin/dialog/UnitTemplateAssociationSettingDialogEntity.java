/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unittemplateplugin.dialog;

import adtekfuji.utility.StringTime;
import java.util.Date;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * ユニットテンプレート・工程順ツリーのドラッグアンドドロップ時の処理
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.11.4.Fri
 */
public class UnitTemplateAssociationSettingDialogEntity {

    private StringProperty offsetTimeProperty;
    private ObjectProperty<Date> startTimeProperty;
    private ObjectProperty<Date> endTimeProperty;

    private long taktTime;
    private String offsetTime;
    private Date startTime;
    private Date endTime;

    private final boolean editSingle;

    public UnitTemplateAssociationSettingDialogEntity() {
        this.editSingle = false;
    }

    public UnitTemplateAssociationSettingDialogEntity(long taktTime, Date startTime, Date endTime, boolean editSingle) {
        this.taktTime = taktTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.editSingle = editSingle;
    }

    public StringProperty offsetTimeProperty() {
        if (Objects.isNull(offsetTimeProperty)) {
            offsetTimeProperty = new SimpleStringProperty(offsetTime);
        }
        return offsetTimeProperty;
    }

    public ObjectProperty<Date> startTimeProperty() {
        if (Objects.isNull(startTimeProperty)) {
            startTimeProperty = new SimpleObjectProperty(startTime);
        }
        return startTimeProperty;
    }

    public ObjectProperty<Date> endTimeProperty() {
        if (Objects.isNull(endTimeProperty)) {
            endTimeProperty = new SimpleObjectProperty(endTime);
        }
        return endTimeProperty;
    }

    public long getTaktTime() {
        return taktTime;
    }

    public void setTaktTime(long taktTime) {
        this.taktTime = taktTime;
    }

    public String getOffsetTime() {
        if (Objects.nonNull(offsetTimeProperty)) {
            return offsetTimeProperty.get();
        }
        return offsetTime;
    }

    public void setOffsetTime(String offsetTime) {
        if (Objects.nonNull(offsetTimeProperty)) {
            offsetTimeProperty.set(offsetTime);
        } else {
            this.offsetTime = offsetTime;
        }
    }

    public Date getStartTime() {
        if (Objects.nonNull(startTimeProperty)) {
            return startTimeProperty.get();
        }
        return startTime;
    }

    public void setStartTime(Date startTime) {
        if (Objects.nonNull(startTimeProperty)) {
            startTimeProperty.set(startTime);
        } else {
            this.startTime = startTime;
        }
    }

    public Date getEndTime() {
        if (Objects.nonNull(endTimeProperty)) {
            return endTimeProperty.get();
        }
        return endTime;
    }

    public void setEndTime(Date endTime) {
        if (Objects.nonNull(endTimeProperty)) {
            endTimeProperty.set(endTime);
        } else {
            this.endTime = endTime;
        }
    }

    public boolean isEditSingle() {
        return editSingle;
    }

    public void Update() {
        this.offsetTime = getOffsetTime();
    }

    /**
     * オフセット(ミリ秒)を取得する。
     *
     * @return
     */
    public long getOffset() {
        String value = this.getOffsetTime();
        boolean isMinus = false;

        if (value.indexOf('-') == 0) {
            isMinus = true;
            value = value.substring(1);
        }

        long millis = StringTime.convertStringTimeToMillis(value);
        if (isMinus) {
            millis = millis * -1;
        }

        return millis;
    }
}
