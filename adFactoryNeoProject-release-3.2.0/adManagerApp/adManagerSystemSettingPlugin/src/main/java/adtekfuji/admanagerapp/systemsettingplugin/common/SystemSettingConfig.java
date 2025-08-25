/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.systemsettingplugin.common;

import adtekfuji.property.AdProperty;
import java.util.Properties;

/**
 *
 * @author nar-nakamura
 */
public class SystemSettingConfig {

    private static final String ENABLE_DAILY_REPORT = "enableDailyReport";// 作業日報が有効か

    /**
     * 作業日報が有効か
     *
     * @return true：有効, false：無効
     */
    public static Boolean getEnableDailyReport() {
        try {
            Properties properties = AdProperty.getProperties();
            if (!properties.containsKey(ENABLE_DAILY_REPORT)) {
                properties.setProperty(ENABLE_DAILY_REPORT, String.valueOf(false));
            }
            return Boolean.parseBoolean(properties.getProperty(ENABLE_DAILY_REPORT));
        }
        catch (Exception ex) {
            return false;
        }
    }
}
