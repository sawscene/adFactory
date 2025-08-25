/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.kanban;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "con_kanban_hierarchy")
@XmlRootElement(name = "conKanbanHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "ConKanbanHierarchyEntity.countChild", query = "SELECT COUNT(c.conKanbanHierarchyEntityPK.fkKanbanId) FROM ConKanbanHierarchyEntity c WHERE c.conKanbanHierarchyEntityPK.fkKanbanHierarchyId = :fkKanbanHierarchyId"),
    @NamedQuery(name = "ConKanbanHierarchyEntity.findAll", query = "SELECT c FROM ConKanbanHierarchyEntity c"),
    @NamedQuery(name = "ConKanbanHierarchyEntity.findByFkKanbanHierarchyId", query = "SELECT c FROM ConKanbanHierarchyEntity c WHERE c.conKanbanHierarchyEntityPK.fkKanbanHierarchyId = :fkKanbanHierarchyId"),
    @NamedQuery(name = "ConKanbanHierarchyEntity.findByFkKanbanId", query = "SELECT c FROM ConKanbanHierarchyEntity c WHERE c.conKanbanHierarchyEntityPK.fkKanbanId = :fkKanbanId"),
    @NamedQuery(name = "ConKanbanHierarchyEntity.removeByFkKanbanId", query = "DELETE FROM ConKanbanHierarchyEntity c WHERE c.conKanbanHierarchyEntityPK.fkKanbanId = :fkKanbanId")})
public class ConKanbanHierarchyEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ConKanbanHierarchyEntityPK conKanbanHierarchyEntityPK;

    public ConKanbanHierarchyEntity() {
    }

    public ConKanbanHierarchyEntity(long fkKanbanHierarchyId, long fkKanbanId) {
        this.conKanbanHierarchyEntityPK = new ConKanbanHierarchyEntityPK(fkKanbanHierarchyId, fkKanbanId);
    }

    public ConKanbanHierarchyEntityPK getConKanbanHierarchyEntityPK() {
        return conKanbanHierarchyEntityPK;
    }

    public void setConKanbanHierarchyEntityPK(ConKanbanHierarchyEntityPK conKanbanHierarchyEntityPK) {
        this.conKanbanHierarchyEntityPK = conKanbanHierarchyEntityPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (conKanbanHierarchyEntityPK != null ? conKanbanHierarchyEntityPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConKanbanHierarchyEntity)) {
            return false;
        }
        ConKanbanHierarchyEntity other = (ConKanbanHierarchyEntity) object;
        if ((this.conKanbanHierarchyEntityPK == null && other.conKanbanHierarchyEntityPK != null) || (this.conKanbanHierarchyEntityPK != null && !this.conKanbanHierarchyEntityPK.equals(other.conKanbanHierarchyEntityPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ConKanbanHierarchyEntity{" + "conKanbanHierarchyEntityPK=" + conKanbanHierarchyEntityPK + '}';
    }

}
