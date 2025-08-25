/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.utility;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nar-nakamura
 */
public class JsonUtils {

    /**
     * JSON文字列をMapに変換する。
     *
     * @param json JSON文字列
     * @return Map
     */
    public static Map<String, String> jsonToMap(String json) {
        Map<String, String> map = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            map = mapper.readValue(json, new TypeReference<LinkedHashMap<String,String>>(){});
        } catch (Exception ex) {
            LogManager.getLogger().fatal(ex, ex);
        }
        return map;
    }

    /**
     * MapをJSON文字列に変換する。
     *
     * @param map Map
     * @return JSON文字列
     */
    public static String mapToJson(Map<String, String> map) {
        String json = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            json = mapper.writeValueAsString(map);
        } catch (Exception ex) {
            LogManager.getLogger().fatal(ex, ex);
        }
        return json;
    }

    public static List<Map<String, String>> jsonToMaps(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return new ArrayList<>(mapper.readValue(json, new TypeReference<ArrayList<LinkedHashMap<String, String>>>() {}));
        } catch (Exception ex) {
            LogManager.getLogger().fatal(ex, ex);
        }
        return null;
    }

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

    /**
     * JSON文字列をObjectリストに変換する。
     *
     * @param <T>
     * @param json JSON文字列
     * @param destClass Objectの型
     * @return Objectリスト
     */
    public static <T> List<T> jsonToObjects(String json, Class<T[]> destClass) {
        List<T> objects = new LinkedList();
        try {
            if (Objects.isNull(json) || json.isEmpty()) {
                return objects;
            }

            ObjectMapper mapper = new ObjectMapper();
            T[] objectArray = mapper.readValue(json, destClass);

            objects.addAll(Arrays.asList(objectArray));
        } catch (Exception ex) {
            LogManager.getLogger().fatal(ex, ex);
            return new LinkedList();
        }
        return objects;
    }

    /**
     * ObjectリストをJSON文字列に変換する。
     *
     * @param <T>
     * @param objects Objectリスト
     * @return JSON文字列
     */
    public static <T> String objectsToJson(List<T> objects) {
        String json = null;
        try {
            if (Objects.isNull(objects) || objects.isEmpty()) {
                return json;
            }

            ObjectMapper mapper = new ObjectMapper();
            json = mapper.writeValueAsString(objects.toArray());
        } catch (Exception ex) {
            LogManager.getLogger().fatal(ex, ex);
        }
        return json;
    }
}
