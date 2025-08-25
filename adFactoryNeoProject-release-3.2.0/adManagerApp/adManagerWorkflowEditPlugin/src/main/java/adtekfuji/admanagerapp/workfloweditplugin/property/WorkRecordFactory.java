/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.property;

import adtekfuji.admanagerapp.workfloweditplugin.common.Constants;
import java.util.LinkedList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.javafxcommon.PropertyBindEntity;
import jp.adtekfuji.javafxcommon.PropertyBindEntityInterface;
import static jp.adtekfuji.javafxcommon.PropertyBindEntityInterface.TYPE.BOOLEAN;
import static jp.adtekfuji.javafxcommon.PropertyBindEntityInterface.TYPE.COLORPICKER;
import static jp.adtekfuji.javafxcommon.PropertyBindEntityInterface.TYPE.COMBO;
import static jp.adtekfuji.javafxcommon.PropertyBindEntityInterface.TYPE.INTEGER;
import static jp.adtekfuji.javafxcommon.PropertyBindEntityInterface.TYPE.PASSWORD;
import static jp.adtekfuji.javafxcommon.PropertyBindEntityInterface.TYPE.REGEX_STRING;
import static jp.adtekfuji.javafxcommon.PropertyBindEntityInterface.TYPE.TEXTAREA;
import static jp.adtekfuji.javafxcommon.PropertyBindEntityInterface.TYPE.TIMESTAMP;

import jp.adtekfuji.javafxcommon.property.*;
import jp.adtekfuji.javafxcommon.property.CellTimeStampField;
import jp.adtekfuji.javafxcommon.property.Record;

/**
 * 工程設定フィールド生成クラス
 *
 * @author s-heya
 */
public class WorkRecordFactory extends AbstractRecordFactory<PropertyBindEntityInterface> {

    public WorkRecordFactory(Table table, LinkedList<PropertyBindEntityInterface> entitys) {
        super(table, entitys);
    }

    @Override
    protected Record createRecord(PropertyBindEntityInterface entity) {
        Record record = new Record(super.getTable(), false);
        LinkedList<AbstractCell> cells = new LinkedList<>();

        cells.add(new CellLabel(record, entity.getLabel()).addStyleClass("ContentTitleLabel"));
        switch (entity.getType()) {
            case STRING:
                cells.add(new CellTextField(record, (StringProperty) entity.getProperty()).addStyleClass("ContentTextBox"));
                break;
            case REGEX_STRING:
                cells.add(new CellRegexTextField(record, entity.getRegex(), (StringProperty) entity.getProperty()).addStyleClass("ContentTextBox"));
                break;
            case TIMESTAMP:
                cells.add(new CellTimeStampField(record, (StringProperty) entity.getProperty(), false, Constants.TAKT_TIME_MAX_MILLIS).addStyleClass("ContentTextBox"));
                break;
            case TIMEHMSTAMP:
                cells.add(new CellTimeHMStampField(record, (StringProperty) entity.getProperty()).addStyleClass("ContentTextBox"));
                break;
            case INTEGER:
                cells.add(new CellNumericField(record, (IntegerProperty) entity.getProperty()).addStyleClass("ContentTextBox"));
                break;
            case BOOLEAN:
                cells.add(new CellCheckBox(record, entity.getText(), (BooleanProperty) entity.getProperty()).addStyleClass("ContentCheckBox"));
                break;
            case COMBO:
                cells.add(new CellComboBox(record, entity.getSelector(), entity.getButtonCellFactory(), entity.getComboCellFactory(), (ObjectProperty) entity.getProperty()).actionListner(entity.getActionListner()).addStyleClass("ContentComboBox"));
                break;
            case PASSWORD:
                cells.add(new CellPassField(record, (StringProperty) entity.getProperty(), entity.getDisable()).addStyleClass("ContentTextBox"));
                break;
            case TEXTAREA:
                cells.add(new CellTextArea(record, (StringProperty) entity.getProperty()).addStyleClass("ContentTextBox"));
                break;
            case COLORPICKER:
                cells.add(new CellColorPicker(record, (ObjectProperty) entity.getProperty()).addStyleClass("ContentTextBox"));
                break;
            case BUTTON:
                cells.add(new CellButton(record, (StringProperty) entity.getProperty(), entity.getEventAction(), entity.getUserData()).addStyleClass("ContentTextBox"));
                break;
        }

        record.setCells(cells);

        return record;
    }

    @Override
    public Class getEntityClass() {
        return PropertyBindEntity.class;
    }
}
