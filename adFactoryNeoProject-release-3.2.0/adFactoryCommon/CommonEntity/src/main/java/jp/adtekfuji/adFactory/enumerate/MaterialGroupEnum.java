/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

/**
 * 資材情報の分類項目
 * 
 * @author s-heya
 */
public enum MaterialGroupEnum {
    PRODUCT("key.ProductNo"),
    UNIT("key.unitNo");

    final private String resourceKey;

    private MaterialGroupEnum(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public String getResourceKey() {
        return resourceKey;
    }    
}
