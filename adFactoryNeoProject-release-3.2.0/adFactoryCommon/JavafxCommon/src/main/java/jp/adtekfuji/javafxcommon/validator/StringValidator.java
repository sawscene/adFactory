/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.validator;

import java.util.Objects;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class StringValidator extends StringConverter<String> {

    private ValidationListener exceptionListener = null;
    private int maxChars = 0;
    private String oldValue;

    private StringValidator() {
    }

    /**
     * TextFieldに検証を付ける
     *
     * @param textField
     * @param stringProperty
     * @return StringValidator instance
     */
    public static StringValidator bindValidator(TextField textField, StringProperty stringProperty) {
        StringValidator validator = new StringValidator();
        validator.oldValue = textField.getText();
        textField.textProperty().bindBidirectional(stringProperty, validator);
        textField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue1, Boolean newValue) -> {
            if (!newValue) {
                if (Objects.isNull(textField.getText()) || textField.getText().equals("")) {
                    validator.oldValue = textField.getText();
                    validator.noProblem(textField);
                } else if (validator.maxChars != 0 && textField.getText().length() > validator.maxChars) {
                    validator.thereProblem(textField);
                    textField.setText(validator.oldValue);
                } else {
                    validator.oldValue = textField.getText();
                    validator.noProblem(textField);
                }
            }
        });
        return validator;
    }

    public StringValidator setValidationListener(ValidationListener exceptionListener) {
        this.exceptionListener = exceptionListener;
        return this;
    }

    public StringValidator setMaxChars(int maxChars) {
        this.maxChars = maxChars;
        return this;
    }

    private void noProblem(TextField field) {
        if (Objects.nonNull(exceptionListener)) {
            exceptionListener.noProblem(field);
        }
    }

    private void thereProblem(TextField field) {
        if (Objects.nonNull(exceptionListener)) {
            exceptionListener.thereProblem(field, new StringValidatorException(field.getText(), maxChars));
        }
    }

    @Override
    public String toString(String object) {
        return object;
    }

    @Override
    public String fromString(String string) {
        return string;
    }

}
