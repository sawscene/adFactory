/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;

/**
 * 階層種別
 *
 * @author nar-nakamura
 */
@XmlEnum(Integer.class)
public enum HierarchyTypeEnum {

    /**
     * 工程
     */
    @XmlEnumValue("0") WORK(0),
    /**
     * 工程順
     */
    @XmlEnumValue("1") WORKFLOW(1);

    private final int id;// 階層種別ID

    /**
     * コンストラクタ
     *
     * @param id 階層種別ID
     */
    private HierarchyTypeEnum(int id) {
        this.id = id;
    }

    /**
     * 階層種別IDを取得する。
     *
     * @return 階層種別ID
     */
    public int getId() {
        return this.id;
    }
}
