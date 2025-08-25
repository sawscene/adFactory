/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.search;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

/**
 * 実績出力情報の検索条件
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "reportOutSearchCondition")
public class ReportOutSearchCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlEnum(String.class)
    public enum ReportOutSortEnum {
        IMPLEMENT_DATETIME,// 実施日時
        ACTUAL_ID;// 工程実績ID
    }

    @XmlElement()
    private Date fromDate = null;
    @XmlElement()
    private Date toDate = null;
    @XmlElementWrapper(name = "actualStatuses")
    @XmlElement(name = "actualStatus")
    private List<KanbanStatusEnum> actualStatusCollection = null;

    // 1.8.1 までの検索条件
    @XmlElement()
    private Long workflowId = null;
    @XmlElementWrapper(name = "organizationNames")
    @XmlElement(name = "organizationName")
    private List<String> organizationNameCollection = null;

    // 1.8.2 からの検索条件
    @XmlElementWrapper(name = "kanbanIds")
    @XmlElement(name = "kanbanId")
    private List<Long> kanbanIdCollection = null;
    @XmlElementWrapper(name = "workflowIds")
    @XmlElement(name = "workflowId")
    private List<Long> workflowIdCollection = null;
    @XmlElementWrapper(name = "workIds")
    @XmlElement(name = "workId")
    private List<Long> workIdCollection = null;
    @XmlElementWrapper(name = "workKanbanIds")
    @XmlElement(name = "workKanbanId")
    private List<Long> workKanbanIdCollection = null;
    @XmlElementWrapper(name = "equipmentIds")
    @XmlElement(name = "equipmentId")
    private List<Long> equipmentIdCollection = null;
    @XmlElementWrapper(name = "organizationIds")
    @XmlElement(name = "organizationId")
    private List<Long> organizationIdCollection = null;
    @XmlElement()
    private String kanbanName = null;
    @XmlElement()
    private String modelName = null;
    @XmlElement()
    private String interruptReason = null;
    @XmlElement()
    private String delayReason = null;
    @XmlElement()
    private ReportOutSortEnum sortType = ReportOutSortEnum.IMPLEMENT_DATETIME;

    @XmlElement()
    private String productionNumber; // 製造番号
    
    @XmlTransient
    private boolean distinctWorkKanban;

    @XmlTransient
    private boolean countLatestEquipment;

    /**
     * コンストラクタ
     */
    public ReportOutSearchCondition() {
    }

    /**
     * 実施日時範囲の先頭を設定する。
     *
     * @param fromDate 実施日時範囲の先頭
     * @return 実績出力情報の検索条件
     */
    public ReportOutSearchCondition fromDate(Date fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    /**
     * 実施日時範囲の末尾を設定する。
     *
     * @param toDate 実施日時範囲の末尾
     * @return 実績出力情報の検索条件
     */
    public ReportOutSearchCondition toDate(Date toDate) {
        this.toDate = toDate;
        return this;
    }

    /**
     * 工程実績ステータス一覧を設定する。
     *
     * @param statusList 工程実績ステータス一覧
     * @return 実績出力情報の検索条件
     */
    public ReportOutSearchCondition statusList(List<KanbanStatusEnum> statusList) {
        this.actualStatusCollection = statusList;
        return this;
    }

    /**
     * 工程順IDを設定する。(ver.1.8.1 までの検索条件)
     *
     * @param workflowId 工程順ID
     * @return 実績出力情報の検索条件
     */
    public ReportOutSearchCondition workflowId(Long workflowId) {
        this.workflowId = workflowId;
        return this;
    }

    /**
     * 組織名一覧を設定する。(ver.1.8.1 までの検索条件)
     *
     * @param organizationNameList 組織名一覧
     * @return 実績出力情報の検索条件
     */
    public ReportOutSearchCondition organizationNameList(List<String> organizationNameList) {
        this.organizationNameCollection = organizationNameList;
        return this;
    }

    /**
     * カンバンID一覧を設定する。(ver.1.8.2 からの検索条件)
     *
     * @param kanbanIdList カンバンID一覧
     * @return 実績出力情報の検索条件
     */
    public ReportOutSearchCondition kanbanIdList(List<Long> kanbanIdList) {
        this.kanbanIdCollection = kanbanIdList;
        return this;
    }

    /**
     * 工程順ID一覧を設定する。(ver.1.8.2 からの検索条件)
     *
     * @param workflowIdList 工程順ID一覧
     * @return 実績出力情報の検索条件
     */
    public ReportOutSearchCondition workflowIdList(List<Long> workflowIdList) {
        this.workflowIdCollection = workflowIdList;
        return this;
    }

    /**
     * 工程ID一覧を設定する。(ver.1.8.2 からの検索条件)
     *
     * @param workIdList 工程ID一覧
     * @return 実績出力情報の検索条件
     */
    public ReportOutSearchCondition workIdList(List<Long> workIdList) {
        this.workIdCollection = workIdList;
        return this;
    }

    /**
     * 工程カンバンID一覧を設定する。(ver.1.8.2 からの検索条件)
     *
     * @param workKanbanIdList 工程カンバンID一覧
     * @return 実績出力情報の検索条件
     */
    public ReportOutSearchCondition workKanbanIdList(List<Long> workKanbanIdList) {
        this.workKanbanIdCollection = workKanbanIdList;
        return this;
    }

    /**
     * 設備ID一覧を設定する。(ver.1.8.2 からの検索条件)
     *
     * @param equipmentIdList 設備ID一覧
     * @return 実績出力情報の検索条件
     */
    public ReportOutSearchCondition equipmentIdList(List<Long> equipmentIdList) {
        this.equipmentIdCollection = equipmentIdList;
        return this;
    }

    /**
     * 組織ID一覧を設定する。(ver.1.8.2 からの検索条件)
     *
     * @param organizationIdList 組織ID一覧
     * @return 実績出力情報の検索条件
     */
    public ReportOutSearchCondition organizationIdList(List<Long> organizationIdList) {
        this.organizationIdCollection = organizationIdList;
        return this;
    }

    /**
     * カンバン名を設定する。(ver.1.8.2 からの検索条件)
     *
     * @param kanbanName カンバン名
     * @return 実績出力情報の検索条件
     */
    public ReportOutSearchCondition kanbanName(String kanbanName) {
        this.kanbanName = kanbanName;
        return this;
    }

    /**
     * モデル名を設定する。(ver.1.8.2 からの検索条件)
     *
     * @param modelName モデル名
     * @return 実績出力情報の検索条件
     */
    public ReportOutSearchCondition modelName(String modelName) {
        this.modelName = modelName;
        return this;
    }

    /**
     * 中断理由を設定する。(ver.1.8.2 からの検索条件)
     *
     * @param interruptReason 中断理由
     * @return 実績出力情報の検索条件
     */
    public ReportOutSearchCondition interruptReason(String interruptReason) {
        this.interruptReason = interruptReason;
        return this;
    }

    /**
     * 遅延理由を設定する。(ver.1.8.2 からの検索条件)
     *
     * @param delayReason 遅延理由
     * @return 実績出力情報の検索条件
     */
    public ReportOutSearchCondition delayReason(String delayReason) {
        this.delayReason = delayReason;
        return this;
    }

    /**
     * ソート順を設定する。(ver.1.8.2 からの検索条件)
     *
     * @param sortType ソート順
     * @return 実績出力情報の検索条件
     */
    public ReportOutSearchCondition sortType(ReportOutSortEnum sortType) {
        this.sortType = sortType;
        return this;
    }

    /**
     * 当日工程計画実績フレーム向け設定(v1.8.3～)<br>
     * 同じ工程カンバンを複数人で作業した場合も工程カンバンの実績を一つと数える。
     * 
     * @param distinctWorkKanban trueの場合重複を排除、falseの場合重複も数える
     * @return 
     */
    public ReportOutSearchCondition distinctWorkKanban(boolean distinctWorkKanban) {
        this.distinctWorkKanban = distinctWorkKanban;
        return this;
    }

    /**
     * 工程計画実績フレーム向け設定(v1.8.3～)<br>
     * 同じ工程カンバンを複数設備から作業した場合、最後に完了を押した設備を実績として数える。
     * 
     * @param countLatestEquipment trueの場合最後の完了設備で数える。falseの場合完了を押した設備すべてでカウント
     * @return 
     */
    public ReportOutSearchCondition countLatestEquipment(boolean countLatestEquipment) {
        this.countLatestEquipment = countLatestEquipment;
        return this;
    }

    /**
     * 実施日時範囲の先頭を取得する。
     *
     * @return 実施日時範囲の先頭
     */
    public Date getFromDate() {
        return this.fromDate;
    }

    /**
     * 実施日時範囲の先頭を設定する。
     *
     * @param fromDate 実施日時範囲の先頭
     */
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * 実施日時範囲の末尾を取得する。
     *
     * @return 実施日時範囲の末尾
     */
    public Date getToDate() {
        return this.toDate;
    }

    /**
     * 実施日時範囲の末尾を設定する。
     * @param toDate 実施日時範囲の末尾
     */
    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    /**
     * 工程実績ステータス一覧を取得する。
     * @return 工程実績ステータス一覧
     */
    public List<KanbanStatusEnum> getActualStatusCollection() {
        return this.actualStatusCollection;
    }

    /**
     * 工程実績ステータス一覧を設定する。
     * @param actualStatusCollection 工程実績ステータス一覧
     */
    public void setActualStatusCollection(List<KanbanStatusEnum> actualStatusCollection) {
        this.actualStatusCollection = actualStatusCollection;
    }

    /**
     * 工程実績IDを取得する。(ver.1.8.1 までの検索条件)
     *
     * @return 工程実績ID
     */
    public Long getWorkflowId() {
        return this.workflowId;
    }

    /**
     * 工程実績IDを設定する。(ver.1.8.1 までの検索条件)
     *
     * @param workflowId 工程実績ID
     */
    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    /**
     * 組織名一覧を取得する。(ver.1.8.1 までの検索条件)
     *
     * @return 組織名一覧
     */
    public List<String> getOrganizationNameCollection() {
        return this.organizationNameCollection;
    }

    /**
     * 組織名一覧を設定する。(ver.1.8.1 までの検索条件)
     *
     * @param organizationNameCollection 組織名一覧
     */
    public void setOrganizationNameCollection(List<String> organizationNameCollection) {
        this.organizationNameCollection = organizationNameCollection;
    }

    /**
     * カンバンID一覧を取得する。(ver.1.8.2 からの検索条件)
     *
     * @return カンバンID一覧
     */
    public List<Long> getKanbanIdCollection() {
        return this.kanbanIdCollection;
    }

    /**
     * カンバンID一覧を設定する。(ver.1.8.2 からの検索条件)
     *
     * @param kanbanIdCollection カンバンID一覧
     */
    public void setKanbanIdCollection(List<Long> kanbanIdCollection) {
        this.kanbanIdCollection = kanbanIdCollection;
    }

    /**
     * 工程順ID一覧を取得する。(ver.1.8.2 からの検索条件)
     *
     * @return 工程順ID一覧
     */
    public List<Long> getWorkflowIdCollection() {
        return this.workflowIdCollection;
    }

    /**
     * 工程順ID一覧を設定する。(ver.1.8.2 からの検索条件)
     *
     * @param workflowIdCollection 工程順ID一覧
     */
    public void setWorkflowIdCollection(List<Long> workflowIdCollection) {
        this.workflowIdCollection = workflowIdCollection;
    }

    /**
     * 工程ID一覧を取得する。(ver.1.8.2 からの検索条件)
     *
     * @return 工程ID一覧
     */
    public List<Long> getWorkIdCollection() {
        return this.workIdCollection;
    }

    /**
     * 工程ID一覧を設定する。(ver.1.8.2 からの検索条件)
     *
     * @param workIdCollection 工程ID一覧
     */
    public void setWorkIdCollection(List<Long> workIdCollection) {
        this.workIdCollection = workIdCollection;
    }

    /**
     * 工程カンバンID一覧を取得する。(ver.1.8.2 からの検索条件)
     *
     * @return 工程カンバンID一覧
     */
    public List<Long> getWorkKanbanIdCollection() {
        return this.workKanbanIdCollection;
    }

    /**
     * 工程カンバンID一覧を設定する。(ver.1.8.2 からの検索条件)
     *
     * @param workKanbanIdCollection 工程カンバンID一覧
     */
    public void setWorkKanbanIdCollection(List<Long> workKanbanIdCollection) {
        this.workKanbanIdCollection = workKanbanIdCollection;
    }

    /**
     * 設備ID一覧を取得する。(ver.1.8.2 からの検索条件)
     *
     * @return 設備ID一覧
     */
    public List<Long> getEquipmentIdCollection() {
        return this.equipmentIdCollection;
    }

    /**
     * 設備ID一覧を設定する。(ver.1.8.2 からの検索条件)
     *
     * @param equipmentIdCollection 設備ID一覧
     */
    public void setEquipmentIdCollection(List<Long> equipmentIdCollection) {
        this.equipmentIdCollection = equipmentIdCollection;
    }

    /**
     * 組織ID一覧を取得する。(ver.1.8.2 からの検索条件)
     *
     * @return 組織ID一覧
     */
    public List<Long> getOrganizationIdCollection() {
        return this.organizationIdCollection;
    }

    /**
     * 組織ID一覧を設定する。(ver.1.8.2 からの検索条件)
     *
     * @param organizationIdCollection 組織ID一覧
     */
    public void setOrganizationIdCollection(List<Long> organizationIdCollection) {
        this.organizationIdCollection = organizationIdCollection;
    }

    /**
     * カンバン名を取得する。(ver.1.8.2 からの検索条件)
     *
     * @return カンバン名
     */
    public String getKanbanName() {
        return this.kanbanName;
    }

    /**
     * カンバン名を設定する。(ver.1.8.2 からの検索条件)
     *
     * @param kanbanName カンバン名
     */
    public void setKanbanName(String kanbanName) {
        this.kanbanName = kanbanName;
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
     * 中断理由を取得する。(ver.1.8.2 からの検索条件)
     *
     * @return 中断理由
     */
    public String getInterruptReason() {
        return this.interruptReason;
    }

    /**
     * 中断理由を設定する。(ver.1.8.2 からの検索条件)
     *
     * @param interruptReason 中断理由
     */
    public void setInterruptReason(String interruptReason) {
        this.interruptReason = interruptReason;
    }

    /**
     * 遅延理由を取得する。(ver.1.8.2 からの検索条件)
     *
     * @return 遅延理由
     */
    public String getDelayReason() {
        return this.delayReason;
    }

    /**
     * 遅延理由を設定する。(ver.1.8.2 からの検索条件)
     *
     * @param delayReason 遅延理由
     */
    public void setDelayReason(String delayReason) {
        this.delayReason = delayReason;
    }

    /**
     * ソート順を取得する。
     *
     * @return ソート順
     */
    public ReportOutSortEnum getSortType() {
        return this.sortType;
    }

    /**
     * ソート順を設定する。
     *
     * @param sortType ソート順
     */
    public void setSortType(ReportOutSortEnum sortType) {
        this.sortType = sortType;
    }

    public boolean isDistinctWorkKanban() {
        return distinctWorkKanban;
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
     * 当日工程計画実績フレーム向け設定を有効にする(v1.8.3からの条件)
     * 
     * @param distinctWorkKanban 
     */
    public void setDistinctWorkKanban(Boolean distinctWorkKanban) {
        this.distinctWorkKanban = distinctWorkKanban;
    }

    public boolean isCountLatestEquipment() {
        return countLatestEquipment;
    }

    /**
     * 工程計画実績フレーム向けの設定を有効にする(v1.8.3からの条件)
     * 
     * @param countLatestEquipment 
     */
    public void setCountLatestEquipment(boolean countLatestEquipment) {
        this.countLatestEquipment = countLatestEquipment;
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現 
     */
    @Override
    public String toString() {
        return new StringBuilder("ReportOutSearchCondition{")
                .append("fromDate=").append(this.fromDate)
                .append(", toDate=").append(this.toDate)
                .append(", actualStatusCollection=").append(this.actualStatusCollection)
                .append(", workflowIdCollection=").append(this.workflowIdCollection)
                .append(", workKanbanIdCollection=").append(this.workKanbanIdCollection)
                .append(", equipmentIdCollection=").append(this.equipmentIdCollection)
                .append(", organizationIdCollection=").append(this.organizationIdCollection)
                .append(", kanbanName=").append(this.kanbanName)
                .append(", modelName=").append(this.modelName)
                .append(", productionNumber").append(this.productionNumber)
                .append(", sortType=").append(this.sortType)
                .append("}")
                .toString();
    }
}
