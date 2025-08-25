
package jp.adtekfuji.adFactory.entity.summaryreport;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jp.adtekfuji.adFactory.enumerate.CategoryEnum;

import jakarta.xml.bind.annotation.*;
import java.io.Serializable;


/**
 * サマリーレポート設定情報のメール内容設定情報
 *
 * @author nara
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SummaryReportInfoEntityElement")
public class SummaryReportInfoEntityElement implements Serializable {

    @JsonProperty("elementType")
    @JsonSerialize(using = CategoryEnum.Serializer.class)
    @JsonDeserialize(using = CategoryEnum.Deserializer.class)
    @XmlElement()
    public CategoryEnum elementType = CategoryEnum.NUMBER_OF_PRODUCTS_PRODUCED;

    @JsonProperty("result")
    @XmlElement()
    public String result;

    public SummaryReportInfoEntityElement(){}

    public SummaryReportInfoEntityElement(CategoryEnum elementType, String result) {
        this.elementType = elementType;
        this.result = result;
    }
}
