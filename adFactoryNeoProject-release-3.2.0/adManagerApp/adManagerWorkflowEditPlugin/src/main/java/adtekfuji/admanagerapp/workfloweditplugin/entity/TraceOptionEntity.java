/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.entity;

import java.util.List;
import java.util.Objects;
import org.simpleframework.xml.Default;
import org.simpleframework.xml.DefaultType;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import jp.adtekfuji.javafxcommon.controls.InputValueColor;

/**
 *
 * @author nar-nakamura
 */
@Root(name="traceOption")
@Default(DefaultType.FIELD)
public class TraceOptionEntity {

    @Element(required = true)
    private String key;
    @Element(required = false)
    private String value;
    @ElementList(name="values", entry="value", inline = false, required = false)
    private List<String> values;

    //miya_add start. 色付入力値リスト追加対応.
    @ElementList(name="valueColors", entry="value", inline = false, required = false)
    private List<InputValueColor> valueColors;
    //miya_add end.
    
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    //miya_add start. 色付入力値リスト追加対応.
    public void setColorTextBkValues(List<InputValueColor> values) {
        this.valueColors = values;
    }
    
    public List<InputValueColor> getColorTextBkValues() {
        return valueColors;
    }
    //miya_add_end.
    
    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TraceOptionEntity other = (TraceOptionEntity) obj;
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TraceOptionEntity{" + "name=" + key + ", value=" + value + '}';
    }
}
