/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.javafx.tree.cell;

import java.util.Objects;
import javafx.scene.control.TreeCell;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.forfujiapp.javafx.tree.entity.WorkflowTreeEntity;

/**
 * ユニットテンプレート・工程順階層のツリー用セル
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.26.Wen
 */
public class UnitTemplateAndWorkflowTreeCell extends TreeCell<TreeCellInterface> {

    public UnitTemplateAndWorkflowTreeCell() {
    }

    @Override
    protected void updateItem(TreeCellInterface item, boolean empty) {
        super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(getString());
            setGraphic(getTreeItem().getGraphic());
        }
    }

    private String getString() {
        StringBuilder sb = new StringBuilder();
        TreeCellInterface itm = getItem();
        if (Objects.nonNull(itm)) {
            sb.append(itm.getName());
            if (itm instanceof WorkflowTreeEntity) {
                sb.append(" : ");
                sb.append(((WorkflowInfoEntity) itm.getEntity()).getWorkflowRev());
            }
        }

        return sb.toString();
    }

    public Object getEntity() {
        return getItem().getEntity();
    }

    public long getHierarchyId() {
        return getItem().getHierarchyId();
    }
}
