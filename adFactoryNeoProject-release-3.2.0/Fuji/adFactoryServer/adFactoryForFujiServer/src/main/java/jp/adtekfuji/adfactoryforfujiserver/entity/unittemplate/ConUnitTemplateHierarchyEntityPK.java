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
public class ConUnitTemplateHierarchyEntityPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_unit_template_hierarchy_id")
    private long fkUnitTemplateHierarchyId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_unit_template_id")
    private long fkUnitTemplateId;

    public ConUnitTemplateHierarchyEntityPK() {
    }

    public ConUnitTemplateHierarchyEntityPK(long fkUnitTemplateHierarchyId, long fkUnitTemplateId) {
        this.fkUnitTemplateHierarchyId = fkUnitTemplateHierarchyId;
        this.fkUnitTemplateId = fkUnitTemplateId;
    }

    public long getFkUnitTemplateHierarchyId() {
        return fkUnitTemplateHierarchyId;
    }

    public void setFkUnitTemplateHierarchyId(long fkUnitTemplateHierarchyId) {
        this.fkUnitTemplateHierarchyId = fkUnitTemplateHierarchyId;
    }

    public long getFkUnitTemplateId() {
        return fkUnitTemplateId;
    }

    public void setFkUnitTemplateId(long fkUnitTemplateId) {
        this.fkUnitTemplateId = fkUnitTemplateId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) fkUnitTemplateHierarchyId;
        hash += (int) fkUnitTemplateId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConUnitTemplateHierarchyEntityPK)) {
            return false;
        }
        ConUnitTemplateHierarchyEntityPK other = (ConUnitTemplateHierarchyEntityPK) object;
        if (this.fkUnitTemplateHierarchyId != other.fkUnitTemplateHierarchyId) {
            return false;
        }
        return this.fkUnitTemplateId == other.fkUnitTemplateId;
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate.ConUnitTemplateHierarchyEntityPK[ fkUnitTemplateHierarchyId=" + fkUnitTemplateHierarchyId + ", fkUnitTemplateId=" + fkUnitTemplateId + " ]";
    }
    
}
