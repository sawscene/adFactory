/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.login;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * ログイン要求情報.
 *
 * @author ke.yokoi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "organizationLoginRequest")
public class OrganizationLoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlEnum(String.class)
    public enum LoginType {

        PASSWORD,
        BARCODE,
        NFC_CARD,
        LDAP
    }

    @XmlElement(required = true)
    private LoginType loginType;
    @XmlElement
    private String loginId;
    @XmlElement
    private String authData;

    public OrganizationLoginRequest() {
    }

    private OrganizationLoginRequest(LoginType loginType, String loginId, String authData) {
        this.loginType = loginType;
        this.loginId = loginId;
        this.authData = authData;
    }

    public static OrganizationLoginRequest passwordType(String loginId, String password) {
        return new OrganizationLoginRequest(LoginType.PASSWORD, loginId, password);
    }

    public static OrganizationLoginRequest barcodeType(String authData) {
        return new OrganizationLoginRequest(LoginType.BARCODE, null, authData);
    }

    public static OrganizationLoginRequest nfcType(String authData) {
        return new OrganizationLoginRequest(LoginType.NFC_CARD, null, authData);
    }
    
    public static OrganizationLoginRequest ldapType(String loginId, String password) {
        return new OrganizationLoginRequest(LoginType.LDAP, loginId, password);
    }

    public LoginType getLoginType() {
        return loginType;
    }

    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getAuthData() {
        return authData;
    }

    public void setAuthData(String authData) {
        this.authData = authData;
    }

    @Override
    public int hashCode() {
        int hash = 3;
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
        final OrganizationLoginRequest other = (OrganizationLoginRequest) obj;
        return true;
    }

    @Override
    public String toString() {
        return "OrganizationLoginRequest{" + "loginType=" + loginType + '}';
    }

}
