/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.workflow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "con_workflow_separatework")
@XmlRootElement(name = "conWorkflowSeparatework")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "ConWorkflowSeparateworkEntity.findAll", query = "SELECT c FROM ConWorkflowSeparateworkEntity c ORDER BY c.workflowOrder"),
    @NamedQuery(name = "ConWorkflowSeparateworkEntity.findByAssociationId", query = "SELECT c FROM ConWorkflowSeparateworkEntity c WHERE c.associationId = :associationId ORDER BY c.workflowOrder"),
    @NamedQuery(name = "ConWorkflowSeparateworkEntity.findByFkWorkflowId", query = "SELECT c FROM ConWorkflowSeparateworkEntity c WHERE c.fkWorkflowId = :fkWorkflowId ORDER BY c.workflowOrder"),
    @NamedQuery(name = "ConWorkflowSeparateworkEntity.findByFkWorkId", query = "SELECT c FROM ConWorkflowSeparateworkEntity c WHERE c.fkWorkId = :fkWorkId ORDER BY c.workflowOrder"),
    @NamedQuery(name = "ConWorkflowSeparateworkEntity.findBySkipFlag", query = "SELECT c FROM ConWorkflowSeparateworkEntity c WHERE c.skipFlag = :skipFlag ORDER BY c.workflowOrder"),
    @NamedQuery(name = "ConWorkflowSeparateworkEntity.findByWorkflowOrder", query = "SELECT c FROM ConWorkflowSeparateworkEntity c WHERE c.workflowOrder = :workflowOrder ORDER BY c.workflowOrder"),
    @NamedQuery(name = "ConWorkflowSeparateworkEntity.findByStandardStartTime", query = "SELECT c FROM ConWorkflowSeparateworkEntity c WHERE c.standardStartTime = :standardStartTime ORDER BY c.workflowOrder"),
    @NamedQuery(name = "ConWorkflowSeparateworkEntity.findByStandardEndTime", query = "SELECT c FROM ConWorkflowSeparateworkEntity c WHERE c.standardEndTime = :standardEndTime ORDER BY c.workflowOrder"),
    @NamedQuery(name = "ConWorkflowSeparateworkEntity.removeByFkWorkflowId", query = "DELETE FROM ConWorkflowSeparateworkEntity c WHERE c.fkWorkflowId = :fkWorkflowId")})
public class ConWorkflowSeparateworkEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "association_id")
    private Long associationId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_workflow_id")
    private Long fkWorkflowId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_work_id")
    private Long fkWorkId;
    @Transient
    private String workName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "skip_flag")
    private Boolean skipFlag;
    @Basic(optional = false)
    @NotNull
    @Column(name = "workflow_order")
    private Integer workflowOrder;
    @Column(name = "standard_start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date standardStartTime;
    @Column(name = "standard_end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date standardEndTime;
    @XmlElementWrapper(name = "equipments")
    @XmlElement(name = "equipment")
    @Transient
    private List<Long> equipmentCollection = null;
    @XmlElementWrapper(name = "organizations")
    @XmlElement(name = "organization")
    @Transient
    private List<Long> organizationCollection = null;

    public ConWorkflowSeparateworkEntity() {
    }

    public ConWorkflowSeparateworkEntity(ConWorkflowSeparateworkEntity in) {
        this.fkWorkflowId = in.fkWorkflowId;
        this.fkWorkId = in.fkWorkId;
        this.skipFlag = in.skipFlag;
        this.workflowOrder = in.workflowOrder;
        this.standardStartTime = in.standardStartTime;
        this.standardEndTime = in.standardEndTime;
        this.equipmentCollection = in.getEquipmentCollection();
        this.organizationCollection = in.getOrganizationCollection();
    }

    public ConWorkflowSeparateworkEntity(Long fkWorkflowId, Long fkWorkId, Boolean skipFlag, Integer workflowOrder, Date standardStartTime, Date standardEndTime) {
        this.fkWorkflowId = fkWorkflowId;
        this.fkWorkId = fkWorkId;
        this.skipFlag = skipFlag;
        this.workflowOrder = workflowOrder;
        this.standardStartTime = standardStartTime;
        this.standardEndTime = standardEndTime;
    }

    public Long getAssociationId() {
        return associationId;
    }

    public void setAssociationId(Long associationId) {
        this.associationId = associationId;
    }

    public Long getFkWorkflowId() {
        return fkWorkflowId;
    }

    public void setFkWorkflowId(Long fkWorkflowId) {
        this.fkWorkflowId = fkWorkflowId;
    }

    public Long getFkWorkId() {
        return fkWorkId;
    }

    public void setFkWorkId(Long fkWorkId) {
        this.fkWorkId = fkWorkId;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public Boolean getSkipFlag() {
        return skipFlag;
    }

    public void setSkipFlag(Boolean skipFlag) {
        this.skipFlag = skipFlag;
    }

    public Integer getWorkflowOrder() {
        return workflowOrder;
    }

    public void setWorkflowOrder(Integer workflowOrder) {
        this.workflowOrder = workflowOrder;
    }

    public Date getStandardStartTime() {
        return standardStartTime;
    }

    public void setStandardStartTime(Date standardStartTime) {
        this.standardStartTime = standardStartTime;
    }

    public Date getStandardEndTime() {
        return standardEndTime;
    }

    public void setStandardEndTime(Date standardEndTime) {
        this.standardEndTime = standardEndTime;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (associationId != null ? associationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConWorkflowSeparateworkEntity)) {
            return false;
        }
        ConWorkflowSeparateworkEntity other = (ConWorkflowSeparateworkEntity) object;
        if ((this.associationId == null && other.associationId != null) || (this.associationId != null && !this.associationId.equals(other.associationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ConWorkflowSeparateworkEntity{" + "associationId=" + associationId + ", fkWorkflowId=" + fkWorkflowId + ", fkWorkId=" + fkWorkId + ", skipFlag=" + skipFlag + ", workflowOrder=" + workflowOrder + ", standardStartTime=" + standardStartTime + ", standardEndTime=" + standardEndTime + '}';
    }

}
