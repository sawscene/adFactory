/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.master;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "mst_breaktime")
@XmlRootElement(name = "breaktime")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "BreaktimeEntity.findAll", query = "SELECT b FROM BreaktimeEntity b"),
    @NamedQuery(name = "BreaktimeEntity.findByBreaktimeId", query = "SELECT b FROM BreaktimeEntity b WHERE b.breaktimeId IN :breaktimeIds ORDER BY b.starttime"),
    @NamedQuery(name = "BreaktimeEntity.findByName", query = "SELECT b FROM BreaktimeEntity b WHERE b.name = :name"),
    @NamedQuery(name = "BreaktimeEntity.findByStarttime", query = "SELECT b FROM BreaktimeEntity b WHERE b.starttime = :starttime"),
    @NamedQuery(name = "BreaktimeEntity.findByEndtime", query = "SELECT b FROM BreaktimeEntity b WHERE b.endtime = :endtime")})
public class BreaktimeEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "breaktime_id")
    private Long breaktimeId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Column(name = "starttime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date starttime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "endtime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endtime;

    public BreaktimeEntity() {
    }

    public BreaktimeEntity(String name, Date starttime, Date endtime) {
        this.name = name;
        this.starttime = starttime;
        this.endtime = endtime;
    }

    public Long getBreaktimeId() {
        return breaktimeId;
    }

    public void setBreaktimeId(Long breaktimeId) {
        this.breaktimeId = breaktimeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (breaktimeId != null ? breaktimeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BreaktimeEntity)) {
            return false;
        }
        BreaktimeEntity other = (BreaktimeEntity) object;
        if ((this.breaktimeId == null && other.breaktimeId != null) || (this.breaktimeId != null && !this.breaktimeId.equals(other.breaktimeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BreaktimeEntity{" + "breaktimeId=" + breaktimeId + ", name=" + name + ", starttime=" + starttime + ", endtime=" + endtime + '}';
    }

}
