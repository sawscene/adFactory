/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.agenda;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

/**
 * 計画実績エンティティクラス
 *
 * @author s-heya
 */
@MappedSuperclass
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "kanbanTopic")
public class AbstractTopicEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "kanban_id")
    private Long kanbanId;
    @Column(name = "work_kanban_id")
    private Long workKanbanId;
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
    @Column(name = "equipment_name")
    private String equipmentName;
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
    private Integer sumTimes;
    // タクトタイム
    @Column(name = "takt_time")
    private Integer taktTime;
    // 工程ID
    @Column(name = "work_id")
    private Long workId;
    // 親ID
    @Transient
    private Long parentId;

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

    public String getEquipmentName() {
        return this.equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
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

    public Integer getSumTimes() {
        return sumTimes;
    }

    public void setSumTimes(Integer sumTimes) {
        this.sumTimes = sumTimes;
    }

    public Integer getTaktTime() {
        return taktTime;
    }

    public void setTaktTime(Integer taktTime) {
        this.taktTime = taktTime;
    }

    public Long getWorkId() {
        return workId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.kanbanId);
        hash = 59 * hash + Objects.hashCode(this.workKanbanId);
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
        final AbstractTopicEntity other = (AbstractTopicEntity) obj;
        if (!Objects.equals(this.kanbanId, other.kanbanId)) {
            return false;
        }
        return Objects.equals(this.workKanbanId, other.workKanbanId);
    }

    @Override
    public String toString() {
        return "AbstractTopicEntity{" + "kanbanId=" + kanbanId + ", workKanbanId=" + workKanbanId + ", organizationId=" + organizationId + ", kanbanName=" + kanbanName +
                ", kanbanStatus=" + kanbanStatus + ", workflowName=" + workflowName + ", workName=" + workName + ", workKanbanStatus=" + workKanbanStatus +
                ", equipmentName=" + equipmentName + ", organizationName=" + organizationName + ", planStartTime=" + planStartTime + ", planEndTime=" + planEndTime +
                ", actualStartTime=" + actualStartTime + ", actualEndTime=" + actualEndTime + ", fontColor=" + fontColor + ", backColor=" + backColor + ", sumTimes=" + sumTimes +
                ", taktTime=" + taktTime + ", workId=" + workId + ", parentId=" + parentId + '}';
    }
}
