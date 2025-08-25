/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.andonsetting.common;

import adtekfuji.locale.LocaleUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellComboBox;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.javafxcommon.property.CellLabel;

/**
 *
 * @author e.mori
 */
public class BreakTimeRecordFactory extends AbstractRecordFactory<BreakTimeIdData> {

    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final List<BreakTimeInfoEntity> breaktimes;
    private ChangeListener<Long> actionListener;

    /**
     * プロパティ情報型表示用セルクラス
     *
     */
    class BreakTimeComboBoxCellFactory extends ListCell<Long> {

        @Override
        protected void updateItem(Long item, boolean empty) {
            SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss");
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else if (item.equals(0l)) {
                setText("");
            } else {
                breaktimes.stream().filter((e) -> (item.equals(e.getBreaktimeId()))).forEach((e) -> {
                    setText(e.getBreaktimeName() + "(" + sf.format(e.getStarttime()) + " - " + sf.format(e.getEndtime()) + ")");
                });
            }
        }
    }

    public BreakTimeRecordFactory(Table table, List<BreakTimeInfoEntity> breaktimes, LinkedList<BreakTimeIdData> entitys) {
        super(table, entitys);
        this.breaktimes = breaktimes;
    }

    public BreakTimeRecordFactory actionListner(ChangeListener<Long> actionListner) {
        this.actionListener = actionListner;
        return this;
    }

    @Override
    protected Record createRecord(BreakTimeIdData entity) {
        Record record = new Record(super.getTable(), true);
        Callback<ListView<Long>, ListCell<Long>> comboCellFactory = (ListView<Long> param) -> new BreakTimeComboBoxCellFactory();

        LinkedList<AbstractCell> cells = new LinkedList<>();
        //休憩
        List<Long> idDatas = new ArrayList<>();
        breaktimes.stream().forEach((e) -> {
            idDatas.add(e.getBreaktimeId());
        });

        if (breaktimes.size() >= 1) {
            if (Objects.isNull(entity.getId())) {
                entity.setId(breaktimes.get(0).getBreaktimeId());
            }
            cells.add(new CellComboBox<>(record, idDatas, new BreakTimeComboBoxCellFactory(), comboCellFactory, entity.getIdProperty()).actionListner(this.actionListener).addStyleClass("ContentComboBox"));
        } else {
            cells.add(new CellLabel(record, new SimpleStringProperty(String.format(LocaleUtils.getString("key.NotData"), LocaleUtils.getString("key.BreakTime")))));
        }

        record.setCells(cells);

        return record;
    }

    @Override
    public Class getEntityClass() {
        return BreakTimeIdData.class;
    }
}
