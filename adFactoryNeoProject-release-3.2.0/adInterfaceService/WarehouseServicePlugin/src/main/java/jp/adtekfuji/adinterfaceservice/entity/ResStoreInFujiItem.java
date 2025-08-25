/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.entity;

import com.opencsv.bean.CsvBindByPosition;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.warehouse.LogStockInfo;

/**
 * FUJI支給品の入庫実績情報
 * 
 * @author s-heya
 */
public class ResStoreInFujiItem {
    public static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    @CsvBindByPosition(position = 0)
    private String issueDate; // 発行日
    @CsvBindByPosition(position = 1)
    private String supplier; // 支給元
    @CsvBindByPosition(position = 2)
    private String personNo; // 出庫者
    @CsvBindByPosition(position = 3)
    private String partnerId; // 取引先コード
    @CsvBindByPosition(position = 4)
    private String partnerName; // 取引先名
    @CsvBindByPosition(position = 5)
    private String productNo; // 品目
    @CsvBindByPosition(position = 6)
    private String productName; // 品名
    @CsvBindByPosition(position = 7)
    private String material; // 材質
    @CsvBindByPosition(position = 8)
    private String manufacturer; // メーカー
    @CsvBindByPosition(position = 9)
    private String spec; // 規格・型式
    @CsvBindByPosition(position = 10)
    private String serialNo; // シリアル番号
    @CsvBindByPosition(position = 11)
    private Integer totalNum; // 合計支給数
    @CsvBindByPosition(position = 12)
    private String supplyNo; // 倉庫オーダ番号 
    @CsvBindByPosition(position = 13)
    private String pos; // 位置番号
    @CsvBindByPosition(position = 14)
    private String seq; // 順序
    @CsvBindByPosition(position = 15)
    private String purchaseNo; // 購買オーダ番号
    @CsvBindByPosition(position = 16)
    private String purchaseRemoved; // 購買オーダ削除フラグ
    @CsvBindByPosition(position = 17)
    private String orderNo; // 製造オーダ番号
    @CsvBindByPosition(position = 18)
    private String removed; // 資材削除フラグ
    @CsvBindByPosition(position = 19)
    private String estimatePos; // 見積資材位置
    @CsvBindByPosition(position = 20)
    private String overlap; // 重複フラグ
    @CsvBindByPosition(position = 21)
    private Integer expectedNum; // 予定数
    @CsvBindByPosition(position = 22)
    private Integer supplyNum; // 支給数
    @CsvBindByPosition(position = 23)
    private String unit; // 単位
    @CsvBindByPosition(position = 24)
    private String date; // 入庫日
    @CsvBindByPosition(position = 25)
    private String vendorFlag; // 入庫日
    @CsvBindByPosition(position = 26)
    private String areaCode; // 倉庫コード

    /**
     * コンストラクタ
     */
    public ResStoreInFujiItem() {
    }
    
    /**
     * コンストラクタ
     * 
     * @param logStock 入出庫実績情報
     * @param areaCode 倉庫コード
     */
    public ResStoreInFujiItem(LogStockInfo logStock, String areaCode) {
        this.issueDate = logStock.getEventDate().format(FORMAT_DATE);
        this.supplier = "";
        this.personNo = logStock.getPersonNo();
        this.partnerId = "";
        this.partnerName = "";
        this.productNo = logStock.getProductNo();
        this.productName = logStock.getProductName();
        this.material = "";
        this.manufacturer = "";
        this.spec = "";
        this.serialNo = ""; 
        this.totalNum = logStock.getInStockNum();
        this.supplyNo = logStock.getSupplyNo();
        this.pos = "";
        this.seq = "";
        this.purchaseNo = "";
        this.purchaseRemoved = "";
        this.orderNo = "";
        this.removed = "";
        this.estimatePos = "10";
        this.overlap = "";
        this.expectedNum = 0;
        this.supplyNum = logStock.getEventNum();
        this.unit = "";
        this.date = logStock.getEventDate().format(FORMAT_DATE);        
        this.vendorFlag = "1";
        this.areaCode = areaCode;
    }

    /**
     * 発行日を取得する。
     * 
     * @return 発行日
     */
    public String getDate() {
        return date;
    }

    /**
     * 発行日を設定する。
     * 
     * @param date 発行日 
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * 出庫者を取得する。
     * 
     * @return 出庫者
     */
    public String getPersonNo() {
        return personNo;
    }

    /**
     * 出庫者を設定する。
     * 
     * @param personNo  出庫者
     */
    public void setPersonNo(String personNo) {
        this.personNo = personNo;
    }

    /**
     * 合計支給数を取得する。
     * 
     * @return 合計支給数
     */
    public Integer getTotalNum() {
        return totalNum;
    }

    /**
     * 合計支給数を設定する。
     * 
     * @param totalNum 合計支給数
     */
    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    /**
     * 今回支給数を取得する。
     * 
     * @return 今回支給数
     */
    public Integer getSupplyNum() {
        return supplyNum;
    }

    /**
     * 今回支給数を設定する。
     * 
     * @param supplyNum 今回支給数
     */
    public void setSupplyNum(Integer supplyNum) {
        this.supplyNum = supplyNum;
    }

    /**
     * 発行日を取得する。
     * 
     * @return 発行日
     */
    public String getIssueDate() {
        return issueDate;
    }

    /**
     * 支給元を取得する。
     * 
     * @return 支給元
     */
    public String getSupplier() {
        return supplier;
    }

    /**
     * 取引先コードを取得する。
     * 
     * @return 取引先コード
     */
    public String getPartnerId() {
        return partnerId;
    }

    /**
     * 取引先名を取得する。
     * 
     * @return 取引先名
     */
    public String getPartnerName() {
        return partnerName;
    }

    /**
     * 品目を取得する。
     * 
     * @return 品目
     */
    public String getProductNo() {
        return productNo;
    }

    /**
     * 品名を取得する。
     * 
     * @return 品名
     */
    public String getProductName() {
        return productName;
    }

    /**
     * 材質を取得する。
     * 
     * @return 材質
     */
    public String getMaterial() {
        return material;
    }

    /**
     * メーカーを取得する。
     * 
     * @return メーカー
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * 規格・型式を取得する。
     * 
     * @return 規格・型式
     */
    public String getSpec() {
        return spec;
    }

    /**
     * シリアル番号を取得する。
     * 
     * @return シリアル番号
     */
    public String getSerialNo() {
        return serialNo;
    }

    /**
     * 倉庫オーダ番号を取得する。
     * 
     * @return 倉庫オーダ番号
     */
    public String getSupplyNo() {
        return supplyNo;
    }

    /**
     * 位置番号を取得する。
     * 
     * @return 位置番号
     */
    public String getPos() {
        return pos;
    }

    /**
     * 順序を取得する。
     * 
     * @return 順序
     */
    public String getSeq() {
        return seq;
    }

    /**
     * 購買オーダ番号を取得する。
     * 
     * @return 購買オーダ番号
     */
    public String getPurchaseNo() {
        return purchaseNo;
    }

    /**
     * 購買オーダ削除フラグを取得する。
     * 
     * @return 購買オーダ削除フラグ
     */
    public String getPurchaseRemoved() {
        return purchaseRemoved;
    }

    /**
     * 製造オーダ番号を取得する。
     * 
     * @return 製造オーダ番号
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 資材削除フラグを取得する。
     * 
     * @return 資材削除フラグ
     */
    public String getRemoved() {
        return removed;
    }

    /**
     * 見積資材位置を取得する。
     * 
     * @return 見積資材位置
     */
    public String getEstimatePos() {
        return estimatePos;
    }

    /**
     * 重複フラグを取得する。
     * 
     * @return 重複フラグ
     */
    public String getOverlap() {
        return overlap;
    }

    /**
     * 予定数を取得する。
     * 
     * @return 予定数
     */
    public Integer getExpectedNum() {
        return expectedNum;
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
     * EDI業者区分を取得する。
     * 
     * @return EDI業者区分
     */
    public String getVendorFlag() {
        return vendorFlag;
    }

    /**
     * 倉庫コードを取得する。
     * 
     * @return 倉庫コード 
     */
    public String getAreaCode() {
        return areaCode;
    }

    /**
     * ハッシュコードを返す。
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.supplyNo);
        return hash;
    }

    /**
     * オブジェクトを比較する。
     * 
     * @param obj オブジェクト
     * @return true: 同じである、false: 異なる
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
        final ResStoreInFujiItem other = (ResStoreInFujiItem) obj;
        return Objects.equals(this.supplyNo, other.supplyNo);
    }

}
