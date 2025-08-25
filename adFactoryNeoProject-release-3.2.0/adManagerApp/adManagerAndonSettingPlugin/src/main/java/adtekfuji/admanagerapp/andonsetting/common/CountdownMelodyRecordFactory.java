/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.andonsetting.common;

import adtekfuji.locale.LocaleUtils;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.andon.entity.CountdownMelodyInfoEntity;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellFileChooser;
import jp.adtekfuji.javafxcommon.property.CellLabel;
import jp.adtekfuji.javafxcommon.property.CellNumericField;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;

/**
 * サイクルカウントダウンメロディ 表示用レコード生成
 *
 * @author s-maeda
 */
public class CountdownMelodyRecordFactory extends AbstractRecordFactory<CountdownMelodyInfoEntity> {

    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    public CountdownMelodyRecordFactory(Table table, LinkedList<CountdownMelodyInfoEntity> countdownMelody) {
        super(table, countdownMelody);
    }

    @Override
    protected Record createRecord(CountdownMelodyInfoEntity entity) {
        Record record = new Record(super.getTable(), false);
        LinkedList<AbstractCell> cells = new LinkedList<>();

        String header = "";
        String footer = "";
        AbstractCell field;
        switch (entity.getMelodyInfoType()) {
            case PATH_BEFORE_COUNTDOWN:
                header = LocaleUtils.getString("key.CountdownMelodyType.BeforeCountdown");
                field = new CellFileChooser(record, (StringProperty) entity.melodyInfoBodyProperty());
                break;
            case PATH_COUNTDOWN_START:
                header = LocaleUtils.getString("key.CountdownMelodyType.CountdownStart");
                field = new CellFileChooser(record, (StringProperty) entity.melodyInfoBodyProperty());
                break;
            case PATH_BEFORE_END_OF_COUNTDOWN:
                header = LocaleUtils.getString("key.CountdownMelodyType.BeforeEndOfCountdown");
                field = new CellFileChooser(record, (StringProperty) entity.melodyInfoBodyProperty());
                break;
            case PATH_WORK_DELAYED:
                header = LocaleUtils.getString("key.CountdownMelodyType.WorkDelayed");
                field = new CellFileChooser(record, (StringProperty) entity.melodyInfoBodyProperty());
                break;
            case PATH_BREAKTIME_START:
                header = LocaleUtils.getString("key.CountdownMelodyType.BreaktimeStart");
                field = new CellFileChooser(record, (StringProperty) entity.melodyInfoBodyProperty());
                break;
            case PATH_BEFORE_END_OF_BREAKTIME:
                header = LocaleUtils.getString("key.CountdownMelodyType.BeforeEndOfBreaktime");
                field = new CellFileChooser(record, (StringProperty) entity.melodyInfoBodyProperty());
                break;
            case TIME_RING_TIMING_END_OF_COUNTDOWN:
            case TIME_RING_TIMING_END_OF_BREAKTIME:
                footer = LocaleUtils.getString("key.CountdownMelodyType.RingTiming");
                if (entity.melodyInfoBodyProperty().get().isEmpty()) {
                    entity.setMelodyInfoBody("60");
                }
                field = new CellNumericField(record, (StringProperty) entity.melodyInfoBodyProperty()).addStyleClass("ContentTextBox");
                break;
            default:
                header = entity.getMelodyInfoType().name();
                field = new CellFileChooser(record, (StringProperty) entity.melodyInfoBodyProperty());
                break;
        }
        cells.add(new CellLabel(record, header).addStyleClass("ContentTitleLabel"));
        cells.add(field);
        cells.add(new CellLabel(record, footer).addStyleClass("ContentTitleLabel"));

        record.setCells(cells);
        return record;
    }

    @Override
    public Class getEntityClass() {
        return CountdownMelodyInfoEntity.class;
    }
}
