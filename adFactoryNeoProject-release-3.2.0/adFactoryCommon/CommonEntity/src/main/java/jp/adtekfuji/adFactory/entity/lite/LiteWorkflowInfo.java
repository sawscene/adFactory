/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.lite;

import java.io.Serializable;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;

/**
 * [Lite] 工程順・工程情報
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "liteWorkflow")
public class LiteWorkflowInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工程順情報
     */
    @XmlElement()
    private WorkflowInfoEntity workflow;

    /**
     * 工程情報一覧
     */
    @XmlElementWrapper(name = "works")
    @XmlElement(name = "work")
    private List<WorkInfoEntity> works;

    /**
     * コンストラクタ
     */
    public LiteWorkflowInfo() {
    }

    /**
     * 工程順情報を取得する。
     *
     * @return 工程順情報
     */
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
    }

    /**
     * 工程情報一覧を取得する。
     *
     * @return 工程情報一覧
     */
    public List<WorkInfoEntity> getWorks() {
        return this.works;
    }

    /**
     * 工程情報一覧を設定する。
     *
     * @param works 工程情報一覧
     */
    public void setWorks(List<WorkInfoEntity> works) {
        this.works = works;
    }

    @Override
    public String toString() {
        return new StringBuilder("LiteWorkflowInfo{")
                .append("workflow=").append(this.workflow)
                .append("}")
                .toString();
    }
}
