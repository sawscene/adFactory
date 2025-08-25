/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.common;

import javafx.scene.control.TreeCell;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowHierarchyInfoEntity;

/**
 * ツリーセル表示用クラス
 *
 * @author ta.ito
 */
public class WorkPlanWorkflowHierarchyTreeCell extends TreeCell<WorkflowHierarchyInfoEntity> {

    public WorkPlanWorkflowHierarchyTreeCell() {
    }

    @Override
    protected void updateItem(WorkflowHierarchyInfoEntity item, boolean empty) {
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
