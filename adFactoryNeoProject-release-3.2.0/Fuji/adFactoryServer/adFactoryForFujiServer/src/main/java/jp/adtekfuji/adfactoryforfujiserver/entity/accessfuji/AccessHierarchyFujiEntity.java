/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.entity.accessfuji;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.forfujiapp.entity.accessfuji.AccessHierarchyFujiTypeEnum;

/**
 *
 * @author j.min
 */
@Entity
@Table(name = "tre_access_hierarchy_fuji")
@XmlRootElement(name = "accessHierarchyFuji")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "AccessHierarchyFujiEntity.find", query = "SELECT a FROM AccessHierarchyFujiEntity a WHERE a.typeId = :type AND a.fkHierarchyId = :id"),
    @NamedQuery(name = "AccessHierarchyFujiEntity.findIds", query = "SELECT a FROM AccessHierarchyFujiEntity a WHERE a.typeId = :type AND a.fkHierarchyId IN :ids"),
    @NamedQuery(name = "AccessHierarchyFujiEntity.getCount", query = "SELECT COUNT(a) FROM AccessHierarchyFujiEntity a WHERE a.typeId = :type AND a.fkHierarchyId = :id"),
    @NamedQuery(name = "AccessHierarchyFujiEntity.check", query = "SELECT COUNT(a) FROM AccessHierarchyFujiEntity a WHERE a.typeId = :type AND a.fkHierarchyId = :id AND a.fkOrganizationId = :data"),
})
public class AccessHierarchyFujiEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "type_id")
    private AccessHierarchyFujiTypeEnum typeId;
    @Id
    @Column(name = "fk_hierarchy_id")
    private Long fkHierarchyId;
    @Id
    @Column(name = "fk_organization_id")
    private Long fkOrganizationId;

    public AccessHierarchyFujiEntity() {
    }

    public AccessHierarchyFujiEntity(AccessHierarchyFujiTypeEnum typeId, Long fkHierarchyId, Long fkOrganizationId) {
        this.typeId = typeId;
        this.fkHierarchyId = fkHierarchyId;
        this.fkOrganizationId = fkOrganizationId;
    }

    public AccessHierarchyFujiTypeEnum getTypeId() {
        return typeId;
    }

    public void setTypeId(AccessHierarchyFujiTypeEnum typeId) {
        this.typeId = typeId;
    }

    public Long getFkHierarchyId() {
        return fkHierarchyId;
    }

    public void setFkHierarchyId(Long fkHierarchyId) {
        this.fkHierarchyId = fkHierarchyId;
    }

    public Long getFkOrganizationId() {
        return fkOrganizationId;
    }

    public void setFkOrganizationId(Long fkOrganizationId) {
        this.fkOrganizationId = fkOrganizationId;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.typeId);
        hash = 89 * hash + Objects.hashCode(this.fkHierarchyId);
        hash = 89 * hash + Objects.hashCode(this.fkOrganizationId);
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
        final AccessHierarchyFujiEntity other = (AccessHierarchyFujiEntity) obj;
        if (!Objects.equals(this.typeId, other.typeId)) {
            return false;
        }
        if (!Objects.equals(this.fkHierarchyId, other.fkHierarchyId)) {
            return false;
        }
        return Objects.equals(this.fkOrganizationId, other.fkOrganizationId);
    }

    @Override
    public String toString() {
        return "AccessHierarchyFujiEntity{" + "typeId=" + typeId + ", fkHierarchyId=" + fkHierarchyId + ", fkOrganizationId=" + fkOrganizationId + '}';
    }
}
