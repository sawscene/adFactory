/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.indirectwork;

import java.io.Serializable;
import java.util.Objects;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 作業区分マスタ
 *
 * @author s-heya
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "workCategory")
public class WorkCategoryInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(required = true)
    private Long workCategoryId;
    @XmlElement(required = true)
    private String workCategoryName;

    @XmlElement()
    private Integer verInfo;// 排他用バーション

    private LongProperty workCategoryIdProperty;
    private StringProperty workCategoryNameProperty;

    /**
     * コンストラクタ
     */
    public WorkCategoryInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param workCategoryId
     * @param workCategoryName
     */
    public WorkCategoryInfoEntity(Long workCategoryId, String workCategoryName) {
        this.workCategoryId = workCategoryId;
        this.workCategoryName = workCategoryName;
    }

    /**
     * 作業区分IDプロパティを取得する。
     *
     * @return
     */
    public LongProperty workCategoryIdProperty() {
        if (Objects.isNull(this.workCategoryIdProperty)) {
            this.workCategoryIdProperty = new SimpleLongProperty(this.workCategoryId);
        }
        return this.workCategoryIdProperty;
    }

    /**
     * 作業区分名プロパティを取得する。
     *
     * @return
     */
    public StringProperty workCategoryNameProperty() {
        if (Objects.isNull(this.workCategoryNameProperty)) {
            this.workCategoryNameProperty = new SimpleStringProperty(this.workCategoryName);
        }
        return this.workCategoryNameProperty;
    }

    /**
     * 作業区分IDを取得する。
     *
     * @return
     */
    public Long getWorkCategoryId() {
        return workCategoryId;
    }

    /**
     * 作業区分IDを設定する。
     *
     * @param workCategoryId
     */
    public void setWorkCategoryId(Long workCategoryId) {
        this.workCategoryId = workCategoryId;
    }

    /**
     * 作業区分名を取得する。
     *
     * @return
     */
    public String getWorkCategoryName() {
        return workCategoryName;
    }

    /**
     * 作業区分名を設定する。
     *
     * @param workCategoryName
     */
    public void setWorkCategoryName(String workCategoryName) {
        this.workCategoryName = workCategoryName;
    }

    /**
     * 排他用バーションを取得する。
     *
     * @return 排他用バーション
     */
    public Integer getVerInfo() {
        return this.verInfo;
    }

    /**
     * 排他用バーションを設定する。
     *
     * @param verInfo 排他用バーション
     */
    public void setVerInfo(Integer verInfo) {
        this.verInfo = verInfo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (workCategoryId != null ? workCategoryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the workCategoryId fields are not set
        if (!(object instanceof WorkCategoryInfoEntity)) {
            return false;
        }
        WorkCategoryInfoEntity other = (WorkCategoryInfoEntity) object;
        if ((this.workCategoryId == null && other.workCategoryId != null) || (this.workCategoryId != null && !this.workCategoryId.equals(other.workCategoryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("WorkCategoryInfoEntity{")
                .append("workCategoryId=").append(this.workCategoryId)
                .append(", ")
                .append("workCategoryName=").append(this.workCategoryName)
                .append(", ")
                .append("verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
