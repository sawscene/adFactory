/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.work;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 工程階層情報
 *
 * @author ta.ito
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "workHierarchy")
public class WorkHierarchyInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty workHierarchyIdProperty;
    private LongProperty parentProperty;
    private StringProperty hierarchyNameProperty;

    @XmlElement(required = true)
    private Long workHierarchyId;// 階層ID
    @XmlElement()
    private Long parentId;// 親階層ID
    @XmlElement
    private String hierarchyName;// 階層名
    @XmlElement()
    private Long childCount = 0L;// 子階層数

    @XmlElementWrapper(name = "works")
    @XmlElement(name = "work")
    private List<WorkInfoEntity> workInfoCollection = null;// 工程情報一覧

    @XmlElement()
    private Integer verInfo;// 排他用バーション

    /**
     * コンストラクタ
     */
    public WorkHierarchyInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param in 
     */
    public WorkHierarchyInfoEntity(WorkHierarchyInfoEntity in) {
        this.workHierarchyId = in.workHierarchyId;
        this.parentId = in.parentId;
        this.hierarchyName = in.hierarchyName;
        this.childCount = in.childCount;
        this.verInfo = in.verInfo;
    }

    /**
     * コンストラクタ
     *
     * @param workHierarchyId 階層ID
     * @param hierarchyName 階層名
     */
    public WorkHierarchyInfoEntity(Long workHierarchyId, String hierarchyName) {
        this.workHierarchyId = workHierarchyId;
        this.hierarchyName = hierarchyName;
    }

    /**
     * 階層IDプロパティを取得する。
     *
     * @return 階層ID
     */
    public LongProperty workHierarchyIdProperty() {
        if (Objects.isNull(this.workHierarchyIdProperty)) {
            this.workHierarchyIdProperty = new SimpleLongProperty(this.workHierarchyId);
        }
        return this.workHierarchyIdProperty;
    }

    /**
     * 親階層IDプロパティを取得する。
     *
     * @return 親階層ID
     */
    public LongProperty parentProperty() {
        if (Objects.isNull(this.parentProperty)) {
            this.parentProperty = new SimpleLongProperty(this.parentId);
        }
        return this.parentProperty;
    }

    /**
     * 階層名プロパティを取得する。
     *
     * @return 階層名
     */
    public StringProperty hierarchyNameProperty() {
        if (Objects.isNull(this.hierarchyNameProperty)) {
            this.hierarchyNameProperty = new SimpleStringProperty(this.hierarchyName);
        }
        return this.hierarchyNameProperty;
    }

    /**
     * 階層IDを取得する。
     *
     * @return 階層ID
     */
    public Long getWorkHierarchyId() {
        if (Objects.nonNull(this.workHierarchyIdProperty)) {
            return this.workHierarchyIdProperty.get();
        }
        return this.workHierarchyId;
    }

    /**
     * 階層IDを設定する。
     *
     * @param workHierarchyId 階層ID
     */
    public void setWorkHierarchyId(Long workHierarchyId) {
        if (Objects.nonNull(this.workHierarchyIdProperty)) {
            this.workHierarchyIdProperty.set(workHierarchyId);
        } else {
            this.workHierarchyId = workHierarchyId;
        }
    }

    /**
     * 親階層IDを取得する。
     *
     * @return 親階層ID
     */
    public Long getParentId() {
        if (Objects.nonNull(this.parentProperty)) {
            return this.parentProperty.get();
        }
        return this.parentId;
    }

    /**
     * 親階層IDを設定する。
     *
     * @param parentId 親階層ID
     */
    public void setParentId(Long parentId) {
        if (Objects.nonNull(this.parentProperty)) {
            this.parentProperty.set(parentId);
        } else {
            this.parentId = parentId;
        }
    }

    /**
     * 階層名を取得する。
     *
     * @return 階層名
     */
    public String getHierarchyName() {
        if (Objects.nonNull(this.hierarchyNameProperty)) {
            return this.hierarchyNameProperty.get();
        }
        return this.hierarchyName;
    }

    /**
     * 階層名を設定する。
     *
     * @param hierarchyName 階層名
     */
    public void setHierarchyName(String hierarchyName) {
        if (Objects.nonNull(this.hierarchyNameProperty)) {
            this.hierarchyNameProperty.set(hierarchyName);
        } else {
            this.hierarchyName = hierarchyName;
        }
    }

    /**
     * 子階層数を取得する。
     *
     * @return 子階層数
     */
    public Long getChildCount() {
        return this.childCount;
    }

    /**
     * 子階層数を設定する。
     *
     * @param childCount 子階層数
     */
    public void setChildCount(Long childCount) {
        this.childCount = childCount;
    }

    /**
     * 工程情報一覧を取得する。
     *
     * @return 工程情報一覧
     */
    public List<WorkInfoEntity> getWorkInfoCollection() {
        return this.workInfoCollection;
    }

    /**
     * 工程情報一覧を設定する。
     *
     * @param workInfoCollection 工程情報一覧
     */
    public void setWorkInfoCollection(List<WorkInfoEntity> workInfoCollection) {
        this.workInfoCollection = workInfoCollection;
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
        int hash = 7;
        hash = 83 * hash + (int) (this.workHierarchyId ^ (this.workHierarchyId >>> 32));
        hash = 83 * hash + Objects.hashCode(this.hierarchyName);
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
        final WorkHierarchyInfoEntity other = (WorkHierarchyInfoEntity) obj;
        if (this.getWorkHierarchyId() != other.getWorkHierarchyId()) {
            return false;
        }
        if (!Objects.equals(this.getHierarchyName(), other.getHierarchyName())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("WorkHierarchyInfoEntity{")
                .append("workHierarchyId=").append(this.workHierarchyId)
                .append(", ")
                .append("parentId=").append(this.parentId)
                .append(", ")
                .append("hierarchyName=").append(this.hierarchyName)
                .append(", ")
                .append("verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
