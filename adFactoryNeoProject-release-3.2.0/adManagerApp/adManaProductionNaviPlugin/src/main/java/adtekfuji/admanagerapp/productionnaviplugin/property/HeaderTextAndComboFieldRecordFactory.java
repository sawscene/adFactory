/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.property;

import java.util.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.importformat.PropHeaderFormatInfo;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.javafxcommon.property.*;
import jp.adtekfuji.javafxcommon.property.Record;

/**
 * CSV形式（ヘッダ名指定）_レコード生成クラス
 * 
 * @author (AQTOR)Koga
 */
public class HeaderTextAndComboFieldRecordFactory extends AbstractRecordFactory<PropHeaderFormatInfo> {
    ResourceBundle rb;

    static class CategoryCellFactory extends ListCell<CustomPropertyTypeEnum> {

        ResourceBundle rb;
        CategoryCellFactory(ResourceBundle rb) {
            this.rb = rb;
        }

        @Override
        protected void updateItem(CustomPropertyTypeEnum item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                this.setText(rb.getString(CustomPropertyTypeEnum.TYPE_STRING.getResourceKey()));
            } else {
                this.setText(rb.getString(item.getResourceKey()));
            }
        }
    }

    public HeaderTextAndComboFieldRecordFactory(ResourceBundle rb, Table table, LinkedList<PropHeaderFormatInfo> entities) {
        super(table, entities);
        this.rb = rb;
    }

    /**
     * レコードを追加する。
     *
     * @param value
     * @return
     */
    @Override
    protected Record createRecord(PropHeaderFormatInfo value) {
        Record record = new Record(super.getTable(), true);

        LinkedList<AbstractCell> cells = new LinkedList<>();

        cells.add(new CellTextField(record, value.propValueProperty()).setPrefWidth(165.0));

        cells.add(new CellTextField(record, value.propNameProperty()).setPrefWidth(165.0));

        Callback<ListView<CustomPropertyTypeEnum>, ListCell<CustomPropertyTypeEnum>> comboCellFactory = (ListView<CustomPropertyTypeEnum> param) -> new CategoryCellFactory(rb);
        List<CustomPropertyTypeEnum> categories = Arrays.asList(CustomPropertyTypeEnum.values());
        cells.add(new CellComboBox(record, categories, new CategoryCellFactory(rb), comboCellFactory, value.propertyTypeProperty(), true).setPrefWidth(60.0));

        record.setCells(cells);

        return record;
    }

    @Override
    protected Record createCulomunTitleRecord() {
        Record titleColumnrecord = new Record(super.getTable(), false);
        List<AbstractCell> cells = new ArrayList();
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(rb.getString("key.AddInfoHeaderName"))));
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(rb.getString("key.AddInfoName"))));
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(rb.getString("key.AddInfoType"))));
        titleColumnrecord.setTitleCells(cells);

        return titleColumnrecord;
    }

    /**
     * エンティティの型を取得する。
     *
     * @return エンティティの型
     */
    @Override
    public Class getEntityClass() {
        return PropHeaderFormatInfo.class;
    }
}
