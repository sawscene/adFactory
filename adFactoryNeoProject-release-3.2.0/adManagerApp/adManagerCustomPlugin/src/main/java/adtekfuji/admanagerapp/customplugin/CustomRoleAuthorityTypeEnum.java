/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.customplugin;

import jp.adtekfuji.adFactory.enumerate.RoleAuthorityType;

/**
 * カスタム機能権限項目
 *
 * @author s-heya
 */
public enum CustomRoleAuthorityTypeEnum implements RoleAuthorityType {
    CUSTOM_FEATURE1("CUSTOM_FEATURE1", "key.Copy"),
    CUSTOM_FEATURE2("CUSTOM_FEATURE2", "key.Delete");

    private final String name;
    private final String resourceKey;

    /**
     * コンストラクタ
     *
     * @param name
     * @param resourceKey
     */
    private CustomRoleAuthorityTypeEnum(String name, String resourceKey) {
        this.name = name;
        this.resourceKey = resourceKey;
    }

    /**
     * 機能権限名を取得する、
     *
     * @return
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * リソースキーを取得する。
     *
     * @return
     */
    @Override
    public String getResourceKey() {
        return this.resourceKey;
    }
}
