/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.selectcompo;

import java.util.List;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;

/**
 *
 * @author j.min
 */
public class AccessAuthSettingEntity {
    
    private String hierarchyName;
    private List<OrganizationInfoEntity> authOrganizations;

    public AccessAuthSettingEntity(String hierarchyName, List<OrganizationInfoEntity> authOrganizations) {
        this.hierarchyName = hierarchyName;
        this.authOrganizations = authOrganizations;
    }

    public String getHierarchyName() {
        return hierarchyName;
    }

    public void setHierarchyName(String hierarchyName) {
        this.hierarchyName = hierarchyName;
    }

    public List<OrganizationInfoEntity> getAuthOrganizations() {
        return authOrganizations;
    }

    public void setAuthOrganizations(List<OrganizationInfoEntity> authOrganizations) {
        this.authOrganizations = authOrganizations;
    }

}
