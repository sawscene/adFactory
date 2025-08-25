/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.validator;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

public class NumericValidator extends StringConverter<Number> {

    private class Range {

        private final long min;
        private final long max;

        public Range(long min, long max) {
            this.min = min;
            this.max = max;
        }

        public Range(long num) {
            this.min = num;
            this.max = num;
        }
    }

    private final NumberFormat formatter = NumberFormat.getIntegerInstance();
    private ValidationListener exceptionListener = null;
    private int maxDegit = 0;
    private final List<Range> limitRanges = new ArrayList<>();
    private String oldValue;

    private NumericValidator() {
    }

    /**
     * TextFieldに検証を付ける
     *
     * @param textField
     * @param integerProperty
     * @return NumericValidator instance
     */
    public static NumericValidator bindValidator(TextField textField, IntegerProperty integerProperty) {
        NumericValidator validator = new NumericValidator();
        validator.bindValidate(textField, integerProperty);
        return validator;
    }

    /**
     * TextFieldに検証を付ける
     *
     * @param textField
     * @param longProperty
     * @return NumericValidator instance
     */
    public static NumericValidator bindValidator(TextField textField, LongProperty longProperty) {
        NumericValidator validator = new NumericValidator();
        validator.bindValidate(textField, longProperty);
        return validator;
    }

    public NumericValidator setMaxDegit(int maxDegit) {
        this.maxDegit = maxDegit;
        return this;
    }

    public NumericValidator addRange(long num) {
        this.limitRanges.add(new Range(num));
        return this;
    }

    public NumericValidator addRange(long min, long max) {
        this.limitRanges.add(new Range(min, max));
        return this;
    }

    public NumericValidator setValidationListener(ValidationListener exceptionListener) {
        this.exceptionListener = exceptionListener;
        return this;
    }

    private void bindValidate(TextField textField, Property property) {
        this.oldValue = textField.getText();
        textField.textProperty().bindBidirectional(property, this);
        textField.addEventFilter(KeyEvent.KEY_TYPED, (KeyEvent event) -> {
            if (!event.getCharacter().matches("\\d|-")) {
                event.consume();
            }
        });
        textField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue1, Boolean newValue) -> {
            if (!newValue) {
                Number num = fromString(textField.getText());
                if (Objects.isNull(num)) {
                    this.oldValue = textField.getText();
                    noProblem(textField);
                    return;
                }
                if (maxDegit > 0 && BigDecimal.valueOf(num.longValue()).precision() > maxDegit) {
                    thereProblem(textField);
                    textField.setText(this.oldValue);
                    return;
                }
                boolean isMatch = false;
                for (Range range : limitRanges) {
                    if (num.longValue() >= range.min && num.longValue() <= range.max) {
                        isMatch = true;
                    }
                }
                if (!limitRanges.isEmpty() && !isMatch) {
                    thereProblem(textField);
                    textField.setText(this.oldValue);
                } else {
                    this.oldValue = textField.getText();
                    noProblem(textField);
                }
            }
        });
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
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (Range range : limitRanges) {
            if (count != 0) {
                sb.append(", ");
            }
            if (range.min == range.max) {
                sb.append(range.min);
            } else {
                sb.append(range.min).append("to").append(range.max);
            }
            count++;
        }
        exceptionListener.thereProblem(field, new NumericValidatorException(field.getText(), maxDegit, sb.toString()));
    }

    @Override
    public String toString(Number object) {
        return formatter.format(object);
    }

    @Override
    public Number fromString(String string) {
        try {
            return formatter.parse(string);
        } catch (ParseException ex) {

        }
        return null;
    }

}
