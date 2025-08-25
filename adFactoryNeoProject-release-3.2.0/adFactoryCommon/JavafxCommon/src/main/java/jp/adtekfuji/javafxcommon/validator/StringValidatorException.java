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
public class StringValidatorException extends Exception {

    private final String input;
    private final int maxChars;

    public StringValidatorException(String input, int maxChars) {
        super("String validation exception. (input:" + input + ", max chars:" + maxChars + ")");
        this.input = input;
        this.maxChars = maxChars;
    }

    public String getInput() {
        return input;
    }

    public int getMaxChars() {
        return maxChars;
    }

}
