/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.access;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.AccessHierarchyTypeEnum;

/**
 * 階層アクセス権情報
 *
 * @author j.min
 */
@Entity
@Table(name = "trn_access_hierarchy")
@XmlRootElement(name = "accessHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // 階層種別ID・階層IDを指定して、階層アクセス権情報一覧を取得する。
    @NamedQuery(name = "AccessHierarchyEntity.find", query = "SELECT a FROM AccessHierarchyEntity a WHERE a.typeId = :type AND a.hierarchyId = :id"),
    // 階層種別ID・階層IDを指定して、階層アクセス権情報の件数を取得する。
    @NamedQuery(name = "AccessHierarchyEntity.count", query = "SELECT COUNT(a) FROM AccessHierarchyEntity a WHERE a.typeId = :type AND a.hierarchyId = :id"),
    // 階層種別ID・階層ID・組織IDを指定して、階層アクセス権情報の件数を取得する。
    @NamedQuery(name = "AccessHierarchyEntity.check", query = "SELECT COUNT(a) FROM AccessHierarchyEntity a WHERE a.typeId = :type AND a.hierarchyId = :id AND a.organizationId = :organizationId"),
    // 階層種別ID・階層IDを指定して、階層アクセス権情報の組織ID一覧を取得する。
    @NamedQuery(name = "AccessHierarchyEntity.getOrganizationIds", query = "SELECT a.organizationId FROM AccessHierarchyEntity a WHERE a.typeId = :type AND a.hierarchyId = :id"),

    // 階層種別ID・階層IDを指定して、階層アクセス権情報を削除する。
    @NamedQuery(name = "AccessHierarchyEntity.removeByTypeAndId", query = "DELETE FROM AccessHierarchyEntity a WHERE a.typeId = :type AND a.hierarchyId = :id"),
    // 階層種別ID・階層ID・組織ID一覧を指定して、階層アクセス権情報を削除する。
    @NamedQuery(name = "AccessHierarchyEntity.removeDatas", query = "DELETE FROM AccessHierarchyEntity a WHERE a.typeId = :type AND a.hierarchyId = :id AND a.organizationId IN :organizationIds"),
})
public class AccessHierarchyEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "type_id")
    private AccessHierarchyTypeEnum typeId;// 階層種別ID

    @Id
    @Column(name = "hierarchy_id")
    @XmlElement(name = "fkHierarchyId")
    private Long hierarchyId;// 階層ID

    @Id
    @Column(name = "organization_id")
    @XmlElement(name = "fkOrganizationId")
    private Long organizationId;// 組織ID

    /**
     * コンストラクタ
     */
    public AccessHierarchyEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param typeId 階層種別ID
     * @param hierarchyId 階層ID
     * @param organizationId 組織ID
     */
    public AccessHierarchyEntity(AccessHierarchyTypeEnum typeId, Long hierarchyId, Long organizationId) {
        this.typeId = typeId;
        this.hierarchyId = hierarchyId;
        this.organizationId = organizationId;
    }

    /**
     * 階層種別IDを取得する。
     *
     * @return 階層種別ID
     */
    public AccessHierarchyTypeEnum getTypeId() {
        return typeId;
    }

    /**
     * 階層種別IDを設定する。
     *
     * @param typeId 階層種別ID
     */
    public void setTypeId(AccessHierarchyTypeEnum typeId) {
        this.typeId = typeId;
    }

    /**
     * 階層IDを取得する。
     *
     * @return 階層ID
     */
    public Long getHierarchyId() {
        return this.hierarchyId;
    }

    /**
     * 階層IDを設定する。
     *
     * @param hierarchyId 階層ID
     */
    public void setHierarchyId(Long hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    /**
     * 組織IDを取得する。
     *
     * @return 組織ID
     */
    public Long getOrganizationId() {
        return this.organizationId;
    }

    /**
     * 組織IDを設定する。
     *
     * @param organizationId 組織ID
     */
    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.typeId);
        hash = 89 * hash + Objects.hashCode(this.hierarchyId);
        hash = 89 * hash + Objects.hashCode(this.organizationId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AccessHierarchyEntity other = (AccessHierarchyEntity) obj;
        if (!Objects.equals(this.typeId, other.typeId)) {
            return false;
        }
        if (!Objects.equals(this.hierarchyId, other.hierarchyId)) {
            return false;
        }
        return Objects.equals(this.organizationId, other.organizationId);
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("AccessHierarchyEntity{")
                .append("typeId=").append(this.typeId)
                .append(", hierarchyId=").append(this.hierarchyId)
                .append(", organizationId=").append(this.organizationId)
                .append("}")
                .toString();
    }
}
