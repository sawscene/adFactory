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
 * 生産ユニット階層関連付け情報
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.12.Mon
 */
@Entity
@Table(name = "con_unit_hierarchy")
@XmlRootElement(name = "conUnitHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // 該当の階層に所属する生産ユニット情報の数
    @NamedQuery(name = "ConUnitHierarchyEntity.countChild", query = "SELECT COUNT(c.conUnitHierarchyEntityPK.fkUnitId) FROM ConUnitHierarchyEntity c WHERE c.conUnitHierarchyEntityPK.fkUnitHierarchyId = :fkUnitHierarchyId"),
    // 階層関連付けの全取得
    @NamedQuery(name = "ConUnitHierarchyEntity.findAll", query = "SELECT c FROM ConUnitHierarchyEntity c"),
    // 階層関連付けの階層ID検索
    @NamedQuery(name = "ConUnitHierarchyEntity.findByFkUnitHierarchyId", query = "SELECT c FROM ConUnitHierarchyEntity c WHERE c.conUnitHierarchyEntityPK.fkUnitHierarchyId = :fkUnitHierarchyId"),
    // 階層関連付けの階層ID検索
    @NamedQuery(name = "ConUnitHierarchyEntity.countByFkUnitHierarchyId", query = "SELECT COUNT(c.conUnitHierarchyEntityPK.fkUnitHierarchyId) FROM ConUnitHierarchyEntity c WHERE c.conUnitHierarchyEntityPK.fkUnitHierarchyId = :fkUnitHierarchyId"),
    // 階層関連付けの生産ユニットID検索
    @NamedQuery(name = "ConUnitHierarchyEntity.findByFkUnitId", query = "SELECT c FROM ConUnitHierarchyEntity c WHERE c.conUnitHierarchyEntityPK.fkUnitId = :fkUnitId"),
    // 指定された生産ユニットIDの階層関連削除
    @NamedQuery(name = "ConUnitHierarchyEntity.removeByFkUnitId", query = "DELETE FROM ConUnitHierarchyEntity c WHERE c.conUnitHierarchyEntityPK.fkUnitId = :fkUnitId")})
public class ConUnitHierarchyEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ConUnitHierarchyEntityPK conUnitHierarchyEntityPK;

    public ConUnitHierarchyEntity() {
    }

    public ConUnitHierarchyEntity(ConUnitHierarchyEntityPK conUnitHierarchyPK) {
        this.conUnitHierarchyEntityPK = conUnitHierarchyPK;
    }

    public ConUnitHierarchyEntity(long fkUnitHierarchyId, long fkUnitId) {
        this.conUnitHierarchyEntityPK = new ConUnitHierarchyEntityPK(fkUnitHierarchyId, fkUnitId);
    }

    public ConUnitHierarchyEntityPK getConUnitHierarchyEntityPK() {
        return conUnitHierarchyEntityPK;
    }

    public void setConUnitHierarchyEntityPK(ConUnitHierarchyEntityPK conUnitHierarchyPK) {
        this.conUnitHierarchyEntityPK = conUnitHierarchyPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (conUnitHierarchyEntityPK != null ? conUnitHierarchyEntityPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConUnitHierarchyEntity)) {
            return false;
        }
        ConUnitHierarchyEntity other = (ConUnitHierarchyEntity) object;
        return !((this.conUnitHierarchyEntityPK == null && other.conUnitHierarchyEntityPK != null) || (this.conUnitHierarchyEntityPK != null && !this.conUnitHierarchyEntityPK.equals(other.conUnitHierarchyEntityPK)));
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryforfujiserver.entity.unit.ConUnitHierarchyEntity[ conUnitHierarchyEntitiyPK=" + conUnitHierarchyEntityPK + " ]";
    }

}
