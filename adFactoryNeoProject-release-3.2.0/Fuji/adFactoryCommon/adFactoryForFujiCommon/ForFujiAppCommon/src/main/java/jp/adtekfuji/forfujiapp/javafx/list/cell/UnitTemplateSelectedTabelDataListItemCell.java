/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.javafx.list.cell;

import javafx.scene.control.ListCell;
import jp.adtekfuji.forfujiapp.dialog.entity.SelectedTableData;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateInfoEntity;

/**
 * ListView表示用セル
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.11.24.thr
 */
public class UnitTemplateSelectedTabelDataListItemCell extends ListCell<SelectedTableData<UnitTemplateInfoEntity>> {

    @Override
    protected void updateItem(SelectedTableData<UnitTemplateInfoEntity> item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            setText(item.getName());
        } else {
            setText("");
        }
    }
}
