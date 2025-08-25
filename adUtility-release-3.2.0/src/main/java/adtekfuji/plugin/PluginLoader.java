/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import org.apache.logging.log4j.LogManager;

/**
 * プラグインローダー
 *
 * @author s-heya
 */
public class PluginLoader {

    private static final AppClassLoader classLoader = new AppClassLoader(new URL[0], ClassLoader.getSystemClassLoader());
    private static String pluginPath = System.getProperty("user.dir") + File.separator + "plugin";
            
    /**
     * Pluginファイルの格納フォルダパスを変更します. 規定ではアプリケーション実行フォルダの pluginフォルダです.
     *
     * @param path pluginフォルダパス
     */
    public static void rebasePath(String path) {
        pluginPath = path;
    }

    /**
     * プラグインをロードする。
     *
     * @author ke.yokoi
     * @param <T> インターフェイスクラス型
     * @param clazz クラス
     * @return 
     */
    public static <T> List<T> load(Class<T> clazz) {
        List<T> list = new ArrayList<>();

        try {
            File dir = new File(pluginPath);

            File[] files = dir.listFiles(f -> f.getPath().toLowerCase().endsWith(".jar"));
            if (Objects.nonNull(files)) {
                for (File file : files) {
                    URL url = file.toURI().toURL();
                    for (T plugin : ServiceLoader.load(clazz, new URLClassLoader(new URL[]{url}))) {
                        list.add(plugin);

                        // クラスパスを追加
                        classLoader.addURL(url);
                    }
                }
            }

        } catch (MalformedURLException ex) {
            // pluginフォルダーが存在しない場合
            LogManager.getLogger().warn(ex);
        }
        return list;
    }
    
    /**
     * クラスローダーを取得する。
     * 
     * @return クラスローダー
     */
    public static ClassLoader getClassLoarder() {
        return classLoader;
    }
}
