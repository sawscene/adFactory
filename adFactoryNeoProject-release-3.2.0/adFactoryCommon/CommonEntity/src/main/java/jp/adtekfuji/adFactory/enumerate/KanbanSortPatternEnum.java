/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

/**
 * ソートパターン列挙
 *
 * @author HN)harada カンバンリストの並べ替え用に追加
 */
public enum KanbanSortPatternEnum {
    /**
     * 計画順
     */
    PLAN("key.planSort"),
    /**
     * 名前順
     */
    NAME("key.nameSort"),
    /**
     * ステータス順
     */
    STATUS("key.StatusSort"),
    /**
     * 作成順
     */
    CREATE("key.CreateSort");
    
    private final String resourceKey;

    /**
     *
     * @param resourceKey
     */
    private KanbanSortPatternEnum(String resourceKey) {
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
    public static KanbanSortPatternEnum getEnum(String str) {
        KanbanSortPatternEnum[] enumArray = KanbanSortPatternEnum.values();
        for (KanbanSortPatternEnum enumStr : enumArray) {
            if (str.equals(enumStr.toString())) {
                return enumStr;
            }
        }
        return null;
    }
}
