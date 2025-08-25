/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 * ユニットテンプレート階層関連付け(プライマリーキー)
 *
 * @author ek.mori 
 * @version 1.4.2
 * @since 2016.10.12.Mon
 */
@Embeddable
public class TreeUnitTemplateHierarchyEntityPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "parent_id")
    private long parentId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "child_id")
    private long childId;

    public TreeUnitTemplateHierarchyEntityPK() {
    }

    public TreeUnitTemplateHierarchyEntityPK(long parentId, long childId) {
        this.parentId = parentId;
        this.childId = childId;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public long getChildId() {
        return childId;
    }

    public void setChildId(long childId) {
        this.childId = childId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) parentId;
        hash += (int) childId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TreeUnitTemplateHierarchyEntityPK)) {
            return false;
        }
        TreeUnitTemplateHierarchyEntityPK other = (TreeUnitTemplateHierarchyEntityPK) object;
        if (this.parentId != other.parentId) {
            return false;
        }
        return this.childId == other.childId;
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate.TreeUnitTemplateHierarchyEntityPK[ parentId=" + parentId + ", childId=" + childId + " ]";
    }
}
