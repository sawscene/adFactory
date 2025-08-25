/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.adinterface.command;

import java.io.Serializable;
import java.util.Date;

/**
 * 作業キャンセルコマンド
 *
 * @author s-heya
 */
public class CancelWorkCommand implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long kanbanId;// カンバンID
    private Date dateTime;// 日時

    /**
     * コンストラクタ
     *
     * @param kanbanId カンバンID
     * @param dateTime 日時
     */
    public CancelWorkCommand(Long kanbanId, Date dateTime) {
        this.kanbanId = kanbanId;
        this.dateTime = dateTime;
    }

    /**
     * カンバンIDを取得する。
     *
     * @return カンバンID
     */
    public Long getKanbanId() {
        return this.kanbanId;
    }

    /**
     * カンバンIDを設定する。
     *
     * @param kanbanId カンバンID
     */
    public void setKanbanId(Long kanbanId) {
        this.kanbanId = kanbanId;
    }

    /**
     * 日時を取得する。
     *
     * @return 日時
     */
    public Date getDateTime() {
        return this.dateTime;
    }

    /**
     * 日時を設定する。
     *
     * @param dateTime 日時
     */
    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return new StringBuilder("CancelWorkCommand{")
                .append("kanbanId=").append(this.kanbanId)
                .append(", dateTime=").append(this.dateTime)
                .append("}")
                .toString();
    }
}
