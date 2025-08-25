/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.summaryreportplugin.builder;

import static jp.adtekfuji.adinterfaceservice.summaryreportplugin.builder.HtmlTagAttributeEnum.BORDER;
import static jp.adtekfuji.adinterfaceservice.summaryreportplugin.builder.HtmlTagAttributeEnum.CELLPADDING;
import static jp.adtekfuji.adinterfaceservice.summaryreportplugin.builder.HtmlTagAttributeEnum.CHARSET;
import static jp.adtekfuji.adinterfaceservice.summaryreportplugin.builder.HtmlTagAttributeEnum.COLSPAN;
import static jp.adtekfuji.adinterfaceservice.summaryreportplugin.builder.HtmlTagAttributeEnum.HREF;
import static jp.adtekfuji.adinterfaceservice.summaryreportplugin.builder.HtmlTagAttributeEnum.STYLE;
import static jp.adtekfuji.adinterfaceservice.summaryreportplugin.builder.HtmlTagTypeEnum.*;

/**
 * HTMLを構築するビルダークラス
 * <br>
 * 以下のようにしてHTML形式文字列をメソッドで作成できます。<br>
 * <br>
 * <pre>
 * {@code
 * new SimpleHtmlBuilder()
 *     .html()
 *         .head()
 *             .line("Sample").
 *         _head()
 *         .body()
 *             .line("HelloWorld")
 *         ._body()
 *     ._html();
 * }
 * </pre>
 * <br>
 * これにより作成されるHTML形式文字列は以下です。<br>
 * <pre>
 * {@code 
 * <html><head>Sample</head><body>HelloWorld</body></html>
 * }
 * </pre>
 * <br>
 * 
 * 生成されたHTML形式文字列がHTML構文として正しいかチェックする機能はないため、<br>
 * HTML形式文字列の妥当性は利用者側で確認する必要があります。<br>
 * 
 * @author shizuka.hirano
 */
public class SimpleHtmlBuilder {

    /**
     * 出力用HTML形式文字列
     */
    private final StringBuilder sb = new StringBuilder();

    /**
     * 出力用HTML形式文字列に文字列を追加します。
     *
     * @param str 追加する文字列
     */
    private void append(String str) {
        sb.append(str);
    }

    /**
     * 出力用HTML形式文字列に引数に指定した出力用HTML形式文字列を結合します。
     *
     * @param builder 出力用HTML形式文字列
     * @return 結合後の出力用HTML形式文字列
     */
    public SimpleHtmlBuilder append(SimpleHtmlBuilder builder) {
        append(builder.toString());
        return this;
    }

    /**
     * 出力用HTML形式文字列に文字列を追加します。
     *
     * @param str 追加する文字列
     * @return 文字列が追加された出力用HTML形式文字列
     */
    public SimpleHtmlBuilder line(String str) {
        append(str);
        return this;
    }

    /**
     * 出力用HTML形式文字列にタグを追加します。<br>
     * <br>
     * tagTypeは{@link HtmlTagTypeEnum}で指定します。<br>
     * <br>
     * 例：html, _html, body, _bodyなど<br>
     * html, _htmlはそれぞれ{@code <html>, </html>}に対応します。
     * 
     * @param tagType 追加するタグの種類
     * @return タグが追加された出力用HTML形式文字列
     */
    public SimpleHtmlBuilder tag(HtmlTagTypeEnum tagType) {
        append(tagType.toString());
        return this;
    }

    /**
     * 出力用HTML形式文字列にタグを追加します。<br>
     * <br>
     * tagTypeは{@link HtmlTagTypeEnum}で指定します。<br>
     * また、引数に属性を取り、タグ内に属性を記述します。<br>
     * <br>
     * 例：tagType=TABLE, attribute={key:border, value:1}の場合、<br>
     * {@code <table border="1">}のようなタグが追加されます。<br>
     * 
     * @param tagType 追加するタグの種類
     * @param attribute 追加するタグ内に設定する属性
     * @return タグが追加された出力用HTML形式文字列
     */
    public SimpleHtmlBuilder tag(HtmlTagTypeEnum tagType, HtmlTagAttributeMap<?> attribute) {
        StringBuilder builder = new StringBuilder(tagType.toString());
        builder.deleteCharAt(builder.length() - 1)
                .append(" ")
                .append(attribute.toString())
                .append(">");
        append(builder.toString());
        return this;
    }

    /**
     * 出力用HTML形式文字列に{@code <html>}タグを追加します。
     *
     * @return 出力用HTML形式文字列
     */
    public SimpleHtmlBuilder html() {
        tag(HTML);
        return this;
    }

    /**
     * 出力用HTML形式文字列に{@code </html>}タグを追加します。
     *
     * @return HTML形式文字列
     */
    public SimpleHtmlBuilder _html() {
        tag(_HTML);
        return this;
    }

    /**
     * 出力用HTML形式文字列に{@code <meta charset="XXX">}タグを追加します。<br>
     * XXXには引数charsetで指定した文字エンコードが記述されます。<br>
     *
     * @param charset 追加するタグ内に設定する文字エンコード
     * @return 出力用HTML形式文字列
     */
    public SimpleHtmlBuilder meta(String charset) {
        tag(META);
        HtmlTagAttributeMap<String> attribute = new HtmlTagAttributeMap<>();
        attribute.put(CHARSET, charset + "");
        tag(_META);
        return this;
    }

    /**
     * 出力用HTML形式文字列に{@code <head>}タグを追加します。
     *
     * @return 出力用HTML形式文字列
     */
    public SimpleHtmlBuilder head() {
        tag(HEAD);
        return this;
    }

    /**
     * 出力用HTML形式文字列に{@code </head>}タグを追加します。
     *
     * @return 出力用HTML形式文字列
     */
    public SimpleHtmlBuilder _head() {
        tag(_HEAD);
        return this;
    }

    /**
     * 出力用HTML形式文字列に{@code <body>}タグを追加します。
     *
     * @return 出力用HTML形式文字列
     */
    public SimpleHtmlBuilder body() {
        tag(BODY);
        return this;
    }

    /**
     * 出力用HTML形式文字列に{@code </body>}タグを追加します。
     *
     * @return HTML形式文字列
     */
    public SimpleHtmlBuilder _body() {
        tag(_BODY);
        return this;
    }

    /**
     * 出力用HTML形式文字列に{@code <h1>}タグを追加します。<br>
     * @param style 追加するタグ内に設定するstyle属性
     * @return 出力用HTML形式文字列
     */
    public SimpleHtmlBuilder h1(String str, String style) {
        HtmlTagAttributeMap<String> attribute = new HtmlTagAttributeMap<>();
        attribute.put(STYLE, style);
        tag(H1, attribute);
        line(str);
        tag(_H1);
        return this;
    }


    /**
     * 出力用HTML形式文字列に{@code <table border="XXX">}タグを追加します。<br>
     * XXXには引数で指定したborderの値が記述されます。<br>
     *
     * @param border テーブルのborder属性に与える値
     * @param style 追加するタグ内に設定するstyle属性
     * @return 出力用HTML形式文字列
     */
    public SimpleHtmlBuilder table(int border, String style) {
        HtmlTagAttributeMap<String> attribute = new HtmlTagAttributeMap<>();
        attribute.put(BORDER, String.valueOf(border));
        attribute.put(STYLE, style);
        tag(TABLE, attribute);
        return this;
    }

    /**
     * 出力用HTML形式文字列に{@code <table border="XXX" style="YYY" cellpadding="ZZZ">}タグを追加します。<br>
     * XXX、YYY、ZZZには引数で与えた文字列が記述されます。<br>
     *
     * @param border テーブルのborder属性に与える値
     * @param style 追加するタグ内に設定するstyle属性
     * @param padding 追加するタグ内に設定するcellpadding属性
     * @return 出力用HTML形式文字列
     */
    public SimpleHtmlBuilder table(int border, String style, int padding) {
        HtmlTagAttributeMap<String> attribute = new HtmlTagAttributeMap<>();
        attribute.put(BORDER, String.valueOf(border));
        attribute.put(STYLE, style);
        attribute.put(CELLPADDING, String.valueOf(padding));
        tag(TABLE, attribute);
        return this;
    }

    /**
     * 出力用HTML形式文字列に{@code </table>}タグを追加します。
     *
     * @return 出力用HTML形式文字列
     */
    public SimpleHtmlBuilder _table() {
        tag(_TABLE);
        return this;
    }

    /**
     * 出力用HTML形式文字列に{@code <th style="XXX" colspan="YYY">ZZZ</th>}タグを追加します。<br>
     * XXX、YYY、ZZZには引数で与えた文字列が記述されます。<br>
     *
     * @param title テーブルのヘッダに表示する文字列
     * @param colspan 行方向(横方向)に結合するセルの数
     * @param style 追加するタグ内に設定するstyle属性
     * @return 出力用HTML形式文字列
     */
    public SimpleHtmlBuilder th(String title, int colspan, String style) {
        HtmlTagAttributeMap<String> attribute = new HtmlTagAttributeMap<>();
        attribute.put(STYLE, style);
        attribute.put(COLSPAN, String.valueOf(colspan));
        tag(TH, attribute);
        line(title);
        tag(_TH);
        return this;
    }

    /**
     * 出力用HTML形式文字列に{@code <tr>}タグを追加します。
     *
     * @return 出力用HTML形式文字列
     */
    public SimpleHtmlBuilder tr() {
        tag(TR);
        return this;
    }

    /**
     * 出力用HTML形式文字列に{@code <tr style="XXX">}タグを追加します。<br>
     * XXXには引数で与えた文字列が記述されます。<br>
     *
     * @param style style属性
     * @return 出力用HTML形式文字列
     */
    public SimpleHtmlBuilder tr(String style) {
        HtmlTagAttributeMap<String> styleAttribute = new HtmlTagAttributeMap<>();
        styleAttribute.put(STYLE, style);

        tag(TR, styleAttribute);
        return this;
    }

    /**
     * 出力用HTML形式文字列に{@code <tr style="XXX">}タグを追加します。<br>
     * 引数のshouldDisplayWarningにtrueが指定された場合のみ、<br>
     * style属性を追加し、XXXには引数で与えた文字列が記述されます。<br>
     *
     * @param shouldDisplayWarning true：警告表示する、false：警告表示しない
     * @param style 追加するタグ内に設定するstyle属性(警告表示を行う場合)
     * @return 出力用HTML形式文字列
     */
    public SimpleHtmlBuilder tr(boolean shouldDisplayWarning, String style) {
        HtmlTagAttributeMap<String> styleAttribute = new HtmlTagAttributeMap<>();
        styleAttribute.put(STYLE, style);

        if (shouldDisplayWarning) {
            tag(TR, styleAttribute);
        } else {
            tag(TR);
        }
        return this;
    }

    /**
     * 出力用HTML形式文字列に{@code </tr>}タグを追加します。
     *
     * @return 出力用HTML形式文字列
     */
    public SimpleHtmlBuilder _tr() {
        tag(_TR);
        return this;
    }

    /**
     * 出力用HTML形式文字列に{@code <td>}タグを追加します。
     *
     * @return 出力用HTML形式文字列
     */
    public SimpleHtmlBuilder td() {
        tag(TD);
        return this;
    }

    /**
     * 出力用HTML形式文字列に{@code <td>XXX</td>}タグを追加します。<br>
     * XXXには引数で与えた文字列が記述されます。<br>
     *
     * @param str tdタグ内に記述する文字列
     * @return 出力用HTML形式文字列
     */
    public SimpleHtmlBuilder td(String str) {
        tag(TD);
        line(str);
        tag(_TD);
        return this;
    }

    /**
     * 出力用HTML形式文字列に{@code <td style="XXX">YYY</td>}を追加します。<br>
     * XXX、YYYには引数で与えた文字列が記述されます。<br>
     *
     * @param str tdタグ内に記述する文字列
     * @param style 追加するタグ内に設定するstyle属性
     * @return 出力用HTML形式文字列
     */
    public SimpleHtmlBuilder td(String str, String style) {
        HtmlTagAttributeMap<String> styleAttribute = new HtmlTagAttributeMap<>();
        styleAttribute.put(STYLE, style);
        tag(TD, styleAttribute);
        line(str);
        tag(_TD);
        return this;
    }

    /**
     * 出力用HTML形式文字列に{@code <td style="XXX">YYY</td>}を追加します。<br>
     * XXXには引数で与えた文字列が記述されます。<br>
     * YYYにはaタグの出力用HTML形式文字列が記述されます。<br>
     *
     * @param builder HTML形式文字列(ボタン)
     * @param style 追加するタグ内に設定するstyle属性
     * @return 出力用HTML形式文字列
     */
    public SimpleHtmlBuilder td(SimpleHtmlBuilder builder, String style) {
        HtmlTagAttributeMap<String> styleAttribute = new HtmlTagAttributeMap<>();
        styleAttribute.put(STYLE, style);
        tag(TD, styleAttribute);
        line(builder.toString());
        tag(_TD);
        return this;
    }

    /**
     * 出力用HTML形式文字列に{@code </td>}タグを追加します。
     *
     * @return 出力用HTML形式文字列
     */
    public SimpleHtmlBuilder _td() {
        tag(_TD);
        return this;
    }

    /**
     * 出力用HTML形式文字列に{@code <a href="XXX" style="YYY">ZZZ</td>}を追加します。<br>
     * XXX、YYY、ZZZには引数で与えた文字列が記述されます。<br>
     *
     * @param title リンクに表示する文字列
     * @param url リンク先のURL
     * @param style リンクのstyle属性
     * @return 出力用HTML形式文字列
     */
    public SimpleHtmlBuilder link(String title, String url, String style) {
        HtmlTagAttributeMap<String> attribute = new HtmlTagAttributeMap<>();
        attribute.put(STYLE, style);
        attribute.put(HREF, url);

        tag(LINK, attribute);
        line(title);
        tag(_LINK);
        return this;
    }

    /**
     * 出力用HTML形式文字列に{@code <center style="XXX">}を追加します。<br>
     * XXXには引数で与えた文字列が記述されます。<br>
     *
     * @param style 追加するタグ内に設定するstyle属性
     * @return 出力用HTML形式文字列
     */
    public SimpleHtmlBuilder center(String style) {
        HtmlTagAttributeMap<String> styleAttribute = new HtmlTagAttributeMap();
        styleAttribute.put(STYLE, style);
        tag(CENTER, styleAttribute);
        return this;
    }

    /**
     * 出力用HTML形式文字列に{@code </center>}を追加します。<br>
     *
     * @return 出力用HTML形式文字列
     */
    public SimpleHtmlBuilder _center() {
        tag(_CENTER);
        return this;
    }

    public SimpleHtmlBuilder br() {
        tag(BR);
        return this;
    }


    /**
     * 文字列表現を取得します。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return sb.toString();
    }
}
