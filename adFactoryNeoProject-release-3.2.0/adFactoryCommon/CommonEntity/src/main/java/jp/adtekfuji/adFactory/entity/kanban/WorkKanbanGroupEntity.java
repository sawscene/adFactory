/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.kanban;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 工程グループエンティティクラス
 *
 * @author s-heya
 */
@XmlRootElement(name = "workKanbanGroup")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkKanbanGroupEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "groupKey")
    private WorkKanbanGroupKey groupKey;
    @XmlElementWrapper(name = "worKanbans")
    @XmlElement(name = "worKanbanId")
    private List<Long> worKanbanCollection;

    public static WorkKanbanGroupEntity createGroup(WorkKanbanGroupKey groupKey, List<Long> worKanbanCollection) {
        WorkKanbanGroupEntity entity = new WorkKanbanGroupEntity();
        entity.setKey(groupKey);
        entity.setWorKanbanCollection(worKanbanCollection);
        return entity;
    }

    public WorkKanbanGroupKey getKey() {
        return this.groupKey;
    }

    public void setKey(WorkKanbanGroupKey key) {
        this.groupKey = key;
    }

    public List<Long> getWorKanbanCollection() {
        return this.worKanbanCollection;
    }

    public void setWorKanbanCollection(List<Long> worKanbanCollection) {
        this.worKanbanCollection = worKanbanCollection;
    }

   @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.groupKey);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WorkKanbanGroupEntity other = (WorkKanbanGroupEntity) obj;
        return Objects.equals(this.groupKey, other.groupKey);
    }

    @Override
    public String toString() {
        return "WorkKanbanGroupEntity{" + "key=" + groupKey + ", worKanbanCollection=" + worKanbanCollection + '}';
    }
}
