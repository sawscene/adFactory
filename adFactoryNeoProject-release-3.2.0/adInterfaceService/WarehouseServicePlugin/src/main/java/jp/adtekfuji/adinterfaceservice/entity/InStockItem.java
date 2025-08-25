/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.entity;

import java.util.Objects;

/**
 * 在庫情報
 * 
 * @author s-heya
 */
public class InStockItem {

    private String supplyNo;
    private String prodNo;
    private String areaNo;
    private String areaName;
    private String locNo;
    private Integer inStockNum = 0;
    private String prodName;
    private String spec;

    /**
     * コンストラクタ
     */
    public InStockItem() {
    }

    /**
     * 納入番号(発注番号)を取得する。
     * 
     * @return 納入番号
     */
    public String getSupplyNo() {
        return this.supplyNo;
    }

    /**
     * 納入番号を設定する。
     * 
     * @param supplyNo 納入番号 
     */
    public void setSupplyNo(String supplyNo) {
        this.supplyNo = supplyNo;
    }

    /**
     * 品目を取得する。
     * 
     * @return 品目
     */
    public String getProdNo() {
        return this.prodNo;
    }

    /**
     * 品目を設定する。
     * 
     * @param prodNo 品目
     */
    public void setProdNo(String prodNo) {
        this.prodNo = prodNo;
    }

    /**
     * 倉庫コードを取得する。
     * 
     * @return 倉庫コード
     */
    public String getAreaNo() {
        return this.areaNo;
    }

    /**
     * 倉庫コードを設定する。
     * 
     * @param areaNo 倉庫コード
     */
    public void setAreaNo(String areaNo) {
        this.areaNo = areaNo;
    }

    /**
     * 区画名を取得する。
     * 
     * @return 区画名
     */
    public String getAreaName() {
        return areaName;
    }

    /**
     * 区画名を設定する。
     * 
     * @param areaName 区画名
     */
    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    /**
     * 棚番号を取得する。
     * 
     * @return 棚番号
     */
    public String getLocNo() {
        return locNo;
    }

    /**
     * 棚番号を設定する。
     * 
     * @param locNo 棚番号 
     */
    public void setLocNo(String locNo) {
        this.locNo = locNo;
    }

    /**
     * 現在庫数を取得する。
     * 
     * @return 現在庫数
     */
    public Integer getInStockNum() {
        return this.inStockNum;
    }

    /**
     * 現在庫数を設定する。
     * 
     * @param inStockNum 現在庫数
     */
    public void setInStockNum(Integer inStockNum) {
        this.inStockNum = inStockNum;
    }

    /**
     * 品名を取得する。
     *
     * @return 品名
     */
    public String getProdName() {
        return this.prodName;
    }

    /**
     * 品名を設定する。
     *
     * @param prodName 品名
     */
    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    /**
     * 規格・型式を取得する。
     *
     * @return 規格・型式
     */
    public String getSpec() {
        return this.spec;
    }

    /**
     * 規格・型式を設定する。
     *
     * @param spec 規格・型式
     */
    public void setSpec(String spec) {
        this.spec = spec;
    }

    /**
     * ハッシュコードを返す。
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.supplyNo);
        hash = 23 * hash + Objects.hashCode(this.prodNo);
        return hash;
    }

    /**
     * オブジェクトを比較する
     * 
     * @param obj 比較するオブジェクト
     * @return オブジェクトが等しい場合はtrueを、異なる場合はfalseを返す。
     */
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
        final InStockItem other = (InStockItem) obj;
        if (!Objects.equals(this.supplyNo, other.supplyNo)) {
            return false;
        }
        return Objects.equals(this.prodNo, other.prodNo);
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("InStockItem{")
            .append("supplyNo=").append(this.supplyNo)
            .append(", prodNo=").append(this.prodNo)
            .append(", areaNo=").append(this.areaNo)
            .append(", inStockNum=").append(this.inStockNum)
            .append(", prodName=").append(this.prodName)
            .append(", spec=").append(this.spec)
            .append("}")
            .toString();
    }   
}
