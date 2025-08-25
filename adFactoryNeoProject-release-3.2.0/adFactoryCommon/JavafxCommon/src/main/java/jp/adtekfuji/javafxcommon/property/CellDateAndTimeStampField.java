/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.DateValidator;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

/**
 * 日付時間用セルコントロール
 *
 * @author e-mori
 */
public class CellDateAndTimeStampField extends AbstractCell {

    private final ObjectProperty<Date> stringData;
    private final static String DATETIME_REGEX = "\\d|:|-|/|\\s";
    private String beforeDate = "";

    /**
     * コンストラクタ
     *
     * @param ｃellInterface
     * @param date
     */
    public CellDateAndTimeStampField(CellInterface ｃellInterface, ObjectProperty<Date> date) {
        super(ｃellInterface);
        this.stringData = date;
    }

    /**
     * コンストラクタ
     *
     * @param ｃellInterface
     * @param date
     * @param isDisable
     */
    public CellDateAndTimeStampField(CellInterface ｃellInterface, ObjectProperty<Date> date, boolean isDisable) {
        super(ｃellInterface, isDisable);
        this.stringData = date;
    }

    /**
     * セルコントロールを生成する
     *
     */
    @Override
    public void createNode() {
        SimpleDateFormat toConvertDatePattern = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));
        TextField textField = new TextField(toConvertDatePattern.format(stringData.get()));
        textField.setDisable(this.isDisable());
        textField.textProperty().bindBidirectional(stringData, toConvertDatePattern);
        textField.addEventFilter(KeyEvent.KEY_TYPED, (KeyEvent event) -> {
            if (!event.getCharacter().matches(DATETIME_REGEX)) {
                event.consume();
            }
        });
        textField.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) -> {
            if (newPropertyValue) {
                beforeDate = textField.getText();
            } else {
                if(!DateValidator.isValid(textField.getText(), LocaleUtils.getString("key.DateTimeFormat"))){
                    textField.setText(beforeDate);
                }
            }
        });
        super.setNode(textField);
    }
}
