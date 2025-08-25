/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正規表現ユーティリティクラス
 *
 * @author s-heya
 */
public class RegexUtils {

    /**
     * 文字列から特定部分を抽出する。
     *
     * @param regex
     * @param target
     * @throws IllegalStateException
     * @return
     */
    public static String extract(String regex, String target) throws IllegalStateException {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(target);
		if (matcher.find()) {
			return matcher.group(0);
		} else {
			throw new IllegalStateException("No match found.");
		}
	}
}
