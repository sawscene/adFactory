/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.kanban;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 製品情報
 * 
 * @author s-heya
 */
@XmlRootElement(name = "product")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductInfoEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    protected Long productId;
    @XmlElement(required = true)
    private String uniqueId;
    @XmlElement()
    protected Long fkKanbanId;
    @XmlElement()
    private Date compDatetime;
    @XmlElement()
    private String status;
    @XmlElement()
    private String defectType;
    @XmlElement()
    private Integer orderNum;
    @XmlElement()
    private String oldStatus;
    @XmlElement()
    private String defectWorkName;

    /**
     * コンストラクタ
     */
    public ProductInfoEntity() {
    }
    
    /**
     * コンストラクタ
     * 
     * @param uniqueId
     */
    public ProductInfoEntity(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     * コンストラクタ
     * 
     * @param productId
     * @param uniqueId
     * @param fkKanbanId
     * @param updateDatetime
     * @param status
     * @param defectType
     * @param orderNum 
     */
    public ProductInfoEntity(Long productId, String uniqueId, Long fkKanbanId, Date updateDatetime, String status, String defectType, Integer orderNum) {
        this.productId = productId;
        this.uniqueId = uniqueId;
        this.fkKanbanId = fkKanbanId;
        this.compDatetime = updateDatetime;
        this.status = status;
        this.defectType = defectType;
        this.orderNum = orderNum;
    }

    /**
     * 製品IDを取得する。
     * 
     * @return 
     */
    public Long getProductId() {
        return this.productId;
    }

    /**
     * 製品IDを設定する。
     * 
     * @param productId 
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * ユニークIDを取得する。
     * 
     * @return 
     */
    public String getUniqueId() {
        return this.uniqueId;
    }

    /**
     * ユニークIDを設定する。
     * 
     * @param uniqueId 
     */
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     * カンバンIDを取得する。
     * 
     * @return 
     */
    public Long getFkKanbanId() {
        return this.fkKanbanId;
    }

    /**
     * カンバンIDを設定する。
     * 
     * @param fkKanbanId 
     */
    public void setFkKanbanId(Long fkKanbanId) {
        this.fkKanbanId = fkKanbanId;
    }

     /**
     * 完成日時を取得する。
     *
     * @return
     */
    public Date getCompDatetime() {
        return this.compDatetime;
    }

    /**
     * 完成日時を設定する。
     *
     * @param compDatetime
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
     * @return 
     */
    public Integer getOrderNum() {
        return orderNum;
    }

    /**
     * 副番を設定する。
     * 
     * @param orderNum 
     */
    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }
    /**
     * 前回のステータスを取得する。
     *
     * @return ステータス
     */
    public String getOldStatus() {
        return oldStatus;
    }

    /**
     * 前回のステータスを設定する。
     *
     * @param oldStatus
     */
    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }
    
    /**
     * 発見工程名を取得する。
     * 
     * @return 
     */
    public String getDefectWorkName() {
        return defectWorkName;
    }

    /**
     * 発見工程名を設定する。
     * 
     * @param defectWorkName 
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
        final ProductInfoEntity other = (ProductInfoEntity) obj;
        return Objects.equals(this.productId, other.productId);
    }

    @Override
    public String toString() {
        return new StringBuilder("ProductInfoEntity{")
                .append("productId=").append(this.productId)
                .append(", uniqueId=").append(this.uniqueId)
                .append(", fkKanbanId=").append(this.fkKanbanId)
                .append(", compDatetime=").append(this.compDatetime)
                .append(", status=").append(this.status)
                .append(", defectType=").append(this.defectType)
                .append("}").toString();
    }
}
