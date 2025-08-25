/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.warehouse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.DeliveryStatusEnum;
import jp.adtekfuji.adfactoryserver.utility.LocaleUtils;

/**
 * 出庫指示情報
 * 
 * @author s-heya
 */
@Entity
@Table(name = "trn_delivery")
@XmlRootElement(name = "delivery")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
@NamedQueries({
    @NamedQuery(name = "TrnDelivery.find", query = "SELECT o FROM TrnDelivery o LEFT OUTER JOIN FETCH o.deliveryList WHERE o.deliveryNo = :deliveryNo"),
    @NamedQuery(name = "TrnDelivery.findUnitAll", query = "SELECT DISTINCT o.unitNo FROM TrnDelivery o WHERE o.deliveryRule IS NOT NULL AND o.unitNo IS NOT NULL AND o.status IN (jp.adtekfuji.adFactory.enumerate.DeliveryStatusEnum.WAITING, jp.adtekfuji.adFactory.enumerate.DeliveryStatusEnum.WORKING, jp.adtekfuji.adFactory.enumerate.DeliveryStatusEnum.SUSPEND) ORDER BY o.unitNo"),
    @NamedQuery(name = "TrnDelivery.findModelAll", query = "SELECT DISTINCT o.modelName FROM TrnDelivery o WHERE o.deliveryRule IS NOT NULL AND o.modelName IS NOT NULL AND o.status IN (jp.adtekfuji.adFactory.enumerate.DeliveryStatusEnum.WAITING, jp.adtekfuji.adFactory.enumerate.DeliveryStatusEnum.WORKING, jp.adtekfuji.adFactory.enumerate.DeliveryStatusEnum.SUSPEND) ORDER BY o.modelName"),
    @NamedQuery(name = "TrnDelivery.findUnitAllDelivery", query = "SELECT DISTINCT o.unitNo FROM TrnDelivery o WHERE o.deliveryRule IS NOT NULL AND o.unitNo IS NOT NULL AND o.status IN (jp.adtekfuji.adFactory.enumerate.DeliveryStatusEnum.SUSPEND, jp.adtekfuji.adFactory.enumerate.DeliveryStatusEnum.PICKED) ORDER BY o.unitNo"),
    @NamedQuery(name = "TrnDelivery.findModelAllDelivery", query = "SELECT DISTINCT o.modelName FROM TrnDelivery o WHERE o.deliveryRule IS NOT NULL AND o.modelName IS NOT NULL AND o.status IN (jp.adtekfuji.adFactory.enumerate.DeliveryStatusEnum.PICKED, jp.adtekfuji.adFactory.enumerate.DeliveryStatusEnum.PICKED) ORDER BY o.modelName"),
    @NamedQuery(name = "TrnDelivery.findAll", query = "SELECT o FROM TrnDelivery o WHERE o.deliveryNo IN :deliveryNo ORDER BY o.deliveryNo"),
    @NamedQuery(name = "TrnDelivery.findAllRule2", query = "SELECT o FROM TrnDelivery o WHERE o.deliveryNo IN :deliveryNo AND o.deliveryRule = 2 ORDER BY o.deliveryNo"),
})
public class TrnDelivery implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 32)
    @Column(name = "delivery_no")
    private String deliveryNo;
    @Size(max = 32)
    @Column(name = "order_no")
    private String orderNo;
    @Size(max = 32)
    @Column(name = "serial_no")
    private String serialNo;
    //@Column(name = "product_id")
    //private BigInteger productId;
    @Column(name = "due_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;
    @Basic(optional = false)
    //@NotNull
    @Column(name = "create_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    @Column(name = "update_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;
    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "product_id")
    private MstProduct product;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name = "delivery_no")
    private List<TrnDeliveryItem> deliveryList;
    @Version
    @Column(name = "ver_info")
    private Integer verInfo;
    @Size(max = 32)
    @Column(name = "serial_start")
    private String serialStart;
    @Size(max = 32)
    @Column(name = "serial_end")
    private String serialEnd;
    @Column(name = "model_name")
    private String modelName;
    @Column(name = "unit_name")
    private String unitName;
    @Column(name = "unit_no")
    private String unitNo;
    @Column(name = "dest_name")
    private String destName;
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    @Column(name = "status")
    private DeliveryStatusEnum status;
    @Column(name = "delivery_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deliveryDate;
    @Column(name = "delivery_rule")
    private Integer deliveryRule;
    @Column(name = "stockout_num")
    private Integer stockOutNum;
        
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/dd");
     
    /**
     * コンストラクタ
     */
    public TrnDelivery() {
    }

    /**
     * コンストラクタ
     * 
     * @param deliveryNo 出庫番号
     * @param createDate 作成日時
     */
    public TrnDelivery(String deliveryNo, Date createDate) {
        this.deliveryNo = deliveryNo;
        this.createDate = createDate;
        this.status = DeliveryStatusEnum.WAITING;
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

    //public BigInteger getProductId() {
    //    return productId;
    //}
    //
    //public void setProductId(BigInteger productId) {
    //    this.productId = productId;
    //}

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
     * 期日を取得する。
     * 
     * @return 納期
     */
    public String getDueDateLong() {
        if (Objects.nonNull(dueDate)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            return dateFormat.format(dueDate);
        }
        return "";
    }
    
    /**
     * 期日を取得する。
     * 
     * @return 納期
     */
    public String getDueDateShort() {
        if (Objects.nonNull(dueDate)) {
            LocalDate localDate = LocalDate.from(dueDate.toInstant().atZone(ZoneOffset.UTC));
            return FORMATTER.format(localDate);
        }
        return "";
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
     * @return 
     */
    public MstProduct getProduct() {
        return product;
    }

    /**
     * 部品マスタを設定する。
     * 
     * @param product 部品マスタ
     */
    public void setProduct(MstProduct product) {
        this.product = product;
    }

    /**
     * 出庫指示アイテム一覧を取得する。
     * 
     * @return 出庫指示アイテム一覧
     */
    public List<TrnDeliveryItem> getDeliveryList() {
        return deliveryList;
    }

    /**
     * 出庫指示アイテム一覧を設定する。
     * 
     * @param deliveryList 出庫指示アイテム一覧
     */
    public void setDeliveryList(List<TrnDeliveryItem> deliveryList) {
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
     * 機種名を取得する。
     * 
     * @return 機種名
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

    public String getDisplayStatus(String lang) {
        return LocaleUtils.getString(this.status.getResourceKey(), lang);
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
        if (!(object instanceof TrnDelivery)) {
            return false;
        }
        TrnDelivery other = (TrnDelivery) object;
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
            .append(", serialStart=").append(this.serialStart)
            .append(", serialEnd=").append(this.serialEnd)
            .append(", unitNo=").append(this.unitNo)
            .append(", unitName=").append(this.unitName)
            .append(", product=").append(this.product)
            .append(", dueDate=").append(this.dueDate)
            .append(", destName=").append(this.destName)
            .append(", status=").append(this.status)
            .append(", deliveryDate=").append(this.deliveryDate)
            .append(", deliveryRule=").append(this.deliveryRule)
            .append("}")
            .toString();
    }
}
