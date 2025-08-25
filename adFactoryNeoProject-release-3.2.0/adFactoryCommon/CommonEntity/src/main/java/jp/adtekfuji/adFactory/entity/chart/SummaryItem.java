/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.chart;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import jp.adtekfuji.adFactory.xmladapter.NumberAdapter;

/**
 * 統計項目
 *
 * @author s-heya
 */
@XmlRootElement(name = "summaryItem")
@XmlAccessorType(XmlAccessType.FIELD)
public class SummaryItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private SummaryTypeEnum type;
    private String name;
    
    @XmlJavaTypeAdapter(NumberAdapter.class)
    private Number value;

    public SummaryItem() {
    }

    public SummaryItem(SummaryTypeEnum type, Long value) {
        this.type = type;
        this.value = value;
    }

    public SummaryItem(String type, String name, Long value) {
        this.type = SummaryTypeEnum.valueOf(type);
        this.name = name;
        this.value = value;
    }

    public SummaryTypeEnum getType() {
        return type;
    }

    public void setType(SummaryTypeEnum type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SummaryItem{" + "type=" + type + ", name=" + name + ", value=" + value + '}';
    }
}
