/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.importformat;

import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;

/**
 * 工程順 正規表現情報
 *
 * @author nar-nakamura
 */
@XmlRootElement(name = "workflowRegexInfo")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class WorkflowRegexInfo {

    private IntegerProperty orderProperty = null;
    private StringProperty regexProperty = null;
    private StringProperty workflowDispNameProperty = null;

    private Integer order = 0;
    private String regex;
    private String workflowName;
    private Integer workflowRev;

    private WorkflowInfoEntity workflow;

    private boolean useLatest;
    
    /**
     * コンストラクタ
     */
    public WorkflowRegexInfo() {
    }

    /**
     * 並び順プロパティを取得する。
     *
     * @return 並び順
     */
    public IntegerProperty orderProperty() {
        if (Objects.isNull(this.orderProperty)) {
            this.orderProperty = new SimpleIntegerProperty(order);
        }
        return this.orderProperty;
    }

    /**
     * 条件(正規表現)プロパティを取得する。
     *
     * @return 条件(正規表現)
     */
    public StringProperty regexProperty() {
        if (Objects.isNull(this.regexProperty)) {
            this.regexProperty = new SimpleStringProperty(this.regex);
        }
        return this.regexProperty;
    }

    /**
     * 工程順の表示名プロパティを取得する。
     *
     * @return 工程順の表示名
     */
    public StringProperty workflowDispNameProperty() {
        if (Objects.isNull(this.workflowDispNameProperty)) {
            this.workflowDispNameProperty = new SimpleStringProperty(this.getWorkflowDispName());
        }
        return this.workflowDispNameProperty;
    }

    /**
     * 並び順を取得する。
     *
     * @return 並び順
     */
    public Integer getOrder() {
        if (Objects.nonNull(this.orderProperty)) {
            return this.orderProperty.get();
        }
        return this.order;
    }

    /**
     * 並び順を設定する。
     *
     * @param order 並び順
     */
    public void setOrder(Integer order) {
        if (Objects.nonNull(this.orderProperty)) {
            this.orderProperty.set(order);
        } else {
            this.order = order;
        }
    }

    /**
     * 条件(正規表現)を取得する。
     *
     * @return 条件(正規表現)
     */
    public String getRegex() {
        if (Objects.nonNull(this.regexProperty)) {
            return this.regexProperty.get();
        }
        return this.regex;
    }

    /**
     * 条件(正規表現)を設定する。
     *
     * @param regex 条件(正規表現)
     */
    public void setRegex(String regex) {
        if (Objects.nonNull(this.regexProperty)) {
            this.regexProperty.set(regex);
        } else {
            this.regex = regex;
        }
    }

    /**
     * 工程順情報を取得する。
     *
     * @return 工程順情報
     */
    @XmlTransient
    public WorkflowInfoEntity getWorkflow() {
        return this.workflow;
    }

    /**
     * 工程順情報を設定する。
     *
     * @param workflow 工程順情報
     */
    public void setWorkflow(WorkflowInfoEntity workflow) {
        this.workflow = workflow;

        if (Objects.isNull(this.workflowDispNameProperty)) {
            this.workflowDispNameProperty = new SimpleStringProperty();
        }

        // 工程順 (工程順名 : 版数)
        this.workflowDispNameProperty.set(this.getWorkflowDispName());

        if (Objects.nonNull(this.workflow)) {
            this.workflowName = this.workflow.getWorkflowName();
            this.workflowRev = this.workflow.getWorkflowRev();
        } else {
            this.workflowName = null;
            this.workflowRev = null;
        }
    }

    /**
     * 工程順名を取得する。
     *
     * @return 工程順名
     */
    public String getWorkflowName() {
        if (Objects.nonNull(this.workflow)) {
            this.workflowName = this.workflow.getWorkflowName();
        }
        return this.workflowName;
    }

    /**
     * 工程順名を設定する。
     *
     * @param workflowName 工程順名
     */
    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    /**
     * 工程順の版数を取得する。
     *
     * @return 工程順の版数
     */
    public Integer getWorkflowRev() {
        if (Objects.nonNull(this.workflow)) {
            this.workflowRev = this.workflow.getWorkflowRev();
        }
        return this.workflowRev;
    }

    /**
     * 工程順の版数を設定する。
     *
     * @param workflowRev 工程順の版数
     */
    public void setWorkflowRev(Integer workflowRev) {
        this.workflowRev = workflowRev;
    }

    /**
     * 工程順の表示名を取得する。
     *
     * @return 工程順の表示名(工程順名 : 版数)
     */
    private String getWorkflowDispName() {
        // 工程順 (工程順名 : 版数)
        StringBuilder sb = new StringBuilder();
        if (Objects.nonNull(this.workflow)) {
            sb.append(this.workflow.getWorkflowName());
            if (!useLatest) {
                sb.append(" : ");
                sb.append(this.workflow.getWorkflowRev());
            }
        }
        return sb.toString();
    }

    /**
     * 最新版を使用するかを取得する。
     *
     * @return 最新版を使用するか
     */
    public boolean isUseLatest() {
        return this.useLatest;
    }

    /**
     * 最新版を使用するかを設定する。
     *
     * @param useLatest 最新版を使用するか
     */
    public void setUseLatest(boolean useLatest) {
        this.useLatest = useLatest;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.regex);
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
        final WorkflowRegexInfo other = (WorkflowRegexInfo) obj;
        if (!Objects.equals(this.regex, other.regex)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("RegexInfo{")
                .append("order=").append(this.order)
                .append(", regex=").append(this.regex)
                .append(", workflow=").append(this.workflow)
                .append(", workflowRev=").append(this.workflowRev)
                .append(", useLatest=").append(this.useLatest)
                .append("}")
                .toString();
    }
}
