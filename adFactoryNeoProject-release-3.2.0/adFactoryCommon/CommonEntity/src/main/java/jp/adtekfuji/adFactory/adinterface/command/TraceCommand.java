/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.adinterface.command;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

/**
 * トレースコマンド
 * 
 * @author s-heya
 */
public class TraceCommand implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private final Long workKanbanId;
    private final Long equipmentId;
    private final String tag;
    private final Properties values;
    private final Properties authors;
    private final Date sendDatetime;
    private final Boolean isStart;

    /**
     * コンストラクタ
     * 
     * @param workKanbanId
     * @param equipmentId
     * @param tag
     * @param values
     * @param sendDatetime
     * @param isStart
     * @param authors
     */
    public TraceCommand(Long workKanbanId, Long equipmentId, String tag, Properties values, Properties authors, Date sendDatetime, Boolean isStart) {
        this.workKanbanId = workKanbanId;
        this.equipmentId = equipmentId;
        this.tag = tag;
        this.values = values;
        this.authors = authors;
        this.sendDatetime = sendDatetime;
        this.isStart = isStart;
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
     * 設備IDを取得する。
     * 
     * @return 
     */
    public Long getEquipmentId() {
        return equipmentId;
    }

    /**
     * タグを取得する。
     * 
     * @return 
     */
    public String getTag() {
        return tag;
    }

    /**
     * トレーサビリティーデータを取得する。
     * 
     * @return 
     */
    public Properties getValues() {
        return values;
    }

    /**
     * 送信日時を取得する。
     * 
     * @return 
     */
    public Date getDate() {
        return sendDatetime;
    }

    /**
     * 開始時かどうかを返す。
     * 
     * @return 
     */
    public Boolean isStart() {
        return isStart;
    }

    /**
     * 入力者を取得する。
     * 
     * @return 
     */
    public Properties getAuthor() {
        return this.authors;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.workKanbanId);
        hash = 23 * hash + Objects.hashCode(this.equipmentId);
        hash = 23 * hash + Objects.hashCode(this.sendDatetime);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TraceCommand other = (TraceCommand) obj;
        if (!Objects.equals(this.workKanbanId, other.workKanbanId)) {
            return false;
        }
        if (!Objects.equals(this.equipmentId, other.equipmentId)) {
            return false;
        }
        return Objects.equals(this.sendDatetime, other.sendDatetime);
    }

    @Override
    public String toString() {
        return "TraceCommand{" + "workKanbanId=" + workKanbanId + ", equipmentId=" + equipmentId + 
                ", tag=" + tag + ", values=" + values + ", sendDatetime=" + sendDatetime + ", isStart=" + isStart + '}';
    }
}
