/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import jakarta.enterprise.context.SessionScoped;

/**
 * ユーザー認証情報
 * 
 * @author s-heya
 */
@SessionScoped
public class UserAuth implements Serializable {
    
    /**
     * ユーザーID
     */
    private String userId;
    
    /**
     * 認証済みかどうか
     */
    private boolean authenticated;

    /**
     * 要求元ページのURI
     */
    private String requestURI;
    
    /**
     * パラメータマップ
     */
    private final Map<String, Object> parameterMap = new HashMap<>();
    
    /**
     * コンストラクタ
     */
    public UserAuth() {
    }    
    
    /**
     * コンストラクタ
     * 
     * @param userId ユーザーID 
     * @param requestURI 要求元ページのURI
     */
    public UserAuth(String userId, String requestURI) {
        this.userId = userId;
        this.requestURI = requestURI;
    }

    /**
     * ユーザーIDを取得する。
     * 
     * @return ユーザーID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * ユーザーIDを設定する。
     * 
     * @param userId ユーザーID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * ユーザーが認証済みかどうかを返す。
     * 
     * @return true: 認証済み、false: 未認証
     */
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * ユーザーが認証済みかどうかを設定する。
     * 
     * @param authenticated true: 認証済み、false: 未認証
     */
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    /**
     * 要求元ページのURIを取得する。
     * 
     * @return 要求元ページのURI
     */
    public String getRequestURI() {
        return requestURI;
    }

    /**
     * 要求元ページのURIを設定する。
     * 
     * @param requestURI 要求元ページのURI
     */
    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    /**
     * パラメータマップを取得する。
     * 
     * @return パラメータマップ
     */
    public Map<String, Object> getParameterMap() {
        return parameterMap;
    }
    
    
}
