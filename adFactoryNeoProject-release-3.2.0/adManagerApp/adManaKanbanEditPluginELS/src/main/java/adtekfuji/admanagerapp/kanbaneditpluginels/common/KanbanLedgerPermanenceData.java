/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditpluginels.common;

import java.util.List;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;

/**
 *
 * @author e-mori
 */
public class KanbanLedgerPermanenceData {

    private String ledgerFilePass;
    private KanbanInfoEntity kanbanInfoEntity;
    private List<WorkKanbanInfoEntity> workKanbanInfoEntitys;
    private List<WorkKanbanInfoEntity> separateworkWorkKanbanInfoEntitys;
    private List<ActualResultEntity> actualResultInfoEntitys;

    public KanbanLedgerPermanenceData() {
    }

    public KanbanLedgerPermanenceData(String ledgerFilePass, KanbanInfoEntity kanbanInfoEntity, List<WorkKanbanInfoEntity> workKanbanInfoEntitys, List<WorkKanbanInfoEntity> separateworkWorkKanbanInfoEntitys, List<ActualResultEntity> actualResultInfoEntitys) {
        this.ledgerFilePass = ledgerFilePass;
        this.kanbanInfoEntity = kanbanInfoEntity;
        this.workKanbanInfoEntitys = workKanbanInfoEntitys;
        this.separateworkWorkKanbanInfoEntitys = separateworkWorkKanbanInfoEntitys;
        this.actualResultInfoEntitys = actualResultInfoEntitys;
    }

    public String getLedgerFilePass() {
        return ledgerFilePass;
    }

    public void setLedgerFilePass(String ledgerFilePass) {
        this.ledgerFilePass = ledgerFilePass;
    }

    public KanbanInfoEntity getKanbanInfoEntity() {
        return kanbanInfoEntity;
    }

    public void setKanbanInfoEntity(KanbanInfoEntity kanbanInfoEntity) {
        this.kanbanInfoEntity = kanbanInfoEntity;
    }

    public List<WorkKanbanInfoEntity> getWorkKanbanInfoEntitys() {
        return workKanbanInfoEntitys;
    }

    public void setWorkKanbanInfoEntitys(List<WorkKanbanInfoEntity> workKanbanInfoEntitys) {
        this.workKanbanInfoEntitys = workKanbanInfoEntitys;
    }

    public List<WorkKanbanInfoEntity> getSeparateworkWorkKanbanInfoEntitys() {
        return separateworkWorkKanbanInfoEntitys;
    }

    public void setSeparateworkWorkKanbanInfoEntitys(List<WorkKanbanInfoEntity> separateworkWorkKanbanInfoEntitys) {
        this.separateworkWorkKanbanInfoEntitys = separateworkWorkKanbanInfoEntitys;
    }

    public List<ActualResultEntity> getActualResultInfoEntitys() {
        return actualResultInfoEntitys;
    }

    public void setActualResultInfoEntitys(List<ActualResultEntity> actualResultInfoEntitys) {
        this.actualResultInfoEntitys = actualResultInfoEntitys;
    }
}
