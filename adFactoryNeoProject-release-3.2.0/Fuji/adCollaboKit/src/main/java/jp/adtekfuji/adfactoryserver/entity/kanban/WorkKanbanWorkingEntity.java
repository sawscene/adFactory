/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.kanban;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "trn_work_kanban_working")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "WorkKanbanWorkingEntity.findAll", query = "SELECT w FROM WorkKanbanWorkingEntity w"),
    @NamedQuery(name = "WorkKanbanWorkingEntity.findByWorkKanbanWorkingId", query = "SELECT w FROM WorkKanbanWorkingEntity w WHERE w.workKanbanWorkingId = :workKanbanWorkingId"),
    @NamedQuery(name = "WorkKanbanWorkingEntity.findByFkWorkKanbanId", query = "SELECT w FROM WorkKanbanWorkingEntity w WHERE w.fkWorkKanbanId = :fkWorkKanbanId"),
    @NamedQuery(name = "WorkKanbanWorkingEntity.findByFkEquipmentId", query = "SELECT w FROM WorkKanbanWorkingEntity w WHERE w.fkEquipmentId = :fkEquipmentId"),
    @NamedQuery(name = "WorkKanbanWorkingEntity.findByFkOrganizationId", query = "SELECT w FROM WorkKanbanWorkingEntity w WHERE w.fkOrganizationId = :fkOrganizationId"),
    @NamedQuery(name = "WorkKanbanWorkingEntity.countByFkWorkKanbanId", query = "SELECT COUNT(w.fkWorkKanbanId) FROM WorkKanbanWorkingEntity w WHERE w.fkWorkKanbanId = :fkWorkKanbanId")})
public class WorkKanbanWorkingEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "work_kanban_working_id")
    private Long workKanbanWorkingId;
    @Column(name = "fk_work_kanban_id")
    private Long fkWorkKanbanId;
    @Column(name = "fk_equipment_id")
    private Long fkEquipmentId;
    @Column(name = "fk_organization_id")
    private Long fkOrganizationId;

    public WorkKanbanWorkingEntity() {
    }

    public WorkKanbanWorkingEntity(Long fkWorkKanbanId, Long fkEquipmentId, Long fkOrganizationId) {
        this.fkWorkKanbanId = fkWorkKanbanId;
        this.fkEquipmentId = fkEquipmentId;
        this.fkOrganizationId = fkOrganizationId;
    }

    public Long getWorkKanbanWorkingId() {
        return workKanbanWorkingId;
    }

    public void setWorkKanbanWorkingId(Long workKanbanWorkingId) {
        this.workKanbanWorkingId = workKanbanWorkingId;
    }

    public Long getFkWorkKanbanId() {
        return fkWorkKanbanId;
    }

    public void setFkWorkKanbanId(Long fkWorkKanbanId) {
        this.fkWorkKanbanId = fkWorkKanbanId;
    }

    public Long getFkEquipmentId() {
        return fkEquipmentId;
    }

    public void setFkEquipmentId(Long fkEquipmentId) {
        this.fkEquipmentId = fkEquipmentId;
    }

    public Long getFkOrganizationId() {
        return fkOrganizationId;
    }

    public void setFkOrganizationId(Long fkOrganizationId) {
        this.fkOrganizationId = fkOrganizationId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (workKanbanWorkingId != null ? workKanbanWorkingId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorkKanbanWorkingEntity)) {
            return false;
        }
        WorkKanbanWorkingEntity other = (WorkKanbanWorkingEntity) object;
        if ((this.workKanbanWorkingId == null && other.workKanbanWorkingId != null) || (this.workKanbanWorkingId != null && !this.workKanbanWorkingId.equals(other.workKanbanWorkingId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "WorkKanbanWorkingEntity{" + "workKanbanWorkingId=" + workKanbanWorkingId + ", fkWorkKanbanId=" + fkWorkKanbanId + ", fkEquipmentId=" + fkEquipmentId + ", fkOrganizationId=" + fkOrganizationId + '}';
    }

}
