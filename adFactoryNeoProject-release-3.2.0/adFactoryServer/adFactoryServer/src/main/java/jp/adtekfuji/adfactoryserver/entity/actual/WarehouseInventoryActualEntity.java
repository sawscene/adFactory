/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.actual;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

/**
 *
 * @author nar-nakamura
 */
@Entity
@Table(name = "tmp_warehouse_inventory_actual")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "warehouseInventoryActual")
public class WarehouseInventoryActualEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "equipment_id")
    private Long equipmentId;
    @Id
    @Column(name = "actual_id")
    private Long actualId;

    @Column(name = "equipment_name")
    private String equipmentName;
    @Column(name = "equipment_identify")
    private String equipmentIdentify;
    @Column(name = "fk_equipment_type_id")
    private Long equipmentTypeId;
    @Column(name = "fk_update_person_id")
    private Long updatePersonId;
    @Column(name = "update_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDatetime;
    @Column(name = "remove_flag")
    private Boolean removeFlag = false;

    @Column(name = "storage_name")
    private String storageName;
    @Column(name = "inventory")
    private String inventory;
    @Column(name = "reserve_inventory")
    private String reserveInventory;
    @Column(name = "in_process_inventory")
    private String inProcessInventory;
    @Column(name = "inventory_stock")
    private String inventoryStock;
    @Column(name = "inventory_temp")
    private String inventoryTemp;

    @Column(name = "management")
    private String management;
    @Column(name = "product")
    private String product;
    @Column(name = "standard")
    private String standard;
    @Column(name = "material")
    private String material;
    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "fk_workflow_id")
    private Long workflowId;
    @Column(name = "implement_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date implementDatetime;
    @Column(name = "fk_organization_id")
    private Long organizationId;
    @Enumerated(EnumType.STRING)
    @Column(name = "actual_status")
    private KanbanStatusEnum actualStatus;

    @Column(name = "organization_identify")
    private String organizationIdentify;
    @Column(name = "organization_name")
    private String organizationName;

    @Column(name = "actual_storage_name")
    private String actualStorageName;
    @Column(name = "actual_inventory_stock")
    private String actualInventoryStock;
    @Column(name = "actual_difference")
    private String actualDifference;
    @Column(name = "actual_affiliation_name")
    private String actualAffiliationName;
    @Column(name = "actual_affiliation_code")
    private String actualAffiliationCode;
    @Column(name = "actual_stktake_label_no")
    private String actualStktakeLabelNo;

    public WarehouseInventoryActualEntity() {
        this.removeFlag = false;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Long getActualId() {
        return actualId;
    }

    public void setActualId(Long actualId) {
        this.actualId = actualId;
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

    public Long getEquipmentTypeId() {
        return equipmentTypeId;
    }

    public void setEquipmentTypeId(Long equipmentTypeId) {
        this.equipmentTypeId = equipmentTypeId;
    }

    public Long getUpdatePersonId() {
        return updatePersonId;
    }

    public void setUpdatePersonId(Long updatePersonId) {
        this.updatePersonId = updatePersonId;
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

    public String getStorageName() {
        return storageName;
    }

    public void setStorageName(String storageName) {
        this.storageName = storageName;
    }

    public String getInventory() {
        return inventory;
    }

    public void setInventory(String inventory) {
        this.inventory = inventory;
    }

    public String getReserveInventory() {
        return reserveInventory;
    }

    public void setReserveInventory(String reserveInventory) {
        this.reserveInventory = reserveInventory;
    }

    public String getInProcessInventory() {
        return inProcessInventory;
    }

    public void setInProcessInventory(String inProcessInventory) {
        this.inProcessInventory = inProcessInventory;
    }

    public String getInventoryStock() {
        return inventoryStock;
    }

    public void setInventoryStock(String inventoryStock) {
        this.inventoryStock = inventoryStock;
    }

    public String getInventoryTemp() {
        return inventoryTemp;
    }

    public void setInventoryTemp(String inventoryTemp) {
        this.inventoryTemp = inventoryTemp;
    }

    public String getManagement() {
        return management;
    }

    public void setManagement(String management) {
        this.management = management;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    public Date getImplementDatetime() {
        return implementDatetime;
    }

    public void setImplementDatetime(Date implementDatetime) {
        this.implementDatetime = implementDatetime;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public KanbanStatusEnum getActualStatus() {
        return actualStatus;
    }

    public void setActualStatus(KanbanStatusEnum actualStatus) {
        this.actualStatus = actualStatus;
    }

    public String getOrganizationIdentify() {
        return organizationIdentify;
    }

    public void setOrganizationIdentify(String organizationIdentify) {
        this.organizationIdentify = organizationIdentify;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getActualStorageName() {
        return actualStorageName;
    }

    public void setActualStorageName(String actualStorageName) {
        this.actualStorageName = actualStorageName;
    }

    public String getActualInventoryStock() {
        return actualInventoryStock;
    }

    public void setActualInventoryStock(String actualInventoryStock) {
        this.actualInventoryStock = actualInventoryStock;
    }

    public String getActualDifference() {
        return actualDifference;
    }

    public void setActualDifference(String actualDifference) {
        this.actualDifference = actualDifference;
    }

    public String getActualAffiliationName() {
        return actualAffiliationName;
    }

    public void setActualAffiliationName(String actualAffiliationName) {
        this.actualAffiliationName = actualAffiliationName;
    }

    public String getActualAffiliationCode() {
        return actualAffiliationCode;
    }

    public void setActualAffiliationCode(String actualAffiliationCode) {
        this.actualAffiliationCode = actualAffiliationCode;
    }

    public String getActualStktakeLabelNo() {
        return actualStktakeLabelNo;
    }

    public void setActualStktakeLabelNo(String actualStktakeLabelNo) {
        this.actualStktakeLabelNo = actualStktakeLabelNo;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.equipmentId);
        hash = 89 * hash + Objects.hashCode(this.actualId);
        hash = 89 * hash + Objects.hashCode(this.equipmentName);
        hash = 89 * hash + Objects.hashCode(this.equipmentIdentify);
        hash = 89 * hash + Objects.hashCode(this.equipmentTypeId);
        hash = 89 * hash + Objects.hashCode(this.updatePersonId);
        hash = 89 * hash + Objects.hashCode(this.updateDatetime);
        hash = 89 * hash + Objects.hashCode(this.removeFlag);
        hash = 89 * hash + Objects.hashCode(this.storageName);
        hash = 89 * hash + Objects.hashCode(this.inventory);
        hash = 89 * hash + Objects.hashCode(this.reserveInventory);
        hash = 89 * hash + Objects.hashCode(this.inProcessInventory);
        hash = 89 * hash + Objects.hashCode(this.inventoryStock);
        hash = 89 * hash + Objects.hashCode(this.inventoryTemp);
        hash = 89 * hash + Objects.hashCode(this.management);
        hash = 89 * hash + Objects.hashCode(this.product);
        hash = 89 * hash + Objects.hashCode(this.standard);
        hash = 89 * hash + Objects.hashCode(this.material);
        hash = 89 * hash + Objects.hashCode(this.manufacturer);
        hash = 89 * hash + Objects.hashCode(this.workflowId);
        hash = 89 * hash + Objects.hashCode(this.implementDatetime);
        hash = 89 * hash + Objects.hashCode(this.organizationId);
        hash = 89 * hash + Objects.hashCode(this.actualStatus);
        hash = 89 * hash + Objects.hashCode(this.organizationIdentify);
        hash = 89 * hash + Objects.hashCode(this.organizationName);
        hash = 89 * hash + Objects.hashCode(this.actualStorageName);
        hash = 89 * hash + Objects.hashCode(this.actualInventoryStock);
        hash = 89 * hash + Objects.hashCode(this.actualDifference);
        hash = 89 * hash + Objects.hashCode(this.actualAffiliationName);
        hash = 89 * hash + Objects.hashCode(this.actualAffiliationCode);
        hash = 89 * hash + Objects.hashCode(this.actualStktakeLabelNo);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WarehouseInventoryActualEntity other = (WarehouseInventoryActualEntity) obj;
        if (!Objects.equals(this.equipmentId, other.equipmentId)) {
            return false;
        }
        if (!Objects.equals(this.actualId, other.actualId)) {
            return false;
        }
        if (!Objects.equals(this.equipmentName, other.equipmentName)) {
            return false;
        }
        if (!Objects.equals(this.equipmentIdentify, other.equipmentIdentify)) {
            return false;
        }
        if (!Objects.equals(this.equipmentTypeId, other.equipmentTypeId)) {
            return false;
        }
        if (!Objects.equals(this.updatePersonId, other.updatePersonId)) {
            return false;
        }
        if (!Objects.equals(this.updateDatetime, other.updateDatetime)) {
            return false;
        }
        if (!Objects.equals(this.removeFlag, other.removeFlag)) {
            return false;
        }
        if (!Objects.equals(this.storageName, other.storageName)) {
            return false;
        }
        if (!Objects.equals(this.inventory, other.inventory)) {
            return false;
        }
        if (!Objects.equals(this.reserveInventory, other.reserveInventory)) {
            return false;
        }
        if (!Objects.equals(this.inProcessInventory, other.inProcessInventory)) {
            return false;
        }
        if (!Objects.equals(this.inventoryStock, other.inventoryStock)) {
            return false;
        }
        if (!Objects.equals(this.inventoryTemp, other.inventoryTemp)) {
            return false;
        }
        if (!Objects.equals(this.management, other.management)) {
            return false;
        }
        if (!Objects.equals(this.product, other.product)) {
            return false;
        }
        if (!Objects.equals(this.standard, other.standard)) {
            return false;
        }
        if (!Objects.equals(this.material, other.material)) {
            return false;
        }
        if (!Objects.equals(this.manufacturer, other.manufacturer)) {
            return false;
        }
        if (!Objects.equals(this.workflowId, other.workflowId)) {
            return false;
        }
        if (!Objects.equals(this.implementDatetime, other.implementDatetime)) {
            return false;
        }
        if (!Objects.equals(this.organizationId, other.organizationId)) {
            return false;
        }
        if (this.actualStatus != other.actualStatus) {
            return false;
        }
        if (!Objects.equals(this.organizationIdentify, other.organizationIdentify)) {
            return false;
        }
        if (!Objects.equals(this.organizationName, other.organizationName)) {
            return false;
        }
        if (!Objects.equals(this.actualStorageName, other.actualStorageName)) {
            return false;
        }
        if (!Objects.equals(this.actualInventoryStock, other.actualInventoryStock)) {
            return false;
        }
        if (!Objects.equals(this.actualDifference, other.actualDifference)) {
            return false;
        }
        if (!Objects.equals(this.actualAffiliationName, other.actualAffiliationName)) {
            return false;
        }
        if (!Objects.equals(this.actualAffiliationCode, other.actualAffiliationCode)) {
            return false;
        }
        if (!Objects.equals(this.actualStktakeLabelNo, other.actualStktakeLabelNo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "WarehouseInventoryActualEntity{"
                + "equipmentId=" + equipmentId
                + "actualId=" + actualId
                + ", equipmentName=" + equipmentName
                + ", equipmentIdentify=" + equipmentIdentify
                + ", equipmentTypeId=" + equipmentTypeId
                + ", updatePersonId=" + updatePersonId
                + ", updateDatetime=" + updateDatetime
                + ", removeFlag=" + removeFlag
                + ", storageName=" + storageName
                + ", inventory=" + inventory
                + ", reserveInventory=" + reserveInventory
                + ", inProcessInventory=" + inProcessInventory
                + ", inventoryStock=" + inventoryStock
                + ", inventoryTemp=" + inventoryTemp
                + ", management=" + management
                + ", product=" + product
                + ", standard=" + standard
                + ", material=" + material
                + ", manufacturer=" + manufacturer
                + ", workflowId=" + workflowId
                + ", implementDatetime=" + implementDatetime
                + ", organizationId=" + organizationId
                + ", actualStatus=" + actualStatus
                + ", organizationIdentify=" + organizationIdentify
                + ", organizationName=" + organizationName
                + ", actualStorageName=" + actualStorageName
                + ", actualInventoryStock=" + actualInventoryStock
                + ", actualDifference=" + actualDifference
                + ", actualAffiliationName=" + actualAffiliationName
                + ", actualAffiliationCode=" + actualAffiliationCode
                + ", actualStktakeLabelNo=" + actualStktakeLabelNo
                + '}';
    }
}