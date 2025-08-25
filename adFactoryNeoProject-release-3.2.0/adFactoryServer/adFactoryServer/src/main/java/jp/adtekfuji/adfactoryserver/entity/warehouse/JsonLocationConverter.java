/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.warehouse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jp.adtekfuji.adFactory.entity.warehouse.Location;
import org.apache.logging.log4j.LogManager;
import org.postgresql.util.PGobject;

/**
 * JPAコンバーター
 * JSONと棚情報一覧を相互変換する。
 * 
 * @author s-heya
 */
@Converter
public class JsonLocationConverter implements AttributeConverter<List<Location>, Object> {
    private static final ObjectMapper mapper = new ObjectMapper();
    
    /**
     * LocationオブジェクトをJSONに変換する。
     * 
     * @param value 棚情報一覧
     * @return JSON
     */
    @Override
    public Object convertToDatabaseColumn(List<Location> value) {
        try {
            if (Objects.isNull(value)) {
                return null;
            }
            return mapper.writeValueAsString(value);
            //String json = mapper.writeValueAsString(value);
            //PGobject pgObject = new PGobject();
            //pgObject.setType("jsonb");
            //pgObject.setValue(json);
            //return pgObject;
        } catch (JsonProcessingException ex) {
            LogManager.getLogger().fatal(ex, ex);
            return null;
        }
    }

    /**
     * JSONを棚情報一覧に変換する。
     * 
     * @param value JSON文字列
     * @return 棚情報一覧
     */
    @Override
    public List<Location> convertToEntityAttribute(Object value) {
        try {
            if (Objects.isNull(value)) {
                return null;
            }
            
            Location[] array = null;
            if (value instanceof PGobject && ((PGobject) value).getType().equals("jsonb")) {
                array = mapper.readValue(((PGobject) value).getValue(), Location[].class);

            } else if (value instanceof String) {
                array = mapper.readValue((String) value, Location[].class);
            }
            
            return Arrays.asList(array);
        } catch (IOException ex) {
            LogManager.getLogger().fatal(ex, ex);
            return null;
        }
    }
}
