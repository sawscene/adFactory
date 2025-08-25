/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.entity.accessfuji;

/**
 * 階層種別
 *
 * @author j.min
 */
public enum AccessHierarchyFujiTypeEnum {
    UnitHierarchy(0),
    UnitTemplateHierarchy(1);

    private final int value;

    private AccessHierarchyFujiTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
