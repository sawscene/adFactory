/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.search;

import java.io.Serializable;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.MatchTypeEnum;

/**
 *
 * @author ke.yokoi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "propertyCondition")
public class PropertyCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private String key = null;
    @XmlElement()
    private String value = null;
    @XmlElement()
    private MatchTypeEnum matchType = MatchTypeEnum.LIKE;

    public PropertyCondition() {
    }

    public PropertyCondition(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public PropertyCondition(String key, String value, MatchTypeEnum matchType) {
        this.key = key;
        this.value = value;
        this.matchType = matchType;
    }

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

    public MatchTypeEnum getMatchType() {
        return this.matchType;
    }

    public void setMatchType(MatchTypeEnum matchType) {
        this.matchType = matchType;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.key);
        hash = 47 * hash + Objects.hashCode(this.value);
        hash = 47 * hash + Objects.hashCode(this.matchType);
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
        final PropertyCondition other = (PropertyCondition) obj;
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (!Objects.equals(this.matchType, other.matchType)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PropertyCondition{" + "key=" + key + ", value=" + value + ", matchType=" + matchType + '}';
    }

}
