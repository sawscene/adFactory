package adtekfuji.utility;

public class PropertyUtils {
    private static boolean isCharacter(char character) {
        return (' ' <= character) && (character <= '~');
    }

    private static String toUnicodeEscape(char character) {
        final char[] hexDigit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        return "\\u"
                + hexDigit[(character >> 12) & 0xF]
                + hexDigit[(character >> 8) & 0xF]
                + hexDigit[(character >> 4) & 0xF]
                + hexDigit[character & 0xF];
    }

    public static String convertToUnicodeEscape(String theString) {
        final int len = theString.length();
        final int bufLen = theString.length() * 2;
        StringBuilder outBuffer = new StringBuilder(bufLen);

        for (int x = 0; x < len; ++x) {
            char aChar = theString.charAt(x);
            if (isCharacter(aChar)) {
                outBuffer.append(aChar);
            } else {
                outBuffer.append(toUnicodeEscape(aChar));
            }
        }
        return outBuffer.toString();
    }
}
