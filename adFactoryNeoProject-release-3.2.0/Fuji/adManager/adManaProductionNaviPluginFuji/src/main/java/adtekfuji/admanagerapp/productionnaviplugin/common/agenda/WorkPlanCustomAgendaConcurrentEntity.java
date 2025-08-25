/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.common.agenda;

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
public class WorkPlanCustomAgendaConcurrentEntity {

    private static final long serialVersionUID = 1L;

    @XmlElementWrapper(name = "customAgendaItems")
    @XmlElement(name = "customAgendaItem")
    private List<WorkPlanCustomAgendaItemEntity> itemCollection = new ArrayList<>();

    public WorkPlanCustomAgendaConcurrentEntity() {
    }

    public List<WorkPlanCustomAgendaItemEntity> getItemCollection() {
        return itemCollection;
    }

    public void setItemCollection(List<WorkPlanCustomAgendaItemEntity> itemCollection) {
        this.itemCollection = itemCollection;
    }

    public WorkPlanCustomAgendaConcurrentEntity addItem(WorkPlanCustomAgendaItemEntity item) {
        this.itemCollection.add(item);
        return this;
    }

    public WorkPlanCustomAgendaConcurrentEntity addItems(WorkPlanCustomAgendaItemEntity... items) {
        this.itemCollection.addAll(Arrays.asList(items));
        return this;
    }
}
