/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.enumeration;

/**
 * Verifier 列挙子
 *
 * @author s-heya
 */
public enum Verifier {
    DEFAULT(""),
    NATURAL_ONLY("^[0-9]+$"),                                           // 0を含む自然数
    NUMBER_ONLY("([-]?)|(^[-]?[0-9]+$)"),                               // 半角数値
    DECIMAL_NUMBER_ONLY("([-]?)|(^[-]?([1-9]\\d*|0)(\\.\\d*)?$)"),      // 半角小数
    ALPHABET_ONLY("^[a-zA-z\\s]+$"),                                    // 半角アルファベット
    CHARACTER_ONLY("^[a-zA-Z0-9!-/:-@\\[-`{-~]+$"),                     // 半角英数字記号
    NOT_BLANK("[^\\s]+");                                               // 空白禁止

    private final String regex;

    private Verifier(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return this.regex;
    }
}
