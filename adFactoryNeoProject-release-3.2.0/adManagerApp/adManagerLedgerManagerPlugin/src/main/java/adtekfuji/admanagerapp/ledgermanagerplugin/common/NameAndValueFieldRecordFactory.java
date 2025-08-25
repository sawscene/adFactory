/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.ledgermanagerplugin.common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import adtekfuji.admanagerapp.ledgermanagerplugin.entity.NameAndTagEntity;
import javafx.beans.property.SimpleStringProperty;
import jp.adtekfuji.javafxcommon.property.*;
import jp.adtekfuji.javafxcommon.property.Record;

/**
 *
 * @author nar-nakamura
 */
public class NameAndValueFieldRecordFactory extends AbstractRecordFactory<NameAndTagEntity> {

    ResourceBundle rb;
    public NameAndValueFieldRecordFactory(ResourceBundle rb, Table table, LinkedList<NameAndTagEntity> entities) {
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
    protected Record createRecord(NameAndTagEntity value) {
        Record record = new Record(super.getTable(), true);

        LinkedList<AbstractCell> cells = new LinkedList<>();
        cells.add(new CellTextField(record, value.nameProperty()).setPrefWidth(150.0));
        cells.add(new CellTextField(record, value.tagProperty()).setPrefWidth(150.0));

        record.setCells(cells);
        return record;
    }

    @Override
    protected Record createCulomunTitleRecord() {
        Record titleColumnrecord = new Record(super.getTable(), false);
        List<AbstractCell> cells = new ArrayList();
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(rb.getString("key.DataCheckErrorType"))));
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(rb.getString("key.Tag"))));
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
        return NameAndTagEntity.class;
    }
}
