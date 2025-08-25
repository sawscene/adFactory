/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.common;

import adtekfuji.property.AdProperty;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * adFactory.properties 設定
 *
 * @author yu.nara
 */
public class SystemConfig {

    private static final Logger logger = LogManager.getLogger();

    // 直接工数の編集
    private static final String WORK_REPORT_DIRECT_WORK_EDITABLE = "workReportDirectWorkEditable";

    // カンバンに作業数追加
    private static final String WORK_REPORT_WORK_NUM_VISIBLE = "workReportWorkNumVisible";


    private static SystemConfig instance = null;

    private Properties properties;

    /**
     * インスタンスを取得する。
     *
     * @return
     */
    public static SystemConfig getInstance() {
        if (Objects.isNull(instance)) {
            instance = new SystemConfig();
        }
        return instance;
    }

    /**
     * コンストラクタ
     */
    public SystemConfig() {
        try {
            AdProperty.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "conf");
            AdProperty.load("adFactory.properties");
            properties = AdProperty.getProperties();
            
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }

    
    /**
     * 直接工数の編集可否
     * @return true: 編集可、false: 編集不可
     */
    public boolean isWorkReportDirectWorkEditable() {
        try {
            if (!properties.containsKey(WORK_REPORT_DIRECT_WORK_EDITABLE)) {
                properties.setProperty(WORK_REPORT_DIRECT_WORK_EDITABLE, String.valueOf(false));
            }
            return Boolean.parseBoolean(properties.getProperty(WORK_REPORT_DIRECT_WORK_EDITABLE));
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 作業数、製番列の表示有無
     * @return true: 表示する、false: 表示しない
     */
    public boolean isWorkReportWorkNumVisible() {
        try {
            if (!properties.containsKey(WORK_REPORT_WORK_NUM_VISIBLE)) {
                properties.setProperty(WORK_REPORT_WORK_NUM_VISIBLE, String.valueOf(false));
            }
            return Boolean.parseBoolean(properties.getProperty(WORK_REPORT_WORK_NUM_VISIBLE));
        } catch (Exception ex) {
            return false;
        }
    }
}
