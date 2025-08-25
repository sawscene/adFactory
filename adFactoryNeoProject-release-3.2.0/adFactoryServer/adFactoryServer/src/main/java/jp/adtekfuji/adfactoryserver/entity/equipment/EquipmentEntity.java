/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.equipment;

import adtekfuji.utility.StringUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.*;
import jp.adtekfuji.adFactory.enumerate.TermUnitEnum;
import jp.adtekfuji.adfactoryserver.entity.resource.LocaleFileEntity;
import jp.adtekfuji.adfactoryserver.utility.PgJsonbConverter;
import org.apache.logging.log4j.LogManager;

/**
 * 設備情報
 *
 * @author ke.yokoi
 */
@XmlRootElement(name = "equipment")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "mst_equipment")
@NamedNativeQueries({
    // 指定した子孫の設備を取得
    @NamedNativeQuery(name = "EquipmentEntity.findDescendantsByIds", query = "WITH RECURSIVE equipments AS (SELECT * FROM mst_equipment eq WHERE eq.parent_equipment_id = ANY(?1) OR eq.equipment_id = ANY(?1) UNION DISTINCT SELECT eq2.* FROM mst_equipment eq2, equipments WHERE equipments.equipment_id = eq2.parent_equipment_id) SELECT * FROM equipments eqs WHERE EXISTS(SELECT * FROM mst_equipment_type met WHERE met.name = ANY(?2) AND met.equipment_type_id = eqs.equipment_type_id)", resultClass = EquipmentEntity.class),
    // 指定した祖先の設備を取得
    @NamedNativeQuery(name = "EquipmentEntity.findAncestorsByIds", query = "WITH RECURSIVE equipments AS (SELECT * FROM mst_equipment eq WHERE eq.parent_equipment_id = ANY (?1) OR equipment_id = ANY (?1) UNION DISTINCT SELECT eq2.* FROM mst_equipment eq2, equipments WHERE equipments.parent_equipment_id = eq2.equipment_id) SELECT * FROM equipments eqs", resultClass = EquipmentEntity.class),

})
@NamedQueries({
    // 設備種別IDを指定して、設備情報の件数を取得する。(削除済の設備は対象外)
    @NamedQuery(name = "EquipmentEntity.countByEquipmentType", query = "SELECT COUNT(e.equipmentId) FROM EquipmentEntity e WHERE e.equipmentTypeId = :equipmentTypeId AND e.removeFlag = false"),

    // 設備識別名の重複確認 (追加時) ※.削除済も対象
    @NamedQuery(name = "EquipmentEntity.checkAddByIdent", query = "SELECT COUNT(e.equipmentId) FROM EquipmentEntity e WHERE e.equipmentIdentify = :equipmentIdentify"),
    // 設備識別名の重複確認 (更新時) ※.削除済も対象
    @NamedQuery(name = "EquipmentEntity.checkUpdateByIdent", query = "SELECT COUNT(e.equipmentId) FROM EquipmentEntity e WHERE e.equipmentIdentify = :equipmentIdentify AND e.equipmentId != :equipmentId"),

    // 設備情報をすべて取得する。 ※.削除済も対象
    @NamedQuery(name = "EquipmentEntity.findAll", query = "SELECT e FROM EquipmentEntity e ORDER BY e.equipmentName, e.equipmentIdentify, e.equipmentId"),
    // 設備ID一覧を指定して、設備情報一覧を取得する。(削除済の設備は対象外)
    @NamedQuery(name = "EquipmentEntity.findByIdsNotRemove", query = "SELECT e FROM EquipmentEntity e WHERE e.equipmentId IN :equipmentIds AND e.removeFlag = false"),

    // 設備識別名を指定して、設備情報を取得する。(削除済の設備は対象外)
    @NamedQuery(name = "EquipmentEntity.findByIdentNorRemove", query = "SELECT e FROM EquipmentEntity e WHERE e.equipmentIdentify = :equipmentIdentify AND e.removeFlag = false"),
    // 設備識別名一覧を指定して、設備情報一覧を取得する。(削除済の設備は対象外)
    @NamedQuery(name = "EquipmentEntity.findByIdentsNotRemove", query = "SELECT e FROM EquipmentEntity e WHERE e.equipmentIdentify IN :equipmentIdentifiers AND e.removeFlag = false ORDER BY e.equipmentIdentify"),

    // 設備IDを指定して、親設備IDを取得する。 ※.削除済も対象
    @NamedQuery(name = "EquipmentEntity.findParentId", query = "SELECT e.parentEquipmentId FROM EquipmentEntity e WHERE e.equipmentId = :equipmentId"),
    // 設備IDを指定して、子設備ID一覧を取得する。 ※.削除済も対象
    @NamedQuery(name = "EquipmentEntity.findChildId", query = "SELECT e.equipmentId FROM EquipmentEntity e WHERE e.parentEquipmentId = :equipmentId"),

    // 設備IDを指定して、子設備の設備情報の件数を取得する。 ※.削除済も対象
    @NamedQuery(name = "EquipmentEntity.countChild", query = "SELECT COUNT(e.equipmentId) FROM EquipmentEntity e WHERE e.parentEquipmentId = :equipmentId"),
    // 設備IDを指定して、子設備の設備情報一覧を取得する。 ※.削除済も対象
    @NamedQuery(name = "EquipmentEntity.findChild", query = "SELECT e FROM EquipmentEntity e WHERE e.parentEquipmentId = :equipmentId ORDER BY e.equipmentName, e.equipmentIdentify, e.equipmentId"),
    // 設備ID・ユーザーID(組織ID)一覧を指定して、アクセス可能な子設備の設備情報の件数を取得する。 ※.削除済も対象
    @NamedQuery(name = "EquipmentEntity.countChildByUserId", query = "SELECT COUNT(DISTINCT(e.equipmentId)) FROM EquipmentEntity e LEFT JOIN AccessHierarchyEntity a ON a.typeId = jp.adtekfuji.adFactory.enumerate.AccessHierarchyTypeEnum.EquipmentHierarchy AND a.hierarchyId = e.equipmentId WHERE e.parentEquipmentId = :equipmentId AND (a.organizationId IS NULL OR a.organizationId IN :ancestors)"),
    // 設備ID・ユーザーID(組織ID)一覧を指定して、アクセス可能な子設備の設備情報一覧を取得する。 ※.削除済も対象
    @NamedQuery(name = "EquipmentEntity.findChildByUserId", query = "SELECT DISTINCT(e) FROM EquipmentEntity e LEFT JOIN AccessHierarchyEntity a ON a.typeId = jp.adtekfuji.adFactory.enumerate.AccessHierarchyTypeEnum.EquipmentHierarchy AND a.hierarchyId = e.equipmentId WHERE e.parentEquipmentId = :equipmentId AND (a.organizationId IS NULL OR a.organizationId IN :ancestors) ORDER BY e.equipmentName, e.equipmentIdentify, e.equipmentId"),

    // ユーザーID(組織ID)一覧を指定して、アクセス可能な設備情報の件数を取得する。 ※.削除済も対象
    @NamedQuery(name = "EquipmentEntity.countByUserId", query = "SELECT COUNT(DISTINCT(e.equipmentId)) FROM EquipmentEntity e LEFT JOIN AccessHierarchyEntity a ON a.typeId = jp.adtekfuji.adFactory.enumerate.AccessHierarchyTypeEnum.EquipmentHierarchy AND a.hierarchyId = e.equipmentId WHERE (a.organizationId IS NULL OR a.organizationId IN :ancestors)"),
    // ユーザーID(組織ID)一覧を指定して、アクセス可能な設備情報一覧を取得する。 ※.削除済も対象
    @NamedQuery(name = "EquipmentEntity.findByUserId", query = "SELECT DISTINCT(e) FROM EquipmentEntity e LEFT JOIN AccessHierarchyEntity a ON a.typeId = jp.adtekfuji.adFactory.enumerate.AccessHierarchyTypeEnum.EquipmentHierarchy AND a.hierarchyId = e.equipmentId WHERE (a.organizationId IS NULL OR a.organizationId IN :ancestors) ORDER BY e.equipmentName, e.equipmentIdentify, e.equipmentId"),

    // 設備ID一覧を指定して、子設備の設備情報一覧を取得する。  ※.削除済も対象
    @NamedQuery(name = "EquipmentEntity.findChilds", query = "SELECT e FROM EquipmentEntity e WHERE e.parentEquipmentId IN :equipmentIds ORDER BY e.equipmentName, e.equipmentIdentify, e.equipmentId"),
    // 設備IDを指定して、設備名を取得する。
    @NamedQuery(name = "EquipmentEntity.findNameById", query = "SELECT e.equipmentName FROM EquipmentEntity e WHERE e.equipmentId = :equipmentId"),
})
public class EquipmentEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "equipment_id")
    private Long equipmentId;// 設備ID

    @Basic(optional = false)
    //@NotNull
    @Size(max = 256)
    @Column(name = "equipment_name")
    private String equipmentName;// 設備名

    @Basic(optional = false)
    @Size(min = 1, max = 256)
    @Column(name = "equipment_identify")
    private String equipmentIdentify;// 設備識別名

    @XmlElement(name = "equipmentTypeId")
    @Column(name = "equipment_type_id")
    private Long equipmentTypeId;// 設備種別ID

    @Column(name = "update_person_id")
    private Long updatePersonId;// 更新者(組織ID)

    @Column(name = "update_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDatetime;// 更新日時

    @Column(name = "remove_flag")
    private Boolean removeFlag = false;// 論理削除フラグ

    @Column(name = "cal_flag")
    private Boolean calFlag;// 機器校正有無

    @Column(name = "cal_next_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date calNextDate;// 次回校正日
    
    @Transient
    @JsonProperty("calNext")
    private String calNext;
    
    @Column(name = "cal_term")
    private Integer calTerm;// 校正間隔

    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    @Column(name = "cal_term_unit")
    private TermUnitEnum calTermUnit;// 間隔単位

    @Column(name = "cal_warning_days")
    private Integer calWarningDays;// 警告表示日数

    @Column(name = "cal_last_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date calLastDate;// 最終校正日
    
    @Transient
    @JsonProperty("calLast")
    private String calLast;
    
    @Column(name = "cal_person_id")
    private Long calPersonId;// 校正実施者

    @Column(name = "parent_equipment_id")
    @XmlElement(name = "parentId")
    private Long parentEquipmentId;// 親設備ID

    @Column(name = "ipv4_address")
    private String ipv4Address = "";// IPv4アドレス

    @Column(name = "work_progress_flag")
    private Boolean workProgressFlag = false;// 工程進捗フラグ

    @Column(name = "plugin_name")
    private String pluginName = "";// プラグイン名

    @Column(name = "equipment_add_info", length = 30000)
    @Convert(converter = PgJsonbConverter.class)
    private String equipmentAddInfo;// 追加情報(JSON)

    @Column(name = "service_info", length = 30000)
    @Convert(converter = PgJsonbConverter.class)
    private String serviceInfo;// サービス情報(JSON)

    @Column(name = "lang_Ids", length = 30000)
    private String langIds;// 言語

    @Column(name = "config", length = 30000)
    private String config;

    @XmlElementWrapper(name = "localeFileInfos")
    @XmlElement(name = "localeFileInfo")
    @Transient
    private List<LocaleFileEntity> localeFileInfos = null;// 言語ファイル

    //@NotNull
    @Column(name = "ver_info")
    @Version
    private Integer verInfo = 1;// 排他用バーション

    @Transient
    private String parentName;// 親設備名

    @Transient
    private String parentIdentName;// 親設備識別名

    @Transient
    private Long childCount = 0L;// 子設備数
    
    @Transient
    private Long licenseCount;  // ライセンス数

    @Transient
    private Long liteCount;     // Lite ライセンス数

    @Transient
    private Long reporterCount;  // Reporter ライセンス数

    /**
     * コンストラクタ
     */
    public EquipmentEntity() {
        this.removeFlag = false;
    }

    /**
     * コンストラクタ
     *
     * @param in
     */
    public EquipmentEntity(EquipmentEntity in) {
        this.equipmentName = in.equipmentName;
        this.equipmentIdentify = in.equipmentIdentify;
        this.equipmentTypeId = in.equipmentTypeId;
        this.updatePersonId = in.updatePersonId;
        this.updateDatetime = in.updateDatetime;
        this.removeFlag = in.removeFlag;
        this.calFlag = in.calFlag;
        this.calNextDate = in.calNextDate;
        this.calTerm = in.calTerm;
        this.calTermUnit = in.calTermUnit;
        this.calWarningDays = in.calWarningDays;
        this.calLastDate = in.calLastDate;
        this.calPersonId = in.calPersonId;

        this.parentEquipmentId = in.parentEquipmentId;

        // 設備情報
        this.ipv4Address = in.ipv4Address;
        this.workProgressFlag = in.workProgressFlag;
        this.pluginName = in.pluginName;

        // 追加情報
        this.equipmentAddInfo = in.equipmentAddInfo;
        // サービス情報
        this.serviceInfo = in.serviceInfo;

        // 言語
        this.langIds = in.langIds;

        // 背景色
        this.config = in.config;

    }

    /**
     * コンストラクタ
     *
     * @param parentEquipmentId 親設備ID
     * @param equipmentName 設備名
     * @param equipmentIdentify 設備識別名
     * @param equipmentTypeId 設備種別
     * @param updatePersonId 更新者(組織ID)
     * @param updateDatetime 更新日時
     */
    public EquipmentEntity(Long parentEquipmentId, String equipmentName, String equipmentIdentify, Long equipmentTypeId, Long updatePersonId, Date updateDatetime) {
        this.parentEquipmentId = parentEquipmentId;
        this.equipmentName = equipmentName;
        this.equipmentIdentify = equipmentIdentify;
        this.equipmentTypeId = equipmentTypeId;
        this.updatePersonId = updatePersonId;
        this.updateDatetime = updateDatetime;
        this.removeFlag = false;
    }

    /**
     * 設備IDを取得する。
     *
     * @return 設備ID
     */
    public Long getEquipmentId() {
        return this.equipmentId;
    }

    /**
     * 設備IDを設定する。
     *
     * @param equipmentId 設備ID
     */
    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    /**
     * 設備名を取得する。
     *
     * @return 設備名
     */
    public String getEquipmentName() {
        return this.equipmentName;
    }

    /**
     * 設備名を設定する。
     *
     * @param equipmentName 設備名
     */
    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    /**
     * 設備識別名を取得する。
     *
     * @return 設備識別名
     */
    public String getEquipmentIdentify() {
        return this.equipmentIdentify;
    }

    /**
     * 設備識別名を設定する。
     *
     * @param equipmentIdentify 設備識別名
     */
    public void setEquipmentIdentify(String equipmentIdentify) {
        this.equipmentIdentify = equipmentIdentify;
    }

    /**
     * 設備種別IDを取得する。
     *
     * @return 設備種別ID
     */
    public Long getEquipmentTypeId() {
        return this.equipmentTypeId;
    }

    /**
     * 設備種別IDを設定する。
     *
     * @param equipmentTypeId 設備種別ID
     */
    public void setEquipmentTypeId(Long equipmentTypeId) {
        this.equipmentTypeId = equipmentTypeId;
    }

    /**
     * 更新者(組織ID)を取得する。
     *
     * @return 更新者(組織ID)
     */
    public Long getUpdatePersonId() {
        return this.updatePersonId;
    }

    /**
     * 更新者(組織ID)を設定する。
     *
     * @param updatePersonId 更新者(組織ID)
     */
    public void setUpdatePersonId(Long updatePersonId) {
        this.updatePersonId = updatePersonId;
    }

    /**
     * 更新日時を取得する。
     *
     * @return 更新日時
     */
    public Date getUpdateDatetime() {
        return this.updateDatetime;
    }

    /**
     * 更新日時を設定する。
     *
     * @param updateDatetime 更新日時
     */
    public void setUpdateDatetime(Date updateDatetime) {
        this.updateDatetime = updateDatetime;
    }

    /**
     * 論理削除フラグを取得する。
     *
     * @return 論理削除フラグ
     */
    public Boolean getRemoveFlag() {
        return this.removeFlag;
    }

    /**
     * 論理削除フラグを設定する。
     *
     * @param removeFlag 論理削除フラグ
     */
    public void setRemoveFlag(Boolean removeFlag) {
        this.removeFlag = removeFlag;
    }

    /**
     * 機器校正有無を取得する。
     *
     * @return 機器校正有無
     */
    public Boolean getCalFlag() {
        return this.calFlag;
    }

    /**
     * 機器校正有無を設定する。
     *
     * @param calFlag 機器校正有無
     */
    public void setCalFlag(Boolean calFlag) {
        this.calFlag = calFlag;
    }

    /**
     * 次回校正日を取得する。
     *
     * @return 次回校正日
     */
    public Date getCalNextDate() {
        if (Objects.isNull(this.calNextDate) && !StringUtils.isEmpty(this.calNext)) {
            // adProductWebの場合、文字列で実施日時が送られる
            try {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
                this.calNextDate = df.parse(this.calNext);
            } catch (ParseException ex) {
                LogManager.getLogger().fatal(ex, ex);
            }
        }
        return this.calNextDate;
    }

    /**
     * 次回校正日を設定する。
     *
     * @param calNextDate 次回校正日
     */
    public void setCalNextDate(Date calNextDate) {
        this.calNextDate = calNextDate;
    }

    /**
     * 次回校正日を取得する。(adProductWeb Only)
     *
     * @return 次回校正日
     */
    public String getCalNext() {
        return calNext;
    }

    /**
     * 次回校正日を設定する。(adProductWeb Only)
     *
     * @param calNext 次回校正日
     */
    public void setCalNext(String calNext) {
        this.calNext = calNext;
    }

    /**
     * 校正間隔を取得する。
     *
     * @return 校正間隔
     */
    public Integer getCalTerm() {
        return this.calTerm;
    }

    /**
     * 校正間隔を設定する。
     *
     * @param calTerm 校正間隔
     */
    public void setCalTerm(Integer calTerm) {
        this.calTerm = calTerm;
    }

    /**
     * 間隔単位を取得する。
     *
     * @return 間隔単位
     */
    public TermUnitEnum getCalTermUnit() {
        return this.calTermUnit;
    }

    /**
     * 間隔単位を設定する。
     *
     * @param calTermUnit 間隔単位
     */
    public void setCalTermUnit(TermUnitEnum calTermUnit) {
        this.calTermUnit = calTermUnit;
    }

    /**
     * 警告表示日数を取得する。
     *
     * @return 警告表示日数
     */
    public Integer getCalWarningDays() {
        return this.calWarningDays;
    }

    /**
     * 警告表示日数を設定する。
     *
     * @param calWarningDays 警告表示日数
     */
    public void setCalWarningDays(Integer calWarningDays) {
        this.calWarningDays = calWarningDays;
    }

    /**
     * 最終校正日を取得する。
     *
     * @return 最終校正日
     */
    public Date getCalLastDate() {
        if (Objects.isNull(this.calLastDate) && !StringUtils.isEmpty(this.calLast)) {
            // adProductWebの場合、文字列で実施日時が送られる
            try {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
                this.calLastDate = df.parse(this.calLast);
            } catch (ParseException ex) {
                LogManager.getLogger().fatal(ex, ex);
            }
        }
        return this.calLastDate;
    }

    /**
     * 最終校正日を設定する。
     *
     * @param calLastDate 最終校正日
     */
    public void setCalLastDate(Date calLastDate) {
        if (Objects.isNull(this.calLastDate) && !StringUtils.isEmpty(this.calLast)) {
            // adProductWebの場合、文字列で実施日時が送られる
            try {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
                this.calLastDate = df.parse(this.calLast);
            } catch (ParseException ex) {
                LogManager.getLogger().fatal(ex, ex);
            }
        }
        this.calLastDate = calLastDate;
    }

    /**
     * 最終校正日を取得する。(adProductWeb Only)
     *
     * @return 最終校正日
     */
    public String getCalLast() {
        return calLast;
    }

    /**
     * 最終校正日を設定する。(adProductWeb Only)
     *
     * @param calLast 最終校正日
     */
    public void setCalLast(String calLast) {
        this.calLast = calLast;
    }

    /**
     * 校正実施者(組織ID)を取得する。
     *
     * @return 校正実施者(組織ID)
     */
    public Long getCalPersonId() {
        return this.calPersonId;
    }

    /**
     * 校正実施者(組織ID)を設定する。
     *
     * @param calPersonId 校正実施者(組織ID)
     */
    public void setCalPersonId(Long calPersonId) {
        this.calPersonId = calPersonId;
    }

    /**
     * 親設備IDを取得する。
     *
     * @return 親設備ID
     */
    public Long getParentEquipmentId() {
        return this.parentEquipmentId;
    }

    /**
     * 親設備IDを設定する。
     *
     * @param parentEquipmentId 親設備ID
     */
    public void setParentEquipmentId(Long parentEquipmentId) {
        this.parentEquipmentId = parentEquipmentId;
    }

    /**
     * IPv4アドレスを取得する。
     *
     * @return IPv4アドレス
     */
    public String getIpv4Address() {
        return this.ipv4Address;
    }

    /**
     * IPv4アドレスを設定する。
     *
     * @param ipv4Address IPv4アドレス
     */
    public void setIpv4Address(String ipv4Address) {
        this.ipv4Address = ipv4Address;
    }

    /**
     * 工程進捗フラグを取得する。
     *
     * @return 工程進捗フラグ
     */
    public Boolean getWorkProgressFlag() {
        return this.workProgressFlag;
    }

    /**
     * 工程進捗フラグを設定する。
     *
     * @param workProgressFlag 工程進捗フラグ
     */
    public void setWorkProgressFlag(Boolean workProgressFlag) {
        this.workProgressFlag = workProgressFlag;
    }

    /**
     * プラグイン名を取得する。
     *
     * @return プラグイン名
     */
    public String getPluginName() {
        return this.pluginName;
    }

    /**
     * プラグイン名を設定する。
     *
     * @param pluginName プラグイン名
     */
    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    /**
     * 追加情報(JSON)を取得する。
     *
     * @return 追加情報(JSON)
     */
    public String getEquipmentAddInfo() {
        return this.equipmentAddInfo;
    }

    /**
     * 追加情報(JSON)を設定する。
     *
     * @param equipmentAddInfo 追加情報(JSON)
     */
    public void setEquipmentAddInfo(String equipmentAddInfo) {
        this.equipmentAddInfo = equipmentAddInfo;
    }

    /**
     * サービス情報を取得する。
     *
     * @return サービス情報
     */
    public String getServiceInfo() {
        return this.serviceInfo;
    }

    /**
     * サービス情報を設定する。
     *
     * @param serviceInfo サービス情報
     */
    public void setServiceInfo(String serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    /**
     * 排他用バーションを取得する。
     *
     * @return 排他用バーション
     */
    public Integer getVerInfo() {
        return this.verInfo;
    }

    /**
     * 排他用バーションを設定する。
     *
     * @param verInfo 排他用バーション
     */
    public void setVerInfo(Integer verInfo) {
        this.verInfo = verInfo;
    }

    /**
     * 親設備名情報を取得する。
     *
     * @return 親設備名
     */
    public String getParentName() {
        return this.parentName;
    }

    /**
     * 親設備名を設定する。
     *
     * @param parentName 親設備名
     */
    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    /**
     * 親設備識別名を取得する。
     *
     * @return 親設備識別名
     */
    public String getParentIdentName() {
        return this.parentIdentName;
    }

    /**
     * 親設備識別名を設定する。
     *
     * @param parentIdentName 親設備識別名
     */
    public void setParentIdentName(String parentIdentName) {
        this.parentIdentName = parentIdentName;
    }

    /**
     * 子設備数を取得する。
     *
     * @return 子設備数
     */
    public Long getChildCount() {
        return this.childCount;
    }

    /**
     * 子設備数を設定する。
     *
     * @param childCount 子設備数
     */
    public void setChildCount(Long childCount) {
        this.childCount = childCount;
    }
    
    /**
     * ライセンス数を取得する。
     *
     * @return ライセンス数
     */
    public Long getLicenseCount() {
        return this.licenseCount;
    }

    /**
     * ライセンス数を設定する。
     *
     * @param licenseCount ライセンス数
     */
    public void setLicenseCount(Long licenseCount) {
        this.licenseCount = licenseCount;
    }


    /**
     * 言語を取得する。
     *
     * @return 言語
     */
    public String getLangIds() {
        return this.langIds;
    }

    /**
     * 言語を設定する。
     *
     * @param langIds 言語
     */
    public void setLangIds(String langIds) {
        this.langIds = langIds;
    }

    /**
     * 設定を取得
     * @return 設定
     */
    public String getConfig() {
        return config;
    }

    /**
     * 設定を設定
     * @param config 設定
     */
    public void setConfig(String config) {
        this.config = config;
    }

    /**
     * 言語ファイル情報を取得する。
     *
     * @return 言語ファイル情報
     */
    public List<LocaleFileEntity> getLocaleFileInfos() {
        return this.localeFileInfos;
    }

    /**
     * 言語ファイル情報を設定する。
     *
     * @param localeFileInfos 言語ファイル情報
     */
    public void setLocaleFileInfos(List<LocaleFileEntity> localeFileInfos) {
        this.localeFileInfos = localeFileInfos;
    }

    /**
     * Lite ライセンス数を取得する。
     * 
     * @return 
     */
    public Long getLiteCount() {
        return liteCount;
    }

    /**
     * Lite ライセンス数を設定する。
     * 
     * @param liteCount 
     */
    public void setLiteCount(Long liteCount) {
        this.liteCount = liteCount;
    }

    /**
     * Reporter ライセンス数を取得する。
     * 
     * @return 
     */
    public Long getReporterCount() {
        return reporterCount;
    }

    /**
     * Reporter ライセンス数を設定する。
     * 
     * @param reporterCount 
     */
    public void setReporterCount(Long reporterCount) {
        this.reporterCount = reporterCount;
    }

    /**
     * メンバーを更新する。
     */
    public void updateMember() {
        this.getCalNextDate();
        this.getCalLastDate();
    }
    
    /**
     * ハッシュコードを返す。
     * 
     * @return ハッシュコード 
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.equipmentId != null ? this.equipmentId.hashCode() : 0);
        return hash;
    }

    /**
     * オブジェクトを比較する。
     * 
     * @param obj オブジェクト
     * @return true: 等しい、false: 異なる
     */
    @Override
    public boolean equals(Object obj) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(obj instanceof EquipmentEntity)) {
            return false;
        }
        EquipmentEntity other = (EquipmentEntity) obj;
        return !((this.equipmentId == null && other.equipmentId != null) || (this.equipmentId != null && !this.equipmentId.equals(other.equipmentId)));
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現 
     */
    @Override
    public String toString() {
        return new StringBuilder("EquipmentEntity{")
                .append("equipmentId=").append(this.equipmentId)
                .append(", equipmentName=").append(this.equipmentName)
                .append(", equipmentIdentify=").append(this.equipmentIdentify)
                .append(", equipmentTypeId=").append(this.equipmentTypeId)
                .append(", updatePersonId=").append(this.updatePersonId)
                .append(", updateDatetime=").append(this.updateDatetime)
                .append(", removeFlag=").append(this.removeFlag)
                .append(", calFlag=").append(this.calFlag)
                .append(", calTerm=").append(this.calTerm)
                .append(", calTermUnit=").append(this.calTermUnit)
                .append(", calWarningDays=").append(this.calWarningDays)
                .append(", calNextDate=").append(this.calNextDate)
                .append(", calPersonId=").append(this.calPersonId)
                .append(", parentEquipmentId=").append(this.parentEquipmentId)
                .append(", ipv4Address=").append(this.ipv4Address)
                .append(", workProgressFlag=").append(this.workProgressFlag)
                .append(", pluginName=").append(this.pluginName)
                .append(", langIds=").append(this.langIds)
                .append(", config=").append(this.config)
                .append(", verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
