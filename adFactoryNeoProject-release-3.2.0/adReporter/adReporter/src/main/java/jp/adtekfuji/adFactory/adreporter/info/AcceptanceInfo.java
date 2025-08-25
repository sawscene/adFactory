/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.adreporter.info;

import java.io.Serializable;

/**
 * 現品票情報
 *
 * @author nar-nakamura
 */
public class AcceptanceInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String chargePerson;
    private String figureNo;
    private String productNo;
    private String productName;
    private String lotNo;
    private String stockNum;

    /**
     * コンストラクタ
     */
    public AcceptanceInfo() {
    }

    /**
     * 担当者名を取得する。
     *
     * @return 担当者名
     */
    public String getChargePerson() {
        return this.chargePerson;
    }

    /**
     * 担当者名を設定する。
     *
     * @param chargePerson 担当者名
     */
    public void setChargePerson(String chargePerson) {
        this.chargePerson = chargePerson;
    }

    /**
     * 図番を取得する。
     *
     * @return 図番
     */
    public String getFigureNo() {
        return this.figureNo;
    }

    /**
     * 図番を設定する。
     *
     * @param figureNo 図番
     */
    public void setFigureNo(String figureNo) {
        this.figureNo = figureNo;
    }

    /**
     * 品目を取得する。
     *
     * @return 品目
     */
    public String getProductNo() {
        return this.productNo;
    }

    /**
     * 品目を設定する。
     *
     * @param productNo 品目
     */
    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    /**
     * 品名を取得する。
     *
     * @return 品名
     */
    public String getProductName() {
        return this.productName;
    }

    /**
     * 品名を設定する。
     *
     * @param productName 品名
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * ロット番号を取得する。
     * 
     * @return ロット番号
     */
    public String getLotNo() {
        return lotNo;
    }

    /**
     * ロット番号を設定する。
     * 
     * @param lotNo ロット番号
     */
    public void setLotNo(String lotNo) {
        this.lotNo = lotNo;
    }

    /**
     * 入庫数を取得する。
     * 
     * @return 入庫数
     */
    public String getStockNum() {
        return stockNum;
    }

    /**
     * 入庫数を設定する。
     * 
     * @param stockNum 入庫数
     */
    public void setStockNum(String stockNum) {
        this.stockNum = stockNum;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("AcceptanceInfo{")
                .append("chargePerson=")
                .append(chargePerson)
                .append(", figureNo=")
                .append(figureNo)
                .append(", productNo=")
                .append(productNo)
                .append(", productName=")
                .append(productName)
                .append(", lotNo=")
                .append(lotNo)
                .append(", stockNum=")
                .append(stockNum)
                .append("}")
                .toString();
    }

}
