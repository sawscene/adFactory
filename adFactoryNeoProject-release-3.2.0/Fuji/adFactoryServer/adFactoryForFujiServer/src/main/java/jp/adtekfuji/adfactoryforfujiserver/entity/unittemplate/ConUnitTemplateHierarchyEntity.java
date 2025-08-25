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
 * ユニットテンプレート階層関連付け情報
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.12.Mon
 */
@Entity
@Table(name = "con_unit_template_hierarchy")
@XmlRootElement(name = "conUnitTemplateHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // 該当の階層に所属するユニットテンプレートの数
    @NamedQuery(name = "ConUnitTemplateHierarchyEntity.countChild", query = "SELECT COUNT(c.conUnitTemplateHierarchyEntityPK.fkUnitTemplateId) FROM ConUnitTemplateHierarchyEntity c WHERE c.conUnitTemplateHierarchyEntityPK.fkUnitTemplateHierarchyId = :fkUnitTemplateHierarchyId"),
    // 階層関連付けの全取得 
    @NamedQuery(name = "ConUnitTemplateHierarchyEntity.findAll", query = "SELECT c FROM ConUnitTemplateHierarchyEntity c"),
    // 階層関連付けの階層ID検索
    @NamedQuery(name = "ConUnitTemplateHierarchyEntity.findByFkUnitTemplateHierarchyId", query = "SELECT c FROM ConUnitTemplateHierarchyEntity c WHERE c.conUnitTemplateHierarchyEntityPK.fkUnitTemplateHierarchyId = :fkUnitTemplateHierarchyId"),
    // 階層関連付けのユニットテンプレートID検索   
    @NamedQuery(name = "ConUnitTemplateHierarchyEntity.findByFkUnitTemplateId", query = "SELECT c FROM ConUnitTemplateHierarchyEntity c WHERE c.conUnitTemplateHierarchyEntityPK.fkUnitTemplateId = :fkUnitTemplateId"),
    // 指定されたユニットテンプレートIDの階層関連削除
    @NamedQuery(name = "ConUnitTemplateHierarchyEntity.removeByFkUnitTemplateId", query = "DELETE FROM ConUnitTemplateHierarchyEntity c WHERE c.conUnitTemplateHierarchyEntityPK.fkUnitTemplateId = :fkUnitTemplateId")})
public class ConUnitTemplateHierarchyEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ConUnitTemplateHierarchyEntityPK conUnitTemplateHierarchyEntityPK;

    public ConUnitTemplateHierarchyEntity() {
    }

    public ConUnitTemplateHierarchyEntity(ConUnitTemplateHierarchyEntityPK conUnitTemplateHierarchyPK) {
        this.conUnitTemplateHierarchyEntityPK = conUnitTemplateHierarchyPK;
    }

    public ConUnitTemplateHierarchyEntity(long fkUnitTemplateHierarchyId, long fkUnitTemplateId) {
        this.conUnitTemplateHierarchyEntityPK = new ConUnitTemplateHierarchyEntityPK(fkUnitTemplateHierarchyId, fkUnitTemplateId);
    }

    public ConUnitTemplateHierarchyEntityPK getConUnitTemplateHierarchyEntityPK() {
        return conUnitTemplateHierarchyEntityPK;
    }

    public void setConUnitTemplateHierarchyEntityPK(ConUnitTemplateHierarchyEntityPK conUnitTemplateHierarchyPK) {
        this.conUnitTemplateHierarchyEntityPK = conUnitTemplateHierarchyPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (conUnitTemplateHierarchyEntityPK != null ? conUnitTemplateHierarchyEntityPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConUnitTemplateHierarchyEntity)) {
            return false;
        }
        ConUnitTemplateHierarchyEntity other = (ConUnitTemplateHierarchyEntity) object;
        return !((this.conUnitTemplateHierarchyEntityPK == null && other.conUnitTemplateHierarchyEntityPK != null) || (this.conUnitTemplateHierarchyEntityPK != null && !this.conUnitTemplateHierarchyEntityPK.equals(other.conUnitTemplateHierarchyEntityPK)));
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate.ConUnitTemplateHierarchyEntity[ conUnitTemplateHierarchyEntityPK=" + conUnitTemplateHierarchyEntityPK + " ]";
    }

}
