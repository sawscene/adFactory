/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.entity.unittemplate;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * ユニットテンプレート関連情報
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.26.Wen
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "conUnitTemplateAssociate")
public class ConUnitTemplateAssociateInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty unitTemplateAssociationIdProperty;
    private LongProperty fkParentUnitTemplateIdProperty;
    private LongProperty fkWorkflowIdProperty;
    private StringProperty workflowNameProperty;
    private LongProperty fkUnitTemplateIdProperty;
    private StringProperty unitTemplateNameProperty;
    private IntegerProperty unitTemplateAssociateOrderProperty;
    private ObjectProperty<Date> standardStartTimeProperty;
    private ObjectProperty<Date> standardEndTimeProperty;

    @XmlElement(required = true)
    private Long unitTemplateAssociationId;
    @XmlElement()
    private Long fkParentUnitTemplateId;
    @XmlElement()
    private Long fkWorkflowId;
    @XmlElement()
    private String workflowName;
    @XmlElement()
    private Long fkUnitTemplateId;
    @XmlElement()
    private String unitTemplateName;
    @XmlElement()
    private Integer unitTemplateAssociateOrder;
    @XmlElement()
    private Date standardStartTime;
    @XmlElement()
    private Date standardEndTime;

    public ConUnitTemplateAssociateInfoEntity() {
    }

    public ConUnitTemplateAssociateInfoEntity(Long unitTemplateAssociationId, Long fkParentUnitTemplateId, boolean skipFlag, Integer unitTemplateAssociateOrder) {
        this.unitTemplateAssociationId = unitTemplateAssociationId;
        this.fkParentUnitTemplateId = fkParentUnitTemplateId;
        this.unitTemplateAssociateOrder = unitTemplateAssociateOrder;
    }

    public ConUnitTemplateAssociateInfoEntity(ConUnitTemplateAssociateInfoEntity in) {
        this.fkParentUnitTemplateId = in.fkParentUnitTemplateId;
        this.fkWorkflowId = in.fkWorkflowId;
        this.fkUnitTemplateId = in.fkUnitTemplateId;
        this.unitTemplateAssociateOrder = in.unitTemplateAssociateOrder;
        this.standardStartTime = in.standardStartTime;
        this.standardEndTime = in.standardEndTime;
    }

    public LongProperty unitTemplateAssociationIdProperty() {
        if (Objects.isNull(unitTemplateAssociationIdProperty)) {
            unitTemplateAssociationIdProperty = new SimpleLongProperty(unitTemplateAssociationId);
        }
        return unitTemplateAssociationIdProperty;
    }

    public LongProperty fkParentUnitTemplateIdProperty() {
        if (Objects.isNull(fkParentUnitTemplateIdProperty)) {
            fkParentUnitTemplateIdProperty = new SimpleLongProperty(fkParentUnitTemplateId);
        }
        return fkParentUnitTemplateIdProperty;
    }

    public LongProperty fkWorkflowIdProperty() {
        if (Objects.isNull(fkWorkflowIdProperty)) {
            fkWorkflowIdProperty = new SimpleLongProperty(fkWorkflowId);
        }
        return fkWorkflowIdProperty;
    }

    public StringProperty workflowNameProperty() {
        if (Objects.isNull(workflowNameProperty)) {
            workflowNameProperty = new SimpleStringProperty(workflowName);
        }
        return workflowNameProperty;
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

    public IntegerProperty unitTemplateAssociateOrderProperty() {
        if (Objects.isNull(unitTemplateAssociateOrderProperty)) {
            unitTemplateAssociateOrderProperty = new SimpleIntegerProperty(unitTemplateAssociateOrder);
        }
        return unitTemplateAssociateOrderProperty;
    }

    public ObjectProperty<Date> standardStartTimeProperty() {
        if (Objects.isNull(standardStartTimeProperty)) {
            standardStartTimeProperty = new SimpleObjectProperty(standardStartTime);
        }
        return standardStartTimeProperty;
    }

    public ObjectProperty<Date> standardEndTimeProperty() {
        if (Objects.isNull(standardEndTimeProperty)) {
            standardEndTimeProperty = new SimpleObjectProperty(standardEndTime);
        }
        return standardEndTimeProperty;
    }

    public Long getUnitTemplateAssociationId() {
        if (Objects.nonNull(unitTemplateAssociationIdProperty)) {
            return unitTemplateAssociationIdProperty.get();
        }
        return unitTemplateAssociationId;
    }

    public void setUnitTemplateAssociationId(Long unitTemplateAssociationId) {
        if (Objects.nonNull(unitTemplateAssociationIdProperty)) {
            unitTemplateAssociationIdProperty.set(unitTemplateAssociationId);
        } else {
            this.unitTemplateAssociationId = unitTemplateAssociationId;
        }
    }

    public Long getFkParentUnitTemplateId() {
        if (Objects.nonNull(fkParentUnitTemplateIdProperty)) {
            return fkParentUnitTemplateIdProperty.get();
        }
        return fkParentUnitTemplateId;
    }

    public void setFkParentUnitTemplateId(Long fkParentUnitTemplateId) {
        if (Objects.nonNull(fkParentUnitTemplateIdProperty)) {
            fkParentUnitTemplateIdProperty.set(fkParentUnitTemplateId);
        } else {
            this.fkParentUnitTemplateId = fkParentUnitTemplateId;
        }
    }

    public Long getFkWorkflowId() {
        if (Objects.nonNull(fkWorkflowIdProperty)) {
            return fkWorkflowIdProperty.get();
        }
        return fkWorkflowId;
    }

    public void setFkWorkflowId(Long fkWorkflowId) {
        if (Objects.nonNull(fkWorkflowIdProperty)) {
            fkWorkflowIdProperty.set(fkWorkflowId);
        } else {
            this.fkWorkflowId = fkWorkflowId;
        }
    }

    public String getWorkflowName() {
        if (Objects.nonNull(workflowNameProperty)) {
            return workflowNameProperty.get();
        }
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        if (Objects.nonNull(workflowNameProperty)) {
            workflowNameProperty.set(workflowName);
        } else {
            this.workflowName = workflowName;
        }
    }

    public Long getFkUnitTemplateId() {
        if (Objects.nonNull(fkUnitTemplateIdProperty)) {
            return fkUnitTemplateIdProperty.get();
        }
        return fkUnitTemplateId;
    }

    public void setFkUnitTemplateId(Long fkUnitTemplateId) {
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
            unitTemplateNameProperty.set(workflowName);
        } else {
            this.unitTemplateName = unitTemplateName;
        }
    }

    public Integer getUnitTemplateAssociateOrder() {
        if (Objects.nonNull(unitTemplateAssociateOrderProperty)) {
            return unitTemplateAssociateOrderProperty.get();
        }
        return unitTemplateAssociateOrder;
    }

    public void setUnitTemplateAssociateOrder(Integer unitTemplateAssociateOrder) {
        if (Objects.nonNull(unitTemplateAssociateOrderProperty)) {
            unitTemplateAssociateOrderProperty.set(unitTemplateAssociateOrder);
        } else {
            this.unitTemplateAssociateOrder = unitTemplateAssociateOrder;
        }
    }

    public Date getStandardStartTime() {
        if (Objects.nonNull(standardStartTimeProperty)) {
            return standardStartTimeProperty.get();
        }
        return standardStartTime;
    }

    public void setStandardStartTime(Date standardStartTime) {
        if (Objects.nonNull(standardStartTimeProperty)) {
            standardStartTimeProperty.set(standardStartTime);
        } else {
            this.standardStartTime = standardStartTime;
        }
    }

    public Date getStandardEndTime() {
        if (Objects.nonNull(standardEndTimeProperty)) {
            return standardEndTimeProperty.get();
        }
        return standardEndTime;
    }

    public void setStandardEndTime(Date standardEndTime) {
        if (Objects.nonNull(standardEndTimeProperty)) {
            standardEndTimeProperty.set(standardEndTime);
        } else {
            this.standardEndTime = standardEndTime;
        }
    }

    /**
     * タクトタイムを取得する。
     *
     * @return タクトタイム
     */
    public long getTaktTime() {
        if (Objects.isNull(this.getStandardStartTime()) || Objects.isNull(this.getStandardEndTime())) {
            return 0;
        }
        return this.getStandardEndTime().getTime() - this.getStandardStartTime().getTime();
    }

    public void updateMember() {
        this.unitTemplateAssociationId = getUnitTemplateAssociationId();
        this.fkParentUnitTemplateId = getFkParentUnitTemplateId();
        this.fkWorkflowId = getFkWorkflowId();
        this.standardStartTime = getStandardStartTime();
        this.standardEndTime = getStandardEndTime();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int) (this.unitTemplateAssociationId ^ (this.unitTemplateAssociationId >>> 32));
        hash = 83 * hash + (int) (this.fkParentUnitTemplateId ^ (this.fkParentUnitTemplateId >>> 32));
        hash = 83 * hash + (int) (this.fkWorkflowId ^ (this.fkWorkflowId >>> 32));
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
        final ConUnitTemplateAssociateInfoEntity other = (ConUnitTemplateAssociateInfoEntity) obj;
        if (!Objects.equals(this.getUnitTemplateAssociationId(), other.getUnitTemplateAssociationId())) {
            return false;
        }
        if (!Objects.equals(this.getFkParentUnitTemplateId(), other.getFkParentUnitTemplateId())) {
            return false;
        }
        return Objects.equals(this.getFkWorkflowId(), other.getFkWorkflowId());
    }

    @Override
    public String toString() {
        return "ConUnitTemplateAssociateInfoEntity{" + "unitTemplateAssociationId=" + getUnitTemplateAssociationId() + ", fkParentUnitTemplateId=" + getFkParentUnitTemplateId() + ", fkWorkflowId=" + getFkWorkflowId() + ", fkWorkflowId=" + getFkUnitTemplateId() + ", unitTemplateAssociateOrder=" + getUnitTemplateAssociateOrder() + ", standardStartTime=" + getStandardStartTime() + ", standardEndTime=" + getStandardEndTime() + '}';
    }
}
