/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;

/**
 * 実績数のカウント方法
 *
 * @author nar-nakamura
 */
@XmlEnum(Integer.class)
public enum CompCountTypeEnum {
    @XmlEnumValue("0") KANBAN("key.CompCountType.Kanban"),
    @XmlEnumValue("1") EQUIPMENT("key.CompCountType.Equipment"),
    @XmlEnumValue("2") WORK("key.CompCountType.Work");

    private final String resourceKey;

    private CompCountTypeEnum(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public String getResourceKey() {
        return this.resourceKey;
    }
}
