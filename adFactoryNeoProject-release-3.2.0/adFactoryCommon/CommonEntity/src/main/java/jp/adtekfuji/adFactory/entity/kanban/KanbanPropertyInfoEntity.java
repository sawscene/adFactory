/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.kanban;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
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
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;

/**
 * カンバンプロパティ情報
 *
 * @author e-mori
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "kanbanProperty")
@JsonIgnoreProperties(ignoreUnknown=true)
public class KanbanPropertyInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlTransient
    private LongProperty kanbanPropIdProperty;
    @XmlTransient
    private LongProperty fkMasterIdProperty;
    @XmlTransient
    private StringProperty kanbanPropNameProperty;
    @XmlTransient
    private ObjectProperty<CustomPropertyTypeEnum> kanbanPropTypeProperty;
    @XmlTransient
    private StringProperty kanbanPropValueProperty;
    @XmlTransient
    private IntegerProperty kanbanPropOrderProperty;

    @XmlElement(required = true)
    @JsonIgnore
    private Long kanbannPropertyId;

    @XmlElement()
    @JsonIgnore
    private Long fkKanbanId;

    @XmlElement()
    @JsonProperty("key")
    private String kanbanPropertyName;

    @XmlElement()
    @JsonProperty("type")
    private CustomPropertyTypeEnum kanbanPropertyType;

    @XmlElement()
    @JsonProperty("val")
    private String kanbanPropertyValue;

    @XmlElement()
    @JsonProperty("disp")
    private Integer kanbanPropertyOrder;

    public KanbanPropertyInfoEntity() {
    }

    public KanbanPropertyInfoEntity(Long kanbannPropertyId, Long fkKanbanId, String kanbanPropertyName, CustomPropertyTypeEnum kanbanPropertyType, String kanbanPropertyValue, Integer kanbanPropertyOrder) {
        this.kanbannPropertyId = kanbannPropertyId;
        this.fkKanbanId = fkKanbanId;
        this.kanbanPropertyName = kanbanPropertyName;
        this.kanbanPropertyType = kanbanPropertyType;
        this.kanbanPropertyValue = kanbanPropertyValue;
        this.kanbanPropertyOrder = kanbanPropertyOrder;
    }

    public KanbanPropertyInfoEntity(KanbanPropertyInfoEntity kanbanPropertyInfoEntity) {
        this.kanbannPropertyId = kanbanPropertyInfoEntity.kanbannPropertyId;
        this.fkKanbanId = kanbanPropertyInfoEntity.fkKanbanId;
        this.kanbanPropertyName = kanbanPropertyInfoEntity.kanbanPropertyName;
        this.kanbanPropertyType = kanbanPropertyInfoEntity.kanbanPropertyType;
        this.kanbanPropertyValue = kanbanPropertyInfoEntity.kanbanPropertyValue;
        this.kanbanPropertyOrder = kanbanPropertyInfoEntity.kanbanPropertyOrder;
    }

    public LongProperty kanbanPropIdProperty() {
        if (Objects.isNull(kanbanPropIdProperty)) {
            kanbanPropIdProperty = new SimpleLongProperty(kanbannPropertyId);
        }
        return kanbanPropIdProperty;
    }

    public LongProperty fkMasterIdProperty() {
        if (Objects.isNull(fkMasterIdProperty)) {
            fkMasterIdProperty = new SimpleLongProperty(fkKanbanId);
        }
        return fkMasterIdProperty;
    }

    public StringProperty kanbanPropNameProperty() {
        if (Objects.isNull(kanbanPropNameProperty)) {
            kanbanPropNameProperty = new SimpleStringProperty(kanbanPropertyName);
        }
        return kanbanPropNameProperty;
    }

    public ObjectProperty kanbanPropTypeProperty() {
        if (Objects.isNull(kanbanPropTypeProperty)) {
            kanbanPropTypeProperty = new SimpleObjectProperty<>(kanbanPropertyType);
        }
        return kanbanPropTypeProperty;
    }

    public StringProperty kanbanPropValueProperty() {
        if (Objects.isNull(kanbanPropValueProperty)) {
            kanbanPropValueProperty = new SimpleStringProperty(kanbanPropertyValue);
        }
        return kanbanPropValueProperty;
    }

    public IntegerProperty kanbanPropOrderProperty() {
        if (Objects.isNull(kanbanPropOrderProperty)) {
            kanbanPropOrderProperty = new SimpleIntegerProperty(kanbanPropertyOrder);
        }
        return kanbanPropOrderProperty;
    }

    @JsonIgnore
    public Long getKanbanPropId() {
        if (Objects.nonNull(kanbanPropIdProperty)) {
            return kanbanPropIdProperty.get();
        }
        return kanbannPropertyId;
    }

    @JsonIgnore
    public void setKanbanPropId(Long kanbanPropId) {
        if (Objects.nonNull(kanbanPropIdProperty)) {
            kanbanPropIdProperty.set(kanbanPropId);
        } else {
            this.kanbannPropertyId = kanbanPropId;
        }
    }

    public Long getFkKanbanId() {
        if (Objects.nonNull(fkMasterIdProperty)) {
            return fkMasterIdProperty.get();
        }
        return fkKanbanId;
    }

    public void setFkKanbanId(Long fkKanbanId) {
        if (Objects.nonNull(fkMasterIdProperty)) {
            fkMasterIdProperty.set(fkKanbanId);
        } else {
            this.fkKanbanId = fkKanbanId;
        }
    }

    public String getKanbanPropertyName() {
        if (Objects.nonNull(kanbanPropNameProperty)) {
            return kanbanPropNameProperty.get();
        }
        return kanbanPropertyName;
    }

    public void setKanbanPropertyName(String kanbanPropertyName) {
        if (Objects.nonNull(kanbanPropNameProperty)) {
            kanbanPropNameProperty.set(kanbanPropertyName);
        } else {
            this.kanbanPropertyName = kanbanPropertyName;
        }
    }

    public CustomPropertyTypeEnum getKanbanPropertyType() {
        if (Objects.nonNull(kanbanPropTypeProperty)) {
            return kanbanPropTypeProperty.get();
        }
        return kanbanPropertyType;
    }

    public void setKanbanPropertyType(CustomPropertyTypeEnum kanbanPropertyType) {
        if (Objects.nonNull(kanbanPropTypeProperty)) {
            kanbanPropTypeProperty.set(kanbanPropertyType);
        } else {
            this.kanbanPropertyType = kanbanPropertyType;
        }
    }

    public String getKanbanPropertyValue() {
        if (Objects.nonNull(kanbanPropValueProperty)) {
            return kanbanPropValueProperty.get();
        }
        return kanbanPropertyValue;
    }

    public void setKanbanPropertyValue(String kanbanPropertyValue) {
        if (Objects.nonNull(kanbanPropValueProperty)) {
            kanbanPropValueProperty.set(kanbanPropertyValue);
        } else {
            this.kanbanPropertyValue = kanbanPropertyValue;
        }
    }

    public Integer getKanbanPropertyOrder() {
        if (Objects.nonNull(kanbanPropOrderProperty)) {
            return kanbanPropOrderProperty.get();
        }
        return kanbanPropertyOrder;
    }

    public void setKanbanPropertyOrder(Integer kanbanPropertyOrder) {
        if (Objects.nonNull(kanbanPropOrderProperty)) {
            kanbanPropOrderProperty.set(kanbanPropertyOrder);
        } else {
            this.kanbanPropertyOrder = kanbanPropertyOrder;
        }
    }

    public void updateMember() {
        this.kanbannPropertyId = getKanbanPropId();
        this.fkKanbanId = getFkKanbanId();
        this.kanbanPropertyName = getKanbanPropertyName();
        this.kanbanPropertyType = getKanbanPropertyType();
        this.kanbanPropertyValue = getKanbanPropertyValue();
        this.kanbanPropertyOrder = getKanbanPropertyOrder();

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.kanbannPropertyId);
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
        final KanbanPropertyInfoEntity other = (KanbanPropertyInfoEntity) obj;
        if (!Objects.equals(this.kanbannPropertyId, other.kanbannPropertyId)
                || !Objects.equals(this.fkKanbanId, other.fkKanbanId)
                || !Objects.equals(this.kanbanPropertyName, other.kanbanPropertyName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "KanbanPropertyInfoEntity{" + "kanbanPropId=" + kanbannPropertyId + ", fkKanbanId=" + fkKanbanId + ", kanbanPropertyName=" + kanbanPropertyName + ", kanbanPropertyType=" + kanbanPropertyType + ", kanbanPropertyValue=" + kanbanPropertyValue + ", kanbanPropertyOrder=" + kanbanPropertyOrder + '}';
    }

    /**
     * 表示される情報をコピーする
     * 
     * @return 
     */
    @Override
    public KanbanPropertyInfoEntity clone() {
        KanbanPropertyInfoEntity entity = new KanbanPropertyInfoEntity();
        
        entity.setKanbanPropId(getKanbanPropId());//比較はしないがソートに使うためコピー
        entity.setKanbanPropertyName(getKanbanPropertyName());
        entity.setKanbanPropertyType(getKanbanPropertyType());
        entity.setKanbanPropertyValue(getKanbanPropertyValue());
        
        return entity;
    }
    
    /**
     * 表示される情報が一致するか調べる
     * 
     * @param other
     * @return 
     */
    public boolean equalsDisplayInfo(KanbanPropertyInfoEntity other) {
        boolean ret = false;
        
        if(Objects.equals(getKanbanPropertyName(), other.getKanbanPropertyName())
                && Objects.equals(getKanbanPropertyType(), other.getKanbanPropertyType())
                && Objects.equals(getKanbanPropertyValue(), other.getKanbanPropertyValue())) {
            ret = true;
        }
        
        return ret;
    }
}
