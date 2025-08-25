/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.controller;

/**
 * 表示用変更内容のクラス
 * 
 * @author shizuka.hirano
 */
public class DispApprovalChangeData {

    // 項目
    private String approvalTitle;
    // 変更前
    private String approvalOld;
    // 変更後
    private String approvalNew;
    // 文字色
    private String approvalColor;
    
    /**
     * 項目を取得する。
     * 
     * @return 項目
     */
    public String getApprovalTitle() {
        return approvalTitle;
    }

    /**
     * 項目を設定する。
     * 
     * @param approvalTitle 項目 
     */
    public void setApprovalTitle(String approvalTitle) {
        this.approvalTitle = approvalTitle;
    }
    
    /**
     * 変更前を取得する。
     * 
     * @return 変更前
     */
    public String getApprovalOld() {
        return approvalOld;
    }

    /**
     * 変更前を設定する。
     * 
     * @param approvalOld 変更前 
     */
    public void setApprovalOld(String approvalOld) {
        this.approvalOld = approvalOld;
    }
    
    /**
     * 変更後を取得する。
     * 
     * @return 変更後
     */
    public String getApprovalNew() {
        return approvalNew;
    }

    /**
     * 変更後を設定する。
     * 
     * @param approvalNew 変更後 
     */
    public void setApprovalNew(String approvalNew) {
        this.approvalNew = approvalNew;
    }
    
    /**
     * 文字色を取得する。
     * 
     * @return 文字色
     */
    public String getApprovalColor() {
        return approvalColor;
    }

    /**
     * 文字色を設定する。
     * 
     * @param approvalColor 文字色
     */
    public void setApprovalColor(String approvalColor) {
        this.approvalColor = approvalColor;
    }
}
