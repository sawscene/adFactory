/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.treecell;

import javafx.scene.control.TreeCell;
import jp.adtekfuji.adFactory.entity.indirectwork.WorkCategoryInfoEntity;
import jp.adtekfuji.adFactory.entity.master.ReasonCategoryInfoEntity;

/**
 * ツリーセル表示用クラス
 *
 * @author hato
 */
public class ReasonCategoryTreeCell extends TreeCell<ReasonCategoryInfoEntity> {

    public ReasonCategoryTreeCell() {
    }

    @Override
    protected void updateItem(ReasonCategoryInfoEntity item, boolean empty) {
        super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
        if (empty) {
            setText(null);
        } else {
            setText(getString());
            setGraphic(getTreeItem().getGraphic());
        }
    }

    private String getString() {
        return getItem() == null ? "" : getItem().getReasonCategoryName();
    }
}
