/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.common;

import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

/**
 * ステータス変更パラメータ
 * 
 * @author s-heya
 */
public class StatusChange {
    private KanbanStatusEnum newStatus;
    private Boolean forced;

    /**
     * コンストラクタ
     */
    public StatusChange() {
        this.forced = false;
    }
    
    /**
     * 変更後のステータスを取得する。
     * 
     * @return 変更後のステータス
     */
    public KanbanStatusEnum getNewStatus() {
        return newStatus;
    }

    /**
     * 変更後のステータスを設定する。
     * 
     * @param newStatus 変更後のステータス
     */
    public void setNewStatus(KanbanStatusEnum newStatus) {
        this.newStatus = newStatus;
    }

    /**
     * 強制的にステータスを変更するかどうかを返す。
     * 
     * @return true:強制的にステータスを変更する、false:安全にステータスを変更する
     */
    public Boolean isForced() {
        return forced;
    }
    
    /**
     * 強制的にステータスを変更するかどうかを設定する。
     * 
     * @param forced true:強制的にステータスを変更する、false:安全にステータスを変更する
     */
    public void setForced(Boolean forced) {
        this.forced = forced;
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return "StatusChange{" + "newStatus=" + newStatus + ", forced=" + forced + '}';
    }
}
