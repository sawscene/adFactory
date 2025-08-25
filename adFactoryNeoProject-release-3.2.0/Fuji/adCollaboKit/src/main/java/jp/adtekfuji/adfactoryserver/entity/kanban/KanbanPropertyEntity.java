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
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "trn_kanban_property")
@XmlRootElement(name = "kanbanProperty")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "KanbanPropertyEntity.findAll", query = "SELECT k FROM KanbanPropertyEntity k"),
    @NamedQuery(name = "KanbanPropertyEntity.findByKanbannPropertyId", query = "SELECT k FROM KanbanPropertyEntity k WHERE k.kanbannPropertyId = :kanbannPropertyId"),
    @NamedQuery(name = "KanbanPropertyEntity.findByFkKanbanId", query = "SELECT k FROM KanbanPropertyEntity k WHERE k.fkKanbanId = :fkKanbanId"),
    @NamedQuery(name = "KanbanPropertyEntity.findByKanbanPropertyName", query = "SELECT k FROM KanbanPropertyEntity k WHERE k.kanbanPropertyName = :kanbanPropertyName"),
    @NamedQuery(name = "KanbanPropertyEntity.findByKanbanPropertyType", query = "SELECT k FROM KanbanPropertyEntity k WHERE k.kanbanPropertyType = :kanbanPropertyType"),
    @NamedQuery(name = "KanbanPropertyEntity.findByKanbanPropertyValue", query = "SELECT k FROM KanbanPropertyEntity k WHERE k.kanbanPropertyValue = :kanbanPropertyValue"),
    @NamedQuery(name = "KanbanPropertyEntity.findByKanbanPropertyOrder", query = "SELECT k FROM KanbanPropertyEntity k WHERE k.kanbanPropertyOrder = :kanbanPropertyOrder"),
    @NamedQuery(name = "KanbanPropertyEntity.removeByFkKanbanId", query = "DELETE FROM KanbanPropertyEntity k WHERE k.fkKanbanId = :fkKanbanId")})
public class KanbanPropertyEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "kanbann_property_id")
    private Long kanbannPropertyId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_kanban_id")
    private Long fkKanbanId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "kanban_property_name")
    private String kanbanPropertyName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "kanban_property_type")
    private String kanbanPropertyType;
    @Size(max = 2147483647)
    @Column(name = "kanban_property_value")
    private String kanbanPropertyValue;
    @Column(name = "kanban_property_order")
    private Integer kanbanPropertyOrder;

    public KanbanPropertyEntity() {
    }

    public KanbanPropertyEntity(KanbanPropertyEntity in) {
        this.kanbannPropertyId = in.kanbannPropertyId;
        this.fkKanbanId = in.fkKanbanId;
        this.kanbanPropertyName = in.kanbanPropertyName;
        this.kanbanPropertyType = in.kanbanPropertyType;
        this.kanbanPropertyValue = in.kanbanPropertyValue;
        this.kanbanPropertyOrder = in.kanbanPropertyOrder;
    }

    public KanbanPropertyEntity(Long fkKanbanId, String kanbanPropertyName, String kanbanPropertyType, String kanbanPropertyValue, Integer kanbanPropertyOrder) {
        this.fkKanbanId = fkKanbanId;
        this.kanbanPropertyName = kanbanPropertyName;
        this.kanbanPropertyType = kanbanPropertyType;
        this.kanbanPropertyValue = kanbanPropertyValue;
        this.kanbanPropertyOrder = kanbanPropertyOrder;
    }

    public Long getKanbannPropertyId() {
        return kanbannPropertyId;
    }

    public void setKanbannPropertyId(Long kanbannPropertyId) {
        this.kanbannPropertyId = kanbannPropertyId;
    }

    public Long getFkKanbanId() {
        return fkKanbanId;
    }

    public void setFkKanbanId(Long fkKanbanId) {
        this.fkKanbanId = fkKanbanId;
    }

    public String getKanbanPropertyName() {
        return kanbanPropertyName;
    }

    public void setKanbanPropertyName(String kanbanPropertyName) {
        this.kanbanPropertyName = kanbanPropertyName;
    }

    public String getKanbanPropertyType() {
        return kanbanPropertyType;
    }

    public void setKanbanPropertyType(String kanbanPropertyType) {
        this.kanbanPropertyType = kanbanPropertyType;
    }

    public String getKanbanPropertyValue() {
        return kanbanPropertyValue;
    }

    public void setKanbanPropertyValue(String kanbanPropertyValue) {
        this.kanbanPropertyValue = kanbanPropertyValue;
    }

    public Integer getKanbanPropertyOrder() {
        return kanbanPropertyOrder;
    }

    public void setKanbanPropertyOrder(Integer kanbanPropertyOrder) {
        this.kanbanPropertyOrder = kanbanPropertyOrder;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (kanbannPropertyId != null ? kanbannPropertyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof KanbanPropertyEntity)) {
            return false;
        }
        KanbanPropertyEntity other = (KanbanPropertyEntity) object;
        if ((this.kanbannPropertyId == null && other.kanbannPropertyId != null) || (this.kanbannPropertyId != null && !this.kanbannPropertyId.equals(other.kanbannPropertyId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "KanbanPropertyEntity{" + "kanbannPropertyId=" + kanbannPropertyId + ", fkKanbanId=" + fkKanbanId + ", kanbanPropertyName=" + kanbanPropertyName + ", kanbanPropertyType=" + kanbanPropertyType + ", kanbanPropertyValue=" + kanbanPropertyValue + ", kanbanPropertyOrder=" + kanbanPropertyOrder + '}';
    }

}
