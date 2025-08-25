/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.admonitorfloorplugin;

import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringUtils;
import java.io.File;
import java.io.IOException;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import jp.adtekfuji.andon.common.Constants;
import jp.adtekfuji.andon.utility.MonitorTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Profile.Section;
import org.ini4j.Wini;

/**
 *
 * @author itage
 */
public class Setting {

    private static final Logger logger = LogManager.getLogger();
    private static final String GLOBAL_SECTION = "global";
    private static final String ICON_SECTION = "icon";
    private static final String KEY_REFRESH_TIMEOUT = "timeou_sec";
    private static final String KEY_MAX_ICON_NUM = "max_icon_num";
    private static final String KEY_BACK_GROUND = "background";
    private static final String KEY_RESOLUTION_WIDTH = "resolution_width";
    private static final String KEY_RESOLUTION_HEIGHT = "resolution_height";
    private static final String KEY_ICON_FORE_COLOR = "icon_fore_color";
    private static final String KEY_ICON_BACK_COLOR = "icon_back_color";
    private static final String KEY_ICON_POSX = "pos_x";
    private static final String KEY_ICON_POSY = "pos_y";
    private static final String KEY_ICON_WIDTH = "width";
    private static final String KEY_ICON_HEIGHT = "height";
    private static final String KEY_FIT_RESOLUTION = "fit_resolution";
    private static final String KEY_BLINK_TIME = "blink_msec";

    private static final Integer REFRESH_TIMEOUT_SEC = 60;
    private static final Integer MAX_ICON_NUM_DEFAULT = 50;
    private static final Double RESOLUTION_WIDTH_DEFAULT = 2024.0;
    private static final Double RESOLUTION_HEIGHT_DEFAULT = 1024.0;
    private static final Color ICON_FORE_COLOR = Color.BLACK;
    private static final Color ICON_BACK_COLOR = Color.WHITE;
    private static final Boolean FIT_RESOLUTION_DEFAULT = false;
    private static final Long BLINK_TIME_DEFAULT = 1000L;

    private static Setting instance = null;
    private final Wini ini = new Wini();

    private Setting() {
    }

    private static Setting getInstance() {
        if (instance == null) {
            instance = new Setting();
            instance.initialize();
        }
        return instance;
    }

    public static int GetRefreshTimeoutSec() {
        String val = getInstance().get(GLOBAL_SECTION, KEY_MAX_ICON_NUM);
        if (StringUtils.isEmpty(val)) {
            return REFRESH_TIMEOUT_SEC;
        }
        return Integer.parseInt(val);
    }

    public static int GetMaxIconNum() {
        String val = getInstance().get(GLOBAL_SECTION, KEY_REFRESH_TIMEOUT);
        if (StringUtils.isEmpty(val)) {
            return MAX_ICON_NUM_DEFAULT;
        }
        return Integer.parseInt(val);
    }

    public static String GetBackImagePath() {
        return getInstance().get(GLOBAL_SECTION, KEY_BACK_GROUND);
    }

    public static double GetResolutionWidth() {
        String val = getInstance().get(GLOBAL_SECTION, KEY_RESOLUTION_WIDTH);
        if (StringUtils.isEmpty(val)) {
            return RESOLUTION_WIDTH_DEFAULT;
        }
        return Double.parseDouble(val);
    }

    public static double GetResolutionHeight() {
        String val = getInstance().get(GLOBAL_SECTION, KEY_RESOLUTION_HEIGHT);
        if (StringUtils.isEmpty(val)) {
            return RESOLUTION_HEIGHT_DEFAULT;
        }
        return Double.parseDouble(val);
    }

    public static Color GetIconForeColor() {
        String val = getInstance().get(GLOBAL_SECTION, KEY_ICON_FORE_COLOR);
        if (StringUtils.isEmpty(val)) {
            return ICON_FORE_COLOR;
        }
        return Color.valueOf(val);
    }

    public static Color GetIconBackColor() {
        String val = getInstance().get(GLOBAL_SECTION, KEY_ICON_BACK_COLOR);
        if (StringUtils.isEmpty(val)) {
            return ICON_BACK_COLOR;
        }
        return Color.valueOf(val);
    }

    public static Boolean GetIconExist(int no) {
        Section section = getInstance().ini.get(ICON_SECTION + no);
        return section != null;
    }

    public static double GetIconPosX(int no) {
        String val = getInstance().get(ICON_SECTION + no, KEY_ICON_POSX);
        if (StringUtils.isEmpty(val)) {
            return 0;
        }
        return Double.parseDouble(val);
    }

    public static double GetIconPosY(int no) {
        String val = getInstance().get(ICON_SECTION + no, KEY_ICON_POSY);
        if (StringUtils.isEmpty(val)) {
            return 0;
        }
        return Double.parseDouble(val);
    }

    public static double GetIconWidth(int no) {
        String val = getInstance().get(ICON_SECTION + no, KEY_ICON_WIDTH);
        if (StringUtils.isEmpty(val)) {
            return 0;
        }
        return Double.parseDouble(val);
    }

    public static double GetIconHeight(int no) {
        String val = getInstance().get(ICON_SECTION + no, KEY_ICON_HEIGHT);
        if (StringUtils.isEmpty(val)) {
            return 0;
        }
        return Double.parseDouble(val);
    }

    public static Font getFont(Double width, String text) {
        // フォントサイズ
        if (!AdProperty.getProperties().containsKey(Constants.FONT_SIZE_MIDDLE)) {
            AdProperty.getProperties().setProperty(Constants.FONT_SIZE_MIDDLE, Constants.DEF_FONT_SIZE_MIDDLE);
        }
        double fontSize = Double.parseDouble(AdProperty.getProperties().getProperty(Constants.FONT_SIZE_MIDDLE));
        Font font  = new Font("Meiryo UI", fontSize);
        Text helper = new Text(text);
        helper.setFont(font);
        fontSize = width >= helper.getBoundsInLocal().getWidth() ? fontSize : fontSize * (width / helper.getBoundsInLocal().getWidth() * 0.9);
        return new Font("Meiryo UI", fontSize);
    }

    /**
     * 指定した幅と高さをもとに、その領域に文字列が収まるようフォントサイズを調整したフォントを作成する
     *
     * @param width 領域の幅
     * @param height 領域の高さ
     * @param text 表示する文字列
     * @return 領域に収まるようフォントサイズが調整されたFont
     */
    public static Font getFont(Double width, Double height, String text) {
        if (!AdProperty.getProperties().containsKey(Constants.FONT_SIZE_MIDDLE)) {
            AdProperty.getProperties().setProperty(Constants.FONT_SIZE_MIDDLE, Constants.DEF_FONT_SIZE_MIDDLE);
        }
        double fontSize = MonitorTools.getFontSize(text, width, height, Double.parseDouble(AdProperty.getProperties().getProperty(Constants.FONT_SIZE_MIDDLE)));
        return new Font("Meiryo UI", fontSize);
    }

    public static boolean GetFitResolution() {
        String val = getInstance().get(GLOBAL_SECTION, KEY_FIT_RESOLUTION);
        if (StringUtils.isEmpty(val)) {
            return FIT_RESOLUTION_DEFAULT;
        }
        return Boolean.valueOf(val);
    }

    public static void SetFitResolution(boolean fitResolution) {
        getInstance().set(GLOBAL_SECTION, KEY_FIT_RESOLUTION, String.valueOf(fitResolution));
    }

    public static long GetBlinkTime() {
        String val = getInstance().get(GLOBAL_SECTION, KEY_BLINK_TIME);
        if (StringUtils.isEmpty(val)) {
            return BLINK_TIME_DEFAULT;
        }
        return Long.parseLong(val);
    }

    public static void SetBlinkTime(long blinkTime) {
        getInstance().set(GLOBAL_SECTION, KEY_BLINK_TIME, String.valueOf(blinkTime));
    }

    private void initialize() {
        try {
            File file = new File(System.getenv("ADFACTORY_HOME") + File.separator + "conf" + File.separator + "adFloorLayoutEditor.ini");
            ini.setFile(file);
            ini.load();
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }

    private String get(String section, String key) {
        String value = getInstance().ini.get(section, key);
        return (value != null) ? value : "";
    }

    private void set(String section, String key, String value) {
        getInstance().ini.put(section, key, value);
    }
}
