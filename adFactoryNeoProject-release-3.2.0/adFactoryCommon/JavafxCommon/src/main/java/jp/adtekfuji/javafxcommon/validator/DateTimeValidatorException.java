/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.validator;

/**
 * 日付確認処理例外クラス
 *
 * @author e.mori
 * @version 1.6.1
 * @since 2017.1.11.Wen
 */
public class DateTimeValidatorException extends Exception {

    private final String input;

    public DateTimeValidatorException(String input) {
        super("DateTime validation exception. (input:" + input + ")");
        this.input = input;
    }

    public String getInput() {
        return input;
    }

}
