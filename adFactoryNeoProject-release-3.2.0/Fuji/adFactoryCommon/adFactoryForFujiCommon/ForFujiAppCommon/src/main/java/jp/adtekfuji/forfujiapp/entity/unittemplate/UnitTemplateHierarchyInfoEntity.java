/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.entity.unittemplate;

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
 * ユニットテンプレート階層情報
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.26.Wen
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "unittemplateHierarchy")
public class UnitTemplateHierarchyInfoEntity {

    private static final long serialVersionUID = 1L;

    private LongProperty unittemplateHierarchyIdProperty;
    private LongProperty parentIdProperty;
    private StringProperty hierarchyNameProperty;

    @XmlElement(required = true)
    private Long unitTemplateHierarchyId;
    @XmlElement()
    private Long parentId;
    @XmlElement()
    private String hierarchyName;
    @XmlElementWrapper(name = "unittemplates")
    @XmlElement(name = "unittemplate")
    private List<UnitTemplateInfoEntity> unittemplateCollection = null;
    @XmlElement()
    private Long childCount;

    public UnitTemplateHierarchyInfoEntity() {
    }

    public UnitTemplateHierarchyInfoEntity(Long unitTemplateHierarchyId, Long parentId, String hierarchyName) {
        this.unitTemplateHierarchyId = unitTemplateHierarchyId;
        this.parentId = parentId;
        this.hierarchyName = hierarchyName;
    }

    public UnitTemplateHierarchyInfoEntity(Long parentId, String hierarchyName) {
        this.parentId = parentId;
        this.hierarchyName = hierarchyName;
    }

    public LongProperty unitTemplateHierarchyIdProperty() {
        if (Objects.isNull(unittemplateHierarchyIdProperty)) {
            unittemplateHierarchyIdProperty = new SimpleLongProperty(unitTemplateHierarchyId);
        }
        return unittemplateHierarchyIdProperty;
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

    public Long getUnitTemplateHierarchyId() {
        if (Objects.nonNull(unittemplateHierarchyIdProperty)) {
            return unittemplateHierarchyIdProperty.get();
        }
        return unitTemplateHierarchyId;
    }

    public void setUnitTemplateHierarchyId(Long unitTemplateHierarchyId) {
        if (Objects.nonNull(unittemplateHierarchyIdProperty)) {
            unittemplateHierarchyIdProperty.set(unitTemplateHierarchyId);
        } else {
            this.unitTemplateHierarchyId = unitTemplateHierarchyId;
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

    public List<UnitTemplateInfoEntity> getUnitTemplateCollection() {
        return unittemplateCollection;
    }

    public void setUnitTemplateCollection(List<UnitTemplateInfoEntity> unittemplateCollection) {
        this.unittemplateCollection = unittemplateCollection;
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
        hash += (unitTemplateHierarchyId != null ? unitTemplateHierarchyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UnitTemplateHierarchyInfoEntity)) {
            return false;
        }
        UnitTemplateHierarchyInfoEntity other = (UnitTemplateHierarchyInfoEntity) object;
        return !((this.unitTemplateHierarchyId == null && other.unitTemplateHierarchyId != null) || (this.unitTemplateHierarchyId != null && !this.unitTemplateHierarchyId.equals(other.unitTemplateHierarchyId)));
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateHierarchyInfoEntity[ unitTemplateHierarchyId=" + unitTemplateHierarchyId + "unitTemplateHierarchyName=" + hierarchyName + " ]";
    }

}
