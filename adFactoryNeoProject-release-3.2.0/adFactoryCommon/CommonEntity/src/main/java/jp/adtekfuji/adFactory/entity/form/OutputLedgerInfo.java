package jp.adtekfuji.adFactory.entity.form;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.simpleframework.xml.Element;

import jakarta.xml.bind.annotation.XmlElement;

public class OutputLedgerInfo {

    @XmlElement()
    @JsonProperty("ledger_path")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String ledgerPath;

    @Element()
    @JsonProperty("result")
    String result;

    @Element()
    @JsonProperty("message")
    String message;

    public OutputLedgerInfo() {
    }

    public OutputLedgerInfo(String ledgerPath, String result, String message) {
        this.ledgerPath = ledgerPath;
        this.result = result;
        this.message = message;
    }


}
