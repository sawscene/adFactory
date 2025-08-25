/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.common;

import adtekfuji.property.AdProperty;
import java.util.Properties;

/**
 * カンバン編集プラグイン(ELS) 設定クラス
 *
 * @author nar-nakamura
 */
public class KanbanEditConfigELS {

    private static final String INSTRUCTION_CODE_LABEL_TEXT = "indtructionCodeLabelText";// 作業指示コード入力欄のラベル表示テキスト

    // 工程順名の比較文字数
    private static final String WORKFLOW_NAME_COMPARE_NUM = "workflowNameCompareNum";
    private static final String WORKFLOW_NAME_COMPARE_NUM_DEF = "5";

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
        } catch (Exception ex) {
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

    /**
     * 工程順名の比較文字数を取得する。
     *
     * @return 工程順名の比較文字数
     */
    public static int getWorkflowNameCompareNum() {
        try {
            Properties properties = AdProperty.getProperties();
            if (!properties.containsKey(WORKFLOW_NAME_COMPARE_NUM)) {
                properties.setProperty(WORKFLOW_NAME_COMPARE_NUM, WORKFLOW_NAME_COMPARE_NUM_DEF);
            }
            return Integer.valueOf(properties.getProperty(WORKFLOW_NAME_COMPARE_NUM));
        } catch (Exception ex) {
            return Integer.valueOf(WORKFLOW_NAME_COMPARE_NUM_DEF);
        }
    }
}
