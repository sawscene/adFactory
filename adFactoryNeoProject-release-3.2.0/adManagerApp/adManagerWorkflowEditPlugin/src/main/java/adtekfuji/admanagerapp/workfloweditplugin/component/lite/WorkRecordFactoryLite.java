/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.component.lite;

import adtekfuji.admanagerapp.workfloweditplugin.common.Constants;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellLabel;
import jp.adtekfuji.javafxcommon.property.CellTextField;
import jp.adtekfuji.javafxcommon.property.CellTimeStampIntegerField;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;

/**
 * 工程テーブル生成クラス
 *
 * @author kenji.yokoi
 * @version 
 * @since 2022.01.26.Wen
 */
public class WorkRecordFactoryLite extends AbstractRecordFactory<WorkInfoEntity> {

    private final Properties properties = AdProperty.getProperties();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    public WorkRecordFactoryLite(Table table, LinkedList<WorkInfoEntity> entitys) {
        super(table, entitys);
    }

    @Override
    protected Record createCulomunTitleRecord() {
        Record titleRecord = new Record(super.getTable(), false);
        List<AbstractCell> cells = new ArrayList();
        //タイトルに該当するカラムを追加する
        cells.add(new CellLabel(titleRecord, new SimpleStringProperty(rb.getString("key.ProcessName") + rb.getString("key.RequiredMark")))
                .setPrefWidth(160.0).addStyleClass("ResizeColumn").setResize(true));

        // 作業番号
        cells.add(new CellLabel(titleRecord, new SimpleStringProperty(rb.getString("key.IndirectWorkNumber")))
                .setPrefWidth(130.0).addStyleClass("ResizeColumn").setResize(true));

        // Lite工程設定で標準作業時間を使用する場合は、標準作業時間ヘッダを表示する
        if (Boolean.valueOf(properties.getProperty(Constants.ENABLE_LITE_TAKT_TIME, Constants.ENABLE_LITE_TAKT_TIME_DEFAULT))) {
            cells.add(new CellLabel(titleRecord, new SimpleStringProperty(rb.getString("key.StandardTime")))
                    .setPrefWidth(120.0).addStyleClass("ContentTitleLabel"));
        }
        titleRecord.setTitleCells(cells);
        return titleRecord;
    }

    @Override
    protected Record createRecord(WorkInfoEntity entity) {
        Record record = new Record(super.getTable(), true);
        record.setEditableOrder(true);
        record.setRecordItem(entity);

        List<AbstractCell> cells = new ArrayList();
        // 工程名
        cells.add(new CellTextField(record, entity.workNameProperty()).setPrefWidth(160.0).addStyleClass("ContentTextBox"));
        // 作業番号
        cells.add(new CellTextField(record, entity.workNumberProperty()).setPrefWidth(130.0).addStyleClass("ContentTextBox"));
        // Lite工程設定で標準作業時間を使用する場合は、標準作業時間フィールドを表示する
        if (Boolean.valueOf(properties.getProperty(Constants.ENABLE_LITE_TAKT_TIME, Constants.ENABLE_LITE_TAKT_TIME_DEFAULT))) {
            cells.add(new CellTimeStampIntegerField(record, entity.taktTimeProperty(), 120, Constants.TAKT_TIME_MAX_MILLIS).addStyleClass("ContentTextBox"));
        }
        record.setCells(cells);
        return record;
    }

    @Override
    public Class getEntityClass() {
        return WorkInfoEntity.class;
    }
    
    /**
     * 編集禁止に設定する。
     * 
     * @param disabled 
     */
    public void setDisable(Boolean disabled) {
        this.getRecords().forEach(record -> {
            // 標準作業時間以外は編集禁止
            record.getCells().stream()
                    .filter(o -> !(o instanceof CellTimeStampIntegerField))
                    .forEach(o -> o.setDisable(disabled));
        });
    }
}
