/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditpluginels.common;

import adtekfuji.property.AdProperty;
import java.util.Properties;

/**
 * カンバン編集プラグイン 設定クラス
 *
 * @author nar-nakamura
 */
public class KanbanEditConfig {

    private static final String KANBAN_EDIT_MENU_ENABLED = "kanbanEditMenuEnabled";// カンバン編集メニューを使用するか
    private static final String INSTRUCTION_CODE_LABEL_TEXT = "indtructionCodeLabelText";// 作業指示コード入力欄のラベル表示テキスト

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

    /**
     * 作業指示コード入力欄のラベル表示テキストを取得する
     *
     * @return 作業指示コード入力欄のラベル表示テキスト
     */
    public static String getIndtructionCodeLabelText() {
        try {
            Properties properties = AdProperty.getProperties();
            if (!properties.containsKey(INSTRUCTION_CODE_LABEL_TEXT)) {
                properties.setProperty(INSTRUCTION_CODE_LABEL_TEXT, "");
            }
            return properties.getProperty(INSTRUCTION_CODE_LABEL_TEXT);
        }
        catch (Exception ex) {
            return "";
        }
    }

    /**
     * 作業指示コード入力欄のラベル表示テキストを設定する
     *
     * @param value 
     */
    public static void setIndtructionCodeLabelText(String value) {
        Properties properties = AdProperty.getProperties();
        properties.setProperty(INSTRUCTION_CODE_LABEL_TEXT, value);
    }
}
