/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.kanban;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adfactoryserver.entity.actual.ActualResultEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity;
import jp.adtekfuji.adfactoryserver.utility.PgJsonbConverter;

/**
 * カンバン情報
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "trn_kanban")
@XmlRootElement(name = "kanban")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedNativeQueries({
    // 注文番号を指定して、残り台数を取得する。
    @NamedNativeQuery(name = "KanbanEntity.calcOrderInfoRemByPorder",
            query = "SELECT a.kvol - a.lvol + a.defect AS rem FROM (SELECT job->>'PORDER' porder, MAX((job->'KVOL')::integer) kvol, SUM((job->'LVOL')::integer) lvol, SUM((job->'DEFECT')::integer) defect FROM trn_kanban t JOIN jsonb_array_elements(t.service_info) items(item) ON item->>'service' = 'els' JOIN jsonb_array_elements(item::jsonb->'job') job ON job->>'PORDER' = ?1 GROUP BY porder) a"),
    // 最新リビジョンのカンバンを取得
    @NamedNativeQuery(name = "KanbanEntity.findLatest", query = "SELECT * FROM trn_kanban tk1 WHERE tk1.kanban_id IN (SELECT FIRST_VALUE(tk.kanban_id) OVER (ORDER BY mw.workflow_rev DESC) FROM trn_kanban tk JOIN mst_workflow mw ON tk.kanban_name = ?1 AND mw.workflow_name = ?2 AND tk.workflow_id = mw.workflow_id)", resultClass = KanbanEntity.class),
    // 工程順名から作業中の工程のあるカンバンを取得する
	@NamedNativeQuery(name ="KanbanEntity.findWithWorkingWorkByWorkflowName", query = "SELECT k.* FROM trn_kanban k JOIN trn_work_kanban wk ON k.kanban_id = wk.kanban_id JOIN mst_workflow wf ON wk.workflow_id = wf.workflow_id JOIN trn_work_kanban_working wkwe ON wk.work_kanban_id = wkwe.work_kanban_id WHERE wf.workflow_name = ANY(?1) AND wk.work_status = 'WORKING'", resultClass = KanbanEntity.class),

})
@NamedQueries({
    // カンバンIDを指定して、カンバン情報を取得する。
    @NamedQuery(name = "KanbanEntity.findByKanbanId", query = "SELECT k FROM KanbanEntity k WHERE k.kanbanId = :kanbanId"),
    // カンバンID一覧を指定して、カンバン情報一覧を取得する。
    @NamedQuery(name = "KanbanEntity.findByKanbanIds", query = "SELECT k FROM KanbanEntity k WHERE k.kanbanId IN :kanbanIds"),
    // カンバン名を指定して、カンバン情報一覧を取得する。
    @NamedQuery(name = "KanbanEntity.findByKanbanName", query = "SELECT k FROM KanbanEntity k WHERE k.kanbanName = :kanbanName ORDER BY k.kanbanId DESC"),
    // カンバン名・工程順名・版数を指定して、カンバン情報を取得する。
    @NamedQuery(name = "KanbanEntity.findByNameAndRev", query = "SELECT k FROM KanbanEntity k LEFT JOIN WorkflowEntity w ON w.workflowId = k.workflowId WHERE k.kanbanName = :kanbanName AND w.workflowName = :workflowName AND w.workflowRev = :workflowRev"),
    // カンバン名・工程順名を指定して、カンバン情報を取得する。
    @NamedQuery(name = "KanbanEntity.findByName", query = "SELECT k FROM KanbanEntity k LEFT JOIN WorkflowEntity w ON w.workflowId = k.workflowId WHERE k.kanbanName = :kanbanName AND w.workflowName = :workflowName ORDER BY k.kanbanId DESC"),

    // カンバン階層IDを指定して、階層に属するカンバン情報一覧を取得する。
    @NamedQuery(name = "KanbanEntity.findByKanbanHierarchyId", query = "SELECT k FROM ConKanbanHierarchyEntity c JOIN KanbanEntity k ON c.kanbanId = k.kanbanId WHERE c.kanbanHierarchyId = :hierarchyId ORDER BY k.kanbanName, k.kanbanSubname, k.kanbanId"),

    // 設備ID・組織IDを指定して、生産可能なカンバンの件数を取得する。
    @NamedQuery(name = "KanbanEntity.countProductAll", query = "SELECT COUNT(k.kanbanId) FROM KanbanEntity k WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId IN (SELECT wk.kanbanId FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.workStatus != jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds)) AND k.kanbanName LIKE :keyword"),
    @NamedQuery(name = "KanbanEntity.findProductAllPlanAsc",        query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.workStatus != jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY wk1.startDatetime, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED THEN 1 ELSE 2 END, k.kanbanId"),
    @NamedQuery(name = "KanbanEntity.findProductAllPlanDesc",       query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.workStatus != jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY wk1.startDatetime DESC, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED THEN 1 ELSE 2 END, k.kanbanId DESC"),
    @NamedQuery(name = "KanbanEntity.findProductAllNameAsc",        query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.workStatus != jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY k.kanbanName, k.kanbanId"),
    @NamedQuery(name = "KanbanEntity.findProductAllNameDesc",       query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.workStatus != jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY k.kanbanName DESC, k.kanbanId DESC"),
    @NamedQuery(name = "KanbanEntity.findProductAllStatusAsc",      query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.workStatus != jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED THEN 1 ELSE 2 END, wk1.startDatetime, k.kanbanId"),
    @NamedQuery(name = "KanbanEntity.findProductAllStatusDesc",     query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.workStatus != jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING THEN 1 ELSE 2 END, wk1.startDatetime DESC, k.kanbanId DESC"),
    @NamedQuery(name = "KanbanEntity.findProductAllCreateAsc",      query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.workStatus != jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED THEN 1 ELSE 2 END, k.kanbanId"),
    @NamedQuery(name = "KanbanEntity.findProductAllCreateDesc",     query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.workStatus != jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING THEN 1 ELSE 2 END, k.kanbanId DESC"),

    @NamedQuery(name = "KanbanEntity.findProductAllTermPlanAsc",    query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.startDatetime <= :toDate AND wk.workStatus != jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY wk1.startDatetime, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED THEN 1 ELSE 2 END, k.kanbanId"),
    @NamedQuery(name = "KanbanEntity.findProductAllTermPlanDesc",   query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.startDatetime <= :toDate AND wk.workStatus != jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY wk1.startDatetime DESC, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED THEN 1 ELSE 2 END, k.kanbanId DESC"),
    @NamedQuery(name = "KanbanEntity.findProductAllTermNameAsc",    query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.startDatetime <= :toDate AND wk.workStatus != jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY k.kanbanName, k.kanbanId"),
    @NamedQuery(name = "KanbanEntity.findProductAllTermNameDesc",   query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.startDatetime <= :toDate AND wk.workStatus != jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY k.kanbanName DESC, k.kanbanId DESC"),
    @NamedQuery(name = "KanbanEntity.findProductAllTermStatusAsc",  query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.startDatetime <= :toDate AND wk.workStatus != jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED THEN 1 ELSE 2 END, wk1.startDatetime, k.kanbanId"),
    @NamedQuery(name = "KanbanEntity.findProductAllTermStatusDesc", query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.startDatetime <= :toDate AND wk.workStatus != jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING THEN 1 ELSE 2 END, wk1.startDatetime DESC, k.kanbanId DESC"),
    @NamedQuery(name = "KanbanEntity.findProductAllTermCreateAsc",  query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.startDatetime <= :toDate AND wk.workStatus != jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED THEN 1 ELSE 2 END, k.kanbanId"),
    @NamedQuery(name = "KanbanEntity.findProductAllTermCreateDesc", query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.startDatetime <= :toDate AND wk.workStatus != jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING THEN 1 ELSE 2 END, k.kanbanId DESC"),

    // 応援モード時に使用する
    @NamedQuery(name = "KanbanEntity.countProduct", query = "SELECT COUNT(k.kanbanId) FROM KanbanEntity k WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId IN (SELECT wk.kanbanId FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.workStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds)) AND k.kanbanName LIKE :keyword"),
    @NamedQuery(name = "KanbanEntity.findProductPlanAsc",        query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.workStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY wk1.startDatetime, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED THEN 1 ELSE 2 END, k.kanbanId"),
    @NamedQuery(name = "KanbanEntity.findProductPlanDesc",       query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.workStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY wk1.startDatetime DESC, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED THEN 1 ELSE 2 END, k.kanbanId"),
    @NamedQuery(name = "KanbanEntity.findProductNameAsc",        query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.workStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY k.kanbanName ,k.kanbanId"),
    @NamedQuery(name = "KanbanEntity.findProductNameDesc",       query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.workStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY k.kanbanName DESC, k.kanbanId DESC"),
    @NamedQuery(name = "KanbanEntity.findProductStatusAsc",      query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.workStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED THEN 1 ELSE 2 END, wk1.startDatetime, k.kanbanId"),
    @NamedQuery(name = "KanbanEntity.findProductStatusDesc",     query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.workStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING THEN 1 ELSE 2 END, wk1.startDatetime DESC, k.kanbanId DESC"),
    @NamedQuery(name = "KanbanEntity.findProductCreateAsc",      query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.workStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED THEN 1 ELSE 2 END, k.kanbanId"),
    @NamedQuery(name = "KanbanEntity.findProductCreateDesc",     query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.workStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING THEN 1 ELSE 2 END, k.kanbanId DESC"),

    @NamedQuery(name = "KanbanEntity.findProductTermPlanAsc",    query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.startDatetime <= :toDate AND wk.workStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY wk1.startDatetime, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED THEN 1 ELSE 2 END, k.kanbanId"),
    @NamedQuery(name = "KanbanEntity.findProductTermPlanDesc",   query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.startDatetime <= :toDate AND wk.workStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY wk1.startDatetime DESC, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED THEN 1 ELSE 2 END, k.kanbanId"),
    @NamedQuery(name = "KanbanEntity.findProductTermNameAsc",    query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.startDatetime <= :toDate AND wk.workStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY k.kanbanName, k.kanbanId"),
    @NamedQuery(name = "KanbanEntity.findProductTermNameDesc",   query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.startDatetime <= :toDate AND wk.workStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY k.kanbanName DESC, k.kanbanId DESC"),
    @NamedQuery(name = "KanbanEntity.findProductTermStatusAsc",  query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.startDatetime <= :toDate AND wk.workStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED THEN 1 ELSE 2 END, k.startDatetime, k.kanbanId"),
    @NamedQuery(name = "KanbanEntity.findProductTermStatusDesc", query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.startDatetime <= :toDate AND wk.workStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING THEN 1 ELSE 2 END, k.startDatetime DESC, k.kanbanId DESC"),
    @NamedQuery(name = "KanbanEntity.findProductTermCreateAsc",  query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.startDatetime <= :toDate AND wk.workStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED THEN 1 ELSE 2 END, k.kanbanId"),
    @NamedQuery(name = "KanbanEntity.findProductTermCreateDesc", query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity.KanbanEntityAndStartDate(k, wk1.startDatetime) FROM KanbanEntity k, (SELECT wk.kanbanId, MIN(wk.startDatetime) startDatetime FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.startDatetime <= :toDate AND wk.workStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) GROUP BY wk.kanbanId) wk1 WHERE (k.kanbanStatus IN :kanbanStatuses) AND k.kanbanId = wk1.kanbanId AND k.kanbanName LIKE :keyword ORDER BY CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND THEN 1 ELSE 2 END, CASE WHEN k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING THEN 1 ELSE 2 END, k.kanbanId DESC"),

    // 指定した設備に対する完了数を取得する
    //@NamedQuery(name = "KanbanEntity.completionByEquipmentId", query = "SELECT COUNT(k.kanbanId) FROM KanbanEntity k WHERE k.actualCompTime >= :fromDate AND k.actualCompTime < :toDate AND k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION AND k.kanbanId IN (SELECT wk.kanbanId FROM WorkKanbanEntity wk WHERE wk.workKanbanId IN (SELECT e.workkanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds))"),
    // 当日開始のカンバン数を取得する
    @NamedQuery(name = "KanbanEntity.countKanban", query = "SELECT COUNT(k.kanbanId) FROM KanbanEntity k WHERE k.startDatetime >= :fromDate AND k.startDatetime <= :toDate AND k.kanbanId NOT IN (SELECT ckh.kanbanId FROM ConKanbanHierarchyEntity ckh WHERE ckh.kanbanHierarchyId IN :kanbanHierarchyIds)"),
    // カンバン検索タイプBの対象カンバン一覧を取得する
    @NamedQuery(name = "KanbanEntity.findProductTypeBByWorkflowId", query = "SELECT k FROM KanbanEntity k WHERE k.kanbanId NOT IN (SELECT ckh.kanbanId FROM ConKanbanHierarchyEntity ckh WHERE ckh.kanbanHierarchyId IN :kanbanHierarchyIds) AND k.kanbanStatus IN (jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED, jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING, jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND) AND k.workflowId = :workflowId AND k.kanbanName LIKE :kanbanName AND k.kanbanId IN (SELECT wk.kanbanId FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.workStatus IN (jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED, jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING, jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND) AND wk.workKanbanId IN (SELECT cwo.workKanbanId FROM ConWorkkanbanOrganizationEntity cwo WHERE cwo.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT cwe.workKanbanId FROM ConWorkkanbanEquipmentEntity cwe WHERE cwe.equipmentId IN :equipmentIds)) ORDER BY k.startDatetime, k.kanbanId"),
    // カンバン検索タイプBの対象カンバン一覧を取得する (工程順の指定なし)
    @NamedQuery(name = "KanbanEntity.findProductTypeB", query = "SELECT k FROM KanbanEntity k WHERE k.kanbanId NOT IN (SELECT ckh.kanbanId FROM ConKanbanHierarchyEntity ckh WHERE ckh.kanbanHierarchyId IN :kanbanHierarchyIds) AND k.kanbanStatus IN (jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED, jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING, jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND) AND k.kanbanName LIKE :kanbanName AND k.kanbanId IN (SELECT wk.kanbanId FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.workStatus IN (jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED, jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING, jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND) AND wk.workKanbanId IN (SELECT cwo.workKanbanId FROM ConWorkkanbanOrganizationEntity cwo WHERE cwo.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT cwe.workKanbanId FROM ConWorkkanbanEquipmentEntity cwe WHERE cwe.equipmentId IN :equipmentIds)) ORDER BY k.startDatetime, k.kanbanId"),

    @NamedQuery(name = "KanbanEntity.findNameAndStatus", query = "SELECT k.kanbanName, k.kanbanStatus FROM KanbanEntity k WHERE k.kanbanId = :kanbanId"),
    // カンバンIDを指定して、追加情報を取得する。
    @NamedQuery(name = "KanbanEntity.findAddInfo", query = "SELECT k.kanbanAddInfo FROM KanbanEntity k WHERE k.kanbanId = :kanbanId"),
    // カンバンIDを指定して、追加情報を更新する。
    @NamedQuery(name = "KanbanEntity.updateAddInfo", query = "UPDATE KanbanEntity k SET k.kanbanAddInfo = :addInfo WHERE k.kanbanId = :kanbanId"),
    // カンバン名・工程順IDを指定して、カンバンIDを取得する。
    @NamedQuery(name = "KanbanEntity.findIdByKanbanName", query = "SELECT k.kanbanId FROM KanbanEntity k WHERE k.kanbanName = :kanbanName AND k.workflowId = :workflowId"),
    // カンバンIDを指定して、サービス情報を更新する。
    @NamedQuery(name = "KanbanEntity.updateServiceInfo", query = "UPDATE KanbanEntity k SET k.serviceInfo = :serviceInfo WHERE k.kanbanId = :kanbanId"),
    // カンバンIDを指定して、承認情報を更新する。
    @NamedQuery(name = "KanbanEntity.updateApproval", query = "UPDATE KanbanEntity k SET k.approval = :approval WHERE k.kanbanId = :kanbanId"),
    // カンバンIDを指定して、ラベル情報を更新する。
    //@NamedQuery(name = "KanbanEntity.updateLabel", query = "UPDATE KanbanEntity k SET k.kanbanLabel = :kanbanLabel WHERE k.kanbanId = :kanbanId"),
    // ワークフロー名からカンバンを検索する
    @NamedQuery(name = "KanbanEntity.findByWorkflowName", query = "SELECT k FROM KanbanEntity k, (SELECT wf.workflowId workflowId FROM WorkflowEntity wf WHERE wf.workflowName IN :workflowName) wf1 WHERE k.workflowId = wf1.workflowId"),

})
public class KanbanEntity implements Serializable, Cloneable {

    public static class KanbanEntityAndStartDate extends KanbanEntity {
        public KanbanEntityAndStartDate(KanbanEntity in, Date workStartDateTime) {
            super(in);
            this.setWorkStartDateTime(new Date(workStartDateTime.getTime()));
        }
    };

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "kanban_id")
    protected Long kanbanId;                        // カンバンID

    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 256)
    @Column(name = "kanban_name")
    private String kanbanName;                      // カンバン名

    @Size(max = 256)
    @Column(name = "kanban_subname")
    private String kanbanSubname;                   // サブカンバン名

    @Basic(optional = false)
    //@NotNull
    @Column(name = "workflow_id")
    @XmlElement(name = "fkWorkflowId")
    private Long workflowId;                        // 工程順ID

    @Column(name = "start_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDatetime;                     // 先頭工程開始予定日時

    @Column(name = "comp_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date compDatetime;                      // 最終工程完了予定日時

    @Column(name = "update_person_id")
    @XmlElement(name = "fkUpdatePersonId")
    private Long updatePersonId;                    // 更新者(組織ID)

    @Column(name = "update_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDatetime;                    // 更新日時

    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    //@NotNull
    @Column(name = "kanban_status")
    private KanbanStatusEnum kanbanStatus;          // カンバンステータス

    @Column(name = "interrupt_reason_id")
    @XmlElement(name = "fkInterruptReasonId")
    private Long interruptReasonId;                 // 中断理由ID

    @Column(name = "delay_reason_id")
    @XmlElement(name = "fkDelayReasonId")
    private Long delayReasonId;                     // 遅延理由ID

    @Column(name = "lot_quantity")
    private Integer lotQuantity;                    // ロット数量

    @Column(name = "actual_start_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualStartTime;                   // 開始日時(実績)

    @Column(name = "actual_comp_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualCompTime;                    // 完了日時(実績)

    @Column(name = "model_name")
    private String modelName;                       // モデル名

    @Column(name = "production_type")
    private Integer productionType = 0;             // 生産タイプ

    @Column(name = "repair_num")
    private Integer repairNum;                      // 補修数

    @Column(name = "kanban_add_info", length = 30000)
    @Convert(converter = PgJsonbConverter.class)
    private String kanbanAddInfo;                   // 追加情報(JSON)

    @Column(name = "service_info", length = 30000)
    @Convert(converter = PgJsonbConverter.class)
    private String serviceInfo;                     // サービス情報(JSON)

    //@NotNull
    @Column(name = "ver_info")
    @Version
    private Integer verInfo = 1;                    // 排他用バーション

    @Column(name = "production_number")
    private String productionNumber;                // 製造番号

    @Column(name = "approval", length = 30000)
    @Convert(converter = PgJsonbConverter.class)
    private String approval;                        // 承認(JSON)

    @Column(name = "kanban_label", length = 30000)
    @Convert(converter = PgJsonbConverter.class)
    private String kanbanLabel;                     // ラベル情報(JSON)
            
    @Transient
    private Long parentId;                          // カンバン階層ID

    @Transient
    private String workflowName;                    // 工程順名

    @Transient
    private Integer workflowRev;                    // 工程順版数

    @XmlTransient
    @JoinColumn(name = "workflow_id", referencedColumnName = "workflow_id", insertable = false, updatable = false, nullable = true)
    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    private WorkflowEntity workflow;                // 工程順情報

    @Transient
    @XmlTransient
    private long workKanbanCount;                   // 工程カンバン数

    @XmlElementWrapper(name = "workKanbans")
    @XmlElement(name = "workKanban")
    @Transient
    private List<WorkKanbanEntity> workKanbanCollection = null; // 通常工程の工程カンバン情報一覧

    @XmlElementWrapper(name = "separateworkKanbans")
    @XmlElement(name = "separateworkKanban")
    @Transient
    private List<WorkKanbanEntity> separateworkKanbanCollection = null; // 追加工程の工程カンバン情報一覧

    @XmlElementWrapper(name = "actualResults")
    @XmlElement(name = "actualResult")
    @Transient
    private List<ActualResultEntity> actualResultCollection = null; // 工程実績情報一覧

    @XmlElementWrapper(name = "products")
    @XmlElement(name = "product")
    @Transient
    private List<ProductEntity> products = null;    // 製品情報一覧

    @Transient
    private String ledgerPath;                      // 帳票テンプレートパス(JSON)

    @Transient
    @JsonProperty("workStartDateTime")
    private Date workStartDateTime = null;          // 工程カンバンの開始時間

    @Column(name = "defect_num")
    private Integer defectNum;                      // 不良数   
    
    @Column(name = "comp_num")
    private Integer compNum = 0;                    // 完成数

    @Column(name = "cycle_time")
    private Integer cycleTime;                      // 標準サイクルタイム

    /**
     * コンストラクタ
     */
    public KanbanEntity() {
    }

    /**
     *
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    public KanbanEntity clone() throws CloneNotSupportedException {
        try {
            return (KanbanEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.toString());
        }
    }

    /**
     * コンストラクタ
     *
     * @param in カンバン情報
     */
    public KanbanEntity(KanbanEntity in) {
        this.kanbanId = in.kanbanId;
        this.kanbanName = in.kanbanName;
        this.kanbanSubname = in.kanbanSubname;
        this.workflowId = in.workflowId;
        this.startDatetime = in.startDatetime;
        this.compDatetime = in.compDatetime;
        this.updatePersonId = in.updatePersonId;
        this.updateDatetime = in.updateDatetime;
        this.kanbanStatus = in.kanbanStatus;
        this.interruptReasonId = in.interruptReasonId;
        this.delayReasonId = in.delayReasonId;
        this.lotQuantity = in.lotQuantity;
        this.actualStartTime = in.actualStartTime;
        this.actualCompTime = in.actualCompTime;
        this.modelName = in.modelName;
        this.repairNum = in.repairNum;
        this.productionType = in.productionType;
        this.kanbanAddInfo = in.kanbanAddInfo;
        this.serviceInfo = in.serviceInfo;
        this.verInfo = in.verInfo;
        this.productionNumber = in.productionNumber;
        this.approval = in.approval;
        this.kanbanLabel = in.kanbanLabel;

        this.parentId = in.parentId;
        this.workflowName = in.workflowName;
        this.workflowRev = in.workflowRev;


        this.workKanbanCollection = new ArrayList();
        this.separateworkKanbanCollection = new ArrayList();

        if (Objects.nonNull(in.getProducts())) {
            this.products = new ArrayList<>();
            for (ProductEntity product : in.getProducts()) {
                this.products.add(new ProductEntity(product));
            }
        }
    }

    /**
     * コンストラクタ
     *
     * @param in カンバン情報
     * @param parentId カンバン階層ID
     * @param workflowName 工程順名
     * @param workflowRev 工程順版数
     */
    public KanbanEntity(KanbanEntity in, Long parentId, String workflowName, Integer workflowRev) {
        this.kanbanId = in.getKanbanId();
        this.kanbanName = in.getKanbanName();
        this.kanbanSubname = in.getKanbanSubname();
        this.workflowId = in.getWorkflowId();
        this.startDatetime = in.getStartDatetime();
        this.compDatetime = in.getCompDatetime();
        this.updatePersonId = in.getUpdatePersonId();
        this.updateDatetime = in.getUpdateDatetime();
        this.kanbanStatus = in.getKanbanStatus();
        this.interruptReasonId = in.getInterruptReasonId();
        this.delayReasonId = in.getDelayReasonId();
        this.actualStartTime = in.getActualStartTime();
        this.actualCompTime = in.getActualCompTime();
        this.lotQuantity = in.getLotQuantity();
        this.modelName = in.getModelName();
        this.parentId = parentId;
        this.workflowName = workflowName;
        this.workflowRev = workflowRev;
        this.productionType = in.getProductionType();
        this.verInfo = in.getVerInfo();
        this.approval = in.getApproval();
        this.kanbanLabel = in.getKanbanLabel();
        this.productionNumber = in.getProductionNumber();
    }

    /**
     * コンストラクタ
     *
     * @param parentId カンバン階層ID
     * @param kanbanName カンバン名
     * @param kanbanSubname サブカンバン名
     * @param workflowId 工程順ID
     * @param workflowName 工程順名
     * @param startDatetime 開始予定日時
     * @param compDatetime 完了予定日時
     * @param updatePersonId 更新者(組織ID)
     * @param updateDatetime 更新日時
     * @param kanbanStatus カンバンステータス
     * @param interruptReasonId 中断理由ID
     * @param delayReasonId 遅延理由ID
     */
    public KanbanEntity(Long parentId, String kanbanName, String kanbanSubname, Long workflowId, String workflowName, Date startDatetime, Date compDatetime, Long updatePersonId, Date updateDatetime, KanbanStatusEnum kanbanStatus, Long interruptReasonId, Long delayReasonId) {
        this.parentId = parentId;
        this.kanbanName = kanbanName;
        this.kanbanSubname = kanbanSubname;
        this.workflowId = workflowId;
        this.workflowName = workflowName;
        this.startDatetime = startDatetime;
        this.compDatetime = compDatetime;
        this.updatePersonId = updatePersonId;
        this.updateDatetime = updateDatetime;
        this.kanbanStatus = kanbanStatus;
        this.interruptReasonId = interruptReasonId;
        this.delayReasonId = delayReasonId;
    }

    /**
     * コンストラクタ
     *
     * @param kanbanName カンバン名
     * @param workflowId 工程順ID
     * @param workflowName 工程順名
     * @param updatePersonId 更新者(組織ID)
     * @param updateDatetime 更新日時
     * @param kanbanStatus カンバンステータス
     */
    public KanbanEntity(String kanbanName, Long workflowId, String workflowName, Long updatePersonId, Date updateDatetime, KanbanStatusEnum kanbanStatus) {
        this.parentId = 0L;
        this.kanbanName = kanbanName;
        this.workflowId = workflowId;
        this.workflowName = workflowName;
        this.updatePersonId = updatePersonId;
        this.updateDatetime = updateDatetime;
        this.kanbanStatus = kanbanStatus;
    }

    /**
     * カンバンIDを取得する。
     *
     * @return カンバンID
     */
    public Long getKanbanId() {
        return this.kanbanId;
    }

    /**
     * カンバンIDを設定する。
     *
     * @param kanbanId カンバンID
     */
    public void setKanbanId(Long kanbanId) {
        this.kanbanId = kanbanId;
    }

    /**
     * カンバン名を取得する。
     *
     * @return カンバン名
     */
    public String getKanbanName() {
        return this.kanbanName;
    }

    /**
     * カンバン名を設定する。
     *
     * @param kanbanName カンバン名
     */
    public void setKanbanName(String kanbanName) {
        this.kanbanName = kanbanName;
    }

    /**
     * サブカンバン名を取得する。
     *
     * @return サブカンバン名
     */
    public String getKanbanSubname() {
        return this.kanbanSubname;
    }

    /**
     * サブカンバン名を設定する。
     *
     * @param kanbanSubname サブカンバン名
     */
    public void setKanbanSubname(String kanbanSubname) {
        this.kanbanSubname = kanbanSubname;
    }

    /**
     * 工程順IDを取得する。
     *
     * @return 工程順ID
     */
    public Long getWorkflowId() {
        return this.workflowId;
    }

    /**
     * 工程順IDを設定する。
     *
     * @param workflowId 工程順ID
     */
    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    /**
     * 先頭工程開始予定日時を取得する。
     *
     * @return 先頭工程開始予定日時
     */
    public Date getStartDatetime() {
        return this.startDatetime;
    }

    /**
     * 先頭工程開始予定日時を設定する。
     *
     * @param startDatetime 先頭工程開始予定日時
     */
    public void setStartDatetime(Date startDatetime) {
        this.startDatetime = startDatetime;
    }

    /**
     * 最終工程完了予定日時を取得する。
     *
     * @return 最終工程完了予定日時
     */
    public Date getCompDatetime() {
        return this.compDatetime;
    }

    /**
     * 最終工程完了予定日時を設定する。
     *
     * @param compDatetime 最終工程完了予定日時
     */
    public void setCompDatetime(Date compDatetime) {
        this.compDatetime = compDatetime;
    }

    /**
     * 更新者(組織ID)を取得する。
     *
     * @return 更新者(組織ID)
     */
    public Long getUpdatePersonId() {
        return this.updatePersonId;
    }

    /**
     * 更新者(組織ID)を設定する。
     *
     * @param updatePersonId 更新者(組織ID)
     */
    public void setUpdatePersonId(Long updatePersonId) {
        this.updatePersonId = updatePersonId;
    }

    /**
     * 更新日時を取得する。
     *
     * @return 更新日時
     */
    public Date getUpdateDatetime() {
        return this.updateDatetime;
    }

    /**
     * 更新日時を設定する。
     *
     * @param updateDatetime 更新日時
     */
    public void setUpdateDatetime(Date updateDatetime) {
        this.updateDatetime = updateDatetime;
    }

    /**
     * カンバンステータスを取得する。
     *
     * @return カンバンステータス
     */
    public KanbanStatusEnum getKanbanStatus() {
        return this.kanbanStatus;
    }

    /**
     * カンバンステータスを設定する。
     *
     * @param kanbanStatus カンバンステータス
     */
    public void setKanbanStatus(KanbanStatusEnum kanbanStatus) {
        this.kanbanStatus = kanbanStatus;
    }

    /**
     * 中断理由IDを取得する。
     *
     * @return 中断理由ID
     */
    public Long getInterruptReasonId() {
        return this.interruptReasonId;
    }

    /**
     * 中断理由IDを設定する。
     *
     * @param interruptReasonId 中断理由ID
     */
    public void setInterruptReasonId(Long interruptReasonId) {
        this.interruptReasonId = interruptReasonId;
    }

    /**
     * 遅延理由IDを取得する。
     *
     * @return 遅延理由ID
     */
    public Long getDelayReasonId() {
        return this.delayReasonId;
    }

    /**
     * 遅延理由IDを設定する。
     *
     * @param delayReasonId 遅延理由ID
     */
    public void setDelayReasonId(Long delayReasonId) {
        this.delayReasonId = delayReasonId;
    }

    /**
     * ロット数量を取得する。
     *
     * @return ロット数量
     */
    public Integer getLotQuantity() {
        return this.lotQuantity;
    }

    /**
     * ロット数量を設定する。
     *
     * @param lotQuantity ロット数量
     */
    public void setLotQuantity(Integer lotQuantity) {
        this.lotQuantity = lotQuantity;
    }

    /**
     * 開始日時(実績)を取得する。
     *
     * @return 開始日時(実績)
     */
    public Date getActualStartTime() {
        return this.actualStartTime;
    }

    /**
     * 開始日時(実績)を設定する。
     *
     * @param actualStartTime 開始日時(実績)
     */
    public void setActualStartTime(Date actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    /**
     * 完了日時(実績)を取得する。
     *
     * @return 完了日時(実績)
     */
    public Date getActualCompTime() {
        return this.actualCompTime;
    }

    /**
     * 完了日時(実績)を設定する。
     *
     * @param actualCompTime 完了日時(実績)
     */
    public void setActualCompTime(Date actualCompTime) {
        this.actualCompTime = actualCompTime;
    }

    /**
     * モデル名を取得する。
     *
     * @return モデル名
     */
    public String getModelName() {
        return this.modelName;
    }

    /**
     * モデル名を設定する。
     *
     * @param modelName モデル名
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * 生産タイプを取得する。
     *
     * @return 生産タイプ
     */
    public Integer getProductionType() {
        return this.productionType;
    }

    /**
     * 生産タイプを設定する。
     *
     * @param productionType 生産タイプ
     */
    public void setProductionType(Integer productionType) {
        this.productionType = productionType;
    }

    /**
     * 補修数を取得する。
     *
     * @return 補修数
     */
    public Integer getRepairNum() {
        return this.repairNum;
    }

    /**
     * 補修数を設定する。
     *
     * @param repairNum 補修数
     */
    public void setRepairNum(Integer repairNum) {
        this.repairNum = repairNum;
    }

    /**
     * 追加情報(JSON)を取得する。
     *
     * @return 追加情報(JSON)
     */
    public String getKanbanAddInfo() {
        return this.kanbanAddInfo;
    }

    /**
     * 追加情報(JSON)を設定する。
     *
     * @param kanbanAddInfo 追加情報(JSON)
     */
    public void setKanbanAddInfo(String kanbanAddInfo) {
        this.kanbanAddInfo = kanbanAddInfo;
    }

    /**
     * サービス情報(JSON)を取得する。
     *
     * @return サービス情報(JSON)
     */
    public String getServiceInfo() {
        return this.serviceInfo;
    }

    /**
     * サービス情報(JSON)を設定する。
     *
     * @param serviceInfo サービス情報(JSON)
     */
    public void setServiceInfo(String serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    /**
     * 排他用バーションを取得する。
     *
     * @return 排他用バーション
     */
    public Integer getVerInfo() {
        return this.verInfo;
    }

    /**
     * 排他用バーションを設定する。
     *
     * @param verInfo 排他用バーション
     */
    public void setVerInfo(Integer verInfo) {
        this.verInfo = verInfo;
    }

    /**
     * 製造番号を取得する。
     *
     * @return 製造番号
     */
    public String getProductionNumber() {
        return this.productionNumber;
    }

    /**
     * 製造番号を設定する。
     *
     * @param productionNumber 製造番号
     */
    public void setProductionNumber(String productionNumber) {
        this.productionNumber = productionNumber;
    }

    /**
     * 承認(JSON)を取得する。
     *
     * @return 承認(JSON)
     */
    public String getApproval() {
        return this.approval;
    }

    /**
     * 承認(JSON)を設定する。
     *
     * @param approval 承認(JSON)
     */
    public void setApproval(String approval) {
        this.approval = approval;
    }

    /**
     * ラベル情報を取得する。
     * 
     * @return ラベル情報(JSON)
     */
    public String getKanbanLabel() {
        return kanbanLabel;
    }

    /**
     * ラベル情報を設定する。
     * 
     * @param kanbanLabel ラベル情報(JSON) 
     */
    public void setKanbanLabel(String kanbanLabel) {
        this.kanbanLabel = kanbanLabel;
    }

    /**
     * カンバン階層IDを取得する。
     *
     * @return カンバン階層ID
     */
    public Long getParentId() {
        return this.parentId;
    }

    /**
     * カンバン階層IDを設定する。
     *
     * @param parentId カンバン階層ID
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    /**
     * 工程順名を取得する。
     *
     * @return 工程順名
     */
    public String getWorkflowName() {
        return this.workflowName;
    }

    /**
     * 工程順名を設定する。
     *
     * @param workflowName 工程順名
     */
    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    /**
     * 工程順の版数を取得する。
     *
     * @return 工程順の版数
     */
    public Integer getWorkflowRev() {
        return this.workflowRev;
    }

    /**
     * 工程順の版数を設定する。
     *
     * @param workflowRev 工程順の版数
     */
    public void setWorkflowRev(Integer workflowRev) {
        this.workflowRev = workflowRev;
    }

    /**
     * 工程順情報を取得する。
     *
     * @return 工程順情報
     */
    public WorkflowEntity getWorkflow() {
        return this.workflow;
    }

    /**
     * 工程順情報を設定する。
     *
     * @param workflow 工程順情報
     */
    public void setWorkflow(WorkflowEntity workflow) {
        this.workflow = workflow;
    }

    /**
     * 工程カンバン数を取得する。
     *
     * @return 工程カンバン数
     */
    public long getWorkKanbanCount() {
        if (Objects.nonNull(this.workKanbanCollection)) {
            return this.workKanbanCollection.size();
        }
        return this.workKanbanCount;
    }

    /**
     * 工程カンバン数を設定する。
     *
     * @param workKanbanCount 工程カンバン数
     */
    public void setWorkKanbanCount(long workKanbanCount) {
        this.workKanbanCount = workKanbanCount;
    }

    /**
     * 工程カンバン情報一覧を取得する。
     *
     * @return 工程カンバン情報一覧
     */
    public List<WorkKanbanEntity> getWorkKanbanCollection() {
        return Objects.isNull(this.workKanbanCollection) ? new ArrayList<>() : this.workKanbanCollection;
    }

    /**
     * 工程カンバン情報一覧を設定する。
     *
     * @param workKanbanCollection 工程カンバン情報一覧
     */
    public void setWorkKanbanCollection(List<WorkKanbanEntity> workKanbanCollection) {
        this.workKanbanCollection = workKanbanCollection;
    }

    /**
     * 追加工程カンバン数を取得する。
     *
     * @return 追加工程カンバン数
     */
    public long getSeparateWorkKanbanCount() {
        if (Objects.nonNull(this.separateworkKanbanCollection)) {
            return this.separateworkKanbanCollection.size();
        }
        return 0L;
    }

//    /**
//     * 追加工程カンバン数を設定する。
//     *
//     * @param separateWorkKanbanCount 追加工程カンバン数
//     */
//    public void setSeparateWorkKanbanCount(long separateWorkKanbanCount) {
//        this.separateWorkKanbanCount = separateWorkKanbanCount;
//    }

    /**
     * 追加工程カンバン情報一覧を取得する。
     *
     * @return 追加工程カンバン情報一覧
     */
    public List<WorkKanbanEntity> getSeparateworkKanbanCollection() {
        return Objects.isNull(this.separateworkKanbanCollection) ? new ArrayList<>() : this.separateworkKanbanCollection;
    }

    /**
     * 追加工程カンバン情報一覧を設定する。
     *
     * @param separateworkKanbanCollection 追加工程カンバン情報一覧
     */
    public void setSeparateworkKanbanCollection(List<WorkKanbanEntity> separateworkKanbanCollection) {
        this.separateworkKanbanCollection = separateworkKanbanCollection;
    }

    /**
     * 工程実績情報一覧を取得する。
     *
     * @return 工程実績情報一覧
     */
    public List<ActualResultEntity> getActualResultCollection() {
        return this.actualResultCollection;
    }

    /**
     * 工程実績情報一覧を設定する。
     *
     * @param actualResultCollection 工程実績情報一覧
     */
    public void setActualResultCollection(List<ActualResultEntity> actualResultCollection) {
        this.actualResultCollection = actualResultCollection;
    }

    /**
     * 製品情報一覧を取得する。
     *
     * @return 製品情報一覧
     */
    public List<ProductEntity> getProducts() {
        return this.products;
    }

    /**
     * 製品情報一覧を設定する。
     *
     * @param products 製品情報一覧
     */
    public void setProducts(List<ProductEntity> products) {
        this.products = products;
    }

    /**
     * 帳票テンプレートパス(JSON)を取得する。
     *
     * @return 帳票テンプレートパス(JSON)
     */
    public String getLedgerPath() {
        return this.ledgerPath;
    }

    /**
     * 帳票テンプレートパス(JSON)を設定する。
     *
     * @param ledgerPath 帳票テンプレートパス(JSON)
     */
    public void setLedgerPath(String ledgerPath) {
        this.ledgerPath = ledgerPath;
    }

    /**
     * 工程の開始時間を取得する
     * 
     * @return 工程の開始時間
     */
    public Date getWorkStartDateTime() {
        return workStartDateTime;
    }

    /**
     * 工程の開始時間を設定(SQLの戻り値でのみ設定)
     * 
     * @param workStartDateTime 工程の開始時間
     */
    public void setWorkStartDateTime(Date workStartDateTime) {
        this.workStartDateTime = workStartDateTime;
    }

    /**
     * 不良数を取得する。
     * 
     * @return 不良数 
     */
    public Integer getDefectNum() {
        return defectNum;
    }

    /**
     * 不良数を設定する。
     * 
     * @param defectNum 不良数 
     */
    public void setDefectNum(Integer defectNum) {
        this.defectNum = defectNum;
    }

    /**
     * 完成数を取得する。
     * 
     * @return 完成数
     */
    public Integer getCompNum() {
        return compNum;
    }

    /**
     * 完成数を設定する。
     * 
     * @param compNum 完成数
     */
    public void setCompNum(Integer compNum) {
        this.compNum = compNum;
    }

    /**
     * 標準サイクルタイムを取得する。
     * 
     * @return 標準サイクルタイム
     */
    public Integer getCycleTime() {
        return cycleTime;
    }

    /**
     * 標準サイクルタイムを設定する。
     * 
     * @param cycleTime 標準サイクルタイム
     */
    public void setCycleTime(Integer cycleTime) {
        this.cycleTime = cycleTime;
    }
    
    /**
     * ハッシュコードを取得する。
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.kanbanId);
        return hash;
    }

    /**
     * オブジェクトを比較する。
     * 
     * @param obj オブジェクト
     * @return true: 等しい、false: 異なる
     */
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
        final KanbanEntity other = (KanbanEntity) obj;
        return Objects.equals(this.kanbanId, other.kanbanId);
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("KanbanEntity{")
                .append("kanbanId=").append(this.kanbanId)
                .append(", kanbanName=").append(this.kanbanName)
                .append(", kanbanSubname=").append(this.kanbanSubname)
                .append(", workflowId=").append(this.workflowId)
                .append(", startDatetime=").append(this.startDatetime)
                .append(", compDatetime=").append(this.compDatetime)
                .append(", updatePersonId=").append(this.updatePersonId)
                .append(", updateDatetime=").append(this.updateDatetime)
                .append(", kanbanStatus=").append(this.kanbanStatus)
                .append(", interruptReasonId=").append(this.interruptReasonId)
                .append(", delayReasonId=").append(this.delayReasonId)
                .append(", lotQuantity=").append(this.lotQuantity)
                .append(", actualStartTime=").append(this.actualStartTime)
                .append(", actualCompTime=").append(this.actualCompTime)
                .append(", modelName=").append(this.modelName)
                .append(", productionType=").append(this.productionType)
                .append(", verInfo=").append(this.verInfo)
                .append(", productionNumber=").append(this.productionNumber)
                .append(", kanbanLabel=").append(this.kanbanLabel)
                .append(", parentId=").append(this.parentId)
                .append(", workflowName=").append(this.workflowName)
                .append(", workflowRev=").append(this.workflowRev)
                .append(", defectNum=").append(this.defectNum)
                .append(", compNum=").append(this.compNum)
                .append(", cycleTime=").append(this.cycleTime)
                .append("}")
                .toString();
    }
}
