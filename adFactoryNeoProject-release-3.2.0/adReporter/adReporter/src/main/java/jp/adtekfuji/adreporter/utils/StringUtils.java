/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adreporter.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * 文字列ユーティリティクラス
 *
 * @author s-heya
 */
public class StringUtils {

    /**
     * 指定された String が null または空文字列かどうかを返す。
     *
     * @param value チェックする String
     * @return null または空文字列かどうか。null または空文字列なら true 、それ以外なら false 。
     */
    public static boolean isEmpty(String value) {
        if (value == null || value.length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 文字列を比較する。
     *
     * @param left
     * @param right
     * @return
     */
    public static boolean equals(String left, String right) {
        if (left == null) {
            return false;
        }
        return left.equalsIgnoreCase(right);
    }

    /**
     * XMLをHTMLに変換する。
     *
     * @param xml
     * @param xsl
     * @return
     * @throws TransformerException
     * @throws IOException
     */
    public static String toHTML(byte[] xml, String xsl) throws TransformerException, IOException {
        String html = "";
        try (InputStream ds = new ByteArrayInputStream(xml)) {
            Source xmlSource = new StreamSource(ds);
            try (InputStream xs = new ByteArrayInputStream(xsl.getBytes("UTF-8"))) {
                Source xsltSource = new StreamSource(xs);
                StringWriter writer = new StringWriter();
                Result result = new StreamResult(writer);
                TransformerFactory factory = TransformerFactory.newInstance();
                Transformer transformer = factory.newTransformer(xsltSource);
                transformer.transform(xmlSource, result);
                html = writer.toString();
            }
        }
        return html;
    }

    /**
     * ファイル名から拡張子を取得する。
     *
     * @param fileName ファイル名
     * @return ファイルの拡張子
     */
    public static String getSuffix(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return null;
        }
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            return fileName.substring(index + 1);
        }
        return fileName;
    }

    /**
     * 半角を1バイト、全角を2バイトとして、文字列の一部を抽出する。 末尾の文字が全角文字の半分の場合、空白に置き換える。
     *
     * @param src
     * @param start
     * @param length
     * @return
     */
    public static final String getBytesString(String src, Integer start, Integer length) {
        byte[] base = src.getBytes();
        String ret = new String(base, start, length);

        // 末尾の文字が全角文字の半分の場合、空白に置き換える。
        String buf = new String(base, start, base.length - start);
        int lastIndex = ret.length() - 1;
        String lastString1 = buf.substring(lastIndex, lastIndex + 1);
        String lastString2 = ret.substring(lastIndex, lastIndex + 1);
        if (!lastString1.equals(lastString2)) {
            ret = ret.substring(0, lastIndex) + " ";
        }

        return ret;
    }
}
