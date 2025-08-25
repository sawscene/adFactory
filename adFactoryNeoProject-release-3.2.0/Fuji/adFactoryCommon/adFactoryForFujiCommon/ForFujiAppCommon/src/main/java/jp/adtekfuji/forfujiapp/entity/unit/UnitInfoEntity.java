/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this  file, choose Tools | s
 * and open the  in the editor.
 */
package jp.adtekfuji.forfujiapp.entity.unit;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * ユニット情報
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.26.Wen
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "unit")
public class UnitInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty unitIdProperty;
    private LongProperty parentIdProperty;
    private StringProperty parentNameProperty;
    private StringProperty unitNameProperty;
    private LongProperty fkUnitTemplateIdProperty;
    private StringProperty unitTemplateNameProperty;
    private StringProperty workflowDiaglamProperty;
    private ObjectProperty<Date> startDatetimeProperty;
    private ObjectProperty<Date> compDatetimeProperty;
    private LongProperty fkUpdatePersonIdProperty;
    private ObjectProperty<Date> updateDatetimePropertyProperty;

    @XmlElement(required = true)
    private Long unitId;
    @XmlElement()
    private Long parentId;
    private String parentName;
    @XmlElement()
    private String unitName;
    @XmlElement()
    private long fkUnitTemplateId;
    @XmlElement()
    private String unitTemplateName;
    @XmlElement()
    private String workflowDiaglam;
    @XmlElement()
    private Date startDatetime;
    @XmlElement()
    private Date compDatetime;
    @XmlElement()
    private Long fkUpdatePersonId;
    @XmlElement()
    private Date updateDatetime;
    @XmlElementWrapper(name = "unitPropertys")
    @XmlElement(name = "unitProperty")
    private LinkedList<UnitPropertyInfoEntity> unitPropertyCollection = null;
    @XmlElementWrapper(name = "conUnitAssociates")
    @XmlElement(name = "conUnitAssociate")
    private List<ConUnitAssociateInfoEntity> conUnitAssociateCollection = null;
    /**
     * カンバンID
     */
    @XmlElementWrapper(name = "kanbanIds")
    @XmlElement(name = "kanbanId")
    private List<Long> kanbanIds;
    /**
     * すべてのカンバンが完了しているか？
     */
    @XmlElement(name = "isCompleted")
    private Boolean isCompleted;

    public UnitInfoEntity() {
    }

    public UnitInfoEntity(Long unitId) {
        this.unitId = unitId;
    }

    public UnitInfoEntity(Long unitId, String unitName, String workflowDiaglam) {
        this.unitId = unitId;
        this.unitName = unitName;
        this.workflowDiaglam = workflowDiaglam;
    }

    public UnitInfoEntity(Long parentId, String unitName, Long fkUnitTemplateId, String workflowDiaglam, Long fkUpdatePersonId, Date updateDatetime) {
        this.parentId = parentId;
        this.unitName = unitName;
        this.fkUnitTemplateId = fkUnitTemplateId;
        this.workflowDiaglam = workflowDiaglam;
        this.fkUpdatePersonId = fkUpdatePersonId;
        this.updateDatetime = updateDatetime;
    }

    public LongProperty unitIdProperty() {
        if (Objects.isNull(unitIdProperty)) {
            unitIdProperty = new SimpleLongProperty(unitId);
        }
        return unitIdProperty;
    }

    public LongProperty parentIdProperty() {
        if (Objects.isNull(parentIdProperty)) {
            parentIdProperty = new SimpleLongProperty(parentId);
        }
        return parentIdProperty;
    }

    public StringProperty parentNameProperty() {
        if (Objects.isNull(parentNameProperty)) {
            parentNameProperty = new SimpleStringProperty(parentName);
        }
        return parentNameProperty;
    }

    public StringProperty unitNameProperty() {
        if (Objects.isNull(unitNameProperty)) {
            unitNameProperty = new SimpleStringProperty(unitName);
        }
        return unitNameProperty;
    }

    public LongProperty fkUnitTemplateIdProperty() {
        if (Objects.isNull(fkUnitTemplateIdProperty)) {
            fkUnitTemplateIdProperty = new SimpleLongProperty(fkUnitTemplateId);
        }
        return fkUnitTemplateIdProperty;
    }

    public StringProperty unitTemplateNameProperty() {
        if (Objects.isNull(unitTemplateNameProperty)) {
            unitTemplateNameProperty = new SimpleStringProperty(unitTemplateName);
        }
        return unitTemplateNameProperty;
    }

    public StringProperty workflowDiaglamProperty() {
        if (Objects.isNull(workflowDiaglamProperty)) {
            workflowDiaglamProperty = new SimpleStringProperty(workflowDiaglam);
        }
        return workflowDiaglamProperty;
    }

    public ObjectProperty<Date> startDatetimeProperty() {
        if (Objects.isNull(startDatetimeProperty)) {
            startDatetimeProperty = new SimpleObjectProperty<>(startDatetime);
        }
        return startDatetimeProperty;
    }

    public ObjectProperty<Date> compDatetimeProperty() {
        if (Objects.isNull(compDatetimeProperty)) {
            compDatetimeProperty = new SimpleObjectProperty<>(compDatetime);
        }
        return compDatetimeProperty;
    }

    public LongProperty fkUpdatePersonIdProperty() {
        if (Objects.isNull(fkUpdatePersonIdProperty)) {
            fkUpdatePersonIdProperty = new SimpleLongProperty(fkUpdatePersonId);
        }
        return fkUpdatePersonIdProperty;
    }

    public ObjectProperty<Date> updateDatetimePropertyProperty() {
        if (Objects.isNull(updateDatetimePropertyProperty)) {
            updateDatetimePropertyProperty = new SimpleObjectProperty<>(updateDatetime);
        }
        return updateDatetimePropertyProperty;
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

    public Long getParentId() {
        if (Objects.nonNull(parentIdProperty)) {
            return parentIdProperty.get();
        }
        return parentId;
    }

    public void setParentId(Long parentId) {
        if (Objects.nonNull(parentIdProperty)) {
            parentIdProperty.set(unitId);
        } else {
            this.parentId = parentId;
        }
    }

    public String getParentName() {
        if (Objects.nonNull(parentNameProperty)) {
            return parentNameProperty.get();
        }
        return parentName;
    }

    public void setParentName(String parentName) {
        if (Objects.nonNull(parentNameProperty)) {
            parentNameProperty.set(parentName);
        } else {
            this.parentName = parentName;
        }
    }

    public String getUnitName() {
        if (Objects.nonNull(unitNameProperty)) {
            return unitNameProperty.get();
        }
        return unitName;
    }

    public void setUnitName(String unitName) {
        if (Objects.nonNull(unitNameProperty)) {
            unitNameProperty.set(unitName);
        } else {
            this.unitName = unitName;
        }
    }

    public long getFkUnitTemplateId() {
        if (Objects.nonNull(fkUnitTemplateIdProperty)) {
            return fkUnitTemplateIdProperty.get();
        }
        return fkUnitTemplateId;
    }

    public void setFkUnitTemplateId(long fkUnitTemplateId) {
        if (Objects.nonNull(fkUnitTemplateIdProperty)) {
            fkUnitTemplateIdProperty.set(fkUnitTemplateId);
        } else {
            this.fkUnitTemplateId = fkUnitTemplateId;
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

    public String getWorkflowDiaglam() {
        if (Objects.nonNull(workflowDiaglamProperty)) {
            return workflowDiaglamProperty.get();
        }
        return workflowDiaglam;
    }

    public void setWorkflowDiaglam(String workflowDiaglam) {
        if (Objects.nonNull(workflowDiaglamProperty)) {
            workflowDiaglamProperty.set(workflowDiaglam);
        } else {
            this.workflowDiaglam = workflowDiaglam;
        }
    }

    public Date getStartDatetime() {
        if (Objects.nonNull(startDatetimeProperty)) {
            return startDatetimeProperty.get();
        }
        return startDatetime;
    }

    public void setStartDatetime(Date startDatetime) {
        if (Objects.nonNull(startDatetimeProperty)) {
            startDatetimeProperty.set(startDatetime);
        } else {
            this.startDatetime = startDatetime;
        }
    }

    public Date getCompDatetime() {
        if (Objects.nonNull(compDatetimeProperty)) {
            return compDatetimeProperty.get();
        }
        return compDatetime;
    }

    public void setCompDatetime(Date compDatetime) {
        if (Objects.nonNull(compDatetimeProperty)) {
            compDatetimeProperty.set(compDatetime);
        } else {
            this.compDatetime = compDatetime;
        }
    }

    public Long getFkUpdatePersonId() {
        if (Objects.nonNull(fkUpdatePersonIdProperty)) {
            return fkUpdatePersonIdProperty.get();
        }
        return fkUpdatePersonId;
    }

    public void setFkUpdatePersonId(Long fkUpdatePersonId) {
        if (Objects.nonNull(fkUpdatePersonIdProperty)) {
            fkUpdatePersonIdProperty.set(fkUpdatePersonId);
        } else {
            this.fkUpdatePersonId = fkUpdatePersonId;
        }
    }

    public Date getUpdateDatetime() {
        if (Objects.nonNull(updateDatetimePropertyProperty)) {
            return updateDatetimePropertyProperty.get();
        }
        return updateDatetime;
    }

    public void setUpdateDatetime(Date updateDatetime) {
        if (Objects.nonNull(updateDatetimePropertyProperty)) {
            updateDatetimePropertyProperty.set(updateDatetime);
        } else {
            this.updateDatetime = updateDatetime;
        }
    }

    public LinkedList<UnitPropertyInfoEntity> getUnitPropertyCollection() {
        return unitPropertyCollection;
    }

    public void setUnitPropertyCollection(LinkedList<UnitPropertyInfoEntity> unitPropertyCollection) {
        this.unitPropertyCollection = unitPropertyCollection;
    }

    public List<ConUnitAssociateInfoEntity> getConUnitAssociateCollection() {
        return conUnitAssociateCollection;
    }

    public void setConUnitAssociateCollection(List<ConUnitAssociateInfoEntity> conUnitAssociatekCollection) {
        this.conUnitAssociateCollection = conUnitAssociatekCollection;
    }

    public List<Long> getKanbanIds() {
        return kanbanIds;
    }

    public void setKanbanIds(List<Long> kanbanIds) {
        this.kanbanIds = kanbanIds;
    }

    /**
     * すべてのカンバンが完了しているか？
     *
     * @return 完了状態 (true:すべて完了, false:未完了)
     */
    public Boolean getIsCompleted() {
        return this.isCompleted;
    }

    /**
     * すべてのカンバンが完了しているか？
     *
     * @param isCompleted 完了状態 (true:すべて完了, false:未完了)
     */
    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public void entityUpdate() {
        this.unitId = getUnitId();
        this.parentId = getParentId();
        this.parentName = getParentName();
        this.unitName = getUnitName();
        this.fkUnitTemplateId = getFkUnitTemplateId();
        this.unitTemplateName = getUnitTemplateName();
        this.workflowDiaglam = getWorkflowDiaglam();
        this.startDatetime = getStartDatetime();
        this.compDatetime = getCompDatetime();
        this.fkUpdatePersonId = getFkUpdatePersonId();
        this.updateDatetime = getUpdateDatetime();
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (unitId != null ? unitId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UnitInfoEntity)) {
            return false;
        }
        UnitInfoEntity other = (UnitInfoEntity) object;
        return !((this.unitId == null && other.unitId != null) || (this.unitId != null && !this.unitId.equals(other.unitId)));
    }

    @Override
    public String toString() {
        return "UnitInfoEntity{" + "unitId=" + unitId + ", unitName=" + unitName + ", unitTemplateName=" + unitTemplateName + ", startDatetime=" + startDatetime + ", compDatetime=" + compDatetime + '}';
    }
}
