/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.csv.jcm;

import com.opencsv.bean.CsvBindByPosition;
import java.util.Comparator;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.warehouse.TrnMaterialInfo;

/**
 * 在庫リスト
 * 
 * @author s-heya
 */
public class InventoryCsv {
    @CsvBindByPosition(position = 0)
    private String locationNo;
    @CsvBindByPosition(position = 1)
    private String productNo;
    @CsvBindByPosition(position = 2)
    private Integer receiptQty;
    @CsvBindByPosition(position = 3)
    private String partsNo;

    public static final Comparator<InventoryCsv> locationNoComparator = (p1, p2) -> {
        String value1 = Objects.isNull(p1.getLocationNo()) ? "" : p1.getLocationNo();
        String value2 = Objects.isNull(p2.getLocationNo()) ? "" : p2.getLocationNo();
        return value1.compareTo(value2);
    };

    public static final Comparator<InventoryCsv> partsNoComparator = (p1, p2) -> {
        String value1 = Objects.isNull(p1.getPartsNo()) ? "" : p1.getPartsNo();
        String value2 = Objects.isNull(p2.getPartsNo()) ? "" : p2.getPartsNo();
        return value1.compareTo(value2);
    };

    /**
     * コンストラクタ
     * 
     * @param material 
     */
    public InventoryCsv(TrnMaterialInfo material) {
        this.locationNo = material.getLocation().getLocationNo();
        this.productNo = material.getProduct().getProductNo();
        this.receiptQty = material.getInStockNum();
        this.partsNo = material.getPartsNo();
    }

    public String getLocationNo() {
        return locationNo;
    }

    public String getProductNo() {
        return productNo;
    }

    public Integer getReceiptQty() {
        return receiptQty;
    }

    public String getPartsNo() {
        return partsNo;
    }
  
    
}
