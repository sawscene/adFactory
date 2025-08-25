/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.entity.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 生産ユニット検索条件用エンティティ
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.18.Tsu
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "unitSearchCondition")
public class UnitSearchCondition {

    private static final long serialVersionUID = 1L;

    //@XmlElement()
    //private Long hierarchyId = null;
    @XmlElement()
    private Long unitId = null;
    @XmlElement()
    private String unitName = null;
    @XmlElementWrapper(name = "unittemplateIds")
    @XmlElement(name = "unittemplateId")
    private List<Long> unitTemplateIdCollection = null;
    @XmlElement()
    private Date fromDate = null;
    @XmlElement()
    private Date toDate = null;
    //@XmlElementWrapper(name = "propertys")
    //@XmlElement(name = "property")
    //private List<PropertyCondition> propertyCollection = null;
    /**
     * 関連データを取得するかどうか
     */
    @XmlElement()
    private Boolean withAssociate = false;

    public UnitSearchCondition() {
    }

    public UnitSearchCondition(String unitName, Long unitTemplateId, Date fromDate, Date toDate) {
        this.unitName = unitName;
        if (Objects.nonNull(unitTemplateId)) {
            unitTemplateIdCollection = new ArrayList<>();
            unitTemplateIdCollection.add(unitTemplateId);
        }
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public UnitSearchCondition(String unitName, List<Long> unitTemplateIdCollection, Date fromDate, Date toDate) {
        this.unitName = unitName;
        this.unitTemplateIdCollection = unitTemplateIdCollection;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    //public UnitSearchCondition hierarchyId(Long hierarchyId) {
    //    this.hierarchyId = hierarchyId;
    //    return this;
    //}

    public UnitSearchCondition unitId(Long unitId) {
        this.unitId = unitId;
        return this;
    }

    public UnitSearchCondition unitName(String unitName) {
        this.unitName = unitName;
        return this;
    }

    //public UnitSearchCondition unitTemplateId(Long unitTemplateId) {
    //    if (Objects.nonNull(unitTemplateId)) {
    //        unitTemplateIdCollection = new ArrayList<>();
    //        unitTemplateIdCollection.add(unitTemplateId);
    //    }
    //    return this;
    //}

    public UnitSearchCondition unitTemplateIdCollection(List<Long> unitTemplateIdCollection) {
        this.unitTemplateIdCollection = unitTemplateIdCollection;
        return this;
    }

    public UnitSearchCondition fromDate(Date fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    public UnitSearchCondition toDate(Date toDate) {
        this.toDate = toDate;
        return this;
    }

    //public UnitSearchCondition propertyList(List<PropertyCondition> propertyList) {
    //    this.propertyCollection = propertyList;
    //    return this;
    //}

    //public Long getHierarchyId() {
    //    return hierarchyId;
    //}

    //public void setHierarchyId(Long hierarchyId) {
    //    this.hierarchyId = hierarchyId;
    //}

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public List<Long> getUnittemplateIdCollection() {
        return unitTemplateIdCollection;
    }

    public void setUnittemplateIdCollection(List<Long> unittemplateIdCollection) {
        this.unitTemplateIdCollection = unittemplateIdCollection;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    //public List<PropertyCondition> getPropertyCollection() {
    //    return propertyCollection;
    //}
    //
    //public void setPropertyCollection(List<PropertyCondition> propertyCollection) {
    //    this.propertyCollection = propertyCollection;
    //}

    public boolean IsWithAssociate() {
        return this.withAssociate;
    }

    public void setWithAssociate(boolean withAssociate) {
        this.withAssociate = withAssociate;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UnitSearchCondition other = (UnitSearchCondition) obj;
        return true;
    }

    @Override
    public String toString() {
        return "UnitSearchCondition{" + "unitId=" + unitId + ", unitName=" + unitName + ", unitTemplateIdCollection=" + unitTemplateIdCollection + ", fromDate=" + fromDate +
                ", toDate=" + toDate + ", withAssociate=" + withAssociate + '}';
    }

}
