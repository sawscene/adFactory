/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.validator;

/**
 *
 * @author ke.yokoi
 */
public class LocalTimeValidatorException extends Exception {

    private final String input;

    public LocalTimeValidatorException(String input) {
        super("LocalTime validation exception. (input:" + input + ")");
        this.input = input;
    }

    public String getInput() {
        return input;
    }

}
