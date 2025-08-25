/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * ユニットテンプレート関連付け情報
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.12.Mon
 */
@Entity
@Table(name = "con_unit_template_associate")
@XmlRootElement(name = "conUnitTemplateAssociate")
@NamedQueries({
    // 指定された親ユニットの関連付け情報の数
    @NamedQuery(name = "ConUnitTemplateAssociateEntity.countChild", query = "SELECT COUNT(c) FROM ConUnitTemplateAssociateEntity c WHERE c.fkParentUnitTemplateId = :fkParentUnitTemplateId"),
    // 関連付け情報の全取得
    @NamedQuery(name = "ConUnitTemplateAssociateEntity.findAll", query = "SELECT c FROM ConUnitTemplateAssociateEntity c"),
    // 関連付け情報ID検索
    @NamedQuery(name = "ConUnitTemplateAssociateEntity.findByUnitTemplateAssociationId", query = "SELECT c FROM ConUnitTemplateAssociateEntity c WHERE c.unitTemplateAssociationId = :unitTemplateAssociationId ORDER BY c.unitTemplateAssociateOrder"),
    // 関連付け情報の親ユニットテンプレートID検索
    @NamedQuery(name = "ConUnitTemplateAssociateEntity.findByFkParentUnitTemplateId", query = "SELECT c FROM ConUnitTemplateAssociateEntity c WHERE c.fkParentUnitTemplateId = :fkParentUnitTemplateId"),
    // 関連付け情報の子工程順ID検索
    @NamedQuery(name = "ConUnitTemplateAssociateEntity.findByFkWorkflowId", query = "SELECT c FROM ConUnitTemplateAssociateEntity c WHERE c.fkWorkflowId = :fkWorkflowId"),
    // 関連付け情報の子ユニットテンプレートID検索
    @NamedQuery(name = "ConUnitTemplateAssociateEntity.findByFkUnitTemplateId", query = "SELECT c FROM ConUnitTemplateAssociateEntity c WHERE c.fkUnitTemplateId = :fkUnitTemplateId"),
    // 関連付け情報の表示順番検索
    @NamedQuery(name = "ConUnitTemplateAssociateEntity.findByUnitTemplateAssociateOrder", query = "SELECT c FROM ConUnitTemplateAssociateEntity c WHERE c.unitTemplateAssociateOrder = :unitTemplateAssociateOrder"),
    // 関連付け情報の開始日時検索
    @NamedQuery(name = "ConUnitTemplateAssociateEntity.findByStandardStartTime", query = "SELECT c FROM ConUnitTemplateAssociateEntity c WHERE c.standardStartTime = :standardStartTime"),
    // 関連付け情報の終了日時検索
    @NamedQuery(name = "ConUnitTemplateAssociateEntity.findByStandardEndTime", query = "SELECT c FROM ConUnitTemplateAssociateEntity c WHERE c.standardEndTime = :standardEndTime"),
    // 関連付け情報の指定された関連削除
    @NamedQuery(name = "ConUnitTemplateAssociateEntity.removeByfkParentUnitTemplateId", query = "DELETE FROM ConUnitTemplateAssociateEntity c WHERE c.fkParentUnitTemplateId = :fkParentUnitTemplateId")})

public class ConUnitTemplateAssociateEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "unit_template_associate_id")
    private Long unitTemplateAssociationId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_parent_unit_template_id")
    private Long fkParentUnitTemplateId;
    @Column(name = "fk_workflow_id")
    private Long fkWorkflowId;
    @Transient
    private String workflowName;
    @Column(name = "fk_unit_template_id")
    private Long fkUnitTemplateId;
    @Transient
    private String unitTemplateName;
    @Column(name = "unit_template_associate_order")
    private Integer unitTemplateAssociateOrder;
    @Column(name = "standard_start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date standardStartTime;
    @Column(name = "standard_end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date standardEndTime;

    public ConUnitTemplateAssociateEntity() {
    }

    public ConUnitTemplateAssociateEntity(Long unitTemplateAssociationId) {
        this.unitTemplateAssociationId = unitTemplateAssociationId;
    }

    public ConUnitTemplateAssociateEntity(ConUnitTemplateAssociateEntity in) {
        this.fkParentUnitTemplateId = in.fkParentUnitTemplateId;
        this.fkWorkflowId = in.fkWorkflowId;
        this.fkUnitTemplateId = in.fkUnitTemplateId;
        this.unitTemplateAssociateOrder = in.unitTemplateAssociateOrder;
        this.standardStartTime = in.standardStartTime;
        this.standardEndTime = in.standardEndTime;
    }

    public ConUnitTemplateAssociateEntity(Long unitTemplateAssociationId, Long fkParentUnitTemplateId, Long fkWorkflowId, Long fkUnitTemplateId, Integer unitTemplateAssociateOrder, Date standardStartTime, Date standardEndTime) {
        this.unitTemplateAssociationId = unitTemplateAssociationId;
        this.fkParentUnitTemplateId = fkParentUnitTemplateId;
        this.fkWorkflowId = fkWorkflowId;
        this.fkUnitTemplateId = fkUnitTemplateId;
        this.unitTemplateAssociateOrder = unitTemplateAssociateOrder;
        this.standardStartTime = standardStartTime;
        this.standardEndTime = standardEndTime;
    }

    public Long getUnitTemplateAssociationIdId() {
        return unitTemplateAssociationId;
    }

    public void setUnitTemplateAssociationIdId(Long unitTemplateAssociationId) {
        this.unitTemplateAssociationId = unitTemplateAssociationId;
    }

    public Long getFkParentUnitTemplateId() {
        return fkParentUnitTemplateId;
    }

    public void setFkParentUnitTemplateId(Long fkParentUnitTemplateId) {
        this.fkParentUnitTemplateId = fkParentUnitTemplateId;
    }

    public Long getFkWorkflowId() {
        return fkWorkflowId;
    }

    public void setFkWorkflowId(Long fkWorkflowId) {
        this.fkWorkflowId = fkWorkflowId;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public Long getFkUnitTemplateId() {
        return fkUnitTemplateId;
    }

    public void setFkUnitTemplateId(Long fkUnitTemplateId) {
        this.fkUnitTemplateId = fkUnitTemplateId;
    }

    public String getUnitTemplateName() {
        return unitTemplateName;
    }

    public void setUnitTemplateName(String unitTemplateName) {
        this.unitTemplateName = unitTemplateName;
    }

    public Integer getUnitTemplateAssociateOrder() {
        return unitTemplateAssociateOrder;
    }

    public void setUnitTemplateAssociateOrder(Integer unitTemplateAssociateOrder) {
        this.unitTemplateAssociateOrder = unitTemplateAssociateOrder;
    }

    public Date getStandardStartTime() {
        return standardStartTime;
    }

    public void setStandardStartTime(Date standardStartTime) {
        this.standardStartTime = standardStartTime;
    }

    public Date getStandardEndTime() {
        return standardEndTime;
    }

    public void setStandardEndTime(Date standardEndTime) {
        this.standardEndTime = standardEndTime;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fkParentUnitTemplateId != null ? fkParentUnitTemplateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConUnitTemplateAssociateEntity)) {
            return false;
        }
        ConUnitTemplateAssociateEntity other = (ConUnitTemplateAssociateEntity) object;
        return !((this.fkParentUnitTemplateId == null && other.fkParentUnitTemplateId != null) || (this.fkParentUnitTemplateId != null && !this.fkParentUnitTemplateId.equals(other.fkParentUnitTemplateId)));
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryforfujiserver.entity.version.ConUnitTemplateAssociateEntity[ fkParentUnitTemplateId=" + fkParentUnitTemplateId + " ]";
    }

}
