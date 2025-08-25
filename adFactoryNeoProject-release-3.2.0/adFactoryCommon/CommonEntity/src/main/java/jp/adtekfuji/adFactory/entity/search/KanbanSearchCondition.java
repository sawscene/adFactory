/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.search;

import adtekfuji.utility.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.MatchTypeEnum;

/**
 * カンバン情報検索条件
 *
 * @author ke.yokoi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "kanbanSearchCondition")
@JsonIgnoreProperties(ignoreUnknown=true)
public class KanbanSearchCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssX";

    @XmlElement()
    private Long hierarchyId = null;// 階層ID

    @XmlElement()
    private Long kanbanId = null;// カンバンID

    @XmlElement()
    private String kanbanName = null;// カンバン名

    @XmlElement()
    private String kanbanSubname = null;// サブカンバン名

    @XmlElementWrapper(name = "workflowIds")
    @XmlElement(name = "workflowId")
    private List<Long> workflowIdCollection = null;// 工程順ID一覧

    @XmlElement()
    private Date fromDate = null;// 日時範囲の先頭

    @XmlElement()
    private Date toDate = null;// 日時範囲の末尾

    @XmlElement()
    @JsonIgnore
    private Date fromActualDate = null;// 実績日時範囲の先頭

    @XmlElement()
    @JsonIgnore
    private Date toActualDate = null;// 実績日時範囲の末尾
    
    @XmlElement()
    private String fromActual;// 実績日時範囲の開始 (Json用)

    @XmlElement()
    private String toActual;// 実績日時範囲の終了 (Json用)
    
    @XmlElementWrapper(name = "actualOrganizationNames")
    @XmlElement(name = "actualOrganizationNames")
    private List<String> actualOrganizationNameCollection = null;// 実績組織名一覧

    @XmlElement()
    private Boolean implementFlag = null;// 実施フラグ

    @XmlElement()
    private Boolean skipFlag = null;// スキップフラグ

    @XmlElementWrapper(name = "kanbanStatuses")
    @XmlElement(name = "kanbanStatus")
    private List<KanbanStatusEnum> kanbanStatusCollection = null;// 工程カンバンステータス一覧

    @XmlElementWrapper(name = "parentStatuses")
    @XmlElement(name = "parentStatus")
    private List<KanbanStatusEnum> parentStatusCollection = null;// カンバンステータス一覧

    @XmlElementWrapper(name = "equipmentIds")
    @XmlElement(name = "equipmentId")
    private List<Long> equipmentCollection = null;// 設備ID一覧

    @XmlElementWrapper(name = "equipmentNames")
    @XmlElement(name = "equipmentName")
    private List<String> equipmentNameCollection = null;// 設備名一覧

    @XmlElement()
    private Boolean equipmentIdWithParent = null;// 親設備フラグ

    @XmlElementWrapper(name = "organizationIds")
    @XmlElement(name = "organizationId")
    private List<Long> organizationCollection = null;// 組織ID一覧

    @XmlElementWrapper(name = "organizationNames")
    @XmlElement(name = "organizationName")
    private List<String> organizationNameCollection = null;// 組織名一覧

    @XmlElement()
    private Boolean organizationIdWithParent = null;// 親組織フラグ

    @XmlElementWrapper(name = "propertys")
    @XmlElement(name = "property")
    private List<PropertyCondition> propertyCollection = null;

    @XmlElement()
    private String modelName = null;// モデル名

    @XmlElement()
    private boolean isAdditionalInfo= true;// 詳細情報を取得してセットするか

    @XmlElement()
    private Long workId;// 工程ID

    @XmlElementWrapper(name = "workKanbanIds")
    @XmlElement(name = "workKanbanId")
    private List<Long> workKanbanCollection = null;// 工程カンバンID一覧
    
    @XmlElement()
    private Long loginUserId = null; // ログインユーザーID
    
    @XmlElement()
    private Boolean includeChildOrganizaitonFlag = null; // 子組織を含んで取得するか
    
    @XmlElement()
    private Boolean outputFlag = null; // 要実績出力フラグ

    @XmlElement()
    @JsonIgnore
    private Date fromActualStartTime = null;
    
    @XmlTransient()
    private String fromActualStart = null;
    
    @XmlElement()
    @JsonIgnore
    private Date fromActualCompTime = null; // 完了日時範囲の先頭

    @XmlTransient()
    private String fromActualComp = null; // 完了日時範囲の先頭 (Json用)

    @XmlElement()
    @JsonIgnore
    private Date toActualCompTime = null; // 完了日時範囲の末尾

    @XmlTransient()
    private String toActualComp = null; // 完了日時範囲の末尾 (Json用)

    @XmlElement()
    private String productionNumber; // 製造番号
    
    @XmlElement()
    private MatchTypeEnum matchType; // マッチタイプ
    
    @XmlElement()
    private Long searchMode; // 検索モード

    @XmlElement()
    private Boolean separateWorkFlag; // 追加工程

    /**
     * コンストラクタ
     */
    public KanbanSearchCondition() {
    }

    /**
     * コンストラクタ
     *
     * @param kanbanId カンバンID
     * @param kanbanName カンバン名
     * @param kanbanSubname サブカンバン名
     * @param workflowId 工程順ID
     * @param fromDate 日時範囲の先頭
     * @param toDate 日時範囲の末尾
     */
    public KanbanSearchCondition(Long kanbanId, String kanbanName, String kanbanSubname, Long workflowId, Date fromDate, Date toDate) {
        this.kanbanId = kanbanId;
        this.kanbanName = kanbanName;
        this.kanbanSubname = kanbanSubname;
        if (Objects.nonNull(workflowId)) {
            workflowIdCollection = new ArrayList<>();
            workflowIdCollection.add(workflowId);
        }
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    /**
     * 階層IDを設定して、検索条件を取得する。
     *
     * @param hierarchyId 階層ID
     * @return カンバン情報検索条件
     */
    public KanbanSearchCondition hierarchyId(Long hierarchyId) {
        this.hierarchyId = hierarchyId;
        return this;
    }

    /**
     * カンバンIDを設定して、検索条件を取得する。
     *
     * @param kanbanId カンバンID
     * @return カンバン情報検索条件
     */
    public KanbanSearchCondition kanbanId(Long kanbanId) {
        this.kanbanId = kanbanId;
        return this;
    }

    /**
     * カンバン名を設定して、検索条件を取得する。
     *
     * @param kanbanName カンバン名
     * @return カンバン情報検索条件
     */
    public KanbanSearchCondition kanbanName(String kanbanName) {
        this.kanbanName = kanbanName;
        return this;
    }

    /**
     * サブカンバン名を設定して、検索条件を取得する。
     *
     * @param kanbanSubname サブカンバン名
     * @return カンバン情報検索条件
     */
    public KanbanSearchCondition kanbanSubname(String kanbanSubname) {
        this.kanbanSubname = kanbanSubname;
        return this;
    }

    /**
     * 工程順IDを設定して、検索条件を取得する。
     *
     * @param workflowId 工程順ID
     * @return カンバン情報検索条件
     */
    public KanbanSearchCondition workflowId(Long workflowId) {
        if (Objects.nonNull(workflowId)) {
            this.workflowIdCollection = new ArrayList<>();
            this.workflowIdCollection.add(workflowId);
        }
        return this;
    }

    /**
     * 工程順ID一覧を設定して、検索条件を取得する。
     *
     * @param workflowIdList 工程順ID一覧
     * @return カンバン情報検索条件
     */
    public KanbanSearchCondition workflowIdList(List<Long> workflowIdList) {
        this.workflowIdCollection = workflowIdList;
        return this;
    }

    /**
     * 日時範囲の先頭を設定して、検索条件を取得する。
     *
     * @param fromDate 日時範囲の先頭
     * @return カンバン情報検索条件
     */
    public KanbanSearchCondition fromDate(Date fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    /**
     * 日時範囲の末尾を設定して、検索条件を取得する。
     *
     * @param toDate 日時範囲の末尾
     * @return カンバン情報検索条件
     */
    public KanbanSearchCondition toDate(Date toDate) {
        this.toDate = toDate;
        return this;
    }

    /**
     * 実績日時範囲の先頭を設定して、検索条件を取得する。
     *
     * @param fromActualDate 実績日時範囲の先頭
     * @return カンバン情報検索条件
     */
    public KanbanSearchCondition fromActualDate(Date fromActualDate) {
        this.fromActualDate = fromActualDate;
        return this;
    }

    /**
     * 実績日時範囲の末尾を設定して、検索条件を取得する。
     *
     * @param toActualDate 実績日時範囲の末尾
     * @return カンバン情報検索条件
     */
    public KanbanSearchCondition toActualDate(Date toActualDate) {
        this.toActualDate = toActualDate;
        return this;
    }

    /**
     * 実績組織名一覧を設定して、検索条件を取得する。
     *
     * @param actualOrganizationNameList 実績組織名一覧
     * @return カンバン情報検索条件
     */
    public KanbanSearchCondition actualOrganizationNameCollection(List<String> actualOrganizationNameList) {
        this.actualOrganizationNameCollection = actualOrganizationNameList;
        return this;
    }

    /**
     * 実施フラグを設定して、検索条件を取得する。
     *
     * @param implementFlag 実施フラグ
     * @return カンバン情報検索条件
     */
    public KanbanSearchCondition implementFlag(Boolean implementFlag) {
        this.implementFlag = implementFlag;
        return this;
    }

    /**
     * スキップフラグを設定して、検索条件を取得する。
     *
     * @param skipFlag スキップフラグ
     * @return カンバン情報検索条件
     */
    public KanbanSearchCondition skipFlag(Boolean skipFlag) {
        this.skipFlag = skipFlag;
        return this;
    }

    /**
     * 工程カンバンステータス一覧を設定して、検索条件を取得する。
     *
     * @param statusList 工程カンバンステータス一覧
     * @return カンバン情報検索条件
     */
    public KanbanSearchCondition statusList(List<KanbanStatusEnum> statusList) {
        this.kanbanStatusCollection = statusList;
        return this;
    }

    /**
     * カンバンステータス一覧を設定して、検索条件を取得する。
     *
     * @param parentStatusList カンバンステータス一覧
     * @return カンバン情報検索条件
     */
    public KanbanSearchCondition parentStatusList(List<KanbanStatusEnum> parentStatusList) {
        this.parentStatusCollection = parentStatusList;
        return this;
    }

    /**
     * 設備ID一覧を設定して、検索条件を取得する。
     *
     * @param equipmentIdList 設備ID一覧
     * @return カンバン情報検索条件
     */
    public KanbanSearchCondition equipmentList(List<Long> equipmentIdList) {
        this.equipmentCollection = equipmentIdList;
        return this;
    }

    /**
     * 設備名一覧を設定して、検索条件を取得する。
     *
     * @param equipmentNameList 設備名一覧
     * @return カンバン情報検索条件
     */
    public KanbanSearchCondition equipmentNameList(List<String> equipmentNameList) {
        this.equipmentNameCollection = equipmentNameList;
        return this;
    }

    /**
     * 親設備フラグを取得して、検索条件を取得する。
     * ※．設備ID一覧の指定があり、親設備フラグが有効な場合、設備ID一覧に親設備のIDを追加する。
     *
     * @param equipmentIdWithParent 親設備フラグ
     * @return カンバン情報検索条件
     */
    public KanbanSearchCondition equipmentIdWithParent(Boolean equipmentIdWithParent) {
        this.equipmentIdWithParent = equipmentIdWithParent;
        return this;
    }

    /**
     * 組織ID一覧を設定して、検索条件を取得する。
     *
     * @param organizationIdList 組織ID一覧
     * @return カンバン情報検索条件
     */
    public KanbanSearchCondition organizationList(List<Long> organizationIdList) {
        this.organizationCollection = organizationIdList;
        return this;
    }

    /**
     * 組織名一覧を設定して、検索条件を取得する。
     *
     * @param organizationNameList 組織名一覧
     * @return カンバン情報検索条件
     */
    public KanbanSearchCondition organizationNameList(List<String> organizationNameList) {
        this.organizationNameCollection = organizationNameList;
        return this;
    }

    /**
     * 親組織フラグを設定して、検索条件を取得する。
     * ※．組織ID一覧の指定があり、親組織フラグが有効な場合、組織ID一覧に親組織のIDを追加する。
     *
     * @param organizationIdWithParent 親組織フラグ
     * @return カンバン情報検索条件
     */
    public KanbanSearchCondition organizationIdWithParent(Boolean organizationIdWithParent) {
        this.organizationIdWithParent = organizationIdWithParent;
        return this;
    }

    public KanbanSearchCondition propertyList(List<PropertyCondition> propertyList) {
        this.propertyCollection = propertyList;
        return this;
    }
    /**
     * モデル名を設定して、検索条件を取得する。(ver.1.8.2 からの検索条件)
     *
     * @param modelName モデル名
     * @return 実績出力情報の検索条件
     */
    public KanbanSearchCondition modelName(String modelName) {
        this.modelName = modelName;
        return this;
    }

    /**
     * 階層IDを取得する。
     *
     * @return 階層ID
     */
    public Long getHierarchyId() {
        return this.hierarchyId;
    }

    /**
     * 階層IDを設定する。
     *
     * @param hierarchyId 階層ID
     */
    public void setHierarchyId(Long hierarchyId) {
        this.hierarchyId = hierarchyId;
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
     * サブカンバン名を取得する。
     *
     * @return サブカンバン名
     */
    public String getKanbanSubname() {
        return this.kanbanSubname;
    }

    /**
     * サブカンバン名を設定する。
     *
     * @param kanbanSubname サブカンバン名
     */
    public void setKanbanSubname(String kanbanSubname) {
        this.kanbanSubname = kanbanSubname;
    }

    /**
     * 工程順ID一覧を取得する。
     *
     * @return 工程順ID一覧
     */
    public List<Long> getWorkflowIdCollection() {
        return this.workflowIdCollection;
    }

    /**
     * 工程順ID一覧を設定する。
     *
     * @param workflowIdCollection 工程順ID一覧
     */
    public void setWorkflowIdCollection(List<Long> workflowIdCollection) {
        this.workflowIdCollection = workflowIdCollection;
    }

    /**
     * 日時範囲の先頭を取得する。
     *
     * @return 日時範囲の先頭
     */
    public Date getFromDate() {
        return this.fromDate;
    }

    /**
     * 日時範囲の先頭を設定する。
     *
     * @param fromDate 日時範囲の先頭
     */
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * 日時範囲の末尾を取得する。
     *
     * @return 日時範囲の末尾
     */
    public Date getToDate() {
        return this.toDate;
    }

    /**
     * 日時範囲の末尾を設定する。
     *
     * @param toDate 日時範囲の末尾
     */
    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    /**
     * 実績日時範囲の先頭を取得する。
     *
     * @return 実績日時範囲の先頭
     */
    public Date getFromActualDate() {
        if (Objects.isNull(this.fromActualDate) && !StringUtils.isEmpty(this.fromActual)) {
            // Json の場合は日時は文字列で送られてくる。
            try {
                SimpleDateFormat df = new SimpleDateFormat(DATETIME_FORMAT);
                this.fromActualDate = df.parse(this.fromActual);
            } catch (ParseException ex) {
            }
        }
        return this.fromActualDate;
    }

    /**
     * 実績日時範囲の先頭を設定する。
     *
     * @param fromActualDate 実績日時範囲の先頭
     */
    public void setFromActualDate(Date fromActualDate) {
        this.fromActualDate = fromActualDate;
    }

    /**
     * 実績日時範囲の開始 (Json用)を取得する。
     * 
     * @return 
     */
    public String getFromActual() {
        return fromActual;
    }

    /**
     * 実績日時範囲の開始 (Json用)を設定する。
     * 
     * @param fromActual 
     */
    public void setFromActual(String fromActual) {
        this.fromActual = fromActual;
    }

    /**
     * 実績日時範囲の末尾を取得する。
     *
     * @return 実績日時範囲の末尾
     */
    public Date getToActualDate() {
        if (Objects.isNull(this.toActualDate) && !StringUtils.isEmpty(this.toActual)) {
            // Json の場合は日時は文字列で送られてくる。
            try {
                SimpleDateFormat df = new SimpleDateFormat(DATETIME_FORMAT);
                this.toActualDate = df.parse(this.toActual);
            } catch (ParseException ex) {
            }
        }
        return this.toActualDate;
    }

    /**
     * 実績日時範囲の末尾を設定する。
     *
     * @param toActualDate 実績日時範囲の末尾
     */
    public void setToActualDate(Date toActualDate) {
        this.toActualDate = toActualDate;
    }

    /**
     * 実績日時範囲の終了 (Json用)を取得する。
     * 
     * @return 
     */
    public String getToActual() {
        return toActual;
    }

    /**
     * 実績日時範囲の終了 (Json用)を取得する。
     * 
     * @param toActual 
     */
    public void setToActual(String toActual) {
        this.toActual = toActual;
    }

    /**
     * 実績の組織名一覧を取得する。
     *
     * @return 実績の組織名一覧
     */
    public List<String> getActualOrganizationNameCollection() {
        return this.actualOrganizationNameCollection;
    }

    /**
     * 実績の組織名一覧を設定する。
     *
     * @param actualOrganizationNameCollection 実績の組織名一覧
     */
    public void setActualOrganizationNameCollection(List<String> actualOrganizationNameCollection) {
        this.actualOrganizationNameCollection = actualOrganizationNameCollection;
    }

    /**
     * 実施フラグを取得する。
     *
     * @return 実施フラグ
     */
    public Boolean getImplementFlag() {
        return this.implementFlag;
    }

    /**
     * 実施フラグを設定する。
     *
     * @param implementFlag 実施フラグ
     */
    public void setImplementFlag(Boolean implementFlag) {
        this.implementFlag = implementFlag;
    }

    /**
     * スキップフラグを取得する。
     *
     * @return スキップフラグ
     */
    public Boolean getSkipFlag() {
        return this.skipFlag;
    }

    /**
     * スキップフラグを設定する。
     *
     * @param skipFlag スキップフラグ
     */
    public void setSkipFlag(Boolean skipFlag) {
        this.skipFlag = skipFlag;
    }

    /**
     * ステータス一覧を取得する。
     *
     * @return ステータス一覧
     */
    public List<KanbanStatusEnum> getKanbanStatusCollection() {
        return this.kanbanStatusCollection;
    }

    /**
     * ステータス一覧を設定する。
     *
     * @param kanbanStatusCollection ステータス一覧
     */
    public void setKanbanStatusCollection(List<KanbanStatusEnum> kanbanStatusCollection) {
        this.kanbanStatusCollection = kanbanStatusCollection;
    }

    /**
     * カンバンステータス一覧を取得する。(工程カンバン検索で使用)
     *
     * @return カンバンステータス一覧
     */
    public List<KanbanStatusEnum> getParentStatusCollection() {
        return this.parentStatusCollection;
    }

    /**
     * カンバンステータス一覧を設定する。(工程カンバン検索で使用)
     *
     * @param parentStatusCollection カンバンステータス一覧
     */
    public void setParentStatusCollection(List<KanbanStatusEnum> parentStatusCollection) {
        this.parentStatusCollection = parentStatusCollection;
    }

    /**
     * 設備ID一覧を取得する。
     *
     * @return 設備ID一覧
     */
    public List<Long> getEquipmentCollection() {
        return this.equipmentCollection;
    }

    /**
     * 設備ID一覧を設定する。
     *
     * @param equipmentCollection 設備ID一覧
     */
    public void setEquipmentCollection(List<Long> equipmentCollection) {
        this.equipmentCollection = equipmentCollection;
    }

    /**
     * 設備名一覧を取得する。
     *
     * @return 設備名一覧
     */
    public List<String> getEquipmentNameCollection() {
        return this.equipmentNameCollection;
    }

    /**
     * 設備名一覧を設定する。
     *
     * @param equipmentNameCollection 設備名一覧
     */
    public void setEquipmentNameCollection(List<String> equipmentNameCollection) {
        this.equipmentNameCollection = equipmentNameCollection;
    }

    /**
     * 親設備フラグを取得する。
     * ※．設備ID一覧の指定があり、親設備フラグが有効な場合、設備ID一覧に親設備のIDを追加する。
     *
     * @return 親設備フラグ
     */
    public Boolean getEquipmentIdWithParent() {
        return this.equipmentIdWithParent;
    }

    /**
     * 親設備フラグを設定する。
     * ※．設備ID一覧の指定があり、親設備フラグが有効な場合、設備ID一覧に親設備のIDを追加する。
     *
     * @param equipmentIdWithParent 親設備フラグ
     */
    public void setEquipmentIdWithParent(Boolean equipmentIdWithParent) {
        this.equipmentIdWithParent = equipmentIdWithParent;
    }

    /**
     * 組織ID一覧を取得する。
     *
     * @return 組織ID一覧
     */
    public List<Long> getOrganizationCollection() {
        return this.organizationCollection;
    }

    /**
     * 組織ID一覧を設定する。
     *
     * @param organizationCollection 組織ID一覧
     */
    public void setOrganizationCollection(List<Long> organizationCollection) {
        this.organizationCollection = organizationCollection;
    }

    /**
     * 組織名一覧を取得する。
     *
     * @return 組織名一覧
     */
    public List<String> getOrganizationNameCollection() {
        return this.organizationNameCollection;
    }

    /**
     * 組織名一覧を設定する。
     *
     * @param organizationNameCollection 組織名一覧
     */
    public void setOrganizationNameCollection(List<String> organizationNameCollection) {
        this.organizationNameCollection = organizationNameCollection;
    }

    /**
     * 親組織フラグを取得する。
     * ※．組織ID一覧の指定があり、親組織フラグが有効な場合、組織ID一覧に親組織のIDを追加する。
     *
     * @return 親組織フラグ
     */
    public Boolean getOrganizationIdWithParent() {
        return this.organizationIdWithParent;
    }

    /**
     * 親組織フラグを設定する。
     * ※．組織ID一覧の指定があり、親組織フラグが有効な場合、組織ID一覧に親組織のIDを追加する。
     *
     * @param organizationIdWithParent 親組織フラグ
     */
    public void setOrganizationIdWithParent(Boolean organizationIdWithParent) {
        this.organizationIdWithParent = organizationIdWithParent;
    }

    public List<PropertyCondition> getPropertyCollection() {
        return propertyCollection;
    }

    public void setPropertyCollection(List<PropertyCondition> propertyCollection) {
        this.propertyCollection = propertyCollection;
    }
    
    /**
     * 詳細情報を取得してセットするかを取得する。
     *
     * @return 詳細情報を取得してセットするか (true: する, false: しない)
     */
    public boolean isAdditionalInfo() {
        return this.isAdditionalInfo;
    }

    /**
     * 詳細情報を取得してセットするかを設定する。
     *
     * @param isAdditionalInfo 詳細情報を取得してセットするか (true: する, false: しない)
     */
    public void setIsAdditionalInfo(boolean isAdditionalInfo) {
        this.isAdditionalInfo = isAdditionalInfo;
    }

    /**
     * モデル名を取得する。(ver.1.8.2 からの検索条件)
     *
     * @return モデル名
     */
    public String getModelName() {
        return this.modelName;
    }

    /**
     * モデル名を設定する。(ver.1.8.2 からの検索条件)
     *
     * @param modelName モデル名
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * 工程IDを取得する。
     * 
     * @return 工程ID
     */
    public Long getWorkId() {
        return this.workId;
    }

    /**
     * 工程IDを設定する。
     * 
     * @param workId 工程ID
     */
    public void setWorkId(Long workId) {
        this.workId = workId;
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
     * @param workKanbanIds 工程カンバンID一覧
     */
    public void setWorkKanbanCollection(List<Long> workKanbanIds) {
        this.workKanbanCollection = workKanbanIds;
    }
    
    /**
     * ログインユーザーIDを取得する。
     * 
     * @return ログインユーザーID
     */
    public Long getLoginUserId() {
        return this.loginUserId;
    }

    /**
     * ログインユーザーIDを設定する。
     * 
     * @param loginUserId ログインユーザーID
     */
    public void setLoginUserId(Long loginUserId) {
        this.loginUserId = loginUserId;
    }
    
    /**
     * 子組織を含むかどうかを取得する。
     * 
     * @return True=子組織を含む False=子組織を含まない
     */
    public boolean getIncludeChildOrganizaitonFlag() {
        return this.includeChildOrganizaitonFlag;
    }

    /**
     * 子組織を含むかどうかを設定する。
     * 
     * @param includeChildOrganizaitonFlag 設定値
     */
    public void setIncludeChildOrganizaitonFlag(boolean includeChildOrganizaitonFlag) {
        this.includeChildOrganizaitonFlag = includeChildOrganizaitonFlag;
    }
    
    /**
     * 要実績出力フラグを取得する。
     * 
     * @return 要実績出力フラグ
     */
    public Boolean getOutputFlag() {
        return this.outputFlag;
    }

    /**
     * 要実績出力フラグを設定する。
     * 
     * @param outputFlag 要実績出力フラグ
     */
    public void setOutputFlag(Boolean outputFlag) {
        this.outputFlag = outputFlag;
    }
    
    /**
     * 完了日時範囲の先頭 (Json用)を取得する。
     * 
     * @return 完了日時範囲の先頭 (Json用)
     */
    public String getFromActualComp() {
        return this.fromActualComp;
    }
    
    /**
     * 完了日時範囲の先頭 (Json用)を設定する。
     * @param fromActualComp 完了日時範囲の先頭 (Json用)
     */
    public void setFromActualComp(String fromActualComp) {
        this.fromActualComp = fromActualComp;
    }

    /**
     * 実績開始日時を取得する。
     *
     * @return 実績開始日時
     */
    public Date getFromActualStartTime() {
        if (Objects.isNull(this.fromActualStartTime) && !StringUtils.isEmpty(this.fromActualStart)) {
            // Json の場合は日時は文字列で送られてくる。
            try {
                SimpleDateFormat df = new SimpleDateFormat(DATETIME_FORMAT);
                this.fromActualStartTime = df.parse(this.fromActualStart);
            } catch (ParseException ex) {
            }
        }
        return this.fromActualStartTime;
    }

    public String getFromActualStart() {
        return fromActualStart;
    }

    public void setFromActualStart(String fromActualStart) {
        this.fromActualStart = fromActualStart;
    }

    /**
     * 完了日時範囲の先頭を取得する。
     *
     * @return 完了日時範囲の先頭
     */
    public Date getFromActualCompTime() {
        if (Objects.isNull(this.fromActualCompTime) && !StringUtils.isEmpty(this.fromActualComp)) {
            // Json の場合は日時は文字列で送られてくる。
            try {
                SimpleDateFormat df = new SimpleDateFormat(DATETIME_FORMAT);
                this.fromActualCompTime = df.parse(this.fromActualComp);
            } catch (ParseException ex) {
            }
        }
        return this.fromActualCompTime;
    }
   
    /**
     * 完了日時範囲の先頭を設定する。
     *
     * @param fromActualCompTime 完了日時範囲の先頭
     */
    public void setFromActualCompTime(Date fromActualCompTime) {
        this.fromActualCompTime = fromActualCompTime;
    }
    
    /**
     * 完了日時範囲の末尾 (Json用)を取得する。
     * 
     * @return 完了日時範囲の末尾 (Json用)
     */
    public String getToActualComp() {
        return this.toActualComp;
    }
    
    /**
     * 完了日時範囲の末尾 (Json用)を設定する。
     * @param toActualComp 完了日時範囲の末尾 (Json用)
     */
    public void setToActualComp(String toActualComp) {
        this.toActualComp = toActualComp;
    }

    /**
     * 完了日時範囲の末尾を取得する。
     *
     * @return 完了日時範囲の末尾
     */
    public Date getToActualCompTime() {
        if (Objects.isNull(this.toActualCompTime) && !StringUtils.isEmpty(this.toActualComp)) {
            // Json の場合は日時は文字列で送られてくる。
            try {
                SimpleDateFormat df = new SimpleDateFormat(DATETIME_FORMAT);
                this.toActualCompTime = df.parse(this.toActualComp);
            } catch (ParseException ex) {
            }
        }
        return this.toActualCompTime;
    }

    /**
     * 完了日時範囲の末尾を設定する。
     *
     * @param toActualCompTime 完了日時範囲の末尾
     */
    public void setToActualCompTime(Date toActualCompTime) {
        this.toActualCompTime = toActualCompTime;
    }

    /**
     * 製造番号を取得する。
     * 
     * @return 製造番号
     */
    public String getProductionNumber() {
        return productionNumber;
    }

    /**
     * 製造番号を設定する。
     * 
     * @param productionNumber 製造番号 
     */
    public void setProductionNumber(String productionNumber) {
        this.productionNumber = productionNumber;
    }

    /**
     * マッチタイプを取得する。
     * 
     * @return 
     */
    public MatchTypeEnum getMatchType() {
        return matchType;
    }

    /**
     * マッチタイプを設定する。
     * 
     * @param matchType 
     */
    public void setMatchType(MatchTypeEnum matchType) {
        this.matchType = matchType;
    }

    /**
     * 検索モードを取得する。
     * 
     * @return 検索モード 0:条件一致で検索(デフォルト)、1:作業期間で検索
     */
    public Long getSearchMode() {
        return searchMode;
    }

    /**
     * 検索モードを設定する。
     * 
     * @param searchMode 検索モード 0:条件一致で検索(デフォルト)、1:作業期間で検索
     */
    public void setSearchMode(Long searchMode) {
        this.searchMode = searchMode;
    }

    /**
     * 追加工程フラグを取得する。
     * 
     * @return 
     */
    public Boolean getSeparateWorkFlag() {
        return separateWorkFlag;
    }

    /**
     * 追加工程フラグを設定する。
     * 
     * @param separateWorkFlag 
     */
    public void setSeparateWorkFlag(Boolean separateWorkFlag) {
        this.separateWorkFlag = separateWorkFlag;
    }

    /**
     * ハッシュ値を返す。
     * 
     * @return ハッシュ値
     */
    @Override
    public int hashCode() {
        int hash = 3;
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        final KanbanSearchCondition other = (KanbanSearchCondition) obj;
        return true;
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("KanbanSearchCondition{")
                .append("hierarchyId=").append(this.hierarchyId)
                .append(", kanbanId=").append(this.kanbanId)
                .append(", kanbanName=").append(this.kanbanName)
                .append(", kanbanSubname=").append(this.kanbanSubname)
                .append(", workflowIdCollection=").append(this.workflowIdCollection)
                .append(", fromDate=").append(this.fromDate)
                .append(", toDate=").append(this.toDate)
                .append(", fromActualDate=").append(this.fromActualDate)
                .append(", toActualDate=").append(this.toActualDate)
                .append(", actualOrganizationNameCollection=").append(this.actualOrganizationNameCollection)
                .append(", implementFlag=").append(this.implementFlag)
                .append(", skipFlag=").append(this.skipFlag)
                .append(", kanbanStatusCollection=").append(this.kanbanStatusCollection)
                .append(", parentStatusCollection=").append(this.parentStatusCollection)
                .append(", equipmentCollection=").append(this.equipmentCollection)
                .append(", equipmentNameCollection=").append(this.equipmentNameCollection)
                .append(", equipmentIdWithParent=").append(this.equipmentIdWithParent)
                .append(", organizationCollection=").append(this.organizationCollection)
                .append(", organizationNameCollection=").append(this.organizationNameCollection)
                .append(", organizationIdWithParent=").append(this.organizationIdWithParent)
                .append(", modelName=").append(this.modelName)
                .append(", isAdditionalInfo=").append(this.isAdditionalInfo)
                .append(", workId=").append(this.workId)
                .append(", workKanbanCollection=").append(this.workKanbanCollection)
                .append(", fromActualStartTime=").append(this.getFromActualStartTime())
                .append(", fromActualCompTime=").append(this.fromActualCompTime)
                .append(", fromActualComp=").append(this.fromActualComp)
                .append(", toActualCompTime=").append(this.toActualCompTime)
                .append(", toActualComp=").append(this.toActualComp)
                .append(", fromActualDate=").append(this.getFromActualDate())
                .append(", toActualDate=").append(this.getToActualDate())
                .append(", searchMode=").append(this.searchMode)
                .append("}")
                .toString();
    }
}
