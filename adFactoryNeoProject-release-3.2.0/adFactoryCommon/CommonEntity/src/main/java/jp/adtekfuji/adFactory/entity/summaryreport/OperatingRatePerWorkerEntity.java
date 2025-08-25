package jp.adtekfuji.adFactory.entity.summaryreport;


public class OperatingRatePerWorkerEntity {

    public String organizationIdentify;

    public String organizationName;

    public Double actualOperatingTime;

    public Double planOperatingTime;

    public Double threshold;

    public String warningBackColor;

    public OperatingRatePerWorkerEntity() {
    }

    public OperatingRatePerWorkerEntity(String organizationIdentify, String organizationName, Double actualOperatingTime, Double planOperatingTime, Double threshold, String warningBackColor) {
        this.organizationIdentify = organizationIdentify;
        this.organizationName = organizationName;
        this.actualOperatingTime = actualOperatingTime;
        this.planOperatingTime = planOperatingTime;
        this.threshold = threshold;
        this.warningBackColor = warningBackColor;
    }
}
