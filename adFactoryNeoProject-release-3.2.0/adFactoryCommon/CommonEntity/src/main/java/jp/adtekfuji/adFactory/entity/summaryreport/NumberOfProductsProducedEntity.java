
package jp.adtekfuji.adFactory.entity.summaryreport;


public class NumberOfProductsProducedEntity {

    public Double actualProductNumber;

    public Double planProductNumber;

    public Double threshold;

    public String warningBackColor;

    public NumberOfProductsProducedEntity() {
    }

    public NumberOfProductsProducedEntity(Double actualProductNumber, Double planProductNumber, Double threshold, String warningBackColor)
    {
        this.actualProductNumber = actualProductNumber;
        this.planProductNumber = planProductNumber;
        this.threshold = threshold;
        this.warningBackColor = warningBackColor;
    }
}


