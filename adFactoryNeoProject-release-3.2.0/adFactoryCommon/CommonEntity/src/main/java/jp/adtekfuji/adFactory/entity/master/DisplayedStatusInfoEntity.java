/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.master;

import java.io.Serializable;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;

/**
 * 表示ステータス情報
 *
 * @author e-mori
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "displayedStatus")
public class DisplayedStatusInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty statusIdProperty;
    private ObjectProperty<StatusPatternEnum> statusNameProperty;
    private ObjectProperty<Color> fontColorProperty;
    private ObjectProperty<Color> backColorProperty;
    private ObjectProperty<LightPatternEnum> lightPatternProperty;
    private StringProperty notationNameProperty;
    private StringProperty melodyPathProperty;
    private BooleanProperty melodyRepeatProperty;

    @XmlElement(required = true)
    private Long statusId;// ステータス表示ID
    @XmlElement()
    private StatusPatternEnum statusName;// ステータス名
    @XmlElement()
    private String fontColor;// 文字色
    @XmlElement()
    private String backColor;// 背景色
    @XmlElement()
    private LightPatternEnum lightPattern;// 点灯パターン
    @XmlElement()
    private String notationName;// 表記
    @XmlElement()
    private String melodyPath;// メロディパス
    @XmlElement()
    private Boolean melodyRepeat = false;// メロディ繰り返し

    @XmlElement()
    private Integer verInfo;// 排他用バーション

    /**
     * コンストラクタ
     */
    public DisplayedStatusInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param in 表示ステータス情報
     */
    public DisplayedStatusInfoEntity(DisplayedStatusInfoEntity in) {
        this.statusId = in.getStatusId();
        this.statusName = in.getStatusName();
        this.fontColor = in.getFontColor();
        this.backColor = in.getBackColor();
        this.lightPattern = in.getLightPattern();
        this.notationName = in.getNotationName();
        this.melodyPath = in.getMelodyPath();
        this.melodyRepeat = in.getMelodyRepeat();
        this.verInfo = in.verInfo;
    }

    /**
     * コンストラクタ
     *
     * @param statusId ステータス表示ID
     * @param statusName ステータス名
     */
    public DisplayedStatusInfoEntity(Long statusId, StatusPatternEnum statusName) {
        this.statusId = statusId;
        this.statusName = statusName;
    }

    /**
     * ステータス表示IDプロパティを取得する。
     *
     * @return ステータス表示ID
     */
    public LongProperty statusIdProperty() {
        if (Objects.isNull(this.statusIdProperty)) {
            this.statusIdProperty = new SimpleLongProperty(this.statusId);
        }
        return this.statusIdProperty;
    }

    /**
     * ステータス名プロパティを取得する。
     *
     * @return ステータス名
     */
    public ObjectProperty statusNameProperty() {
        if (Objects.isNull(this.statusNameProperty)) {
            this.statusNameProperty = new SimpleObjectProperty(this.statusName);
        }
        return this.statusNameProperty;
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
     * 表記プロパティを取得する。
     *
     * @return 表記
     */
    public StringProperty notationNameProperty() {
        if (Objects.isNull(this.notationNameProperty)) {
            this.notationNameProperty = new SimpleStringProperty(this.notationName);
        }
        return this.notationNameProperty;
    }

    /**
     * メロディパスプロパティを取得する。
     *
     * @return 
     */
    public StringProperty melodyPathProperty() {
        if (Objects.isNull(this.melodyPathProperty)) {
            this.melodyPathProperty = new SimpleStringProperty(this.melodyPath);
        }
        return this.melodyPathProperty;
    }

    /**
     * メロディ繰り返しプロパティを取得する。
     *
     * @return メロディ繰り返し
     */
    public BooleanProperty melodyRepeatProperty() {
        if (Objects.isNull(this.melodyRepeatProperty)) {
            this.melodyRepeatProperty = new SimpleBooleanProperty(this.melodyRepeat);
        }
        return this.melodyRepeatProperty;
    }

    /**
     * ステータス表示IDを取得する。
     *
     * @return ステータス表示ID
     */
    public Long getStatusId() {
        if (Objects.nonNull(this.statusIdProperty)) {
            return this.statusIdProperty.get();
        }
        return this.statusId;
    }

    /**
     * ステータス表示IDを設定する。
     *
     * @param statusId ステータス表示ID
     */
    public void setStatusId(Long statusId) {
        if (Objects.nonNull(this.statusIdProperty)) {
            this.statusIdProperty.set(statusId);
        } else {
            this.statusId = statusId;
        }
    }

    /**
     * ステータス名を取得する。
     *
     * @return ステータス名
     */
    public StatusPatternEnum getStatusName() {
        if (Objects.nonNull(this.statusNameProperty)) {
            return this.statusNameProperty.get();
        }
        return this.statusName;
    }

    /**
     * ステータス名を設定する。
     *
     * @param statusName ステータス名
     */
    public void setStatusName(StatusPatternEnum statusName) {
        if (Objects.nonNull(this.statusNameProperty)) {
            this.statusNameProperty.set(statusName);
        } else {
            this.statusName = statusName;
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
     * 表記を取得する。
     *
     * @return 表記
     */
    public String getNotationName() {
        if (Objects.nonNull(this.notationNameProperty)) {
            return this.notationNameProperty.get();
        }
        return this.notationName;
    }

    /**
     * 表記を設定する。
     *
     * @param notationName 表記
     */
    public void setNotationName(String notationName) {
        if (Objects.nonNull(this.notationNameProperty)) {
            this.notationNameProperty.set(notationName);
        } else {
            this.notationName = notationName;
        }
    }

    /**
     * メロディパスを取得する。
     *
     * @return メロディパス
     */
    public String getMelodyPath() {
        if (Objects.nonNull(this.melodyPathProperty)) {
            return this.melodyPathProperty.get();
        }
        return this.melodyPath;
    }

    /**
     * メロディパスを設定する。
     *
     * @param melodyPath メロディパス
     */
    public void setMelodyPath(String melodyPath) {
        if (Objects.nonNull(this.melodyPathProperty)) {
            this.melodyPathProperty.set(melodyPath);
        } else {
            this.melodyPath = melodyPath;
        }
    }

    /**
     * メロディ繰り返しを取得する。
     *
     * @return メロディ繰り返し
     */
    public Boolean getMelodyRepeat() {
        if (Objects.nonNull(this.melodyRepeatProperty)) {
            return this.melodyRepeatProperty.get();
        }
        return this.melodyRepeat;
    }

    /**
     * メロディ繰り返しを設定する。
     *
     * @param melodyRepeat メロディ繰り返し
     */
    public void setMelodyRepeat(Boolean melodyRepeat) {
        if (Objects.nonNull(this.melodyRepeatProperty)) {
            this.melodyRepeatProperty.set(melodyRepeat);
        } else {
            this.melodyRepeat = melodyRepeat;
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
        this.statusName = getStatusName();
        this.fontColor = getFontColor();
        this.backColor = getBackColor();
        this.lightPattern = getLightPattern();
        this.notationName = getNotationName();
        this.melodyPath = getMelodyPath();
        this.melodyRepeat = getMelodyRepeat();
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
        hash = 83 * hash + (int) (this.statusId ^ (this.statusId >>> 32));
        hash = 83 * hash + Objects.hashCode(this.statusName);
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
        final DisplayedStatusInfoEntity other = (DisplayedStatusInfoEntity) obj;
        if (!Objects.equals(this.getStatusId(), other.getStatusId())) {
            return false;
        }
        return Objects.equals(this.getStatusName(), other.getStatusName());
    }

    @Override
    public String toString() {
        return new StringBuilder("DisplayedStatusInfoEntity{")
                .append("statusId=").append(this.statusId)
                .append(", ")
                .append("statusName=").append(this.statusName)
                .append(", ")
                .append("fontColor=").append(this.fontColor)
                .append(", ")
                .append("backColor=").append(this.backColor)
                .append(", ")
                .append("lightPattern=").append(this.lightPattern)
                .append(", ")
                .append("notationName=").append(this.notationName)
                .append(", ")
                .append("melodyPath=").append(this.melodyPath)
                .append(", ")
                .append("melodyRepeat=").append(this.melodyRepeat)
                .append(", ")
                .append("verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
