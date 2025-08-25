/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.equipmenteditplugin.common;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import jp.adtekfuji.javafxcommon.controls.RestrictedTextField;
import jp.adtekfuji.javafxcommon.enumeration.Verifier;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.CellInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ラベル付きのテキストフィールド
 *
 * @author fu-kato
 */
class CellLabeledTextField extends AbstractCell {

    private final static Logger logger = LogManager.getLogger();
    private final StringProperty numberProperty;
    private final String labelStr;

    private final HBox hbox = new HBox();

    /**
     * コンストラクタ
     *
     * @param cell
     * @param valueProperty
     * @param labelStr テキストフィールドの後ろに表示する文字列
     */
    public CellLabeledTextField(CellInterface cell, StringProperty valueProperty, String labelStr) {
        super(cell);
        this.numberProperty = valueProperty;
        this.labelStr = labelStr;
    }

    @Override
    public void createNode() {
        final RestrictedTextField text = new RestrictedTextField(String.valueOf(numberProperty.getValue()));
        text.setVerifier(Verifier.NATURAL_ONLY);
        text.setPrefWidth(160);
        text.addEventFilter(KeyEvent.KEY_TYPED, (KeyEvent event) -> {
            if (!event.getCharacter().matches("\\d")) {
                event.consume();
            }
        });
        text.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            numberProperty.setValue(newValue);
        });
        Label label = new Label(this.labelStr);

        hbox.getChildren().clear();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.getChildren().add(text);
        hbox.getChildren().add(label);

        super.setNode(hbox);
    }

    public BooleanProperty disableProperty() {
        return hbox.disableProperty();
    }
}
