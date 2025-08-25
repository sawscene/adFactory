/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.organization;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 認証情報
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "mst_authentication_info")
@XmlRootElement(name = "authenticationInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class AuthenticationInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "authentication_id")
    private Long authenticationId;// 認証ID

    @Basic(optional = false)
    @Column(name = "organization_id")
    @XmlElement(name = "fkMastgerId")
    private int organizationId;// 組織ID

    @Size(max = 128)
    @Column(name = "authentication_type")
    private String authenticationType;// 種別

    @Size(max = 2147483647)
    @Column(name = "authentication_data")
    private String authenticationData;// 認証情報

    @Column(name = "validity_period")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validityPeriod;// 有効期限

    @Column(name = "use_lock")
    private Boolean useLock;// 使用ロック

    //@NotNull
    @Column(name = "ver_info")
    @Version
    private Integer verInfo = 1;// 排他用バーション

    /**
     * コンストラクタ
     */
    public AuthenticationInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param authenticationId 認証ID
     */
    public AuthenticationInfoEntity(Long authenticationId) {
        this.authenticationId = authenticationId;
    }

    /**
     * コンストラクタ
     *
     * @param authenticationId 認証ID
     * @param organizationId 組織ID
     */
    public AuthenticationInfoEntity(Long authenticationId, int organizationId) {
        this.authenticationId = authenticationId;
        this.organizationId = organizationId;
    }

    /**
     * 認証IDを取得する。
     *
     * @return 認証ID
     */
    public Long getAuthenticationId() {
        return this.authenticationId;
    }

    /**
     * 認証IDを設定する。
     *
     * @param authenticationId 認証ID
     */
    public void setAuthenticationId(Long authenticationId) {
        this.authenticationId = authenticationId;
    }

    /**
     * 組織IDを取得する。
     *
     * @return 組織ID
     */
    public int getOrganizationId() {
        return this.organizationId;
    }

    /**
     * 組織IDを設定する。
     *
     * @param organizationId 組織ID
     */
    public void setOrganizationId(int organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * 種別を取得する。
     *
     * @return 種別
     */
    public String getAuthenticationType() {
        return this.authenticationType;
    }

    /**
     * 種別を設定する。
     *
     * @param authenticationType 種別
     */
    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    /**
     * 認証情報を取得する。
     *
     * @return 認証情報
     */
    public String getAuthenticationData() {
        return this.authenticationData;
    }

    /**
     * 認証情報を設定する。
     *
     * @param authenticationData 認証情報
     */
    public void setAuthenticationData(String authenticationData) {
        this.authenticationData = authenticationData;
    }

    /**
     * 有効期限を取得する。
     *
     * @return 有効期限
     */
    public Date getValidityPeriod() {
        return this.validityPeriod;
    }

    /**
     * 有効期限を設定する。
     *
     * @param validityPeriod 有効期限
     */
    public void setValidityPeriod(Date validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    /**
     * 使用ロックを取得する。
     *
     * @return 使用ロック
     */
    public Boolean getUseLock() {
        return this.useLock;
    }

    /**
     * 使用ロックを設定する。
     *
     * @param useLock 使用ロック
     */
    public void setUseLock(Boolean useLock) {
        this.useLock = useLock;
    }

    /**
     * 排他用バーションを取得する。
     *
     * @return 排他用バーション
     */
    public Integer getVerInfo() {
        return this.verInfo;
    }

    /**
     * 排他用バーションを設定する。
     *
     * @param verInfo 排他用バーション
     */
    public void setVerInfo(Integer verInfo) {
        this.verInfo = verInfo;
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
        return new StringBuilder("AuthenticationInfoEntity{")
                .append(",authenticationId=").append(this.authenticationId)
                .append(",organizationId=").append(this.organizationId)
                .append(",authenticationType=").append(this.authenticationType)
                .append(",authenticationData=").append(this.authenticationData)
                .append(",validityPeriod=").append(this.validityPeriod)
                .append(",useLock=").append(this.useLock)
                .append(",verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
