/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.actual;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * ライン生産データ
 *
 * @author s-heya
 */
@Entity
@Table(name = "view_line_product")
@XmlRootElement
@NamedQueries({
    // ラインの生産数を取得
    @NamedQuery(name = "LineProductEntity.countByEquipmentId", query = "SELECT COUNT(l.kanbanId) FROM LineProductEntity l WHERE l.equipmentId IN :equipmentIds AND l.actualEndTime >= :fromDate AND l.actualEndTime <= :toDate"),
})
public class LineProductEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "kanban_id")
    private BigInteger kanbanId;
    @Column(name = "equipment_id")
    private BigInteger equipmentId;
    @Column(name = "actual_end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualEndTime;
    @Column(name = "model_name")
    private String modelName;
    @Column(name = "lot_quantity")
    private Integer lotQuantity;
    @Column(name = "defect_num")
    private Integer defectNum;

    /**
     * コンストラクタ
     */
    public LineProductEntity() {
    }

    /**
     * カンバンIDを取得する。
     *
     * @return カンバンID
     */
    public BigInteger getKanbanId() {
        return this.kanbanId;
    }

    /**
     * カンバンIDを設定する。
     *
     * @param kanbanId カンバンID
     */
    public void setKanbanId(BigInteger kanbanId) {
        this.kanbanId = kanbanId;
    }

    /**
     * 設備IDを取得する。
     *
     * @return 設備ID
     */
    public BigInteger getEquipmentId() {
        return this.equipmentId;
    }

    /**
     * 設備IDを設定する。
     *
     * @param equipmentId 設備ID
     */
    public void setEquipmentId(BigInteger equipmentId) {
        this.equipmentId = equipmentId;
    }

    /**
     * 実績完了日時を取得する。
     *
     * @return 実績完了日時
     */
    public Date getActualEndTime() {
        return this.actualEndTime;
    }

    /**
     * 実績完了日時を設定する。
     *
     * @param actualEndTime 実績完了日時
     */
    public void setActualEndTime(Date actualEndTime) {
        this.actualEndTime = actualEndTime;
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
     * ロット数量を取得する。
     *
     * @return ロット数量
     */
    public Integer getLotQuantity() {
        return this.lotQuantity;
    }

    /**
     * ロット数量を設定する。
     *
     * @param lotQuantity ロット数量
     */
    public void setLotQuantity(Integer lotQuantity) {
        this.lotQuantity = lotQuantity;
    }

    /**
     * 不良数を取得する。
     * 
     * @return 不良数 
     */
    public Integer getDefectNum() {
        return defectNum;
    }

    /**
     * 不良数を設定する。
     * 
     * @param defectNum 不良数 
     */
    public void setDefectNum(Integer defectNum) {
        this.defectNum = defectNum;
    }

    @Override
    public String toString() {
        return new StringBuilder("LineProductEntity{")
                .append("kanbanId=").append(this.kanbanId)
                .append(", equipmentId=").append(this.equipmentId)
                .append(", actualEndTime=").append(this.actualEndTime)
                .append(", modelName=").append(this.modelName)
                .append(", lotQuantity=").append(this.lotQuantity)
                .append(", defectNum=").append(this.defectNum)
                .append("}")
                .toString();
    }
}
