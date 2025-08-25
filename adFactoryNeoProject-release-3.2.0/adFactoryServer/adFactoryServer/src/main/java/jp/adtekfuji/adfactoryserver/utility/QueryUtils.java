/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.utility;

import adtekfuji.utility.StringUtils;

/**
 * クエリーユーティリティークラス
 * 
 * @author s-heya
 */
public class QueryUtils {
    
    /**
     * LIKE文用の値の文字列を取得する。
     *
     * @param value
     * @return
     */
    public static String getLikeValue(String value) {
        return new StringBuilder("%").append(StringUtils.toLowerCase(value)).append("%").toString();
    }
}
