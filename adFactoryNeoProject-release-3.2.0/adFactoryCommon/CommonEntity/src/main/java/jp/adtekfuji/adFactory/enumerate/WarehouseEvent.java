/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 倉庫案内イベント
 *
 * @author s-heya
 */
public enum WarehouseEvent {
    // 受入
    RECIVE(10, "key.Accepted"),
    // 受入検査
    INSPECTION(11, "warehouse.inspection"),
    // 入庫
    ENTRY(20, "key.Warehousing"),
    // 完成入庫
    RECEIPT_PRODUCTION(21, "warehouse.production"),
    // 出庫
    LEAVE(30, "key.Picking"),
    // 出荷払出
    SHIPPING(31, "warehouse.shipping"),
    // 在庫払出
    DELIVERY(32, "warehouse.delivery"),
    // 棚卸
    INVENTORY(40, "key.ApplyInventory"),
    // 棚卸実施
    INVENTORY_IMPL(41, "key.InventoryImplementation"),
    // 資材移動
    MOVE(50, "key.Shelf"),
    // データ修正
    EDIT_DATA(60, "key.FixStock");

    private final Short id;
    private final String resourceKey;

    /**
     * コンストラクタ
     *
     * @param id
     * @param resourceKey
     */
    private WarehouseEvent(Integer id, String resourceKey) {
        this.id = id.shortValue();
        this.resourceKey = resourceKey;
    }

    /**
     * イベントIDを取得する。
     *
     * @return イベントID
     */
    public Short getId() {
        return id;
    }

    /**
     * リソースキーを取得する。
     * 
     * @return リソースキー
     */
    public String getResourceKey() {
        return resourceKey;
    }
    
    /**
     * リソース文字列を取得する。
     *
     * @param rb リソースバンドル
     * @return リソース文字列
     */
    public String getMessage(ResourceBundle rb) {
        return rb.getString(resourceKey);
    }

    /**
     * リソース文字列一覧を取得する。
     * 
     * @param rb リソースバンドル
     * @return リソース文字列一覧
     */
    public static List<String> getMessages(ResourceBundle rb) {
        List<String> messages = new ArrayList<>();
        for (WarehouseEvent event : WarehouseEvent.values()) {
            messages.add(rb.getString(event.resourceKey));
        }
        return messages;
    }

    /**
     * イベントIDをWarehouseEvent型に変換する。
     *
     * @param eventId イベントID
     * @return WarehouseEvent
     */
    public static WarehouseEvent valueOf(int eventId) {
        for (WarehouseEvent event : WarehouseEvent.values()) {
            if (event.getId() == eventId) {
                return event;
            }
        }
        return null;
    }
    
    public static WarehouseEvent get(int index) {
        if (WarehouseEvent.values().length <= index) {
            return null;
        }
        return WarehouseEvent.values()[index];
    }
    
    /**
     * イベントが等しいかどうかを返す
     *
     * @param id イベントID
     * @return 等しい場合はtrueを、それ以外の場合はfalseを返す。
     */
    public boolean equals(Integer id) {
        if (id == null) {
            return false;
        }
        return this.id.equals(id.shortValue());
    }
}
