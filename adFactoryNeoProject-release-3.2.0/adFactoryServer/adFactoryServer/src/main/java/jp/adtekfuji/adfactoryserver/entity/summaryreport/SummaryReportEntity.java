
package jp.adtekfuji.adfactoryserver.entity.summaryreport;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jp.adtekfuji.adFactory.enumerate.AggregateUnitEnum;


import jakarta.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * サマリーレポート設定情報のメール内容設定情報
 *
 * @author nara
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SummaryReportInfoEntity")
public class SummaryReportEntity implements Serializable {

    @JsonProperty("title")
    @XmlElement()
    public String title = "";

    @XmlElement()
    public String period = "";

    // 集計単位
    @XmlElement()
    public String aggregateUnit = "";

    // 項目名
    @XmlElement()
    public String itemName = "";



    @JsonProperty("summaryReportInfoEntityElements")
    @XmlElementWrapper(name = "summaryReportInfoEntityElements")
    @XmlElement(name = "SummaryReportInfoEntityElement")
    public List<SummaryReportEntityElement> summaryReportEntityElements = new ArrayList<>();

    public SummaryReportEntity(){}

}
