/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import jp.adtekfuji.adFactory.utility.DateAdapter;

/**
 * ソフトウェア更新情報
 *
 * @author s-heya
 */
@XmlRootElement(name = "softwareupdate")
@XmlAccessorType(XmlAccessType.FIELD)
public class SoftwareUpdateEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date date;
    private final long retryDownload = 3;
    @XmlElementWrapper(name = "packages")
    @XmlElement(name = "package")
    private List<PackageEntity> packageCollection = null;

    public SoftwareUpdateEntity() {
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<PackageEntity> getPackageCollection() {
        if (Objects.isNull(this.packageCollection)) {
            this.packageCollection = new ArrayList<>();
        }
        return this.packageCollection;
    }

    public void setPackageCollection(List<PackageEntity> packageCollection) {
        this.packageCollection = packageCollection;
    }

    public long getRetryDownload() {
        return retryDownload;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.date);
        hash = 97 * hash + Objects.hashCode(this.retryDownload);
        hash = 97 * hash + Objects.hashCode(this.packageCollection);
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
        final SoftwareUpdateEntity other = (SoftwareUpdateEntity) obj;
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        if (!Objects.equals(this.retryDownload, other.retryDownload)) {
            return false;
        }
        return Objects.equals(this.packageCollection, other.packageCollection);
    }

    @Override
    public String toString() {
        return "SoftwareUpdateEntity{" + "date=" + date + ", retryDownload=" + retryDownload + ", packageCollection=" + packageCollection + '}';
    }
}
