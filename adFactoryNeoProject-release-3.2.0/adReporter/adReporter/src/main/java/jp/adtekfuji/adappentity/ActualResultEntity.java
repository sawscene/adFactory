/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adappentity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import jp.adtekfuji.adappentity.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adreporter.utils.DateUtilsEx;

/**
 * 生産実績
 *
 * @author nar-nakamura
 */
public class ActualResultEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long actualId;
    private Long fkKanbanId;
    private String kanbanParentName;
    private String kanbanName;
    private String kanbanSubname;
    private Long fkWorkKanbanId;
    private String implementDatetime;
    private Long transactionId;
    private Long fkEquipmentId;
    private String equipmentParentName;
    private String equipmentParentIdentName;
    private String equipmentName;
    private String equipmentIdentName;
    private Long fkOrganizationId;
    private String organizationName;
    private String organizationIdentName;
    private String organizationParentName;
    private String organizationParentIdentName;
    private Long fkWorkflowId;
    private String workflowParentName;
    private String workflowName;
    private String workflowRevision;
    private Long fkWorkId;
    private String workParentName;
    private String workName;
    private KanbanStatusEnum actualStatus;
    private Integer taktTime;
    private Integer workingTime;
    private Boolean isSeparateWork;
    private String interruptReason;
    private String delayReason;
    private List<ActualPropertyEntity> propertyCollection = null;

    public ActualResultEntity() {
    }

    public Long getActualId() {
        return this.actualId;
    }

    public void setActualId(Long actualId) {
        this.actualId = actualId;
    }

    public Long getFkKanbanId() {
        return this.fkKanbanId;
    }

    public void setFkKanbanId(Long fkKanbanId) {
        this.fkKanbanId = fkKanbanId;
    }

    public String getKanbanParentName() {
        return this.kanbanParentName;
    }

    public void setKanbanParentName(String kanbanParentName) {
        this.kanbanParentName = kanbanParentName;
    }

    public String getKanbanName() {
        return kanbanName;
    }

    public void setKanbanName(String kanbanName) {
        this.kanbanName = kanbanName;
    }

    public String getKanbanSubname() {
        return this.kanbanSubname;
    }

    public void setKanbanSubname(String kanbanSubname) {
        this.kanbanSubname = kanbanSubname;
    }

    public Long getFkWorkKanbanId() {
        return this.fkWorkKanbanId;
    }

    public void setFkWorkKanbanId(Long fkWorkKanbanId) {
        this.fkWorkKanbanId = fkWorkKanbanId;
    }

    public Date getImplementDatetime() {
        return DateUtilsEx.toDate(this.implementDatetime);
    }

    public void setImplementDatetime(Date implementDatetime) {
        this.implementDatetime = DateUtilsEx.format(implementDatetime);
    }

    public Long getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getFkEquipmentId() {
        return fkEquipmentId;
    }

    public void setFkEquipmentId(Long fkEquipmentId) {
        this.fkEquipmentId = fkEquipmentId;
    }

    public String getEquipmentParentName() {
        return this.equipmentParentName;
    }

    public void setEquipmentParentName(String equipmentParentName) {
        this.equipmentParentName = equipmentParentName;
    }

    public String getEquipmentParentIdentName() {
        return this.equipmentParentIdentName;
    }

    public void setEquipmentParentIdentName(String equipmentParentIdentName) {
        this.equipmentParentIdentName = equipmentParentIdentName;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getEquipmentIdentName() {
        return this.equipmentIdentName;
    }

    public void setEquipmentIdentName(String equipmentIdentName) {
        this.equipmentIdentName = equipmentIdentName;
    }

    public Long getFkOrganizationId() {
        return this.fkOrganizationId;
    }

    public void setFkOrganizationId(Long fkOrganizationId) {
        this.fkOrganizationId = fkOrganizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationIdentName() {
        return this.organizationIdentName;
    }

    public void setOrganizationIdentName(String organizationIdentName) {
        this.organizationIdentName = organizationIdentName;
    }

    public String getOrganizationParentName() {
        return this.organizationParentName;
    }

    public void setOrganizationParentName(String organizationParentName) {
        this.organizationParentName = organizationParentName;
    }

    public String getOrganizationParentIdentName() {
        return this.organizationParentIdentName;
    }

    public void setOrganizationParentIdentName(String organizationParentIdentName) {
        this.organizationParentIdentName = organizationParentIdentName;
    }

    public Long getFkWorkflowId() {
        return fkWorkflowId;
    }

    public void setFkWorkflowId(Long fkWorkflowId) {
        this.fkWorkflowId = fkWorkflowId;
    }

    public String getWorkflowParentName() {
        return this.workflowParentName;
    }

    public void setWorkflowParentName(String workflowParentName) {
        this.workflowParentName = workflowParentName;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public String getWorkflowRevision() {
        return this.workflowRevision;
    }

    public void setWorkflowRevision(String workflowRevision) {
        this.workflowRevision = workflowRevision;
    }

    public Long getFkWorkId() {
        return this.fkWorkId;
    }

    public void setFkWorkId(Long fkWorkId) {
        this.fkWorkId = fkWorkId;
    }

    public String getWorkParentName() {
        return workParentName;
    }

    public void setWorkParentName(String workParentName) {
        this.workParentName = workParentName;
    }

    public String getWorkName() {
        return this.workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public KanbanStatusEnum getActualStatus() {
        return this.actualStatus;
    }

    public void setActualStatus(KanbanStatusEnum actualStatus) {
        this.actualStatus = actualStatus;
    }

    public Integer getTaktTime() {
        return taktTime;
    }

    public void setTaktTime(Integer taktTime) {
        this.taktTime = taktTime;
    }

    public Integer getWorkingTime() {
        return this.workingTime;
    }

    public void setWorkingTime(Integer workingTime) {
        this.workingTime = workingTime;
    }

    public Boolean getIsSeparateWork() {
        return isSeparateWork;
    }

    public void setIsSeparateWork(Boolean isSeparateWork) {
        this.isSeparateWork = isSeparateWork;
    }

    public String getInterruptReason() {
        return this.interruptReason;
    }

    public void setInterruptReason(String interruptReason) {
        this.interruptReason = interruptReason;
    }

    public String getDelayReason() {
        return this.delayReason;
    }

    public void setDelayReason(String delayReason) {
        this.delayReason = delayReason;
    }

    public List<ActualPropertyEntity> getPropertyCollection() {
        return this.propertyCollection;
    }

    public void setPropertyCollection(List<ActualPropertyEntity> propertyCollection) {
        this.propertyCollection = propertyCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (actualId != null ? actualId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ActualResultEntity)) {
            return false;
        }
        ActualResultEntity other = (ActualResultEntity) object;
        if ((this.actualId == null && other.actualId != null) || (this.actualId != null && !this.actualId.equals(other.actualId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ActualResultEntity{" + "actualId=" + this.actualId + ", fkKanbanId=" + this.fkKanbanId + ", fkWorkKanbanId=" + this.fkWorkKanbanId
                + ", implementDatetime=" + this.implementDatetime + ", transactionId=" + this.transactionId + ", fkEquipmentId=" + this.fkEquipmentId
                + ", fkOrganizationId=" + this.fkOrganizationId + ", fkWorkflowId=" + this.fkWorkflowId + ", fkWorkId=" + this.fkWorkId
                + ", actualStatus=" + this.actualStatus + ", interruptReason=" + this.interruptReason + ", delayReason=" + this.delayReason + '}';
    }
}
