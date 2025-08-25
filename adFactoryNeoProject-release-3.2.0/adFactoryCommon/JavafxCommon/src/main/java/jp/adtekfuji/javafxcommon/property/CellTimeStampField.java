/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.StringTime;
import adtekfuji.utility.StringUtils;
import java.util.Objects;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.control.Tooltip;
import jp.adtekfuji.javafxcommon.Locale;
import jp.adtekfuji.javafxcommon.controls.TimeTextField;

/**
 * 時間入力セル
 *
 * @author adtekfuji
 */
public class CellTimeStampField extends AbstractCell {

    private final StringProperty textProperty;
    private ChangeListener<String> actionListner = null;
    private Integer maxMillis = null;
    private final Tooltip tooltip = new Tooltip();

    /**
     * 項目の有効/無効状態(true：無効、false：有効)
     */
    private final boolean isDisabled;

    /**
     * コンストラクタ
     *
     * @param cellInterface セルのインターフェイス
     * @param textProperty 文字列プロパティ
     */
    public CellTimeStampField(CellInterface cellInterface, StringProperty textProperty) {
        this(cellInterface, textProperty, false);
    }

    /**
     * コンストラクタ
     *
     * @param cellInterface セルのインターフェイス
     * @param textProperty 文字列プロパティ
     * @param isDisabled 項目の有効/無効状態(true：無効、false：有効)
     * @param maxMillis 時間テキストフィールドの上限値(ミリ秒)
     */
    public CellTimeStampField(CellInterface cellInterface, StringProperty textProperty, boolean isDisabled, int maxMillis) {
        this(cellInterface, textProperty, false);
        this.maxMillis = maxMillis;
    }

    /**
     * コンストラクタ
     *
     * @param cellInterface セルのインターフェイス
     * @param textProperty 文字列プロパティ
     * @param isDisabled 項目の有効/無効状態(true：無効、false：有効)
     */
    public CellTimeStampField(CellInterface cellInterface, StringProperty textProperty, boolean isDisabled) {
        super(cellInterface);
        this.textProperty = textProperty;
        this.isDisabled = isDisabled;
    }

    public CellTimeStampField actionListner(ChangeListener<String> actionListner) {
        this.actionListner = actionListner;
        return this;
    }

    /**
     * ノードを生成する。
     */
    @Override
    public void createNode() {
        final String minStringTime = "00:00:00";
        String time = this.textProperty.get();
        if (StringUtils.isEmpty(time)) {
            time = minStringTime;
        }
        TimeTextField textField = new TimeTextField(time, this.maxMillis);
        this.textProperty.bind(textField.textProperty());
        if (Objects.nonNull(this.actionListner)) {
            textField.textProperty().addListener(this.actionListner);
        }

        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                return;
            }

            String text = textField.getText();
            if (Objects.nonNull(text) && Objects.nonNull(this.maxMillis)
                    && (!StringTime.validStringTime(text) || StringTime.convertStringTimeToMillis(text) > this.maxMillis)) {
                // 最大値(20日)より大きい場合
                final String maxStringTime = StringTime.convertMillisToStringTime(this.maxMillis);
                textField.setText(maxStringTime);

                // ヒントの表示
                tooltip.setText(String.format(LocaleUtils.getString(Locale.RANGE_THREE_DIGIT_HOUR), minStringTime, maxStringTime));
                tooltip.setStyle("-fx-font-size: 10pt; -fx-text-fill: black; -fx-background-color: #F8F0D7; -fx-background-radius: 0 0 0 0;");
                Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                tooltip.show(textField, bounds.getMinX(), bounds.getMaxY() + 2.0);

                textField.requestFocus();
            } else {
                tooltip.setText("");
                tooltip.hide();
            }
        });

        this.setDisable(this.isDisabled);
        super.setNode(textField);
    }
}
