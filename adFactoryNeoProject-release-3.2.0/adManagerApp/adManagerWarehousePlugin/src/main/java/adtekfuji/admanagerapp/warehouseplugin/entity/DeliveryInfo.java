/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.entity;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import jp.adtekfuji.adFactory.entity.warehouse.TrnDeliveryInfo;

/**
 * 出庫指示情報
 * 
 * @author
 */
public class DeliveryInfo {

    private final BooleanProperty selected = new SimpleBooleanProperty();
    private TrnDeliveryInfo trnDelivery;

    /**
     * コンストラクタ
     */
    public DeliveryInfo() {
    }

    /**
     * コンストラクタ
     * 
     * @param trnDelivery 
     */
    public DeliveryInfo(TrnDeliveryInfo trnDelivery) {
        this.trnDelivery = trnDelivery;
    }

    /**
     * 選択プロパティ
     * 
     * @return 
     */
    public BooleanProperty selectedProperty() {
        return selected;
    }

    /**
     * 選択されているかどうかを返す。
     * 
     * @return 
     */
    public Boolean isSelected() {
        return selected.get();
    }

    /**
     * 選択を設定する。
     * 
     * @param selected 
     */
    public void setSelected(Boolean selected) {
        this.selected.set(selected);
    }

    /**
     * 出庫指示情報を取得する。
     * 
     * @return 出庫指示情報
     */
    public TrnDeliveryInfo getValue() {
        return trnDelivery;
    }
}
