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
public class RegexpValidatorException extends Exception {

    private final String input;
    private final String regexp;
    private final int maxChars;

    public RegexpValidatorException(String input, String regexp, int maxChars) {
        super("Regexp validation exception. (input:" + input + ", regexp:" + regexp + ", max chars:" + maxChars + ")");
        this.input = input;
        this.regexp = regexp;
        this.maxChars = maxChars;
    }

    public String getInput() {
        return input;
    }

    public String getRegexp() {
        return regexp;
    }

    public int getMaxChars() {
        return maxChars;
    }

}
