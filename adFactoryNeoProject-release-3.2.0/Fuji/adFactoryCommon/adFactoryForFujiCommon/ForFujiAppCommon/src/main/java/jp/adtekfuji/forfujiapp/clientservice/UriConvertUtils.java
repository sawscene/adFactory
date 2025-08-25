/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.clientservice;

import org.apache.logging.log4j.LogManager;

/**
 * URIからIDを取得するためのユーティリティ
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.27.Tha
 */
public class UriConvertUtils {

    private final static String URI_SPLIT = "/";

    /**
     * 階層用変換(スプリットが3
     *
     * @param uri Uri
     * @return Id
     */
    public static long getUriToId(String uri) {
        long ret = 0;
        try {
            String[] split = uri.split(URI_SPLIT);
            if (!(split.length == 0)) {
                ret = Long.parseLong(split[split.length - 1]);
            }
        } catch (Exception ex) {
            LogManager.getLogger().fatal(ex, ex);
        }
        return ret;
    }
}
