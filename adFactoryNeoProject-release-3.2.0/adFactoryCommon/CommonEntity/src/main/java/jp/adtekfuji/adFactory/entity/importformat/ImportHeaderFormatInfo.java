/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.importformat;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * CSV形式（ヘッダー名指定）_インポートフォーマット設定
 *
 * @author (AQTOR)Koga
 */
@XmlRootElement(name = "importHeaderFormatInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class ImportHeaderFormatInfo {

    private WorkHeaderFormatInfo workHeaderFormatInfo;
    private WorkPropHeaderFormatInfo workPropHeaderFormatInfo;
    private WorkflowHeaderFormatInfo workflowHeaderFormatInfo;
    private WorkflowPropHeaderFormatInfo workflowPropHeaderFormatInfo;
    private WorkKanbanHeaderFormatInfo workKanbanHeaderFormatInfo;
    private WorkKanbanPropHeaderFormatInfo workKanbanPropHeaderFormatInfo;
    private KanbanHeaderFormatInfo kanbanHeaderFormatInfo;
    private KanbanPropHeaderFormatInfo kanbanPropHeaderFormatInfo;

    /**
     * コンストラクタ
     */
    public ImportHeaderFormatInfo() {
    }

    /**
     * カンバンのフォーマット情報を取得する。
     *
     * @return カンバンのフォーマット情報
     */
    public KanbanHeaderFormatInfo getKanbanHeaderFormatInfo() {
        return this.kanbanHeaderFormatInfo;
    }

    /**
     * カンバンのフォーマット情報を設定する。
     *
     * @param kanbanHeaderFormatInfo カンバンのフォーマット情報
     */
    public void setKanbanHeaderFormatInfo(KanbanHeaderFormatInfo kanbanHeaderFormatInfo) {
        this.kanbanHeaderFormatInfo = kanbanHeaderFormatInfo;
    }

    /**
     * カンバンプロパティのフォーマット情報を取得する。
     *
     * @return カンバンプロパティのフォーマット情報
     */
    public KanbanPropHeaderFormatInfo getKanbanPropHeaderFormatInfo() {
        return this.kanbanPropHeaderFormatInfo;
    }

    /**
     * カンバンプロパティのフォーマット情報を設定する。
     *
     * @param kanbanPropHeaderFormatInfo カンバンプロパティのフォーマット情報
     */
    public void setKanbanPropHeaderFormatInfo(KanbanPropHeaderFormatInfo kanbanPropHeaderFormatInfo) {
        this.kanbanPropHeaderFormatInfo = kanbanPropHeaderFormatInfo;
    }

    /**
     * 工程カンバンのフォーマット情報を取得
     * @return 工程カンバンプロパティのフォーマット情報
     */
    public WorkKanbanHeaderFormatInfo getWorkKanbanHeaderFormatInfo() {
        return workKanbanHeaderFormatInfo;
    }

    /**
     * 工程カンバンフォーマットの情報を設定
     * @param workKanbanHeaderFormatInfo 工程カンバンフォーマット情報
     */
    public void setWorkKanbanHeaderFormatInfo(WorkKanbanHeaderFormatInfo workKanbanHeaderFormatInfo) {
        this.workKanbanHeaderFormatInfo = workKanbanHeaderFormatInfo;
    }

    /**
     * 工程カンバンプロパティフォーマット情報を取得
     * @return 工程カンバンプロパティフォーマット情報
     */
    public WorkKanbanPropHeaderFormatInfo getWorkKanbanPropHeaderFormatInfo() {
        return workKanbanPropHeaderFormatInfo;
    }

    /**
     * 工程カンバンプロパティフォーマット情報を設定
     * @param workKanbanPropHeaderFormatInfo 工程カンバンフォーマット情報
     */
    public void setWorkKanbanPropHeaderFormatInfo(WorkKanbanPropHeaderFormatInfo workKanbanPropHeaderFormatInfo) {
        this.workKanbanPropHeaderFormatInfo = workKanbanPropHeaderFormatInfo;
    }

    /**
     * 工程のフォーマット情報を取得する。
     *
     * @return 工程のフォーマット情報
     */
    public WorkHeaderFormatInfo getWorkHeaderFormatInfo() {
        return workHeaderFormatInfo;
    }
    /**
     * 工程のフォーマット情報を設定する。
     *
     * @param workHeaderFormatInfo 工程のフォーマット情報
     */
    public void setWorkHeaderFormatInfo(WorkHeaderFormatInfo workHeaderFormatInfo) {
        this.workHeaderFormatInfo = workHeaderFormatInfo;
    }

    /**
     * 工程プロパティのフォーマット情報を取得する。
     *
     * @return 工程プロパティのフォーマット情報
     */
    public WorkPropHeaderFormatInfo getWorkPropHeaderFormatInfo() {
        return workPropHeaderFormatInfo;
    }
    /**
     * 工程プロパティのフォーマット情報を設定する。
     *
     * @param workPropHeaderFormatInfo 工程プロパティのフォーマット情報
     */
    public void setWorkPropHeaderFormatInfo(WorkPropHeaderFormatInfo workPropHeaderFormatInfo) {
        this.workPropHeaderFormatInfo = workPropHeaderFormatInfo;
    }

    /**
     * 工程順のフォーマット情報を取得する。
     *
     * @return 工程順のフォーマット情報
     */
    public WorkflowHeaderFormatInfo getWorkflowHeaderFormatInfo() {
        return workflowHeaderFormatInfo;
    }
    /**
     * 工程順のフォーマット情報を設定する。
     *
     * @param workflowHeaderFormatInfo 工程順のフォーマット情報
     */
    public void setWorkflowHeaderFormatInfo(WorkflowHeaderFormatInfo workflowHeaderFormatInfo) {
        this.workflowHeaderFormatInfo = workflowHeaderFormatInfo;
    }

    /**
     * 工程順プロパティのフォーマット情報を取得する。
     *
     * @return 工程順プロパティのフォーマット情報
     */
    public WorkflowPropHeaderFormatInfo getWorkflowPropHeaderFormatInfo() {
        return workflowPropHeaderFormatInfo;
    }
    /**
     * 工程順プロパティのフォーマット情報を設定する。
     *
     * @param workflowPropHeaderFormatInfo 工程順プロパティのフォーマット情報
     */
    public void setWorkflowPropHeaderFormatInfo(WorkflowPropHeaderFormatInfo workflowPropHeaderFormatInfo) {
        this.workflowPropHeaderFormatInfo = workflowPropHeaderFormatInfo;
    }


    @Override
    public String toString() {
        return new StringBuilder("ImportHeaderFormatInfo{")
                .append("workHeaderFormatInfo=").append(this.workHeaderFormatInfo)
                .append(", workPropHeaderFormatInfo=").append(this.workPropHeaderFormatInfo)
                .append(", workflowHeaderFormatInfo=").append(this.workflowHeaderFormatInfo)
                .append(", workflowPropHeaderFormatInfo=").append(this.workflowPropHeaderFormatInfo)
                .append(", workKanbanHeaderFormatInfo=").append(this.workKanbanHeaderFormatInfo)
                .append(", workKanbanPropHeaderFormatInfo=").append(this.workKanbanPropHeaderFormatInfo)
                .append(", kanbanHeaderFormatInfo=").append(this.kanbanHeaderFormatInfo)
                .append(", kanbanPropHeaderFormatInfo=").append(this.kanbanPropHeaderFormatInfo)
                .append("}")
                .toString();
    }
}
