/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.adinterface.command;

import java.io.Serializable;
import java.util.Date;

/**
 * 作業コマンド
 * 
 * @author s-heya
 */
public class WorkCommand implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String request;
    private final Long kanbanId;
    private final Long workKanbanId;
    private final Date dateTime;

    /**
     * コンストラクタ
     * 
     * @param request
     * @param kanbanId
     * @param workKanbanId
     * @param dateTime
     */
    public WorkCommand(String request, Long kanbanId, Long workKanbanId, Date dateTime) {
        this.request = request;
        this.kanbanId = kanbanId;
        this.workKanbanId = workKanbanId;
        this.dateTime = dateTime;
    }

    /**
     * リクエストを取得する。
     * 
     * "complete" : 指定された工程カンバンの作業を完了する
     * "cancel" : 指定されたカンバンの作業を中止する
     * 
     * @return 
     */
    public String getRequest() {
        return request;
    }

    /**
     * カンバンIDを取得する。
     * 
     * @return 
     */
    public Long getKanbanId() {
        return kanbanId;
    }

    /**
     * 工程カンバンIDを取得する。
     * 
     * @return 
     */
    public Long getWorkKanbanId() {
        return workKanbanId;
    }

    /**
     * 日時を取得する。
     * 
     * @return 
     */
    public Date getDateTime() {
        return dateTime;
    }

    @Override
    public String toString() {
        return "WorkCommand{" + "request=" + request + ", kanbanId=" + kanbanId + ", workKanbanId=" + workKanbanId + ", datetime=" + dateTime + '}';
    }
}
