/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.actual;

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
@Table(name = "trn_actual_property")
@XmlRootElement(name = "actualProperty")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "ActualPropertyEntity.findAll", query = "SELECT a FROM ActualPropertyEntity a"),
    @NamedQuery(name = "ActualPropertyEntity.findByActualPropId", query = "SELECT a FROM ActualPropertyEntity a WHERE a.actualPropId = :actualPropId"),
    @NamedQuery(name = "ActualPropertyEntity.findByFkActualId", query = "SELECT a FROM ActualPropertyEntity a WHERE a.fkActualId = :fkActualId"),
    @NamedQuery(name = "ActualPropertyEntity.findByActualPropName", query = "SELECT a FROM ActualPropertyEntity a WHERE a.actualPropName = :actualPropName"),
    @NamedQuery(name = "ActualPropertyEntity.findByActualPropType", query = "SELECT a FROM ActualPropertyEntity a WHERE a.actualPropType = :actualPropType"),
    @NamedQuery(name = "ActualPropertyEntity.findByActualPropValue", query = "SELECT a FROM ActualPropertyEntity a WHERE a.actualPropValue = :actualPropValue"),
    @NamedQuery(name = "ActualPropertyEntity.findByActualPropOrder", query = "SELECT a FROM ActualPropertyEntity a WHERE a.actualPropOrder = :actualPropOrder"),
    @NamedQuery(name = "ActualPropertyEntity.removeByFkActualId", query = "DELETE FROM ActualPropertyEntity a WHERE a.fkActualId IN :actualIds")})
public class ActualPropertyEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "actual_prop_id")
    private Long actualPropId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_actual_id")
    private long fkActualId;
    @Basic(optional = false)
    @NotNull
    @Size(max = 256)
    @Column(name = "actual_prop_name")
    private String actualPropName;
    @Basic(optional = false)
    @NotNull
    @Size(max = 128)
    @Column(name = "actual_prop_type")
    private String actualPropType;
    @Size(max = 2147483647)
    @Column(name = "actual_prop_value")
    private String actualPropValue;
    @Column(name = "actual_prop_order")
    private Integer actualPropOrder;

    public ActualPropertyEntity() {
    }

    public ActualPropertyEntity(String actualPropName, String actualPropType, String actualPropValue, Integer actualPropOrder) {
        this.actualPropName = actualPropName;
        this.actualPropType = actualPropType;
        this.actualPropValue = actualPropValue;
        this.actualPropOrder = actualPropOrder;
    }

    public Long getActualPropId() {
        return actualPropId;
    }

    public void setActualPropId(Long actualPropId) {
        this.actualPropId = actualPropId;
    }

    public long getFkActualId() {
        return fkActualId;
    }

    public void setFkActualId(long fkActualId) {
        this.fkActualId = fkActualId;
    }

    public String getActualPropName() {
        return actualPropName;
    }

    public void setActualPropName(String actualPropName) {
        this.actualPropName = actualPropName;
    }

    public String getActualPropType() {
        return actualPropType;
    }

    public void setActualPropType(String actualPropType) {
        this.actualPropType = actualPropType;
    }

    public String getActualPropValue() {
        return actualPropValue;
    }

    public void setActualPropValue(String actualPropValue) {
        this.actualPropValue = actualPropValue;
    }

    public Integer getActualPropOrder() {
        return actualPropOrder;
    }

    public void setActualPropOrder(Integer actualPropOrder) {
        this.actualPropOrder = actualPropOrder;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (actualPropId != null ? actualPropId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ActualPropertyEntity)) {
            return false;
        }
        ActualPropertyEntity other = (ActualPropertyEntity) object;
        if ((this.actualPropId == null && other.actualPropId != null) || (this.actualPropId != null && !this.actualPropId.equals(other.actualPropId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryserver.entity.actual.ActualPropertyEntity[ actualPropId=" + actualPropId + " ]";
    }
    
}
