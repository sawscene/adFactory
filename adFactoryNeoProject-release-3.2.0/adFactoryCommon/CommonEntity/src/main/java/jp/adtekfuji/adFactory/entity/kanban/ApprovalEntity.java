/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.kanban;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 承認情報
 *
 * @author nar-nakamura
 */
@XmlRootElement(name = "approval")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown=true)
public class ApprovalEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    @JsonProperty("order")
    private Integer order;// 順

    @XmlElement()
    @JsonProperty("approve")
    private Boolean approve;// 承認

    @XmlElement()
    @JsonProperty("approver")
    private String approver;// 承認者

    @XmlElement()
    @JsonProperty("reason")
    private String reason;// 理由
    
    @XmlElement()
    @JsonProperty("date")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private Date date;// 承認日付

    /**
     * コンストラクタ
     */
    public ApprovalEntity() {
    }

    /**
     * 順を取得する。
     *
     * @return 順
     */
    public Integer getOrder() {
        return this.order;
    }

    /**
     * 順を設定する。
     *
     * @param order 順
     */
    public void setOrder(Integer order) {
        this.order = order;
    }

    /**
     * 承認を取得する。
     *
     * @return 承認
     */
    public Boolean getApprove() {
        return this.approve;
    }

    /**
     * 承認を設定する。
     *
     * @param approve 承認
     */
    public void setApprove(Boolean approve) {
        this.approve = approve;
    }

    /**
     * 承認者を取得する。
     *
     * @return 承認者
     */
    public String getApprover() {
        return this.approver;
    }

    /**
     * 承認者を設定する。
     *
     * @param approver 承認者
     */
    public void setApprover(String approver) {
        this.approver = approver;
    }

    /**
     * 理由を取得する。
     *
     * @return 理由
     */
    public String getReason() {
        return this.reason;
    }

    /**
     * 理由を設定する。
     *
     * @param reason 理由
     */
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    /**
     * 承認日付を取得する。
     *
     * @return 承認日付
     */
    public Date getDate() {
        return this.date;
    }

    /**
     * 承認日付を設定する。
     *
     * @param date 承認日付
     */
    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.order);
        hash = 41 * hash + Objects.hashCode(this.approve);
        hash = 41 * hash + Objects.hashCode(this.approver);
        hash = 41 * hash + Objects.hashCode(this.reason);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ApprovalEntity other = (ApprovalEntity) obj;
        if (!Objects.equals(this.approver, other.approver)) {
            return false;
        }
        if (!Objects.equals(this.reason, other.reason)) {
            return false;
        }
        if (!Objects.equals(this.order, other.order)) {
            return false;
        }
        if (!Objects.equals(this.approve, other.approve)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ApprovalEntity{")
                .append("order=").append(this.order)
                .append(", approve=").append(this.approve)
                .append(", approver=").append(this.approver)
                .append(", reason=").append(this.reason)
                .append("}")
                .toString();
    }
}
