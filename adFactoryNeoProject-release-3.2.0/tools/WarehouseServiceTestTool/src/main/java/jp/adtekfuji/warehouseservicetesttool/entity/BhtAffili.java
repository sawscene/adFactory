/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.warehouseservicetesttool.entity;

/**
 * ハンディ端末 部品所属マスタ
 * @author nar-nakamura
 */
public class BhtAffili {
    private String affiliName;
    private String affiliCode;

    public BhtAffili() {
        
    }

    /**
     * 部品所属マスタ
     * @param affiliName
     * @param affiliCode
     */
    public BhtAffili(String affiliName, String affiliCode) {
        this.affiliName = affiliName;
        this.affiliCode = affiliCode;
    }

    /**
     * 部品所属名
     * @return 
     */
    public String getAffiliName() {
        return this.affiliName;
    }

    /**
     * 部品所属名
     * @param affiliName 
     */
    public void setAffiliName(String affiliName) {
        this.affiliName = affiliName;
    }

    /**
     * 部品所属コード
     * @return 
     */
    public String getAffiliCode() {
        return this.affiliCode;
    }

    /**
     * 部品所属コード
     * @param affiliCode 
     */
    public void setAffiliCode(String affiliCode) {
        this.affiliCode = affiliCode;
    }

    @Override
    public String toString() {
        return "BhtAffili{" +
                "affiliName=" + this.affiliName +
                ", affiliCode=" + this.affiliCode +
                "}";
    }    
}
