/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adreporter.utils;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * ロケールユーティリティ
 *
 * @author nar-nakamura
 */
public class LocaleUtils {

    private static ResourceBundle rb = null;

    /**
     * リソースをロードする
     *
     * @param bundleName
     * @param locale
     * @return
     */
    public static ResourceBundle load(String bundleName, Locale locale) {
        try {
            rb = ResourceBundle.getBundle("locale.adreporter.locale", locale);
        } catch (Exception ex) {
            rb = ResourceBundle.getBundle(bundleName, Locale.US);
        }
        return rb;
    }

    /**
     * ResourceBundleを取得する
     *
     * @return
     */
    public static ResourceBundle getResourceBundle() {
        return rb;
    }

    /**
     * 文字列を取得する。
     *
     * @param key
     * @return
     */
    public static String getString(String key) {
        assert rb != null;
        if (!rb.containsKey(key)) {
            return key;
        }
        return rb.getString(key);
    }
}
