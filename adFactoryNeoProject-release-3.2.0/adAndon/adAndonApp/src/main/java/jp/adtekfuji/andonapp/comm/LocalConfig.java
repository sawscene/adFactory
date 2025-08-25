/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andonapp.comm;

import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringUtils;

/**
 * 設定クラス
 *
 * @author s-heya
 */
public class LocalConfig {

    /**
     * フルスクリーン表示かどうかを返す。
     *
     * @return
     */
    public static boolean isFullScreen() {
        boolean fullScreen = true;
        try {
            String value = AdProperty.getProperties().getProperty("fullScreen");
            if (!StringUtils.isEmpty(value)) {
                fullScreen = Boolean.parseBoolean(value);
            }
        }
        catch (Exception ex) {
        }
        return fullScreen;
    }

    /**
     * ウィンドウフレームを表示するかどうか
     *
     * @return 設定ファイルの「showFrame」がtrueの場合trueを返す
     */
    public static boolean isShowFrame() {
        boolean showFrame = true;
        try {
            String value = AdProperty.getProperties().getProperty("showFrame");
            if (!StringUtils.isEmpty(value)) {
                showFrame = Boolean.parseBoolean(value);
            }
        } catch (Exception ex) {
        }
        return showFrame;
    }
}
