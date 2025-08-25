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
 * TECHSの入庫実績情報
 * 0801015 入庫データ取込シート_V62SR4_01.xlsx に基づく
 * 
 * @author s-heya
 */
public class ResStoreInTechsItem {
    public static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    // 調整
    @CsvBindByPosition(position = 0)
    private String coordinate;
    // 入庫担当者コード★
    @CsvBindByPosition(position = 1)
    private String personNo;
    // 入庫担当者
    @CsvBindByPosition(position = 2)
    private String personName;
    // 入庫日★
    @CsvBindByPosition(position = 3)
    private String date;
    // 品番★
    @CsvBindByPosition(position = 4)
    private String productNo;
    // 倉庫コード★
    @CsvBindByPosition(position = 5)
    private String areaCode;
    // 倉庫
    @CsvBindByPosition(position = 6)
    private String areaName;
    // 棚番
    @CsvBindByPosition(position = 7)
    private String locationNo;
    // ロットNo
    @CsvBindByPosition(position = 8)
    private String orderNo;
    // チャージNo
    @CsvBindByPosition(position = 9)
    private String chargeNo;
    // 製造日
    @CsvBindByPosition(position = 10)
    private String manufactureDate;
    // 元製番
    @CsvBindByPosition(position = 11)
    private String oldOrderNo;
    // 元製品No
    @CsvBindByPosition(position = 12)
    private String oldProductNo;
    // 入庫数★
    @CsvBindByPosition(position = 13)
    private Integer stockNum;
    // 単位
    @CsvBindByPosition(position = 14)
    private String unit;
    // 単価入力区分★
    @CsvBindByPosition(position = 15)
    private String category;
    // 単価
    @CsvBindByPosition(position = 16)
    private String unitPrice;
    // 金額
    @CsvBindByPosition(position = 17)
    private String price;
    // 備考
    @CsvBindByPosition(position = 18)
    private String note;

    /**
     * コンストラクタ
     */
    public ResStoreInTechsItem() {
    }
    
    /**
     * コンストラクタ
     * 
     * @param logStock 入出庫実績情報
     * @param areaCode 倉庫コード
     */
    public ResStoreInTechsItem(LogStockInfo logStock, String areaCode) {
        this.coordinate = "";
        this.personNo = logStock.getPersonNo();
        this.personName = "";
        this.date = logStock.getEventDate().format(FORMAT_DATE);        
        this.productNo = logStock.getProductNo();
        this.areaCode = areaCode;
        this.areaName = "";
        this.locationNo = "";
        this.orderNo = "";
        this.chargeNo = "";
        this.manufactureDate = "";
        this.oldOrderNo = "";
        this.oldProductNo = "";
        this.stockNum = logStock.getEventNum();
        this.unit = "";
        this.category = "S0";
        this.unitPrice = "";
        this.price = "";
        this.note = logStock.getSupplyNo();
    }

    public String getCoordinate() {
        return coordinate;
    }

    public String getPersonNo() {
        return personNo;
    }

    public String getPersonName() {
        return personName;
    }

    public String getDate() {
        return date;
    }

    public String getProductNo() {
        return productNo;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public String getLocationNo() {
        return locationNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getChargeNo() {
        return chargeNo;
    }

    public String getManufactureDate() {
        return manufactureDate;
    }

    public String getOldOrderNo() {
        return oldOrderNo;
    }

    public String getOldProductNo() {
        return oldProductNo;
    }

    public Integer getStockNum() {
        return stockNum;
    }

    public String getUnit() {
        return unit;
    }

    public String getCategory() {
        return category;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public String getPrice() {
        return price;
    }

    public String getNote() {
        return note;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStockNum(Integer stockNum) {
        this.stockNum = stockNum;
    }

    /**
     * ハッシュコードを返す。
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.note);
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
        final ResStoreInTechsItem other = (ResStoreInTechsItem) obj;
        return Objects.equals(this.note, other.note);
    }

}
