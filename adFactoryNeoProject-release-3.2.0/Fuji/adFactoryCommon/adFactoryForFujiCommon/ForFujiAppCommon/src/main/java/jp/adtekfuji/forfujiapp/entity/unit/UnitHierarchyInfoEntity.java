/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this  file, choose Tools | s
 * and open the  in the editor.
 */
package jp.adtekfuji.forfujiapp.entity.unit;

import java.util.List;
import java.util.Objects;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * ユニット階層情報
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.26.Wen
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "unitHierarchy")
public class UnitHierarchyInfoEntity {

    private static final long serialVersionUID = 1L;

    private LongProperty unitHierarchyIdProperty;
    private LongProperty parentIdProperty;
    private StringProperty hierarchyNameProperty;

    @XmlElement(required = true)
    private Long unitHierarchyId;
    @XmlElement()
    private Long parentId;
    @XmlElement()
    private String hierarchyName;
    @XmlElementWrapper(name = "units")
    @XmlElement(name = "unit")
    private List<UnitInfoEntity> unitCollection = null;
    @XmlElement()
    private Long childCount;

    public UnitHierarchyInfoEntity() {
    }

    public UnitHierarchyInfoEntity(Long unitHierarchyId, Long parentId, String hierarchyName) {
        this.unitHierarchyId = unitHierarchyId;
        this.parentId = parentId;
        this.hierarchyName = hierarchyName;
    }

    public UnitHierarchyInfoEntity(Long parentId, String hierarchyName) {
        this.parentId = parentId;
        this.hierarchyName = hierarchyName;
    }

    public LongProperty workHierarchyIdProperty() {
        if (Objects.isNull(unitHierarchyIdProperty)) {
            unitHierarchyIdProperty = new SimpleLongProperty(unitHierarchyId);
        }
        return unitHierarchyIdProperty;
    }

    public LongProperty parentProperty() {
        if (Objects.isNull(parentIdProperty)) {
            parentIdProperty = new SimpleLongProperty(parentId);
        }
        return parentIdProperty;
    }

    public StringProperty hierarchyNameProperty() {
        if (Objects.isNull(hierarchyNameProperty)) {
            hierarchyNameProperty = new SimpleStringProperty(hierarchyName);
        }
        return hierarchyNameProperty;
    }

    public Long getUnitHierarchyId() {
        if (Objects.nonNull(unitHierarchyIdProperty)) {
            return unitHierarchyIdProperty.get();
        }
        return unitHierarchyId;
    }

    public void setUnitHierarchyId(Long unitHierarchyId) {
        if (Objects.nonNull(unitHierarchyIdProperty)) {
            unitHierarchyIdProperty.set(unitHierarchyId);
        } else {
            this.unitHierarchyId = unitHierarchyId;
        }
    }

    public Long getParentId() {
        if (Objects.nonNull(parentIdProperty)) {
            return parentIdProperty.get();
        }
        return parentId;
    }

    public void setParentId(Long parentId) {
        if (Objects.nonNull(parentIdProperty)) {
            parentIdProperty.set(parentId);
        } else {
            this.parentId = parentId;
        }
    }

    public String getHierarchyName() {
        if (Objects.nonNull(hierarchyNameProperty)) {
            return hierarchyNameProperty.get();
        }
        return hierarchyName;
    }

    public void setHierarchyName(String hierarchyName) {
        if (Objects.nonNull(hierarchyNameProperty)) {
            hierarchyNameProperty.set(hierarchyName);
        } else {
            this.hierarchyName = hierarchyName;
        }
    }

    public List<UnitInfoEntity> getUnitCollection() {
        return unitCollection;
    }

    public void setUnitCollection(List<UnitInfoEntity> unitCollection) {
        this.unitCollection = unitCollection;
    }

    public Long getChildCount() {
        return childCount;
    }

    public void setChildCount(Long childCount) {
        this.childCount = childCount;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (unitHierarchyId != null ? unitHierarchyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UnitHierarchyInfoEntity)) {
            return false;
        }
        UnitHierarchyInfoEntity other = (UnitHierarchyInfoEntity) object;
        return !((this.unitHierarchyId == null && other.unitHierarchyId != null) || (this.unitHierarchyId != null && !this.unitHierarchyId.equals(other.unitHierarchyId)));
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.forfujiapp.entity.unit.UnitHierarchyInfoEntity[ unitHierarchyId=" + unitHierarchyId + "unitHierarchyName=" + hierarchyName + " ]";
    }

}
