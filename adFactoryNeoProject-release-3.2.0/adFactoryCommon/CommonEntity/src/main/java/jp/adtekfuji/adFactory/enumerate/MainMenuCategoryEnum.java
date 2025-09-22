/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jp.adtekfuji.adFactory.enumerate;

import adtekfuji.locale.LocaleUtils;

/**
 *
 * @author SashinRanjitkar
 */
public enum MainMenuCategoryEnum {
    OPERATION ("key.MainMenuTitle.Operation"),   // 運用
    LITE      ("key.MainMenuTitle.Lite"),        // Lite
    REPORTER  ("key.MainMenuTitle.Reporter"),    // Reporter
    RESULT    ("key.MainMenuTitle.ActualOutput"),// 実績
    WAREHOUSE ("key.MainMenuTitle.WareHouse"),   // 倉庫
    SETTINGS  ("key.MainMenuTitle.Settings"),    // 設定
    UNUSED_MENU_CATEGORY("key.MainMenuTitle.Unused");

    private final String resourceKey;

    MainMenuCategoryEnum(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /** key as-is (useful when you want the i18n key)
     * @return  
    */
    public String getResourceKey() {
        return resourceKey;
    }

    /** localized display name with safe fallback
     * @return  
     */
    public String getDisplayName() {
        String s = LocaleUtils.getString(resourceKey);
        return (s == null || s.isBlank()) ? name() : s;
    }

    /** flexible lookup: by enum name or by key
     * @param value
     * @return  
     */
    public static MainMenuCategoryEnum from(String value) {
        if (value == null) return UNUSED_MENU_CATEGORY;
        for (MainMenuCategoryEnum c : values()) {
            if (c.name().equalsIgnoreCase(value) || c.resourceKey.equalsIgnoreCase(value)) {
                return c;
            }
        }
        return UNUSED_MENU_CATEGORY;
    }
}

