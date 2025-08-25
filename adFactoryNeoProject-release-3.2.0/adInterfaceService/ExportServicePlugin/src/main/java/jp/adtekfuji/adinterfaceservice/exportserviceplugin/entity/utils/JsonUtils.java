/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.exportserviceplugin.entity.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;

/**
 * JSONユーティリティ
 *
 * @author nar-nakamura
 */
public class JsonUtils {

    /**
     * JSON文字列をObjectに変換する。
     *
     * @param <T>
     * @param json JSON文字列
     * @param destClass Objectの型
     * @return Object
     */
    public static <T> T jsonToObject(String json, Class<T> destClass) {
        T object = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            object = mapper.readValue(json, destClass);
        } catch (Exception ex) {
            LogManager.getLogger().fatal(ex, ex);
        }
        return object;
    }

    /**
     * ObjectをJSON文字列に変換する。
     *
     * @param <T>
     * @param object Object
     * @return JSON文字列
     */
    public static <T> String objectToJson(T object) {
        String json = null;
        try {
            if (Objects.isNull(object)) {
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            json = mapper.writeValueAsString(object);
        } catch (Exception ex) {
            LogManager.getLogger().fatal(ex, ex);
        }
        return json;
    }
}
