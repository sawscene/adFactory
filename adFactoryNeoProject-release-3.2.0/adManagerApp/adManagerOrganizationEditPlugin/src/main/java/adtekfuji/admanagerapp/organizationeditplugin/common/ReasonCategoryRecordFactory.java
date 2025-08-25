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
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.master.ReasonCategoryInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ReasonTypeEnum;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellComboBox;
import jp.adtekfuji.javafxcommon.property.CellLabel;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;

/**
 * 理由区分ファクトリー
 * 
 * @author s-heya
 */
public class ReasonCategoryRecordFactory extends AbstractRecordFactory<SimpleRecordParam> {

    /**
     * 理由区分セル
     */
    class RecordCell extends ListCell<Long> {
        @Override
        protected void updateItem(Long id, boolean empty) {
            super.updateItem(id, empty);
            if (empty) {
                setText("");
            } else if (id.equals(0l)) {
                setText("");
            } else {
                reasonCategories.stream().filter(o -> Objects.equals(id, o.getId())).findFirst()
                    .ifPresent(o -> setText(o.getReasonCategoryName()));
            }
        }
    }

    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final ReasonTypeEnum reasonType; // 理由種別
    private final List<ReasonCategoryInfoEntity> reasonCategories;
    
    /**
     * コンストラクタ
     * 
     * @param table
     * @param items
     * @param reasonType
     */
    public ReasonCategoryRecordFactory(Table table, LinkedList<SimpleRecordParam> items, ReasonTypeEnum reasonType) {
        super(table, items);

        this.reasonType = reasonType;
        List<ReasonCategoryInfoEntity> list = CashManager.getInstance().getItemList(ReasonCategoryInfoEntity.class, new ArrayList());
        this.reasonCategories = list.stream().filter(o -> this.reasonType.equals(o.getReasonType())).collect(Collectors.toList());
    }

    /**
     * データ行を作成する。
     * 
     * @param item
     * @return 
     */
    @Override
    protected Record createRecord(SimpleRecordParam item) {
        Record record = new Record(this.getTable(), true);
        Callback<ListView<Long>, ListCell<Long>> comboCellFactory = (p) -> new RecordCell();
        LinkedList<AbstractCell> cells = new LinkedList<>();

        if (!this.reasonCategories.isEmpty()) {
            if (Objects.isNull(item.getKey())) {
                item.setKey(this.reasonCategories.get(0).getId());
            }

            List<Long> list = this.reasonCategories.stream().map(o -> o.getId()).collect(Collectors.toList());
            cells.add(new CellComboBox<>(record, list, new RecordCell(), comboCellFactory, item.getKeyProperty()).addStyleClass("ContentComboBox"));
        } else {
            // 未設定
            cells.add(new CellLabel(record, new SimpleStringProperty(String.format(LocaleUtils.getString("key.NotData"), this.getTable().getTitleName()))));
        }

        record.setCells(cells);
        return record;
    }

    /**
     * エンティティクラスを取得する。
     * 
     * @return 
     */
    @Override
    public Class getEntityClass() {
        return SimpleRecordParam.class;
    }
}
