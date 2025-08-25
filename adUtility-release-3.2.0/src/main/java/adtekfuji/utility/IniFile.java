/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * INIファイル
 *
 * @author s-heya
 */
public class IniFile {
    private final Pattern sectionPattern = Pattern.compile("\\s*\\[([^]]*)\\]\\s*");
    private final Pattern keyValuePattern = Pattern.compile("\\s*([^=]*)=(.*)");
    private final Map<String, Map<String, String>> entries = new HashMap<>();

    /**
     * コンストラクタ
     */
    public IniFile() {

    }

    /**
     * コンストラクタ
     *
     * @param path
     * @throws IOException
     */
    public IniFile( String path ) throws IOException {
        this.load(path);
    }

    /**
     * INIファイルを読み込む。
     *
     * @param path
     * @throws IOException
     */
    public void load(String path) throws IOException {
        try( BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            String section = null;
            while ((line = br.readLine()) != null) {
                Matcher m = this.sectionPattern.matcher(line);
                if (m.matches()) {
                    section = m.group( 1 ).trim();
                }
                else if (section != null ) {
                    m = this.keyValuePattern.matcher(line);
                    if (m.matches()) {
                        String key   = m.group(1).trim();
                        String value = m.group(2).trim();
                        Map<String, String> keyValue = entries.get(section);
                        if (keyValue == null) {
                            this.entries.put( section, keyValue = new HashMap<>());
                        }
                        keyValue.put(key, value);
                    }
                }
            }
        }
   }

    /**
     * 値を取得する。
     *
     * @param section
     * @param key
     * @param defaultvalue
     * @return
     */
    public String getString(String section, String key, String defaultvalue) {
        Map<String, String> keyValue = this.entries.get(section);
        if (keyValue == null) {
            return defaultvalue;
        }
        return keyValue.getOrDefault(key, defaultvalue);
    }

    /**
     * 値を取得する。
     *
     * @param section
     * @param key
     * @param defaultvalue
     * @return
     */
    public int getInt(String section, String key, int defaultvalue ) {
        Map<String, String> keyValue = this.entries.get(section);
        if (keyValue == null) {
            return defaultvalue;
        }
        return Integer.parseInt(keyValue.get(key));
    }

    /**
     * 値を取得する。
     *
     * @param section
     * @param key
     * @param defaultvalue
     * @return
     */
    public double getDouble( String section, String key, double defaultvalue ) {
        Map<String, String> keyValue = this.entries.get(section);
        if (keyValue == null) {
            return defaultvalue;
        }
        return Double.parseDouble(keyValue.get(key));
    }
    
    /**
     * セクション名を列挙する。
     * 
     * @return セクション名一覧
     */
    public List<String> getSectionKeys() {
        return new ArrayList(this.entries.keySet());
    }
}
