/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

/**
 * フィールド種別
 *
 * @author s-heya
 */
public enum AccessoryFieldTypeEnum {
    EQUIPMENT("key.ReferenceNumber"),
    SERIAL("key.SerialNumber"),
    LOT("key.LotNumber"),
    QUANTITY("key.Quantity"),
    CUSTOM("key.CustomInputField"),
    PARTS_ID("key.PartsID");

    private final String resourceKey;

    /**
     * コンストラクタ
     *
     * @param resourceKey
     */
    private AccessoryFieldTypeEnum(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * リソースキーを取得する
     *
     * @return
     */
    public String getResourceKey() {
        return this.resourceKey;
    }

    /**
     * 文字列と比較する
     *
     * @param value
     * @return
     */
    public boolean equals(String value) {
        return this.toString().equals(value);
    }
}
