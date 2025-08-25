/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.common;

import adtekfuji.locale.LocaleUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellComboBox;
import jp.adtekfuji.javafxcommon.property.CellLabel;
import jp.adtekfuji.javafxcommon.property.CellRegexTextField;
import jp.adtekfuji.javafxcommon.property.CellTextField;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;

/**
 *
 * @author e.mori
 */
public class WorkPlanWorkKanbanPropertyRecordFactory extends AbstractRecordFactory<WorkKanbanPropertyInfoEntity> {

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

    public WorkPlanWorkKanbanPropertyRecordFactory(Table table, LinkedList<WorkKanbanPropertyInfoEntity> entitys) {
        super(table, entitys);
    }

    @Override
    protected Record createCulomunTitleRecord() {
        Record titleColumnrecord = new Record(super.getTable(), false);
        List<AbstractCell> cells = new ArrayList();
        //タイトルに該当するカラムを追加する

        //
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.PropertyName") + LocaleUtils.getString("key.RequiredMark"))).addStyleClass("ContentTitleLabel"));
        //
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.PropertyType") + LocaleUtils.getString("key.RequiredMark"))).addStyleClass("ContentTitleLabel"));
        //
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.PropertyContent"))).addStyleClass("ContentTitleLabel"));
        //
        titleColumnrecord.setTitleCells(cells);

        return titleColumnrecord;
    }

    @Override
    protected Record createRecord(WorkKanbanPropertyInfoEntity entity) {
        Record record = new Record(super.getTable(), true);
        Callback<ListView<CustomPropertyTypeEnum>, ListCell<CustomPropertyTypeEnum>> comboCellFactory = (ListView<CustomPropertyTypeEnum> param) -> new CustomPropertyTypeComboBoxCellFactory();

        LinkedList<AbstractCell> cells = new LinkedList<>();
        //プロパティ名
        cells.add(new CellRegexTextField(record, "^.{0,256}$", entity.workKanbanPropNameProperty(), CellRegexTextField.RegexType.STRING).addStyleClass("ContentTextBox"));
        //プロパティタイプ名
        cells.add(new CellComboBox<>(record, Arrays.asList(CustomPropertyTypeEnum.values()), new CustomPropertyTypeComboBoxCellFactory(), comboCellFactory, entity.workKanbanPropTypeProperty()).addStyleClass("ContentComboBox"));
        //プロパティ値
        cells.add(new CellTextField(record, entity.workKanbanPropValueProperty()).addStyleClass("ContentTextBox"));
        record.setCells(cells);

        return record;
    }

    @Override
    public Class getEntityClass() {
        return WorkKanbanPropertyInfoEntity.class;
    }
}
