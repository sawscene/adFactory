/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.workflow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.WorkKbnEnum;

/**
 * 工程順バラ工程関連付け情報.
 *
 * @author ta.ito
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "conWorkflowSeparatework")
public class ConWorkflowSeparateworkInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty associationIdProperty;
    private LongProperty fkWorkflowIdProperty;
    private LongProperty fkWorkIdProperty;
    private StringProperty workNameProperty;
    private BooleanProperty skipFlagProperty;
    private IntegerProperty workflowOrderProperty;
    private ObjectProperty<Date> standardStartTimeProperty;
    private ObjectProperty<Date> standardEndTimeProperty;

    @XmlElement()
    private WorkKbnEnum workKbn = WorkKbnEnum.ADDITIONAL_WORK;

//    @XmlElement(required = true)
    private Long associationId;
    @XmlElement()
    private Long fkWorkflowId;
    @XmlElement()
    private Long fkWorkId;
    @XmlElement()
    private String workName;
    @XmlElement()
    private boolean skipFlag;
    @XmlElement()
    private Integer workflowOrder;
    @XmlElement()
    private Date standardStartTime;
    @XmlElement()
    private Date standardEndTime;
    @XmlElementWrapper(name = "equipments")
    @XmlElement(name = "equipment")
    private List<Long> equipmentCollection;
    @XmlElementWrapper(name = "organizations")
    @XmlElement(name = "organization")
    private List<Long> organizationCollection = null;

    public ConWorkflowSeparateworkInfoEntity() {
    }

    public ConWorkflowSeparateworkInfoEntity(Long associationId, Long fkWorkflowId, Long fkWorkId, boolean skipFlag, Integer workflowOrder) {
        this.associationId = associationId;
        this.fkWorkflowId = fkWorkflowId;
        this.fkWorkId = fkWorkId;
        this.skipFlag = skipFlag;
        this.workflowOrder = workflowOrder;
    }

    public LongProperty associationIdProperty() {
        if (Objects.isNull(associationIdProperty)) {
            associationIdProperty = new SimpleLongProperty(associationId);
        }
        return associationIdProperty;
    }

    public LongProperty fkWorkflowIdProperty() {
        if (Objects.isNull(fkWorkflowIdProperty)) {
            fkWorkflowIdProperty = new SimpleLongProperty(fkWorkflowId);
        }
        return fkWorkflowIdProperty;
    }

    public LongProperty fkWorkIdProperty() {
        if (Objects.isNull(fkWorkIdProperty)) {
            fkWorkIdProperty = new SimpleLongProperty(fkWorkId);
        }
        return fkWorkIdProperty;
    }

    public StringProperty workNameProperty() {
        if (Objects.isNull(workNameProperty)) {
            workNameProperty = new SimpleStringProperty(workName);
        }
        return workNameProperty;
    }

    public BooleanProperty skipFlagProperty() {
        if (Objects.isNull(skipFlagProperty)) {
            skipFlagProperty = new SimpleBooleanProperty(skipFlag);
        }
        return skipFlagProperty;
    }

    public IntegerProperty workflowOrderProperty() {
        if (Objects.isNull(workflowOrderProperty)) {
            workflowOrderProperty = new SimpleIntegerProperty(workflowOrder);
        }
        return workflowOrderProperty;
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

    /**
     * 工程区分を取得する。
     *
     * @return 工程区分
     */
    public WorkKbnEnum getWorkKbn() {
        return this.workKbn;
    }

    /**
     * 工程区分を設定する。
     *
     * @param workKbn 工程区分
     */
    public void setWorkKbn(WorkKbnEnum workKbn) {
        this.workKbn = workKbn;
    }

    public Long getAssociationId() {
        if (Objects.nonNull(associationIdProperty)) {
            return associationIdProperty.get();
        }
        return associationId;
    }

    public void setAssociationId(Long associationId) {
        if (Objects.nonNull(associationIdProperty)) {
            associationIdProperty.set(associationId);
        } else {
            this.associationId = associationId;
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

    public Long getFkWorkId() {
        if (Objects.nonNull(fkWorkIdProperty)) {
            return fkWorkIdProperty.get();
        }
        return fkWorkId;
    }

    public void setFkWorkId(Long fkWorkId) {
        if (Objects.nonNull(fkWorkIdProperty)) {
            fkWorkIdProperty.set(fkWorkId);
        } else {
            this.fkWorkId = fkWorkId;
        }
    }

    public String getWorkName() {
        if (Objects.nonNull(workNameProperty)) {
            return workNameProperty.get();
        }
        return workName;
    }

    public void setWorkName(String workName) {
        if (Objects.nonNull(workNameProperty)) {
            workNameProperty.set(workName);
        } else {
            this.workName = workName;
        }
    }

    public boolean getSkipFlag() {
        if (Objects.nonNull(skipFlagProperty)) {
            return skipFlagProperty.get();
        }
        return skipFlag;
    }

    public void setSkipFlag(boolean skipFlag) {
        if (Objects.nonNull(skipFlagProperty)) {
            skipFlagProperty.set(skipFlag);
        } else {
            this.skipFlag = skipFlag;
        }
    }

    public Integer getWorkflowOrder() {
        if (Objects.nonNull(workflowOrderProperty)) {
            return workflowOrderProperty.get();
        }
        return workflowOrder;
    }

    public void setWorkflowOrder(Integer workflowOrder) {
        if (Objects.nonNull(workflowOrderProperty)) {
            workflowOrderProperty.set(workflowOrder);
        } else {
            this.workflowOrder = workflowOrder;
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

    public List<Long> getEquipmentCollection() {
        return equipmentCollection;
    }

    public void setEquipmentCollection(List<Long> equipmentCollection) {
        this.equipmentCollection = equipmentCollection;
    }

    public List<Long> getOrganizationCollection() {
        return organizationCollection;
    }

    public void setOrganizationCollection(List<Long> organizationCollection) {
        this.organizationCollection = organizationCollection;
    }

    public void updateMember() {
        this.associationId = getAssociationId();
        this.fkWorkflowId = getFkWorkflowId();
        this.fkWorkId = getFkWorkId();
        this.skipFlag = getSkipFlag();
        this.standardStartTime = getStandardStartTime();
        this.standardEndTime = getStandardEndTime();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.workKbn);
        hash = 83 * hash + Objects.hashCode(this.fkWorkflowId);
        hash = 83 * hash + Objects.hashCode(this.fkWorkId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConWorkflowSeparateworkInfoEntity other = (ConWorkflowSeparateworkInfoEntity) obj;
        if (this.workKbn != other.workKbn) {
            return false;
        }
        if (!Objects.equals(this.fkWorkflowId, other.fkWorkflowId)) {
            return false;
        }
        if (!Objects.equals(this.fkWorkId, other.fkWorkId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ConWorkflowSeparateworkInfoEntity{")
                .append("workKbn=").append(this.workKbn)
                .append(", ")
                .append("fkWorkflowId=").append(this.fkWorkflowId)
                .append(", ")
                .append("fkWorkId=").append(this.fkWorkId)
                .append(", ")
                .append("workName=").append(this.workName)
                .append(", ")
                .append("skipFlag=").append(this.skipFlag)
                .append(", ")
                .append("workflowOrder=").append(this.workflowOrder)
                .append(", ")
                .append("standardStartTime=").append(this.standardStartTime)
                .append(", ")
                .append("standardEndTime=").append(this.standardEndTime)
                .append("}")
                .toString();
    }

    /**
     * 表示される情報が一致するか調べる
     *
     * @param other
     * @return
     */
    public boolean equalsDisplayInfo(ConWorkflowSeparateworkInfoEntity other) {
        if (Objects.equals(this.getWorkName(), other.getWorkName())
                && Objects.equals(this.getWorkflowOrder(), other.getWorkflowOrder())
                && Objects.equals(this.getSkipFlag(), other.getSkipFlag())
                && Objects.equals(this.getStandardEndTime(), other.getStandardEndTime())
                && Objects.equals(this.getStandardStartTime(), other.getStandardStartTime())
                && Objects.equals(this.getEquipmentCollection(), other.getEquipmentCollection())
                && Objects.equals(this.getOrganizationCollection(), other.getOrganizationCollection())) {
            return true;
        }

        return false;
    }

    /**
     * 情報をコピーする
     *
     * @return
     */
    @Override
    public ConWorkflowSeparateworkInfoEntity clone() {
        ConWorkflowSeparateworkInfoEntity entity = new ConWorkflowSeparateworkInfoEntity();

        entity.setWorkKbn(this.workKbn);

        entity.setAssociationId(this.associationId);
        entity.setFkWorkflowId(this.fkWorkflowId);
        entity.setFkWorkId(this.fkWorkId);
        entity.setWorkName(this.workName);
        entity.setSkipFlag(this.skipFlag);
        entity.setWorkflowOrder(this.workflowOrder);
        entity.setStandardStartTime((Date) this.standardStartTime.clone());
        entity.setStandardEndTime((Date) this.standardEndTime.clone());

        entity.setEquipmentCollection(new ArrayList<>());
        entity.getEquipmentCollection().addAll(this.equipmentCollection);

        entity.setOrganizationCollection(new ArrayList<>());
        entity.getOrganizationCollection().addAll(this.organizationCollection);

        return entity;
    }
}
