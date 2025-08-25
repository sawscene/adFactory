/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.actual;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

/**
 * 作業者別計画実績エンティティクラス
 *
 * @author s-heya
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "workRecord")
@Entity
@Table(name = "view_work_history")
@NamedQueries({
    @NamedQuery(name = "WorkRecordEntity.countActualByKanbanId", query = "SELECT COUNT(v.kanbanId) FROM WorkRecordEntity v WHERE v.kanbanId IN :kanbanIds AND ((v.actualStartTime >= :fromDate AND (v.actualEndTime <= :toDate OR v.actualStartTime <= :toDate)) OR (v.actualEndTime <= :toDate AND v.actualEndTime >= :fromDate))"),
    @NamedQuery(name = "WorkRecordEntity.findActualByKanbanId", query = "SELECT v FROM WorkRecordEntity v WHERE v.kanbanId IN :kanbanIds AND ((v.actualStartTime >= :fromDate AND (v.actualEndTime <= :toDate OR v.actualStartTime <= :toDate)) OR (v.actualEndTime <= :toDate AND v.actualEndTime >= :fromDate)) ORDER BY v.kanbanId, v.workKanbanId, v.actualId"),
    @NamedQuery(name = "WorkRecordEntity.countActualByOrganizationId", query = "SELECT COUNT(v.kanbanId) FROM WorkRecordEntity v WHERE v.organizationId IN :organizationIds AND ((v.actualStartTime >= :fromDate AND (v.actualEndTime <= :toDate OR v.actualStartTime <= :toDate)) OR (v.actualEndTime <= :toDate AND v.actualEndTime >= :fromDate))"),
    @NamedQuery(name = "WorkRecordEntity.findActualByOrganizationId", query = "SELECT v FROM WorkRecordEntity v WHERE v.organizationId IN :organizationIds AND ((v.actualStartTime >= :fromDate AND (v.actualEndTime <= :toDate OR v.actualStartTime <= :toDate)) OR (v.actualEndTime <= :toDate AND v.actualEndTime >= :fromDate)) ORDER BY v.organizationId, v.actualId"),
    @NamedQuery(name = "WorkRecordEntity.countPlanByKanbanId", query = "SELECT COUNT(v.kanbanId) FROM WorkRecordEntity v WHERE v.kanbanId IN :kanbanIds AND ((v.planStartTime >= :fromDate AND (v.planEndTime <= :toDate OR v.planStartTime <= :toDate)) OR (v.planEndTime <= :toDate AND v.planEndTime >= :fromDate))"),
    @NamedQuery(name = "WorkRecordEntity.findPlanByKanbanId", query = "SELECT v FROM WorkRecordEntity v WHERE v.kanbanId IN :kanbanIds AND ((v.planStartTime >= :fromDate AND (v.planEndTime <= :toDate OR v.planStartTime <= :toDate)) OR (v.planEndTime <= :toDate AND v.planEndTime >= :fromDate)) ORDER BY v.kanbanId, v.workKanbanId, v.actualId"),
    @NamedQuery(name = "WorkRecordEntity.countPlanByOrganizationId", query = "SELECT COUNT(v.kanbanId) FROM WorkRecordEntity v WHERE v.organizationId IN :organizationIds AND ((v.planStartTime >= :fromDate AND (v.planEndTime <= :toDate OR v.planStartTime <= :toDate)) OR (v.planEndTime <= :toDate AND v.planEndTime >= :fromDate))"),
    @NamedQuery(name = "WorkRecordEntity.findPlanByOrganizationId", query = "SELECT v FROM WorkRecordEntity v WHERE v.organizationId IN :organizationIds AND ((v.planStartTime >= :fromDate AND (v.planEndTime <= :toDate OR v.planStartTime <= :toDate)) OR (v.planEndTime <= :toDate AND v.planEndTime >= :fromDate)) ORDER BY v.organizationId, v.actualId"),
})
public class WorkRecordEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "actual_id")
    private Long actualId;
    @Id
    @Column(name = "kanban_id")
    private Long kanbanId;
    @Id
    @Column(name = "work_kanban_id")
    private Long workKanbanId;
    @Column(name = "work_id")
    private Long workId;
    @Column(name = "organization_id")
    private Long organizationId;
    @Column(name = "kanban_name")
    private String kanbanName;
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    @NotNull
    @Column(name = "kanban_status")
    private KanbanStatusEnum kanbanStatus;
    @Column(name = "workflow_name")
    private String workflowName;
    @Column(name = "work_name")
    private String workName;
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    @NotNull
    @Column(name = "work_kanban_status")
    private KanbanStatusEnum workKanbanStatus;
    @Column(name = "organization_name")
    private String organizationName;
    @Column(name = "plan_start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date planStartTime;
    @Column(name = "plan_end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date planEndTime;
    @Column(name = "actual_start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualStartTime;
    @Column(name = "actual_end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualEndTime;
    // 工程の文字色
    @Column(name = "font_color")
    private String fontColor;
    // 工程の背景色
    @Column(name = "back_color")
    private String backColor;
    // 作業累計時間
    @Column(name = "sum_times")
    private Long sumTimes;
    // タクトタイム
    @Column(name = "takt_time")
    private Integer taktTime;

    public Long getActualId() {
        return actualId;
    }

    public void setActualId(Long actualId) {
        this.actualId = actualId;
    }

    public Long getKanbanId() {
        return this.kanbanId;
    }

    public void setKanbanId(Long kanbanId) {
        this.kanbanId = kanbanId;
    }

    public Long getWorkKanbanId() {
        return this.workKanbanId;
    }

    public void setWorkKanbanId(Long workKanbanId) {
        this.workKanbanId = workKanbanId;
    }

    public Long getWorkId() {
        return workId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getKanbanName() {
        return this.kanbanName;
    }

    public void setKanbanName(String kanbanName) {
        this.kanbanName = kanbanName;
    }

    public KanbanStatusEnum getKanbanStatus() {
        return this.kanbanStatus;
    }

    public void setKanbanStatus(KanbanStatusEnum kanbanStatus) {
        this.kanbanStatus = kanbanStatus;
    }

    public String getWorkflowName() {
        return this.workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public String getWorkName() {
        return this.workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public KanbanStatusEnum getWorkKanbanStatus() {
        return this.workKanbanStatus;
    }

    public void setWorkKanbanStatus(KanbanStatusEnum workKanbanStatus) {
        this.workKanbanStatus = workKanbanStatus;
    }

    public String getOrganizationName() {
        return this.organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Date getPlanStartTime() {
        return this.planStartTime;
    }

    public void setPlanStartTime(Date planStartTime) {
        this.planStartTime = planStartTime;
    }

    public Date getPlanEndTime() {
        return this.planEndTime;
    }

    public void setPlanEndTime(Date planEndTime) {
        this.planEndTime = planEndTime;
    }

    public Date getActualStartTime() {
        return this.actualStartTime;
    }

    public void setActualStartTime(Date actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public Date getActualEndTime() {
        return this.actualEndTime;
    }

    public void setActualEndTime(Date actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getBackColor() {
        return backColor;
    }

    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }

    public Long getSumTimes() {
        return sumTimes;
    }

    public void setSumTimes(Long sumTimes) {
        this.sumTimes = sumTimes;
    }

    public Integer getTaktTime() {
        return taktTime;
    }

    public void setTaktTime(Integer taktTime) {
        this.taktTime = taktTime;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.actualId);
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
        final WorkRecordEntity other = (WorkRecordEntity) obj;
        return Objects.equals(this.actualId, other.actualId);
    }



    @Override
    public String toString() {
        return "OrganizationTopicEntity{" + "actualId=" + actualId + "kanbanId=" + kanbanId + ", workKanbanId=" + workKanbanId + ", organizationId=" + organizationId + ", workId=" + workId + ", kanbanName=" + kanbanName +
                ", kanbanStatus=" + kanbanStatus + ", workflowName=" + workflowName + ", workName=" + workName + ", workKanbanStatus=" + workKanbanStatus +
                ", organizationName=" + organizationName + ", planStartTime=" + planStartTime + ", planEndTime=" + planEndTime +
                ", actualStartTime=" + actualStartTime + ", actualEndTime=" + actualEndTime + ", fontColor=" + fontColor + ", backColor=" + backColor + ", sumTimes=" + sumTimes +
                ", taktTime=" + taktTime + '}';
    }
}
