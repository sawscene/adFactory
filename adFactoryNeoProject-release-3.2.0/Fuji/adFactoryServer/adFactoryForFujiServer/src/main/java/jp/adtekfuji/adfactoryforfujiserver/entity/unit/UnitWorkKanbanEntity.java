/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.entity.unit;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

/**
 *
 * @author s-heya
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {
  "work_kanban_id",
  "fk_kanban_id",
  "fk_work_id",
  "work_name",
  "start_datetime",
  "comp_datetime",
  "takt_time",
  "sum_times",
  "work_status",
  "actual_start_datetime",
  "actual_comp_datetime"}))
public class UnitWorkKanbanEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "work_kanban_id")
    private Long workKanbanId;
    @Column(name = "fk_kanban_id")
    private Long fkKanbanId;
    @Column(name = "fk_work_id")
    private Long fkWorkId;
    @Column(name = "work_name")
    private String workName;
    @Column(name = "start_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDatetime;
    @Column(name = "comp_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date compDatetime;
    @Column(name = "takt_time")
    private Integer taktTime;
    @Column(name = "sum_times")
    private Integer sumTimes;
    @Column(name = "work_status")
    @Enumerated(EnumType.STRING)
    private KanbanStatusEnum workStatus;
    @Column(name = "actual_start_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualStartTime;
    @Column(name = "actual_comp_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualCompTime;

    public Long getWorkKanbanId() {
        return workKanbanId;
    }

    public Long getFkKanbanId() {
        return fkKanbanId;
    }

    public Long getFkWorkId() {
        return fkWorkId;
    }

    public String getWorkName() {
        return workName;
    }

    public Date getStartDatetime() {
        return startDatetime;
    }

    public Date getCompDatetime() {
        return compDatetime;
    }

    public Integer getTaktTime() {
        return taktTime;
    }

    public Integer getSumTimes() {
        return sumTimes;
    }

    public KanbanStatusEnum getWorkStatus() {
        return workStatus;
    }

    public Date getActualStartTime() {
        return actualStartTime;
    }

    public Date getActualCompTime() {
        return actualCompTime;
    }



    @Override
    public String toString() {
        return "UnitWorkKanbanEntity{" + "workKanbanId=" + workKanbanId + ", fkKanbanId=" + fkKanbanId + ", fkWorkId=" + fkWorkId + ", workName=" + workName +
                ", startDatetime=" + startDatetime + ", compDatetime=" + compDatetime + ", taktTime=" + taktTime + ", sumTimes=" + sumTimes + ", workStatus=" + workStatus +
                ", actualStartTime=" + actualStartTime + ", actualCompTime=" + actualCompTime + '}';
    }

}
