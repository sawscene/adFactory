package jp.adtekfuji.prodcountreporter.json;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * レスポンスデータを受け取るクラス
 *
 * @author y.yamashita
 *
 */
public class Response {

    @SerializedName("d")
    public DataWrapper d;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Response response)) return false;
        return Objects.equals(d, response.d);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(d);
    }

    /**
     * レスポンスデータの'd'要素を表すクラス
     */
    public static class DataWrapper {
        @SerializedName("OUT_RESULT")
        public ResultItem resultItem;

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof DataWrapper that)) return false;
            return Objects.equals(resultItem, that.resultItem);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(resultItem);
        }
    }

    /**
     * 'OUT_RESULT'配列の各要素を表すクラス
     */
    public static class ResultItem {

        @SerializedName("RESULT_STATUS")
        public String resultStatus;

        @SerializedName("AUFNR")
        public String aufnr;

        @SerializedName("VORNR")
        public String vornr;

        @SerializedName("LMNGA")
        public Long lmnga;

        @SerializedName("BUDAT")
        public String budat;

        @SerializedName("AUERU")
        public String aueru;

        @SerializedName("CANCEL_KBN")
        public String cancelKbn;

        @SerializedName("MESSAGE_CLASS")
        public String messageClass;

        @SerializedName("MESSAGE_NO")
        public String messageNo;

        @SerializedName("ErrorMessage")
        public String errorMessage;


        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ResultItem that)) return false;
            return Objects.equals(resultStatus, that.resultStatus)
                    && Objects.equals(aufnr, that.aufnr)
                    && Objects.equals(vornr, that.vornr)
                    && Objects.equals(lmnga, that.lmnga)
                    && Objects.equals(budat, that.budat)
                    && Objects.equals(aueru, that.aueru)
                    && Objects.equals(cancelKbn, that.cancelKbn)
                    && Objects.equals(messageClass, that.messageClass)
                    && Objects.equals(messageNo, that.messageNo)
                    && Objects.equals(errorMessage, that.errorMessage);
        }

        @Override
        public int hashCode() {
            return Objects.hash(resultStatus, aufnr, vornr, lmnga, budat, aueru, cancelKbn, messageClass, messageNo, errorMessage);
        }
    }
}
