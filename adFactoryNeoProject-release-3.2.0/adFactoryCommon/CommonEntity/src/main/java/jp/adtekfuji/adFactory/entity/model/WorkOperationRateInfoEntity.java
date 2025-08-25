package jp.adtekfuji.adFactory.entity.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "workerOperationRateInfoEntity")
public class WorkOperationRateInfoEntity {

    /**
     * 工程名
     */
    @XmlElement()
    String workName;

    /**
     * タクトタイム
     */
    @XmlElement()
    Long tactTime;

    /**
     * 平均作業時間
     */
    @XmlElement
    Long averageWorkingTime;

    /**
     * 工程名取得
     * @return 工程名
     */
    public String getWorkName() {
        return workName;
    }

    /**
     * 工程名設定
     * @param workName 工程名
     */
    public void setWorkName(String workName) {
        this.workName = workName;
    }

    /**
     * タクトタイム取得
     * @return タクトタイム
     */
    public Long getTactTime() {
        return tactTime;
    }

    /**
     * タクトタイム設定
     * @param tactTime タクトタイム
     */
    public void setTactTime(Long tactTime) {
        this.tactTime = tactTime;
    }

    /**
     * 平均作業時間取得
     * @return 平均作業時間
     */
    public Long getAverageWorkingTime() {
        return averageWorkingTime;
    }

    /**
     * 平均作業時間設定
     * @param averageWorkingTime 平均作業時間
     */
    public void setAverageWorkingTime(Long averageWorkingTime) {
        this.averageWorkingTime = averageWorkingTime;
    }

    @Override
    public String toString() {
        return "WorkOperationRateInfoEntity{" +
                "workName='" + workName + '\'' +
                ", tactTime='" + tactTime + '\'' +
                ", averageWorkingTime=" + averageWorkingTime +
                '}';
    }
}
