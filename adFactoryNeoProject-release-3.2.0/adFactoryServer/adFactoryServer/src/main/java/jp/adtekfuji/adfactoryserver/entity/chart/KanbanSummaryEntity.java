/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.chart;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * カンバン集計データ
 *
 * @author s-heya
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {
  "kanban_id",
  "kanban_name",
  "work_times",
  "actual_start_time",
  "actual_end_time"}))
// マッピングを使う場合は、@NamedNativeQueryにresultSetMapping属性を指定する
//@SqlResultSetMappings({
//    @SqlResultSetMapping(name = "KanbanSummary",  entities = {
//        @EntityResult(entityClass = KanbanSummaryEntity.class, fields = {
//            @FieldResult(name = "kanbanName", column = "kanban_name"),
//            @FieldResult(name = "workTimes", column = "work_times")})
//    })
//})
@NamedNativeQueries({
    @NamedNativeQuery(name = "KanbanSummaryEntity.calcKanban",
            query = "SELECT k.kanban_id, k.kanban_name, SUM(wk.sum_times) work_times, k.actual_start_datetime actual_start_time, k.actual_comp_datetime actual_end_time FROM trn_kanban k " +
                    "LEFT JOIN trn_work_kanban wk ON wk.kanban_id = k.kanban_id " +
                    "WHERE k.workflow_id = ?3 AND (k.actual_start_datetime >= ?1 AND k.actual_comp_datetime <= ?2) " +
                    "GROUP BY k.kanban_id " +
                    "ORDER BY k.kanban_name",
            resultClass = KanbanSummaryEntity.class)
})
@NamedQueries({
    // 作業時間
    @NamedQuery(name = "KanbanSummaryEntity.countWorkTime", query = "SELECT SUM(wk.sumTimes) FROM KanbanEntity k LEFT JOIN WorkKanbanEntity wk ON wk.kanbanId = k.kanbanId WHERE k.workflowId = :workflowId AND (k.actualStartTime >= :fromDate AND k.actualCompTime <= :toDate)"),
    // ロット数の合計を取得する。
    @NamedQuery(name = "KanbanSummaryEntity.sumLotQuantity", query = "SELECT SUM(COALESCE(k.lotQuantity, 1)) FROM KanbanEntity k WHERE k.workflowId = :workflowId AND (k.actualStartTime >= :fromDate AND k.actualCompTime <= :toDate)"),
})
@XmlRootElement(name = "kanbanSummary")
@XmlAccessorType(XmlAccessType.FIELD)
public class KanbanSummaryEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "kanban_id")
    private Long kanbanId;// カンバンID

    @Column(name = "kanban_name")
    private String kanbanName;// カンバン名

    @Column(name = "work_times")
    private Long workTimes;// 作業時間[ms]

    @Column(name = "actual_start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualStartTime;// 開始日時(実績)

    @Column(name = "actual_end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualEndTime;// 完了日時(実績)

    /**
     * コンストラクタ
     */
    public KanbanSummaryEntity() {
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.kanbanId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final KanbanSummaryEntity other = (KanbanSummaryEntity) obj;
        if (!Objects.equals(this.kanbanId, other.kanbanId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("KanbanSummaryEntity{")
                .append("kanbanId=").append(this.kanbanId)
                .append(", kanbanName=").append(this.kanbanName)
                .append(", workTimes=").append(this.workTimes)
                .append(", actualStartTime=").append(this.actualStartTime)
                .append(", actualEndTime=").append(this.actualEndTime)
                .append("}")
                .toString();
    }
}
