/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.warehouseservicetesttool.entity;

/**
 * ハンディ端末 棚卸データ
 *
 * @author nar-nakamura
 */
public class BhtStktake {

    private String control;
    private String chartNo;
    private String articleName;
    private String standard;
    private String maker;
    private String rackNo;
    private String stockNum;
    private String truthNum;
    private String workDate;
    private String workTime;
    private String workerID;
    private String workerName;
    private String affiliName;
    private String affiliCode;
    private String labelNo;

    public BhtStktake() {

    }

    /**
     * 棚卸データ
     *
     * @param control 部品識別名 (管理区分＋図番)
     * @param chartNo 図番
     * @param articleName 品名
     * @param standard 規格
     * @param maker メーカ
     * @param rackNo 棚番
     * @param stockNum 数量 (在庫数)
     * @param truthNum 数量 (実際の数)
     * @param workDate 作業日付 (yy/MM/dd)
     * @param workTime 作業時刻 (hh:mm)
     * @param workerID 作業者ID
     * @param workerName 作業者名
     * @param affiliName 部品所属名
     * @param affiliCode 部品所属コード
     * @param labelNo 連番
     */
    public BhtStktake(
            String control, String chartNo,
            String articleName, String standard, String maker,
            String rackNo, String stockNum, String truthNum,
            String workDate, String workTime, String workerID,
            String workerName, String affiliName, String affiliCode, String labelNo) {
        this.control = control;
        this.chartNo = chartNo;
        this.articleName = articleName;
        this.standard = standard;
        this.maker = maker;
        this.rackNo = rackNo;
        this.stockNum = stockNum;
        this.truthNum = truthNum;
        this.workDate = workDate;
        this.workTime = workTime;
        this.workerID = workerID;
        this.workerName = workerName;
        this.affiliName = affiliName;
        this.affiliCode = affiliCode;
        this.labelNo = labelNo;
    }

    /**
     * 部品識別名 (管理区分＋図番)
     *
     * @return
     */
    public String getControl() {
        return this.control;
    }

    /**
     * 部品識別名 (管理区分＋図番)
     *
     * @param control
     */
    public void setControl(String control) {
        this.control = control;
    }

    /**
     * 図番
     *
     * @return
     */
    public String getChartNo() {
        return this.chartNo;
    }

    /**
     * 図番
     *
     * @param chartNo
     */
    public void setChartNo(String chartNo) {
        this.chartNo = chartNo;
    }

    /**
     * 品名
     *
     * @return
     */
    public String getArticleName() {
        return this.articleName;
    }

    /**
     * 品名
     *
     * @param articleName
     */
    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    /**
     * 規格
     *
     * @return
     */
    public String getStandard() {
        return this.standard;
    }

    /**
     * 規格
     *
     * @param standard
     */
    public void setStandard(String standard) {
        this.standard = standard;
    }

    /**
     * メーカ
     *
     * @return
     */
    public String getMaker() {
        return this.maker;
    }

    /**
     * メーカ
     *
     * @param maker
     */
    public void setMaker(String maker) {
        this.maker = maker;
    }

    /**
     * 棚番
     *
     * @return
     */
    public String getRackNo() {
        return this.rackNo;
    }

    /**
     * 棚番
     *
     * @param rackNo
     */
    public void setRackNo(String rackNo) {
        this.rackNo = rackNo;
    }

    /**
     * 数量 (在庫数)
     *
     * @return
     */
    public String getStockNum() {
        return this.stockNum;
    }

    /**
     * 数量 (在庫数)
     *
     * @param stockNum
     */
    public void setStockNum(String stockNum) {
        this.stockNum = stockNum;
    }

    /**
     * 数量 (実際の数)
     *
     * @return
     */
    public String getTruthNum() {
        return this.truthNum;
    }

    /**
     * 数量 (実際の数)
     *
     * @param truthNum
     */
    public void setTruthNum(String truthNum) {
        this.truthNum = truthNum;
    }

    /**
     * 作業日付 (yy/MM/dd)
     *
     * @return
     */
    public String getWorkDate() {
        return this.workDate;
    }

    /**
     * 作業日付 (yy/MM/dd)
     *
     * @param workDate
     */
    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }

    /**
     * 作業時刻 (hh:mm)
     *
     * @return
     */
    public String getWorkTime() {
        return this.workTime;
    }

    /**
     * 作業時刻 (hh:mm)
     *
     * @param workTime
     */
    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

    /**
     * 作業者ID
     *
     * @return
     */
    public String getWorkerID() {
        return this.workerID;
    }

    /**
     * 作業者ID
     *
     * @param workerID
     */
    public void setWorkerID(String workerID) {
        this.workerID = workerID;
    }

    /**
     * 作業者名
     *
     * @return
     */
    public String getWorkerName() {
        return this.workerName;
    }

    /**
     * 作業者名
     *
     * @param workerName
     */
    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    /**
     * 部品所属名
     *
     * @return
     */
    public String getAffiliName() {
        return this.affiliName;
    }

    /**
     * 部品所属名
     *
     * @param affiliName
     */
    public void setAffiliName(String affiliName) {
        this.affiliName = affiliName;
    }

    /**
     * 部品所属コード
     *
     * @return
     */
    public String getAffiliCode() {
        return this.affiliCode;
    }

    /**
     * 部品所属コード
     *
     * @param affiliCode
     */
    public void setAffiliCode(String affiliCode) {
        this.affiliCode = affiliCode;
    }

    /**
     * 連番
     *
     * @return
     */
    public String getLabelNo() {
        return this.labelNo;
    }

    /**
     * 連番
     *
     * @param labelNo
     */
    public void setLabelNo(String labelNo) {
        this.labelNo = labelNo;
    }

    @Override
    public String toString() {
        return "BhtStktake{"
                + ", control=" + this.control
                + ", chartNo=" + this.chartNo
                + ", articleName=" + this.articleName
                + ", standard=" + this.standard
                + ", maker=" + this.maker
                + ", rackNo=" + this.rackNo
                + ", stockNum=" + this.stockNum
                + ", truthNum=" + this.truthNum
                + ", workDate=" + this.workDate
                + ", workTime=" + this.workTime
                + ", workerID=" + this.workerID
                + ", workerName=" + this.workerName
                + ", affiliName=" + this.affiliName
                + ", affiliCode=" + this.affiliCode
                + ", labelNo=" + this.labelNo
                + "}";
    }
}
