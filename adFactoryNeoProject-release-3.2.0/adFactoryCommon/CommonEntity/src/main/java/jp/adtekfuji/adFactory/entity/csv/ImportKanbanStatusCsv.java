/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.csv;

import java.util.Objects;

/**
 * カンバンステータス インポート用データ
 *
 * @author nar-nakamura
 */
public class ImportKanbanStatusCsv {

    private String kanbanName;// カンバン名
    private String kanbanStatus;// カンバンステータス

    /**
     * カンバンスタータス インポート用データ
     */
    public ImportKanbanStatusCsv() {
    }

    /**
     * カンバンタータス インポート用データ
     *
     * @param kanbanName カンバン名
     * @param kanbanStatus カンバンステータス
     */
    public ImportKanbanStatusCsv(String kanbanName, String kanbanStatus) {
        this.kanbanName = kanbanName;
        this.kanbanStatus = kanbanStatus;
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
    public String getKanbanStatus() {
        return this.kanbanStatus;
    }

    /**
     * カンバンステータスを設定する。
     *
     * @param kanbanStatus カンバンステータス
     */
    public void setKanbanStatus(String kanbanStatus) {
        this.kanbanStatus = kanbanStatus;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.kanbanName);
        hash = 41 * hash + Objects.hashCode(this.kanbanStatus);
        return hash;
    }

    @Override
    public String toString() {
        return new StringBuilder("ImportKanbanStatusCsv{")
                .append("kanbanName=").append(this.kanbanName)
                .append(", kanbanStatus=").append(this.kanbanStatus)
                .append("}")
                .toString();
    }
}
