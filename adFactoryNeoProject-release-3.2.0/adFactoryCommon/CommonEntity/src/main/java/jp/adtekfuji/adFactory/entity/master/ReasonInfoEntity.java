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
import jp.adtekfuji.adFactory.enumerate.ReasonTypeEnum;

/**
 * 理由マスタ
 *
 * @author
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "reason")
public class ReasonInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty idProperty;
    private StringProperty reasonProperty;
    private ObjectProperty<ReasonTypeEnum> typeProperty;

    private ObjectProperty<Color> fontColorProperty;
    private ObjectProperty<Color> backColorProperty;
    private ObjectProperty<LightPatternEnum> lightPatternProperty;

    @XmlElement(required = true)
    private Long id;// 理由ID
    @XmlElement()
    private String reason;// 理由
    @XmlElement()
    private ReasonTypeEnum reason_type = ReasonTypeEnum.TYPE_CALL;// 理由種別
    @XmlElement()
    private Long reason_order;// 理由オーダー

    @XmlElement()
    private String fontColor = "#000000";// 文字色
    @XmlElement()
    private String backColor = "#FFFFFF";// 背景色
    @XmlElement()
    private LightPatternEnum lightPattern = LightPatternEnum.LIGHTING;// 点灯パターン

    @XmlElement()
    private Integer verInfo;// 排他用バーション

    @XmlElement()
    private Long reason_category_id;// 理由区分ID

    
    /**
     * コンストラクタ
     */
    public ReasonInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param in 理由マスタ
     */
    public ReasonInfoEntity(ReasonInfoEntity in) {
        this.id = in.id;
        this.reason = in.reason;
        this.reason_type = in.reason_type;
        this.reason_order = in.reason_order;
        this.fontColor = in.fontColor;
        this.backColor = in.backColor;
        this.lightPattern = in.lightPattern;
        this.verInfo = in.verInfo;
        this.reason_category_id = in.reason_category_id;
    }

    /**
     * コンストラクタ
     *
     * @param id 理由ID
     * @param type 理由種別
     * @param reason 理由
     */
    public ReasonInfoEntity(Long id, ReasonTypeEnum type, String reason) {
        this.id = id;
        this.reason = reason;
        this.reason_type = type;
        this.reason_order = -1L;
    }
    
    /**
     * コンストラクタ
     * 
     * @param reason_type 理由種別
     */
    public ReasonInfoEntity(ReasonTypeEnum reason_type) {
        this.reason_type = reason_type;
    }

    /**
     * 理由IDプロパティを取得する。
     *
     * @return 理由ID
     */
    public LongProperty idProperty() {
        if (Objects.isNull(this.idProperty)) {
            this.idProperty = new SimpleLongProperty(this.id);
        }
        return this.idProperty;
    }

    /**
     * 理由プロパティを取得する。
     *
     * @return 理由
     */
    public StringProperty reasonProperty() {
        if (Objects.isNull(this.reasonProperty)) {
            this.reasonProperty = new SimpleStringProperty(this.reason);
        }
        return this.reasonProperty;
    }

    /**
     * 理由種別プロパティを取得する。
     *
     * @return 理由種別
     */
    public ObjectProperty<ReasonTypeEnum> typeProperty() {
        if (Objects.isNull(this.typeProperty)) {
            this.typeProperty = new SimpleObjectProperty(this.reason_type);
        }
        return this.typeProperty;
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
     * 理由IDを取得する。
     *
     * @return 理由ID
     */
    public Long getId() {
        if (Objects.nonNull(this.idProperty)) {
            return this.idProperty.get();
        }
        return this.id;
    }

    /**
     * 理由IDを設定する。
     *
     * @param id 理由ID
     */
    public void setId(Long id) {
        if (Objects.nonNull(this.idProperty)) {
            this.idProperty.set(id);
        } else {
            this.id = id;
        }
    }

    /**
     * 理由を取得する。
     *
     * @return 理由
     */
    public String getReason() {
        if (Objects.nonNull(this.reasonProperty)) {
            return this.reasonProperty.get();
        }
        return this.reason;
    }

    /**
     * 理由を設定する。
     *
     * @param reason 理由
     */
    public void setReason(String reason) {
        if (Objects.nonNull(this.reasonProperty)) {
            this.reasonProperty.set(reason);
        } else {
            this.reason = reason;
        }
    }

    /**
     * 理由種別を取得する。
     *
     * @return 理由種別
     */
    public ReasonTypeEnum getType() {
        if (Objects.nonNull(this.typeProperty)) {
            return this.typeProperty.get();
        }
        return this.reason_type;
    }

    /**
     * 理由種別を設定する。
     *
     * @param type 理由種別
     */
    public void setType(ReasonTypeEnum type) {
        if (Objects.nonNull(this.typeProperty)) {
            this.typeProperty.set(type);
        } else {
            this.reason_type = type;
        }
    }

    /**
     * 理由オーダーを取得する。
     *
     * @return 理由オーダー
     */
    public Long getReasonOrder() {
        return this.reason_order;
    }

    /**
     * 理由オーダーを設定する。
     *
     * @param order 理由オーダー
     */
    public void setReasonOrder(Long order) {
        this.reason_order = order;
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
     * 理由区分IDを取得する。
     *
     * @return 理由区分ID
     */
    public Long getReasonCategoryId() {
        return this.reason_category_id;
    }

    /**
     * 理由区分IDを設定する。
     *
     * @param reasonCategoryId 理由区分ID
     */
    public void setReasonCategoryId(Long reasonCategoryId) {
        this.reason_category_id = reasonCategoryId;
    }
    /**
     * 内部変数を更新する。
     */
    public void updateData() {
        this.reason_type = this.getType();
        this.reason = this.getReason();
        this.fontColor = this.getFontColor();
        this.backColor = this.getBackColor();
        this.lightPattern = this.getLightPattern();
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
        hash = 83 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 83 * hash + Objects.hashCode(this.reason);
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
        final ReasonInfoEntity other = (ReasonInfoEntity) obj;
        if (!Objects.equals(this.getId(), other.getId())
                || !Objects.equals(this.getType(), other.getType())
                || !Objects.equals(this.getReason(), other.getReason())
                || !Objects.equals(this.getBackColor(), other.getBackColor())
                || !Objects.equals(this.getFontColor(), other.getFontColor())
                || !Objects.equals(this.getLightPattern(), other.getLightPattern())
                || !Objects.equals(this.getReasonOrder(), other.getReasonOrder())) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ReasonInfoEntity{")
                .append("id=").append(this.id)
                .append(", ")
                .append("reason_type=").append(this.reason_type)
                .append(", ")
                .append("reason=").append(this.reason)
                .append(", ")
                .append("fontColor=").append(this.fontColor)
                .append(", ")
                .append("backColor=").append(this.backColor)
                .append(", ")
                .append("lightPattern=").append(this.lightPattern)
                .append(", ")
                .append("reason_order=").append(this.reason_order)
                .append(", ")
                .append("verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
