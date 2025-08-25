/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.systemsettingplugin.common;

import adtekfuji.locale.LocaleUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.master.ReasonInfoEntity;
import jp.adtekfuji.adFactory.enumerate.LightPatternEnum;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellColorPicker;
import jp.adtekfuji.javafxcommon.property.CellComboBox;
import jp.adtekfuji.javafxcommon.property.CellLabel;
import jp.adtekfuji.javafxcommon.property.CellTextField;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;

/**
 * レコードファクトリー
 * 
 * @author s-heya
 */
public class ReasonRecordFactory2 extends AbstractRecordFactory<ReasonInfoEntity> {

    /**
     * プロパティ情報型表示用セルクラス
     *
     */
    class LightPatternTypeComboBoxCellFactory extends ListCell<LightPatternEnum> {
        @Override
        protected void updateItem(LightPatternEnum item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else {
                setText(LocaleUtils.getString(item.getResourceKey()));
            }
        }
    }

    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    /**
     * コンストラクタ
     * 
     * @param table
     * @param entitys 
     */
    public ReasonRecordFactory2(Table table, LinkedList<ReasonInfoEntity> entitys) {
        super(table, entitys);
    }

    /**
     * タイトル行を作成する。
     * 
     * @return 
     */
    @Override
    protected Record createCulomunTitleRecord() {
        Record record = new Record(super.getTable(), false);
        List<AbstractCell> cells = new ArrayList();

        // 理由
        cells.add(new CellLabel(record, new SimpleStringProperty(LocaleUtils.getString("key.Reason") + LocaleUtils.getString("key.RequiredMark"))).addStyleClass("ContentTitleLabel"));
        // 文字色
        cells.add(new CellLabel(record, new SimpleStringProperty(LocaleUtils.getString("key.FontColor"))).addStyleClass("ContentTitleLabel"));
        // 背景色
        cells.add(new CellLabel(record, new SimpleStringProperty(LocaleUtils.getString("key.BackColor"))).addStyleClass("ContentTitleLabel"));
        // 点灯パターン
        cells.add(new CellLabel(record, new SimpleStringProperty(LocaleUtils.getString("key.LightPattern"))).addStyleClass("ContentTitleLabel"));

        record.setTitleCells(cells);
        return record;
    }

    /**
     * データ行を作成する。
     * 
     * @param entity
     * @return 
     */
    @Override
    protected Record createRecord(ReasonInfoEntity entity) {
        Record record = new Record(super.getTable(), true);
        List<AbstractCell> cells = new ArrayList<>();
        Callback<ListView<LightPatternEnum>, ListCell<LightPatternEnum>> comboCellFactory = (o) -> new LightPatternTypeComboBoxCellFactory();

        // 理由
        cells.add(new CellTextField(record, entity.reasonProperty()).addStyleClass("ContentTextBox"));
        // 文字色
        cells.add(new CellColorPicker(record, entity.fontColorProperty()));
        // 背景色
        cells.add(new CellColorPicker(record, entity.backColorProperty()));
        // 点灯パターン
        cells.add(new CellComboBox<>(record, Arrays.asList(LightPatternEnum.values()), new LightPatternTypeComboBoxCellFactory(), comboCellFactory, entity.lightPatternProperty()));

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
        return ReasonInfoEntity.class;
    }
}
