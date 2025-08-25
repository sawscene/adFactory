/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.csv;

/**
 * 製品情報 インポート用データ
 *
 * @author nar-nakamura
 */
public class ImportProductCsv {

    private String uniqueId;// ユニークID
    private String kanbanName;// カンバン名

    /**
     * 製品情報 インポート用データ
     */
    public ImportProductCsv() {
    }

    /**
     * ユニークIDを取得する。
     *
     * @return ユニークID
     */
    public String getUniqueId() {
        return this.uniqueId;
    }

    /**
     * ユニークIDを設定する。
     *
     * @param uniqueId ユニークID
     */
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
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

    @Override
    public String toString() {
        return new StringBuilder("ImportProductCsv{")
                .append("uniqueId=").append(this.uniqueId)
                .append(", kanbanName=").append(this.kanbanName)
                .append("}")
                .toString();
    }
}
