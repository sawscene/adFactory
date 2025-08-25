/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.dsKanban;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;

/**
 * 構成部品情報
 * 
 * @author s-heya
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DsParts {

    @JsonProperty("key")
    private String key;

    @JsonProperty("ic")
    private String productNo;

    @JsonProperty("in")
    private String productName;
    
    @JsonProperty("qt")
    private Integer quantity;

    @JsonProperty("pr")
    private String preProcess;
    
    @JsonProperty("lo")
    private String location;
    
    @JsonProperty("pn")
    private String personName;

    @JsonProperty("ct")
    private String compDateTime;

    public static final Comparator<DsParts> keyComparator = (p1, p2) -> {
        String value1 = Objects.isNull(p1.getKey()) ? "" : p1.getKey();
        String value2 = Objects.isNull(p2.getKey()) ? "" : p2.getKey();
        return value1.compareTo(value2);
    };  

    /**
     * コンストラクタ
     */
    public DsParts() {
    }

    /**
     * コンストラクタ
     * 
     * @param map 
     */
    public DsParts(Map<String, Object> map) {
        this.key = (String) map.get("key");
        this.productNo = (String) map.get("ic");
        this.productName = (String) map.get("in");
        this.quantity = (Integer) map.get("qt");
        this.preProcess = (String) map.get("pr");
        this.location = (String) map.get("lo");
        this.personName = (String) map.get("personName");
        this.compDateTime = (String) map.get("ct");
    }

    /**
     * コンストラクタ
     * 
     * @param key 連番
     * @param productNo 品番
     * @param productName 品名
     * @param quantity 数量
     * @param preProcess 前区
     */
    public DsParts(String key, String productNo, String productName, Integer quantity, String preProcess) {
        this.key = key;
        this.productNo = productNo;
        this.productName = productName;
        this.quantity = quantity;
        this.preProcess = preProcess;
    }
    
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getPreProcess() {
        return preProcess;
    }

    public void setPreProcess(String preProcess) {
        this.preProcess = preProcess;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getCompDateTime() {
        return compDateTime;
    }

    public void setCompDateTime(String compDateTime) {
        this.compDateTime = compDateTime;
    }

    /**
     * オブジェクトのクローンを返す。
     * 
     * @return 
     */
    public DsParts clone() {
        return new DsParts(this.getKey(), this.getProductNo(), this.getProductName(), this.getQuantity(), this.getPreProcess());
    }

    /**
     * ハッシュ値を返す。
     * 
     * @return ハッシュ値
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.key);
        return hash;
    }

    /**
     * オブジェクトを比較する。
     * 
     * @param obj
     * @return 
     */
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
        final DsParts other = (DsParts) obj;
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        if (!Objects.equals(this.productNo, other.productNo)) {
            return false;
        }
        if (!Objects.equals(this.productName, other.productName)) {
            return false;
        }
        if (!Objects.equals(this.preProcess, other.preProcess)) {
            return false;
        }
        return Objects.equals(this.quantity, other.quantity);
    }


}
