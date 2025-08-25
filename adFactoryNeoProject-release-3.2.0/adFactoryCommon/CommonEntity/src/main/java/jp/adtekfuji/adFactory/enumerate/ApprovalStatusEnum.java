/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;

/**
 * 承認状態
 *
 * @author nar-nakamura
 */
@XmlEnum(Integer.class)
public enum ApprovalStatusEnum {

    /**
     * 未承認
     */
    @XmlEnumValue("0") UNAPPROVED(0),
    /**
     * 申請中
     */
    @XmlEnumValue("1") APPLY(1),
    /**
     * 取消
     */
    @XmlEnumValue("2") CANCEL_APPLY(2),
    /**
     * 却下
     */
    @XmlEnumValue("3") REJECT(3),
    /**
     * 最終承認済
     */
    @XmlEnumValue("4") FINAL_APPROVE(4),
    /**
     * 承認済(承認フローで使用)
     */
    @XmlEnumValue("5") APPROVE(5);

    /**
     * 承認状態ID
     */
    private final int id;

    /**
     * コンストラクタ
     *
     * @param id 承認状態ID
     */
    private ApprovalStatusEnum(int id) {
        this.id = id;
    }

    /**
     * 承認状態IDを取得する。
     *
     * @return 承認状態ID
     */
    public int getId() {
        return this.id;
    }
}
