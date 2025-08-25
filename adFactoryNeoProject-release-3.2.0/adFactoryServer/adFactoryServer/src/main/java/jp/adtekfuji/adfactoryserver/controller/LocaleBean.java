/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.controller;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

/**
 * ロケール
 * 
 * @author s-heya
 */
public class LocaleBean implements Serializable {

    private String language;
    private Locale locale;
    
    public LocaleBean() {
    }
    
    /**
     * コンストラクタ
     * 
     * @param language 言語
     * @param locale ロケール
     */
    public LocaleBean(String language, Locale locale) {
        this.language = language;
        this.locale = locale;
    }

    /**
     * 言語を取得する。
     * 
     * @return 言語
     */
    public String getLanguage() {
        return language;
    }

    /**
     * ロケールを取得する。
     * 
     * @return ロケール
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * ハッシュコードを返す。
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.locale);
        return hash;
    }

    /**
     * オブジェクトを比較する。
     * 
     * @param obj オブジェクト
     * @return 
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LocaleBean other = (LocaleBean) obj;
        return Objects.equals(this.locale, other.locale);
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return "LocaleBean{" + "language=" + language + ", locale=" + locale + '}';
    }
    
}
