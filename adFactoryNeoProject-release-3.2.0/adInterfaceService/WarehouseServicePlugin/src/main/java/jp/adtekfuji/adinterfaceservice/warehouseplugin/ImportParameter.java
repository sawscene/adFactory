/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.warehouseplugin;

/**
 * インポート設定のパラメーター
 * @author 14-0282
 */
public class ImportParameter {

    public String key;
    public Integer column;

    /**
     * コンストラクタ
     */
    public ImportParameter() {
    }
    
    /**
     * コンストラクタ
     * 
     * @param key 項目名
     * @param column カラム位置
     */
    public ImportParameter(String key, Integer column) {
        this.key = key;
        this.column = column;
    }

}
