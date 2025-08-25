/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.approval;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jp.adtekfuji.adFactory.enumerate.ApprovalDataTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;

/**
 * 申請情報
 *
 * @author nar-nakamura
 */
@XmlRootElement(name = "approval")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApprovalInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 申請ID
     */
    @XmlElement(required = true)
    private Long approvalId;

    /**
     * ルートID
     */
    @XmlElement()
    private Long routeId;

    /**
     * 申請者(組織ID)
     */
    @XmlElement()
    private Long requestorId;

    /**
     * 申請日
     */
    @XmlElement()
    private Date requestDatetime;

    /**
     * 承認状態(0:未承認, 1:申請中, 2:取消, 3:差戻, 4:最終承認済)
     */
    @XmlElement()
    private ApprovalStatusEnum approvalState;

    /**
     * データ種別(0:工程, 1:工程順, 2:カンバン)
     */
    @XmlElement()
    private ApprovalDataTypeEnum dataType;

    /**
     * 新しいデータ(工程ID, 工程順ID, カンバンID)
     */
    @XmlElement()
    private Long newData;

    /**
     * 古いデータ(工程ID, 工程順ID, カンバンID)　※.新規データの申請ではnull
     */
    @XmlElement()
    private Long oldData;

    /**
     * 承認履歴(JSON)
     */
    @XmlElement()
    private String approvalHistory;

    /**
     * 申請コメント
     */
    @XmlElement()
    private String comment;

    /**
     * 承認フロー情報一覧
     */
    @XmlElementWrapper(name = "approvalFlows")
    @XmlElement(name = "approvalFlow")
    private List<ApprovalFlowInfoEntity> approvalFlows = null;

    /**
     * 承認履歴情報一覧
     */
    @XmlTransient
    private List<ApprovalHistoryInfo> approvalHistoryInfos;

    /**
     * コンストラクタ
     */
    public ApprovalInfoEntity() {
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
    public List<ApprovalFlowInfoEntity> getApprovalFlows() {
        return this.approvalFlows;
    }

    /**
     * 承認フロー情報一覧を設定する。
     *
     * @param approvalFlows 承認フロー情報一覧
     */
    public void setApprovalFlows(List<ApprovalFlowInfoEntity> approvalFlows) {
        this.approvalFlows = approvalFlows;
    }

    /**
     * 承認履歴情報一覧を取得する。
     *
     * @return 承認履歴情報一覧
     */
    public List<ApprovalHistoryInfo> getApprovalHistoryInfos() {
        if (Objects.isNull(this.approvalHistoryInfos)) {
            this.approvalHistoryInfos = JsonUtils.jsonToObjects(this.approvalHistory, ApprovalHistoryInfo[].class);
        }
        return this.approvalHistoryInfos;
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
        final ApprovalInfoEntity other = (ApprovalInfoEntity) obj;
        if (!Objects.equals(this.approvalId, other.approvalId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ApprovalInfoEntity{")
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
