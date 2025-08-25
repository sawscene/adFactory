/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.view;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adfactoryserver.utility.PgJsonbConverter;

/**
 * 実績出力情報(VIEW)
 *
 * @author nar-nakamura
 */
@Entity
@Table(name = "view_report_out")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "reportOut")
@NamedQueries({
    // 実績IDを指定して、実績出力情報を取得する。
    @NamedQuery(name = "ReportOutEntity.findByActualId", query = "SELECT a FROM ReportOutEntity a WHERE a.actualId = :actualId"),
})
@Cacheable(false)
public class ReportOutEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "actual_id")
    private Long actualId;// 実績ID

    @Column(name = "kanban_hierarchy_name")
    private String kanbanParentName;// カンバン階層名

    @Column(name = "fk_kanban_id")
    @XmlElement(name = "fkKanbanId")
    private Long kanbanId;// カンバンID

    @Column(name = "kanban_name")
    private String kanbanName;// カンバン名

    @Column(name = "kanban_subname")
    private String kanbanSubname;// サブカンバン名

    @Column(name = "workflow_hierarchy_name")
    private String workflowParentName;// 工程順階層名

    @Column(name = "fk_workflow_id")
    @XmlElement(name = "fkWorkflowId")
    private Long workflowId;// 工程順ID

    @Column(name = "workflow_name")
    private String workflowName;// 工程順名

    @Column(name = "workflow_rev")
    private Integer workflowRev;// 工程順版数

    @Column(name = "work_hierarchy_name")
    private String workParentName;// 工程階層名

    @Column(name = "fk_work_id")
    @XmlElement(name = "fkWorkId")
    private Long workId;// 工程ID

    @Column(name = "work_name")
    private String workName;// 工程名

    @Column(name = "fk_work_kanban_id")
    @XmlElement(name = "fkWorkKanbanId")
    private Long workKanbanId;// 工程カンバンID

    @Column(name = "separate_work_flag")
    private Boolean isSeparateWork;// 追加工程フラグ

    @Column(name = "skip_flag")
    private Boolean isSkip;// スキップフラグ

    @Column(name = "parent_organization_name")
    private String organizationParentName;// 親組織の組織名

    @Column(name = "parent_organization_identify")
    private String organizationParentIdentName;// 親組織の組織識別名

    @Column(name = "fk_organization_id")
    @XmlElement(name = "fkOrganizationId")
    private Long organizationId;// 組織ID

    @Column(name = "organization_name")
    private String organizationName;// 組織名

    @Column(name = "organization_identify")
    private String organizationIdentName;// 組織識別名

    @Column(name = "parent_equipment_name")
    private String equipmentParentName;// 親設備の設備名

    @Column(name = "parent_equipment_identify")
    private String equipmentParentIdentName;// 親設備の設備識別名

    @Column(name = "fk_equipment_id")
    @XmlElement(name = "fkEquipmentId")
    private Long equipmentId;// 設備ID

    @Column(name = "equipment_name")
    private String equipmentName;// 設備名

    @Column(name = "equipment_identify")
    private String equipmentIdentName;// 設備識別名

    @Enumerated(EnumType.STRING)
    @Column(name = "actual_status")
    private KanbanStatusEnum actualStatus;// 工程実績ステータス

    @Column(name = "interrupt_reason")
    private String interruptReason;// 中断理由

    @Column(name = "delay_reason")
    private String delayReason;// 遅延理由

    @Column(name = "implement_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date implementDatetime;// 実施日時

    @Column(name = "takt_time")
    private Integer taktTime;// タクトタイム

    @Column(name = "work_time")
    private Integer workingTime;// 作業時間[ms]

    @Column(name = "model_name")
    private String modelName;// モデル名

    @Column(name = "comp_num")
    private Integer compNum;// 完成数

    @Column(name = "defect_reason")
    private String defectReason;// 不良理由

    @Column(name = "defect_num")
    private Integer defectNum;// 不良数

    @Column(name = "actual_add_info", length = 30000)
    @Convert(converter = PgJsonbConverter.class)
    private String actualAddInfo;// 追加情報(JSON)
    
    @Column(name = "production_number")
    private String productionNumber;// 製造番号
    
    @Column(name = "serial_no")
    private String serialNo; // シリア番号

    @Column(name = "non_work_time")
    private Integer nonWorkTime;// 中断時間[ms]
    /**
     * コンストラクタ
     */
    public ReportOutEntity() {
    }

    /**
     * 実績IDを取得する。
     *
     * @return 実績ID
     */
    public Long getActualId() {
        return this.actualId;
    }

    /**
     * 実績IDを設定する。
     *
     * @param actualId 実績ID
     */
    public void setActualId(Long actualId) {
        this.actualId = actualId;
    }

    /**
     * カンバン階層名を取得する。
     *
     * @return カンバン階層名
     */
    public String getKanbanParentName() {
        return this.kanbanParentName;
    }

    /**
     * カンバン階層名を設定する。
     *
     * @param kanbanParentName カンバン階層名
     */
    public void setKanbanParentName(String kanbanParentName) {
        this.kanbanParentName = kanbanParentName;
    }

    /**
     * カンバンIDを取得する。
     *
     * @return カンバンID
     */
    public Long getKanbanId() {
        return this.kanbanId;
    }

    /**
     * カンバンIDを設定する。
     *
     * @param kanbanId カンバンID
     */
    public void setKanbanId(Long kanbanId) {
        this.kanbanId = kanbanId;
    }

    /**
     * カンバン名を取得する。
     *
     * @return カンバン名
     */
    public String getKanbanName() {
        return this.kanbanName;
    }

    /**
     * カンバン名を設定する。
     *
     * @param kanbanName カンバン名
     */
    public void setKanbanName(String kanbanName) {
        this.kanbanName = kanbanName;
    }

    /**
     * サブカンバン名を取得する。
     *
     * @return サブカンバン名
     */
    public String getKanbanSubname() {
        return this.kanbanSubname;
    }

    /**
     * サブカンバン名を設定する。
     *
     * @param kanbanSubname サブカンバン名
     */
    public void setKanbanSubname(String kanbanSubname) {
        this.kanbanSubname = kanbanSubname;
    }

    /**
     * 工程順階層名を取得する。
     *
     * @return 工程順階層名
     */
    public String getWorkflowParentName() {
        return this.workflowParentName;
    }

    /**
     * 工程順階層名を設定する。
     *
     * @param workflowParentName 工程順階層名
     */
    public void setWorkflowParentName(String workflowParentName) {
        this.workflowParentName = workflowParentName;
    }

    /**
     * 工程順IDを取得する。
     *
     * @return 工程順ID
     */
    public Long getWorkflowId() {
        return this.workflowId;
    }

    /**
     * 工程順IDを設定する。
     *
     * @param workflowId 工程順ID
     */
    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    /**
     * 工程順名を取得する。
     *
     * @return 工程順名
     */
    public String getWorkflowName() {
        return this.workflowName;
    }

    /**
     * 工程順名を設定する。
     *
     * @param workflowName 工程順名
     */
    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    /**
     * 工程順の版数を取得する。
     *
     * @return 工程順の版数
     */
    public Integer getWorkflowRev() {
        return this.workflowRev;
    }

    /**
     * 工程順の版数を設定する。
     *
     * @param workflowRev 工程順の版数
     */
    public void setWorkflowRev(Integer workflowRev) {
        this.workflowRev = workflowRev;
    }

    /**
     * 工程階層名を取得する。
     *
     * @return 工程階層名
     */
    public String getWorkParentName() {
        return this.workParentName;
    }

    /**
     * 工程階層名を設定する。
     *
     * @param workParentName 工程階層名
     */
    public void setWorkParentName(String workParentName) {
        this.workParentName = workParentName;
    }

    /**
     * 工程IDを取得する。
     *
     * @return 工程ID
     */
    public Long getWorkId() {
        return this.workId;
    }

    /**
     * 工程IDを設定する。
     *
     * @param workId 工程ID
     */
    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    /**
     * 工程名を取得する。
     *
     * @return 工程名
     */
    public String getWorkName() {
        return this.workName;
    }

    /**
     * 工程名を設定する。
     *
     * @param workName 工程名
     */
    public void setWorkName(String workName) {
        this.workName = workName;
    }

    /**
     * 工程カンバンIDを取得する。
     *
     * @return 工程カンバンID
     */
    public Long getWorkKanbanId() {
        return this.workKanbanId;
    }

    /**
     * 工程カンバンIDを設定する。
     *
     * @param workKanbanId 工程カンバンID
     */
    public void setWorkKanbanId(Long workKanbanId) {
        this.workKanbanId = workKanbanId;
    }

    /**
     * 追加工程フラグを取得する。
     *
     * @return 追加工程フラグ
     */
    public Boolean getIsSeparateWork() {
        return this.isSeparateWork;
    }

    /**
     * 追加工程フラグを設定する。
     *
     * @param isSeparateWork 追加工程フラグ
     */
    public void setIsSeparateWork(Boolean isSeparateWork) {
        this.isSeparateWork = isSeparateWork;
    }

    /**
     * スキップフラグを取得する。
     *
     * @return スキップフラグ
     */
    public Boolean getIsSkip() {
        return this.isSkip;
    }

    /**
     * スキップフラグを設定する。
     *
     * @param isSkip スキップフラグ
     */
    public void setIsSkip(Boolean isSkip) {
        this.isSkip = isSkip;
    }

    /**
     * 親組織の組織名を取得する。
     *
     * @return 親組織の組織名
     */
    public String getOrganizationParentName() {
        return this.organizationParentName;
    }

    /**
     * 親組織の組織名を設定する。
     *
     * @param organizationParentName 親組織の組織名
     */
    public void setOrganizationParentName(String organizationParentName) {
        this.organizationParentName = organizationParentName;
    }

    /**
     * 親組織の組織識別名を取得する。
     *
     * @return 親組織の組織識別名
     */
    public String getOrganizationParentIdentName() {
        return this.organizationParentIdentName;
    }

    /**
     * 親組織の組織識別名を設定する。
     *
     * @param organizationParentIdentName 親組織の組織識別名
     */
    public void setOrganizationParentIdentName(String organizationParentIdentName) {
        this.organizationParentIdentName = organizationParentIdentName;
    }

    /**
     * 組織IDを取得する。
     *
     * @return 組織ID
     */
    public Long getOrganizationId() {
        return this.organizationId;
    }

    /**
     * 組織IDを設定する。
     *
     * @param organizationId 組織ID
     */
    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * 組織名を取得する。
     *
     * @return 組織名
     */
    public String getOrganizationName() {
        return this.organizationName;
    }

    /**
     * 組織名を設定する。
     *
     * @param organizationName 組織名
     */
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    /**
     * 組織識別名を取得する。
     *
     * @return 組織識別名
     */
    public String getOrganizationIdentName() {
        return this.organizationIdentName;
    }

    /**
     * 組織識別名を設定する。
     *
     * @param organizationIdentName 組織識別名
     */
    public void setOrganizationIdentName(String organizationIdentName) {
        this.organizationIdentName = organizationIdentName;
    }

    /**
     * 親設備の設備名を取得する。
     *
     * @return 親設備の設備名
     */
    public String getEquipmentParentName() {
        return this.equipmentParentName;
    }

    /**
     * 親設備の設備名を設定する。
     *
     * @param equipmentParentName 親設備の設備名
     */
    public void setEquipmentParentName(String equipmentParentName) {
        this.equipmentParentName = equipmentParentName;
    }

    /**
     * 親設備の設備識別名を取得する。
     *
     * @return 親設備の設備識別名
     */
    public String getEquipmentParentIdentName() {
        return this.equipmentParentIdentName;
    }

    /**
     * 親設備の設備識別名を設定する。
     *
     * @param equipmentParentIdentName 親設備の設備識別名
     */
    public void setEquipmentParentIdentName(String equipmentParentIdentName) {
        this.equipmentParentIdentName = equipmentParentIdentName;
    }

    /**
     * 設備IDを取得する。
     *
     * @return 設備ID
     */
    public Long getEquipmentId() {
        return this.equipmentId;
    }

    /**
     * 設備IDを設定する。
     *
     * @param equipmentId 設備ID
     */
    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    /**
     * 設備名を取得する。
     *
     * @return 設備名
     */
    public String getEquipmentName() {
        return this.equipmentName;
    }

    /**
     * 設備名を設定する。
     *
     * @param equipmentName 設備名
     */
    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    /**
     * 設備識別名を取得する。
     *
     * @return 設備識別名
     */
    public String getEquipmentIdentName() {
        return this.equipmentIdentName;
    }

    /**
     * 設備識別名を設定する。
     *
     * @param equipmentIdentName 設備識別名
     */
    public void setEquipmentIdentName(String equipmentIdentName) {
        this.equipmentIdentName = equipmentIdentName;
    }

    /**
     * 工程実績ステータスを取得する。
     *
     * @return 工程実績ステータス
     */
    public KanbanStatusEnum getActualStatus() {
        return this.actualStatus;
    }

    /**
     * 工程実績ステータスを設定する。
     *
     * @param actualStatus 工程実績ステータス
     */
    public void setActualStatus(KanbanStatusEnum actualStatus) {
        this.actualStatus = actualStatus;
    }

    /**
     * 中断理由を取得する。
     *
     * @return 中断理由
     */
    public String getInterruptReason() {
        return this.interruptReason;
    }

    /**
     * 中断理由を設定する。
     *
     * @param interruptReason 中断理由
     */
    public void setInterruptReason(String interruptReason) {
        this.interruptReason = interruptReason;
    }

    /**
     * 遅延理由を取得する。
     *
     * @return 遅延理由
     */
    public String getDelayReason() {
        return this.delayReason;
    }

    /**
     * 遅延理由を設定する。
     *
     * @param delayReason 遅延理由
     */
    public void setDelayReason(String delayReason) {
        this.delayReason = delayReason;
    }

    /**
     * 実施日時を取得する。
     *
     * @return 実施日時
     */
    public Date getImplementDatetime() {
        return this.implementDatetime;
    }

    /**
     * 実施日時を設定する。
     *
     * @param implementDatetime 実施日時
     */
    public void setImplementDatetime(Date implementDatetime) {
        this.implementDatetime = implementDatetime;
    }

    /**
     * タクトタイムを取得する。
     *
     * @return タクトタイム
     */
    public Integer getTaktTime() {
        return this.taktTime;
    }

    /**
     * タクトタイムを設定する。
     *
     * @param taktTime タクトタイム
     */
    public void setTaktTime(Integer taktTime) {
        this.taktTime = taktTime;
    }

    /**
     * 作業時間[ms]を取得する。
     *
     * @return 作業時間[ms]
     */
    public Integer getWorkingTime() {
        return this.workingTime;
    }

    /**
     * 作業時間[ms]を設定する。
     *
     * @param workingTime 作業時間[ms]
     */
    public void setWorkingTime(Integer workingTime) {
        this.workingTime = workingTime;
    }

    /**
     * モデル名を取得する。
     *
     * @return モデル名
     */
    public String getModelName() {
        return this.modelName;
    }

    /**
     * モデル名を設定する。
     *
     * @param modelName モデル名
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * 完成数を取得する。
     *
     * @return 完成数
     */
    public Integer getCompNum() {
        return this.compNum;
    }

    /**
     * 完成数を設定する。
     *
     * @param compNum 完成数
     */
    public void setCompNum(Integer compNum) {
        this.compNum = compNum;
    }

    /**
     * 不良理由を取得する。
     *
     * @return 不良理由
     */
    public String getDefectReason() {
        return this.defectReason;
    }

    /**
     * 不良理由を設定する。
     *
     * @param defectReason 不良理由
     */
    public void setDefectReason(String defectReason) {
        this.defectReason = defectReason;
    }

    /**
     * 不良数を取得する。
     *
     * @return 不良数
     */
    public Integer getDefectNum() {
        return this.defectNum;
    }

    /**
     * 不良数を設定する。
     *
     * @param defectNum 不良数
     */
    public void setDefectNum(Integer defectNum) {
        this.defectNum = defectNum;
    }

    /**
     * 追加情報(JSON)を取得する。
     *
     * @return 追加情報(JSON)
     */
    public String getActualAddInfo() {
        return this.actualAddInfo;
    }

    /**
     * 追加情報(JSON)を設定する。
     *
     * @param actualAddInfo 追加情報(JSON)
     */
    public void setActualAddInfo(String actualAddInfo) {
        this.actualAddInfo = actualAddInfo;
    }
    
    
    /**
     * 製造番号を取得する
     * 
     * @return  製造番号　
     */
    public String getProductionNumber() {
        return productionNumber;
    }

    /**
     * 製造番号を取得する
     * 
     * @param productionNumber  製造番号　
     */
    public void setProductionNumber(String productionNumber) {
        this.productionNumber = productionNumber;
    }

    /**
     * シリア番号を取得する。
     * 
     * @return シリアル番号 
     */
    public String getSerialNo() {
        return serialNo;
    }

    /**
     * シリアル番号を設定する。
     * 
     * @param serialNo シリア番号
     */
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    /**
     * 中断時間を取得する。
     * 
     * @return 中断時間 
     */
    public Integer getNonWorkTime() {
        return nonWorkTime;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.actualId);
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
        final ReportOutEntity other = (ReportOutEntity) obj;
        if (!Objects.equals(this.actualId, other.actualId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ReportOutEntity{")
                .append("actualId=").append(this.actualId)
                .append(", kanbanParentName=").append(this.kanbanParentName)
                .append(", kanbanId=").append(this.kanbanId)
                .append(", kanbanName=").append(this.kanbanName)
                .append(", kanbanSubname=").append(this.kanbanSubname)
                .append(", workflowParentName=").append(this.workflowParentName)
                .append(", workflowId=").append(this.workflowId)
                .append(", workflowName=").append(this.workflowName)
                .append(", workflowRev=").append(this.workflowRev)
                .append(", workParentName=").append(this.workParentName)
                .append(", workId=").append(this.workId)
                .append(", workName=").append(this.workName)
                .append(", workKanbanId=").append(this.workKanbanId)
                .append(", isSeparateWork=").append(this.isSeparateWork)
                .append(", isSkip=").append(this.isSkip)
                .append(", organizationParentName=").append(this.organizationParentName)
                .append(", organizationParentIdentName=").append(this.organizationParentIdentName)
                .append(", organizationId=").append(this.organizationId)
                .append(", organizationName=").append(this.organizationName)
                .append(", organizationIdentName=").append(this.organizationIdentName)
                .append(", equipmentParentName=").append(this.equipmentParentName)
                .append(", equipmentParentIdentName=").append(this.equipmentParentIdentName)
                .append(", equipmentId=").append(this.equipmentId)
                .append(", equipmentName=").append(this.equipmentName)
                .append(", equipmentIdentName=").append(this.equipmentIdentName)
                .append(", actualStatus=").append(this.actualStatus)
                .append(", interruptReason=").append(this.interruptReason)
                .append(", delayReason=").append(this.delayReason)
                .append(", implementDatetime=").append(this.implementDatetime)
                .append(", taktTime=").append(this.taktTime)
                .append(", workingTime=").append(this.workingTime)
                .append(", modelName=").append(this.modelName)
                .append(", compNum=").append(this.compNum)
                .append(", defectReason=").append(this.defectReason)
                .append(", defectNum=").append(this.defectNum)
                .append("}")
                .toString();
    }
}
