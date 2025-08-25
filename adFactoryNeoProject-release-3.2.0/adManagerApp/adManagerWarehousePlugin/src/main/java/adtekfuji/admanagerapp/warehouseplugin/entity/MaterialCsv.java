/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.entity;

import adtekfuji.utility.DateUtils;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.warehouse.TrnMaterialInfo;

/**
 * 資材CSV情報
 *
 * @author nar-nakamura
 */
public class MaterialCsv implements Serializable {

    private static final long serialVersionUID = 1L;

    @CsvBindByPosition(position = 0)
    private final String productNo; // 品目
    @CsvBindByPosition(position = 1)
    private final String productName; // 品名

    @CsvBindByPosition(position = 2)
    private final String supplyNo; // 発注番号
    @CsvBindByPosition(position = 3)
    private final String orderNo; // 製造オーダー番号
    @CsvBindByPosition(position = 4)
    private final String serialNo; // 製造番号
    @CsvBindByPosition(position = 5)
    private final String materialNo; // 資材番号
    @CsvBindByPosition(position = 6)
    private final Integer inStockNum; // 在庫数
    @CsvBindByPosition(position = 7)
    private final String areaName; // 区画名
    @CsvBindByPosition(position = 8)
    private final String locationNo; // 棚番号
    @CsvBindByPosition(position = 9)
    private final Integer arrivalNum; // 納入予定数
    @CsvBindByPosition(position = 10)
    @CsvDate(value = "yyyy/MM/dd")
    private final Date arrivalPlan; // 納入予定日
    @CsvBindByPosition(position = 11)
    @CsvDate(value = "yyyy/MM/dd")
    private final Date stockDate; // 最終入庫日

    @CsvBindByPosition(position = 12)
    private final String inventoryDiff; // 在庫過不足
    @CsvBindByPosition(position = 13)
    private final Integer inventoryNum; // 棚卸数
    @CsvBindByPosition(position = 14)
    private final String inventoryLocationNo; // 棚番訂正
    @CsvBindByPosition(position = 15)
    @CsvDate(value = "yyyy/MM/dd")
    private final Date inventoryDate; // 棚卸実施日

    /**
     * コンストラクタ
     *
     * @param material 資材情報
     * @param inventoryLarge 超過の文字列
     * @param inventorySmall 不足の文字列
     */
    public MaterialCsv(TrnMaterialInfo material, String inventoryLarge, String inventorySmall) {
        if (Objects.nonNull(material.getProduct())) {
            this.productNo = material.getProduct().getProductNo(); // 品目
            this.productName = material.getProduct().getProductName(); // 品名
        } else {
            this.productNo = "";
            this.productName = "";
        }

        this.supplyNo = material.getSupplyNo(); // 発注番号
        this.orderNo = material.getOrderNo(); // 製造オーダー番号
        this.serialNo = material.getSerialNo(); // 製造番号
        this.materialNo = material.getMaterialNo(); // 資材番号
        this.inStockNum = material.getInStockNum(); // 在庫数

        if (Objects.nonNull(material.getLocation())) {
            this.areaName = material.getLocation().getAreaName(); // 区画名
            this.locationNo = material.getLocation().getLocationNo(); // 棚番号
        } else {
            this.areaName = "";
            this.locationNo = "";
        }

        this.arrivalNum = material.getArrivalNum(); // 納入予定数

        // 納入予定日
        if (Objects.nonNull(material.getArrivalPlan())) {
            this.arrivalPlan = DateUtils.toDate(material.getArrivalPlan());
        } else {
            this.arrivalPlan = null;
        }

        // 最終入庫日
        if (Objects.nonNull(material.getStockDate())) {
            this.stockDate = DateUtils.toDate(material.getStockDate());
        } else {
            this.stockDate = null;
        }

        // 在庫過不足
        if (Objects.nonNull(material.getInStockNum())
                && Objects.nonNull(material.getInventoryNum())) {
            Integer diff = material.getInventoryNum() - material.getInStockNum();
            if (diff > 0) {
                this.inventoryDiff = new StringBuilder()
                        .append(diff)
                        .append(" ")
                        .append(inventoryLarge) // 超過
                        .toString();
            } else if (diff < 0) {
                this.inventoryDiff = new StringBuilder()
                        .append(-diff)
                        .append(" ")
                        .append(inventorySmall) // 不足
                        .toString();
            } else {
                this.inventoryDiff = "";
            }
        } else {
            this.inventoryDiff = "";
        }

        this.inventoryNum = material.getInventoryNum(); // 棚卸数

        // 棚番訂正
        if (Objects.nonNull(material.getInventoryLocation())) {
            this.inventoryLocationNo = material.getInventoryLocation().getLocationNo();
        } else {
            this.inventoryLocationNo = "";
        }

        // 棚卸実施日
        if (Objects.nonNull(material.getInventoryDate())) {
            this.inventoryDate = DateUtils.toDate(material.getInventoryDate());
        } else {
            this.inventoryDate = null;
        }
    }

    /**
     * 品目を取得する。
     *
     * @return 品目
     */
    public String getProductNo() {
        return this.productNo;
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
     * 発注番号を取得する。
     *
     * @return 発注番号
     */
    public String getSupplyNo() {
        return this.supplyNo;
    }

    /**
     * 製造オーダー番号を取得する。
     *
     * @return 製造オーダー番号
     */
    public String getOrderNo() {
        return this.orderNo;
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
     * 資材番号を取得する。
     *
     * @return 資材番号
     */
    public String getMaterialNo() {
        return this.materialNo;
    }

    /**
     * 在庫数を取得する。
     *
     * @return 在庫数
     */
    public Integer getInStockNum() {
        return this.inStockNum;
    }

    /**
     * 区画名を取得する。
     *
     * @return 区画名
     */
    public String getAreaName() {
        return this.areaName;
    }

    /**
     * 棚番号を取得する。
     *
     * @return 棚番号
     */
    public String getLocationNo() {
        return this.locationNo;
    }

    /**
     * 納入予定数を取得する。
     *
     * @return 納入予定数
     */
    public Integer getArrivalNum() {
        return this.arrivalNum;
    }

    /**
     * 納入予定日を取得する。
     *
     * @return 納入予定日
     */
    public Date getArrivalPlan() {
        return this.arrivalPlan;
    }

    /**
     * 最終入庫日を取得する。
     *
     * @return 最終入庫日
     */
    public Date getStockDate() {
        return this.stockDate;
    }

    /**
     * 在庫過不足を取得する。
     *
     * @return 在庫過不足
     */
    public String getInventoryDiff() {
        return this.inventoryDiff;
    }

    /**
     * 棚卸数を取得する。
     *
     * @return 棚卸数
     */
    public Integer getInventoryNum() {
        return this.inventoryNum;
    }

    /**
     * 棚番訂正を取得する。
     *
     * @return 棚番訂正
     */
    public String getInventoryLocationNo() {
        return this.inventoryLocationNo;
    }

    /**
     * 棚卸実施日を取得する。
     *
     * @return 棚卸実施日
     */
    public Date getInventoryDate() {
        return this.inventoryDate;
    }

    @Override
    public String toString() {
        return new StringBuilder("MaterialCsv{")
                .append("productNo= ").append(this.productNo)
                .append(", productName= ").append(this.productName)
                .append(", supplyNo= ").append(this.supplyNo)
                .append(", orderNo= ").append(this.orderNo)
                .append(", serialNo= ").append(this.serialNo)
                .append(", materialNo= ").append(this.materialNo)
                .append(", inStockNum= ").append(this.inStockNum)
                .append(", areaName= ").append(this.areaName)
                .append(", locationNo= ").append(this.locationNo)
                .append(", arrivalNum= ").append(this.arrivalNum)
                .append(", arrivalPlan= ").append(this.arrivalPlan)
                .append(", stockDate= ").append(this.stockDate)
                .append(", inventoryDiff= ").append(this.inventoryDiff)
                .append(", inventoryNum= ").append(this.inventoryNum)
                .append(", inventoryLocationNo= ").append(this.inventoryLocationNo)
                .append(", inventoryDate= ").append(this.inventoryDate)
                .append("}")
                .toString();
    }
}
