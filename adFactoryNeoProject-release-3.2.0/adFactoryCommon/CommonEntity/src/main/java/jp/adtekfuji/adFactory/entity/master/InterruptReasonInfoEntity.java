/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.master;

import java.io.Serializable;
import java.util.Objects;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.LightPatternEnum;

/**
 * 中断理由情報
 *
 * @author e-mori
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "interruptReason")
public class InterruptReasonInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty interruptIdProperty;
    private StringProperty interruptReasonProperty;
    private ObjectProperty<Color> fontColorProperty;
    private ObjectProperty<Color> backColorProperty;
    private ObjectProperty<LightPatternEnum> lightPatternProperty;

    @XmlElement(required = true)
    private Long interruptId;// 中断理由ID
    @XmlElement()
    private String interruptReason;// 中断理由
    @XmlElement()
    private String fontColor = "#000000";// 文字色
    @XmlElement()
    private String backColor = "#FFFFFF";// 背景色
    @XmlElement()
    private LightPatternEnum lightPattern = LightPatternEnum.LIGHTING;// 点灯パターン

    @XmlElement()
    private Integer verInfo;// 排他用バーション

    /**
     * コンストラクタ
     */
    public InterruptReasonInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param in 中断理由情報
     */
    public InterruptReasonInfoEntity(InterruptReasonInfoEntity in) {
        this.interruptId = in.interruptId;
        this.interruptReason = in.interruptReason;
        this.fontColor = in.fontColor;
        this.backColor = in.backColor;
        this.lightPattern = in.lightPattern;
        this.verInfo = in.verInfo;
    }

    /**
     * コンストラクタ
     *
     * @param interruptId 中断理由ID
     * @param interruptReason 中断理由
     */
    public InterruptReasonInfoEntity(Long interruptId, String interruptReason) {
        this.interruptId = interruptId;
        this.interruptReason = interruptReason;
    }

    /**
     * 中断理由IDプロパティを取得する。
     *
     * @return 中断理由ID
     */
    public LongProperty interruptIdProperty() {
        if (Objects.isNull(this.interruptIdProperty)) {
            this.interruptIdProperty = new SimpleLongProperty(this.interruptId);
        }
        return this.interruptIdProperty;
    }

    /**
     * 中断理由プロパティを取得する。
     *
     * @return 中断理由
     */
    public StringProperty interruptReasonProperty() {
        if (Objects.isNull(this.interruptReasonProperty)) {
            this.interruptReasonProperty = new SimpleStringProperty(this.interruptReason);
        }
        return this.interruptReasonProperty;
    }

    /**
     * 文字色プロパティを取得する。
     *
     * @return 文字色
     */
    public ObjectProperty<Color> fontColorProperty() {
        if (Objects.isNull(this.fontColorProperty)) {
            this.fontColorProperty = new SimpleObjectProperty<>(Color.web(this.fontColor));
        }
        return this.fontColorProperty;
    }

    /**
     * 背景色プロパティを取得する。
     *
     * @return 背景色
     */
    public ObjectProperty<Color> backColorProperty() {
        if (Objects.isNull(this.backColorProperty)) {
            this.backColorProperty = new SimpleObjectProperty<>(Color.web(this.backColor));
        }
        return this.backColorProperty;
    }

    /**
     * 点灯パターンプロパティを取得する。
     *
     * @return 点灯パターン
     */
    public ObjectProperty<LightPatternEnum> lightPatternProperty() {
        if (Objects.isNull(this.lightPatternProperty)) {
            this.lightPatternProperty = new SimpleObjectProperty<>(this.lightPattern);
        }
        return this.lightPatternProperty;
    }

    /**
     * 中断理由IDを取得する。
     *
     * @return 中断理由ID
     */
    public Long getInterruptId() {
        if (Objects.nonNull(this.interruptIdProperty)) {
            return this.interruptIdProperty.get();
        }
        return this.interruptId;
    }

    /**
     * 中断理由IDを設定する。
     *
     * @param interruptId 中断理由ID
     */
    public void setInterruptId(Long interruptId) {
        if (Objects.nonNull(this.interruptIdProperty)) {
            this.interruptIdProperty.set(interruptId);
        } else {
            this.interruptId = interruptId;
        }
    }

    /**
     * 中断理由を取得する。
     *
     * @return 中断理由
     */
    public String getInterruptReason() {
        if (Objects.nonNull(this.interruptReasonProperty)) {
            return this.interruptReasonProperty.get();
        }
        return this.interruptReason;
    }

    /**
     * 中断理由を設定する。
     *
     * @param interruptReason 中断理由
     */
    public void setInterruptReason(String interruptReason) {
        if (Objects.nonNull(this.interruptReasonProperty)) {
            this.interruptReasonProperty.set(interruptReason);
        } else {
            this.interruptReason = interruptReason;
        }
    }

    /**
     * 文字色を取得する。
     *
     * @return 文字色
     */
    public String getFontColor() {
        if (Objects.nonNull(this.fontColorProperty)) {
            return toRGBCode(this.fontColorProperty.get());
        }
        return this.fontColor;
    }

    /**
     * 文字色を設定する。
     *
     * @param fontColor 文字色
     */
    public void setFontColor(String fontColor) {
        if (Objects.nonNull(this.fontColorProperty)) {
            this.fontColorProperty.set(Color.web(fontColor));
        } else {
            this.fontColor = fontColor;
        }
    }

    /**
     * 背景色を取得する。
     *
     * @return 背景色
     */
    public String getBackColor() {
        if (Objects.nonNull(this.backColorProperty)) {
            return toRGBCode(this.backColorProperty.get());
        }
        return this.backColor;
    }

    /**
     * 背景色を設定する。
     *
     * @param backColor 背景色
     */
    public void setBackColor(String backColor) {
        if (Objects.nonNull(this.backColorProperty)) {
            this.backColorProperty.set(Color.web(backColor));
        } else {
            this.backColor = backColor;
        }
    }

    /**
     * 点灯パターンを取得する。
     *
     * @return 点灯パターン
     */
    public LightPatternEnum getLightPattern() {
        if (Objects.nonNull(this.lightPatternProperty)) {
            return this.lightPatternProperty.get();
        }
        return this.lightPattern;
    }

    /**
     * 点灯パターンを設定する。
     *
     * @param lightPattern 点灯パターン
     */
    public void setLightPattern(LightPatternEnum lightPattern) {
        if (Objects.nonNull(this.lightPatternProperty)) {
            this.lightPatternProperty.set(lightPattern);
        } else {
            this.lightPattern = lightPattern;
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
     * 内部変数を更新する。
     */
    public void updateData() {
        this.interruptReason = getInterruptReason();
        this.fontColor = getFontColor();
        this.backColor = getBackColor();
        this.lightPattern = getLightPattern();
    }

    /**
     * Colorを文字列に変換する。
     *
     * @param color 色
     * @return Colorの文字列
     */
    private String toRGBCode(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int) (this.interruptId ^ (this.interruptId >>> 32));
        hash = 83 * hash + Objects.hashCode(this.interruptReason);
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
        final InterruptReasonInfoEntity other = (InterruptReasonInfoEntity) obj;
        if (!Objects.equals(this.getInterruptId(), other.getInterruptId())) {
            return false;
        }
        if (!this.getInterruptReason().equals(other.getInterruptReason())) {
            return false;
        }
        if (!this.getFontColor().equals(other.getFontColor())) {
            return false;
        }
        if (!this.getBackColor().equals(other.getBackColor())) {
            return false;
        }
        if (!this.getLightPattern().equals(other.getLightPattern())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("InterruptReasonEntity{")
                .append("interruptId=").append(this.interruptId)
                .append(", ")
                .append("interruptReason=").append(this.interruptReason)
                .append(", ")
                .append("fontColor=").append(this.fontColor)
                .append(", ")
                .append("backColor=").append(this.backColor)
                .append(", ")
                .append("lightPattern=").append(this.lightPattern)
                .append(", ")
                .append("verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
