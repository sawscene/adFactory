/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adappentity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import jp.adtekfuji.adreporter.utils.DateUtilsEx;
import jp.adtekfuji.adreporter.utils.StringUtils;
import jp.adtekfuji.adappentity.enumerate.DisplayedStatusEnum;
import jp.adtekfuji.adappentity.enumerate.KanbanStatusEnum;

/**
 * 工程カンバン
 *
 * @author nar-nakamura
 */
public class WorkKanbanEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long workKanbanId;
    private Long parentId;
    private Long fkKanbanId;
    private Long fkWorkflowId;
    private Long fkWorkId;
    private String workName;
    private Boolean separateWorkFlag;
    private Boolean implementFlag;
    private Boolean skipFlag;
    private String startDatetime;
    private String compDatetime;
    private Integer taktTime;
    private Integer sumTimes;
    private Long fkUpdatePersonId;
    private String updateDatetime;
    private KanbanStatusEnum workStatus;
    private Integer workKanbanOrder;
    private List<WorkKanbanPropertyEntity> properties;
    private List<Long> equipments;
    private List<Long> organizations;
    private Integer serialNumber;

    private Integer actualNum1;
    private Integer actualNum2;
    private Integer actualNum3;

    /**
     * 1日の完成数
     */
    private Integer completeCount;

    /**
     * 工程開始日時
     */
    private String actualStartTime;

    /**
     * 工程完了日時
     */
    private String actualCompTime;

    private Date started;
    private Date completed;

    private BooleanProperty selectedProperty;
    private StringProperty timeLeftProperty;
    private ObjectProperty<KanbanStatusEnum> statusProperty;
    private Node node;
    private long workTime = 0;
    private long breakTime = 0;

    public String getName() {
        return this.workName;
    }

    public void setName(String workName) {
        this.workName = workName;
    }

    public Integer getSerialNumber() {
        if (Objects.isNull(this.serialNumber)) {
            return 0;
        }
        return this.serialNumber;
    }

    public Date getStartDatetime() {
        return DateUtilsEx.toDate(this.startDatetime);
    }

    public void setStartDatetime(Date date) {
        this.startDatetime = DateUtilsEx.format(date);
    }

    public Date getCompDatetime() {
        return DateUtilsEx.toDate(this.compDatetime);
    }

    public void setCompDatetime(Date date) {
        this.compDatetime = DateUtilsEx.format(date);
    }

    public long getWorkKanbanId() {
        return this.workKanbanId;
    }

    public void setWorkKanbanId(long workKanbanId) {
        this.workKanbanId = workKanbanId;
    }

    public long getParentId() {
        return this.parentId;
    }

    public long getFkKanbanId() {
        return this.fkKanbanId;
    }

    public void setFkKanbanId(long fkKanbanId) {
        this.fkKanbanId = fkKanbanId;
    }

    public long getFkWorkflowId() {
        if (Objects.isNull(this.fkWorkflowId)) {
            return 0L;
        }
        return this.fkWorkflowId;
    }

    public void setFkWorkflowId(long fkWorkflowId) {
        this.fkWorkflowId = fkWorkflowId;
    }

    public long getFkWorkId() {
        return this.fkWorkId;
    }

    public void setFkWorkId(long fkWorkId) {
        this.fkWorkId = fkWorkId;
    }

    public void setSeparateWorkFlag(boolean separateWorkFlag) {
        this.separateWorkFlag = separateWorkFlag;
    }

    public boolean getImplementFlag() {
        return this.implementFlag;
    }

    public void setImplementFlag(Boolean implementFlag) {
        this.implementFlag = implementFlag;
    }

    public boolean getSkipFlag() {
        return this.skipFlag;
    }

    public void setSkipFlag(Boolean skipFlag) {
        this.skipFlag = skipFlag;
    }

    public Integer getTaktTime() {
        if (Objects.isNull(this.taktTime)) {
            return 0;
        }
        return this.taktTime;
    }

    public void setTaktTime(Integer taktTime) {
        this.taktTime = taktTime;
    }

    public Integer getSumTimes() {
        if (Objects.isNull(this.sumTimes)) {
            return 0;
        }
        return this.sumTimes;
    }

    public void setSumTimes(int sumTimes) {
        this.sumTimes = sumTimes;
    }

    public void setFkUpdatePersonId(long fkUpdatePersonId) {
        this.fkUpdatePersonId = fkUpdatePersonId;
    }

    public void setUpdateDatetime(Date updateDatetime) {
        this.updateDatetime = DateUtilsEx.format(updateDatetime);
    }

    public Date getActualStartTime() {
        return Objects.nonNull(this.actualStartTime) ? DateUtilsEx.toDate(this.actualStartTime) : null;
    }

    public void setActualStartTime(Date actualStartTime) {
        this.actualStartTime = DateUtilsEx.format(actualStartTime);
    }

    public Date getActualCompTime() {
        return Objects.nonNull(this.actualCompTime) ? DateUtilsEx.toDate(this.actualCompTime) : null;
    }

    public void setActualCompTime(Date actualCompTime) {
        this.actualCompTime = DateUtilsEx.format(actualCompTime);
    }

    public Date getActualStarted() {
        return this.started;
    }

    public void setActualStarted(Date value) {
        this.started = value;
    }

    public Date getActualCompleted() {
        return this.completed;
    }

    public void setActualCompleted(Date value) {
        this.completed = value;
    }

    public KanbanStatusEnum getWorkStatus() {
        if (Objects.isNull(this.workStatus)) {
            return KanbanStatusEnum.PLANNED;
        }
        return this.workStatus;
    }

    public int getWorkKanbanOrder() {
        return workKanbanOrder;
    }

    public void setWorkKanbanOrder(Integer workKanbanOrder) {
        this.workKanbanOrder = workKanbanOrder;
    }

    public List<WorkKanbanPropertyEntity> getProperties() {
        return this.properties;
    }

    public List<Long> getEquipments() {
        return this.equipments;
    }

    public void setEquipments(List<Long> equipments) {
        this.equipments = equipments;
    }

    public List<Long> getOrganizations() {
        return this.organizations;
    }

    public void setOrganizations(List<Long> organizations) {
        this.organizations = organizations;
    }

    public void setWorkStatus(KanbanStatusEnum workStatus) {
        this.workStatus = workStatus;
        this.statusProperty().set(workStatus);
    }

    public DisplayedStatusEnum getDisplayedStatus() throws Exception {
        return this.getDisplayedStatus(System.currentTimeMillis());
    }

    /**
     * 進捗ステータスを取得する。
     *
     * @param now
     * @return
     * @throws Exception
     */
    public DisplayedStatusEnum getDisplayedStatus(long now) throws Exception {
        DisplayedStatusEnum status = DisplayedStatusEnum.WORK_NORMAL;
        switch (this.getWorkStatus()) {
            case PLANNED:
                if (this.getStartDatetime().getTime() < now) {
                    status = DisplayedStatusEnum.PLAN_DELAYSTART;
                } else {
                    status = DisplayedStatusEnum.PLAN_NORMAL;
                }
                break;

            case WORKING:
                if (this.getCompDatetime().getTime() < now) {
                    status = DisplayedStatusEnum.WORK_DELAYCOMP;
                } else if (Objects.nonNull(this.started) && this.getStartDatetime().getTime() < this.started.getTime()) {
                    status = DisplayedStatusEnum.WORK_DELAYSTART;
                }
                break;

            case COMPLETION:
                if (this.getCompDatetime().getTime() >= now) {
                    status = DisplayedStatusEnum.COMP_NORMAL;
                } else {
                    status = DisplayedStatusEnum.COMP_DELAYCOMP;
                }
                break;

            case INTERRUPT:
                status = DisplayedStatusEnum.INTERRUPT_NORMAL;
                break;

            case SUSPEND:
                status = DisplayedStatusEnum.SUSPEND_NORMAL;
                break;
        }

        return status;
    }

    public Boolean isSelected() {
        return this.selectedProperty().get();
    }

    public void setSelected(Boolean value) {
        if (this.getImplementFlag()) {
            this.selectedProperty().set(value);
        }
    }

    public BooleanProperty selectedProperty() {
        if (Objects.isNull(this.selectedProperty)) {
            this.selectedProperty = new SimpleBooleanProperty(false);
        }
        return this.selectedProperty;
    }

    public void setTimeLeft(String value) {
        this.timeLeftProperty().set(value);
    }

    public StringProperty timeLeftProperty() {
        if (Objects.isNull(this.timeLeftProperty)) {
            this.timeLeftProperty = new SimpleStringProperty();
        }
        return this.timeLeftProperty;
    }

    public ObjectProperty<KanbanStatusEnum> statusProperty() {
        if (Objects.isNull(this.statusProperty)) {
            this.statusProperty = new SimpleObjectProperty<>();
        }
        return this.statusProperty;
    }

    public Node getNode() {
        return this.node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public void setWorkTime(long workTime) {
        this.workTime = workTime;
    }

    public long getWorkTime() {
        return this.workTime;
    }

    public long getBreakTime() {
        return breakTime;
    }

    public void setBreakTime(long breakTime) {
        this.breakTime = breakTime;
    }

    public Integer getCompleteCount() {
        return completeCount;
    }

    public void setCompleteCount(int completeCount) {
        this.completeCount = completeCount;
    }

    /**
     * プロパティを取得する
     *
     * @param name
     * @return
     */
    public String getProperty(String name) {
        String value = null;
        for (WorkKanbanPropertyEntity property : this.properties) {
            if (StringUtils.equals(name, property.getWorkKanbanPropName())) {
                value = property.getWorkKanbanPropValue();
                break;
            }
        }
        return value;
    }

    public Integer getActualNum1() {
        return actualNum1;
    }

    public void setActualNum1(Integer actualNum1) {
        this.actualNum1 = actualNum1;
    }

    public Integer getActualNum2() {
        return actualNum2;
    }

    public void setActualNum2(Integer actualNum2) {
        this.actualNum2 = actualNum2;
    }

    public Integer getActualNum3() {
        return actualNum3;
    }

    public void setActualNum3(Integer actualNum3) {
        this.actualNum3 = actualNum3;
    }

    @Override
    public String toString() {
        return "WorkKanbanEntity{" + "workKanbanId=" + workKanbanId + ", fkKanbanId=" + fkKanbanId + ", fkWorkflowId=" + fkWorkflowId + ", fkWorkId=" + fkWorkId
                + ", workName=" + workName + ", implementFlag=" + implementFlag + ", skipFlag=" + skipFlag + ", startDatetime=" + startDatetime
                + ", compDatetime=" + compDatetime + ", taktTime=" + taktTime + ", sumTimes=" + sumTimes + ", workStatus=" + workStatus + ", serialNumber=" + serialNumber
                + ", actualStartTime=" + actualStartTime + '}';
    }
}
