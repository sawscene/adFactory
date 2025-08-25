/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.approval;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum;

/**
 * 承認フロー情報
 *
 * @author nar-nakamura
 */
@Entity
@Table(name = "trn_approval_flow")
@XmlRootElement(name = "approvalFlow")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // 申請IDを指定して、承認フロー情報一覧を取得する。
    @NamedQuery(name = "ApprovalFlowEntity.findByApprovalId", query = "SELECT a FROM ApprovalFlowEntity a WHERE a.approvalId IN :approvalIds ORDER BY a.approvalId, a.approvalOrder"),
    // 申請IDを指定して、承認フロー情報を削除する。
    @NamedQuery(name = "ApprovalFlowEntity.removeByApprovalId", query = "DELETE FROM ApprovalFlowEntity a WHERE a.approvalId = :approvalId"),
})
public class ApprovalFlowEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 申請ID
     */
    @Id
    @Basic(optional = false)
    @Column(name = "approval_id")
    private Long approvalId;

    /**
     * 承認順
     */
    @Id
    @Basic(optional = false)
    @Column(name = "approval_order")
    private Integer approvalOrder;

    /**
     * 最終承認
     */
    @Basic(optional = false)
    //@NotNull
    @Column(name = "approval_final")
    private Boolean approvalFinal;

    /**
     * 承認者(組織ID)
     */
    @Basic(optional = false)
    //@NotNull
    @Column(name = "approver_id")
    private Long approverId;

    /**
     * 操作日時
     */
    @Column(name = "approval_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date approvalDatetime;

    /**
     * 承認状態(0:未承認, 1:申請中, 2:取消, 3:差戻, 4:最終承認済)
     */
    @Basic(optional = false)
    //@NotNull
    @Column(name = "approval_state")
    private ApprovalStatusEnum approvalState;

    /**
     * コメント
     */
    @Column(name = "comment")
    private String comment;

    /**
     * コンストラクタ
     */
    public ApprovalFlowEntity() {
    }

    /**
     * 申請IDを取得する。
     *
     * @return 申請ID
     */
    public Long getApprovalId() {
        return this.approvalId;
    }

    /**
     * 申請IDを設定する。
     *
     * @param approvalId 申請ID
     */
    public void setApprovalId(Long approvalId) {
        this.approvalId = approvalId;
    }

    /**
     * 承認順を取得する。
     *
     * @return 承認順
     */
    public Integer getApprovalOrder() {
        return this.approvalOrder;
    }

    /**
     * 承認順を設定する。
     *
     * @param approvalOrder 承認順
     */
    public void setApprovalOrder(Integer approvalOrder) {
        this.approvalOrder = approvalOrder;
    }

    /**
     * 最終承認を取得する。
     *
     * @return 最終承認
     */
    public Boolean getApprovalFinal() {
        return this.approvalFinal;
    }

    /**
     * 最終承認を設定する。
     *
     * @param approvalFinal 最終承認
     */
    public void setApprovalFinal(Boolean approvalFinal) {
        this.approvalFinal = approvalFinal;
    }

    /**
     * 承認者(組織ID)を取得する。
     *
     * @return 承認者(組織ID)
     */
    public Long getApproverId() {
        return this.approverId;
    }

    /**
     * 承認者(組織ID)を設定する。
     *
     * @param approverId 承認者(組織ID)
     */
    public void setApproverId(Long approverId) {
        this.approverId = approverId;
    }

    /**
     * 操作日時を取得する。
     *
     * @return 操作日時
     */
    public Date getApprovalDatetime() {
        return this.approvalDatetime;
    }

    /**
     * 操作日時を設定する。
     *
     * @param approvalDatetime 操作日時
     */
    public void setApprovalDatetime(Date approvalDatetime) {
        this.approvalDatetime = approvalDatetime;
    }

    /**
     * 承認状態(を取得する。
     *
     * @return 承認状態(0:未承認, 1:申請中, 2:取消, 3:差戻, 4:最終承認済)
     */
    public ApprovalStatusEnum getApprovalState() {
        return this.approvalState;
    }

    /**
     * 承認状態(を設定する。
     *
     * @param approvalState 承認状態(0:未承認, 1:申請中, 2:取消, 3:差戻, 4:最終承認済)
     */
    public void setApprovalState(ApprovalStatusEnum approvalState) {
        this.approvalState = approvalState;
    }

    /**
     * コメントを取得する。
     *
     * @return コメント
     */
    public String getComment() {
        return this.comment;
    }

    /**
     * コメントを設定する。
     *
     * @param comment コメント
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.approvalId);
        hash = 29 * hash + Objects.hashCode(this.approvalOrder);
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
        final ApprovalFlowEntity other = (ApprovalFlowEntity) obj;
        if (!Objects.equals(this.approvalId, other.approvalId)) {
            return false;
        }
        if (!Objects.equals(this.approvalOrder, other.approvalOrder)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ApprovalFlowEntity{")
                .append("approvalId=").append(this.approvalId)
                .append(", approvalOrder=").append(this.approvalOrder)
                .append(", approvalFinal=").append(this.approvalFinal)
                .append(", approverId=").append(this.approverId)
                .append(", approvalDatetime=").append(this.approvalDatetime)
                .append(", approvalState=").append(this.approvalState)
                .append(", comment=").append(this.comment)
                .append("}")
                .toString();
    }
}
