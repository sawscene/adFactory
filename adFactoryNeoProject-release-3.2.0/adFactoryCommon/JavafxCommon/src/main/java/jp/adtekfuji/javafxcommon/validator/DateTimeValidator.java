/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

/**
 * 日付確認クラス
 *
 * @author e.mori
 * @version 1.6.1
 * @since 2017.1.11.Wen
 */
public class DateTimeValidator extends StringConverter<Date> {

    private final SimpleDateFormat formatter;
    private ValidationListener exceptionListener = null;
    private String oldValue;

    public DateTimeValidator(SimpleDateFormat formatter) {
        this.formatter = formatter;
    }

    /**
     * TextFieldに検証を付ける
     *
     * @param textField バリデーションをかける入力フォーム
     * @param dateTimeProperty 日付情報
     * @param timeFormatter 日付のフォーマット情報
     * @return DateTimeValidator instance バリデータのインスタンスを返す
     */
    public static DateTimeValidator bindValidator(TextField textField, ObjectProperty<Date> dateTimeProperty, SimpleDateFormat timeFormatter) {
        DateTimeValidator validator = new DateTimeValidator(timeFormatter);
        if (Objects.isNull(dateTimeProperty.get())) {
            dateTimeProperty.set(new Date());
        }
        validator.oldValue = validator.toString(dateTimeProperty.get());
        textField.textProperty().bindBidirectional(dateTimeProperty, validator);
        textField.addEventFilter(KeyEvent.KEY_TYPED, (KeyEvent event) -> {
            if (!event.getCharacter().matches("\\d|:|-|/")) {
                event.consume();
            }
        });
        textField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue1, Boolean newValue) -> {
            if (!newValue) {
                if (textField.getText().equals("")) {
                    validator.oldValue = textField.getText();
                    validator.noProblem(textField);
                }
                try {
                    validator.formatter.parse(textField.getText());
                } catch (DateTimeParseException | ParseException ex) {
                    validator.thereProblem(textField);
                    textField.setText(validator.oldValue);
                    return;
                }
                validator.oldValue = textField.getText();
                validator.noProblem(textField);
            }
        });
        return validator;
    }

    public DateTimeValidator setValidationListener(ValidationListener exceptionListener) {
        this.exceptionListener = exceptionListener;
        return this;
    }

    /**
     * 問題なく文字列の検証が終了した場合にリスナーに返す処理
     * 
     * @param field 実行結果
     */
    private void noProblem(TextField field) {
        if (Objects.nonNull(exceptionListener)) {
            exceptionListener.noProblem(field);
        }
    }
    
    /**
     * 問題が発生した場合に発生個所をリスナーに返す処理
     * 
     * @param field 実行結果
     */
    private void thereProblem(TextField field) {
        if (Objects.isNull(exceptionListener)) {
            return;
        }
        exceptionListener.thereProblem(field, new DateTimeValidatorException(field.getText()));
    }

    /**
     * 日付を文字列にして返す
     * 
     * @param date 日付の情報
     * @return 文字列で返す
     */
    
    @Override
    public String toString(Date date) {
        return formatter.format(date);
    }

    /**
     * 文字列を日付にして返す
     * 
     * @param string 日付の文字列情報
     * @return 日付で返す
     */
    @Override
    public Date fromString(String string) {
        try {
            return formatter.parse(string);
        } catch (DateTimeParseException | ParseException ex) {
        }
        return null;
    }
}

