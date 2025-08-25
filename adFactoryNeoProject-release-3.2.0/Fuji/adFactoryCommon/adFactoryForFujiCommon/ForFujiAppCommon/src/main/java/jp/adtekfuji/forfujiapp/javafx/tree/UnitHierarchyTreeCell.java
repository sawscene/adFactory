/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this  file, choose Tools | s
 * and open the  in the editor.
 */
package jp.adtekfuji.forfujiapp.javafx.tree;

import java.util.Objects;
import javafx.scene.control.TreeCell;
import jp.adtekfuji.forfujiapp.entity.unit.UnitHierarchyInfoEntity;

/**
 * ユニットテンプレートツリー表示用セル
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.26.Wen
 */
public class UnitHierarchyTreeCell extends TreeCell<UnitHierarchyInfoEntity> {

    public UnitHierarchyTreeCell() {
    }

    /**
     * 更新処理
     *
     * @param item
     * @param empty
     */
    @Override
    protected void updateItem(UnitHierarchyInfoEntity item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(getString());
            setGraphic(getTreeItem().getGraphic());
        }
    }

    private String getString() {
        return Objects.isNull(getItem()) ? "" : getItem().getHierarchyName();
    }
}
