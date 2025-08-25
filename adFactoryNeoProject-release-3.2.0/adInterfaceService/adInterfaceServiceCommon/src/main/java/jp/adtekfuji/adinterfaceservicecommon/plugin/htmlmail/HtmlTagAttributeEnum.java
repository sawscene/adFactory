/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservicecommon.plugin.htmlmail;

/**
 * HTMLタグの属性
 *
 * @author shizuka.hirano
 */
public enum HtmlTagAttributeEnum {
    /**
     * 境界線の太さ
     */
    BORDER("border="),
    /**
     * 表におけるセルのデータと境界線との間隔
     */
    CELLPADDING("cellpadding="),
    /**
     * 文字エンコーディング
     */
    CHARSET("charset="),
    /**
     * 行方向(横方向)に結合するセルの数
     */
    COLSPAN("colspan="),
    /**
     * リンク先
     */
    HREF("href="),
    /**
     * スタイル情報
     */
    STYLE("style=");

    /**
     * HTMLタグの属性名
     */
    private String attributeName;

    /**
     * コンストラクタ
     *
     * @param attributeName HTMLタグの属性名
     */
    private HtmlTagAttributeEnum(String attributeName) {
        this.attributeName = attributeName;
    }

    /**
     * 文字列表現を取得します。
     *
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return attributeName;
    }
}

