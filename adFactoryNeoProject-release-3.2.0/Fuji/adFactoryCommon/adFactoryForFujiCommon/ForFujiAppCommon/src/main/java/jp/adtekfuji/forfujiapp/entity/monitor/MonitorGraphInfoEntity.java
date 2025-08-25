/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.entity.monitor;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 進捗モニタグラフ表示用情報クラス
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.20.Thr
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "monitorGraphInfo")
public class MonitorGraphInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private Long unitId;
    @XmlElement()
    private String title;
    @XmlElementWrapper(name = "monitorGraphDatas")
    @XmlElement(name = "monitorGraphData")
    private List<MonitorGraphData> graphData;
    @XmlElementWrapper(name = "kanbanIds")
    @XmlElement(name = "kanbanId")
    private List<Long> kanbanIds;

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<MonitorGraphData> getGraphData() {
        return graphData;
    }

    public void setGraphData(List<MonitorGraphData> graphData) {
        this.graphData = graphData;
    }

    public List<Long> getKanbanIds() {
        return kanbanIds;
    }

    public void setKanbanIds(List<Long> kanbanIds) {
        this.kanbanIds = kanbanIds;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MonitorGraphInfoEntity other = (MonitorGraphInfoEntity) obj;
        return true;
    }

    @Override
    public String toString() {
        return "MonitorGraphInfoEntity{" + "title=" + title + '}';
    }
}
