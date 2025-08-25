/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.entity.unit;

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
 * 生産ユニットプロパティエンティティ
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.12.Mon
 */
@Entity
@Table(name = "trn_unit_property")
@XmlRootElement(name = "unitProperty")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // プロパティ全取得
    @NamedQuery(name = "UnitPropertyEntity.findAll", query = "SELECT u FROM UnitPropertyEntity u"),
    // プロパティID検索
    @NamedQuery(name = "UnitPropertyEntity.findByUnitPropertyId", query = "SELECT u FROM UnitPropertyEntity u WHERE u.unitPropertyId = :unitPropertyId"),
    // 生産ユニットID検索
    @NamedQuery(name = "UnitPropertyEntity.findByFkUnitId", query = "SELECT u FROM UnitPropertyEntity u WHERE u.fkUnitId = :fkUnitId"),
    // プロパティ名検索
    @NamedQuery(name = "UnitPropertyEntity.findByUnitPropertyName", query = "SELECT u FROM UnitPropertyEntity u WHERE u.unitPropertyName = :unitPropertyName"),
    // プロパティ型検索
    @NamedQuery(name = "UnitPropertyEntity.findByUnitPropertyType", query = "SELECT u FROM UnitPropertyEntity u WHERE u.unitPropertyType = :unitPropertyType"),
    // プロパティ値検索
    @NamedQuery(name = "UnitPropertyEntity.findByUnitPropertyValue", query = "SELECT u FROM UnitPropertyEntity u WHERE u.unitPropertyValue = :unitPropertyValue"),
    // プロパティ表示順番検索
    @NamedQuery(name = "UnitPropertyEntity.findByUnitPropertyOrder", query = "SELECT u FROM UnitPropertyEntity u WHERE u.unitPropertyOrder = :unitPropertyOrder"),
    // 指定した生産ユニットIDに該当するプロパティ削除
    @NamedQuery(name = "UnitPropertyEntity.removeByFkUnitId", query = "DELETE FROM UnitPropertyEntity u WHERE u.fkUnitId = :fkUnitId")})
public class UnitPropertyEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "unit_property_id")
    private Long unitPropertyId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_unit_id")
    private long fkUnitId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "unit_property_name")
    private String unitPropertyName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "unit_property_type")
    private String unitPropertyType;
    @Size(max = 2147483647)
    @Column(name = "unit_property_value")
    private String unitPropertyValue;
    @Column(name = "unit_property_order")
    private Integer unitPropertyOrder;

    public UnitPropertyEntity() {
    }

    public UnitPropertyEntity(Long unitPropertyId) {
        this.unitPropertyId = unitPropertyId;
    }

    public UnitPropertyEntity(Long unitPropertyId, long fkUnitId, String unitPropertyName, String unitPropertyType) {
        this.unitPropertyId = unitPropertyId;
        this.fkUnitId = fkUnitId;
        this.unitPropertyName = unitPropertyName;
        this.unitPropertyType = unitPropertyType;
    }

    public UnitPropertyEntity(long fkUnitId, String unitPropertyName, String unitPropertyType, String unitPropertyValue, Integer unitPropertyOrder) {
        this.fkUnitId = fkUnitId;
        this.unitPropertyName = unitPropertyName;
        this.unitPropertyType = unitPropertyType;
        this.unitPropertyValue = unitPropertyValue;
        this.unitPropertyOrder = unitPropertyOrder;
    }

    public Long getUnitPropertyId() {
        return unitPropertyId;
    }

    public void setUnitPropertyId(Long unitPropertyId) {
        this.unitPropertyId = unitPropertyId;
    }

    public long getFkUnitId() {
        return fkUnitId;
    }

    public void setFkUnitId(long fkUnitId) {
        this.fkUnitId = fkUnitId;
    }

    public String getUnitPropertyName() {
        return unitPropertyName;
    }

    public void setUnitPropertyName(String unitPropertyName) {
        this.unitPropertyName = unitPropertyName;
    }

    public String getUnitPropertyType() {
        return unitPropertyType;
    }

    public void setUnitPropertyType(String unitPropertyType) {
        this.unitPropertyType = unitPropertyType;
    }

    public String getUnitPropertyValue() {
        return unitPropertyValue;
    }

    public void setUnitPropertyValue(String unitPropertyValue) {
        this.unitPropertyValue = unitPropertyValue;
    }

    public Integer getUnitPropertyOrder() {
        return unitPropertyOrder;
    }

    public void setUnitPropertyOrder(Integer unitPropertyOrder) {
        this.unitPropertyOrder = unitPropertyOrder;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (unitPropertyId != null ? unitPropertyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UnitPropertyEntity)) {
            return false;
        }
        UnitPropertyEntity other = (UnitPropertyEntity) object;
        return !((this.unitPropertyId == null && other.unitPropertyId != null) || (this.unitPropertyId != null && !this.unitPropertyId.equals(other.unitPropertyId)));
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryforfujiserver.entity.unit.UnitPropertyEntity[ unitPropertyId=" + unitPropertyId + ", unitPropertyNamec=" + unitPropertyName + ", unitPropertyType=" + unitPropertyType + ", unitPropertyValue=" + unitPropertyValue + ", unitPorpertyOrder=" + unitPropertyOrder + " ]";
    }
}
