/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.common;

import adtekfuji.property.AdProperty;
import java.util.Properties;

/**
 * カンバン編集プラグイン(ELS) 設定クラス
 *
 * @author nar-nakamura
 */
public class WorkPlanKanbanEditConfigELS {

    private static final String INSTRUCTION_CODE_LABEL_TEXT = "indtructionCodeLabelText";// 作業指示コード入力欄のラベル表示テキスト

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
