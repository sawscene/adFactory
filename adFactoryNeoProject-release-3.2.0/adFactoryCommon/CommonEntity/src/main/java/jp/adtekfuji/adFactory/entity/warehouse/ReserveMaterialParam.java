/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.warehouse;

import java.io.Serializable;
import java.util.List;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

/**
 *
 * @author s-heya
 */
public class ReserveMaterialParam  implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @XmlElementWrapper(name = "reserveMaterials")
    @XmlElement(name = "reserveMaterial")
    private List<TrnReserveMaterialInfo> reserveMaterials;

    /**
     * コンストラクタ
     * 
     * @param reserveMaterials 
     */
    public ReserveMaterialParam(List<TrnReserveMaterialInfo> reserveMaterials) {
        this.reserveMaterials = reserveMaterials;
    }

    public List<TrnReserveMaterialInfo> getReserveMaterials() {
        return reserveMaterials;
    }

    public void setReserveMaterials(List<TrnReserveMaterialInfo> reserveMaterials) {
        this.reserveMaterials = reserveMaterials;
    }
    
}
