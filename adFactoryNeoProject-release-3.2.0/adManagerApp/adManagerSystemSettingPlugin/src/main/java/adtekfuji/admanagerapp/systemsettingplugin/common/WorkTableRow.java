/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.systemsettingplugin.common;

import adtekfuji.admanagerapp.systemsettingplugin.entity.WorkTableData;
import javafx.scene.control.TableRow;

/**
 *
 * @author s-maeda
 */
public class WorkTableRow extends TableRow<WorkTableData> {

    public WorkTableRow() {
    }

    @Override
    protected void updateItem(WorkTableData item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            if (item.isAdded()) {
                this.setStyle("-fx-font-style: italic; -fx-font-weight: bold;");
            } else if (item.isEdited()) {
                this.setStyle("-fx-font-style: normal; -fx-font-weight: bold;");
            } else {
                this.setStyle("-fx-font-style: normal; -fx-font-weight: normal;");
            }
            this.requestLayout();
        }
    }
}
