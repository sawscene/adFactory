/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.common;

import adtekfuji.locale.LocaleUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.kanban.KanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellComboBox;
import jp.adtekfuji.javafxcommon.property.CellLabel;
import jp.adtekfuji.javafxcommon.property.CellRegexTextField;
import jp.adtekfuji.javafxcommon.property.CellText;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;

/**
 * カンバン追加情報レコード
 *
 * @author e.mori
 */
public class KanbanPropertyRecordFactory extends AbstractRecordFactory<KanbanPropertyInfoEntity> {

    private static final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

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

    /**
     * コンストラクタ
     *
     * @param table テーブル
     * @param entities カンバン追加情報一覧
     */
    public KanbanPropertyRecordFactory(Table table, LinkedList<KanbanPropertyInfoEntity> entities) {
        super(table, entities);
    }

    /**
     * ヘッダー行を作成する。
     *
     * @return ヘッダー行
     */
    @Override
    protected Record createCulomunTitleRecord() {
        Record titleColumnrecord = new Record(super.getTable(), false);
        List<AbstractCell> cells = new ArrayList();
        //タイトルに該当するカラムを追加する

        // プロパティ名
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.PropertyName") + LocaleUtils.getString("key.RequiredMark"))).addStyleClass("ContentTitleLabel"));
        // プロパティタイプ
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.PropertyType") + LocaleUtils.getString("key.RequiredMark"))).addStyleClass("ContentTitleLabel"));
        // プロパティ値
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.PropertyContent"))).addStyleClass("ContentTitleLabel"));

        titleColumnrecord.setTitleCells(cells);

        return titleColumnrecord;
    }

    /**
     * データ行を作成する。
     *
     * @param entity カンバン追加情報
     * @return データ行
     */
    @Override
    protected Record createRecord(KanbanPropertyInfoEntity entity) {
        Record record = new Record(super.getTable(), true);
        LinkedList<AbstractCell> cells = new LinkedList<>();
        Callback<ListView<CustomPropertyTypeEnum>, ListCell<CustomPropertyTypeEnum>> comboCellFactory = (ListView<CustomPropertyTypeEnum> param) -> new CustomPropertyTypeComboBoxCellFactory();

        // プロパティ名
        cells.add(new CellRegexTextField(record, "^.{0,256}$", entity.kanbanPropNameProperty(), CellRegexTextField.RegexType.STRING).addStyleClass("ContentTextBox"));

        // プロパティタイプ
        CellComboBox propTypeCell = new CellComboBox<>(record, Arrays.asList(CustomPropertyTypeEnum.values()), new CustomPropertyTypeComboBoxCellFactory(), comboCellFactory, entity.kanbanPropTypeProperty());
        cells.add(propTypeCell.addStyleClass("ContentComboBox"));

        // プロパティ値
        CellText propValueCell = new CellText(record, entity.kanbanPropValueProperty());

        if (Objects.equals(entity.getKanbanPropertyType(), CustomPropertyTypeEnum.TYPE_STRING)) {
            propValueCell.setCellTextType(CellText.CellTextType.TextArea);
        } else {
            propValueCell.setCellTextType(CellText.CellTextType.TextField);
        }

        cells.add(propValueCell.addStyleClass("ContentCellText"));

        record.setCells(cells);

        // プロパティタイプ 変更イベント
        propTypeCell.actionListner(new ChangeListener<CustomPropertyTypeEnum>() {
            @Override
            public void changed(ObservableValue<? extends CustomPropertyTypeEnum> ov, CustomPropertyTypeEnum oldValue, CustomPropertyTypeEnum newValue) {
                // プロパティ値入力セルの表示を切り替える。
                if (Objects.equals(newValue, CustomPropertyTypeEnum.TYPE_STRING)) {
                    propValueCell.setCellTextType(CellText.CellTextType.TextArea);
                } else {
                    propValueCell.setCellTextType(CellText.CellTextType.TextField);
                }
            }
        });

        return record;
    }

    /**
     *
     * @param record
     */
    @Override
    public void removeRecord(Record record) {
        super.removeRecord(record);
    }

    /**
     *
     * @return
     */
    @Override
    public Class getEntityClass() {
        return KanbanPropertyInfoEntity.class;
    }
}
