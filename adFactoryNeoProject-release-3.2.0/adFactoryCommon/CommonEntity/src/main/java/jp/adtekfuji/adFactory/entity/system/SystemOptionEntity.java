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
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ke.yokoi
 */
@XmlRootElement(name = "systemOption")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class SystemOptionEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    private String optionName;
    private Boolean enable;

    public SystemOptionEntity() {
    }

    public SystemOptionEntity(String optionName, Boolean enable) {
        this.optionName = optionName;
        this.enable = enable;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.optionName);
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
        final SystemOptionEntity other = (SystemOptionEntity) obj;
        if (!Objects.equals(this.optionName, other.optionName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SystemOptionEntity{" + "optionName=" + optionName + ", enable=" + enable + '}';
    }

}
