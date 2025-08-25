package jp.adtekfuji.adFactory.entity.summaryreport;

public class AverageProductWorkingHourEntity {

    public Double actualWorkProductTime;

    public Double planWorkProductTime;

    public Double threshold;

    public String warningBackColor;

    public AverageProductWorkingHourEntity() {
    }

    public AverageProductWorkingHourEntity(Double actualWorkProductTime, Double planWorkProductTime, Double threshold, String warningBackColor) {
        this.actualWorkProductTime = actualWorkProductTime;
        this.planWorkProductTime = planWorkProductTime;
        this.threshold = threshold;
        this.warningBackColor = warningBackColor;
    }
}
