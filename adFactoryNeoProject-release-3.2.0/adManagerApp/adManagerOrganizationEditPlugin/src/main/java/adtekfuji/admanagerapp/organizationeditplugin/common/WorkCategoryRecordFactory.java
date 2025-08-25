/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.organizationeditplugin.common;

import adtekfuji.cash.CashManager;
import adtekfuji.locale.LocaleUtils;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.indirectwork.WorkCategoryInfoEntity;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellComboBox;
import jp.adtekfuji.javafxcommon.property.CellLabel;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;

/**
 * 作業区分設定
 * 
 * @author s-maeda
 */
public class WorkCategoryRecordFactory extends AbstractRecordFactory<SimpleRecordParam> {

    /**
     * 作業区分セル
     *
     */
    class WorkCategoryComboBoxCellFactory extends ListCell<Long> {
        @Override
        protected void updateItem(Long id, boolean empty) {
            super.updateItem(id, empty);
            if (empty) {
                setText("");
            } else if (id.equals(0l)) {
                setText("");
            } else {
                entities.stream().filter(o -> Objects.equals(id, o.getWorkCategoryId())).findFirst()
                    .ifPresent(o -> setText(o.getWorkCategoryName()));
            }
        }
    }

    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final CashManager cashManager = CashManager.getInstance();
    private final List<WorkCategoryInfoEntity> entities = cashManager.getItemList(WorkCategoryInfoEntity.class, new ArrayList());

    /**
     * コンストラクタ
     * 
     * @param table
     * @param items 
     */
    public WorkCategoryRecordFactory(Table table, LinkedList<SimpleRecordParam> items) {
        super(table, items);
    }

    @Override
    protected Record createRecord(SimpleRecordParam item) {
        Record record = new Record(super.getTable(), true);
        Callback<ListView<Long>, ListCell<Long>> comboCellFactory = (p) -> new WorkCategoryComboBoxCellFactory();

        LinkedList<AbstractCell> cells = new LinkedList<>();
        List<Long> idDatas = new ArrayList<>();
        entities.stream().forEach((e) -> {
            idDatas.add(e.getWorkCategoryId());
        });

        if (entities.size() >= 1) {
            if (Objects.isNull(item.getKey())) {
                item.setKey(entities.get(0).getWorkCategoryId());
            }
            cells.add(new CellComboBox<>(record, idDatas, new WorkCategoryComboBoxCellFactory(),
                    comboCellFactory, item.getKeyProperty()).addStyleClass("ContentComboBox"));
        } else {
            cells.add(new CellLabel(record, new SimpleStringProperty(
                    String.format(LocaleUtils.getString("key.NotData"), LocaleUtils.getString("key.WorkClassification")))));
        }

        record.setCells(cells);

        return record;
    }

    @Override
    public Class getEntityClass() {
        return SimpleRecordParam.class;
    }
}
