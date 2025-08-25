/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.indirectwork;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

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
    @NamedQuery(name = "IndirectWorkEntity.countIndirectActual", query = "SELECT COUNT(i.indirectActualId) FROM IndirectActualEntity i WHERE i.fkIndirectWorkId = :fkIndirectWorkId"),
    @NamedQuery(name = "IndirectWorkEntity.getUsedIndirectWorkIdsAll", query = "SELECT i.fkIndirectWorkId FROM IndirectActualEntity i GROUP BY i.fkIndirectWorkId"),
    @NamedQuery(name = "IndirectWorkEntity.getUsedIndirectWorkIds", query = "SELECT i.fkIndirectWorkId FROM IndirectActualEntity i WHERE i.fkIndirectWorkId IN :indirectWorkIds GROUP BY i.fkIndirectWorkId"),

    @NamedQuery(name = "IndirectWorkEntity.findAll", query = "SELECT i FROM IndirectWorkEntity i"),
    @NamedQuery(name = "IndirectWorkEntity.findByIndirectWorkId", query = "SELECT i FROM IndirectWorkEntity i WHERE i.indirectWorkId = :indirectWorkId"),
    @NamedQuery(name = "IndirectWorkEntity.findByClassNumber", query = "SELECT i FROM IndirectWorkEntity i WHERE i.classNumber = :classNumber"),
    @NamedQuery(name = "IndirectWorkEntity.findByWorkNumber", query = "SELECT i FROM IndirectWorkEntity i WHERE i.workNumber = :workNumber"),
    @NamedQuery(name = "IndirectWorkEntity.findByWorkName", query = "SELECT i FROM IndirectWorkEntity i WHERE i.workName = :workName")})
public class IndirectWorkEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "indirect_work_id")
    private Long indirectWorkId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "class_number")
    private String classNumber;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "work_number")
    private String workNumber;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "work_name")
    private String workName;

    @Transient
    private Boolean isUsed;

    /**
     * コンストラクタ
     */
    public IndirectWorkEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param indirectWorkId
     * @param classNumber
     * @param workNumber
     * @param workName 
     */
    public IndirectWorkEntity(Long indirectWorkId, String classNumber, String workNumber, String workName) {
        this.indirectWorkId = indirectWorkId;
        this.classNumber = classNumber;
        this.workNumber = workNumber;
        this.workName = workName;
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
        return "IndirectWorkEntity{" + "indirectWorkId=" + this.indirectWorkId + ", classNumber=" + this.classNumber + ", workNumber=" + this.workNumber + ", workName=" + this.workName + ", isUsed=" + this.isUsed + '}';
    }
}
