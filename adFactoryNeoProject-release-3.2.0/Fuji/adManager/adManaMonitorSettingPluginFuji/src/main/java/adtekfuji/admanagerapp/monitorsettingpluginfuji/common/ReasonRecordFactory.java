/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.monitorsettingpluginfuji.common;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellComboBox;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;

/**
 *
 * @author ke.yokoi
 */
public class ReasonRecordFactory extends AbstractRecordFactory<ReasonData> {

    private final List<String> reasons;

    /**
     * プロパティ情報型表示用セルクラス
     *
     */
    class ReasonComboBoxCellFactory extends ListCell<String> {

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else {
                reasons.stream().filter((e) -> (item.equals(e))).forEach((e) -> {
                    setText(item);
                });
            }
        }
    }

    public ReasonRecordFactory(Table table, List<String> reasons, LinkedList<ReasonData> text) {
        super(table, text);
        this.reasons = reasons;
    }

    @Override
    protected Record createRecord(ReasonData entity) {
        Record record = new Record(super.getTable(), true);
        record.setEditableOrder(true);

        Callback<ListView<String>, ListCell<String>> comboCellFactory = (ListView<String> param) -> new ReasonComboBoxCellFactory();

        LinkedList<AbstractCell> cells = new LinkedList<>();
        if (Objects.isNull(entity.getReason())) {
            entity.setReason(reasons.get(0));
        }
        cells.add(new CellComboBox<>(record, reasons, new ReasonComboBoxCellFactory(), comboCellFactory, entity.reasonProperty()).addStyleClass("ContentComboBox"));
        record.setCells(cells);
        return record;
    }

    @Override
    public Class getEntityClass() {
        return ReasonData.class;
    }
}
