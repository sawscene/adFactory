/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.locale;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * ロケールユーティリティクラス
 * 
 * @author s-heya
 */
public class LocaleUtils {

    private static ResourceBundle rb = null;

    /**
     * 言語リソースを読み込む。
     *
     * @param bundleName リソースバンドル名
     * @return ResourceBundle
     */
    public static ResourceBundle load(String bundleName) {
        try {
            File dir = Paths.get(System.getenv("ADFACTORY_HOME") + "\\client\\cache").toFile();
            URLClassLoader urlLoader = new URLClassLoader(new URL[]{dir.toURI().toURL()});
            rb = ResourceBundle.getBundle(bundleName, Locale.getDefault(), urlLoader);
            
        } catch (MissingResourceException | MalformedURLException e1) {
            // デフォルトの言語ファイルをロード
            rb = LocaleUtils.getBundle(bundleName);
        }
        return rb;
    }

    /**
     * リソースバンドルを取得する。
     *
     * @param bundleName リソースバンドル名
     * @return ResourceBundle
     */
    public static ResourceBundle getBundle(String bundleName) {
        if (Objects.isNull(rb)) {
            try {
                // デフォルトの言語ファイルをロード
                File dir = Paths.get(System.getenv("ADFACTORY_HOME") + "\\bin\\locale").toFile();
                URLClassLoader urlLoader = new URLClassLoader(new URL[]{dir.toURI().toURL()});
                rb = ResourceBundle.getBundle(bundleName, Locale.getDefault(), urlLoader);

            } catch (MissingResourceException | MalformedURLException e2) {
                System.err.println(e2);
            }
        }
        return rb;
    }

    /**
     * 言語リソースを取得する。
     *
     * @param key キー
     * @return 言語リソース
     */
    public static String getString(String key) {
        assert rb != null;
        if (!rb.containsKey(key)) {
            return key;
        }
        return rb.getString(key);
    }

    /**
     * 言語リソースファイルを削除する
     *
     * @param bundleName リソースバンドル名
     * @return 
     */
    public static boolean clearLocaleFile(String bundleName) {
        try {
            Path filePath = Paths.get(System.getenv("ADFACTORY_HOME") + "\\client\\cache\\" + bundleName + "_" +  Locale.getDefault() + ".properties");
            if (Files.exists(filePath)) {
                filePath.toFile().delete();
                // 言語リソースをリセット
                rb = null;
            }

            rb = LocaleUtils.getBundle(bundleName);

            return true;
        } catch(Exception ex) {
            return false;
        }
    }

    /**
     * 言語リソースファイルを取得する。
     *
     * @param bundleName リソースバンドル名
     * @return 
     */
    public static File getLocaleFile(String bundleName) {
        try {
            Path filePath = Paths.get(System.getenv("ADFACTORY_HOME") + "\\client\\cache\\" + bundleName + "_" +  Locale.getDefault() + ".properties");
            if (!Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }
            return filePath.toFile();
        } catch (Exception ex) {
            return null;
        }
    }
}
