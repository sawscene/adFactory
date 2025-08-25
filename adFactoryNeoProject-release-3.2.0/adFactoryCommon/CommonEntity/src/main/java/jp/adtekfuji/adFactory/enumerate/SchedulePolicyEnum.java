/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

/**
 * スケジュールポリシー列挙
 *
 * @author s-heya
 */
public enum SchedulePolicyEnum {
    PriorityParallel(0, "key.PriorityParallel"),
    PrioritySerial(1, "key.PrioritySerial");

    private final int value;
    private final String resourceKey;

    private SchedulePolicyEnum(int value, String resourceKey) {
        this.value = value;
        this.resourceKey = resourceKey;
    }

    public int getValue() {
        return value;
    }

    public String getResourceKey() {
        return this.resourceKey;
    }
}
