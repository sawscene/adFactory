/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.addatabase.utils;

import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nar-nakamura
 */
public class LocaleUtils {

    private static final Logger logger = LogManager.getLogger();
    private static ResourceBundle rb = null;

    /**
     * 言語リソースをロードする。
     *
     * @param localeBase 言語リソースのベース名
     * @param lang ロケール ("ja-JP"か"ja"の場合は日本語、それ以外は英語のリソースを取得する)
     * @return ResourceBundle
     */
    public static ResourceBundle load(String localeBase, String lang) {
        if (rb == null) {
            try {
                Locale locale;
                if (Locale.JAPAN.toLanguageTag().equalsIgnoreCase(lang)
                        || Locale.JAPANESE.toLanguageTag().equalsIgnoreCase(lang)) {
                    locale = Locale.JAPANESE;
                } else {
                    locale = Locale.ENGLISH;
                }
                rb = ResourceBundle.getBundle(localeBase, locale);
            } catch (Exception ex) {
                logger.fatal(ex, ex);
                rb = ResourceBundle.getBundle(localeBase, Locale.getDefault());
            }
        }
        return rb;
    }

    /**
     * ResourceBundleを取得する。
     *
     * @return ResourceBundle
     */
    public static ResourceBundle getResourceBundle() {
        return rb;
    }

    /**
     * 文字列を取得する。
     *
     * @param key リソースのキー
     * @return リソースの値
     */
    public static String getString(String key) {
        assert rb != null;
        if (!rb.containsKey(key)) {
            logger.warn("Resource not found: {}", key);
            return key;
        }
        return rb.getString(key);
    }
}
