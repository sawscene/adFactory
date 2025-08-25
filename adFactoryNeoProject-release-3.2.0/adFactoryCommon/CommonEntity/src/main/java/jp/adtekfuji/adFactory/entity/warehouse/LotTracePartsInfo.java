/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.warehouse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Objects;

/**
 * ロットトレース部品情報
 *
 * @author nar-nakamura
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class LotTracePartsInfo implements Serializable {

    private String deliveryNo;          // 出庫番号

    private Integer itemNo;             // 明細番号

    private String materialNo;          // 資材番号

    private Integer number;             // No

    private String figureNo;            // 図番

    private String productName;         // 品名

    private String serialNo;            // 製造番号(ロット番号)

    private String partsNo;             // 部品番号(自動採番したロット番号)

    private Integer traceNum;           // 数量

    private Boolean isDone;             // 確認チェック
    
    private Boolean isDisabled;         // 追跡無効フラグ

    private Boolean isDelete;           // 削除フラグ

    /**
     * コンストラクタ
     */
    public LotTracePartsInfo() {
        this.isDone = false;
        this.isDelete = false;
    }

    /**
     * 出庫番号を取得する。
     *
     * @return 出庫番号
     */
    public String getDeliveryNo() {
        return this.deliveryNo;
    }

    /**
     * 出庫番号を設定する。
     *
     * @param deliveryNo 出庫番号
     */
    public void setDeliveryNo(String deliveryNo) {
        this.deliveryNo = deliveryNo;
    }

    /**
     * 明細番号を取得する。
     *
     * @return 明細番号
     */
    public Integer getItemNo() {
        return this.itemNo;
    }

    /**
     * 明細番号を設定する。
     *
     * @param itemNo 明細番号
     */
    public void setItemNo(Integer itemNo) {
        this.itemNo = itemNo;
    }

    /**
     * 資材番号を取得する。
     *
     * @return 資材番号
     */
    public String getMaterialNo() {
        return this.materialNo;
    }

    /**
     * 資材番号を設定する。
     *
     * @param materialNo 資材番号
     */
    public void setMaterialNo(String materialNo) {
        this.materialNo = materialNo;
    }

    /**
     * Noを取得する。
     *
     * @return No
     */
    public Integer getNumber() {
        return this.number;
    }

    /**
     * Noを設定する。
     *
     * @param number No
     */
    public void setNumber(Integer number) {
        this.number = number;
    }

    /**
     * 図番を取得する。
     *
     * @return 図番
     */
    public String getFigureNo() {
        return this.figureNo;
    }

    /**
     * 図番を設定する。
     *
     * @param figureNo 図番
     */
    public void setFigureNo(String figureNo) {
        this.figureNo = figureNo;
    }

    /**
     * 品名を取得する。
     *
     * @return 品名
     */
    public String getProductName() {
        return this.productName;
    }

    /**
     * 品名を設定する。
     *
     * @param productName 品名
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * 製造番号(ロット番号)を取得する。
     *
     * @return 製造番号(ロット番号)
     */
    public String getSerialNo() {
        return this.serialNo;
    }

    /**
     * 製造番号(ロット番号)を設定する。
     *
     * @param serialNo 製造番号(ロット番号)
     */
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    /**
     * 部品番号(自動採番したロット番号)を取得する。
     *
     * @return 部品番号(自動採番したロット番号)
     */
    public String getPartsNo() {
        return this.partsNo;
    }

    /**
     * 部品番号(自動採番したロット番号)を設定する。
     *
     * @param partsNo 部品番号(自動採番したロット番号)
     */
    public void setPartsNo(String partsNo) {
        this.partsNo = partsNo;
    }

    /**
     * 数量を取得する。
     *
     * @return 数量
     */
    public Integer getTraceNum() {
        return this.traceNum;
    }

    /**
     * 数量を設定する。
     *
     * @param traceNum 数量
     */
    public void setTraceNum(Integer traceNum) {
        this.traceNum = traceNum;
    }

    /**
     * 確認チェックを取得する。
     *
     * @return 確認チェック
     */
    public Boolean getIsDone() {
        return this.isDone;
    }

    /**
     * 確認チェックを設定する。
     *
     * @param isDone 確認チェック
     */
    public void setDone(Boolean isDone) {
        this.isDone = isDone;
    }

    /**
     * 追跡を無効にするかを返す。
     * 
     * @return true:追跡無効、false:追跡有効
     */
    public Boolean isDisabled() {
        return isDisabled;
    }

    /**
     * 追跡無効を設定する。
     * 
     * @param isDisabled true:追跡無効、false:追跡有効
     */
    public void setDisabled(Boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    /**
     * 工程情報を削除するかを返す。
     * 
     * @return true:工程情報を削除する、false:工程情報を削除しない
     */
    public Boolean getIsDelete() {
        return Objects.nonNull(isDelete) ? isDelete : false;
    }

    /**
     * 工程情報の削除を設定する。
     * 
     * @param isDelete true:工程情報を削除する、false:工程情報を削除しない
     */
    public void setDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }

    @Override
    public String toString() {
        return new StringBuilder("LotTracePartsInfo{")
                .append("deliveryNo=").append(this.deliveryNo)
                .append(", itemNo=").append(this.itemNo)
                .append(", materialNo=").append(this.materialNo)
                .append(", number=").append(this.number)
                .append(", figureNo=").append(this.figureNo)
                .append(", productName=").append(this.productName)
                .append(", serialNo=").append(this.serialNo)
                .append(", partsNo=").append(this.partsNo)
                .append(", traceNum=").append(this.traceNum)
                .append(", isDone=").append(this.isDone)
                .append(", isDisabled=").append(this.isDisabled)
                .append(", isDelete=").append(this.isDelete)
                .append("}")
                .toString();
    }
}
