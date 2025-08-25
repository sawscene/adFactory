/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.indirectwork;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Objects;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 間接作業マスタ
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "indirectWork")
public class IndirectWorkInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String CLASS_NUMBER_NONE = "NONE";
    private static final long DEFAULT_WORK_CATEGORY = 1L;

    @XmlTransient
    private LongProperty indirectWorkIdProperty;
    @XmlTransient
    private StringProperty classNumberProperty;
    @XmlTransient
    private StringProperty workNumberProperty;
    @XmlTransient
    private StringProperty workNameProperty;

    @XmlElement(required = true)
    private Long indirectWorkId;
    @XmlElement()
    private String classNumber;
    @XmlElement()
    private String workNumber;
    @XmlElement()
    private String workName;
    @XmlElement()
    private Long fkWorkCategoryId;

    @XmlElement()
    private Boolean isUsed;

    @XmlElement()
    private Integer verInfo;// 排他用バーション

    /**
     * コンストラクタ
     */
    public IndirectWorkInfoEntity() {
        this.classNumber = CLASS_NUMBER_NONE;
        this.fkWorkCategoryId = DEFAULT_WORK_CATEGORY;
    }

    /**
     * コンストラクタ
     *
     * @param in 間接作業マスタ
     */
    public IndirectWorkInfoEntity(IndirectWorkInfoEntity in) {
        this.indirectWorkId = in.indirectWorkId;
        this.classNumber = in.classNumber;
        this.workNumber = in.workNumber;
        this.workName = in.workName;
        this.fkWorkCategoryId = in.fkWorkCategoryId;
        this.isUsed = in.isUsed;
        this.verInfo = in.verInfo;
    }

    /**
     * コンストラクタ
     * @param indirectWorkId 間接作業ID
     * @param workNumber 分類番号
     * @param workName 作業番号
     */
    public IndirectWorkInfoEntity(Long indirectWorkId, String workNumber, String workName) {
        this.indirectWorkId = indirectWorkId;
        this.classNumber = CLASS_NUMBER_NONE;
        this.workNumber = workNumber;
        this.workName = workName;
        this.fkWorkCategoryId = DEFAULT_WORK_CATEGORY;
    }

    /**
     * コンストラクタ
     * @param indirectWorkId 間接作業ID
     * @param workNumber 分類番号
     * @param workName 作業番号
     * @param fkWorkCategoryId 作業区分ID
     */
    public IndirectWorkInfoEntity(Long indirectWorkId, String workNumber, String workName, Long fkWorkCategoryId) {
        this.indirectWorkId = indirectWorkId;
        this.classNumber = CLASS_NUMBER_NONE;
        this.workNumber = workNumber;
        this.workName = workName;
        this.fkWorkCategoryId = fkWorkCategoryId;
    }

    /**
     * コンストラクタ
     *
     * @param indirectWorkId 間接作業ID
     * @param classNumber 分類番号
     * @param workNumber 作業番号
     * @param workName 作業名
     */
    public IndirectWorkInfoEntity(Long indirectWorkId, String classNumber, String workNumber, String workName) {
        this.indirectWorkId = indirectWorkId;
        this.classNumber = classNumber;
        this.workNumber = workNumber;
        this.workName = workName;
        this.fkWorkCategoryId = DEFAULT_WORK_CATEGORY;
    }

    /**
     * 間接作業IDプロパティを取得する。
     *
     * @return 間接作業ID
     */
    public LongProperty indirectWorkIdProperty() {
        if (Objects.isNull(this.indirectWorkIdProperty)) {
            this.indirectWorkIdProperty = new SimpleLongProperty(this.indirectWorkId);
        }
        return this.indirectWorkIdProperty;
    }

    /**
     * 分類番号プロパティを取得する。
     *
     * @return 分類番号
     */
    public StringProperty classNumberProperty() {
        if (Objects.isNull(this.classNumberProperty)) {
            this.classNumberProperty = new SimpleStringProperty(this.classNumber);
        }
        return this.classNumberProperty;
    }

    /**
     * 作業番号プロパティを取得する。
     *
     * @return 作業番号
     */
    public StringProperty workNumberProperty() {
        if (Objects.isNull(this.workNumberProperty)) {
            this.workNumberProperty = new SimpleStringProperty(this.workNumber);
        }
        return this.workNumberProperty;
    }

    /**
     * 作業名プロパティを取得する。
     *
     * @return 作業名
     */
    public StringProperty workNameProperty() {
        if (Objects.isNull(this.workNameProperty)) {
            this.workNameProperty = new SimpleStringProperty(this.workName);
        }
        return this.workNameProperty;
    }

    /**
     * 間接作業IDを取得する。
     *
     * @return 間接作業ID
     */
    public Long getIndirectWorkId() {
        if (Objects.nonNull(this.indirectWorkIdProperty)) {
            return this.indirectWorkIdProperty.get();
        }
        return this.indirectWorkId;
    }

    /**
     * 間接作業IDを設定する。
     *
     * @param indirectWorkId 間接作業ID
     */
    public void setIndirectWorkId(Long indirectWorkId) {
        if (Objects.nonNull(this.indirectWorkIdProperty)) {
            this.indirectWorkIdProperty.set(indirectWorkId);
        } else {
            this.indirectWorkId = indirectWorkId;
        }
    }

    /**
     * 分類番号を取得する。
     *
     * @return 分類番号
     */
    public String getClassNumber() {
        if (Objects.nonNull(this.classNumberProperty)) {
            return this.classNumberProperty.get();
        }
        return this.classNumber;
    }

    /**
     * 分類番号を設定する。
     *
     * @param classNumber 分類番号
     */
    public void setClassNumber(String classNumber) {
        if (Objects.nonNull(this.classNumberProperty)) {
            this.classNumberProperty.set(classNumber);
        } else {
            this.classNumber = classNumber;
        }
    }

    /**
     * 作業番号を取得する。
     *
     * @return 作業番号
     */
    public String getWorkNumber() {
        if (Objects.nonNull(this.workNumberProperty)) {
            return this.workNumberProperty.get();
        }
        return this.workNumber;
    }

    /**
     * 作業番号を設定する。
     *
     * @param workNumber 作業番号
     */
    public void setWorkNumber(String workNumber) {
        if (Objects.nonNull(this.workNumberProperty)) {
            this.workNumberProperty.set(workNumber);
        } else {
            this.workNumber = workNumber;
        }
    }

    /**
     * 作業名を取得する。
     *
     * @return 作業名
     */
    public String getWorkName() {
        if (Objects.nonNull(this.workNameProperty)) {
            return this.workNameProperty.get();
        }
        return this.workName;
    }

    /**
     * 作業名を設定する。
     *
     * @param workName 作業名
     */
    public void setWorkName(String workName) {
        if (Objects.nonNull(this.workNameProperty)) {
            this.workNameProperty.set(workName);
        } else {
            this.workName = workName;
        }
    }

    /**
     * 作業区分IDを取得する。
     * 
     * @return 
     */
    public Long getFkWorkCategoryId() {
        return fkWorkCategoryId;
    }

    /**
     * 作業区分IDを設定する。
     * 
     * @param fkWorkCategoryId 
     */
    public void setFkWorkCategoryId(Long fkWorkCategoryId) {
        this.fkWorkCategoryId = fkWorkCategoryId;
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
     * メンバー更新
     */
    public void updateMember() {
        this.indirectWorkId = this.getIndirectWorkId();
        this.classNumber = this.getClassNumber();
        this.workNumber = this.getWorkNumber();
        this.workName = this.getWorkName();
        this.fkWorkCategoryId = this.getFkWorkCategoryId();
        this.isUsed = this.getIsUsed();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.getIndirectWorkId());
        hash = 79 * hash + Objects.hashCode(this.getClassNumber());
        hash = 79 * hash + Objects.hashCode(this.getWorkNumber());
        hash = 79 * hash + Objects.hashCode(this.getWorkName());
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
        final IndirectWorkInfoEntity other = (IndirectWorkInfoEntity) obj;
        if (!Objects.equals(this.getIndirectWorkId(), other.getIndirectWorkId())) {
            return false;
        }
        if (!Objects.equals(this.getClassNumber(), other.getClassNumber())) {
            return false;
        }
        if (!Objects.equals(this.getWorkNumber(), other.getWorkNumber())) {
            return false;
        }
        if (!Objects.equals(this.getWorkName(), other.getWorkName())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("IndirectWorkInfoEntity{")
                .append("indirectWorkId=").append(this.indirectWorkId)
                .append(", ")
                .append("classNumber=").append(this.classNumber)
                .append(", ")
                .append("workNumber=").append(this.workNumber)
                .append(", ")
                .append("workName=").append(this.workName)
                .append(", ")
                .append("fkWorkCategoryId=").append(this.fkWorkCategoryId)
                .append(", ")
                .append("isUsed=").append(this.isUsed)
                .append(", ")
                .append("verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
