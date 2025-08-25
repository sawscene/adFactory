/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

/**
 *
 * @author e-mori
 */
public interface RecordInterface {

    void addRecord(Record record, int rowIndex);

    void removeRecord(Record record);

    void clearRecord();

    void registCheckListener(PropertyChangeListener listener);

    /**
     * 表示順を上げる
     *
     * @param record
     */
    void increaseOrder(Record record);

    /**
     * 表示順を下げる
     *
     * @param record
     */
    void decreaseOrder(Record record);

    /**
     * タイトルを表示するかどうか
     *
     * @param isTitle
     */
    void setTitle(boolean isTitle);
}
