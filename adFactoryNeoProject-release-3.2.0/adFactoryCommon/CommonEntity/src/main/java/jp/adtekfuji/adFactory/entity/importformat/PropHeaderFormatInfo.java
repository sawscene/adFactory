/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.importformat;

import adtekfuji.utility.Tuple;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import org.simpleframework.xml.Transient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * CSV形式（ヘッダー名指定）_工程プロパティのフォーマット情報
 *
 * @author (AQTOR)Koga
 */
@XmlRootElement(name = "propHeaderFormatInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class PropHeaderFormatInfo {

    private String propValue;


    private String propName;

    private CustomPropertyTypeEnum propertyType = CustomPropertyTypeEnum.TYPE_STRING;

    @Transient
    private StringProperty propValueProperty;

    @Transient
    private StringProperty propNameProperty;

    @Transient
    private ObjectProperty<CustomPropertyTypeEnum> propertyTypeProperty;


    /**
     * コンストラクタ
     */
    public PropHeaderFormatInfo() {
    }

    public PropHeaderFormatInfo(String propValue, String propName, CustomPropertyTypeEnum propertyType) {
        this.propValue = propValue;
        this.propName = propName;
        this.propertyType = propertyType;
    }

    public StringProperty propValueProperty() {
        if (Objects.isNull(propValueProperty)) {
            this.propValueProperty = new SimpleStringProperty(propValue);
        }
        return this.propValueProperty;
    }

    public String getPropValue() {
        if (Objects.isNull(this.propValueProperty)) {
            return propValue;
        }

        return propValueProperty.getValue();

    }

    public void setPropValue(String propValue) {
        if (Objects.isNull(this.propValueProperty)) {
            this.propValue = propValue;
        } else {
            this.propValueProperty.setValue(propValue);
        }
    }

    public StringProperty propNameProperty() {
        if(Objects.isNull(this.propNameProperty)) {
            this.propNameProperty = new SimpleStringProperty(this.propName);
        }
        return this.propNameProperty;
    }

    public String getPropName() {
        if(Objects.nonNull(this.propNameProperty)) {
            return this.propNameProperty.getValue();
        }
        return this.propName;
    }

    public void setPropName(String propName) {
        if (Objects.isNull(this.propNameProperty)) {
            this.propName = propName;
        } else {
            this.propNameProperty.setValue(propName);
        }
    }


    public ObjectProperty<CustomPropertyTypeEnum>  propertyTypeProperty()
    {
        if (Objects.isNull(propertyTypeProperty)) {
            this.propertyTypeProperty = new SimpleObjectProperty<>(this.propertyType);
        }
        return this.propertyTypeProperty;
    }

    public CustomPropertyTypeEnum getPropertyType() {
        if (Objects.isNull(this.propertyTypeProperty)) {
            return propertyType;
        }
        return this.propertyTypeProperty.getValue();
    }

    public void setPropertyType(CustomPropertyTypeEnum propertyType) {
        if (Objects.isNull(this.propertyTypeProperty)) {
            this.propertyType = propertyType;
        } else {
            this.propertyTypeProperty.setValue(propertyType);
        }
    }

    @Override
    public String toString() {
        return "PropHeaderFormatInfo{" +
                "propValue='" + propValue + '\'' +
                ", propertyType=" + propertyType +
                '}';
    }
}
