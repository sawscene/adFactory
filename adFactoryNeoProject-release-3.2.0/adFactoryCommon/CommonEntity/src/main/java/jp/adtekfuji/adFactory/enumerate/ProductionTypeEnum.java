/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;

/**
 * 生産タイプ
 *
 * @author nar-nakamura
 */
@XmlEnum(Integer.class)
public enum ProductionTypeEnum {
    @XmlEnumValue("0") ONE_PIECE("key.OnePieceFlow"),
    @XmlEnumValue("1") LOT_ONE_PIECE("key.LotOnePiece"),
    @XmlEnumValue("2") LOT("key.LotProduction"),
    @XmlEnumValue("3") LOT_ONE_PIECE2("key.LotOnePiece2");

    private final String resourceKey;

    /**
     *
     * @param resourceKey
     */
    private ProductionTypeEnum(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     *
     * @return
     */
    public String getResourceKey() {
        return this.resourceKey;
    }
}
