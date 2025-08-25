/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;

import java.util.List;

/**
 * メニュー表示ボタン
 *
 * @author okada
 */
public class CellMenuShowButton extends AbstractCell {

    private ContextMenu contextMenu;

    public CellMenuShowButton(CellInterface abstractCellInterface, ContextMenu contextMenu) {
        super(abstractCellInterface);
        this.contextMenu = contextMenu;
    }

    @Override
    public void createNode() {
        // ボタンの設定
        Button menuShowButton = new Button("…");
        menuShowButton.addEventHandler(MouseEvent.MOUSE_CLICKED,  e->this.contextMenu.show(this.getNode().getParent(), e.getScreenX(), e.getScreenY()));
        super.setNode(menuShowButton);
    }
}
