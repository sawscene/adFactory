/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.utility;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

/**
 *
 * @author e-mori
 */
public class DateValidator {

    private DateValidator() {
    }

    public static boolean isValid(String date, String format) {
        if (date.length() != format.length()) {
            return false;
        }
        DateFormat dateFormat = createStrictDateFormat(format);
        ParsePosition pos = new ParsePosition(0);
        return dateFormat.parse(date, pos) != null
                && pos.getIndex() == format.length();  //解析した長さと書式の長さを比較
    }

    protected static DateFormat createStrictDateFormat(String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setLenient(false);
        return dateFormat;
    }
}
