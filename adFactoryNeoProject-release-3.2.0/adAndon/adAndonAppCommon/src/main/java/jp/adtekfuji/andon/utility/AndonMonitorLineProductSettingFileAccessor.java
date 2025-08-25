/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.utility;

import java.io.File;
import jakarta.xml.bind.JAXB;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;

/**
 * 進捗モニタ設定ファイル処理 (設定をxml形式で保存する)
 *
 * @author ke.yokoi
 */
public class AndonMonitorLineProductSettingFileAccessor {

    private final static String SETTING_FILE_NAME = "MonitorLineProductSetting_line%d.xml";// 進捗モニタ設定ファイル名のフォーマット
    private final static String SETTING_DEFAULT_NAME = "MonitorLineProductSetting.xml";// 進捗モニタ設定ファイル名 (デフォルト)

    /**
     * コンストラクタ
     */
    public AndonMonitorLineProductSettingFileAccessor() {
    }

    /**
     * 設備IDを<b>指定せず</b>デフォルトの進捗モニタ設定ファイルのパスを取得する。
     *
     * @return 設定ファイルパス
     */
    public String getFilePath() {
        return System.getenv("ADFACTORY_HOME") + File.separator + "conf" + File.separator + String.format(SETTING_DEFAULT_NAME);
    }

    /**
     * 進捗モニタの設備IDを指定して、進捗モニタ設定ファイルのパスを取得する。
     *
     * @param monitorId 進捗モニタの設備ID
     * @return 設定ファイルパス
     */
    public String getFilePath(Long monitorId) {
        return System.getenv("ADFACTORY_HOME") + File.separator + "conf" + File.separator + String.format(SETTING_FILE_NAME, monitorId);
    }

    /**
     * 進捗モニタの設備IDを指定して、進捗モニタ設定ファイルを読み込む。
     *
     * @param monitorId 進捗モニタの設備ID
     * @return 進捗モニタ設定
     */
    public AndonMonitorLineProductSetting load(Long monitorId) {
        String path = this.getFilePath(monitorId);
        AndonMonitorLineProductSetting property;
        File file = new File(path);
        if (file.exists()) {
            property = JAXB.unmarshal(file, AndonMonitorLineProductSetting.class);
        } else {
            property = AndonMonitorLineProductSetting.create();
        }
        return property;
    }

    /**
     * デフォルトの進捗モニタ設定ファイルを読み込む。
     *
     * @return 進捗モニタ設定
     */
    public AndonMonitorLineProductSetting load() {
        String path = this.getFilePath();
        AndonMonitorLineProductSetting property;
        File file = new File(path);
        if (file.exists()) {
            property = JAXB.unmarshal(file, AndonMonitorLineProductSetting.class);
        } else {
            property = AndonMonitorLineProductSetting.create();
        }
        return property;
    }

    /**
     * 進捗モニタの設備IDを指定して、進捗モニタ設定を保存する。
     *
     * @param monitorId 進捗モニタの設備ID
     * @param property 進捗モニタ設定
     */
    public void save(Long monitorId, AndonMonitorLineProductSetting property) {
        String path = this.getFilePath(monitorId);
        JAXB.marshal(property, new File(path));
    }

    /**
     * デフォルトの進捗モニタ設定を保存する。
     *
     * @param property 進捗モニタ設定
     */
    public void save(AndonMonitorLineProductSetting property) {
        String path = getFilePath();
        JAXB.marshal(property, new File(path));
    }

    /**
     * 進捗モニタの設備IDを指定して、進捗モニタ設定を削除する。
     *
     * @param monitorId 進捗モニタの設備ID
     */
    public void remove(Long monitorId) {
        String path = this.getFilePath(monitorId);
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }
}
