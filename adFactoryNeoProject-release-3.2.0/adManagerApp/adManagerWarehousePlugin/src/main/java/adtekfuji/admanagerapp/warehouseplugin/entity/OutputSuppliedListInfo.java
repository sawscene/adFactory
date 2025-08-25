/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.entity;

import java.util.List;

/**
 * 支給品リスト出力情報
 * 
 * @author nar-nakamura
 */
public class OutputSuppliedListInfo {

    /**
     * 出力対象リスト
     */
    private List<DeliveryInfo> deliveryInfoList;

    /**
     * 
     */
    public OutputSuppliedListInfo() {
        
    }

    /**
     * 払出情報(支給品リスト出力対象)リストを設定する
     * @param value 
     */
    public void setDeliveryInfoList(List<DeliveryInfo> value) {
        this.deliveryInfoList = value;
    }

    /**
     * 払出情報(支給品リスト出力対象)リストを取得する
     * @return 
     */
    public List<DeliveryInfo> getDeliveryInfoList() {
        return this.deliveryInfoList;
    }
}
