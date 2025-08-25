/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditpluginels.component;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

/**
 * カンバン編集メニュー
 *
 * @author nar-nakamura
 */
@FxComponent(id = "KanbanEditMenuCompoELS", fxmlPath = "/fxml/admanakanbaneditpluginels/kanban_edit_menu.fxml")
public class KanbanEditMenuCompoFxController implements Initializable, ArgumentDelivery {

    private SceneContiner sc = SceneContiner.getInstance();

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    /**
     * 
     * @param argument 
     */
    @Override
    public void setArgument(Object argument) {

    }

    /**
     * カンバン編集
     *
     * @param event 
     */
    @FXML
    public void onViewKanbanList(ActionEvent event) {
        sc.setComponent("ContentNaviPane", "KanbanListCompoELS");
    }

    /**
     * 生産計画読み込み
     *
     * @param event 
     */
    @FXML
    public void onViewKanbanImport(ActionEvent event) {
        sc.setComponent("ContentNaviPane", "KanbanImportCompoELS");
    }

}
