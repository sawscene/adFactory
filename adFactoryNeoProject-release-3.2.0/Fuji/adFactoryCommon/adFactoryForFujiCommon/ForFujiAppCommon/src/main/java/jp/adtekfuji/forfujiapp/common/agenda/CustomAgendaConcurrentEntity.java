/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.common.agenda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * ユニットの予実情報並列
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.11.24.thr
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "customAgendaConcurrent")
public class CustomAgendaConcurrentEntity {

    private static final long serialVersionUID = 1L;

    @XmlElementWrapper(name = "customAgendaItems")
    @XmlElement(name = "customAgendaItem")
    private List<CustomAgendaItemEntity> itemCollection = new ArrayList<>();

    public CustomAgendaConcurrentEntity() {
    }

    public List<CustomAgendaItemEntity> getItemCollection() {
        return itemCollection;
    }

    public void setItemCollection(List<CustomAgendaItemEntity> itemCollection) {
        this.itemCollection = itemCollection;
    }

    public CustomAgendaConcurrentEntity addItem(CustomAgendaItemEntity item) {
        this.itemCollection.add(item);
        return this;
    }

    public CustomAgendaConcurrentEntity addItems(CustomAgendaItemEntity... items) {
        this.itemCollection.addAll(Arrays.asList(items));
        return this;
    }
}
