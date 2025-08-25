package jp.adtekfuji.adFactory.entity.summaryreport;

public class VariationEntity {
    public String workName;
    public double median;
    public double q1;
    public double q3;
    public double average;
    public double distributed;

    public VariationEntity() {
    }

    public VariationEntity(String workName, double median, double q1, double q3, double average, double distributed) {
        this.workName = workName;
        this.median = median;
        this.q1 = q1;
        this.q3 = q3;
        this.average = average;
        this.distributed = distributed;
    }
}
