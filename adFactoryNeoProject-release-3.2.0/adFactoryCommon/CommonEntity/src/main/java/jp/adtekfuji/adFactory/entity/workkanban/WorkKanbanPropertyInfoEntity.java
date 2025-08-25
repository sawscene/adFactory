/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.workkanban;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;

/**
 * 工程カンバン情報
 *
 * @author e-mori
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "workKanbanProperty")
@JsonIgnoreProperties(ignoreUnknown=true)
public class WorkKanbanPropertyInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty workKanbanPropIdProperty;
    private LongProperty fkMasterIdProperty;
    private StringProperty workKanbanPropNameProperty;
    private ObjectProperty<CustomPropertyTypeEnum> workKanbanPropTypeProperty;
    private StringProperty workKanbanPropValueProperty;
    private IntegerProperty workKanbanPropOrderProperty;

    @XmlElement(required = true)
    @JsonIgnore
    private Long workKanbannPropertyId;
    
    @XmlElement()
    @JsonIgnore
    private Long fkWorkKanbanId;
    
    @XmlElement()
    @JsonProperty("key")
    private String workKanbanPropName;
    
    @XmlElement()
    @JsonProperty("type")
    private CustomPropertyTypeEnum workKanbanPropType;
    
    @XmlElement()
    @JsonProperty("val")
    private String workKanbanPropValue;
    
    @XmlElement()
    @JsonProperty("disp")
    private Integer workKanbanPropOrder;

    public WorkKanbanPropertyInfoEntity() {
    }

    public WorkKanbanPropertyInfoEntity(Long workKanbanPropId, Long fkMasterId, String workKanbanPropName, CustomPropertyTypeEnum workKanbanPropType, String workKanbanPropValue, Integer workKanbanPropOrder) {
        this.workKanbannPropertyId = workKanbanPropId;
        this.fkWorkKanbanId = fkMasterId;
        this.workKanbanPropName = workKanbanPropName;
        this.workKanbanPropType = workKanbanPropType;
        this.workKanbanPropValue = workKanbanPropValue;
        this.workKanbanPropOrder = workKanbanPropOrder;
    }

    public LongProperty workKanbanPropIdProperty() {
        if (Objects.isNull(workKanbanPropIdProperty)) {
            workKanbanPropIdProperty = new SimpleLongProperty(workKanbannPropertyId);
        }
        return workKanbanPropIdProperty;
    }

    public LongProperty fkMasterIdProperty() {
        if (Objects.isNull(fkMasterIdProperty)) {
            fkMasterIdProperty = new SimpleLongProperty(fkWorkKanbanId);
        }
        return fkMasterIdProperty;
    }

    public StringProperty workKanbanPropNameProperty() {
        if (Objects.isNull(workKanbanPropNameProperty)) {
            workKanbanPropNameProperty = new SimpleStringProperty(workKanbanPropName);
        }
        return workKanbanPropNameProperty;
    }

    public ObjectProperty<CustomPropertyTypeEnum> workKanbanPropTypeProperty() {
        if (Objects.isNull(workKanbanPropTypeProperty)) {
            workKanbanPropTypeProperty = new SimpleObjectProperty<>(workKanbanPropType);
        }
        return workKanbanPropTypeProperty;
    }

    public StringProperty workKanbanPropValueProperty() {
        if (Objects.isNull(workKanbanPropValueProperty)) {
            workKanbanPropValueProperty = new SimpleStringProperty(workKanbanPropValue);
        }
        return workKanbanPropValueProperty;
    }

    public IntegerProperty workKanbanPropOrderProperty() {
        if (Objects.isNull(workKanbanPropOrderProperty)) {
            workKanbanPropOrderProperty = new SimpleIntegerProperty(workKanbanPropOrder);
        }
        return workKanbanPropOrderProperty;
    }

    @JsonIgnore
    public Long getWorkKanbanPropId() {
        if (Objects.nonNull(workKanbanPropIdProperty)) {
            return workKanbanPropIdProperty.get();
        }
        return workKanbannPropertyId;
    }

    @JsonIgnore
    public void setWorkKanbanPropId(Long workKanbanPropId) {
        if (Objects.nonNull(workKanbanPropIdProperty)) {
            workKanbanPropIdProperty.set(workKanbanPropId);
        } else {
            this.workKanbannPropertyId = workKanbanPropId;
        }
    }

    public Long getFkMasterId() {
        if (Objects.nonNull(fkMasterIdProperty)) {
            return fkMasterIdProperty.get();
        }
        return fkWorkKanbanId;
    }

    public void setFkMasterId(Long fkMasterId) {
        if (Objects.nonNull(fkMasterIdProperty)) {
            fkMasterIdProperty.set(fkMasterId);
        } else {
            this.fkWorkKanbanId = fkMasterId;
        }
    }

    @JsonIgnore
    public String getWorkKanbanPropName() {
        if (Objects.nonNull(workKanbanPropNameProperty)) {
            return workKanbanPropNameProperty.get();
        }
        return workKanbanPropName;
    }

    @JsonIgnore
    public void setWorkKanbanPropName(String workKanbanPropName) {
        if (Objects.nonNull(workKanbanPropNameProperty)) {
            workKanbanPropNameProperty.set(workKanbanPropName);
        } else {
            this.workKanbanPropName = workKanbanPropName;
        }
    }

    @JsonIgnore
    public CustomPropertyTypeEnum getWorkKanbanPropType() {
        if (Objects.nonNull(workKanbanPropTypeProperty)) {
            return workKanbanPropTypeProperty.get();
        }
        return workKanbanPropType;
    }

    @JsonIgnore
    public void setWorkKanbanPropType(CustomPropertyTypeEnum workKanbanPropType) {
        if (Objects.nonNull(workKanbanPropTypeProperty)) {
            workKanbanPropTypeProperty.set(workKanbanPropType);
        } else {
            this.workKanbanPropType = workKanbanPropType;
        }
    }

    @JsonIgnore
    public String getWorkKanbanPropValue() {
        if (Objects.nonNull(workKanbanPropValueProperty)) {
            return workKanbanPropValueProperty.get();
        }
        return workKanbanPropValue;
    }

    @JsonIgnore
    public void setWorkKanbanPropValue(String workKanbanPropValue) {
        if (Objects.nonNull(workKanbanPropValueProperty)) {
            workKanbanPropValueProperty.set(workKanbanPropValue);
        } else {
            this.workKanbanPropValue = workKanbanPropValue;
        }
    }

    @JsonIgnore
    public Integer getWorkKanbanPropOrder() {
        if (Objects.nonNull(workKanbanPropOrderProperty)) {
            return workKanbanPropOrderProperty.get();
        }
        return workKanbanPropOrder;
    }

    @JsonIgnore
    public void setWorkKanbanPropOrder(Integer workKanbanPropOrder) {
        if (Objects.nonNull(workKanbanPropOrderProperty)) {
            workKanbanPropOrderProperty.set(workKanbanPropOrder);
        } else {
            this.workKanbanPropOrder = workKanbanPropOrder;
        }
    }

    public void updateMember() {
        this.workKanbannPropertyId = getWorkKanbanPropId();
        this.fkWorkKanbanId = getFkMasterId();
        this.workKanbanPropName = getWorkKanbanPropName();
        this.workKanbanPropType = getWorkKanbanPropType();
        this.workKanbanPropValue = getWorkKanbanPropValue();
        this.workKanbanPropOrder = getWorkKanbanPropOrder();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int) (this.workKanbannPropertyId ^ (this.workKanbannPropertyId >>> 32));
        hash = 83 * hash + (int) (this.fkWorkKanbanId ^ (this.fkWorkKanbanId >>> 32));
        hash = 83 * hash + Objects.hashCode(this.workKanbanPropName);
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
        final WorkKanbanPropertyInfoEntity other = (WorkKanbanPropertyInfoEntity) obj;
        if (this.getWorkKanbanPropId() != other.getWorkKanbanPropId()) {
            return false;
        }
        if (this.getFkMasterId() != other.getFkMasterId()) {
            return false;
        }
        if (!Objects.equals(this.getWorkKanbanPropName(), other.getWorkKanbanPropName())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "WorkKanbanPropertyEntity{" + "workKanbannPropertyId=" + getWorkKanbanPropId() + ", fkWorkKanbanId=" + getFkMasterId() + ", workKanbanPropertyName=" + getWorkKanbanPropName() + ", workKanbanPropertyType=" + getWorkKanbanPropType() + ", workKanbanPropertyValue=" + getWorkKanbanPropValue() + ", workKanbanPropertyOrder=" + getWorkKanbanPropOrder() + '}';
    }

    /**
     * 表示される情報をコピーする
     *
     * @return
     */
    @Override
    public WorkKanbanPropertyInfoEntity clone() {
        WorkKanbanPropertyInfoEntity entity = new WorkKanbanPropertyInfoEntity();
        
        entity.setWorkKanbanPropId(getWorkKanbanPropId());//比較はしないがソートに使う。名前や値は同じものでも有効なため。
        entity.setWorkKanbanPropName(getWorkKanbanPropName());
        entity.setWorkKanbanPropType(getWorkKanbanPropType());
        entity.setWorkKanbanPropValue(getWorkKanbanPropValue());
        
        return entity;
    }
    
    /**
     * 表示される情報が一致するか調べる
     * 
     * @param other
     * @return 
     */
    public boolean equalsDisplayInfo(WorkKanbanPropertyInfoEntity other) {
        boolean ret = false;
        
        if(Objects.equals(getWorkKanbanPropName(), other.getWorkKanbanPropName())
                && Objects.equals(getWorkKanbanPropType(), other.getWorkKanbanPropType())
                && Objects.equals(getWorkKanbanPropValue(), other.getWorkKanbanPropValue())
                ) {
            ret = true;
        }
        
        return ret;
    }
}
