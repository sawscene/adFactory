/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.model;

import jakarta.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 統計情報
 *
 * @author kentarou.suzuki
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "summaryReport")
public class SummaryReport implements Serializable {

    /**
     * コンストラクタ
     */
    public SummaryReport() {
    }

    /**
     * コンストラクタ
     * 
     * @param date 実施日
     * @param modelName モデル名
     */
    public SummaryReport(Date date, String modelName) {
        this.date = date;
        this.modelName = modelName;
    }

    /**
     * 実施日
     */
    @XmlElement()
    Date date;

    /**
     * モデル名
     */
    @XmlElement()
    String modelName;

    /**
     * 工程順名
     */
    @XmlElement()
    String workflowName;

    /**
     * 実績値
     */
    @XmlElement()
    Integer achievementValue;

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
     * 作業員一人当たりの稼働率一覧
     */
    @XmlElementWrapper(name = "workerOperationRates")
    @XmlElement(name = "workerOperationRate")
    List<WorkerOperationRateInfoEntity> workerOperationRates;


    /**
     * モデルの作業情報
     */
    @XmlElement(name = "modelOperationRate")
    ModelOperationRateInfoEntity modelOperationRate;

    /**
     * 工程の作業情報一覧
     */
    @XmlElementWrapper(name = "workOperationRates")
    @XmlElement(name = "workOperationRate")
    List<WorkOperationRateInfoEntity> workOperationRates;

    /**
     * 平均作業時間
     */
    @XmlElement()
    Long averageWorkingTime;

    /**
     * 中断理由一覧
     */
    @XmlElementWrapper(name = "interruptReasons")
    @XmlElement(name = "interruptReason")
    List<ReasonTimeInfoEntity> interruptReasons;

    /**
     * 遅延理由一覧
     */
    @XmlElementWrapper(name = "delayReasons")
    @XmlElement(name = "delayReason")
    List<ReasonTimeInfoEntity> delayReasons;

    /**
     * 呼出理由一覧
     */
    @XmlElementWrapper(name = "callReasons")
    @XmlElement(name = "callReason")
    List<ReasonNumInfoEntity> callReasons;

    /**
     * 実施日を取得する。
     *
     * @return 実施日
     */
    public Date getDate() {
        return date;
    }

    /**
     * 実施日を設定する。
     *
     * @param date 実施日
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * モデル名を取得する。
     *
     * @return モデル名
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * モデル名を設定する。
     *
     * @param modelName モデル名
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * 工程順名を取得する。
     *
     * @return 工程順名
     */
    public String getWorkflowName() {
        return workflowName;
    }

    /**
     * 工程順名を設定する。
     *
     * @param workflowName 工程順名
     */
    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    /**
     * 実績値を取得する。
     *
     * @return 実績値
     */
    public Integer getAchievementValue() {
        return achievementValue;
    }

    /**
     * 実績値を設定する。
     *
     * @param achievementValue 実績値
     */
    public void setAchievementValue(Integer achievementValue) {
        this.achievementValue = achievementValue;
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
     * 作業員一人当たりの稼働率一覧を取得する。
     *
     * @return 作業員一人当たりの稼働率一覧
     */
    public List<WorkerOperationRateInfoEntity> getWorkerOperationRates() {
        return workerOperationRates;
    }

    /**
     * 作業員一人当たりの稼働率一覧を設定する。
     *
     * @param workerOperationRates 作業員一人当たりの稼働率一覧
     */
    public void setWorkerOperationRates(List<WorkerOperationRateInfoEntity> workerOperationRates) {
        this.workerOperationRates = workerOperationRates;
    }

    public ModelOperationRateInfoEntity getModelOperationRate() {
        return modelOperationRate;
    }

    public void setModelOperationRate(ModelOperationRateInfoEntity modelOperationRate) {
        this.modelOperationRate = modelOperationRate;
    }

    /**
     * 工程の作業情報一覧を取得する。
     *
     * @return 工程の作業情報一覧
     */
    public List<WorkOperationRateInfoEntity> getWorkOperationRates() {
        return workOperationRates;
    }

    /**
     * 工程の作業情報一覧を設定する。
     *
     * @param workOperationRates 工程の作業情報一覧
     */
    public void setWorkOperationRates(List<WorkOperationRateInfoEntity> workOperationRates) {
        this.workOperationRates = workOperationRates;
    }

    /**
     * 平均作業時間を取得する。
     *
     * @return 平均作業時間
     */
    public Long getAverageWorkingTime() {
        return averageWorkingTime;
    }

    /**
     * 平均作業時間を設定する。
     *
     * @param averageWorkingTime 平均作業時間
     */
    public void setAverageWorkingTime(Long averageWorkingTime) {
        this.averageWorkingTime = averageWorkingTime;
    }

    /**
     * 中断理由一覧を取得する。
     *
     * @return 中断理由一覧
     */
    public List<ReasonTimeInfoEntity> getInterruptReasons() {
        return interruptReasons;
    }

    /**
     * 中断理由一覧を設定する。
     *
     * @param interruptReasons 中断理由一覧
     */
    public void setInterruptReasons(List<ReasonTimeInfoEntity> interruptReasons) {
        this.interruptReasons = interruptReasons;
    }

    /**
     * 遅延理由一覧を取得する。
     *
     * @return 遅延理由一覧
     */
    public List<ReasonTimeInfoEntity> getDelayReasons() {
        return delayReasons;
    }

    /**
     * 遅延理由一覧を設定する。
     *
     * @param delayReasons 遅延理由一覧
     */
    public void setDelayReasons(List<ReasonTimeInfoEntity> delayReasons) {
        this.delayReasons = delayReasons;
    }

    /**
     * 呼出理由一覧を取得する。
     *
     * @return 呼出理由一覧
     */
    public List<ReasonNumInfoEntity> getCallReasons() {
        return callReasons;
    }

    /**
     * 呼出理由一覧を設定する。
     *
     * @param callReasons 呼出理由一覧
     */
    public void setCallReasons(List<ReasonNumInfoEntity> callReasons) {
        this.callReasons = callReasons;
    }

    /**
     * 文字列表現を取得する。
     *
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return "SummaryReport{" +
                "date=" + date +
                ", modelName=" + modelName +
                ", workflowName=" + workflowName +
                ", achievementValue=" + achievementValue +
                ", workingPossibleTime=" + workingPossibleTime +
                ", productionTime=" + productionTime +
                ", workerOperationRates=" + workerOperationRates +
                ", workOperationRates=" + workOperationRates +
                ", averageWorkingTime=" + averageWorkingTime +
                ", interruptReasons=" + interruptReasons +
                ", delayReasons=" + delayReasons +
                ", callReasons=" + callReasons +
                '}';
    }
}
