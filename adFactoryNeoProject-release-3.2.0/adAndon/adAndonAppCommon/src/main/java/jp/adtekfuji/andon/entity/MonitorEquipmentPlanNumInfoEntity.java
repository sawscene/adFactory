/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 進捗モニタ 設備生産数情報
 *
 * @author ke.yokoi
 */
@XmlRootElement(name = "monitorEquipmentPlanNumInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class MonitorEquipmentPlanNumInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer order;// 表示順
    private Integer planNum;// 計画数
    private Integer actualNum;// 実績数
    private Date suspendTime;// 中断時間

    /**
     * コンストラクタ
     */
    public MonitorEquipmentPlanNumInfoEntity() {
    }

    /**
     * 表示順を設定して、設備生産数情報を取得する。
     *
     * @param order 表示順
     * @return 設備生産数情報
     */
    public MonitorEquipmentPlanNumInfoEntity order(Integer order) {
        this.order = order;
        return this;
    }

    /**
     * 計画数を設定して、設備生産数情報を取得する。
     *
     * @param planNum 計画数
     * @return 設備生産数情報
     */
    public MonitorEquipmentPlanNumInfoEntity planNum(Integer planNum) {
        this.planNum = planNum;
        return this;
    }

    /**
     * 実績数を設定して、設備生産数情報を取得する。
     *
     * @param actualNum 実績数
     * @return 設備生産数情報
     */
    public MonitorEquipmentPlanNumInfoEntity actualNum(Integer actualNum) {
        this.actualNum = actualNum;
        return this;
    }

    /**
     * 中断時間を設定して、設備生産数情報を取得する。
     *
     * @param suspendTime 中断時間
     * @return 設備生産数情報
     */
    public MonitorEquipmentPlanNumInfoEntity suspendTime(Date suspendTime) {
        this.suspendTime = suspendTime;
        return this;
    }

    /**
     * 表示順を取得する。
     *
     * @return 表示順
     */
    public Integer getOrder() {
        return this.order;
    }

    /**
     * 表示順を設定する。
     *
     * @param order 表示順
     */
    public void setOrder(Integer order) {
        this.order = order;
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
     * 中断時間を取得する。
     *
     * @return 中断時間
     */
    public Date getSuspendTime() {
        return this.suspendTime;
    }

    /**
     * 中断時間を設定する。
     *
     * @param suspendTime 中断時間
     */
    public void setSuspendTime(Date suspendTime) {
        this.suspendTime = suspendTime;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode(this.order);
        hash = 19 * hash + Objects.hashCode(this.planNum);
        hash = 19 * hash + Objects.hashCode(this.actualNum);
        hash = 19 * hash + Objects.hashCode(this.suspendTime);
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
        final MonitorEquipmentPlanNumInfoEntity other = (MonitorEquipmentPlanNumInfoEntity) obj;
        if (!Objects.equals(this.order, other.order)) {
            return false;
        }
        if (!Objects.equals(this.planNum, other.planNum)) {
            return false;
        }
        if (!Objects.equals(this.actualNum, other.actualNum)) {
            return false;
        }
        if (!Objects.equals(this.suspendTime, other.suspendTime)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("MonitorEquipmentPlanNumInfoEntity{")
                .append("order=").append(this.order)
                .append(", ")
                .append("planNum=").append(this.planNum)
                .append(", ")
                .append("actualNum=").append(this.actualNum)
                .append(", ")
                .append("suspendTime=").append(this.suspendTime)
                .append("}")
                .toString();
    }
}
