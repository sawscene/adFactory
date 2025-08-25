/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import adtekfuji.utility.StringUtils;
import java.util.Objects;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import jp.adtekfuji.javafxcommon.controls.TimeHMTextField;

/**
 * 時間入力セル
 *
 * @author adtekfuji
 */
public class CellTimeHMStampField extends AbstractCell {

    private final StringProperty textProperty;
    private ChangeListener<String> actionListner = null;

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
    public CellTimeHMStampField(CellInterface cellInterface, StringProperty textProperty) {
        this(cellInterface, textProperty, false);
    }

    /**
     * コンストラクタ
     *
     * @param cellInterface セルのインターフェイス
     * @param textProperty 文字列プロパティ
     * @param isDisabled 項目の有効/無効状態(true：無効、false：有効)
     */
    public CellTimeHMStampField(CellInterface cellInterface, StringProperty textProperty, boolean isDisabled) {
        super(cellInterface);
        this.textProperty = textProperty;
        this.isDisabled = isDisabled;
    }

    public CellTimeHMStampField actionListner(ChangeListener<String> actionListner) {
        this.actionListner = actionListner;
        return this;
    }

    /**
     * ノードを生成する。
     */
    @Override
    public void createNode() {
        String time = this.textProperty.get();
        if (StringUtils.isEmpty(time)) {
            time = "00:00";
        }
        TimeHMTextField textField = new TimeHMTextField(time);
        this.textProperty.bind(textField.textProperty());
        if (Objects.nonNull(this.actionListner)) {
            textField.textProperty().addListener(this.actionListner);
        }
        this.setDisable(this.isDisabled);
        super.setNode(textField);
    }
}
