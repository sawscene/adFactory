/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.beans.property.SimpleStringProperty;

/**
 * プロパティユーティリティクラス
 *
 * @author nar-nakamura
 */
public class PropertyUtils {

    /**
     * 文字列を区切り文字で分解して、SimpleStringPropertyのリストに変換する。
     *
     * @param value 文字列
     * @param sep 区切り文字
     * @return SimpleStringPropertyのリスト
     */
    public static List<SimpleStringProperty> stringToPropertyList(String value, String sep) {
        if (Objects.isNull(value) || value.isEmpty()) {
            return new ArrayList<>();
        }
        return Stream.of(value.split(sep))
                .map(SimpleStringProperty::new)
                .collect(Collectors.toList());
    }

    /**
     * SimpleStringPropertyのリストを、区切り文字を付けて結合した文字列に変換する。
     *
     * @param properties SimpleStringPropertyのリスト
     * @param sep 区切り文字
     * @return 区切り文字を付けて結合した文字列
     */
    public static String propertyListToString(List<SimpleStringProperty> properties, String sep) {
        StringBuilder sb = new StringBuilder();
        if (Objects.nonNull(properties)) {
            for (SimpleStringProperty property : properties) {
                if (Objects.nonNull(property.getValue()) && !property.getValue().isEmpty()) {
                    if (sb.length() > 0) {
                        sb.append(sep);
                    }
                    sb.append(property.getValue());
                }
            }
        }
        return sb.toString();
    }
}
