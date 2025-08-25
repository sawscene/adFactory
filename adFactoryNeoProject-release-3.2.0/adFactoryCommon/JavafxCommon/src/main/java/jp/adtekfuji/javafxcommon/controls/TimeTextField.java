/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.controls;

import adtekfuji.utility.StringUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextField;
import jp.adtekfuji.platform.enumerate.InputScope;
import jp.adtekfuji.platform.windows.PlatformService;

/**
 * 時間入力フィールド (HH:mm:ss形式)
 *
 * @author s-heya
 */
public class TimeTextField extends TextField {

    enum Unit {HOURS, MINUTES, SECONDS};
    private final Pattern timePattern;
    private final ReadOnlyIntegerWrapper hours;
    private final ReadOnlyIntegerWrapper minutes;
    private final ReadOnlyIntegerWrapper seconds;
    private Integer maxMillis = null;

    /**
     * コンストラクタ
     */
    public TimeTextField() {
        this("00:00:00");
    }

    /**
     * コンストラクタ
     */
    public TimeTextField(String time) {
        super(time);
        this.timePattern = Pattern.compile("\\d{2,3}:\\d\\d:\\d\\d");
        this.hours = new ReadOnlyIntegerWrapper(this, "hours");
        this.minutes = new ReadOnlyIntegerWrapper(this, "minutes");
        this.seconds = new ReadOnlyIntegerWrapper(this, "seconds");
        this.hours.bind(new TimeTextField.TimeUnitBinding(Unit.HOURS));
        this.minutes.bind(new TimeTextField.TimeUnitBinding(Unit.MINUTES));
        this.seconds.bind(new TimeTextField.TimeUnitBinding(Unit.SECONDS));

        if (!this.validate(time)) {
            throw new IllegalArgumentException("Invalid time: " + time);
        }

        this.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                PlatformService.SetInputScope(InputScope.ALPHANUMERIC_HALF);
            } else {
                PlatformService.SetInputScope(InputScope.DEFAULT);
            }
        });
    }
    
    /**
     * コンストラクタ
     * @param time 時間
     * @param maxMillis 時間テキストフィールドの上限値(ミリ秒)
     */
    public TimeTextField(String time, Integer maxMillis) {
        this(time);
        this.maxMillis = maxMillis;
    }

    public ReadOnlyIntegerProperty hoursProperty() {
        return this.hours.getReadOnlyProperty();
    }

    public int getHours() {
        return this.hours.get();
    }

    public ReadOnlyIntegerProperty minutesProperty() {
        return this.minutes.getReadOnlyProperty();
    }

    public int getMinutes() {
        return this.minutes.get();
    }

    public ReadOnlyIntegerProperty secondsProperty() {
        return this.seconds.getReadOnlyProperty();
    }

    public int getSeconds() {
        return this.seconds.get();
    }

    /**
     * 時間テキストフィールドの上限値(ミリ秒)を設定する。
     * 
     * @param maxMillis 時間テキストフィールドの上限値(ミリ秒)
     */
    public void setMaxMillis(Integer maxMillis) {
        this.maxMillis = maxMillis;
    }

   /**
     * 入力時間をDate型で取得する
     *
     * @return
     */
    public Date getDate() {
        Date date = null;
        try {
            SimpleDateFormat sd = new SimpleDateFormat("HH:mm:ss");
            date = sd.parse(this.getText());
        } catch (Exception ex) {
        }
        return date;
    }

    @Override
    public void appendText(String text) {
        // Ignore this. Our text is always 8 characters long, we cannot append anything
    }

    @Override
    public boolean deleteNextChar() {
        boolean success = false;

        // If there's a selection, delete it:
        final IndexRange selection = getSelection();
        if (selection.getLength()>0) {
            int selectionEnd = selection.getEnd();
            this.deleteText(selection);
            this.positionCaret(selectionEnd);
            success = true;
        } else {
            // If the caret preceeds a digit, replace that digit with a zero and move the caret forward. Else just move the caret forward.
            int caret = this.getCaretPosition();
            String currentText = this.getText();
            if (currentText.length() % 3 == 0) {
                if (caret == 1) {
                    setText(currentText.substring(caret));
                } else if (!currentText.substring(caret - 1, caret).equals(":")) {
                    setText(currentText.substring(0, caret - 1) + "0" + currentText.substring(caret));
                }
            } else if (caret % 3 != 2) {
                // not preceeding a colon
                setText(currentText.substring(0, caret) + "0" + currentText.substring(caret + 1));
                success = true;
            }
            this.positionCaret(Math.min(caret+1, this.getText().length()));
        }
        return success;
    }

    @Override
    public boolean deletePreviousChar() {
        boolean success = false;
        // If there's a selection, delete it:
        final IndexRange selection = getSelection();
        if (selection.getLength()>0) {
            int selectionStart = selection.getStart();
            this.deleteText(selection);
            this.positionCaret(selectionStart);
            success = true;
        } else {
            // If the caret is after a digit, replace that digit with a zero and move the caret backward. Else just move the caret back.
            int caret = this.getCaretPosition();
            String currentText = this.getText();
            if (currentText.length() % 3 == 0) {
                if (caret == 1) {
                    setText(currentText.substring(caret));
                } else if (!currentText.substring(caret - 1, caret).equals(":")) {
                    setText(currentText.substring(0, caret - 1) + "0" + currentText.substring(caret));
                }
            } else if (caret % 3 != 0) {
                // not following a colon
                setText(currentText.substring(0, caret-1) + "0" + currentText.substring(caret));
                success = true;
            }
            this.positionCaret(Math.max(caret-1, 0));
        }
        return success;
    }

    @Override
    public void deleteText(IndexRange range) {
        this.deleteText(range.getStart(), range.getEnd());
    }

    @Override
    public void deleteText(int begin, int end) {
        // Replace all digits in the given range with zero:
        StringBuilder builder = new StringBuilder(this.getText());
        if (builder.length() % 3 == 0) {
            for (int c = begin; c < end; c++) {
                if (c == 0 || !builder.substring(c, c + 1).equals(":")) {
                    builder.replace(c, c + 1, "0");
                }
            }
        } else {
            for (int c = begin; c < end; c++) {
                if (c % 3 != 2) {
                    // Not at a colon:
                    builder.replace(c, c + 1, "0");
                }
            }
        }
        this.setText(builder.toString());
    }

    @Override
    public void insertText(int index, String text) {
        // Handle an insert by replacing the range from index to index+text.length() with text, if that results in a valid string:
        StringBuilder builder = new StringBuilder(this.getText());
        if (Objects.nonNull(this.maxMillis) && index == 2 && builder.length() % 3 != 0) {
            builder.insert(index, text);
        } else {
            builder.replace(index, index + text.length(), text);
        }
        final String testText = builder.toString();
        if (validate(testText)) {
            this.setText(testText);
        }
        this.positionCaret(index + text.length());
    }

    @Override
    public void replaceSelection(String replacement) {
        final IndexRange selection = this.getSelection();
        if (0 == selection.getLength()) {
            this.insertText(selection.getStart(), replacement);
        } else {
            this.replaceText(selection.getStart(), selection.getEnd(), replacement);
        }
    }

    @Override
    public void replaceText(IndexRange range, String text) {
        this.replaceText(range.getStart(), range.getEnd(), text);
    }

    @Override
    public void replaceText(int begin, int end, String text) {
        if (begin == end) {
            this.insertText(begin, text);
        } else {
            // only handle this if text.length() is equal to the number of characters being replaced, and if the replacement results in a valid string:
            if (text.length() == end - begin) {
                StringBuilder builder = new StringBuilder(this.getText());
                builder.replace(begin, end, text);
                String testText = builder.toString();
                if (validate(testText)) {
                    this.setText(testText);
                }
                this.positionCaret(end);
            }
        }
    }

    private boolean validate(String time) {
        if (!timePattern.matcher(time).matches()) {
            return false;
        }
        String[] tokens = time.split(":");
        assert tokens.length == 3;
        try {
//            int h = Integer.parseInt(tokens[0]);
            int m = Integer.parseInt(tokens[1]);
            int s = Integer.parseInt(tokens[2]);
//            if (h < 0 || h > 23) {
//                return false;
//            }
            if (m < 0 || m > 59) {
                return false;
            }
            return !(s < 0 || s > 59);
        } catch (NumberFormatException nfe) {
            // regex matching should assure we never reach this catch block
            assert false;
            return false;
        }
    }

    private final class TimeUnitBinding extends IntegerBinding {
        final Unit unit;
        TimeUnitBinding(Unit unit) {
            this.bind(textProperty());
            this.unit = unit;
        }

        @Override
        protected int computeValue() {
            if (StringUtils.isEmpty(getText())) {
                return 0;
            }
            String token = getText().split(":")[unit.ordinal()];
            return Integer.parseInt(token);
        }
    }
}