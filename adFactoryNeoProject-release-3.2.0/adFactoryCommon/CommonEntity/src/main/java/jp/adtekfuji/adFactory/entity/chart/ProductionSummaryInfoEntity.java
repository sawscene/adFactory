/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.chart;

import java.io.Serializable;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 生産統計データ
 *
 * @author s-heya
 */
@XmlRootElement(name = "productionSummary")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductionSummaryInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElementWrapper(name = "summaryItems")
    @XmlElement(name = "summaryItem")
    private List<SummaryItem> summaryItems;

    @XmlElementWrapper(name = "kanban")
    @XmlElement(name = "kanbanSummary")
    private List<KanbanSummaryInfoEntity> kanban;

    @XmlElementWrapper(name = "organization")
    @XmlElement(name = "organizationSummary")
    private List<OrganizationSummaryInfoEntity> organization;

    @XmlElementWrapper(name = "timeLine")
    @XmlElement(name = "timeLine")
    private List<TimeLineInfoEntity> timeLine;

    @XmlElementWrapper(name = "work")
    @XmlElement(name = "workSummary")
    private List<WorkSummaryInfoEntity> work;

    /**
     * 工程カンバン集計データ一覧
     */
    @XmlElementWrapper(name = "workKanban")
    @XmlElement(name = "workKanbanSummary")
    private List<WorkKanbanSummaryInfoEntity> workKanban;

    public List<SummaryItem> getSummaryItems() {
        return summaryItems;
    }

    public void setSummaryItems(List<SummaryItem> summaryItems) {
        this.summaryItems = summaryItems;
    }

    public List<KanbanSummaryInfoEntity> getKanban() {
        return kanban;
    }

    public void setKanban(List<KanbanSummaryInfoEntity> kanban) {
        this.kanban = kanban;
    }

    public List<OrganizationSummaryInfoEntity> getOrganization() {
        return organization;
    }

    public void setOrganization(List<OrganizationSummaryInfoEntity> organization) {
        this.organization = organization;
    }

    public List<TimeLineInfoEntity> getTimeLine() {
        return timeLine;
    }

    public void setTimeLine(List<TimeLineInfoEntity> timeLine) {
        this.timeLine = timeLine;
    }

    public List<WorkSummaryInfoEntity> getWork() {
        return work;
    }

    public void setWork(List<WorkSummaryInfoEntity> work) {
        this.work = work;
    }

    /**
     * 工程カンバン集計情報一覧を取得する。
     * 
     * @return 工程カンバン集計情報一覧
     */
    public List<WorkKanbanSummaryInfoEntity> getWorkKanban() {
        return workKanban;
    }

    /**
     * 工程カンバン集計情報一覧を設定する。
     * 
     * @param workKanban 工程カンバン集計情報一覧
     */
    public void setWorkKanban(List<WorkKanbanSummaryInfoEntity> workKanban) {
        this.workKanban = workKanban;
    }

    @Override
    public String toString() {
        return "ProductionSummaryInfoEntity{" + "summaryItems=" + summaryItems + '}';
    }
}
