/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.systemsettingplugin.common;

import adtekfuji.locale.LocaleUtils;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import jp.adtekfuji.adFactory.entity.master.LabelInfoEntity;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellColorPicker;
import jp.adtekfuji.javafxcommon.property.CellLabel;
import jp.adtekfuji.javafxcommon.property.CellTextField;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;

/**
 * ラベルマスタレコード生成クラス
 * 
 * @author kentarou.suzuki
 */
public class LabelRecordFactory extends AbstractRecordFactory<LabelInfoEntity> {

    /**
     * リソースバンドル
     */
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    /**
     * コンストラクタ
     * 
     * @param table ラベルマスタテーブル
     * @param entities ラベルマスタのリスト
     */
    public LabelRecordFactory(Table table, LinkedList<LabelInfoEntity> entities) {
        super(table, entities);
    }

    /**
     * エンティティの型を取得する。
     * 
     * @return エンティティの型
     */
    @Override
    public Class getEntityClass() {
        return LabelInfoEntity.class;
    }

    /**
     * ヘッダ行を作成する。
     * 
     * @return ヘッダ行のレコード
     */
    @Override
    protected Record createCulomunTitleRecord() {
        Record titleColumnrecord = new Record(super.getTable(), false);
        List<AbstractCell> cells = new ArrayList();
        
        // ラベル名
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.EditLabelTitle") + LocaleUtils.getString("key.RequiredMark"))).addStyleClass("ContentTitleLabel"));
        // 文字色
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.FontColor"))).addStyleClass("ContentTitleLabel"));
        // 背景色
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.BackColor"))).addStyleClass("ContentTitleLabel"));
        titleColumnrecord.setTitleCells(cells);

        return titleColumnrecord;
    }

    /**
     * データ行を作成する。
     * 
     * @param entity ラベル情報
     * @return データ行のレコード
     */
    @Override
    protected Record createRecord(LabelInfoEntity entity) {
        Record record = new Record(super.getTable(), true);
        List<AbstractCell> cells = new ArrayList<>();
        record.setEditableOrder(true);

        // ラベル名
        cells.add(new CellTextField(record, entity.labelNameProperty()).addStyleClass("ContentTextBox"));
        // 文字色
        cells.add(new CellColorPicker(record, entity.fontColorProperty()));
        // 背景色
        cells.add(new CellColorPicker(record, entity.backColorProperty()));
        record.setCells(cells);

        return record;
    }
}
