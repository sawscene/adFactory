/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.indirectwork;

import java.io.Serializable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 作業区分マスタ
 *
 * @author s-heya
 */
@Entity
@Table(name = "mst_work_category")
@XmlRootElement(name = "workCategory")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "WorkCategoryEntity.findById", query = "SELECT m FROM WorkCategoryEntity m WHERE m.workCategoryId = :workCategoryId"),
    @NamedQuery(name = "WorkCategoryEntity.findByName", query = "SELECT m FROM WorkCategoryEntity m WHERE m.workCategoryName = :workCategoryName"),
    @NamedQuery(name = "WorkCategoryEntity.countById", query = "SELECT COUNT(m.workCategoryId) FROM WorkCategoryEntity m WHERE m.workCategoryId = :workCategoryId"),
    @NamedQuery(name = "WorkCategoryEntity.countByName", query = "SELECT COUNT(m.workCategoryId) FROM WorkCategoryEntity m WHERE m.workCategoryName = :workCategoryName"),
    @NamedQuery(name = "WorkCategoryEntity.countByKey", query = "SELECT COUNT(m.workCategoryId) FROM WorkCategoryEntity m WHERE m.workCategoryId != :workCategoryId AND m.workCategoryName = :workCategoryName")
})
public class WorkCategoryEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "work_category_id")
    private Long workCategoryId;// 作業区分ID

    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 256)
    @Column(name = "work_category_name")
    private String workCategoryName;// 作業区分名

    //@NotNull
    @Column(name = "ver_info")
    @Version
    private Integer verInfo = 1;// 排他用バーション

    /**
     * コンストラクタ
     */
    public WorkCategoryEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param workCategoryName
     */
    public WorkCategoryEntity(String workCategoryName) {
        this.workCategoryName = workCategoryName;
    }

    /**
     * 作業区分IDを取得する。
     *
     * @return 作業区分ID
     */
    public Long getWorkCategoryId() {
        return this.workCategoryId;
    }

    /**
     * 作業区分IDを設定する。
     *
     * @param workCategoryId 作業区分ID
     */
    public void setWorkCategoryId(Long workCategoryId) {
        this.workCategoryId = workCategoryId;
    }

    /**
     * 作業区分名を取得する。
     *
     * @return 作業区分名
     */
    public String getWorkCategoryName() {
        return this.workCategoryName;
    }

    /**
     * 作業区分名を設定する。
     *
     * @param workCategoryName 作業区分名
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
        if (!(object instanceof WorkCategoryEntity)) {
            return false;
        }
        WorkCategoryEntity other = (WorkCategoryEntity) object;
        if ((this.workCategoryId == null && other.workCategoryId != null) || (this.workCategoryId != null && !this.workCategoryId.equals(other.workCategoryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("WorkCategoryEntity{")
                .append("workCategoryId=").append(this.workCategoryId)
                .append(", ")
                .append("workCategoryName=").append(this.workCategoryName)
                .append(", ")
                .append("verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
