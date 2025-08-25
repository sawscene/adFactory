/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.adinterface.command;

import java.io.Serializable;

/**
 * 呼び出し通知コマンド
 * 
 * @author ke.yokoi
 */
public class CallingNoticeCommand implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean isCall;
    private Long equipmentId;
    private Long organizationId;
    private String callReason;
    private Long reason_order;
    private Long workId;

    public CallingNoticeCommand() {
    }

    public CallingNoticeCommand(Boolean isCall, Long equipmentId, Long organizationId, String reason) {
        this.isCall = isCall;
        this.equipmentId = equipmentId;
        this.organizationId = organizationId;
        this.callReason = reason;
        this.reason_order = -1L;
    }

    public CallingNoticeCommand isCall(Boolean isCall) {
        this.isCall = isCall;
        return this;
    }

    public CallingNoticeCommand equipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
        return this;
    }

    public CallingNoticeCommand organizationId(Long organizationId) {
        this.organizationId = organizationId;
        return this;
    }

    public CallingNoticeCommand reason(String reason) {
        this.callReason = reason;
        return this;
    }

    public Boolean getIsCall() {
        return isCall;
    }

    public void setIsCall(Boolean isCall) {
        this.isCall = isCall;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getReason() {
        return this.callReason;
    }

    public void setReason(String reason) {
        this.callReason = reason;
    }

    public void setReasonOrder(Long order) {
        this.reason_order = order;
    }

    public Long getReasonOrder() {
        return this.reason_order;
    }

    public Long getWorkId() {
        return workId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
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
        final CallingNoticeCommand other = (CallingNoticeCommand) obj;
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("CallingNoticeCommand{isCall=").append(this.isCall)
                .append(", equipmentId=").append(this.equipmentId)
                .append(", organizationId=").append(this.organizationId)
                .append(", callReason=").append(this.callReason)
                .append(", reason_order=").append(this.reason_order)
                .append(", workId=").append(this.workId)
                .append("}")
                .toString();
    }

}
