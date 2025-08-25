/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.kanban;

import jp.adtekfuji.adFactory.enumerate.ContentTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

/**
 * 工程カンバン詳細情報
 * ※.NamedQueryで工程カンバンの関連情報を取得する際に使用。
 *
 * @author nar-nakamura
 */
public class WorkKanbanDetail {

    private String kanbanName;// カンバン名
    private KanbanStatusEnum kanbanStatus;// カンバンステータス
    private String workflowName;// 工程順名
    private Integer workflowRev;// 工程順版数
    private String workName;// 工程名
    private String content;// コンテンツ
    private ContentTypeEnum contentType;// コンテンツ種別

    /**
     * コンストラクタ
     */
    public WorkKanbanDetail() {
    }

    /**
     * コンストラクタ
     *
     * @param kanbanName カンバン名
     * @param kanbanStatus カンバンステータス
     * @param workflowName 工程順名
     * @param workflowRev 工程順版数
     * @param workName 工程名
     * @param content コンテンツ
     * @param contentType コンテンツ種別
     */
    public WorkKanbanDetail(String kanbanName, KanbanStatusEnum kanbanStatus,String workflowName, Integer workflowRev, String workName, String content, ContentTypeEnum contentType) {
        this.kanbanName = kanbanName;
        this.kanbanStatus = kanbanStatus;
        this.workflowName = workflowName;
        this.workflowRev = workflowRev;
        this.workName = workName;
        this.content = content;
        this.contentType = contentType;
    }

    /**
     * カンバン名を取得する。
     *
     * @return カンバン名
     */
    public String getKanbanName() {
        return this.kanbanName;
    }

    /**
     * カンバン名を設定する。
     *
     * @param kanbanName カンバン名
     */
    public void setKanbanName(String kanbanName) {
        this.kanbanName = kanbanName;
    }

    /**
     * カンバンステータスを取得する。
     *
     * @return カンバンステータス
     */
    public KanbanStatusEnum getKanbanStatus() {
        return this.kanbanStatus;
    }

    /**
     * カンバンステータスを設定する。
     *
     * @param kanbanStatus カンバンステータス
     */
    public void setKanbanStatus(KanbanStatusEnum kanbanStatus) {
        this.kanbanStatus = kanbanStatus;
    }

    /**
     * 工程順名を取得する。
     *
     * @return 工程順名
     */
    public String getWorkflowName() {
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
     * 工程順版数を取得する。
     *
     * @return 工程順版数
     */
    public Integer getWorkflowRev() {
        return this.workflowRev;
    }

    /**
     * 工程順版数を設定する。
     *
     * @param workflowRev 工程順版数
     */
    public void setWorkflowRev(Integer workflowRev) {
        this.workflowRev = workflowRev;
    }

    /**
     * 工程名を取得する。
     *
     * @return 工程名
     */
    public String getWorkName() {
        return this.workName;
    }

    /**
     * 工程名を設定する。
     *
     * @param workName 工程名
     */
    public void setWorkName(String workName) {
        this.workName = workName;
    }

    /**
     * コンテンツを取得する。
     *
     * @return コンテンツ
     */
    public String getContent() {
        return this.content;
    }

    /**
     * コンテンツを設定する。
     *
     * @param content コンテンツ
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * コンテンツ種別を取得する。
     *
     * @return コンテンツ種別
     */
    public ContentTypeEnum getContentType() {
        return this.contentType;
    }

    /**
     * コンテンツ種別を設定する。
     *
     * @param contentType コンテンツ種別
     */
    public void setContentType(ContentTypeEnum contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return new StringBuilder("WorkKanbanDetail{")
                .append("kanbanName=").append(this.kanbanName)
                .append(", ")
                .append("kanbanStatus=").append(this.kanbanStatus)
                .append(", ")
                .append("workflowName=").append(this.workflowName)
                .append(", ")
                .append("workflowRev=").append(this.workflowRev)
                .append(", ")
                .append("workName=").append(this.workName)
                .append(", ")
                .append("content=").append(this.content)
                .append(", ")
                .append("contentType=").append(this.contentType)
                .append("}")
                .toString();
    }
}
