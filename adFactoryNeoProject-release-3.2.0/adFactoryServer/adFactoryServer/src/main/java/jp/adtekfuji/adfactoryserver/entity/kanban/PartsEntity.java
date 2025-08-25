/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.kanban;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adfactoryserver.utility.PgJsonbConverter;

/**
 * 完成品情報
 *
 * @author nar-nakamura
 */
@Entity
@Table(name = "trn_parts")
@XmlRootElement(name = "parts")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // パーツIDを指定して、完成品情報を取得する。(削除済も対象として、未削除・製造日が新しいものを優先)
    @NamedQuery(name = "PartsEntity.findByPartsId", query = "SELECT p FROM PartsEntity p WHERE p.partsId = :partsId ORDER BY p.removeFlag, p.compDatetime DESC"),
    // パーツID一覧を指定して、完成品情報を削除する。(削除フラグON)
    @NamedQuery(name = "PartsEntity.removeParts", query = "UPDATE PartsEntity p SET p.destWorkKanbanId = :destWorkKanbanId, p.removeFlag = true WHERE p.partsId IN :partsIds AND p.removeFlag = false"),
    // 工程カンバンIDを指定して、完成品情報を削除する。
    @NamedQuery(name = "PartsEntity.deleteByWorkKanbanId", query = "DELETE FROM PartsEntity p WHERE p.workKanbanId = :workKanbanId"),
    // カンバンIDを指定して、完成品情報を削除する。
    @NamedQuery(name = "PartsEntity.deleteByKanbanId", query = "DELETE FROM PartsEntity p WHERE p.workKanbanId IN (SELECT w.workKanbanId FROM WorkKanbanEntity w WHERE w.kanbanId = :kanbanId)"),
    @NamedQuery(name = "PartsEntity.searchByPartsId", query = "SELECT p FROM PartsEntity p WHERE UPPER(p.partsId) LIKE UPPER(:keyword) AND p.removeFlag = false ORDER BY p.partsId, p.compDatetime"),
    @NamedQuery(name = "PartsEntity.removeForced", query = "UPDATE PartsEntity p SET p.destWorkKanbanId = 0L, p.removeFlag = true WHERE p.partsId IN :partsIds AND p.removeFlag = false"),
})
@JsonIgnoreProperties(ignoreUnknown=true)
public class PartsEntity implements Serializable {

    @Id
    @Basic(optional = false)
    @Column(name = "parts_id")
    private String partsId;// パーツID

    @Column(name = "serial_no_info", length = 30000)
    @Convert(converter = PgJsonbConverter.class)
    private String serialNoInfo;// シリアル番号情報(JSON)

    @Id
    @Basic(optional = false)
    @Column(name = "work_kanban_id")
    private Long workKanbanId;// 製造元工程カンバンID

    @Column(name = "comp_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date compDatetime;// 製造日

    @Column(name = "dest_work_kanban_id")
    private Long destWorkKanbanId;// 供給先工程カンバンID

    @Column(name = "remove_flag")
    private Boolean removeFlag = false;// 論理削除フラグ

    /**
     * コンストラクタ
     */
    public PartsEntity() {
    }

    /**
     * パーツIDを取得する。
     *
     * @return パーツID
     */
    public String getPartsId() {
        return this.partsId;
    }

    /**
     * パーツIDを設定する。
     *
     * @param partsId パーツID
     */
    public void setPartsId(String partsId) {
        this.partsId = partsId;
    }

    /**
     * シリアル番号情報(JSON)を取得する。
     *
     * @return シリアル番号情報(JSON)
     */
    public String getSerialNoInfo() {
        return this.serialNoInfo;
    }

    /**
     * シリアル番号情報(JSON)を設定する。
     *
     * @param serialNoInfo シリアル番号情報(JSON)
     */
    public void setSerialNoInfo(String serialNoInfo) {
        this.serialNoInfo = serialNoInfo;
    }

    /**
     * 製造元工程カンバンIDを取得する。
     *
     * @return 製造元工程カンバンID
     */
    public Long getWorkKanbanId() {
        return this.workKanbanId;
    }

    /**
     * 製造元工程カンバンIDを設定する。
     *
     * @param workKanbanId 製造元工程カンバンID
     */
    public void setWorkKanbanId(Long workKanbanId) {
        this.workKanbanId = workKanbanId;
    }

    /**
     * 製造日を取得する。
     *
     * @return 製造日
     */
    public Date getCompDatetime() {
        return this.compDatetime;
    }

    /**
     * 製造日を設定する。
     *
     * @param compDatetime 製造日
     */
    public void setCompDatetime(Date compDatetime) {
        this.compDatetime = compDatetime;
    }

    /**
     * 供給先工程カンバンIDを取得する。
     *
     * @return 供給先工程カンバンID
     */
    public Long getDestWorkKanbanId() {
        return this.destWorkKanbanId;
    }

    /**
     * 供給先工程カンバンIDを設定する。
     *
     * @param destWorkKanbanId 供給先工程カンバンID
     */
    public void setDestWorkKanbanId(Long destWorkKanbanId) {
        this.destWorkKanbanId = destWorkKanbanId;
    }

    /**
     * 論理削除フラグを取得する。
     *
     * @return 論理削除フラグ
     */
    public Boolean getRemoveFlag() {
        return this.removeFlag;
    }

    /**
     * 論理削除フラグを設定する。
     *
     * @param removeFlag 論理削除フラグ
     */
    public void setRemoveFlag(Boolean removeFlag) {
        this.removeFlag = removeFlag;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.partsId);
        hash = 53 * hash + Objects.hashCode(this.workKanbanId);
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
        final PartsEntity other = (PartsEntity) obj;
        if (!Objects.equals(this.partsId, other.partsId)) {
            return false;
        }
        if (!Objects.equals(this.workKanbanId, other.workKanbanId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("PartsEntity{")
                .append("partsId=").append(this.partsId)
                .append(", workKanbanId=").append(this.workKanbanId)
                .append(", compDatetime=").append(this.compDatetime)
                .append(", destWorkKanbanId=").append(this.destWorkKanbanId)
                .append(", removeFlag=").append(this.removeFlag)
                .append("}")
                .toString();
    }
}
