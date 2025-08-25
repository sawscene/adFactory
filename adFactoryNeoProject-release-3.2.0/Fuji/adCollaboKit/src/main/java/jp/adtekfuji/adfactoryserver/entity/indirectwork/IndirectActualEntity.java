/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.indirectwork;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
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
@NamedQueries({
    @NamedQuery(name = "IndirectActualEntity.findAll", query = "SELECT i FROM IndirectActualEntity i"),
    @NamedQuery(name = "IndirectActualEntity.findByIndirectActualId", query = "SELECT i FROM IndirectActualEntity i WHERE i.indirectActualId = :indirectActualId"),
    @NamedQuery(name = "IndirectActualEntity.findByFkIndirectWorkId", query = "SELECT i FROM IndirectActualEntity i WHERE i.fkIndirectWorkId = :fkIndirectWorkId"),
    @NamedQuery(name = "IndirectActualEntity.findByImplementDatetime", query = "SELECT i FROM IndirectActualEntity i WHERE i.implementDatetime = :implementDatetime"),
    @NamedQuery(name = "IndirectActualEntity.findByTransactionId", query = "SELECT i FROM IndirectActualEntity i WHERE i.transactionId = :transactionId"),
    @NamedQuery(name = "IndirectActualEntity.findByFkOrganizationId", query = "SELECT i FROM IndirectActualEntity i WHERE i.fkOrganizationId = :fkOrganizationId"),
    @NamedQuery(name = "IndirectActualEntity.findByWorkTime", query = "SELECT i FROM IndirectActualEntity i WHERE i.workTime = :workTime")})
public class IndirectActualEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "indirect_actual_id")
    private Long indirectActualId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_indirect_work_id")
    private long fkIndirectWorkId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "implement_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date implementDatetime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "transaction_id")
    private long transactionId;
    @Column(name = "fk_organization_id")
    private Long fkOrganizationId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "work_time")
    private int workTime;

    @XmlTransient
    @JoinColumn(name = "fk_indirect_work_id", referencedColumnName = "indirect_work_id", insertable = false, updatable = false, nullable = true)
    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    private IndirectWorkEntity indirectWork;

    @XmlTransient
    @JoinColumn(name = "fk_organization_id", referencedColumnName = "organization_id", insertable = false, updatable = false, nullable = true)
    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    private OrganizationEntity organization;

    /**
     * コンストラクタ
     */
    public IndirectActualEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param indirectActualId 間接工数実績ID
     * @param fkIndirectWorkId 間接作業ID
     * @param implementDatetime 実施日時
     * @param transactionId トランザクションID
     * @param fkOrganizationId 組織ID
     * @param workTime 作業時間[ms]
     */
    public IndirectActualEntity(Long indirectActualId, long fkIndirectWorkId, Date implementDatetime, long transactionId, Long fkOrganizationId, int workTime) {
        this.indirectActualId = indirectActualId;
        this.fkIndirectWorkId = fkIndirectWorkId;
        this.implementDatetime = implementDatetime;
        this.transactionId = transactionId;
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
    public long getFkIndirectWorkId() {
        return this.fkIndirectWorkId;
    }

    /**
     * 間接作業IDを設定する。
     *
     * @param fkIndirectWorkId 間接作業ID
     */
    public void setFkIndirectWorkId(long fkIndirectWorkId) {
        this.fkIndirectWorkId = fkIndirectWorkId;
    }

    /**
     * 実施日時を取得する。
     *
     * @return 実施日時
     */
    public Date getImplementDatetime() {
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
    public Long getFkOrganizationId() {
        return this.fkOrganizationId;
    }

    /**
     * 組織IDを設定する。
     *
     * @param fkOrganizationId 組織ID
     */
    public void setFkOrganizationId(Long fkOrganizationId) {
        this.fkOrganizationId = fkOrganizationId;
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
        hash = 11 * hash + (int) (this.fkIndirectWorkId ^ (this.fkIndirectWorkId >>> 32));
        hash = 11 * hash + Objects.hashCode(this.implementDatetime);
        hash = 11 * hash + (int) (this.transactionId ^ (this.transactionId >>> 32));
        hash = 11 * hash + Objects.hashCode(this.fkOrganizationId);
        hash = 11 * hash + this.workTime;
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
        if (this.fkIndirectWorkId != other.fkIndirectWorkId) {
            return false;
        }
        if (!Objects.equals(this.implementDatetime, other.implementDatetime)) {
            return false;
        }
        if (this.transactionId != other.transactionId) {
            return false;
        }
        if (!Objects.equals(this.fkOrganizationId, other.fkOrganizationId)) {
            return false;
        }
        if (this.workTime != other.workTime) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "IndirectActualEntity{" + "indirectActualId=" + this.indirectActualId + ", fkIndirectWorkId=" + this.fkIndirectWorkId + ", implementDatetime=" + this.implementDatetime + ", transactionId=" + this.transactionId + ", workTime=" + this.workTime + '}';
    }
}
