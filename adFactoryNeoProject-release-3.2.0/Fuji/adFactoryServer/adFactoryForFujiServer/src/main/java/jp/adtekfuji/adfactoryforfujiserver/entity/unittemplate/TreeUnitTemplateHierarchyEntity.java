/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate;

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
 * ユニットテンプレート階層関連付け
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.12.Mon
 */
@Entity
@Table(name = "tre_unit_template_hierarchy")
@XmlRootElement(name = "treeUnitTemplateHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // 該当の階層に所属する子階層の数
    @NamedQuery(name = "TreeUnitTemplateHierarchyEntity.countChild", query = "SELECT COUNT(t.treeUnitTemplateHierarchyEntityPK.childId) FROM TreeUnitTemplateHierarchyEntity t WHERE t.treeUnitTemplateHierarchyEntityPK.parentId = :parentId"),
    // 階層関連付けの全取得
    @NamedQuery(name = "TreeUnitTemplateHierarchyEntity.findAll", query = "SELECT u FROM TreeUnitTemplateHierarchyEntity u"),
    // 階層関連付けの階層親ID検索
    @NamedQuery(name = "TreeUnitTemplateHierarchyEntity.findByParentId", query = "SELECT u FROM TreeUnitTemplateHierarchyEntity u WHERE u.treeUnitTemplateHierarchyEntityPK.parentId = :parentId"),
    // 階層関連付けの階層子ID検索
    @NamedQuery(name = "TreeUnitTemplateHierarchyEntity.findByChildId", query = "SELECT u FROM TreeUnitTemplateHierarchyEntity u WHERE u.treeUnitTemplateHierarchyEntityPK.childId = :childId"),
    // 指定された階層子IDの階層関連削除
    @NamedQuery(name = "TreeUnitTemplateHierarchyEntity.removeByChildId", query = "DELETE FROM TreeUnitTemplateHierarchyEntity t WHERE t.treeUnitTemplateHierarchyEntityPK.childId = :childId")})
public class TreeUnitTemplateHierarchyEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TreeUnitTemplateHierarchyEntityPK treeUnitTemplateHierarchyEntityPK;

    public TreeUnitTemplateHierarchyEntity() {
    }

    public TreeUnitTemplateHierarchyEntity(TreeUnitTemplateHierarchyEntityPK unitTemplateHierarchyPK) {
        this.treeUnitTemplateHierarchyEntityPK = unitTemplateHierarchyPK;
    }

    public TreeUnitTemplateHierarchyEntity(long parentId, long childId) {
        this.treeUnitTemplateHierarchyEntityPK = new TreeUnitTemplateHierarchyEntityPK(parentId, childId);
    }

    public TreeUnitTemplateHierarchyEntityPK getTreeUnitTemplateHierarchyEntityPK() {
        return treeUnitTemplateHierarchyEntityPK;
    }

    public void setTreeUnitTemplateHierarchyEntityPK(TreeUnitTemplateHierarchyEntityPK unitTemplateHierarchyPK) {
        this.treeUnitTemplateHierarchyEntityPK = unitTemplateHierarchyPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (treeUnitTemplateHierarchyEntityPK != null ? treeUnitTemplateHierarchyEntityPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TreeUnitTemplateHierarchyEntity)) {
            return false;
        }
        TreeUnitTemplateHierarchyEntity other = (TreeUnitTemplateHierarchyEntity) object;
        return !((this.treeUnitTemplateHierarchyEntityPK == null && other.treeUnitTemplateHierarchyEntityPK != null) || (this.treeUnitTemplateHierarchyEntityPK != null && !this.treeUnitTemplateHierarchyEntityPK.equals(other.treeUnitTemplateHierarchyEntityPK)));
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate.TreeUnitTemplateHierarchyEntity[ unitTemplateHierarchyPK=" + treeUnitTemplateHierarchyEntityPK + " ]";
    }
}
