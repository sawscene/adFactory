/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.indirectwork;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 間接作業マスタ
 *
 * @author nar-nakamura
 */
@Entity
@Table(name = "mst_indirect_work")
@XmlRootElement(name = "indirectWork")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "IndirectWorkEntity.findByUk", query = "SELECT i FROM IndirectWorkEntity i WHERE i.classNumber = :classNumber AND i.workNumber = :workNumber"),
    @NamedQuery(name = "IndirectWorkEntity.checkAddByUk", query = "SELECT COUNT(i.indirectWorkId) FROM IndirectWorkEntity i WHERE i.classNumber = :classNumber AND i.workNumber = :workNumber"),
    @NamedQuery(name = "IndirectWorkEntity.checkUpdateByUk", query = "SELECT COUNT(i.indirectWorkId) FROM IndirectWorkEntity i WHERE i.classNumber = :classNumber AND i.workNumber = :workNumber AND i.indirectWorkId != :indirectWorkId"),

    @NamedQuery(name = "IndirectWorkEntity.countIndirectActual", query = "SELECT COUNT(i.indirectActualId) FROM IndirectActualEntity i WHERE i.indirectWorkId = :indirectWorkId"),
    @NamedQuery(name = "IndirectWorkEntity.getUsedIndirectWorkIds", query = "SELECT i.indirectWorkId FROM IndirectActualEntity i WHERE i.indirectWorkId IN :indirectWorkIds GROUP BY i.indirectWorkId"),

    @NamedQuery(name = "IndirectWorkEntity.countByWorkCategory", query = "SELECT COUNT(i.indirectWorkId) FROM IndirectWorkEntity i WHERE i.workCategoryId IN :workCategoryIds"),
    @NamedQuery(name = "IndirectWorkEntity.findByWorkCategory", query = "SELECT i FROM IndirectWorkEntity i WHERE i.workCategoryId IN :workCategoryIds ORDER BY i.workCategoryId, i.workName"),
})
public class IndirectWorkEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "indirect_work_id")
    private Long indirectWorkId;// 間接作業ID

    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 64)
    @Column(name = "class_number")
    private String classNumber;// 分類番号

    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 64)
    @Column(name = "work_number")
    private String workNumber;// 作業番号

    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 256)
    @Column(name = "work_name")
    private String workName;// 作業名

    //@NotNull
    @Column(name = "work_category_id")
    @XmlElement(name = "fkWorkCategoryId")
    private Long workCategoryId;// 作業区分ID

    //@NotNull
    @Column(name = "ver_info")
    @Version
    private Integer verInfo = 1;// 排他用バーション

    @Transient
    private Boolean isUsed;// 間接工数実績での使用状態

    /**
     * コンストラクタ
     */
    public IndirectWorkEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param workNumber 作業番号
     * @param workName 作業名
     * @param workCategoryId 作業区分ID
     */
    public IndirectWorkEntity(String workNumber, String workName, Long workCategoryId) {
        this.workNumber = workNumber;
        this.workName = workName;
        this.workCategoryId = workCategoryId;
    }

    /**
     * 間接作業IDを取得する。
     *
     * @return 間接作業ID
     */
    public Long getIndirectWorkId() {
        return this.indirectWorkId;
    }

    /**
     * 間接作業IDを設定する。
     *
     * @param indirectWorkId 間接作業ID
     */
    public void setIndirectWorkId(Long indirectWorkId) {
        this.indirectWorkId = indirectWorkId;
    }

    /**
     * 分類番号を取得する。
     *
     * @return 分類番号
     */
    public String getClassNumber() {
        return this.classNumber;
    }

    /**
     * 分類番号を設定する。
     *
     * @param classNumber 分類番号
     */
    public void setClassNumber(String classNumber) {
        this.classNumber = classNumber;
    }

    /**
     * 作業番号を取得する。
     *
     * @return 作業番号
     */
    public String getWorkNumber() {
        return this.workNumber;
    }

    /**
     * 作業番号を設定する。
     *
     * @param workNumber 作業番号
     */
    public void setWorkNumber(String workNumber) {
        this.workNumber = workNumber;
    }

    /**
     * 作業名を取得する。
     *
     * @return 作業名
     */
    public String getWorkName() {
        return this.workName;
    }

    /**
     * 作業名を設定する。
     *
     * @param workName 作業名
     */
    public void setWorkName(String workName) {
        this.workName = workName;
    }

    /**
     * 作業区分IDを取得する。
     * 
     * @return 作業区分ID
     */
    public Long getWorkCategoryId() {
        return workCategoryId;
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

    /**
     * 間接工数実績での使用状態を取得する。
     *
     * @return 使用状態 (true: 使用中, false: 未使用)
     */
    public Boolean getIsUsed() {
        return this.isUsed;
    }

    /**
     * 間接工数実績での使用状態を設定する。
     *
     * @param isUsed 使用状態 (true: 使用中, false: 未使用)
     */
    public void setIsUsed(Boolean isUsed) {
        this.isUsed = isUsed;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.indirectWorkId);
        hash = 59 * hash + Objects.hashCode(this.classNumber);
        hash = 59 * hash + Objects.hashCode(this.workNumber);
        hash = 59 * hash + Objects.hashCode(this.workName);
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
        final IndirectWorkEntity other = (IndirectWorkEntity) obj;
        if (!Objects.equals(this.indirectWorkId, other.indirectWorkId)) {
            return false;
        }
        if (!Objects.equals(this.classNumber, other.classNumber)) {
            return false;
        }
        if (!Objects.equals(this.workNumber, other.workNumber)) {
            return false;
        }
        if (!Objects.equals(this.workName, other.workName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("IndirectWorkEntity{")
                .append("indirectWorkId=").append(this.indirectWorkId)
                .append(", ")
                .append("classNumber=").append(this.classNumber)
                .append(", ")
                .append("workNumber=").append(this.workNumber)
                .append(", ")
                .append("workName=").append(this.workName)
                .append(", ")
                .append("workCategoryId=").append(this.workCategoryId)
                .append(", ")
                .append("verInfo=").append(this.verInfo)
                .append(", ")
                .append("isUsed=").append(this.isUsed)
                .append("}")
                .toString();
    }
}
