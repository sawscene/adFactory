/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.common;

import adtekfuji.property.AdProperty;
import java.util.Properties;

/**
 * カンバン編集プラグイン 設定クラス
 *
 * @author nar-nakamura
 */
public class WorkPlanKanbanEditConfig {

    private static final String KANBAN_EDIT_MENU_ENABLED = "kanbanEditMenuEnabled";// カンバン編集メニューを使用するか

    /**
     * カンバン編集メニューを使用するか
     *
     * @return true：使用する, false：使用しない (カンバン編集画面を表示)
     */
    public static Boolean getKanbanEditMenuEnabled() {
        try {
            Properties properties = AdProperty.getProperties();
            if (!properties.containsKey(KANBAN_EDIT_MENU_ENABLED)) {
                properties.setProperty(KANBAN_EDIT_MENU_ENABLED, String.valueOf(false));
            }
            return Boolean.parseBoolean(properties.getProperty(KANBAN_EDIT_MENU_ENABLED));
        }
        catch (Exception ex) {
            return false;
        }
    }
}
