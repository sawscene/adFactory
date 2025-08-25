/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.entity;

import adtekfuji.utility.StringUtils;
import com.opencsv.bean.CsvBindByPosition;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.warehouse.LogStockInfo;
import jp.adtekfuji.adinterfaceservice.warehouseplugin.WarehouseConfig;

/**
 * 入庫実績情報
 * 
 * @author s-heya
 */
public class ResStoreInItem {
    //@CsvBindByName(column = "納入番号", required = true)
    @CsvBindByPosition(position = 0)
    private String supplyNo;
    //@CsvBindByName(column = "製番(工事番号)", required = true)
    @CsvBindByPosition(position = 1)
    private String orderNo;
    //@CsvBindByName(column = "製品№(枝番)", required = true)
    @CsvBindByPosition(position = 2)
    private String serialNo;
    //@CsvBindByName(column = "倉庫コード", required = true)
    @CsvBindByPosition(position = 3)
    private String areaNo;
    //@CsvBindByName(column = "入庫数", required = true)
    @CsvBindByPosition(position = 4)
    private Integer stockNum;
    //@CsvBindByName(column = "担当者コード", required = true)
    @CsvBindByPosition(position = 5)
    private String personNo;
    //@CsvBindByName(column = "入庫日", required = true)
    @CsvBindByPosition(position = 6)
    private String date;

    /**
     * コンストラクタ
     */
    public ResStoreInItem() {
    }
    
    /**
     * コンストラクタ
     * 
     * @param logStock 入出庫実績情報
     * @param areaCode 倉庫コード
     */
    public ResStoreInItem(LogStockInfo logStock, String areaCode) {
        if (logStock.getCategory() == 2) {
            // 購入品は納入番号を出力
            this.supplyNo = logStock.getSupplyNo();
        }
        this.orderNo = logStock.getOrderNo();
        this.serialNo = logStock.getSerialNo();
        this.areaNo = areaCode;
        this.stockNum = logStock.getEventNum();
        this.personNo = logStock.getPersonNo();
        this.date = logStock.getEventDate().format(WarehouseConfig.FORMAT_DATE);        
    }

    /**
     * 納入番号を取得する。
     * 
     * @return 納入番号
     */
    public String getSupplyNo() {
        return supplyNo;
    }

    /**
     * 製番(工事番号)を取得する。
     * 
     * @return 製番(工事番号)
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 製品№(枝番)を取得する。
     * 
     * @return 製品№(枝番)
     */
    public String getSerialNo() {
        return serialNo;
    }

    /**
     * 倉庫コードを取得する。
     * 
     * @return 倉庫コード
     */
    public String getAreaNo() {
        return areaNo;
    }

    /**
     * 入庫数を取得する。
     * 
     * @return 入庫数
     */
    public Integer getStockNum() {
        return stockNum;
    }

    /**
     * 入庫数を設定する。
     * 
     * @param stockNum 入庫数
     */
    public void setStockNum(Integer stockNum) {
        this.stockNum = stockNum;
    }

    /**
     * 担当者コードを取得する。
     * 
     * @return 担当者コード
     */
    public String getPersonNo() {
        return personNo;
    }
    
    /**
     * 担当者コードを設定する。
     * 
     * @param personNo 担当者コード
     */
    public void setPersonNo(String personNo) {
        this.personNo = personNo;
    }
    
    /**
     * 入庫日を取得する。
     * 
     * @return 棚卸日
     */
    public String getDate() {
        return date;
    }

    /**
     * 入庫日を設定する。
     * 
     * @param date 出庫日
     */
    public void setDate(String date) {
        this.date = date;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.orderNo);
        hash = 61 * hash + Objects.hashCode(this.serialNo);
        return hash;
    }

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
        final ResStoreInItem other = (ResStoreInItem) obj;
        if (!StringUtils.isEmpty(this.supplyNo)) {
            // 納入番号
            return Objects.equals(this.supplyNo, other.supplyNo);
        }
        if (!Objects.equals(this.orderNo, other.orderNo)) {
            return false;
        }
        return Objects.equals(this.serialNo, other.serialNo);
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("ResStoreInItem{")
            .append("supplyNo=").append(this.supplyNo)
            .append(", orderNo=").append(this.orderNo)
            .append(", serialNo=").append(this.serialNo)
            .append(", areaNo=").append(this.areaNo)
            .append(", stockNum=").append(this.stockNum)
            .append(", personNo=").append(this.personNo)
            .append(", date=").append(this.date)
            .append("}")
            .toString();
    }
}
