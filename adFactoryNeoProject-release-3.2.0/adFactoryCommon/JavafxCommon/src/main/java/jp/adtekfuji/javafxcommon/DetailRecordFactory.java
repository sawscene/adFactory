/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.javafxcommon.property.*;
import jp.adtekfuji.javafxcommon.property.Record;

/**
 *
 * @author e.mori
 */
public class DetailRecordFactory extends AbstractRecordFactory<PropertyBindEntityInterface> {
    /**
     * 時間テキストフィールドの上限値(ミリ秒)
     */
    private Integer maxMillis = null;

    public DetailRecordFactory(Table table, LinkedList<PropertyBindEntityInterface> entitys) {
        super(table, entitys);
    }
    
    /**
     * コンストラクタ
     * @param table テーブル
     * @param entitys エンティティ
     * @param maxMillis 時間テキストフィールドの上限値(ミリ秒)
     */
    public DetailRecordFactory(Table table, LinkedList<PropertyBindEntityInterface> entitys, int maxMillis) {
        super(table, entitys);
        this.maxMillis = maxMillis;
    }

    @Override
    protected Record createRecord(PropertyBindEntityInterface entity) {
        Record record = new Record(super.getTable(), false);
        LinkedList<AbstractCell> cells = new LinkedList<>();
        AbstractCell cell = null;

        cells.add(new CellLabel(record, entity.getLabel()).addStyleClass("ContentTitleLabel"));
        switch (entity.getType()) {
            case STRING:
                cell = new CellTextField(record, (StringProperty) entity.getProperty(), entity.getMaxLength()).addStyleClass("ContentTextBox");
                break;
            case REGEX_STRING:
                cell = new CellRegexTextField(record, entity.getRegex(), (StringProperty) entity.getProperty()).addStyleClass("ContentTextBox");
				if( entity.getDisable() ){
					//グレーアウトによる編集不可
					cell.setDisable(true);
				}
                break;
            case TIMESTAMP:
                if (Objects.nonNull(this.maxMillis)) {
                    cell = new CellTimeStampField(record, (StringProperty) entity.getProperty(), false, this.maxMillis).actionListner(entity.getActionListner()).addStyleClass("ContentTextBox");
                } else {
                    cell = new CellTimeStampField(record, (StringProperty) entity.getProperty()).actionListner(entity.getActionListner()).addStyleClass("ContentTextBox");
                }
                break;
            case TIMEHMSTAMP:
                cell = new CellTimeHMStampField(record, (StringProperty) entity.getProperty()).actionListner(entity.getActionListner()).addStyleClass("ContentTextBox");
                break;
            case INTEGER:
                cell = new CellNumericField(record, (IntegerProperty) entity.getProperty()).actionListner(entity.getActionListner()).addStyleClass("ContentTextBox");
                break;
            case BOOLEAN:
                cell = new CellCheckBox(record, entity.getText(), (BooleanProperty) entity.getProperty()).actionListener(entity.getActionListner()).addStyleClass("ContentCheckBox");
                break;
            case COMBO:
                cell = new CellComboBox(record, entity.getSelector(), entity.getButtonCellFactory(), entity.getComboCellFactory(), (ObjectProperty) entity.getProperty()).actionListner(entity.getActionListner()).addStyleClass("ContentComboBox");
                break;
            case PASSWORD:
                cell = new CellPassField(record, (StringProperty) entity.getProperty(), entity.getDisable()).addStyleClass("ContentTextBox");
                break;
            case TEXTAREA:
                cell = new CellTextArea(record, (StringProperty) entity.getProperty()).addStyleClass("ContentTextBox");
                break;
            case COLORPICKER:
                cell = new CellColorPicker(record, (ObjectProperty) entity.getProperty()).addStyleClass("ContentTextBox");
                break;
            case BUTTON:
                cell = new CellButton(record, (StringProperty) entity.getProperty(), entity.getEventAction(), entity.getUserData()).addStyleClass("ContentTextBox");
                break;
            case TIMEPERIODS:
                List<Object> properties = entity.getProperties();
                cell = new CellTimePeriodsField(record, (StringProperty) properties.get(0), (StringProperty) properties.get(1)).addStyleClass("ContentTextBox");
                break;
        }

        if (Objects.nonNull(cell)) {
            if (entity.getPrefWidth() > 0) {
                cell.setPrefWidth(entity.getPrefWidth());
            }
            cells.add(cell);
        }

        cells.forEach(elem->elem.setNodeConsumer(entity.getNodeConsumer()));

        record.setCells(cells);

        return record;
    }

    @Override
    public Class getEntityClass() {
        return PropertyBindEntity.class;
    }
}
