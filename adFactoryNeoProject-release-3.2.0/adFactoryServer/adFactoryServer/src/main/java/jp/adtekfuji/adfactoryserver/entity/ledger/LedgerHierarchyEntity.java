/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.ledger;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 帳票階層マスタ情報
 *
 * @author yu.nara
 */
@Entity
@Table(name = "mst_ledger_hierarchy")
@XmlRootElement(name = "ledgerHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedNativeQueries({
        @NamedNativeQuery(name = "LedgerHierarchyEntity.checkMovable",
                query = "WITH RECURSIVE descendants AS ( SELECT mlh.hierarchy_id FROM mst_ledger_hierarchy mlh WHERE mlh.parent_hierarchy_id = ?1 UNION ALL SELECT mlh2.hierarchy_id FROM mst_ledger_hierarchy mlh2, descendants d WHERE mlh2.parent_hierarchy_id = d.hierarchy_id ) SELECT COUNT(*)=0 FROM descendants d2 WHERE d2.hierarchy_id = ?2"),

})
@NamedQueries({
        @NamedQuery(name = "LedgerHierarchyEntity.findByParentIds", query = "SELECT l FROM LedgerHierarchyEntity l WHERE l.parentHierarchyId IN :parentIds"),
        @NamedQuery(name = "LedgerHierarchyEntity.checkAddByHierarchyName", query = "SELECT COUNT(h.hierarchyId) FROM LedgerHierarchyEntity h WHERE h.hierarchyName = :hierarchyName AND h.hierarchyId <> :hierarchyId"),

})
public class LedgerHierarchyEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "hierarchy_id")
    private Long hierarchyId;// 階層ID

    //@NotNull
    @Size(min = 1, max = 256)
    @Column(name = "hierarchy_name")
    private String hierarchyName;// 階層名

    @Column(name = "parent_hierarchy_id")
    private Long parentHierarchyId; // 親階層ID

    @Column(name = "ver_info")
    @Version
    private Integer verInfo = 1;// 排他用バーション

    public LedgerHierarchyEntity() {
    }

    public Long getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(Long hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    public String getHierarchyName() {
        return hierarchyName;
    }

    public void setHierarchyName(String hierarchyName) {
        this.hierarchyName = hierarchyName;
    }

    public Long getParentHierarchyId() {
        return parentHierarchyId;
    }

    public void setParentHierarchyId(Long parentHierarchyId) {
        this.parentHierarchyId = parentHierarchyId;
    }

    public Integer getVerInfo() {
        return verInfo;
    }

    public void setVerInfo(Integer verInfo) {
        this.verInfo = verInfo;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.hierarchyId);
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
        final LedgerHierarchyEntity other = (LedgerHierarchyEntity) obj;
        if (!Objects.equals(this.hierarchyId, other.hierarchyId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("KanbanHierarchyEntity{")
                .append("hierarchyId=").append(this.hierarchyId)
                .append(", ")
                .append("hierarchyName=").append(this.hierarchyName)
                .append(", ")
                .append("parentHierarchyId=").append(this.parentHierarchyId)
                .append("}")
                .toString();
    }
}
