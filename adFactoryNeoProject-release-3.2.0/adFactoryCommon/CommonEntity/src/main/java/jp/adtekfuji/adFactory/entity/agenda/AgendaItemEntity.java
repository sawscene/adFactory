/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.agenda;

import java.io.Serializable;
import java.util.Date;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 予実情報
 *
 * @author ke.yokoi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "agendaItem")
public class AgendaItemEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private String title1;// タイトル1

    @XmlElement()
    private String title2;// タイトル2

    @XmlElement()
    private String title3;// タイトル3

    @XmlElement()
    private Integer taktTime;// タクトタイム

    @XmlElement()
    private Date startTime;// 開始日時

    @XmlElement()
    private Date endTIme;// 完了日時

    @XmlElement()
    private String fontColor;// 文字色

    @XmlElement()
    private String backgraundColor;// 背景色

    @XmlElement()
    private String frameColor;// 枠の色

    @XmlElement()
    private Boolean isBlink;// 点滅フラグ

    /**
     * コンストラクタ
     */
    public AgendaItemEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param title1 タイトル1
     * @param title2 タイトル2
     * @param title3 タイトル3
     * @param taktTime タクトタイム
     * @param startTime 開始日時
     * @param endTIme 完了日時
     * @param fontColor 文字色
     * @param backgraundColor 背景色
     * @param frameColor 枠の色
     * @param isBlink 点滅フラグ
     */
    public AgendaItemEntity(String title1, String title2, String title3, Integer taktTime, Date startTime, Date endTIme, String fontColor, String backgraundColor, String frameColor, Boolean isBlink) {
        this.title1 = title1;
        this.title2 = title2;
        this.title3 = title3;
        this.taktTime = taktTime;
        this.startTime = startTime;
        this.endTIme = endTIme;
        this.fontColor = fontColor;
        this.backgraundColor = backgraundColor;
        this.frameColor = frameColor;
        this.isBlink = isBlink;
    }

    /**
     * タイトル1を取得する。
     *
     * @return タイトル1
     */
    public String getTitle1() {
        return this.title1;
    }

    /**
     * タイトル1を設定する。
     *
     * @param title1 タイトル1
     */
    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    /**
     * タイトル2を取得する。
     *
     * @return タイトル2
     */
    public String getTitle2() {
        return this.title2;
    }

    /**
     * タイトル2を設定する。
     *
     * @param title2 タイトル2
     */
    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    /**
     * タイトル3を取得する。
     *
     * @return タイトル3
     */
    public String getTitle3() {
        return this.title3;
    }

    /**
     * タイトル3を設定する。
     *
     * @param title3 タイトル3
     */
    public void setTitle3(String title3) {
        this.title3 = title3;
    }

    /**
     * タクトタイムを取得する。
     *
     * @return タクトタイム
     */
    public Integer getTaktTime() {
        return this.taktTime;
    }

    /**
     * タクトタイムを設定する。
     *
     * @param taktTime タクトタイム
     */
    public void setTaktTime(Integer taktTime) {
        this.taktTime = taktTime;
    }

    /**
     * 開始日時を取得する。
     *
     * @return 開始日時
     */
    public Date getStartTime() {
        return this.startTime;
    }

    /**
     * 開始日時を設定する。
     *
     * @param startTime 開始日時
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * 完了日時を取得する。
     *
     * @return 完了日時
     */
    public Date getEndTIme() {
        return this.endTIme;
    }

    /**
     * 完了日時を設定する。
     *
     * @param endTIme 完了日時
     */
    public void setEndTIme(Date endTIme) {
        this.endTIme = endTIme;
    }

    /**
     * 文字色を取得する。
     *
     * @return 文字色
     */
    public String getFontColor() {
        return this.fontColor;
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
    public String getBackgraundColor() {
        return this.backgraundColor;
    }

    /**
     * 背景色を設定する。
     *
     * @param backgraundColor 背景色
     */
    public void setBackgraundColor(String backgraundColor) {
        this.backgraundColor = backgraundColor;
    }

    /**
     * 枠の色を取得する。
     *
     * @return 枠の色
     */
    public String getFrameColor() {
        return this.frameColor;
    }

    /**
     * 枠の色を設定する。
     *
     * @param frameColor 枠の色
     */
    public void setFrameColor(String frameColor) {
        this.frameColor = frameColor;
    }

    /**
     * 点滅フラグを取得する。
     *
     * @return 点滅フラグ
     */
    public Boolean getIsBlink() {
        return this.isBlink;
    }

    /**
     * 点滅フラグを設定する。
     *
     * @param isBlink 点滅フラグ
     */
    public void setIsBlink(Boolean isBlink) {
        this.isBlink = isBlink;
    }

    @Override
    public int hashCode() {
        int hash = 7;
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
        final AgendaItemEntity other = (AgendaItemEntity) obj;
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("AgendaItemEntity{")
                .append("title1=").append(this.title1)
                .append(", ")
                .append("title2=").append(this.title2)
                .append(", ")
                .append("title3=").append(this.title3)
                .append(", ")
                .append("taktTime=").append(this.taktTime)
                .append(", ")
                .append("startTime=").append(this.startTime)
                .append(", ")
                .append("endTIme=").append(this.endTIme)
                .append(", ")
                .append("fontColor=").append(this.fontColor)
                .append(", ")
                .append("backgraundColor=").append(this.backgraundColor)
                .append(", ")
                .append("frameColor=").append(this.frameColor)
                .append(", ")
                .append("isBlink=").append(this.isBlink)
                .append("}")
                .toString();
    }
}
