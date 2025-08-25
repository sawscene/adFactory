package jp.adtekfuji.adFactory.entity.actual;

import jp.adtekfuji.adFactory.utility.JsonUtils;

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
public class IndirectWorkReportOutInfoEntity implements Serializable {

    @XmlElement()
    private Long operationId;

    @XmlElement()
    private Long equipmentId;

    @XmlElement()
    private Long organizationId;

    @XmlElement()
    private Date startDateTime;

    @XmlElement()
    private Date compDateTime;

    @XmlElement()
    private Long indirectWorkId;

    @XmlElement()
    private String suspendActualResultId;

    public IndirectWorkReportOutInfoEntity() {
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
