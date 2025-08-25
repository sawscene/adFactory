/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.warehouse;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 有効在庫情報
 * 
 * @author s-heya
 */
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {
    "material_no",
    "supply_no",
    "order_no",
    "arrival_num",
    "stock_num",
    "delivery_num",
    "in_stock_num",
    "reserved_num",
    "reservation_num",
    "note"
}))
@NamedNativeQueries({
    @NamedNativeQuery(name = "AvailableInventoryEntity.find",
            query = "SELECT o.material_no, o.supply_no, o.order_no, o.arrival_num, o.stock_num, r2.delivery_num, o.in_stock_num, r1.reserved_num, r2.reservation_num, r2.note FROM trn_material as o LEFT JOIN mst_location l ON l.location_id = o.location_id LEFT JOIN mst_product p ON p.product_id = o.product_id LEFT JOIN (SELECT r.material_no, SUM(r.reserved_num) as reserved_num FROM trn_reserve_material r GROUP BY r.material_no) as r1 ON r1.material_no = o.material_no LEFT JOIN (SELECT r.material_no, r.delivery_no, r.item_no, r.reserved_num as reservation_num, r.delivery_num, r.note FROM trn_reserve_material r) as r2 ON r2.material_no = o.material_no AND r2.delivery_no = ?1 AND r2.item_no = ?2 WHERE p.product_id = ?3 AND ((l.area_name = ?4 AND ((r1.material_no IS NULL AND o.in_stock_num > 0) OR o.in_stock_num - r1.reserved_num > 0 OR o.arrival_num > o.stock_num OR r2.reservation_num > 0)) OR (l IS NULL AND o.stock_num = 0)) ORDER BY o.material_no;",
            resultClass = AvailableInventoryEntity.class)})
@Entity
@XmlRootElement(name = "availableInventory")
@XmlAccessorType(XmlAccessType.FIELD)
public class AvailableInventoryEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "material_no")
    private String materialNo;

    @Column(name = "supply_no")
    private String supplyNo;

    @Column(name = "order_no")
    private String orderNo;

    @Column(name = "arrival_num")
    private Integer arrivalNum;

    @Column(name = "stock_num")
    private Integer stockNum;

    @Column(name = "delivery_num")
    private Integer deliveryNum;

    @Column(name = "in_stock_num")
    private Integer inStockNum;
    
    @Column(name = "reserved_num")
    private Integer reservedNum;
    
    @Column(name = "reservation_num")
    private Integer reservationNum;

    @Column(name = "note")
    private String note;

    /**
     * ハッシュコードを返す。
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.materialNo);
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
        final AvailableInventoryEntity other = (AvailableInventoryEntity) obj;
        return Objects.equals(this.materialNo, other.materialNo);
    }
    
    /**
     * 文字列表現を返す。
     *
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("TrnMaterial{")
                .append("materialNo=").append(this.materialNo)
                .append(", supplyNo=").append(this.supplyNo)
                .append(", orderNo=").append(this.orderNo)
                .append(", arrivalNum=").append(this.arrivalNum)
                .append(", stockNum=").append(this.stockNum)
                .append(", deliveryNum=").append(this.deliveryNum)
                .append(", inStockNum=").append(this.inStockNum)
                .append(", reservedNum=").append(this.reservedNum)
                .append(", reservationNum=").append(this.reservationNum)
                .append(", note=").append(this.note)
                .append("}").toString();
    }

}