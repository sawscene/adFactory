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
import java.util.stream.Collectors;

import adtekfuji.utility.StringUtils;
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

import jakarta.xml.bind.annotation.*;

import jp.adtekfuji.adFactory.entity.schedule.ScheduleConditionInfoEntity;
import jp.adtekfuji.adFactory.enumerate.WorkKbnEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;

/**
 * 工程順工程関連付け情報.
 *
 * @author ta.ito
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "conWorkflowWork")
public class ConWorkflowWorkInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient LongProperty associationIdProperty;
    private transient LongProperty fkWorkflowIdProperty;
    private transient LongProperty fkWorkIdProperty;
    private transient StringProperty workNameProperty;
    private transient BooleanProperty skipFlagProperty;
    private transient IntegerProperty workflowOrderProperty;
    private transient ObjectProperty<Date> standardStartTimeProperty;
    private transient ObjectProperty<Date> standardEndTimeProperty;

    @XmlElement()
    private WorkKbnEnum workKbn = WorkKbnEnum.BASE_WORK;

//    @XmlElement(required = true)
    private Long associationId;
    @XmlElement()
    private Long fkWorkflowId;
    @XmlElement()
    private Long fkWorkId;
    @XmlElement()
    private String workName;
    @XmlElement()
    private Integer workRev;
    @XmlElement()
    private Boolean skipFlag = false;
    @XmlElement()
    private Integer workflowOrder;
    @XmlElement()
    private Date standardStartTime;
    @XmlElement()
    private Date standardEndTime;
    @XmlElementWrapper(name = "equipments")
    @XmlElement(name = "equipment")
    private List<Long> equipmentCollection = null;
    @XmlElementWrapper(name = "organizations")
    @XmlElement(name = "organization")
    private List<Long> organizationCollection = null;

    @XmlElement(name = "schedule")
    private String schedule = null;

    @XmlTransient
    private List<String> organizationIdentifyCollection = null;

    @XmlTransient
    private List<String> equipmentIdentifyCollection = null;

    /**
     * 
     */
    public ConWorkflowWorkInfoEntity() {
    }

    /**
     * 
     * @param associationId
     * @param fkWorkflowId
     * @param fkWorkId
     * @param skipFlag
     * @param workflowOrder 
     */
    public ConWorkflowWorkInfoEntity(Long associationId, Long fkWorkflowId, Long fkWorkId, boolean skipFlag, Integer workflowOrder) {
        this.associationId = associationId;
        this.fkWorkflowId = fkWorkflowId;
        this.fkWorkId = fkWorkId;
        this.skipFlag = skipFlag;
        this.workflowOrder = workflowOrder;
    }

    /**
     * 
     * @return 
     */
    public LongProperty associationIdProperty() {
        if (Objects.isNull(associationIdProperty)) {
            associationIdProperty = new SimpleLongProperty(associationId);
        }
        return associationIdProperty;
    }

    /**
     * 
     * @return 
     */
    public LongProperty fkWorkflowIdProperty() {
        if (Objects.isNull(fkWorkflowIdProperty)) {
            fkWorkflowIdProperty = new SimpleLongProperty(fkWorkflowId);
        }
        return fkWorkflowIdProperty;
    }

    /**
     * 
     * @return 
     */
    public LongProperty fkWorkIdProperty() {
        if (Objects.isNull(fkWorkIdProperty)) {
            fkWorkIdProperty = new SimpleLongProperty(fkWorkId);
        }
        return fkWorkIdProperty;
    }

    /**
     * 
     * @return 
     */
    public StringProperty workNameProperty() {
        if (Objects.isNull(workNameProperty)) {
            workNameProperty = new SimpleStringProperty(workName);
        }
        return workNameProperty;
    }

    /**
     * 
     * @return 
     */
    public BooleanProperty skipFlagProperty() {
        if (Objects.isNull(skipFlagProperty)) {
            skipFlagProperty = new SimpleBooleanProperty(skipFlag);
        }
        return skipFlagProperty;
    }

    /**
     * 
     * @return 
     */
    public IntegerProperty workflowOrderProperty() {
        if (Objects.isNull(workflowOrderProperty)) {
            workflowOrderProperty = new SimpleIntegerProperty(workflowOrder);
        }
        return workflowOrderProperty;
    }

    /**
     * 
     * @return 
     */
    public ObjectProperty<Date> standardStartTimeProperty() {
        if (Objects.isNull(standardStartTimeProperty)) {
            standardStartTimeProperty = new SimpleObjectProperty(standardStartTime);
        }
        return standardStartTimeProperty;
    }

    /**
     * 
     * @return 
     */
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

    /**
     * 
     * @return 
     */
    public Long getAssociationId() {
        if (Objects.nonNull(associationIdProperty)) {
            return associationIdProperty.get();
        }
        return associationId;
    }

    /**
     * 
     * @param associationId 
     */
    public void setAssociationId(Long associationId) {
        if (Objects.nonNull(associationIdProperty)) {
            associationIdProperty.set(associationId);
        } else {
            this.associationId = associationId;
        }
    }

    /**
     * 
     * @return 
     */
    public Long getFkWorkflowId() {
        if (Objects.nonNull(fkWorkflowIdProperty)) {
            return fkWorkflowIdProperty.get();
        }
        return fkWorkflowId;
    }

    /**
     * 
     * @param fkWorkflowId 
     */
    public void setFkWorkflowId(Long fkWorkflowId) {
        if (Objects.nonNull(fkWorkflowIdProperty)) {
            fkWorkflowIdProperty.set(fkWorkflowId);
        } else {
            this.fkWorkflowId = fkWorkflowId;
        }
    }

    /**
     * 
     * @return 
     */
    public Long getFkWorkId() {
        if (Objects.nonNull(fkWorkIdProperty)) {
            return fkWorkIdProperty.get();
        }
        return fkWorkId;
    }

    /**
     * 
     * @param fkWorkId 
     */
    public void setFkWorkId(Long fkWorkId) {
        if (Objects.nonNull(fkWorkIdProperty)) {
            fkWorkIdProperty.set(fkWorkId);
        } else {
            this.fkWorkId = fkWorkId;
        }
    }

    /**
     * 
     * @return 
     */
    public String getWorkName() {
        if (Objects.nonNull(workNameProperty)) {
            return workNameProperty.get();
        }
        return workName;
    }

    /**
     * 
     * @param workName 
     */
    public void setWorkName(String workName) {
        if (Objects.nonNull(workNameProperty)) {
            workNameProperty.set(workName);
        } else {
            this.workName = workName;
        }
    }

    /**
     * 工程の版数を取得する。
     *
     * @return 工程の版数
     */
    public Integer getWorkRev() {
        return this.workRev;
    }

    /**
     * 工程の版数を設定する。
     *
     * @param workRev 工程の版数
     */
    public void setWorkRev(Integer workRev) {
        this.workRev = workRev;
    }

    /**
     * 
     * @return 
     */
    public boolean getSkipFlag() {
        if (Objects.nonNull(skipFlagProperty)) {
            return skipFlagProperty.get();
        }
        return skipFlag;
    }

    /**
     * 
     * @param skipFlag 
     */
    public void setSkipFlag(boolean skipFlag) {
        if (Objects.nonNull(skipFlagProperty)) {
            skipFlagProperty.set(skipFlag);
        } else {
            this.skipFlag = skipFlag;
        }
    }

    /**
     * 
     * @return 
     */
    public Integer getWorkflowOrder() {
        if (Objects.nonNull(workflowOrderProperty)) {
            return workflowOrderProperty.get();
        }
        return workflowOrder;
    }

    /**
     * 
     * @param workflowOrder 
     */
    public void setWorkflowOrder(Integer workflowOrder) {
        if (Objects.nonNull(workflowOrderProperty)) {
            workflowOrderProperty.set(workflowOrder);
        } else {
            this.workflowOrder = workflowOrder;
        }
    }

    /**
     * 
     * @return 
     */
    public Date getStandardStartTime() {
        if (Objects.nonNull(standardStartTimeProperty)) {
            return standardStartTimeProperty.get();
        }
        return standardStartTime;
    }

    /**
     * 
     * @param standardStartTime 
     */
    public void setStandardStartTime(Date standardStartTime) {
        if (Objects.nonNull(standardStartTimeProperty)) {
            standardStartTimeProperty.set(standardStartTime);
        } else {
            this.standardStartTime = standardStartTime;
        }
    }

    /**
     * 
     * @return 
     */
    public Date getStandardEndTime() {
        if (Objects.nonNull(standardEndTimeProperty)) {
            return standardEndTimeProperty.get();
        }
        return standardEndTime;
    }

    /**
     * 
     * @param standardEndTime 
     */
    public void setStandardEndTime(Date standardEndTime) {
        if (Objects.nonNull(standardEndTimeProperty)) {
            standardEndTimeProperty.set(standardEndTime);
        } else {
            this.standardEndTime = standardEndTime;
        }
    }

    /**
     * 
     * @return 
     */
    public List<Long> getEquipmentCollection() {
        return equipmentCollection;
    }

    /**
     * 
     * @param equipmentCollection 
     */
    public void setEquipmentCollection(List<Long> equipmentCollection) {
        this.equipmentCollection = equipmentCollection;
    }

    public void addEquipmentIdentify(String equipmentIdentify) {
        if (StringUtils.isEmpty(equipmentIdentify)) {
            return;
        }

        if (Objects.isNull(this.equipmentIdentifyCollection)) {
            this.equipmentIdentifyCollection = new ArrayList<>();
        }
        this.equipmentIdentifyCollection.add(equipmentIdentify);
    }

    /**
     * 設備識別名
     * @return 設備名取得
     */
    public List<String> getEquipmentIdentifyCollection() {
        return equipmentIdentifyCollection;
    }

    /**
     * 設備識別名設定
     * @param equipmentIdentifyCollection 設備識別名
     */
    public void setEquipmentIdentifyCollection(List<String> equipmentIdentifyCollection) {
        this.equipmentIdentifyCollection = equipmentIdentifyCollection;
    }

    /**
     *
     * @return
     */
    public List<Long> getOrganizationCollection() {
        return organizationCollection;
    }

    /**
     * 
     * @param organizationCollection 
     */
    public void setOrganizationCollection(List<Long> organizationCollection) {
        this.organizationCollection = organizationCollection;
    }

    /**
     * 組織識別名追加
     * @param organizationIdentify 組織識別名
     */
    public void addOrganizationIdentify(String organizationIdentify)
    {
        if (StringUtils.isEmpty(organizationIdentify)) {
            return;
        }

        if (Objects.isNull(this.organizationIdentifyCollection)) {
            this.organizationIdentifyCollection = new ArrayList<>();
        }
        this.organizationIdentifyCollection.add(organizationIdentify);
    }

    /**
     * 組織識別名取得
     * @return 組織識別名
     */
    public List<String> getOrganizationIdentifyCollection() {
        return this.organizationIdentifyCollection;
    }

    /**
     * 組織識別名設定
     * @param organizationIdentifyCollection 組織識別名
     */
    public void setOrganizationIdentifyCollection(List<String> organizationIdentifyCollection) {
        this.organizationIdentifyCollection = organizationIdentifyCollection;
    }

    /**
     * タクトタイムを取得する。
     *
     * @return
     */
    public long getTaktTime() {
        if (Objects.isNull(this.getStandardStartTime()) || Objects.isNull(this.getStandardEndTime())) {
            return 0;
        }
        return this.getStandardEndTime().getTime() - this.getStandardStartTime().getTime();
    }

    /**
     * スケジュールを取得
     * @return スケジュール
     */
    public List<ScheduleConditionInfoEntity> getSchedule()
    {
        if (StringUtils.isEmpty(this.schedule)) {
            return new ArrayList<>();
        }
        return JsonUtils.jsonToObjects(this.schedule, ScheduleConditionInfoEntity[].class);
    }

    /**
     * スケジュールを設定
     * @param schedule スケジュール
     */
    public void setSchedule(List<ScheduleConditionInfoEntity> schedule) {
        if (Objects.isNull(schedule)) {
            this.schedule = null;
            return;
        }

        List<ScheduleConditionInfoEntity> newSchedule
                = schedule
                .stream()
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());

        if (newSchedule.isEmpty()) {
            this.schedule = null;
            return;
        }
        this.schedule = JsonUtils.objectsToJson(newSchedule);
    }

    /**
     * 
     */
    public void updateMember() {
        this.associationId = this.getAssociationId();
        this.fkWorkflowId = this.getFkWorkflowId();
        this.fkWorkId = this.getFkWorkId();
        this.skipFlag = this.getSkipFlag();
        this.standardStartTime = this.getStandardStartTime();
        this.standardEndTime = this.getStandardEndTime();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.workKbn);
        hash = 47 * hash + Objects.hashCode(this.fkWorkflowId);
        hash = 47 * hash + Objects.hashCode(this.fkWorkId);
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
        final ConWorkflowWorkInfoEntity other = (ConWorkflowWorkInfoEntity) obj;
        if (this.workKbn != other.workKbn) {
            return false;
        }
        if (!Objects.equals(this.fkWorkflowId, other.fkWorkflowId)) {
            return false;
        }
        if (!Objects.equals(this.fkWorkId, other.fkWorkId)) {
            return false;
        }
        if (!Objects.equals(this.schedule, other.schedule)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ConWorkflowWorkInfoEntity{")
                .append("workKbn=").append(this.workKbn)
                .append(", fkWorkflowId=").append(this.fkWorkflowId)
                .append(", fkWorkId=").append(this.fkWorkId)
                .append(", workName=").append(this.workName)
                .append(", workRev=").append(this.workRev)
                .append(", skipFlag=").append(this.skipFlag)
                .append(", workflowOrder=").append(this.workflowOrder)
                .append(", standardStartTime=").append(this.standardStartTime)
                .append(", standardEndTime=").append(this.standardEndTime)
                .append(", schedule=").append(this.schedule)
                .append("}")
                .toString();
    }

    /**
     * 表示される情報が一致するか調べる
     *
     * @param other
     * @return
     */
    public boolean equalsDisplayInfo(ConWorkflowWorkInfoEntity other) {
        boolean ret = false;
        if (Objects.equals(this.getWorkName(), other.getWorkName())
                && Objects.equals(this.getWorkflowOrder(), other.getWorkflowOrder())
                && Objects.equals(this.getSkipFlag(), other.getSkipFlag())
                && Objects.equals(this.getStandardStartTime(), other.getStandardStartTime())
                && Objects.equals(this.getStandardEndTime(), other.getStandardEndTime())
                && Objects.equals(this.getEquipmentCollection(), other.getEquipmentCollection())
                && Objects.equals(this.getOrganizationCollection(), other.getOrganizationCollection())
                && Objects.equals(this.schedule, other.schedule)) {
            ret = true;
        }
        return ret;
    }

    /**
     * 情報をコピーする
     *
     * @return
     */
    @Override
    public ConWorkflowWorkInfoEntity clone() {
        ConWorkflowWorkInfoEntity entity = new ConWorkflowWorkInfoEntity();

        entity.setWorkKbn(this.workKbn);

        entity.setAssociationId(this.associationId);
        entity.setFkWorkflowId(this.fkWorkflowId);
        entity.setFkWorkId(this.fkWorkId);
        entity.setWorkName(this.workName);
        entity.setWorkRev(this.workRev);
        entity.setSkipFlag(this.skipFlag);
        entity.setWorkflowOrder(this.workflowOrder);
        entity.setStandardStartTime((Date) this.standardStartTime.clone());
        entity.setStandardEndTime((Date) this.standardEndTime.clone());
        entity.schedule = this.schedule;

        if (Objects.nonNull(this.equipmentCollection)) {
            entity.setEquipmentCollection(new ArrayList<>());
            entity.getEquipmentCollection().addAll(this.equipmentCollection);
        }

        if (Objects.nonNull(this.organizationCollection)) {
            entity.setOrganizationCollection(new ArrayList<>());
            entity.getOrganizationCollection().addAll(this.organizationCollection);
        }

        return entity;
    }
}
