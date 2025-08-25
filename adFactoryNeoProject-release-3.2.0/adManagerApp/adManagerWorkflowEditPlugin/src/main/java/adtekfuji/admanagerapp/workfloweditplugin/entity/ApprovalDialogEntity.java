/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.entity;

import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ApprovalDataTypeEnum;

/**
 * 申請ダイアログ情報
 *
 * @author akihiro.yoshida
 */
public class ApprovalDialogEntity {

    /**
     * 申請区分(true: 申請、false：申請取消)
     */
    private Boolean isRequestTypeApproval;

    /**
     * データ種別(0:工程, 1:工程順, 2:カンバン)
     */
    private ApprovalDataTypeEnum approvalDataType;

    /**
     * 工程順情報
     */
    private WorkflowInfoEntity workflow;

    /**
     * 工程情報
     */
    private WorkInfoEntity work;

    /**
     * コンストラクタ
     */
    public ApprovalDialogEntity() {
    }

    /**
     * 申請区分を取得する。
     *
     * @return work 申請区分(true: 申請、false：申請取消)
     */
    public Boolean getIsRequestTypeApproval() {
        return this.isRequestTypeApproval;
    }

    /**
     * 申請区分を設定する。
     *
     * @param isRequestTypeApproval 申請区分(true: 申請、false：申請取消)
     */
    public void setIsRequestTypeApproval(Boolean isRequestTypeApproval) {
        this.isRequestTypeApproval = isRequestTypeApproval;
    }

    /**
     * データ種別を取得する。
     *
     * @return approvalDataType データ種別(0:工程, 1:工程順, 2:カンバン)
     */
    public ApprovalDataTypeEnum getApprovalDataType() {
        return this.approvalDataType;
    }

    /**
     * データ種別を設定する。
     *
     * @param approvalDataType データ種別(0:工程, 1:工程順, 2:カンバン)
     */
    public void setApprovalDataType(ApprovalDataTypeEnum approvalDataType) {
        this.approvalDataType = approvalDataType;
    }

    /**
     * 工程順情報を取得する。
     *
     * @return work 工程順情報
     */
    public WorkflowInfoEntity getWorkflow() {
        return this.workflow;
    }

    /**
     * 工程順情報を設定する。
     *
     * @param workflow 工程順情報
     */
    public void setWorkflow(WorkflowInfoEntity workflow) {
        this.workflow = workflow;
    }

    /**
     * 工程情報を取得する。
     *
     * @return work 工程情報
     */
    public WorkInfoEntity getWork() {
        return this.work;
    }

    /**
     * 工程情報を設定する。
     *
     * @param work 工程情報
     */
    public void setWork(WorkInfoEntity work) {
        this.work = work;
    }
}
