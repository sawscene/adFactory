/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.component;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;

/**
 * FXML Controller class
 *
 * @author s-maeda
 */
@FxComponent(id = "KanbanHierarchyEditDialogCompo", fxmlPath = "/fxml/compo/kanban_hierarchy_edit_dialog_compo.fxml")
public class KanbanHierarchyEditDialogCompoFxController implements Initializable, ArgumentDelivery {

    private KanbanHierarchyInfoEntity entity = null;

    @FXML
    private TextField hierarchyNameField;
    @FXML
    private CheckBox partitionFlagCheck;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    /**
     * 引数を引き渡す
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {
        if (Objects.isNull(argument) || !(argument instanceof KanbanHierarchyInfoEntity)) {
            return;
        }
        entity = (KanbanHierarchyInfoEntity) argument;

        hierarchyNameField.setText(entity.getHierarchyName());
        partitionFlagCheck.setSelected(entity.getPartitionFlag());
        
        hierarchyNameField.textProperty().addListener((observable, oldValue, newValue)->{
            if(Objects.nonNull(newValue)){
                entity.setHierarchyName(newValue);
            }
        });
        
        partitionFlagCheck.selectedProperty().addListener((observable, oldValue, newValue)->{
            if(Objects.nonNull(newValue)){
                entity.setPartitionFlag(newValue);
            }
        });
    }
}
