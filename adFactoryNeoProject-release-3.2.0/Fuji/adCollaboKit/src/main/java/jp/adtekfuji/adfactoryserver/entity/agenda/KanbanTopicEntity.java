/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.agenda;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * カンバン別計画実績エンティティクラス
 *
 * @author s-heya
 */
@Entity
@Table(name = "view_kanban_topic")
@NamedQueries({
    @NamedQuery(name = "KanbanTopicEntity.countByKanbanId", query = "SELECT COUNT(v.kanbanId) FROM KanbanTopicEntity v WHERE v.kanbanId IN :kanbanIds"),
    @NamedQuery(name = "KanbanTopicEntity.findByKanbanId", query = "SELECT v FROM KanbanTopicEntity v WHERE v.kanbanId IN :kanbanIds ORDER BY v.kanbanId"),
    @NamedQuery(name = "KanbanTopicEntity.countByKanbanIdTerm", query = "SELECT COUNT(v.kanbanId) FROM KanbanTopicEntity v WHERE v.kanbanId IN :kanbanIds AND v.planStartTime >= :fromDate AND v.planEndTime <= :toDate"),
    @NamedQuery(name = "KanbanTopicEntity.findByKanbanIdTerm", query = "SELECT v FROM KanbanTopicEntity v WHERE v.kanbanId IN :kanbanIds AND v.planStartTime >= :fromDate AND v.planEndTime <= :toDate ORDER BY v.kanbanId, v.planStartTime"),
})
public class KanbanTopicEntity extends AbstractTopicEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return "KanbanTopicEntity{" + "kanbanId=" + getKanbanId() + ", workKanbanId=" + getWorkKanbanId() + ", organizationId=" + getOrganizationId() + ", kanbanName=" + getKanbanName()
                + ", kanbanStatus=" + getKanbanStatus() + ", worlflowName=" + getWorkflowName() + ", workName=" + getWorkName() + ", workKanbanStatus=" + getWorkKanbanStatus()
                + ", equipmentName=" + getEquipmentName() + ", organizationName=" + getOrganizationName() + ", planStartTime=" + getPlanStartTime() + ", planEndTime=" + getPlanEndTime()
                + ", actualStartTime=" + getActualStartTime() + ", actualEndTime=" + getActualEndTime() + '}';
    }
}
