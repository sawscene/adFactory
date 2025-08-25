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

public class RegexpValidator extends StringConverter<String> {

    private ValidationListener exceptionListener = null;
    private int maxChars = 0;
    private final String regexp;
    private String oldValue;

    private RegexpValidator(String regexp) {
        this.regexp = regexp;
    }

    public static final String NUMBER = "\\d*";
    public static final String ALPHABET = "[a-zA-Z]*";
    public static final String ALPHABET_AND_NUMBER = "[0-9a-zA-Z]*";
    public static final String IP_ADDRESS = "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})|(\\w{1,2})\\.(\\w{1,2})\\.(\\w{1,2})\\.(\\w{1,2})";
    public static final String MAC_ADDRESS = "(\\d{1,2}):(\\d{1,2}):(\\d{1,2}):(\\d{1,2}):(\\d{1,2}):(\\d{1,2})";

    /**
     * TextFieldに検証を付ける
     *
     * @param textField
     * @param stringProperty
     * @param regexp
     * @return RegexpValidator instance
     */
    public static RegexpValidator bindValidator(TextField textField, StringProperty stringProperty, String regexp) {
        RegexpValidator validator = new RegexpValidator(regexp);
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
                } else if (!textField.getText().matches(regexp)) {
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

    public RegexpValidator setValidationListener(ValidationListener exceptionListener) {
        this.exceptionListener = exceptionListener;
        return this;
    }

    public RegexpValidator setMaxChars(int maxChars) {
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
            exceptionListener.thereProblem(field, new RegexpValidatorException(field.getText(), regexp, maxChars));
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
