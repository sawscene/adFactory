/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.utility;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Objects;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * クッキーユーティリティー
 * 
 * @author
 */
public class CookieUtils {

    /**
     * クッキーを取得する。
     *
     * @param req HttpServletRequest
     * @param key
     * @return クッキー
     */
    public static Cookie getCookie(HttpServletRequest req, String key) {
        Cookie cookie[] = req.getCookies();
        if (Objects.nonNull(cookie)) {
            for (int i = 0; i < cookie.length; i++) {
                if (cookie[i].getName().equals(key)) {
                    return cookie[i];
                }
            }
        }
        return null;
    }

    /**
     * クッキーに保存された設定値を取得する
     *
     * @param key 設定キー
     * @return 設定値
     */
    public static String getCookieValue(String key) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletRequest req = (HttpServletRequest) externalContext.getRequest();

        Cookie cookie = getCookie(req, key);
        String value = null;

        if (Objects.nonNull(cookie)) {
            value = cookie.getValue();
            try {
                value = URLDecoder.decode(value, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return null;
            }
        }
        return value;
    }

}
