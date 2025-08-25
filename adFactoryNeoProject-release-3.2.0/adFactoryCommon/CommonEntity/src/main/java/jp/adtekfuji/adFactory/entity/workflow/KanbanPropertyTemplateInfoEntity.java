/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.workflow;

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
 * カンバンテンプレート情報.
 *
 * @author ta.ito
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "kanbanPropertyTemplate")
@JsonIgnoreProperties(ignoreUnknown=true)
public class KanbanPropertyTemplateInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty kanbanPropIdProperty;
    private LongProperty fkMasterIdProperty;
    private StringProperty kanbanPropNameProperty;
    private ObjectProperty<CustomPropertyTypeEnum> kanbanPropTypeProperty;
    private StringProperty kanbanPropInitialValueProperty;
    private IntegerProperty kanbanPropOrderProperty;

    @XmlElement(required = true, name = "kanbanPropTemplateId")
    @JsonIgnore
    private Long kanbanPropId;
    
    @XmlElement(name = "fkMasterId")
    @JsonIgnore
    private Long fkMasterId;
    
    @XmlElement(name = "propertyName")
    @JsonProperty("key")
    private String kanbanPropName;
    
    @XmlElement(name = "propertyType")
    @JsonProperty("type")
    private CustomPropertyTypeEnum kanbanPropType;
    
    @XmlElement(name = "propInitialValue")
    @JsonProperty("val")
    private String kanbanPropInitialValue;
    
    @XmlElement(name = "propertyOrder")
    @JsonProperty("disp")
    private Integer kanbanPropOrder;

    public KanbanPropertyTemplateInfoEntity() {
    }

    public KanbanPropertyTemplateInfoEntity(Long kanbanPropId, Long fkMasterId, String kanbanPropName, CustomPropertyTypeEnum kanbanPropType, String kanbanPropInitialValue, Integer kanbanPropOrder) {
        this.kanbanPropId = kanbanPropId;
        this.fkMasterId = fkMasterId;
        this.kanbanPropName = kanbanPropName;
        this.kanbanPropType = kanbanPropType;
        this.kanbanPropInitialValue = kanbanPropInitialValue;
        this.kanbanPropOrder = kanbanPropOrder;
    }

    public LongProperty kanbanPropIdProperty() {
        if (Objects.isNull(kanbanPropIdProperty)) {
            kanbanPropIdProperty = new SimpleLongProperty(kanbanPropId);
        }
        return kanbanPropIdProperty;
    }

    public LongProperty fkMasterIdProperty() {
        if (Objects.isNull(fkMasterIdProperty)) {
            fkMasterIdProperty = new SimpleLongProperty(fkMasterId);
        }
        return fkMasterIdProperty;
    }

    public StringProperty kanbanPropNameProperty() {
        if (Objects.isNull(kanbanPropNameProperty)) {
            kanbanPropNameProperty = new SimpleStringProperty(kanbanPropName);
        }
        return kanbanPropNameProperty;
    }

    public ObjectProperty<CustomPropertyTypeEnum> kanbanPropTypeProperty() {
        if (Objects.isNull(kanbanPropTypeProperty)) {
            kanbanPropTypeProperty = new SimpleObjectProperty<>(kanbanPropType);
        }
        return kanbanPropTypeProperty;
    }

    public StringProperty kanbanPropInitialValueProperty() {
        if (Objects.isNull(kanbanPropInitialValueProperty)) {
            kanbanPropInitialValueProperty = new SimpleStringProperty(kanbanPropInitialValue);
        }
        return kanbanPropInitialValueProperty;
    }

    public IntegerProperty kanbanPropOrderProperty() {
        if (Objects.isNull(kanbanPropOrderProperty)) {
            kanbanPropOrderProperty = new SimpleIntegerProperty(kanbanPropOrder);
        }
        return kanbanPropOrderProperty;
    }

    public Long getKanbanPropId() {
        if (Objects.nonNull(kanbanPropIdProperty)) {
            return kanbanPropIdProperty.get();
        }
        return kanbanPropId;
    }

    public void setKanbanPropId(Long kanbanPropId) {
        if (Objects.nonNull(kanbanPropIdProperty)) {
            kanbanPropIdProperty.set(kanbanPropId);
        } else {
            this.kanbanPropId = kanbanPropId;
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

    public String getKanbanPropName() {
        if (Objects.nonNull(kanbanPropNameProperty)) {
            return kanbanPropNameProperty.get();
        }
        return kanbanPropName;
    }

    public void setKanbanPropName(String kanbanPropName) {
        if (Objects.nonNull(kanbanPropNameProperty)) {
            kanbanPropNameProperty.set(kanbanPropName);
        } else {
            this.kanbanPropName = kanbanPropName;
        }
    }

    public CustomPropertyTypeEnum getKanbanPropType() {
        if (Objects.nonNull(kanbanPropTypeProperty)) {
            return kanbanPropTypeProperty.get();
        }
        return kanbanPropType;
    }

    public void setKanbanPropType(CustomPropertyTypeEnum kanbanPropType) {
        if (Objects.nonNull(kanbanPropTypeProperty)) {
            kanbanPropTypeProperty.set(kanbanPropType);
        } else {
            this.kanbanPropType = kanbanPropType;
        }
    }

    public String getKanbanPropInitialValue() {
        if (Objects.nonNull(kanbanPropInitialValueProperty)) {
            return kanbanPropInitialValueProperty.get();
        }
        return kanbanPropInitialValue;
    }

    public void setKanbanPropInitialValue(String kanbanPropInitialValue) {
        if (Objects.nonNull(kanbanPropInitialValueProperty)) {
            kanbanPropInitialValueProperty.set(kanbanPropInitialValue);
        } else {
            this.kanbanPropInitialValue = kanbanPropInitialValue;
        }
    }

    public Integer getKanbanPropOrder() {
        if (Objects.nonNull(kanbanPropOrderProperty)) {
            return kanbanPropOrderProperty.get();
        }
        return kanbanPropOrder;
    }

    public void setKanbanPropOrder(Integer kanbanPropOrder) {
        if (Objects.nonNull(kanbanPropOrderProperty)) {
            kanbanPropOrderProperty.set(kanbanPropOrder);
        } else {
            this.kanbanPropOrder = kanbanPropOrder;
        }
    }

    public void updateMember() {
        this.kanbanPropId = getKanbanPropId();
        this.fkMasterId = getFkMasterId();
        this.kanbanPropName = getKanbanPropName();
        this.kanbanPropType = getKanbanPropType();
        this.kanbanPropInitialValue = getKanbanPropInitialValue();
        this.kanbanPropOrder = getKanbanPropOrder();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int) (this.kanbanPropId ^ (this.kanbanPropId >>> 32));
        hash = 83 * hash + (int) (this.fkMasterId ^ (this.fkMasterId >>> 32));
        hash = 83 * hash + Objects.hashCode(this.kanbanPropName);
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
        final KanbanPropertyTemplateInfoEntity other = (KanbanPropertyTemplateInfoEntity) obj;
        if (this.getKanbanPropId() != other.getKanbanPropId()) {
            return false;
        }
        if (this.getFkMasterId() != other.getFkMasterId()) {
            return false;
        }
        if (!Objects.equals(this.getKanbanPropName(), other.getKanbanPropName())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "KanbanPropertyTemplateInfoEntity{" + "kanbanPropId=" + getKanbanPropId() + ", fkMasterId=" + getFkMasterId() + ", kanbanPropName=" + getKanbanPropName() + ", kanbanPropType=" + getKanbanPropType() + ", kanbanPropInitialValue=" + getKanbanPropInitialValue() + ", kanbanPropOrder=" + getKanbanPropOrder() + '}';
    }

    /**
     * 表示される情報が一致するか調べる
     *
     * @param other
     * @return
     */
    public boolean equalsDisplayInfo(KanbanPropertyTemplateInfoEntity other) {
        if (Objects.equals(getKanbanPropName(), other.getKanbanPropName())
                && Objects.equals(getKanbanPropType(), other.getKanbanPropType())
                && Objects.equals(getKanbanPropInitialValue(), other.getKanbanPropInitialValue())) {
            return true;
        }
        return false;
    }

    /**
     * 情報をコピーする
     *
     * @return
     */
    @Override
    public KanbanPropertyTemplateInfoEntity clone() {
        KanbanPropertyTemplateInfoEntity entity = new KanbanPropertyTemplateInfoEntity();
        
        entity.setKanbanPropId(kanbanPropId);
        entity.setFkMasterId(fkMasterId);
        entity.setKanbanPropName(kanbanPropName);
        entity.setKanbanPropType(kanbanPropType);
        entity.setKanbanPropInitialValue(kanbanPropInitialValue);
        entity.setKanbanPropOrder(kanbanPropOrder);

        return entity;
    }
}
