/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.entity;

/**
 * 納入情報
 * 
 * @author 14-0282
 */
public class ReqStoreInItem {

    private String supplyNo;
    private String orderNo;
    private Integer no;
    private String prodNo;
    private String prodName;
    private Integer arrNum;
    private String arrPlan;
    private Integer mod;
    private Integer del;
    private Integer category;
    private String material;
    private String vendor;
    private String spec;
    private String note;
    private String unitNo;

    /**
     * コンストラクタ
     */
    public ReqStoreInItem() {
        this.supplyNo = "";
        this.orderNo = "";
        this.no = 1;
        this.prodNo = "";
        this.prodName = "";
        this.arrNum = 0;
        this.arrPlan = "";
        this.mod = 0;
        this.del = 0;
    }
    
    /**
     * 納入番号 取得
     *
     * @return 納入番号
     */
    public String getSupplyNo() {
        return supplyNo;
    }

    /**
     * 納入番号 設定
     *
     * @param supplyNo 納入番号
     */
    public void setSupplyNo(String supplyNo) {
        this.supplyNo = supplyNo;
    }

    /**
     * 製造番号 取得
     *
     * @return 製造番号
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 製造番号 設定
     *
     * @param orderNo 製造番号
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 明細番号 取得
     *
     * @return 明細番号
     */
    public Integer getNo() {
        return no;
    }

    /**
     * 明細番号 設定
     *
     * @param no 明細番号
     */
    public void setNo(Integer no) {
        this.no = no;
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
     * 納入予定数 取得
     *
     * @return 納入予定数
     */
    public Integer getArrNum() {
        return arrNum;
    }

    /**
     * 納入予定数 設定
     *
     * @param arrNum 納入予定数
     */
    public void setArrNum(Integer arrNum) {
        this.arrNum = arrNum;
    }

    /**
     * 納入予定日 取得
     *
     * @return 納入予定日
     */
    public String getArrPlan() {
        return arrPlan;
    }

    /**
     * 納入予定日 設定
     *
     * @param arrPlan 納入予定日
     */
    public void setArrPlan(String arrPlan) {
        this.arrPlan = arrPlan;
    }

    /**
     * 修正フラグ 取得
     *
     * @return 修正フラグ
     */
    public Integer getMod() {
        return mod;
    }

    /**
     * 修正フラグ 設定
     *
     * @param mod 修正フラグ
     */
    public void setMod(Integer mod) {
        this.mod = mod;
    }

    /**
     * 削除フラグ 取得
     *
     * @return 削除フラグ
     */
    public Integer getDel() {
        return del;
    }

    /**
     * 削除フラグ 設定
     *
     * @param del 削除フラグ
     */
    public void setDel(Integer del) {
        this.del = del;
    }

    /**
     * 手配区分を取得する。
     * 
     * @return 手配区分 (1:支給品、2:購入品、3:加工品)
     */
    public Integer getCategory() {
        return category;
    }

    /**
     * 手配区分を設定する。
     * 
     * @param category 手配区分 (1:支給品、2:購入品、3:加工品)
     */
    public void setCategory(Integer category) {
        this.category = category;
    }

    /**
     * 材質を取得する。
     * @return 材質
     */
    public String getMaterial() {
        return material;
    }

    /**
     * 材質を設定する。
     * @param material 材質
     */
    public void setMaterial(String material) {
        this.material = material;
    }

    /**
     * メーカーを取得する。
     * @return メーカー
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * メーカーを設定する。
     * @param vendor メーカー
     */
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    /**
     * 規格・型式を取得する。
     * @return 規格・型式
     */
    public String getSpec() {
        return spec;
    }

    /**
     * 規格・型式を設定する。
     * @param spec 規格・型式
     */
    public void setSpec(String spec) {
        this.spec = spec;
    }

    /**
     * 備考を取得する。
     * @return 備考
     */
    public String getNote() {
        return note;
    }

    /**
     * 備考を設定する。
     * @param note 備考
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * ユニット番号を取得する。
     * 
     * @return ユニット番号
     */
    public String getUnitNo() {
        return unitNo;
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
