/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.common;

import adtekfuji.admanagerapp.productionnaviplugin.utils.WorkPlanWorkGroupPropertyData;
import adtekfuji.locale.LocaleUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellDateAndTimeStampField;
import jp.adtekfuji.javafxcommon.property.CellLabel;
import jp.adtekfuji.javafxcommon.property.CellNumericLongField;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;

/**
 * ワークグループレコード生成クラス
 *
 * @author e.mori
 * @version 1.6.1
 * @since 2017.1.11.Wen
 */
public class WorkPlanWorkGroupPropertyDataRecordFactory extends AbstractRecordFactory<WorkPlanWorkGroupPropertyData> {

    private final ObjectProperty<Date> startTimeProperty;
    private final IntegerProperty sumProperty;

    public WorkPlanWorkGroupPropertyDataRecordFactory(Table table, LinkedList<WorkPlanWorkGroupPropertyData> entities, ObjectProperty<Date> startTimeProperty, IntegerProperty sumProperty) {
        super(table, entities);
        this.startTimeProperty = startTimeProperty;
        this.sumProperty = sumProperty;
    }

    /**
     * テーブルのタイトルカラムを生成
     *
     * @return
     */
    @Override
    protected Record createCulomunTitleRecord() {
        Record titleColumnrecord = new Record(super.getTable(), false);
        List<AbstractCell> cells = new ArrayList();
        //タイトルに該当するカラムを追加する

        // 生産数
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.ProductionNum") + "*")).addStyleClass("ContentTitleLabel"));
        // 開始時刻
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.StartTime") + "*")).addStyleClass("ContentTitleLabel"));

        titleColumnrecord.setTitleCells(cells);

        return titleColumnrecord;
    }

    /**
     * テーブルのレコードを生成
     *
     * @param entity
     * @return
     */
    @Override
    protected Record createRecord(WorkPlanWorkGroupPropertyData entity) {

        entity.setQauntity(1L);
        if (Objects.nonNull(this.startTimeProperty)) {
            entity.setStartTime(this.startTimeProperty.get());
        } else {
            entity.setStartTime(new Date());
        }

        entity.qauntityProperty().addListener((oservable, oldValue, newValue) -> {
            sumTotal();
        });

        Record record = new Record(super.getTable(), true);
        List<AbstractCell> cells = new ArrayList();

        // 生産数
        cells.add(new CellNumericLongField(record, entity.qauntityProperty()).addRange(1L, 300L));
        // 開始時刻
        cells.add(new CellDateAndTimeStampField(record, entity.startTimeProperty(), false));

        record.setCells(cells);
        record.setRecordItem(entity);

        sumTotal();

        return record;
    }

    /**
     * テーブル生成クラスが使用しているクラスを返す
     *
     * @return
     */
    @Override
    public Class getEntityClass() {
        return WorkPlanWorkGroupPropertyData.class;
    }

    /**
     * 総数を計算する
     */
    private void sumTotal() {
        int sum = 0;
        sum = this.getEntities().stream().map((data) -> data.getQauntity().intValue()).reduce(sum, Integer::sum);
        this.sumProperty.set(sum);
    }

    /**
     * 行を削除する
     *
     * @param record
     */
    @Override
    public void removeRecord(Record record) {
        super.removeRecord(record);
        sumTotal();
    }
}
