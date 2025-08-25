/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;

/**
 * 理由種別
 *
 * @author
 */
@XmlEnum(Integer.class)
public enum ReasonTypeEnum {

    /**
     * 呼び出し理由
     */
    @XmlEnumValue("0") TYPE_CALL(0),
    /**
     * 中断理由
     */
    @XmlEnumValue("1") TYPE_INTERRUPT(1),
    /**
     * 遅延理由
     */
    @XmlEnumValue("2") TYPE_DELAY(2),
    /**
     * 不良理由
     */
    @XmlEnumValue("3") TYPE_DEFECT(3);

    private final int id;// 理由種別ID

    /**
     * コンストラクタ
     *
     * @param id 理由種別ID
     */
    private ReasonTypeEnum(int id) {
        this.id = id;
    }

    /**
     * 理由種別IDを取得する。
     *
     * @return 理由種別ID
     */
    public int getId() {
        return this.id;
    }
}
