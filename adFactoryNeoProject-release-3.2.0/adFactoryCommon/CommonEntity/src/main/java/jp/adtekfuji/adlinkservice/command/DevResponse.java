/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adlinkservice.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 応答コマンド
 * 
 * @author s-heya
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "devResponse")
@JsonIgnoreProperties(ignoreUnknown=true)
public class DevResponse  implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 処理結果列挙型
     */
    public enum Result {
        SUCCESSED,
        FAILED,
        SYSTEM_ERROR,
        INVALID_MESSAGE,
        MISSING_PLUGIN,
        UNKNOWN_COMMAND,
        ILLEGAL_OPERATION;

        /**
         * シリアライズ
         */
        public static class Serializer extends JsonSerializer<Result> {
            @Override
            public void serialize(Result value, JsonGenerator generator, SerializerProvider serializers) throws IOException {
                generator.writeString(value.name());
            }
        }

        /**
         * デシリアライズ
         */
        public static class Deserializer extends JsonDeserializer<Result> {
            @Override
            public Result deserialize(JsonParser parser, DeserializationContext context) throws IOException {
                return Result.valueOf(parser.getText());
            }
        }
    }
   
    @XmlElement()
    private String plugin;

    @XmlElement()
    private String cmd;

    @XmlElement()
    private String equipIdent;

    @XmlElement()
    @JsonSerialize(using = DevResponse.Result.Serializer.class)
    @JsonDeserialize(using = DevResponse.Result.Deserializer.class)
    private Result result;
    
    @XmlElement()
    private String value;

    /**
     * コンストラクタ
     * 
     * @param plugin プラグイン名
     * @param cmd コマンド
     * @param result 処理結果
     */
    public DevResponse(String plugin, String cmd, Result result) {
        this.plugin = plugin;
        this.cmd = cmd;
        this.result = result;
    }

    /**
     * コンストラクタ
     * 
     * @param plugin プラグイン名
     * @param cmd コマンド
     * @param result 処理結果
     * @param value 測定値
     */
    public DevResponse(String plugin, String cmd, Result result, String value) {
        this.plugin = plugin;
        this.cmd = cmd;
        this.result = result;
        this.value = value;
    }

    /**
     * コンストラクタ
     * 
     * @param plugin プラグイン名
     * @param cmd コマンド
     * @param equipIdent 設備管理名
     * @param result 処理結果
     * @param value 測定値
     */
    public DevResponse(String plugin, String cmd, String equipIdent, Result result, String value) {
        this.plugin = plugin;
        this.cmd = cmd;
        this.equipIdent = equipIdent;
        this.result = result;
        this.value = value;
    }
    
    /**
     * プラグイン名を取得する。
     * 
     * @return プラグイン名
     */
    public String getPlugin() {
        return plugin;
    }

    /**
     * プラグイン名を設定する。
     * 
     * @param plugin プラグイン名
     */
    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    /**
     * コマンドを取得する。
     * 
     * @return コマンド 
     */
    public String getCmd() {
        return cmd;
    }

    /**
     * コマンドを設定する。
     * 
     * @param cmd コマンド 
     */
    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    /**
     * 設備管理名を取得する。
     * 
     * @return 設備管理名
     */
    public String getEquipIdent() {
        return equipIdent;
    }

    /**
     * 設備管理名を設定する。
     * 
     * @param equipIdent 設備管理名
     */
    public void setEquipIdent(String equipIdent) {
        this.equipIdent = equipIdent;
    }

    /**
     * 処理結果を取得する。
     * 
     * @return 処理結果 
     */
    public Result getResult() {
        return result;
    }

    /**
     * 処理結果を設定する。
     * 
     * @param result 処理結果
     */
    public void setResult(Result result) {
        this.result = result;
    }

    /**
     * 測定値を取得する。
     * 
     * @return 測定値
     */
    public String getValue() {
        return value;
    }

    /**
     * 測定値を設定する。
     * 
     * @param value 測定値
     */
    public void setValue(String value) {
        this.value = value;
    }
}
