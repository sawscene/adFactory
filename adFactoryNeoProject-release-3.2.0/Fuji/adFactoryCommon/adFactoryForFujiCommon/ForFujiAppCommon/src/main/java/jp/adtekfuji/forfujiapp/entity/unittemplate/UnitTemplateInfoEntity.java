/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.entity.unittemplate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
 * @since 2016.10.26.Wen
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "unittemplate")
public class UnitTemplateInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(required = true)
    private Long unitTemplateId;
    @XmlElement()
    private Long parentId;
    private String parentName;
    @XmlElement()
    private String unitTemplateName;
    @XmlElement()
    private String workflowDiaglam;
    @XmlElement()
    private Long fkOutputKanbanHierarchyId;
    @XmlElement()
    private String OutputKanbanHierarchyName;
    @XmlElement()
    private Long fkUpdatePersonId;
    @XmlElement()
    private Date updateDatetime;
    @XmlElement()
    private Boolean removeFlag;
    @XmlElementWrapper(name = "unittemplatePropertys")
    @XmlElement(name = "unittemplateProperty")
    private List<UnitTemplatePropertyInfoEntity> unittemplatePropertyCollection = null;
    @XmlElementWrapper(name = "conUnitTemplateAssociates")
    @XmlElement(name = "conUnitTemplateAssociate")
    private List<ConUnitTemplateAssociateInfoEntity> conUnitTemplateAssociateCollection = null;
    private Long tactTime = 0L;

    public UnitTemplateInfoEntity() {
    }

    public UnitTemplateInfoEntity(Long unitTemplateId) {
        this.unitTemplateId = unitTemplateId;
    }

    public UnitTemplateInfoEntity(Long unitTemplateId, String unitTemplateName, String workflowDiaglam) {
        this.unitTemplateId = unitTemplateId;
        this.unitTemplateName = unitTemplateName;
        this.workflowDiaglam = workflowDiaglam;
    }

    public UnitTemplateInfoEntity(Long parentId, String unitTemplateName, String workflowDiaglam, Long fkOutputKanbanHierarchyId, Long fkUpdatePersonId, Date updateDatetime) {
        this.parentId = parentId;
        this.unitTemplateName = unitTemplateName;
        this.workflowDiaglam = workflowDiaglam;
        this.fkOutputKanbanHierarchyId = fkOutputKanbanHierarchyId;
        this.fkUpdatePersonId = fkUpdatePersonId;
        this.updateDatetime = updateDatetime;
        this.removeFlag = false;
    }

    public UnitTemplateInfoEntity(UnitTemplateInfoEntity in) {
        this.unitTemplateId = in.unitTemplateId;
        this.parentId = in.parentId;
        this.unitTemplateName = in.unitTemplateName;
        this.workflowDiaglam = in.workflowDiaglam;
        this.fkOutputKanbanHierarchyId = in.fkOutputKanbanHierarchyId;
        this.fkUpdatePersonId = in.fkUpdatePersonId;
        this.updateDatetime = in.updateDatetime;
        this.removeFlag = in.removeFlag;
        this.unittemplatePropertyCollection = new ArrayList<>();
        for (UnitTemplatePropertyInfoEntity property : in.getUnitTemplatePropertyCollection()) {
            this.unittemplatePropertyCollection.add(new UnitTemplatePropertyInfoEntity(property));
        }
        this.conUnitTemplateAssociateCollection = new ArrayList<>();
        for (ConUnitTemplateAssociateInfoEntity connect : in.getConUnitTemplateAssociateCollection()) {
            this.conUnitTemplateAssociateCollection.add(new ConUnitTemplateAssociateInfoEntity(connect));
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

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
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

    public List<UnitTemplatePropertyInfoEntity> getUnitTemplatePropertyCollection() {
        return unittemplatePropertyCollection;
    }

    public void setUnitTemplatePropertyCollection(List<UnitTemplatePropertyInfoEntity> unittemplatePropertyCollection) {
        this.unittemplatePropertyCollection = unittemplatePropertyCollection;
    }

    public List<ConUnitTemplateAssociateInfoEntity> getConUnitTemplateAssociateCollection() {
        return conUnitTemplateAssociateCollection;
    }

    public void setConUnitTemplateAssociateCollection(List<ConUnitTemplateAssociateInfoEntity> conUnitTemplateAssociatekCollection) {
        this.conUnitTemplateAssociateCollection = conUnitTemplateAssociatekCollection;
    }

    public Long getTactTime() {
        if (Objects.nonNull(this.conUnitTemplateAssociateCollection) && !this.conUnitTemplateAssociateCollection.isEmpty()) {
            if (getConUnitTemplateAssociateCollection().size() == 1) {
                tactTime = getConUnitTemplateAssociateCollection().get(0).getTaktTime();
            } else if (getConUnitTemplateAssociateCollection().size() > 1) {
                Long min = getConUnitTemplateAssociateCollection().get(0).getStandardStartTime().getTime();
                Long max = getConUnitTemplateAssociateCollection().get(0).getStandardEndTime().getTime();
                for (ConUnitTemplateAssociateInfoEntity con : getConUnitTemplateAssociateCollection()) {
                    if (min > con.getStandardStartTime().getTime()) {
                        min = con.getStandardStartTime().getTime();
                    }
                    if (max < con.getStandardEndTime().getTime()) {
                        max = con.getStandardEndTime().getTime();
                    }
                }
                tactTime = max - min;
            }
        }
        return tactTime;
    }

    public void setTactTime(Long tactTime) {
        this.tactTime = tactTime;
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
        if (!(object instanceof UnitTemplateInfoEntity)) {
            return false;
        }
        UnitTemplateInfoEntity other = (UnitTemplateInfoEntity) object;
        return !((this.unitTemplateId == null && other.unitTemplateId != null) || (this.unitTemplateId != null && !this.unitTemplateId.equals(other.unitTemplateId)));
    }

    @Override
    public String toString() {
        return "UnitTemplateInfoEntity{" + "unitTemplateId=" + getUnitTemplateId() + ", parentId=" + getParentId() + ", unitTemplateName=" + getUnitTemplateName() + ", fkUpdatePersonId=" + getFkUpdatePersonId() + ", updateDatetime=" + getUpdateDatetime() + '}';
    }

}
