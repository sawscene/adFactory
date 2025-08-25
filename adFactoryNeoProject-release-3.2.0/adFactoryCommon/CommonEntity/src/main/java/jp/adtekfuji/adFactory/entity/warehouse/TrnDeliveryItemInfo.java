/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.warehouse;

import adtekfuji.utility.StringUtils;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 出庫指示アイテム情報
 *
 * @author s-heya
 */
@XmlRootElement(name = "deliveryItem")
@XmlAccessorType(XmlAccessType.FIELD)
public class TrnDeliveryItemInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private TrnDeliveryItemPK pk;
    @XmlElement
    private String materialNo;
    @XmlElement
    private String orderNo;
    @XmlElement
    private int requiredNum;
    @XmlElement
    private Integer deliveryNum;
    @XmlElement
    private Map<String, String> property;
    @XmlElement
    private Integer verInfo;
    @XmlElement
    private Date createDate;
    @XmlElement
    private Date updateDate;
    @XmlElement
    private MstProductInfo product;
    @XmlElement
    private Date dueDate;
    @XmlElement
    private String serialNo;
    @XmlElement
    private String unitNo;
    @XmlElement
    private String locationNo;
    @XmlElement
    private Integer usageNum;
    @XmlElement
    private Integer arrange;
    @XmlElement
    private String arrangeNo;
    @XmlElement
    private Integer reserve;
    @XmlElement
    private String modelName;
    @XmlElement
    private Integer withdrawNum;
    
    private List<TrnReserveMaterialInfo> reserveMaterials;
    
    private IntegerProperty itemNoProperty;
    private IntegerProperty deliveryNumProperty;
    private IntegerProperty withdrawNumProperty;

    /**
     * コンストラクタ
     */
    public TrnDeliveryItemInfo() {
    }
    
    /**
     * 出庫番号を取得する。
     * 
     * @return 出庫番号
     */
    public String getDeliveryNo() {
        return Objects.nonNull(this.pk) ? this.pk.getDeliveryNo() : null;
    }

    /**
     * 明細番号を取得する。
     * 
     * @return 明細番号
     */
    public Integer getItemNo() {
        return Objects.nonNull(this.pk) ? this.pk.getItemNo() : null;
    }

    /**
     * 資材番号を取得する。
     *
     * @return 資材番号
     */
    public String getMaterialNo() {
        return materialNo;
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
     * 要求数を取得する。
     *
     * @return 要求数
     */
    public int getRequiredNum() {
        return requiredNum;
    }

    /**
     * 要求数を設定する。
     *
     * @param requiredNum 要求数
     */
    public void setRequiredNum(int requiredNum) {
        this.requiredNum = requiredNum;
    }

   /**
     * 要求数プロパティを取得する。
     *
     * @return 要求数
     */
    public IntegerProperty requiredNumProperty() {
        if (Objects.isNull(this.requiredNum)) {
            this.requiredNum = 0;
        }
        return new SimpleIntegerProperty(this.requiredNum);
    }

    /**
     * 出庫数を取得する。
     *
     * @return 出庫数
     */
    public Integer getDeliveryNum() {
        return deliveryNum;
    }

    /**
     * 出庫数を設定する。
     *
     * @param deliveryNum 出庫数
     */
    public void setDeliveryNum(Integer deliveryNum) {
        this.deliveryNum = deliveryNum;
    }

    /**
     * プロパティーを取得する。
     *
     * @return
     */
    public Map<String, String> getProperty() {
        return property;
    }

    /**
     * プロパティーを設定する。
     *
     * @param property
     */
    public void setProperty(Map<String, String> property) {
        this.property = property;
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
     * 棚番号を取得する。
     * 
     * @return 棚番号
     */
    public String getDefaultLocationNo() {
        if (Objects.isNull(this.locationNo) 
                && Objects.nonNull(this.reserveMaterials) 
                && !this.reserveMaterials.isEmpty()) {
            MstLocationInfo location = this.reserveMaterials.get(0).getMaterial().getLocation();
            if (Objects.nonNull(location) && Objects.nonNull(location.getLocationNo())) {
                this.locationNo = location.getLocationNo();
            }
        }
        return Objects.nonNull(this.locationNo) ? this.locationNo : "";
    }

    /**
     * 棚番号を取得する。
     * 
     * @param areaName
     * @return 棚番号
     */
    public String getLocationNo(String areaName) {
        if (StringUtils.isEmpty(this.locationNo)) {
            if (Objects.nonNull(this.reserveMaterials) && !this.reserveMaterials.isEmpty()) {
                MstLocationInfo location = this.reserveMaterials.get(0).getMaterial().getLocation();
                if (Objects.nonNull(location) && StringUtils.equals(location.getAreaName(), areaName)) {
                   return location.getLocationNo();
                }
            }
            return Objects.nonNull(this.product) ? this.product.getLocationNo(areaName) : "";
        }
        return this.locationNo;
    }
    
    /**
     * 在庫引当数を取得する。
     * 
     * @return 在庫引当数
     */
    public int getReservedNum() {
        if (Objects.nonNull(this.reserveMaterials) && !this.reserveMaterials.isEmpty()) {
            int reservedNum = this.reserveMaterials.stream()
                .mapToInt(o -> o.getReservedNum())
                .sum();
            return reservedNum;
        }
        return 0;
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
     * ユニット番号を取得する。
     * 
     * @return ユニット番号
     */
    public String getUnitNo() {
        return Objects.nonNull(this.unitNo) ? this.unitNo : "";
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
     * 使用数を取得する。
     * 
     * @return 使用数
     */
    public Integer getUsageNum() {
        return usageNum;
    }

    /**
     * 使用数を設定する。
     * 
     * @param usageNum 使用数
     */
    public void setUsageNum(Integer usageNum) {
        this.usageNum = usageNum;
    }

    /**
     * 手配区分を取得する。
     * 
     * @return 手配区分
     */
    public Integer getArrange() {
        return arrange;
    }

    /**
     * 手配区分を設定する。
     * 
     * @param arrange 手配区分
     */
    public void setArrange(Integer arrange) {
        this.arrange = arrange;
    }

    /**
     * 在庫引当状況を取得する。
     * 
     * @return 在庫引当状況
     */
    public Integer getReserve() {
        if (Objects.equals(this.arrange, 2)) {
            // 在庫品
            return 3;
        }
        return reserve;
    }

    /**
     * 在庫引当状況を設定する。
     * 
     * @param reserve 在庫引当状況
     */
    public void setReserve(Integer reserve) {
        this.reserve = reserve;
    }

    /**
     * 在庫引当状況を返す
     * 
     * 0: 在庫無[×]
     * 1: 一部引当[△]
     * 2: 一部引当(製番違い)[！△]
     * 3: 引当済[○]
     * 4: 引当済(製番違い)[！○]
    **/
    public String getReserveStatus() {
        if (Objects.equals(this.arrange, 2)) {
            // 在庫品
            return "○";
        }

        if (Objects.isNull(this.reserve)) {
            return "×";
        }
        
        switch (this.reserve) {
            default:
            case 0:
                return "×";
            case 1:
                return "△";
            case 2:
                return "！△";
            case 3:
                return "○";
            case 4:
                return "！○";
        }
    }

    /**
     * 先行手配番号を取得する。
     * 
     * @return 先行手配番号
     */
    public String getArrangeNo() {
        return arrangeNo;
    }

    /**
     * 先行手配番号を設定する。
     * 
     * @param arrangeNo 先行手配番号
     */
    public void setArrangeNo(String arrangeNo) {
        this.arrangeNo = arrangeNo;
    }
    
    /**
     * モデル名を取得する。
     * 
     * @return モデル名
     */
    public String getModelName() {
        return modelName;
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
     * 在庫払出数を取得する。
     * 
     * @return 在庫払出数
     */
    public Integer getWithdrawNum() {
        return withdrawNum;
    }

    /**
     * 在庫払出数を設定する。
     * 
     * @param withdrawNum 在庫払出数
     */
    public void setWithdrawNum(Integer withdrawNum) {
        this.withdrawNum = withdrawNum;
    }
    
    /**
     * 明細番号プロパティを取得する。
     * 
     * @return 明細番号プロパティ
     */
    public IntegerProperty itemNoProperty() {
        if (Objects.isNull(this.itemNoProperty)) {
            this.itemNoProperty = new SimpleIntegerProperty(this.pk.getItemNo());
        }
        return this.itemNoProperty;
    }
    
    /**
     * 出庫数プロパティを取得する。
     * 
     * @return 出庫数プロパティ
     */
    public IntegerProperty deliveryNumProperty() {
        if (Objects.isNull(this.deliveryNumProperty)) {
            this.deliveryNumProperty = new SimpleIntegerProperty(Objects.nonNull(this.getDeliveryNum()) ? this.getDeliveryNum() : 0);
        }
        return this.deliveryNumProperty;
    }

    /**
     * 払出数プロパティを取得する。
     * 
     * @return 出庫数プロパティ
     */
    public IntegerProperty withdrawNumProperty() {
        if (Objects.isNull(this.withdrawNumProperty)) {
            this.withdrawNumProperty = new SimpleIntegerProperty(Objects.nonNull(this.getWithdrawNum()) ? this.getWithdrawNum() : 0);
        }
        return this.withdrawNumProperty;
    }

    /**
     * ハッシュコードを返す。
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.pk);
        return hash;
    }

    /**
     * オブジェクトを比較する。
     *
     * @param obj オブジェクト
     * @return true:同じである、false:異なる
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
        final TrnDeliveryItemInfo other = (TrnDeliveryItemInfo) obj;
        return Objects.equals(this.pk, other.pk);
    }

    /**
     * 文字列表現を返す。
     *
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("TrnDeliveryItem{")
                .append("pk=").append(this.pk)
                .append(", materialNo=").append(this.materialNo)
                .append(", orderNo=").append(this.orderNo)
                .append(", product=").append(this.product)
                .append(", requiredNum=").append(this.requiredNum)
                .append(", dueDate=").append(this.dueDate)
                .append(", deliveryNum=").append(this.deliveryNum)
                .append(", requiredNum=").append(this.requiredNum)
                .append(", arrange=").append(this.arrange)
                .append(", arrangeNo=").append(this.arrangeNo)
                .append(", reserve=").append(this.reserve)
                .append(", modelName=").append(this.modelName)
                .append(", verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
