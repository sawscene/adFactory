/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

/**
 * 間隔を表す単位
 *
 * @author fu-kato
 */
public enum TermUnitEnum {
    YEAR("key.Year"),
    MONTHLY("key.Month"),
    WEEKLY("key.Week"),
    DAYLY("key.Day");

    final private String value;

    private TermUnitEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static TermUnitEnum fromString(String value) {
        TermUnitEnum[] enumArray = TermUnitEnum.values();
        for (TermUnitEnum enumStr : enumArray) {
            if (value.equals(enumStr.toString())) {
                return enumStr;
            }
        }
        return null;
    }
}
