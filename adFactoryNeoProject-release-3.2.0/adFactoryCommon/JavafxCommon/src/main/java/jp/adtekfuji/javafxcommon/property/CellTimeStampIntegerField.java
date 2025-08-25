/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.StringTime;
import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.control.Tooltip;
import jp.adtekfuji.javafxcommon.Locale;
import jp.adtekfuji.javafxcommon.controls.TimeTextField;

/**
 * 引数がIntegerProperty型の時間入力セル
 *
 * @author koga
 */
public class CellTimeStampIntegerField extends AbstractCell {

    private final IntegerProperty integerData;
    private final StringProperty stringData;
    private ChangeListener<String> actionListner = null;
    private final int length;
    private Integer maxMillis = null;
    private final Tooltip tooltip = new Tooltip();

    /**
     * コンストラクタ
     *
     * @param cellInterface セルのインターフェイス
     * @param integerData 数値プロパティ
     * @param length テキストフィールドの幅
     * @param maxMillis 時間テキストフィールドの上限値(ミリ秒)
     */
    public CellTimeStampIntegerField(CellInterface cellInterface, IntegerProperty integerData, int length, int maxMillis) {
        super(cellInterface);
        this.integerData = integerData;
        this.stringData = new SimpleStringProperty();
        this.length = length;
        this.maxMillis = maxMillis;
    }

    /**
     * リスナーを設定する
     *
     * @param actionListner
     * @return
     */
    public CellTimeStampIntegerField actionListner(ChangeListener<String> actionListner) {
        this.actionListner = actionListner;
        return this;
    }

    /**
     * ノードを生成する。
     */
    @Override
    public void createNode() {
        final String minStringTime = "00:00:00";
        String taktTime = minStringTime;
        if (Objects.nonNull(this.integerData.get())) {
            // タクトタイムを時：分：秒の形式に変換
            taktTime = StringTime.convertMillisToStringTime(this.integerData.get());
        }
        TimeTextField timeTextField = new TimeTextField(taktTime, this.maxMillis);
        timeTextField.setPrefWidth(this.length);
        stringData.bind(timeTextField.textProperty());
        if (Objects.nonNull(this.actionListner)) {
            timeTextField.textProperty().addListener(this.actionListner);
        }
        timeTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            String time = timeTextField.textProperty().get();
            // 時：分：秒の形式をミリ秒に変換
            long millisecond = StringTime.convertStringTimeToMillis(time);
            integerData.set((int)millisecond);
        });

        timeTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                return;
            }

            String text = timeTextField.getText();
            if (Objects.nonNull(text) && Objects.nonNull(this.maxMillis)
                    && (!StringTime.validStringTime(text) || StringTime.convertStringTimeToMillis(text) > this.maxMillis)) {
                // 最大値(20日)より大きい場合
                final String maxStringTime = StringTime.convertMillisToStringTime(this.maxMillis);
                timeTextField.setText(maxStringTime);

                // ヒントの表示
                tooltip.setText(String.format(LocaleUtils.getString(Locale.RANGE_THREE_DIGIT_HOUR), minStringTime, maxStringTime));
                tooltip.setStyle("-fx-font-size: 10pt; -fx-text-fill: black; -fx-background-color: #F8F0D7; -fx-background-radius: 0 0 0 0;");
                Bounds bounds = timeTextField.localToScreen(timeTextField.getBoundsInLocal());
                tooltip.show(timeTextField, bounds.getMinX(), bounds.getMaxY() + 2.0);

                timeTextField.requestFocus();
            } else {
                tooltip.setText("");
                tooltip.hide();
            }
        });

        super.setNode(timeTextField);
    }
}
