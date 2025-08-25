/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate;

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
 * ユニットテンプレート階層情報
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.12.Mon
 */
@Entity
@Table(name = "mst_unit_template_hierarchy")
@XmlRootElement(name = "unittemplateHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // 階層登録時の名前重複チェック
    // TODO:同じ階層の中で重複してたらNGにする必要有
    @NamedQuery(name = "UnitTemplateHierarchyEntity.checkAddByHierarchyName", query = "SELECT COUNT(u.unitTemplateHierarchyId) FROM UnitTemplateHierarchyEntity u WHERE u.hierarchyName = :hierarchyName"),
    // 階層更新時の名前重複チェック
    // TODO:同じ階層の中で重複してたらNGにする必要有
    @NamedQuery(name = "UnitTemplateHierarchyEntity.checkUpdateByHierarchyName", query = "SELECT COUNT(u.unitTemplateHierarchyId) FROM UnitTemplateHierarchyEntity u WHERE u.hierarchyName = :hierarchyName AND u.unitTemplateHierarchyId != :unitTemplateHierarchyId"),
    // 階層情報全取得
    @NamedQuery(name = "UnitTemplateHierarchyEntity.findAll", query = "SELECT u FROM UnitTemplateHierarchyEntity u"),
    // 階層関連付けの階層ID検索
    @NamedQuery(name = "UnitTemplateHierarchyEntity.findByUnitTemplateHierarchyId", query = "SELECT u FROM UnitTemplateHierarchyEntity u WHERE u.unitTemplateHierarchyId = :unitTemplateHierarchyId"),
    // 階層関連付けの階層名前検索
    @NamedQuery(name = "UnitTemplateHierarchyEntity.findByHierarchyName", query = "SELECT u FROM UnitTemplateHierarchyEntity u WHERE u.hierarchyName = :hierarchyName")})
public class UnitTemplateHierarchyEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "unit_template_hierarchy_id")
    private Long unitTemplateHierarchyId;
    @Transient
    private Long parentId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "hierarchy_name")
    private String hierarchyName;
    @XmlElementWrapper(name = "unittemplates")
    @XmlElement(name = "unittemplate")
    @Transient
    private List<UnitTemplateEntity> unittemplateCollection = null;
    @Transient
    private Long childCount = 0L;

    public UnitTemplateHierarchyEntity() {
    }

    public UnitTemplateHierarchyEntity(Long parentId, String hierarchyName) {
        this.parentId = parentId;
        this.hierarchyName = hierarchyName;
    }

    public Long getUnitTemplateHierarchyId() {
        return unitTemplateHierarchyId;
    }

    public void setUnitTemplateHierarchyId(Long unitTemplateHierarchyId) {
        this.unitTemplateHierarchyId = unitTemplateHierarchyId;
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

    public List<UnitTemplateEntity> getUnitTemplateCollection() {
        return unittemplateCollection;
    }

    public void setUnitTemplateCollection(List<UnitTemplateEntity> unittemplateCollection) {
        this.unittemplateCollection = unittemplateCollection;
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
        hash += (unitTemplateHierarchyId != null ? unitTemplateHierarchyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UnitTemplateHierarchyEntity)) {
            return false;
        }
        UnitTemplateHierarchyEntity other = (UnitTemplateHierarchyEntity) object;
        return !((this.unitTemplateHierarchyId == null && other.unitTemplateHierarchyId != null) || (this.unitTemplateHierarchyId != null && !this.unitTemplateHierarchyId.equals(other.unitTemplateHierarchyId)));
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate.UnitTemplateHierarchyEntity[ unitTemplateHierarchyId=" + unitTemplateHierarchyId + "unitTemplateHierarchyName=" + hierarchyName + " ]";
    }
}
