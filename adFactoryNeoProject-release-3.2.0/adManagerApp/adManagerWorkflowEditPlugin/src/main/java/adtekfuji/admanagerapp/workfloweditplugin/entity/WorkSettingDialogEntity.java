/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.entity;

import java.util.*;

import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.schedule.ScheduleConditionInfoEntity;

/**
 * 工程設定ダイアログエンティティ
 *
 * @author ta.ito
 */
public class WorkSettingDialogEntity {

    private long taktTime;
    private Integer standardDay;
    private Date startTime;
    private Date endTime;
    private Boolean skip;
    private List<EquipmentInfoEntity> equipments;
    private List<OrganizationInfoEntity> organizations;
    private LinkedList<ScheduleConditionInfoEntity> scheduleConditionInfoEntity;
    private boolean editSingle;
    private boolean isSeparatework;

    public WorkSettingDialogEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param taktTime
     * @param startTime
     * @param endTime
     * @param skip
     * @param equipments
     * @param organizations
     * @param editSingle
     */
    public WorkSettingDialogEntity(long taktTime, Date startTime, Date endTime, boolean skip, List<EquipmentInfoEntity> equipments, List<OrganizationInfoEntity> organizations, boolean editSingle, boolean isSeparatework, LinkedList<ScheduleConditionInfoEntity> scheduleConditionInfoEntity) {
        this.taktTime = taktTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.skip = skip;
        this.equipments = equipments;
        this.organizations = organizations;
        this.scheduleConditionInfoEntity = scheduleConditionInfoEntity;
        this.editSingle = editSingle;
        this.isSeparatework = isSeparatework;
    }

    public long getTaktTime() {
        return taktTime;
    }

    public void setTaktTime(long taktTime) {
        this.taktTime = taktTime;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Boolean getSkip() {
        return this.skip;
    }

    public void setSkip(Boolean skip) {
        this.skip = skip;
    }

    public List<EquipmentInfoEntity> getEquipments() {
        return this.equipments;
    }

    public void setEquipments(List<EquipmentInfoEntity> equipments) {
        this.equipments = equipments;
    }

    public List<OrganizationInfoEntity> getOrganizations() {
        return this.organizations;
    }

    public void setOrganizations(List<OrganizationInfoEntity> organizations) {
        this.organizations = organizations;
    }

    public boolean isEditSingle() {
        return this.editSingle;
    }

    public boolean isSeparatework() {
        return isSeparatework;
    }

    public void setSeparatework(boolean separatework) {
        isSeparatework = separatework;
    }

    public Integer getStandardDay() {
        return this.standardDay;
    }

    public void setStandardDay(Integer standardDay) {
        this.standardDay = standardDay;
    }

    public LinkedList<ScheduleConditionInfoEntity> getSchedule() {
        if (Objects.isNull(this.scheduleConditionInfoEntity)) {
            return new LinkedList<>();
        }
        return scheduleConditionInfoEntity;
    }

    public void setScheduleConditionInfoEntity(LinkedList<ScheduleConditionInfoEntity> scheduleConditionInfoEntity) {
        this.scheduleConditionInfoEntity = scheduleConditionInfoEntity;
    }
}
