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
 * 集計単位
 *
 * @author okada
 */
public enum AggregateUnitEnum {
    /**
     * 工程順
     */
    ORDER_PROCESSES(0, "key.OrderProcesses"),
    /**
     * モデル
     */
    MODEL_NAME(1, "key.ModelName");

    private final int id;
    private final String value;

    private AggregateUnitEnum(int id, String value) {
        this.id = id;
        this.value = value;
    }

    private AggregateUnitEnum(int id) {
        AggregateUnitEnum info = valueOf(id);
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
     * @return AggregateUnitEnum
     */
    public static AggregateUnitEnum valueOf(int id) {
        AggregateUnitEnum[] array = values();
        for (AggregateUnitEnum num : array) {
            if (id == num.getId()) {
                return num;
            }
        }
        return null;
    }

    /**
     * JSON生成時にAggregateUnitEnumオブジェクトを文字列に変換する。
     */
    public static class Serializer extends JsonSerializer<AggregateUnitEnum> {

        @Override
        public void serialize(AggregateUnitEnum value, JsonGenerator generator, SerializerProvider serializers) throws IOException {
            generator.writeString(String.valueOf(value.ordinal()));
        }
    }

    /**
     * JSON解析時に文字列をAggregateUnitEnumオブジェクトに変換する。
     */
    public static class Deserializer extends JsonDeserializer<AggregateUnitEnum> {

        final static List<AggregateUnitEnum> list = Arrays.asList(AggregateUnitEnum.values());

        @Override
        public AggregateUnitEnum deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            return list.get(Integer.parseInt(parser.getText()));
        }
    }
}
