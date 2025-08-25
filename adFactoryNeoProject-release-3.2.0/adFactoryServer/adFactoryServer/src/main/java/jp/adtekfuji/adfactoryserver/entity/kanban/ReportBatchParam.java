/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.kanban;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.entity.kanban.ActualProductReportEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adfactoryserver.entity.operation.OperationEntity;

/**
 * 工程実績登録情報
 * 
 * @author s-heya
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "param")
@JsonIgnoreProperties(ignoreUnknown=true)
public class ReportBatchParam {
    @XmlElement(required = true)
    @JsonProperty("status")
    private KanbanStatusEnum status;

    @XmlElementWrapper(name = "reports")
    @XmlElement(name = "report")
    @JsonProperty("reports")
    private List<ActualProductReportEntity> reports;

    @XmlElementWrapper(name = "operations")
    @XmlElement(name = "operation")
    @JsonProperty("operations")
    private List<OperationEntity> operations;

    /**
     * ステータスを取得する。
     * 
     * @return ステータス 
     */
    public KanbanStatusEnum getStatus() {
        return status;
    }

    /**
     * ステータスを設定する。
     * 
     * @param status ステータス
     */
    public void setStatus(KanbanStatusEnum status) {
        this.status = status;
    }

    /**
     * 工程実績情報一覧を取得する。
     * 
     * @return 工程実績情報一覧
     */
    public List<ActualProductReportEntity> getReports() {
        return reports;
    }

    /**
     * 工程実績情報一覧を設定する。
     * 
     * @param reports 工程実績情報一覧
     */
    public void setReports(List<ActualProductReportEntity> reports) {
        this.reports = reports;
    }

    /**
     * 作業者操作実績情報一覧を取得する。
     * 
     * @return 作業者操作実績情報一覧
     */
    public List<OperationEntity> getOperations() {
        return operations;
    }

    /**
     * 作業者操作実績情報一覧を設定する。
     * 
     * @param operations 作業者操作実績情報一覧
     */
    public void setOperations(List<OperationEntity> operations) {
        this.operations = operations;
    }
    
}
