/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.kanban;

import java.io.Serializable;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 完成情報削除条件
 * 
 * @author s-heya
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "partsRemoveCondition")
public class PartsRemoveCondition implements Serializable {

    private static final long serialVersionUID = 1L;
            
    @XmlElementWrapper(name = "items")
    @XmlElement(name = "item")
    private List<PartsInfoEntity> items;

    /**
     * コンストラクタ
     */
    public PartsRemoveCondition() {
    }

    /**
     * コンストラクタ
     * 
     * @param items 削除対象の完成情報
     */
    public PartsRemoveCondition(List<PartsInfoEntity> items) {
        this.items = items;
    }

    /**
     * 削除対象となる完成情報を取得する。
     * 
     * @return 完成情報
     */
    public List<PartsInfoEntity> getItems() {
        return items;
    }

    /**
     * 削除対象となる完成情報を設定する。
     * 
     * @param items 完成情報
     */
    public void setItems(List<PartsInfoEntity> items) {
        this.items = items;
    }
    
    
}
