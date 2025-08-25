/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.organization;

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
 * 組織プロパティ情報
 *
 * @author e-mori
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "organizationProperty")
@JsonIgnoreProperties(ignoreUnknown=true)
public class OrganizationPropertyInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty organizationPropIdProperty;
    private LongProperty fkMasterIdProperty;
    private StringProperty organizationPropNameProperty;
    private ObjectProperty<CustomPropertyTypeEnum> organizationPropTypeProperty;
    private StringProperty organizationPropValueProperty;
    private IntegerProperty organizationPropOrderProperty;

    @XmlElement(required = true)
    @JsonIgnore
    private Long organizationPropId;
    
    @XmlElement()
    @JsonIgnore
    private Long fkMasterId;
    
    @XmlElement()
    @JsonProperty("key")
    private String organizationPropName;
    
    @XmlElement()
    @JsonProperty("type")
    private String organizationPropType;
    
    @XmlElement()
    @JsonProperty("val")
    private String organizationPropValue;
    
    @XmlElement()
    @JsonProperty("disp")
    private Integer organizationPropOrder;

    public OrganizationPropertyInfoEntity() {
    }

    public OrganizationPropertyInfoEntity(Long organizationPropId, Long fkMasterId, String organizationPropName, String organizationPropType, String organizationPropValue, Integer organizationPropOrder) {
        this.organizationPropId = organizationPropId;
        this.fkMasterId = fkMasterId;
        this.organizationPropName = organizationPropName;
        this.organizationPropType = organizationPropType;
        this.organizationPropValue = organizationPropValue;
        this.organizationPropOrder = organizationPropOrder;
    }

    public LongProperty organizationPropIdProperty() {
        if (Objects.isNull(organizationPropIdProperty)) {
            organizationPropIdProperty = new SimpleLongProperty(organizationPropId);
        }
        return organizationPropIdProperty;
    }

    public LongProperty fkMasterIdProperty() {
        if (Objects.isNull(fkMasterIdProperty)) {
            fkMasterIdProperty = new SimpleLongProperty(fkMasterId);
        }
        return fkMasterIdProperty;
    }

    public StringProperty organizationPropNameProperty() {
        if (Objects.isNull(organizationPropNameProperty)) {
            organizationPropNameProperty = new SimpleStringProperty(organizationPropName);
        }
        return organizationPropNameProperty;
    }

    public ObjectProperty organizationPropTypeProperty() {
        if (Objects.isNull(organizationPropTypeProperty)) {
            if(!Objects.nonNull(organizationPropType)){
                organizationPropType = CustomPropertyTypeEnum.TYPE_STRING.toString();
            }
            organizationPropTypeProperty = new SimpleObjectProperty<>(CustomPropertyTypeEnum.valueOf(organizationPropType));
        }
        return organizationPropTypeProperty;
    }

    public StringProperty organizationPropValueProperty() {
        if (Objects.isNull(organizationPropValueProperty)) {
            organizationPropValueProperty = new SimpleStringProperty(organizationPropValue);
        }
        return organizationPropValueProperty;
    }

    public IntegerProperty organizationPropOrderProperty() {
        if (Objects.isNull(organizationPropOrderProperty)) {
            organizationPropOrderProperty = new SimpleIntegerProperty(organizationPropOrder);
        }
        return organizationPropOrderProperty;
    }

    public Long getOrganizationPropId() {
        if (Objects.nonNull(organizationPropIdProperty)) {
            return organizationPropIdProperty.get();
        }
        return organizationPropId;
    }

    public void setOrganizationPropId(Long organizationPropId) {
        if (Objects.nonNull(organizationPropIdProperty)) {
            organizationPropIdProperty.set(organizationPropId);
        } else {
            this.organizationPropId = organizationPropId;
        }
    }

    public Long getFkMasterId() {
        if (Objects.nonNull(fkMasterIdProperty)) {
            return fkMasterIdProperty.get();
        }
        return fkMasterId;
    }

    public void setFkMasterId(Long fkMasterId) {
        if (Objects.nonNull(fkMasterIdProperty)) {
            fkMasterIdProperty.set(fkMasterId);
        } else {
            this.fkMasterId = fkMasterId;
        }
    }

    public String getOrganizationPropName() {
        if (Objects.nonNull(organizationPropNameProperty)) {
            return organizationPropNameProperty.get();
        }
        return organizationPropName;
    }

    public void setOrganizationPropName(String organizationPropName) {
        if (Objects.nonNull(organizationPropNameProperty)) {
            organizationPropNameProperty.set(organizationPropName);
        } else {
            this.organizationPropName = organizationPropName;
        }
    }

    public String getOrganizationPropType() {
        if (Objects.nonNull(organizationPropTypeProperty)) {
            return organizationPropTypeProperty.get().toString();
        }
        return organizationPropType;
    }

    public void setOrganizationPropType(String organizationPropType) {
        if (Objects.nonNull(organizationPropTypeProperty)) {
            organizationPropTypeProperty.set(CustomPropertyTypeEnum.valueOf(organizationPropType));
        } else {
            this.organizationPropType = organizationPropType;
        }
    }

    public String getOrganizationPropValue() {
        if (Objects.nonNull(organizationPropValueProperty)) {
            return organizationPropValueProperty.get();
        }
        return organizationPropValue;
    }

    public void setOrganizationPropValue(String organizationPropValue) {
        if (Objects.nonNull(organizationPropValueProperty)) {
            organizationPropValueProperty.set(organizationPropValue);
        } else {
            this.organizationPropValue = organizationPropValue;
        }
    }

    public Integer getOrganizationPropOrder() {
        if (Objects.nonNull(organizationPropOrderProperty)) {
            return organizationPropOrderProperty.get();
        }
        return organizationPropOrder;
    }

    public void setOrganizationPropOrder(Integer organizationPropOrder) {
        if (Objects.nonNull(organizationPropOrderProperty)) {
            organizationPropOrderProperty.set(organizationPropOrder);
        } else {
            this.organizationPropOrder = organizationPropOrder;
        }
    }
    
    public void updateMember() {
        this.organizationPropId = getOrganizationPropId();
        this.fkMasterId = getFkMasterId();
        this.organizationPropName = getOrganizationPropName();
        this.organizationPropType = getOrganizationPropType();
        this.organizationPropValue = getOrganizationPropValue();
        this.organizationPropOrder = getOrganizationPropOrder();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.organizationPropId);
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
        final OrganizationPropertyInfoEntity other = (OrganizationPropertyInfoEntity) obj;
        if (!Objects.equals(this.organizationPropId, other.organizationPropId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "OrganizationPropertyInfoEntity{" + "organizationPropId=" + organizationPropId + ", fkMasterId=" + fkMasterId + ", organizationPropName=" + organizationPropName + ", organizationPropType=" + organizationPropType + ", organizationPropValue=" + organizationPropValue + ", organizationPropOrder=" + organizationPropOrder + '}';
    }
    
    /**
     * 表示される情報をコピーする
     * 
     * @return 
     */
    public OrganizationPropertyInfoEntity clone() {
        OrganizationPropertyInfoEntity entity = new OrganizationPropertyInfoEntity();
        entity.setOrganizationPropName(getOrganizationPropName());
        entity.setOrganizationPropType(getOrganizationPropType());
        entity.setOrganizationPropValue(getOrganizationPropValue());
        
        //以下のものは表示されないがソートで用いられることがあるためコピー
        entity.setFkMasterId(getFkMasterId());
        entity.setOrganizationPropOrder(getOrganizationPropOrder());
        entity.setOrganizationPropId(getOrganizationPropId());
        
        return entity;
    }

    /**
     * 内容が一致しているか調べる
     * 
     * @return 
     */
    public boolean displayInfoEquals(OrganizationPropertyInfoEntity info) {
        boolean ret = false;
        if(Objects.equals(getOrganizationPropName(), info.getOrganizationPropName())
                && Objects.equals(getOrganizationPropType(), info.getOrganizationPropType())
                && Objects.equals(getOrganizationPropValue(), info.getOrganizationPropValue())) {
            ret = true;
        }
        return ret;
    }
}
