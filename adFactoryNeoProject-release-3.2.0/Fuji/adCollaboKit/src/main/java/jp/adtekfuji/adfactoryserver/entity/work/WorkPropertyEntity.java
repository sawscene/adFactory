/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.work;

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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "mst_work_property")
@XmlRootElement(name = "workProperty")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "WorkPropertyEntity.findAll", query = "SELECT w FROM WorkPropertyEntity w ORDER BY w.workPropOrder"),
    @NamedQuery(name = "WorkPropertyEntity.findByWorkPropId", query = "SELECT w FROM WorkPropertyEntity w WHERE w.workPropId = :workPropId ORDER BY w.workPropOrder"),
    @NamedQuery(name = "WorkPropertyEntity.findByFkMasterId", query = "SELECT w FROM WorkPropertyEntity w WHERE w.fkMasterId = :fkMasterId ORDER BY w.workPropOrder"),
    @NamedQuery(name = "WorkPropertyEntity.findByWorkPropName", query = "SELECT w FROM WorkPropertyEntity w WHERE w.workPropName = :workPropName ORDER BY w.workPropOrder"),
    @NamedQuery(name = "WorkPropertyEntity.findByWorkPropType", query = "SELECT w FROM WorkPropertyEntity w WHERE w.workPropType = :workPropType ORDER BY w.workPropOrder"),
    @NamedQuery(name = "WorkPropertyEntity.findByWorkPropValue", query = "SELECT w FROM WorkPropertyEntity w WHERE w.workPropValue = :workPropValue ORDER BY w.workPropOrder"),
    @NamedQuery(name = "WorkPropertyEntity.findByWorkPropOrder", query = "SELECT w FROM WorkPropertyEntity w WHERE w.workPropOrder = :workPropOrder ORDER BY w.workPropOrder"),
    @NamedQuery(name = "WorkPropertyEntity.removeByFkMasterId", query = "DELETE FROM WorkPropertyEntity w WHERE w.fkMasterId = :fkMasterId")})
public class WorkPropertyEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "work_prop_id")
    private Long workPropId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_master_id")
    private Long fkMasterId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "work_prop_name")
    private String workPropName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "work_prop_type")
    private String workPropType;
    @Size(max = 2147483647)
    @Column(name = "work_prop_value")
    private String workPropValue;
    @Column(name = "work_prop_order")
    private Integer workPropOrder;

    // プロパティ種別
    @Size(max = 128)
    @Column(name = "work_prop_category")
    private String workPropCategory;
    // 付加情報
    @Size(max = 2147483647)
    @Column(name = "work_prop_option")
    private String workPropOption;
    // 基準値下限
    @Column(name = "work_prop_lower_tolerance")
    private Double workPropLowerTolerance;
    // 基準値上限
    @Column(name = "work_prop_upper_tolerance")
    private Double workPropUpperTolerance;
    // タグ
    @Size(max = 128)
    @Column(name = "work_prop_tag")
    private String workPropTag;
    // 入力規則
    @Size(max = 128)
    @Column(name = "work_prop_validation_rule")
    private String workPropValidationRule;
    // 工程セクション表示順
    @Column(name = "work_section_order")
    private Integer workSectionOrder;
    // 進捗チェックポイント
    @Column(name = "work_prop_checkpoint")
    private Integer workPropCheckpoint;

    public WorkPropertyEntity() {
    }

    public WorkPropertyEntity(WorkPropertyEntity in) {
        this.workPropId = in.workPropId;
        this.fkMasterId = in.fkMasterId;
        this.workPropName = in.workPropName;
        this.workPropType = in.workPropType;
        this.workPropValue = in.workPropValue;
        this.workPropOrder = in.workPropOrder;
        this.workPropCategory = in.workPropCategory;
        this.workPropOption = in.workPropOption;
        this.workPropLowerTolerance = in.workPropLowerTolerance;
        this.workPropUpperTolerance = in.workPropUpperTolerance;
        this.workPropTag = in.workPropTag;
        this.workPropValidationRule = in.workPropValidationRule;
        this.workSectionOrder = in.workSectionOrder;
        this.workPropCheckpoint = in.workPropCheckpoint;
    }

    public WorkPropertyEntity(Long fkMasterId, String workPropName, String workPropType, String workPropValue, Integer workPropOrder,
            String workPropCategory, String workPropOption, Double workPropLowerTolerance, Double workPropUpperTolerance, String workPropValidationRule) {
        this.fkMasterId = fkMasterId;
        this.workPropName = workPropName;
        this.workPropType = workPropType;
        this.workPropValue = workPropValue;
        this.workPropOrder = workPropOrder;
        this.workPropCategory = workPropCategory;
        this.workPropOption = workPropOption;
        this.workPropLowerTolerance = workPropLowerTolerance;
        this.workPropUpperTolerance = workPropUpperTolerance;
        this.workPropValidationRule = workPropValidationRule;
    }

    public Long getWorkPropId() {
        return workPropId;
    }

    public void setWorkPropId(Long workPropId) {
        this.workPropId = workPropId;
    }

    public Long getFkMasterId() {
        return fkMasterId;
    }

    public void setFkMasterId(Long fkMasterId) {
        this.fkMasterId = fkMasterId;
    }

    public String getWorkPropName() {
        return workPropName;
    }

    public void setWorkPropName(String workPropName) {
        this.workPropName = workPropName;
    }

    public String getWorkPropType() {
        return workPropType;
    }

    public void setWorkPropType(String workPropType) {
        this.workPropType = workPropType;
    }

    public String getWorkPropValue() {
        return workPropValue;
    }

    public void setWorkPropValue(String workPropValue) {
        this.workPropValue = workPropValue;
    }

    public Integer getWorkPropOrder() {
        return workPropOrder;
    }

    public void setWorkPropOrder(Integer workPropOrder) {
        this.workPropOrder = workPropOrder;
    }

    public String getWorkPropCategory() {
        return this.workPropCategory;
    }

    public void setWorkPropCategory(String workPropCategory) {
        this.workPropCategory = workPropCategory;
    }

    public String getWorkPropOption() {
        return this.workPropOption;
    }

    public void setWorkPropOption(String workPropOption) {
        this.workPropOption = workPropOption;
    }

    public Double getWorkPropLowerTolerance() {
        return this.workPropLowerTolerance;
    }

    public void setWorkPropLowerTolerance(Double workPropLowerTolerance) {
        this.workPropLowerTolerance = workPropLowerTolerance;
    }

    public Double getWorkPropUpperTolerance() {
        return this.workPropUpperTolerance;
    }

    public void setWorkPropUpperTolerance(Double workPropUpperTolerance) {
        this.workPropUpperTolerance = workPropUpperTolerance;
    }

    public String getWorkPropTag() {
        return this.workPropTag;
    }

    public void setWorkPropTag(String workPropTag) {
        this.workPropTag = workPropTag;
    }

    public String getWorkPropValidationRule() {
        return this.workPropValidationRule;
    }

    public void setWorkPropValidationRule(String workPropValidationRule) {
        this.workPropValidationRule = workPropValidationRule;
    }

    public Integer getWorkSectionOrder() {
        return this.workSectionOrder;
    }

    public void setWorkSectionOrder(Integer workSectionOrder) {
        this.workSectionOrder = workSectionOrder;
    }

    public Integer getWorkPropCheckpoint() {
        return this.workPropCheckpoint;
    }

    public void setWorkPropCheckpoint(Integer workPropCheckpoint) {
        this.workPropCheckpoint = workPropCheckpoint;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (workPropId != null ? workPropId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorkPropertyEntity)) {
            return false;
        }
        WorkPropertyEntity other = (WorkPropertyEntity) object;
        if ((this.workPropId == null && other.workPropId != null) || (this.workPropId != null && !this.workPropId.equals(other.workPropId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "WorkPropertyEntity{" + "workPropId=" + workPropId + ", fkMasterId=" + fkMasterId + ", workPropName=" + workPropName + ", "
                + "workPropType=" + workPropType + ", workPropValue=" + workPropValue + ", workPropOrder=" + workPropOrder + ", "
                + "workPropCategory=" + workPropCategory + ", workPropOption=" + workPropOption + ", workPropLowerTolerance=" + workPropLowerTolerance + ", "
                + "workPropUpperTolerance=" + workPropUpperTolerance + "workPropValidationRule=" + workPropValidationRule + '}';
    }
}
