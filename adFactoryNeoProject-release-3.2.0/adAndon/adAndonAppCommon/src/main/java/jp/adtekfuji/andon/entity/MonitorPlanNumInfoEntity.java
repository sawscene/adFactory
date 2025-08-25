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
 * 進捗モニタ 生産数情報
 *
 * @author ke.yokoi
 */
@XmlRootElement(name = "monitorPlanNumInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class MonitorPlanNumInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer planNum;// 計画数
    private Integer actualNum;// 実績数
    private String unit;// 単位
    private Long lineTakt;// ラインタクト

    /**
     * コンストラクタ
     */
    public MonitorPlanNumInfoEntity() {
    }

    /**
     * 計画数を設定して、生産数情報を取得する。
     *
     * @param planNum 計画数
     * @return 生産数情報
     */
    public MonitorPlanNumInfoEntity planNum(Integer planNum) {
        this.planNum = planNum;
        return this;
    }

    /**
     * 実績数を設定して、生産数情報を取得する。
     *
     * @param actualNum 実績数
     * @return 生産数情報
     */
    public MonitorPlanNumInfoEntity actualNum(Integer actualNum) {
        this.actualNum = actualNum;
        return this;
    }

    /**
     * 単位を設定して、生産数情報を取得する。
     *
     * @param unit 単位
     * @return 生産数情報
     */
    public MonitorPlanNumInfoEntity unit(String unit) {
        this.unit = unit;
        return this;
    }

    /**
     * 計画数を取得する。
     *
     * @return 計画数
     */
    public Integer getPlanNum() {
        return this.planNum;
    }

    /**
     * 計画数を設定する。
     *
     * @param planNum 計画数
     */
    public void setPlanNum(Integer planNum) {
        this.planNum = planNum;
    }

    /**
     * 実績数を取得する。
     *
     * @return 実績数
     */
    public Integer getActualNum() {
        return this.actualNum;
    }

    /**
     * 実績数を設定する。
     *
     * @param actualNum 実績数
     */
    public void setActualNum(Integer actualNum) {
        this.actualNum = actualNum;
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

    /**
     * ラインタクトを取得する。
     *
     * @return ラインタクト
     */
    public Long getLineTakt() {
        return this.lineTakt;
    }

    /**
     * ラインタクトを設定する。
     *
     * @param lineTakt ラインタクト
     */
    public void setLineTakt(Long lineTakt) {
        this.lineTakt = lineTakt;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.planNum);
        hash = 29 * hash + Objects.hashCode(this.actualNum);
        hash = 29 * hash + Objects.hashCode(this.unit);
        hash = 29 * hash + Objects.hashCode(this.lineTakt);
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
        final MonitorPlanNumInfoEntity other = (MonitorPlanNumInfoEntity) obj;
        if (!Objects.equals(this.unit, other.unit)) {
            return false;
        }
        if (!Objects.equals(this.planNum, other.planNum)) {
            return false;
        }
        if (!Objects.equals(this.actualNum, other.actualNum)) {
            return false;
        }
        if (!Objects.equals(this.lineTakt, other.lineTakt)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("MonitorPlanNumInfoEntity{")
                .append("planNum=").append(this.planNum)
                .append(", ")
                .append("actualNum=").append(this.actualNum)
                .append(", ")
                .append("unit=").append(this.unit)
                .append(", ")
                .append("lineTakt=").append(this.lineTakt)
                .append("}")
                .toString();
    }
}
