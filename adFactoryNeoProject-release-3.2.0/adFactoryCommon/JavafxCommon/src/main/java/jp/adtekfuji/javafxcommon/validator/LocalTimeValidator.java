/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.validator;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

public class LocalTimeValidator extends StringConverter<LocalTime> {

    private final DateTimeFormatter formatter;
    private ValidationListener exceptionListener = null;
    private String oldValue;

    private LocalTimeValidator(DateTimeFormatter timeFormatter) {
        formatter = timeFormatter;
    }

    /**
     * TextFieldに検証を付ける
     *
     * @param textField
     * @param taktTimeProperty
     * @param timeFormatter
     * @return LocalTimeValidator instance
     */
    public static LocalTimeValidator bindValidator(TextField textField, ObjectProperty<LocalTime> taktTimeProperty, DateTimeFormatter timeFormatter) {
        LocalTimeValidator validator = new LocalTimeValidator(timeFormatter);
        if (Objects.isNull(taktTimeProperty.get())) {
            taktTimeProperty.set(LocalTime.of(0, 0, 0));
        }
        validator.oldValue = validator.toString(taktTimeProperty.get());
        textField.textProperty().bindBidirectional(taktTimeProperty, validator);
        textField.addEventFilter(KeyEvent.KEY_TYPED, (KeyEvent event) -> {
            if (!event.getCharacter().matches("\\d|:")) {
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
                    validator.formatter.parse(textField.getText(), LocalTime::from);
                } catch (DateTimeParseException ex) {
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

    public LocalTimeValidator setValidationListener(ValidationListener exceptionListener) {
        this.exceptionListener = exceptionListener;
        return this;
    }

    private void noProblem(TextField field) {
        if (Objects.nonNull(exceptionListener)) {
            exceptionListener.noProblem(field);
        }
    }

    private void thereProblem(TextField field) {
        if (Objects.isNull(exceptionListener)) {
            return;
        }
        exceptionListener.thereProblem(field, new LocalTimeValidatorException(field.getText()));
    }

    @Override
    public String toString(LocalTime object) {
        return formatter.format(object);
    }

    @Override
    public LocalTime fromString(String string) {
        try {
            return formatter.parse(string, LocalTime::from);
        } catch (DateTimeParseException ex) {

        }
        return null;
    }

}
