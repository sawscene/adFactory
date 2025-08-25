package jp.adtekfuji.adFactory.entity.summaryreport;

public class OverallLineUtilizationEntity {

    public Double actualUtilizationTime;

    public Double planUtilizationTime;

    public Double threshold;

    public String warningBackColor;

    public OverallLineUtilizationEntity() {
    }

    public OverallLineUtilizationEntity(Double actualUtilizationTime, Double planUtilizationTime, Double threshold, String warningBackColor) {
        this.actualUtilizationTime = actualUtilizationTime;
        this.planUtilizationTime = planUtilizationTime;
        this.threshold = threshold;
        this.warningBackColor = warningBackColor;
    }
}
