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
 * 組織集計データ
 *
 * @author s-heya
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {
    "work_id",
    "work_name",
    "organization_id",
    "organization_name",
    "avg_worktime",
    "kanban_count",
    "work_rev"
}))
@NamedNativeQueries({
    @NamedNativeQuery(name = "OrganizationSummaryEntity.calcOrganization",
            query ="SELECT ret.work_id work_id, mw.work_name work_name, ret.organization_id organization_id, mo.organization_name, ret.avg_worktime avg_worktime, ret.stddev_worktime stddev_worktime, ret.work_count kanban_count, mw.work_rev " +
                    "FROM(SELECT work_id, organization_id, ROUND(AVG(act.sum_time), 2) avg_worktime, ROUND(STDDEV_POP(act.sum_time),2) stddev_worktime, COUNT(work_id) work_count " +
                    "FROM (SELECT tar.work_kanban_id, MAX(tar.actual_id) actual_id, SUM(tar.work_time) sum_time " +
                    "FROM (SELECT tk.kanban_id kanban_id " +
                    "FROM trn_kanban tk " +
                    "WHERE tk.comp_datetime BETWEEN ?1 AND ?2 " +
                    "AND tk.workflow_id = ?3 " +
                    "AND tk.kanban_status = 'COMPLETION') k " +
                    "JOIN trn_actual_result tar ON tar.kanban_id = k.kanban_id " +
                    "GROUP BY tar.work_kanban_id, tar.organization_id) act " +
                    "JOIN trn_actual_result tar2 ON tar2.actual_id = act.actual_id " +
                    "GROUP BY tar2.work_id, tar2.organization_id) ret " +
                    "JOIN mst_work mw ON ret.work_id = mw.work_id " +
                    "JOIN mst_organization mo on ret.organization_id = mo.organization_id",
            resultClass = OrganizationSummaryEntity.class)
})
@XmlRootElement(name = "organizationSummary")
@XmlAccessorType(XmlAccessType.FIELD)
public class OrganizationSummaryEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "work_id")
    private Long workId;

    @Id
    @Column(name = "organization_id")
    private Long organizationId;

    @Column(name = "work_name")
    private String workName;

    @Column(name = "organization_name")
    private String organizationName;

    @Column(name = "avg_worktime")
    private Double avgWorkTime;

    @Column(name = "stddev_worktime")
    private Double standardDeviation;

    @Column(name = "kanban_count")
    private Integer kanbanCount;

    @Column(name = "work_rev")
    private Integer workRev;

    /**
     * コンストラクタ
     */
    public OrganizationSummaryEntity() {
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
     * 組織IDを取得する。
     *
     * @return 組織ID
     */
    public Long getOrganizationId() {
        return this.organizationId;
    }

    /**
     * 組織IDを設定する。
     *
     * @param organizationId 組織ID
     */
    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
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
     * 組織名を取得する。
     *
     * @return 組織名
     */
    public String getOrganizationName() {
        return this.organizationName;
    }

    /**
     * 組織名を設定する。
     *
     * @param organizationName 組織名
     */
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
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
        if (!(object instanceof OrganizationSummaryEntity)) {
            return false;
        }
        OrganizationSummaryEntity other = (OrganizationSummaryEntity) object;
        if ((this.workName == null && other.workName != null) || (this.workName != null && !this.workName.equals(other.workName))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("OrganizationSummaryEntity{")
                .append("workId=").append(this.workId)
                .append(", organizationId=").append(this.organizationId)
                .append(", workName=").append(this.workName)
                .append(", organizationName=").append(this.organizationName)
                .append(", avgWorkTime=").append(this.avgWorkTime)
                .append(", standardDeviation=").append(this.standardDeviation)
                .append(", kanbanCount=").append(this.kanbanCount)
                .append(", workRev=").append(this.workRev)
                .append("}")
                .toString();
    }
}
