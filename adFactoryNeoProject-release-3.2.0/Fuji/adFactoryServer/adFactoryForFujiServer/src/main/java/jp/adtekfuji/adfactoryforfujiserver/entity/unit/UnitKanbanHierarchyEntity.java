/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.entity.unit;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * カンバン階層情報
 *
 * @author s-heya
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {
    "kanban_hierarchy_id",
    "hierarchy_name"}))
public class UnitKanbanHierarchyEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "kanban_hierarchy_id")
    private Long kanbanHierarchyId;
    @Column(name = "hierarchy_name")
    private String hierarchyName;

    public Long getKanbanHierarchyId() {
        return kanbanHierarchyId;
    }

    public String getHierarchyName() {
        return hierarchyName;
    }

    @Override
    public String toString() {
        return "UnitKanbanHierarchyEntity{" + "kanbanHierarchyId=" + kanbanHierarchyId + ", hierarchyName=" + hierarchyName + '}';
    }
}
