/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.kanban;

import java.io.Serializable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 工程カンバン・設備関連付け情報
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "con_workkanban_equipment")
@XmlRootElement(name = "conWorkkanbanEquipment")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // 工程カンバンIDを指定して、関連付けられた設備ID一覧を取得する。
    @NamedQuery(name = "ConWorkkanbanEquipmentEntity.findEquipmentIdByWorkKanbanId", query = "SELECT c.equipmentId FROM ConWorkkanbanEquipmentEntity c WHERE c.workKanbanId = :workKanbanId GROUP BY c.equipmentId"),
    // 工程カンバンIDを指定して、工程カンバン・設備関連付け情報を削除する。
    @NamedQuery(name = "ConWorkkanbanEquipmentEntity.removeByWorkKanbanId", query = "DELETE FROM ConWorkkanbanEquipmentEntity c WHERE c.workKanbanId = :workKanbanId"),
    // 設備IDを指定して、工程カンバン・設備関連付け情報の件数を取得する。
    @NamedQuery(name = "ConWorkkanbanEquipmentEntity.countByEquipmentId", query = "SELECT COUNT(c.workKanbanId) FROM ConWorkkanbanEquipmentEntity c WHERE c.equipmentId = :equipmentId"),
})
public class ConWorkkanbanEquipmentEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    //@NotNull
    @Column(name = "workkanban_id")
    private long workKanbanId;// 工程カンバンID

    @Id
    @Basic(optional = false)
    //@NotNull
    @Column(name = "equipment_id")
    private long equipmentId;// 設備ID

    /**
     * コンストラクタ
     */
    public ConWorkkanbanEquipmentEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param workKanbanId 工程カンバンID
     * @param equipmentId 設備ID
     */
    public ConWorkkanbanEquipmentEntity(long workKanbanId, long equipmentId) {
        this.workKanbanId = workKanbanId;
        this.equipmentId = equipmentId;
    }

    /**
     * 工程カンバンIDを取得する。
     *
     * @return 工程カンバンID
     */
    public long getWorkKanbanId() {
        return this.workKanbanId;
    }

    /**
     * 工程カンバンIDを設定する。
     *
     * @param workKanbanId 工程カンバンID
     */
    public void setWorkKanbanId(long workKanbanId) {
        this.workKanbanId = workKanbanId;
    }

    /**
     * 設備IDを取得する。
     *
     * @return 設備ID
     */
    public long getEquipmentId() {
        return this.equipmentId;
    }

    /**
     * 設備IDを設定する。
     *
     * @param equipmentId 設備ID
     */
    public void setEquipmentId(long equipmentId) {
        this.equipmentId = equipmentId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int) (this.workKanbanId ^ (this.workKanbanId >>> 32));
        hash = 83 * hash + (int) (this.equipmentId ^ (this.equipmentId >>> 32));
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
        final ConWorkkanbanEquipmentEntity other = (ConWorkkanbanEquipmentEntity) obj;
        if (this.workKanbanId != other.workKanbanId) {
            return false;
        }
        if (this.equipmentId != other.equipmentId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ConWorkkanbanEquipmentEntity{")
                .append("workKanbanId=").append(this.workKanbanId)
                .append(", ")
                .append("equipmentId=").append(this.equipmentId)
                .append("}")
                .toString();
    }
}
