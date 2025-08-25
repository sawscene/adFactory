/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.entity;

/**
 *
 * @author 14-0282
 */
public class MstPartsItem {

    private String prodNo;
    private String prodName;
    private String vendor;
    private String spec;
    private String figNo;
    private String unit;

    /**
     * コンストラクタ
     */
    public MstPartsItem() {
        this.prodNo = "";
        this.prodName = "";
        this.vendor = "";
        this.spec = "";
        this.figNo = "";
    }

    /**
     * 品目 取得
     *
     * @return 品目
     */
    public String getProdNo() {
        return prodNo;
    }

    /**
     * 品目 設定
     *
     * @param prodNo 品目
     */
    public void setProdNo(String prodNo) {
        this.prodNo = prodNo;
    }

    /**
     * 品名 取得
     *
     * @return 品名
     */
    public String getProdName() {
        return prodName;
    }

    /**
     * 品名 設定
     *
     * @param prodName 品名
     */
    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    /**
     * メーカー 取得
     *
     * @return メーカー
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * メーカー 設定
     *
     * @param vendor メーカー
     */
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    /**
     * 規格 取得
     *
     * @return 規格
     */
    public String getSpec() {
        return spec;
    }

    /**
     * 規格 設定
     *
     * @param spec 規格
     */
    public void setSpec(String spec) {
        this.spec = spec;
    }

    /**
     * 図番を取得する。
     * 
     * @return 図番
     */
    public String getFigNo() {
        return figNo;
    }

    /**
     * 図番を設定する。
     * 
     * @param figNo 図番 
     */
    public void setFigNo(String figNo) {
        this.figNo = figNo;
    }
    
    /**
     * 単位を取得する。
     * 
     * @return 単位
     */
    public String getUnit() {
        return unit;
    }

    /**
     * 単位を設定する。
     * 
     * @param unit 単位
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }    
}
