package jp.adtekfuji.adfactoryserver.entity.operation;

import org.simpleframework.xml.Default;
import org.simpleframework.xml.DefaultType;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * 作業者操作実績
 *
 * @author yu.nara
 */
@XmlRootElement(name = "operation_actual")
@XmlAccessorType(XmlAccessType.FIELD)
public class OperationActualEntity implements Serializable {

    @XmlElement()
    private Long equipmentId = null;// 設備ID

    @XmlElement()
    private Long organizationId = null;// 組織ID

    @XmlElement()
    private String operateApp = null;// 操作アプリ

    @XmlElement()
    private String operationType = null;// 操作タイプ

    @XmlElement()
    private Long workTime = null; // 操作実績

    public OperationActualEntity() {
    }

    public OperationActualEntity(Long equipmentId, Long organizationId, String operateApp, String operationType, Long workTime) {
        this.equipmentId = equipmentId;
        this.organizationId = organizationId;
        this.operateApp = operateApp;
        this.operationType = operationType;
        this.workTime = workTime;
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

    public Long getWorkTime() {
        return workTime;
    }

    public void setWorkTime(Long workTime) {
        this.workTime = workTime;
    }
}
