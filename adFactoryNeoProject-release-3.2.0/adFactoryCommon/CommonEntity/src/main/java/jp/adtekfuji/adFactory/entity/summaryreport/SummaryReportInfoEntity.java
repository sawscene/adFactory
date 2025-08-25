
package jp.adtekfuji.adFactory.entity.summaryreport;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * サマリーレポート設定情報のメール内容設定情報
 *
 * @author nara
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SummaryReportInfoEntity")
public class SummaryReportInfoEntity implements Serializable {

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
    public List<SummaryReportInfoEntityElement> summaryReportInfoEntityElements = new ArrayList<>();

    public SummaryReportInfoEntity() {
    }

    static public boolean isValid(SummaryReportInfoEntity summaryReportInfoEntity)
    {
        return Objects.nonNull(summaryReportInfoEntity)
                && Objects.nonNull(summaryReportInfoEntity.summaryReportInfoEntityElements)
                && !summaryReportInfoEntity.summaryReportInfoEntityElements.isEmpty();
    }
}
