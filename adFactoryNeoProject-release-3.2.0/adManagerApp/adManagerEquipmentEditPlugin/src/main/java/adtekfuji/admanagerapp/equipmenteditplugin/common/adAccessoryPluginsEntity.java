/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.equipmenteditplugin.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * プラグイン情報エンティティクラス
 *
 * @author s-heya
 */
@XmlRootElement(name = "adaccessoryplugins")
@XmlAccessorType(XmlAccessType.FIELD)
public class adAccessoryPluginsEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElementWrapper(name = "plugins")
    @XmlElement(name = "name")
    private List<String> pluginNames = null;

    public adAccessoryPluginsEntity() {
    }

    public List<String> getPluginNames() {
        if (Objects.isNull(this.pluginNames)) {
            this.pluginNames = new ArrayList<>();
        }
        return this.pluginNames;
    }

    @Override
    public int hashCode() {
        int hash = 3;
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
        final adAccessoryPluginsEntity other = (adAccessoryPluginsEntity) obj;
        if (!Objects.equals(this.pluginNames, other.pluginNames)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AccessoryPluginInfoEntity{" + "pluginNameCollection=" + pluginNames + '}';
    }
}
