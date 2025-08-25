/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * ユニットテンプレート情報
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.12.Mon
 */
@Entity
@Table(name = "mst_unit_template")
@XmlRootElement(name = "unittemplate")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // 指定したユニットテンプレートを元にいくつ生産ユニットが作成されているか検索
    @NamedQuery(name = "UnitTemplateEntity.countUnitAssociation", query = "SELECT COUNT(u.unitId) FROM UnitEntity u WHERE u.fkUnitTemplateId = :fkUnitTemplateId"),
    // ユニットテンプレート登録時のユニットテンプレート名重複チェック
    // TODO:同じ階層の中で重複してたらNGにする必要有
    @NamedQuery(name = "UnitTemplateEntity.checkAddByUnitTemplateName", query = "SELECT COUNT(u.unitTemplateId) FROM UnitTemplateEntity u WHERE u.unitTemplateName = :unitTemplateName"),
    // ユニットテンプレート更新時のユニットテンプレート名重複チェック
    // TODO:同じ階層の中で重複してたらNGにする必要有
    @NamedQuery(name = "UnitTemplateEntity.checkUpdateByUnitTemplateName", query = "SELECT COUNT(u.unitTemplateId) FROM UnitTemplateEntity u WHERE u.unitTemplateName = :unitTemplateName AND u.unitTemplateId != :unitTemplateId"),
    // ユニットテンプレート情報全取得
    @NamedQuery(name = "UnitTemplateEntity.findAll", query = "SELECT u FROM UnitTemplateEntity u"),
    // ユニットテンプレートID検索
    @NamedQuery(name = "UnitTemplateEntity.findByUnitTemplateIds", query = "SELECT u FROM UnitTemplateEntity u WHERE u.unitTemplateId IN :unitTemplateIds"),
    // ユニットテンプレート名前検索
    @NamedQuery(name = "UnitTemplateEntity.findByUnitTemplateName", query = "SELECT u FROM UnitTemplateEntity u WHERE u.unitTemplateName = :unitTemplateName"),
    // ユニットテンプレートワークフロー検索
    @NamedQuery(name = "UnitTemplateEntity.findByWorkflowDiaglam", query = "SELECT u FROM UnitTemplateEntity u WHERE u.workflowDiaglam = :workflowDiaglam"),
    // ユニットテンプレート更新者ID検索    @NamedQuery(name = "UnitTemplateEntity.findByFkUpdatePersonId", query = "SELECT u FROM UnitTemplateEntity u WHERE u.fkUpdatePersonId = :fkUpdatePersonId"),
    // ユニットテンプレート更新日検索
    @NamedQuery(name = "UnitTemplateEntity.findByUpdateDatetime", query = "SELECT u FROM UnitTemplateEntity u WHERE u.updateDatetime = :updateDatetime"),
    // ユニットテンプレート削除フラグ検索
    @NamedQuery(name = "UnitTemplateEntity.findByRemoveFlag", query = "SELECT u FROM UnitTemplateEntity u WHERE u.removeFlag = :removeFlag"),
    //
    @NamedQuery(name = "UnitTemplateEntity.findByHierarchyId", query = "SELECT u FROM ConUnitTemplateHierarchyEntity c JOIN UnitTemplateEntity u ON u.unitTemplateId = c.conUnitTemplateHierarchyEntityPK.fkUnitTemplateId WHERE c.conUnitTemplateHierarchyEntityPK.fkUnitTemplateHierarchyId = :unitTemplateHierarchyId"),
})
public class UnitTemplateEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "unit_template_id")
    private Long unitTemplateId;
    @Transient
    private Long parentId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "unit_template_name")
    private String unitTemplateName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "workflow_diaglam")
    private String workflowDiaglam;
    @Column(name = "fk_output_kanban_hierarchy_id")
    private Long fkOutputKanbanHierarchyId;
    @Transient
    private String OutputKanbanHierarchyName;
    @Column(name = "fk_update_person_id")
    private Long fkUpdatePersonId;
    @Column(name = "update_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDatetime;
    @Column(name = "remove_flag")
    private Boolean removeFlag;
    @XmlElementWrapper(name = "unittemplatePropertys")
    @XmlElement(name = "unittemplateProperty")
    @Transient
    private List<UnitTemplatePropertyEntity> unittemplatePropertyCollection = null;
    @XmlElementWrapper(name = "conUnitTemplateAssociates")
    @XmlElement(name = "conUnitTemplateAssociate")
    @Transient
    private List<ConUnitTemplateAssociateEntity> conUnitTemplateAssociateCollection = null;

    public UnitTemplateEntity() {
    }

    public UnitTemplateEntity(Long unitTemplateId) {
        this.unitTemplateId = unitTemplateId;
    }

    public UnitTemplateEntity(Long unitTemplateId, String unitTemplateName, String workflowDiaglam) {
        this.unitTemplateId = unitTemplateId;
        this.unitTemplateName = unitTemplateName;
        this.workflowDiaglam = workflowDiaglam;
    }

    public UnitTemplateEntity(Long parentId, String unitTemplateName, String workflowDiaglam, Long fkOutputKanbanHierarchyId, Long fkUpdatePersonId, Date updateDatetime) {
        this.parentId = parentId;
        this.unitTemplateName = unitTemplateName;
        this.workflowDiaglam = workflowDiaglam;
        this.fkOutputKanbanHierarchyId = fkOutputKanbanHierarchyId;
        this.fkUpdatePersonId = fkUpdatePersonId;
        this.updateDatetime = updateDatetime;
        this.removeFlag = false;
    }

    public UnitTemplateEntity(UnitTemplateEntity in) {
        this.unitTemplateId = in.unitTemplateId;
        this.parentId = in.parentId;
        this.unitTemplateName = in.unitTemplateName;
        this.workflowDiaglam = in.workflowDiaglam;
        this.fkOutputKanbanHierarchyId = in.fkOutputKanbanHierarchyId;
        this.fkUpdatePersonId = in.fkUpdatePersonId;
        this.updateDatetime = in.updateDatetime;
        this.removeFlag = in.removeFlag;
        this.unittemplatePropertyCollection = new ArrayList<>();
        for (UnitTemplatePropertyEntity property : in.getUnitTemplatePropertyCollection()) {
            this.unittemplatePropertyCollection.add(new UnitTemplatePropertyEntity(property));
        }
        this.conUnitTemplateAssociateCollection = new ArrayList<>();
        for (ConUnitTemplateAssociateEntity connect : in.getConUnitTemplateAssociateCollection()) {
            this.conUnitTemplateAssociateCollection.add(new ConUnitTemplateAssociateEntity(connect));
        }
    }

    public Long getUnitTemplateId() {
        return unitTemplateId;
    }

    public void setUnitTemplateId(Long unitTemplateId) {
        this.unitTemplateId = unitTemplateId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getUnitTemplateName() {
        return unitTemplateName;
    }

    public void setUnitTemplateName(String unitTemplateName) {
        this.unitTemplateName = unitTemplateName;
    }

    public String getWorkflowDiaglam() {
        return workflowDiaglam;
    }

    public void setWorkflowDiaglam(String workflowDiaglam) {
        this.workflowDiaglam = workflowDiaglam;
    }

    public Long getFkOutputKanbanHierarchyId() {
        return fkOutputKanbanHierarchyId;
    }

    public void setFkOutputKanbanHierarchyId(Long fkOutputKanbanHierarchyId) {
        this.fkOutputKanbanHierarchyId = fkOutputKanbanHierarchyId;
    }

    public String getOutputKanbanHierarchyName() {
        return OutputKanbanHierarchyName;
    }

    public void setOutputKanbanHierarchyName(String OutputKanbanHierarchyName) {
        this.OutputKanbanHierarchyName = OutputKanbanHierarchyName;
    }

    public Long getFkUpdatePersonId() {
        return fkUpdatePersonId;
    }

    public void setFkUpdatePersonId(Long fkUpdatePersonId) {
        this.fkUpdatePersonId = fkUpdatePersonId;
    }

    public Date getUpdateDatetime() {
        return updateDatetime;
    }

    public void setUpdateDatetime(Date updateDatetime) {
        this.updateDatetime = updateDatetime;
    }

    public Boolean getRemoveFlag() {
        return removeFlag;
    }

    public void setRemoveFlag(Boolean removeFlag) {
        this.removeFlag = removeFlag;
    }

    public List<UnitTemplatePropertyEntity> getUnitTemplatePropertyCollection() {
        return unittemplatePropertyCollection;
    }

    public void setUnitTemplatePropertyCollection(List<UnitTemplatePropertyEntity> unittemplatePropertyCollection) {
        this.unittemplatePropertyCollection = unittemplatePropertyCollection;
    }

    public List<ConUnitTemplateAssociateEntity> getConUnitTemplateAssociateCollection() {
        return conUnitTemplateAssociateCollection;
    }

    public void setConUnitTemplateAssociateCollection(List<ConUnitTemplateAssociateEntity> conUnitTemplateAssociatekCollection) {
        this.conUnitTemplateAssociateCollection = conUnitTemplateAssociatekCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (unitTemplateId != null ? unitTemplateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UnitTemplateEntity)) {
            return false;
        }
        UnitTemplateEntity other = (UnitTemplateEntity) object;
        return !((this.unitTemplateId == null && other.unitTemplateId != null) || (this.unitTemplateId != null && !this.unitTemplateId.equals(other.unitTemplateId)));
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate.UnitTemplateEntity[ unitTemplateId=" + unitTemplateId + "unitTemplateName=" + unitTemplateName + " ]";
    }

}
