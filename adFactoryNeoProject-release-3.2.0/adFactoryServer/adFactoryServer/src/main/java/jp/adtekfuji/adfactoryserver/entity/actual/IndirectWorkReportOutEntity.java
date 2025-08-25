package jp.adtekfuji.adfactoryserver.entity.actual;

import jp.adtekfuji.adFactory.utility.JsonUtils;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "indirectWorkReportOutInfoEntity")
@NamedNativeQueries({
        @NamedNativeQuery(name = "IndirectWorkReportOutEntity.reportOut", query = "WITH indirect_info as (SELECT tope.operation_id,  tope.operate_datetime,  tope.equipment_id,  tope.organization_id,  x.\"pairId\" pair_id,  x.\"suspendActualResultId\" suspend_actual_result_id,  x.\"indirectWorkId\" as indirect_work_id  FROM trn_operation tope,  jsonb_to_record(tope.add_info -> 'indirectWork') as x(\"indirectWorkId\" bigint,  \"pairId\" bigint,  \"suspendActualResultId\" text)) SELECT a1.operation_id, a1.equipment_id, a2.organization_id, a1.operate_datetime start_datetime, a2.operate_datetime comp_datetime, a1.indirect_work_id indirect_work_id, a1.suspend_actual_result_id suspend_actual_result_id FROM indirect_info a1  JOIN indirect_info a2 ON a1.operation_id = a2.pair_id WHERE a1.operate_datetime >= ?1 AND a2.operate_datetime < ?2", resultClass = IndirectWorkReportOutEntity.class),
})
@Entity
public class IndirectWorkReportOutEntity implements Serializable {

    @Id
    @Column(name = "operation_id")
    @XmlElement()
    private Long operationId;

    @Column(name = "equipment_id")
    @XmlElement()
    private Long equipmentId;

    @Column(name = "organization_id")
    @XmlElement()
    private Long organizationId;

    @Column(name = "start_datetime")
    @XmlElement()
    private Date startDateTime;

    @Column(name = "comp_datetime")
    @XmlElement()
    private Date compDateTime;

    @Column(name = "indirect_work_id")
    @XmlElement()
    private Long indirectWorkId;

    @Column(name = "suspend_actual_result_id")
    @XmlElement()
    private String suspendActualResultId;


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

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getCompDateTime() {
        return compDateTime;
    }

    public void setCompDateTime(Date compDateTime) {
        this.compDateTime = compDateTime;
    }

    public Long getIndirectWorkId() {
        return indirectWorkId;
    }

    public void setIndirectWorkId(Long indirectWorkId) {
        this.indirectWorkId = indirectWorkId;
    }

    public List<Long> getSuspendActualResultId() {
        if (Objects.isNull(this.suspendActualResultId)) {
            return new ArrayList<>();
        }
        return JsonUtils.jsonToObjects(this.suspendActualResultId, Long[].class);
    }

    public void setSuspendActualResultId(List<Long> suspendActualResultId) {
        if (Objects.isNull(suspendActualResultId) || suspendActualResultId.isEmpty()) {
            this.suspendActualResultId = null;
        } else {
            this.suspendActualResultId = JsonUtils.objectsToJson(suspendActualResultId);
        }
    }
}
