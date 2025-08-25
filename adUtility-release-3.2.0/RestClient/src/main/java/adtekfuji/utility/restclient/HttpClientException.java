/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.utility.restclient;

/**
 * HTPPクライアントの例外クラス
 * 
 * @author s-heya
 */
public class HttpClientException extends Exception {

    private final int responseCode;
    
    /**
     * コンストラクタ
     * 
     * @param responseCode
     * @param responseMessage 
     */
    public HttpClientException(int responseCode, String responseMessage) {
        super(responseMessage);
        this.responseCode = responseCode;
    }
    
    /**
     * HTTP状態コードを取得する。
     * 
     * @return 
     */
    public int getResponseCode() {
        return this.responseCode;
    }
    
}
