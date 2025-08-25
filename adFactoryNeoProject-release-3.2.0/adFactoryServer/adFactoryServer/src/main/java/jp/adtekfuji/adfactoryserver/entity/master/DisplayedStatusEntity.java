/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.master;

import java.io.Serializable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.LightPatternEnum;
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;

/**
 * ステータス表示マスタ
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "mst_displayed_status")
@XmlRootElement(name = "displayedStatus")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "DisplayedStatusEntity.findByStatusName", query = "SELECT d FROM DisplayedStatusEntity d WHERE d.statusName = :statusName")
})
public class DisplayedStatusEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "status_id")
    private Long statusId;// ステータス表示ID

    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    @Column(name = "status_name")
    private StatusPatternEnum statusName;// ステータス名

    @Size(max = 128)
    @Column(name = "font_color")
    private String fontColor;// 文字色

    @Size(max = 128)
    @Column(name = "back_color")
    private String backColor;// 背景色

    @Enumerated(EnumType.STRING)
    @Column(name = "light_pattern")
    private LightPatternEnum lightPattern;// 点灯パターン

    @Size(max = 256)
    @Column(name = "notation_name")
    private String notationName = "";// 表記

    @Column(name = "melody_path", length = 32672)
    private String melodyPath = "";// メロディパス

    @Column(name = "melody_repeat")
    private Boolean melodyRepeat = false;// メロディー繰り返し

    //@NotNull
    @Column(name = "ver_info")
    @Version
    private Integer verInfo = 1;// 排他用バーション

    /**
     * コンストラクタ
     */
    public DisplayedStatusEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param statusName ステータス名
     * @param fontColor 文字色
     * @param backColor 背景色
     * @param lightPattern 点灯パターン
     */
    public DisplayedStatusEntity(StatusPatternEnum statusName, String fontColor, String backColor, LightPatternEnum lightPattern) {
        this.statusName = statusName;
        this.fontColor = fontColor;
        this.backColor = backColor;
        this.lightPattern = lightPattern;
    }

    /**
     * ステータス表示IDを取得する。
     *
     * @return ステータス表示ID
     */
    public Long getStatusId() {
        return statusId;
    }

    /**
     * ステータス表示IDを設定する。
     *
     * @param statusId ステータス表示ID
     */
    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    /**
     * ステータス名を取得する。
     *
     * @return ステータス名
     */
    public StatusPatternEnum getStatusName() {
        return statusName;
    }

    /**
     * ステータス名を設定する。
     *
     * @param statusName ステータス名
     */
    public void setStatusName(StatusPatternEnum statusName) {
        this.statusName = statusName;
    }

    /**
     * 文字色を取得する。
     *
     * @return 文字色
     */
    public String getFontColor() {
        return fontColor;
    }

    /**
     * 文字色を設定する。
     *
     * @param fontColor 文字色
     */
    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    /**
     * 背景色を取得する。
     *
     * @return 背景色
     */
    public String getBackColor() {
        return backColor;
    }

    /**
     * 背景色を設定する。
     *
     * @param backColor 背景色
     */
    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }

    /**
     * 点灯パターンを取得する。
     *
     * @return 点灯パターン
     */
    public LightPatternEnum getLightPattern() {
        return lightPattern;
    }

    /**
     * 点灯パターンを設定する。
     *
     * @param lightPattern 点灯パターン
     */
    public void setLightPattern(LightPatternEnum lightPattern) {
        this.lightPattern = lightPattern;
    }

    /**
     * 表記を取得する。
     *
     * @return 表記
     */
    public String getNotationName() {
        return notationName;
    }

    /**
     * 表記を設定する。
     *
     * @param notationName 表記
     */
    public void setNotationName(String notationName) {
        this.notationName = notationName;
    }

    /**
     * メロディーパスを取得する。
     *
     * @return メロディーパス
     */
    public String getMelodyPath() {
        return melodyPath;
    }

    /**
     * メロディーパスを設定する。
     *
     * @param melodyPath メロディーパス
     */
    public void setMelodyPath(String melodyPath) {
        this.melodyPath = melodyPath;
    }

    /**
     * メロディー繰り返しを取得する。
     *
     * @return メロディー繰り返し
     */
    public Boolean getMelodyRepeat() {
        return melodyRepeat;
    }

    /**
     * メロディー繰り返しを設定する。
     *
     * @param melodyRepeat メロディー繰り返し
     */
    public void setMelodyRepeat(Boolean melodyRepeat) {
        this.melodyRepeat = melodyRepeat;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (statusId != null ? statusId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DisplayedStatusEntity)) {
            return false;
        }
        DisplayedStatusEntity other = (DisplayedStatusEntity) object;
        if ((this.statusId == null && other.statusId != null) || (this.statusId != null && !this.statusId.equals(other.statusId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("DisplayedStatusEntity{")
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
