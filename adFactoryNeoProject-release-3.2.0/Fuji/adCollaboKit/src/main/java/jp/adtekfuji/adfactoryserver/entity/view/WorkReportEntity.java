/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.view;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 作業日報情報
 *
 * @author nar-nakamura
 */
@Entity
@Table(name = "view_work_report")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "workReport")
@NamedQueries({
    // 作業者の日報
    @NamedQuery(name = "WorkReportEntity.countOrganizationIdDaily", query = "SELECT COUNT(w) FROM WorkReportEntity w WHERE w.organizationId = :organizationId AND w.workDate = :workDate"),
    @NamedQuery(name = "WorkReportEntity.findOrganizationIdDaily", query = "SELECT w FROM WorkReportEntity w WHERE w.organizationId = :organizationId AND w.workDate = :workDate ORDER BY w.workType, w.classNumber, w.workNumber, w.workId"),
    // 指定期間の日報 (月報用)
    @NamedQuery(name = "WorkReportEntity.countFromToDate", query = "SELECT COUNT(w) FROM WorkReportEntity w WHERE w.workDate >= :fromDate AND w.workDate <= :toDate"),
    @NamedQuery(name = "WorkReportEntity.findFromToDate", query = "SELECT w FROM WorkReportEntity w WHERE w.workDate >= :fromDate AND w.workDate <= :toDate ORDER BY w.workType, w.classNumber, w.workNumber, w.workId"),

    @NamedQuery(name = "WorkReportEntity.count", query = "SELECT COUNT(w) FROM WorkReportEntity w"),
    @NamedQuery(name = "WorkReportEntity.findAll", query = "SELECT w FROM WorkReportEntity w")})
public class WorkReportEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "work_type")
    private Integer workType;
    @Id
    @Column(name = "work_date")
    private String workDate;
    @Id
    @Column(name = "organization_id")
    private Long organizationId;
    @Size(max = 256)
    @Column(name = "organization_identify")
    private String organizationIdentify;
    @Size(max = 256)
    @Column(name = "organization_name")
    private String organizationName;
    @Column(name = "indirect_actual_id")
    private Long indirectActualId;
    @Id
    @Column(name = "work_id")
    private Long workId;
    @Size(max = 2147483647)
    @Column(name = "class_number")
    private String classNumber;
    @Size(max = 64)
    @Column(name = "work_number")
    private String workNumber;
    @Size(max = 256)
    @Column(name = "work_name")
    private String workName;
    @Id
    @Size(max = 2147483647)
    @Column(name = "order_number")
    private String orderNumber;
    @Column(name = "work_time")
    private Long workTime;

    /**
     * コンストラクタ
     */
    public WorkReportEntity() {
    }

    /**
     * 作業種別を取得する。
     *
     * @return 作業種別 (0:直接作業, 1:間接作業)
     */
    public Integer getWorkType() {
        return this.workType;
    }

    /**
     * 作業種別を設定する。
     *
     * @param workType 作業種別 (0:直接作業, 1:間接作業)
     */
    public void setWorkType(Integer workType) {
        this.workType = workType;
    }

    /**
     * 作業日を取得する。
     *
     * @return 作業日 ('yyyyMMdd')
     */
    public String getWorkDate() {
        return this.workDate;
    }

    /**
     * 作業日を設定する。
     *
     * @param workDate 作業日 ('yyyyMMdd')
     */
    public void setWorkDate(String workDate) {
        this.workDate = workDate;
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
     * 組織識別名を取得する。
     *
     * @return 組織識別名
     */
    public String getOrganizationIdentify() {
        return this.organizationIdentify;
    }

    /**
     * 組織識別名を設定する。
     *
     * @param organizationIdentify 組織識別名
     */
    public void setOrganizationIdentify(String organizationIdentify) {
        this.organizationIdentify = organizationIdentify;
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
     * 間接工数実績IDを取得する。
     *
     * @return 間接工数実績ID
     */
    public Long getIndirectActualId() {
        return this.indirectActualId;
    }

    /**
     * 間接工数実績IDを設定する。
     *
     * @param indirectActualId 間接工数実績ID
     */
    public void setIndirectActualId(Long indirectActualId) {
        this.indirectActualId = indirectActualId;
    }

    /**
     * 作業IDを取得する。
     *
     * @return 作業ID
     */
    public Long getWorkId() {
        return this.workId;
    }

    /**
     * 作業IDを設定する。
     *
     * @param workId 作業ID
     */
    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    /**
     * 分類番号を取得する。
     *
     * @return 分類番号
     */
    public String getClassNumber() {
        return this.classNumber;
    }

    /**
     * 分類番号を設定する。
     *
     * @param classNumber 分類番号
     */
    public void setClassNumber(String classNumber) {
        this.classNumber = classNumber;
    }

    /**
     * 作業番号を取得する。
     *
     * @return 作業番号
     */
    public String getWorkNumber() {
        return this.workNumber;
    }

    /**
     * 作業番号を設定する。
     *
     * @param workNumber 作業番号
     */
    public void setWorkNumber(String workNumber) {
        this.workNumber = workNumber;
    }

    /**
     * 作業名を取得する。
     *
     * @return 作業名
     */
    public String getWorkName() {
        return this.workName;
    }

    /**
     * 作業名を設定する。
     *
     * @param workName 作業名
     */
    public void setWorkName(String workName) {
        this.workName = workName;
    }

    /**
     * 注文番号を取得する。
     *
     * @return 注文番号
     */
    public String getOrderNumber() {
        return this.orderNumber;
    }

    /**
     * 注文番号を設定する。
     *
     * @param orderNumber 注文番号
     */
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * 工数(ms)を取得する。
     *
     * @return 工数(ms)
     */
    public Long getWorkTime() {
        return this.workTime;
    }

    /**
     * 工数(ms)を設定する。
     *
     * @param workTime 工数(ms)
     */
    public void setWorkTime(Long workTime) {
        this.workTime = workTime;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.workType);
        hash = 13 * hash + Objects.hashCode(this.workDate);
        hash = 13 * hash + Objects.hashCode(this.organizationId);
        hash = 13 * hash + Objects.hashCode(this.organizationIdentify);
        hash = 13 * hash + Objects.hashCode(this.organizationName);
        hash = 13 * hash + Objects.hashCode(this.indirectActualId);
        hash = 13 * hash + Objects.hashCode(this.workId);
        hash = 13 * hash + Objects.hashCode(this.classNumber);
        hash = 13 * hash + Objects.hashCode(this.workNumber);
        hash = 13 * hash + Objects.hashCode(this.workName);
        hash = 13 * hash + Objects.hashCode(this.orderNumber);
        hash = 13 * hash + Objects.hashCode(this.workTime);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WorkReportEntity other = (WorkReportEntity) obj;
        if (!Objects.equals(this.workType, other.workType)) {
            return false;
        }
        if (!Objects.equals(this.workDate, other.workDate)) {
            return false;
        }
        if (!Objects.equals(this.organizationId, other.organizationId)) {
            return false;
        }
        if (!Objects.equals(this.organizationIdentify, other.organizationIdentify)) {
            return false;
        }
        if (!Objects.equals(this.organizationName, other.organizationName)) {
            return false;
        }
        if (!Objects.equals(this.indirectActualId, other.indirectActualId)) {
            return false;
        }
        if (!Objects.equals(this.workId, other.workId)) {
            return false;
        }
        if (!Objects.equals(this.classNumber, other.classNumber)) {
            return false;
        }
        if (!Objects.equals(this.workNumber, other.workNumber)) {
            return false;
        }
        if (!Objects.equals(this.workName, other.workName)) {
            return false;
        }
        if (!Objects.equals(this.orderNumber, other.orderNumber)) {
            return false;
        }
        if (!Objects.equals(this.workTime, other.workTime)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "WorkReportEntity{"
                + "workType=" + this.workType
                + ", workDate=" + this.workDate
                + ", organizationId=" + this.organizationId
                + ", organizationIdentify=" + this.organizationIdentify
                + ", organizationName=" + this.organizationName
                + ", indirectActualId=" + this.indirectActualId
                + ", workId=" + this.workId
                + ", classNumber=" + this.classNumber
                + ", workNumber=" + this.workNumber
                + ", workName=" + this.workName
                + ", orderNumber=" + this.orderNumber
                + ", workTime=" + this.workTime
                + '}';
    }
}
