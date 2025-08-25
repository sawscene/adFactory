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
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

public class DoubleValidator extends StringConverter<Number> {
    
    private class Range {
        
        private final double min;
        private final double max;
        
        public Range(double min, double max) {
            this.min = min;
            this.max = max;
        }
        
        public Range(double num) {
            this.min = num;
            this.max = num;
        }
    }
    
    private final NumberFormat formatter = NumberFormat.getIntegerInstance();
    private ValidationListener exceptionListener = null;
    private final int scale;
    private final List<Range> limitRanges = new ArrayList<>();
    private String oldValue;
    
    private DoubleValidator(int scale) {
        this.scale = scale;
    }

    /**
     * TextFieldに検証を付ける
     *
     * @param textField
     * @param doubleProperty
     * @param scale 小数点以下の桁数
     * @return DoubleValidator instance
     */
    public static DoubleValidator bindValidator(TextField textField, DoubleProperty doubleProperty, int scale) {
        DoubleValidator validator = new DoubleValidator(scale);
        validator.oldValue = textField.getText();
        textField.textProperty().bindBidirectional(doubleProperty, validator);
        textField.addEventFilter(KeyEvent.KEY_TYPED, (KeyEvent event) -> {
            if (!event.getCharacter().matches("\\d|-|.")) {
                event.consume();
            }
        });
        textField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue1, Boolean newValue) -> {
            if (!newValue) {
                if (textField.getText().equals("")) {
                    validator.oldValue = textField.getText();
                    validator.noProblem(textField);
                    return;
                }
                BigDecimal num = new BigDecimal(textField.getText());
                if (num.scale() > validator.scale) {
                    validator.thereProblem(textField);
                    textField.setText(validator.oldValue);
                }
                boolean isMatch = false;
                for (Range range : validator.limitRanges) {
                    if (num.doubleValue() >= range.min && num.doubleValue() <= range.max) {
                        isMatch = true;
                    }
                }
                if (!validator.limitRanges.isEmpty() && !isMatch) {
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
    
    public DoubleValidator addRange(double num) {
        this.limitRanges.add(new Range(num));
        return this;
    }
    
    public DoubleValidator addRange(double min, double max) {
        this.limitRanges.add(new Range(min, max));
        return this;
    }
    
    public DoubleValidator setValidationListener(ValidationListener exceptionListener) {
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
        exceptionListener.thereProblem(field, new DoubleValidatorException(field.getText(), scale, sb.toString()));
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
