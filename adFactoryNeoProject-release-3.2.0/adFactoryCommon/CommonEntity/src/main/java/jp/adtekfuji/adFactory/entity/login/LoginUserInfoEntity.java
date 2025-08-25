/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.login;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jp.adtekfuji.adFactory.enumerate.AuthorityEnum;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityType;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;

/**
 * ログインユーザー情報 シングルトンクラス
 *
 * @author e-mori
 */
public class LoginUserInfoEntity {

    private static final LoginUserInfoEntity instance = new LoginUserInfoEntity();
    private Long id;
    private String name;
    private String loginId;
    private Date loginTime;
    private Date logoutTime;
    private AuthorityEnum authorityType;
    // 機能権限名
    private List<String> roleAuthCollection = new ArrayList<>();

    // アクセス権限(全て)
    public static final String ADMIN_LOGIN_ID = "admin";

    private LoginUserInfoEntity() {
    }

    /**
     * インスタンス取得
     *
     * @return ログインユーザー情報のインスタンス
     */
    public static LoginUserInfoEntity getInstance() {
        return instance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String _loginId) {
        this.loginId = _loginId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Date getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(Date logoutTime) {
        this.logoutTime = logoutTime;
    }

    public AuthorityEnum getAuthorityType() {
        return authorityType;
    }

    public void setAuthorityType(AuthorityEnum authorityType) {
        this.authorityType = authorityType;
    }

    public List<String> getRoleAuthCollection() {
        return roleAuthCollection;
    }

    public void setRoleAuthCollection(List<String> roleAuthCollection) {
        this.roleAuthCollection = roleAuthCollection;
    }

    public boolean checkRoleAuthority(RoleAuthorityType type) {
        if (authorityType == AuthorityEnum.SYSTEM_ADMIN) {
            return true;
        }
        return roleAuthCollection.contains(type.getName());
    }

    public boolean checkRoleAuthority(RoleAuthorityTypeEnum type) {
        if (authorityType == AuthorityEnum.SYSTEM_ADMIN) {
            return true;
        }
        return roleAuthCollection.contains(type.getName());
    }
}
