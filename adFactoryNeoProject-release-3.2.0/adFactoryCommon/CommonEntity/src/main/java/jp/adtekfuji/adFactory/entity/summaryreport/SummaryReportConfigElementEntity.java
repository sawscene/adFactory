/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.summaryreport;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import jp.adtekfuji.adFactory.enumerate.CategoryEnum;

import jakarta.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * サマリーレポート設定情報のメール内容設定情報
 *
 * @author okada
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SummaryReportConfigElementEntity")
public class SummaryReportConfigElementEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    // 種別
    @JsonProperty("elementType")
    @JsonSerialize(using = CategoryEnum.Serializer.class)
    @JsonDeserialize(using = CategoryEnum.Deserializer.class)
    @XmlElement()
    private CategoryEnum elementType = CategoryEnum.NUMBER_OF_PRODUCTS_PRODUCED;

    // 基準値
    @JsonProperty("targetValue")
    @XmlElement()
    private String targetValue;

    // 閾値
    @JsonProperty("threshold")
    @XmlElement()
    private String threshold;

    // 警告色
    @JsonProperty("warningBackColor")
    @XmlElement()
    private String warningBackColor = "#FFFFFF";

    // 種別プロパティ
    @JsonIgnore
    @XmlTransient
    private ObjectProperty<CategoryEnum> elementTypeProperty = null;

    // 基準値プロパティ
    @JsonIgnore
    @XmlTransient
    private StringProperty targetValueProperty = null;

    // 閾値プロパティ
    @JsonIgnore
    @XmlTransient
    private StringProperty thresholdProperty = null;

    // 警告色プロパティ
    @JsonIgnore
    @XmlTransient
    private ObjectProperty<Color> warningBackColorProperty = null;


    /**
     * 種別を設定
     *
     * @param elementType 種別
     */
    public void setElementType(CategoryEnum elementType) {
        if (Objects.nonNull(this.elementTypeProperty)) {
            this.elementTypeProperty.set(elementType);
        } else {
            this.elementType = elementType;
        }
    }

    /**
     * 種別を取得
     *
     * @return 種別
     */
    public CategoryEnum getElementType() {
        if (Objects.nonNull(this.elementTypeProperty)) {
            return this.elementTypeProperty.get();
        }
        return this.elementType;
    }

    /**
     * 種別のプロパティを取得
     *
     * @return 種別プロパティ
     */
    public ObjectProperty<CategoryEnum> sendElementTypeProperty() {
        if (Objects.isNull(this.elementTypeProperty)) {
            this.elementTypeProperty = new SimpleObjectProperty<>(this.elementType);
        }
        return this.elementTypeProperty;
    }

    /**
     * 基準値を設定
     *
     * @param targetValue 基準値
     */
    public void setTargetValue(String targetValue) {
        if (Objects.nonNull(targetValueProperty)) {
            this.targetValueProperty.set(targetValue);
        }
        this.targetValue = targetValue;
    }

    /**
     * 基準値を取得
     *
     * @return 基準値
     */
    public String getTargetValue() {
        if (Objects.nonNull(targetValueProperty)) {
            return this.targetValueProperty.get();
        }
        return targetValue;
    }

    /**
     * 基準値のプロパティを取得
     *
     * @return 基準値プロパティ
     */
    public StringProperty targetValueProperty() {
        if (Objects.isNull(this.targetValueProperty)) {
            this.targetValueProperty = new SimpleStringProperty(this.targetValue);
        }
        return this.targetValueProperty;
    }

    /**
     * 閾値を設定
     *
     * @param threshold 閾値
     */
    public void setThreshold(String threshold) {
        if (Objects.nonNull(thresholdProperty)) {
            this.thresholdProperty.set(threshold);
        }
        this.threshold = threshold;
    }

    /**
     * 閾値を取得
     *
     * @return 閾値
     */
    public String getThreshold() {
        if (Objects.nonNull(thresholdProperty)) {
            return this.thresholdProperty.get();
        }
        return threshold;
    }

    /**
     * 閾値のプロパティを取得
     *
     * @return 閾値プロパティ
     */
    public StringProperty thresholdProperty() {
        if (Objects.isNull(this.thresholdProperty)) {
            this.thresholdProperty = new SimpleStringProperty(this.threshold);
        }
        return this.thresholdProperty;
    }

    /**
     * 異常色を設定
     *
     * @param warningBackColor 異常色
     */
    public void setWarningBackColor(String warningBackColor) {
        if (Objects.nonNull(warningBackColorProperty)) {
            this.warningBackColorProperty = new SimpleObjectProperty<>(Color.web(this.warningBackColor));
        }
        this.warningBackColor = warningBackColor;
    }

    /**
     * 異常色を取得
     *
     * @return 閾値
     */
    public String getWarningBackColor() {
        if (Objects.nonNull(warningBackColorProperty)) {
            return this.warningBackColorProperty.get().toString();
        }
        return this.warningBackColor;
    }

    /**
     * 異常色のプロパティを取得
     *
     * @return 異常色プロパティ
     */
    public ObjectProperty<Color> warningBackColorProperty() {
        if (Objects.isNull(this.warningBackColorProperty)) {
            this.warningBackColorProperty = new SimpleObjectProperty<>(Color.web(this.warningBackColor));
        }
        return this.warningBackColorProperty;
    }

    /**
     * 表示される情報が一致するか調べる
     *
     * @param other 比較対象
     * @return true:一致している
     */
    public boolean equalsDisplayInfo(SummaryReportConfigElementEntity other) {
        return Objects.equals(getElementType(), other.getElementType())
                && Objects.equals(getTargetValue(), other.getTargetValue())
                && Objects.equals(getThreshold(), other.getThreshold())
                && Objects.equals(warningBackColorProperty().getValue(), other.warningBackColorProperty().getValue());
    }

    /**
     * クローン作成
     * ※StringProperty等プロパティを利用している為、cloneableインターフェイスを実装する方法では対応出来なかった。
     *
     * @return クローンエンティティ
     */
    @Override
    public SummaryReportConfigElementEntity clone() {
        SummaryReportConfigElementEntity entity = new SummaryReportConfigElementEntity();

        // 種別
        entity.setElementType(this.getElementType());
        // 基準値
        entity.setTargetValue(this.getTargetValue());
        // 閾値
        entity.setThreshold(this.getThreshold());
        // 異常色
        entity.setWarningBackColor(this.getWarningBackColor());

        return entity;
    }
}
