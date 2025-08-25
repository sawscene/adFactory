/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.chart;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 工程集計データ
 *
 * @author s-heya
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {
    "work_id",
    "work_name",
    "avg_worktime",
    "kanban_count",
    "work_rev"
}))
@NamedNativeQueries({
        @NamedNativeQuery(name = "WorkSummaryEntity.calcWork",
                query = "SELECT w.work_id work_id, mw.work_name work_name, w.avg_worktime avg_worktime, w.kanban_count kanban_count, w.stdev_worktime stddev_worktime, mw.work_rev " +
                        "FROM (SELECT twk.work_id work_id, ROUND(AVG(twk.sum_times),2) avg_worktime, COUNT(k.kanban_id) kanban_count, ROUND(STDDEV_POP(twk.sum_times),2) stdev_worktime " +
                        "FROM (SELECT tk.kanban_id FROM trn_kanban tk WHERE tk.kanban_status = 'COMPLETION' AND tk.actual_comp_datetime BETWEEN ?1 AND ?2 AND tk.workflow_id = ?3) k " +
                        "JOIN trn_work_kanban twk ON twk.kanban_id = k.kanban_id " +
                        "GROUP BY twk.work_id) w " +
                        "JOIN mst_work mw ON mw.work_id=w.work_id " +
                        "ORDER BY mw.work_name",
                resultClass = WorkSummaryEntity.class)
})
@XmlRootElement(name = "workSummary")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkSummaryEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "work_id")
    private Long workId;

    @Column(name = "work_name")
    private String workName;

    @Column(name = "avg_worktime")
    private Double avgWorkTime;

    @Column(name = "kanban_count")
    private Integer kanbanCount;

    @Column(name = "stddev_worktime")
    private Double standardDeviation;

    @Column(name = "work_rev")
    private Integer workRev;

    /**
     * コンストラクタ
     */
    public WorkSummaryEntity() {
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
     * 平均作業時間を取得する。
     *
     * @return 平均作業時間
     */
    public Double getAvgWorkTime() {
        return this.avgWorkTime;
    }

    /**
     * 平均作業時間を設定する。
     *
     * @param avgWorkTime 平均作業時間
     */
    public void setAvgWorkTime(Double avgWorkTime) {
        this.avgWorkTime = avgWorkTime;
    }

    /**
     * カンバン数を取得する。
     *
     * @return カンバン数
     */
    public Integer getKanbanCount() {
        return this.kanbanCount;
    }

    /**
     * カンバン数を設定する。
     *
     * @param kanbanCount カンバン数
     */
    public void setKanbanCount(Integer kanbanCount) {
        this.kanbanCount = kanbanCount;
    }
    
    /**
     * 標準偏差を取得する。
     * 
     * @return 標準偏差
     */
    public Double getStandardDeviation() {
        return this.standardDeviation;
    }

    /**
     * 標準偏差を設定する。
     *
     * @param standardDeviation 標準偏差
     */
    public void setStandardDeviation(Double standardDeviation) {
        this.standardDeviation = standardDeviation;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (workName != null ? workName.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorkSummaryEntity)) {
            return false;
        }
        WorkSummaryEntity other = (WorkSummaryEntity) object;
        if ((this.workName == null && other.workName != null) || (this.workName != null && !this.workName.equals(other.workName))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("WorkSummaryEntity{")
                .append("workId=").append(this.workId)
                .append(", workName=").append(this.workName)
                .append(", avgWorkTime=").append(this.avgWorkTime)
                .append(", kanbanCount=").append(this.kanbanCount)
                .append(", standardDeviation=").append(this.standardDeviation)
                .append(", workRev=").append(this.workRev)
                .append("}")
                .toString();
    }
}
