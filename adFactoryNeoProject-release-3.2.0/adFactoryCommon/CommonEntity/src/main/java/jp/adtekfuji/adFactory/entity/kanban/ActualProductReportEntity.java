/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.kanban;

import adtekfuji.utility.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.entity.operation.OperateAppEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.ServiceTypeEnum;
import org.apache.logging.log4j.LogManager;

/**
 * 生産実績通知
 *
 * @author ke.yokoi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "actualProductReport")
@JsonIgnoreProperties(ignoreUnknown=true)
public class ActualProductReportEntity implements Serializable {

    public static final String FORCED = "FORCED";

    private static final long serialVersionUID = 1L;

    @XmlElement(required = true)
    @JsonProperty("transactionId")
    private Long transactionId;// トランザクションID

    @XmlElement(required = true)
    @JsonProperty("kanbanId")
    private Long kanbanId;// カンバンID

    @XmlElement
    @JsonProperty("workKanbanId")
    private Long workKanbanId;// 工程カンバンID

    @XmlElement
    @JsonProperty("equipmentId")
    private Long equipmentId;// 設備ID

    @XmlElement
    @JsonProperty("terminalIdentName")
    private String terminalIdentName;// 設備識別名

    @XmlElement
    @JsonProperty("organizationId")
    private Long organizationId;// 組織ID

    @XmlElement
    @JsonProperty("reportDate")
    private String reportDate;

    @XmlElement(required = true)
    @JsonIgnore
    private Date reportDatetime;// 実施日時

    @XmlElement(required = true)
    @JsonProperty("status")
    private KanbanStatusEnum status;// 工程実績ステータス

    @XmlElement
    @JsonProperty("interruptReason")
    private String interruptReason;// 中断理由

    @XmlElement
    @JsonProperty("delayReason")
    private String delayReason;// 遅延理由

    @XmlElementWrapper(name = "properties")
    @XmlElement(name = "property")
    @JsonProperty("propertyCollection")
    private List<ActualProductReportPropertyEntity> propertyCollection;// 検査結果一覧

    @XmlElement
    @JsonProperty("isSchedule")
    private Boolean isSchedule;// スケジューリング機能フラグ

    @XmlElement
    @JsonProperty("serviceType")
    private ServiceTypeEnum serviceType;// サービス種別

    @XmlElementWrapper(name = "workKanbans")
    @XmlElement(name = "workKanbanId")
    @JsonProperty("workKanbanCollection")
    private List<Long> workKanbanCollection;// 工程カンバンID一覧

    @XmlElementWrapper(name = "products")
    @XmlElement(name = "product")
    @JsonIgnore
    private List<ProductInfoEntity> products;// 製品情報一覧

    @XmlElement
    @JsonProperty("rework")
    private Boolean rework = false;// 工程のやり直しか

    // 2019/12/12 １行程のみのやり直し対応 後工程のやり直し要否フラグを追加
    @XmlElement
    @JsonProperty("laterRework")
    private Boolean laterRework = false;

    @XmlElementWrapper(name = "traceabilities")
    @XmlElement(name = "traceability")
    @JsonProperty("traceabilities")
    private List<TraceabilityEntity> traceabilities;// トレーサビリティ一覧

    @XmlElement
    @JsonIgnore
    @JsonProperty("parts")
    private String parts;// 完成品情報一覧(JSON)

    @XmlElement
    @JsonProperty("usedParts")
    private String usedParts;// 使用部品情報一覧(JSON)

    @XmlElement
    @JsonProperty("defects")
    private String defects;// 不良品情報一覧(JSON)

    @XmlElementWrapper(name = "aditions")
    @XmlElement(name = "adition")
    @JsonProperty("aditions")
    private List<ActualAditionInfoEntity> aditions;// 工程実績付加情報一覧

    @XmlElement
    @JsonProperty("workSupport")
    private Boolean workSupport;

    @XmlElement
    @JsonProperty("supportMode")
    private Boolean supportMode;

    @XmlElement
    @JsonProperty("lotTraceParts")
    private String lotTraceParts;       // ロットトレース部品情報一覧(JSON)

    @XmlElement
    @JsonProperty("defectReason")
    private String defectReason;        // 不良理由（ロットアウト）

    @XmlElement
    @JsonProperty("defectNum")
    private Integer defectNum;          // 不良数
    
    @XmlElement
    @JsonProperty("taktTime")
    private Integer taktTime;

    @XmlElement
    @JsonProperty("serialNo")
    private String serialNo;            // シリアル番号

    @XmlElement
    @JsonProperty("serviceInfo")
    private String serviceInfo;         // サービス情報

    @XmlElement
    @JsonProperty("operateApp")
    private String operateApp;          // アプリケーション名

    @XmlElement
    @JsonProperty("isAllowSupportWork")
    private Boolean isAllowSupportWork; // 応援作業を許可するか？
    
    /**
     * コンストラクタ
     */
    public ActualProductReportEntity() {
    }

    //作業者端末用.
    /**
     * コンストラクタ
     *
     * @param transactionId トランザクションID
     * @param kanbanId カンバンIS
     * @param workKanbanId 工程カンバンID
     * @param equipmentId 設備ID
     * @param organizationId 組織ID
     * @param reportDatetime 日時
     * @param status 工程実績ステータス
     * @param interruptReason 中断理由
     * @param delayReason 遅延理由
     */
    public ActualProductReportEntity(Long transactionId, Long kanbanId, Long workKanbanId, Long equipmentId, Long organizationId, Date reportDatetime, KanbanStatusEnum status, String interruptReason, String delayReason) {
        this.transactionId = transactionId;
        this.kanbanId = kanbanId;
        this.workKanbanId = workKanbanId;
        this.equipmentId = equipmentId;
        this.organizationId = organizationId;
        this.reportDatetime = reportDatetime;
        this.status = status;
        this.interruptReason = interruptReason;
        this.delayReason = delayReason;
    }

    //adManagerApp用.
    /**
     * コンストラクタ
     *
     * @param transactionId
     * @param kanbanId
     * @param workKanbanId
     * @param terminalIdentName
     * @param reportDatetime
     * @param status
     * @param interruptReason
     * @param delayReason
     */
    public ActualProductReportEntity(Long transactionId, Long kanbanId, Long workKanbanId, String terminalIdentName, Date reportDatetime, KanbanStatusEnum status, String interruptReason, String delayReason) {
        this.transactionId = transactionId;
        this.kanbanId = kanbanId;
        this.workKanbanId = workKanbanId;
        this.terminalIdentName = terminalIdentName;
        this.reportDatetime = reportDatetime;
        this.status = status;
        this.interruptReason = interruptReason;
        this.delayReason = delayReason;
    }

    /**
     * トランザクションIDを取得する。
     *
     * @return トランザクションID
     */
    public Long getTransactionId() {
        return this.transactionId;
    }

    /**
     * トランザクションIDを設定する。
     *
     * @param transactionId トランザクションID
     */
    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
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
     * 工程カンバンIDを取得する。
     *
     * @return 工程カンバンID
     */
    public Long getWorkKanbanId() {
        return this.workKanbanId;
    }

    /**
     * 工程カンバンIDを設定する。
     *
     * @param workKanbanId 工程カンバンID
     */
    public void setWorkKanbanId(Long workKanbanId) {
        this.workKanbanId = workKanbanId;
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
     * 組織IDを取得する。
     *
     * @return 組織ID
     */
    public Long getOrganizationId() {
        return this.organizationId;
    }

    /**
     * 組織IDを設定する。
     *
     * @param organizationId 組織ID
     */
    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * 設備識別名を取得する。
     *
     * @return 設備識別名
     */
    public String getTerminalIdentName() {
        return this.terminalIdentName;
    }

    /**
     * 設備識別名を設定する。
     *
     * @param terminalIdentName 設備識別名
     */
    public void setTerminalIdentName(String terminalIdentName) {
        this.terminalIdentName = terminalIdentName;
    }

    /**
     * 実施日時を取得する。
     *
     * @return 実施日時
     */
    public Date getReportDatetime() {
        if (Objects.isNull(this.reportDatetime) && !StringUtils.isEmpty(this.reportDate)) {
            // adProductWebの場合、文字列で実施日時が送られる
            try {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
                this.reportDatetime = df.parse(this.reportDate);
            } catch (ParseException ex) {
                LogManager.getLogger().fatal(ex, ex);
            }
        }
        return this.reportDatetime;
    }

    /**
     * 実施日時を設定する。
     *
     * @param reportDatetime 実施日時
     */
    public void setReportDatetime(Date reportDatetime) {
        this.reportDatetime = reportDatetime;
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
     * 工程実績ステータスを取得する。
     *
     * @return 工程実績ステータス
     */
    public KanbanStatusEnum getStatus() {
        return this.status;
    }

    /**
     * 工程実績ステータスを設定する。
     *
     * @param status 工程実績ステータス
     */
    public void setStatus(KanbanStatusEnum status) {
        this.status = status;
    }

    /**
     * 中断理由を取得する。
     *
     * @return 中断理由
     */
    public String getInterruptReason() {
        return this.interruptReason;
    }

    /**
     * 中断理由を設定する。
     *
     * @param interruptReason 中断理由
     */
    public void setInterruptReason(String interruptReason) {
        this.interruptReason = interruptReason;
    }

    /**
     * 遅延理由を取得する。
     *
     * @return 遅延理由
     */
    public String getDelayReason() {
        return this.delayReason;
    }

    /**
     * 遅延理由を設定する。
     *
     * @param delayReason 遅延理由
     */
    public void setDelayReason(String delayReason) {
        this.delayReason = delayReason;
    }

    /**
     * 検査結果一覧を取得する。
     *
     * @return 検査結果一覧
     */
    public List<ActualProductReportPropertyEntity> getPropertyCollection() {
        return this.propertyCollection;
    }

    /**
     * 検査結果一覧を設定する。
     *
     * @param propertyCollection 検査結果一覧
     */
    public void setPropertyCollection(List<ActualProductReportPropertyEntity> propertyCollection) {
        this.propertyCollection = propertyCollection;
    }

    /**
     * スケジューリング機能フラグを取得する。
     *
     * @return スケジューリング機能フラグ
     */
    public Boolean getIsSchedule() {
        if (Objects.isNull(isSchedule)) {
            return false;
        }
        return this.isSchedule;
    }

    /**
     * スケジューリング機能フラグを設定する。
     *
     * @param isSchedule スケジューリング機能フラグ
     */
    public void setIsSchedule(Boolean isSchedule) {
        this.isSchedule = isSchedule;
    }

    /**
     * サービス種別を取得する。
     *
     * @return サービス種別
     */
    public ServiceTypeEnum getServiceType() {
        if (Objects.isNull(this.serviceType)) {
            return ServiceTypeEnum.GENERIC;
        }
        return this.serviceType;
    }

    /**
     * サービス種別を設定する。
     *
     * @param serviceType サービス種別
     */
    public void setServiceType(ServiceTypeEnum serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * 工程カンバンID一覧を取得する。
     *
     * @return 工程カンバンID一覧
     */
    public List<Long> getWorkKanbanCollection() {
        return this.workKanbanCollection;
    }

    /**
     * 工程カンバンID一覧を設定する。
     *
     * @param worKanbanCollection 工程カンバンID一覧
     */
    public void setWorkKanbanCollection(List<Long> worKanbanCollection) {
        this.workKanbanCollection = worKanbanCollection;
    }

    /**
     * 製品情報一覧を取得する。
     *
     * @return 製品情報一覧
     */
    public List<ProductInfoEntity> getProducts() {
        return this.products;
    }

    /**
     * 製品情報一覧を設定する。
     *
     * @param products 製品情報一覧
     */
    public void setProducts(List<ProductInfoEntity> products) {
        this.products = products;
    }

    /**
     * 工程のやり直しかどうかを取得する。
     *
     * @return 工程のやり直しか
     */
    public Boolean isRework() {
        return this.rework;
    }

    /**
     * 工程のやり直しを設定する。
     *
     * @param rework 工程のやり直しか
     */
    public void setRework(Boolean rework) {
        this.rework = rework;
    }

    /**
     * 後続工程のやり直しを行うか否かを返す。
     *
     * @return 後続工程のやり直しを行うか (true:行なう, false:行なわない)
     */
    public Boolean isLaterRework() {
        return this.laterRework;
    }

    /**
     * 後続工程のやり直しを行うか否かを設定する。
     *
     * @param laterRework 後続工程のやり直しを行うか (true:行なう, false:行なわない)
     */
    public void setLaterRework(Boolean laterRework) {
        this.laterRework = laterRework;
    }

    /**
     * トレーサビリティ一覧を取得する。
     *
     * @return トレーサビリティ一覧
     */
    public List<TraceabilityEntity> getTraceabilities() {
        return this.traceabilities;
    }

    /**
     * トレーサビリティ一覧を設定する。
     *
     * @param traceabilities トレーサビリティ一覧
     */
    public void setTraceabilities(List<TraceabilityEntity> traceabilities) {
        this.traceabilities = traceabilities;
    }

    /**
     * 完成品情報一覧(JSON)を取得する。
     *
     * @return 完成品情報(JSON)
     */
    public String getParts() {
        return this.parts;
    }

    /**
     * 完成品情報一覧(JSON)を設定する。
     *
     * @param parts 完成品情報(JSON)
     */
    public void setParts(String parts) {
        this.parts = parts;
    }

    /**
     * 使用部品情報一覧(JSON)を取得する。
     *
     * @return 使用部品情報(JSON)
     */
    public String getUsedParts() {
        return this.usedParts;
    }

    /**
     * 使用部品情報一覧(JSON)を設定する。
     *
     * @param usedParts 使用部品情報(JSON)
     */
    public void setUsedParts(String usedParts) {
        this.usedParts = usedParts;
    }

    /**
     * 不良品情報一覧(JSON)を取得する。
     *
     * @return 不良品情報一覧(JSON)
     */
    public String getDefects() {
        return this.defects;
    }

    /**
     * 不良品情報一覧(JSON)を設定する。
     *
     * @param defects 不良品情報一覧(JSON)
     */
    public void setDefects(String defects) {
        this.defects = defects;
    }

    /**
     * 実績付加情報一覧を取得する。
     *
     * @return 実績付加情報一覧
     */
    public List<ActualAditionInfoEntity> getAditions() {
        return this.aditions;
    }

    /**
     * 実績付加情報一覧を設定する。
     *
     * @param aditions 実績付加情報一覧
     */
    public void setAditions(List<ActualAditionInfoEntity> aditions) {
        this.aditions = aditions;
    }

    /**
     * 同一作業者端末での応援者フラグを返す。
     *
     * @return true:同一作業者端末での応援、false:同一作業者端末での応援以外
     */
    public boolean isWorkSupport() {
        if (Objects.isNull(this.workSupport)) {
            return false;
        }
        return this.workSupport;
    }

    /**
     * 同一作業者端末での応援者フラグを設定する。
     *
     * @param workSupport 同一作業者端末での応援者フラグ
     */
    public void setWorkSupport(Boolean workSupport) {
        this.workSupport = workSupport;
    }

    /**
     * 応援モードかどうかを返す。
     *
     * @return true:応援モード、false:応援モード以外
     */
    public Boolean isSupportMode() {
        return this.supportMode;
    }

    /**
     * 応援モードを設定する。
     *
     * @param supportMode 応援モード
     */
    public void setSupportMode(Boolean supportMode) {
        this.supportMode = supportMode;
    }

    /**
     * ロットトレース部品情報一覧(JSON)を取得する。
     *
     * @return ロットトレース部品情報一覧(JSON)
     */
    public String getLotTraceParts() {
        return this.lotTraceParts;
    }

    /**
     * ロットトレース部品情報一覧(JSON)を設定する。
     *
     * @param lotTraceParts ロットトレース部品情報一覧(JSON)
     */
    public void setLotTraceParts(String lotTraceParts) {
        this.lotTraceParts = lotTraceParts;
    }

    /**
     * 不良理由を取得する。
     *
     * @return 不良理由
     */
    public String getDefectReason() {
        return this.defectReason;
    }

    /**
     * 不良数を取得する。
     * 
     * @return 不良数
     */
    public Integer getDefectNum() {
        return defectNum;
    }

    /**
     * 不良数を設定する。
     * 
     * @param defectNum 不良数 
     */
    public void setDefectNum(Integer defectNum) {
        this.defectNum = defectNum;
    }

    /**
     * タクトタイムを取得する。
     * 
     * @return タクトタイム
     */
    public Integer getTaktTime() {
        return this.taktTime;
    }

    /**
     * シリアル番号を取得する。
     * 
     * @return シリアル番号
     */
    public String getSerialNo() {
        return serialNo;
    }

    /**
     * サービス情報を設定する。
     * 
     * @return サービス情報 
     */
    public String getServiceInfo() {
        return serviceInfo;
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
     * アプリケーション名を取得する。
     * 
     * @return アプリケーション名
     */
    public OperateAppEnum getOperateApp() {
        return OperateAppEnum.toEnum(this.operateApp);
    }

    /**
     * アプリケーション名を設定する。
     * 
     * @param operateApp アプリケーション名
     */
    public void setOperateApp(OperateAppEnum operateApp) {
        this.operateApp = operateApp.getName();
    }

    /**
     * 応援業務が許可するかどうかを判定します。
     *
     * @return サポート業務が許可されている場合はtrue、そうでない場合はfalse
     */
    public Boolean isAllowSupportWork() {
        return isAllowSupportWork;
    }

    /**
     * 応援作業を許可するかどうかを設定します。
     *
     * @param allowSupportWork サポート作業を許可する場合はtrue、許可しない場合はfalse
     */
    public void setAllowSupportWork(Boolean allowSupportWork) {
        isAllowSupportWork = allowSupportWork;
    }

    /**
     * ハッシュ値を返す。
     *
     * @return ハッシュ値
     */
    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    /**
     * オブジェクトを比較する。
     *
     * @param obj オブジェクト
     * @return true:オブジェクトが一致、false:オブジェクトが不一致
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return getClass() == obj.getClass();
    }

    /**
     * 文字列表現を返す。
     *
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("ActualProductReportEntity{")
                .append("transactionId=").append(this.transactionId)
                .append(", kanbanId=").append(this.kanbanId)
                .append(", workKanbanId=").append(this.workKanbanId)
                .append(", equipmentId=").append(this.equipmentId)
                .append(", terminalIdentName=").append(this.terminalIdentName)
                .append(", organizationId=").append(this.organizationId)
                .append(", reportDate=").append(this.reportDate)
                .append(", reportDatetime=").append(this.reportDatetime)
                .append(", status=").append(this.status)
                .append(", interruptReason=").append(this.interruptReason)
                .append(", delayReason=").append(this.delayReason)
                .append(", isSchedule=").append(this.isSchedule)
                .append(", serviceType=").append(this.serviceType)
                .append(", rework=").append(this.rework)
                .append(", workSupport=").append(this.workSupport)
                .append(", supportMode=").append(this.supportMode)
                .append(", serialNo=").append(this.serialNo)
                .append(", operateApp=").append(this.operateApp)
                .append("}")
                .toString();
    }
}
