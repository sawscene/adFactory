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
public class NumericValidatorException extends Exception {

    private final String input;
    private final int maxDegit;
    private final String limitRanges;

    public NumericValidatorException(String input, int maxDegit, String limitRanges) {
        super("Numeric validation exception. (input:" + input + ", max degit:" + maxDegit + ", ranges:" + limitRanges + ")");
        this.input = input;
        this.maxDegit = maxDegit;
        this.limitRanges = limitRanges;
    }

    public String getInput() {
        return input;
    }

    public int getMaxDegit() {
        return maxDegit;
    }

    public String getLimitRanges() {
        return limitRanges;
    }

}
