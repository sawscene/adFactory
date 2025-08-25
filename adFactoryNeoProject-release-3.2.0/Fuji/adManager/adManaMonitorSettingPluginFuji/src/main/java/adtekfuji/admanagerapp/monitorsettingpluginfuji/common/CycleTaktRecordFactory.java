/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.monitorsettingpluginfuji.common;

import adtekfuji.utility.DateUtils;
import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.converter.TimeStringConverter;
import jp.adtekfuji.forfujiapp.entity.monitor.CycleTaktInfoEntity;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellAutoNumberLabel;
import jp.adtekfuji.javafxcommon.property.CellTimeStampField;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;

/**
 * サイクルタクトタイム 表示用レコード生成
 *
 * @author nar-nakamura
 */
public class CycleTaktRecordFactory extends AbstractRecordFactory<CycleTaktInfoEntity> {

    public CycleTaktRecordFactory(Table table, LinkedList<CycleTaktInfoEntity> cycleTakt) {
        super(table, cycleTakt);
    }

    @Override
    protected Record createRecord(CycleTaktInfoEntity entity) {
        Record record = new Record(super.getTable(), true);
        record.setEditableOrder(true);
        LinkedList<AbstractCell> cells = new LinkedList<>();

        TimeStringConverter timeStringConverter = new TimeStringConverter("HH:mm:ss");

        // 番号
        cells.add(new CellAutoNumberLabel(record, "", super.getRecodeNum() + 1).addStyleClass("ContentTitleLabel"));

        // タクトタイム
        if (Objects.isNull(entity.getTaktTime())) {
            Date taktTime;
            int beforeId = this.getEntities().size() - 2;// 1つ前のインデックス
            if (beforeId >= 0) {
                taktTime = this.getEntities().get(beforeId).getTaktTime();
            } else {
                taktTime = DateUtils.min();
            }
            entity.setTaktTime(taktTime);
        }
        StringProperty cycleTaktProp = new SimpleStringProperty(entity.getTaktTime().toString());
        cycleTaktProp.bindBidirectional(entity.taktTimeProperty(), timeStringConverter);
        cells.add(new CellTimeStampField(record, cycleTaktProp).addStyleClass("ContentTextBox"));

        record.setCells(cells);
        return record;
    }

    @Override
    public Class getEntityClass() {
        return CycleTaktInfoEntity.class;
    }
}
