/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

/**
 *
 * @author SashinRanjitkar
 */
public enum MenuTypeEnum {
    TREE("tree"),
    DEFAULT("default");
    
    private final String value;

    MenuTypeEnum(String value) {
        this.value = value;
    }

    /** Returns the stable machine value ("tree" / "default").
     * @return  
     */
    public String getValue() {
        return value;
    }

    /**
     * Parse a string to MenuTypeEnum, fallback to DEFAULT.
     * Accepts:
     * @param value ("tree", "default")
     * @return 
     *
     */
    public static MenuTypeEnum from(String value) {
        if (value == null) {
            return DEFAULT;
        }
        for (MenuTypeEnum t : values()) {
            if (t.value.equalsIgnoreCase(value) || t.name().equalsIgnoreCase(value)) {
                return t;
            }
        }
        return DEFAULT;
    }
}
