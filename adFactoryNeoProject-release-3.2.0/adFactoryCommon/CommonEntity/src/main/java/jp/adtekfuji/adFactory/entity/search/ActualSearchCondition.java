/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.search;

import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.ActualResultDailyEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

/**
 * 工程実績情報 検索条件
 *
 * @author ke.yokoi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "actualSearchCondition")
@JsonIgnoreProperties(ignoreUnknown=true)
public class ActualSearchCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private Long kanbanId = null;// カンバンID

    @XmlElement()
    private String kanbanName = null;// カンバン名

    @XmlElement()
    private String kanbanSubname = null;// サブカンバン名

    @XmlElement()
    private Long workflowId = null;// 工程順ID

    @XmlElement()
    @JsonIgnore
    private Date fromDate = null;// 日時範囲の先頭

    @XmlElement(name = "jsonFromDate")
    @JsonProperty("jsonFromDate")
    private String jsonFromDate;

    @XmlElement()
    @JsonIgnore
    private Date toDate = null;// 日時範囲の末尾

    @XmlElement(name = "jsonToDate")
    @JsonProperty("jsonToDate")
    private String jsonToDate;

    @XmlElementWrapper(name = "workKanbans")
    @XmlElement(name = "workKanban")
    private List<Long> workKanbanCollection = null;// 工程カンバンID一覧

    @XmlElementWrapper(name = "kanbanStatuses")
    @XmlElement(name = "kanbanStatus")
    private List<KanbanStatusEnum> kanbanStatusCollection = null;// ステータス一覧

    @XmlElementWrapper(name = "equipmentIds")
    @XmlElement(name = "equipmentId")
    private List<Long> equipmentCollection = null;// 設備ID一覧

    @XmlElementWrapper(name = "equipmentNames")
    @XmlElement(name = "equipmentName")
    private List<String> equipmentNameCollection = null;// 設備名一覧

    @XmlElementWrapper(name = "organizationIds")
    @XmlElement(name = "organizationId")
    private List<Long> organizationCollection = null;// 組織ID一覧

    @XmlElementWrapper(name = "organizationNames")
    @XmlElement(name = "organizationName")
    private List<String> organizationNameCollection = null;// 組織名一覧

    @XmlElement()
    private String interruptReason = null;// 中断理由

    @XmlElement()
    private String delayReason = null;// 遅延理由

    @XmlElement()
    private Boolean exportedFlag = null;

    @XmlElement()
    private Boolean equipmentIsNull = null;// 設備なしの実績か

    @XmlElement()
    private Boolean isOrderDesc = false;// ソート順を降順にするか (true: する(降順), false: しない(昇順))

    /**
     * 工程名
     */
    @XmlElementWrapper(name = "workNames")
    @XmlElement(name = "workName")
    private List<String> workNameCollection = null;// 工程名一覧

    @XmlElementWrapper(name = "workIds")
    @XmlElement(name = "workId")
    private List<Long> workCollection = null;// 工程ID

    @XmlElement()
    private ActualResultDailyEnum resultDailyEnum = null;// 実績範囲(DAILY, ALL)

    @XmlElement()
    private String modelName = null;// モデル名

    @XmlElement()
    private String serialNo;

    @XmlElement()
    private Boolean checkRemoveFlag;

    /**
     * コンストラクタ
     */
    public ActualSearchCondition() {
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
    public ActualSearchCondition(Long kanbanId, String kanbanName, String kanbanSubname, Long workflowId, Date fromDate, Date toDate) {
        this.kanbanId = kanbanId;
        this.kanbanName = kanbanName;
        this.kanbanSubname = kanbanSubname;
        this.workflowId = workflowId;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    /**
     * カンバンIDを設定して、検索条件を取得する。
     *
     * @param kanbanId カンバンID
     * @return 検索条件
     */
    public ActualSearchCondition kanbanId(Long kanbanId) {
        this.kanbanId = kanbanId;
        return this;
    }

    /**
     * カンバン名を設定して、検索条件を取得する。
     *
     * @param kanbanName カンバン名
     * @return 検索条件
     */
    public ActualSearchCondition kanbanName(String kanbanName) {
        this.kanbanName = kanbanName;
        return this;
    }

    /**
     * サブカンバン名を設定して、検索条件を取得する。
     *
     * @param kanbanSubname サブカンバン名
     * @return 検索条件
     */
    public ActualSearchCondition kanbanSubname(String kanbanSubname) {
        this.kanbanSubname = kanbanSubname;
        return this;
    }

    /**
     * 工程順IDを設定して、検索条件を取得する。
     *
     * @param workflowId 工程順ID
     * @return 検索条件
     */
    public ActualSearchCondition workflowId(Long workflowId) {
        this.workflowId = workflowId;
        return this;
    }

    /**
     * 日時範囲の先頭を設定して、検索条件を取得する。
     *
     * @param fromDate 日時範囲の先頭
     * @return 検索条件
     */
    public ActualSearchCondition fromDate(Date fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    /**
     * 日時範囲の末尾を設定して、検索条件を取得する。
     *
     * @param toDate 日時範囲の末尾
     * @return 検索条件
     */
    public ActualSearchCondition toDate(Date toDate) {
        this.toDate = toDate;
        return this;
    }

    /**
     * カンバンID一覧を設定して、検索条件を取得する。
     *
     * @param workKanbanCollection カンバンID一覧
     * @return 検索条件
     */
    public ActualSearchCondition workKanbanList(List<Long> workKanbanCollection) {
        this.workKanbanCollection = workKanbanCollection;
        return this;
    }

    /**
     * ステータス一覧を設定して、検索条件を取得する。
     *
     * @param statusList ステータス一覧
     * @return 検索条件
     */
    public ActualSearchCondition statusList(List<KanbanStatusEnum> statusList) {
        this.kanbanStatusCollection = statusList;
        return this;
    }

    /**
     * 設備ID一覧を設定して、検索条件を取得する。
     *
     * @param equipmentList 設備ID一覧
     * @return 検索条件
     */
    public ActualSearchCondition equipmentList(List<Long> equipmentList) {
        this.equipmentCollection = equipmentList;
        return this;
    }

    /**
     * 設備名一覧を設定して、検索条件を取得する。
     *
     * @param equipmentNameList 設備名一覧
     * @return 検索条件
     */
    public ActualSearchCondition equipmentNameList(List<String> equipmentNameList) {
        this.equipmentNameCollection = equipmentNameList;
        return this;
    }

    /**
     * 組織ID一覧を設定して、検索条件を取得する。
     *
     * @param organizationList 組織ID一覧
     * @return 検索条件
     */
    public ActualSearchCondition organizationList(List<Long> organizationList) {
        this.organizationCollection = organizationList;
        return this;
    }

    /**
     * 組織名一覧を設定して、検索条件を取得する。
     *
     * @param organizationNameList 組織名一覧
     * @return 検索条件
     */
    public ActualSearchCondition organizationNameList(List<String> organizationNameList) {
        this.organizationNameCollection = organizationNameList;
        return this;
    }

    /**
     * 中断理由を設定して、検索条件を取得する。
     *
     * @param interruptReason 中断理由
     * @return 検索条件
     */
    public ActualSearchCondition interruptReason(String interruptReason) {
        this.interruptReason = interruptReason;
        return this;
    }

    /**
     * 遅延理由を設定して、検索条件を取得する。
     *
     * @param delayReason 遅延理由
     * @return 検索条件
     */
    public ActualSearchCondition delayReason(String delayReason) {
        this.delayReason = delayReason;
        return this;
    }

    /**
     * 工程名一覧を設定して、検索条件を取得する。
     *
     * @param workNameList 工程名一覧
     * @return 検索条件
     */
    public ActualSearchCondition workNameList(List<String> workNameList) {
        this.workNameCollection = workNameList;
        return this;
    }

    /**
     * 工程ID一覧を設定して、検索条件を取得する。
     *
     * @param workCollection 工程ID一覧
     * @return 検索条件
     */
    public ActualSearchCondition workList(List<Long> workCollection) {
        this.workCollection = workCollection;
        return this;
    }

    /**
     * 実績範囲を設定して、検索条件を取得する。
     *
     * @param resultDailyEnum 実績範囲(DAILY, ALL)
     * @return 検索条件
     */
    public ActualSearchCondition resultDailyEnum(ActualResultDailyEnum resultDailyEnum) {
        this.resultDailyEnum = resultDailyEnum;
        return this;
    }

    /**
     * エクスポート済フラグを設定して、検索条件を取得する。
     *
     * @param exportedFlag (true: エクスポート済, false: エクスポート未実施)
     * @return
     */
    public ActualSearchCondition exportedFlag(Boolean exportedFlag) {
        this.exportedFlag = exportedFlag;
        return this;
    }
    
    /**
     * 設備なしの実績かを設定して、検索条件を取得する。
     *
     * @param equipmentIsNull (true: 設備なしの実績, false: 設備ありの実績)
     * @return 検索条件
     */
    public ActualSearchCondition equipmentIsNull(Boolean equipmentIsNull) {
        this.equipmentIsNull = equipmentIsNull;
        return this;
    }

    /**
     * モデル名を設定して、検索条件を取得する。
     *
     * @param modelName モデル名
     * @return 検索条件
     */
    public ActualSearchCondition modelName(String modelName) {
        this.modelName = modelName;
        return this;
    }

    /**
     * ソート順を降順にするか
     *
     * @param isOrderDesc (true: する(降順), false: しない(昇順))
     * @return 検索条件
     */
    public ActualSearchCondition isOrderDesc(Boolean isOrderDesc) {
        this.isOrderDesc = isOrderDesc;
        return this;
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
     * 工程順IDを取得する。
     *
     * @return 工程順ID
     */
    public Long getWorkflowId() {
        return this.workflowId;
    }

    /**
     * 工程順IDを設定する。
     *
     * @param workflowId 工程順ID
     */
    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    /**
     * 日時範囲の先頭を取得する。
     *
     * @return 日時範囲の先頭
     */
    public Date getFromDate() {
        if (Objects.isNull(this.fromDate) && !StringUtils.isEmpty(this.jsonFromDate)) {
            this.fromDate = DateUtils.parseJson(this.jsonFromDate);
        }
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
     * 日時範囲の先頭を取得する。
     *
     * @return 日時範囲の先頭
     */
    public String getJsonFromDate() {
        return this.jsonFromDate;
    }

    /**
     * 日時範囲の先頭を設定する。
     *
     * @param jsonFromDate 日時範囲の先頭
     */
    public void setJsonFromDate(String jsonFromDate) {
        this.jsonFromDate = jsonFromDate;
    }

    /**
     * 日時範囲の末尾を取得する。
     *
     * @return 日時範囲の末尾
     */
    public Date getToDate() {
        if (Objects.isNull(this.toDate) && !StringUtils.isEmpty(this.jsonToDate)) {
            this.toDate = DateUtils.parseJson(this.jsonToDate);
        }
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
     * 日時範囲の末尾を取得する。
     *
     * @return 日時範囲の末尾
     */
    public String getJsonToDate() {
        return this.jsonToDate;
    }

    /**
     * 日時範囲の末尾を設定する。
     *
     * @param jsonToDate 日時範囲の末尾
     */
    public void setJsonToDate(String jsonToDate) {
        this.jsonToDate = jsonToDate;
    }

    /**
     * カンバンID一覧を取得する。
     *
     * @return カンバンID一覧
     */
    public List<Long> getWorkKanbanCollection() {
        return this.workKanbanCollection;
    }

    /**
     * カンバンID一覧を設定する。
     *
     * @param workKanbanCollection カンバンID一覧
     */
    public void setWorkKanbanCollection(List<Long> workKanbanCollection) {
        this.workKanbanCollection = workKanbanCollection;
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
     * 工程名一覧を取得する。
     *
     * @return 工程名一覧
     */
    public List<String> getWorkNameCollection() {
        return this.workNameCollection;
    }

    /**
     * 工程名一覧を設定する。
     *
     * @param workNameCollection 工程名一覧
     */
    public void setWorkNameCollection(List<String> workNameCollection) {
        this.workNameCollection = workNameCollection;
    }

    /**
     * 工程ID一覧を取得する。
     *
     * @return 工程ID一覧
     */
    public List<Long> getWorkCollection() {
        return this.workCollection;
    }

    /**
     * 工程ID一覧を設定する。
     *
     * @param workCollection 工程ID一覧
     */
    public void setWorkCollection(List<Long> workCollection) {
        this.workCollection = workCollection;
    }

    /**
     * 実績範囲を取得する。
     *
     * @return 実績範囲(DAILY, ALL)
     */
    public ActualResultDailyEnum getResultDailyEnum() {
        return this.resultDailyEnum;
    }

    /**
     * 実績範囲を設定する。
     *
     * @param resultDailyEnum 実績範囲(DAILY, ALL)
     */
    public void setResultDailyEnum(ActualResultDailyEnum resultDailyEnum) {
        this.resultDailyEnum = resultDailyEnum;
    }

    /**
     * エクスポート済フラグを取得する。
     *
     * @return (true: エクスポート済み, false または null: エクスポート未実施)
     */
    public Boolean getExportedFlag() {
        return this.exportedFlag;
    }

    /**
     * エクスポート済フラグを設定する。
     *
     * @param exportedFlag (true: エクスポート済み, false または null: エクスポート未実施)
     */
    public void setExportedFlag(Boolean exportedFlag) {
        this.exportedFlag = exportedFlag;
    }
    
    /**
     * 設備なしの実績かを取得する。
     *
     * @return (true: 設備なしの実績, false: 設備ありの実績)
     */
    public Boolean getEquipmentIsNull() {
        return this.equipmentIsNull;
    }

    /**
     * 設備なしの実績かを設定する。
     *
     * @param equipmentIsNull (true: 設備なしの実績, false: 設備ありの実績)
     */
    public void setEquipmentIsNull(Boolean equipmentIsNull) {
        this.equipmentIsNull = equipmentIsNull;
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
     * ソート順を降順にするかを取得する。
     *
     * @return (true: する(降順), false: しない(昇順))
     */
    public Boolean isOrderDesc() {
        return this.isOrderDesc;
    }

    /**
     * ソート順を降順にするかを設定する。
     *
     * @param isOrderDesc (true: する(降順), false: しない(昇順))
     */
    public void setOrderDesc(Boolean isOrderDesc) {
        this.isOrderDesc = isOrderDesc;
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
     * シリアル番号を設定する。
     * 
     * @param serialNo シリアル番号
     */
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }


    public Boolean getCheckRemoveFlag() {
        return checkRemoveFlag;
    }

    public void setCheckRemoveFlag(Boolean checkRemoveFlag) {
        this.checkRemoveFlag = checkRemoveFlag;
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
     * @param object オブジェクト
     * @return true:オブジェクトが一致、false:オブジェクトが不一致
     */    
    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        return getClass() == object.getClass();
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("ActualSearchCondition{")
                .append("kanbanId=").append(this.kanbanId)
                .append(", kanbanName=").append(this.kanbanName)
                .append(", kanbanSubname=").append(this.kanbanSubname)
                .append(", workflowId=").append(this.workflowId)
                .append(", fromDate=").append(this.fromDate)
                .append(", jsonFromDate=").append(this.jsonFromDate)
                .append(", toDate=").append(this.toDate)
                .append(", jsonToDate=").append(this.jsonToDate)
                .append(", workKanbanCollection=").append(this.workKanbanCollection)
                .append(", kanbanStatusCollection=").append(this.kanbanStatusCollection)
                .append(", equipmentCollection=").append(this.equipmentCollection)
                .append(", equipmentNameCollection=").append(this.equipmentNameCollection)
                .append(", organizationCollection=").append(this.organizationCollection)
                .append(", organizationNameCollection=").append(this.organizationNameCollection)
                .append(", interruptReason=").append(this.interruptReason)
                .append(", delayReason=").append(this.delayReason)
                .append(", equipmentIsNull=").append(this.equipmentIsNull)
                .append(", workNameCollection=").append(this.workNameCollection)
                .append(", workCollection=").append(this.workCollection)
                .append(", resultDailyEnum=").append(this.resultDailyEnum)
                .append(", modelName=").append(this.modelName)
                .append("}")
                .toString();
    }
}
