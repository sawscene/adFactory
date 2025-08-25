/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 倉庫案内用ユーティリティー
 * 
 * @author s-heya
 */
public class WarehouseUtils {
    
    /**
     * 先行手配の発注番号を取得する。
     * 
     * @param arrangeNo 手配番号(例: SE0123456+001-005)
     * @return 
     */
    public static List<String> extractSupplyNo(String arrangeNo) {
        try {
            List<String> list = new ArrayList<>();
            
            String value[] = arrangeNo.split("\\+|\\-");
            if (value.length >= 3) {
                int start = Integer.parseInt(value[value.length - 2]);
                int end = Integer.parseInt(value[value.length - 1]);
                String format = value[0] + "+%03d";
                for (; start <= end; start++) {
                    list.add(String.format(format, start));
                }
            }
            
            return list;
        } catch (NumberFormatException e) {
            return Arrays.asList(arrangeNo);
        }
    }
}
