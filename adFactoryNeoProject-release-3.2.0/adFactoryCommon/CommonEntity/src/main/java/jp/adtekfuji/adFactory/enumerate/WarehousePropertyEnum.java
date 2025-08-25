package jp.adtekfuji.adFactory.enumerate;

import adtekfuji.locale.LocaleUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ke.yokoi
 */
public enum WarehousePropertyEnum {
    /** ハンディ端末のシリアル番号 */
    HANDY_SERIAL("handy_serial", ""),
    /** 設備種別 */
    EQUIPMENT_TYPE("equipment_type", ""), //設備種別
    /** 設備種別(部品) */
    EQUIPMENT_TYPE_PARTS("parts", ""), //設備種別(部品)
    /** 設備種別(棚) */
    EQUIPMENT_TYPE_STRAGE("strage", ""), //設備種別(棚)
    /** 設備種別(台車) */
    EQUIPMENT_TYPE_CARRIAGE("carriage", ""),
    /** 設備種別(台車行先) */
    EQUIPMENT_TYPE_MOVE_LOCATION("move_location", ""),
    /** 在庫数 */
    INVENTORY("inventory", "key.StockQuantity"), //在庫数
    /** 仮在庫数 */
    INVENTORY_TEMP("inventory_temp", "key.TempStockQuantity"), //仮在庫数
    /** 引当在庫数 */
    RESERVE_INVENTORY("reserve_inventory", "key.ReserveInventory"), //引当在庫数
    /** 仕掛在庫数 */
    IN_PROCESS_INVENTORY("in_process_inventory", "key.InProcessInventory"), //仕掛在庫数
    /** 開始番号 */
    START_NUMBER("start_number", "key.StartNumber"), //開始番号
    /** 終了番号 */
    END_NUMBER("end_number", "key.CompNumber"), //終了番号
    /** 台数 */
    VOLUME("volume", ""), //台数
    /** ユニット名 */
    UNIT_NAME("unit_name", "key.UnitName"), //ユニット名
    /** 発注先コード */
    ACCEPT_CODE("accept_code", ""), //発注先コード
    /** 発注先名 */
    ACCEPT_NAME("accept_name", ""), //発注先名
    /** 払出先コード */
    PAYOUT_CODE("payout_code", "key.PayoutCode"), //払出先コード
    /** 払出先名 */
    PAYOUT_NAME("payout_name", "key.PayoutDestination"), //払出先名
    /** 管理区分 */
    MANAGEMENT("management", "key.ManagementName"), //管理区分
    /** 品名 */
    PRODUCT("product", "key.ProductName"), //品名
    /** 規格 */
    STANDARD("standard", "key.Standard"), //規格
    /** 材質 */
    MATERIAL("material", "key.Material"), //材質
    /** メーカ */
    MANUFACTURER("manufacturer", "key.Maker"), //メーカー
    /** 棚番 */
    STORAGE_NAME("storage_name", "key.StorageNumbers"), //棚番
    /** 部品識別名 */
    PARTS_NAME("parts_name", ""), //部品識別名
    /** 棚卸在庫数 */
    INVENTORY_STOCK("inventory_stock", "key.InventoryStockQuantity"), //棚卸在庫数
    /** 差異数 */
    DIFFERENCE("difference", "key.NumberOfDifferences"), //差異数
    /** 部品所属名 */
    AFFILIATION_NAME("affiliation_name", "key.AffiliationName"), //部品所属名
    /** 部品所属コード */
    AFFILIATION_CODE("affiliation_code", "key.AffiliationCode"), //部品所属コード
    /** 連番 */
    STKTAKE_LABEL_NO("stktake_label_no", "key.StktakeLabelNo"), //連番
    /** 図番 */
    FIGURE_NUMBER("figure_number", "key.FigureNumber"), //図番
    /** 受入数 */
    ACCESSION_COUNT("accession_count", "key.AccessionCount"), //受入数
    /** 受入状態 */
    ACCESSION_STATE("accession_state", ""), //受入状態
    /** 受入修正理由 */
    ACCESSION_REASON("accession_reason", ""), //受入状態
    /** 入荷ラベル一括印刷 */
    ACCEPT_LABEL_PRINT("accept_label_print", ""), //入荷ラベル一括印刷
    /** 作業者 */
    WORKING_PARSON("working_parson", ""), //作業者
    /** 要求数 */
    REQUEST_COUNT("request_count", "key.RequestNumber"), //要求数
    /** 入庫数 */
    RECEIPTS_COUNT("receipts_count", "key.Warehouses"), //入庫数
    /** 払出数 */
    PAYOUT_COUNT("payout_count", "key.PayoutNumber"), //払出数
    /** 欠品数 */
    MISSING_COUNT("missing_count", "key.MissingGoods"), //欠品数
    /** 移動前棚番 */
    BEFORE_STORAGE_NAME("before_storage_name", ""), //移動前棚番
    /** 移動後棚番 */
    AFTER_STORAGE_NAME("after_storage_name", ""), //移動後棚番
    /** 台車ステータス */
    CARRIAGE_STATUS("carriage_status", "");

    private final String name;
    private final String resourceKey;

    private WarehousePropertyEnum(String name, String resourceKey) {
        this.name = name;
        this.resourceKey = resourceKey;
    }

    public String getName() {
        return name;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public static String getMessage(ResourceBundle rb, WarehousePropertyEnum val) {
        WarehousePropertyEnum[] enumArray = WarehousePropertyEnum.values();
        for (WarehousePropertyEnum enumStr : enumArray) {
            if (enumStr.equals(val)) {
                return LocaleUtils.getString(enumStr.resourceKey);
            }
        }
        return "";
    }

    public static List<String> getMessages(ResourceBundle rb) {
        List<String> messages = new ArrayList<>();
        WarehousePropertyEnum[] enumArray = WarehousePropertyEnum.values();
        for (WarehousePropertyEnum enumStr : enumArray) {
            messages.add(LocaleUtils.getString(enumStr.resourceKey));
        }
        return messages;
    }

    public static WarehousePropertyEnum getEnum(String str) {
        WarehousePropertyEnum[] enumArray = WarehousePropertyEnum.values();
        for (WarehousePropertyEnum enumStr : enumArray) {
            if (str.equals(enumStr.getName())) {
                return enumStr;
            }
        }
        return null;
    }

}
