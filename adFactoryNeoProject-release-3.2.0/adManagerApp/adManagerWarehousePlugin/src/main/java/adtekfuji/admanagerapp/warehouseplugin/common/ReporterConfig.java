/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.common;

import adtekfuji.property.AdProperty;
import java.util.Properties;

/**
 *
 * @author nar-nakamura
 */
public class ReporterConfig {

    // 現品票テンプレートファイル名
    private static final String ACCEPTANCE_TEMPLATE_KEY = "acceptTemplate";
    private static final String ACCEPTANCE_TEMPLATE_DEF = "accept_label.xlsm";

    /**
     * 現品票テンプレートファイル名を取得する。
     *
     * @return 受入ラベルテンプレートファイル名
     */
    public static String getAcceptanceTemplate() {
        try {
            Properties properties = AdProperty.getProperties();
            if (!properties.containsKey(ACCEPTANCE_TEMPLATE_KEY)) {
                properties.setProperty(ACCEPTANCE_TEMPLATE_KEY, ACCEPTANCE_TEMPLATE_DEF);
            }
            return properties.getProperty(ACCEPTANCE_TEMPLATE_KEY);
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * 現品票テンプレートファイル名を設定する。
     *
     * @param value 受入ラベルテンプレートファイル名
     */
    public static void setAcceptanceTemplate(String value) {
        Properties properties = AdProperty.getProperties();
        properties.setProperty(ACCEPTANCE_TEMPLATE_KEY, value);
    }
}
