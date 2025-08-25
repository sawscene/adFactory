/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.common;

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
import jp.adtekfuji.adFactory.entity.work.WorkPropertyInfoEntity;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellComboBox;
import jp.adtekfuji.javafxcommon.property.CellLabel;
import jp.adtekfuji.javafxcommon.property.CellTextField;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;

/**
 * 工程プロパティファクトリークラス
 *
 * @author ta.ito
 */
public class WorkPropertyRecordFactory extends AbstractRecordFactory<WorkPropertyInfoEntity> {

    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    /**
     * プロパティ情報型表示用セルクラス
     *
     */
    class CustomPropertyTypeComboBoxCellFactory extends ListCell<CustomPropertyTypeEnum> {

        @Override
        protected void updateItem(CustomPropertyTypeEnum item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else {
                setText(LocaleUtils.getString(item.getResourceKey()));
            }
        }
    }

    public WorkPropertyRecordFactory(Table table, LinkedList<WorkPropertyInfoEntity> entitys) {
        super(table, entitys);
    }

    @Override
    protected Record createCulomunTitleRecord() {
        Record titleColumnrecord = new Record(super.getTable(), false);
        List<AbstractCell> cells = new ArrayList();
        //タイトルに該当するカラムを追加する

        //プロパティ名
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.PropertyName") + LocaleUtils.getString("key.RequiredMark"))).addStyleClass("ContentTitleLabel"));
        //プロパティタイプ名
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.PropertyType") + LocaleUtils.getString("key.RequiredMark"))).addStyleClass("ContentTitleLabel"));
        //プロパティ値
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.PropertyContent"))).addStyleClass("ContentTitleLabel"));
        titleColumnrecord.setTitleCells(cells);

        return titleColumnrecord;
    }

    @Override
    protected Record createRecord(WorkPropertyInfoEntity entity) {
        Record record = new Record(super.getTable(), true);
        Callback<ListView<CustomPropertyTypeEnum>, ListCell<CustomPropertyTypeEnum>> comboCellFactory = (ListView<CustomPropertyTypeEnum> param) -> new CustomPropertyTypeComboBoxCellFactory();

        LinkedList<AbstractCell> cells = new LinkedList<>();
        //プロパティ名
        cells.add(new CellTextField(record, entity.workPropNameProperty()).addStyleClass("ContentTextBox"));
        //プロパティタイプ名
        cells.add(new CellComboBox<CustomPropertyTypeEnum>(record, Arrays.asList(CustomPropertyTypeEnum.values()), new CustomPropertyTypeComboBoxCellFactory(), comboCellFactory, entity.workPropTypeProperty()).addStyleClass("ContentComboBox"));
        //プロパティ値
        cells.add(new CellTextField(record, entity.workPropValueProperty()).addStyleClass("ContentTextBox"));
        record.setCells(cells);

        return record;
    }

    @Override
    public Class getEntityClass() {
        return WorkPropertyInfoEntity.class;
    }
}
