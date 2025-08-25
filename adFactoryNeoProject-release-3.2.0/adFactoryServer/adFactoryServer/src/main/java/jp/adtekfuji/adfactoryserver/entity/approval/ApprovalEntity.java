/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.approval;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.ApprovalDataTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum;

/**
 * 申請情報
 *
 * @author nar-nakamura
 */
@Entity
@Table(name = "trn_approval")
@XmlRootElement(name = "approval")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // 申請IDを指定して、申請情報を削除する。
    @NamedQuery(name = "ApprovalEntity.remove", query = "DELETE FROM ApprovalEntity a WHERE a.approvalId = :approvalId"),
    // 工程IDを指定して、対象の工程を使用している工程順で、申請中の工程順の件数を取得する。
    @NamedQuery(name = "ApprovalEntity.countApplyWorkflowByWorkId", query = "SELECT COUNT(w.workflowId) FROM WorkflowEntity w WHERE w.workflowId IN (SELECT c.workflowId FROM ConWorkflowWorkEntity c WHERE c.workId IN :workIds) AND w.approvalState = jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum.APPLY"),
    // 工程順IDを指定して、対象の工程順で使用している工程で、申請中の工程の件数を取得する。
    @NamedQuery(name = "ApprovalEntity.countApplyWorkByWorkflowId", query = "SELECT COUNT(w.workId) FROM WorkEntity w WHERE w.workId IN (SELECT c.workId FROM ConWorkflowWorkEntity c WHERE c.workflowId = :workflowId) AND w.approvalState = jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum.APPLY"),
})
public class ApprovalEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 申請ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "approval_id")
    private Long approvalId;

    /**
     * ルートID
     */
    @Basic(optional = false)
    //@NotNull
    @Column(name = "route_id")
    private Long routeId;

    /**
     * 申請者(組織ID)
     */
    @Basic(optional = false)
    //@NotNull
    @Column(name = "requestor_id")
    private Long requestorId;

    /**
     * 申請日
     */
    @Basic(optional = false)
    //@NotNull
    @Column(name = "request_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestDatetime;

    /**
     * 承認状態(0:未承認, 1:申請中, 2:取消, 3:差戻, 4:最終承認済)
     */
    @Basic(optional = false)
    //@NotNull
    @Column(name = "approval_state")
    private ApprovalStatusEnum approvalState;

    /**
     * データ種別(0:工程, 1:工程順, 2:カンバン)
     */
    @Basic(optional = false)
    //@NotNull
    @Column(name = "data_type")
    private ApprovalDataTypeEnum dataType;

    /**
     * 新しいデータ(工程ID, 工程順ID, カンバンID)
     */
    @Basic(optional = false)
    //@NotNull
    @Column(name = "new_data")
    private Long newData;

    /**
     * 古いデータ(工程ID, 工程順ID, カンバンID)　※.新規データの申請ではnull
     */
    @Column(name = "old_data")
    private Long oldData;

    /**
     * 承認履歴(JSON)
     */
    @Column(name = "approval_history", length = 30000)
    private String approvalHistory;

    /**
     * 申請コメント
     */
    @Column(name = "comment")
    private String comment;

    /**
     * 承認フロー情報一覧
     */
    @XmlElementWrapper(name = "approvalFlows")
    @XmlElement(name = "approvalFlow")
    @Transient
    private List<ApprovalFlowEntity> approvalFlows = null;

    /**
     * コンストラクタ
     */
    public ApprovalEntity() {
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
     * ルートIDを取得する。
     *
     * @return ルートID
     */
    public Long getRouteId() {
        return this.routeId;
    }

    /**
     * ルートIDを設定する。
     *
     * @param routeId ルートID
     */
    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    /**
     * 申請者(組織ID)を取得する。
     *
     * @return 申請者(組織ID)
     */
    public Long getRequestorId() {
        return this.requestorId;
    }

    /**
     * 申請者(組織ID)を設定する。
     *
     * @param requestorId 申請者(組織ID)
     */
    public void setRequestorId(Long requestorId) {
        this.requestorId = requestorId;
    }

    /**
     * 申請日を取得する。
     *
     * @return 申請日
     */
    public Date getRequestDatetime() {
        return this.requestDatetime;
    }

    /**
     * 申請日を設定する。
     *
     * @param requestDatetime 申請日
     */
    public void setRequestDatetime(Date requestDatetime) {
        this.requestDatetime = requestDatetime;
    }

    /**
     * 承認状態を取得する。
     *
     * @return 承認状態(0:未承認, 1:申請中, 2:取消, 3:差戻, 4:最終承認済)
     */
    public ApprovalStatusEnum getApprovalState() {
        return this.approvalState;
    }

    /**
     * 承認状態を設定する。
     *
     * @param approvalState 承認状態(0:未承認, 1:申請中, 2:取消, 3:差戻, 4:最終承認済)
     */
    public void setApprovalState(ApprovalStatusEnum approvalState) {
        this.approvalState = approvalState;
    }

    /**
     * データ種別を取得する。
     *
     * @return データ種別(0:工程, 1:工程順, 2:カンバン)
     */
    public ApprovalDataTypeEnum getDataType() {
        return this.dataType;
    }

    /**
     * データ種別を設定する。
     *
     * @param dataType データ種別(0:工程, 1:工程順, 2:カンバン)
     */
    public void setDataType(ApprovalDataTypeEnum dataType) {
        this.dataType = dataType;
    }

    /**
     * 新しいデータを取得する。
     *
     * @return 新しいデータ(工程ID, 工程順ID, カンバンID)
     */
    public Long getNewData() {
        return this.newData;
    }

    /**
     * 新しいデータを設定する。
     *
     * @param newData 新しいデータ(工程ID, 工程順ID, カンバンID)
     */
    public void setNewData(Long newData) {
        this.newData = newData;
    }

    /**
     * 古いデータを取得する。
     *
     * @return 古いデータ(工程ID, 工程順ID, カンバンID)　※.新規データの申請ではnull
     */
    public Long getOldData() {
        return this.oldData;
    }

    /**
     * 古いデータを設定する。
     *
     * @param oldData 古いデータ(工程ID, 工程順ID, カンバンID)　※.新規データの申請ではnull
     */
    public void setOldData(Long oldData) {
        this.oldData = oldData;
    }

    /**
     * 承認履歴(JSON)を取得する。
     *
     * @return 承認履歴(JSON)
     */
    public String getApprovalHistory() {
        return this.approvalHistory;
    }

    /**
     * 承認履歴(JSON)を設定する。
     *
     * @param approvalHistory 承認履歴(JSON)
     */
    public void setApprovalHistory(String approvalHistory) {
        this.approvalHistory = approvalHistory;
    }

    /**
     * 申請コメントを取得する。
     *
     * @return 申請コメント
     */
    public String getComment() {
        return this.comment;
    }

    /**
     * 申請コメントを設定する。
     *
     * @param comment 申請コメント
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * 承認フロー情報一覧を取得する。
     *
     * @return 承認フロー情報一覧
     */
    public List<ApprovalFlowEntity> getApprovalFlows() {
        return this.approvalFlows;
    }

    /**
     * 承認フロー情報一覧を設定する。
     *
     * @param approvalFlows 承認フロー情報一覧
     */
    public void setApprovalFlows(List<ApprovalFlowEntity> approvalFlows) {
        this.approvalFlows = approvalFlows;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.approvalId);
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
        final ApprovalEntity other = (ApprovalEntity) obj;
        if (!Objects.equals(this.approvalId, other.approvalId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ApprovalEntity{")
                .append("approvalId=").append(this.approvalId)
                .append(", routeId=").append(this.routeId)
                .append(", requestorId=").append(this.requestorId)
                .append(", requestDatetime=").append(this.requestDatetime)
                .append(", approvalState=").append(this.approvalState)
                .append(", dataType=").append(this.dataType)
                .append(", newData=").append(this.newData)
                .append(", oldData=").append(this.oldData)
                .append(", approvalHistory=").append(this.approvalHistory)
                .append(", comment=").append(this.comment)
                .append("}")
                .toString();
    }
}
