/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.logging.log4j.LogManager;
import org.postgresql.util.PGobject;

/**
 * JPAコンバーター
 * JSONとMapオブジェクトを相互変換する。
 * 
 * @author s-heya
 */
@Converter
public class JsonMapConverter implements AttributeConverter<Map<String, String>, Object> {
    private static final ObjectMapper mapper = new ObjectMapper();
    
    /**
     * MapオブジェクトをJSONに変換する。
     * 
     * @param value Mapオブジェクト
     * @return JSON
     */
    @Override
    public Object convertToDatabaseColumn(Map<String, String> value) {
        try {
            if (Objects.isNull(value)) {
                return null;
            }
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            LogManager.getLogger().fatal(ex, ex);
            return null;
        }
    }

    /**
     * JSONをMapオブジェクトに変換する。
     * 
     * @param value JSON文字列
     * @return Mapオブジェクト
     */
    @Override
    public Map<String, String> convertToEntityAttribute(Object value) {
        try {
            if (Objects.isNull(value)) {
                return null;
            }

            Map<String, String> map = null;
            if (value instanceof PGobject && ((PGobject) value).getType().equals("jsonb")) {
                map = mapper.readValue(((PGobject) value).getValue(), new TypeReference<Map<String, String>>(){});

            } else if (value instanceof String) {
                map = mapper.readValue((String) value, new TypeReference<Map<String, String>>(){});
            }
            
            return map;
        } catch (IOException ex) {
            LogManager.getLogger().fatal(ex, ex);
            return null;
        }
    }
}
