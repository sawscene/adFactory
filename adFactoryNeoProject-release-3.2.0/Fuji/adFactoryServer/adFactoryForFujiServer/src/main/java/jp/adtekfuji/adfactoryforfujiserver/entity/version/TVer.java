/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.entity.version;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DBバージョンデータ
 * 
 * @author ek.mori
 */
@Entity
@Table(name = "t_ver")
@XmlRootElement(name = "tver")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "TVer.findAll", query = "SELECT t FROM TVer t"),
    @NamedQuery(name = "TVer.findBySid", query = "SELECT t FROM TVer t WHERE t.sid = :sid"),
    @NamedQuery(name = "TVer.findByVerno", query = "SELECT t FROM TVer t WHERE t.verno = :verno")})
public class TVer implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "sid")
    private Short sid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "verno")
    private String verno;

    public TVer() {
    }

    public TVer(Short sid) {
        this.sid = sid;
    }

    public TVer(Short sid, String verno) {
        this.sid = sid;
        this.verno = verno;
    }

    public Short getSid() {
        return sid;
    }

    public void setSid(Short sid) {
        this.sid = sid;
    }

    public String getVerno() {
        return verno;
    }

    public void setVerno(String verno) {
        this.verno = verno;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (sid != null ? sid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TVer)) {
            return false;
        }
        TVer other = (TVer) object;
        if ((this.sid == null && other.sid != null) || (this.sid != null && !this.sid.equals(other.sid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "service.TVer[ sid=" + sid + " ]";
    }
    
}
