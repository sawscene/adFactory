/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.plugin;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityType;

/**
 * adManagerAppのプラグインインターフェース定義.
 *
 * @author ke.yokoi
 */
public interface AdManagerAppMainMenuInterface {

    /**
     * 表示カテゴリー定義.
     */
    enum DisplayCategoryType {

        MANAGEMENT_FUNCTION, //編集系機能
        REFERENCE_FUNCTION, //参照系機能
        OTHER_FUNCTION
    }

    /**
     * 表示カテゴリー順序.
     */
    enum DisplayCategoryOrder {

        HIGHEST_PRIORITY(90),
        HIGH_PRIORITY(80),
        MIDDLE_PRIORITY(50),
        LOW_PRIORITY(20),
        LOWEST_PRIORITY(10);

        private final Integer order;

        private DisplayCategoryOrder(Integer order) {
            if (order < 10 || order > 90) {
                throw new IllegalArgumentException("Out of order");
            }
            this.order = order;
        }

        public Integer getOrder() {
            return order;
        }
    }
    
    enum MainMenuCategory  {
        OPERATION,   // 運用
        LITE,        // Lite
        REPORTER,    // Reporter
        RESULT,      // 実績
        WAREHOUSE,   // 倉庫
        SETTINGS,     // 設定
        UNUSED_MENU_CATEGORY
    }
    
    /**
     * プラグイン毎の初期処理呼出し
     */
    public default void pluginInitialize() {
    }

    /**
     * プラグイン毎の後処理呼出し
     */
    public default void pluginDestructor() {
    }

    /**
     * プラグイン毎のサービス開始
     */
    public default void pluginServiceStart() {
    }

    /**
     * プラグイン毎のサービス終了
     */
    public default void pluginServiceStop() {
    }

    /**
     * メインメニューに表示する文字列の取得.
     *
     * @return メニュー文字列
     */
    public String getDisplayName();

    /**
     * 表示カテゴリー取得.
     *
     * @return 表示カテゴリー
     */
    public DisplayCategoryType getDisplayCategory();

    /**
     * 表示順序番号取得.
     *
     * まずは enum.DisplayCategoryType が優先となります。 enum.DisplayCategoryType の中で優先度を調整したいときにこの値を使います。 基本は
     * enum.DisplayCategoryOrder の数値となります。微調整は +/- で調整してください。 HIGH_PRIORITY-5 とすると HIGH_PRIORITY
     * より少し低い優先度となります。
     *
     * @return 表示順序番号
     */
    public default Integer getDisplayOrder() {
        return DisplayCategoryOrder.MIDDLE_PRIORITY.getOrder();
    }

    /**
     * メニュー選択時の処理.
     *
     */
    public void onSelectMenuAction();

    /**
     * オプション種別の取得.
     *
     * @return LicenseOptionType
     */
    public LicenseOptionType getOptionType();

    /**
     * 役割権限種別の取得.
     *
     * @return RoleAuthorityTypeEnum collection
     */
    public List<RoleAuthorityType> getRoleAuthorityType();

    /**
     * プロパティを設定する
     *
     * @param properties
     */
    public void setProperties(Properties properties);
    
    /**
    * メインメニューのカテゴリを取得します.
    *
    * @return メインメニューのカテゴリ
    */
//    public default MainMenuCategory getMainMenuCategory() {
//        return MainMenuCategory.OPERATION;
//    }

    
    /**
     * サブメニューの表示名称一覧を取得.
     *
     * @return サブメニュー表示名称リスト
     */
    public default Map<MainMenuCategory, List<String>> getSubMenuDisplayNames() {
        return Collections.emptyMap();
    }
    
        /**
     * サブメニュー選択時の処理.
     *
     * @param displayName サブメニュー表示名
     */
    public default void onSelectSubMenuAction(String displayName) {
    }
}
