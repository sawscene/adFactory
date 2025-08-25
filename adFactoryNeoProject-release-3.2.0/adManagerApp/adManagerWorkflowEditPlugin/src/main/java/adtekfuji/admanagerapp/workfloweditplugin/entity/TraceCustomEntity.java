/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.entity;

import java.util.Objects;
import org.simpleframework.xml.Default;
import org.simpleframework.xml.DefaultType;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * カスタム設定値
 *
 * @author nar-nakamura
 */
@Root(name="traceCustom")
@Default(DefaultType.FIELD)
public class TraceCustomEntity {

    @Element(required = true)
    private String name; 
    @Element(required = false)
    private String rule; 
    @Element(required = false)
    private String value; 

    /**
     * コンストラクタ
     */
    public TraceCustomEntity() {
    }

    /**
     * カスタム設定名を取得する。
     *
     * @return カスタム設定名
     */
    public String getName() {
        return name;
    }

    /**
     * カスタム設定名を設定する。
     *
     * @param name カスタム設定名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 入力規則を取得する。
     *
     * @return 入力規則
     */
    public String getRule() {
        return this.rule;
    }

    /**
     * 入力規則を設定する。
     *
     * @param rule 入力規則
     */
    public void setRule(String rule) {
        this.rule = rule;
    }

    /**
     * カスタム設定値を取得する。
     *
     * @return カスタム設定値
     */
    public String getValue() {
        return value;
    }

    /**
     * カスタム設定値を設定する。
     *
     * @param value カスタム設定値
     */
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.name);
        hash = 59 * hash + Objects.hashCode(this.rule);
        hash = 59 * hash + Objects.hashCode(this.value);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TraceCustomEntity other = (TraceCustomEntity) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.rule, other.rule)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("TraceCustomEntity{)")
                .append("name=").append(this.name)
                .append(", rule=").append(this.rule)
                .append(", value=").append(this.value)
                .append("}")
                .toString();
    }
}
