/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.entity.unit;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
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
  "actual_id",
  "fk_kanban_id",
  "fk_work_kanban_id",
  "actual_status",
  "implement_datetime",
  "organization_name"}))
public class UnitActualResultEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "actual_id")
    private Long actualId;
    @Column(name = "fk_kanban_id")
    private Long fkKanbanId;
    @Column(name = "fk_work_kanban_id")
    private Long fkWorkKanbanId;
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    @Column(name = "actual_status")
    private KanbanStatusEnum actualStatus;
    @Column(name = "implement_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date implementDatetime;
    @Column(name = "organization_name")
    private String organizationName;
    //@Column(name = "fk_work_id")
    //private Long fkWorkId;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getActualId() {
        return actualId;
    }

    public Long getFkKanbanId() {
        return fkKanbanId;
    }

    public Long getFkWorkKanbanId() {
        return fkWorkKanbanId;
    }

    public KanbanStatusEnum getActualStatus() {
        return actualStatus;
    }

    public Date getImplementDatetime() {
        return implementDatetime;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    @Override
    public String toString() {
        return "UnitActualResultEntity{" + "actualId=" + actualId + ", fkKanbanId=" + fkKanbanId + ", fkWorkKanbanId=" + fkWorkKanbanId + ", actualStatus=" + actualStatus +
                ", implementDatetime=" + implementDatetime + ", organizationName=" + organizationName + '}';
    }

}
