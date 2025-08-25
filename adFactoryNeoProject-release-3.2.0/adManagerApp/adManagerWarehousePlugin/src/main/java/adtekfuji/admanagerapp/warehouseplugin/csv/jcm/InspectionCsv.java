/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.csv.jcm;

import com.opencsv.bean.CsvBindByPosition;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.warehouse.TrnMaterialInfo;

/**
 * 受入検査実績
 * 
 * @author s-heya
 */
public class InspectionCsv {

    @CsvBindByPosition(position = 0)
    private String supplyNo;
    @CsvBindByPosition(position = 1)
    private Integer itemNo;
    @CsvBindByPosition(position = 2)
    private String productNo;
    @CsvBindByPosition(position = 3)
    private Integer receiptQty;
    @CsvBindByPosition(position = 4)
    private String date;
    @CsvBindByPosition(position = 5)
    private String partsNo;
    @CsvBindByPosition(position = 6)
    private Integer defectQty;

    public static final Comparator<InspectionCsv> supplyNoComparator = (p1, p2) -> {
        String value1 = Objects.isNull(p1.getSupplyNo()) ? "" : p1.getSupplyNo();
        String value2 = Objects.isNull(p2.getSupplyNo()) ? "" : p2.getSupplyNo();
        return value1.compareTo(value2);
    };
    
    public static final Comparator<InspectionCsv> dateComparator = (p1, p2) -> {
        String value1 = Objects.isNull(p1.getDate()) ? "" : p1.getDate();
        String value2 = Objects.isNull(p2.getDate()) ? "" : p2.getDate();
        return value1.compareTo(value2);
    };
    
    /**
     * コンストラクタ
     * 
     * @param material 
     */
    public InspectionCsv(TrnMaterialInfo material) {
        this.supplyNo = material.getSupplyNo();
        this.itemNo = material.getItemNo();
        this.productNo = material.getProduct().getProductNo();
        this.receiptQty = material.getStockNum() + material.getDefectNum();
        this.date = new SimpleDateFormat("yyyy/MM/dd").format(material.getInspectedAt());
        this.partsNo = material.getPartsNo();
        this.defectQty = material.getDefectNum();
    }

    public String getSupplyNo() {
        return supplyNo;
    }

    public Integer getItemNo() {
        return itemNo;
    }

    public String getProductNo() {
        return productNo;
    }

    public Integer getReceiptQty() {
        return receiptQty;
    }

    public String getDate() {
        return date;
    }

    public String getPartsNo() {
        return partsNo;
    }

    public Integer getDefectQty() {
        return defectQty;
    }


}
