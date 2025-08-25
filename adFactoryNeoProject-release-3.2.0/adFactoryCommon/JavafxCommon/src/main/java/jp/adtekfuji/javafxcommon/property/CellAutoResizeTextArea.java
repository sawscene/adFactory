/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import java.util.Objects;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextArea;

/**
 *
 * @author s-maeda
 */
public class CellAutoResizeTextArea extends AbstractCell {

    private final StringProperty stringData;
    private TextArea textArea;
    private int linesLimit;                 // 入力制限行数
    private final int heightOffset;         // 高さ変更時のオフセット値
    private final int heightPitchOfLine;    // 高さ変更時、1行ごとの増加量

    /**
     * 項目の有効/無効状態(true：無効、false：有効)
     */
    private final boolean isDisabled;

    /**
     * コンストラクタ
     * 
     * @param abstractCellInterface セルのインターフェイス
     * @param stringData 文字列プロパティ
     */
    public CellAutoResizeTextArea(CellInterface abstractCellInterface, StringProperty stringData) {
        this(abstractCellInterface, stringData, false);
    }

    /**
     * コンストラクタ
     * 
     * @param abstractCellInterface セルのインターフェイス
     * @param stringData 文字列プロパティ
     * @param isDisabled 項目の有効/無効状態(true：無効、false：有効)
     */
    public CellAutoResizeTextArea(CellInterface abstractCellInterface, StringProperty stringData, boolean isDisabled) {
        super(abstractCellInterface);
        this.stringData = stringData;
        this.linesLimit = 1;
        this.heightOffset = 12;
        this.heightPitchOfLine = 25;
        this.isDisabled = isDisabled;
    }

    @Override
    public void createNode() {
        textArea = new TextArea(stringData.get());
        textArea.getStyleClass().add("ContentAutoResizeTextArea");
        stringData.bindBidirectional(textArea.textProperty());

        textArea.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (getLines() > linesLimit) {
                textArea.setText(oldValue);
            }
            Platform.runLater(() -> {
                textArea.setPrefHeight(heightOffset + (getLines() * heightPitchOfLine));
            });
        });
        textArea.setPrefHeight(heightOffset + (getLines() * heightPitchOfLine));
        this.setDisable(this.isDisabled);

        super.setNode(textArea);
    }

    /**
     * 最大行数を設定する
     *
     * @param maxLines
     * @return
     */
    public CellAutoResizeTextArea setLinesLimit(int maxLines) {
        this.linesLimit = maxLines;
        return this;
    }

    /**
     * 現在の行数を取得する
     *
     * @return
     */
    private int getLines() {
        int lines = 1;
        if (Objects.nonNull(textArea.getText())) {
            String current = textArea.getText();

            int idx = current.indexOf("\n");

            while (idx != -1) {
                lines++;
                idx = current.indexOf("\n", idx + 1);
            }
        }
        return lines;
    }
}
