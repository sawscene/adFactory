/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.entity;

import java.io.Serializable;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 進捗モニタ ライン全体ステータス情報
 *
 * @author ke.yokoi
 */
@XmlRootElement(name = "monitorLineStatusInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class MonitorLineStatusInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String status;// ステータス
    private String fontColor;// 文字色
    private String backColor;// 背景色
    private String melodyFilePath;// メロディファイルパス
    private Boolean melodyReplay;// メロディ繰り返しフラグ

    /**
     * コンストラクタ
     */
    public MonitorLineStatusInfoEntity() {
    }

    /**
     * ステータスを設定して、ラインステータス情報を取得する。
     *
     * @param status ステータス
     * @return ライン全体ステータス情報
     */
    public MonitorLineStatusInfoEntity status(String status) {
        this.status = status;
        return this;
    }

    /**
     * 文字色を設定して、ラインステータス情報を取得する。
     *
     * @param fontColor 文字色
     * @return ライン全体ステータス情報
     */
    public MonitorLineStatusInfoEntity fontColor(String fontColor) {
        this.fontColor = fontColor;
        return this;
    }

    /**
     * 背景色を設定して、ラインステータス情報を取得する。
     *
     * @param backColor 背景色
     * @return ライン全体ステータス情報
     */
    public MonitorLineStatusInfoEntity backColor(String backColor) {
        this.backColor = backColor;
        return this;
    }

    /**
     * メロディファイルパスを設定して、ラインステータス情報を取得する。
     *
     * @param melodyFilePath
     * @return ライン全体ステータス情報
     */
    public MonitorLineStatusInfoEntity melodyFilePath(String melodyFilePath) {
        this.melodyFilePath = melodyFilePath;
        return this;
    }

    /**
     * メロディ繰り返しフラグを設定して、ラインステータス情報を取得する。
     *
     * @param melodyReplay メロディ繰り返しフラグ
     * @return ライン全体ステータス情報
     */
    public MonitorLineStatusInfoEntity melodyReplay(Boolean melodyReplay) {
        this.melodyReplay = melodyReplay;
        return this;
    }

    /**
     * ステータスを取得する。
     *
     * @return ステータス
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * ステータスを設定する。
     *
     * @param status ステータス
     */
    public void setStatus(String status) {
        this.status = status;
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
    public String getBackColor() {
        return this.backColor;
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
     * メロディファイルパスを取得する。
     *
     * @return メロディファイルパス
     */
    public String getMelodyFilePath() {
        return this.melodyFilePath;
    }

    /**
     * メロディファイルパスを設定する。
     *
     * @param melodyFilePath メロディファイルパス
     */
    public void setMelodyFilePath(String melodyFilePath) {
        this.melodyFilePath = melodyFilePath;
    }

    /**
     * メロディ繰り返しフラグを取得する。
     *
     * @return メロディ繰り返しフラグ
     */
    public Boolean getMelodyReplay() {
        return this.melodyReplay;
    }

    /**
     * メロディ繰り返しフラグを設定する。
     *
     * @param melodyReplay メロディ繰り返しフラグ
     */
    public void setMelodyReplay(Boolean melodyReplay) {
        this.melodyReplay = melodyReplay;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.status);
        hash = 79 * hash + Objects.hashCode(this.fontColor);
        hash = 79 * hash + Objects.hashCode(this.backColor);
        hash = 79 * hash + Objects.hashCode(this.melodyFilePath);
        hash = 79 * hash + Objects.hashCode(this.melodyReplay);
        return hash;
    }

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
        final MonitorLineStatusInfoEntity other = (MonitorLineStatusInfoEntity) obj;
        if (!Objects.equals(this.status, other.status)) {
            return false;
        }
        if (!Objects.equals(this.fontColor, other.fontColor)) {
            return false;
        }
        if (!Objects.equals(this.backColor, other.backColor)) {
            return false;
        }
        if (!Objects.equals(this.melodyFilePath, other.melodyFilePath)) {
            return false;
        }
        if (!Objects.equals(this.melodyReplay, other.melodyReplay)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("MonitorLineStatusInfoEntity{")
                .append("status=").append(this.status)
                .append(", ")
                .append("fontColor=").append(this.fontColor)
                .append(", ")
                .append("backColor=").append(this.backColor)
                .append(", ")
                .append("melodyFilePath=").append(this.melodyFilePath)
                .append(", ")
                .append("melodyReplay=").append(this.melodyReplay)
                .append("}")
                .toString();
    }
}
