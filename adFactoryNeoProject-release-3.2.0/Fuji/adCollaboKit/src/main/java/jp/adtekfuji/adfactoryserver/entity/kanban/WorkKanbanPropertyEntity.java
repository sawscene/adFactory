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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 工程カンバンプロパティエンティティクラス
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "trn_work_kanban_property")
@XmlRootElement(name = "workKanbanProperty")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "WorkKanbanPropertyEntity.findAll", query = "SELECT w FROM WorkKanbanPropertyEntity w"),
    @NamedQuery(name = "WorkKanbanPropertyEntity.findByWorkKanbannPropertyId", query = "SELECT w FROM WorkKanbanPropertyEntity w WHERE w.workKanbannPropertyId = :workKanbannPropertyId"),
    @NamedQuery(name = "WorkKanbanPropertyEntity.findByFkWorkKanbanId", query = "SELECT w FROM WorkKanbanPropertyEntity w WHERE w.fkWorkKanbanId = :fkWorkKanbanId"),
    @NamedQuery(name = "WorkKanbanPropertyEntity.findByKanbanPropertyName", query = "SELECT w FROM WorkKanbanPropertyEntity w WHERE w.workKanbanPropName = :workKanbanPropName"),
    @NamedQuery(name = "WorkKanbanPropertyEntity.findByKanbanPropertyType", query = "SELECT w FROM WorkKanbanPropertyEntity w WHERE w.workKanbanPropType = :workKanbanPropType"),
    @NamedQuery(name = "WorkKanbanPropertyEntity.findByKanbanPropertyValue", query = "SELECT w FROM WorkKanbanPropertyEntity w WHERE w.workKanbanPropValue = :workKanbanPropValue"),
    @NamedQuery(name = "WorkKanbanPropertyEntity.findByKanbanPropertyOrder", query = "SELECT w FROM WorkKanbanPropertyEntity w WHERE w.workKanbanPropOrder = :workKanbanPropOrder"),
    @NamedQuery(name = "WorkKanbanPropertyEntity.removeByFkWorkKanbanId", query = "DELETE FROM WorkKanbanPropertyEntity w WHERE w.fkWorkKanbanId = :fkWorkKanbanId")})
public class WorkKanbanPropertyEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "work_kanbann_property_id")
    private Long workKanbannPropertyId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_work_kanban_id")
    private long fkWorkKanbanId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "kanban_property_name")
    private String workKanbanPropName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "kanban_property_type")
    private String workKanbanPropType;
    @Size(max = 2147483647)
    @Column(name = "kanban_property_value")
    private String workKanbanPropValue;
    @Column(name = "kanban_property_order")
    private Integer workKanbanPropOrder;

    public WorkKanbanPropertyEntity() {
    }

    public WorkKanbanPropertyEntity(long fkWorkKanbanId, String workkanbanPropertyName, String workkanbanPropertyType, String workkanbanPropertyValue, Integer workkanbanPropertyOrder) {
        this.fkWorkKanbanId = fkWorkKanbanId;
        this.workKanbanPropName = workkanbanPropertyName;
        this.workKanbanPropType = workkanbanPropertyType;
        this.workKanbanPropValue = workkanbanPropertyValue;
        this.workKanbanPropOrder = workkanbanPropertyOrder;
    }

    public Long getWorkKanbannPropertyId() {
        return workKanbannPropertyId;
    }

    public void setWorkKanbannPropertyId(Long workKanbannPropertyId) {
        this.workKanbannPropertyId = workKanbannPropertyId;
    }

    public long getFkWorkKanbanId() {
        return fkWorkKanbanId;
    }

    public void setFkWorkKanbanId(long fkWorkKanbanId) {
        this.fkWorkKanbanId = fkWorkKanbanId;
    }

    public String getWorkKanbanPropName() {
        return workKanbanPropName;
    }

    public void setWorkKanbanPropName(String workKanbanPropName) {
        this.workKanbanPropName = workKanbanPropName;
    }

    public String getWorkKanbanPropType() {
        return workKanbanPropType;
    }

    public void setWorkKanbanPropType(String workKanbanPropType) {
        this.workKanbanPropType = workKanbanPropType;
    }

    public String getWorkKanbanPropValue() {
        return workKanbanPropValue;
    }

    public void setWorkKanbanPropValue(String workKanbanPropValue) {
        this.workKanbanPropValue = workKanbanPropValue;
    }

    public Integer getWorkKanbanPropOrder() {
        return workKanbanPropOrder;
    }

    public void setWorkKanbanPropOrder(Integer workKanbanPropOrder) {
        this.workKanbanPropOrder = workKanbanPropOrder;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (workKanbannPropertyId != null ? workKanbannPropertyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorkKanbanPropertyEntity)) {
            return false;
        }
        WorkKanbanPropertyEntity other = (WorkKanbanPropertyEntity) object;
        return !((this.workKanbannPropertyId == null && other.workKanbannPropertyId != null) || (this.workKanbannPropertyId != null && !this.workKanbannPropertyId.equals(other.workKanbannPropertyId)));
    }

    @Override
    public String toString() {
        return "WorkKanbanPropertyEntity{" + "workKanbannPropertyId=" + workKanbannPropertyId + ", fkWorkKanbanId=" + fkWorkKanbanId +
                ", workKanbanPropName=" + workKanbanPropName + ", workKanbanPropType=" + workKanbanPropType + ", workKanbanPropValue=" + workKanbanPropValue + ", workKanbanPropOrder=" + workKanbanPropOrder + '}';
    }
}
