/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.common;

import adtekfuji.property.AdProperty;
import java.util.Properties;
import jp.adtekfuji.adFactory.enumerate.PurchaseTypeEnum;
import jp.adtekfuji.adFactory.enumerate.WarehouseMode;

/**
 * 設定クラス
 *
 * @author s-heya
 */
public class WarehouseConfig {

    private static final String CSV_STOCKOUT_KEY = "csv_stockout";
    private static final String OPERATION_MODE_KEY = "operationMode";
    private static final String OPERATION_MODE_SUPPLIEDPARTS_KEY = "operationModeSuppliedParts";

    /**
     * 欠品リストのCSVファイルのパスを取得する
     *
     * @return
     */
    public static String getCsvPathStockout() {
        Properties properties = AdProperty.getProperties();
        if (!properties.containsKey(CSV_STOCKOUT_KEY)) {
           properties.setProperty(CSV_STOCKOUT_KEY, System.getProperty("user.home") + "\\Desktop\\stockout.csv");
        }
        return properties.getProperty(CSV_STOCKOUT_KEY);
    }

    /**
     * 欠品リストのCSVファイルのパスを設定する
     *
     * @param value
     */
    public static void setCsvPathStockout(String value) {
        Properties properties = AdProperty.getProperties();
        properties.setProperty(CSV_STOCKOUT_KEY, value);
    }

    /**
     * 運用モードを返す。
     *
     * @param contorl
     * @return
     */
    public static int getOperationMode(String contorl) {
        if (PurchaseTypeEnum.SUPPLIED.equals(PurchaseTypeEnum.toPurchaseType(contorl))) {
            return WarehouseConfig.getOperationModeSuppliedParts();
        }
        return WarehouseConfig.getOperationMode();
    }

    /**
     * 運用モードを取得する
     *
     * @return
     */
    private static int getOperationMode() {
        try {
            Properties properties = AdProperty.getProperties();
            if (!properties.containsKey(OPERATION_MODE_KEY)) {
                properties.setProperty(OPERATION_MODE_KEY, "0");
            }
            return Integer.parseInt(properties.getProperty(OPERATION_MODE_KEY));
        }
        catch (Exception ex) {
            return 0;
        }
    }

    /**
     * 運用モード(支給品)を取得する
     *
     * @return
     */
    private static int getOperationModeSuppliedParts() {
        try {
            Properties properties = AdProperty.getProperties();
            if (!properties.containsKey(OPERATION_MODE_SUPPLIEDPARTS_KEY)) {
                properties.setProperty(OPERATION_MODE_SUPPLIEDPARTS_KEY, "0");
            }
            return Integer.parseInt(properties.getProperty(OPERATION_MODE_SUPPLIEDPARTS_KEY));
        }
        catch (Exception ex) {
            return 0;
        }
    }

    /**
     * 運用モードを取得する。
     * 
     * @return 運用モード
     */
    public static WarehouseMode getWarehouseMode() {
        return WarehouseMode.valueOf(AdProperty.getProperties().getProperty("wh_mode", "STANDARD"));
    }
}
