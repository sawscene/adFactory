/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * メール頻度
 *
 * @author okada
 */
public enum SendFrequencyEnum {
    /**
     * 毎日
     */
    EVERYDAY(0, "key.Everyday"),
    /**
     * 月曜日
     */
    MONDAY(1, "key.Monday"),
    /**
     * 火曜日
     */
    TUESDAY(2, "key.Tuesday"),
    /**
     * 水曜日
     */
    WEDNESDAY(3, "key.Wednesday"),
    /**
     * 木曜日
     */
    THURSDAY(4, "key.Thursday"),
    /**
     * 金曜日
     */
    FRIDAY(5, "key.Friday"),
    /**
     * 土曜日
     */
    SATURDAY(6, "key.Saturday"),
    /**
     * 日曜日
     */
    SUNDAY(7, "key.Sunday"),
    /**
     * 毎月
     */
    MONTHLY(8, "key.Monthly");

    private final int id;
    private final String value;

    private SendFrequencyEnum(int id, String value) {
        this.id = id;
        this.value = value;
    }

    private SendFrequencyEnum(int id) {
        SendFrequencyEnum info = valueOf(id);
        this.id = info.id;
        this.value = info.value;
    }

    public int getId() {
        return this.id;
    }

    public String getValue() {
        return this.value;
    }

    /**
     * 列挙子の一つ目の値に一致する列挙型を返す
     *
     * @param id 列挙子の一つ目の値
     * @return SendFrequencyEnum
     */
    public static SendFrequencyEnum valueOf(int id) {
        SendFrequencyEnum[] array = values();
        for (SendFrequencyEnum num : array) {
            if (id == num.getId()) {
                return num;
            }
        }
        return null;
    }

    /**
     * JSON生成時にSendFrequencyEnumオブジェクトを文字列に変換する。
     */
    public static class Serializer extends JsonSerializer<SendFrequencyEnum> {

        @Override
        public void serialize(SendFrequencyEnum value, JsonGenerator generator, SerializerProvider serializers) throws IOException {
            generator.writeString(String.valueOf(value.ordinal()));
        }
    }

    /**
     * JSON解析時に文字列をSendFrequencyEnumオブジェクトに変換する。
     */
    public static class Deserializer extends JsonDeserializer<SendFrequencyEnum> {

        final static List<SendFrequencyEnum> list = Arrays.asList(SendFrequencyEnum.values());

        @Override
        public SendFrequencyEnum deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            return list.get(Integer.parseInt(parser.getText()));
        }
    }
}
