/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.lite;

import java.io.Serializable;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity;

/**
 * [Lite] 工程順・工程情報
 *
 * @author nar-nakamura
 */
@XmlRootElement(name = "liteWorkflow")
@XmlAccessorType(XmlAccessType.FIELD)
public class LiteWorkflow implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工程順情報
     */
    private WorkflowEntity workflow;

    /**
     * 工程情報一覧
     */
    @XmlElementWrapper(name = "works")
    @XmlElement(name = "work")
    private List<WorkEntity> works;

    /**
     * コンストラクタ
     */
    public LiteWorkflow() {
    }

    /**
     * 工程順情報を取得する。
     *
     * @return 工程順情報
     */
    public WorkflowEntity getWorkflow() {
        return this.workflow;
    }

    /**
     * 工程順情報を設定する。
     *
     * @param workflow 工程順情報
     */
    public void setWorkflow(WorkflowEntity workflow) {
        this.workflow = workflow;
    }

    /**
     * 工程情報一覧を取得する。
     *
     * @return 工程情報一覧
     */
    public List<WorkEntity> getWorks() {
        return this.works;
    }

    /**
     * 工程情報一覧を設定する。
     *
     * @param works 工程情報一覧
     */
    public void setWorks(List<WorkEntity> works) {
        this.works = works;
    }

    @Override
    public String toString() {
        return new StringBuilder("LiteWorkflow{")
                .append("workflow=").append(this.workflow)
                .append("}")
                .toString();
    }
}
