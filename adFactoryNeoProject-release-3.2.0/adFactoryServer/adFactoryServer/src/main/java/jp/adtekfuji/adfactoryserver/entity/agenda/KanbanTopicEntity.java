/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.agenda;

import java.io.Serializable;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 * カンバン別計画実績情報(VIEW)
 *
 * @author s-heya
 */
@Entity
@Table(name = "view_kanban_topic")
@NamedQueries({
    // カンバンID一覧を指定して、計画実績情報の件数を取得する。
    @NamedQuery(name = "KanbanTopicEntity.countByKanbanId", query = "SELECT COUNT(v.kanbanId) FROM KanbanTopicEntity v WHERE v.kanbanId IN :kanbanIds"),
    // カンバンID一覧を指定して、計画実績情報一覧を取得する。
    @NamedQuery(name = "KanbanTopicEntity.findByKanbanId", query = "SELECT v FROM KanbanTopicEntity v WHERE v.kanbanId IN :kanbanIds ORDER BY v.kanbanId"),
})
public class KanbanTopicEntity extends AbstractTopicEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return new StringBuilder("KanbanTopicEntity{")
                .append("kanbanId=").append(this.getKanbanId())
                .append(", workKanbanId=").append(this.getWorkKanbanId())
                .append(", organizationId=").append(this.getOrganizationId())
                .append(", kanbanName=").append(this.getKanbanName())
                .append(", kanbanStatus=").append(this.getKanbanStatus())
                .append(", workflowName=").append(this.getWorkflowName())
                .append(", modelName=").append(this.getModelName())
                .append(", workName=").append(this.getWorkName())
                .append(", workKanbanStatus=").append(this.getWorkKanbanStatus())
                .append(", equipmentName=").append(this.getEquipmentName())
                .append(", organizationName=").append(this.getOrganizationName())
                .append(", planStartTime=").append(this.getPlanStartTime())
                .append(", planEndTime=").append(this.getPlanEndTime())
                .append(", actualStartTime=").append(this.getActualStartTime())
                .append(", actualEndTime=").append(this.getActualEndTime())
                .append(", fontColor=").append(this.getFontColor())
                .append(", backColor=").append(this.getBackColor())
                .append(", sumTimes=").append(this.getSumTimes())
                .append(", taktTime=").append(this.getTaktTime())
                .append(", workId=").append(this.getWorkId())
                .append(", parentId=").append(this.getParentId())
                .append(", workflowRev=").append(this.getWorkflowRev())
                .append(", kanbanPlanStartTime=").append(this.getKanbanPlanStartTime())
                .append(", kanbanPlanEndTime=").append(this.getKanbanPlanEndTime())
                .append(", kanbanActualStartTime=").append(this.getKanbanActualStartTime())
                .append(", kanbanActualEndTime=").append(this.getKanbanActualEndTime())
                .append(", workKanbanOrder=").append(this.getWorkKanbanOrder())
                .append(", separateWorkFlag=").append(this.isSeparateWorkFlag())
                .append(", workerName=").append(this.getWorkerName())
                .append("}")
                .toString();
    }
}
