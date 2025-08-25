/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.entity.monitor;

import java.util.List;
import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.forfujiapp.entity.unit.UnitPropertyInfoEntity;

/**
 * 進捗モニタリスト表示用情報クラス
 *
 * @author yu.kikukawa
 * @version 1.7.3
 * @since 2017.07.26
 */
public class MonitorList2InfoEntity {

    private Long unitId;
    private KanbanStatusEnum kanban1Status;
    private KanbanStatusEnum kanban2Status;
    private Long kanban1Id;
    private Long kanban2Id;
    private OrganizationInfoEntity organization1;
    private OrganizationInfoEntity organization2;
    private List<UnitPropertyInfoEntity> unitPropertyCollection;

    public MonitorList2InfoEntity() {
    }

    public MonitorList2InfoEntity(Long unitId, KanbanStatusEnum kanban1Status, KanbanStatusEnum kanban2Status, Long kanban1Id, Long kanban2Id, OrganizationInfoEntity organization1, OrganizationInfoEntity organization2) {
        this.unitId = unitId;
        this.kanban1Status = kanban1Status;
        this.kanban2Status = kanban2Status;
        this.kanban1Id = kanban1Id;
        this.kanban2Id = kanban2Id;
        this.organization1 = organization1;
        this.organization2 = organization2;
    }

    public MonitorList2InfoEntity copy(MonitorList2InfoEntity in) {
        this.setUnitId(in.unitId);
        this.setKanban1Status(in.kanban1Status);
        this.setKanban2Status(in.kanban2Status);
        this.setKanban1Id(in.kanban1Id);
        this.setKanban2Id(in.kanban2Id);
        this.setOrganization1(in.organization1);
        this.setOrganization2(in.organization2);
        this.setUnitPropertyCollection(in.unitPropertyCollection);
        return this;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public KanbanStatusEnum getKanban1Status() {
        return kanban1Status;
    }

    public void setKanban1Status(KanbanStatusEnum kanban1Status) {
        this.kanban1Status = kanban1Status;
    }

    public KanbanStatusEnum getKanban2Status() {
        return kanban2Status;
    }

    public void setKanban2Status(KanbanStatusEnum kanban2Status) {
        this.kanban2Status = kanban2Status;
    }

    public Long getKanban1Id() {
        return kanban1Id;
    }

    public void setKanban1Id(Long kanban1Id) {
        this.kanban1Id = kanban1Id;
    }

    public Long getKanban2Id() {
        return kanban2Id;
    }

    public void setKanban2Id(Long kanban2Id) {
        this.kanban2Id = kanban2Id;
    }

    public OrganizationInfoEntity getOrganization1() {
        return organization1;
    }

    public void setOrganization1(OrganizationInfoEntity organization1) {
        this.organization1 = organization1;

    }

    public OrganizationInfoEntity getOrganization2() {
        return organization2;
    }

    public void setOrganization2(OrganizationInfoEntity organization2) {
        this.organization2 = organization2;
    }

    public List<UnitPropertyInfoEntity> getUnitPropertyCollection() {
        return this.unitPropertyCollection;
    }

    public void setUnitPropertyCollection(List<UnitPropertyInfoEntity> unitPropertyCollection) {
        this.unitPropertyCollection = unitPropertyCollection;
    }

    public StringProperty getPropertyValue(String name) {
        if (Objects.nonNull(unitPropertyCollection)) {
            for (UnitPropertyInfoEntity entity : unitPropertyCollection) {
                if (entity.getUnitPropertyName().equals(name)) {
                    return entity.unitPropertyValueProperty();
                }
            }
        }
        return new SimpleStringProperty();
    }

    public void update(MonitorList2InfoEntity in) {
        this.setUnitId(in.unitId);
        this.setKanban1Status(in.kanban1Status);
        this.setKanban2Status(in.kanban2Status);
        this.setKanban1Id(in.kanban1Id);
        this.setKanban2Id(in.kanban2Id);
        this.setOrganization1(in.organization1);
        this.setOrganization2(in.organization2);
        this.setUnitPropertyCollection(in.unitPropertyCollection);
    }

    @Override
    public String toString() {
        return "MonitorList2InfoEntity{" + "unitId=" + unitId + ", kanban1Status=" + kanban1Status.toString() + ", kanban2Status=" + kanban2Status.toString() + ", organization1=" + organization1.getOrganizationName() + ", organization2=" + organization2.getOrganizationName() + '}';
    }
}
