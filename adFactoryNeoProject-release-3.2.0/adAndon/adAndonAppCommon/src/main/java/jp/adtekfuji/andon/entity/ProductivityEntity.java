/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.entity;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 生産情報エンティティクラス
 *
 * @author s-heya
 */
@XmlRootElement(name = "productivity")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductivityEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;// 工程ID
    private Long prodCount;// 生産数

    /**
     * コンストラクタ
     */
    public ProductivityEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param id
     * @param prodCount 
     */
    public ProductivityEntity(long id, long prodCount) {
        this.id = id;
        this.prodCount = prodCount;
    }

    /**
     * 工程IDを取得する。
     *
     * @return 工程ID
     */
    public Long getId() {
        return this.id;
    }

    /**
     * 工程IDを設定する。
     *
     * @param id 工程ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 生産数を取得する。
     *
     * @return 生産数
     */
    public Long getProdCount() {
        return this.prodCount;
    }

    /**
     * 生産数を設定する。
     *
     * @param prodCount 生産数
     */
    public void setProdCount(Long prodCount) {
        this.prodCount = prodCount;
    }

    @Override
    public String toString() {
        return new StringBuilder("ProductivityEntity{")
                .append("id=").append(this.id)
                .append(", ")
                .append("prodCount=").append(this.prodCount)
                .append("}")
                .toString();
    }
}
