/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.ledger;

import adtekfuji.utility.StringUtils;
import jp.adtekfuji.adFactory.enumerate.LedgerTypeEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.*;
/**
 * カンバン情報
 *
 * @author ke.yokoi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ledger")
public class LedgerInfoEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    protected Long ledgerId;                        // 帳票ID

    @XmlElement()
    protected Long parentHierarchyId;               // 親階層ID

    @XmlElement()
    private String ledgerName = "";                      // 帳票名

    @XmlElement()
    private String ledgerFileName = "";                      // 帳票ファイル名

    @XmlElement()
    private String ledgerPhysicalFileName = "";             // 物理名

    @XmlElement()
    private String ledgerTarget;                    // 帳票出力対象

    @XmlElement()
    private String ledgerCondition;                // 出力条件

    @XmlElement()
    private Date updateDatetime; // 更新日

    @XmlElement()
    private Long updatePersonId;// 更新者(組織ID)

    @XmlElement()
    private Date lastImplementDatetime; // 最終実施日

    @XmlElement()
    private Integer verInfo = 1;// 排他用バーション


    /**
     * コンストラクタ
     */
    public LedgerInfoEntity() {
    }

    public LedgerInfoEntity(Long parentHierarchyId, Long updatePersonId) {
        this.parentHierarchyId = parentHierarchyId;
        this.updatePersonId = updatePersonId;
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

    public List<LedgerTargetEntity> getLedgerTarget() {
        if (StringUtils.isEmpty(this.ledgerTarget)) {
            return new ArrayList<>();
        }
        return JsonUtils.jsonToObjects(this.ledgerTarget, LedgerTargetEntity[].class);
    }

    public void setLedgerTarget(List<LedgerTargetEntity> ledgerTarget) {
        if (Objects.isNull(ledgerTarget) || ledgerTarget.isEmpty()) {
            this.ledgerTarget = null;
            return;
        }
        this.ledgerTarget = JsonUtils.objectsToJson(ledgerTarget);
    }

    public LedgerConditionEntity getLedgerCondition() {
        if (StringUtils.isEmpty(this.ledgerCondition)) {
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

    public Long getUpdatePersonId() {
        return updatePersonId;
    }

    public void setUpdatePersonId(Long updatePersonId) {
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
        final LedgerInfoEntity other = (LedgerInfoEntity) obj;
        return Objects.equals(this.ledgerId, other.ledgerId);
    }

    @Override
    public Object clone() {
        LedgerInfoEntity copy = new LedgerInfoEntity();
        copy.ledgerId = this.ledgerId;
        copy.parentHierarchyId = this.parentHierarchyId;
        copy.ledgerName = this.ledgerName;
        copy.ledgerFileName = this.ledgerFileName;
        copy.ledgerPhysicalFileName = this.ledgerPhysicalFileName;
        copy.ledgerTarget = this.ledgerTarget;
        copy.ledgerCondition = this.ledgerCondition;
        copy.updateDatetime = this.updateDatetime;
        copy.updatePersonId = this.updatePersonId;
        copy.lastImplementDatetime = this.lastImplementDatetime;
        copy.verInfo = this.verInfo;
        return copy;
    }

    public void apply(LedgerInfoEntity ledgerInfoEntity) {
        this.ledgerId = ledgerInfoEntity.ledgerId;
        this.parentHierarchyId = ledgerInfoEntity.parentHierarchyId;
        this.ledgerName = ledgerInfoEntity.ledgerName;
        this.ledgerFileName = ledgerInfoEntity.ledgerFileName;
        this.ledgerPhysicalFileName = ledgerInfoEntity.ledgerPhysicalFileName;
        this.ledgerTarget = ledgerInfoEntity.ledgerTarget;
        this.ledgerCondition = ledgerInfoEntity.ledgerCondition;
        this.updateDatetime = ledgerInfoEntity.updateDatetime;
        this.updatePersonId = ledgerInfoEntity.updatePersonId;
        this.lastImplementDatetime = ledgerInfoEntity.lastImplementDatetime;
        this.verInfo = ledgerInfoEntity.verInfo;
    }

    /**
     * 文字列表現を返す。
     *
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return "LedgerInfoEntity{" +
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
