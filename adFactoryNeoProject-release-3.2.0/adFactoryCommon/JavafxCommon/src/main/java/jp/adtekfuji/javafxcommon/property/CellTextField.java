/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import javafx.beans.property.StringProperty;
import jp.adtekfuji.javafxcommon.controls.RestrictedTextField;

/**
 * テキストフィールド
 *
 * @author s-heya
 */
public class CellTextField extends AbstractCell {

    private final StringProperty valueProperty;
    private final int maxLength;

    /**
     * 項目の有効/無効状態(true：無効、false：有効)
     */
    private final boolean isDisabled;

    /**
     * コンストラクタ
     * 
     * @param cell セルのインターフェイス
     * @param valueProperty 文字列プロパティ
     */
    public CellTextField(CellInterface cell, StringProperty valueProperty) {
        this(cell, valueProperty, 1024);
    }

    /**
     * コンストラクタ
     * 
     * @param cell セルのインターフェイス
     * @param valueProperty 文字列プロパティ
     * @param maxLength 最大長
     */
    public CellTextField(CellInterface cell, StringProperty valueProperty, int maxLength) {
        this(cell, valueProperty, maxLength, false);
    }

    /**
     * コンストラクタ
     * 
     * @param cell セルのインターフェイス
     * @param valueProperty 文字列プロパティ
     * @param isDisabled 項目の有効/無効状態(true：無効、false：有効)
     */
    public CellTextField(CellInterface cell, StringProperty valueProperty, boolean isDisabled) {
        this(cell, valueProperty, 1024, isDisabled);
    }

    /**
     * コンストラクタ
     * 
     * @param cell セルのインターフェイス
     * @param valueProperty 文字列プロパティ
     * @param maxLength 最大長
     * @param isDisabled 項目の有効/無効状態(true：無効、false：有効)
     */
    public CellTextField(CellInterface cell, StringProperty valueProperty, int maxLength, boolean isDisabled) {
        super(cell);
        this.valueProperty = valueProperty;
        this.maxLength = maxLength;
        this.isDisabled = isDisabled;
    }

    /**
     * ノードを生成する
     */
    @Override
    public void createNode() {
        RestrictedTextField textField = new RestrictedTextField(valueProperty.get());
        textField.setMaxLength(this.maxLength);
        this.valueProperty.bindBidirectional(textField.textProperty());
        this.setDisable(this.isDisabled);
        super.setNode(textField);
    }
}
