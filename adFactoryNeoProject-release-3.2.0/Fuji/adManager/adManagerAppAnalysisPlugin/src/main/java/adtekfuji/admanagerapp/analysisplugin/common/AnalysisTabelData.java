/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.analysisplugin.common;

/**
 * 分析画面テーブル情報クラス
 *
 * @author e-mori
 * @version 1.4.2
 * @since 2016.08.01.Mon
 */
public class AnalysisTabelData {
    
    private String itemName;
    private String value;

    public AnalysisTabelData(String itemName, String value) {
        this.itemName = itemName;
        this.value = value;
    }

    public String getItemName() {
        return itemName;
    }

    public String getValue() {
        return value;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}
