/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.organizationeditplugin.common;

import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * レコードパラメータークラス
 * 
 * @author s-heya
 */
public class SimpleRecordParam {

    private ObjectProperty<Long> keyProperty;
    private Long key;

    /**
     * コンストラクタ
     */
    public SimpleRecordParam() {
    }

    /**
     * コンストラクタ
     * 
     * @param key 
     */
    public SimpleRecordParam(Long key) {
        this.key = key;
    }

    /**
     * キープロパティを取得する。
     * 
     * @return 
     */
    public ObjectProperty<Long> getKeyProperty() {
        if (Objects.isNull(this.keyProperty)) {
            this.keyProperty = new SimpleObjectProperty<>(key);
        }
        return this.keyProperty;
    }

    /**
     * キーを取得する。
     * 
     * @return 
     */
    public Long getKey() {
        if (Objects.nonNull(this.keyProperty)) {
            return this.keyProperty.get();
        }
        return this.key;
    }

    /**
     * キーを設定する。
     * 
     * @param id 
     */
    void setKey(Long id) {
        if (Objects.nonNull(this.keyProperty)) {
            this.keyProperty.set(id);
        } else {
            this.key = id;
        }
    }
}
