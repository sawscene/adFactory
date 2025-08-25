/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.workflow;

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
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "mst_kanban_property_template")
@XmlRootElement(name = "kanbanPropertyTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "KanbanPropertyTemplateEntity.findAll", query = "SELECT k FROM KanbanPropertyTemplateEntity k ORDER BY k.propertyOrder"),
    @NamedQuery(name = "KanbanPropertyTemplateEntity.findByWorkPropId", query = "SELECT k FROM KanbanPropertyTemplateEntity k WHERE k.kanbanPropTemplateId = :kanbanPropTemplateId ORDER BY k.propertyOrder"),
    @NamedQuery(name = "KanbanPropertyTemplateEntity.findByFkMasterId", query = "SELECT k FROM KanbanPropertyTemplateEntity k WHERE k.fkMasterId = :fkMasterId ORDER BY k.propertyOrder"),
    @NamedQuery(name = "KanbanPropertyTemplateEntity.findByWorkPropName", query = "SELECT k FROM KanbanPropertyTemplateEntity k WHERE k.propertyName = :propertyName ORDER BY k.propertyOrder"),
    @NamedQuery(name = "KanbanPropertyTemplateEntity.findByWorkPropType", query = "SELECT k FROM KanbanPropertyTemplateEntity k WHERE k.propertyType = :propertyType ORDER BY k.propertyOrder"),
    @NamedQuery(name = "KanbanPropertyTemplateEntity.findByWorkPropInitialValue", query = "SELECT k FROM KanbanPropertyTemplateEntity k WHERE k.propInitialValue = :workPropInitialValue ORDER BY k.propertyOrder"),
    @NamedQuery(name = "KanbanPropertyTemplateEntity.findByWorkPropOrder", query = "SELECT k FROM KanbanPropertyTemplateEntity k WHERE k.propertyOrder = :propertyOrder ORDER BY k.propertyOrder"),
    @NamedQuery(name = "KanbanPropertyTemplateEntity.removeByFkMasterId", query = "DELETE FROM KanbanPropertyTemplateEntity k WHERE k.fkMasterId = :fkMasterId")})
public class KanbanPropertyTemplateEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "kanban_prop_template_id")
    private Long kanbanPropTemplateId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_master_id")
    private Long fkMasterId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "property_name")
    private String propertyName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "property_type")
    private String propertyType;
    @Size(max = 2147483647)
    @Column(name = "property_initial_value")
    private String propInitialValue;
    @Column(name = "property_order")
    private Integer propertyOrder;

    public KanbanPropertyTemplateEntity() {
    }

    public KanbanPropertyTemplateEntity(KanbanPropertyTemplateEntity in) {
        this.fkMasterId = in.fkMasterId;
        this.propertyName = in.propertyName;
        this.propertyType = in.propertyType;
        this.propInitialValue = in.propInitialValue;
        this.propertyOrder = in.propertyOrder;
    }

    public KanbanPropertyTemplateEntity(Long fkMasterId, String workPropName, String workPropType, String workPropInitialValue, Integer workPropOrder) {
        this.fkMasterId = fkMasterId;
        this.propertyName = workPropName;
        this.propertyType = workPropType;
        this.propInitialValue = workPropInitialValue;
        this.propertyOrder = workPropOrder;
    }

    public Long getKanbanPropTemplateId() {
        return kanbanPropTemplateId;
    }

    public void setKanbanPropTemplateId(Long workPropId) {
        this.kanbanPropTemplateId = workPropId;
    }

    public Long getFkMasterId() {
        return fkMasterId;
    }

    public void setFkMasterId(Long fkMasterId) {
        this.fkMasterId = fkMasterId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getPropInitialValue() {
        return propInitialValue;
    }

    public void setPropInitialValue(String propInitialValue) {
        this.propInitialValue = propInitialValue;
    }

    public Integer getPropertyOrder() {
        return propertyOrder;
    }

    public void setPropertyOrder(Integer propertyOrder) {
        this.propertyOrder = propertyOrder;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (kanbanPropTemplateId != null ? kanbanPropTemplateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof KanbanPropertyTemplateEntity)) {
            return false;
        }
        KanbanPropertyTemplateEntity other = (KanbanPropertyTemplateEntity) object;
        if ((this.kanbanPropTemplateId == null && other.kanbanPropTemplateId != null) || (this.kanbanPropTemplateId != null && !this.kanbanPropTemplateId.equals(other.kanbanPropTemplateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryserver.entity.workflow.KanbanPropertyTemplateEntity[ workPropId=" + kanbanPropTemplateId + " ]";
    }
    
}
