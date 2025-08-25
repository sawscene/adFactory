/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.actual;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

/**
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "warehouseInventoryActual")
public class WarehouseInventoryActualEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty equipmentIdProperty;
    private LongProperty actualIdProperty;

    private StringProperty equipmentNameProperty;
    private StringProperty equipmentIdentifyProperty;
    private LongProperty equipmentTypeIdProperty;
    private LongProperty updatePersonIdProperty;
    private ObjectProperty<Date> updateDateTimeProperty;

    private StringProperty storageNameProperty;
    private StringProperty inventoryProperty;
    private StringProperty reserveInventoryProperty;
    private StringProperty inProcessInventoryProperty;
    private StringProperty inventoryStockProperty;
    private StringProperty inventoryTempProperty;

    private StringProperty managementProperty;
    private StringProperty productProperty;
    private StringProperty standardProperty;
    private StringProperty materialProperty;
    private StringProperty manufacturerProperty;

    private LongProperty workflowIdProperty;
    private ObjectProperty<Date> implementDatetimeProperty;
    private LongProperty organizationIdProperty;
    private ObjectProperty<KanbanStatusEnum> actualStatusProperty;

    private StringProperty organizationIdentifyProperty;
    private StringProperty organizationNameProperty;

    private StringProperty actualStorageNameProperty;
    private StringProperty actualInventoryStockProperty;
    private StringProperty actualDifferenceProperty;
    private StringProperty actualAffiliationNameProperty;
    private StringProperty actualAffiliationCodeProperty;
    private StringProperty actualStktakeLabelNoProperty;

    private Long equipmentId;
    private Long actualId;

    private String equipmentName;
    private String equipmentIdentify;
    private Long equipmentTypeId;
    private Long updatePersonId;
    private Date updateDatetime;
    private Boolean removeFlag = false;

    private String storageName;
    private String inventory;
    private String reserveInventory;
    private String inProcessInventory;
    private String inventoryStock;
    private String inventoryTemp;

    private String management;
    private String product;
    private String standard;
    private String material;
    private String manufacturer;

    private Long workflowId;
    private Date implementDatetime;
    private Long organizationId;
    private KanbanStatusEnum actualStatus;

    private String organizationIdentify;
    private String organizationName;

    private String actualStorageName;
    private String actualInventoryStock;
    private String actualDifference;
    private String actualAffiliationName;
    private String actualAffiliationCode;
    private String actualStktakeLabelNo;

    public WarehouseInventoryActualEntity() {
    }

    public LongProperty equipmentIdProperty() {
        if (Objects.isNull(equipmentIdProperty)) {
            equipmentIdProperty = new SimpleLongProperty(equipmentId);
        }
        return equipmentIdProperty;
    }

    public LongProperty actualIdProperty() {
        if (Objects.isNull(actualIdProperty)) {
            actualIdProperty = new SimpleLongProperty(actualId);
        }
        return actualIdProperty;
    }

    public StringProperty equipmentIdentifyProperty() {
        if (Objects.isNull(equipmentIdentifyProperty)) {
            equipmentIdentifyProperty = new SimpleStringProperty(equipmentIdentify);
        }
        return equipmentIdentifyProperty;
    }

    public StringProperty equipmentNameProperty() {
        if (Objects.isNull(equipmentNameProperty)) {
            equipmentNameProperty = new SimpleStringProperty(equipmentName);
        }
        return equipmentNameProperty;
    }

    public LongProperty equipmentTypeIdProperty() {
        if (Objects.isNull(equipmentTypeIdProperty)) {
            equipmentTypeIdProperty = new SimpleLongProperty(equipmentTypeId);
        }
        return equipmentTypeIdProperty;
    }

    public LongProperty updatePersonIdProperty() {
        if (Objects.isNull(updatePersonIdProperty)) {
            updatePersonIdProperty = new SimpleLongProperty(updatePersonId);
        }
        return updatePersonIdProperty;
    }

    public ObjectProperty<Date> updateDateTimeProperty() {
        if (Objects.isNull(updateDateTimeProperty)) {
            updateDateTimeProperty = new SimpleObjectProperty(updateDatetime);
        }
        return updateDateTimeProperty;
    }

    public StringProperty storageNameProperty() {
        if (Objects.isNull(storageNameProperty)) {
            storageNameProperty = new SimpleStringProperty(storageName);
        }
        return storageNameProperty;
    }

    public StringProperty inventoryProperty() {
        if (Objects.isNull(inventoryProperty)) {
            inventoryProperty = new SimpleStringProperty(inventory);
        }
        return inventoryProperty;
    }

    public StringProperty reserveInventoryProperty() {
        if (Objects.isNull(reserveInventoryProperty)) {
            reserveInventoryProperty = new SimpleStringProperty(reserveInventory);
        }
        return reserveInventoryProperty;
    }

    public StringProperty inProcessInventoryProperty() {
        if (Objects.isNull(inProcessInventoryProperty)) {
            inProcessInventoryProperty = new SimpleStringProperty(inProcessInventory);
        }
        return inProcessInventoryProperty;
    }

    public StringProperty inventoryStockProperty() {
        if (Objects.isNull(inventoryStockProperty)) {
            inventoryStockProperty = new SimpleStringProperty(inventoryStock);
        }
        return inventoryStockProperty;
    }

    public StringProperty inventoryTempProperty() {
        if (Objects.isNull(inventoryTempProperty)) {
            inventoryTempProperty = new SimpleStringProperty(inventoryTemp);
        }
        return inventoryTempProperty;
    }

    public StringProperty managementProperty() {
        if (Objects.isNull(managementProperty)) {
            managementProperty = new SimpleStringProperty(management);
        }
        return managementProperty;
    }

    public StringProperty productProperty() {
        if (Objects.isNull(productProperty)) {
            productProperty = new SimpleStringProperty(product);
        }
        return productProperty;
    }

    public StringProperty standardProperty() {
        if (Objects.isNull(standardProperty)) {
            standardProperty = new SimpleStringProperty(standard);
        }
        return standardProperty;
    }

    public StringProperty materialProperty() {
        if (Objects.isNull(materialProperty)) {
            materialProperty = new SimpleStringProperty(material);
        }
        return materialProperty;
    }

    public StringProperty manufacturerProperty() {
        if (Objects.isNull(manufacturerProperty)) {
            manufacturerProperty = new SimpleStringProperty(manufacturer);
        }
        return manufacturerProperty;
    }

    public LongProperty workflowIdProperty() {
        if (Objects.isNull(workflowIdProperty)) {
            workflowIdProperty = new SimpleLongProperty(workflowId);
        }
        return workflowIdProperty;
    }

    public ObjectProperty<Date> implementDatetimeProperty() {
        if (Objects.isNull(implementDatetimeProperty)) {
            implementDatetimeProperty = new SimpleObjectProperty(implementDatetime);
        }
        return implementDatetimeProperty;
    }

    public LongProperty organizationIdProperty() {
        if (Objects.isNull(organizationIdProperty)) {
            organizationIdProperty = new SimpleLongProperty(organizationId);
        }
        return organizationIdProperty;
    }

    public ObjectProperty<KanbanStatusEnum> actualStatusProperty() {
        if (Objects.isNull(actualStatusProperty)) {
            actualStatusProperty = new SimpleObjectProperty(actualStatus);
        }
        return actualStatusProperty;
    }

    public StringProperty organizationIdentifyProperty() {
        if (Objects.isNull(organizationIdentifyProperty)) {
            organizationIdentifyProperty = new SimpleStringProperty(organizationIdentify);
        }
        return organizationIdentifyProperty;
    }

    public StringProperty organizationNameProperty() {
        if (Objects.isNull(organizationNameProperty)) {
            organizationNameProperty = new SimpleStringProperty(organizationName);
        }
        return organizationNameProperty;
    }

    public StringProperty actualStorageNameProperty() {
        if (Objects.isNull(actualStorageNameProperty)) {
            actualStorageNameProperty = new SimpleStringProperty(actualStorageName);
        }
        return actualStorageNameProperty;
    }

    public StringProperty actualInventoryStockProperty() {
        if (Objects.isNull(actualInventoryStockProperty)) {
            actualInventoryStockProperty = new SimpleStringProperty(actualInventoryStock);
        }
        return actualInventoryStockProperty;
    }

    public StringProperty actualDifferenceProperty() {
        if (Objects.isNull(actualDifferenceProperty)) {
            actualDifferenceProperty = new SimpleStringProperty(actualDifference);
        }
        return actualDifferenceProperty;
    }

    public StringProperty actualAffiliationNameProperty() {
        if (Objects.isNull(actualAffiliationNameProperty)) {
            actualAffiliationNameProperty = new SimpleStringProperty(actualAffiliationName);
        }
        return actualAffiliationNameProperty;
    }

    public StringProperty actualAffiliationCodeProperty() {
        if (Objects.isNull(actualAffiliationCodeProperty)) {
            actualAffiliationCodeProperty = new SimpleStringProperty(actualAffiliationCode);
        }
        return actualAffiliationCodeProperty;
    }

    public StringProperty actualStktakeLabelNoProperty() {
        if (Objects.isNull(actualStktakeLabelNoProperty)) {
            actualStktakeLabelNoProperty = new SimpleStringProperty(actualStktakeLabelNo);
        }
        return actualStktakeLabelNoProperty;
    }

    public Long getEquipmentId() {
        if (Objects.nonNull(equipmentIdProperty)) {
            return equipmentIdProperty.get();
        }
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        if (Objects.nonNull(equipmentIdProperty)) {
            equipmentIdProperty.set(equipmentId);
        } else {
            this.equipmentId = equipmentId;
        }
    }

    public Long getActualId() {
        if (Objects.nonNull(actualIdProperty)) {
            return actualIdProperty.get();
        }
        return actualId;
    }

    public void setActualId(Long actualId) {
        if (Objects.nonNull(actualIdProperty)) {
            actualIdProperty.set(actualId);
        } else {
            this.actualId = actualId;
        }
    }

    public String getEquipmentIdentify() {
        if (Objects.nonNull(equipmentIdentifyProperty)) {
            return equipmentIdentifyProperty.get();
        }
        return equipmentIdentify;
    }

    public void setEquipmentIdentify(String equipmentIdentify) {
        if (Objects.nonNull(equipmentIdentifyProperty)) {
            equipmentIdentifyProperty.set(equipmentIdentify);
        } else {
            this.equipmentIdentify = equipmentIdentify;
        }
    }

    public String getEquipmentName() {
        if (Objects.nonNull(equipmentNameProperty)) {
            return equipmentNameProperty.get();
        }
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        if (Objects.nonNull(equipmentNameProperty)) {
            equipmentNameProperty.set(equipmentName);
        } else {
            this.equipmentName = equipmentName;
        }
    }

    public Long getEquipmentType() {
        if (Objects.nonNull(equipmentTypeIdProperty)) {
            return equipmentTypeIdProperty.get();
        }
        return equipmentTypeId;
    }

    public void setEquipmentType(Long equipmentType) {
        if (Objects.nonNull(equipmentTypeIdProperty)) {
            equipmentTypeIdProperty.set(equipmentType);
        } else {
            this.equipmentTypeId = equipmentType;
        }
    }

    public Long getUpdatePersonId() {
        if (Objects.nonNull(updatePersonIdProperty)) {
            return updatePersonIdProperty.get();
        }
        return updatePersonId;
    }

    public void setUpdatePersonId(Long updatePersonId) {
        if (Objects.nonNull(updatePersonIdProperty)) {
            updatePersonIdProperty.set(updatePersonId);
        } else {
            this.updatePersonId = updatePersonId;
        }
    }

    public Date getUpdateDateTime() {
        if (Objects.nonNull(updateDateTimeProperty)) {
            return updateDateTimeProperty.get();
        }
        return updateDatetime;
    }

    public void setUpdateDateTime(Date updateDateTime) {
        if (Objects.nonNull(updateDateTimeProperty)) {
            updateDateTimeProperty.set(updateDateTime);
        } else {
            this.updateDatetime = updateDateTime;
        }
    }

    public String getStorageName() {
        if (Objects.nonNull(storageNameProperty)) {
            return storageNameProperty.get();
        }
        return storageName;
    }

    public void setStorageName(String storageName) {
        if (Objects.nonNull(storageNameProperty)) {
            storageNameProperty.set(storageName);
        } else {
            this.storageName = storageName;
        }
    }

    public String getInventory() {
        if (Objects.nonNull(inventoryProperty)) {
            return inventoryProperty.get();
        }
        return inventory;
    }

    public void setInventory(String inventory) {
        if (Objects.nonNull(inventoryProperty)) {
            inventoryProperty.set(inventory);
        } else {
            this.inventory = inventory;
        }
    }

    public String getReserveInventory() {
        if (Objects.nonNull(reserveInventoryProperty)) {
            return reserveInventoryProperty.get();
        }
        return reserveInventory;
    }

    public void setReserveInventory(String reserveInventory) {
        if (Objects.nonNull(reserveInventoryProperty)) {
            reserveInventoryProperty.set(reserveInventory);
        } else {
            this.reserveInventory = reserveInventory;
        }
    }

    public String getInProcessInventory() {
        if (Objects.nonNull(inProcessInventoryProperty)) {
            return inProcessInventoryProperty.get();
        }
        return inProcessInventory;
    }

    public void setInProcessInventory(String inProcessInventory) {
        if (Objects.nonNull(inProcessInventoryProperty)) {
            inProcessInventoryProperty.set(inProcessInventory);
        } else {
            this.inProcessInventory = inProcessInventory;
        }
    }

    public String getInventoryStock() {
        if (Objects.nonNull(inventoryStockProperty)) {
            return inventoryStockProperty.get();
        }
        return inventoryStock;
    }

    public void setInventoryStock(String inventoryStock) {
        if (Objects.nonNull(inventoryStockProperty)) {
            inventoryStockProperty.set(inventoryStock);
        } else {
            this.inventoryStock = inventoryStock;
        }
    }

    public String getInventoryTemp() {
        if (Objects.nonNull(inventoryTempProperty)) {
            return inventoryTempProperty.get();
        }
        return inventoryTemp;
    }

    public void setInventoryTemp(String inventoryTemp) {
        if (Objects.nonNull(inventoryTempProperty)) {
            inventoryTempProperty.set(inventoryTemp);
        } else {
            this.inventoryTemp = inventoryTemp;
        }
    }

    public String getManagement() {
        if (Objects.nonNull(managementProperty)) {
            return managementProperty.get();
        }
        return management;
    }

    public void setManagement(String management) {
        if (Objects.nonNull(managementProperty)) {
            managementProperty.set(management);
        } else {
            this.management = management;
        }
    }

    public String getProduct() {
        if (Objects.nonNull(productProperty)) {
            return productProperty.get();
        }
        return product;
    }

    public void setProduct(String product) {
        if (Objects.nonNull(productProperty)) {
            productProperty.set(product);
        } else {
            this.product = product;
        }
    }

    public String getStandard() {
        if (Objects.nonNull(standardProperty)) {
            return standardProperty.get();
        }
        return standard;
    }

    public void setStandard(String standard) {
        if (Objects.nonNull(standardProperty)) {
            standardProperty.set(standard);
        } else {
            this.standard = standard;
        }
    }

    public String getMaterial() {
        if (Objects.nonNull(materialProperty)) {
            return materialProperty.get();
        }
        return material;
    }

    public void setMaterial(String material) {
        if (Objects.nonNull(materialProperty)) {
            materialProperty.set(material);
        } else {
            this.material = material;
        }
    }

    public String getManufacturer() {
        if (Objects.nonNull(manufacturerProperty)) {
            return manufacturerProperty.get();
        }
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        if (Objects.nonNull(manufacturerProperty)) {
            manufacturerProperty.set(manufacturer);
        } else {
            this.manufacturer = manufacturer;
        }
    }

    public Long getWorkflowId() {
        if (Objects.nonNull(workflowIdProperty)) {
            return workflowIdProperty.get();
        }
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        if (Objects.nonNull(workflowIdProperty)) {
            workflowIdProperty.set(workflowId);
        } else {
            this.workflowId = workflowId;
        }
    }

    public Date getImplementDatetime() {
        if (Objects.nonNull(implementDatetimeProperty)) {
            return implementDatetimeProperty.get();
        }
        return implementDatetime;
    }

    public void setImplementDatetime(Date implementDatetime) {
        if (Objects.nonNull(implementDatetimeProperty)) {
            implementDatetimeProperty.set(implementDatetime);
        } else {
            this.implementDatetime = implementDatetime;
        }
    }

    public Long getOrganizationId() {
        if (Objects.nonNull(organizationIdProperty)) {
            return organizationIdProperty.get();
        }
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        if (Objects.nonNull(organizationIdProperty)) {
            organizationIdProperty.set(organizationId);
        } else {
            this.organizationId = organizationId;
        }
    }

    public KanbanStatusEnum getActualStatus() {
        if (Objects.nonNull(actualStatusProperty)) {
            return actualStatusProperty.get();
        }
        return actualStatus;
    }

    public void setActualStatus(KanbanStatusEnum actualStatus) {
        if (Objects.nonNull(actualStatusProperty)) {
            actualStatusProperty.set(actualStatus);
        } else {
            this.actualStatus = actualStatus;
        }
    }

    public String getOrganizationIdentify() {
        if (Objects.nonNull(organizationIdentifyProperty)) {
            return organizationIdentifyProperty.get();
        }
        return organizationIdentify;
    }

    public void setOrganizationIdentify(String organizationIdentify) {
        if (Objects.nonNull(organizationIdentifyProperty)) {
            organizationIdentifyProperty.set(organizationIdentify);
        } else {
            this.organizationIdentify = organizationIdentify;
        }
    }

    public String getOrganizationName() {
        if (Objects.nonNull(organizationNameProperty)) {
            return organizationNameProperty.get();
        }
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        if (Objects.nonNull(organizationNameProperty)) {
            organizationNameProperty.set(organizationName);
        } else {
            this.organizationName = organizationName;
        }
    }

    public String getActualStorageName() {
        if (Objects.nonNull(actualStorageNameProperty)) {
            return actualStorageNameProperty.get();
        }
        return actualStorageName;
    }

    public void setActualStorageName(String actualStorageName) {
        if (Objects.nonNull(actualStorageNameProperty)) {
            actualStorageNameProperty.set(actualStorageName);
        } else {
            this.actualStorageName = actualStorageName;
        }
    }

    public String getActualInventoryStock() {
        if (Objects.nonNull(actualInventoryStockProperty)) {
            return actualInventoryStockProperty.get();
        }
        return actualInventoryStock;
    }

    public void setActualInventoryStock(String actualInventoryStock) {
        if (Objects.nonNull(actualInventoryStockProperty)) {
            actualInventoryStockProperty.set(actualInventoryStock);
        } else {
            this.actualInventoryStock = actualInventoryStock;
        }
    }

    public String getActualDifference() {
        if (Objects.nonNull(actualDifferenceProperty)) {
            return actualDifferenceProperty.get();
        }
        return actualDifference;
    }

    public void setActualDifference(String actualDifference) {
        if (Objects.nonNull(actualDifferenceProperty)) {
            actualDifferenceProperty.set(actualDifference);
        } else {
            this.actualDifference = actualDifference;
        }
    }

    public String getActualAffiliationName() {
        if (Objects.nonNull(actualAffiliationNameProperty)) {
            return actualAffiliationNameProperty.get();
        }
        return actualAffiliationName;
    }

    public void setActualAffiliationName(String actualAffiliationName) {
        if (Objects.nonNull(actualAffiliationNameProperty)) {
            actualAffiliationNameProperty.set(actualAffiliationName);
        } else {
            this.actualAffiliationName = actualAffiliationName;
        }
    }

    public String getActualAffiliationCode() {
        if (Objects.nonNull(actualAffiliationCodeProperty)) {
            return actualAffiliationCodeProperty.get();
        }
        return actualAffiliationCode;
    }

    public void setActualAffiliationCode(String actualAffiliationCode) {
        if (Objects.nonNull(actualAffiliationCodeProperty)) {
            actualAffiliationCodeProperty.set(actualAffiliationCode);
        } else {
            this.actualAffiliationCode = actualAffiliationCode;
        }
    }

    public String getActualStktakeLabelNo() {
        if (Objects.nonNull(actualStktakeLabelNoProperty)) {
            return actualStktakeLabelNoProperty.get();
        }
        return actualStktakeLabelNo;
    }

    public void setActualStktakeLabelNo(String actualStktakeLabelNo) {
        if (Objects.nonNull(actualStktakeLabelNoProperty)) {
            actualStktakeLabelNoProperty.set(actualStktakeLabelNo);
        } else {
            this.actualStktakeLabelNo = actualStktakeLabelNo;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + Objects.hashCode(this.equipmentIdProperty);
        hash = 61 * hash + Objects.hashCode(this.actualIdProperty);
        hash = 61 * hash + Objects.hashCode(this.equipmentNameProperty);
        hash = 61 * hash + Objects.hashCode(this.equipmentIdentifyProperty);
        hash = 61 * hash + Objects.hashCode(this.equipmentTypeIdProperty);
        hash = 61 * hash + Objects.hashCode(this.updatePersonIdProperty);
        hash = 61 * hash + Objects.hashCode(this.updateDateTimeProperty);
        hash = 61 * hash + Objects.hashCode(this.storageNameProperty);
        hash = 61 * hash + Objects.hashCode(this.inventoryProperty);
        hash = 61 * hash + Objects.hashCode(this.reserveInventoryProperty);
        hash = 61 * hash + Objects.hashCode(this.inProcessInventoryProperty);
        hash = 61 * hash + Objects.hashCode(this.inventoryStockProperty);
        hash = 61 * hash + Objects.hashCode(this.inventoryTempProperty);
        hash = 61 * hash + Objects.hashCode(this.managementProperty);
        hash = 61 * hash + Objects.hashCode(this.productProperty);
        hash = 61 * hash + Objects.hashCode(this.standardProperty);
        hash = 61 * hash + Objects.hashCode(this.materialProperty);
        hash = 61 * hash + Objects.hashCode(this.manufacturerProperty);
        hash = 61 * hash + Objects.hashCode(this.workflowIdProperty);
        hash = 61 * hash + Objects.hashCode(this.implementDatetimeProperty);
        hash = 61 * hash + Objects.hashCode(this.organizationIdProperty);
        hash = 61 * hash + Objects.hashCode(this.actualStatusProperty);
        hash = 61 * hash + Objects.hashCode(this.organizationIdentifyProperty);
        hash = 61 * hash + Objects.hashCode(this.organizationNameProperty);
        hash = 61 * hash + Objects.hashCode(this.actualStorageNameProperty);
        hash = 61 * hash + Objects.hashCode(this.actualInventoryStockProperty);
        hash = 61 * hash + Objects.hashCode(this.actualDifferenceProperty);
        hash = 61 * hash + Objects.hashCode(this.actualAffiliationNameProperty);
        hash = 61 * hash + Objects.hashCode(this.actualAffiliationCodeProperty);
        hash = 61 * hash + Objects.hashCode(this.actualStktakeLabelNoProperty);
        hash = 61 * hash + Objects.hashCode(this.equipmentId);
        hash = 61 * hash + Objects.hashCode(this.equipmentName);
        hash = 61 * hash + Objects.hashCode(this.equipmentIdentify);
        hash = 61 * hash + Objects.hashCode(this.equipmentTypeId);
        hash = 61 * hash + Objects.hashCode(this.updatePersonId);
        hash = 61 * hash + Objects.hashCode(this.updateDatetime);
        hash = 61 * hash + Objects.hashCode(this.removeFlag);
        hash = 61 * hash + Objects.hashCode(this.storageName);
        hash = 61 * hash + Objects.hashCode(this.inventory);
        hash = 61 * hash + Objects.hashCode(this.reserveInventory);
        hash = 61 * hash + Objects.hashCode(this.inProcessInventory);
        hash = 61 * hash + Objects.hashCode(this.inventoryStock);
        hash = 61 * hash + Objects.hashCode(this.inventoryTemp);
        hash = 61 * hash + Objects.hashCode(this.management);
        hash = 61 * hash + Objects.hashCode(this.product);
        hash = 61 * hash + Objects.hashCode(this.standard);
        hash = 61 * hash + Objects.hashCode(this.material);
        hash = 61 * hash + Objects.hashCode(this.manufacturer);
        hash = 61 * hash + Objects.hashCode(this.workflowId);
        hash = 61 * hash + Objects.hashCode(this.implementDatetime);
        hash = 61 * hash + Objects.hashCode(this.organizationId);
        hash = 61 * hash + Objects.hashCode(this.actualStatus);
        hash = 61 * hash + Objects.hashCode(this.organizationIdentify);
        hash = 61 * hash + Objects.hashCode(this.organizationName);
        hash = 61 * hash + Objects.hashCode(this.actualStorageName);
        hash = 61 * hash + Objects.hashCode(this.actualInventoryStock);
        hash = 61 * hash + Objects.hashCode(this.actualDifference);
        hash = 61 * hash + Objects.hashCode(this.actualAffiliationName);
        hash = 61 * hash + Objects.hashCode(this.actualAffiliationCode);
        hash = 61 * hash + Objects.hashCode(this.actualStktakeLabelNo);
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
        if (!Objects.equals(this.equipmentIdProperty, other.equipmentIdProperty)) {
            return false;
        }
        if (!Objects.equals(this.actualIdProperty, other.actualIdProperty)) {
            return false;
        }
        if (!Objects.equals(this.equipmentNameProperty, other.equipmentNameProperty)) {
            return false;
        }
        if (!Objects.equals(this.equipmentIdentifyProperty, other.equipmentIdentifyProperty)) {
            return false;
        }
        if (!Objects.equals(this.equipmentTypeIdProperty, other.equipmentTypeIdProperty)) {
            return false;
        }
        if (!Objects.equals(this.updatePersonIdProperty, other.updatePersonIdProperty)) {
            return false;
        }
        if (!Objects.equals(this.updateDateTimeProperty, other.updateDateTimeProperty)) {
            return false;
        }
        if (!Objects.equals(this.storageNameProperty, other.storageNameProperty)) {
            return false;
        }
        if (!Objects.equals(this.inventoryProperty, other.inventoryProperty)) {
            return false;
        }
        if (!Objects.equals(this.reserveInventoryProperty, other.reserveInventoryProperty)) {
            return false;
        }
        if (!Objects.equals(this.inProcessInventoryProperty, other.inProcessInventoryProperty)) {
            return false;
        }
        if (!Objects.equals(this.inventoryStockProperty, other.inventoryStockProperty)) {
            return false;
        }
        if (!Objects.equals(this.inventoryTempProperty, other.inventoryTempProperty)) {
            return false;
        }
        if (!Objects.equals(this.managementProperty, other.managementProperty)) {
            return false;
        }
        if (!Objects.equals(this.productProperty, other.productProperty)) {
            return false;
        }
        if (!Objects.equals(this.standardProperty, other.standardProperty)) {
            return false;
        }
        if (!Objects.equals(this.materialProperty, other.materialProperty)) {
            return false;
        }
        if (!Objects.equals(this.manufacturerProperty, other.manufacturerProperty)) {
            return false;
        }
        if (!Objects.equals(this.workflowIdProperty, other.workflowIdProperty)) {
            return false;
        }
        if (!Objects.equals(this.implementDatetimeProperty, other.implementDatetimeProperty)) {
            return false;
        }
        if (!Objects.equals(this.organizationIdProperty, other.organizationIdProperty)) {
            return false;
        }
        if (!Objects.equals(this.actualStatusProperty, other.actualStatusProperty)) {
            return false;
        }
        if (!Objects.equals(this.organizationIdentifyProperty, other.organizationIdentifyProperty)) {
            return false;
        }
        if (!Objects.equals(this.organizationNameProperty, other.organizationNameProperty)) {
            return false;
        }
        if (!Objects.equals(this.actualStorageNameProperty, other.actualStorageNameProperty)) {
            return false;
        }
        if (!Objects.equals(this.actualInventoryStockProperty, other.actualInventoryStockProperty)) {
            return false;
        }
        if (!Objects.equals(this.actualDifferenceProperty, other.actualDifferenceProperty)) {
            return false;
        }
        if (!Objects.equals(this.actualAffiliationNameProperty, other.actualAffiliationNameProperty)) {
            return false;
        }
        if (!Objects.equals(this.actualAffiliationCodeProperty, other.actualAffiliationCodeProperty)) {
            return false;
        }
        if (!Objects.equals(this.actualStktakeLabelNoProperty, other.actualStktakeLabelNoProperty)) {
            return false;
        }
        if (!Objects.equals(this.equipmentId, other.equipmentId)) {
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
                + ", actualId=" + actualId
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
