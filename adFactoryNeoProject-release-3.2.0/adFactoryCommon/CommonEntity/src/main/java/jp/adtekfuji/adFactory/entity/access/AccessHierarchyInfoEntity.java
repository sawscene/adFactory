/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.access;

import java.io.Serializable;
import java.util.Objects;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.AccessHierarchyTypeEnum;

/**
 * アクセス階層情報
 *
 * @author j.min
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "accessHierarchy")
public class AccessHierarchyInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private ObjectProperty<AccessHierarchyTypeEnum> typeIdProperty;
    private LongProperty fkHierarchyIdProperty;
    private LongProperty fkOrganizationIdProperty;

    @XmlElement(required = true)
    private AccessHierarchyTypeEnum typeId;
    @XmlElement()
    private Long fkHierarchyId;
    @XmlElement()
    private Long fkOrganizationId;

    public AccessHierarchyInfoEntity() {
    }

    public AccessHierarchyInfoEntity(AccessHierarchyTypeEnum typeId, Long fkHierarchyId, Long fkOrganizationId) {
        this.typeId = typeId;
        this.fkHierarchyId = fkHierarchyId;
        this.fkOrganizationId = fkOrganizationId;
    }

    public ObjectProperty<AccessHierarchyTypeEnum> typeIdProperty() {
        if (Objects.isNull(typeIdProperty)) {
            typeIdProperty = new SimpleObjectProperty<>(typeId);
        }
        return typeIdProperty;
    }

    public LongProperty fkHierarchyIdProperty() {
        if (Objects.isNull(fkHierarchyIdProperty)) {
            fkHierarchyIdProperty = new SimpleLongProperty(fkHierarchyId);
        }
        return fkHierarchyIdProperty;
    }

    public LongProperty fkOrganizationIdProperty() {
        if (Objects.isNull(fkOrganizationIdProperty)) {
            fkOrganizationIdProperty = new SimpleLongProperty(fkOrganizationId);
        }
        return fkOrganizationIdProperty;
    }
    
    public AccessHierarchyTypeEnum getTypeId() {
        if (Objects.nonNull(typeIdProperty)) {
            return typeIdProperty.get();
        }
        return typeId;
    }

    public void setTypeId(AccessHierarchyTypeEnum typeId) {
        if (Objects.nonNull(typeIdProperty)) {
            typeIdProperty.set(typeId);
        } else {
            this.typeId = typeId;
        }
    }

    public Long getFkHierarchyId() {
        if (Objects.nonNull(fkHierarchyIdProperty)) {
            return fkHierarchyIdProperty.get();
        }
        return fkHierarchyId;
    }

    public void setFkHierarchyId(Long fkHierarchyId) {
        if (Objects.nonNull(fkHierarchyIdProperty)) {
            fkHierarchyIdProperty.set(fkHierarchyId);
        } else {
            this.fkHierarchyId = fkHierarchyId;
        }
    }
    
    public Long getFkOrganizationId() {
        if (Objects.nonNull(fkOrganizationIdProperty)) {
            return fkOrganizationIdProperty.get();
        }
        return fkOrganizationId;
    }

    public void setFkOrganizationId(Long fkOrganizationId) {
        if (Objects.nonNull(fkOrganizationIdProperty)) {
            fkOrganizationIdProperty.set(fkOrganizationId);
        } else {
            this.fkOrganizationId = fkOrganizationId;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.typeId);
        hash = 83 * hash + Objects.hashCode(this.fkHierarchyId);
        hash = 83 * hash + Objects.hashCode(this.fkOrganizationId);
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
        final AccessHierarchyInfoEntity other = (AccessHierarchyInfoEntity) obj;
        if (!Objects.equals(this.typeId, other.typeId)) {
            return false;
        }
        if (!Objects.equals(this.fkHierarchyId, other.fkHierarchyId)) {
            return false;
        }
        if (!Objects.equals(this.fkOrganizationId, other.fkOrganizationId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AccessHierarchyInfoEntity{" + "typeId=" + typeId + ", fkHierarchyId=" + fkHierarchyId + ", fkOrganizationId=" + fkOrganizationId + '}';
    }
}
