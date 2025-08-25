/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.controller;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * 表示用工程項目
 *
 * @author shizuka.hirano
 */

public class DispWorkInfoData implements Serializable {

    private String sheetName;// シート名
    private List<DispApprovalChangeData> sheetInfo;// ドキュメント情報
    private List<List<DispApprovalChangeData>> dispInfo;// 表示項目一覧
    private Integer pageNum;// 表示順
    
    /**
     * コンストラクタ
     */
    public DispWorkInfoData() {
    }

    /**
     * シート名を取得する。
     *
     * @return シート名
     */
    public String getSheetName() {
        return this.sheetName;
    }

    /**
     * シート名を設定する。
     *
     * @param sheetName シート名
     */
    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
    
    /**
     * シート情報を取得する。
     * 
     * @return シート情報
     */
    public List<DispApprovalChangeData> getSheetInfo() {
        return this.sheetInfo;
    }
    
    /**
     * シート情報を設定する。
     *
     * @param sheetInfo シート情報
     */
    public void setSheetInfo(List<DispApprovalChangeData> sheetInfo) {
        this.sheetInfo = sheetInfo;
    }

    /**
     * 表示項目一覧を取得する。
     *
     * @return 表示項目一覧
     */
    public List<List<DispApprovalChangeData>> getDispInfo() {
        return this.dispInfo;
    }

    /**
     * 表示項目一覧を設定する。
     *
     * @param dispInfo 表示項目一覧
     */
    public void setDispInfo(List<List<DispApprovalChangeData>> dispInfo) {
        this.dispInfo = dispInfo;
    }
    
    /**
     * 表示項目件数を取得する。
     *
     * @return 表示項目件数
     */
    public Integer getPageNum() {
        return this.pageNum;
    }

    /**
     * 表示項目件数を設定する。
     *
     * @param pageNum 表示項目件数
     */
    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.sheetName);
        hash = 59 * hash + Objects.hashCode(this.dispInfo);
        hash = 59 * hash + Objects.hashCode(this.sheetInfo);
        hash = 59 * hash + Objects.hashCode(this.pageNum);
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
        final DispWorkInfoData other = (DispWorkInfoData) obj;
        if (!Objects.equals(this.sheetName, other.sheetName)) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public String toString() {
        return "dispAddInfo{"
                + "sheetName=" + this.sheetName
                + ", dispInfo=" + this.dispInfo
                + ", pageNum=" + this.pageNum
                + '}';
    }
}
