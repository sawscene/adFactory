package jp.adtekfuji.adfactoryserver.entity.operation;

import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jp.adtekfuji.adFactory.entity.operation.OperateAppEnum;
import jp.adtekfuji.adFactory.entity.operation.OperationTypeEnum;
import jp.adtekfuji.adfactoryserver.utility.JsonUtils;
import jp.adtekfuji.adfactoryserver.utility.PgJsonbConverter;

/**
 * 作業者操作実績
 *
 * @author yu.nara
 */
@Entity
@Table(name = "trn_operation")
@XmlRootElement(name = "operation")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown=true)
@NamedNativeQueries({
        @NamedNativeQuery(name = "OperationEntity.findCallPair",
                query = "SELECT CASE WHEN (CASE ?4 WHEN 'Call' THEN (o.add_info -> 'call') -> 'call' WHEN 'IndirectWork' THEN (o.add_info -> 'indirect') -> 'indirect' ELSE 'false' END)::BOOL THEN o.operation_id ELSE -1 END FROM trn_operation o WHERE o.operate_app = ?1 AND o.operation_type = ?2 AND o.equipment_id = ?3 ORDER BY o.operate_datetime DESC LIMIT(1)"),
        @NamedNativeQuery(name = "OperationEntity.countActiveIndirectWork",
                query = "WITH top AS (SELECT operation_id, jsonb_extract_path(add_info, 'indirectWork', 'doIndirect')::BOOLEAN do_indirect, jsonb_extract_path(add_info, 'indirectWork', 'pairId')::INTEGER pair_id FROM trn_operation WHERE operation_type = 'IndirectWork' AND operate_app = 'adProductLite' AND jsonb_extract_path(add_info, 'indirectWork', 'indirectWorkId')::INTEGER = ?1) SELECT COUNT(*) FROM top t1 WHERE t1.do_indirect AND NOT EXISTS( SELECT * FROM top t2 WHERE t2.pair_id = t1.operation_id)"),
})
@NamedQueries({
    @NamedQuery(name = "OperationEntity.findFinish", query = "SELECT o FROM OperationEntity o WHERE o.organizationId IN :organizationIds AND o.operationType = 'Finish'"),
})
public class OperationEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "operation_id")
    @XmlElement(name = "operationId")
    private Long operationId;   //操作ID

    @Basic(optional = false)
    //@NotNull
    @Column(name = "operate_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlElement(name = "operateDatetime")
    @JsonIgnore
    private Date operateDatetime;// 操作時間
    
    @Transient
    @XmlElement(name = "reportDate")
    @JsonProperty("reportDate")
    private String reportDate;
    
    @Basic(optional = false)
    //@NotNull
    @Column(name = "equipment_id")
    @XmlElement(name = "equipmentId")
    private Long equipmentId;// 設備ID

    @Basic(optional = false)
    //@NotNull
    @Column(name = "organization_id")
    @XmlElement(name = "organizationId")
    private Long organizationId;// 組織ID

    @Basic(optional = false)
    //@NotNull
    @Column(name = "operate_app")
    @XmlElement(name = "operateApp")
    private String operateApp;// 操作アプリ

    @Basic(optional = false)
    //@NotNull
    @Column(name = "operation_type")
    @XmlElement(name = "operationType")
    private String operationType;// 操作タイプ

    @Basic(optional = false)
    //@NotNull
    @Column(name = "add_info")
    @XmlElement(name = "addInfo")
    @Convert(converter = PgJsonbConverter.class)
    private String addInfo;// 追加情報
    
    @Transient
    @XmlTransient()
    private Long workTime; // 作業時間

    /**
     * コンストラクタ
     * 
     * @param operateDatetime
     * @param equipmentId
     * @param organizationId
     * @param operateApp
     * @param operationType
     * @param addInfo 
     */
    public OperationEntity(Date operateDatetime, Long equipmentId, Long organizationId, OperateAppEnum operateApp, OperationTypeEnum operationType, OperationAddInfoEntity addInfo) {
        this.operateDatetime = operateDatetime;
        this.equipmentId = equipmentId;
        this.organizationId = organizationId;
        this.operateApp = operateApp.getName();
        this.operationType = operationType.getName();
        this.addInfo = JsonUtils.objectToJson(addInfo);
    }

    public OperationEntity() {

    }

    /**
     * 操作ID取得
     *
     * @return 操作ID
     */
    public Long getOperationId() {
        return operationId;
    }

    /**
     * 操作ID設定
     *
     * @param operationId 　操作ID
     */
    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }

    /**
     * 操作時間取得
     *
     * @return 操作時間
     */
    public Date getOperateDatetime() {
        if (Objects.isNull(this.operateDatetime) && !StringUtils.isEmpty(this.reportDate)) {
            this.operateDatetime = DateUtils.parseJson(this.reportDate);
        }
        return operateDatetime;
    }

    /**
     * 実施日時を設定する。(adProductWeb Only)
     * 
     * @return 実施日時
     */
    public String getReportDate() {
        return reportDate;
    }

    /**
     * 実施日時を取得する。(adProductWeb Only)
     * 
     * @param reportDate 実施日時
     */
    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    /**
     * 操作時間設定
     *
     * @param operateDatetime 　操作時間
     */
    public void setOperateDatetime(Date operateDatetime) {
        this.operateDatetime = operateDatetime;
    }

    /**
     * 設備ID取得
     *
     * @return 設備ID
     */
    public Long getEquipmentId() {
        return equipmentId;
    }

    /**
     * 設備ID設定
     *
     * @param equipmentId 設備ID
     */
    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    /**
     * 組織ID取得
     *
     * @return 設備ID
     */
    public Long getOrganizationId() {
        return organizationId;
    }

    /**
     * 組織ID
     *
     * @param organizationId 組織ID
     */
    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * 操作アプリ取得
     *
     * @return 操作アプリ
     */
    public OperateAppEnum getOperateApp() {
        return OperateAppEnum.toEnum(this.operateApp);
    }

    /**
     * 操作アプリ設定
     *
     * @param operateApp 操作アプリ設定
     */
    public void setOperateApp(OperateAppEnum operateApp) {
        this.operateApp = operateApp.getName();
    }

    /**
     * 操作タイプ取得
     *
     * @return 操作タイプ
     */
    public OperationTypeEnum getOperationType() {
        return OperationTypeEnum.toEnum(operationType);
    }

    /**
     * 操作タイプ設定
     *
     * @param operationType 操作タイプ設定
     */
    public void setOperationType(OperationTypeEnum operationType) {
        this.operationType = operationType.getName();
    }

    /**
     * 追加情報取得
     *
     * @return 追加情報
     */
    public OperationAddInfoEntity getAddInfo() {
        return JsonUtils.jsonToObject(addInfo, OperationAddInfoEntity.class);
    }

    /**
     * 追加情報取得
     *
     * @param addInfo 追加情報
     */
    public void setAddInfo(OperationAddInfoEntity addInfo) {
        this.addInfo = JsonUtils.objectToJson(addInfo);
    }

    /**
     * 作業時間を取得する。
     * 
     * @return 作業時間 
     */
    public Long getWorkTime() {
        return workTime;
    }

    /**
     * 作業時間を設定する。
     * 
     * @param workTime 作業時間
     */
    public void setWorkTime(Long workTime) {
        this.workTime = workTime;
    }

    /**
     * ハッシュ値を返す。
     *
     * @return ハッシュ値
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.operationId != null ? this.operationId.hashCode() : 0);
        return hash;
    }

    /**
     * オブジェクトを比較する。
     *
     * @param object オブジェクト
     * @return true:オブジェクトが一致、false:オブジェクトが不一致
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        OperationEntity that = (OperationEntity) object;
        return operationId.equals(that.operationId);
    }

    /**
     * 文字列表現を返す。
     *
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return "OperationEntity{" +
                "operationId=" + operationId +
                ", operateDatetime=" + operateDatetime +
                ", reportDate=" + reportDate +
                ", equipmentId=" + equipmentId +
                ", organizationId=" + organizationId +
                ", operateApp='" + operateApp + '\'' +
                ", operationType='" + operationType + '\'' +
                ", addInfo='" + addInfo + '\'' +
                '}';
    }
}
