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
 *
 * @author ke.yokoi
 */
public enum KanbanStatusEnum {

    PLANNING("key.KanbanStatusPlanning", true, true),
    PLANNED("key.KanbanStatusPlanned", true, true),
    WORKING("key.KanbanStatusWorking", true, false),
    SUSPEND("key.KanbanStatusSuspend", true, true),
    INTERRUPT("key.KanbanStatusInterrupt", true, false),
    COMPLETION("key.KanbanStatusCompletion", true,false),
    OTHER("key.KanbanStatusOther", false, false),
    DEFECT("key.KanbanStatusDefect", false, false);

    private final String resourceKey;
    public final boolean isKanbanUpdatableStatus;
    public final boolean isWorkKanbanUpdatableStatus;

    /**
     * 
     * @param resourceKey 
     */
    private KanbanStatusEnum(String resourceKey, boolean isKanbanUpdatableStatus, boolean isWorkKanbanUpdatableStatus) {
        this.resourceKey = resourceKey;
        this.isKanbanUpdatableStatus = isKanbanUpdatableStatus;
        this.isWorkKanbanUpdatableStatus = isWorkKanbanUpdatableStatus;
    }

    /**
     * 
     * @return 
     */
    public String getResourceKey() {
        return resourceKey;
    }

    /**
     * 
     * @param idx
     * @return 
     */
    public static String getValueText(int idx) {
        String value = "";

        // 列挙型を中身の並び順に取得する
        KanbanStatusEnum[] enumArray = KanbanStatusEnum.values();
        // 引数の数値が並び順のMAX数より大きいか判定
        if (enumArray.length > idx) {
            value = KanbanStatusEnum.values()[idx].toString();
        }

        return value;
    }

    /**
     * 
     * @param str
     * @return 
     */
    public static KanbanStatusEnum getEnum(String str) {
        return Arrays.stream(KanbanStatusEnum.values())
                .filter(data->data.toString().equals(str))
                .findFirst()
                .orElse(null);
    }

    /**
     * 
     * @param rb
     * @param val
     * @return 
     */
    public static String getMessage(ResourceBundle rb, KanbanStatusEnum val) {
        KanbanStatusEnum[] enumArray = KanbanStatusEnum.values();
        for (KanbanStatusEnum enumStr : enumArray) {
            if (enumStr.equals(val)) {
                return LocaleUtils.getString(enumStr.resourceKey);
            }
        }
        return "";
    }

    /**
     * 
     * @param rb
     * @return 
     */
    public static List<String> getMessages(ResourceBundle rb) {
        List<String> messages = new ArrayList<>();
        KanbanStatusEnum[] enumArray = KanbanStatusEnum.values();
        for (KanbanStatusEnum enumStr : enumArray) {
            messages.add(LocaleUtils.getString(enumStr.resourceKey));
        }
        return messages;
    }

    // 2019/12/25 カンバン操作 変更ステータス一覧
    /**
     * 
     */
    public static final KanbanStatusEnum[] CanChangeKanbanStatusEnum = {
        PLANNING,
        PLANNED,
        COMPLETION,
        INTERRUPT
    };

    /**
     * JSON生成時にKanbanStatusEnumオブジェクトを文字列に変換する。
     */
    public static class Serializer extends JsonSerializer<KanbanStatusEnum> {

        @Override
        public void serialize(KanbanStatusEnum value, JsonGenerator generator, SerializerProvider serializers) throws IOException {
            generator.writeString(value.name());
        }
    }

    /**
     * JSON解析時に文字列をKanbanStatusEnumオブジェクトに変換する。
     */
    public static class Deserializer extends JsonDeserializer<KanbanStatusEnum> {

        @Override
        public KanbanStatusEnum deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            return KanbanStatusEnum.valueOf(parser.getText());
        }
    }
}
