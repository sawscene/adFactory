/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adappentity;

import adtekfuji.utility.StringUtils;
import java.io.Serializable;
import java.util.Objects;
import jp.adtekfuji.adappentity.enumerate.DisposalClassEnum;

/**
 * 製品情報
 *
 * @author nar-nakamura
 */
public class ProductEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Long productId;
    private String uniqueId;
    protected Long fkKanbanId;
    private String upateDatetime;
    private String status;
    private String defectType;
    private Integer orderNum;
    private String oldStatus;
    private boolean visible = false;
    private Integer defectNum;
    private String defectWorkName;

    /**
     * コンストラクタ
     */
    public ProductEntity() {
    }

    /**
     * 製品IDを取得する。
     *
     * @return
     */
    public Long getProductId() {
        return productId;
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
        return uniqueId;
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
        return fkKanbanId;
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
     * タイムスタンプを取得する。
     *
     * @return タイムスタンプ
     */
    public String getUpateDatetime() {
        return upateDatetime;
    }

    /**
     * タイムスタンプを設定する。
     *
     * @param upateDatetime タイムスタンプ
     */
    public void setUpateDatetime(String upateDatetime) {
        this.upateDatetime = upateDatetime;
    }

    /**
     * ステータスを取得する。
     *
     * @return ステータス
     */
    public String getStatus() {
        if (StringUtils.isEmpty(status)) {
            status = DisposalClassEnum.A.toString();
        }
        return status;
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
        return defectType;
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

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * 廃棄数を取得する。
     * 
     * @return 
     */
    public Integer getDefectNum() {
        return defectNum;
    }

    /**
     * 廃棄数を設定する。
     * 
     * @param defectNum 
     */
    public void setDefectNum(Integer defectNum) {
        this.defectNum = defectNum;
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
        final ProductEntity other = (ProductEntity) obj;
        return Objects.equals(this.productId, other.productId);
    }

    @Override
    public String toString() {
        return "ProductEntity{" + "productId=" + productId + ", uniqueId=" + uniqueId + ", fkKanbanId=" + fkKanbanId + ", upateDatetime=" + upateDatetime + ", status=" + status + ", defectType=" + defectType + '}';
    }
}
