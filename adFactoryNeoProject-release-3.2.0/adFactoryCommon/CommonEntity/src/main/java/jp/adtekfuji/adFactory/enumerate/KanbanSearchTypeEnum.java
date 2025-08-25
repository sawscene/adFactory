/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

/**
 * カンバン検索種別
 *
 * @author nar-nakamura
 */
public enum KanbanSearchTypeEnum {

    A("A", "key.type.A"),
    B("B", "key.type.B");

    private final String name;
    private final String resourceKey;

    private KanbanSearchTypeEnum(String name, String resourceKey) {
        this.name = name;
        this.resourceKey = resourceKey;
    }

    /**
     * カンバン検索種別名を取得する。
     *
     * @return カンバン検索種別名
     */
    public String getName() {
        return this.name;
    }

    /**
     * リソースキーを取得する。
     *
     * @return リソースキー
     */
    public String getResourceKey() {
        return this.resourceKey;
    }

    public static KanbanSearchTypeEnum getEnum(String str) {
        KanbanSearchTypeEnum[] enumArray = KanbanSearchTypeEnum.values();
        for (KanbanSearchTypeEnum enumStr : enumArray) {
            if (str.equals(enumStr.toString())) {
                return enumStr;
            }
        }
        return null;
    }
}
