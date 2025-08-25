/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

/**
 * 工程接続タイプ
 *
 * @author HN)y-harada 
 */
public enum WorkConnectionTypeEnum {

    SERIAL("key.Series"),      // 直列
    PARALLEL("key.Parallel");  // 並列

    private final String resourceKey;

    /**
     *
     * @param resourceKey
     */
    private WorkConnectionTypeEnum(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     *
     * @return
     */
    public String getResourceKey() {
        return this.resourceKey;
    }

    /**
     *
     * @param str
     * @return
     */
    public static WorkConnectionTypeEnum getEnum(String str) {
        WorkConnectionTypeEnum[] enumArray = WorkConnectionTypeEnum.values();
        for (WorkConnectionTypeEnum enumStr : enumArray) {
            if (str.equals(enumStr.toString())) {
                return enumStr;
            }
        }
        return null;
    }
}
