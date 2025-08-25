/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.ContextMenu;

/**
 *
 * @author e-mori
 * @param <E>
 */
public abstract class AbstractRecordFactory<E> implements RecordFactoryInterface {

    private final Table table;
    private final LinkedList<E> entities;
    private final LinkedList<Record> records = new LinkedList<>();
    private Record culomnTitleRecord = null;

    public AbstractRecordFactory(Table table, LinkedList<E> entities) {
        this.table = table;
        this.entities = entities;
    }

    protected void createRecords() {
        this.entities.stream().forEach((entity) -> {
            records.add(createRecord(entity));
        });
    }

    protected List<Record> getRecords() {
        return records;
    }

    protected List<Object> getCheckedRecordItems() {
        List<Object> checkedRecordItems = new LinkedList<>();
        records.stream().filter((record) -> (record.isChecked())).forEach((record) -> {
            checkedRecordItems.add(record.getRecordItem());
        });

        return checkedRecordItems;
    }

    protected Record createRecord(E entity) {
        Record record = new Record(table);
        return record;
    }

    protected Record createCulomunTitleRecord(){
        culomnTitleRecord = new Record(table);
        return culomnTitleRecord;
    }

    public Record addRecord() {
        Record newRecord = createRecord(addEntity());
        records.add(newRecord);
        return newRecord;
    }

    protected E addEntity() {
        try {
            E entity = (E) getEntityClass().newInstance();
            entities.add(entity);
            return entity;
        } catch (InstantiationException | IllegalAccessException e) {

        }
        return null;
    }

    /**
     * 行を削除する。
     *
     * @param record
     */
    public void removeRecord(Record record) {
        int index = records.indexOf(record);
        if (0 <= index) {
            entities.remove(records.indexOf(record));
        }
        if (records.contains(record)) {
            records.remove(record);
        }
    }

    public void clearRecord() {
        entities.clear();
        records.clear();
    }

    public Table getTable() {
        return table;
    }

    public int getRecodeNum() {
        return records.size();
    }

    /**
     * エンティティを取得する。
     *
     * @return
     */
    public List<E> getEntities() {
        return this.entities;
    }

    /**
     * 対象のエンティティを取得する。
     *
     * @param record
     * @return
     */
    public E getEntity(Record record) {
        return this.entities.get(this.getRowIndex(record));
    }

    /**
     * 対象の行番号を取得する。
     *
     * @param record
     * @return
     */
    public int getRowIndex(Record record) {
        return this.records.indexOf(record);
    }

    /**
     * 表示順を上げる
     *
     * @param record
     */
    public void increaseOrder(Record record) {
        int index = this.records.indexOf(record) - 1;
        if (index >= 0) {
            E entity = this.entities.get(this.records.indexOf(record));
            this.removeRecord(record);
            this.entities.add(index, entity);
            this.records.add(index, record);
        }
    }

    /**
     * 表示順を下げる
     *
     * @param record
     */
    public void decreaseOrder(Record record) {
        int index = this.records.indexOf(record) + 1;
        if (index < this.records.size()) {
            E entity = this.entities.get(this.records.indexOf(record));
            this.removeRecord(record);
            this.entities.add(index, entity);
            this.records.add(index, record);
        }
    } 

    /**
     * コンテキストメニューのメニュー設定を取得
     *
     * @param record 対象行のRecordクラス
     * @return Lコンテキストメニューのメニュー設定(未設定の場合は0件)
     */
    public Optional<ContextMenu> createContextMenu(Record record) {
        return Optional.empty();
    }

}
