package jp.adtekfuji.adFactory.entity.summaryreport;

import java.util.List;

public class LineBalanceEntity {
    public String workflowName;

    public List<String> criticalPath;

    public Double actualWorkTime;

    public Double planWorkTime;

    public Double lineBalanceRate;

    public LineBalanceEntity() {
    }

    public LineBalanceEntity(String workflowName, List<String> criticalPath, Double actualWorkTime, Double planWorkTime, Double lineBalanceRate) {
        this.workflowName = workflowName;
        this.criticalPath = criticalPath;
        this.actualWorkTime = actualWorkTime;
        this.planWorkTime = planWorkTime;
        this.lineBalanceRate = lineBalanceRate;
    }
}
