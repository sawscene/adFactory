/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import adtekfuji.utility.StringUtils;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;
import jp.adtekfuji.javafxcommon.controls.TimeHMTextField;

/**
 * 時間帯入力セル
 *
 * @author s-heya
 */
public class CellTimePeriodsField extends AbstractCell {

    private final StringProperty fromProperty;
    private final StringProperty toProperty;

    /**
     * コンストラクタ
     *
     * @param cellInterface
     * @param fromProperty
     * @param toProperty
     */
    public CellTimePeriodsField(CellInterface cellInterface, StringProperty fromProperty, StringProperty toProperty) {
        super(cellInterface);
        this.fromProperty = fromProperty;
        this.toProperty = toProperty;
    }

    /**
     * ノードを生成する。
     */
    @Override
    public void createNode() {
        // 開始時間入力フィールド
        String from = this.fromProperty.get();
        if (StringUtils.isEmpty(from)) {
            from = "00:00";
        }
        TimeHMTextField fromField = new TimeHMTextField(from);
        fromField.setPrefWidth(90.0);
        fromField.setAlignment(Pos.CENTER);
        this.fromProperty.bind(fromField.textProperty());

        // 終了時間入力フィールド
        String to = this.toProperty.get();
        if (StringUtils.isEmpty(to)) {
            to = "00:00";
        }
        TimeHMTextField toField = new TimeHMTextField(to);
        toField.setPrefWidth(90.0);
        toField.setAlignment(Pos.CENTER);
        this.toProperty.bind(toField.textProperty());

        Label label = new Label("-");
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(label, Priority.ALWAYS);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setMaxWidth(220.0);
        hBox.getChildren().addAll(fromField, label, toField);
        HBox.setHgrow(hBox, Priority.NEVER);

        super.setNode(hBox);
    }
}