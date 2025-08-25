/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.chart;

import java.io.Serializable;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 組織集計データ
 *
 * @author s-heya
 */
@XmlRootElement(name = "organizationSummary")
@XmlAccessorType(XmlAccessType.FIELD)
public class OrganizationSummaryInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private Long workId;

    @XmlElement
    private Long organizationId;

    @XmlElement
    private String workName;

    @XmlElement
    private String organizationName;

    @XmlElement
    private Double avgWorkTime;

    @XmlElement
    private Integer kanbanCount;

    /**
     * 標準偏差
     */
    @XmlElement
    private Double standardDeviation;

    /**
     * 工程の版数
     */
    @XmlElement
    private Integer workRev;

    /**
     * コンストラクタ
     */
    public OrganizationSummaryInfoEntity() {
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

    /**
     * 表示名を取得する。
     *
     * @return 表示名(工程名 : 版数)
     */
    public String getDisplayWorkName() {
        StringBuilder name = new StringBuilder(this.workName);
        if (Objects.nonNull(this.workRev)) {
            name.append(" : ").append(this.workRev);
        }
        return name.toString();
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
        if (!(object instanceof OrganizationSummaryInfoEntity)) {
            return false;
        }
        OrganizationSummaryInfoEntity other = (OrganizationSummaryInfoEntity) object;
        if ((this.workName == null && other.workName != null) || (this.workName != null && !this.workName.equals(other.workName))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("OrganizationSummaryInfoEntity{")
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
