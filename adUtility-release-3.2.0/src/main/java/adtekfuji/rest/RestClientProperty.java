/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.rest;

import jakarta.ws.rs.core.MediaType;


/**
 *
 * @author ke.yokoi
 */
public class RestClientProperty {

    private String uriBase = "";
    private Integer connectTimeout = 30 * 1000;// 接続タイムアウト(ms)
    private Integer readTimeout = 60 * 1000;// 読込タイムアウト(ms)
    private MediaType mediaType = MediaType.APPLICATION_XML_TYPE;
    private String charset = "Shift_JIS";
    private Boolean encryptConnection = false;

    /**
     * 
     */
    public RestClientProperty() {
    }

    /**
     * 
     * @param uriBase 
     */
    public RestClientProperty(String uriBase) {
        if (uriBase.startsWith("https")) {
            this.encryptConnection = true;
        }
        this.uriBase = uriBase;
    }

    /**
     * 
     * @param uriBase 
     * @param connectTimeout 
     * @param readTimeout 
     */
    public RestClientProperty(String uriBase, int connectTimeout, int readTimeout) {
        if (uriBase.startsWith("https")) {
            this.encryptConnection = true;
        }
        this.uriBase = uriBase;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    /**
     * 
     * @return 
     */
    public String getUriBase() {
        return uriBase;
    }

    /**
     * 
     * @param uriBase 
     */
    public void setUriBase(String uriBase) {
        if (uriBase.startsWith("https")) {
            this.encryptConnection = true;
        } else {
            this.encryptConnection = false;
        }
        this.uriBase = uriBase;
    }

    /**
     * タイムアウト(ms)を取得する。(読込タイムアウト)
     *
     * @return タイムアウト(ms)
     */
    public Integer getTimeout() {
        return this.readTimeout;
    }

    /**
     * タイムアウト(ms)を設定する。(接続・読込タイムアウトに同じ値をセット)
     *
     * @param timeout タイムアウト(ms)
     */
    public void setTimeout(Integer timeout) {
        this.connectTimeout = timeout;
        this.readTimeout = timeout;
    }

    /**
     * 接続タイムアウト(ms)を取得する。
     *
     * @return 接続タイムアウト(ms)
     */
    public Integer getConnectTimeout() {
        return this.connectTimeout;
    }

    /**
     * 接続タイムアウト(ms)を設定する。
     *
     * @param connectTimeout 接続タイムアウト(ms)
     */
    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * 読込タイムアウト(ms)を取得する。
     *
     * @return 読込タイムアウト(ms)
     */
    public Integer getReadTimeout() {
        return this.readTimeout;
    }

    /**
     * 読込タイムアウト(ms)を設定する。
     *
     * @param readTimeout 読込タイムアウト(ms)
     */
    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    /**
     * 
     * @return 
     */
    public MediaType getMediaType() {
        return this.mediaType;
    }

    /**
     * 
     * @param mediaType 
     */
    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    /**
     * 
     * @return 
     */
    public String getCharset() {
        return this.charset;
    }

    /**
     * 
     * @param charset 
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * 
     * @return 
     */
    public Boolean isEncryptConnection() {
        return this.encryptConnection;
    }


}
