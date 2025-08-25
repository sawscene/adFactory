/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.javafx.tree;

import javafx.scene.control.TreeCell;
import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;

/**
 * カンバンツリー表示用セル
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.26.Wen
 */
public class KanbanHierarchyTreeCell extends TreeCell<KanbanHierarchyInfoEntity> {

    public KanbanHierarchyTreeCell() {
    }

    @Override
    protected void updateItem(KanbanHierarchyInfoEntity item, boolean empty) {
        super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
        if (empty) {
            setText(null);
        } else {
            setText(getString());
            setGraphic(getTreeItem().getGraphic());
        }
    }

    private String getString() {
        return getItem() == null ? "" : getItem().getHierarchyName();
    }

}
