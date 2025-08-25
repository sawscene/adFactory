/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.javafx.record.factory;

import adtekfuji.locale.LocaleUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplatePropertyInfoEntity;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellComboBox;
import jp.adtekfuji.javafxcommon.property.CellLabel;
import jp.adtekfuji.javafxcommon.property.CellRegexTextField;
import jp.adtekfuji.javafxcommon.property.CellTextField;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;

/**
 * プロパティ編集画面生成クラス
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.26.Wen
 */
public class UnitTemplatePropertyRecordFactory extends AbstractRecordFactory<UnitTemplatePropertyInfoEntity> {

    private static final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final List<TextField> propValueTextFieldCollection = new ArrayList<>();

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

    public UnitTemplatePropertyRecordFactory(Table table, LinkedList<UnitTemplatePropertyInfoEntity> entitys) {
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
    protected Record createRecord(UnitTemplatePropertyInfoEntity entity) {
        Record record = new Record(super.getTable(), true);
        LinkedList<AbstractCell> cells = new LinkedList<>();
        Callback<ListView<CustomPropertyTypeEnum>, ListCell<CustomPropertyTypeEnum>> comboCellFactory = (ListView<CustomPropertyTypeEnum> param) -> new CustomPropertyTypeComboBoxCellFactory();

        // プロパティ名
        // cells.add(new CellTextField(record, entity.unitTemplatePropertyNameProperty()).addStyleClass("ContentTextBox"));
        cells.add(new CellRegexTextField(record, "^.{0,255}$", entity.unitTemplatePropertyNameProperty(), CellRegexTextField.RegexType.STRING).addStyleClass("ContentTextBox"));
        // TODO: カスタムフィールド用のEnumから選択できるようにする.デフォルトの値はその他
        // プロパティタイプ
        cells.add(new CellComboBox<>(record, Arrays.asList(CustomPropertyTypeEnum.values()), new CustomPropertyTypeComboBoxCellFactory(), comboCellFactory, entity.unitTemplatePropertyTypeProperty()).addStyleClass("ContentComboBox"));
        // プロパティ値
        cells.add(new CellTextField(record, entity.unitTemplatePropertyValueProperty()).addStyleClass("ContentTextBox"));
        if (cells.getLast().getNode() instanceof TextField) {
            propValueTextFieldCollection.add((TextField) cells.getLast().getNode());
        }
        record.setCells(cells);

        return record;
    }

    @Override
    public void removeRecord(Record record) {
        super.removeRecord(record);
        if (record.getCells().get(2).getNode() instanceof TextField) {
            propValueTextFieldCollection.remove((TextField) record.getCells().get(2).getNode());
        }
    }

    @Override
    public Class getEntityClass() {
        return UnitTemplatePropertyInfoEntity.class;
    }

    /**
     * カンバンプロパティ値のテキストフィールド一覧を取得する
     *
     * @return
     */
    public List<TextField> getPropValueTextFieldCollection() {
        return propValueTextFieldCollection;
    }
}
