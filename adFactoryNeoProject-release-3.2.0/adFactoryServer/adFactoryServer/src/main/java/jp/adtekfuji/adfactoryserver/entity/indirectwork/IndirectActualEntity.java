/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.indirectwork;

import adtekfuji.utility.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;


/**
 * 間接工数実績
 *
 * @author nar-nakamura
 */
@Entity
@Table(name = "trn_indirect_actual")
@XmlRootElement(name = "indirectActual")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown=true)
@NamedQueries({
    @NamedQuery(name = "IndirectActualEntity.findByIndirectWorkId", query = "SELECT a FROM IndirectActualEntity a WHERE a.indirectWorkId = :indirectWorkId AND a.organizationId = :organizationId AND :fromDate <= a.implementDatetime AND a.implementDatetime <= :toDate ORDER BY a.indirectActualId DESC"),
})
public class IndirectActualEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "indirect_actual_id")
    @JsonProperty("indirectActualId")
    private Long indirectActualId;// 間接工数実績ID

    @Basic(optional = false)
    //@NotNull
    @Column(name = "indirect_work_id")
    @XmlElement(name = "fkIndirectWorkId")
    @JsonProperty("indirectWorkId")
    private long indirectWorkId;// 間接作業ID

    @Basic(optional = false)
    //@NotNull
    @Column(name = "implement_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date implementDatetime;// 実施日時

    @Transient
    @JsonProperty("reportDate")
    private String reportDate;
    
    @Basic(optional = false)
    //@NotNull
    @Column(name = "transaction_id")
    @JsonProperty("transactionId")
    private long transactionId;// トランザクションID

    @Column(name = "organization_id")
    @XmlElement(name = "fkOrganizationId")
    @JsonProperty("organizationId")
    private Long organizationId;// 組織ID

    @Basic(optional = false)
    //@NotNull
    @Column(name = "work_time")
    @JsonProperty("workTime")
    private int workTime;// 作業時間[ms]

    @Column(name = "production_number")
    @JsonProperty("productionNum")
    private String productionNum;// 製造番号

    @XmlTransient
    @JoinColumn(name = "indirect_work_id", referencedColumnName = "indirect_work_id", insertable = false, updatable = false, nullable = true)
    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @JsonIgnore
    private IndirectWorkEntity indirectWork;// 間接作業情報

    @XmlTransient
    @JoinColumn(name = "organization_id", referencedColumnName = "organization_id", insertable = false, updatable = false, nullable = true)
    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @JsonIgnore
    private OrganizationEntity organization;// 組織情報

    /**
     * コンストラクタ
     */
    public IndirectActualEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param indirectActualId 間接工数実績ID
     * @param indirectWorkId 間接作業ID
     * @param implementDatetime 実施日時
     * @param transactionId トランザクションID
     * @param organizationId 組織ID
     * @param workTime 作業時間[ms]
     */
    public IndirectActualEntity(Long indirectActualId, long indirectWorkId, Date implementDatetime, long transactionId, Long organizationId, int workTime) {
        this.indirectActualId = indirectActualId;
        this.indirectWorkId = indirectWorkId;
        this.implementDatetime = implementDatetime;
        this.transactionId = transactionId;
        this.organizationId = organizationId;
        this.workTime = workTime;
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
     * 間接作業IDを取得する。
     *
     * @return 間接作業ID
     */
    public long getIndirectWorkId() {
        return this.indirectWorkId;
    }

    /**
     * 間接作業IDを設定する。
     *
     * @param indirectWorkId 間接作業ID
     */
    public void setIndirectWorkId(long indirectWorkId) {
        this.indirectWorkId = indirectWorkId;
    }

    /**
     * 実施日時を取得する。
     *
     * @return 実施日時
     */
    public Date getImplementDatetime() {
        if (Objects.isNull(this.implementDatetime) && !StringUtils.isEmpty(this.reportDate)) {
            // adProductWebの場合、文字列で実施日時が送られる
            try {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
                this.implementDatetime = df.parse(this.reportDate);
            } catch (ParseException ex) {
            }
        }
        return this.implementDatetime;
    }

    /**
     * 実施日時を設定する。
     *
     * @param implementDatetime 実施日時
     */
    public void setImplementDatetime(Date implementDatetime) {
        this.implementDatetime = implementDatetime;
    }

    /**
     * 実施日時を設定する。(adProductWeb Only)
     * 
     * @return 実施日時
     */
    public String getReportDate() {
        return reportDate;
    }

    /**
     * 実施日時を取得する。(adProductWeb Only)
     * 
     * @param reportDate 実施日時
     */
    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    /**
     * トランザクションIDを取得する。
     *
     * @return トランザクションID
     */
    public long getTransactionId() {
        return this.transactionId;
    }

    /**
     * トランザクションIDを設定する。
     *
     * @param transactionId トランザクションID
     */
    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
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
     * 作業時間[ms]を取得する。
     *
     * @return 作業時間[ms]
     */
    public int getWorkTime() {
        return this.workTime;
    }

    /**
     * 作業時間[ms]を設定する。
     *
     * @param workTime 作業時間[ms]
     */
    public void setWorkTime(int workTime) {
        this.workTime = workTime;
    }

    /**
     * 製造番号を取得する。
     *
     * @return 製造番号
     */
    public String getProductionNum() {
        return this.productionNum;
    }

    /**
     * 製造番号を設定する。
     *
     * @param productionNum 製造番号
     */
    public void setProductionNum(String productionNum) {
        this.productionNum = productionNum;
    }

    /**
     * 間接作業情報を取得する。
     *
     * @return 間接作業情報
     */
    public IndirectWorkEntity getIndirectWork() {
        return this.indirectWork;
    }

    /**
     * 組織情報を取得する。
     *
     * @return 組織情報
     */
    public OrganizationEntity getOrganization() {
        return this.organization;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + Objects.hashCode(this.indirectActualId);
        hash = 11 * hash + (int) (this.indirectWorkId ^ (this.indirectWorkId >>> 32));
        hash = 11 * hash + Objects.hashCode(this.implementDatetime);
        hash = 11 * hash + (int) (this.transactionId ^ (this.transactionId >>> 32));
        hash = 11 * hash + Objects.hashCode(this.organizationId);
        hash = 11 * hash + this.workTime;
        hash = 11 * hash + Objects.hashCode(this.productionNum);
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
        final IndirectActualEntity other = (IndirectActualEntity) obj;
        if (!Objects.equals(this.indirectActualId, other.indirectActualId)) {
            return false;
        }
        if (this.indirectWorkId != other.indirectWorkId) {
            return false;
        }
        if (!Objects.equals(this.implementDatetime, other.implementDatetime)) {
            return false;
        }
        if (this.transactionId != other.transactionId) {
            return false;
        }
        if (!Objects.equals(this.organizationId, other.organizationId)) {
            return false;
        }
        if (this.workTime != other.workTime) {
            return false;
        }
        if (!Objects.equals(this.productionNum, other.productionNum)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("IndirectActualEntity{")
                .append("indirectActualId=").append(this.indirectActualId)
                .append(", indirectWorkId=").append(this.indirectWorkId)
                .append(", implementDatetime=").append(this.implementDatetime)
                .append(", reportDate=").append(this.reportDate)
                .append(", transactionId=").append(this.transactionId)
                .append(", organizationId=").append(this.organizationId)
                .append(", workTime=").append(this.workTime)
                .append(", productionNum=").append(this.productionNum)
                .append("}")
                .toString();
    }
}
