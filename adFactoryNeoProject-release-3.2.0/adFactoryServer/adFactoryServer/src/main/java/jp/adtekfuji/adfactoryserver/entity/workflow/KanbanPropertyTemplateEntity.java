/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.workflow;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ke.yokoi
 */
@XmlRootElement(name = "kanbanPropertyTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class KanbanPropertyTemplateEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long kanbanPropTemplateId;
    private Long fkMasterId;
    private String propertyName;
    private String propertyType;
    private String propInitialValue;
    private Integer propertyOrder;

    public KanbanPropertyTemplateEntity() {
    }

    public KanbanPropertyTemplateEntity(KanbanPropertyTemplateEntity in) {
        this.fkMasterId = in.fkMasterId;
        this.propertyName = in.propertyName;
        this.propertyType = in.propertyType;
        this.propInitialValue = in.propInitialValue;
        this.propertyOrder = in.propertyOrder;
    }

    public KanbanPropertyTemplateEntity(Long fkMasterId, String workPropName, String workPropType, String workPropInitialValue, Integer workPropOrder) {
        this.fkMasterId = fkMasterId;
        this.propertyName = workPropName;
        this.propertyType = workPropType;
        this.propInitialValue = workPropInitialValue;
        this.propertyOrder = workPropOrder;
    }

    public Long getKanbanPropTemplateId() {
        return kanbanPropTemplateId;
    }

    public void setKanbanPropTemplateId(Long workPropId) {
        this.kanbanPropTemplateId = workPropId;
    }

    public Long getFkMasterId() {
        return fkMasterId;
    }

    public void setFkMasterId(Long fkMasterId) {
        this.fkMasterId = fkMasterId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getPropInitialValue() {
        return propInitialValue;
    }

    public void setPropInitialValue(String propInitialValue) {
        this.propInitialValue = propInitialValue;
    }

    public Integer getPropertyOrder() {
        return propertyOrder;
    }

    public void setPropertyOrder(Integer propertyOrder) {
        this.propertyOrder = propertyOrder;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (kanbanPropTemplateId != null ? kanbanPropTemplateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof KanbanPropertyTemplateEntity)) {
            return false;
        }
        KanbanPropertyTemplateEntity other = (KanbanPropertyTemplateEntity) object;
        if ((this.kanbanPropTemplateId == null && other.kanbanPropTemplateId != null) || (this.kanbanPropTemplateId != null && !this.kanbanPropTemplateId.equals(other.kanbanPropTemplateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryserver.entity.workflow.KanbanPropertyTemplateEntity[ workPropId=" + kanbanPropTemplateId + " ]";
    }
    
}
