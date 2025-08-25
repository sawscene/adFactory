/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

import adtekfuji.utility.StringUtils;

/**
 * 調達方法
 *
 * @author s-heya
 */
public enum PurchaseTypeEnum {
    SELF("01"),       // 自社調達
    SUPPLIED("02");   // 支給

    private final String code;

    /**
     * コンストラクタ
     *
     * @param code
     */
    PurchaseTypeEnum(String code) {
        this.code = code;
    }

    /**
     * 識別コードを取得する。
     *
     * @return
     */
    public String getCode() {
        return this.code;
    }

    /**
     * 調達方法を返す。
     *
     * @param contorl
     * @return
     */
    public static PurchaseTypeEnum toPurchaseType(String contorl) {
        PurchaseTypeEnum purchaseType = PurchaseTypeEnum.SELF;
        if (!StringUtils.isEmpty(contorl) && contorl.length() >= 2) {
            if (PurchaseTypeEnum.SUPPLIED.getCode().equals(contorl.substring(0, 2))) {
                purchaseType = PurchaseTypeEnum.SUPPLIED;
            }
        }
        return purchaseType;
    }
}
