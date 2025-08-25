/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.property;

import java.util.LinkedList;
import javafx.beans.property.SimpleStringProperty;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellTextField;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;

/**
 *
 * @author nar-nakamura
 */
public class TextFieldRecordFactory extends AbstractRecordFactory<SimpleStringProperty> {

    public TextFieldRecordFactory(Table table, LinkedList<SimpleStringProperty> entities) {
        super(table, entities);
    }

    /**
     * レコードを追加する。
     *
     * @param value 
     * @return 
     */
    @Override
    protected Record createRecord(SimpleStringProperty value) {
        Record record = new Record(super.getTable(), true);
//        record.setEditableOrder(true);

        LinkedList<AbstractCell> cells = new LinkedList<>();

        cells.add(new CellTextField(record, value).setPrefWidth(40.0));

        record.setCells(cells);

        return record;
    }

    /**
     * エンティティの型を取得する。
     *
     * @return エンティティの型
     */
    @Override
    public Class getEntityClass() {
        return SimpleStringProperty.class;
    }
}
