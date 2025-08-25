/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.workflow;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

/**
 * 作業パラメータ
 * 
 * @author i.chugin
 */
@Entity
@Table(name = "mst_work_parameters")
@XmlRootElement(name = "workParameters")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedNativeQueries({
    // 品番が一致する作業パラメータを抽出
    @NamedNativeQuery(name = "WorkParametersEntity.findByItemNumber", query = "SELECT w.* FROM mst_work_parameters w WHERE w.workflow_id = ?1 AND ?2 ~* w.item_number", resultClass = WorkParametersEntity.class)
})
@NamedQueries({
    @NamedQuery(name = "WorkParametersEntity.deleteByWorkflowId", query = "DELETE FROM WorkParametersEntity w WHERE w.key.workflowId = :workflowId")
})
public class WorkParametersEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Embeddable
    public static class Key {
        @Column(name = "item_number")
        private String itemNumber;

        @Column(name = "workflow_id")
        private Long workflowId;
        
        public Key() {
        }

        public Key(String itemNumber, Long workflowId) {
            this.itemNumber = itemNumber;
            this.workflowId = workflowId;
        }
    }

    @EmbeddedId
    @XmlTransient
    private Key key = new Key();

    @Column(name = "work_parameter")
    private String workParameter;

    public WorkParametersEntity() {
    }

    @XmlElement(name="itemNumber")
    public String getItemNumber() {
        return key.itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.key.itemNumber = itemNumber;
    }

    @XmlElement(name="workflowId")
    public Long getWorkflowId() {
        return key.workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.key.workflowId = workflowId;
    }

    public String getWorkParameter() {
        return workParameter;
    }

    public void setWorkParameter(String workParameter) {
        this.workParameter = workParameter;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("WorkParametersEntity{")
                .append("itemNumber=").append(this.key.itemNumber)
                .append(", workflowId=").append(this.key.workflowId)
                .append("}")
                .toString();
    }
    
}
