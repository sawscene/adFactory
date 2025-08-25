/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.system;

import java.io.Serializable;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * プロパティ情報
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "systemProp")
public class SystemPropEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(required = true)
    private String key;
    @XmlElement(required = false)
    private String value;

    public SystemPropEntity() {
    }

    public SystemPropEntity(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * プロパティ情報のキーを取得する。
     *
     * @return プロパティ情報のキー
     */
    public String getKey() {
        return this.key;
    }

    /**
     * プロパティ情報のキーを設定する。
     *
     * @param key プロパティ情報のキー
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * プロパティ情報の値を取得する。
     *
     * @return プロパティ情報の値
     */
    public String getValue() {
        return this.value;
    }

    /**
     * プロパティ情報の値を設定する。
     *
     * @param value プロパティ情報の値
     */
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.key);
        hash = 37 * hash + Objects.hashCode(this.value);
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
        final SystemPropEntity other = (SystemPropEntity) obj;
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SystemPropEntity{" + "key=" + this.key + ", value=" + this.value + '}';
    }
}
