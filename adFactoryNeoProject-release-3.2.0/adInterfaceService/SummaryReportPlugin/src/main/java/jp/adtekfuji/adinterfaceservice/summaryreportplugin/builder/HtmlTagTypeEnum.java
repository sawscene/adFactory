/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.summaryreportplugin.builder;

/**
 * HTMLタグの種類
 * 
 * @author shizuka.hirano
 */
public enum HtmlTagTypeEnum {
    /**
     * HTMLの開始タグ
     */
    HTML("<html>"),
    /**
     * HTMLの終了タグ
     */
    _HTML("</html>"),
    /**
     * HEADの開始タグ
     */
    HEAD("<head>"),
    /**
     * HEADの終了タグ
     */
    _HEAD("</head>"),
    /**
     * METAの開始タグ
     */
    META("<meta>"),
    /**
     * METAの終了タグ
     */
    _META("</meta>"),
    /**
     * BODYの開始タグ
     */
    BODY("<body>"),
    /**
     * BODYの終了タグ
     */
    _BODY("</body>"),
    /**
     * TABLEの開始タグ
     */
    TABLE("<table>"),
    /**
     * TABLEの終了タグ
     */
    _TABLE("</table>"),
    /**
     * THの開始タグ
     */
    TH("<th>"),
    /**
     * THの終了タグ
     */
    _TH("</th>"),
    /**
     * TRの開始タグ
     */
    TR("<tr>"),
    /**
     * TRの終了タグ
     */
    _TR("</tr>"),
    /**
     * TDの開始タグ
     */
    TD("<td>"),
    /**
     * TDの終了タグ
     */
    _TD("</td>"),
    /**
     * Aの開始タグ
     */
    LINK("<a>"),
    /**
     * Aの終了タグ
     */
    _LINK("</a>"),
    /**
     * CENTERの開始タグ
     */
    CENTER("<center>"),
    /**
     * CENTERの終了タグ
     */
    _CENTER("</center>"),
    /**
     * H1の開始タグ
     */
    H1("<h1>"),
    /**
     * H1の終了タグ
     */
    _H1("</h1>"),
    /**
     * pタグの開始
     */
    BR("<br>");

    /**
     * タグ
     */
    private String tag;

    /**
     * コンストラクタ
     *
     * @param tag タグ
     */
    private HtmlTagTypeEnum(String tag) {
        this.tag = tag;
    }

    /**
     * 文字列表現を取得します。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return this.tag;
    }
}
