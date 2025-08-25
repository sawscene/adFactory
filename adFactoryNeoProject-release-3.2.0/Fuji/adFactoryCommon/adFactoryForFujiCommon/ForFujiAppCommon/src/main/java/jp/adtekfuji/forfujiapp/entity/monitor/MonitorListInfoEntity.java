/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.entity.monitor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

/**
 * 進捗モニタリスト表示用情報クラス
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.20.Thr
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "monitorListInfo")
public class MonitorListInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty unitIdProperty;
    private StringProperty mainTitleProperty;
    private StringProperty subTitleProperty;
    private ObjectProperty<Date> shipDateProperty;
    private StringProperty unitTemplateNameProperty;
    private DoubleProperty workProgressProperty;
    private ObjectProperty<KanbanStatusEnum> unitStatusProperty;

    @XmlElement()
    private Long unitId;
    @XmlElement
    private String mainTitle;
    @XmlElement
    private String subTitle;
    @XmlElement()
    private Date startDate;
    @XmlElement()
    private Date shipDate;
    @XmlElement()
    private String unitTemplateName;
    @XmlElement()
    private Double workProgress;
    @XmlElement()
    private KanbanStatusEnum unitStatus;
    @XmlElement()
    private Long progressTimeMillisec;
    @XmlElement()
    private String backgroundColor;
    @XmlElementWrapper(name = "kanbanIds")
    @XmlElement(name = "kanbanId")
    private List<Long> kanbanIds;

    public MonitorListInfoEntity() {
    }

    public MonitorListInfoEntity(Long unitId, String mainTitle, String subTitle, Date startDate, Date shipDate, String unitTemplateName, KanbanStatusEnum unitStatus, Double workProgress, Long progressTimeMillisec) {
        this.unitId = unitId;
        this.mainTitle = mainTitle;
        this.subTitle = subTitle;
        this.startDate = startDate;
        this.shipDate = shipDate;
        this.unitTemplateName = unitTemplateName;
        this.workProgress = workProgress;
        this.unitStatus = unitStatus;
        this.progressTimeMillisec = progressTimeMillisec;
    }

    public MonitorListInfoEntity copy(MonitorListInfoEntity in) {
        this.setUnitId(in.unitId);
        this.setMainTitle(in.mainTitle);
        this.setSubTitle(in.subTitle);
        this.setStartDate(in.startDate);
        this.setShipDate(in.shipDate);
        this.setUnitTemplateName(in.unitTemplateName);
        this.setWorkProgress(in.workProgress);
        this.setUnitStatus(in.unitStatus);
        this.setBackgroundColor(in.backgroundColor);
        this.setProgressTimeMillisec(in.progressTimeMillisec);
        this.setKanbanIds(in.kanbanIds);
        return this;
    }

    public LongProperty unitIdProperty() {
        if (Objects.isNull(unitIdProperty)) {
            unitIdProperty = new SimpleLongProperty(unitId);
        }
        return unitIdProperty;
    }

    public StringProperty mainTitleProperty() {
        if (Objects.isNull(mainTitleProperty)) {
            mainTitleProperty = new SimpleStringProperty(mainTitle);
        }
        return mainTitleProperty;
    }

    public StringProperty subTitleProperty() {
        if (Objects.isNull(subTitleProperty)) {
            subTitleProperty = new SimpleStringProperty(subTitle);
        }
        return subTitleProperty;
    }

    public ObjectProperty<Date> shipDateProperty() {
        if (Objects.isNull(shipDateProperty)) {
            shipDateProperty = new SimpleObjectProperty<>(shipDate);
        }
        return shipDateProperty;
    }

    public StringProperty unitTemplateNameProperty() {
        if (Objects.isNull(unitTemplateNameProperty)) {
            unitTemplateNameProperty = new SimpleStringProperty(unitTemplateName);
        }
        return unitTemplateNameProperty;
    }

    public DoubleProperty workProgressProperty() {
        if (Objects.isNull(workProgressProperty)) {
            workProgressProperty = new SimpleDoubleProperty(workProgress);
        }
        return workProgressProperty;
    }

    public ObjectProperty<KanbanStatusEnum> unitStatusProperty() {
        if (Objects.isNull(unitStatusProperty)) {
            unitStatusProperty = new SimpleObjectProperty<>(unitStatus);
        }
        return unitStatusProperty;
    }

    public Long getUnitId() {
        if (Objects.nonNull(unitIdProperty)) {
            return unitIdProperty.get();
        }
        return unitId;
    }

    public void setUnitId(Long unitId) {
        if (Objects.nonNull(unitIdProperty)) {
            unitIdProperty.set(unitId);
        } else {
            this.unitId = unitId;
        }
    }

    public String getMainTitle() {
        if (Objects.nonNull(mainTitleProperty)) {
            return mainTitleProperty.get();
        }
        return mainTitle;
    }

    public void setMainTitle(String mainTitle) {
        if (Objects.nonNull(mainTitleProperty)) {
            mainTitleProperty.set(mainTitle);
        } else {
            this.mainTitle = mainTitle;
        }
    }

    public String getSubTitle() {
        if (Objects.nonNull(subTitleProperty)) {
            return subTitleProperty.get();
        }
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        if (Objects.nonNull(subTitleProperty)) {
            subTitleProperty.set(subTitle);
        } else {
            this.subTitle = subTitle;
        }
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getShipDate() {
        if (Objects.nonNull(shipDateProperty)) {
            return shipDateProperty.get();
        }
        return shipDate;
    }

    public void setShipDate(Date shipDate) {
        if (Objects.nonNull(shipDateProperty)) {
            shipDateProperty.set(shipDate);
        } else {
            this.shipDate = shipDate;
        }
    }

    public String getUnitTemplateName() {
        if (Objects.nonNull(unitTemplateNameProperty)) {
            return unitTemplateNameProperty.get();
        }
        return unitTemplateName;
    }

    public void setUnitTemplateName(String unitTemplateName) {
        if (Objects.nonNull(unitTemplateNameProperty)) {
            unitTemplateNameProperty.set(unitTemplateName);
        } else {
            this.unitTemplateName = unitTemplateName;
        }
    }

    public Double getWorkProgress() {
        if (Objects.nonNull(workProgressProperty)) {
            return workProgressProperty.get();
        }
        return workProgress;
    }

    public void setWorkProgress(Double workProgress) {
        if (Objects.nonNull(workProgressProperty)) {
            workProgressProperty.set(workProgress);
        } else {
            this.workProgress = workProgress;
        }
    }

    public KanbanStatusEnum getUnitStatus() {
        if (Objects.nonNull(unitStatusProperty)) {
            return unitStatusProperty.get();
        }
        return unitStatus;
    }

    public void setUnitStatus(KanbanStatusEnum unitStatus) {
        if (Objects.nonNull(unitStatusProperty)) {
            unitStatusProperty.set(unitStatus);
        } else {
            this.unitStatus = unitStatus;
        }
    }

    public Long getProgressTimeMillisec() {
        return progressTimeMillisec;
    }

    public void setProgressTimeMillisec(Long progressTimeMillisec) {
        this.progressTimeMillisec = progressTimeMillisec;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public List<Long> getKanbanIds() {
        return kanbanIds;
    }

    public void setKanbanIds(List<Long> kanbanIds) {
        this.kanbanIds = kanbanIds;
    }

    public void update() {
        this.unitId = unitIdProperty.get();
        this.mainTitle = mainTitleProperty.get();
        this.subTitle = subTitleProperty.get();
        this.shipDate = shipDateProperty.get();
        this.unitTemplateName = unitTemplateNameProperty.get();
        this.workProgress = workProgressProperty.get();
        this.unitStatus = unitStatusProperty.get();
    }

    public void update(MonitorListInfoEntity in) {
        this.setUnitId(in.unitId);
        this.setMainTitle(in.mainTitle);
        this.setSubTitle(in.subTitle);
        this.setShipDate(in.shipDate);
        this.setUnitTemplateName(in.unitTemplateName);
        this.setWorkProgress(in.workProgress);
        this.setUnitStatus(in.unitStatus);
        this.setBackgroundColor(in.backgroundColor);
        this.setProgressTimeMillisec(in.progressTimeMillisec);
        this.setKanbanIds(in.kanbanIds);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MonitorListInfoEntity other = (MonitorListInfoEntity) obj;
        return true;
    }

    @Override
    public String toString() {
        return "AgendaEntity{" + "unitId=" + unitId + ", mainTitle=" + mainTitle + ", subTitle=" + subTitle + ", shipDate=" + shipDate + ", unitTemplateName=" + unitTemplateName + ", workProgress=" + workProgress + ", unitStatus=" + unitStatus + '}';
    }
}
