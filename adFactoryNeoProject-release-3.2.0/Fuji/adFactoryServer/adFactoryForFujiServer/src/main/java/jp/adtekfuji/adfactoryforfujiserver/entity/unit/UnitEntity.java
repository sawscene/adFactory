/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.entity.unit;

import java.io.Serializable;
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
 * 生産ユニット情報
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.12.Mon
 */
@Entity
@Table(name = "trn_unit")
@XmlRootElement(name = "unit")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // 生産ユニット登録時の生産ユニット名重複チェック
    // TODO:同じ階層の中で重複してたらNGにする必要有
    @NamedQuery(name = "UnitEntity.checkAddOvarlapName", query = "SELECT COUNT(u.unitId) FROM UnitEntity u WHERE u.unitName = :unitName AND u.fkUnitTemplateId = :fkUnitTemplateId"),
    // 生産ユニット更新時の生産ユニット名重複チェック
    // TODO:同じ階層の中で重複してたらNGにする必要有
    @NamedQuery(name = "UnitEntity.checkUpdateOvarlapName", query = "SELECT COUNT(u.unitId) FROM UnitEntity u WHERE u.unitName = :unitName AND u.fkUnitTemplateId = :fkUnitTemplateId AND u.unitId != :unitId"),
    // 生産ユニット情報全取得
    @NamedQuery(name = "UnitEntity.findAll", query = "SELECT u FROM UnitEntity u"),
    // 生産ユニットID検索
    @NamedQuery(name = "UnitEntity.findByUnitId", query = "SELECT u FROM UnitEntity u WHERE u.unitId = :unitId"),
    // 生産ユニット名前検索
    @NamedQuery(name = "UnitEntity.findByUnitName", query = "SELECT u FROM UnitEntity u WHERE u.unitName = :unitName"),
    // ユニットテンプレートID検索
    @NamedQuery(name = "UnitEntity.findByFkUnitTypeId", query = "SELECT u FROM UnitEntity u WHERE u.fkUnitTemplateId = :fkUnitTemplateId"),
    // ワークフローダイアグラムの検索
    @NamedQuery(name = "UnitEntity.findByWorkflowDiaglam", query = "SELECT u FROM UnitEntity u WHERE u.workflowDiaglam = :workflowDiaglam"),
    // 開始日時の検索
    @NamedQuery(name = "UnitEntity.findByStartDatetime", query = "SELECT u FROM UnitEntity u WHERE u.startDatetime = :startDatetime"),
    // 終了日時の検索
    @NamedQuery(name = "UnitEntity.findByCompDatetime", query = "SELECT u FROM UnitEntity u WHERE u.compDatetime = :compDatetime"),
    // 更新者IDの検索
    @NamedQuery(name = "UnitEntity.findByFkUpdatePersonId", query = "SELECT u FROM UnitEntity u WHERE u.fkUpdatePersonId = :fkUpdatePersonId"),
    // 更新日時の検索
    @NamedQuery(name = "UnitEntity.findByUpdateDatetime", query = "SELECT u FROM UnitEntity u WHERE u.updateDatetime = :updateDatetime"),
    // ユニットテンプレートと期間から生産ユニット情報を取得する
    @NamedQuery(name = "UnitEntity.countByUnitTemplateIds", query = "SELECT COUNT(u.unitId) FROM UnitEntity u WHERE u.fkUnitTemplateId IN :unitTemplateIds AND ((u.startDatetime >= :fromDate AND (u.compDatetime <= :toDate OR u.startDatetime <= :toDate)) OR (u.compDatetime <= :toDate AND u.compDatetime >= :fromDate))"),
    @NamedQuery(name = "UnitEntity.findByUnitTemplateIds", query = "SELECT u FROM UnitEntity u WHERE u.fkUnitTemplateId IN :unitTemplateIds AND ((u.startDatetime >= :fromDate AND (u.compDatetime <= :toDate OR u.startDatetime <= :toDate)) OR (u.compDatetime <= :toDate AND u.compDatetime >= :fromDate)) ORDER BY u.unitId"),
    // 生産ユニットID検索
    //@NamedQuery(name = "UnitEntity.findByUnitIds", query = "SELECT u FROM UnitEntity u WHERE u.unitId IN :unitIds"),
    // ユニット階層IDをキーにして、ユニット情報を抽出
    @NamedQuery(name = "UnitEntity.findByUnitHierarchyId", query = "SELECT u FROM ConUnitHierarchyEntity c JOIN UnitEntity u ON u.unitId = c.conUnitHierarchyEntityPK.fkUnitId WHERE c.conUnitHierarchyEntityPK.fkUnitHierarchyId = :fkUnitHierarchyId"),
})
public class UnitEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "unit_id")
    private Long unitId;
    @Transient
    private Long parentId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "unit_name")
    private String unitName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_unit_template_id")
    private long fkUnitTemplateId;
    @Transient
    private String unitTemplateName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "workflow_diaglam")
    private String workflowDiaglam;
    @Column(name = "start_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDatetime;
    @Column(name = "comp_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date compDatetime;
    @Column(name = "fk_update_person_id")
    private Long fkUpdatePersonId;
    @Column(name = "update_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDatetime;
    @XmlElementWrapper(name = "unitPropertys")
    @XmlElement(name = "unitProperty")
    @Transient
    private List<UnitPropertyEntity> unitPropertyCollection = null;
    @XmlElementWrapper(name = "conUnitAssociates")
    @XmlElement(name = "conUnitAssociate")
    @Transient
    private List<ConUnitAssociateEntity> conUnitAssociateCollection = null;
    /**
     * カンバンID
     */
    @XmlElementWrapper(name = "kanbanIds")
    @XmlElement(name = "kanbanId")
    @Transient
    private List<Long> kanbanIds;
    /**
     * すべてのカンバンが完了しているか？
     */
    @XmlElement(name = "isCompleted")
    @Transient
    private Boolean isCompleted;

    public UnitEntity() {
    }

    public UnitEntity(Long unitId) {
        this.unitId = unitId;
    }

    public UnitEntity(Long parentId, String unitName, long fkUnitTemplateId, String workflowDiaglam) {
        this.parentId = parentId;
        this.unitName = unitName;
        this.fkUnitTemplateId = fkUnitTemplateId;
        this.workflowDiaglam = workflowDiaglam;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public long getFkUnitTemplateId() {
        return fkUnitTemplateId;
    }

    public void setFkUnitTemplateId(long fkUnitTemplateId) {
        this.fkUnitTemplateId = fkUnitTemplateId;
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

    public Date getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(Date startDatetime) {
        this.startDatetime = startDatetime;
    }

    public Date getCompDatetime() {
        return compDatetime;
    }

    public void setCompDatetime(Date compDatetime) {
        this.compDatetime = compDatetime;
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

    public List<UnitPropertyEntity> getUnitPropertyCollection() {
        return unitPropertyCollection;
    }

    public void setUnitPropertyCollection(List<UnitPropertyEntity> propertyCollection) {
        this.unitPropertyCollection = propertyCollection;
    }

    public List<ConUnitAssociateEntity> getConUnitAssociateCollection() {
        return conUnitAssociateCollection;
    }

    public void setConUnitAssociateCollection(List<ConUnitAssociateEntity> conUnitAssociateCollection) {
        this.conUnitAssociateCollection = conUnitAssociateCollection;
    }

    public List<Long> getKanbanIds() {
        return kanbanIds;
    }

    public void setKanbanIds(List<Long> kanbanIds) {
        this.kanbanIds = kanbanIds;
    }

    /**
     * すべてのカンバンが完了しているか？
     *
     * @return 完了状態 (true:すべて完了, false:未完了)
     */
    public Boolean getIsCompleted() {
        return this.isCompleted;
    }

    /**
     * すべてのカンバンが完了しているか？
     *
     * @param isCompleted 完了状態 (true:すべて完了, false:未完了)
     */
    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (unitId != null ? unitId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UnitEntity)) {
            return false;
        }
        UnitEntity other = (UnitEntity) object;
        return !((this.unitId == null && other.unitId != null) || (this.unitId != null && !this.unitId.equals(other.unitId)));
    }

    @Override
    public String toString() {
        return "UnitEntity{" + "unitId=" + unitId + ", unitName=" + unitName + ", unitTemplateName=" + unitTemplateName + '}';
    }
}
