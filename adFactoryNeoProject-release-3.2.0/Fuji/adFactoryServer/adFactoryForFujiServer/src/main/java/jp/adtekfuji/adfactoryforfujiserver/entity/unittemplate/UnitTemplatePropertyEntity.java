/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate;

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
 * ユニットテンプレート情報カスタムフィールド
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.12.Mon
 */
@Entity
@Table(name = "mst_unit_template_property")
@XmlRootElement(name = "unittemplateProperty")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // プロパティ全取得
    @NamedQuery(name = "UnitTemplatePropertyEntity.findAll", query = "SELECT u FROM UnitTemplatePropertyEntity u"),
    // プロパティID検索
    @NamedQuery(name = "UnitTemplatePropertyEntity.findByUnitTemplatePropertyId", query = "SELECT u FROM UnitTemplatePropertyEntity u WHERE u.unitTemplatePropertyId = :unitTemplatePropertyId"),
    // ユニットテンプレートID検索
    @NamedQuery(name = "UnitTemplatePropertyEntity.findByFkUnitTemplateId", query = "SELECT u FROM UnitTemplatePropertyEntity u WHERE u.fkUnitTemplateId = :fkUnitTemplateId"),
    // プロパティ名検索
    @NamedQuery(name = "UnitTemplatePropertyEntity.findByUnitTemplatePropertyName", query = "SELECT u FROM UnitTemplatePropertyEntity u WHERE u.unitTemplatePropertyName = :unitTemplatePropertyName"),
    // プロパティ型検索
    @NamedQuery(name = "UnitTemplatePropertyEntity.findByUnitTemplatePropertyType", query = "SELECT u FROM UnitTemplatePropertyEntity u WHERE u.unitTemplatePropertyType = :unitTemplatePropertyType"),
    // プロパティ値検索
    @NamedQuery(name = "UnitTemplatePropertyEntity.findByUnitTemplatePropertyValue", query = "SELECT u FROM UnitTemplatePropertyEntity u WHERE u.unitTemplatePropertyValue = :unitTemplatePropertyValue"),
    // プロパティ表示順番検索
    @NamedQuery(name = "UnitTemplatePropertyEntity.findByUnitTemplatePropertyOrder", query = "SELECT u FROM UnitTemplatePropertyEntity u WHERE u.unitTemplatePropertyOrder = :unitTemplatePropertyOrder"),
    // 指定した生産ユニットIDに該当するプロパティ削除
    @NamedQuery(name = "UnitTemplatePropertyEntity.removeByFkUnitTemplateId", query = "DELETE FROM UnitTemplatePropertyEntity u WHERE u.fkUnitTemplateId = :fkUnitTemplateId")})
public class UnitTemplatePropertyEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "unit_template_property_id")
    private Long unitTemplatePropertyId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_unit_template_id")
    private long fkUnitTemplateId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "unit_template_property_name")
    private String unitTemplatePropertyName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "unit_template_property_type")
    private String unitTemplatePropertyType;
    @Size(max = 2147483647)
    @Column(name = "unit_template_property_value")
    private String unitTemplatePropertyValue;
    @Column(name = "unit_template_property_order")
    private Integer unitTemplatePropertyOrder;

    public UnitTemplatePropertyEntity() {
    }

    public UnitTemplatePropertyEntity(Long unitTemplatePropertyId) {
        this.unitTemplatePropertyId = unitTemplatePropertyId;
    }

    public UnitTemplatePropertyEntity(Long unitTemplatePropertyId, String unitTemplatePropertyName, String unitTemplatePropertyType, String unitTemplatePropertyValue, Integer unitTemplatePropertyOrder) {
        this.unitTemplatePropertyId = unitTemplatePropertyId;
        this.unitTemplatePropertyName = unitTemplatePropertyName;
        this.unitTemplatePropertyType = unitTemplatePropertyType;
        this.unitTemplatePropertyValue = unitTemplatePropertyValue;
        this.unitTemplatePropertyOrder = unitTemplatePropertyOrder;
    }

    public UnitTemplatePropertyEntity(UnitTemplatePropertyEntity in) {
        this.fkUnitTemplateId = in.fkUnitTemplateId;
        this.unitTemplatePropertyName = in.unitTemplatePropertyName;
        this.unitTemplatePropertyType = in.unitTemplatePropertyType;
        this.unitTemplatePropertyValue = in.unitTemplatePropertyValue;
        this.unitTemplatePropertyOrder = in.unitTemplatePropertyOrder;
    }

    public Long getUnitTemplatePropertyId() {
        return unitTemplatePropertyId;
    }

    public void setUnitTemplatePropertyId(Long unitTemplatePropertyId) {
        this.unitTemplatePropertyId = unitTemplatePropertyId;
    }

    public long getFkUnitTemplateId() {
        return fkUnitTemplateId;
    }

    public void setFkUnitTemplateId(long fkUnitTemplateId) {
        this.fkUnitTemplateId = fkUnitTemplateId;
    }

    public String getUnitTemplatePropertyName() {
        return unitTemplatePropertyName;
    }

    public void setUnitTemplatePropertyName(String unitTemplatePropertyName) {
        this.unitTemplatePropertyName = unitTemplatePropertyName;
    }

    public String getUnitTemplatePropertyType() {
        return unitTemplatePropertyType;
    }

    public void setUnitTemplatePropertyType(String unitTemplatePropertyType) {
        this.unitTemplatePropertyType = unitTemplatePropertyType;
    }

    public String getUnitTemplatePropertyValue() {
        return unitTemplatePropertyValue;
    }

    public void setUnitTemplatePropertyValue(String unitTemplatePropertyValue) {
        this.unitTemplatePropertyValue = unitTemplatePropertyValue;
    }

    public Integer getUnitTemplatePropertyOrder() {
        return unitTemplatePropertyOrder;
    }

    public void setUnitTemplatePropertyOrder(Integer unitTemplatePropertyOrder) {
        this.unitTemplatePropertyOrder = unitTemplatePropertyOrder;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (unitTemplatePropertyId != null ? unitTemplatePropertyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UnitTemplatePropertyEntity)) {
            return false;
        }
        UnitTemplatePropertyEntity other = (UnitTemplatePropertyEntity) object;
        return !((this.unitTemplatePropertyId == null && other.unitTemplatePropertyId != null) || (this.unitTemplatePropertyId != null && !this.unitTemplatePropertyId.equals(other.unitTemplatePropertyId)));
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate.UnitTemplatePropertyEntity[ unitTemplatePropertyId=" + unitTemplatePropertyId + ", unitTemplatePropertyNamec=" + unitTemplatePropertyName + ", unitTemplatePropertyType=" + unitTemplatePropertyType + ", unitTemplatePropertyValue=" + unitTemplatePropertyValue + ", unitPorpertyOrder=" + unitTemplatePropertyOrder + "]";
    }
}
