/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.treecell;

import java.util.Objects;
import javafx.scene.control.TreeCell;
import jp.adtekfuji.adFactory.entity.work.WorkHierarchyInfoEntity;

/**
 * 工程階層ツリー表現用セルクラス
 *
 * @author e-mori
 * @version 1.4.2
 * @since 2016.08.02.Tue
 */
public class WorkHierarchyTreeCell extends TreeCell<WorkHierarchyInfoEntity> {

    public WorkHierarchyTreeCell() {
    }

    /**
     * 更新処理
     * 
     * @param item
     * @param empty 
     */
    @Override
    protected void updateItem(WorkHierarchyInfoEntity item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
        } else {
            setText(getString());
            setGraphic(getTreeItem().getGraphic());
        }
    }

    private String getString() {
        return Objects.isNull(getItem()) ? "" : getItem().getHierarchyName();
    }

}
