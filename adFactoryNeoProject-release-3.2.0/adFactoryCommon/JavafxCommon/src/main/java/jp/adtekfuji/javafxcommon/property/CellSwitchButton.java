/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import java.util.Objects;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.util.converter.BooleanStringConverter;
import jp.adtekfuji.javafxcommon.controls.SwitchButton;

/**
 * スイッチボタンセル
 *
 * @author s-heya
 */
public class CellSwitchButton extends AbstractCell {

    private final StringProperty textProperty;
    private final BooleanProperty booleanProperty;
    private final SwitchButton button = new SwitchButton();

    /**
     * コンストラクタ
     *
     * @param record
     * @param textProperty
     */
    public CellSwitchButton(Record record, StringProperty textProperty) {
        super(record);
        this.textProperty = textProperty;
        this.booleanProperty = null;
    }

    /**
     * コンストラクタ
     * 
     * @param record
     * @param booleanProperty 
     */
    public CellSwitchButton(Record record, BooleanProperty booleanProperty) {
        super(record);
        this.booleanProperty = booleanProperty;
        this.textProperty = null;
    }

    /**
     * ノードを生成する。
     */
    @Override
    public void createNode() {
        if (Objects.nonNull(this.textProperty)) {
            button.switchOnProperty().set(Boolean.parseBoolean(this.textProperty.get()));
            Bindings.bindBidirectional(this.textProperty, button.switchOnProperty(), new BooleanStringConverter());
        } else {
            button.switchOnProperty().set(this.booleanProperty.get());
            Bindings.bindBidirectional(this.booleanProperty, button.switchOnProperty());
        }
        super.setNode(button);
    }

    public BooleanProperty switchOnProperty() {
        return button.switchOnProperty();
    }

    public BooleanProperty booleanProperty() {
        return booleanProperty;
    }
}
