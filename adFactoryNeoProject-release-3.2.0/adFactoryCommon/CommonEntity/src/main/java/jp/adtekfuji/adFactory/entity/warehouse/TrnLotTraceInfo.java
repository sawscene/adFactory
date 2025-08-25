/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.warehouse;

import adtekfuji.rest.LocalDateTimeAdapter;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * ロットトレース情報
 *
 * @author s-heya
 */
@XmlRootElement(name = "lotTrace")
@XmlAccessorType(XmlAccessType.FIELD)
public class TrnLotTraceInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private String deliveryNo;
    @XmlElement
    private int itemNo;
    @XmlElement
    private String materialNo;
    @XmlElement
    private String serialNo;
    @XmlElement
    private String partsNo;
    @XmlElement
    private Integer traceNum;
    @XmlElement
    private String personNo;
    @XmlElement
    private Long kanbanId;
    @XmlElement
    private Long workKanbanId;
    @XmlElement
    private Boolean confirm;        // 確認
    @XmlElement
    private String kanbanName;      // カンバン名
    @XmlElement
    private String modelName;       // モデル名
    @XmlElement
    private String workName;        // 工程名
    @XmlElement
    private String personName;      // 作業者
    @XmlElement
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime assemblyDatetime;  // 組付け日時
    @XmlElement
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime createDate;
    @XmlElement
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime updateDate;
    @XmlElement
    private Integer verInfo;

    @XmlElement
    private TrnDeliveryItemInfo deliveryItem;

    @XmlTransient
    private boolean parent = false;

    /**
     * コンストラクタ
     */
    public TrnLotTraceInfo() {
    }

    /**
     * ロットトレース情報を生成する。(ロットトレース画面の表示用)
     * 
     * @param workName 工程名
     * @return ロットトレース情報
     */
    public static TrnLotTraceInfo createWorkItem(String workName) {
        TrnLotTraceInfo lotTrace = new TrnLotTraceInfo();
        lotTrace.workName = workName;
        lotTrace.kanbanName = workName;
        return lotTrace;
    }

    /**
     * カンバンのロットトレース情報を生成する。(ロットトレース画面の表示用)
     *
     * @param kanbanId カンバンID
     * @param kanbanName カンバン名
     * @param modelName モデル名
     * @return ロットトレース情報
     */
    public static TrnLotTraceInfo createKanbanItem(Long kanbanId, String kanbanName, String modelName) {
        TrnLotTraceInfo lotTrace = new TrnLotTraceInfo();
        lotTrace.setKanbanName(kanbanName);
        lotTrace.setModelName(modelName);

        lotTrace.setKanbanId(kanbanId);
        lotTrace.parent = true;

        return lotTrace;
    }

    /**
     * 作業者のロットトレース情報を生成する。(ロットトレース画面の表示用)
     *
     * @param kanbanId カンバンID
     * @param kanbanName カンバン名
     * @param modelName モデル名
     * @param personNo 社員番号
     * @return ロットトレース情報
     */
    public static TrnLotTraceInfo createPersonItem(Long kanbanId, String kanbanName, String modelName, String personNo) {
        TrnLotTraceInfo lotTrace = new TrnLotTraceInfo();
        lotTrace.setKanbanName(kanbanName);
        lotTrace.setModelName(modelName);

        lotTrace.setKanbanId(kanbanId);
        lotTrace.setPersonNo(personNo);
        lotTrace.parent = true;

        return lotTrace;
    }

    /**
     * 品目に工程名を表示するためのロットトレース情報を生成する。(ロットトレース画面の表示用)
     *
     * @param workName 工程名
     * @return ロットトレース情報
     */
    public static TrnLotTraceInfo createProductWorkItem(String workName) {
        MstProductInfo product = new MstProductInfo();
        product.setProductNo(workName);

        TrnDeliveryItemInfo deliveryItem = new TrnDeliveryItemInfo();
        deliveryItem.setProduct(product);

        TrnLotTraceInfo lotTrace = new TrnLotTraceInfo();
        lotTrace.setDeliveryItem(deliveryItem);
        lotTrace.setWorkName(workName);
        lotTrace.parent = true;

        return lotTrace;
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
    public int getItemNo() {
        return this.itemNo;
    }

    /**
     * 明細番号を設定する。
     * 
     * @param itemNo 明細番号
     */
    public void setItemNo(int itemNo) {
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
     * 製造番号を取得する。
     *
     * @return 製造番号
     */
    public String getSerialNo() {
        return this.serialNo;
    }

    /**
     * 製造番号を設定する。
     *
     * @param serialNo 製造番号
     */
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    /**
     * 部品番号を取得する。
     *
     * @return 部品番号
     */
    public String getPartsNo() {
        return this.partsNo;
    }

    /**
     * 部品番号を設定する。
     *
     * @param partsNo 部品番号
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
     * 社員番号を取得する。
     *
     * @return 社員番号
     */
    public String getPersonNo() {
        return this.personNo;
    }

    /**
     * 社員番号を設定する。
     *
     * @param personNo 社員番号
     */
    public void setPersonNo(String personNo) {
        this.personNo = personNo;
    }

    /**
     * カンバンIDを取得する。
     *
     * @return カンバンID
     */
    public Long getKanbanId() {
        return this.kanbanId;
    }

    /**
     * カンバンIDを設定する。
     *
     * @param kanbanId カンバンID
     */
    public void setKanbanId(Long kanbanId) {
        this.kanbanId = kanbanId;
    }

    /**
     * 工程カンバンIDを取得する。
     *
     * @return 工程カンバンID
     */
    public Long getWorkKanbanId() {
        return this.workKanbanId;
    }

    /**
     * 工程カンバンIDを設定する。
     *
     * @param workKanbanId 工程カンバンID
     */
    public void setWorkKanbanId(Long workKanbanId) {
        this.workKanbanId = workKanbanId;
    }

    /**
     * 確認を取得する。
     *
     * @return 確認
     */
    public Boolean getConfirm() {
        return this.confirm;
    }

    /**
     * 確認を設定する。
     *
     * @param confirm 確認
     */
    public void setConfirm(Boolean confirm) {
        this.confirm = confirm;
    }

    /**
     * カンバン名を取得する。
     *
     * @return カンバン名
     */
    public String getKanbanName() {
        return this.kanbanName;
    }

    /**
     * カンバン名を設定する。
     *
     * @param kanbanName カンバン名
     */
    public void setKanbanName(String kanbanName) {
        this.kanbanName = kanbanName;
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
     * 工程名を取得する。
     *
     * @return 工程名
     */
    public String getWorkName() {
        return this.workName;
    }

    /**
     * 工程名を設定する。
     *
     * @param workName 工程名
     */
    public void setWorkName(String workName) {
        this.workName = workName;
    }

    /**
     * 作業者を取得する。
     *
     * @return 作業者
     */
    public String getPersonName() {
        return this.personName;
    }

    /**
     * 作業者を設定する。
     *
     * @param personName 作業者
     */
    public void setPersonName(String personName) {
        this.personName = personName;
    }

    /**
     * 組付け日時を取得する。
     *
     * @return 組付け日時
     */
    public LocalDateTime getAssemblyDatetime() {
        return this.assemblyDatetime;
    }

    /**
     * 組付け日時を設定する。
     *
     * @param assemblyDatetime 組付け日時
     */
    public void setAssemblyDatetime(LocalDateTime assemblyDatetime) {
        this.assemblyDatetime = assemblyDatetime;
    }

    /**
     * 作成日時を取得する。
     *
     * @return 作成日時
     */
    public LocalDateTime getCreateDate() {
        return this.createDate;
    }

    /**
     * 作成日時を設定する。
     *
     * @param createDate 作成日時
     */
    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    /**
     * 更新日時を取得する。
     *
     * @return 更新日時
     */
    public LocalDateTime getUpdateDate() {
        return this.updateDate;
    }

    /**
     * 更新日時を設定する。
     *
     * @param updateDate 更新日時
     */
    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * 排他用バージョンを取得する。
     *
     * @return 排他用バージョン
     */
    public Integer getVerInfo() {
        return this.verInfo;
    }

    /**
     * 排他用バージョンを設定する。
     *
     * @param verInfo 排他用バージョン
     */
    public void setVerInfo(Integer verInfo) {
        this.verInfo = verInfo;
    }

    /**
     * 出庫アイテム情報を取得する。
     *
     * @return 出庫アイテム情報
     */
    public TrnDeliveryItemInfo getDeliveryItem() {
        return this.deliveryItem;
    }

    /**
     * 出庫アイテム情報を設定する。
     *
     * @param deliveryItem 出庫アイテム情報
     */
    public void setDeliveryItem(TrnDeliveryItemInfo deliveryItem) {
        this.deliveryItem = deliveryItem;
    }

    /**
     * 親項目かどうかを返す。
     * 
     * @return
     */
    public boolean isParent() {
        return this.parent;
    }

    /**
     * 製造オーダー番号を取得する。
     *
     * @return 製造オーダー番号
     */
    public String getOrderNo() {
        if (Objects.isNull(this.deliveryItem)) {
            return null;
        }

        return this.deliveryItem.getOrderNo();
    }

    /**
     * 図番を取得する。
     *
     * @return 図番
     */
    public String getFigureNo() {
        if (Objects.isNull(this.deliveryItem)
                || Objects.isNull(this.deliveryItem.getProduct())) {
            return null;
        }

        return this.getDeliveryItem().getProduct().getFigureNo();
    }

    /**
     * 品目を取得する。
     *
     * @return 品目
     */
    public String getProductNo() {
        if (Objects.isNull(this.deliveryItem)
                || Objects.isNull(this.deliveryItem.getProduct())) {
            return null;
        }

        return this.getDeliveryItem().getProduct().getProductNo();
    }

    /**
     * 品名を取得する。
     *
     * @return 品名
     */
    public String getProductName() {
        if (Objects.isNull(this.deliveryItem)
                || Objects.isNull(this.deliveryItem.getProduct())) {
            return null;
        }

        return this.getDeliveryItem().getProduct().getProductName();
    }

    /**
     * ハッシュコードを返す。
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.deliveryNo);
        hash = 89 * hash + this.itemNo;
        hash = 89 * hash + Objects.hashCode(this.materialNo);
        return hash;
    }

    /**
     * オブジェクトを比較する。
     *
     * @param object オブジェクト
     * @return true:同じである、false:異なる
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final TrnLotTraceInfo other = (TrnLotTraceInfo) object;
        if (!Objects.equals(this.deliveryNo, other.deliveryNo)) {
            return false;
        }
        if (this.itemNo != other.itemNo) {
            return false;
        }
        return Objects.equals(this.materialNo, other.materialNo);
    }

    /**
     * 文字列表現を返す。
     *
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("TrnLotTrace{")
                .append("deliveryNo=").append(this.deliveryNo)
                .append(", itemNo=").append(this.itemNo)
                .append(", materialNo=").append(this.materialNo)
                .append(", serialNo=").append(this.serialNo)
                .append(", partsNo=").append(this.partsNo)
                .append(", traceNum=").append(this.traceNum)
                .append(", personNo=").append(this.personNo)
                .append(", kanbanId=").append(this.kanbanId)
                .append(", workKanbanId=").append(this.workKanbanId)
                .append(", confirm=").append(this.confirm)
                .append(", kanbanName=").append(this.kanbanName)
                .append(", modelName=").append(this.modelName)
                .append(", workName=").append(this.workName)
                .append(", personName=").append(this.personName)
                .append(", assemblyDatetime=").append(this.assemblyDatetime)
                .append(", verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
