/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.login;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;

/**
 * ログイン結果情報.
 *
 * @author ke.yokoi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "organizationLoginResult")
public class OrganizationLoginResult implements Serializable {

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    private Boolean isSuccess = Boolean.FALSE;
    @XmlElement
    private ServerErrorTypeEnum errorType;
    @XmlElement(required = true)
    private Long organizationId;
    @XmlElement
    private OrganizationInfoEntity organizationInfo;
    @XmlElementWrapper(name = "roleAuthorities")
    @XmlElement(name = "roleAuthority")
    private List<String> roleAuth;

    @XmlElement(required = false)
    private String message = "";

    public OrganizationLoginResult() {
    }

    private OrganizationLoginResult(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    private OrganizationLoginResult(Boolean isSuccess, List<String> roleAuth) {
        this.isSuccess = isSuccess;
        this.roleAuth = new ArrayList<>();
        if (Objects.nonNull(roleAuth)) {
            for (String roleAuthority : roleAuth) {
                this.roleAuth.add(roleAuthority);
            }
        }
    }

    public static OrganizationLoginResult success(Long organizationId) {
        return new OrganizationLoginResult(true).errorType(ServerErrorTypeEnum.SUCCESS).organizationId(organizationId);
    }

    public static OrganizationLoginResult success(Long organizationId, List<String> roleAuth) {
        return new OrganizationLoginResult(true, roleAuth).errorType(ServerErrorTypeEnum.SUCCESS).organizationId(organizationId);
    }

    public static OrganizationLoginResult failed(ServerErrorTypeEnum errorType) {
        return new OrganizationLoginResult(false).errorType(errorType);
    }

    public OrganizationLoginResult errorType(ServerErrorTypeEnum errorType) {
        this.errorType = errorType;
        return this;
    }

    public OrganizationLoginResult organizationId(Long organizationId) {
        this.organizationId = organizationId;
        return this;
    }

    public OrganizationLoginResult organizationInfo(OrganizationInfoEntity organizationInfo) {
        this.organizationInfo = organizationInfo;
        return this;
    }

    public Boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public ServerErrorTypeEnum getErrorType() {
        return errorType;
    }

    public void setErrorType(ServerErrorTypeEnum errorType) {
        this.errorType = errorType;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public OrganizationInfoEntity getOrganizationInfo() {
        return organizationInfo;
    }

    public void setOrganizationInfo(OrganizationInfoEntity organizationInfo) {
        this.organizationInfo = organizationInfo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 機能権限一覧を取得する。
     *
     * @return
     */
    public List<String> getRoleAuth() {
        return roleAuth;
    }

    /**
     * 機能権限一覧を取得する
     *
     * @param roleAuth
     */
    public void setRoleAuth(List<String> roleAuth) {
        this.roleAuth = roleAuth;
    }

    @Override
    public int hashCode() {
        int hash = 7;
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
        final OrganizationLoginResult other = (OrganizationLoginResult) obj;
        return true;
    }

    @Override
    public String toString() {
        return "OrganizationLoginResult{" + "isSuccess=" + isSuccess + ", errorType=" + errorType + ", organizationId=" + organizationId + '}';
    }

}
