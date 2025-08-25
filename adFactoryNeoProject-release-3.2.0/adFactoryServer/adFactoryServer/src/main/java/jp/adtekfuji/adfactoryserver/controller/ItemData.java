/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.controller;

/**
 * 資材情報詳細 アイテムクラス
 *
 * @author 14-0282
 */
public class ItemData {

    public String name;
    public String value;

    /**
     * コンストラクタ
     *
     * @param name
     * @param value
     */
    public ItemData(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * アイテム名 取得
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * アイテム名 設定
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * アイテム値 取得
     *
     * @return
     */
    public String getValue() {
        return value;
    }

    /**
     * アイテム値 設定
     *
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }
}
