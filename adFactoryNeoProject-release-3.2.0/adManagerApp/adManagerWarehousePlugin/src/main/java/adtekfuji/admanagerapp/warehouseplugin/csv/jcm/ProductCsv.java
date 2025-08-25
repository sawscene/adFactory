/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.csv.jcm;

import static adtekfuji.admanagerapp.warehouseplugin.csv.jcm.OperationCsvFactory.FORMAT_DATE;
import com.opencsv.bean.CsvBindByPosition;
import java.util.Comparator;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.warehouse.LogStockInfo;

/**
 * 完成実績
 * 
 * @author s-heya
 */
public class ProductCsv {

    @CsvBindByPosition(position = 0)
    private String productNo;
    @CsvBindByPosition(position = 1)
    private String date;
    @CsvBindByPosition(position = 2)
    private Integer productQty;
    @CsvBindByPosition(position = 3)
    private String serialNo;

    public static final Comparator<ProductCsv> productNoComparator = (p1, p2) -> {
        String value1 = Objects.isNull(p1.getProductNo()) ? "" : p1.getProductNo();
        String value2 = Objects.isNull(p2.getProductNo()) ? "" : p2.getProductNo();
        return value1.compareTo(value2);
    };
    
    public static final Comparator<ProductCsv> dateComparator = (p1, p2) -> {
        String value1 = Objects.isNull(p1.getDate()) ? "" : p1.getDate();
        String value2 = Objects.isNull(p2.getDate()) ? "" : p2.getDate();
        return value1.compareTo(value2);
    };

    /**
     * コンストラクタ
     * 
     * @param log 
     */
    public ProductCsv(LogStockInfo log) {
        productNo = log.getProductNo();
        date = log.getEventDate().format(FORMAT_DATE);
        productQty = log.getEventNum();
        serialNo = log.getSerialNo();
    }

    public String getProductNo() {
        return productNo;
    }

    public String getDate() {
        return date;
    }

    public Integer getProductQty() {
        return productQty;
    }

    public String getSerialNo() {
        return serialNo;
    }

}
