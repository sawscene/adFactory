/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.actual;

import java.io.Serializable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 工程実績情報
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "trn_actual_adition")
@XmlRootElement(name = "actualAdition")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedNativeQueries({})
@NamedQueries({
        // カンバンID・タグを指定して、工程実績付加情報を取得する。(タグは大文字で検索)
        @NamedQuery(name = "ActualAditionEntity.findByTag", query = "SELECT ad FROM ActualAditionEntity ad JOIN ActualResultEntity a ON ad.actualId = a.actualId WHERE a.kanbanId = :kanbanId AND UPPER(ad.tag) = UPPER(:tag) ORDER BY a.implementDatetime DESC, a.actualId DESC, ad.actualAditionId DESC"),
        // カンバンIDを指定して、工程実績付加情報を削除する。
        @NamedQuery(name = "ActualAditionEntity.removeByKanbanId", query = "DELETE FROM ActualAditionEntity ad WHERE ad.actualId IN (SELECT a.actualId FROM ActualResultEntity a WHERE a.kanbanId = :kanbanId)"),
        // 工程実績IDを指定して、情報を取得する
        @NamedQuery(name = "ActualAditionEntity.findByActualID", query = "SELECT ad FROM ActualAditionEntity ad WHERE ad.actualId = :actualID AND ad.tag = :tag ORDER BY ad.actualAditionId"),
        // 工程実績IDを指定して、情報を取得する(画像無し)
        @NamedQuery(name = "ActualAditionEntity.findByActualIDWithoutImage", query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.actual.ActualAditionEntity(ad.actualAditionId, ad.actualId, ad.tag) FROM ActualAditionEntity ad WHERE ad.actualId IN :actualID"),
})
public class ActualAditionEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "actual_adition_id")
    private Long actualAditionId;// 工程実績付加情報ID

    @Basic(optional = false)
    @Column(name = "actual_id")
    @XmlElement(name = "actualId")
    private Long actualId;// 工程実績ID

    @Size(max = 256)
    @Basic(optional = false)
    //@NotNull
    @Column(name = "data_name")
    @XmlElement(name = "dataName")
    private String dataName;// データ名

    @Size(max = 256)
    @Basic(optional = false)
    //@NotNull
    @Column(name = "tag")
    @XmlElement(name = "tag")
    private String tag;// タグ

    @Basic(optional = false)
    @Column(name = "raw_data")
    private byte[] rawData;// RAWデータ

    /**
     * コンストラクタ
     */
    public ActualAditionEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param actualId 工程実績ID
     * @param dataName データ名
     * @param tag タグ
     * @param rawData RAWデータ
     */
    public ActualAditionEntity(Long actualId, String dataName, String tag, byte[] rawData) {
        this.actualId = actualId;
        this.dataName = dataName;
        this.tag = tag;
        this.rawData = rawData;
    }

    /**
     * コンストラクタ
     * 
     * @param actualAditionId 工程実績付加情報ID
     * @param actualId 工程実績ID
     * @param tag タグ
     */
    public ActualAditionEntity(Long actualAditionId, Long actualId, String tag) {
        this.actualId = actualId;
        this.actualAditionId = actualAditionId;
        this.dataName = null;
        this.tag = tag;
        this.rawData = null;
    }

    /**
     * 工程実績付加情報IDを取得する。
     *
     * @return 工程実績ID
     */
    public Long getActualAditionId() {
        return this.actualAditionId;
    }

    /**
     * 工程実績付加情報IDを設定する。
     *
     * @param actualAditionId 工程実績付加情報ID
     */
    public void setActualAditionId(Long actualAditionId) {
        this.actualAditionId = actualAditionId;
    }

    /**
     * 工程実績IDを取得する。
     *
     * @return 工程実績ID
     */
    public Long getActualId() {
        return this.actualId;
    }

    /**
     * 工程実績IDを設定する。
     *
     * @param actualId 工程実績ID
     */
    public void setActualId(Long actualId) {
        this.actualId = actualId;
    }

    /**
     * データ名を取得する。
     *
     * @return データ名
     */
    public String getDataName() {
        return this.dataName;
    }

    /**
     * データ名を設定する。
     *
     * @param dataName データ名
     */
    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    /**
     * タグを取得する。
     *
     * @return タグ
     */
    public String getTag() {
        return this.tag;
    }

    /**
     * タグを設定する。
     *
     * @param tag タグ
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * RAWデータを取得する。
     *
     * @return RAWデータ
     */
    public byte[] getRawData() {
        return this.rawData;
    }

    /**
     * RAWデータを設定する。
     *
     * @param rawData RAWデータ
     */
    public void setRawData(byte[] rawData) {
        this.rawData = rawData;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.actualAditionId != null ? actualAditionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ActualAditionEntity)) {
            return false;
        }
        ActualAditionEntity other = (ActualAditionEntity) object;
        if ((this.actualAditionId == null && other.actualAditionId != null) || (this.actualAditionId != null && !this.actualAditionId.equals(other.actualAditionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ActualAditionEntity{")
                .append("actualAditionId=").append(this.actualAditionId)
                .append(", actualId=").append(this.actualId)
                .append(", dataName=").append(this.dataName)
                .append(", tag=").append(this.tag)
                .append("}")
                .toString();
    }
}
