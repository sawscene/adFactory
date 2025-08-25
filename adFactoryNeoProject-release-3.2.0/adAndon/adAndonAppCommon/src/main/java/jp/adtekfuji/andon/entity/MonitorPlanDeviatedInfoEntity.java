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
 * 進捗モニタ 生産進捗情報
 *
 * @author ke.yokoi
 */
@XmlRootElement(name = "monitorPlanDeviatedInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class MonitorPlanDeviatedInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer planDeviatedNum;// 進捗数
    private Long planDeviatedTime;// 進捗時間
    private Long workResult;// 作業結果
    private String fontColor;// 文字色
    private String backColor;// 背景色
    private String timeFormat;// 時間フォーマット
    private String unit;// 単位

    /**
     * コンストラクタ
     */
    public MonitorPlanDeviatedInfoEntity() {
    }

    /**
     * 進捗数を設定して、生産進捗情報を取得する。
     *
     * @param planDeviatedNum 進捗数
     * @return 生産進捗情報
     */
    public MonitorPlanDeviatedInfoEntity planDeviatedNum(Integer planDeviatedNum) {
        this.planDeviatedNum = planDeviatedNum;
        return this;
    }

    /**
     * 進捗時間を設定して、生産進捗情報を取得する。
     *
     * @param planDeviatedTime 進捗時間
     * @return 生産進捗情報
     */
    public MonitorPlanDeviatedInfoEntity planDeviatedTime(Long planDeviatedTime) {
        this.planDeviatedTime = planDeviatedTime;
        return this;
    }

    /**
     * 文字色を設定して、生産進捗情報を取得する。
     *
     * @param fontColor 文字色
     * @return 生産進捗情報
     */
    public MonitorPlanDeviatedInfoEntity fontColor(String fontColor) {
        this.fontColor = fontColor;
        return this;
    }

    /**
     * 背景色を設定して、生産進捗情報を取得する。
     *
     * @param backColor 背景色
     * @return 生産進捗情報
     */
    public MonitorPlanDeviatedInfoEntity backColor(String backColor) {
        this.backColor = backColor;
        return this;
    }

    /**
     * 時間フォーマットを設定して、生産進捗情報を取得する。
     *
     * @param timeFormat 時間フォーマット
     * @return 生産進捗情報
     */
    public MonitorPlanDeviatedInfoEntity timeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
        return this;
    }

    /**
     * 単位を設定して、生産進捗情報を取得する。
     *
     * @param unit 単位
     * @return 生産進捗情報
     */
    public MonitorPlanDeviatedInfoEntity unit(String unit) {
        this.unit = unit;
        return this;
    }

    /**
     * 進捗数を取得する。
     *
     * @return 進捗数
     */
    public Integer getPlanDeviatedNum() {
        return this.planDeviatedNum;
    }

    /**
     * 進捗数を設定する。
     *
     * @param planDeviatedNum 進捗数
     */
    public void setPlanDeviatedNum(Integer planDeviatedNum) {
        this.planDeviatedNum = planDeviatedNum;
    }

    /**
     * 進捗時間を取得する。
     *
     * @return 進捗時間
     */
    public Long getPlanDeviatedTime() {
        return this.planDeviatedTime;
    }

    /**
     * 進捗時間を設定する。
     *
     * @param planDeviatedTime 進捗時間
     */
    public void setPlanDeviatedTime(Long planDeviatedTime) {
        this.planDeviatedTime = planDeviatedTime;
    }

    /**
     * 作業結果を取得する。
     *
     * @return 作業結果
     */
    public Long getWorkResult() {
        return this.workResult;
    }

    /**
     * 作業結果を設定する。
     *
     * @param workResult 作業結果
     */
    public void setWorkResult(Long workResult) {
        this.workResult = workResult;
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
     * 時間フォーマットを取得する。
     *
     * @return 時間フォーマット
     */
    public String getTimeFormat() {
        return this.timeFormat;
    }

    /**
     * 時間フォーマットを設定する。
     *
     * @param timeFormat 時間フォーマット
     */
    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    /**
     * 単位を取得する。
     *
     * @return 単位
     */
    public String getUnit() {
        return this.unit;
    }

    /**
     * 単位を設定する。
     *
     * @param unit 単位
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.planDeviatedNum);
        hash = 43 * hash + Objects.hashCode(this.planDeviatedTime);
        hash = 43 * hash + Objects.hashCode(this.workResult);
        hash = 43 * hash + Objects.hashCode(this.fontColor);
        hash = 43 * hash + Objects.hashCode(this.backColor);
        hash = 43 * hash + Objects.hashCode(this.timeFormat);
        hash = 43 * hash + Objects.hashCode(this.unit);
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
        final MonitorPlanDeviatedInfoEntity other = (MonitorPlanDeviatedInfoEntity) obj;
        if (!Objects.equals(this.fontColor, other.fontColor)) {
            return false;
        }
        if (!Objects.equals(this.backColor, other.backColor)) {
            return false;
        }
        if (!Objects.equals(this.timeFormat, other.timeFormat)) {
            return false;
        }
        if (!Objects.equals(this.unit, other.unit)) {
            return false;
        }
        if (!Objects.equals(this.planDeviatedNum, other.planDeviatedNum)) {
            return false;
        }
        if (!Objects.equals(this.planDeviatedTime, other.planDeviatedTime)) {
            return false;
        }
        if (!Objects.equals(this.workResult, other.workResult)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("MonitorPlanDeviatedInfoEntity{")
                .append("planDeviatedNum=").append(this.planDeviatedNum)
                .append(", ")
                .append("planDeviatedTime=").append(this.planDeviatedTime)
                .append(", ")
                .append("workResult=").append(this.workResult)
                .append(", ")
                .append("fontColor=").append(this.fontColor)
                .append(", ")
                .append("backColor=").append(this.backColor)
                .append(", ")
                .append("timeFormat=").append(this.timeFormat)
                .append(", ")
                .append("unit=").append(this.unit)
                .append("}")
                .toString();
    }
}
