/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adlinkservice.adlinkfpinspection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通信ファイル
 *
 * @author s-heya
 */
public class InspectionCommandFile {

    private final Pattern sectionPattern = Pattern.compile("\\s*\\[([^]]*)\\]\\s*");
    private final Pattern keyValuePattern = Pattern.compile("\\s*([^=]*)=(.*)");
    private final Map<String, Map<String, String>> entries = new HashMap<>();
    private final String filePath;
    private final String encode;

    /**
     * コンストラクタ
     *
     * @param path ファイルパス
     * @throws IOException
     */
    public InspectionCommandFile(String path) throws IOException {
        this.filePath = path;
        this.encode = System.getProperty("file.encoding");
        this.load(path);
    }

    /**
     * コンストラクタ
     *
     * @param path ファイルパス
     * @param encode エンコード
     * @throws IOException 
     */
    public InspectionCommandFile(String path, String encode) throws IOException {
        this.filePath = path;
        this.encode = encode;
        this.load(path);
    }

    /**
     * 通信ファイルを読み込む。
     *
     * @param path ファイルパス
     * @throws IOException
     */
    public void load(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), this.encode))) {
            String line;
            String section = null;
            while ((line = br.readLine()) != null) {
                Matcher m = this.sectionPattern.matcher(line);
                if (m.matches()) {
                    section = m.group(1).trim();
                } else if (section != null) {
                    m = this.keyValuePattern.matcher(line);
                    if (m.matches()) {
                        String key = m.group(1).trim();
                        String value = m.group(2).trim();
                        Map<String, String> keyValue = entries.get(section);
                        if (keyValue == null) {
                            this.entries.put(section, keyValue = new HashMap<>());
                        }
                        keyValue.put(key, value);
                    }
                }
            }
        }
    }

    /**
     * 引数に指定したセクションが存在するか確認する。
     *
     * @param section
     * @return
     */
    public boolean hasSection(String section) {
        return this.entries.containsKey(section);
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
        if (keyValue == null || !keyValue.containsKey(key)) {
            return defaultvalue;
        }
        return keyValue.get(key);
    }

    /**
     * 値を取得する。
     *
     * @param section
     * @param key
     * @param defaultvalue
     * @return
     */
    public int getInt(String section, String key, int defaultvalue) {
        Map<String, String> keyValue = this.entries.get(section);
        if (keyValue == null || !keyValue.containsKey(key)) {
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
    public double getDouble(String section, String key, double defaultvalue) {
        Map<String, String> keyValue = this.entries.get(section);
        if (keyValue == null || !keyValue.containsKey(key)) {
            return defaultvalue;
        }
        return Double.parseDouble(keyValue.get(key));
    }

    /**
     * 指定したセクションに文字列を追加する
     *
     * @param section
     * @param key
     * @param value
     */
    public void addString(String section, String key, String value) {
        Map<String, String> keyValue = this.entries.get(section);
        if (keyValue == null) {
            keyValue = new HashMap<>();
            this.entries.put(section, keyValue);
        }
        keyValue.put(key, value);
    }

    /**
     * 全てのセクションに文字列を追加する
     *
     * @param key
     * @param value
     */
    public void addStringAllSection(String key, String value) {
        for (Map.Entry<String, Map<String, String>> entry : this.entries.entrySet()) {
            Map<String, String> keyValue = entry.getValue();
            if (keyValue != null) {
                keyValue.put(key, value);
            }
        }
    }

    /**
     * 現在の設定でロードしたときのパスに書き出す
     *
     * @throws java.io.IOException
     */
    public void write() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.filePath), this.encode))) {
            for (Map.Entry<String, Map<String, String>> entry : this.entries.entrySet()) {
                final String section = entry.getKey();
                final Map<String, String> valueSet = entry.getValue();

                bw.write("[" + section + "]");
                bw.newLine();
                for (Map.Entry<String, String> valueMap : valueSet.entrySet()) {
                    bw.write(valueMap.getKey() + "=" + valueMap.getValue());
                    bw.newLine();
                }
            }
        }
    }
}
