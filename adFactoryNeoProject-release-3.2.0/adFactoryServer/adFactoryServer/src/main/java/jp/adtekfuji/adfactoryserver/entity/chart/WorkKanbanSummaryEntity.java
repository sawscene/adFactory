package jp.adtekfuji.adfactoryserver.entity.chart;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 * 工程カンバン集計情報
 *
 * @author
 */
@Entity
@NamedNativeQueries({
        @NamedNativeQuery(name = "WorkKanbanSummaryEntity.calcWorkKanban",
        query = "SELECT wk.work_kanban_id, wk.kanban_id kanban_id, k.kanban_name kanban_name, wk.workflow_id workflow_id, mwf.workflow_name workflow_name, wk.work_id work_id, mw.work_name work_name, wk.sum_times work_times, wk.start_datetime start_datetime, wk.comp_datetime comp_datetime, k.lot_quantity lot_quantity, mw.work_rev " +
                "FROM (SELECT tk.kanban_id, tk.kanban_name, tk.lot_quantity FROM trn_kanban tk WHERE tk.kanban_status = 'COMPLETION' AND tk.actual_comp_datetime BETWEEN ?1 AND ?2 AND tk.workflow_id = ?3) k " +
                "JOIN trn_work_kanban wk ON wk.kanban_id = k.kanban_id " +
                "JOIN mst_work mw ON mw.work_id=wk.work_id " +
                "JOIN mst_workflow mwf ON mwf.workflow_id = wk.workflow_id " +
                "ORDER BY k.kanban_name",
        resultClass = WorkKanbanSummaryEntity.class)
})
@XmlRootElement(name = "workKanbanSummary")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkKanbanSummaryEntity implements Serializable {

    @Id
    @Column(name = "work_kanban_id")
    private Long workKanbanId;

    @Column(name = "kanban_id")
    private Long kanbanId; // カンバンID

    @Column(name = "kanban_name")
    private String kanbanName; // カンバン名

    @Column(name = "workflow_id")
    private String workflowId; // ワークフローID

    @Column(name = "workflow_name")
    private String workflowName; // 工程順名

    @Column(name = "work_id")
    private String workId; // 工程ID

    @Column(name = "work_name")
    private String workName; // 工程名

    @Column(name = "work_times")
    private Long workTimes;// 作業時間[ms]

    @Column(name = "actual_start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualStartTime;// 開始日時(実績)

    @Column(name = "actual_end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualEndTime;// 完了日時(実績)

    @Column(name = "lot_quantity")
    private Long LotQuantity;

    @Column(name = "work_rev")
    private Integer workRev; // 工程の版数

    /**
     * コンストラクタ
     */
    public WorkKanbanSummaryEntity() {
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
     * 工程順IDを取得する。
     *
     * @return 工程順ID
     */
    public String getWorkflowId() {
        return this.workflowId;
    }

    /**
     * 工程順IDを設定する。
     *
     * @param workflowId 工程順ID
     */
    public void setWorkflowId(String workflowId) {
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
     * 工程IDを取得する。
     *
     * @return 工程ID
     */
    public String getWorkId() {
        return this.workId;
    }

    /**
     * 工程IDを設定する。
     *
     * @param workId 工程ID
     */
    public void setWorkId(String workId) {
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
     * 作業時間[ms]を取得する。
     *
     * @return 作業時間[ms]
     */
    public Long getWorkTimes() {
        return this.workTimes;
    }

    /**
     * 作業時間[ms]を設定する。
     *
     * @param workTimes 作業時間[ms]
     */
    public void setWorkTimes(Long workTimes) {
        this.workTimes = workTimes;
    }

    /**
     * 開始日時(実績)を取得する。
     *
     * @return 開始日時(実績)
     */
    public Date getActualStartTime() {
        return this.actualStartTime;
    }

    /**
     * 開始日時(実績)を設定する。
     *
     * @param actualStartTime 開始日時(実績)
     */
    public void setActualStartTime(Date actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    /**
     * 完了日時(実績)を取得する。
     *
     * @return 完了日時(実績)
     */
    public Date getActualEndTime() {
        return this.actualEndTime;
    }

    /**
     * 完了日時(実績)を設定する。
     *
     * @param actualEndTime 完了日時(実績)
     */
    public void setActualEndTime(Date actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

    /**
     * ロット数量を取得する。
     *
     * @return ロット数量
     */
    public Long getLotQuantity() {
        return this.LotQuantity;
    }

    /**
     * ロット数量を設定する。
     *
     * @param lotQuantity ロット数量
     */
    public void setLotQuantity(Long lotQuantity) {
        this.LotQuantity = lotQuantity;
    }

    /**
     * 工程の版数を取得する。
     *
     * @return 工程の版数
     */
    public Integer getWorkRev() {
        return this.workRev;
    }

    /**
     * 工程の版数を設定する。
     *
     * @param workRev 工程の版数
     */
    public void setWorkRev(Integer workRev) {
        this.workRev = workRev;
    }

    @Override
    public String toString() {
        return "WorkKanbanSummaryEntity{" +
                "work_kanban_id=" + workKanbanId +
                ", kanbanId=" + kanbanId +
                ", kanbanName='" + kanbanName + '\'' +
                ", workflowId='" + workflowId + '\'' +
                ", workflowName='" + workflowName + '\'' +
                ", workId='" + workId + '\'' +
                ", workName='" + workName + '\'' +
                ", workTimes=" + workTimes +
                ", actualStartTime=" + actualStartTime +
                ", actualEndTime=" + actualEndTime +
                ", LotQuantity=" + LotQuantity +
                '}';
    }
}
