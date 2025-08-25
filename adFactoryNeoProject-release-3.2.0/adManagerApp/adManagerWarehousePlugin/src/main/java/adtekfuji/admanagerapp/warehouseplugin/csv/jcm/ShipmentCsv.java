/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.csv.jcm;

import static adtekfuji.admanagerapp.warehouseplugin.csv.jcm.OperationCsvFactory.FORMAT_DATE;
import com.opencsv.bean.CsvBindByPosition;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;

/**
 * 出荷実績
 * 
 * @author s-heya
 */
public class ShipmentCsv {
    @CsvBindByPosition(position = 0)
    private String productNo;
    @CsvBindByPosition(position = 1)
    private Integer shipmentQty;
    @CsvBindByPosition(position = 2)
    private String date;

    public static final Comparator<ShipmentCsv> productNoComparator = (p1, p2) -> {
        String value1 = Objects.isNull(p1.getProductNo()) ? "" : p1.getProductNo();
        String value2 = Objects.isNull(p2.getProductNo()) ? "" : p2.getProductNo();
        return value1.compareTo(value2);
    };
    
    public static final Comparator<ShipmentCsv> dateComparator = (p1, p2) -> {
        String value1 = Objects.isNull(p1.getDate()) ? "" : p1.getDate();
        String value2 = Objects.isNull(p2.getDate()) ? "" : p2.getDate();
        return value1.compareTo(value2);
    };

    /**
     * コンストラクタ
     * 
     * @param productNo
     * @param shipmentQty
     * @param date 
     */
    public ShipmentCsv(String productNo, Integer shipmentQty, LocalDateTime date) {
        this.productNo = productNo;
        this.shipmentQty = shipmentQty;
        this.date = date.format(FORMAT_DATE);
    }

    public String getProductNo() {
        return productNo;
    }

    public Integer getShipmentQty() {
        return shipmentQty;
    }

    public String getDate() {
        return date;
    }
}
