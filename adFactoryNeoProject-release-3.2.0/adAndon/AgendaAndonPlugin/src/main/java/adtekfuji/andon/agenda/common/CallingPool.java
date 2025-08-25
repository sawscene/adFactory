/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.andon.agenda.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.adinterface.command.CallingNoticeCommand;

/**
 * 呼出し情報
 * @author kenji.yokoi
 */
public class CallingPool {
    static public CallingPool instance;
    final private List<Long> equipment = new ArrayList<>();
    final private List<Long> organization = new ArrayList<>();
    final private List<Long> work = new ArrayList<>();
    private String callReason;
    
    private CallingPool () {}
    
    static public CallingPool getInstance() {
        if (Objects.isNull(instance)) {
            instance = new CallingPool();
        }
        return instance;
    }

    /**
     * 呼出し設定を記憶
     * @param callingCommand 
     */
    public void setCaller(CallingNoticeCommand callingCommand) {
        if (callingCommand.getIsCall()) {
            if (!this.equipment.contains(callingCommand.getEquipmentId())) {
                this.equipment.add(callingCommand.getEquipmentId());
            }
            if (!this.organization.contains(callingCommand.getOrganizationId())) {
                this.organization.add(callingCommand.getOrganizationId());
            }
            if (!this.work.contains(callingCommand.getWorkId())) {
                this.work.add(callingCommand.getWorkId());
            }
            this.callReason = callingCommand.getReason();
        } else {
            if (this.equipment.contains(callingCommand.getEquipmentId())) {
                this.equipment.remove(callingCommand.getEquipmentId());
            }
            if (this.organization.contains(callingCommand.getOrganizationId())) {
                this.organization.remove(callingCommand.getOrganizationId());
            }
            if (this.work.contains(callingCommand.getWorkId())) {
                this.work.remove(callingCommand.getWorkId());
            }
            this.callReason = "";
        }
    }

    /**
     * 設備の呼出しがあるか
     * @param equipmentId
     * @return 呼出し有無
     */
    public boolean isEquipmentCall(Long equipmentId) {
        return this.equipment.contains(equipmentId);
    }

    /**
     * 組織の呼出しがあるか
     * @param organizationId
     * @return 呼出し有無
     */
    public boolean isOrganizationCall(Long organizationId) {
        return this.organization.contains(organizationId);
    }

    /**
     * 組織の呼出しがあるか
     * @param organizationList
     * @return 
     */
    public boolean containsOrganizationCall(List<Long> organizationList) {
        for (Long id : organizationList) {
            if (this.organization.contains(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 工程の呼出しがあるか
     * @param workKanbanId
     * @return 呼出し有無
     */
    public boolean isWorkCall(Long workKanbanId) {
        return this.work.contains(workKanbanId);
    }

    /**
     * 工程の呼出しがあるか
     * @param workKanbanIdList
     * @return 
     */
    public boolean containsWorkCall(List<Long> workKanbanIdList) {
        for (Long id : workKanbanIdList) {
            if (this.work.contains(id)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 呼び出し理由の取得
     * @return 
     */
    public String getCallReason() {
        return callReason;
    }
}
