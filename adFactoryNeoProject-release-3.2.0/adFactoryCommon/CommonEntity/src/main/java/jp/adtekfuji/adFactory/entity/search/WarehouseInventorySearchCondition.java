/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.search;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "warehouseInventorySearchCondition")
public class WarehouseInventorySearchCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private Long inventoriesWorkflowId = null;
    @XmlElement()
    private Long payoutWorkflowId = null;
    @XmlElement()
    private Date fromDate = null;
    @XmlElement()
    private Date toDate = null;
    @XmlElement()
    private String equipmentName = null;
    @XmlElement()
    private String equipmentIdentName = null;
    @XmlElement()
    private String storageName = null;
    @XmlElement()
    private String management = null;
    @XmlElement()
    private String product = null;
    @XmlElement()
    private String standard = null;
    @XmlElement()
    private String material = null;
    @XmlElement()
    private String manufacturer = null;
    @XmlElement()
    private String organizationIdentify = null;
    @XmlElement()
    private String organizationName = null;
    @XmlElement()
    private String actualAffiliationName = null;
    @XmlElement()
    private String actualAffiliationCode = null;
    @XmlElement()
    private Boolean isMatch = false;
    @XmlElement()
    private Boolean isAcutualNull = true;
    @XmlElement()
    private Boolean isAcutualNotNull = true;

    public WarehouseInventorySearchCondition() {
    }

    public WarehouseInventorySearchCondition(Long inventoriesWorkflowId, Long payoutWorkflowId, Date fromDate, Date toDate) {
        this.inventoriesWorkflowId = inventoriesWorkflowId;
        this.payoutWorkflowId = payoutWorkflowId;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public WarehouseInventorySearchCondition inventoriesWorkflowId(Long inventoriesWorkflowId) {
        this.inventoriesWorkflowId = inventoriesWorkflowId;
        return this;
    }

    public WarehouseInventorySearchCondition payoutWorkflowId(Long payoutWorkflowId) {
        this.payoutWorkflowId = payoutWorkflowId;
        return this;
    }

    public WarehouseInventorySearchCondition fromDate(Date fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    public WarehouseInventorySearchCondition toDate(Date toDate) {
        this.toDate = toDate;
        return this;
    }

    public WarehouseInventorySearchCondition equipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
        return this;
    }

    public WarehouseInventorySearchCondition equipmentIdentName(String equipmentIdentName) {
        this.equipmentIdentName = equipmentIdentName;
        return this;
    }

    public WarehouseInventorySearchCondition storageName(String storageName) {
        this.storageName = storageName;
        return this;
    }

    public WarehouseInventorySearchCondition management(String management) {
        this.management = management;
        return this;
    }

    public WarehouseInventorySearchCondition product(String product) {
        this.product = product;
        return this;
    }

    public WarehouseInventorySearchCondition standard(String standard) {
        this.standard = standard;
        return this;
    }

    public WarehouseInventorySearchCondition material(String material) {
        this.material = material;
        return this;
    }

    public WarehouseInventorySearchCondition manufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
        return this;
    }

    public WarehouseInventorySearchCondition organizationIdentify(String organizationIdentify) {
        this.organizationIdentify = organizationIdentify;
        return this;
    }

    public WarehouseInventorySearchCondition organizationName(String organizationName) {
        this.organizationName = organizationName;
        return this;
    }

    public WarehouseInventorySearchCondition actualAffiliationName(String actualAffiliationName) {
        this.actualAffiliationName = actualAffiliationName;
        return this;
    }

    public WarehouseInventorySearchCondition actualAffiliationCode(String actualAffiliationCode) {
        this.actualAffiliationCode = actualAffiliationCode;
        return this;
    }

    public WarehouseInventorySearchCondition isMatch(Boolean isMatch) {
        this.isMatch = isMatch;
        return this;
    }

    public WarehouseInventorySearchCondition isAcutualNull(Boolean isAcutualNull) {
        this.isAcutualNull = isAcutualNull;
        return this;
    }

    public WarehouseInventorySearchCondition isAcutualNotNull(Boolean isAcutualNotNull) {
        this.isAcutualNotNull = isAcutualNotNull;
        return this;
    }

    public Long getInventoriesWorkflowId() {
        return this.inventoriesWorkflowId;
    }

    public void setInventoriesWorkflowId(Long inventoriesWorkflowId) {
        this.inventoriesWorkflowId = inventoriesWorkflowId;
    }

    public Long getPayoutWorkflowId() {
        return this.payoutWorkflowId;
    }

    public void setPayoutWorkflowId(Long payoutWorkflowId) {
        this.payoutWorkflowId = payoutWorkflowId;
    }

    public Date getFromDate() {
        return this.fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return this.toDate;
    }

    public String getEquipmentName() {
        return this.equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getEquipmentIdentName() {
        return this.equipmentIdentName;
    }

    public void setEquipmentIdentName(String equipmentIdentName) {
        this.equipmentIdentName = equipmentIdentName;
    }

    public String getStorageName() {
        return this.storageName;
    }

    public void setStorageName(String storageName) {
        this.storageName = storageName;
    }

    public String getManagement() {
        return this.management;
    }

    public void setManagement(String management) {
        this.management = management;
    }

    public String getProduct() {
        return this.product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getStandard() {
        return this.standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getMaterial() {
        return this.material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getManufacturer() {
        return this.manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getOrganizationIdentify() {
        return this.organizationIdentify;
    }

    public void setOrganizationIdentify(String organizationIdentify) {
        this.organizationIdentify = organizationIdentify;
    }

    public String getOrganizationName() {
        return this.organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getActualAffiliationName() {
        return this.actualAffiliationName;
    }

    public void setActualAffiliationName(String actualAffiliationName) {
        this.actualAffiliationName = actualAffiliationName;
    }

    public String getActualAffiliationCode() {
        return this.actualAffiliationCode;
    }

    public void setActualAffiliationCode(String actualAffiliationCode) {
        this.actualAffiliationCode = actualAffiliationCode;
    }

    /**
     * 条件を完全一致にするか
     * @return 
     */
    public Boolean getIsMatch() {
        return this.isMatch;
    }

    /**
     * 条件を完全一致にするか
     * @param isMatch 
     */
    public void setMatch(Boolean isMatch) {
        this.isMatch = isMatch;
    }

    /**
     * 棚卸未実施の部品を取得するか
     * @return 
     */
    public Boolean getIsActualNull() {
        return this.isAcutualNull;
    }

    /**
     * 棚卸未実施の部品を取得するか
     * @param isAcutualNull 
     */
    public void setIsActualNull(Boolean isAcutualNull) {
        this.isAcutualNull = isAcutualNull;
    }

    /**
     * 棚卸済の部品を取得するか
     * @return 
     */
    public Boolean getIsActualNotNull() {
        return this.isAcutualNotNull;
    }

    /**
     * 棚卸済の部品を取得するか
     * @param isAcutualNotNull 
     */
    public void setIsActualNotNull(Boolean isAcutualNotNull) {
        this.isAcutualNotNull = isAcutualNotNull;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.inventoriesWorkflowId);
        hash = 89 * hash + Objects.hashCode(this.payoutWorkflowId);
        hash = 89 * hash + Objects.hashCode(this.fromDate);
        hash = 89 * hash + Objects.hashCode(this.toDate);
        hash = 89 * hash + Objects.hashCode(this.equipmentName);
        hash = 89 * hash + Objects.hashCode(this.equipmentIdentName);
        hash = 89 * hash + Objects.hashCode(this.storageName);
        hash = 89 * hash + Objects.hashCode(this.management);
        hash = 89 * hash + Objects.hashCode(this.product);
        hash = 89 * hash + Objects.hashCode(this.standard);
        hash = 89 * hash + Objects.hashCode(this.material);
        hash = 89 * hash + Objects.hashCode(this.manufacturer);
        hash = 89 * hash + Objects.hashCode(this.organizationIdentify);
        hash = 89 * hash + Objects.hashCode(this.organizationName);
        hash = 89 * hash + Objects.hashCode(this.actualAffiliationName);
        hash = 89 * hash + Objects.hashCode(this.actualAffiliationCode);
        hash = 89 * hash + Objects.hashCode(this.isMatch);
        hash = 89 * hash + Objects.hashCode(this.isAcutualNull);
        hash = 89 * hash + Objects.hashCode(this.isAcutualNotNull);
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
        final WarehouseInventorySearchCondition other = (WarehouseInventorySearchCondition) obj;
        if (!Objects.equals(this.inventoriesWorkflowId, other.inventoriesWorkflowId)) {
            return false;
        }
        if (!Objects.equals(this.payoutWorkflowId, other.payoutWorkflowId)) {
            return false;
        }
        if (!Objects.equals(this.fromDate, other.fromDate)) {
            return false;
        }
        if (!Objects.equals(this.toDate, other.toDate)) {
            return false;
        }
        if (!Objects.equals(this.equipmentName, other.equipmentName)) {
            return false;
        }
        if (!Objects.equals(this.equipmentIdentName, other.equipmentIdentName)) {
            return false;
        }
        if (!Objects.equals(this.storageName, other.storageName)) {
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
        if (!Objects.equals(this.organizationIdentify, other.organizationIdentify)) {
            return false;
        }
        if (!Objects.equals(this.organizationName, other.organizationName)) {
            return false;
        }
        if (!Objects.equals(this.actualAffiliationName, other.actualAffiliationName)) {
            return false;
        }
        if (!Objects.equals(this.actualAffiliationCode, other.actualAffiliationCode)) {
            return false;
        }
        if (!Objects.equals(this.isMatch, other.isMatch)) {
            return false;
        }
        if (!Objects.equals(this.isAcutualNull, other.isAcutualNull)) {
            return false;
        }
        if (!Objects.equals(this.isAcutualNotNull, other.isAcutualNotNull)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "WarehouseInventoryActualEntity{"
                + "inventoriesWorkflowId=" + this.inventoriesWorkflowId
                + ", payoutWorkflowId=" + this.payoutWorkflowId
                + ", fromDate=" + this.fromDate
                + ", toDate=" + this.toDate
                + ", equipmentName=" + this.equipmentName
                + ", equipmentIdentName=" + this.equipmentIdentName
                + ", storageName=" + this.storageName
                + ", management=" + this.management
                + ", product=" + this.product
                + ", standard=" + this.standard
                + ", material=" + this.material
                + ", manufacturer=" + this.manufacturer
                + ", organizationIdentify=" + this.organizationIdentify
                + ", organizationName=" + this.organizationName
                + ", actualAffiliationName=" + this.actualAffiliationName
                + ", actualAffiliationCode=" + this.actualAffiliationCode
                + ", isMatch=" + this.isMatch
                + ", isAcutualNull=" + this.isAcutualNull
                + ", isAcutualNotNull=" + this.isAcutualNotNull
                + '}';
    }
}
