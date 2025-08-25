/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.schedule.cell;

import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanScheduleCellSizeTypeEnum;
import adtekfuji.locale.LocaleUtils;
import javafx.scene.control.ListCell;

/**
 *
 * @author (TST)min
 */
public class WorkPlanScheduleCellSizeCellFactory extends ListCell<WorkPlanScheduleCellSizeTypeEnum> {

    /**
     * コンストラクタ
     */
    public WorkPlanScheduleCellSizeCellFactory() {
    }

    @Override
    protected void updateItem(WorkPlanScheduleCellSizeTypeEnum item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText("");
        } else {
            setText(LocaleUtils.getString(item.getResourceKey()));
        }
    }
}
