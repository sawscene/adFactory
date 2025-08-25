package jp.adtekfuji.adFactory.entity.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "modelOperationRateInfoEntity")
public class ModelOperationRateInfoEntity {

    /**
     * モデル名
     */
    @XmlElement()
    String modelName;

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

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Long getTactTime() {
        return tactTime;
    }

    public void setTactTime(Long tactTime) {
        this.tactTime = tactTime;
    }

    public Long getAverageWorkingTime() {
        return averageWorkingTime;
    }

    public void setAverageWorkingTime(Long averageWorkingTime) {
        this.averageWorkingTime = averageWorkingTime;
    }

    @Override
    public String toString() {
        return "ModelOperationRateInfoEntity{" +
                "modelName='" + modelName + '\'' +
                ", tactTime=" + tactTime +
                ", averageWorkingTime=" + averageWorkingTime +
                '}';
    }
}
