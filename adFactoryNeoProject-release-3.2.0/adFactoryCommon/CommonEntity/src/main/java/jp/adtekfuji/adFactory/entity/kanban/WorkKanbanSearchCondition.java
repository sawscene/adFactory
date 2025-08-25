/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.kanban;

import java.io.Serializable;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 工程カンバン検索条件
 *
 * @author s-heya
 */
@XmlRootElement(name = "workKanbanSearchCondition")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkKanbanSearchCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElementWrapper(name = "worKanbans")
    @XmlElement(name = "worKanbanId")
    private List<Long> worKanbanCollection;

    public List<Long> getWorKanbanCollection() {
        return this.worKanbanCollection;
    }

    public void setWorKanbanCollection(List<Long> worKanbanCollection) {
        this.worKanbanCollection = worKanbanCollection;
    }

    @Override
    public String toString() {
        return "WorkKanbanSearchCondition{" + "worKanbanCollection=" + worKanbanCollection + '}';
    }
}
