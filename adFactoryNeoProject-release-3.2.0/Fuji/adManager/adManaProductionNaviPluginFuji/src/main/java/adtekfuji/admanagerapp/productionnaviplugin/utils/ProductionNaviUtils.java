/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.utils;

import java.io.File;
import java.util.Objects;
import java.util.regex.Pattern;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author (TST)H.Nishimura
 * @version 2.0.0
 * @since 2018/09/28
 */
public class ProductionNaviUtils {

    // 
    // タブ：CSV形式
    public static final int IMPORT_TAB_IDX_CSV = 0;
    // タブ：Excel形式
    public static final int IMPORT_TAB_IDX_EXCEL = 1;

    // タブ：カンバン形式
    public static final int WORKPLAN_TAB_IDX_KANBAN = 0;
    // タブ：カンバンプロパティ形式
    public static final int WORKPLAN_TAB_IDX_KANBAN_PROPERTY = 1;
    // タブ：工程カンバン形式
    public static final int WORKPLAN_TAB_IDX_WORK_KANBAN = 2;
    // タブ：工程カンバンプロパティ形式
    public static final int WORKPLAN_TAB_IDX_WORK_KANBAN_PROPERTY = 3;
    // タブ：カンバンステータス形式
    public static final int WORKPLAN_TAB_IDX_KANBAN_STATUS = 4;

    public static final int WORKPLAN_TAB_IDX_PRODUCT = 5;// 製品情報タブのインデックス

    // 入力エリアの背景
//    public Background COLOR_ERROR = new Background(new BackgroundFill( Color.RED , new CornerRadii(5) , Insets.EMPTY ));
//    public Background COLOR_NORMAL = new Background(new BackgroundFill( Color.WHITE , new CornerRadii(5) , Insets.EMPTY ));
    //
    public static final Border INPUT_ERROR = new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
    public static final Border INPUT_NORMAL = new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
    public static final String STYLE_NORMAL = "-fx-border-color : gray;";
    public static final String STYLE_ERROR = "-fx-border-color : red;";
//    public static final String STYLE_NORMAL= "-fx-text-fill : white;";
//    public static final String STYLE_ERROR = "-fx-text-fill : red;";

    /**
     * 入力データのチェック
     *
     * @param _textField テキスト入力
     * @return 判定結果
     */
    public static boolean isNull(TextField _textField) {
        if (!ProductionNaviUtils.isNotNull(_textField.getText())) {
            setFieldNormal(_textField);
            return true;
        }

        setFieldError(_textField);
        return false;
    }

    /**
     * 入力データのチェック
     *
     * @param _object 日付入力
     * @return 判定結果
     */
    public static boolean isNull(DatePicker _object) {
        if (Objects.nonNull(_object.getValue())) {
            setFieldNormal(_object);
            return true;
        }

        setFieldError(_object);
        return false;
    }

    /**
     * 入力データのチェック
     *
     * @param _object 日付入力
     * @return 判定結果
     */
    public static boolean isNull(ListView<?> _object) {
        if (Objects.nonNull(_object) && _object.getItems().size() > 0) {
            setFieldNormal(_object);
            return true;
        }

        setFieldError(_object);
        return false;
    }

    /**
     * 入力データのチェック
     *
     * @param _object 日付入力
     * @return 判定結果
     */
    public static boolean isNull(ComboBox _object) {
        if (Objects.nonNull(_object.getValue())) {
            setFieldNormal(_object);
            return true;
        }

        setFieldError(_object);
        return false;
    }

    /**
     * 入力データのチェック
     *
     * @param _textField テキスト入力
     * @return 判定結果
     */
    public static boolean isNotNull(TextField _textField) {
        if (ProductionNaviUtils.isNotNull(_textField.getText())) {
            setFieldNormal(_textField);
            return true;
        }

        setFieldError(_textField);
        return false;
    }

    /**
     * 入力データのチェック
     *
     * @param _object コンボボックス
     * @return 判定結果
     */
    public static boolean isNotNull(ComboBox _object) {
        if (ProductionNaviUtils.isNotNull(_object.getSelectionModel().getSelectedItem().toString())) {
            setFieldNormal(_object);
            return true;
        }

        setFieldError(_object);
        return false;
    }

    /**
     * 入力データのチェック
     *
     * @param value テキスト入力
     * @return 判定結果
     */
    public static boolean isNotNull(String value) {
        if (StringUtils.isNotEmpty(value)) {
            if (StringUtils.isNotBlank(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 数値データのチェック
     *
     * @param _textField テキスト入力
     * @return 判定結果
     */
    public static boolean isNumber(TextField _textField) {
        return isNumber(_textField, true);
    }

    /**
     * 数値データのチェック
     *
     * @param _textField テキスト入力
     * @param isEmptyCheck 未入力チェックを行なう？
     * @return 判定結果
     */
    public static boolean isNumber(TextField _textField, boolean isEmptyCheck) {
        if (ProductionNaviUtils.isNotNull(_textField)) {
            if (NumberUtils.isDigits(_textField.getText())) {
                setFieldNormal(_textField);
                return true;
            }
        } else if (!isEmptyCheck) {
            setFieldNormal(_textField);
            return true;
        }

        setFieldError(_textField);
        return false;
    }

    /**
     * アルファベットのチェック
     *
     * @param _textField テキスト入力
     * @param size
     * @return 判定結果
     */
    public static boolean isAlphabet(TextField _textField, int size) {
        return isAlphabet(_textField, size, true);
    }

    /**
     * アルファベットのチェック
     *
     * @param _textField テキスト入力
     * @param size
     * @param isEmptyCheck 未入力チェックを行なう？
     * @return 判定結果
     */
    public static boolean isAlphabet(TextField _textField, int size, boolean isEmptyCheck) {
        if (ProductionNaviUtils.isNotNull(_textField)) {
            if (Pattern.matches("^[a-zA-Z]+$", _textField.getText())) {
                if (_textField.getText().length() <= size) {
                    setFieldNormal(_textField);
                }
                return true;
            }
        } else if (!isEmptyCheck) {
            setFieldNormal(_textField);
            return true;
        }

        setFieldError(_textField);
        return false;
    }

    /**
     * 入力エリアの異常
     *
     * @param _textField テキスト入力
     */
    public static void setFieldError(TextField _textField) {
        _textField.setBorder(INPUT_ERROR);
//        _textField.setStyle(STYLE_ERROR);
    }

    /**
     * 入力エリアの異常
     *
     * @param _object リストビュー
     */
    public static void setFieldError(ListView<?> _object) {
        _object.setBorder(INPUT_ERROR);
//        _object.setStyle(STYLE_ERROR);
    }

    /**
     * 入力エリアの異常
     *
     * @param _object コンボボックス
     */
    public static void setFieldError(ComboBox _object) {
        _object.setBorder(INPUT_ERROR);
        _object.setStyle(STYLE_ERROR);
    }

    /**
     * 入力エリアの異常
     *
     * @param _object 日付入力
     */
    public static void setFieldError(DatePicker _object) {
//        _object.setBorder(INPUT_ERROR);
        _object.setStyle(STYLE_ERROR);
    }

    /**
     * 入力エリアの正常
     *
     * @param _textField テキスト入力
     */
    public static void setFieldNormal(TextField _textField) {
        _textField.setBorder(INPUT_NORMAL);
//        _textField.setStyle(STYLE_NORMAL);
    }

    /**
     * 入力エリアの正常
     *
     * @param _object リストビュー
     */
    public static void setFieldNormal(ListView<?> _object) {
        _object.setBorder(INPUT_NORMAL);
//        _object.setStyle(STYLE_NORMAL);
    }

    /**
     * 入力エリアの正常
     *
     * @param _object コンボボックス
     */
    public static void setFieldNormal(ComboBox _object) {
        _object.setBorder(INPUT_NORMAL);
//        _object.setStyle(STYLE_NORMAL);
    }

    /**
     * 入力エリアの正常
     *
     * @param _object 日付入力
     */
    public static void setFieldNormal(DatePicker _object) {
        _object.setBorder(INPUT_NORMAL);
//        _object.setStyle(STYLE_NORMAL);
    }

    /**
     * ファイルのチェック処理
     *
     * @param mode ファイルタイプ
     * @param filename ファイル名
     * @return 結果
     */
    public static boolean isFileCheck(int mode, String filename) {
        boolean value = false;

        if (!adtekfuji.utility.StringUtils.isEmpty(filename)) {
            // ファイルが存在しない？
            File file = new File(filename);
            if (file.exists() && file.isFile() && file.canRead()) {
                switch (mode) {
                    case ProductionNaviUtils.IMPORT_TAB_IDX_CSV:
                        if (file.getPath().toLowerCase().endsWith(".csv")) {
                            value = true;
                        } else if (file.getPath().toLowerCase().endsWith(".csv")) {
                            value = true;
                        }

                        break;
                    case ProductionNaviUtils.IMPORT_TAB_IDX_EXCEL:
                        if (file.getPath().toLowerCase().endsWith(".xls")) {
                            value = true;
                        } else if (file.getPath().toLowerCase().endsWith(".xlsx") || filename.toLowerCase().endsWith(".xlsm")) {
                            value = true;
                        }
                        break;
                }
            } else {
                LogManager.getLogger().warn("Not File={}, exists={}, isDirectory={}, canRead={}", filename, file.exists(), file.isDirectory(), file.canRead());
            }
        }

        return value;
    }
}
