/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.common;

import java.util.ArrayList;
import java.util.Collections;
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
public class WorkSettingDialogEntity<E> {

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

    private String originalTaktTime;
    private Boolean originalSkip;
    private List<Long> originalOrganizationIds;

    public WorkSettingDialogEntity() {
    }

    public WorkSettingDialogEntity(String taktTime, String offsetTime, boolean skip, List<Long> equipments, List<Long> organizations, LinkedList<E> properties) {
        this.taktTime = taktTime;
        this.offsetTime = offsetTime;
        this.skip = skip;
        this.equipmentIds = equipments;
        this.organizationIds = organizations;
        this.properties = properties;
        this.separateEditFlag = true;

        this.originalTaktTime = taktTime;
        this.originalSkip = skip;
        this.originalOrganizationIds = new ArrayList(organizations);
        Collections.sort(this.originalOrganizationIds);
    }

    public StringProperty taktTimeProperty() {
        if (Objects.isNull(taktTimeProperty)) {
            taktTimeProperty = new SimpleStringProperty(taktTime);
        }
        return taktTimeProperty;
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
            propertysProperty = new SimpleObjectProperty<>(properties);
        }
        return propertysProperty;
    }

    public String getTaktTime() {
        if (Objects.nonNull(taktTimeProperty)) {
            return taktTimeProperty.get();
        }
        return taktTime;
    }

    public void setTaktTime(String taktTime) {
        if (Objects.nonNull(taktTimeProperty)) {
            taktTimeProperty.set(taktTime);
        } else {
            this.taktTime = taktTime;
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

    public LinkedList<E> getProperties() {
        if (Objects.nonNull(propertysProperty)) {
            return propertysProperty.get();
        }
        return properties;
    }

    public void setProperties(LinkedList<E> properties) {
        if (Objects.nonNull(propertysProperty)) {
            this.propertysProperty.set(properties);
        } else {
            this.properties = properties;
        }
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

    /**
     * 基本開始時間変更かを取得する。
     *
     * @return 基本開始時間変更か？ (true:基本開始時間変更, false:オフセット)
     */
    public Boolean isStartTimeOffset() {
        return isStartTimeOffset;
    }

    /**
     * 基本開始時間変更かを設定する。
     *
     * @param isStartTimeOffset 基本開始時間変更か？ (true:基本開始時間変更, false:オフセット)
     */
    public void setIsStartTimeOffset(Boolean isStartTimeOffset) {
        this.isStartTimeOffset = isStartTimeOffset;
    }

    /**
     * タクトタイムを変更したかを取得する。
     *
     * @return タクトタイムを変更したか？ (true:変更した, false:変更していない)
     */
    public boolean isUpdateTaktTime() {
        return !this.taktTime.equals(this.originalTaktTime);
    }

    /**
     * スキップを変更したかを取得する。
     *
     * @return スキップを変更したか？ (true:変更した, false:変更していない)
     */
    public boolean isUpdateSkip() {
        return !this.skip.equals(this.originalSkip);
    }

    /**
     * 組織を変更したかを取得する。
     *
     * @return 組織を変更したか？ (true:変更した, false:変更していない)
     */
    public boolean isUpdateOrganization() {
        Collections.sort(this.organizationIds);
        return !this.organizationIds.equals(this.originalOrganizationIds);
    }

    public void Update() {
        this.taktTime = getTaktTime();
        this.offsetTime = getOffsetTime();
        this.skip = getSkip();
        this.equipments = getEquipments();
        this.organizations = getOrganizations();
        this.properties = getProperties();
    }
}
