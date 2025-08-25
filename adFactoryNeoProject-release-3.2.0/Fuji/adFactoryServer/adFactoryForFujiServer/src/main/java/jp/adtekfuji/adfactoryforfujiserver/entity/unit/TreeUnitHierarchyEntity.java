/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.entity.unit;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 生産ユニット階層関連付けテーブル
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.12.Mon
 */
@Entity
@Table(name = "tre_unit_hierarchy")
@XmlRootElement(name = "treeUnitHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // 該当の階層に所属する子階層の数
    @NamedQuery(name = "TreeUnitHierarchyEntity.countChild", query = "SELECT COUNT(t.treeUnitHierarchyEntityPK.childId) FROM TreeUnitHierarchyEntity t WHERE t.treeUnitHierarchyEntityPK.parentId = :parentId"),
    // 階層関連付けの全取得
    @NamedQuery(name = "TreeUnitHierarchyEntity.findAll", query = "SELECT t FROM TreeUnitHierarchyEntity t"),
    // 階層関連付けの階層親ID検索
    @NamedQuery(name = "TreeUnitHierarchyEntity.findByParentId", query = "SELECT t From TreeUnitHierarchyEntity t WHERE t.treeUnitHierarchyEntityPK.parentId = :parentId"),
    // 階層関連付けの階層子ID検索
    @NamedQuery(name = "TreeUnitHierarchyEntity.findByChildId", query = "SELECT t From  TreeUnitHierarchyEntity t WHERE t.treeUnitHierarchyEntityPK.childId = :childId"),
    // 指定された階層子IDの階層関連削除
    @NamedQuery(name = "TreeUnitHierarchyEntity.removeByChildId", query = "DELETE FROM TreeUnitHierarchyEntity t WHERE t.treeUnitHierarchyEntityPK.childId = :childId")})
public class TreeUnitHierarchyEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TreeUnitHierarchyEntityPK treeUnitHierarchyEntityPK;

    public TreeUnitHierarchyEntity() {
    }

    public TreeUnitHierarchyEntity(TreeUnitHierarchyEntityPK unitHierarchyPK) {
        this.treeUnitHierarchyEntityPK = unitHierarchyPK;
    }

    public TreeUnitHierarchyEntity(long parentId, long childId) {
        this.treeUnitHierarchyEntityPK = new TreeUnitHierarchyEntityPK(parentId, childId);
    }

    public TreeUnitHierarchyEntityPK getTreeUnitHierarchyEntityPK() {
        return treeUnitHierarchyEntityPK;
    }

    public void setTreeUnitHierarchyEntityPK(TreeUnitHierarchyEntityPK unitHierarchyPK) {
        this.treeUnitHierarchyEntityPK = unitHierarchyPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (treeUnitHierarchyEntityPK != null ? treeUnitHierarchyEntityPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TreeUnitHierarchyEntity)) {
            return false;
        }
        TreeUnitHierarchyEntity other = (TreeUnitHierarchyEntity) object;
        return !((this.treeUnitHierarchyEntityPK == null && other.treeUnitHierarchyEntityPK != null) || (this.treeUnitHierarchyEntityPK != null && !this.treeUnitHierarchyEntityPK.equals(other.treeUnitHierarchyEntityPK)));
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryforfujiserver.entity.unit.TreeUnitHierarchy[ treeUnitHierarchyPK=" + treeUnitHierarchyEntityPK + " ]";
    }
}
