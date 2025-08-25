/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.utility;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.scene.paint.Color;

/**
 * 文字列ユーティリティクラス
 *
 * @author s-heya
 */
public class StringUtils {

    private StringUtils() {
    }

    /**
     * 指定された String が null または空文字列ではないかどうかを返す
     *
     * @param value チェックする String
     * @return null または空文字列かどうか。null または空文字列なら false 、それ以外なら true 。
     */
    public static boolean nonEmpty(String value) {
        return !isEmpty(value);
    }

    /**
     * 指定された String が null または空文字列かどうかを返す。
     *
     * @param value チェックする String
     * @return null または空文字列かどうか。null または空文字列なら true 、それ以外なら false 。
     */
    public static boolean isEmpty(String value) {
        return value == null || value.length() == 0;
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
        return left.equals(right);
    }

    /**
     * Color型のオブジェクトを"#RRGGBB"形式の文字列に変換する。
     *
     * @param color
     * @return "#RRGGBB"形式の文字列
     */
    public static String colorToRGBCode(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    /**
     * Stringをbooleanに変換する
     *
     * @param value
     * @return
     */
    public static boolean parseBoolean(String value) {
        boolean result = false;
        try {
            result = Boolean.parseBoolean(value);
        } catch (IllegalArgumentException | NullPointerException e) {
        }
        return result;
    }

    /**
     * String から int に変換する
     * @param value
     * @return
     */
    public static int parseInteger(String value) {
        if (!StringUtils.isEmpty(value)) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ex) {
                return 0;
            }
        } else {
            return 0;
        }
    }

    /**
     * 文字列をlongに変換する。変換できない場合0を返す。
     *
     * @param value
     * @return 正常に変換できた場合そのlong値。変換できなかった場合0を返す。
     */
    public static long parseLong(String value) {
        if (!StringUtils.isEmpty(value)) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException ex) {
                return 0L;
            }
        } else {
            return 0L;
        }
    }

    /**
     * 文字列の前後のスペースを削除する (全角スペースも削除する)
     *
     * @param value
     * @return
     */
    public static String trim2(String value) {
        if (value == null || value.length() == 0) {
            return value;
        }
        int st = 0;
        int len = value.length();
        char[] val = value.toCharArray();
        while ((st < len) && ((val[st] <= ' ') || (val[st] == '　'))) {
            st++;
        }
        while ((st < len) && ((val[len - 1] <= ' ') || (val[len - 1] == '　'))) {
            len--;
        }
        return ((st > 0) || (len < value.length())) ? value.substring(st, len) : value;
    }

    /**
     * 文字列が指定文字数を超える場合、短縮して末尾に"..."を付けて返す
     *
     * @param value
     * @param len
     * @return
     */
    public static String getShortName(String value, long len) {
        if (value.length() <= len) {
            return value;
        } else {
            return String.format("%." + Long.toString(len) + "s", value) + "...";
        }
    }

    /**
     * 半角を1バイト、全角を2バイトとして、文字列が指定サイズになるよう末尾を空白埋めする。
     * @param src
     * @param length
     * @return
     */
    public static String padBytesString(String src, int length) {
        byte[] base = src.getBytes();
        Integer formatLen = length + src.length() - base.length;
        if (formatLen < src.length()) {
            formatLen = src.length();
        }
        String formatString = "%-" + formatLen.toString() + "s";
        String ret =  String.format(formatString, src);
        return getBytesString(ret, 0, length);
    }

    /**
     * 半角を1バイト、全角を2バイトとして、文字列の一部を抽出する。
     * 末尾の文字が全角文字の半分の場合、空白に置き換える。
     * @param src
     * @param start
     * @param length
     * @return
     */
    public static String getBytesString(String src, Integer start, Integer length) {
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

    /**
     * 半角英字を小文字に変換する。
     *
     * @param value
     * @return
     */
    public static String toLowerCase(final String value) {
        String sentence = "";
        for (int i = 0 ; i < value.length(); i++) {
            char ch = value.charAt(i);
            if ('A' <= ch && ch <= 'Z') {
                sentence = sentence + (char)(ch + 32);
            } else {
                sentence = sentence + ch;
            }
        }
        return sentence;
    }

    /**
     * 半角英字を大文字に変換する。
     *
     * @param value
     * @return
     */
    public static String toUpperCase(final String value) {
        String sentence = "";
        for (int i = 0 ; i < value.length(); i++) {
            char ch = value.charAt(i);
            if ('a' <= ch && ch <= 'z') {
                sentence = sentence + (char)(ch - 32);
            } else {
                sentence = sentence + ch;
            }
        }
        return sentence;
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
     * ファイル名から拡張子をのぞく
     * @param fileName ファイル名
     * @return ファイル名
     */
    public static String removeExtension(String fileName) {
        // 最後のドットの位置を取得します
        int lastDotIndex = fileName.lastIndexOf(".");

        // ドットが見つからなかった場合、元のファイル名を返します
        if (lastDotIndex == -1) {
            return fileName;
        }

        // 拡張子を除いたファイル名を返します
        return fileName.substring(0, lastDotIndex);
    }

    /**
     * LIKEによる部分一致を擬似的な正規表現で表すために文字列をSQL部分一致用文字列に変換する。<br>
     * 先頭の".*"は"%"に置換<br>
     * 末尾の".*"は"%"に置換<br>
     * "_"は"\_"に置換(_はLIKEの検索で任意の一文字として扱われるため)<br>
     * "%"は"\%"に置換(%はLIKEの検索で任意の文字列として扱われるため)<br>
     *
     * @param str
     * @return
     */
    public static String escapeLike(String str) {
        return escapeLikeChar(str).replaceFirst("^(\\.\\*)", "%").replaceFirst("(\\.\\*)$", "%");
    }

    /**
     * SQLでLIKE実行時%と_はそれぞれ任意の文字列、任意の一文字と解釈されるためそれをエスケープ
     *
     * @param str
     * @return
     */
    public static String escapeLikeChar(String str) {
        return str.replaceAll("%", "\\\\%").replaceAll("_", "\\\\_");
    }

    /**
     * {@link #escapeLike(java.lang.String) escapeLike}
     * でのLIKE部分一致検索と同等の比較を文字列同士で行う。
     *
     * @param modelName 比較対象文字列　これがnullの場合常にtrue
     * @param regexp 擬似的な正規表現　これがnullまたは空白の場合常にtrue
     * @return
     */
    public static boolean like(String modelName, String regexp) {
        // 進捗モニター設定モデル名が設定されてない場合、サーバー側はすべてのモデル名にマッチするためtrue
        if (isEmpty(regexp)) {
            return true;
        }

        if (Objects.isNull(modelName)) {
            return false;
        }

        final String unreg = regexp.replaceFirst("^(\\.\\*)", "").replaceFirst("(\\.\\*)$", "");

        if (regexp.startsWith(".*")) {
            return regexp.endsWith(".*") ? modelName.contains(unreg) : modelName.endsWith(unreg);
        }
        return regexp.endsWith(".*") ? modelName.startsWith(unreg) : Objects.equals(modelName, regexp);
    }

    /**
     * 数値文字列(Integer)かチェックする。
     *
     * @param value 文字列
     * @return 数値文字列?(true:数値, false:数値以外)
     */
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * 数値文字列(Long)かチェックする。
     *
     * @param value 文字列
     * @return 数値文字列?(true:数値, false:数値以外)
     */
    public static boolean isLong(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * 指定フォーマットに合った日付文字列かチェックする。
     *
     * @param value 文字列
     * @param format 日付フォーマット文字列
     * @param permitDoubleByte 全角を許可? (true:半角に変換してチェックする)
     * @return 日付文字列? (true:日付, false:日付以外)
     */
    public static boolean isDate(String value, String format, boolean permitDoubleByte) {
        try {
            String checkValue;
            if (permitDoubleByte) {
                checkValue = Normalizer.normalize(value, Normalizer.Form.NFKC);// 半角に変換する。 
            } else {
                checkValue = value;
            }

            LocalDate.parse(checkValue,
                    DateTimeFormatter.ofPattern(format).withResolverStyle(ResolverStyle.STRICT));
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 文字列の左側の空白を取り除く。
     * 
     * @param value 文字列
     * @return 左側の空白を取り除いた文字列
     */
    public static String trimLeft(String value) {
        if (isEmpty(value)) {
            return "";
        }
        int startPos = 0;
        for (int ii = 0; ii < value.length(); ii++) {
            if (value.charAt(ii) != ' ') {
                startPos = ii;
                break;
            }
        }
        return value.substring(startPos);
    }

    /**
     * 4バイト毎に分割する
     * @param str
     * @return 分割されたデータ
     */
    public static List<String> splitBy(String str, int length) {
        List<String> list = new ArrayList<>();
        int next = 0;
        for (int i = 0; i < str.length(); i = next) {
            next = Math.min(str.length(), next + length);
            list.add(str.substring(i, next));
        }
        return list;
    }
}
