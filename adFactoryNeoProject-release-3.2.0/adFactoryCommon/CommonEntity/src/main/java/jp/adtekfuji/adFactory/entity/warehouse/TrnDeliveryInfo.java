/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.warehouse;

import adtekfuji.utility.DateUtils;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.DeliveryStatusEnum;

/**
 * 出庫指示情報
 * 
 * @author sh.hirano
 */
@XmlRootElement(name = "delivery")
@XmlAccessorType(XmlAccessType.FIELD)
public class TrnDeliveryInfo {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private String deliveryNo;
    @XmlElement
    private String orderNo;
    @XmlElement
    private String serialNo;
    @XmlElement
    private Date dueDate;
    @XmlElement
    private Date createDate;
    @XmlElement
    private Date updateDate;
    @XmlElement
    private MstProductInfo product;
    @XmlElement
    private List<TrnDeliveryItemInfo> deliveryList;
    @XmlElement
    private Integer verInfo;
    @XmlElement
    private String serialStart;
    @XmlElement
    private String serialEnd;
    @XmlElement
    private String unitNo;
    @XmlElement
    private String unitName;
    @XmlElement
    private String destName;
    @XmlElement
    private DeliveryStatusEnum status;
    @XmlElement
    private Date deliveryDate;
    @XmlElement
    private String modelName;
    @XmlElement
    private Integer deliveryRule;
    @XmlElement
    private Integer stockOutNum;
    
    private IntegerProperty stockOutNumProperty;

    /**
     * 払出予定日 コンパレーター
     */
    public static final Comparator<TrnDeliveryInfo> dueDateComparator = (p1, p2) -> {
        LocalDateTime value1 = Objects.isNull(p1.getDueDate()) ? LocalDateTime.MAX : DateUtils.toLocalDateTime(p1.getDueDate());
        LocalDateTime value2 = Objects.isNull(p2.getDueDate()) ? LocalDateTime.MAX : DateUtils.toLocalDateTime(p2.getDueDate());
        return DateUtils.localDateTimeComparator.compare(value1, value2);
    };
    
    /**
     * 払出ステータス コンパレーター
     */
    public static final Comparator<TrnDeliveryInfo> statusComparator = (p1, p2) -> {
        String value1 = Objects.isNull(p1.getStatus()) ? "" : p1.getStatus().getSortKey();
        String value2 = Objects.isNull(p2.getStatus()) ? "" : p2.getStatus().getSortKey();
        return value1.compareTo(value2);
    };

    /**
     * ユニットNo コンパレーター
     */
    public static final Comparator<TrnDeliveryInfo> unitNoComparator = (p1, p2) -> {
        String value1 = Objects.isNull(p1.getUnitNo()) ? "" : p1.getUnitNo();
        String value2 = Objects.isNull(p2.getUnitNo()) ? "" : p2.getUnitNo();
        return value1.compareTo(value2);
    };
    
    /**
     * 製番 コンパレーター
     */
    public static final Comparator<TrnDeliveryInfo> orderNoComparator = (p1, p2) -> {
        String value1 = Objects.isNull(p1.getOrderNo()) ? "" : p1.getOrderNo();
        String value2 = Objects.isNull(p2.getOrderNo()) ? "" : p2.getOrderNo();
        return value1.compareTo(value2);
    };

    /**
     * コンストラクタ
     */
    public TrnDeliveryInfo() {
    }

    /**
     * コンストラクタ
     *
     * @param deliveryNo 出庫番号
     * @param createDate 作成日時
     */
    public TrnDeliveryInfo(String deliveryNo, Date createDate) {
        this.deliveryNo = deliveryNo;
        this.createDate = createDate;
    }

    /**
     * 出庫番号を取得する。
     *
     * @return 出庫番号
     */
    public String getDeliveryNo() {
        return deliveryNo;
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
     * 出庫番号プロパティを取得する。
     *
     * @return 出庫番号
     */
    public StringProperty getDeliveryNoProperty() {
        if (Objects.isNull(this.deliveryNo)) {
            this.deliveryNo = "";
        }
        return new SimpleStringProperty(this.deliveryNo);
    }

    /**
     * 製造番号を取得する。
     *
     * @return 製造番号
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 製造番号を設定する。
     *
     * @param orderNo 製造番号
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * シリアル番号を取得する。
     *
     * @return シリアル番号
     */
    public String getSerialNo() {
        return serialNo;
    }

    /**
     * シリアル番号を設定する。
     *
     * @param serialNo シリアル番号
     */
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    /**
     * 納期を取得する。
     *
     * @return 納期
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * 納期を設定する。
     *
     * @param dueDate 納期
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * 作成日時を取得する。
     *
     * @return 作成日時
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * 作成日時を設定する。
     *
     * @param createDate 作成日時
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * 更新日時を取得する。
     *
     * @return 更新日時
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * 更新日時を設定する。
     *
     * @param updateDate 更新日時
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * 部品マスタを取得する。
     *
     * @return 部品マスタ
     */
    public MstProductInfo getProduct() {
        return product;
    }

    /**
     * 部品マスタを設定する。
     *
     * @param product 部品マスタ
     */
    public void setProduct(MstProductInfo product) {
        this.product = product;
    }

    /**
     * 出庫指示アイテム一覧を取得する。
     *
     * @return 出庫指示アイテム一覧
     */
    public List<TrnDeliveryItemInfo> getDeliveryList() {
        return deliveryList;
    }

    /**
     * 出庫指示アイテム一覧を設定する。
     *
     * @param deliveryList 出庫指示アイテム一覧
     */
    public void setDeliveryList(List<TrnDeliveryItemInfo> deliveryList) {
        this.deliveryList = deliveryList;
    }

    /**
     * 開始シリアルを取得する。
     * 
     * @return 開始シリアル
     */
    public String getSerialStart() {
        return serialStart;
    }

    /**
     * 開始シリアルを設定する。
     * 
     * @param serialStart 開始シリアル
     */
    public void setSerialStart(String serialStart) {
        this.serialStart = serialStart;
    }

    /**
     * 開始シリアルプロパティを取得する。
     *
     * @return 開始シリアル
     */
    public StringProperty getSerialStartProperty() {
        if (Objects.isNull(this.serialStart)) {
            this.serialStart = "";
        }
        return new SimpleStringProperty(this.serialStart);
    }

    /**
     * 終了シリアルを取得する。
     *
     * @return 終了シリアル
     */
    public String getSerialEnd() {
        return serialEnd;
    }

    /**
     * 終了シリアルを設定する。
     *
     * @param serialEnd 終了シリアル
     */
    public void setSerialEnd(String serialEnd) {
        this.serialEnd = serialEnd;
    }

    /**
     * 終了シリアルプロパティを取得する。
     *
     * @return 終了シリアル
     */
    public StringProperty getSerialEndProperty() {
        if (Objects.isNull(this.serialEnd)) {
            this.serialEnd = "";
        }
        return new SimpleStringProperty(this.serialEnd);
    }

    /**
     * 機種名を取得する。
     * 
     * @return 機種名号
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * 機種名を設定する。
     * 
     * @param modelName 機種名
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * 機種名プロパティを取得する。
     *
     * @return 機種名
     */
    public StringProperty getModelNameProperty() {
        if (Objects.isNull(this.modelName)) {
            this.modelName = "";
        }
        return new SimpleStringProperty(this.modelName);
    }

    /**
     * ユニット番号を取得する。
     * 
     * @return ユニット番号
     */
    public String getUnitNo() {
        return unitNo;
    }

    /**
     * ユニット番号を設定する。
     * 
     * @param unitNo ユニット番号
     */
    public void setUnitNo(String unitNo) {
        this.unitNo = unitNo;
    }

    /**
     * ユニット番号プロパティを取得する。
     *
     * @return ユニット番号
     */
    public StringProperty getUnitNoProperty() {
        if (Objects.isNull(this.unitNo)) {
            this.unitNo = "";
        }
        return new SimpleStringProperty(this.unitNo);
    }

    /**
     * ユニット名を取得する。
     *
     * @return ユニット名
     */
    public String getUnitName() {
        return unitName;
    }

    /**
     * ユニット名を設定する。
     *
     * @param unitName ユニット名
     */
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    /**
     * ユニット名プロパティを取得する。
     *
     * @return ユニット名
     */
    public StringProperty getUnitNameProperty() {
        if (Objects.isNull(this.unitName)) {
            this.unitName = "";
        }
        return new SimpleStringProperty(this.unitName);
    }

    /**
     * 出庫先を取得する。
     *
     * @return 出庫先
     */
    public String getDestName() {
        return destName;
    }

    /**
     * 出庫先を設定する。
     *
     * @param destName 出庫先
     */
    public void setDestName(String destName) {
        this.destName = destName;
    }

    /**
     * 出庫先プロパティを取得する。
     *
     * @return 出庫先
     */
    public StringProperty getDestNameProperty() {
        if (Objects.isNull(this.destName)) {
            this.destName = "";
        }
        return new SimpleStringProperty(this.destName);
    }

    /**
     * ステータスを取得する。
     *
     * @return ステータス
     */
    public DeliveryStatusEnum getStatus() {
        return status;
    }

    /**
     * ステータスを設定する。
     *
     * @param status ステータス
     */
    public void setStatus(DeliveryStatusEnum status) {
        this.status = status;
    }

    /**
     * 出庫日を取得する。
     * 
     * @return 出庫日
     */
    public Date getDeliveryDate() {
        return deliveryDate;
    }

    /**
     * 出庫日を設定する。
     * 
     * @param deliveryDate 出庫日 
     */
    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    /**
     * 出庫ルールを取得する。
     * 
     * @return 出庫ルール
     */
    public Integer getDeliveryRule() {
        return deliveryRule;
    }

    /**
     * 出庫ルールを設定する。
     * 
     * @param deliveryRule 出庫ルール
     */
    public void setDeliveryRule(Integer deliveryRule) {
        this.deliveryRule = deliveryRule;
    }

    /**
     * 欠品数を取得する。
     * 
     * @return 欠品数
     */
    public Integer getStockOutNum() {
        return stockOutNum;
    }

    /**
     * 欠品数を設定する。
     * 
     * @param stockOutNum 欠品数
     */
    public void setStockOutNum(Integer stockOutNum) {
        this.stockOutNum = stockOutNum;
    }

    /**
     * 欠品数プロパティーを取得する。
     * 
     * @return 欠品数
     */
    public IntegerProperty stockOutNumProperty() {
        if (Objects.isNull(this.stockOutNumProperty)) {
            this.stockOutNumProperty = new SimpleIntegerProperty(Objects.nonNull(this.getStockOutNum()) ? this.getStockOutNum() : 0);
        }
        return this.stockOutNumProperty;
    }

    /**
     * ハッシュコードを返す。
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (deliveryNo != null ? deliveryNo.hashCode() : 0);
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
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TrnDeliveryInfo)) {
            return false;
        }
        TrnDeliveryInfo other = (TrnDeliveryInfo) object;
        return Objects.equals(this.deliveryNo, other.deliveryNo);
    }

    /**
     * 文字列表現を返す。
     *
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("TrnDelivery{")
                .append("deliveryNo=").append(this.deliveryNo)
                .append(", orderNo=").append(this.orderNo)
                .append(", serialNo=").append(this.serialNo)
                .append(", dueDate=").append(this.dueDate)
                .append(", createDate=").append(this.createDate)
                .append(", updateDate=").append(this.updateDate)
                .append(", product=").append(this.product)
                .append(", deliveryList=").append(this.deliveryList)
                .append(", verInfo=").append(this.verInfo)
                .append(", serialStart=").append(this.serialStart)
                .append(", serialEnd=").append(this.serialEnd)
                .append(", unitCode=").append(this.unitNo)
                .append(", unitNamee=").append(this.unitName)
                .append(", destName=").append(this.destName)
                .append(", status=").append(this.status)
                .append(", deliveryDate=").append(this.deliveryDate)
                .append("}")
                .toString();
    }
}
