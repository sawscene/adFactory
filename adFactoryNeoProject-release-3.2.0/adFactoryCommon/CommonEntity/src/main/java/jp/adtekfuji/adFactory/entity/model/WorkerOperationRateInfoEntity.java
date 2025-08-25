/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 作業員一人当たりの稼働率
 * 
 * @author shizuka.hirano
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "workerOperationRateInfoEntity")
public class WorkerOperationRateInfoEntity {

    /**
     * 作業者名
     */
    @XmlElement()
    String workerName;

    /**
     * 稼働可能時間
     */
    @XmlElement()
    Long workingPossibleTime;

    /**
     * 実稼働時間
     */
    @XmlElement()
    Long productionTime;

    /**
     * 作業者名を取得する。
     * 
     * @return 作業者名
     */
    public String getWorkerName() {
        return workerName;
    }

    /**
     * 作業者名を設定する。
     * 
     * @param workerName 作業者名
     */
    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    /**
     * 稼働可能時間を取得する。
     * 
     * @return 稼働可能時間
     */
    public Long getWorkingPossibleTime() {
        return workingPossibleTime;
    }

    /**
     * 稼働可能時間を設定する。
     * 
     * @param workingPossibleTime 稼働可能時間
     */
    public void setWorkingPossibleTime(Long workingPossibleTime) {
        this.workingPossibleTime = workingPossibleTime;
    }

    /**
     * 実稼働時間を取得する。
     * 
     * @return 実稼働時間
     */
    public Long getProductionTime() {
        return productionTime;
    }

    /**
     * 実稼働時間を設定する。
     * 
     * @param productionTime 実稼働時間
     */
    public void setProductionTime(Long productionTime) {
        this.productionTime = productionTime;
    }

    /**
     * 文字列表現を取得する。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return "WorkerOperationRateInfoEntity{" +
                "workerName=" + workerName +
                ", workingPossibleTime=" + workingPossibleTime +
                ", productionTime" + productionTime +
                '}';
    }
}
