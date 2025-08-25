/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.scheduleplugin.schedule.cell;

import adtekfuji.admanagerapp.scheduleplugin.common.ScheduleCellSizeTypeEnum;
import adtekfuji.locale.LocaleUtils;
import javafx.scene.control.ListCell;

/**
 *
 * @author adtekfuji
 */
public class ScheduleCellSizeCellFactory extends ListCell<ScheduleCellSizeTypeEnum> {

    /**
     * コンストラクタ
     */
    public ScheduleCellSizeCellFactory() {
    }

    @Override
    protected void updateItem(ScheduleCellSizeTypeEnum item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText("");
        } else {
            setText(LocaleUtils.getString(item.getResourceKey()));
        }
    }
}
