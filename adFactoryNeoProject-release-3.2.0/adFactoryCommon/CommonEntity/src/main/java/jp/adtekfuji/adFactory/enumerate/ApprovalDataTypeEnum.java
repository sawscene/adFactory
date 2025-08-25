/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;

/**
 * 承認データ種別
 *
 * @author nar-nakamura
 */
@XmlEnum(Integer.class)
public enum ApprovalDataTypeEnum {

    /**
     * 工程
     */
    @XmlEnumValue("0") WORK(0),
    /**
     * 工程順
     */
    @XmlEnumValue("1") WORKFLOW(1),
    /**
     * カンバン
     */
    @XmlEnumValue("2") KANBAN(2);

    /**
     * データ種別ID
     */
    private final int id;

    /**
     * コンストラクタ
     *
     * @param id データ種別ID
     */
    private ApprovalDataTypeEnum(int id) {
        this.id = id;
    }

    /**
     * データ種別IDを取得する。
     *
     * @return データ種別ID
     */
    public int getId() {
        return this.id;
    }
}
