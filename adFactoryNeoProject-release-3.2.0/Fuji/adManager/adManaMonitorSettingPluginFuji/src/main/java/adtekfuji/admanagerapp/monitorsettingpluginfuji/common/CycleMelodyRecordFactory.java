/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.monitorsettingpluginfuji.common;

import adtekfuji.locale.LocaleUtils;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.forfujiapp.entity.monitor.CycleMelodyInfoEntity;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellFileChooser;
import jp.adtekfuji.javafxcommon.property.CellLabel;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;

/**
 * サイクルメロディ 表示用レコード生成
 *
 * @author nar-nakamura
 */
public class CycleMelodyRecordFactory extends AbstractRecordFactory<CycleMelodyInfoEntity> {

    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    public CycleMelodyRecordFactory(Table table, LinkedList<CycleMelodyInfoEntity> cycleMelody) {
        super(table, cycleMelody);
    }

    @Override
    protected Record createRecord(CycleMelodyInfoEntity entity) {
        Record record = new Record(super.getTable(), false);
        LinkedList<AbstractCell> cells = new LinkedList<>();

        // サイクルメロディ種別
        String melodyType;
        switch (entity.getMelodyType()) {
            case WORK_START_30SEC:
                melodyType = LocaleUtils.getString("key.ForFuji.CycleMelody.WorkStart30sec");
                break;
            case CYCLE_START:
                melodyType = LocaleUtils.getString("key.ForFuji.CycleMelody.CycleStart");
                break;
            case CYCLE_60SEC:
                melodyType = LocaleUtils.getString("key.ForFuji.CycleMelody.Cycle60sec");
                break;
            case CYCLE_30SEC:
                melodyType = LocaleUtils.getString("key.ForFuji.CycleMelody.Cycle30sec");
                break;
            case CYCLE_5SEC:
                melodyType = LocaleUtils.getString("key.ForFuji.CycleMelody.Cycle5sec");
                break;
            case LUNCH_TIME_START:
                melodyType = LocaleUtils.getString("key.ForFuji.CycleMelody.LunchTimeStart");
                break;
            case LUNCH_TIME_30SEC:
                melodyType = LocaleUtils.getString("key.ForFuji.CycleMelody.LunchTime30sec");
                break;
            case REFRESH_TIME_START:
                melodyType = LocaleUtils.getString("key.ForFuji.CycleMelody.RefreshTimeStart");
                break;
            case REFRESH_TIME_30SEC:
                melodyType = LocaleUtils.getString("key.ForFuji.CycleMelody.RefreshTime30sec");
                break;
            case WORK_END:
                melodyType = LocaleUtils.getString("key.ForFuji.CycleMelody.WorkEnd");
                break;
            default:
                melodyType = entity.getMelodyType().name();
        }
        cells.add(new CellLabel(record, melodyType).addStyleClass("ContentTitleLabel"));

        // サイクルメロディパス
        //cells.add(new CellTextField(record, (StringProperty) entity.melodyPathProperty()).addStyleClass("ContentTextBox"));
        cells.add(new CellFileChooser(record, (StringProperty) entity.melodyPathProperty()));

        record.setCells(cells);
        return record;
    }

    @Override
    public Class getEntityClass() {
        return CycleMelodyInfoEntity.class;
    }
}
