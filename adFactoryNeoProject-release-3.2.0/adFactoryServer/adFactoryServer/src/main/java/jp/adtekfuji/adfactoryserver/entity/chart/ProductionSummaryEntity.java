/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.chart;

import java.io.Serializable;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.entity.chart.SummaryItem;

/**
 * 生産統計データ
 *
 * @author s-heya
 */
@XmlRootElement(name = "productionSummary")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductionSummaryEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElementWrapper(name = "summaryItems")
    @XmlElement(name = "summaryItem")
    private List<SummaryItem> summaryItems;

    @XmlElementWrapper(name = "kanban")
    @XmlElement(name = "kanbanSummary")
    private List<KanbanSummaryEntity> kanban;

    @XmlElementWrapper(name = "organization")
    @XmlElement(name = "organizationSummary")
    private List<OrganizationSummaryEntity> organization;

    @XmlElementWrapper(name = "workKanban")
    @XmlElement(name = "workKanbanSummary")
    private List<WorkKanbanSummaryEntity> workKanban;


    @XmlElementWrapper(name = "timeLine")
    @XmlElement(name = "timeLine")
    private List<TimeLineEntity> timeLine;

    @XmlElementWrapper(name = "work")
    @XmlElement(name = "workSummary")
    private List<WorkSummaryEntity> work;

    public List<SummaryItem> getSummaryItems() {
        return summaryItems;
    }

    public void setSummaryItems(List<SummaryItem> summaryItems) {
        this.summaryItems = summaryItems;
    }

    public List<KanbanSummaryEntity> getKanban() {
        return kanban;
    }

    public void setKanban(List<KanbanSummaryEntity> kanban) {
        this.kanban = kanban;
    }

    public List<OrganizationSummaryEntity> getOrganization() {
        return organization;
    }

    public void setOrganization(List<OrganizationSummaryEntity> organization) {
        this.organization = organization;
    }

    public List<TimeLineEntity> getTimeLine() {
        return timeLine;
    }

    public void setTimeLine(List<TimeLineEntity> timeLine) {
        this.timeLine = timeLine;
    }

    public List<WorkSummaryEntity> getWork() {
        return work;
    }

    public void setWork(List<WorkSummaryEntity> work) {
        this.work = work;
    }

    public List<WorkKanbanSummaryEntity> getWorkKanban() {
        return workKanban;
    }

    public void setWorkKanban(List<WorkKanbanSummaryEntity> workKanban) {
        this.workKanban = workKanban;
    }

    @Override
    public String toString() {
        return "ProductionSummaryEntity{" + "summaryItems=" + summaryItems + '}';
    }
}
