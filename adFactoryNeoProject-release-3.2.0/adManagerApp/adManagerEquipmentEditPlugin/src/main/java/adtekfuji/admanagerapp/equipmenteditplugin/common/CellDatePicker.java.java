/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.equipmenteditplugin.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.DatePicker;
import javafx.util.converter.LocalDateStringConverter;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.CellInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * DatePicker表示セル<b>
 * 日付を文字列として、あるいはDate型として保持する
 *
 * @author fu-kato
 */
class CellDatePicker extends AbstractCell {

    private final static Logger logger = LogManager.getLogger();
    private final StringProperty valueProperty;
    private final ObjectProperty<LocalDate> localDateProperty;
    
    final DatePicker picker = new DatePicker();

    public CellDatePicker(CellInterface cell, StringProperty valueProperty) {
        super(cell);
        this.valueProperty = valueProperty;
        this.localDateProperty = null;
    }

    public CellDatePicker(CellInterface cell, ObjectProperty<LocalDate> localDateProperty) {
        super(cell);
        this.valueProperty = null;
        this.localDateProperty = localDateProperty;
    }

    @Override
    public void createNode() {
        try {
            if (Objects.nonNull(valueProperty)) {
                final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

                final LocalDate localDate = Objects.isNull(this.valueProperty.get()) || this.valueProperty.get().equals("")
                        ? LocalDate.now()
                        : LocalDate.parse(this.valueProperty.get(), formatter);

                picker.setValue(localDate);
                valueProperty.bindBidirectional(picker.valueProperty(), new LocalDateStringConverter(formatter, formatter));
                super.setNode(picker);
            } else if (Objects.nonNull(localDateProperty)) {
                picker.setValue(localDateProperty.getValue());
                localDateProperty.bindBidirectional(picker.valueProperty());
                super.setNode(picker);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    public BooleanProperty disableProperty() {
        return picker.disableProperty();
    }
}
