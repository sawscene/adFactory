/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.controls;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Objects;
import java.util.regex.Pattern;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Point2D;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import jp.adtekfuji.javafxcommon.enumeration.Verifier;
import jp.adtekfuji.platform.enumerate.InputScope;
import jp.adtekfuji.platform.windows.PlatformService;
import org.apache.commons.lang3.StringUtils;

/**
 * 入力文字数制限付き TextField
 *      TODO: 半角のみ対応。IME入力時は正常に動作しない。
 * @author nar-nakamura
 */
public class RestrictedTextField extends javafx.scene.control.TextField {

    private IntegerProperty maxLength;
    private DoubleProperty minLimit;
    private DoubleProperty maxLimit;
    private StringProperty format;
    private ObjectProperty<Verifier> verifier;
    private Pattern pattern;
    private Tooltip tooltip;
    private String oldText;

    private StringProperty textFormat;

    /**
     * コンストラクタ
     */
    public RestrictedTextField() {
        this.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!(Verifier.NOT_BLANK == this.getVerifier() || Verifier.DEFAULT == this.getVerifier())) {
                if (newValue) {
                    if (!StringUtils.isEmpty(this.getTextFormat()) && !StringUtils.isEmpty(this.getText())) {
                        Pattern _pattern = Pattern.compile(this.getTextFormat());
                        if (!_pattern.matcher(this.getText()).matches()) {
                            this.showValidator("入力形式が異なります");
                            //this.setText(oldText);
                            this.requestFocus();
                            return;
                        }
                    }
                    PlatformService.SetInputScope(InputScope.ALPHANUMERIC_HALF);
                } else {
                    if (Verifier.NUMBER_ONLY == this.getVerifier() || Verifier.DECIMAL_NUMBER_ONLY == this.getVerifier() || Verifier.NATURAL_ONLY == this.getVerifier()) {
                        String text = this.getText();
                        if (StringUtils.isEmpty(text)) {
                            text = "0";
                        }
                        
                        double value = Double.parseDouble(text);
                        if (value >= this.getMinLimit() && value <= this.getMaxLimit()) {
                            this.hideValidator();
                            PlatformService.SetInputScope(InputScope.DEFAULT);
                        } else {
                            Formatter formatter = new Formatter();
                            formatter.format(String.format("入力範囲: %s ～ %s", this.getFormat(), this.getFormat()), this.getMinLimit(), this.getMaxLimit());
                            this.showValidator(formatter.toString());
                            this.setText(oldText);
                            this.requestFocus();
                        }

                    } else if (!StringUtils.isEmpty(this.getTextFormat())) {
                        String text = this.getText();
                        if (!StringUtils.isEmpty(text)) {
                            Pattern _pattern = Pattern.compile(this.getTextFormat());
                            if (!_pattern.matcher(text).matches()) {
                                this.showValidator("入力形式が異なります");
                                //this.setText(oldText);
                                this.requestFocus();
                                return;
                            }
                        }
                        PlatformService.SetInputScope(InputScope.DEFAULT);
                        
                    } else {
                        PlatformService.SetInputScope(InputScope.DEFAULT);
                    }
                }
            }
        });
        
        this.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (KeyCode.ENTER == event.getCode() && Verifier.DEFAULT == getVerifier()) {
                int scope = PlatformService.GetInputScope();
                if (0 < scope) {
                    byte[] bytes = RestrictedTextField.getBytes(getText());
                    if (bytes.length > getMaxLength()) {
                        String str = new String(Arrays.copyOfRange(bytes, 0, getMaxLength()));
                        this.setText(str);
                        this.positionCaret(str.length());

                        Formatter formatter = new Formatter();
                        formatter.format("最大文字数(半角): %d", getMaxLength());
                        this.showValidator(formatter.toString());
                    } else {
                        this.hideValidator();
                    }
                }
            }
        });
    }

    /**
     * コンストラクタ
     *
     * @param text
     */
    public RestrictedTextField(String text) {
       this();
       setText(text);
    }

    public void setMaxLength(int value) {
        this.maxLengthProperty().set(value);
    }

    public int getMaxLength() {
        return this.maxLengthProperty().get();
    }

    public IntegerProperty maxLengthProperty() {
        if (this.maxLength == null) this.maxLength = new SimpleIntegerProperty(this, "Maximum Length", 1024);
        return this.maxLength;
    }

    public void setMinLimit(double value) {
        this.minLimitProperty().set(value);
    }

    public double getMinLimit() {
        return this.minLimitProperty().get();
    }

    public DoubleProperty minLimitProperty() {
        if (this.minLimit == null) this.minLimit = new SimpleDoubleProperty(this, "Minimum Limit", 0);
        return this.minLimit;
    }

    public void setMaxLimit(double value) {
        this.maxLimitProperty().set(value);
    }

    public double getMaxLimit() {
        return this.maxLimitProperty().get();
    }

    public DoubleProperty maxLimitProperty() {
        if (this.maxLimit == null) this.maxLimit = new SimpleDoubleProperty(this, "Maximum Limit", 999);
        return this.maxLimit;
    }

    public void setFormat(String value) {
        this.formatProperty().set(value);
    }

    public String getFormat() {
        return this.formatProperty().get();
    }

    public StringProperty formatProperty() {
        if (this.format == null) this.format = new SimpleStringProperty(this, "Format", "%.0f");
        return this.format;
    }

    public void setVerifier(Verifier value) {

        this.verifierProperty().setValue(value);
    }
    public Verifier getVerifier() {
        return this.verifierProperty().getValue();
    }

    public String getOldText() {
        return oldText;
    }

    public ObjectProperty<Verifier> verifierProperty() {
        if (this.verifier == null) this.verifier = new SimpleObjectProperty<Verifier>(this, "Verifier", Verifier.DEFAULT);
        return this.verifier;
    }

    public void setTextFormat(String value) {
        this.textFormat().set(value);
    }

    public String getTextFormat() {
        return this.textFormat().get();
    }

    public StringProperty textFormat() {
        if (this.textFormat == null) this.textFormat = new SimpleStringProperty(this, "TextFormat", "");
        return this.textFormat;
    }

    /**
     * フォーカスを取得する。
     */
    @Override
    public void requestFocus() {
        this.oldText = this.getText();
        if (Objects.isNull(this.pattern)) {
            this.pattern = Pattern.compile(this.getVerifier().getRegex());
        }
        super.requestFocus();
    }

    /**
     * テキストを置き換える。
     *
     * @param start
     * @param end
     * @param text
     */
    @Override
    public void replaceText(int start, int end, String text) {
        if (StringUtils.isEmpty(text)) {
            if (this.verify(text)) {
                super.replaceText(start, end, text);
            }
        } else {
            StringBuilder sb = new StringBuilder();
            if (StringUtils.isNotEmpty(this.getText())) {
                sb.append(this.getText());
            }
            sb.insert(start, text);
            if (this.verify(sb.toString())) {
                super.replaceText(start, end, text);
            }
        }
    }

    /**
     * テキストを置き換える。
     *
     * @param text
     */
    @Override
    public void replaceSelection(String text) {
        if (this.verify(text)) {
            super.replaceSelection(text);
        }
    }

    /**
     * 入力文字を検証する。
     *
     * @param text
     * @return
     */
    private boolean verify(String text) {
        //System.out.println("Text: " + text);

        boolean result = false;
        switch (this.getVerifier()) {
            case NATURAL_ONLY:
            case NUMBER_ONLY:
            case DECIMAL_NUMBER_ONLY:
                if (StringUtils.isEmpty(text)) {
                    result = true;
                    this.hideValidator();
                } else {
                    if (this.pattern.matcher(text).matches()) {
                        result = true;
                        this.hideValidator();
                    }
                }
                break;

            case ALPHABET_ONLY:
            case CHARACTER_ONLY:
                if (StringUtils.isEmpty(text)) {
                    result = true;
                    this.hideValidator();
                } else {
                    if (this.getMaxLength() > 0 && text.length() <= this.getMaxLength()) {
                        if (this.pattern.matcher(text).matches()) {
                            result = true;
                            this.hideValidator();
                        }
                    } else {
                        Formatter formatter = new Formatter();
                        formatter.format("最大文字数(半角): %d", this.getMaxLength());
                        this.showValidator(formatter.toString());
                    }
                }
                break;

            case NOT_BLANK:
                if (this.getMaxLength() > 0 && RestrictedTextField.getBytes(text).length <= this.getMaxLength()) {
                    if (this.pattern.matcher(text).matches()) {
                        result = true;
                        this.hideValidator();
                    }
                } else {
                    Formatter formatter = new Formatter();
                    formatter.format("最大文字数(半角): %d", this.getMaxLength());
                    this.showValidator(formatter.toString());
                }
                break;

            default:
                int scope = PlatformService.GetInputScope();
                if (0 == scope) {
                    if (this.getMaxLength() > 0 && RestrictedTextField.getBytes(text).length <= this.getMaxLength()) {
                        result = true;
                        this.hideValidator();
                    } else {
                        Formatter formatter = new Formatter();
                        formatter.format("最大文字数(半角): %d", this.getMaxLength());
                        this.showValidator(formatter.toString());
                    }
                } else {
                    result = true;
                }
                break;
        }
        return result;
    }

    /**
	 * 文字列をバイトに変換する。
	 *
	 * @param text 処理対象となる文字列
	 * @return 文字列のバイト数
	 */
	public static byte[] getBytes(String text) {
	    if (StringUtils.isEmpty(text)) {
	        return new byte[0];
        }

	    byte[] bytes = null;
	    try {
	        bytes = text.getBytes("Shift_JIS");
	    } catch (UnsupportedEncodingException e) {
	        bytes = new byte[0];
	    }
	    return bytes;
    }

    /**
     * ヒントを表示する。
     */
    private void showValidator(String text) {
        if (Objects.isNull(this.getScene())) {
            return;
        }

        this.hideValidator();

        this.tooltip = new Tooltip();
        this.tooltip.setText(text);
        this.tooltip.setAutoHide(true);
        this.tooltip.setHideOnEscape(true);
        this.tooltip.setStyle("-fx-font-size: 10pt; -fx-text-fill: black; -fx-background-color: #F8F0D7; -fx-background-radius: 0 0 0 0;");

        Point2D coordinates = this.localToScene(0.0, 0.0);
        this.tooltip.show(this, coordinates.getX() + this.getScene().getX() + this.getScene().getWindow().getX(),
                coordinates.getY() + this.getScene().getY() + this.getScene().getWindow().getY() + this.getHeight() + 2.0);
    }

    /**
     * ヒントを隠す。
     */
    private void hideValidator() {
        if (Objects.nonNull(this.tooltip)) {
            if (this.tooltip.isShowing()) {
                this.tooltip.hide();
            }
        }
    }
}
