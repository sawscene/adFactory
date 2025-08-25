/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

/**
 * ログイン認証種別
 *
 * @author HN)y-harada
 */
public enum LoginAuthTypeEnum {

    adFactory, // デフォルトの認証
    LDAP;      // LDAP認証
    
    /**
     * 文字列からEnum取得
     *
     * @param str 文字列
     * @return LoginAuthTypeEnum
     */
    public static LoginAuthTypeEnum getEnum(String str) {
        LoginAuthTypeEnum[] enumArray = LoginAuthTypeEnum.values();
        for (LoginAuthTypeEnum enumStr : enumArray) {
            if (str.equals(enumStr.toString())) {
                return enumStr;
            }
        }
        return getDefoult();
    }
    
    /**
     * デフォルト取得
     *
     * @return デフォルトのLoginAuthTypeEnum
     */
    private static LoginAuthTypeEnum getDefoult() {
        return adFactory;
    }
}
