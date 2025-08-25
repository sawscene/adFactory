/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import java.util.Objects;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import jp.adtekfuji.javafxcommon.validator.NumericValidator;

/**
 * レコードファクトリー数値入力制限フォーム(Long型)
 *
 * @author e.mori
 * @version 1.6.1
 * @since 2017.1.11.Wen
 */
public class CellNumericLongField extends AbstractCell {

    private final LongProperty longData;
    private final StringProperty stringData = new SimpleStringProperty();
    private Long min;
    private Long max;

    public CellNumericLongField(CellInterface abstractCellInterface, LongProperty integerData) {
        super(abstractCellInterface);
        this.longData = integerData;
    }

    /**
     * 許容値設定
     *
     * @param min 最小値
     * @param max 最大値
     * @return
     */
    public CellNumericLongField addRange(Long min, Long max) {
        this.min = min;
        this.max = max;
        return this;
    }

    @Override
    public void createNode() {
        TextField textField = new TextField(String.valueOf(longData.get()));
        if (Objects.nonNull(min) && Objects.nonNull(max)) {
            NumericValidator.bindValidator(textField, longData).addRange(min, max);
        } else {
            NumericValidator.bindValidator(textField, longData);
        }
//        textField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
//            longData.set(Integer.valueOf(newValue));
//        });
//        textField.addEventFilter(KeyEvent.KEY_TYPED, (KeyEvent event) -> {
//            if (!event.getCharacter().matches("\\d")) {
//                event.consume();
//            }
//        });
        super.setNode(textField);
    }
}
