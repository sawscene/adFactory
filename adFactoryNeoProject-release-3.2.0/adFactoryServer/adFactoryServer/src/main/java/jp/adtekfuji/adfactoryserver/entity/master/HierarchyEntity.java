/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.master;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.HierarchyTypeEnum;

/**
 * 階層マスタ
 *
 * @author nar-nakamura
 */
@Entity
@Table(name = "mst_hierarchy")
@XmlRootElement(name = "hierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // 階層種別・階層名を指定して、件数を取得する。(追加時の階層名重複チェック)
    @NamedQuery(name = "HierarchyEntity.checkAddByHierarchyName", query = "SELECT COUNT(h.hierarchyId) FROM HierarchyEntity h WHERE h.hierarchyType = :hierarchyType AND h.hierarchyName = :hierarchyName"),
    // 階層種別・階層名・階層IDを指定して、件数を取得する。(更新時の階層名重複チェック)
    @NamedQuery(name = "HierarchyEntity.checkUpdateByHierarchyName", query = "SELECT COUNT(h.hierarchyId) FROM HierarchyEntity h WHERE h.hierarchyType = :hierarchyType AND h.hierarchyName = :hierarchyName AND h.hierarchyId != :hierarchyId"),

    // 階層名を指定して、階層情報を取得する。
    @NamedQuery(name = "HierarchyEntity.findByHierarchyName", query = "SELECT h FROM HierarchyEntity h WHERE h.hierarchyType = :hierarchyType AND h.hierarchyName = :hierarchyName"),

    // 階層IDを指定して、階層種別を取得する。
    @NamedQuery(name = "HierarchyEntity.getTypeById", query = "SELECT h.hierarchyType FROM HierarchyEntity h WHERE h.hierarchyId = :hierarchyId"),

    // 階層IDを指定して、子階層の階層情報の件数を取得する。
    @NamedQuery(name = "HierarchyEntity.countChild", query = "SELECT COUNT(h.hierarchyId) FROM HierarchyEntity h WHERE h.hierarchyType = :hierarchyType AND h.parentHierarchyId = :hierarchyId"),
    // 階層IDを指定して、子階層の階層情報一覧を取得する。
    @NamedQuery(name = "HierarchyEntity.findChild", query = "SELECT h FROM HierarchyEntity h WHERE h.hierarchyType = :hierarchyType AND h.parentHierarchyId = :hierarchyId ORDER BY h.hierarchyName, h.hierarchyId"),
    // 階層ID・ユーザーID(組織ID)一覧を指定して、アクセス可能な子階層の階層情報の件数を取得する。
    @NamedQuery(name = "HierarchyEntity.countChildByUserId", query = "SELECT COUNT(DISTINCT(h.hierarchyId)) FROM HierarchyEntity h LEFT JOIN AccessHierarchyEntity a ON a.typeId = :type AND a.hierarchyId = h.hierarchyId WHERE h.hierarchyType = :hierarchyType AND h.parentHierarchyId = :hierarchyId AND (a.organizationId IS NULL OR a.organizationId IN :ancestors)"),
    // 階層ID・ユーザーID(組織ID)一覧を指定して、アクセス可能な子階層の階層情報一覧を取得する。
    @NamedQuery(name = "HierarchyEntity.findChildByUserId", query = "SELECT DISTINCT(h) FROM HierarchyEntity h LEFT JOIN AccessHierarchyEntity a ON a.typeId = :type AND a.hierarchyId = h.hierarchyId WHERE h.hierarchyType = :hierarchyType AND h.parentHierarchyId = :hierarchyId AND (a.organizationId IS NULL OR a.organizationId IN :ancestors) ORDER BY h.hierarchyName, h.hierarchyId"),

    // 階層IDを指定して、親階層IDを取得する。
    @NamedQuery(name = "HierarchyEntity.findParentId", query = "SELECT h.parentHierarchyId FROM HierarchyEntity h WHERE h.hierarchyId = :hierarchyId"),
})
public class HierarchyEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "hierarchy_id")
    private Long hierarchyId;// 階層ID

    @Basic(optional = false)
    //@NotNull
    @Column(name = "hierarchy_type")
    private HierarchyTypeEnum hierarchyType;// 階層種別

    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 256)
    @Column(name = "hierarchy_name")
    private String hierarchyName;// 階層名

    @Column(name = "parent_hierarchy_id")
    @XmlElement(name="parentId")
    private Long parentHierarchyId;// 親階層ID

    //@NotNull
    @Column(name = "ver_info")
    @Version
    private Integer verInfo = 1;// 排他用バーション

    @Transient
    private Long childCount = 0L;// 子階層数

    /**
     * コンストラクタ
     */
    public HierarchyEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param hierarchyType 階層種別
     * @param parentHierarchyId 親階層ID
     * @param hierarchyName 階層名
     */
    public HierarchyEntity(HierarchyTypeEnum hierarchyType, Long parentHierarchyId, String hierarchyName) {
        this.hierarchyType = hierarchyType;
        this.parentHierarchyId = parentHierarchyId;
        this.hierarchyName = hierarchyName;
    }

    /**
     * 階層マスタを継承しているクラスに変換する。
     *
     * @param <T> 
     * @param destClass 階層マスタを継承しているクラス
     * @return 階層マスタを継承しているクラスの階層情報
     * @throws Exception 
     */
    public <T extends HierarchyEntity> T downcast(Class<T> destClass) throws Exception {
        T obj = destClass.newInstance();
        obj.setHierarchyId(this.hierarchyId);
        obj.setHierarchyType(this.hierarchyType);
        obj.setHierarchyName(this.hierarchyName);
        obj.setParentHierarchyId(this.parentHierarchyId);
        obj.setVerInfo(this.verInfo);
        obj.setChildCount(this.childCount);
        return obj;
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
     * 階層種別を取得する。
     *
     * @return 階層種別
     */
    public HierarchyTypeEnum getHierarchyType() {
        return this.hierarchyType;
    }

    /**
     * 階層種別を設定する。
     *
     * @param hierarchyType 階層種別
     */
    public void setHierarchyType(HierarchyTypeEnum hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    /**
     * 階層名を取得する。
     *
     * @return 階層名
     */
    public String getHierarchyName() {
        return this.hierarchyName;
    }

    /**
     * 階層名を設定する。
     *
     * @param hierarchyName 階層名
     */
    public void setHierarchyName(String hierarchyName) {
        this.hierarchyName = hierarchyName;
    }

    /**
     * 親階層IDを取得する。
     *
     * @return 親階層ID
     */
    public Long getParentHierarchyId() {
        return this.parentHierarchyId;
    }

    /**
     * 親階層IDを設定する。
     *
     * @param parentHierarchyId 親階層ID
     */
    public void setParentHierarchyId(Long parentHierarchyId) {
        this.parentHierarchyId = parentHierarchyId;
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

    /**
     * 子階層数を取得する。
     *
     * @return 子階層数
     */
    public Long getChildCount() {
        return this.childCount;
    }

    /**
     * 子階層数を設定する。
     *
     * @param childCount 子階層数
     */
    public void setChildCount(Long childCount) {
        this.childCount = childCount;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.hierarchyId);
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
        final HierarchyEntity other = (HierarchyEntity) obj;
        if (!Objects.equals(this.hierarchyId, other.hierarchyId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("HierarchyEntity{")
                .append("hierarchyId=").append(this.hierarchyId)
                .append(", ")
                .append("hierarchyType=").append(this.hierarchyType)
                .append(", ")
                .append("hierarchyName=").append(this.hierarchyName)
                .append(", ")
                .append("parentHierarchyId=").append(this.parentHierarchyId)
                .append(", ")
                .append("verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
