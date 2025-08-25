/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.utility.restclient;

/**
 * HTTPクライアントのインターフェイス
 * 
 * @param  <T> 受信データのエンティティクラス
 * @author s-heya
 */
public interface HttpClient<T> {
        <T> T getRequest(String method,  Object...args) throws Exception, HttpClientException;
        <T> T postRequest(String method, Object reqObject,  Class clazz, Object...args) throws Exception, HttpClientException;
        void setServerURI(String serverUri);
        void setEnableSSL(Boolean isEnable);
        void setConnctTimeout(int msec);
}
