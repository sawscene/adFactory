/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.utility;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import org.apache.commons.lang3.StringUtils;

/**
 * ロケールユーティリティ
 *
 * @author nar-nakamura
 */
public class LocaleUtils {

    private static final ResourceBundle resource_ja = ResourceBundle.getBundle("jp.adtekfuji.adfactoryserver.locale.locale", Locale.JAPANESE);
    private static final ResourceBundle resource_en = ResourceBundle.getBundle("jp.adtekfuji.adfactoryserver.locale.locale", Locale.ENGLISH);
    private static final Map<String, ResourceBundle> resources = new HashMap<>();

    static {
        resources.put(Locale.JAPANESE.getLanguage(), resource_ja);
        resources.put(Locale.ENGLISH.getLanguage(), resource_en);
    }
    
    /**
     * キーを指定して、文言を取得する。
     *
     * @param key キー
     * @return 文言 (存在しない場合はキーをそのまま返す)
     */
    public static String getString(String key) {
        if (resource_ja.containsKey(key)) {
            return resource_ja.getString(key);
        } else {
            return key;
        }
    }

    /**
     * キーを指定して、文言を取得する。
     * 
     * @param key キー
     * @param language 言語 (ja: 日本語、en: 英語)
     * @return 文言 (存在しない場合はキーをそのまま返す)
     */
    public static String getString(String key, String language) {
        ResourceBundle resource;
        if (StringUtils.isEmpty(language) || resources.containsKey(language)) {
            resource = resources.get(language);
        } else {
            resource = ResourceBundle.getBundle("jp.adtekfuji.adfactoryserver.locale.locale", new Locale(language));
        }
        
        if (Objects.nonNull(resource) && resource.containsKey(key)) {
            return resource.getString(key);
        }

        return key;
    }
}
 
