/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.entity.accessfuji;

import java.io.Serializable;
import java.util.Objects;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * アクセス階層情報
 *
 * @author j.min
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "accessHierarchyFuji")
public class AccessHierarchyFujiInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private ObjectProperty<AccessHierarchyFujiTypeEnum> typeIdProperty;
    private LongProperty fkHierarchyIdProperty;
    private LongProperty fkOrganizationIdProperty;

    @XmlElement(required = true)
    private AccessHierarchyFujiTypeEnum typeId;
    @XmlElement()
    private Long fkHierarchyId;
    @XmlElement()
    private Long fkOrganizationId;

    public AccessHierarchyFujiInfoEntity() {
    }

    public AccessHierarchyFujiInfoEntity(AccessHierarchyFujiTypeEnum typeId, Long fkHierarchyId, Long fkOrganizationId) {
        this.typeId = typeId;
        this.fkHierarchyId = fkHierarchyId;
        this.fkOrganizationId = fkOrganizationId;
    }

    public ObjectProperty<AccessHierarchyFujiTypeEnum> typeIdProperty() {
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
    
    public AccessHierarchyFujiTypeEnum getTypeId() {
        if (Objects.nonNull(typeIdProperty)) {
            return typeIdProperty.get();
        }
        return typeId;
    }

    public void setTypeId(AccessHierarchyFujiTypeEnum typeId) {
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
        final AccessHierarchyFujiInfoEntity other = (AccessHierarchyFujiInfoEntity) obj;
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
        return "AccessHierarchyFujiInfoEntity{" + "typeId=" + typeId + ", fkHierarchyId=" + fkHierarchyId + ", fkOrganizationId=" + fkOrganizationId + '}';
    }
}
