/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.entity;

/**
 *
 * @author 18-0326
 */
public class MstStockItem {

    private String prodNo;
    private String area;
    private String loc;
    private int rank;

    /**
     * コンストラクタ
     */
    public MstStockItem() {
        this.prodNo = "";
        this.area = "";
        this.loc = "";
        this.rank = 0;
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
     * 区画 取得
     *
     * @return 区画
     */
    public String getArea() {
        return area;
    }

    /**
     * 区画 設定
     *
     * @param area 区画
     */
    public void setArea(String area) {
        this.area = area;
    }

    /**
     * 棚番号 取得
     *
     * @return 棚番号
     */
    public String getLoc() {
        return loc;
    }

    /**
     * 棚番号 設定
     *
     * @param loc 棚番号
     */
    public void setLoc(String loc) {
        this.loc = loc;
    }

    /**
     * 重要度ランク 取得
     *
     * @return 重要度
     */
    public int getRank() {
        return rank;
    }

    /**
     * 重要度ランク 設定
     *
     * @param rank 重要度
     */
    public void setRank(int rank) {
        this.rank = rank;
    }
}
