/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.ledger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.*;

import adtekfuji.utility.StringUtils;
import jp.adtekfuji.adFactory.entity.ledger.LedgerConditionEntity;
import jp.adtekfuji.adfactoryserver.utility.JsonUtils;

/**
 * カンバン情報
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "mst_ledger")
@XmlRootElement(name = "ledger")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedNativeQueries({
        // 対象の工程カンバンを取得
        @NamedNativeQuery(name="LedgerEntity.findWorkKanbanIds", query = "WITH target AS (SELECT X.* FROM mst_ledger ml, jsonb_to_recordset(ml.ledger_target) AS X(hierarchy_id BIGINT, workflow_id BIGINT, work_id BIGINT) WHERE ml.ledger_id = ?1), kanban_id AS (WITH RECURSIVE kanban_hierarchy AS (SELECT mkh.kanban_hierarchy_id id FROM mst_kanban_hierarchy mkh WHERE mkh.hierarchy_name = ?2 UNION DISTINCT SELECT mkh2.child_id AS kanban_hierarch_id FROM tre_kanban_hierarchy mkh2, kanban_hierarchy WHERE mkh2.parent_id = kanban_hierarchy.id) SELECT ckh.kanban_id FROM kanban_hierarchy JOIN con_kanban_hierarchy ckh ON ckh.kanban_hierarchy_id = kanban_hierarchy.id), workflow_id AS (WITH RECURSIVE workflow_hierarchy AS (SELECT target.hierarchy_id FROM target WHERE target.hierarchy_id IS NOT NULL UNION DISTINCT SELECT mh2.hierarchy_id FROM mst_hierarchy mh2, workflow_hierarchy WHERE workflow_hierarchy.hierarchy_id = mh2.parent_hierarchy_id) SELECT work_workflow_id FROM workflow_hierarchy JOIN con_hierarchy ch ON ch.hierarchy_id = workflow_hierarchy.hierarchy_id UNION DISTINCT SELECT target.workflow_id FROM target WHERE target.work_id IS NOT NULL) SELECT twk.work_kanban_id FROM trn_work_kanban twk JOIN workflow_id ON twk.workflow_id = workflow_id.work_workflow_id JOIN kanban_id ON twk.kanban_id = kanban_id.kanban_id UNION DISTINCT SELECT twk2.work_kanban_id FROM trn_work_kanban twk2 JOIN kanban_id ON twk2.kanban_id = kanban_id.kanban_id JOIN target ON target.work_id = twk2.work_id AND target.workflow_id = twk2.workflow_id"),
})
@NamedQueries({
        @NamedQuery(name = "LedgerEntity.findByParentIds", query = "SELECT l FROM LedgerEntity l WHERE l.parentHierarchyId IN :parentIds"),
})
public class LedgerEntity implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ledger_id")
    private Long ledgerId;                        // 帳票ID

    @Column(name = "parent_hierarchy_id")
    private Long parentHierarchyId;                        // 親階層ID

    @Size(min = 1, max = 256)
    @Column(name = "ledger_name")
    private String ledgerName;                      // 帳票名

    @Column(name = "ledger_file_name")
    private String ledgerFileName;                      // 帳票ファイル名


    @Column(name = "ledger_physical_file_name")
    private String ledgerPhysicalFileName;                    // 物理ファイル名

    @Column(name = "ledger_target")
    private String ledgerTarget;                    // 帳票出力対象

    @Column(name = "ledger_condition")
    private String ledgerCondition;                 // 帳票出力条件

    @Column(name = "update_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDatetime;                    // 更新日

    @Column(name = "update_person_id")
    private long updatePersonId;// 更新者(組織ID)

    @Column(name = "last_implement_datetime")
    private Date lastImplementDatetime; // 最終実施日

    @Column(name = "ver_info")
    @Version
    private Integer verInfo = 1;// 排他用バーション


    /**
     * コンストラクタ
     */
    public LedgerEntity() {
    }

    public Long getLedgerId() {
        return ledgerId;
    }

    public void setLedgerId(Long ledgerId) {
        this.ledgerId = ledgerId;
    }

    public Long getParentHierarchyId() {
        return parentHierarchyId;
    }

    public void setParentHierarchyId(Long parentHierarchyId) {
        this.parentHierarchyId = parentHierarchyId;
    }

    public String getLedgerName() {
        return ledgerName;
    }

    public void setLedgerName(String ledgerName) {
        this.ledgerName = ledgerName;
    }

    public String getLedgerFileName() {
        return ledgerFileName;
    }

    public void setLedgerFileName(String ledgerFileName) {
        this.ledgerFileName = ledgerFileName;
    }

    public String getLedgerPhysicalFileName() {
        return ledgerPhysicalFileName;
    }

    public void setLedgerPhysicalFileName(String ledgerPhysicalFileName) {
        this.ledgerPhysicalFileName = ledgerPhysicalFileName;
    }

    public LedgerConditionEntity getLedgerCondition() {
        if (StringUtils.isEmpty(ledgerCondition)) {
            return new LedgerConditionEntity();
        }
        return JsonUtils.jsonToObject(ledgerCondition, LedgerConditionEntity.class);
    }

    public void setLedgerCondition(LedgerConditionEntity ledgerCondition) {
        this.ledgerCondition = JsonUtils.objectToJson(ledgerCondition);
    }

    public Date getUpdateDatetime() {
        return updateDatetime;
    }

    public void setUpdateDatetime(Date updateDatetime) {
        this.updateDatetime = updateDatetime;
    }

    public long getUpdatePersonId() {
        return updatePersonId;
    }

    public void setUpdatePersonId(long updatePersonId) {
        this.updatePersonId = updatePersonId;
    }

    public Date getLastImplementDatetime() {
        return lastImplementDatetime;
    }

    public void setLastImplementDatetime(Date lastImplementDatetime) {
        this.lastImplementDatetime = lastImplementDatetime;
    }

    public Integer getVerInfo() {
        return verInfo;
    }

    public void setVerInfo(Integer verInfo) {
        this.verInfo = verInfo;
    }

    /**
     * ハッシュコードを取得する。
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.ledgerId);
        return hash;
    }

    /**
     * オブジェクトを比較する。
     * 
     * @param obj オブジェクト
     * @return true: 等しい(同値)、false: 異なる
     */
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
        final LedgerEntity other = (LedgerEntity) obj;
        return Objects.equals(this.ledgerId, other.ledgerId);
    }

    /**
     * 文字列表現を返す。
     *
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return "LedgerEntity{" +
                "ledgerId=" + ledgerId +
                ", parentHierarchyId=" + parentHierarchyId +
                ", ledgerName='" + ledgerName + '\'' +
                ", ledgerFileName='" + ledgerFileName + '\'' +
                ", ledgerPhysicalFileName='" + ledgerPhysicalFileName + '\'' +
                ", ledgerTarget='" + ledgerTarget + '\'' +
                ", ledgerCondition='" + ledgerCondition + '\'' +
                ", updateDatetime=" + updateDatetime +
                ", updatePersonId=" + updatePersonId +
                ", lastImplementDatetime=" + lastImplementDatetime +
                ", verInfo=" + verInfo +
                '}';
    }
}
