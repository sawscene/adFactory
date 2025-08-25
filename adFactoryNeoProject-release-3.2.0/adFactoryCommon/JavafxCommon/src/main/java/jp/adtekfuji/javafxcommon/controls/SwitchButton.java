/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.controls;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

/**
 * スイッチボタン
 *
 * @author s-heya
 */
public class SwitchButton extends HBox
{
    private SimpleBooleanProperty switchedOn = new SimpleBooleanProperty(true);
    private Button onButton;
    private Button offButton;

    /**
     * コンストラクタ
     */
    public SwitchButton()
    {
        this.setMaxWidth(200.0);

        this.onButton = new Button("ON");
        this.onButton.setPrefWidth(100.0);
        this.onButton.setAlignment(Pos.CENTER);
        this.onButton.prefHeightProperty().bind(this.heightProperty());
        this.onButton.setOnAction(value -> {
            switchedOn.set(true);
        });

        this.offButton = new Button("OFF");
        this.offButton.setPrefWidth(100.0);
        this.offButton.setAlignment(Pos.CENTER);
        this.offButton.prefHeightProperty().bind(this.heightProperty());
        this.offButton.setOnAction(value -> {
            switchedOn.set(false);
        });

        this.getChildren().addAll(this.onButton, this.offButton);
        this.setStyle("-fx-background-color: grey; -fx-border-insets: 0 0 0 0;");

        this.switchedOn.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue) {
                onButton.setStyle("-fx-background-color: green; -fx-border-insets: 0 0 0 0; -fx-text-fill: white; -fx-font-size: 18px;");
                offButton.setStyle("-fx-background-color: darkgray; -fx-border-insets: 0 0 0 0; -fx-text-fill: lightgray; -fx-font-size: 18px;");
            } else {
                onButton.setStyle("-fx-background-color: darkgray; -fx-border-insets: 0 0 0 0; -fx-text-fill: lightgray; -fx-font-size: 18px;");
                offButton.setStyle("-fx-background-color: gray; -fx-border-insets: 0 0 0 0; -fx-text-fill: white; -fx-font-size: 18px;");
            }
        });

        this.switchedOn.set(false);
    }

    /**
     * スイッチプロパティを取得する。
     *
     * @return
     */
    public SimpleBooleanProperty switchOnProperty() {
        return this.switchedOn;
    }

    /**
     * テキストを設定する
     *
     * @param onText
     * @param offText
     */
    public void setText(String onText, String offText) {
        this.onButton.setText(onText);
        this.offButton.setText(offText);
    }
}