/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.kanban;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;

/**
 * 実績通知結果
 * 
 * @author e-mori
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "actualProductReportResult")
public class ActualProductReportResult implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(required = true)
    private ServerErrorTypeEnum resultType;
    @XmlElement(required = true)
    private Long nextTransactionID;

    /**
     * 実績登録に成功した工程カンバンIDがセットされる
     */
    @XmlElementWrapper(name = "worKanbans")
    @XmlElement(name = "worKanbanId")
    private List<Long> worKanbanCollection;

    /**
     * 1日の完了数
     */
    @XmlElement(required = false)
    private Long completeCount;

    /**
     * エラー詳細
     */
    @XmlElement(required = false)
    private String details;

    public ActualProductReportResult() {
    }

    public ActualProductReportResult(ServerErrorTypeEnum resultType, Long nextTransactionID) {
        this.resultType = resultType;
        this.nextTransactionID = nextTransactionID;
    }

    public ActualProductReportResult(ServerErrorTypeEnum resultType, Long nextTransactionID, List<Long> worKanbanCollection) {
        this.resultType = resultType;
        this.nextTransactionID = nextTransactionID;
        this.worKanbanCollection = worKanbanCollection;
    }

    public ActualProductReportResult(ServerErrorTypeEnum resultType, Long nextTransactionID, List<Long> worKanbanCollection, Long completeCount) {
        this.resultType = resultType;
        this.nextTransactionID = nextTransactionID;
        this.worKanbanCollection = worKanbanCollection;
        this.completeCount = completeCount;
    }

    /**
     * エラー詳細を設定する。
     * 
     * @param details エラー詳細
     * @return 
     */
    public ActualProductReportResult details(String details) {
        this.details = details;
        return this;
    }
    
    public ServerErrorTypeEnum getResultType() {
        return resultType;
    }

    public void setResultType(ServerErrorTypeEnum resultType) {
        this.resultType = resultType;
    }

    public Long getNextTransactionID() {
        return nextTransactionID;
    }

    public void setNextTransactionID(Long nextTransactionID) {
        this.nextTransactionID = nextTransactionID;
    }

    public List<Long> getWorKanbanCollection() {
        return worKanbanCollection;
    }

    public void setWorKanbanCollection(List<Long> worKanbanCollection) {
        this.worKanbanCollection = worKanbanCollection;
    }

    public Long getCompleteCount() {
        return completeCount;
    }

    /**
     * エラー詳細を返す。
     * 
     * @return エラー詳細
     */
    public String getDetails() {
        return details;
    }

    /**
     * エラー詳細を設定する。
     * 
     * @param details エラー詳細
     */
    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * ハッシュコードを返す。
     * 
     * @return ハッシュコード 
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.resultType);
        return hash;
    }

    /**
     * オブジェクトを比較する。
     * 
     * @param obj オブジェクト
     * @return true: 同一のオブジェクトである、false: 異なるオブジェクである
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ActualProductReportResult other = (ActualProductReportResult) obj;
        return this.resultType == other.resultType;
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現 
     */
    @Override
    public String toString() {
        return "ActualProductReportResult{" + "resultType=" + resultType + ", nextTransactionID=" + nextTransactionID + '}';
    }

}
