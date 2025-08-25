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
import jp.adtekfuji.adFactory.entity.warehouse.TrnLotTraceInfo;

/**
 * ロットトレースCSV情報
 *
 * @author nar-nakamura
 */
public class LotTraceCsv implements Serializable {
    private static final long serialVersionUID = 1L;

    @CsvBindByPosition(position = 0)
    private final String figureNo; // 図番
    @CsvBindByPosition(position = 1)
    private final String productNo; // 品目
    @CsvBindByPosition(position = 2)
    private final String productName; // 品名
    @CsvBindByPosition(position = 3)
    private final String orderNo; // 製造オーダー番号
    @CsvBindByPosition(position = 4)
    private final String lotNo; // ロット番号
    @CsvBindByPosition(position = 5)
    private final String deliveryNo; // 出庫番号
    @CsvBindByPosition(position = 6)
    private final int itemNo; // 明細番号
    @CsvBindByPosition(position = 7)
    private final String materialNo; // 資材番号
    @CsvBindByPosition(position = 8)
    private final String kanbanName; // カンバン名
    @CsvBindByPosition(position = 9)
    private final String modelName; // モデル名
    @CsvBindByPosition(position = 10)
    private final String workName; // 工程名
    @CsvBindByPosition(position = 11)
    private final String personNo; // 社員番号
    @CsvBindByPosition(position = 12)
    private final String personName; // 社員名
    @CsvBindByPosition(position = 13)
    @CsvDate(value = "yyyy/MM/dd HH:mm:ss")
    private final Date assemblyDatetime; // 組付け日時

    /**
     * コンストラクタ
     *
     * @param lotTrace ロットトレース情報
     */
    public LotTraceCsv(TrnLotTraceInfo lotTrace) {
        this.figureNo = lotTrace.getFigureNo();
        this.productNo = lotTrace.getProductNo();
        this.productName = lotTrace.getProductName();
        this.orderNo = lotTrace.getOrderNo();
        this.lotNo = lotTrace.getPartsNo();
        this.deliveryNo = lotTrace.getDeliveryNo();
        this.itemNo = lotTrace.getItemNo();
        this.materialNo = lotTrace.getMaterialNo();
        this.kanbanName = lotTrace.getKanbanName();
        this.modelName = lotTrace.getModelName();
        this.workName = lotTrace.getWorkName();
        this.personNo = lotTrace.getPersonNo();
        this.personName = lotTrace.getPersonName();
        this.assemblyDatetime = DateUtils.toDate(lotTrace.getAssemblyDatetime());
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
     * 製造オーダー番号を取得する。
     *
     * @return 製造オーダー番号
     */
    public String getOrderNo() {
        return this.orderNo;
    }

    /**
     * ロット番号を取得する。
     *
     * @return ロット番号
     */
    public String getLotNo() {
        return this.lotNo;
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
     * 明細番号を取得する。
     *
     * @return 明細番号
     */
    public int getItemNo() {
        return this.itemNo;
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
     * カンバン名を取得する。
     *
     * @return カンバン名
     */
    public String getKanbanName() {
        return this.kanbanName;
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
     * 工程名を取得する。
     *
     * @return 工程名
     */
    public String getWorkName() {
        return this.workName;
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
     * 社員名を取得する。
     *
     * @return 社員名
     */
    public String getPersonName() {
        return this.personName;
    }

    /**
     * 組付け日時を取得する。
     *
     * @return 組付け日時
     */
    public Date getAssemblyDatetime() {
        return this.assemblyDatetime;
    }

    @Override
    public String toString() {
        return new StringBuilder("LotTracePersonCsv{")
                .append("figureNo=").append(this.figureNo)
                .append(", productNo=").append(this.productNo)
                .append(", productName=").append(this.productName)
                .append(", orderNo=").append(this.orderNo)
                .append(", lotNo=").append(this.lotNo)
                .append(", deliveryNo=").append(this.deliveryNo)
                .append(", itemNo=").append(this.itemNo)
                .append(", materialNo=").append(this.materialNo)
                .append(", kanbanName=").append(this.kanbanName)
                .append(", modelName=").append(this.modelName)
                .append(", workName=").append(this.workName)
                .append(", personNo=").append(this.personNo)
                .append(", personName=").append(this.personName)
                .append(", assemblyDatetime=").append(this.assemblyDatetime)
                .append("}")
                .toString();
    }
}
