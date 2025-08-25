package jp.adtekfuji.adfactoryserver.entity.summaryreport;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Date;

@Entity
public class OperationActualWorkEntity {

    @Id
    @Column(name="operation_id")
    private Long operationId;

    @Column(name="equipment_id")
    private Long equipmentId;

    @Column(name="organization_id")
    private Long organizationId;

    @Column(name="operate_app")
    private String operateApp;

    @Column(name="operation_type")
    private String operationType;

    @Column(name ="start_time")
    private Date startTime;

    @Column(name ="end_time")
    private Date endTime;


    public OperationActualWorkEntity() {}

    public OperationActualWorkEntity(Long operationId, Long equipmentId, Long organizationId, String operateApp, String operationType) {
        this.operationId = operationId;
        this.equipmentId = equipmentId;
        this.organizationId = organizationId;
        this.operateApp = operateApp;
        this.operationType = operationType;
    }

    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
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

    public String getOperateApp() {
        return operateApp;
    }

    public void setOperateApp(String operateApp) {
        this.operateApp = operateApp;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
