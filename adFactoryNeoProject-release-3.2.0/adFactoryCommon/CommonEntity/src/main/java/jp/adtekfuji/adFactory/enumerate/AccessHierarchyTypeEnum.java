/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

/**
 * 階層種別
 *
 * @author j.min
 */
public enum AccessHierarchyTypeEnum {
    OrganizationHierarchy(0),
    EquipmentHierarchy(1),
    WorkHierarchy(2),
    WorkflowHierarchy(3),
    KanbanHierarchy(4);

    private final int value;

    private AccessHierarchyTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
