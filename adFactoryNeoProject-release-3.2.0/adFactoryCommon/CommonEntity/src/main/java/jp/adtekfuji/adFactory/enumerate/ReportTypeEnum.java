/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;

/**
 * 帳票種別
 *
 * @author nar-nakamura
 */
@XmlEnum(Integer.class)
public enum ReportTypeEnum {

    /**
     * カンバン帳票
     */
    @XmlEnumValue("0") KANBAN_REPORT(0),
    /**
     * まとめて帳票
     */
    @XmlEnumValue("1") MULTIPLE_REPORT(1);

    private final int id;// 帳票種別ID

    /**
     * コンストラクタ
     *
     * @param id 帳票種別ID
     */
    private ReportTypeEnum(int id) {
        this.id = id;
    }

    /**
     * 帳票種別IDを取得する。
     *
     * @return 帳票種別ID
     */
    public int getId() {
        return this.id;
    }
}
