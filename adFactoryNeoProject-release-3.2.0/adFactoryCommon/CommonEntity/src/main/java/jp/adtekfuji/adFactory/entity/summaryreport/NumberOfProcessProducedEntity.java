package jp.adtekfuji.adFactory.entity.summaryreport;


public class NumberOfProcessProducedEntity {

    public String workName;

    public Double actualProducedNumber;

    public Double planProducedNumber;

    public Double threshold;

    public String warningBackColor;

    public NumberOfProcessProducedEntity() {
    }

    public NumberOfProcessProducedEntity(String workName, Double producedNumber, Double planProducedNumber, Double threshold, String warningBackColor) {
        this.workName = workName;
        this.actualProducedNumber = producedNumber;
        this.planProducedNumber = planProducedNumber;
        this.threshold = threshold;
        this.warningBackColor = warningBackColor;
    }

}
