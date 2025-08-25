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
 * 出荷払出情報
 * 
 * @author s-heya
 */
public class ResShippingItem {

    // 1) 払出担当者コード
    @CsvBindByPosition(position = 0)
    private String personNo;

    // 2) 払出担当者
    @CsvBindByPosition(position = 1)
    private String personName;

    // 3) 払出日(yyyy/mm/dd)
    @CsvBindByPosition(position = 2)
    private String date;

    // 4) 出荷No
    @CsvBindByPosition(position = 3)
    private String orderNo;

    // 5) 払出指示明細№
    @CsvBindByPosition(position = 4)
    private String deliveryNo;

    // 6) 品番
    @CsvBindByPosition(position = 5)
    private String productNo;

    // 7) 払出数
    @CsvBindByPosition(position = 6)
    private Integer deliveryNum;

    // 8) 単位 			▲ 空文字を設定
    @CsvBindByPosition(position = 7)
    private String unit;

    // 9) 原価計上課コード 	▲ 空文字を設定
    @CsvBindByPosition(position = 8)
    private String divisionNo;

    // 10) 原価計上課 		▲ 空文字を設定
    @CsvBindByPosition(position = 9)
    private String divisionName;

    // 11) 原価計上部門コード 	▲ 空文字を設定
    @CsvBindByPosition(position = 10)
    private String departmentNo;

    // 12) 原価計上部門 		▲ 空文字を設定
    @CsvBindByPosition(position = 11)
    private String departmentName;

    // 13) 倉庫コード		▲ 0010／0011／0012 のいづれかを設定
    @CsvBindByPosition(position = 12)
    private String areaNo;

    // 14) 倉庫 			▲ 本社／AS工場／吉川 VMｾﾝﾀｰ のいづれかを設定
    @CsvBindByPosition(position = 13)
    private String areaName;

    // 15) 棚番
    @CsvBindByPosition(position = 14)
    private String locationNo;

    // 16) 備考 			▲ 空文字を設定
    @CsvBindByPosition(position = 15)
    private String note;

    // 明細数
    private Integer itemNum;
    
    // 払出要求数
    private Integer requestNum;
    
    private Long eventId;
    
    private boolean flag;
    
    /**
     * コンストラクタ
     */
    public ResShippingItem() {
    }
    
    /**
     * コンストラクタ
     * 
     * @param logStock 入出庫実績情報
     * @param personName 払出担当者
     * @param areaCode 倉庫コード
     */
    public ResShippingItem(LogStockInfo logStock, String personName, String areaCode) {
        this.personNo = logStock.getPersonNo();
        this.personName = personName;
        this.date = logStock.getEventDate().format(WarehouseConfig.FORMAT_DATE);
        this.orderNo = logStock.getOrderNo();
        this.deliveryNo = logStock.getDeliveryNo();
        this.productNo = logStock.getProductNo();
        this.deliveryNum = logStock.getEventNum();
        this.unit = "";
        this.divisionNo = "";
        this.divisionName = "";
        this.departmentNo = "";
        this.departmentName = "";
        this.areaNo = areaCode;
        this.areaName = logStock.getAreaName();
        this.locationNo = "";
        this.note = "";
        this.itemNum = Integer.valueOf(logStock.getSerialNo());
        this.requestNum = logStock.getRequestNum();
        this.eventId = logStock.getEventId();
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

    public String getPersonName() {
        return personName;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getProductNo() {
        return productNo;
    }

    public String getUnit() {
        return unit;
    }

    public String getDivisionNo() {
        return divisionNo;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public String getDepartmentNo() {
        return departmentNo;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public String getAreaName() {
        return areaName;
    }

    public String getLocationNo() {
        return locationNo;
    }

    public String getNote() {
        return note;
    }

    public Integer getItemNum() {
        return itemNum;
    }
    
    /**
     * 払出要求数を取得する。
     * 
     * @return 払出要求数
     */
    public Integer getRequestNum() {
        return requestNum;
    }

    /**
     * イベントIDを取得する。
     * 
     * @return イベントID
     */
    public Long getEventId() {
        return eventId;
    }

    /**
     * 完結したデータかどうかを返す。
     * 
     * @return 
     */
    public boolean isFlag() {
        return flag;
    }

    /**
     * 完結フラグを設定する。
     * 
     * @param flag 
     */
    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    /**
     * ハッシュコードを返す。
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + Objects.hashCode(this.orderNo);
        hash = 43 * hash + Objects.hashCode(this.deliveryNo);
        return hash;
    }

    /**
     * オブジェクトを比較する。
     * 
     * @param obj オブジェクト
     * @return true: 一致、false: 不一致
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
        final ResShippingItem other = (ResShippingItem) obj;
        if (!Objects.equals(this.orderNo, other.orderNo)) {
            return false;
        }
        return Objects.equals(this.deliveryNo, other.deliveryNo);
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("ResShippingItem{")
            .append("deliveryNo=").append(this.deliveryNo)
            .append(", areaNo=").append(this.areaNo)
            .append(", locationNo=").append(this.locationNo)
            .append(", deliveryNum=").append(this.deliveryNum)
            .append(", requestNum=").append(this.requestNum)
            .append(", itemNum=").append(this.itemNum)
            .append(", personNo=").append(this.personNo)
            .append(", date=").append(this.date)
            .append("}")
            .toString();
    }
}
