/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.object;

import java.io.Serializable;
import java.util.Objects;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * モノ種別情報
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "objectType")
public class ObjectTypeInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty objectTypeIdProperty;
    private StringProperty objectTypeNameProperty;

    @XmlElement(required = true)
    private Long objectTypeId;
    @XmlElement()
    private String objectTypeName;

    @XmlElement()
    private Integer verInfo;// 排他用バーション

    public ObjectTypeInfoEntity() {
    }

    public ObjectTypeInfoEntity(Long objectTypeId, String objectTypeName) {
        this.objectTypeId = objectTypeId;
        this.objectTypeName = objectTypeName;
    }

    public LongProperty objectTypeIdProperty() {
        if (Objects.isNull(this.objectTypeIdProperty)) {
            this.objectTypeIdProperty = new SimpleLongProperty(this.objectTypeId);
        }
        return this.objectTypeIdProperty;
    }

    public StringProperty objectTypeNameProperty() {
        if (Objects.isNull(this.objectTypeNameProperty)) {
            this.objectTypeNameProperty = new SimpleStringProperty(this.objectTypeName);
        }
        return this.objectTypeNameProperty;
    }

    public Long getObjectTypeId() {
        if (Objects.nonNull(this.objectTypeIdProperty)) {
            return this.objectTypeIdProperty.get();
        }
        return this.objectTypeId;
    }

    public void setObjectTypeId(Long objectTypeId) {
        if (Objects.nonNull(this.objectTypeIdProperty)) {
            this.objectTypeIdProperty.set(objectTypeId);
        } else {
            this.objectTypeId = objectTypeId;
        }
    }

    public String getObjectTypeName() {
        if (Objects.nonNull(this.objectTypeNameProperty)) {
            return this.objectTypeNameProperty.get();
        }
        return this.objectTypeName;
    }

    public void setObjectTypeName(String objectTypeName) {
        if (Objects.nonNull(this.objectTypeNameProperty)) {
            this.objectTypeNameProperty.set(objectTypeName);
        } else {
            this.objectTypeName = objectTypeName;
        }
    }

    /**
     * 排他用バーションを取得する。
     *
     * @return 排他用バーション
     */
    public Integer getVerInfo() {
        return this.verInfo;
    }

    /**
     * 排他用バーションを設定する。
     *
     * @param verInfo 排他用バーション
     */
    public void setVerInfo(Integer verInfo) {
        this.verInfo = verInfo;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.objectTypeId);
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
        final ObjectTypeInfoEntity other = (ObjectTypeInfoEntity) obj;
        return Objects.equals(this.objectTypeId, other.objectTypeId);
    }

    @Override
    public String toString() {
        return new StringBuilder("ObjectTypeInfoEntity{")
                .append("objectTypeId=").append(this.objectTypeId)
                .append(", ")
                .append("objectTypeName=").append(this.objectTypeName)
                .append(", ")
                .append("verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
