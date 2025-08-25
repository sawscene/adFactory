/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.organization;

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
@Table(name = "mst_organization_property")
@XmlRootElement(name = "organizationProperty")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "OrganizationPropertyEntity.findAll", query = "SELECT o FROM OrganizationPropertyEntity o ORDER BY o.organizationPropOrder"),
    @NamedQuery(name = "OrganizationPropertyEntity.findByOrganizationPropId", query = "SELECT o FROM OrganizationPropertyEntity o WHERE o.organizationPropId = :organizationPropId ORDER BY o.organizationPropOrder"),
    @NamedQuery(name = "OrganizationPropertyEntity.findByFkMasterId", query = "SELECT o FROM OrganizationPropertyEntity o WHERE o.fkMasterId = :fkMasterId ORDER BY o.organizationPropOrder"),
    @NamedQuery(name = "OrganizationPropertyEntity.findByOrganizationPropName", query = "SELECT o FROM OrganizationPropertyEntity o WHERE o.organizationPropName = :organizationPropName ORDER BY o.organizationPropOrder"),
    @NamedQuery(name = "OrganizationPropertyEntity.findByOrganizationPropType", query = "SELECT o FROM OrganizationPropertyEntity o WHERE o.organizationPropType = :organizationPropType ORDER BY o.organizationPropOrder"),
    @NamedQuery(name = "OrganizationPropertyEntity.findByOrganizationPropValue", query = "SELECT o FROM OrganizationPropertyEntity o WHERE o.organizationPropValue = :organizationPropValue ORDER BY o.organizationPropOrder"),
    @NamedQuery(name = "OrganizationPropertyEntity.findByOrganizationPropOrder", query = "SELECT o FROM OrganizationPropertyEntity o WHERE o.organizationPropOrder = :organizationPropOrder ORDER BY o.organizationPropOrder"),
    @NamedQuery(name = "OrganizationPropertyEntity.removeByFkMasterId", query = "DELETE FROM OrganizationPropertyEntity o WHERE o.fkMasterId = :fkMasterId")})
public class OrganizationPropertyEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "organization_prop_id")
    private Long organizationPropId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_master_id")
    private long fkMasterId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "organization_prop_name")
    private String organizationPropName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "organization_prop_type")
    private String organizationPropType;
    @Size(max = 2147483647)
    @Column(name = "organization_prop_value")
    private String organizationPropValue;
    @Column(name = "organization_prop_order")
    private Integer organizationPropOrder;

    public OrganizationPropertyEntity() {
    }

    public OrganizationPropertyEntity(OrganizationPropertyEntity in) {
        this.fkMasterId = in.fkMasterId;
        this.organizationPropName = in.organizationPropName;
        this.organizationPropType = in.organizationPropType;
        this.organizationPropValue = in.organizationPropValue;
        this.organizationPropOrder = in.organizationPropOrder;
    }

    public OrganizationPropertyEntity(String organizationPropName, String organizationPropType, String organizationPropValue, Integer organizationPropOrder) {
        this.organizationPropName = organizationPropName;
        this.organizationPropType = organizationPropType;
        this.organizationPropValue = organizationPropValue;
        this.organizationPropOrder = organizationPropOrder;
    }

    public Long getOrganizationPropId() {
        return organizationPropId;
    }

    public void setOrganizationPropId(Long organizationPropId) {
        this.organizationPropId = organizationPropId;
    }

    public long getFkMasterId() {
        return fkMasterId;
    }

    public void setFkMasterId(long fkMasterId) {
        this.fkMasterId = fkMasterId;
    }

    public String getOrganizationPropName() {
        return organizationPropName;
    }

    public void setOrganizationPropName(String organizationPropName) {
        this.organizationPropName = organizationPropName;
    }

    public String getOrganizationPropType() {
        return organizationPropType;
    }

    public void setOrganizationPropType(String organizationPropType) {
        this.organizationPropType = organizationPropType;
    }

    public String getOrganizationPropValue() {
        return organizationPropValue;
    }

    public void setOrganizationPropValue(String organizationPropValue) {
        this.organizationPropValue = organizationPropValue;
    }

    public Integer getOrganizationPropOrder() {
        return organizationPropOrder;
    }

    public void setOrganizationPropOrder(Integer organizationPropOrder) {
        this.organizationPropOrder = organizationPropOrder;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (organizationPropId != null ? organizationPropId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OrganizationPropertyEntity)) {
            return false;
        }
        OrganizationPropertyEntity other = (OrganizationPropertyEntity) object;
        if ((this.organizationPropId == null && other.organizationPropId != null) || (this.organizationPropId != null && !this.organizationPropId.equals(other.organizationPropId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "OrganizationPropertyEntity{" + "organizationPropId=" + organizationPropId + ", fkMasterId=" + fkMasterId + ", organizationPropName=" + organizationPropName + ", organizationPropType=" + organizationPropType + ", organizationPropValue=" + organizationPropValue + ", organizationPropOrder=" + organizationPropOrder + '}';
    }

}
