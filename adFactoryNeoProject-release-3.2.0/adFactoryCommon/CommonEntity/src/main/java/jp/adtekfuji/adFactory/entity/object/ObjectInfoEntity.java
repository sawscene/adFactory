/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.object;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * モノ情報
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "object")
public class ObjectInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private StringProperty objectIdProperty;
    private LongProperty fkObjectTypeIdProperty;
    private StringProperty objectNameProperty;
    private LongProperty updatePersonIdProperty;
    private ObjectProperty<Date> updateDateTimeProperty;

    @XmlElement(required = true)
    private String objectId;
    @XmlElement(required = true)
    private Long fkObjectTypeId;
    @XmlElement()
    private String objectName;
    @XmlElement()
    private Long updatePersonId;
    @XmlElement()
    private Date updateDatetime;

    @XmlElement()
    private Integer verInfo;// 排他用バーション

    public ObjectInfoEntity() {
    }

    public ObjectInfoEntity(ObjectInfoEntity in) {
        this.objectId = in.objectId;
        this.fkObjectTypeId = in.fkObjectTypeId;
        this.objectName = in.objectName;
        this.updatePersonId = in.updatePersonId;
        this.updateDatetime = in.updateDatetime;
        this.verInfo = in.verInfo;
    }

    public ObjectInfoEntity(String objectId, Long fkObjectTypeId, String objectName) {
        this.objectId = objectId;
        this.fkObjectTypeId = fkObjectTypeId;
        this.objectName = objectName;
    }

    public StringProperty objectIdProperty() {
        if (Objects.isNull(this.objectIdProperty)) {
            this.objectIdProperty = new SimpleStringProperty(this.objectId);
        }
        return this.objectIdProperty;
    }

    public LongProperty fkObjectTypeIdProperty() {
        if (Objects.isNull(this.fkObjectTypeIdProperty)) {
            this.fkObjectTypeIdProperty = new SimpleLongProperty(this.fkObjectTypeId);
        }
        return this.fkObjectTypeIdProperty;
    }

    public StringProperty objectNameProperty() {
        if (Objects.isNull(this.objectNameProperty)) {
            this.objectNameProperty = new SimpleStringProperty(this.objectName);
        }
        return this.objectNameProperty;
    }

    public LongProperty updatePersonIdProperty() {
        if (Objects.isNull(this.updatePersonIdProperty)) {
            this.updatePersonIdProperty = new SimpleLongProperty(this.updatePersonId);
        }
        return this.updatePersonIdProperty;
    }

    public ObjectProperty<Date> updateDateTimeProperty() {
        if (Objects.isNull(this.updateDateTimeProperty)) {
            this.updateDateTimeProperty = new SimpleObjectProperty(this.updateDatetime);
        }
        return this.updateDateTimeProperty;
    }

    public String getObjectId() {
        if (Objects.nonNull(this.objectIdProperty)) {
            return this.objectIdProperty.get();
        }
        return this.objectId;
    }

    public void setObjectId(String objectId) {
        if (Objects.nonNull(this.objectIdProperty)) {
            this.objectIdProperty.set(objectId);
        } else {
            this.objectId = objectId;
        }
    }

    public Long getFkObjectTypeId() {
        if (Objects.nonNull(this.fkObjectTypeIdProperty)) {
            return this.fkObjectTypeIdProperty.get();
        }
        return this.fkObjectTypeId;
    }

    public void setFkObjectTypeId(Long fkObjectTypeId) {
        if (Objects.nonNull(this.fkObjectTypeIdProperty)) {
            this.fkObjectTypeIdProperty.set(fkObjectTypeId);
        } else {
            this.fkObjectTypeId = fkObjectTypeId;
        }
    }

    public String getObjectName() {
        if (Objects.nonNull(this.objectNameProperty)) {
            return this.objectNameProperty.get();
        }
        return this.objectName;
    }

    public void setObjectName(String objectName) {
        if (Objects.nonNull(this.objectNameProperty)) {
            this.objectNameProperty.set(objectName);
        } else {
            this.objectName = objectName;
        }
    }

    public Long getUpdatePersonId() {
        if (Objects.nonNull(this.updatePersonIdProperty)) {
            return this.updatePersonIdProperty.get();
        }
        return this.updatePersonId;
    }

    public void setUpdatePersonId(Long updatePersonId) {
        if (Objects.nonNull(this.updatePersonIdProperty)) {
            this.updatePersonIdProperty.set(updatePersonId);
        } else {
            this.updatePersonId = updatePersonId;
        }
    }

    public Date getUpdateDateTime() {
        if (Objects.nonNull(this.updateDateTimeProperty)) {
            return this.updateDateTimeProperty.get();
        }
        return this.updateDatetime;
    }

    public void setUpdateDateTime(Date updateDateTime) {
        if (Objects.nonNull(this.updateDateTimeProperty)) {
            this.updateDateTimeProperty.set(updateDateTime);
        } else {
            this.updateDatetime = updateDateTime;
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

    /**
     * キーを取得する。
     *
     * @return
     */
    public String getObjectKey() {
        return this.getObjectId() + "(" + this.getFkObjectTypeId() + ")";
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + Objects.hashCode(this.objectId);
        hash = 19 * hash + Objects.hashCode(this.fkObjectTypeId);
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
        final ObjectInfoEntity other = (ObjectInfoEntity) obj;
        return Objects.equals(this.objectId, other.objectId) && Objects.equals(this.fkObjectTypeId, other.fkObjectTypeId);
    }

    @Override
    public String toString() {
        return new StringBuilder("ObjectInfoEntity{")
                .append("objectId=").append(this.objectId)
                .append(", ")
                .append("fkObjectTypeId=").append(this.fkObjectTypeId)
                .append(", ")
                .append("objectName=").append(this.objectName)
                .append(", ")
                .append("updatePersonId=").append(this.updatePersonId)
                .append(", ")
                .append("updateDatetime=").append(this.updateDatetime)
                .append(", ")
                .append("verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }

    /**
     * 表示される情報が一致するか調べる
     * 
     * @param other
     * @return 
     */
    public boolean displayInfoEquals(ObjectInfoEntity other) {
        if(Objects.equals(getObjectName(), other.getObjectName())) {
            return true;
        }
        return false;
    }
}
