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
 * 遅延理由情報
 *
 * @author e-mori
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "delayReason")
public class DelayReasonInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty delayIdProperty;
    private StringProperty delayReasonProperty;
    private ObjectProperty<Color> fontColorProperty;
    private ObjectProperty<Color> backColorProperty;
    private ObjectProperty<LightPatternEnum> lightPatternProperty;

    @XmlElement(required = true)
    private Long delayId;// 遅延理由ID
    @XmlElement()
    private String delayReason;// 遅延理由
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
    public DelayReasonInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param in 遅延理由情報
     */
    public DelayReasonInfoEntity(DelayReasonInfoEntity in) {
        this.delayId = in.delayId;
        this.delayReason = in.delayReason;
        this.fontColor = in.fontColor;
        this.backColor = in.backColor;
        this.lightPattern = in.lightPattern;
        this.verInfo = in.verInfo;
    }

    /**
     * コンストラクタ
     *
     * @param delayId 遅延理由ID
     * @param delayReason 
     */
    public DelayReasonInfoEntity(Long delayId, String delayReason) {
        this.delayId = delayId;
        this.delayReason = delayReason;
    }

    /**
     * 遅延理由IDプロパティを取得する。
     *
     * @return 遅延理由ID
     */
    public LongProperty delayIdProperty() {
        if (Objects.isNull(this.delayIdProperty)) {
            this.delayIdProperty = new SimpleLongProperty(this.delayId);
        }
        return this.delayIdProperty;
    }

    /**
     * 遅延理由プロパティを取得する。
     *
     * @return 遅延理由
     */
    public StringProperty delayReasonProperty() {
        if (Objects.isNull(this.delayReasonProperty)) {
            this.delayReasonProperty = new SimpleStringProperty(this.delayReason);
        }
        return this.delayReasonProperty;
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
     * プロパティを取得する。
     *
     * @return 
     */
    public ObjectProperty<LightPatternEnum> lightPatternProperty() {
        if (Objects.isNull(this.lightPatternProperty)) {
            this.lightPatternProperty = new SimpleObjectProperty<>(this.lightPattern);
        }
        return this.lightPatternProperty;
    }

    /**
     * 遅延理由IDを取得する。
     *
     * @return 遅延理由ID
     */
    public Long getDelaytId() {
        if (Objects.nonNull(this.delayIdProperty)) {
            return this.delayIdProperty.get();
        }
        return this.delayId;
    }

    /**
     * 遅延理由IDを設定する。
     *
     * @param delayId 遅延理由ID
     */
    public void setDelayId(Long delayId) {
        if (Objects.nonNull(this.delayIdProperty)) {
            this.delayIdProperty.set(delayId);
        } else {
            this.delayId = delayId;
        }
    }

    /**
     * 遅延理由を取得する。
     *
     * @return 遅延理由
     */
    public String getDelayReason() {
        if (Objects.nonNull(this.delayReasonProperty)) {
            return this.delayReasonProperty.get();
        }
        return this.delayReason;
    }

    /**
     * 遅延理由を設定する。
     *
     * @param delayReason 遅延理由
     */
    public void setDelayReason(String delayReason) {
        if (Objects.nonNull(this.delayReasonProperty)) {
            this.delayReasonProperty.set(delayReason);
        } else {
            this.delayReason = delayReason;
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
        this.delayReason = getDelayReason();
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
        hash = 83 * hash + (int) (this.delayId ^ (this.delayId >>> 32));
        hash = 83 * hash + Objects.hashCode(this.delayReason);
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
        final DelayReasonInfoEntity other = (DelayReasonInfoEntity) obj;
        if (!Objects.equals(this.getDelaytId(), other.getDelaytId())
                || !Objects.equals(this.getDelayReason(), other.getDelayReason())
                || !Objects.equals(this.getBackColor(), other.getBackColor())
                || !Objects.equals(this.getFontColor(), other.getFontColor())
                || !Objects.equals(this.getLightPattern(), other.getLightPattern())) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("DelayReasonInfoEntity{")
                .append("delayId=").append(this.delayId)
                .append(", ")
                .append("delayReason=").append(this.delayReason)
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
