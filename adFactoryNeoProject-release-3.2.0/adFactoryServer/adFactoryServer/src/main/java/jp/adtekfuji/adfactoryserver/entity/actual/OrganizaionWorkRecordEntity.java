/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.actual;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 作業者別作業時間履歴エンティティクラス
 *
 * @author s-heya
 */
@Entity
@Table(name = "view_org_work_history")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "OrganizaionWorkRecordEntity.findByOrganizationId", query = "SELECT o FROM OrganizaionWorkRecordEntity o WHERE o.organizationId = :organizationId"),
})
public class OrganizaionWorkRecordEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "actual_id")
    private BigInteger actualId;
    @Column(name = "kanban_id")
    private BigInteger kanbanId;
    @Column(name = "workflow_id")
    private BigInteger workflowId;
    @Column(name = "work_kanban_id")
    private BigInteger workKanbanId;
    @Column(name = "work_id")
    private BigInteger workId;
    @Column(name = "organization_id")
    private BigInteger organizationId;
    @Size(max = 256)
    @Column(name = "kanban_name")
    private String kanbanName;
    @Size(max = 128)
    @Column(name = "kanban_status")
    private String kanbanStatus;
    @Size(max = 256)
    @Column(name = "workflow_name")
    private String workflowName;
    @Size(max = 256)
    @Column(name = "work_name")
    private String workName;
    @Size(max = 128)
    @Column(name = "work_kanban_status")
    private String workKanbanStatus;
    @Size(max = 256)
    @Column(name = "organization_name")
    private String organizationName;
    @Column(name = "plan_start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date planStartTime;
    @Column(name = "plan_end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date planEndTime;
    @Column(name = "work_start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date workStartTime;
    @Column(name = "work_end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date workEndTime;
    @Column(name = "actual_start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualStartTime;
    @Column(name = "actual_end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualEndTime;
    @Size(max = 128)
    @Column(name = "font_color")
    private String fontColor;
    @Size(max = 128)
    @Column(name = "back_color")
    private String backColor;
    @Column(name = "sum_times")
    private Long sumTimes;
    @Column(name = "takt_time")
    private Integer taktTime;
    @Column(name = "work_time")
    private Integer workTime;

    public OrganizaionWorkRecordEntity() {
    }

    public BigInteger getActualId() {
        return actualId;
    }

    public void setActualId(BigInteger actualId) {
        this.actualId = actualId;
    }

    public BigInteger getKanbanId() {
        return kanbanId;
    }

    public void setKanbanId(BigInteger kanbanId) {
        this.kanbanId = kanbanId;
    }

    public BigInteger getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(BigInteger workflowId) {
        this.workflowId = workflowId;
    }

    public BigInteger getWorkKanbanId() {
        return workKanbanId;
    }

    public void setWorkKanbanId(BigInteger workKanbanId) {
        this.workKanbanId = workKanbanId;
    }

    public BigInteger getWorkId() {
        return workId;
    }

    public void setWorkId(BigInteger workId) {
        this.workId = workId;
    }

    public BigInteger getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(BigInteger organizationId) {
        this.organizationId = organizationId;
    }

    public String getKanbanName() {
        return kanbanName;
    }

    public void setKanbanName(String kanbanName) {
        this.kanbanName = kanbanName;
    }

    public String getKanbanStatus() {
        return kanbanStatus;
    }

    public void setKanbanStatus(String kanbanStatus) {
        this.kanbanStatus = kanbanStatus;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public String getWorkKanbanStatus() {
        return workKanbanStatus;
    }

    public void setWorkKanbanStatus(String workKanbanStatus) {
        this.workKanbanStatus = workKanbanStatus;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Date getPlanStartTime() {
        return planStartTime;
    }

    public void setPlanStartTime(Date planStartTime) {
        this.planStartTime = planStartTime;
    }

    public Date getPlanEndTime() {
        return planEndTime;
    }

    public void setPlanEndTime(Date planEndTime) {
        this.planEndTime = planEndTime;
    }

    public Date getWorkStartTime() {
        return workStartTime;
    }

    public void setWorkStartTime(Date workStartTime) {
        this.workStartTime = workStartTime;
    }

    public Date getWorkEndTime() {
        return workEndTime;
    }

    public void setWorkEndTime(Date workEndTime) {
        this.workEndTime = workEndTime;
    }

    public Date getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(Date actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public Date getActualEndTime() {
        return actualEndTime;
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

    public Integer getWorkTime() {
        return workTime;
    }

    public void setWorkTime(Integer workTime) {
        this.workTime = workTime;
    }

}
