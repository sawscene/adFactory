/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.utility;

/**
 * 文字列チェック
 *
 * @author s-heya
 */
public class DataValidator {
    // ASCII文字と空白
    public static final String MATCH_ASCII = "^[\\u0020-\\u007E]*$";

    // 英字と空白
    public static final String MATCH_ALPHA = "^[a-zA-Z]*$";

    // 数字と空白
    public static final String MATCH_NUMBER = "^[0-9]*$";

    // 英数字とハイフン、空白
    public static final String MATCH_ALPHA_NUMBER = "^[a-zA-Z0-9-]*$";

    // メールアドレス(簡易)
    public static final String MATCH_MAIL = "([a-zA-Z0-9][a-zA-Z0-9_.+\\-]*)@(([a-zA-Z0-9][a-zA-Z0-9_\\-]+\\.)+[a-zA-Z]{2,6})";
    
    // 256文字以下
    public static final String MATCH_256ORLESS = "^.{0,256}$";
    
    // 1024文字以下
    public static final String MATCH_1024ORLESS = "^.{0,1024}$";

    /**
     * 文字列をチェックする
     *
     * @param value 文字列
     * @param regex 正規表現のパターン
     * @param isRequired 必須かどうか
     * @return
     */
    public static boolean isValid(String value, String regex, boolean isRequired) {
        if (isRequired) {
            if (StringUtils.isEmpty(value)) {
                return false;
            }
        }

        if (StringUtils.isEmpty(regex)) {
            return true;
        } else if (!value.matches(regex)) {
            return false;
        }

        return true;
    }
}
