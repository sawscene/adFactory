/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.equipment;

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
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ke.yokoi
 */
@XmlRootElement(name = "equipment")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "mst_equipment")
@NamedQueries({
    @NamedQuery(name = "EquipmentEntity.countWorkflowWorkAssociation", query = "SELECT COUNT(c.conWorkEquipmentEntityPK.fkWorkId) FROM ConWorkEquipmentEntity c WHERE c.conWorkEquipmentEntityPK.fkEquipmentId = :fkEquipmentId"),
    @NamedQuery(name = "EquipmentEntity.countWorkflowSeparateWorkAssociation", query = "SELECT COUNT(c.conSeparateworkEquipmentEntityPK.fkWorkId) FROM ConSeparateworkEquipmentEntity c WHERE c.conSeparateworkEquipmentEntityPK.fkEquipmentId = :fkEquipmentId"),
    @NamedQuery(name = "EquipmentEntity.countKanbanAssociation", query = "SELECT COUNT(c.conWorkkanbanEquipmentEntityPK.fkWorkkanbanId) FROM ConWorkkanbanEquipmentEntity c WHERE c.conWorkkanbanEquipmentEntityPK.fkEquipmentId = :fkEquipmentId"),
    @NamedQuery(name = "EquipmentEntity.countByEquipmentType", query = "SELECT COUNT(e.equipmentId) FROM EquipmentEntity e WHERE e.fkEquipmentTypeId = :fkEquipmentTypeId AND e.removeFlag = false"),

    // 設備識別名の重複確認 (追加時) ※.削除済も対象
    @NamedQuery(name = "EquipmentEntity.checkAddByEquipmentIdentify", query = "SELECT COUNT(e.equipmentId) FROM EquipmentEntity e WHERE e.equipmentIdentify = :equipmentIdentify"),
    // 設備識別名の重複確認 (更新時) ※.削除済も対象
    @NamedQuery(name = "EquipmentEntity.checkUpdateByEquipmentIdentify", query = "SELECT COUNT(e.equipmentId) FROM EquipmentEntity e WHERE e.equipmentIdentify = :equipmentIdentify AND e.equipmentId != :equipmentId"),

    @NamedQuery(name = "EquipmentEntity.findAll", query = "SELECT e FROM EquipmentEntity e WHERE e.removeFlag = false"),
    @NamedQuery(name = "EquipmentEntity.findByEquipmentId", query = "SELECT e FROM EquipmentEntity e WHERE e.equipmentId = :equipmentId AND e.removeFlag = false"),
    @NamedQuery(name = "EquipmentEntity.findByEquipmentName", query = "SELECT e FROM EquipmentEntity e WHERE e.equipmentName = :equipmentName AND e.removeFlag = false"),
    @NamedQuery(name = "EquipmentEntity.findByEquipmentIdentify", query = "SELECT e FROM EquipmentEntity e WHERE e.equipmentIdentify = :equipmentIdentify AND e.removeFlag = false"),
    @NamedQuery(name = "EquipmentEntity.findByFfkEquipmentTypeId", query = "SELECT e FROM EquipmentEntity e WHERE e.fkEquipmentTypeId = :fkEquipmentTypeId AND e.removeFlag = false"),
    @NamedQuery(name = "EquipmentEntity.findByFkUpdatePersonId", query = "SELECT e FROM EquipmentEntity e WHERE e.fkUpdatePersonId = :fkUpdatePersonId AND e.removeFlag = false"),
    @NamedQuery(name = "EquipmentEntity.findByUpdateDatetime", query = "SELECT e FROM EquipmentEntity e WHERE e.updateDatetime = :updateDatetime AND e.removeFlag = false"),
    @NamedQuery(name = "EquipmentEntity.findByRemoveFlag", query = "SELECT e FROM EquipmentEntity e WHERE e.removeFlag = :removeFlag"),
    // 親設備に属する設備を問い合わせる
    @NamedQuery(name = "EquipmentEntity.findByParentId", query = "SELECT e FROM EquipmentHierarchyEntity pk JOIN EquipmentEntity e ON pk.equipmentHierarchyEntityPK.childId = e.equipmentId WHERE pk.equipmentHierarchyEntityPK.parentId = :parentId ORDER BY e.equipmentName")})
public class EquipmentEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "equipment_id")
    private Long equipmentId;
    @Basic(optional = false)
    @NotNull
    @Size(max = 256)
    @Column(name = "equipment_name")
    private String equipmentName;
    @Basic(optional = false)
    @Size(min = 1, max = 256)
    @Column(name = "equipment_identify")
    private String equipmentIdentify;
    @Transient
    private Long parentId;
    @Transient
    private String parentName;
    @Transient
    private String parentIdentName;
    @XmlElement(name = "equipmentTypeId")
    @Column(name = "fk_equipment_type_id")
    private Long fkEquipmentTypeId;
    @XmlElement(name = "updatePersonId")
    @Column(name = "fk_update_person_id")
    private Long fkUpdatePersonId;
    @Column(name = "update_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDatetime;
    @XmlTransient
    @Column(name = "remove_flag")
    private Boolean removeFlag = false;
    @Transient
    private Long childCount = 0L;
    @XmlElementWrapper(name = "equipmentPropertys")
    @XmlElement(name = "equipmentProperty")
    @Transient
    private List<EquipmentPropertyEntity> propertyCollection = null;
    @XmlElementWrapper(name = "equipmentSettings")
    @XmlElement(name = "equipmentSetting")
    @Transient
    private List<EquipmentSettingEntity> settingCollection = null;

    public EquipmentEntity() {
        this.removeFlag = false;
    }

    public EquipmentEntity(EquipmentEntity in) {
        this.parentId = in.parentId;
        this.equipmentName = in.equipmentName;
        this.equipmentIdentify = in.equipmentIdentify;
        this.fkEquipmentTypeId = in.fkEquipmentTypeId;
        this.fkUpdatePersonId = in.fkUpdatePersonId;
        this.updateDatetime = in.updateDatetime;
        this.removeFlag = in.removeFlag;
        this.propertyCollection = new ArrayList<>();
        for (EquipmentPropertyEntity property : in.getPropertyCollection()) {
            this.propertyCollection.add(new EquipmentPropertyEntity(property));
        }
        this.settingCollection = new ArrayList<>();
        for (EquipmentSettingEntity setting : in.getSettingCollection()) {
            this.settingCollection.add(new EquipmentSettingEntity(setting));
        }
    }

    public EquipmentEntity(Long parentId, String equipmentName, String equipmentIdentify, Long fkEquipmentTypeId, Long fkUpdatePersonId, Date updateDatetime) {
        this.parentId = parentId;
        this.equipmentName = equipmentName;
        this.equipmentIdentify = equipmentIdentify;
        this.fkEquipmentTypeId = fkEquipmentTypeId;
        this.fkUpdatePersonId = fkUpdatePersonId;
        this.updateDatetime = updateDatetime;
        this.removeFlag = false;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getEquipmentIdentify() {
        return equipmentIdentify;
    }

    public void setEquipmentIdentify(String equipmentIdentify) {
        this.equipmentIdentify = equipmentIdentify;
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

    public String getParentIdentName() {
        return parentIdentName;
    }

    public void setParentIdentName(String parentIdentName) {
        this.parentIdentName = parentIdentName;
    }

    public Long getFkEquipmentTypeId() {
        return fkEquipmentTypeId;
    }

    public void setFkEquipmentTypeId(Long fkEquipmentTypeId) {
        this.fkEquipmentTypeId = fkEquipmentTypeId;
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

    public Long getChildCount() {
        return childCount;
    }

    public void setChildCount(Long childCount) {
        this.childCount = childCount;
    }

    public List<EquipmentPropertyEntity> getPropertyCollection() {
        return propertyCollection;
    }

    public void setPropertyCollection(List<EquipmentPropertyEntity> propertyCollection) {
        this.propertyCollection = propertyCollection;
    }

    public List<EquipmentSettingEntity> getSettingCollection() {
        return settingCollection;
    }

    public void setSettingCollection(List<EquipmentSettingEntity> settingCollection) {
        this.settingCollection = settingCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (equipmentId != null ? equipmentId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EquipmentEntity)) {
            return false;
        }
        EquipmentEntity other = (EquipmentEntity) object;
        if ((this.equipmentId == null && other.equipmentId != null) || (this.equipmentId != null && !this.equipmentId.equals(other.equipmentId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "EquipmentEntity{" + "equipmentId=" + equipmentId + ", parentId=" + parentId + ", equipmentName=" + equipmentName + ", equipmentIdentify=" + equipmentIdentify + ", fkEquipmentTypeId=" + fkEquipmentTypeId + ", fkUpdatePersonId=" + fkUpdatePersonId + ", updateDatetime=" + updateDatetime + ", removeFlag=" + removeFlag + '}';
    }

}
