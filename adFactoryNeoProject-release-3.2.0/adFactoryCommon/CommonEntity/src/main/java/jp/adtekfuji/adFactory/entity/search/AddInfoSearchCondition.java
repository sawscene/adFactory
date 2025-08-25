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

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "addInfoSearchCondition")
public class AddInfoSearchCondition implements Serializable {

    public enum SEARCH_TYPE{
        LIKE("LIKE"),
        EQUAL("="),
        REGX("~");

        final public String type;
        SEARCH_TYPE(String type) {
            this.type = type;
        }
    };

    @XmlElement()
    private String key;// プロパティ名

    @XmlElement()
    private String val;// 値

    @XmlElement()
    private SEARCH_TYPE searchType = SEARCH_TYPE.EQUAL;

    public AddInfoSearchCondition() {
    }

    public AddInfoSearchCondition(String key, String val) {
        this.key = key;
        this.val = val;
    }

    public AddInfoSearchCondition(String key, String val, SEARCH_TYPE searchType) {
        this.key = key;
        this.val = val;
        this.searchType = searchType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public SEARCH_TYPE getSearchType() {
        return searchType;
    }

    public void setSearchType(SEARCH_TYPE searchType) {
        this.searchType = searchType;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.key);
        hash = 47 * hash + Objects.hashCode(this.val);
        hash = 47 * hash + Objects.hashCode(this.searchType);
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
        final AddInfoSearchCondition other = (AddInfoSearchCondition) obj;
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        if (!Objects.equals(this.val, other.val)) {
            return false;
        }

        if (!Objects.equals(this.searchType, other.searchType)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "PropertyCondition{" + "key=" + key + ", val=" + val + ", isSimilarTo=" + searchType + '}';
    }

}
