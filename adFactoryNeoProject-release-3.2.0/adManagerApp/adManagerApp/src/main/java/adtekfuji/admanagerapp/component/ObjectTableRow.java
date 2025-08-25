/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.component;

import adtekfuji.admanagerapp.entity.ObjectTableData;
import javafx.scene.control.TableRow;

/**
 *
 * @author nar-nakamura
 */
public class ObjectTableRow extends TableRow<ObjectTableData> {

    public ObjectTableRow() {
    }

    @Override
    protected void updateItem(ObjectTableData item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            if (item.isCreated()) {
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
