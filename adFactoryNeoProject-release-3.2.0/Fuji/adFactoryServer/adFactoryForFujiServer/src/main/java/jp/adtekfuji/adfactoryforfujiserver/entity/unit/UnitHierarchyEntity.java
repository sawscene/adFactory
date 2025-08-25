/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.entity.unit;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 生産ユニット階層情報
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.12.Mon
 */
@Entity
@Table(name = "mst_unit_hierarchy")
@XmlRootElement(name = "unitHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // 階層登録時の名前重複チェック
    // TODO:同じ階層の中で重複してたらNGにする必要有
    @NamedQuery(name = "UnitHierarchyEntity.checkAddByHierarchyName", query = "SELECT COUNT(u.unitHierarchyId) FROM UnitHierarchyEntity u WHERE u.hierarchyName = :hierarchyName"),
    // 階層更新時の名前重複チェック
    // TODO:同じ階層の中で重複してたらNGにする必要有
    @NamedQuery(name = "UnitHierarchyEntity.checkUpdateByHierarchyName", query = "SELECT COUNT(u.unitHierarchyId) FROM UnitHierarchyEntity u WHERE u.hierarchyName = :hierarchyName AND u.unitHierarchyId != :unitHierarchyId"),
    // 階層情報全取得
    @NamedQuery(name = "UnitHierarchyEntity.findAll", query = "SELECT u FROM UnitHierarchyEntity u"),
    // 階層関連付けの階層ID検索
    @NamedQuery(name = "UnitHierarchyEntity.findByUnitHierarchyId", query = "SELECT u FROM UnitHierarchyEntity u WHERE u.unitHierarchyId = :unitHierarchyId"),
    // 階層関連付けの階層名前検索
    @NamedQuery(name = "UnitHierarchyEntity.findByHierarchyName", query = "SELECT u FROM UnitHierarchyEntity u WHERE u.hierarchyName = :hierarchyName")})
public class UnitHierarchyEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "unit_hierarchy_id")
    private Long unitHierarchyId;
    @Transient
    private Long parentId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "hierarchy_name")
    private String hierarchyName;
    @XmlElementWrapper(name = "units")
    @XmlElement(name = "unit")
    @Transient
    private List<UnitEntity> unitCollection = null;
    @Transient
    private Long childCount = 0L;

    public UnitHierarchyEntity() {
    }

    public UnitHierarchyEntity(Long parentId, String hierarchyName) {
        this.parentId = parentId;
        this.hierarchyName = hierarchyName;
    }

    public Long getUnitHierarchyId() {
        return unitHierarchyId;
    }

    public void setUnitHierarchyId(Long unitHierarchyId) {
        this.unitHierarchyId = unitHierarchyId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getHierarchyName() {
        return hierarchyName;
    }

    public void setHierarchyName(String hierarchyName) {
        this.hierarchyName = hierarchyName;
    }

    public List<UnitEntity> getUnitCollection() {
        return unitCollection;
    }

    public void setUnitCollection(List<UnitEntity> unitCollection) {
        this.unitCollection = unitCollection;
    }

    public Long getChildCount() {
        return childCount;
    }

    public void setChildCount(Long childCount) {
        this.childCount = childCount;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (unitHierarchyId != null ? unitHierarchyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UnitHierarchyEntity)) {
            return false;
        }
        UnitHierarchyEntity other = (UnitHierarchyEntity) object;
        return !((this.unitHierarchyId == null && other.unitHierarchyId != null) || (this.unitHierarchyId != null && !this.unitHierarchyId.equals(other.unitHierarchyId)));
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryforfujiserver.entity.unit.UnitHierarchyEntity[ unitHierarchyId=" + unitHierarchyId + ", unitHierarchyName=" + hierarchyName + " ]";
    }

}
