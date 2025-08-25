/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.treecell;

import javafx.scene.control.TreeCell;
import jp.adtekfuji.adFactory.entity.summaryreport.SummaryReportConfigInfoEntity;

import java.util.Objects;

/**
 * ツリーセル表示用クラス
 *
 * @author e-mori
 */
public class SummaryReportTreeCell extends TreeCell<SummaryReportConfigInfoEntity> {

    public SummaryReportTreeCell() {
    }

    @Override
    protected void updateItem(SummaryReportConfigInfoEntity item, boolean empty) {
        super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
        if (empty) {
            setText(null);
        } else {
            setText(getString());
            setGraphic(getTreeItem().getGraphic());
        }
    }

    private String getString() {
        return Objects.isNull((getItem())) ? "" : getItem().getTitle();
    }
    
}
