/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.kanban;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 追加情報
 * 
 * @author ke.yokoi
 */
@XmlRootElement(name = "kanbanProperty")
@XmlAccessorType(XmlAccessType.FIELD)
public class KanbanPropertyEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long kanbannPropertyId;
    private Long fkKanbanId;
    private String kanbanPropertyName;
    private String kanbanPropertyType;
    private String kanbanPropertyValue;
    private Integer kanbanPropertyOrder;

    public KanbanPropertyEntity() {
    }

    public KanbanPropertyEntity(KanbanPropertyEntity in) {
        this.kanbannPropertyId = in.kanbannPropertyId;
        this.fkKanbanId = in.fkKanbanId;
        this.kanbanPropertyName = in.kanbanPropertyName;
        this.kanbanPropertyType = in.kanbanPropertyType;
        this.kanbanPropertyValue = in.kanbanPropertyValue;
        this.kanbanPropertyOrder = in.kanbanPropertyOrder;
    }

    public KanbanPropertyEntity(Long fkKanbanId, String kanbanPropertyName, String kanbanPropertyType, String kanbanPropertyValue, Integer kanbanPropertyOrder) {
        this.fkKanbanId = fkKanbanId;
        this.kanbanPropertyName = kanbanPropertyName;
        this.kanbanPropertyType = kanbanPropertyType;
        this.kanbanPropertyValue = kanbanPropertyValue;
        this.kanbanPropertyOrder = kanbanPropertyOrder;
    }

    public Long getKanbannPropertyId() {
        return kanbannPropertyId;
    }

    public void setKanbannPropertyId(Long kanbannPropertyId) {
        this.kanbannPropertyId = kanbannPropertyId;
    }

    public Long getFkKanbanId() {
        return fkKanbanId;
    }

    public void setFkKanbanId(Long fkKanbanId) {
        this.fkKanbanId = fkKanbanId;
    }

    public String getKanbanPropertyName() {
        return kanbanPropertyName;
    }

    public void setKanbanPropertyName(String kanbanPropertyName) {
        this.kanbanPropertyName = kanbanPropertyName;
    }

    public String getKanbanPropertyType() {
        return kanbanPropertyType;
    }

    public void setKanbanPropertyType(String kanbanPropertyType) {
        this.kanbanPropertyType = kanbanPropertyType;
    }

    public String getKanbanPropertyValue() {
        return kanbanPropertyValue;
    }

    public void setKanbanPropertyValue(String kanbanPropertyValue) {
        this.kanbanPropertyValue = kanbanPropertyValue;
    }

    public Integer getKanbanPropertyOrder() {
        return kanbanPropertyOrder;
    }

    public void setKanbanPropertyOrder(Integer kanbanPropertyOrder) {
        this.kanbanPropertyOrder = kanbanPropertyOrder;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (kanbannPropertyId != null ? kanbannPropertyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof KanbanPropertyEntity)) {
            return false;
        }
        KanbanPropertyEntity other = (KanbanPropertyEntity) object;
        if ((this.kanbannPropertyId == null && other.kanbannPropertyId != null) || (this.kanbannPropertyId != null && !this.kanbannPropertyId.equals(other.kanbannPropertyId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "KanbanPropertyEntity{" + "kanbannPropertyId=" + kanbannPropertyId + ", fkKanbanId=" + fkKanbanId + ", kanbanPropertyName=" + kanbanPropertyName + ", kanbanPropertyType=" + kanbanPropertyType + ", kanbanPropertyValue=" + kanbanPropertyValue + ", kanbanPropertyOrder=" + kanbanPropertyOrder + '}';
    }

}
