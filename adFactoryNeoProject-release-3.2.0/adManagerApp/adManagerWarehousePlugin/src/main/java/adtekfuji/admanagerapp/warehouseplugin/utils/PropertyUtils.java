/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.utils;

import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author nar-nakamura
 */
public class PropertyUtils {

    /**
     * 文字列プロパティから整数プロパティを取得する。
     *
     * @param value 文字列プロパティ
     * @return 整数プロパティ
     */
    public static IntegerProperty convertPropertyStrToInt(StringProperty value) {
        if (Objects.nonNull(value) && Objects.nonNull(value.get()) && !value.get().isEmpty()) {
            return new SimpleIntegerProperty(Integer.valueOf(value.get()));
        } else {
            return null;
        }
    }
}
