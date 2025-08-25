/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservicecommon.plugin.common;

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
 *
 * @author e-mori
 * @param <E>
 */
public class WorkPlanWorkSettingDialogEntity<E> {

    private StringProperty taktTimeProperty;
    private StringProperty offsetTimeProperty;
    private BooleanProperty skipProperty;
    private ObjectProperty<List<EquipmentInfoEntity>> equipmentsProperty;
    private ObjectProperty<List<OrganizationInfoEntity>> organizationsProperty;
    private ObjectProperty<LinkedList<E>> propertysProperty;

    private String taktTime;
    private String offsetTime;
    private Boolean skip;
    private List<EquipmentInfoEntity> equipments = new ArrayList<>();
    private List<OrganizationInfoEntity> organizations = new ArrayList<>();
    private List<Long> equipmentIds = new ArrayList<>();
    private List<Long> organizationIds = new ArrayList<>();
    private LinkedList<E> properties = new LinkedList<>();

    private Boolean separateEditFlag = false;
    private Boolean isStartTimeOffset = false;

    /**
     *
     */
    public WorkPlanWorkSettingDialogEntity() {
    }

    /**
     *
     * @param taktTime
     * @param offsetTime
     * @param skip
     * @param equipments
     * @param organizations
     * @param properties
     */
    public WorkPlanWorkSettingDialogEntity(String taktTime, String offsetTime, boolean skip, List<Long> equipments, List<Long> organizations, LinkedList<E> properties) {
        this.taktTime = taktTime;
        this.offsetTime = offsetTime;
        this.skip = skip;
        this.equipmentIds = equipments;
        this.organizationIds = organizations;
        this.properties = properties;
        this.separateEditFlag = true;
    }

    /**
     *
     * @return
     */
    public StringProperty taktTimeProperty() {
        if (Objects.isNull(this.taktTimeProperty)) {
            this.taktTimeProperty = new SimpleStringProperty(this.taktTime);
        }
        return this.taktTimeProperty;
    }

    /**
     *
     * @return
     */
    public StringProperty offsetTimeProperty() {
        if (Objects.isNull(this.offsetTimeProperty)) {
            this.offsetTimeProperty = new SimpleStringProperty(this.offsetTime);
        }
        return this.offsetTimeProperty;
    }

    /**
     *
     * @return
     */
    public BooleanProperty skipProperty() {
        if (Objects.isNull(this.skipProperty)) {
            this.skipProperty = new SimpleBooleanProperty(this.skip);
        }
        return this.skipProperty;
    }

    /**
     *
     * @return
     */
    public ObjectProperty<List<EquipmentInfoEntity>> equipmentsProperty() {
        if (Objects.isNull(this.equipmentsProperty)) {
            this.equipmentsProperty = new SimpleObjectProperty<>(this.equipments);
        }
        return this.equipmentsProperty;
    }

    /**
     *
     * @return
     */
    public ObjectProperty<List<OrganizationInfoEntity>> organizationsProperty() {
        if (Objects.isNull(this.organizationsProperty)) {
            this.organizationsProperty = new SimpleObjectProperty<>(this.organizations);
        }
        return this.organizationsProperty;
    }

    /**
     *
     * @return
     */
    public ObjectProperty<LinkedList<E>> propertysProperty() {
        if (Objects.isNull(this.propertysProperty)) {
            this.propertysProperty = new SimpleObjectProperty<>(this.properties);
        }
        return this.propertysProperty;
    }

    /**
     *
     * @return
     */
    public String getTaktTime() {
        if (Objects.nonNull(this.taktTimeProperty)) {
            return this.taktTimeProperty.get();
        }
        return this.taktTime;
    }

    /**
     *
     * @param taktTime
     */
    public void setTaktTime(String taktTime) {
        if (Objects.nonNull(this.taktTimeProperty)) {
            this.taktTimeProperty.set(taktTime);
        } else {
            this.taktTime = taktTime;
        }
    }

    /**
     *
     * @return
     */
    public String getOffsetTime() {
        if (Objects.nonNull(this.offsetTimeProperty)) {
            return this.offsetTimeProperty.get();
        }
        return this.offsetTime;
    }

    /**
     *
     * @param offsetTime
     */
    public void setOffsetTime(String offsetTime) {
        if (Objects.nonNull(this.offsetTimeProperty)) {
            this.offsetTimeProperty.set(offsetTime);
        } else {
            this.offsetTime = offsetTime;
        }
    }

    /**
     *
     * @return
     */
    public Boolean getSkip() {
        if (Objects.nonNull(this.skipProperty)) {
            return this.skipProperty.get();
        }
        return this.skip;
    }

    /**
     * *
     *
     * @param skip
     */
    public void setSkip(Boolean skip) {
        if (Objects.nonNull(this.skipProperty)) {
            this.skipProperty.set(skip);
        } else {
            this.skip = skip;
        }
    }

    /**
     *
     * @return
     */
    public List<EquipmentInfoEntity> getEquipments() {
        if (Objects.nonNull(this.equipmentsProperty)) {
            return this.equipmentsProperty.get();
        }
        return this.equipments;
    }

    /**
     *
     * @param equipments
     */
    public void setEquipments(List<EquipmentInfoEntity> equipments) {
        if (Objects.nonNull(this.equipmentsProperty)) {
            this.equipmentsProperty.set(equipments);
        } else {
            this.equipments = equipments;
        }
    }

    /**
     *
     * @return
     */
    public List<OrganizationInfoEntity> getOrganizations() {
        if (Objects.nonNull(this.organizationsProperty)) {
            return this.organizationsProperty.get();
        }
        return this.organizations;
    }

    /**
     *
     * @param organizations
     */
    public void setOrganizations(List<OrganizationInfoEntity> organizations) {
        if (Objects.nonNull(this.organizationsProperty)) {
            this.organizationsProperty.set(organizations);
        } else {
            this.organizations = organizations;
        }
    }

    /**
     *
     * @return
     */
    public LinkedList<E> getProperties() {
        if (Objects.nonNull(this.propertysProperty)) {
            return this.propertysProperty.get();
        }
        return this.properties;
    }

    /**
     *
     * @param properties
     */
    public void setProperties(LinkedList<E> properties) {
        if (Objects.nonNull(this.propertysProperty)) {
            this.propertysProperty.set(properties);
        } else {
            this.properties = properties;
        }
    }

    /**
     *
     * @return
     */
    public BooleanProperty getSkipProperty() {
        return this.skipProperty;
    }

    /**
     *
     * @return
     */
    public Boolean getSeparateEditFlag() {
        return this.separateEditFlag;
    }

    /**
     *
     * @return
     */
    public List<Long> getEquipmentIds() {
        return this.equipmentIds;
    }

    /**
     *
     * @param equipmentIds
     */
    public void setEquipmentIds(List<Long> equipmentIds) {
        this.equipmentIds = equipmentIds;
    }

    /**
     *
     * @return
     */
    public List<Long> getOrganizationIds() {
        return this.organizationIds;
    }

    /**
     *
     * @param organizationIds
     */
    public void setOrganizationIds(List<Long> organizationIds) {
        this.organizationIds = organizationIds;
    }

    /**
     *
     * @return
     */
    public Boolean isStartTimeOffset() {
        return this.isStartTimeOffset;
    }

    /**
     *
     * @param isStartTimeOffset
     */
    public void setIsStartTimeOffset(Boolean isStartTimeOffset) {
        this.isStartTimeOffset = isStartTimeOffset;
    }

    /**
     *
     */
    public void Update() {
        this.taktTime = getTaktTime();
        this.offsetTime = getOffsetTime();
        this.skip = getSkip();
        this.equipments = getEquipments();
        this.organizations = getOrganizations();
        this.properties = getProperties();
    }
}
