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
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import jp.adtekfuji.adFactory.entity.indirectwork.IndirectWorkInfoEntity;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellLabel;
import jp.adtekfuji.javafxcommon.property.CellTextField;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;

/**
 *
 * @author nar-nakamura
 */
public class IndirectWorkRecordFactory extends AbstractRecordFactory<IndirectWorkInfoEntity> {

    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    /**
     * コンストラクタ
     *
     * @param table
     * @param entitys 
     */
    public IndirectWorkRecordFactory(Table table, LinkedList<IndirectWorkInfoEntity> entitys) {
        super(table, entitys);
    }

    /**
     * ヘッダー行を作成する。
     *
     * @return 行情報
     */
    @Override
    protected Record createCulomunTitleRecord() {
        Record titleColumnrecord = new Record(super.getTable(), false);
        List<AbstractCell> cells = new ArrayList();

        // 作業番号
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.IndirectWorkNumber") + LocaleUtils.getString("key.RequiredMark"))).addStyleClass("ContentTitleLabel"));
        // 作業名
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.IndirectWorkName") + LocaleUtils.getString("key.RequiredMark"))).addStyleClass("ContentTitleLabel"));

        titleColumnrecord.setTitleCells(cells);

        return titleColumnrecord;
    }

    /**
     * データ行を作成する。
     *
     * @param entity 間接作業情報
     * @return 行情報
     */
    @Override
    protected Record createRecord(IndirectWorkInfoEntity entity) {
        boolean isEditable = true;
        if (Objects.nonNull(entity) && Objects.nonNull(entity.getIsUsed())) {
            isEditable = !entity.getIsUsed();
        }
        Record record = new Record(super.getTable(), isEditable);
        List<AbstractCell> cells = new ArrayList<>();

        // 作業番号
        cells.add(new CellTextField(record, entity.workNumberProperty()).addStyleClass("ContentTextBox"));
        // 作業名
        cells.add(new CellTextField(record, entity.workNameProperty()).addStyleClass("ContentTextBox"));

        record.setCells(cells);

        return record;
    }

    @Override
    public Class getEntityClass() {
        return IndirectWorkInfoEntity.class;
    }
}
