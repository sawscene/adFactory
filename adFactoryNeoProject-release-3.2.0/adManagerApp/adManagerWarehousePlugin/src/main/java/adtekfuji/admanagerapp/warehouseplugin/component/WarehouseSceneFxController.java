/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.component;

import adtekfuji.fxscene.ComponentArea;
import adtekfuji.fxscene.FxScene;
import adtekfuji.fxscene.SceneContiner;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author e.mori
 */
@FxScene(id = "WarehouseScene", fxmlPath = "/fxml/warehouseplugin/warehouse_scene.fxml")
public class WarehouseSceneFxController implements Initializable {

    private final SceneContiner sc = SceneContiner.getInstance();

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sc.setComponent(MenuPane, "MainMenuCompo");
    }

}
