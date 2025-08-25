/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.warehouse;

import jakarta.persistence.Basic;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * 在庫引当
 * 
 * @author s-heya
 */
@Entity
@Table(name = "trn_reserve_material")
@XmlRootElement(name = "reserveMaterial")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "TrnReserveMaterial.findByDeliveryNo", query = "SELECT t FROM TrnReserveMaterial t LEFT OUTER JOIN FETCH t.material WHERE t.pk.deliveryNo = :deliveryNo"),
    @NamedQuery(name = "TrnReserveMaterial.findByItemNo", query = "SELECT t FROM TrnReserveMaterial t LEFT OUTER JOIN FETCH t.material WHERE t.pk.deliveryNo = :deliveryNo AND t.pk.itemNo = :itemNo"),
    @NamedQuery(name = "TrnReserveMaterial.find", query = "SELECT t FROM TrnReserveMaterial t LEFT OUTER JOIN FETCH t.material WHERE t.pk.deliveryNo = :deliveryNo AND t.pk.itemNo = :itemNo AND t.pk.materialNo = :materialNo"),
    @NamedQuery(name = "TrnReserveMaterial.deleteAll", query = "DELETE FROM TrnReserveMaterial t  WHERE t.pk.deliveryNo IN :deliveryNo"),
    @NamedQuery(name = "TrnReserveMaterial.deleteByItemNo", query = "DELETE FROM TrnReserveMaterial t  WHERE t.pk.deliveryNo = :deliveryNo AND t.pk.itemNo = :itemNo"),
    @NamedQuery(name = "TrnReserveMaterial.sumByItemNo", query = "SELECT SUM(t.reservedNum) FROM TrnReserveMaterial t WHERE t.pk.deliveryNo = :deliveryNo AND t.pk.itemNo = :itemNo"),
    @NamedQuery(name = "TrnReserveMaterial.countByMaterialNo", query = "SELECT COUNT(t.pk) FROM TrnReserveMaterial t WHERE t.pk.materialNo = :materialNo"),
    @NamedQuery(name = "TrnReserveMaterial.findByMaterialNo", query = "SELECT t FROM TrnReserveMaterial t WHERE t.pk.materialNo = :materialNo ORDER BY t.pk.deliveryNo, t.pk.itemNo"),
})
@Cacheable(false)
public class TrnReserveMaterial implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected TrnReserveMaterialPK pk;

    @Basic(optional = false)
    //@NotNull
    @Column(name = "reserved_num")
    private int reservedNum;

    @Basic(optional = false)
    //@NotNull
    @Column(name = "reserved_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reservedAt;

    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 256)
    @Column(name = "person_no")
    private String personNo;

    @Column(name = "delivery_num")
    private Integer deliveryNum;

    @Column(name = "note")
    private String note;

    @Version
    @Column(name = "ver_info")
    private Integer verInfo;

    // 親 TrnDeliveryItem → 子 TrnReserveMaterial (双方向)
    @ManyToOne
    @JoinColumn(name = "material_no", referencedColumnName = "material_no", insertable = false, updatable = false, nullable = true)
    private TrnMaterial material;

    @Transient
    @XmlElement
    private String deliveryNo;

    @Transient
    @XmlElement
    private Integer itemNo;

    @Transient
    @XmlElement
    private String materialNo;

    
    /**
     * コンストラクタ
     */
    public TrnReserveMaterial() {
    }

    /**
     * コンストラクタ
     * 
     * @param pk 
     */
    public TrnReserveMaterial(TrnReserveMaterialPK pk) {
        this.pk = pk;
    }

    /**
     * コンストラクタ
     * 
     * @param deliveryNo
     * @param itemNo
     * @param materialNo
     * @param reservedNum
     * @param reservedAt
     * @param personNo 
     */
    public TrnReserveMaterial(String deliveryNo, int itemNo, String materialNo, int reservedNum, Date reservedAt, String personNo) {
        this.pk = new TrnReserveMaterialPK(deliveryNo, itemNo, materialNo);
        this.reservedNum = reservedNum;
        this.reservedAt = reservedAt;
        this.personNo = personNo;
        this.deliveryNum = 0;
    }

    /**
     * コンストラクタ
     * 
     * @param pk
     * @param reservedNum
     * @param reservedAt
     * @param personNo 
     */
    public TrnReserveMaterial(TrnReserveMaterialPK pk, int reservedNum, Date reservedAt, String personNo) {
        this.pk = pk;
        this.reservedNum = reservedNum;
        this.reservedAt = reservedAt;
        this.personNo = personNo;
    }
    
    /**
     * 複合プライマリキーを取得する。
     *
     * @return 複合プライマリキー
     */
    public TrnReserveMaterialPK getPK() {
        return pk;
    }

    /**
     * 引当数を取得する。
     * 
     * @return 引当数
     */
    public int getReservedNum() {
        return reservedNum;
    }

    /**
     * 引当数を設定する。
     * 
     * @param reservedNum 引当数 
     */
    public void setReservedNum(int reservedNum) {
        this.reservedNum = reservedNum;
    }

    /**
     * 引当日時を取得する。
     * 
     * @return 引当日時 
     */
    public Date getReservedAt() {
        return reservedAt;
    }

    /**
     * 引当日時を設定する。
     * 
     * @param reservedAt 引当日時
     */
    public void setReservedAt(Date reservedAt) {
        this.reservedAt = reservedAt;
    }

    /**
     * 社員番号を取得する。
     * 
     * @return 社員番号
     */
    public String getPersonNo() {
        return personNo;
    }

    /**
     * 社員番号を設定する。
     * 
     * @param personNo 社員番号
     */
    public void setPersonNo(String personNo) {
        this.personNo = personNo;
    }

    /**
     * 出庫数を取得する。
     * 
     * @return 出庫数
     */
    public Integer getDeliveryNum() {
        return deliveryNum;
    }

    /**
     * 出庫数を設定する。
     * 
     * @param deliveryNum 出庫数 
     */
    public void setDeliveryNum(Integer deliveryNum) {
        this.deliveryNum = deliveryNum;
    }

    /**
     * コメントを取得する。
     * 
     * @return コメント
     */
    public String getNote() {
        return note;
    }

    /**
     * コメントを設定する。
     * 
     * @param note コメント
     */
    public void setNote(String note) {
        this.note = note;
    }
    
    /**
     * 排他用バージョン
     * 
     * @return 排他用バージョン
     */
    public Integer getVerInfo() {
        return verInfo;
    }

    /**
     * 排他用バージョンを設定する。
     *
     * @param verInfo 排他用バージョン
     */
    public void setVerInfo(Integer verInfo) {
        this.verInfo = verInfo;
    }

    /**
     * 資材情報を取得する。
     * 
     * @return 資材情報
     */
    public TrnMaterial getMaterial() {
        return material;
    }

    /**
     * 資材情報を設定する。
     * 
     * @param material 資材情報
     */
    public void setMaterial(TrnMaterial material) {
        this.material = material;
    }

    public String getDeliveryNo() {
        return deliveryNo;
    }

    public Integer getItemNo() {
        return itemNo;
    }

    public String getMaterialNo() {
        return materialNo;
    }

    /**
     * ハッシュコードを返す。
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pk != null ? pk.hashCode() : 0);
        return hash;
    }

    /**
     * オブジェクトを比較する。
     * 
     * @param object オブジェクト
     * @return true:同じである、false:異なる
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TrnReserveMaterial)) {
            return false;
        }
        TrnReserveMaterial other = (TrnReserveMaterial) object;
        return Objects.equals(this.pk, other.pk);
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("TrnReserveMaterial{")
            .append("deliveryNo=").append(this.pk.getDeliveryNo())
            .append(", itemNo=").append(this.pk.getItemNo())
            .append(", materialNo=").append(this.pk.getMaterialNo())
            .append(", reservedNum=").append(this.reservedNum)
            .append(", reservedAt=").append(this.reservedAt)
            .append(", personNo=").append(this.personNo)
            .append(", deliveryNum=").append(this.deliveryNum)
            .append("}")
            .toString();
    }
    
}
