/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.TargetEnum;

/**
 * パッケージ情報
 *
 * @author s-heya
 */
@XmlRootElement(name = "package")
@XmlAccessorType(XmlAccessType.FIELD)
public class PackageEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(required = true)
    private String name;
    @XmlElement(required = true)
    private String version;
    @XmlElement(required = true)
    private String path;
    private String params;
    private String see;
    @XmlElement(name = "force", defaultValue = "false")
    private Boolean isForce = false;
    @XmlElement(name = "finish", defaultValue = "false")
    private Boolean isFinish = false;
    @XmlElementWrapper(name = "classes")
    @XmlElement(name = "class")
    private List<String> classCollection = null;
    @XmlElementWrapper(name = "targets")
    @XmlElement(name = "target")
    private List<TargetEnum> targetCollection = null;
    @XmlElement(required = true)
    private long size = 0;

    public PackageEntity() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getParams() {
        return this.params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getSee() {
        return this.see;
    }

    public void setSee(String see) {
        this.see = see;
    }

    public Boolean isForce() {
        return this.isForce;
    }

    public void setIsForce(Boolean isForce) {
        this.isForce = isForce;
    }

    public Boolean isFinish() {
        return this.isFinish;
    }

    public void setIsFinish(Boolean isFinish) {
        this.isFinish = isFinish;
    }

    public List<String> getClassCollection() {
        if (Objects.isNull(this.classCollection)) {
            this.classCollection = new ArrayList<>();
        }
        return this.classCollection;
    }

    public void setClassCollection(List<String> classCollection) {
        this.classCollection = classCollection;
    }

    public List<TargetEnum> getTargetCollection() {
        if (Objects.isNull(this.targetCollection)) {
            this.targetCollection = new ArrayList<>();
        }
        return this.targetCollection;
    }

    public void setTargetCollection(List<TargetEnum> targetCollection) {
        this.targetCollection = targetCollection;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.name);
        hash = 37 * hash + Objects.hashCode(this.version);
        hash = 37 * hash + Objects.hashCode(this.path);
        hash = 37 * hash + Objects.hashCode(this.params);
        hash = 37 * hash + Objects.hashCode(this.see);
        hash = 37 * hash + Objects.hashCode(this.isForce);
        hash = 37 * hash + Objects.hashCode(this.isFinish);
        hash = 37 * hash + Objects.hashCode(this.classCollection);
        hash = 37 * hash + Objects.hashCode(this.targetCollection);
        hash = 37 * hash + (int) (this.size ^ (this.size >>> 32));
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
        final PackageEntity other = (PackageEntity) obj;
        if (this.size != other.size) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.version, other.version)) {
            return false;
        }
        if (!Objects.equals(this.path, other.path)) {
            return false;
        }
        if (!Objects.equals(this.params, other.params)) {
            return false;
        }
        if (!Objects.equals(this.see, other.see)) {
            return false;
        }
        if (!Objects.equals(this.isForce, other.isForce)) {
            return false;
        }
        if (!Objects.equals(this.isFinish, other.isFinish)) {
            return false;
        }
        if (!Objects.equals(this.classCollection, other.classCollection)) {
            return false;
        }
        return Objects.equals(this.targetCollection, other.targetCollection);
    }

    @Override
    public String toString() {
        return "PackageEntity{" + "name=" + name + ", version=" + version + ", path=" + path + ", params=" + params
                + ", see=" + see + ", isForce=" + isForce + ", isFinish=" + isFinish + ", classCollection=" + classCollection + ", targetCollection=" + targetCollection + ", size=" + size + '}';
    }
}
