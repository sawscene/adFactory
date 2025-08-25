/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.entity;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 不具合情報エンティティクラス
 *
 * @author s-heya
 */
@XmlRootElement(name = "defect")
@XmlAccessorType(XmlAccessType.FIELD)
public class DefectEntity {

    private Long id;// 工程ID
    private Long defectCount;// 不具合数
    private Long prodCount;// 生産数

    /**
     * コンストラクタ
     */
    public DefectEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param id 工程ID
     * @param defectCount 不具合数
     */
    public DefectEntity(Long id, Long defectCount) {
        this.id = id;
        this.defectCount = defectCount;
        this.prodCount = 0L;
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
     * 不具合数を取得する。
     *
     * @return 不具合数
     */
    public Long getDefectCount() {
        return this.defectCount;
    }

    /**
     * 不具合数を設定する。
     *
     * @param defectCount 不具合数
     */
    public void setDefectCount(Long defectCount) {
        this.defectCount = defectCount;
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
        return new StringBuilder("DefectEntity{")
                .append("id=").append(this.id)
                .append(", ")
                .append("defectCount=").append(this.defectCount)
                .append(", ")
                .append("prodCount=").append(this.prodCount)
                .append("}")
                .toString();
    }
}
