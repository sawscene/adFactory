/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.workflow;

import java.io.Serializable;
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
 * 工程順階層情報
 *
 * @author ta.ito
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "workflowHierarchy")
public class WorkflowHierarchyInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty workflowHierarchyIdProperty;
    private LongProperty parentIdProperty;
    private StringProperty hierarchyNameProperty;

    @XmlElement(required = true)
    private Long workflowHierarchyId;// 階層ID
    @XmlElement()
    private Long parentId;// 親階層ID
    @XmlElement()
    private String hierarchyName;// 階層名
    @XmlElement()
    private Long childCount = 0L;// 子階層数

    @XmlElementWrapper(name = "workflows")
    @XmlElement(name = "workflow")
    private List<WorkflowInfoEntity> workflowInfoCollection = null;// 工程順情報一覧

    @XmlElement()
    private Integer verInfo;// 排他用バーション

    /**
     * コンストラクタ
     */
    public WorkflowHierarchyInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param workflowHierarchyId 階層ID
     * @param hierarchyName 階層名
     */
    public WorkflowHierarchyInfoEntity(Long workflowHierarchyId, String hierarchyName) {
        this.workflowHierarchyId = workflowHierarchyId;
        this.hierarchyName = hierarchyName;
    }

    /**
     * 階層IDプロパティを取得する。
     *
     * @return 階層ID
     */
    public LongProperty workflowHierarchyIdProperty() {
        if (Objects.isNull(this.workflowHierarchyIdProperty)) {
            this.workflowHierarchyIdProperty = new SimpleLongProperty(this.workflowHierarchyId);
        }
        return this.workflowHierarchyIdProperty;
    }

    /**
     * 親階層IDプロパティを取得する。
     *
     * @return 親階層ID
     */
    public LongProperty parentIdProperty() {
        if (Objects.isNull(this.parentIdProperty)) {
            this.parentIdProperty = new SimpleLongProperty(this.parentId);
        }
        return this.parentIdProperty;
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
    public Long getWorkflowHierarchyId() {
        if (Objects.nonNull(this.workflowHierarchyIdProperty)) {
            return this.workflowHierarchyIdProperty.get();
        }
        return this.workflowHierarchyId;
    }

    /**
     * 階層IDを設定する。
     *
     * @param workflowHierarchyId 階層ID
     */
    public void setWorkflowHierarchyId(Long workflowHierarchyId) {
        if (Objects.nonNull(this.workflowHierarchyIdProperty)) {
            this.workflowHierarchyIdProperty.set(workflowHierarchyId);
        } else {
            this.workflowHierarchyId = workflowHierarchyId;
        }
    }

    /**
     * 親階層IDを取得する。
     *
     * @return 親階層ID
     */
    public Long getParentId() {
        if (Objects.nonNull(this.parentIdProperty)) {
            return this.parentIdProperty.get();
        }
        return this.parentId;
    }

    /**
     * 親階層IDを設定する。
     *
     * @param parentId 親階層ID
     */
    public void setParentId(Long parentId) {
        if (Objects.nonNull(this.parentIdProperty)) {
            this.parentIdProperty.set(parentId);
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
     * 工程順情報一覧を取得する。
     *
     * @return 工程順情報一覧
     */
    public List<WorkflowInfoEntity> getWorkflowInfoCollection() {
        return this.workflowInfoCollection;
    }

    /**
     * 工程順情報一覧を設定する。
     *
     * @param workflowInfoCollection 工程順情報一覧
     */
    public void setWorkflowInfoCollection(List<WorkflowInfoEntity> workflowInfoCollection) {
        this.workflowInfoCollection = workflowInfoCollection;
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
        hash = 83 * hash + (int) (this.workflowHierarchyId ^ (this.workflowHierarchyId >>> 32));
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
        final WorkflowHierarchyInfoEntity other = (WorkflowHierarchyInfoEntity) obj;
        if (this.getWorkflowHierarchyId() != other.getWorkflowHierarchyId()) {
            return false;
        }
        if (!Objects.equals(this.getHierarchyName(), other.getHierarchyName())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("WorkflowHierarchyInfoEntity{")
                .append("workflowHierarchyId=").append(this.workflowHierarchyId)
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
