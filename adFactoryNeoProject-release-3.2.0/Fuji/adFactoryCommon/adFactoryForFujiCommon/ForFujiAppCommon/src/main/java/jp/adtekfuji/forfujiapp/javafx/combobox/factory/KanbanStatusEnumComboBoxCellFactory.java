/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.javafx.combobox.factory;

import adtekfuji.locale.LocaleUtils;
import java.util.ResourceBundle;
import javafx.scene.control.ListCell;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

/**
 * プロパティ情報型表示用セルクラス
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.11.11.Fri
 */
public class KanbanStatusEnumComboBoxCellFactory extends ListCell<KanbanStatusEnum> {

    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    @Override
    protected void updateItem(KanbanStatusEnum item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText("");
        } else {
            setText(LocaleUtils.getString(item.getResourceKey()));
        }
    }
}
