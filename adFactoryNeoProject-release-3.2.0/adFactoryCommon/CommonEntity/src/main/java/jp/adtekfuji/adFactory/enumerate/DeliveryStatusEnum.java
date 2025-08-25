/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

import adtekfuji.locale.LocaleUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 払出ステータス
 * 
 * @author s-heya
 */
public enum DeliveryStatusEnum {

    CONFIRM("waitingConfirm", "5"), // 確認待ち
    WAITING("waitingDelivery", "4"), // 未払出
    WORKING("key.PayoutStatusWorking", "1"), // 払出中
    SUSPEND("key.PayoutStatusSuspend", "2"), // 中断中
    PICKED("picked", "3"), // ピッキング完了(払出待ち)
    COMPLETED("key.PayoutEnd", "6"); // 払出完了

    private final String resourceKey;
    private final String sortKey;

    /**
     * コンストラクタ
     * 
     * @param resourceKey リソースキー 
     * @param sortKey ソートキー
     */
    private DeliveryStatusEnum(String resourceKey, String sortKey) {
        this.resourceKey = resourceKey;
        this.sortKey = sortKey;
    }

    /**
     *  リソースキーを取得する。
     * 
     * @return リソースキー
     */
    public String getResourceKey() {
        return resourceKey;
    }

    /**
     * ソートキーを取得する。
     * 
     * @return ソートキー
     */
    public String getSortKey() {
        return sortKey;
    }

    /**
     * 
     * @param idx
     * @return 
     */
    public static String getValueText(int idx) {
        String value = "";

        // 列挙型を中身の並び順に取得する
        DeliveryStatusEnum[] enumArray = DeliveryStatusEnum.values();
        // 引数の数値が並び順のMAX数より大きいか判定
        if (enumArray.length > idx) {
            value = DeliveryStatusEnum.values()[idx].toString();
        }

        return value;
    }

    /**
     * 
     * @param str
     * @return 
     */
    public static DeliveryStatusEnum getEnum(String str) {
        return Arrays.stream(DeliveryStatusEnum.values())
                .filter(data->data.toString().equals(str))
                .findFirst()
                .orElse(null);
    }

    /**
     * 表示名を取得する。
     * 
     * @param rb リソースバンドル
     * @param status 払出ステータス
     * @return 表示名
     */
    public static String getMessage(ResourceBundle rb, DeliveryStatusEnum status) {
        DeliveryStatusEnum[] enumArray = DeliveryStatusEnum.values();
        for (DeliveryStatusEnum enumStr : enumArray) {
            if (enumStr.equals(status)) {
                return LocaleUtils.getString(enumStr.resourceKey);
            }
        }
        return "";
    }

    /**
     * 表示名一覧を取得する。
     * 
     * @param rb リソースバンドル
     * @return 表示名一覧
     */
    public static List<String> getMessages(ResourceBundle rb) {
        List<String> messages = new ArrayList<>();
        DeliveryStatusEnum[] enumArray = DeliveryStatusEnum.values();
        for (DeliveryStatusEnum enumStr : enumArray) {
            messages.add(LocaleUtils.getString(enumStr.resourceKey));
        }
        return messages;
    }

    /**
     * JSON生成時にDeliveryStatusEnumオブジェクトを文字列に変換する。
     */
    public static class Serializer extends JsonSerializer<DeliveryStatusEnum> {

        @Override
        public void serialize(DeliveryStatusEnum value, JsonGenerator generator, SerializerProvider serializers) throws IOException {
            generator.writeString(value.name());
        }
    }

    /**
     * JSON解析時に文字列をDeliveryStatusEnumオブジェクトに変換する。
     */
    public static class Deserializer extends JsonDeserializer<DeliveryStatusEnum> {

        @Override
        public DeliveryStatusEnum deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            return DeliveryStatusEnum.valueOf(parser.getText());
        }
    }
}
