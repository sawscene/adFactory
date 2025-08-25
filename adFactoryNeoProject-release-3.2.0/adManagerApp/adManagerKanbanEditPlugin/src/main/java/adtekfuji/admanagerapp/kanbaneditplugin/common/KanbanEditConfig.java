/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.common;

import adtekfuji.property.AdProperty;
import java.util.Properties;

/**
 * カンバン編集プラグイン 設定クラス
 *
 * @author nar-nakamura
 */
public class KanbanEditConfig {

    /**
     * カンバン編集メニューを使用するか
     *
     * @return true：使用する、false：使用しない (カンバン編集画面を表示)
     */
    public static Boolean isKanbanEditMenuEnabled() {
        try {
            Properties properties = AdProperty.getProperties();
            if (!properties.containsKey("kanbanEditMenuEnabled")) {
                properties.setProperty("kanbanEditMenuEnabled", String.valueOf(false));
            }
            return Boolean.valueOf(properties.getProperty("kanbanEditMenuEnabled"));
        }
        catch (Exception ex) {
            return false;
        }
    }

    /**
     * 生産計画の読み込みを使用するか
     * 
     * @return true：使用する、false：使用しない 
     */
    public static Boolean isUseKanbanImport() {
        try {
            Properties properties = AdProperty.getProperties();
            if (!properties.containsKey("useKanbanImport")) {
                properties.setProperty("useKanbanImport", String.valueOf(false));
            }
            return Boolean.valueOf(properties.getProperty("useKanbanImport"));
        }
        catch (Exception ex) {
            return false;
        }
    }
}
