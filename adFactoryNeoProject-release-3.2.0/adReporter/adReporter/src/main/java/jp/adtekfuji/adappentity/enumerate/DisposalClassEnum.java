/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adappentity.enumerate;

import org.apache.commons.lang3.StringUtils;

/**
 * 処理区分
 *
 * @author s-heya
 */
public enum DisposalClassEnum {

    A,
    B,
    C,
    D;

    /**
     * 名前からオブジェクトに変換する
     *
     * @param value
     * @return
     */
    public static DisposalClassEnum toEnum(String value) {
        if (StringUtils.isEmpty(value)) {
            return DisposalClassEnum.A;
        }
        return DisposalClassEnum.valueOf(value);
    }
}
