/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.ledger;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * 帳票階層マスタ情報
 *
 * @author yu.nara
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ledgerHierarchy")
public class LedgerHierarchyInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private Long hierarchyId;// 階層ID

    @XmlElement()
    private String hierarchyName;// 階層名

    @XmlElement()
    private Long parentHierarchyId; // 親階層ID

    @XmlElement()
    private Integer verInfo = 1;// 排他用バーション

    private List<LedgerInfoEntity> ledgerInfoEntities = null;



    public LedgerHierarchyInfoEntity() {
    }

    public LedgerHierarchyInfoEntity(Long hierarchyId, String hierarchyName) {
        this.hierarchyId = hierarchyId;
        this.hierarchyName = hierarchyName;
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

    public List<LedgerInfoEntity> getLedgerInfoEntities() {
        return ledgerInfoEntities;
    }

    public void setLedgerInfoEntities(List<LedgerInfoEntity> ledgerInfoEntities) {
        this.ledgerInfoEntities = ledgerInfoEntities;
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
        final LedgerHierarchyInfoEntity other = (LedgerHierarchyInfoEntity) obj;
        if (!Objects.equals(this.hierarchyId, other.hierarchyId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LedgerHierarchyInfoEntity{" +
                "hierarchyId=" + hierarchyId +
                ", hierarchyName='" + hierarchyName + '\'' +
                ", parentHierarchyId=" + parentHierarchyId +
                ", verInfo=" + verInfo +
                '}';
    }
}
