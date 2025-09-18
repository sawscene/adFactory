/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.scene;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.ComponentArea;
import adtekfuji.fxscene.FxScene;
import adtekfuji.fxscene.SceneContiner;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author e.mori
 */
@FxScene(id = "KanbanEditScene", fxmlPath = "/fxml/scene/kanban_edit_scene.fxml")
public class SceneFxController implements Initializable, ArgumentDelivery  {

    private final SceneContiner sc = SceneContiner.getInstance();
    
    SceneFxController sceneFxController;

    @FXML
    @ComponentArea
    private AnchorPane AppBarPane;
    @FXML
    @ComponentArea
    private AnchorPane SideNaviPane;
    @FXML
    @ComponentArea
    private AnchorPane ContentNaviPane;
    @FXML
    @ComponentArea
    private AnchorPane MenuPane;
    @FXML
    @ComponentArea
    private AnchorPane MenuPaneUnderlay;
    @FXML
    @ComponentArea
    private SplitPane MainSceneSplitPane;
    
    @Override
    public void setArgument(Object argument) {
        
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sc.setComponent(MenuPane, "MainMenuCompo");
    }
    
    public void hideSideNaviPane() {
         MainSceneSplitPane.getItems().remove(SideNaviPane);
    } 
}
