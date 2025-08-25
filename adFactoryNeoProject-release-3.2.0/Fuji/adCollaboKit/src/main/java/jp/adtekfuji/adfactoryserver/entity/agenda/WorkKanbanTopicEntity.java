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
 * 工程カンバン別計画実績エンティティクラス
 *
 * @author s-heya
 */
@Entity
@Table(name = "view_work_kanban_topic")
@NamedQueries({
    @NamedQuery(name = "WorkKanbanTopicEntity.countByKanbanId", query = "SELECT COUNT(v.kanbanId) FROM WorkKanbanTopicEntity v WHERE v.kanbanId IN :kanbanIds"),
    @NamedQuery(name = "WorkKanbanTopicEntity.findByKanbanId", query = "SELECT v FROM WorkKanbanTopicEntity v WHERE v.kanbanId IN :kanbanIds ORDER BY v.kanbanId, v.workKanbanId, v.organizationId"),
    //@NamedQuery(name = "WorkKanbanTopicEntity.countByKanbanIdTerm", query = "SELECT COUNT(v.kanbanId) FROM WorkKanbanTopicEntity v WHERE v.kanbanId IN :kanbanIds AND v.planStartTime >= :fromDate AND v.planEndTime <= :toDate"),
    //@NamedQuery(name = "WorkKanbanTopicEntity.findByKanbanIdTerm", query = "SELECT v FROM WorkKanbanTopicEntity v WHERE v.kanbanId IN :kanbanIds AND v.planStartTime >= :fromDate AND v.planEndTime <= :toDate ORDER BY v.kanbanId, v.workKanbanId, v.organizationId"),
    // 計画が日を跨ぐ計画実績を取得する
    @NamedQuery(name = "WorkKanbanTopicEntity.countByKanbanIdTerm", query = "SELECT COUNT(v.kanbanId) FROM WorkKanbanTopicEntity v WHERE v.kanbanId IN :kanbanIds AND ((v.planStartTime >= :fromDate AND (v.planEndTime <= :toDate OR v.planStartTime <= :toDate)) OR (v.planEndTime <= :toDate AND v.planEndTime >= :fromDate))"),
    @NamedQuery(name = "WorkKanbanTopicEntity.findByKanbanIdTerm", query = "SELECT v FROM WorkKanbanTopicEntity v WHERE v.kanbanId IN :kanbanIds AND ((v.planStartTime >= :fromDate AND (v.planEndTime <= :toDate OR v.planStartTime <= :toDate)) OR (v.planEndTime <= :toDate AND v.planEndTime >= :fromDate)) ORDER BY v.kanbanId, v.workKanbanId, v.organizationId"),
    //@NamedQuery(name = "WorkKanbanTopicEntity.countByOrganizationIdTerm", query = "SELECT COUNT(v.kanbanId) FROM WorkKanbanTopicEntity v WHERE v.planStartTime >= :fromDate AND v.planEndTime <= :toDate AND v.organizationId IN :organizationIds"),
    //@NamedQuery(name = "WorkKanbanTopicEntity.findByOrganizationIdTerm", query = "SELECT v FROM WorkKanbanTopicEntity v WHERE v.planStartTime >= :fromDate AND v.planEndTime <= :toDate AND v.organizationId IN :organizationIds ORDER BY v.organizationId, v.workKanbanId"),
    // 計画が日を跨ぐ計画実績を取得する
    @NamedQuery(name = "WorkKanbanTopicEntity.countByOrganizationIdTerm", query = "SELECT COUNT(v.kanbanId) FROM WorkKanbanTopicEntity v WHERE v.organizationId IN :organizationIds AND ((v.planStartTime >= :fromDate AND (v.planEndTime <= :toDate OR v.planStartTime <= :toDate)) OR (v.planEndTime <= :toDate AND v.planEndTime >= :fromDate))"),
    @NamedQuery(name = "WorkKanbanTopicEntity.findByOrganizationIdTerm", query = "SELECT v FROM WorkKanbanTopicEntity v WHERE v.organizationId IN :organizationIds AND ((v.planStartTime >= :fromDate AND (v.planEndTime <= :toDate OR v.planStartTime <= :toDate)) OR (v.planEndTime <= :toDate AND v.planEndTime >= :fromDate)) ORDER BY v.organizationId, v.workKanbanId"),
    // 工程別生産情報を取得する
    //@NamedQuery(name = "ProductivityEntity.completionByFkWorkId", query = "SELECT NEW jp.adtekfuji.andon.entity.ProductivityEntity(a.workId, COUNT(a.workId)) FROM WorkKanbanTopicEntity a WHERE a.actualEndTime >= :fromDate AND a.actualEndTime < :toDate AND a.workId IN :workIds GROUP BY a.workId"),
})
public class WorkKanbanTopicEntity extends AbstractTopicEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return "WorkKanbanTopicEntity{" + "kanbanId=" + getKanbanId() + ", workKanbanId=" + getWorkKanbanId() + ", organizationId=" + getOrganizationId() + ", kanbanName=" + getKanbanName()
                + ", kanbanStatus=" + getKanbanStatus() + ", worlflowName=" + getWorkflowName() + ", workName=" + getWorkName() + ", workKanbanStatus=" + getWorkKanbanStatus()
                + ", equipmentName=" + getEquipmentName() + ", organizationName=" + getOrganizationName() + ", planStartTime=" + getPlanStartTime() + ", planEndTime=" + getPlanEndTime()
                + ", actualStartTime=" + getActualStartTime() + ", actualEndTime=" + getActualEndTime() + '}';
    }
}
