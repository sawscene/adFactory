/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.property;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * プロパティユーティリティ.
 *
 * @author ke.yokoi
 */
public class AdProperty {

    private static String BASE_PATH = System.getProperty("user.dir") + File.separator + "conf";
    private static final String FILE_CHAR = "MS932";
    private static final String DEFAULT = "DEFAULT";
    private static final Map<Object, Properties> properties = new HashMap<>();
    private static final Map<Object, File> propfiles = new HashMap<>();

    private AdProperty() {
    }

    /**
     * プロパティファイルの格納フォルダパスを変更します. 規定ではアプリケーション実行フォルダの confフォルダです.
     *
     * @param path confフォルダパス
     */
    public static void rebasePath(String path) {
        BASE_PATH = path;
    }

    /**
     * プロパティを読み込む。
     * 指定されたプロパティファイルが存在しない場合は生成する。
     *
     * @param fileName プロパティファイル名
     * @throws IOException
     */
    public static void load(String fileName) throws IOException {
        load(DEFAULT, fileName);
    }

    /**
     * プロパティを読み込む。
     * 指定されたプロパティファイルが存在しない場合は生成する。
     *
     * @param propId プロパティID
     * @param fileName プロパティファイル名
     * @throws IOException
     */
    public static void load(Object propId, String fileName) throws IOException {
        File dir = new File(BASE_PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(BASE_PATH + File.separator + fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        
        Properties property = new Properties();
        property.load(new InputStreamReader(new FileInputStream(file), FILE_CHAR));

        propfiles.put(propId, file);
        properties.put(propId, property);
    }

    /**
     * プロパティを取得する。
     *
     * @return プロパティ
     */
    public static Properties getProperties() {
        return getProperties(DEFAULT);
    }

    /**
     * プロパティを取得する。
     *
     * @param propId プロパティID
     * @return プロパティ
     */
    public static Properties getProperties(Object propId) {
        if (!contains(propId)) {
            throw new IllegalArgumentException("Property is not present: " + propId);
        }
        return properties.get(propId);
    }

    /**
     * プロパティを保存する。
     *
     * @throws IOException
     */
    public static void store() throws IOException {
        store(DEFAULT);
    }

    /**
     * プロパティを保存する。
     *
     * @param propId プロパティID
     * @throws IOException
     */
    public static void store(Object propId) throws IOException {
        if (!contains(propId)) {
            throw new IllegalArgumentException("Property is not present: " + propId);
        }
        File file = propfiles.get(propId);
        
        //ライセンスに関連する項目を除いて保存
        Properties propertiesNoLicense = new Properties();
        propertiesNoLicense.putAll(properties.get(propId).entrySet().stream()
                .filter(v -> !v.getKey().toString().startsWith("@"))
                .collect(Collectors.toMap(v -> v.getKey(), v -> v.getValue())));

        propertiesNoLicense.store(new OutputStreamWriter(new FileOutputStream(file), FILE_CHAR), null);
    }
    
    /**
     * プロパティが存在するかどうかを返す。
     * 
     * @param propId プロパティID
     * @return true: 存在する、false: 存在しない
     */
    public static boolean contains(Object propId) {
        return properties.containsKey(propId);
    }
}
