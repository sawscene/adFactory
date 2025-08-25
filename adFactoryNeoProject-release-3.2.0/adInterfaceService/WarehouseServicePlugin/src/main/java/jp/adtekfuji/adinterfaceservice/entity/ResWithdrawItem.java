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
 * 出庫実績情報
 * 
 * @author s-heya
 */
public class ResWithdrawItem {
    //@CsvBindByName(column = "出庫依頼番号", required = true)
    @CsvBindByPosition(position = 0)
    private String deliveryNo;
    //@CsvBindByName(column = "倉庫コード", required = true)
    @CsvBindByPosition(position = 1)
    private String areaNo;
    //@CsvBindByName(column = "出庫数", required = true)
    @CsvBindByPosition(position = 2)
    private Integer deliveryNum;
    //@CsvBindByName(column = "担当者コード", required = true)
    @CsvBindByPosition(position = 3)
    private String personNo;
    //@CsvBindByName(column = "出庫日", required = true)
    @CsvBindByPosition(position = 4)
    private String date;

    /**
     * コンストラクタ
     */
    public ResWithdrawItem() {
    }
    
    /**
     * コンストラクタ
     * 
     * @param logStock 入出庫実績情報
     * @param areaCode 倉庫コード
     */
    public ResWithdrawItem(LogStockInfo logStock, String areaCode) {
        this.deliveryNo = logStock.getDeliveryNo();
        this.areaNo = areaCode;
        this.deliveryNum = logStock.getEventNum();
        this.personNo = logStock.getPersonNo();
        this.date = logStock.getEventDate().format(WarehouseConfig.FORMAT_DATE);        
    }

    /**
     * 出庫依頼番号を取得する。
     * 
     * @return 出庫依頼番号
     */
    public String getDeliveryNo() {
        return deliveryNo;
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
     * 出庫数を取得する。
     * 
     * @return 出庫数
     */
    public Integer getDeliveryNum() {
        return deliveryNum;
    }

    /**
     * 出庫数を設定する。
     * 
     * @param deliveryNum 出庫数
     */
    public void setDeliveryNum(Integer deliveryNum) {
        this.deliveryNum = deliveryNum;
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
     * 出庫日を取得する。
     * 
     * @return 出庫日
     */
    public String getDate() {
        return date;
    }
    
    /**
     * 出庫日を設定する。
     * 
     * @param date 出庫日
     */
    public void setDate(String date) {
        this.date = date;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.deliveryNo);
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
        final ResWithdrawItem other = (ResWithdrawItem) obj;
        return Objects.equals(this.deliveryNo, other.deliveryNo);
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("ResWithdrawItem{")
            .append("deliveryNo=").append(this.deliveryNo)
            .append(", areaNo=").append(this.areaNo)
            .append(", deliveryNum=").append(this.deliveryNum)
            .append(", personNo=").append(this.personNo)
            .append(", date=").append(this.date)
            .append("}")
            .toString();
    }
}
