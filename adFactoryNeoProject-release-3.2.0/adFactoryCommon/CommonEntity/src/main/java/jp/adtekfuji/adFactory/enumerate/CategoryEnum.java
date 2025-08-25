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
 * サマリーレポート設定の メール内容設定の 種別
 *
 * @author okada
 */
public enum CategoryEnum {
    /**
     * 製品の生産数
     */
    NUMBER_OF_PRODUCTS_PRODUCED("key.NumberOfProductsProduced"),
    /**
     * 工程の生産数
     */
    NUMBER_OF_PROCESSES_PRODUCED("key.NumberOfProcessesProduced"),
    /**
     * 製品の平均作業時間
     */
    AVERAGE_PRODUCT_WORKING_HOURS("key.AverageProductWorkingHours"),
    /**
     * 工程の平均作業時間
     */
    WORK_AVERAGE_WORK_TIME("key.WorkAverageWorkTime"),
    /**
     * ライン全体の稼働率
     */
    OVERALL_LINE_UTILIZATION("key.OverallLineUtilization"),
    /**
     * 作業者毎の稼働率
     */
    OPERATING_RATE_PER_WORKER("key.OperatingRatePerWorker"),
    /**
     * 工程内作業のバラツキ
     */
    IN_PROCESS_WORK_VARIATION("key.In-processWorkVariation"),
    /**
     * 作業者間のバラツキ(工程内)
     */
    VARIATION_AMONG_WORKERS("key.VariationAmongWorkers(in-process)"),
    /**
     * 設備完のバラツキ(工程内)
     */
    VARIATION_IN_EQUIPMENT_COMPLETION("key.VariationInEquipmentCompletion(in-process)"),
    /**
     * ラインバランス
     */
    LINE_BALANCE("key.LineBalance"),
    /**
     * 工程間待ち時間
     */
    INTER_PROCESS_WAITING_TIME("key.Inter-processWaitingTime"),
    /**
     * 遅延ランキング
     */
    DELAY_RANKING("key.DelayRanking"),
    /**
     * 中断ランキング
     */
    INTERRUPT_RANKING("key.InterruptRanking"),
    /**
     * 呼出ランキング
     */
    CALL_RANKING("key.CallRanking"),
    ;

    private final String value;

    private CategoryEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    /**
     * JSON生成時にCategoryEnumオブジェクトを文字列に変換する。
     */
    public static class Serializer extends JsonSerializer<CategoryEnum> {

        @Override
        public void serialize(CategoryEnum value, JsonGenerator generator, SerializerProvider serializers) throws IOException {
            generator.writeString(String.valueOf(value.ordinal()));
        }
    }

    /**
     * JSON解析時に文字列をCategoryEnumオブジェクトに変換する。
     */
    public static class Deserializer extends JsonDeserializer<CategoryEnum> {

        final static List<CategoryEnum> list = Arrays.asList(CategoryEnum.values());

        @Override
        public CategoryEnum deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            return list.get(Integer.parseInt(parser.getText()));
        }
    }
}
