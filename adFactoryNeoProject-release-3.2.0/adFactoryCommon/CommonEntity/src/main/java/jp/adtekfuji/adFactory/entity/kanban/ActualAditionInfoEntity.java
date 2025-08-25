/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.kanban;

import java.io.Serializable;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

/**
 * 工程実績付加情報
 * 
 * @author HN)y-harada
 */
@XmlRootElement(name = "actualAdition")
@XmlAccessorType(XmlAccessType.FIELD)
public class ActualAditionInfoEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private Long actualAditionId;// 工程実績付加情報ID
    @XmlElement()
    private Long actualId;// 工程実績ID
    @XmlElement()
    private String dataName;// データ名
    @XmlElement()
    private String tag;// タグ

    @XmlTransient
    private byte[] rawData;// RAWデータ

    /**
     * コンストラクタ
     */
    public ActualAditionInfoEntity() {
    }


    /**
     * コンストラクタ
     * 
     * @param actualAditionId
     * @param actualId
     * @param dataName
     * @param tag
     */
    public ActualAditionInfoEntity(Long actualAditionId, Long actualId, String dataName, String tag) {
        this.actualAditionId = actualAditionId;
        this.actualId = actualId;
        this.dataName = dataName;
        this.tag = tag;
    }

    /**
     * 工程実績付加情報IDを取得する。
     * 
     * @return 
     */
    public Long getActualAditionId() {
        return this.actualAditionId;
    }

    /**
     * 工程実績付加情報IDを設定する。
     * 
     * @param actualAditionId 
     */
    public void setActualAditionId(Long actualAditionId) {
        this.actualAditionId = actualAditionId;
    }

    /**
     * 工程実績情報IDを取得する。
     * 
     * @return 
     */
    public long getActualId() {
        return this.actualId;
    }

    /**
     * 工程実績情報IDを設定する。
     * 
     * @param actualId 
     */
    public void setActualId(long actualId) {
        this.actualId = actualId;
    }

    /**
     * データ名を取得する。
     * 
     * @return 
     */
    public String getDataName() {
        return this.dataName;
    }

    /**
     * データ名を設定する。
     * 
     * @param dataName 
     */
    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

     /**
     * タグを取得する。
     *
     * @return
     */
    public String getTag() {
        return this.tag;
    }

    /**
     * タグを設定する。
     *
     * @param tag
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

     /**
     * ROWデータを取得する。
     *
     * @return
     */
    public byte[] getRawData() {
        return this.rawData;
    }

    /**
     * ROWデータを設定する。
     *
     * @param rawData
     */
    public void setRawData(byte[] rawData) {
        this.rawData = rawData;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.actualAditionId);
        return hash;
    }

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
        final ActualAditionInfoEntity other = (ActualAditionInfoEntity) obj;
        return Objects.equals(this.actualAditionId, other.actualAditionId);
    }

    @Override
    public String toString() {
        return new StringBuilder("ActualAditionInfoEntity{")
                .append("actualAditionId=").append(this.actualAditionId)
                .append(", actualId=").append(this.actualId)
                .append(", dataName=").append(this.dataName)
                .append(", tag=").append(this.tag)
                .append("}").toString();
    }
}
