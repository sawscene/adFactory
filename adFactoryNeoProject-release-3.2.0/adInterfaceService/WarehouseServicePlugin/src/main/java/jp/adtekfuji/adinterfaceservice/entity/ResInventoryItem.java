/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.entity;

import com.opencsv.bean.CsvBindByPosition;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.warehouse.LogStockInfo;
import jp.adtekfuji.adinterfaceservice.warehouseplugin.WarehouseConfig;

/**
 * 棚卸情報
 * 
 * @author s-heya
 */
public class ResInventoryItem {
    //@CsvBindByName(column = "品番", required = true)
    @CsvBindByPosition(position = 0)
    private String productNo;
    //@CsvBindByName(column = "倉庫コード", required = true)
    @CsvBindByPosition(position = 1)
    private String areaNo;
    //@CsvBindByName(column = "在庫数", required = true)
    @CsvBindByPosition(position = 2)
    private Integer inventoryNum;
    //@CsvBindByName(column = "担当者コード", required = true)
    @CsvBindByPosition(position = 3)
    private String personNo;
    //@CsvBindByName(column = "棚卸日", required = true)
    @CsvBindByPosition(position = 4)
    private String date;

    /**
     * コンストラクタ
     */
    public ResInventoryItem() {
    }
    
    /**
     * コンストラクタ
     * 
     * @param logStock 入出庫実績情報
     * @param areaCode 倉庫コード
     */
    public ResInventoryItem(LogStockInfo logStock, String areaCode) {
        this.productNo = logStock.getProductNo();
        this.areaNo = areaCode;
        this.inventoryNum = logStock.getEventNum();
        this.personNo = logStock.getPersonNo();
        this.date = logStock.getEventDate().format(WarehouseConfig.FORMAT_DATE);        
    }

    /**
     * 品番を取得する。
     * 
     * @return 品番
     */
    public String getProductNo() {
        return productNo;
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
     * 在庫数を取得する。
     * 
     * @return 在庫数
     */
    public Integer getInventoryNum() {
        return inventoryNum;
    }

    /**
     * 在庫数を設定する。
     * 
     * @param inventoryNum 在庫数
     */
    public void setInventoryNum(Integer inventoryNum) {
        this.inventoryNum = inventoryNum;
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
     * 棚卸日を取得する。
     * 
     * @return 棚卸日
     */
    public String getDate() {
        return date;
    }

    /**
     * 棚卸日を設定する。
     * 
     * @param date 棚卸日
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * ハッシュコードを返す。
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.productNo);
        hash = 89 * hash + Objects.hashCode(this.areaNo);
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
        final ResInventoryItem other = (ResInventoryItem) obj;
        if (!Objects.equals(this.productNo, other.productNo)) {
            return false;
        }
        if (!Objects.equals(this.areaNo, other.areaNo)) {
            return false;
        }
        return true;
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("ResInventoryItem{")
            .append("productNo=").append(this.productNo)
            .append(", areaNo=").append(this.areaNo)
            .append(", inventoryNum=").append(this.inventoryNum)
            .append(", personNo=").append(this.personNo)
            .append(", date=").append(this.date)
            .append("}")
            .toString();
    }
}
