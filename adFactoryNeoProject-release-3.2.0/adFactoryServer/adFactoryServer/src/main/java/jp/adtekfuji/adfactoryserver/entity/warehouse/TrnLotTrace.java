/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.warehouse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * ロットトレース情報
 *
 * @author s-heya
 */
@Entity
@Table(name = "trn_lot_trace")
@XmlRootElement(name = "lotTrace")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
@NamedQueries({
    @NamedQuery(name = "TrnLotTrace.find", query = "SELECT t FROM TrnLotTrace t WHERE t.pk.deliveryNo = :deliveryNo AND t.pk.itemNo = :itemNo AND t.pk.materialNo = :materialNo AND t.pk.workKanbanId = :workKanbanId"),
    @NamedQuery(name = "TrnLotTrace.findByDeliveryNo", query = "SELECT t FROM TrnLotTrace t WHERE t.pk.deliveryNo = :deliveryNo AND t.assemblyDatetime IS NOT NULL"),
    @NamedQuery(name = "TrnLotTrace.findBySerialNo", query = "SELECT t FROM TrnLotTrace t WHERE t.serialNo = :serialNo AND t.assemblyDatetime IS NOT NULL"),
    @NamedQuery(name = "TrnLotTrace.findByPartsNo", query = "SELECT t FROM TrnLotTrace t WHERE t.partsNo = :partsNo AND t.assemblyDatetime IS NOT NULL"),
    @NamedQuery(name = "TrnLotTrace.findByPersonNo", query = "SELECT t FROM TrnLotTrace t WHERE t.personNo = :personNo AND t.assemblyDatetime IS NOT NULL"),
    @NamedQuery(name = "TrnLotTrace.findByWorkKanbanId", query = "SELECT t FROM TrnLotTrace t WHERE t.pk.workKanbanId = :workKanbanId"),
    @NamedQuery(name = "TrnLotTrace.deleteByDeliveryNo", query = "DELETE FROM TrnLotTrace t WHERE t.pk.deliveryNo = :deliveryNo AND t.assemblyDatetime IS NULL"),
    @NamedQuery(name = "TrnLotTrace.deleteWorkInfo", query = "DELETE FROM TrnLotTrace t WHERE t.pk.workKanbanId = :workKanbanId"),
})
public class TrnLotTrace implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected TrnLotTracePK pk;

    @Size(max = 32)
    @Column(name = "serial_no")
    private String serialNo;

    @Size(max = 32)
    @Column(name = "parts_no")
    private String partsNo;

    @Column(name = "trace_num")
    private Integer traceNum;

    @Size(max = 256)
    @Column(name = "person_no")
    private String personNo;

    @Column(name = "kanban_id")
    private Long kanbanId;

    @Column(name = "confirm")
    private Boolean confirm; // 確認

    @Column(name = "kanban_name")
    private String kanbanName; // カンバン名

    @Column(name = "model_name")
    private String modelName; // モデル名

    @Column(name = "work_name")
    private String workName; // 工程名

    @Column(name = "person_name")
    private String personName; // 作業者

    @Column(name = "assembly_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date assemblyDatetime; // 組付け日時

    @Column(name = "disabled")
    private Boolean disabled;

    @Basic(optional = false)
    //@NotNull
    @Column(name = "create_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(name = "update_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    @Version
    @Column(name = "ver_info")
    private Integer verInfo;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumns ({
        @JoinColumn(name="delivery_no", referencedColumnName = "delivery_no", insertable=false, updatable=false),
        @JoinColumn(name="item_no", referencedColumnName = "item_no", insertable=false, updatable=false)
    })
    private TrnDeliveryItem deliveryItem;

    /**
     * コンストラクタ
     */
    public TrnLotTrace() {
    }

    /**
     * コンストラクタ
     *
     * @param deliveryNo 出図番号
     * @param itemNo 明細番号
     * @param materialNo 資材番号
     * @param workKanbanId カンバンID
     * @param createDate 作成日時
     */
    public TrnLotTrace(String deliveryNo, int itemNo, String materialNo, Long workKanbanId, Date createDate) {
        this.pk = new TrnLotTracePK(deliveryNo, itemNo, materialNo, workKanbanId);
        this.createDate = createDate;
        this.disabled = false;
    }

    /**
     * 複合プライマリキーを取得する。
     *
     * @return 複合プライマリキー
     */
    public TrnLotTracePK getPk() {
        return this.pk;
    }

    /**
     * 製造番号を取得する。
     *
     * @return 製造番号
     */
    public String getSerialNo() {
        return this.serialNo;
    }

    /**
     * 製造番号を設定する。
     *
     * @param serialNo 製造番号
     */
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    /**
     * 部品番号を取得する。
     *
     * @return 部品番号
     */
    public String getPartsNo() {
        return this.partsNo;
    }

    /**
     * 部品番号を設定する。
     *
     * @param partsNo 部品番号
     */
    public void setPartsNo(String partsNo) {
        this.partsNo = partsNo;
    }

    /**
     * 数量を取得する。
     *
     * @return 数量
     */
    public Integer getTraceNum() {
        return this.traceNum;
    }

    /**
     * 数量を設定する。
     *
     * @param traceNum 数量
     */
    public void setTraceNum(Integer traceNum) {
        this.traceNum = traceNum;
    }

    /**
     * 社員番号を取得する。
     *
     * @return 社員番号
     */
    public String getPersonNo() {
        return this.personNo;
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
     * カンバンIDを取得する。
     *
     * @return カンバンID
     */
    public Long getKanbanId() {
        return this.kanbanId;
    }

    /**
     * カンバンIDを設定する。
     *
     * @param kanbanId カンバンID
     */
    public void setKanbanId(Long kanbanId) {
        this.kanbanId = kanbanId;
    }

    /**
     * 確認を取得する。
     *
     * @return 確認
     */
    public Boolean getConfirm() {
        return this.confirm;
    }

    /**
     * 確認を設定する。
     *
     * @param confirm 確認
     */
    public void setConfirm(Boolean confirm) {
        this.confirm = confirm;
    }

    /**
     * カンバン名を取得する。
     *
     * @return カンバン名
     */
    public String getKanbanName() {
        return this.kanbanName;
    }

    /**
     * カンバン名を設定する。
     *
     * @param kanbanName カンバン名
     */
    public void setKanbanName(String kanbanName) {
        this.kanbanName = kanbanName;
    }

    /**
     * モデル名を取得する。
     *
     * @return モデル名
     */
    public String getModelName() {
        return this.modelName;
    }

    /**
     * モデル名を設定する。
     *
     * @param modelName モデル名
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * 工程名を取得する。
     *
     * @return 工程名
     */
    public String getWorkName() {
        return this.workName;
    }

    /**
     * 工程名を設定する。
     *
     * @param workName 工程名
     */
    public void setWorkName(String workName) {
        this.workName = workName;
    }

    /**
     * 作業者を取得する。
     *
     * @return 作業者
     */
    public String getPersonName() {
        return this.personName;
    }

    /**
     * 作業者を設定する。
     *
     * @param personName 作業者
     */
    public void setPersonName(String personName) {
        this.personName = personName;
    }

    /**
     * 組付け日時を取得する。
     *
     * @return 組付け日時
     */
    public Date getAssemblyDatetime() {
        return this.assemblyDatetime;
    }

    /**
     * 組付け日時を設定する。
     *
     * @param assemblyDatetime 組付け日時
     */
    public void setAssemblyDatetime(Date assemblyDatetime) {
        this.assemblyDatetime = assemblyDatetime;
    }

    /**
     * 追跡無効を取得する。
     * 
     * @return 追跡無効
     */
    public Boolean getDisabled() {
        return disabled;
    }

    /**
     * 追跡無効を設定する。
     * 
     * @param disabled 追跡無効
     */
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * 作成日時を取得する。
     *
     * @return 作成日時
     */
    public Date getCreateDate() {
        return this.createDate;
    }

    /**
     * 作成日時を設定する。
     *
     * @param createDate 作成日時
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * 更新日時を取得する。
     *
     * @return 更新日時
     */
    public Date getUpdateDate() {
        return this.updateDate;
    }

    /**
     * 更新日時を設定する。
     *
     * @param updateDate 更新日時
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * 排他用バージョンを取得する。
     *
     * @return 排他用バージョン
     */
    public Integer getVerInfo() {
        return this.verInfo;
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
     * 出庫アイテム情報を取得する。
     *
     * @return 出庫アイテム情報
     */
    public TrnDeliveryItem getDeliveryItem() {
        return this.deliveryItem;
    }

    /**
     * 出庫アイテム情報を設定する。
     * 
     * @param deliveryItem 出庫アイテム情報
     */
    public void setDeliveryItem(TrnDeliveryItem deliveryItem) {
        this.deliveryItem = deliveryItem;
    }
    
    /**
     * 出庫番号を取得する。
     * 
     * @return 出庫番号
     */
    @XmlElement(name = "deliveryNo")
    public String getDeliveryNo() {
        if (Objects.isNull(this.pk)) {
            return null;
        }
        return this.pk.getDeliveryNo();
    }

    /**
     * 明細番号を取得する。
     * 
     * @return 明細番号
     */
    @XmlElement(name = "itemNo")
    public Integer getItemNo() {
        if (Objects.isNull(this.pk)) {
            return null;
        }
        return this.pk.getItemNo();
    }

    /**
     * 資材番号を取得する。
     * 
     * @return 資材番号
     */
    @XmlElement(name = "materialNo")
    public String getMaterialNo() {
        if (Objects.isNull(this.pk)) {
            return null;
        }
        return this.pk.getMaterialNo();
    }

    /**
     * ハッシュコードを返す。
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.pk != null ? this.pk.hashCode() : 0);
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
        if (!(object instanceof TrnLotTrace)) {
            return false;
        }
        TrnLotTrace other = (TrnLotTrace) object;
        return Objects.equals(this.pk, other.pk);
    }

    /**
     * 文字列表現を返す。
     *
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("TrnLotTrace{")
                .append(this.pk)
                .append("serialNo=").append(this.serialNo)
                .append(", partsNo=").append(this.partsNo)
                .append(", traceNum=").append(this.traceNum)
                .append(", personNo=").append(this.personNo)
                .append(", kanbanId=").append(this.kanbanId)
                .append(", confirm=").append(this.confirm)
                .append(", kanbanName=").append(this.kanbanName)
                .append(", modelName=").append(this.modelName)
                .append(", workName=").append(this.workName)
                .append(", personName=").append(this.personName)
                .append(", assemblyDatetime=").append(this.assemblyDatetime)
                .append(", disabled=").append(this.disabled)
                .append(", verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
