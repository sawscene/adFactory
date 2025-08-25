/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.component;

import adtekfuji.utility.StringUtils;

/**
 * データを解析するクラス
 * 
 * @author s-heya
 */
public class DataParser {
    
    /**
     * 生産タイプを解析する。
     * 
     * @param value
     * @return
     * @throws Exception 
     */
    public static int parseProductionType(String value) throws Exception {
        int productionType = 0;
        if (!StringUtils.isEmpty(value)) {
            productionType = Integer.parseInt(value);
            if (productionType < 0 || productionType > 2) {
                throw new IllegalArgumentException("productionType");
            }
        }
        return productionType;
    }

    /**
     * ロット数量を解析する。
     * 
     * @param value
     * @return
     * @throws Exception 
     */
    public static int parseLotNum(String value) throws Exception {
        int lotNum = 1;
        if (!StringUtils.isEmpty(value)) {
            lotNum = Integer.parseInt(value);
            if (lotNum < 1 || lotNum > 99999) {
                throw new IllegalArgumentException("lotNum");
            }
        }
        return lotNum;
    }
}
