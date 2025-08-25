package jp.adtekfuji.adFactory.entity.ledger;

import jp.adtekfuji.adFactory.enumerate.DisplayLimitTypeEnum;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ledgerFileSearch")
public class LedgerFileSearchEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private List<Long> ledgerIds;// 帳票ID

    @XmlElement()
    Date fromDatetime; // 開始日

    @XmlElement()
    Date toDatetime; // 終了日

    @XmlElement()
    List<NameValueEntity> keywords;

    public LedgerFileSearchEntity() {
    }

    public LedgerFileSearchEntity(Long ledgerId) {
        this.ledgerIds = Collections.singletonList(ledgerId);
    }

    public LedgerFileSearchEntity(List<Long> ledgerIds) {
        this.ledgerIds = ledgerIds;
    }

    @XmlElement()
    private Integer limit; // 検索数


    public List<Long> getLedgerIds() {
        return ledgerIds;
    }

    public void setLedgerIds(List<Long> ledgerIds) {
        this.ledgerIds = ledgerIds;
    }

    public Date getFromDatetime() {
        return fromDatetime;
    }

    public void setFromDatetime(Date fromDatetime) {
        this.fromDatetime = fromDatetime;
    }

    public Date getToDatetime() {
        return toDatetime;
    }

    public void setToDatetime(Date toDatetime) {
        this.toDatetime = toDatetime;
    }

    public List<NameValueEntity> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<NameValueEntity> keywords) {
        this.keywords = keywords;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }


}
