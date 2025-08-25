package jp.adtekfuji.prodcountreporter.json;

import java.text.SimpleDateFormat;
import java.util.*;
import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

/**
 * SQLデータをjson形式にしておくるクラス
 *
 * @author y.yamashita
 *
 */
public class Request {

    @SerializedName("IN_DATA")
    public InData inData = new InData();

    @SerializedName("OUT_RESULT")
    public OutData outData = new OutData();

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Request request)) return false;
        return Objects.equals(inData, request.inData) && Objects.equals(outData, request.outData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inData, outData);
    }

    /**
     * IN_DATAの中データ
     */
    public static class InData {

        @SerializedName("AUFNR")
        public String aufnr;

        @SerializedName("VORNR")
        public String vornr;

        @SerializedName("LMNGA")
        public String lmnga;

        @SerializedName("BUDAT")
        public String budat;

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof InData inData)) return false;
            return Objects.equals(aufnr, inData.aufnr) && Objects.equals(vornr, inData.vornr) && Objects.equals(lmnga, inData.lmnga) && Objects.equals(budat, inData.budat);
        }

        @Override
        public int hashCode() {
            return Objects.hash(aufnr, vornr, lmnga, budat);
        }
    }

    public static class OutData {
        @Override
        public boolean equals(Object o) {
            return o instanceof OutData;
        }
    }

    public List<String> CheckData() {

        List<String> data = new ArrayList<>();

        if (isNotHalfAlphanumeric(inData.aufnr) || inData.aufnr.length() != 12) {
            data.add("指図番号データ異常");
        }

        if (isNotHalfAlphanumeric(inData.vornr) || inData.vornr.length() != 4) {
            data.add("作業/活動番号データ異常");
        }

        if (isNotCheckNumberOfDigits(Long.parseLong(inData.lmnga), 10, 3)) {
            data.add("確認対象歩留データ異常");
        }

        if (isNotHalfNumeric(inData.budat) || isNotHalfNumeric(inData.budat) || inData.budat.length() != 8) {
            data.add("転記日付データ異常");
        }

        return data;
    }

    private static final String HAS_HALF_ALPHANUMERIC = "^[0-9a-zA-Z-]+$";

    public static boolean isNotHalfAlphanumeric(String str) {
        if (Objects.isNull(str)) {
            return true;
        }

        if (str.isEmpty()) {
            return true;
        }

        return !str.matches(HAS_HALF_ALPHANUMERIC);
    }

    private static final String HAS_HALF_NUMERIC = "^[0-9]+$";

    public static boolean isNotHalfNumeric(String str) {
        if (Objects.isNull(str)) {
            return true;
        }

        if (str.isEmpty()) {
            return true;
        }

        return !str.matches(HAS_HALF_NUMERIC);
    }

    /**
     * 桁数のチェック
     *
     * @param value チェックする数値
     * @param integerDigit 整数部分の桁数
     * @param decimalDigits 小数部分の桁数
     * @return 数値の桁数が指定された桁数を超えている場合はtrue、そうでない場合はfalse
     */
    public static boolean isNotCheckNumberOfDigits(Long value, Integer integerDigit, Integer decimalDigits) {

        if (Objects.isNull(value)) {
            return true;
        }

        BigDecimal bigdecimal = BigDecimal.valueOf(value);

        return (bigdecimal.precision() - bigdecimal.scale() > integerDigit) || (bigdecimal.scale() > decimalDigits);
    }



    public String createMessage() {
        return "[" + ("[" + inData.aufnr + ", " + inData.vornr + ", " + inData.lmnga + ", " + inData.budat + "]") + "]";
    }
}
