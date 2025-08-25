package jp.adtekfuji.adFactory.entity.summaryreport;


public class InterProcessWaitingTimeEntity {
    public String fromWork;

    public String toWork;

    public Double waitTimeAverage;

    public Double waitTimeDiv;

    public Double waitTimeMedian;

    public Double waitTimeMax;

    public Double waitTimeMin;

    public Double waitTimeQ1;

    public Double waitTimeQ3;

    public InterProcessWaitingTimeEntity() {
    }

    public InterProcessWaitingTimeEntity(String fromWork, String toWork, Double waitTimeAverage, Double waitTimeDiv, Double waitTimeMedian, Double waitTimeMax, Double waitTimeMin, Double waitTimeQ1, Double waitTimeQ3) {
        this.fromWork = fromWork;
        this.toWork = toWork;
        this.waitTimeAverage = waitTimeAverage;
        this.waitTimeDiv = waitTimeDiv;
        this.waitTimeMedian = waitTimeMedian;
        this.waitTimeMax = waitTimeMax;
        this.waitTimeMin = waitTimeMin;
        this.waitTimeQ1 = waitTimeQ1;
        this.waitTimeQ3 = waitTimeQ3;
    }
}
