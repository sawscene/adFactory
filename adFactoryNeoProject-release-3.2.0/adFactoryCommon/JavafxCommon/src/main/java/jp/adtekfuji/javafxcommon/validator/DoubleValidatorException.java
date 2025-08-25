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
public class DoubleValidatorException extends Exception {

    private final String input;
    private final int scale;
    private final String limitRanges;

    public DoubleValidatorException(String input, int scale, String limitRanges) {
        super("Double validation exception. (input:" + input + ", scale:" + scale + ", ranges:" + limitRanges + ")");
        this.input = input;
        this.scale = scale;
        this.limitRanges = limitRanges;
    }

    public String getInput() {
        return input;
    }

    public int getScale() {
        return scale;
    }

    public String getLimitRanges() {
        return limitRanges;
    }

}
