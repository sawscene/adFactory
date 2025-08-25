/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;

/**
 * 表示順ダウンボタン
 *
 * @author s-heya
 */
public class CellDecreaseButton extends AbstractCell {
    /**
     * コンストラクタ
     *
     * @param abstractCellInterface
     */
    public CellDecreaseButton(CellInterface abstractCellInterface) {
        super(abstractCellInterface);
    }

    /**
     * ノードを生成する。
     */
    @Override
    public void createNode() {
        Button button = new Button();
        button.setStyle("-fx-pref-width: 22px; -fx-background-image: url('/image/arrow-down.png'); -fx-background-position: center; -fx-background-repeat: no-repeat; -fx-background-size: 20 20");
        button.setOnAction((ActionEvent e) -> {
            super.getCellInterface().decreaseOrder();
        });
        super.setNode(button);
    }
}
