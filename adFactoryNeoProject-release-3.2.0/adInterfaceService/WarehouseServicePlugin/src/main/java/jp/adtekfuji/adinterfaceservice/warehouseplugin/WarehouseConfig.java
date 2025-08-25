/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.warehouseplugin;

import adtekfuji.property.AdProperty;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 倉庫案内サービスの構成情報
 * 
 * @author 14-0282
 */
public class WarehouseConfig {
    private static final Logger logger = LogManager.getLogger();

    public static final String WAREHOUSE_PROPERTY = "warehouse";
    public static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    // インポートフォルダーのパス
    private static final String IMPORT_PATH = "importPath";
    private static final String IMPORT_PATH_DEF = "C:\\adFactory_IN";

    // エクスポートフォルダーのパス
    private static final String EXPORT_PATH = "exportPath";
    private static final String EXPORT_PATH_DEF = "C:\\adFactory_OUT";

    // 自動同期 
    private static final String AUTO_SYNC = "autoSync";
    private static final boolean AUTO_SYNC_DEF = false;

    // ポーリング間隔(単位：分)
    private static final String SYNC_INTERVAL_MIN = "syncIntervalMin";
    private static final int SYNC_INTERVAL_MIN_DEF = 1;

    // 最大出力件数
    private static final String MAX_EXPORT = "maxExport";
    private static final int MAX_EXPORT_DEF = 200;

    // 支給品リスト
    private static final String SUPPLY_LIST_PREFIX = "supplyList";
    private static final String SUPPLY_LIST_PREFIX_DEF = "支給品リスト";
    
    private static WarehouseConfig instance = null;
    private Properties properties;

    /**
     * コンストラクタ
     */
    private WarehouseConfig() {
        try {
            AdProperty.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "conf");
            AdProperty.load(WAREHOUSE_PROPERTY, "adWarehouse.properties");
            properties = AdProperty.getProperties(WAREHOUSE_PROPERTY);
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 構成情報のインスタンスを取得する。
     *
     * @return インスタンス
     */
    public static WarehouseConfig getInstance() {
        if (Objects.isNull(instance)) {
            instance = new WarehouseConfig();
        }
        return instance;
    }

    /**
     * インポートフォルダーのパスを取得する。
     *
     * @return インポートフォルダーのパス
     */
    public String getImportPath() {
        try {
            if (!properties.containsKey(IMPORT_PATH)) {
                properties.setProperty(IMPORT_PATH, String.valueOf(IMPORT_PATH_DEF));
                store();
            } else if (properties.getProperty(IMPORT_PATH).isEmpty()) {
                return IMPORT_PATH_DEF;
            }
            return properties.getProperty(IMPORT_PATH);
        } catch (Exception ex) {
            return IMPORT_PATH_DEF;
        }
    }

    /**
     * インポートフォルダーのパスを設定する。
     *
     * @param importPath インポートフォルダーのパス
     */
    public void setImportPath(String importPath) {
        properties.setProperty(IMPORT_PATH, String.valueOf(importPath));
    }

    /**
     * エクスポートフォルダーのパスを取得する。
     * 
     * @return エクスポートフォルダーのパス
     */
    public String getExportPath() {
        try {
            if (!this.properties.containsKey(EXPORT_PATH)) {
                this.properties.setProperty(EXPORT_PATH, String.valueOf(EXPORT_PATH_DEF));
                store();
            } else if (this.properties.getProperty(EXPORT_PATH).isEmpty()) {
                return EXPORT_PATH_DEF;
            }
            return this.properties.getProperty(EXPORT_PATH);
        } catch (Exception ex) {
            return EXPORT_PATH_DEF;
        }
    }

    /**
     * 自動同期を取得する。
     *
     * @return 自動同期
     */
    public boolean getAutoSync() {
        try {
            if (!properties.containsKey(AUTO_SYNC)) {
                properties.setProperty(AUTO_SYNC, String.valueOf(AUTO_SYNC_DEF));
                store();
            } else if (properties.getProperty(AUTO_SYNC).isEmpty()) {
                return AUTO_SYNC_DEF;
            }
            return Boolean.parseBoolean(properties.getProperty(AUTO_SYNC));
        } catch (Exception ex) {
            return AUTO_SYNC_DEF;
        }
    }

    /**
     * 自動同期を設定する。
     *
     * @param autoSync 自動同期
     */
    public void setAutoSync(boolean autoSync) {
        properties.setProperty(AUTO_SYNC, String.valueOf(autoSync));
    }

    /**
     * ポーリング間隔(単位：分)を取得する。
     *
     * @return ポーリング間隔
     */
    public String getSyncIntervalMin() {
        try {
            if (!properties.containsKey(SYNC_INTERVAL_MIN)) {
                properties.setProperty(SYNC_INTERVAL_MIN, String.valueOf(SYNC_INTERVAL_MIN_DEF));
                store();
            } else if (properties.getProperty(SYNC_INTERVAL_MIN).isEmpty()) {
                return String.valueOf(SYNC_INTERVAL_MIN_DEF);
            }
            return properties.getProperty(SYNC_INTERVAL_MIN);
        } catch (Exception ex) {
            return String.valueOf(SYNC_INTERVAL_MIN_DEF);
        }
    }

    /**
     * ポーリング間隔(単位：分)を設定する。
     *
     * @param syncIntervalMin ポーリング間隔
     */
    public void setSyncIntervalMin(String syncIntervalMin) {
        properties.setProperty(SYNC_INTERVAL_MIN, syncIntervalMin);
    }

    /**
     * 最大エクスポート件数を取得する。
     * 
     * @return 最大エクスポート件数
     */
    public int getMaxExport() {
        try {
            if (!properties.containsKey(MAX_EXPORT)) {
                properties.setProperty(MAX_EXPORT, String.valueOf(MAX_EXPORT_DEF));
                store();
            } else if (properties.getProperty(MAX_EXPORT).isEmpty()) {
                return MAX_EXPORT_DEF;
            }
            return Integer.parseInt(properties.getProperty(MAX_EXPORT));
        } catch (Exception ex) {
            return MAX_EXPORT_DEF;
        }
    }

    /**
     * 支給品リストファイル名の接頭語を取得する。
     *
     * @return 支給品リストファイル名の接頭語
     */
    public String getSupplyListPrefix() {
        try {
            if (!properties.containsKey(SUPPLY_LIST_PREFIX)) {
                properties.setProperty(SUPPLY_LIST_PREFIX, SUPPLY_LIST_PREFIX_DEF);
                store();
            } else if (properties.getProperty(SUPPLY_LIST_PREFIX).isEmpty()) {
                return SUPPLY_LIST_PREFIX_DEF;
            }
            return properties.getProperty(SUPPLY_LIST_PREFIX);
        } catch (Exception ex) {
            return SUPPLY_LIST_PREFIX_DEF;
        }
    }

    /**
     * 設定を保存する。
     */
    private void store() {
        try {
            AdProperty.store(WAREHOUSE_PROPERTY);
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 区画コードを取得する。
     * 
     * @return 区画名と区画コードのマップ
     */
    public Map<String, String> getAreaCodes() {
        
        // 区画名と区画コードを格納したファイルを読み込む
        String filePath = System.getenv("ADFACTORY_HOME") + Constants.CONF_PATH + File.separator + "AreaCode.json";

        File file = new File(filePath);
        if (!file.exists()) {
            return new HashMap<>();
        }

        try {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "SJIS"))) {
               String line;
               while ((line = reader.readLine()) != null) {
                   sb.append(line);
               }
            }

            if (sb.length() == 0) {
               return new HashMap<>();
            }

            return JsonUtils.jsonToMap(sb.toString());

        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }

        return new HashMap<>();
    }
}
