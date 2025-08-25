/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.utility.restclient;

import java.util.concurrent.Future;

/**
 * HTTPタスクのインターフェイス
 * 
 * @author s-heya
 */
public interface HttpTask {
    enum HttpMethod {
        GET,
        POST;
    }

    void setHttpClient(HttpClient<?> client);
    Future<Object> getRequest(String method,  Object...args) throws Exception;
    Future<Object> postRequest(String method,  Object param,  Class clazz, Object...args) throws Exception;
}
