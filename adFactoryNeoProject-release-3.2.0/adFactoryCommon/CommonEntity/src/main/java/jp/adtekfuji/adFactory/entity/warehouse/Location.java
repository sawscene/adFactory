/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.warehouse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 棚情報
 * 
 * @author s-heya
 */
@XmlRootElement(name = "locInfo")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown=true)
public class Location implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @XmlElement
    @JsonProperty("area")
    private String areaName;
    @XmlElement
    @JsonProperty("loc")
    private String locationNo;

    /**
     * コンストラクタ
     */
    public Location() {
    }

    /**
     * コンストラクタ
     * 
     * @param areaName 区画名
     * @param locationNo 棚番号
     */
    public Location(String areaName, String locationNo) {
        this.areaName = areaName;
        this.locationNo = locationNo;
    }

    /**
     * 区画名を取得する。
     * 
     * @return 区画名
     */
    public String getAreaName() {
        return areaName;
    }

    /**
     * 区画名を設定する。
     * 
     * @param areaName 区画名
     */
    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    /**
     * 棚番号を取得する。
     * 
     * @return 棚番号
     */
    public String getLocationNo() {
        return locationNo;
    }

    /**
     * 棚番号を設定する。
     * 
     * @param locationNo 棚番号
     */
    public void setLocationNo(String locationNo) {
        this.locationNo = locationNo;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.areaName);
        hash = 29 * hash + Objects.hashCode(this.locationNo);
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
        final Location other = (Location) obj;
        if (!Objects.equals(this.areaName, other.areaName)) {
            return false;
        }
        return Objects.equals(this.locationNo, other.locationNo);
    }
    
    @Override
    public String toString() {
        return new StringBuilder("Location{")
            .append("areaName=").append(this.areaName)
            .append(", locationNo=").append(this.locationNo)
            .append("}")
            .toString();
    }
}
