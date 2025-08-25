/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditpluginels.common;

import javafx.scene.control.TreeCell;
import jp.adtekfuji.adFactory.entity.work.WorkHierarchyInfoEntity;

/**
 * ツリーセル表示用クラス
 *
 * @author ta.ito
 */
public class WorkHierarchyTreeCell extends TreeCell<WorkHierarchyInfoEntity> {

    public WorkHierarchyTreeCell() {
    }

    @Override
    protected void updateItem(WorkHierarchyInfoEntity item, boolean empty) {
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
