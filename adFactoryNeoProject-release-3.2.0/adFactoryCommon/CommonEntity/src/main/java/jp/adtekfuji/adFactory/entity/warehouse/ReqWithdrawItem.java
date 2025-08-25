/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.warehouse;

/**
 * 出庫指示情報
 * 
 * @author 
 */
public class ReqWithdrawItem {

    private String deliveryNo;
    private Integer no;
    private String orderNo;
    private String type;
    private String prodNo;
    private String prodName;
    private String vendor;
    private String spec;
    private String unit;
    private Integer reqNum;
    private String due;
    private Integer mod;
    private Integer del;
    private String unitNo;
    private String modelName;
    private String locationNo;
    private Integer usageNum;
    private Integer arrange;
    private String arrangeNo;
    private Integer deliveryRule;
    
    /**
     * コンストラクタ
     *
     */
    public ReqWithdrawItem() {
        this.deliveryNo = "";
        this.no = 1;
        this.orderNo = "";
        this.prodNo = "";
        this.prodName = "";
        this.reqNum = 0;
        this.due = "";
        this.mod = 0;
        this.del = 0;
        this.deliveryRule = 1;
    }

    /**
     * 出庫番号 取得
     *
     * @return 出庫番号
     */
    public String getDeliveryNo() {
        return deliveryNo;
    }

    /**
     * 出庫番号 設定
     *
     * @param deliveryNo 出庫番号
     */
    public void setDeliveryNo(String deliveryNo) {
        this.deliveryNo = deliveryNo;
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
     * 品番分類 取得
     *
     * @return 品番分類
     */
    public String getType() {
        return type;
    }

    /**
     * 品番分類 設定
     *
     * @param type 品番分類
     */
    public void setType(String type) {
        this.type = type;
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
     * 要求数 取得
     *
     * @return 要求数
     */
    public Integer getReqNum() {
        return reqNum;
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

    /**
     * 要求数 設定
     *
     * @param reqNum 要求数
     */
    public void setReqNum(Integer reqNum) {
        this.reqNum = reqNum;
    }

    /**
     * 出庫予定日 取得
     *
     * @return 出庫予定日
     */
    public String getDue() {
        return due;
    }

    /**
     * 出庫予定日 設定
     *
     * @param due 出庫予定日
     */
    public void setDue(String due) {
        this.due = due;
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
     * 機種名を取得する。
     * 
     * @return 機種名
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * 機種名を設定する。
     * 
     * @param modelName 機種名
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
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

    /**
     * 棚番号を取得する。
     * 
     * @return 棚番号
     */
    public String getLocationNo() {
        return locationNo;
    }

    /**
     * 棚番号を設定する。
     * 
     * @param locationNo 
     */
    public void setLocationNo(String locationNo) {
        this.locationNo = locationNo;
    }    

    /**
     * 使用数を取得する。
     * 
     * @return 使用数
     */
    public Integer getUsageNum() {
        return usageNum;
    }

    /**
     * 使用数を設定する。
     * 
     * @param usageNum 使用数
     */
    public void setUsageNum(Integer usageNum) {
        this.usageNum = usageNum;
    }

    /**
     * 手配区分を取得する。
     * 
     * @return 手配区分
     */
    public Integer getArrange() {
        return arrange;
    }

    /**
     * 手配区分を設定する。
     * 
     * @param arrange 手配区分
     */
    public void setArrange(Integer arrange) {
        this.arrange = arrange;
    }

    /**
     * 先行手配番号を取得する。
     * 
     * @return 先行手配番号
     */
    public String getArrangeNo() {
        return arrangeNo;
    }

    /**
     * 先行手配番号を設定する。
     * 
     * @param arrangeNo 先行手配番号
     */
    public void setArrangeNo(String arrangeNo) {
        this.arrangeNo = arrangeNo;
    }

    /**
     * 出庫ルールを取得する。
     * 
     * @return 出庫ルール
     */
    public Integer getDeliveryRule() {
        return deliveryRule;
    }

    /**
     * 出庫ルールを設定する。
     * 
     * @param deliveryRule 出庫ルール
     */
    public void setDeliveryRule(Integer deliveryRule) {
        this.deliveryRule = deliveryRule;
    }
}
