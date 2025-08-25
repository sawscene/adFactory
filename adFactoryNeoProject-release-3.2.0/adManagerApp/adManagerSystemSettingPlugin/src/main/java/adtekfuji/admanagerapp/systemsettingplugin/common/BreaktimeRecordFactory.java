/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.systemsettingplugin.common;

import adtekfuji.locale.LocaleUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.converter.TimeStringConverter;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.CellLabel;
import jp.adtekfuji.javafxcommon.property.CellTextField;
import jp.adtekfuji.javafxcommon.property.CellTimeStampField;

/**
 *
 * @author e.mori
 */
public class BreaktimeRecordFactory extends AbstractRecordFactory<BreakTimeInfoEntity> {

    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    /**
     * 
     * @param table
     * @param entitys 
     */
    public BreaktimeRecordFactory(Table table, LinkedList<BreakTimeInfoEntity> entitys) {
        super(table, entitys);
    }

    /**
     * 
     * @return 
     */
    @Override
    protected Record createCulomunTitleRecord() {
        Record titleColumnrecord = new Record(super.getTable(), false);
        List<AbstractCell> cells = new ArrayList();
        //タイトルに該当するカラムを追加する

        //
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.EditBreakTimeTitle") + LocaleUtils.getString("key.RequiredMark"))).addStyleClass("ContentTitleLabel"));
        //
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.StartTime") + LocaleUtils.getString("key.TimeTitle"))).addStyleClass("ContentTitleLabel"));
        //
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.EndTime") + LocaleUtils.getString("key.TimeTitle"))).addStyleClass("ContentTitleLabel"));

        titleColumnrecord.setTitleCells(cells);

        return titleColumnrecord;
    }

    /**
     * 
     * @param entity
     * @return 
     */
    @Override
    protected Record createRecord(BreakTimeInfoEntity entity) {
        Record record = new Record(super.getTable(), true);
        List<AbstractCell> cells = new ArrayList<>();

        SimpleDateFormat TO_CONVERT_TIME_PATTERN = new SimpleDateFormat(LocaleUtils.getString("key.TimeFormat"));
        TimeStringConverter timeStringConverter = new TimeStringConverter(TO_CONVERT_TIME_PATTERN);

        //休憩理由
        cells.add(new CellTextField(record, entity.breaktimeNameProperty()).addStyleClass("ContentTextBox"));
        //開始時間
        if (Objects.isNull(entity.getStarttime())) {
            entity.setStarttime(getCurrentTime());
        }
        StringProperty startTimeStrProp = new SimpleStringProperty(entity.getStarttime().toString());
        startTimeStrProp.bindBidirectional(entity.starttimeProperty(), timeStringConverter);
        cells.add(new CellTimeStampField(record, startTimeStrProp).addStyleClass("ContentTextBox"));

        //終了時間
        if (Objects.isNull(entity.getEndtime())) {
            entity.setEndtime(getCurrentTime());
        }
        StringProperty endTimeStrProp = new SimpleStringProperty(entity.getStarttime().toString());
        endTimeStrProp.bindBidirectional(entity.endtimeProperty(), timeStringConverter);
        cells.add(new CellTimeStampField(record, endTimeStrProp).addStyleClass("ContentTextBox"));

        record.setCells(cells);

        return record;
    }

    /**
     * 
     * @return 
     */
    @Override
    public Class getEntityClass() {
        return BreakTimeInfoEntity.class;
    }

    /**
     * 現在の時刻を年月日を1970/01/01として取得する
     *
     * @return
     */
    private Date getCurrentTime() {
        try {
            String timeStr = (new Date()).toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_TIME);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

            return sdf.parse(timeStr);
        } catch (ParseException ex) {

            return new Date();
        }
    }
}
