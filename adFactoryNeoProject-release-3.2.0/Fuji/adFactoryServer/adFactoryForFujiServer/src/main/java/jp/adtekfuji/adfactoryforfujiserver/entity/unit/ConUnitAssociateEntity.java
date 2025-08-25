/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.entity.unit;

import java.io.Serializable;
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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 生産ユニット関連付け情報
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.12.Mon
 */
@Entity
@Table(name = "con_unit_associate")
@XmlRootElement(name = "conUnitAssociate")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // 指定された親ユニット直属のカンバンID一覧を取得 (子ユニット以下のカンバンは含まない)
    @NamedQuery(name = "ConUnitAssociateEntity.findUnitKanbanId", query = "SELECT c.fkKanbanId FROM ConUnitAssociateEntity c WHERE c.fkParentUnitId = :fkParentUnitId AND c.fkKanbanId IS NOT NULL"),
    // 指定された親ユニット直属の子ユニットID一覧を取得 (孫以下のユニットは含まない)
    @NamedQuery(name = "ConUnitAssociateEntity.findChildUnitId", query = "SELECT c.fkUnitId FROM ConUnitAssociateEntity c WHERE c.fkParentUnitId = :fkParentUnitId AND c.fkUnitId IS NOT NULL"),
    // 指定された親ユニットの関連付け情報の数
    @NamedQuery(name = "ConUnitAssociateEntity.countChild", query = "SELECT COUNT(c) FROM ConUnitAssociateEntity c WHERE c.fkParentUnitId = :fkParentUnitId"),
    // 関連付け情報の全取得
    @NamedQuery(name = "ConUnitAssociateEntity.findAll", query = "SELECT c FROM ConUnitAssociateEntity c"),
    // 関連付け情報ID検索
    @NamedQuery(name = "ConUnitAssociateEntity.findByUnitAssociationId", query = "SELECT c FROM ConUnitAssociateEntity c WHERE c.unitAssociationId = :unitAssociationId ORDER BY c.unitAssociateOrder"),
    // 関連付け情報の親ユニットID検索
    @NamedQuery(name = "ConUnitAssociateEntity.findByFkParentUnitId", query = "SELECT c FROM ConUnitAssociateEntity c WHERE c.fkParentUnitId = :fkParentUnitId"),    
    // 指定されたIDのユニット情報のみ取得
    @NamedQuery(name = "ConUnitAssociateEntity.findByFkParentUnitIdAndFkUnit", query = "SELECT c FROM ConUnitAssociateEntity c WHERE c.fkParentUnitId = :fkParentUnitId AND c.fkUnitId > :Limit"),
    // 指定されたIDのカンバン情報のみ取得
    @NamedQuery(name = "ConUnitAssociateEntity.findByFkParentUnitIdAndFkKanban", query = "SELECT c FROM ConUnitAssociateEntity c WHERE c.fkParentUnitId = :fkParentUnitId AND c.fkKanbanId > :Limit"),
    // 関連付け情報の子カンバンID検索
    @NamedQuery(name = "ConUnitAssociateEntity.findByFkKanbanId", query = "SELECT c FROM ConUnitAssociateEntity c WHERE c.fkKanbanId = :fkKanbanId"),
    // 関連付け情報の子ユニットID検索
    @NamedQuery(name = "ConUnitAssociateEntity.findByFkUnitId", query = "SELECT c FROM ConUnitAssociateEntity c WHERE c.fkUnitId = :fkUnitId"),
    // 関連付け情報の指定された関連削除
    @NamedQuery(name = "ConUnitAssociateEntity.removeByFkParentUnitId", query = "DELETE FROM ConUnitAssociateEntity c WHERE c.fkParentUnitId = :fkParentUnitId")})
public class ConUnitAssociateEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "unit_associate_id")
    private Long unitAssociationId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_parent_unit_id")
    private Long fkParentUnitId;
    @Column(name = "fk_kanban_id")
    private Long fkKanbanId;
    @Transient
    private String kanbanName;
    @Column(name = "fk_unit_id")
    private Long fkUnitId;
    @Transient
    private String unitName;
    @Column(name = "unit_associate_order")
    private Integer unitAssociateOrder;

    public ConUnitAssociateEntity() {
    }

    public ConUnitAssociateEntity(Long unitAssociationId) {
        this.unitAssociationId = unitAssociationId;
    }

    public ConUnitAssociateEntity(Long fkParentUnitId, Long fkUnitId, Long fkKanbanId) {
        this.fkParentUnitId = fkParentUnitId;
        this.fkUnitId = fkUnitId;
        this.fkKanbanId = fkKanbanId;
    }

    public Long getUnitAssociationId() {
        return unitAssociationId;
    }

    public void setUnitAssociationId(Long unitAssociationId) {
        this.unitAssociationId = unitAssociationId;
    }

    public Long getFkParentUnitId() {
        return fkParentUnitId;
    }

    public void setFkParentUnitId(Long fkParentUnitId) {
        this.fkParentUnitId = fkParentUnitId;
    }

    public Long getFkKanbanId() {
        return fkKanbanId;
    }

    public void setFkKanbanId(Long fkKanbanId) {
        this.fkKanbanId = fkKanbanId;
    }

    public String getKanbanName() {
        return kanbanName;
    }

    public void setKanbanName(String kanbanName) {
        this.kanbanName = kanbanName;
    }

    public Long getFkUnitId() {
        return fkUnitId;
    }

    public void setFkUnitId(Long fkUnitId) {
        this.fkUnitId = fkUnitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Integer getUnitAssociateOrder() {
        return unitAssociateOrder;
    }

    public void setUnitAssociateOrder(Integer unitAssociateOrder) {
        this.unitAssociateOrder = unitAssociateOrder;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fkParentUnitId != null ? fkParentUnitId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConUnitAssociateEntity)) {
            return false;
        }
        ConUnitAssociateEntity other = (ConUnitAssociateEntity) object;
        return !((this.fkParentUnitId == null && other.fkParentUnitId != null) || (this.fkParentUnitId != null && !this.fkParentUnitId.equals(other.fkParentUnitId)));
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryforfujiserver.entity.unit.ConUnitAssociateEntity[ fkParentUnitId=" + fkParentUnitId + " ]";
    }

}
