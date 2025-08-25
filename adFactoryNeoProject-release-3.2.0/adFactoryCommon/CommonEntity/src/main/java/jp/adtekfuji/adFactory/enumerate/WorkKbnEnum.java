/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;

/**
 * 工程区分
 *
 * @author nar-nakamura
 */
@XmlEnum(Integer.class)
public enum WorkKbnEnum {

    /**
     * 通常工程
     */
    @XmlEnumValue("0") BASE_WORK(0),
    /**
     * 追加工程
     */
    @XmlEnumValue("1") ADDITIONAL_WORK(1);

    private final int id;// 工程区分ID

    /**
     * コンストラクタ
     *
     * @param id 工程区分ID
     */
    private WorkKbnEnum(int id) {
        this.id = id;
    }

    /**
     * 工程区分IDを取得する。
     *
     * @return 工程区分ID
     */
    public int getId() {
        return this.id;
    }
}
