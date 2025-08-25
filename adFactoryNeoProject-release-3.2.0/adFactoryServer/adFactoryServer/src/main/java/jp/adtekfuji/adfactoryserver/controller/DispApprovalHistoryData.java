/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.controller;

/**
 * 表示用承認履歴
 * @author shizuka.hirano
 */
public class DispApprovalHistoryData {

    // 承認順
    private String approvalOrder;
    // 承認者
    private String approvalName;
    // 承認状態
    private String approvalState;
    // コメント
    private String approvalComment;
    
    /**
     * 承認順を取得する。
     * 
     * @return 承認順
     */
    public String getApprovalOrder() {
        return approvalOrder;
    }

    /**
     * 承認順を設定する。
     * 
     * @param approvalOrder 承認順 
     */
    public void setApprovalOrder(String approvalOrder) {
        this.approvalOrder = approvalOrder;
    }
    
    /**
     * 承認者を取得する。
     * 
     * @return 承認者
     */
    public String getApprovalName() {
        return approvalName;
    }

    /**
     * 承認者を設定する。
     * 
     * @param approvalName 承認者 
     */
    public void setApprovalName(String approvalName) {
        this.approvalName = approvalName;
    }
    
    /**
     * 承認状態を取得する。
     * 
     * @return 承認状態
     */
    public String getApprovalState() {
        return approvalState;
    }

    /**
     * 承認状態を設定する。
     * 
     * @param approvalState 承認状態 
     */
    public void setApprovalState(String approvalState) {
        this.approvalState = approvalState;
    }
    
    /**
     * コメントを取得する。
     * 
     * @return コメント
     */
    public String getApprovalComment() {
        return approvalComment;
    }

    /**
     * コメントを設定する。
     * 
     * @param approvalComment コメント 
     */
    public void setApprovalComment(String approvalComment) {
        this.approvalComment = approvalComment;
    }
}
