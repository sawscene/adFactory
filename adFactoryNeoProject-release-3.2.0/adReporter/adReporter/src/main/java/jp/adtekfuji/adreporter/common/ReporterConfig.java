/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adreporter.common;

import adtekfuji.property.AdProperty;
import java.util.Properties;

/**
 *
 * @author nar-nakamura
 */
public class ReporterConfig {

    // adReporterのポート
    private static final String REPORTER_PORT_KEY = "adReporterPort";
    private static final String REPORTER_PORT_DEF = "1099";

    // 廃棄伝票テンプレートファイル名
    private static final String DISPOSAL_TEMPLATE_KEY = "disposalTemplate";
    private static final String DISPOSAL_TEMPLATE_DEF = "disposal.xlsm";

    /**
     * adReporterのポートを取得する。
     *
     * @return adReporterのポート
     */
    public static String getReporterPort() {
        try {
            Properties properties = AdProperty.getProperties();
            if (!properties.containsKey(REPORTER_PORT_KEY)) {
                properties.setProperty(REPORTER_PORT_KEY, REPORTER_PORT_DEF);
            }
            return properties.getProperty(REPORTER_PORT_KEY);
        } catch (Exception ex) {
            return REPORTER_PORT_DEF;
        }
    }

    /**
     * adReporterのポートを設定する。
     *
     * @param value adReporterのポート
     */
    public static void setReporterPort(String value) {
        Properties properties = AdProperty.getProperties();
        properties.setProperty(REPORTER_PORT_KEY, value);
    }

    /**
     * 廃棄伝票テンプレートファイル名を取得する。
     *
     * @return 廃棄伝票テンプレートファイル名
     */
    public static String getDisposalTemplate() {
        try {
            Properties properties = AdProperty.getProperties();
            if (!properties.containsKey(DISPOSAL_TEMPLATE_KEY)) {
                properties.setProperty(DISPOSAL_TEMPLATE_KEY, DISPOSAL_TEMPLATE_DEF);
            }
            return properties.getProperty(DISPOSAL_TEMPLATE_KEY);
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * 廃棄伝票テンプレートファイル名を設定する。
     *
     * @param value 廃棄伝票テンプレートファイル名
     */
    public static void setDisposalTemplate(String value) {
        Properties properties = AdProperty.getProperties();
        properties.setProperty(DISPOSAL_TEMPLATE_KEY, value);
    }
}
