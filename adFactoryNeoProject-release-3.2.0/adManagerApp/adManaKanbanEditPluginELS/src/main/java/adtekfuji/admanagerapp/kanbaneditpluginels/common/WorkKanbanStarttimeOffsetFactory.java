/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditpluginels.common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellRegexTextField;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;

/**
 *
 * @author e-mori
 */
public class WorkKanbanStarttimeOffsetFactory extends AbstractRecordFactory<SimpleStringProperty> {

    public WorkKanbanStarttimeOffsetFactory(Table table, LinkedList<SimpleStringProperty> entitys) {
        super(table, entitys);
    }

    @Override
    protected Record createRecord(SimpleStringProperty entity) {
        Record record = new Record(super.getTable(), false);
        List<AbstractCell> cells = new ArrayList();

        cells.add(new CellRegexTextField(record, "\\d|:|/|\\s", entity, CellRegexTextField.RegexType.CHARACTER).addStyleClass("ContentTextBox"));

        record.setCells(cells);

        return record;
    }

    @Override
    public Class getEntityClass() {
        return SimpleStringProperty.class;
    }

}
