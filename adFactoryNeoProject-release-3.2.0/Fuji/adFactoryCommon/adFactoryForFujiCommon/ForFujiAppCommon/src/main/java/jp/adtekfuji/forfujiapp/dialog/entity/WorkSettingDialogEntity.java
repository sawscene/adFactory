/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.dialog.entity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;

/**
 * 工程カンバン設定用エンティティ
 *
 * @author ek.mori
 * @version 1.4.3
 * @param <E>
 * @since 2016.11.17.Tsu
 */
public class WorkSettingDialogEntity<E> {

    private StringProperty tactTimeProperty;
    private StringProperty offsetTimeProperty;
    private BooleanProperty skipProperty;
    private ObjectProperty<List<EquipmentInfoEntity>> equipmentsProperty;
    private ObjectProperty<List<OrganizationInfoEntity>> organizationsProperty;
    private ObjectProperty<LinkedList<E>> propertysProperty;

    private String tactTime;
    private String offsetTime;
    private Boolean skip;
    private List<EquipmentInfoEntity> equipments = new ArrayList<>();
    private List<OrganizationInfoEntity> organizations = new ArrayList<>();
    private List<Long> equipmentIds = new ArrayList<>();
    private List<Long> organizationIds = new ArrayList<>();
    private LinkedList<E> propertys = new LinkedList<>();

    private Boolean separateEditFlag = false;
    private Boolean isStartTimeOffset = false;

    

    public WorkSettingDialogEntity() {
    }

    public WorkSettingDialogEntity(String tactTime, String offsetTime, boolean skip, List<Long> equipments, List<Long> organizations, LinkedList<E> propertyList) {
        this.tactTime = tactTime;
        this.offsetTime = offsetTime;
        this.skip = skip;
        this.equipmentIds = equipments;
        this.organizationIds = organizations;
        this.propertys = propertyList;
        this.separateEditFlag = true;
    }

    public StringProperty tactTimeProperty() {
        if (Objects.isNull(tactTimeProperty)) {
            tactTimeProperty = new SimpleStringProperty(tactTime);
        }
        return tactTimeProperty;
    }

    public StringProperty offsetTimeProperty() {
        if (Objects.isNull(offsetTimeProperty)) {
            offsetTimeProperty = new SimpleStringProperty(offsetTime);
        }
        return offsetTimeProperty;
    }

    public BooleanProperty skipProperty() {
        if (Objects.isNull(skipProperty)) {
            skipProperty = new SimpleBooleanProperty(skip);
        }
        return skipProperty;
    }

    public ObjectProperty<List<EquipmentInfoEntity>> equipmentsProperty() {
        if (Objects.isNull(equipmentsProperty)) {
            equipmentsProperty = new SimpleObjectProperty<>(equipments);
        }
        return equipmentsProperty;
    }

    public ObjectProperty<List<OrganizationInfoEntity>> organizationsProperty() {
        if (Objects.isNull(organizationsProperty)) {
            organizationsProperty = new SimpleObjectProperty<>(organizations);
        }
        return organizationsProperty;
    }

    public ObjectProperty<LinkedList<E>> propertysProperty() {
        if (Objects.isNull(propertysProperty)) {
            propertysProperty = new SimpleObjectProperty<>(propertys);
        }
        return propertysProperty;
    }

    public String getTactTime() {
        if (Objects.nonNull(tactTimeProperty)) {
            return tactTimeProperty.get();
        }
        return tactTime;
    }

    public void setTactTime(String tactTime) {
        if (Objects.nonNull(tactTimeProperty)) {
            tactTimeProperty.set(tactTime);
        } else {
            this.tactTime = tactTime;
        }
    }

    public String getOffsetTime() {
        if (Objects.nonNull(offsetTimeProperty)) {
            return offsetTimeProperty.get();
        }
        return offsetTime;
    }

    public void setOffsetTime(String offsetTime) {
        if (Objects.nonNull(offsetTimeProperty)) {
            offsetTimeProperty.set(offsetTime);
        } else {
            this.offsetTime = offsetTime;
        }
    }

    public Boolean getSkip() {
        if (Objects.nonNull(skipProperty)) {
            return skipProperty.get();
        }
        return skip;
    }

    public void setSkip(Boolean skip) {
        if (Objects.nonNull(skipProperty)) {
            skipProperty.set(skip);
        } else {
            this.skip = skip;
        }
    }

    public List<EquipmentInfoEntity> getEquipments() {
        if (Objects.nonNull(equipmentsProperty)) {
            return equipmentsProperty.get();
        }
        return equipments;
    }

    public void setEquipments(List<EquipmentInfoEntity> equipments) {
        if (Objects.nonNull(equipmentsProperty)) {
            equipmentsProperty.set(equipments);
        } else {
            this.equipments = equipments;
        }
    }

    public List<OrganizationInfoEntity> getOrganizations() {
        if (Objects.nonNull(organizationsProperty)) {
            return organizationsProperty.get();
        }
        return organizations;
    }

    public void setOrganizations(List<OrganizationInfoEntity> organizations) {
        if (Objects.nonNull(organizationsProperty)) {
            organizationsProperty.set(organizations);
        } else {
            this.organizations = organizations;
        }
    }

    public LinkedList<E> getPropertys() {
        if (Objects.nonNull(propertysProperty)) {
            return propertysProperty.get();
        }
        return propertys;
    }

    public void setPropertys(LinkedList<E> propertyList) {
        if (Objects.nonNull(propertysProperty)) {
            propertysProperty.set(propertys);
        } else {
            this.propertys = propertys;
        }
        this.propertys = propertyList;
    }

    public BooleanProperty getSkipProperty() {
        return skipProperty;
    }

    public Boolean getSeparateEditFlag() {
        return separateEditFlag;
    }

    public List<Long> getEquipmentIds() {
        return equipmentIds;
    }

    public void setEquipmentIds(List<Long> equipmentIds) {
        this.equipmentIds = equipmentIds;
    }

    public List<Long> getOrganizationIds() {
        return organizationIds;
    }

    public void setOrganizationIds(List<Long> organizationIds) {
        this.organizationIds = organizationIds;
    }

    public Boolean getIsStartTimeOffset() {
        return isStartTimeOffset;
    }

    public void setIsStartTimeOffset(Boolean isStartTimeOffset) {
        this.isStartTimeOffset = isStartTimeOffset;
    }
    
    public void Update() {
        this.tactTime = getTactTime();
        this.offsetTime = getOffsetTime();
        this.skip = getSkip();
        this.equipments = getEquipments();
        this.organizations = getOrganizations();
        this.propertys = getPropertys();
    }
}
