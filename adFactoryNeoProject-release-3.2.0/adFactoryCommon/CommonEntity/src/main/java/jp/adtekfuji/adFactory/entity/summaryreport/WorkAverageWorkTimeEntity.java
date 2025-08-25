package jp.adtekfuji.adFactory.entity.summaryreport;

public class WorkAverageWorkTimeEntity {
    public String workName;

    public Double actualWorkTime;

    public Double planWorkTime;

    public Double threshold;

    public String warningBackColor;

    public WorkAverageWorkTimeEntity() {
    }

    public WorkAverageWorkTimeEntity(String workName, Double actualWorkTime, Double planWorkTime, Double threshold, String warningBackColor) {
        this.workName = workName;
        this.actualWorkTime = actualWorkTime;
        this.planWorkTime = planWorkTime;
        this.threshold = threshold;
        this.warningBackColor = warningBackColor;
    }

}
