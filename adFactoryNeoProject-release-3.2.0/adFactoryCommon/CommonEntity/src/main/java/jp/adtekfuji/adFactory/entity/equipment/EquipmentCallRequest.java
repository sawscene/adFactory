/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.equipment;

import java.io.Serializable;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.entity.operation.OperateAppEnum;

/**
 * 呼出要求情報
 * @author s-heya
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "equipmentCallRequest")
public class EquipmentCallRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(required = true)
    private Long equipmentId;

    @XmlElement(required = true)
    private Long organizationId;

    @XmlElement(required = false)
    private Boolean isCall;

    @XmlElement(required = false)
    private String reason;
    
    @XmlElement(required = false)
    private Long workId;

    @XmlElement(required = false)
    private Long workKanbanId;

    @XmlElement(required = false)
    private String operateApp;// 操作アプリ

    public EquipmentCallRequest() {
        this.equipmentId = 0L;
        this.organizationId = 0L;
        this.isCall = false;
	this.reason = "";
    }

    public EquipmentCallRequest(Long equipmentId, Long organizationId, Boolean isCall, String reason) {
        this.equipmentId = equipmentId;
        this.organizationId = organizationId;
        this.isCall = isCall;
	this.reason = reason;
    }

    public Long getEquipmentId() {
        return this.equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Long getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Boolean isCall() {
        return this.isCall;
    }

    /**
     * 呼出を取得する。
     * 
     * @return true: 呼出、false: 呼出解除
     */
    public Boolean getIsCall() {
        return this.isCall;
    }

    /**
     * 呼出を設定する。
     * 
     * @param isCall true: 呼出、false: 呼出解除
     */
    public void setIsCall(Boolean isCall) {
        this.isCall = isCall;
    }

    public String getReason() {
            return this.reason;
    }

    public void setReason(String reason) {
            this.reason = reason;
    }

    /**
     * 工程IDを取得する。
     * 
     * @return 
     */
    public Long getWorkId() {
        return workId;
    }

    /**
     * 工程IDを設定する。
     * 
     * @param workId 
     */
    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    /**
     * 工程カンバンID
     * @return
     */
    public Long getWorkKanbanId() {
        return workKanbanId;
    }

    /**
     * 工程カンバンID
     * @param workKanbanId 工程カンバンID
     */
    public void setWorkKanbanId(Long workKanbanId) {
        this.workKanbanId = workKanbanId;
    }

    /**
     * 操作アプリ取得
     * @return 操作アプリを取得
     */
    public OperateAppEnum getOperateApp() {
        return OperateAppEnum.toEnum(operateApp);
    }

    /**
     * 操作アプリ設定
     * @param operateApp 操作アプリ
     */
    public void setOperateApp(OperateAppEnum operateApp) {
        this.operateApp = operateApp.getName();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.equipmentId);
        hash = 31 * hash + Objects.hashCode(this.organizationId);
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
        final EquipmentCallRequest other = (EquipmentCallRequest) obj;
        return true;
    }

    @Override
    public String toString() {
        return "EquipmentCallRequest{" + "equipmentId=" + equipmentId + 
                ", organizationId=" + organizationId + 
                ", isCall=" + isCall + 
                ", reason=" + reason +
                ", workId=" + workId +
                ", workKanbanId=" + workKanbanId +
                ", operateApp="+ operateApp + "}";
    }
}
