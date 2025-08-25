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
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * 階層情報
 * 
 * @author s-heya
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {
  "depth",
  "id",
  "name",
  "organization"}))
@NamedNativeQueries({
    // 組織のアクセス権を取得
    @NamedNativeQuery(name = "Hierarchy.findOrganization", 
            query = "WITH RECURSIVE ancestor(depth, id, name, parent_id) AS (SELECT 0, o.organization_id, o.organization_name, o.parent_organization_id FROM mst_organization o WHERE o.organization_id = ?1 UNION DISTINCT SELECT ancestor.depth + 1, tree.organization_id, tree.organization_name, tree.parent_organization_id FROM ancestor, mst_organization tree WHERE ancestor.parent_id = tree.organization_id) SELECT o.depth, o.id, o.name, string_agg(to_char(p.organization_id, '999999999'), ',') AS organization FROM ancestor o INNER JOIN trn_access_hierarchy p ON p.type_id = 0 AND o.id = p.hierarchy_id GROUP BY o.depth, o.id, o.name ORDER BY depth DESC;", 
            resultClass = Hierarchy.class),
    // 設備のアクセス権を取得
    @NamedNativeQuery(name = "Hierarchy.findEquipment", 
            query = "WITH RECURSIVE ancestor(depth, id, name, parent_id) AS (SELECT 0, eq.equipment_id, eq.equipment_name, eq.parent_equipment_id FROM mst_equipment eq WHERE eq.equipment_id = ?1 UNION DISTINCT SELECT ancestor.depth + 1, tree.equipment_id, tree.equipment_name, tree.parent_equipment_id FROM ancestor, mst_equipment tree WHERE ancestor.parent_id = tree.equipment_id) SELECT o.depth, o.id, o.name, string_agg(to_char(p.organization_id, '999999999'), ',') AS organization FROM ancestor o INNER JOIN trn_access_hierarchy p ON p.type_id = 1 AND o.id = p.hierarchy_id GROUP BY o.depth, o.id, o.name ORDER BY depth DESC;", 
            resultClass = Hierarchy.class),
    // 階層をすべて取得 (1: 階層ID、2: 階層タイプ)
    @NamedNativeQuery(name = "Hierarchy.find", 
            query = "WITH RECURSIVE child(depth, id, name, parent_id) AS (SELECT 0, o.hierarchy_id, o.hierarchy_name, o.parent_hierarchy_id FROM mst_hierarchy o WHERE o.hierarchy_id = ANY(?1) UNION ALL SELECT child.depth + 1, tree.hierarchy_id, tree.hierarchy_name, tree.parent_hierarchy_id FROM mst_hierarchy tree, child WHERE tree.parent_hierarchy_id = child.id) SELECT c.depth, c.id, c.name, NULL AS organization FROM child c ORDER BY depth;",
            resultClass = Hierarchy.class),
    // アクセス可能な階層をすべて取得 (1: 階層ID、2: 階層タイプ、3: 組織ID)
    @NamedNativeQuery(name = "Hierarchy.findAccessOnly", 
            query = "WITH RECURSIVE child(depth, id, name, parent_id) AS (SELECT 0, o.hierarchy_id, o.hierarchy_name, o.parent_hierarchy_id FROM mst_hierarchy o WHERE o.hierarchy_id = ANY(?1) UNION ALL SELECT child.depth + 1, tree.hierarchy_id, tree.hierarchy_name, tree.parent_hierarchy_id FROM mst_hierarchy tree, child WHERE tree.parent_hierarchy_id = child.id) SELECT c.depth, c.id, c.name, NULL AS organization FROM child c WHERE NOT EXISTS (SELECT * FROM trn_access_hierarchy a WHERE a.type_id = ?2 AND c.id = a.hierarchy_id) OR EXISTS (SELECT * FROM trn_access_hierarchy a WHERE a.type_id = ?2 AND c.id = a.hierarchy_id AND a.organization_id = ANY(?3)) ORDER BY depth;",
            resultClass = Hierarchy.class),
})
public class Hierarchy  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "depth")
    private Long depth; 

    @Id
    @Column(name = "id")
    private Long hierarchyId;

    @Column(name = "name")
    private String name;

    @Column(name = "organization")
    private String organization;

    /**
     * 階層の深度を取得する。
     * 
     * @return 階層の深度
     */
    public Long getDepth() {
        return depth;
    }

    /**
     * 階層の深度を設定する。
     * 
     * @param depth 階層の深度
     */
    public void setDepth(Long depth) {
        this.depth = depth;
    }

    /**
     * 階層IDを取得する。
     * 
     * @return 階層ID 
     */
    public Long getHierarchyId() {
        return hierarchyId;
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
     * 階層名を取得する。
     * 
     * @return 階層名
     */
    public String getName() {
        return name;
    }

    /**
     * アクセス可能な組織を取得する。
     * 
     * @return アクセス可能な組織
     */
    public String getOrganization() {
        return organization;
    }

    /**
     * ハッシュコードを返す。
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.hierarchyId);
        return hash;
    }

    /**
     * オブジェクトが等しいかどうかを返す。
     * 
     * @param obj オブジェクト
     * @return true: 同一、false: 異なる 
     */
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
        final Hierarchy other = (Hierarchy) obj;
        return Objects.equals(this.hierarchyId, other.hierarchyId);
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return "Hierarchy{" + "depth=" + depth + ", hierarchyId=" + hierarchyId + ", name=" + name + ", organization=" + organization + '}';
    }
}
