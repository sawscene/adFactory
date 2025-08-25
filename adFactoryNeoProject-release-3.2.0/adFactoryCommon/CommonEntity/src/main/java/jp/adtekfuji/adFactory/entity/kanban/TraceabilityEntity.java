/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.kanban;

import adtekfuji.utility.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * トレーサビリティ
 *
 * @author nar-nakamura
 */
@XmlRootElement(name = "traceability")
@XmlAccessorType(XmlAccessType.FIELD)
public class TraceabilityEntity implements Serializable {

    @XmlElement()
    private Long kanbanId;
    @XmlElement()
    private String kanbanName;
    @XmlElement()
    private String modelName;
    @XmlElement()
    private String workflowName;
    @XmlElement()
    private Integer workflowRev;
    @XmlElement()
    private Long workKanbanId;
    @XmlElement()
    private Long actualId;
    @XmlElement()
    private String traceName;
    @XmlElement()
    private Integer traceOrder;
    @XmlElement()
    private Double lowerLimit;
    @XmlElement()
    private Double upperLimit;
    @XmlElement()
    private String traceValue;
    @XmlElement()
    private Boolean traceConfirm;
    @XmlElement()
    private String equipmentName;
    @XmlElement()
    private String organizationName;
    @XmlElement()
    private Date implementDatetime;
    @XmlElement()
    private String traceTag;
    @XmlElement()
    private String traceProps;
    @XmlElement()
    @JsonIgnore
    private Boolean latestFlag ;
    
    @XmlElement
    @JsonProperty("reportDate")
    private String reportDate;
    
    /**
     * コンストラクタ
     */
    public TraceabilityEntity() {
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
     * 工程順名を取得する。
     *
     * @return 工程順名
     */
    public String getWorkflowName() {
        return this.workflowName;
    }

    /**
     * 工程順名を設定する。
     *
     * @param workflowName 工程順名
     */
    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    /**
     * 版数を取得する。
     *
     * @return 版数
     */
    public Integer getWorkflowRev() {
        return this.workflowRev;
    }

    /**
     * 版数を設定する。
     *
     * @param workflowRev 版数
     */
    public void setWorkflowRev(Integer workflowRev) {
        this.workflowRev = workflowRev;
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
     * 項目名を取得する。
     *
     * @return 項目名
     */
    public String getTraceName() {
        return this.traceName;
    }

    /**
     * 項目名を設定する。
     *
     * @param traceName 項目名
     */
    public void setTraceName(String traceName) {
        this.traceName = traceName;
    }

    /**
     * 順を取得する。
     *
     * @return 順
     */
    public Integer getTraceOrder() {
        return this.traceOrder;
    }

    /**
     * 順を設定する。
     *
     * @param traceOrder 順
     */
    public void setTraceOrder(Integer traceOrder) {
        this.traceOrder = traceOrder;
    }

    /**
     * 規格下限を取得する。
     *
     * @return 規格下限
     */
    public Double getLowerLimit() {
        return this.lowerLimit;
    }

    /**
     * 規格下限を設定する。
     *
     * @param lowerLimit 規格下限
     */
    public void setLowerLimit(Double lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    /**
     * 規格上限を取得する。
     *
     * @return 規格上限
     */
    public Double getUpperLimit() {
        return this.upperLimit;
    }

    /**
     * 規格上限を設定する。
     *
     * @param upperLimit 規格上限
     */
    public void setUpperLimit(Double upperLimit) {
        this.upperLimit = upperLimit;
    }

    /**
     * 値を取得する。
     *
     * @return 値
     */
    public String getTraceValue() {
        return this.traceValue;
    }

    /**
     * 値を設定する。
     *
     * @param traceValue 値
     */
    public void setTraceValue(String traceValue) {
        this.traceValue = traceValue;
    }

    /**
     * 確認を取得する。
     *
     * @return 確認
     */
    public Boolean getTraceConfirm() {
        return this.traceConfirm;
    }

    /**
     * 確認を設定する。
     *
     * @param traceConfirm 確認
     */
    public void setTraceConfirm(Boolean traceConfirm) {
        this.traceConfirm = traceConfirm;
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
     * 組織名を取得する。
     *
     * @return 組織名
     */
    public String getOrganizationName() {
        return this.organizationName;
    }

    /**
     * 組織名を設定する。
     *
     * @param organizationName 組織名
     */
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    /**
     * 作業日時を取得する。
     *
     * @return 作業日時
     */
    public Date getImplementDatetime() {
        if (Objects.isNull(this.implementDatetime) && !StringUtils.isEmpty(this.reportDate)) {
            // adProductWebの場合、文字列で実施日時が送られる
            try {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
                this.implementDatetime = df.parse(this.reportDate);
            } catch (ParseException ex) {
            }
        }
        return this.implementDatetime;
    }

    /**
     * 作業日時を設定する。
     *
     * @param implementDatetime 作業日時
     */
    public void setImplementDatetime(Date implementDatetime) {
        this.implementDatetime = implementDatetime;
    }

    /**
     * タグを設定する。
     *
     * @return タグ
     */
    public String getTraceTag() {
        return this.traceTag;
    }

    /**
     * タグを取得する。
     *
     * @param traceTag タグ
     */
    public void setTraceTag(String traceTag) {
        this.traceTag = traceTag;
    }

    /**
     * 追加トレーサビリティ一覧を取得する。
     *
     * @return 追加トレーサビリティ一覧
     */
    public String getTraceProps() {
        return this.traceProps;
    }

    /**
     * 追加トレーサビリティ一覧を設定する。
     *
     * @param traceProps 追加トレーサビリティ一覧
     */
    public void setTraceProps(String traceProps) {
        this.traceProps = traceProps;
    }

    /**
     * 最終フラグを取得する。
     *
     * @return 最終フラグ
     */
    public Boolean getLatestFlag() {
        return this.latestFlag;
    }

    /**
     * 最終フラグを設定する。
     *
     * @param latestFlag 最終フラグ
     */
    public void setLatestFlag(Boolean latestFlag) {
        this.latestFlag = latestFlag;
    }

    @Override
    public String toString() {
        return new StringBuilder("TraceabilityEntity{")
                .append("kanbanId=").append(this.kanbanId)
                .append(", kanbanName=").append(this.kanbanName)
                .append(", modelName=").append(this.modelName)
                .append(", workflowName=").append(this.workflowName)
                .append(", workflowRev=").append(this.workflowRev)
                .append(", workKanbanId=").append(this.workKanbanId)
                .append(", actualId=").append(this.actualId)
                .append(", traceName=").append(this.traceName)
                .append(", traceOrder=").append(this.traceOrder)
                .append(", lowerLimit=").append(this.lowerLimit)
                .append(", upperLimit=").append(this.upperLimit)
                .append(", traceValue=").append(this.traceValue)
                .append(", traceConfirm=").append(this.traceConfirm)
                .append(", equipmentName=").append(this.equipmentName)
                .append(", organizationName=").append(this.organizationName)
                .append(", implementDatetime=").append(this.implementDatetime)
                .append(", traceTag=").append(this.traceTag)
                .append(", latestFlag=").append(this.latestFlag)
                .append("}")
                .toString();
    }
}
