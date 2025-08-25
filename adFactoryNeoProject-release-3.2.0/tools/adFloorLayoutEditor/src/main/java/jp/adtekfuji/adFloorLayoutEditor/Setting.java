/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFloorLayoutEditor;

import adtekfuji.utility.StringUtils;
import java.io.File;
import java.io.IOException;
import javafx.scene.paint.Color;
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
    private static final String KEY_MAX_ICON_NUM = "max_icon_num";
    private static final String KEY_BACK_GROUND = "background";
    private static final String KEY_RESOLUTION_WIDTH = "resolution_width";
    private static final String KEY_RESOLUTION_HEIGHT = "resolution_height";
    private static final String KEY_ICON_DEFAULT_WIDTH = "icon_default_width";
    private static final String KEY_ICON_DEFAULT_HEIGHT = "icon_default_height";
    private static final String KEY_ICON_MIN_WIDTH = "icon_min_width";
    private static final String KEY_ICON_MIN_HEIGHT = "icon_min_height";
    private static final String KEY_ICON_FORE_COLOR = "icon_fore_color";
    private static final String KEY_ICON_BACK_COLOR = "icon_back_color";
    private static final String KEY_ICON_POSX = "pos_x";
    private static final String KEY_ICON_POSY = "pos_y";
    private static final String KEY_ICON_WIDTH = "width";
    private static final String KEY_ICON_HEIGHT = "height";
    private static final String KEY_FIT_RESOLUTION = "fit_resolution";

    private static final Integer MAX_ICON_NUM_DEFAULT = 50;
    private static final Double RESOLUTION_WIDTH_DEFAULT = 1920.0;
    private static final Double RESOLUTION_HEIGHT_DEFAULT = 1080.0;
    private static final Double ICON_DEFAULT_WIDTH_SIZE = 100.0;
    private static final Double ICON_DEFAULT_HEIGHT_SIZE = 50.0;
    private static final Double ICON_MIN_WIDTH_SIZE = 50.0;
    private static final Double ICON_MIN_HEIGHT_SIZE = 20.0;
    private static final Color ICON_FORE_COLOR = Color.BLACK;
    private static final Color ICON_BACK_COLOR = Color.WHITE;
    private static final Boolean FIT_RESOLUTION_DEFAULT = false;

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

    public static void store() {
        try {
            getInstance().ini.store();
        }
        catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }

    public static int GetMaxIconNum() {
        String val = getInstance().get(GLOBAL_SECTION, KEY_MAX_ICON_NUM);
        if (StringUtils.isEmpty(val)) {
            return MAX_ICON_NUM_DEFAULT;
        }
        return Integer.parseInt(val);
    }

    public static void SetMaxIconNum(int num) {
        getInstance().set(GLOBAL_SECTION, KEY_MAX_ICON_NUM, String.valueOf(num));
    }

    public static String GetBackImagePath() {
        return getInstance().get(GLOBAL_SECTION, KEY_BACK_GROUND);
    }

    public static void SetBackImagePath(String path) {
        getInstance().set(GLOBAL_SECTION, KEY_BACK_GROUND, path);
    }

    public static double GetResolutionWidth() {
        String val = getInstance().get(GLOBAL_SECTION, KEY_RESOLUTION_WIDTH);
        if (StringUtils.isEmpty(val)) {
            return RESOLUTION_WIDTH_DEFAULT;
        }
        return Double.parseDouble(val);
    }

    public static void SetResolutionWidth(Double width) {
        getInstance().set(GLOBAL_SECTION, KEY_RESOLUTION_WIDTH, String.valueOf(width));
    }

    public static double GetResolutionHeight() {
        String val = getInstance().get(GLOBAL_SECTION, KEY_RESOLUTION_HEIGHT);
        if (StringUtils.isEmpty(val)) {
            return RESOLUTION_HEIGHT_DEFAULT;
        }
        return Double.parseDouble(val);
    }

    public static void SetResolutionHeight(Double heitgh) {
        getInstance().set(GLOBAL_SECTION, KEY_RESOLUTION_HEIGHT, String.valueOf(heitgh));
    }

    public static double GetIconDefalutWidth() {
        String val = getInstance().get(GLOBAL_SECTION, KEY_ICON_DEFAULT_WIDTH);
        if (StringUtils.isEmpty(val)) {
            return ICON_DEFAULT_WIDTH_SIZE;
        }
        return Double.parseDouble(val);
    }

    public static void SetIconDefalutWidth(Double width) {
        getInstance().set(GLOBAL_SECTION, KEY_ICON_DEFAULT_WIDTH, String.valueOf(width));
    }

    public static double GetIconDefalutHeight() {
        String val = getInstance().get(GLOBAL_SECTION, KEY_ICON_DEFAULT_HEIGHT);
        if (StringUtils.isEmpty(val)) {
            return ICON_DEFAULT_HEIGHT_SIZE;
        }
        return Double.parseDouble(val);
    }

    public static void SetIconDefalutHeight(Double height) {
        getInstance().set(GLOBAL_SECTION, KEY_ICON_DEFAULT_HEIGHT, String.valueOf(height));
    }

    public static double GetIconMinWidth() {
        String val = getInstance().get(GLOBAL_SECTION, KEY_ICON_MIN_WIDTH);
        if (StringUtils.isEmpty(val)) {
            return ICON_MIN_WIDTH_SIZE;
        }
        return Double.parseDouble(val);
    }

    public static void SetIconMinWidth(Double width) {
        getInstance().set(GLOBAL_SECTION, KEY_ICON_MIN_WIDTH, String.valueOf(width));
    }

    public static double GetIconMinHeight() {
        String val = getInstance().get(GLOBAL_SECTION, KEY_ICON_MIN_HEIGHT);
        if (StringUtils.isEmpty(val)) {
            return ICON_MIN_HEIGHT_SIZE;
        }
        return Double.parseDouble(val);
    }

    public static void SetIconMinHeight(Double height) {
        getInstance().set(GLOBAL_SECTION, KEY_ICON_MIN_HEIGHT, String.valueOf(height));
    }

    public static Color GetIconForeColor() {
        String val = getInstance().get(GLOBAL_SECTION, KEY_ICON_FORE_COLOR);
        if (StringUtils.isEmpty(val)) {
            return ICON_FORE_COLOR;
        }
        return Color.valueOf(val);
    }

    public static void SetIconForeColor(Color color) {
        getInstance().set(GLOBAL_SECTION, KEY_ICON_FORE_COLOR, String.valueOf(color));
    }

    public static Color GetIconBackColor() {
        String val = getInstance().get(GLOBAL_SECTION, KEY_ICON_BACK_COLOR);
        if (StringUtils.isEmpty(val)) {
            return ICON_BACK_COLOR;
        }
        return Color.valueOf(val);
    }

    public static void SetIconBackColor(Color color) {
        getInstance().set(GLOBAL_SECTION, KEY_ICON_BACK_COLOR, String.valueOf(color));
    }

    public static void RemoveIcon(int no) {
        getInstance().ini.remove(ICON_SECTION + no);
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

    public static void SetIconPosX(int no, Double posX) {
        getInstance().set(ICON_SECTION + no, KEY_ICON_POSX, String.valueOf(posX));
    }

    public static double GetIconPosY(int no) {
        String val = getInstance().get(ICON_SECTION + no, KEY_ICON_POSY);
        if (StringUtils.isEmpty(val)) {
            return 0;
        }
        return Double.parseDouble(val);
    }

    public static void SetIconPosY(int no, Double posX) {
        getInstance().set(ICON_SECTION + no, KEY_ICON_POSY, String.valueOf(posX));
    }

    public static double GetIconWidth(int no) {
        String val = getInstance().get(ICON_SECTION + no, KEY_ICON_WIDTH);
        if (StringUtils.isEmpty(val)) {
            return 0;
        }
        return Double.parseDouble(val);
    }

    public static void SetIconWidth(int no, Double height) {
        getInstance().set(ICON_SECTION + no, KEY_ICON_WIDTH, String.valueOf(height));
    }

    public static double GetIconHeight(int no) {
        String val = getInstance().get(ICON_SECTION + no, KEY_ICON_HEIGHT);
        if (StringUtils.isEmpty(val)) {
            return 0;
        }
        return Double.parseDouble(val);
    }

    public static void SetIconHeight(int no, Double height) {
        getInstance().set(ICON_SECTION + no, KEY_ICON_HEIGHT, String.valueOf(height));
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

    private void initialize() {
        try {
            File file = new File(System.getenv("ADFACTORY_HOME") + File.separator + "conf" + File.separator + "adFloorLayoutEditor.ini");
            ini.setFile(file);
            if (!file.exists()) {
                SetBackImagePath("");
                SetMaxIconNum(MAX_ICON_NUM_DEFAULT);
                SetResolutionWidth(RESOLUTION_WIDTH_DEFAULT);
                SetResolutionHeight(RESOLUTION_HEIGHT_DEFAULT);
                SetFitResolution(FIT_RESOLUTION_DEFAULT);
                SetIconDefalutWidth(ICON_DEFAULT_WIDTH_SIZE);
                SetIconDefalutHeight(ICON_DEFAULT_HEIGHT_SIZE);
                SetIconForeColor(ICON_FORE_COLOR);
                SetIconBackColor(ICON_BACK_COLOR);
                ini.store();
            }
            ini.load();
        }
        catch (IOException ex) {
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
