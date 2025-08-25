package jp.adtekfuji.javafxcommon.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Locale;


public class ResourceUtil {

    /**
     * 指定されたリソースバンドルのキーに対応する文字列を取得します。
     * 指定されたキーが見つからない場合は、デフォルトの英語リソースバンドルを検索し、
     * それでも見つからない場合はキー自身を返します。
     *
     * @param baseName リソースバンドルの基本名
     * @param key 取得したい値に対応するキー
     * @return キーに対応する文字列。またはキーに対応する値が見つからない場合はキー自身
     */
    public static String getString(String baseName, String key) {
        try {
            return ResourceBundle.getBundle(baseName).getString(key);
        } catch (MissingResourceException e) {
            try {
                return ResourceBundle.getBundle(baseName, Locale.ENGLISH).getString(key);
            } catch (MissingResourceException e2) {
                return key;
            }
        }
    }

    /**
     * 指定されたキーに対応するコントロールリソースの文字列を取得します。
     * 指定されたキーが見つからない場合は、デフォルトの英語リソースバンドルを検索し、
     * それでも見つからない場合はキー自身を返します。
     *
     * @param key 取得したい値に対応するキー
     * @return キーに対応するコントロールリソースの文字列
     */
    public static String getControlString(String key) {
        return getString("com.sun.javafx.scene.control.skin.resources.controls", key);
    }
}