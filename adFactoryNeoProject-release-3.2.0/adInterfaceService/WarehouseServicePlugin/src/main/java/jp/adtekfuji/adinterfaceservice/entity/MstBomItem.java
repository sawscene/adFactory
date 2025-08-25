/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.entity;

/**
 * 部品構成マスタ インポート情報
 *
 * @author
 */
public class MstBomItem {

    private String prodNo;
    private String partsNo;
    private Integer reqNum;
    private String unitNo;

    /**
     * コンストラクタ
     */
    public MstBomItem() {
        this.prodNo = "";
        this.partsNo = "";
        this.reqNum = 0;
    }

    /**
     * 親品目を設定する。
     *
     * @param prodNo 親品目
     */
    public void setProdNo(String prodNo) {
        this.prodNo = prodNo;
    }

    /**
     * 子品目を設定する。
     *
     * @param partsNo 子品目
     */
    public void setPartsNo(String partsNo) {
        this.partsNo = partsNo;
    }

    /**
     * 要求数を設定する。
     *
     * @param reqNum
     */
    public void setReqNum(Integer reqNum) {
        this.reqNum = reqNum;
    }

    /**
     * ユニット番号を設定する。
     * 
     * @param unitNo ユニット番号
     */
    public void setUnitNo(String unitNo) {
        this.unitNo = unitNo;
    }

}
