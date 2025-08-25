/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.monitorsettingpluginels.common;

import adtekfuji.locale.LocaleUtils;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellComboBox;
import jp.adtekfuji.javafxcommon.property.CellLabel;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;

/**
 *
 * @author e.mori
 */
public class WeekDayRecordFactory extends AbstractRecordFactory<WeekDayData> {

    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final List<DayOfWeek> weekdays;

    /**
     * プロパティ情報型表示用セルクラス
     *
     */
    class BreakTimeComboBoxCellFactory extends ListCell<DayOfWeek> {

        @Override
        protected void updateItem(DayOfWeek item, boolean empty) {
            SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss");
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else if (item.equals(0l)) {
                setText("");
            } else {
                weekdays.stream().filter((e) -> (item.equals(e))).forEach((e) -> {
                    setText(e.getDisplayName(TextStyle.FULL, Locale.JAPANESE));
                });
            }
        }
    }

    public WeekDayRecordFactory(Table table, List<DayOfWeek> weekdays, LinkedList<WeekDayData> entitys) {
        super(table, entitys);
        this.weekdays = weekdays;
    }

    @Override
    protected Record createRecord(WeekDayData entity) {
        Record record = new Record(super.getTable(), true);
        Callback<ListView<DayOfWeek>, ListCell<DayOfWeek>> comboCellFactory = (ListView<DayOfWeek> param) -> new BreakTimeComboBoxCellFactory();

        LinkedList<AbstractCell> cells = new LinkedList<>();
        if (weekdays.size() >= 1) {
            if (Objects.isNull(entity.getWeekDay())) {
                entity.setWeekDay(weekdays.get(0));
            }
            cells.add(new CellComboBox<>(record, weekdays, new BreakTimeComboBoxCellFactory(), comboCellFactory, entity.weekDayProperty()).addStyleClass("ContentComboBox"));
        } else {
            cells.add(new CellLabel(record, new SimpleStringProperty(String.format(LocaleUtils.getString("key.NotData"), LocaleUtils.getString("key.BreakTime")))));
        }

        record.setCells(cells);

        return record;
    }

    @Override
    public Class getEntityClass() {
        return WeekDayData.class;
    }
}
