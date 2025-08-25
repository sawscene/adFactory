/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.kanban;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.entity.kanban.ProductInfoEntity;

/**
 * 製品情報
 *
 * @author s-heya
 */
@Entity
@Table(name = "trn_product")
@XmlRootElement(name = "product")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // ユニークID一覧を指定して、製品情報一覧を取得する。(製品IDの逆順)
    @NamedQuery(name = "ProductEntity.findByUID", query = "SELECT o FROM ProductEntity o WHERE o.uniqueId IN :uniqueIds ORDER BY o.productId DESC"),
    @NamedQuery(name = "ProductEntity.countByKanbanId", query = "SELECT COUNT(o.productId) FROM ProductEntity o WHERE o.fkKanbanId = :fkKanbanId"),
    @NamedQuery(name = "ProductEntity.findByKanbanId", query = "SELECT o FROM ProductEntity o WHERE o.fkKanbanId = :fkKanbanId"),
    @NamedQuery(name = "ProductEntity.removeByKanbanId", query = "DELETE FROM ProductEntity o WHERE o.fkKanbanId = :fkKanbanId"),
    @NamedQuery(name = "ProductEntity.removeByProductIds", query = "DELETE FROM ProductEntity o WHERE o.productId IN :productIds"),
})
public class ProductEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "product_id")
    protected Long productId;// 製品ID

    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 32)
    @Column(name = "unique_id")
    private String uniqueId;// ユニークID

    @Column(name = "fk_kanban_id")
    protected Long fkKanbanId;// カンバンID

    @Column(name = "comp_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date compDatetime;// 完成日時

    @Column(name = "status")
    private String status;// 製品ステータス

    @Column(name = "defect_type")
    private String defectType;// 不良種別

    @Column(name = "order_num")
    private Integer orderNum;// 副番

    @Column(name = "defect_work_name")
    private String defectWorkName;// 発見工程名

    /**
     * コンストラクタ
     */
    public ProductEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param uniqueId ユニークID
     * @param kanbanId カンバンID
     * @param orderNum 副番
     */
    public ProductEntity(String uniqueId, long kanbanId, int orderNum) {
        this.uniqueId = uniqueId;
        this.fkKanbanId = kanbanId;
        this.orderNum = orderNum;
    }

    /**
     * コントラクタ
     *
     * @param obj 製品情報
     */
    public ProductEntity(ProductEntity obj) {
        this.productId = obj.getProductId();
        this.uniqueId = obj.getUniqueId();
        this.fkKanbanId = obj.getFkKanbanId();
        this.compDatetime = obj.getCompDatetime();
        this.status = obj.getStatus();
        this.defectType = obj.getDefectType();
    }

    /**
     * コントラクタ
     *
     * @param obj 製品情報 (CommonEntity)
     */
    public ProductEntity(ProductInfoEntity obj) {
        this.productId = obj.getProductId();
        this.uniqueId = obj.getUniqueId();
        this.fkKanbanId = obj.getFkKanbanId();
        this.compDatetime = obj.getCompDatetime();
        this.status = obj.getStatus();
        this.defectType = obj.getDefectType();
    }

    /**
     * 製品IDを取得する。
     *
     * @return 製品ID
     */
    public Long getProductId() {
        return productId;
    }

    /**
     * 製品IDを設定する。
     *
     * @param productId 製品ID
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * ユニークIDを取得する。
     *
     * @return ユニークID
     */
    public String getUniqueId() {
        return uniqueId;
    }

    /**
     * ユニークIDを設定する。
     *
     * @param uniqueId ユニークID
     */
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     * カンバンIDを取得する。
     *
     * @return カンバンID
     */
    public Long getFkKanbanId() {
        return fkKanbanId;
    }

    /**
     * カンバンIDを設定する。
     *
     * @param fkKanbanId カンバンID
     */
    public void setFkKanbanId(Long fkKanbanId) {
        this.fkKanbanId = fkKanbanId;
    }

    /**
     * 完成日時を取得する。
     *
     * @return 完成日時
     */
    public Date getCompDatetime() {
        return this.compDatetime;
    }

    /**
     * 完成日時を設定する。
     *
     * @param compDatetime 完成日時
     */
    public void setCompDatetime(Date compDatetime) {
        this.compDatetime = compDatetime;
    }

    /**
     * ステータスを取得する。
     *
     * @return ステータス
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * ステータスを設定する。
     *
     * @param status ステータス
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 不良種別を取得する。
     *
     * @return 不良種別
     */
    public String getDefectType() {
        return this.defectType;
    }

    /**
     * 不良種別を設定する。
     *
     * @param defectType 不良種別
     */
    public void setDefectType(String defectType) {
        this.defectType = defectType;
    }

    /**
     * 副番を取得する。
     *
     * @return 副番
     */
    public Integer getOrderNum() {
        return orderNum;
    }

    /**
     * 副番を設定する。
     *
     * @param orderNum 副番
     */
    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    /**
     * 発見工程名を取得する。
     *
     * @return 発見工程名
     */
    public String getDefectWorkName() {
        return defectWorkName;
    }

    /**
     * 発見工程名を設定する。
     *
     * @param defectWorkName 発見工程名
     */
    public void setDefectWorkName(String defectWorkName) {
        this.defectWorkName = defectWorkName;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.productId);
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
        final ProductEntity other = (ProductEntity) obj;
        return Objects.equals(this.productId, other.productId);
    }

    @Override
    public String toString() {
        return new StringBuilder("ProductInfoEntity{productId=").append(this.productId)
                .append(", uniqueId=").append(this.uniqueId)
                .append(", fkKanbanId=").append(this.fkKanbanId)
                .append(", compDatetime=").append(this.compDatetime)
                .append(", status=").append(this.status)
                .append(", defectType=").append(this.defectType)
                .append("}")
                .toString();
    }
}
