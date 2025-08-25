/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.ledger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jp.adtekfuji.adFactory.entity.schedule.ScheduleConditionInfoEntity;
import jp.adtekfuji.adFactory.enumerate.LedgerTypeEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 帳票出力ターゲット
 *
 * @author ke.yokoi
 */
public class LedgerConditionEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("equipment_ids")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Long> equipmentIds;

    @JsonProperty("organization_ids")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Long> organizationIds;

    @JsonProperty("key_tag")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<NameValueEntity> keyTag;

    @JsonProperty("no_remove_Tags")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean noRemoveTags = false;

    @JsonProperty("schedule")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ScheduleConditionInfoEntity> scheduleConditionInfoEntity;

    @JsonProperty("ledger_type")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LedgerTypeEnum ledgerType;

    public LedgerConditionEntity()
    {}

    public LedgerConditionEntity(List<Long> equipmentIds, List<Long> organizationIds, Boolean noRemoveTags, LedgerTypeEnum ledgerType, List<NameValueEntity> keyTag, List<ScheduleConditionInfoEntity> scheduleConditionInfoEntities) {
        this.equipmentIds = equipmentIds;
        this.organizationIds = organizationIds;
        this.noRemoveTags = noRemoveTags;
        this.ledgerType = ledgerType;
        this.keyTag = keyTag;
        this.scheduleConditionInfoEntity = scheduleConditionInfoEntities;
    }

    public List<Long> getEquipmentIds() {
        if (Objects.isNull(this.equipmentIds)) {
            return new ArrayList<>();
        }
        return equipmentIds;
    }

    public void setEquipmentIds(List<Long> equipmentIds) {
        if (Objects.isNull(equipmentIds) || equipmentIds.isEmpty()) {
            this.equipmentIds = null;
            return;
        }
        this.equipmentIds = equipmentIds;
    }

    public List<Long> getOrganizationIds() {
        if (Objects.isNull(this.organizationIds)) {
            return new ArrayList<>();
        }
        return organizationIds;
    }

    public void setOrganizationIds(List<Long> organizationIds) {
        if (Objects.isNull(organizationIds) || organizationIds.isEmpty()) {
            this.organizationIds = null;
            return;
        }
        this.organizationIds = organizationIds;
    }

    public List<NameValueEntity> getKeyTag() {
        if (Objects.isNull(keyTag)) {
            return new ArrayList<>();
        }
        return keyTag;
    }

    public void setKeyTag(List<NameValueEntity> keyTag) {
        if (Objects.isNull(keyTag) || !keyTag.isEmpty()) {
            this.keyTag = keyTag;
        }
    }

    public Boolean getNoRemoveTags() {
        return noRemoveTags;
    }

    public void setNoRemoveTags(Boolean noRemoveTags) {
        this.noRemoveTags = noRemoveTags;
    }

    public LedgerTypeEnum getLedgerType() {
        return ledgerType;
    }

    public void setLedgerType(LedgerTypeEnum ledgerType) {
        this.ledgerType = ledgerType;
    }

    public List<ScheduleConditionInfoEntity> getScheduleConditionInfoEntity() {
        if (Objects.isNull(scheduleConditionInfoEntity)) {
            return new ArrayList<>();
        }
        return scheduleConditionInfoEntity;
    }

    public void setScheduleConditionInfoEntity(List<ScheduleConditionInfoEntity> scheduleConditionInfoEntity) {
        if (scheduleConditionInfoEntity.isEmpty() || scheduleConditionInfoEntity.stream().allMatch(ScheduleConditionInfoEntity::isEmpty)) {
            this.scheduleConditionInfoEntity = null;
            return;
        }
        this.scheduleConditionInfoEntity = scheduleConditionInfoEntity;
    }
}
