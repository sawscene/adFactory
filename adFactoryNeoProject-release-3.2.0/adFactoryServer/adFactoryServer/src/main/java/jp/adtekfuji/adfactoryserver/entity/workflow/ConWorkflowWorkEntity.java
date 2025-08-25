/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.workflow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import adtekfuji.utility.StringUtils;
import jp.adtekfuji.adFactory.entity.schedule.ScheduleConditionInfoEntity;
import jp.adtekfuji.adFactory.enumerate.WorkKbnEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;

/**
 * 工程順・工程関連付け情報
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "con_workflow_work")
@XmlRootElement(name = "conWorkflowWork")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedNativeQueries({
        // 工程区分・工程ID・工程順IDを指定して、工程順・工程関連付け情報一覧を取得する。
        @NamedNativeQuery(name = "ConWorkflowWorkEntity.findByKbnAndWorkIdAndWorkflowId", query="SELECT c.* FROM con_workflow_work c WHERE c.work_kbn = ?1 AND c.work_id=?2 AND c.workflow_id=?3", resultClass = ConWorkflowWorkEntity.class),
})
@NamedQueries({
    // 工程区分・工程順IDを指定して、工程順・工程関連付け情報一覧を取得する。
    @NamedQuery(name = "ConWorkflowWorkEntity.findByKbnAndWorkflowId", query = "SELECT c FROM ConWorkflowWorkEntity c WHERE c.workKbn = :workKbn AND c.workflowId = :workflowId ORDER BY c.workKbn, c.workflowOrder"),

    // 工程区分・工程IDを指定して、工程順・工程関連付け情報一覧を取得する。
    @NamedQuery(name = "ConWorkflowWorkEntity.findByKbnAndWorkId", query = "SELECT c FROM ConWorkflowWorkEntity c WHERE c.workKbn = :workKbn AND c.workId = :workId ORDER BY c.workflowOrder"),
    // 工程区分・工程IDを指定して、工程順ID一覧を取得する。
    @NamedQuery(name = "ConWorkflowWorkEntity.findWorkflowIdByKbnAndWorkId", query = "SELECT c.workflowId FROM ConWorkflowWorkEntity c WHERE c.workKbn = :workKbn AND c.workId = :workId ORDER BY c.workflowOrder"),

    // 工程順IDを指定して、工程順・工程関連付け情報を削除する。
    @NamedQuery(name = "ConWorkflowWorkEntity.removeByWorkflowId", query = "DELETE FROM ConWorkflowWorkEntity c WHERE c.workflowId = :workflowId"),
    // 工程順IDを指定して、工程ID一覧を取得する。
    @NamedQuery(name = "ConWorkflowWorkEntity.findWorkIdByWorkflowId", query = "SELECT c.workId FROM ConWorkflowWorkEntity c WHERE c.workflowId = :workflowId ORDER BY c.workKbn, c.workflowOrder"),

    // 工程順ID・工程IDと新しい工程IDを指定して、工程順・工程関連付け情報の工程IDを更新する。
    @NamedQuery(name = "ConWorkflowWorkEntity.updateWorkId", query = "UPDATE ConWorkflowWorkEntity c SET c.workId = :newWorkId WHERE c.workflowId = :workflowId AND c.workId = :oldWorkId"),
})
public class ConWorkflowWorkEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    //@NotNull
    @Column(name = "work_kbn")
    private WorkKbnEnum workKbn;// 工程区分

    @Id
    @Basic(optional = false)
    //@NotNull
    @Column(name = "workflow_id")
    @XmlElement(name = "fkWorkflowId")
    private Long workflowId;// 工程順ID

    @Id
    @Basic(optional = false)
    //@NotNull
    @Column(name = "work_id")
    @XmlElement(name = "fkWorkId")
    private Long workId;// 工程ID

    @Basic(optional = false)
    //@NotNull
    @Column(name = "skip_flag")
    private Boolean skipFlag;// スキップフラグ

    @Basic(optional = false)
    //@NotNull
    @Column(name = "workflow_order")
    private Integer workflowOrder;// 表示順

    @Column(name = "standard_start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date standardStartTime;// 基準開始時間

    @Column(name = "standard_end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date standardEndTime;// 基準完了時間

    @Transient
    private String workName;// 工程名
    @Transient
    private Integer workRev; // 工程の版数

    @XmlElementWrapper(name = "equipments")
    @XmlElement(name = "equipment")
    @Transient
    private List<Long> equipmentCollection = null;// 設備ID一覧

    @XmlElementWrapper(name = "organizations")
    @XmlElement(name = "organization")
    @Transient
    private List<Long> organizationCollection = null;// 組織ID一覧

    @XmlElement(name = "schedule")
    @Column(name = "schedule_info")
    private String schedule;


    /**
     * コンストラクタ
     */
    public ConWorkflowWorkEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param workKbn
     * @param in 
     */
    public ConWorkflowWorkEntity(WorkKbnEnum workKbn, ConWorkflowWorkEntity in) {
        this.workKbn = workKbn;
        this.workflowId = in.workflowId;
        this.workId = in.workId;
        this.workName = in.workName;
        this.workRev = in.workRev;
        this.skipFlag = in.skipFlag;
        this.workflowOrder = in.workflowOrder;
        this.standardStartTime = in.standardStartTime;
        this.standardEndTime = in.standardEndTime;
        this.equipmentCollection = in.getEquipmentCollection();
        this.organizationCollection = in.getOrganizationCollection();
    }

    /**
     * コンストラクタ
     *
     * @param workKbn 工程区分
     * @param workflowId 工程順ID
     * @param workId 工程ID
     * @param skipFlag スキップフラグ
     * @param workflowOrder 表示順
     * @param standardStartTime 基準開始時間
     * @param standardEndTime 基準完了時間
     */
    public ConWorkflowWorkEntity(WorkKbnEnum workKbn, Long workflowId, Long workId, Boolean skipFlag, Integer workflowOrder, Date standardStartTime, Date standardEndTime) {
        this.workKbn = workKbn;
        this.workflowId = workflowId;
        this.workId = workId;
        this.skipFlag = skipFlag;
        this.workflowOrder = workflowOrder;
        this.standardStartTime = standardStartTime;
        this.standardEndTime = standardEndTime;
    }

    /**
     * 工程区分を取得する。
     *
     * @return 工程区分
     */
    public WorkKbnEnum getWorkKbn() {
        return this.workKbn;
    }

    /**
     * 工程区分を設定する。
     *
     * @param workKbn 工程区分
     */
    public void setWorkKbn(WorkKbnEnum workKbn) {
        this.workKbn = workKbn;
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
     * 工程IDを取得する。
     *
     * @return 工程ID
     */
    public Long getWorkId() {
        return this.workId;
    }

    /**
     * 工程IDを設定する。
     *
     * @param workId 工程ID
     */
    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    /**
     * スキップフラグを取得する。
     *
     * @return スキップフラグ
     */
    public Boolean getSkipFlag() {
        return this.skipFlag;
    }

    /**
     * スキップフラグを設定する。
     *
     * @param skipFlag スキップフラグ
     */
    public void setSkipFlag(Boolean skipFlag) {
        this.skipFlag = skipFlag;
    }

    /**
     * 表示順を取得する。
     *
     * @return 表示順
     */
    public Integer getWorkflowOrder() {
        return this.workflowOrder;
    }

    /**
     * 表示順を設定する。
     *
     * @param workflowOrder 表示順
     */
    public void setWorkflowOrder(Integer workflowOrder) {
        this.workflowOrder = workflowOrder;
    }

    /**
     * 基準開始時間を取得する。
     *
     * @return 基準開始時間
     */
    public Date getStandardStartTime() {
        return this.standardStartTime;
    }

    /**
     * 基準開始時間を設定する。
     *
     * @param standardStartTime 基準開始時間
     */
    public void setStandardStartTime(Date standardStartTime) {
        this.standardStartTime = standardStartTime;
    }

    /**
     * 基準完了時間を取得する。
     *
     * @return 基準完了時間
     */
    public Date getStandardEndTime() {
        return this.standardEndTime;
    }

    /**
     * 基準完了時間を設定する。
     *
     * @param standardEndTime 基準完了時間
     */
    public void setStandardEndTime(Date standardEndTime) {
        this.standardEndTime = standardEndTime;
    }

    /**
     * 工程名を取得する。
     *
     * @return 工程名
     */
    public String getWorkName() {
        return this.workName;
    }

    /**
     * 工程名を設定する。
     *
     * @param workName 工程名
     */
    public void setWorkName(String workName) {
        this.workName = workName;
    }

    /**
     * 工程の版数を取得する。
     *
     * @return 工程の版数
     */
    public Integer getWorkRev() {
        return this.workRev;
    }

    /**
     * 工程の版数を設定する。
     *
     * @param workRev 工程の版数
     */
    public void setWorkRev(Integer workRev) {
        this.workRev = workRev;
    }

    /**
     * 設備ID一覧を取得する。
     *
     * @return 設備ID一覧
     */
    public List<Long> getEquipmentCollection() {
        return this.equipmentCollection;
    }

    /**
     * 設備ID一覧を設定する。
     *
     * @param equipmentCollection 設備ID一覧
     */
    public void setEquipmentCollection(List<Long> equipmentCollection) {
        this.equipmentCollection = equipmentCollection;
    }

    /**
     * 組織ID一覧を取得する。
     *
     * @return 組織ID一覧
     */
    public List<Long> getOrganizationCollection() {
        return this.organizationCollection;
    }

    /**
     * 組織ID一覧を設定する。
     *
     * @param organizationCollection 組織ID一覧
     */
    public void setOrganizationCollection(List<Long> organizationCollection) {
        this.organizationCollection = organizationCollection;
    }

    /**
     * スケジュールを取得
     * @return スケジュール
     */
    public List<ScheduleConditionInfoEntity> getSchedule()
    {
        if (StringUtils.isEmpty(this.schedule)) {
            return new ArrayList<>();
        }
        return JsonUtils.jsonToObjects(this.schedule, ScheduleConditionInfoEntity[].class);
    }

    /**
     * スケジュールを設定
     * @param schedule スケジュール
     */
    public void setSchedule(List<ScheduleConditionInfoEntity> schedule) {
        if (Objects.isNull(schedule) || schedule.isEmpty()) {
            this.schedule = null;
            return;
        }

        this.schedule = JsonUtils.objectsToJson(schedule);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.workKbn);
        hash = 97 * hash + Objects.hashCode(this.workflowId);
        hash = 97 * hash + Objects.hashCode(this.workId);
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
        final ConWorkflowWorkEntity other = (ConWorkflowWorkEntity) obj;
        if (this.workKbn != other.workKbn) {
            return false;
        }
        if (!Objects.equals(this.workflowId, other.workflowId)) {
            return false;
        }
        if (!Objects.equals(this.workId, other.workId)) {
            return false;
        }
        if (!Objects.equals(this.schedule, other.schedule)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ConWorkflowWorkEntity{")
                .append("workKbn=").append(this.workKbn)
                .append(", workflowId=").append(this.workflowId)
                .append(", workId=").append(this.workId)
                .append(", workName=").append(this.workName)
                .append(", workRev=").append(this.workRev)
                .append(", skipFlag=").append(this.skipFlag)
                .append(", workflowOrder=").append(this.workflowOrder)
                .append(", standardStartTime=").append(this.standardStartTime)
                .append(", standardEndTime=").append(this.standardEndTime)
                .append(", schedule=").append(this.schedule)
                .append("}")
                .toString();
    }
}
