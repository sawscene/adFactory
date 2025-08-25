/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.master;

import adtekfuji.utility.StringUtils;
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
import javafx.scene.paint.Color;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * ラベルマスタ
 *
 * @author kentarou.suzuki
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "label")
public class LabelInfoEntity implements Serializable {

    /**
     * エンティティのバージョン
     */
    private static final long serialVersionUID = 1L;

    /**
     * ラベルIDプロパティ
     */
    private LongProperty labelIdProperty;
    /**
     * ラベル名プロパティ
     */
    private StringProperty labelNameProperty;
    /**
     * 文字色プロパティ
     */
    private ObjectProperty<Color> fontColorProperty;
    /**
     * 背景色プロパティ
     */
    private ObjectProperty<Color> backColorProperty;
    /**
     * 優先度プロパティ
     */
    private IntegerProperty labelPriorityProperty;

    /**
     * ラベルID
     */
    @XmlElement(required = true)
    private Long labelId;
    /**
     * ラベル名
     */
    @XmlElement()
    private String labelName;
    /**
     * 文字色
     */
    @XmlElement()
    private String fontColor = "#000000";
    /**
     * 背景色
     */
    @XmlElement()
    private String backColor = "#FFFFFF";
    /**
     * 優先度
     */
    @XmlElement()
    private Integer labelPriority;
    /**
     * 排他用バージョン
     */
    @XmlElement()
    private Integer verInfo;

    /**
     * コンストラクタ
     */
    public LabelInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param in ラベルマスタエンティティ
     */
    public LabelInfoEntity(LabelInfoEntity in) {
        this.labelId = in.labelId;
        this.labelName = in.labelName;
        this.fontColor = in.fontColor;
        this.backColor = in.backColor;
        this.labelPriority = in.labelPriority;
        this.verInfo = in.verInfo;
    }

    /**
     * コンストラクタ
     *
     * @param labelId ラベルID
     * @param labelName ラベル名
     */
    public LabelInfoEntity(Long labelId, String labelName) {
        this.labelId = labelId;
        this.labelName = labelName;
    }

    /**
     * ラベルIDプロパティを取得する。
     *
     * @return ラベルIDプロパティ
     */
    public LongProperty labelIdProperty() {
        if (Objects.isNull(this.labelIdProperty)) {
            this.labelIdProperty = new SimpleLongProperty(this.labelId);
        }
        return this.labelIdProperty;
    }

    /**
     * ラベル名プロパティを取得する。
     *
     * @return ラベル名プロパティ
     */
    public StringProperty labelNameProperty() {
        if (Objects.isNull(this.labelNameProperty)) {
            this.labelNameProperty = new SimpleStringProperty(this.labelName);
        }
        return this.labelNameProperty;
    }

    /**
     * 文字色プロパティを取得する。
     *
     * @return 文字色プロパティ
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
     * @return 背景色プロパティ
     */
    public ObjectProperty<Color> backColorProperty() {
        if (Objects.isNull(this.backColorProperty)) {
            this.backColorProperty = new SimpleObjectProperty<>(Color.web(this.backColor));
        }
        return this.backColorProperty;
    }

    /**
     * 優先度プロパティを取得する。
     *
     * @return 優先度プロパティ
     */
    public IntegerProperty labelPriorityProperty() {
        if (Objects.isNull(this.labelPriorityProperty)) {
            this.labelPriorityProperty = new SimpleIntegerProperty(this.labelPriority);
        }
        return this.labelPriorityProperty;
    }

    /**
     * ラベルIDを取得する。
     *
     * @return ラベルID
     */
    public Long getLabelId() {
        if (Objects.nonNull(this.labelIdProperty)) {
            return this.labelIdProperty.get();
        }
        return this.labelId;
    }

    /**
     * ラベルIDを設定する。
     *
     * @param labelId ラベルID
     */
    public void setLabelId(Long labelId) {
        if (Objects.nonNull(this.labelIdProperty)) {
            this.labelIdProperty.set(labelId);
        } else {
            this.labelId = labelId;
        }
    }

    /**
     * ラベル名を取得する。
     *
     * @return ラベル名
     */
    public String getLabelName() {
        if (Objects.nonNull(this.labelNameProperty)) {
            return this.labelNameProperty.get();
        }
        return this.labelName;
    }

    /**
     * ラベル名を設定する。
     *
     * @param labelName ラベル名
     */
    public void setLabelName(String labelName) {
        if (Objects.nonNull(this.labelNameProperty)) {
            this.labelNameProperty.set(labelName);
        } else {
            this.labelName = labelName;
        }
    }

    /**
     * 文字色を取得する。
     *
     * @return 文字色
     */
    public String getFontColor() {
        if (Objects.nonNull(this.fontColorProperty)) {
            return StringUtils.colorToRGBCode(this.fontColorProperty.get());
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
            return StringUtils.colorToRGBCode(this.backColorProperty.get());
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
     * 優先度を取得する。
     *
     * @return 優先度
     */
    public Integer getLabelPriority() {
        if (Objects.nonNull(this.labelPriorityProperty)) {
            return this.labelPriorityProperty.get();
        }
        return this.labelPriority;
    }

    /**
     * 優先度を設定する。
     *
     * @param labelPriority 優先度
     */
    public void setLabelPriority(Integer labelPriority) {
        if (Objects.nonNull(this.labelPriorityProperty)) {
            this.labelPriorityProperty.set(labelPriority);
        } else {
            this.labelPriority = labelPriority;
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
        this.labelName = getLabelName();
        this.fontColor = getFontColor();
        this.backColor = getBackColor();
        this.labelPriority = getLabelPriority();
    }

    /**
     * ハッシュコードを取得する。
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.labelId);
        hash = 17 * hash + Objects.hashCode(this.labelName);
        return hash;
    }

    /**
     * オブジェクトが等しいかどうかを取得する。
     * 
     * @param obj 比較対象のオブジェクト
     * @return オブジェクトが等しい場合はtrue、それ以外の場合はfalse
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LabelInfoEntity other = (LabelInfoEntity) obj;
        if (!Objects.equals(this.getLabelId(), other.getLabelId())) {
            return false;
        }
        if (!Objects.equals(this.getLabelName(), other.getLabelName())) {
            return false;
        }
        if (!Objects.equals(this.getFontColor(), other.getFontColor())) {
            return false;
        }
        if (!Objects.equals(this.getBackColor(), other.getBackColor())) {
            return false;
        }
        if (!Objects.equals(this.getLabelPriority(), other.getLabelPriority())) {
            return false;
        }
        return true;
    }

    /**
     * 文字列表現を取得する。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LabelInfoEntity{labelId=").append(labelId);
        sb.append(", labelName=").append(labelName);
        sb.append(", fontColor=").append(fontColor);
        sb.append(", backColor=").append(backColor);
        sb.append(", labelPriority=").append(labelPriority);
        sb.append(", verInfo=").append(verInfo);
        sb.append('}');
        return sb.toString();
    }
}
