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
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

/**
 * 間接工数実績の検索条件
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "indirectActualSearchCondition")
@JsonIgnoreProperties(ignoreUnknown=true)
public class IndirectActualSearchCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    @JsonIgnore
    private Date fromDate = null;// 実施日時範囲の先頭

    @XmlTransient()
    private String jsonFromDate = null;

    @XmlElement()
    @JsonIgnore
    private Date toDate = null;// 実施日時範囲の末尾

    @XmlTransient()
    private String jsonToDate = null;

    @XmlElementWrapper(name = "organizationIds")
    @XmlElement(name = "organizationId")
    private List<Long> organizationCollection = null;// 組織ID一覧

    @XmlElement()
    private Long indirectWorkId;
    
    /**
     * コンストラクタ
     */
    public IndirectActualSearchCondition() {
    }

    /**
     * 実施日時範囲の先頭を設定する。
     *
     * @param fromDate 実施日時範囲の先頭
     * @return 間接工数実績の検索条件
     */
    public IndirectActualSearchCondition fromDate(Date fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    /**
     * 実施日時範囲の末尾を設定する。
     *
     * @param toDate 実施日時範囲の末尾
     * @return 間接工数実績の検索条件
     */
    public IndirectActualSearchCondition toDate(Date toDate) {
        this.toDate = toDate;
        return this;
    }

    /**
     * 組織ID一覧を設定する。
     *
     * @param organizationList
     * @return 間接工数実績の検索条件
     */
    public IndirectActualSearchCondition organizationList(List<Long> organizationList) {
        this.organizationCollection = organizationList;
        return this;
    }

    /**
     * 実施日時範囲の先頭を取得する。
     *
     * @return 実施日時範囲の先頭
     */
    public Date getFromDate() {
        if (Objects.isNull(this.fromDate) && !StringUtils.isEmpty(this.jsonFromDate)) {
            this.fromDate = DateUtils.parseJson(this.jsonFromDate);
        }
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
        if (Objects.isNull(this.toDate) && !StringUtils.isEmpty(this.jsonToDate)) {
            this.toDate = DateUtils.parseJson(this.jsonToDate);
        }
        return this.toDate;
    }

    /**
     * 実施日時範囲の末尾を設定する。
     *
     * @param toDate 実施日時範囲の末尾
     */
    public void setToDate(Date toDate) {
        this.toDate = toDate;
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
     * 間接作業IDを取得する。
     * 
     * @return 間接作業ID
     */
    public Long getIndirectWorkId() {
        return indirectWorkId;
    }

    /**
     * 間接作業IDを取得する。
     * 
     * @param indirectWorkId 間接作業ID 
     */
    public void setIndirectWorkId(Long indirectWorkId) {
        this.indirectWorkId = indirectWorkId;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.fromDate);
        hash = 29 * hash + Objects.hashCode(this.toDate);
        hash = 29 * hash + Objects.hashCode(this.organizationCollection);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IndirectActualSearchCondition other = (IndirectActualSearchCondition) obj;
        if (!Objects.equals(this.fromDate, other.fromDate)) {
            return false;
        }
        if (!Objects.equals(this.toDate, other.toDate)) {
            return false;
        }
        return Objects.equals(this.organizationCollection, other.organizationCollection);
    }

    @Override
    public String toString() {
        return new StringBuilder("IndirectActualSearchCondition{")
                .append("fromDate=").append(this.fromDate)
                .append(", toDate=").append(this.toDate)
                .append(", organizationCollection=").append(this.organizationCollection)
                .append(", indirectWorkId=").append(this.indirectWorkId)
                .append("}")
                .toString();
    }
}
