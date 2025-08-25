/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * プロパティ種別 列挙型
 *
 * @author s-heya
 */
public enum WorkPropertyCategoryEnum {
    INFO(""),
    PARTS("key.parts"),
    WORK("key.work"),
    INSPECTION("key.inspection"),
    MEASURE("key.measure"),
    TIMESTAMP("key.timestamp"),
    CUSTOM("key.custom"),
    TIMER("key.timer"),
    LIST("key.list"),
    JUDG("key.judg"),
    PRODUCT("key.product");


    // リソースキー
    private final String resourceKey;

    /**
     * コンストラクタ
     *
     * @param resourceKey
     */
    private WorkPropertyCategoryEnum(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * 項目名を取得する
     *
     * @param rb
     * @param obj
     * @return
     */
    public static String getName(ResourceBundle rb, WorkPropertyCategoryEnum obj) {
        if (!StringUtils.isEmpty(obj.resourceKey)) {
            return LocaleUtils.getString(obj.resourceKey);
        }
        return obj.resourceKey;
    }

    /**
     * 項目リストを取得する
     *
     * @param rb
     * @return
     */
    public static List<String> getNames(ResourceBundle rb) {
        List<String> list = new ArrayList<>();
        WorkPropertyCategoryEnum[] array = WorkPropertyCategoryEnum.values();
        for (WorkPropertyCategoryEnum obj : array) {
            list.add(WorkPropertyCategoryEnum.getName(rb, obj));
        }
        return list;
    }

    /**
     * リソースキーを取得する
     *
     * @return
     */
    public String getResourceKey() {
        return this.resourceKey;
    }
}
