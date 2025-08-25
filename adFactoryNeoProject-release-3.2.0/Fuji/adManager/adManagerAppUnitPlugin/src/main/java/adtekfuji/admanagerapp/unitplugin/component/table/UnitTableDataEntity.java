/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this  file, choose Tools | s
 * and open the  in the editor.
 */
package adtekfuji.admanagerapp.unitplugin.component.table;

import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.StringTime;
import java.util.Objects;
import java.util.ResourceBundle;
import jp.adtekfuji.forfujiapp.entity.unit.UnitHierarchyInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unit.UnitInfoEntity;

/**
 * ユニットテーブル用エンティティ
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.27.Tha
 */
public class UnitTableDataEntity {

    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private UnitHierarchyInfoEntity hierarchyInfoEntity;
    private UnitInfoEntity infoEntity;
    private String name;
    private String outsetDateTime;
    private String deliveryDateTime;
    private String updatePersonName;
    private String updateDatetime;
    private Boolean isHierarchy;
    private String status;

    public UnitTableDataEntity(UnitHierarchyInfoEntity hierarchyInfoEntity) {
        this.hierarchyInfoEntity = hierarchyInfoEntity;
        this.infoEntity = null;
        this.name = hierarchyInfoEntity.getHierarchyName();
        this.outsetDateTime = null;
        this.deliveryDateTime = null;
        this.updatePersonName = null;
        this.updateDatetime = null;
        this.isHierarchy = Boolean.TRUE;
        this.status = null;
    }

    public UnitTableDataEntity(UnitInfoEntity infoEntity) {
        this.hierarchyInfoEntity = null;
        this.infoEntity = infoEntity;
        this.name = infoEntity.getUnitName();
        this.outsetDateTime = Objects.isNull(infoEntity.getStartDatetime()) ? "" : StringTime.convertDateToString(infoEntity.getStartDatetime(), LocaleUtils.getString("key.DateTimeFormat"));;
        this.deliveryDateTime = Objects.isNull(infoEntity.getCompDatetime()) ? "" : StringTime.convertDateToString(infoEntity.getCompDatetime(), LocaleUtils.getString("key.DateTimeFormat"));;
        this.updateDatetime = Objects.isNull(infoEntity.getUpdateDatetime()) ? "" : StringTime.convertDateToString(infoEntity.getUpdateDatetime(), LocaleUtils.getString("key.DateTimeFormat"));
        this.isHierarchy = Boolean.FALSE;
        this.status = infoEntity.getIsCompleted() ? LocaleUtils.getString("key.complete") : LocaleUtils.getString("key.Incomplete");
    }

    public static ResourceBundle getRb() {
        return rb;
    }

    public UnitHierarchyInfoEntity getUnitHierarchyInfoEntity() {
        return hierarchyInfoEntity;
    }

    public UnitInfoEntity getUnitInfoEntity() {
        return infoEntity;
    }

    public String getName() {
        return name;
    }

    public String getUpdatePersonName() {
        return updatePersonName;
    }

    public String getUpdateDatetime() {
        return updateDatetime;
    }

    public Boolean getIsHierarchy() {
        return isHierarchy;
    }

    public void setUnitHierarchyInfoEntity(UnitHierarchyInfoEntity hierarchyInfoEntity) {
        this.hierarchyInfoEntity = hierarchyInfoEntity;
    }

    public void setUnitInfoEntity(UnitInfoEntity infoEntity) {
        this.infoEntity = infoEntity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOutsetDateTime() {
        return outsetDateTime;
    }

    public void setOutsetDateTime(String outsetDateTime) {
        this.outsetDateTime = outsetDateTime;
    }

    public String getDeliveryDateTime() {
        return deliveryDateTime;
    }

    public void setDeliveryDateTime(String deliveryDateTime) {
        this.deliveryDateTime = deliveryDateTime;
    }
    
    public void setUpdatePersonName(String updatePersonName) {
        this.updatePersonName = updatePersonName;
    }

    public void setUpdateDatetime(String updateDatetime) {
        this.updateDatetime = updateDatetime;
    }

    public void setIsHierarchy(Boolean isHierarchy) {
        this.isHierarchy = isHierarchy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
