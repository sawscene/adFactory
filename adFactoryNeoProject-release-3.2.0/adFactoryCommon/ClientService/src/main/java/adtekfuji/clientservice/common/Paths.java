/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.clientservice.common;

import java.io.File;

/**
 * 既定パス
 */
public class Paths {
    // ホーム
    public static final String ADFACTORY_HOME = System.getenv("ADFACTORY_HOME");
    // サーバー アップロード 電子マニュアル
    public static final String SERVER_UPLOAD_PDOC = "/data/pdoc";
    // サーバー ダウンロード 電子マニュアル
    public static final String SERVER_DOWNLOAD_PDOC = "/adFactoryServer/data/pdoc";
    // サーバー アップロード 帳票
    public static final String SERVER_UPLOAD_REPORT = "/data/report";
    // サーバー ダウンロード
    public static final String SERVER_DOWNLOAD = "/adFactoryServer";
    // クライアント キャッシュ
    public static final String CLIENT_CACHE = ADFACTORY_HOME + File.separator + "client" + File.separator + "cache";
    // クライアント キャッシュ 電子マニュアル
    public static final String CLIENT_CACHE_PDOC = CLIENT_CACHE + File.separator + "pdoc";
    // クライアント キャッシュ 帳票
    public static final String CLIENT_CACHE_REPORT = CLIENT_CACHE + File.separator + "report";

    public static final String QUERY_PATH = "?";
    public static final String AND_PATH = "&";
    public static final String SEPARATOR_PATH = "/";
    public static final String COUNT_PATH = "/count";
    public static final String RANGE_PATH = "/range";
    public static final String FROM_TO_PATH = "from=%d&to=%d";
    public static final String AUTHID_PATH = "authId=%s";
}
