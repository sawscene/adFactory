package jp.adtekfuji.adFactory.entity.search;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "indirectWorkSearchCondition")
public class IndirectWorkSearchCondition {

    @XmlElement()
    private Date fromDate = null;
    @XmlElement()
    private Date toDate = null;

    public IndirectWorkSearchCondition() {
    }

    public IndirectWorkSearchCondition fromDate(Date fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    public IndirectWorkSearchCondition toDate(Date toDate) {
        this.toDate = toDate;
        return this;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }
}
