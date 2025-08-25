/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.adinterface.command;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 実績データ
 *
 * @author s-heya
 */
public class WorkResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String kanbanParentName;        // カンバン階層名
    private final String kanbanName;              // カンバン名
    private final String kanbanSubname;           // サブカンバン名
    private final String workflowName;            // 工程順名
    private final String workName;                // 工程名
    private final String organizationName;        // 組織名
    private final String organizationIdentName;   // 組織識別名
    private final String equipmentName;           // 設備名
    private final String equipmentIdentName;      // 設備識別名
    private final String interruptReason;         // 中断理由
    private final String delayReason;             // 遅延理由
    private final Date implementDatetime;         // 実施日時
    private final Integer taktTime;               // タクトタイム
    private final Integer workingTime;            // 作業時間[ms]
    private final Long pairId;                    // ペアID
    private List<WorkDetail> details = null;      // 実績詳細データ

    /**
     *
     * @param kanbanParentName
     * @param kanbanName
     * @param kanbanSubname
     * @param workflowName
     * @param workName
     * @param organizationName
     * @param organizationIdentName
     * @param equipmentName
     * @param equipmentIdentName
     * @param interruptReason
     * @param delayReason
     * @param implementDatetime
     * @param taktTime
     * @param workingTime
     * @param pairId
     */
    public WorkResult(String kanbanParentName, String kanbanName, String kanbanSubname, String workflowName, String workName, String organizationName, String organizationIdentName, String equipmentName, String equipmentIdentName, String interruptReason, String delayReason, Date implementDatetime, Integer taktTime, Integer workingTime, Long pairId) {
        this.kanbanParentName = kanbanParentName;
        this.kanbanName = kanbanName;
        this.kanbanSubname = kanbanSubname;
        this.workflowName = workflowName;
        this.workName = workName;
        this.organizationName = organizationName;
        this.organizationIdentName = organizationIdentName;
        this.equipmentName = equipmentName;
        this.equipmentIdentName = equipmentIdentName;
        this.interruptReason = interruptReason;
        this.delayReason = delayReason;
        this.implementDatetime = implementDatetime;
        this.taktTime = taktTime;
        this.workingTime = workingTime;
        this.pairId = pairId;
    }

    /**
     *
     * @return
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     *
     * @return
     */
    public String getKanbanParentName() {
        return this.kanbanParentName;
    }

    /**
     *
     * @return
     */
    public String getKanbanName() {
        return this.kanbanName;
    }

    /**
     *
     * @return
     */
    public String getKanbanSubname() {
        return this.kanbanSubname;
    }

    /**
     *
     * @return
     */
    public String getWorkflowName() {
        return this.workflowName;
    }

    /**
     *
     * @return
     */
    public String getWorkName() {
        return this.workName;
    }

    /**
     *
     * @return
     */
    public String getOrganizationName() {
        return this.organizationName;
    }

    /**
     *
     * @return
     */
    public String getOrganizationIdentName() {
        return this.organizationIdentName;
    }

    /**
     *
     * @return
     */
    public String getEquipmentName() {
        return this.equipmentName;
    }

    /**
     *
     * @return
     */
    public String getEquipmentIdentName() {
        return this.equipmentIdentName;
    }

    /**
     *
     * @return
     */
    public String getInterruptReason() {
        return this.interruptReason;
    }

    /**
     *
     * @return
     */
    public String getDelayReason() {
        return this.delayReason;
    }

    /**
     *
     * @return
     */
    public Date getImplementDatetime() {
        return this.implementDatetime;
    }

    /**
     *
     * @return
     */
    public Integer getTaktTime() {
        return this.taktTime;
    }

    /**
     *
     * @return
     */
    public Integer getWorkingTime() {
        return this.workingTime;
    }

    /**
     *
     * @return
     */
    public Long getPairId() {
        return this.pairId;
    }

    /**
     *
     * @return
     */
    public List<WorkDetail> getDetails() {
        return this.details;
    }

    /**
     *
     * @param details
     */
    public void setDetails(List<WorkDetail> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return new StringBuilder("WorkResult{")
                .append("kanbanParentName=").append(this.kanbanParentName)
                .append(", kanbanName=").append(this.kanbanName)
                .append(", kanbanSubname=").append(this.kanbanSubname)
                .append(", workflowName=").append(this.workflowName)
                .append(", workName=").append(this.workName)
                .append(", organizationName=").append(this.organizationName)
                .append(", organizationIdentName=").append(this.organizationIdentName)
                .append(", equipmentName=").append(this.equipmentName)
                .append(", equipmentIdentName=").append(this.equipmentIdentName)
                .append(", interruptReason=").append(this.interruptReason)
                .append(", delayReason=").append(this.delayReason)
                .append(", implementDatetime=").append(this.implementDatetime)
                .append(", taktTime=").append(this.taktTime)
                .append(", workingTime=").append(this.workingTime)
                //.append(", pairId=").append(this.pairId)
                //.append(", details=").append(this.details)
                .append("}")
                .toString();
    }
}
