/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.organization;

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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "mst_authentication_info")
@XmlRootElement(name = "authenticationInfo")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "AuthenticationInfoEntity.findAll", query = "SELECT a FROM AuthenticationInfoEntity a"),
    @NamedQuery(name = "AuthenticationInfoEntity.findByAuthenticationId", query = "SELECT a FROM AuthenticationInfoEntity a WHERE a.authenticationId = :authenticationId"),
    @NamedQuery(name = "AuthenticationInfoEntity.findByAuthenticationData", query = "SELECT a FROM AuthenticationInfoEntity a WHERE a.authenticationData = :authenticationData"),
    @NamedQuery(name = "AuthenticationInfoEntity.findByAuthenticationType", query = "SELECT a FROM AuthenticationInfoEntity a WHERE a.authenticationType = :authenticationType"),
    @NamedQuery(name = "AuthenticationInfoEntity.findByFkMastgerId", query = "SELECT a FROM AuthenticationInfoEntity a WHERE a.fkMastgerId = :fkMastgerId"),
    @NamedQuery(name = "AuthenticationInfoEntity.findByValidityPeriod", query = "SELECT a FROM AuthenticationInfoEntity a WHERE a.validityPeriod = :validityPeriod"),
    @NamedQuery(name = "AuthenticationInfoEntity.findByUseLock", query = "SELECT a FROM AuthenticationInfoEntity a WHERE a.useLock = :useLock")})
public class AuthenticationInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "authentication_id")
    private Long authenticationId;
    @Size(max = 2147483647)
    @Column(name = "authentication_data")
    private String authenticationData;
    @Size(max = 128)
    @Column(name = "authentication_type")
    private String authenticationType;
    @Basic(optional = false)
    @Column(name = "fk_mastger_id")
    private int fkMastgerId;
    @Column(name = "validity_period")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validityPeriod;
    @Column(name = "use_lock")
    private Boolean useLock;

    public AuthenticationInfoEntity() {
    }

    public AuthenticationInfoEntity(Long authenticationId) {
        this.authenticationId = authenticationId;
    }

    public AuthenticationInfoEntity(Long authenticationId, int fkMastgerId) {
        this.authenticationId = authenticationId;
        this.fkMastgerId = fkMastgerId;
    }

    public Long getAuthenticationId() {
        return authenticationId;
    }

    public void setAuthenticationId(Long authenticationId) {
        this.authenticationId = authenticationId;
    }

    public String getAuthenticationData() {
        return authenticationData;
    }

    public void setAuthenticationData(String authenticationData) {
        this.authenticationData = authenticationData;
    }

    public String getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    public int getFkMastgerId() {
        return fkMastgerId;
    }

    public void setFkMastgerId(int fkMastgerId) {
        this.fkMastgerId = fkMastgerId;
    }

    public Date getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(Date validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public Boolean getUseLock() {
        return useLock;
    }

    public void setUseLock(Boolean useLock) {
        this.useLock = useLock;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (authenticationId != null ? authenticationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AuthenticationInfoEntity)) {
            return false;
        }
        AuthenticationInfoEntity other = (AuthenticationInfoEntity) object;
        if ((this.authenticationId == null && other.authenticationId != null) || (this.authenticationId != null && !this.authenticationId.equals(other.authenticationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AuthenticationInfoEntity{" + "authenticationId=" + authenticationId + ", authenticationData=" + authenticationData + ", authenticationType=" + authenticationType + ", fkMastgerId=" + fkMastgerId + ", validityPeriod=" + validityPeriod + ", useLock=" + useLock + '}';
    }

}
