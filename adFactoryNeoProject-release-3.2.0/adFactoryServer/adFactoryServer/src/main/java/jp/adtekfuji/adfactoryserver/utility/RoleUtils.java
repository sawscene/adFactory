/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.utility;

import java.util.List;
import java.util.Objects;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jp.adtekfuji.adFactory.enumerate.AuthorityEnum;
import jp.adtekfuji.adfactoryserver.entity.master.RoleAuthorityEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;

/**
 * 役割権限ユーティリティ
 *
 * @author nar-nakamura
 */
public class RoleUtils {

    /**
     * 指定した組織にリソース編集権限があるかどうかを取得する。
     *
     * @param em
     * @param organization 組織情報
     * @return リソース編集権限(true:あり, false:なし)
     */
    public static boolean checkResourceEditRole(EntityManager em, OrganizationEntity organization) {
        if (Objects.equals(organization.getAuthorityType(), AuthorityEnum.SYSTEM_ADMIN)) {
            return true;
        }

        List<RoleAuthorityEntity> roles = findRoleByOrganizationId(em, organization.getOrganizationId());

        return roles.stream().anyMatch(p -> p.getResourceEdit());
    }

    /**
     * 指定した組織に承認権限があるかどうかを取得する。
     *
     * @param em
     * @param organization 組織情報
     * @return 承認権限(true:あり, false:なし)
     */
    public static boolean checkApproveRole(EntityManager em, OrganizationEntity organization) {
        if (Objects.equals(organization.getAuthorityType(), AuthorityEnum.SYSTEM_ADMIN)) {
            return true;
        }

        List<RoleAuthorityEntity> roles = findRoleByOrganizationId(em, organization.getOrganizationId());

        return roles.stream().anyMatch(p -> p.getApprove());
    }

    /**
     * 組織IDを指定して、役割権限情報一覧を取得する。
     *
     * @param em
     * @param organizationId 組織ID
     * @return 役割権限情報一覧
     */
    private static List<RoleAuthorityEntity> findRoleByOrganizationId(EntityManager em, long organizationId) {
        TypedQuery<RoleAuthorityEntity> query = em.createNamedQuery("RoleAuthorityEntity.findByOrganizationId", RoleAuthorityEntity.class);
        query.setParameter("organizationId", organizationId);
        return query.getResultList();
    }
}
